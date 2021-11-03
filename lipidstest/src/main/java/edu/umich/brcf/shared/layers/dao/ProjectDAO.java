package edu.umich.brcf.shared.layers.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import edu.emory.mathcs.backport.java.util.Collections;
import edu.umich.brcf.shared.layers.domain.Client;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.layers.dto.ProjectDTO;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.comparator.ExperimentbyExpIdComparator;

@Repository
public class ProjectDAO extends BaseDAO
	{
	public void createProject(Project project)
		{
		getEntityManager().persist(project);
		}
	
	public void deleteProject(Project project)
		{
		getEntityManager().remove(project);
		}
		
	public List<Project> allProjects()
		{
		List<Project> projectList = getEntityManager().createQuery("from Project").getResultList();
		
		for( Project project : projectList)
			initializeTheKids(project, new String[]{"client", "contactPerson"});
			
		return projectList;
		}
	
	public List<Project> allProjectsSmall()
		{
		List<Project> projectList = getEntityManager().createQuery("from Project").getResultList();
		
//		for( Project project : projectList)
//			initializeTheKids(project, new String[]{"client", "contactPerson"});
			
		return projectList;
		}
	
	
	public List<Project> allProjectsWithDocs()
		{
		List<Project> projectList = getEntityManager().createQuery("from Project").getResultList();
		
		for( Project project : projectList)
			initializeTheKids(project, new String[]{"client", "contactPerson", "docList"});
		
		return projectList;
		}
	
	public List<String> allProjectNames() 
		{
		Query query = getEntityManager().createNativeQuery("select p.project_name||' ('||p.project_id||')' from project p order by p.start_date desc");
		List<String> orgList = query.getResultList();
		return orgList;
		}

	public List<String> allProjectNamesByName() 
		{
		Query query = getEntityManager().createNativeQuery("select p.project_name||' ('||p.project_id||')' from project p order by nlssort(p.project_name,'NLS_SORT=BINARY_CI')");
		List<String> orgList = query.getResultList();
		return orgList;
		}
	
	public Project loadById(String id)
		{
		Project project =  getEntityManager().find(Project.class, id);
		
		initializeTheKids(project, new String[]{"client", "contactPerson" , "docList"});
		return project;
		}
		
	public Project loadByName(String name)
		{
		List<Project> lst = getEntityManager().createQuery("from Project e where e.projectName = :name").setParameter("name", name).getResultList();
		
		Project project = (Project) DataAccessUtils.requiredSingleResult(lst);
		initializeTheKids(project, new String[]{"client", "contactPerson" , "docList"});
		return project;
		}
	
	public List<Project> loadProjectByClient(Client client)
		{
		List<Project> lst =  getEntityManager().createQuery("from Project p where p.client = :client")
				.setParameter("client", client).getResultList();
		
		for( Project project : lst)
			{
			initializeTheKids(project, new String[]{"client"});
			initializeTheKids(project, new String[]{"contactPerson"});
			}
		return lst;
		}
	
	public List<Project> allActiveProjects() 
		{
		List<Project> lst =  getEntityManager().createQuery("from Project p where trim(p.statusID) = :status")
				.setParameter("status", "A").getResultList();
		
		for( Project project : lst)
			initializeTheKids(project, new String[]{"client", "contactPerson"});
			
		return lst;
		}	
	
	public List<String> getMatchingProjects(String input) 
		{
		Query query = getEntityManager().createQuery("select p.projectID from Project p where p.projectID like '%"+input+"%'");
		return (List<String>)query.getResultList();
		}
	
	public Project getProject(String project) 
		{
		if(!FormatVerifier.verifyFormat(Project.fullIdFormat,project.toUpperCase()))
			project = StringParser.parseId(project);
		return loadProjectWithExpList(project);
		}
	
	public Project getProjectWithOrderedExperiments(String project)
		{
		Project proj = getProject(project);
		Collections.sort(proj.getExperimentList(), new ExperimentbyExpIdComparator());
		return proj;
		}

	public Project loadProjectWithExpList(String id) 
		{
		Project project =  getEntityManager().find(Project.class, id);
		initializeTheKids(project, new String[]{"experimentList"});
		
		return project;
		}
		
	public List<Project> getProjectExperimentsForOrganization(String organization) 
		{
		List<Project> projectList = new ArrayList<Project>();
		Query query = getEntityManager().createQuery("select distinct(p.projectID) from Project p, Client c "+ 
				"where p.client.clientID=c.clientID and c.organizationID = '"+organization+"'");
		List<String> pidList = query.getResultList();
		for( String pid : pidList)
			{
			Project p = loadProjectWithExpList(pid);
			if(!projectList.contains(p))
				projectList.add(p);
			}
		return projectList;
		}

	public List<Project> loadProjectExperimentByClientContact(String searchStr) 
		{
		List<Project> projectList = new ArrayList<Project>();
		
		String contact = searchStr.replaceAll(",", "").replaceAll("'", "''"); // issue 462
		Query query = getEntityManager().createQuery("select distinct(p.projectID) from Project p, Client c "+ 
				"where p.client.clientID=c.clientID and (c.contact.firstName||' ' " +
				"||c.contact.lastName = '"+contact+
				"' or c.investigator.firstName ||' '|| c.investigator.lastName= '"+contact+
				"' or c.contact.lastName ||' '|| c.contact.firstName= '"+contact+
				"' or c.investigator.lastName ||' '|| c.investigator.firstName= '"+contact+
				"')");
		
		List<String> pidList = query.getResultList();
		for( String pid : pidList)
			{
			Project p = loadProjectWithExpList(pid);
			if(!projectList.contains(p))
				projectList.add(p);
			}
		
		return projectList;
		}
	
	// issue 181
	public List<Project> loadProjectExperimentByContact(String searchStr) 
		{
		List<Project> projectList = new ArrayList<Project>();
		
		String contact = searchStr.replaceAll(",", "").replaceAll("'", "''"); // issue 462
		Query query = getEntityManager().createQuery("select distinct(p.projectID) from Project p, User u "+ 
				"where p.contactPerson =u.id and (u.firstName||' ' " +
				"||u.lastName = '"+contact+
				"' or u.lastName ||' '|| u.firstName= '"+contact+
				"')");
		
		List<String> pidList = query.getResultList();
		for( String pid : pidList)
			{
			Project p = loadProjectWithExpList(pid);
			if (!projectList.contains(p))
				projectList.add(p);
			}
		
		return projectList;
		}
	
	// issue 187
	public List<Project> loadProjectExperimentByAssay(String searchStr) 
		{	
		List<Project> projectList = new ArrayList<Project>();
		searchStr = StringParser.parseId(searchStr);
		Query query;
		query = getEntityManager().createNativeQuery("select distinct(e.project_id) from experiment e, sample s, sample_assays sa  " + 
				" where e.exp_id = s.exp_id and s.sample_id = sa.sample_id  and status is not null and trim(status) != ' ' and status != 'X' " + 
				" and sa.assay_id ='" + searchStr + "' order by 1");
		List<String> pidList = query.getResultList();
		for( String pid : pidList)
			{
			Project p = loadProjectWithExpList(pid);
			if(!projectList.contains(p))
				projectList.add(p);
			}		
		return projectList;		
		}
		
	// issue 187
	public List <String> loadExpProjectExperimentByAssay(String searchStr) 
		{	
		List<Project> projectList = new ArrayList<Project>();
		searchStr = StringParser.parseId(searchStr);
		Query query;
		
		query = getEntityManager().createNativeQuery("select project_id || ':' || u.exp_id from experiment u where project_id in ( " + 
				" select distinct project_id from experiment t1 where project_id in (select distinct(project_id)  from experiment e, sample s, sample_assays sa   where status is not null and trim(status) != ' ' and status != 'X' and  e.exp_id = s.exp_id and s.sample_id = sa.sample_id  and sa.assay_id ='" +  searchStr + "')) " + 
				" minus " + 
				" select distinct(project_id || ':' || e.exp_id )  from experiment e, sample s, sample_assays sa   where e.exp_id = s.exp_id and s.sample_id = sa.sample_id   and status is not null and trim(status) != ' ' and status != 'X' and sa.assay_id = '" +
				searchStr + "' order by 1 ");		
		List<String> pidList = query.getResultList();			
		return pidList;		
		}
	
	// issue 187
	public List<String> loadExpProjectExperimentByAssay(String searchStr, String fromDate, String toDate) 
		{	
		List<Project> projectList = new ArrayList<Project>();
		searchStr = StringParser.parseId(searchStr);
		Query query;		
		query = getEntityManager().createNativeQuery("select project_id || '->' || u.exp_id from experiment u where project_id in ( " + 
				" select distinct project_id from experiment t1 where project_id in (select distinct(project_id)  from experiment e, sample s, sample_assays sa   where e.exp_id = s.exp_id and s.sample_id = sa.sample_id and status is not null and trim(status) != ' ' and status != 'X' and sa.assay_id ='" +  searchStr + "'     and trunc(e.creationdate) between to_date('" + fromDate + "', 'mm/dd/yy')  and to_date('" + toDate + "', 'mm/dd/yy'))) " + 
				" minus " + 
				" select distinct(project_id || '->' || e.exp_id )  from experiment e, sample s, sample_assays sa   where e.exp_id = s.exp_id and s.sample_id = sa.sample_id and status is not null and trim(status) != ' ' and status != 'X' and sa.assay_id = '" +
				searchStr + "'  and trunc(e.creationdate) between to_date('" + fromDate + "', 'mm/dd/yy')  and to_date('" + toDate + "', 'mm/dd/yy')    order by 1 ");		
		List<String> pidList = query.getResultList();	
		return pidList;		
		}
	
	public List<Project> loadProjectExperimentByAssay(String searchStr, String fromDate, String toDate) 
		{
		
		List<Project> projectList = new ArrayList<Project>();
		searchStr = StringParser.parseId(searchStr);
		Query query;
		query = getEntityManager().createNativeQuery("select distinct(e.project_id) from experiment e, sample s, sample_assays sa  " + 
		" where e.exp_id = s.exp_id and s.sample_id = sa.sample_id " + " and status is not null and trim(status) != ' ' and status != 'X' " + 
		" and sa.assay_id ='" + searchStr + "'" + " and trunc(e.creationdate) between to_date('" + fromDate + "', 'mm/dd/yy')  and to_date('" + toDate + "', 'mm/dd/yy') order by 1 ");		
		String sqlString  = "select distinct(e.project_id) from experiment e, sample s, sample_assays sa  " + 
		" where e.exp_id = s.exp_id and s.sample_id = sa.sample_id " + " and status is not null and trim(status) != ' ' and status != 'X' " + 
		" and sa.assay_id ='" + searchStr + "'" + " and trunc(e.creationdate) between to_date('" + fromDate + "', 'mm/dd/yy')  and to_date('" + toDate + "', 'mm/dd/yy') order by 1 ";
		List<String> pidList = query.getResultList();
		for( String pid : pidList)
			{
			Project p = loadProjectWithExpList(pid);
			if(!projectList.contains(p))
				projectList.add(p);
			}		
		return projectList;
		}

	public boolean checkProjNameExists(String name)
		{
		Query query = getEntityManager().createNativeQuery("select * from project p where p.project_name = ?1").setParameter(1, name);
		return (query.getResultList().size() > 0);
		}
				
	public boolean checkProjNameExistsAndIsNotSameItem(String name, ProjectDTO dto)
		{
		String currentId = dto.getId();
		Query query = getEntityManager().createNativeQuery("select * from project p where project_name = ?1 and project_id <> ?2").setParameter(1, name).setParameter(2, currentId);
		return (query.getResultList().size() > 0);
		}

	public List<String> projectIdsByStartDate()
		{
		Query query = getEntityManager().createNativeQuery("select p.project_id from project p order by p.project_id desc");
		return query.getResultList();
		}
	}

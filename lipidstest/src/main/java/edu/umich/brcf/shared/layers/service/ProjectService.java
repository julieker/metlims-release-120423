package edu.umich.brcf.shared.layers.service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.springframework.util.Assert;

import edu.umich.brcf.shared.layers.dao.ClientDAO;
import edu.umich.brcf.shared.layers.dao.ProjectDAO;
import edu.umich.brcf.shared.layers.dao.UserDAO;
import edu.umich.brcf.shared.layers.domain.Client;
import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.layers.domain.User;
import edu.umich.brcf.shared.layers.dto.ProjectDTO;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;



@Transactional
public class ProjectService
	{
	ProjectDAO projectDao;
	UserDAO userDao;
	ClientDAO clientDao;

	public List<Project> allProjects()
		{
		return projectDao.allProjects();
		}

	public List<Project> allProjectsSmall()
		{
		return projectDao.allProjectsSmall();
		}
	
	public List<Project> allProjectsWithDocs()
		{
		return projectDao.allProjectsWithDocs();
		}

	public List<String> allProjectNamesByName()
		{
		return projectDao.allProjectNamesByName();
		}

	public Project loadById(String id)
		{
		Assert.notNull(id);
		return projectDao.loadById(id);
		}

	public List<Project> loadProjectByClient(Client client)
		{
		return projectDao.loadProjectByClient(client);
		}

	public List<Project> allActiveProjects()
		{
		return projectDao.allActiveProjects();
		}

	public List<String> getMatchingProjects(String input)
		{
		return projectDao.getMatchingProjects(input);
		}

	public Project getProject(String project)
		{
		return projectDao.getProject(project);
		}

	public Project getProjectWithOrderedExperiments(String project)
		{
		return projectDao.getProjectWithOrderedExperiments(project);
		}
	
	public List<Project> loadProjectExperimentByClientContact(String contact)
		{
		return projectDao.loadProjectExperimentByClientContact(contact);
		}

	public List<Project> getProjectExperimentsForOrganization(String organization)
		{
		return projectDao.getProjectExperimentsForOrganization(organization);
		}

	
	public Project save(ProjectDTO dto)
		{
		Assert.notNull(dto);
    
        Client client = null;
        try
	        {
	        client = clientDao.loadById(dto.getClientID());
	        client.getDept();
	        }
        catch (Exception e) { throw new RuntimeException("Client field cannot be empty and must refer to an existing client lab"); }
		
		User contactPerson = null;
		try 
			{
			contactPerson = userDao.loadById(dto.getContactPerson());
			contactPerson.getFirstName();
			}
		catch (Exception e) { throw new RuntimeException("Contact field cannot be empty and must refer to an registered user"); }
		
		
		 if (StringUtils.isEmptyOrNull(dto.getId()) || "to be assigned".equals(dto.getId()))
			 {
			if (projectDao.checkProjNameExists(dto.getProjectName()))
			{
				System.out.println("Throwing exception on project name...");
				throw new RuntimeException("Duplicate project name. Please enter a different project name");
			}
			 }
		 else if (projectDao.checkProjNameExistsAndIsNotSameItem(dto.getProjectName(), dto))
		 {
			 System.out.println("Throwing exception on project name already exists...");
			 throw new RuntimeException("Duplicate project name. Please enter a different project name");
		 } 
		
		Project project = null;
        if (StringUtils.isEmptyOrNull(dto.getId()) || "to be assigned".equals(dto.getId()))
            try
                {
                project = Project.instance(client, dto.getProjectName(), dto.getDescription(), contactPerson,
					dto.getStatusID(), DateUtils.calendarFromDateStr(dto.getStartDate(), Project.PROJECT_DATE_FORMAT),
					dto.getFinalDeadline(), dto.getTimelineID(), dto.getNotes());
		        projectDao.createProject(project);
                }
		    catch (Exception e) { e.printStackTrace(); project  = null; }
        
        else
            try
	            {
	            project = projectDao.loadById(dto.getId());
			    project.update(dto, client, contactPerson);
			    }
	        catch (Exception e) { e.printStackTrace(); project  = null; }
        
        return project;
        }


	public void updateProject(String id, ProjectDTO dto)
		{
		Assert.notNull(id);
		Assert.notNull(dto);
		Project project = projectDao.loadById(id);
		Client client = clientDao.loadById(dto.getClientID());
		User contactPerson = userDao.loadById(dto.getContactPerson());
		project.update(dto, client, contactPerson);
		}
	

	public boolean isValidProjectSearch(String project)
		{
		Project proj = null;
		
		try { proj = projectDao.loadById(project); } 
		catch (Exception e) { proj = null; }
		
		return (proj != null);
		}

	
	public String getProjectIdForSearchString(String str)
		{
		return getProjectIdForSearchString(str, "search string");
		}
	
	
	public String getProjectIdForSearchString(String str, String label)
		{
		if (str == null) 
			throw new RuntimeException("String can't be null");
		
		String projectId = str;
		if(!FormatVerifier.verifyFormat(Project.fullIdFormat,str.toUpperCase()))
			projectId = StringParser.parseId(str);
		
		if(!FormatVerifier.verifyFormat(Project.fullIdFormat,projectId.toUpperCase()))
			{
			try  
				{
				Project proj = projectDao.loadByName(str);
				projectId = proj.getProjectID();
				}
			catch (Exception e) { throw new RuntimeException("Project load error : cannot find project for " + label + " " + str);  }
			 }
		
		return projectId;
		} 
	

	public List<String> projectIdsByStartDate()
		{
		return projectDao.projectIdsByStartDate();
		}
	

	public void setProjectDao(ProjectDAO projectDao) { this.projectDao = projectDao; }
	public void setUserDao(UserDAO userDao) { this.userDao = userDao; }
	public void setClientDao(ClientDAO clientDao) { this.clientDao = clientDao; }
	}

package edu.umich.brcf.shared.layers.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.hibernate.Hibernate;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import edu.umich.brcf.metabolomics.panels.lims.newexperiment.datacollectors.SubjectInfoItem;
import edu.umich.brcf.shared.layers.domain.Aliquot;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Factor;
import edu.umich.brcf.shared.layers.domain.Priority;
import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.domain.SampleAssay;
import edu.umich.brcf.shared.layers.domain.Shortcode;
import edu.umich.brcf.shared.layers.dto.ClientSampleAliquotsBean;
import edu.umich.brcf.shared.layers.dto.ClientSampleAssaysBean;
import edu.umich.brcf.shared.layers.dto.ExperimentDTO;
import edu.umich.brcf.shared.layers.dto.SampleAssaysBean;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.StringParser;


@Repository
public class ExperimentDAO extends BaseDAO
	{

	public void createExperiment(Experiment exp)
		{
		getEntityManager().persist(exp);
		initializeTheKids(exp, new String[] { "project", "priority" });
		initializeTheKids(exp.getProject(), new String[] { "experimentList" });
		}

	
	public void deleteExperiment(Experiment exp)
		{
		getEntityManager().remove(exp);
		}

	
	public List<Experiment> allExperiments()
		{
		List<Experiment> expList = getEntityManager().createQuery("from Experiment").getResultList();
		for (Experiment exp : expList)
			{
			initializeTheKids(exp, new String[] { "project", "priority" });
			Hibernate.initialize(exp.getProject().getClient());
			Hibernate.initialize(exp.getProject().getContactPerson());
			}
		return expList;
		}
	
 
	public boolean checkExpNameExists(String name)
		{
		Query query = getEntityManager().createNativeQuery("select * from experiment e where e.exp_name = ?1").setParameter(1, name);
		return (query.getResultList().size() > 0);
		}
	
	
	public boolean checkExpNameExistsAndIsNotSameItem(String name, ExperimentDTO dto)
		{
		String currentId = dto.getExpID();
		
		Query query = getEntityManager().createNativeQuery("select * from experiment e where e.exp_name = ?1 and e.exp_id <> ?2").setParameter(1, name).setParameter(2, currentId);
		return (query.getResultList().size() > 0);
		}
	
	
    public List<Experiment> allExperimentsCompact()
	    {
	    return getEntityManager().createQuery("from Experiment").getResultList();
	    }
	    
    // issue 441
	public Experiment loadById(String expID)
		{
		Experiment exp = getEntityManager().find(Experiment.class, expID);
	    initializeTheKids(exp, new String[] { "project", "priority", "sampleList",  "factors"	 });
		for (Sample sample : exp.getSampleList())
			initializeTheKids(sample, new String[] { "subject", "genusOrSpecies", "status", "sampleType" });		
		return exp;
		}
		
	public Experiment loadWithInfoForSubjectTracking(String expId)
		{
		Experiment exp = getEntityManager().find(Experiment.class, expId);
		initializeTheKids(exp, new String[] { "sampleList"});
		for (Sample sample : exp.getSampleList())
			initializeTheKids(sample, new String[] { "subject", "genusOrSpecies" });
		return exp;
		}
	
	
	public List<SubjectInfoItem> loadOptimizedWithInfoForSubjectTracking(String expId)
		{
		List<SubjectInfoItem> lst= new ArrayList<SubjectInfoItem>();
		
		
		Query query = getEntityManager().createNativeQuery(
				"select cast(sample_id as VARCHAR2(9)), "
						+ "cast(sample_name as VARCHAR2(120)), "
						+ "cast(genusorspecies_id as VARCHAR2(22)), "
						+ "cast(subject_id as VARCHAR2(9)), "
						+ "cast(subject_type_id as VARCHAR2(30)), "
						+ "cast(subject_tax_id as VARCHAR2(22)), "
						+ "cast(user_subject_id as VARCHAR2(100)) "
						+ "from VW_SUBJECT_DESIGN_INFO sui where sui.exp_id = ?1").setParameter(1, expId);

		List<Object[]> resultList  = query.getResultList();
				
				
		for (Object[] result : resultList)
			{
			int sz = result.length;

			String genusOrSpeciesId = sz > 2 ? (String) result[2] : " ";
			String subjectId = sz > 3 ? (String) result[3] : " ";
			String subjectType = sz > 4 ? (String) result[4] : " ";
			String taxonomyId = sz > 5 ? (String) result[5] : " ";
			String userSubjectId = sz > 6 ? (String) result[6] : " ";
			
			String compositeSubjId = userSubjectId + " / " + subjectId;
			
			SubjectInfoItem s = new SubjectInfoItem(compositeSubjId, subjectType, genusOrSpeciesId, genusOrSpeciesId, taxonomyId);
			lst.add(s);
			}

		
		return lst;
		}
	
	
	public Experiment loadByIdWithProject(String expID)
		{
		Experiment exp = getEntityManager().find(Experiment.class, expID);
		initializeTheKids(exp, new String[] { "project"  });
		//for (Sample sample : exp.getSampleList())
		///	initializeTheKids(sample, new String[] { "subject", "genusOrSpecies", "status", "sampleType" });
		
		return exp;
		}
	

	public Experiment loadSimplestById(String expID)
		{
		Experiment exp = getEntityManager().find(Experiment.class, expID);
		//Make sure exception is thrown if experiment doesn't exist;
		exp.getExpDescription();
		return exp;
		}
	
	
	public Experiment loadSimpleExperimetById(String expID)
		{
		Experiment exp = getEntityManager().find(Experiment.class, expID);
		
		initializeTheKids(exp, new String[] { "project", "sampleList" });
		for (Sample sample : exp.getSampleList())
			initializeTheKids(sample, new String[] { "subject" });
		
		return exp;
		}

	
	public Experiment loadExperimentWithInfoForDrcc(String expId)
		{
		Experiment exp = getEntityManager().find(Experiment.class, expId);
		
		if (exp == null) return null;
		
		initializeTheKids(exp, new String[] { "project", "sampleList" });
		for (Sample sample : exp.getSampleList())
			initializeTheKids(sample, new String[] { "subject", "genusOrSpecies", "status", "sampleType" });
		
		return exp;
		}


	public Experiment loadExperimentWithInfoForInventoryReport(String expId)
		{
		Experiment exp = getEntityManager().find(Experiment.class, expId);
		
		if (exp == null) return null;
		
		initializeTheKids(exp, new String[] { "project", "sampleList" });
		initializeTheKids(exp.getProject(), new String[] { "client"});
		initializeTheKids(exp.getProject().getClient(), new String [] { "investigator"});
		
		return exp;
		}
	
	
//	public Experiment loadWithSimpleSamples(String expId)
//		{
//		Experiment exp = getEntityManager().find(Experiment.class, expId);
//		
//		if (exp == null) return null;
//		initializeTheKids(exp, new String[] { "sampleList" });
//		}
	
	
	public Experiment loadExperimentWithInfoForEditing(String expId)
		{
		Experiment exp = getEntityManager().find(Experiment.class, expId);
		
		initializeTheKids(exp, new String[] { "project", "sampleList","factors" });
		for (Sample sample : exp.getSampleList())
			{
			initializeTheKids(sample, new String[] { "subject", "genusOrSpecies", "status", "sampleType", "sampleAssays" });
			for (SampleAssay sa : sample.getSampleAssays())
				initializeTheKids(sa, new String[] { "assay" });
			}

		return exp;
		}

	
	public Experiment loadByName(String name)
		{
		List<Experiment> lst = getEntityManager().createQuery("from Experiment e where e.expName = :name").setParameter("name", name).getResultList();
		
		Experiment exp = (Experiment) DataAccessUtils.requiredSingleResult(lst);
		initializeTheKids(exp, new String[] { "project", "priority" });
		return exp;
		}
	

	public List<Experiment> loadExperimentByProject(Project project)
		{
		List<Experiment> lst = getEntityManager().createQuery("from Experiment e where e.project = :project")
          .setParameter("project", project).getResultList();
		
		for (Experiment exp : lst)
			initializeTheKids(exp, new String[] { "project", "priority" });
		
		return lst;
		}
	

	public List<Experiment> allActiveExperiments()
		{
		List<Experiment> lst = getEntityManager().createQuery("from Experiment e where trim(e.priority.id) <> :priority")
				.setParameter("priority", "LOW").getResultList();
		
		for (Experiment exp : lst)
			initializeTheKids(exp, new String[] { "project", "priority" });
		
		return lst;
		}

	
	public Priority loadPriorityByID(String id)
		{
		Priority priority = getEntityManager().find(Priority.class, id);
		return priority;
		}
	

	public String countSamples(String expId)
		{
		// JAK fix issue 147
		String queryStr = "select cast(sample_id as VARCHAR2(9)) from sample where exp_id = ?1";
		Query query = getEntityManager().createNativeQuery(queryStr).setParameter(1, expId);
		
		List<Object[]> resultList  = query.getResultList();
		
		Integer count = resultList == null ? 0 : resultList.size();
		
		return count.toString();
		}
	
	
	public List<String> getMatchingExperiments(String input)
		{
		Query query = getEntityManager().createQuery("select e.expID from Experiment e where e.expID like '%" + input + "%'");
		 return query.getResultList();
		}

	
	public void invalidateSamples(String eid)
		{
		String expId = eid;

		String queryString = "select cast(s.subject_id as VARCHAR2(9)) from sample s where s.exp_id = ?1 ";
		Query query = getEntityManager().createNativeQuery(queryString).setParameter(1, expId);
		List<String> subjectList = query.getResultList();
		System.out.println(queryString);

		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (int i = 0; i < subjectList.size(); i++)
			{
			if (i > 0)
				sb.append(",");
			sb.append("'" + subjectList.get(i) + "'");
			}
		sb.append(")");

		String subjectsListString = sb.toString();
		
		queryString = "delete from experiment_setup where level_id in "
				+ " (select level_id from factor_levels fl inner join experimental_factors ef on "
				+ " fl.factor_id = ef.factor_id where ef.experiment_id = ?1)";

		query = getEntityManager().createNativeQuery(queryString).setParameter(1,expId);
		System.out.println(queryString);
		query.executeUpdate();

		queryString = "delete from factor_levels where factor_id in (select factor_id from experimental_factors where experiment_id = ?1)";
		query = getEntityManager().createNativeQuery(queryString).setParameter(1,expId);
		System.out.println(queryString);
		query.executeUpdate();

		
		queryString = " delete from experimental_factors where experiment_id = ?1 ";
		query = getEntityManager().createNativeQuery(queryString).setParameter(1, expId);
		System.out.println(queryString);
		query.executeUpdate();

		queryString = " delete from experimental_group where exp_id = ?1";
		query = getEntityManager().createNativeQuery(queryString).setParameter(1, expId);
		System.out.println(queryString);
		query.executeUpdate();

	//	queryString = "delete from subject_properties where subject_id in (select subject_id from sample s where s.exp_id = ?1)";
	//	query = getEntityManager().createNativeQuery(queryString).setParameter(1,expId);
	//	System.out.println(queryString);
	//	query.executeUpdate();


		queryString = "delete from sample_assays where sample_id in (select s.sample_id from sample s where s.exp_id = ?1)";
		query = getEntityManager().createNativeQuery(queryString).setParameter(1, expId);
		System.out.println(queryString);
		query.executeUpdate();

		
		queryString = " delete from sample_location_history where sample_id in (select sample_id from sample where exp_id = ?1)";
		query = getEntityManager().createNativeQuery(queryString).setParameter(1, expId);
		System.out.println(queryString);
		query.executeUpdate();


		queryString = "delete from sample where exp_id = ?1";
		query = getEntityManager().createNativeQuery(queryString).setParameter(1, expId);		
		System.out.println(queryString);
		query.executeUpdate();

		// if (subjectList.size() > 0 && subjectList.size() < 700)
		// queryString = "delete from subject where subject_id in " +
		// subjectsListString;
		// query = getEntityManager().createNativeQuery(queryString);
		// query.executeUpdate();
		}

	
	public List<Project> getProjects(String exp)
		{
		List<Project> projectList = new ArrayList<Project>();
	
		if (FormatVerifier.verifyFormat(Experiment.idFormat, exp.toUpperCase()))
			projectList.add(loadById(exp).getProject());
		else if (FormatVerifier.matchFormat(Experiment.idFormat, exp.toUpperCase()))
			projectList.add(loadById(StringParser.parseId(exp)).getProject());
		else
			projectList.add(loadByName(StringParser.parseName(exp)).getProject());
		
		if (!projectList.isEmpty())
			initializeTheKids(projectList.get(0),new String[] { "experimentList" });
		
		return projectList;
		}

	
	public List<Experiment> loadExperimentsByProjectID(String id)
		{
		List<Experiment> lst = getEntityManager().createQuery("from Experiment e where e.project.projectID = :id")
			.setParameter("id", id).getResultList();
		
		for (Experiment exp : lst)
			initializeTheKids(exp, new String[] { "project", "priority","sampleList" });
			
		return lst;
		}

	
	public List<Project> loadProjectExperimentByPrep(String prep)
		{
		List<Project> projectList = new ArrayList<Project>();
		Query query = getEntityManager().createQuery("select distinct(e.expID) from Experiment e, Sample s, PreppedSample ps "
								+ "where ps.samplePrep.prepID = '"
								+ prep
								+ "' and ps.sample.sampleID=s.sampleID and s.exp.expID=e.expID and e.expID not in ('EX00005')");
		
		List<String> eidList = query.getResultList();
		for (String eid : eidList)
			{
			List<Project> projList = getProjects(eid);
			for (Project p : projList)
				if (!projectList.contains(p))
					projectList.add(p);
			}
		
		return projectList;
		}

	
	public List<Experiment> loadExperimentsByPrep(String prep)
		{
		List<Project> projectList = new ArrayList<Project>();
		Query query = getEntityManager().createQuery("select distinct(e.expID) from Experiment e, Sample s, PreppedSample ps "
								+ "where ps.samplePrep.prepID = '" + prep
								+ "' and ps.sample.sampleID=s.sampleID and s.exp.expID=e.expID and e.expID not in ('EX00005')");
		
		List<String> eidList = query.getResultList();
		List<Experiment> expList = new ArrayList<Experiment>();
		for (String eid : eidList)
			expList.add(loadSimpleExperimetById(eid));
			
		return expList;
		}

	
	public List<Experiment> loadExperimentsByClientContact(String contact)
		{
		List<Experiment> experimentList = new ArrayList<Experiment>();

	//	Query query = getEntityManager().createQuery(
	//					"select distinct(e.expID) from Project p, Experiment e, Client c "
	//							+ "where e.project.projectID=p.projectID and p.client.clientID=c.clientID and (c.contact.firstName||' ' "
	//							+ "|| c.contact.lastName = '"
	///							+ contact
	//							+ "' or c.investigator.firstName ||' '|| c.investigator.lastName= '"
		//						+ contact  
	//							+ "' or c.investigator.lastName ||' '|| c.investigator.firstName = '"
	//							+ contact 
	//							+ "' or c.contact.lastName ||' '|| c.contact.firstName = '"
	//							+ contact 
	//							+ "')");

		List<String> eidList = loadExperimentIdsByClientContact(contact); //query.getResultList();
		for (String eid : eidList)
			{
			Experiment e = loadSimpleExperimetById(eid);
			if (!experimentList.contains(e))
				experimentList.add(e);
			}

		return experimentList;
		}

	
	public List<String> loadExperimentIdsByClientContact(String contact)
		{
		String searchString = contact.replaceAll(",", "").replaceAll("'", "''"); // issue 462
		Query query = getEntityManager().createQuery(
				"select distinct(e.expID) from Project p, Experiment e, Client c "
						+ "where e.project.projectID=p.projectID and p.client.clientID=c.clientID and (c.contact.firstName||' ' "
						+ "|| c.contact.lastName = '"
						+ searchString
						+ "' or c.investigator.firstName ||' '|| c.investigator.lastName= '"
						+ searchString  
						+ "' or c.investigator.lastName ||' '|| c.investigator.firstName = '"
						+ searchString 
						+ "' or c.contact.lastName ||' '|| c.contact.firstName = '"
						+ searchString 
						+ "')");

		List<String> eidList = query.getResultList();
		return eidList;
		}
	
	public List<String> getShortcodeIdsForExpId(String expId)
		{
		Query query2 = getEntityManager().createNativeQuery("select cast(s.shortcode as VARCHAR2(20)) from "
				+ "Shortcodes s where s.exp_id = ?1").setParameter(1,expId);

		List<String> orgList2 = query2.getResultList();
		return orgList2;
		}

	
	public Shortcode getExperimentShortcode(String code, String expId)
		{
		List<Shortcode> lst = getEntityManager().createQuery("from Shortcode s where s.code=:code and s.exp.expID = :eid")
		.setParameter("code", code.trim()).setParameter("eid", expId).getResultList();
		  
		Shortcode shortcode;
		try { shortcode = (Shortcode) DataAccessUtils.requiredSingleResult(lst); } 
		catch (Exception e) { shortcode = null; }

		return shortcode;
		}

	
	public void saveShortcode(Shortcode sc)
		{
		getEntityManager().persist(sc);
		}
	

	public String[][] getDesignMatrix(Experiment experiment)
		{
		experiment = loadSimpleExperimetById(experiment.getExpID());
		List<Sample> samples = experiment.getSampleList();
		List<Factor> factors = experiment.getFactors();

		List<String> fNames = new ArrayList<String>();
		for (Factor factor : factors)
			fNames.add(factor.getFactorName());

		Map<String, Integer> factorColumn = new HashMap<String, Integer>();
		String[][] designMatrix = new String[samples.size() + 1][factors.size() + 1];

		if (fNames.size() < 1)
			return new String[0][0];

		Collections.sort(fNames);
		for (int f = 0; f < fNames.size(); f++)
			factorColumn.put(fNames.get(f), f + 1);

		int rowCount = 0, colCount = 0;
		String sid = "S";
		
		String query = "select cast(es.sample_id as VARCHAR2(9)), ef.factor_name, fl.value "
				+ " from experiment_setup es inner join factor_levels fl on fl.level_id = es.level_id "
				+ " inner join experimental_factors ef on ef.factor_id = fl.factor_id "
				+ " where ef.experiment_id = ?1"
				+ " order by es.sample_id, ef.factor_name";
		
		List<Object[]> results = getEntityManager().createNativeQuery(query).setParameter(1, experiment.getExpID()).getResultList();


		for (Object object : results)
			{
			Object[] objArr = (Object[]) object;
			
			colCount = factorColumn.get((String) objArr[1]);
			designMatrix[rowCount][colCount] = ((String) objArr[2]);
			
			if (!sid.equals((String) objArr[0]))
				{
				sid = ((String) objArr[0]);
				designMatrix[rowCount++][0] = sid;
				}
			}

		return designMatrix;
		}

	
	public List<SampleAssaysBean> getAssaysForExperiment(Experiment experiment)
		{
		List<SampleAssaysBean> sampleAssayList = new ArrayList<SampleAssaysBean>();
		Experiment exp = getEntityManager().find(Experiment.class, experiment.getExpID());
		
		List<String> assays;
		initializeTheKids(exp, new String[] { "sampleList" });
		for (Sample sample : exp.getSampleList())
			{
			initializeTheKids(sample, new String[] { "sampleAssays" });
			assays = new ArrayList<String>();
			for (SampleAssay sa : sample.getSampleAssays())
				{
				initializeTheKids(sa, new String[] { "assay" });
				assays.add(sa.getAssay().getAssayName());
				}
			sampleAssayList.add(new SampleAssaysBean(sample.getSampleID(), assays));
			}
		return sampleAssayList;
		}

	
	public List<SampleAssay> getSampleAssaysForExperimentAndAssay(String expId, String assayId)
		{
		List<SampleAssay> sampleAssayList = new ArrayList<SampleAssay>();
		Experiment exp = getEntityManager().find( Experiment.class, expId);

		initializeTheKids(exp, new String[] { "sampleList" });
		for (Sample sample : exp.getSampleList())
			{
			initializeTheKids(sample, new String[] { "sampleAssays" });
			for (SampleAssay sa : sample.getSampleAssays())
				{
				initializeTheKids(sa, new String[] { "assay" });
				if (sa.getAssay().getAssayId().equals(assayId))
					sampleAssayList.add(sa);
				}
			}

		return sampleAssayList;
		}

	
	public List<ClientSampleAssaysBean> getExperimentAssaysForClient( Experiment experiment)
		{
		List<ClientSampleAssaysBean> sampleAssayList = new ArrayList<ClientSampleAssaysBean>();
		Experiment exp = getEntityManager().find(Experiment.class, experiment.getExpID());

		initializeTheKids(exp, new String[] { "sampleList" });
		for (Sample sample : exp.getSampleList())
			{
			initializeTheKids(sample, new String[] { "sampleAssays" });
			for (SampleAssay sa : sample.getSampleAssays())
				initializeTheKids(sa, new String[] { "assay", "status" });

			sampleAssayList.add(new ClientSampleAssaysBean(sample.getSampleID(), sample.getSampleAssays()));
			}
		
		return sampleAssayList;
		}

	/*
	
	   public List<ClientSampleAliquotsBean> getExperimentAliquots(Experiment experiment)
	       {
	       List<ClientSampleAliquotsBean> sampleAliquotList = new ArrayList<ClientSampleAliquotsBean>();
	       Experiment exp = getEntityManager().find(Experiment.class, experiment.getExpID());
	
	       initializeTheKids(exp, new String[]{"sampleList"});
	       for( Sample sample : exp.getSampleList())
	               {
	               initializeTheKids(sample, new String[]{"aliquotList"});
	               //for (SampleAssay sa: sample.getSampleAssays())
	               //      initializeTheKids(sa, new String[]{"assay", "status" });
	
	               //sample.
	               sampleAliquotList.add(new ClientSampleAliquotsBean(sample.getSampleID(), sample.getAliquotList$
	               }
	       return sampleAliquotList;
	       } */

	
	public List<String> allExpIdsForAbsciex()
		{
		Query query2 = getEntityManager().createNativeQuery("select cast(s.exp_id as VARCHAR2(7)) from Sample s"
						+ " inner join Sample_Assays sa on s.sample_id = sa.sample_id where sa.assay_id = 'A004'"
						+ " group by s.exp_id order by s.exp_id desc");

		List<String> expList = query2.getResultList();

		return expList;
		}


	public List<String> allExpIdsForAgilent()
		{
		Query query2 = getEntityManager().createNativeQuery("select cast(s.exp_id as VARCHAR2(7)) from Sample s "
								+ " inner join Sample_Assays sa on s.sample_id = sa.sample_id "
								+ " where sa.assay_id != 'A004' group by s.exp_id order by s.exp_id desc");

		List<String> expList = query2.getResultList();

		return expList;
		}

	
	public List<String> getFactorNamesForExpId(String eid)
		{
		String queryString = "select cast(f.factor_name as VARCHAR2(120)) from experimental_factors f where f.experiment_id = ?1";
		Query query = getEntityManager().createNativeQuery(queryString).setParameter(1, eid)	;	

		return query.getResultList();
		}

	
	public List<String> expIdsByInceptionDate()
		{
		Query query = getEntityManager().createNativeQuery("select cast(e.exp_id as VARCHAR2(7)) from Experiment e order by e.exp_id desc");

		List<String> orgList = query.getResultList();

		return (orgList == null ? new ArrayList<String>() : orgList);
		}
	

	public List<String> getGrantInfo(String expId)
		{
		Query query = getEntityManager().createNativeQuery("select cast(nih_grant_number as VARCHAR2(100)) from Shortcodes where exp_id = ?1");

		List<String> orgList = query.setParameter(1, expId).getResultList();

		return (orgList == null ? new ArrayList<String>() : orgList);
		}

	
	public List<ClientSampleAliquotsBean> getExperimentAliquots(Experiment experiment) 
		{
		List<ClientSampleAliquotsBean> sampleAliquotList = new ArrayList<ClientSampleAliquotsBean>();
		Experiment exp = getEntityManager().find(Experiment.class, experiment.getExpID());
		
		initializeTheKids(exp, new String[]{"sampleList"});
		for(Sample sample : exp.getSampleList())
			{
			initializeTheKids(sample, new String[]{"aliquotList"});
			//for (SampleAssay sa: sample.getSampleAssays())
			//	initializeTheKids(sa, new String[]{"assay", "status" });
			
			sampleAliquotList.add(new ClientSampleAliquotsBean(sample.getSampleID(), new ArrayList<Aliquot>()));
			}
		
		return sampleAliquotList;
		}

	
	public String experimentNameForId(String expId)
		{
		Experiment exp =  getEntityManager().find(Experiment.class, expId);
		return (exp != null ? exp.getExpName() : "");
		}
	}

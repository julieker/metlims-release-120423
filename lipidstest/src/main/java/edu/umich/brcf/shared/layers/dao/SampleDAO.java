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

import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.ExperimentSetup;
import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.domain.SampleAssay;
import edu.umich.brcf.shared.layers.domain.SampleType;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.comparator.SimpleClientSampleAssaysBeanComparator;
import edu.umich.brcf.shared.util.datacollectors.SampleAssayInfo;
import edu.umich.brcf.shared.util.datacollectors.SimpleClientSampleAssaysBean;

@Repository
public class SampleDAO extends BaseDAO
	{

	public void createSample(Sample sample)
		{
		getEntityManager().persist(sample);
		}

	
	public void deleteSample(Sample sample)
		{
		getEntityManager().remove(sample);
		}

	// Issue 214
	public List<String[]> gerExpiredSamples(int lowerLimit, int upperLimit)
	    {
		Query query = getEntityManager().createNativeQuery("select  exp_name, exp_id , total_expired, date_created_formatted, formatted_name, email from  (" +
	                 " select t1.exp_id, t1.exp_name, count(*) total_expired,   to_char(max(t2.date_created), 'mm/dd/yyyy') date_created_formatted, max(date_created) date_created, first_name || ' ' || last_name formatted_name, email " + 
                     " from experiment t1, sample t2, project t3, researcher t4 " +
	                 " where t1.project_id = t3.project_id and t3.CONTACTPERSON_ID = t4.RESEARCHER_ID " +
  	                 " and t2.exp_id = t1.exp_id and date_created <= sysdate - " + lowerLimit + " and date_created >= sysdate -" + upperLimit + " and realobject <> 'T' group by t1.exp_id, t1.exp_name,first_name || ' ' || last_name, email  )" + 
                     " order by date_created , exp_id ");		
		List<String[]> sampleArrayList = query.getResultList();	
        return sampleArrayList;       
	    }
	
	public List<Sample> allSamples()
		{
		List<Sample> sampleList = getEntityManager().createQuery("from Sample").getResultList();
		
		for (Sample sample : sampleList)
			initializeTheKids(sample, new String[] { "exp", "genusOrSpecies", "status", "sampleType", "group" });
		
		return sampleList;
		}

	
	public Sample loadSampleById(String sampleID)
		{
		return (getEntityManager().find(Sample.class, sampleID));
		}

	
	public Sample loadById(String sampleID)
		{
		Sample sample = getEntityManager().find(Sample.class, sampleID);
		if (sample != null)
			initializeTheKids(sample, new String[] { "exp", "genusOrSpecies","sampleType", "docList", "factorLevels", "subject" });
		
		return sample;
		}

	
	public Map<String, List<String>> getFactorValueMapForExpId(String expId)
		{
		Map<String, List<String>> valueMap = new HashMap<String, List<String>>();
		
		Query q = getEntityManager().createNativeQuery("select cast(sample_id as VARCHAR2(9)), "
						+ "cast(value as VARCHAR2(40)) "
						+ "from VW_FACTOR_LEVEL_INFO fli where fli.experiment_id = ?1 "
						+ "order by fli.sample_id, fli.factor_name").setParameter(1, expId);

		List<Object[]> resultList  = q.getResultList();
		
		for (Object[] result : resultList)
			{
			int sz = result.length;
			String sampleId = sz > 0 ? (String) result[0] : " ";
			String value = sz > 1 ? (String) result[1] : " ";
			
			if (valueMap.get(sampleId) == null)
				valueMap.put(sampleId, new ArrayList<String>());
			
			valueMap.get(sampleId).add(value);
			}
	
	return valueMap;
	}
	
	
	public Sample loadWithFactorsById(String sampleId)
		{
		Sample sample = getEntityManager().find(Sample.class, sampleId);
		if (sample == null)
			return sample;

		initializeTheKids(sample, new String[] { "factorLevels" });
		for (ExperimentSetup setup : sample.getFactorLevels())
			{
			Hibernate.initialize(setup.getLevel());
			Hibernate.initialize(setup.getLevel().getFactor());
			}

		return sample;
		}

	
	public Sample loadBasicsById(String sampleID)
		{
		Sample sample = getEntityManager().find(Sample.class, sampleID);
		initializeTheKids(sample, new String[] { "exp", "genusOrSpecies", "status", "sampleType", "group", "parent", "docList", 
			"factorLevels", "preppedList" });
		
		return sample;
		}

	
	public Sample loadSampleAlongWithExpById(String sampleID)
		{
		Sample sample = getEntityManager().find(Sample.class, sampleID);
		initializeTheKids(sample, new String[] { "exp", "sampleType", "subject", "genusOrSpecies" });
		return sample;
		}

	
	public Sample loadByName(String name)
		{
		List<Sample> lst = getEntityManager().createQuery("from Sample s where s.sampleName = :name").setParameter("name", name).getResultList();
		
		Sample sample = (Sample) DataAccessUtils.requiredSingleResult(lst);
		initializeTheKids(sample, new String[] { "exp", "genusOrSpecies","status", "sampleType", "group" });

		return sample;
		}

	// issue 287
	public String sampleNameForId(String id)
		{
		Sample s = loadById(id);
		if (s == null)
			return "";
		return s.getSampleName();
		}

	
	public List<Sample> loadSampleByExperiment(Experiment exp)
		{
		return getSampleListNatively(exp.getExpID()); // ("exp", exp);
		}

	
	public List<Sample> loadSampleByExperimentId(String expId)
		{
		return getSampleListNatively(expId); // ("exp", exp);
		}
	
	
	public List<Sample> loadBasicSamplesForExpId(String expId)
		{
		List<Sample> lst = getEntityManager().createQuery("from Sample s where s.exp.expID = :expId order by s.sampleID").setParameter("expId", expId).getResultList();
		
		return lst;
		}

	
	public List<Sample> loadSampleForStatusTracking(String expId)
		{
		List<Sample> lst = getEntityManager().createQuery("from Sample s where s.exp.expID= :expId order by s.sampleID")
				.setParameter("expId", expId).getResultList();
		
		for (Sample sample : lst)
			initializeTheKids(sample, new String[] { "exp", "status" });

		return lst;
		}

	
	public List<Sample> loadSampleForAssayStatusTracking(String expId)
		{
		List<Sample> lst = getEntityManager().createQuery("from Sample s where s.exp.expID= :expId order by s.sampleID")
				.setParameter("expId", expId).getResultList();
		
		for (Sample sample : lst)
			{
			initializeTheKids(sample, new String[] { "exp", "status", "sampleAssays" });
			for (SampleAssay sa : sample.getSampleAssays())
				initializeTheKids(sa, new String[] { "assay", "status","sample" });
			}

		return lst;
		}

	
	public List<Sample> loadSampleByProject(Project proj)
		{
		return getSampleList("exp.project", proj);
		}

	
	public List<Sample> loadSampleByType(SampleType st)
		{
		return getSampleList("sampleType", st);
		}

	
	public List<Sample> loadSampleByLocation(String locID)
		{
		return getSampleList("locID", locID);
		}

	
	public List<Sample> getSampleListNatively(String eid)
		{
		Query query = getEntityManager().createNativeQuery("select cast(s.sample_id as VARCHAR2(9)) from Sample s where s.exp_id = "
			+ " ?1 order by  s.sample_id desc").setParameter(1, eid);

		List<Sample> sampleList = new ArrayList<Sample>();
		List<String> sampleIdList = query.getResultList();

		for (String sampleId : sampleIdList)
			sampleList.add(loadById(sampleId));
			
		return sampleList;
		}

	
	public List<Sample> getSampleList(String str, Object o)
		{
		List<Sample> lst;
		String query;
		
		if (str.indexOf(".") > 0)
			query = "from Sample s where s." + str + " = :" + str.substring(str.indexOf(".") + 1) + " order by s.exp.expName, s.sampleName";
		else
			query = "from Sample s where s." + str + " = :" + str + " order by s.exp.expName, s.sampleName";
		
		if  (str.indexOf(".") > 0)
			lst = getEntityManager().createQuery(query).setParameter(str.substring(str.indexOf(".") + 1), o).getResultList();
		else
			lst = getEntityManager().createQuery(query).setParameter(str, o).getResultList();
			
		return lst;
		}
	

	public List<String> getMatchingSamples(String input)
		{
		Query query = getEntityManager().createQuery("select s.sampleID from BiologicalSample s where s.sampleID like '%"+ input + "%'");
		List<String> fidList = query.getResultList();
		return fidList;
		}

	
	public List<String> sampleIdsForExpId(String eid)
		{
		Query query = getEntityManager().createNativeQuery("select cast(s.sample_id as VARCHAR2(9)) from Sample s "
				+ "where s.exp_id =  ?1  order by  s.sample_id desc").setParameter(1, eid);
		List<String> sampleList = query.getResultList();

		return sampleList;
		}
	
	// issue 268 include assay
	public List<String> sampleIdsForExpIdAssayId(String eid, String assayId)
	     {
	     Query query = getEntityManager().createNativeQuery("select cast(s.sample_id as VARCHAR2(9)) from Sample s , sample_assays sa"
			+ " where s.exp_id =  ?1  and  s.sample_id = sa.sample_id and sa.assay_id = ?2 order by  s.sample_id desc").setParameter(1, eid).setParameter(2,  assayId);
	     List<String> sampleList = query.getResultList();
	     return sampleList;
	     }

	// JAK issue 180 allow for ascending order
	public List<String> orderedSampleIdsForExpId(String eid, Boolean desc)
	    {
        String strOrder = ((desc) ? "desc" : "");    
	    Query query = getEntityManager().createNativeQuery("select cast(s.sample_id as VARCHAR2(9)) from Sample s "
			+ "where s.exp_id =  ?1  order by  s.sample_id " + strOrder ).setParameter(1, eid);
	    List<String> sampleList = query.getResultList();
	    return sampleList;
	}
	
	
	public List<String> sampleNamesForExpId(String eid)
		{
		Query query = getEntityManager().createNativeQuery("select cast(s.sample_name as VARCHAR2(9)) from Sample s where s.exp_id = "
				+ " ?1 order by  s.exp_id desc").setParameter(1, eid);

		List<String> sampleList = query.getResultList();

		return sampleList;
		}

	
	public List<String> sampleIdsForExpIdAndAssayId(String eid, String aid)
		{
		Query query2 = getEntityManager().createNativeQuery("select cast(t.sample_id as VARCHAR2(9)) from "
					+ "(select s.sample_id from Sample s where s.exp_id = ?1) t"
					+ " inner join Sample_Assays sa on t.sample_id = sa.sample_id "
					+ " where sa.assay_id = ?2 order by t.sample_id");

		query2.setParameter(1, eid);
		query2.setParameter(2, aid);
		List<String> orgList2 = query2.getResultList();
		return orgList2;
		}

	
	public List<String> sampleIdsForExpIdAndAssayIdMinusExcluded(String eid, String aid)
		{
		Query query2 = getEntityManager().createNativeQuery("select cast(t.sample_id as VARCHAR2(9)) from (select s.sample_id from Sample s where s.exp_id = ?1 ) t "
								+ " inner join Sample_Assays sa on t.sample_id = sa.sample_id  where sa.assay_id = ?2  and sa.status != ?3  order by t.sample_id")
				   .setParameter(1, eid ).setParameter(2, aid).setParameter(3,  'X');
		

		List<String> orgList2 = query2.getResultList();
		return orgList2;
		}

	
	public List<String> excludedSampleIdsForExpIdAndAssayId(String eid, String aid)
		{
		Query query2 = getEntityManager().createNativeQuery("select cast(t.sample_id as VARCHAR2(9)) from (select s.sample_id from Sample s where s.exp_id = ?1) t"
						+ " inner join Sample_Assays sa on t.sample_id = sa.sample_id  where sa.assay_id = ?2 and sa.status =  ?3  order by t.sample_id")
				        .setParameter(1,  eid).setParameter(2, aid).setParameter(3,  'X');


		List<String> orgList2 = query2.getResultList();
		return orgList2;
		}

	
	public List<String> getFactorValuesById(String sampleId)
		{

		Query query2 = getEntityManager().createNativeQuery("select cast(f.value as VARCHAR2(40)) from experiment_setup e"
					+ " inner join Factor_Levels f on e.level_id = f.level_id  where e.sample_id = ?1  order by f.factor_id")
					.setParameter(1,  sampleId);

		List<String> orgList2 = query2.getResultList();

		return orgList2;
		}

	
	public List<String> getFactorValuesByIdSortedByName(String sampleId)
		{
		if (!FormatVerifier.verifyFormat(Sample.idFormat, sampleId.toUpperCase()))
			return new ArrayList<String>();

		Query query2 = getEntityManager().createNativeQuery("select cast(f.value as VARCHAR2(40)) from experiment_setup e inner join Factor_Levels f on "
				+  "e.level_id = f.level_id  inner join Experimental_Factors ef on ef.factor_id = f.factor_id  where e.sample_id = ?1  order by ef.factor_name")
				.setParameter(1,  sampleId);

		List<String> orgList2 = query2.getResultList();
	
		return orgList2;
		}

	
	public Map<String, String> getFactorValueMapForId(String sampleId)
		{
		Map<String, String> map = new HashMap<String, String>();
		if (!FormatVerifier.verifyFormat(Sample.idFormat, sampleId.toUpperCase()))
			return new HashMap<String, String>();
		
		
		Query query2 = getEntityManager().createNativeQuery("select cast(value as VARCHAR2(40)),  "
				+ "cast(factor_name as VARCHAR2(120)) from vw_factor_level_info where sample_id = ?1 order "
				+ "by factor_name").setParameter(1,  sampleId);

		List<Object []> resultList = query2.getResultList();
		
		for (Object [] obj : resultList)
			{
			if (obj.length < 2) 
				continue;
			
			map.put((String) obj[1], (String) obj[0]); 
			}
	
		return map;
		}

	
	public String sampleDescriptionForSampleId(String sid)
		{
		Query query2 = getEntityManager().createNativeQuery("select cast(t.sample_name as VARCHAR2(120)) from Sample where sa.sample_id = ?1").setParameter(1,  sid);
		
		List<String> orgList2 = query2.getResultList();

		return (orgList2.size() > 0) ? orgList2.get(0) : "";
		}

	
	public String sampleIdForExpIdAndControlType(String eid, String ct)
		{
		Query query = getEntityManager().createNativeQuery("select cast(s.sample_id as VARCHAR2(9)) from Sample s  where s.exp_id = ?1 and s.control_type = ?2 " )
				.setParameter(1, eid).setParameter(2 , ct);

		String controlId = "1";

		return controlId;
		}

	
	public String getUserSubjectId(String sampleId)
		{
		Query query = getEntityManager().createNativeQuery("select cast(sb.user_subject_id as VARCHAR2(40)) from Sample s inner join Subject sb on "
				+ "s.subject_id = sb.subject_id where s.sample_id =  ?1" ).setParameter(1, sampleId);
		
		List<String> orgList2 = query.getResultList();

		return (orgList2.size() > 0 ? orgList2.get(0) : "");
		}
	
	
	public List<SampleAssayInfo> loadExperimentSampleAssayNamesEfficiently(String expId)
		{
		
		/// JAK issue 176 create view vw_sample_assay_names2
		String queryStr = "select * from vw_sample_assay_names2 where exp_id = ?1 order by sample_id";
		Query query = getEntityManager().createNativeQuery(queryStr).setParameter(1, expId);
		
		List<Object[]> resultList  = query.getResultList();
		List<String> returnList = new ArrayList<String>();
		
		String eid, sid,  aid, aname, rsid, status;
		
		List<SampleAssayInfo> info = new ArrayList<SampleAssayInfo>();
		for (Object [] obj : resultList)
			{		
			if (obj.length < 6)
				continue;
			
			eid = (String) obj[0];
			sid = (String) obj[1];
			rsid = (String) obj[2];
			aid = (String) obj[3];
			aname = (String) obj[4];
			status = (String) obj[5];
	
			info.add(new SampleAssayInfo(sid, rsid, aid, aname, eid ));
			}
		
		return info;
		}


	public List<SimpleClientSampleAssaysBean> loadExperimentSampleAssayStatusEfficiently(String expId)
		{
		/// JAK issue 176 create view vw_sample_assay_names2
		String queryStr = "select * from vw_sample_assay_names2 where exp_id = ?1 order by sample_id, assay_id";
		Query query = getEntityManager().createNativeQuery(queryStr).setParameter(1, expId);
		
		
		List<Object[]> resultList  = query.getResultList();
		
		String eid, sid,  aid, aname, rsid;
		String status;
		
		List<SimpleClientSampleAssaysBean> info = new ArrayList<SimpleClientSampleAssaysBean>();
		
		Map<String, SimpleClientSampleAssaysBean> map = new HashMap<String, SimpleClientSampleAssaysBean>();
	
		for (Object [] obj : resultList)
			{		
			if (obj.length < 6)
				continue;
			
			eid = (String) obj[0];
			sid = (String) obj[1];
			rsid = (String) obj[2];
			aid = (String) obj[3];
			aname = (String) obj[4];
			status = (String) obj[5];

			if (map.get(sid) == null)
				map.put(sid, new SimpleClientSampleAssaysBean(sid, rsid));
			
			map.get(sid).addAssayStatus(aname, status == null ? "" : status.toString()); 
			}
		
		for (String key : map.keySet())
			info.add(map.get(key));
		
		Collections.sort(info, new SimpleClientSampleAssaysBeanComparator());
		
		return info;
		}

	
	
	public List<String> loadExperimentAssaysEfficiently(String expId)
		{
		/// JAK issue 176 create view vw_sample_assay_names2
		String queryStr = "select cast(assay_name as varchar2(150)) from vw_sample_assay_names2 where exp_id = ?1 group by assay_name";
		Query query = getEntityManager().createNativeQuery(queryStr).setParameter(1, expId);
		
		return query.getResultList();
		}
	
	// issue 297
	public Map<String, String> sampleIdToResearcherNameMapForExpId(String expId) 
	    {
		String queryStr = "select cast(s.sample_id as VARCHAR2(9)), cast(s.sample_name as VARCHAR2(120)) "
				+ "from Sample s where exp_id = ?1 order by sample_id";				
		Query query = getEntityManager().createNativeQuery(queryStr).setParameter(1, expId);
		List<Object[]> resultList  = query.getResultList();
		Map<String, String> idMap = new HashMap<String, String>();		
		String sid, sname; 		
		for (Object [] obj : resultList)
			{		
			if (obj.length < 2)
				continue;			
			sid = (String) obj[0];
			sname = (String) obj[1];	
			idMap.put(sid.toString().replace("NaN", "").trim(), sname);
			}	
		return idMap;
	    }
	}

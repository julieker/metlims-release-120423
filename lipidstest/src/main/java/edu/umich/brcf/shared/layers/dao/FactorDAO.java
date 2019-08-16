package edu.umich.brcf.shared.layers.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.ExperimentSetup;
import edu.umich.brcf.shared.layers.domain.Factor;
import edu.umich.brcf.shared.layers.domain.FactorLevel;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.utilpackages.ListUtils;


@Repository
public class FactorDAO  extends BaseDAO
	{
	public Factor createFactor(Factor factor) 
		{
		getEntityManager().persist(factor);
		Query query = getEntityManager().createQuery("select max(f.factorId) from Factor f ");
		String factorId = (String) query.getSingleResult();
		return loadFactorById(factorId);
		}

	
	public Factor loadFactorById(String factorId)
		{
		Factor factor = getEntityManager().find(Factor.class, factorId);
		initializeTheKids(factor, new String[]{"exp"});
		return factor;
		}
	
	
	public FactorLevel loadFactorLevelById(String levelId)
		{
		FactorLevel level = getEntityManager().find(FactorLevel.class, levelId);
		initializeTheKids(level, new String[]{"factor"});
		return level;
		}

	
	public FactorLevel createFactorLevel(FactorLevel level) 
		{
		getEntityManager().persist(level);
		Query query = getEntityManager().createQuery("select max(l.levelId) from FactorLevel l ");
		String levelId = (String) query.getSingleResult();
		return loadFactorLevelById(levelId);
		}

	
	public Boolean factorLabelsMatch(List<String> factorLabels, String expId)
		{
		List<String> existingFactors = loadFactorNamesForExpId(expId);
		
		System.out.println("Existing factors are " + existingFactors);
		if (existingFactors == null || factorLabels == null)
			return false;
		
		if (existingFactors.size() != factorLabels.size())
			return false;
		
		if (!existingFactors.containsAll(factorLabels))
			return false;
		
		return true;
		}
	
	 public List<String> loadFactorNamesForExpId(String expId)
	     {
	     if (!(FormatVerifier.verifyFormat(Experiment.fullIdFormat, expId)))
	             return new ArrayList<String>();
	
	     String queryString = "select cast(factor_name as VARCHAR2(120)) from experimental_factors ef where ef.experiment_id = ?1";
	     Query query = getEntityManager().createNativeQuery(queryString).setParameter(1, expId)  ;
	
	     List<String> results = query.getResultList();
	     return (ListUtils.isNonEmpty(results) ? results : new ArrayList<String>());
	     }

	public void createSampleFactorLevel(ExperimentSetup expSetup) 
		{
		getEntityManager().persist(expSetup);
		}
	
	
	public void deleteSampleFactorLevel(ExperimentSetup setup)
		{
		getEntityManager().remove(setup);
		}
	
	
	public void substituteSampleFactorLevel(Sample sample, FactorLevel newLevel, FactorLevel oldLevel)
		{
		String sample_id = sample.getSampleID();
		String oldLevelId = oldLevel.getLevelId();
		String newLevelId = newLevel.getLevelId();
	
		String queryString = "delete from experiment_setup where sample_id = ?1 " + " and level_id = ?2 ";
		Query query = getEntityManager().createNativeQuery(queryString).setParameter(1, sample_id).setParameter(2, oldLevelId);
		query.executeUpdate();
		
		//queryString = "insert into experiment_setup (sample_id, level_id) values ('" + sample_id + "', '" + newLevelId + "')";
		//Query query = getEntityManager().createNativeQuery(queryString);
		//query.executeUpdate();
		//List<String> controlList = query.getResultList();
		//String levelId = (String) DataAccessUtils.requiredSingleResult(controlList);
		//ExperimentSetup oldSetup = ExperimentSetup.instance(sample, oldLevel);
	
		ExperimentSetup newSetup =  ExperimentSetup.instance(sample, newLevel);
		//getJpaTemplate().remove(oldSetup);  
		//deleteSampleFactorLevel(oldSetup);
		createSampleFactorLevel(newSetup);		
		}

	
	// New and untested/incomplete
	public Map<String, FactorLevel> loadLevelMapForFactor(String factorId)
		{
		Map<String, FactorLevel> level_map = new HashMap<String, FactorLevel>();

		List<String> levelIds = loadLevelIdsForFactorId(factorId);
		for (String id : levelIds)
			{
			FactorLevel f = loadFactorLevelById(id);
			level_map.put(f.getValue(), f);
			}
		
		return level_map;
		}
	
	
	public List<Factor> loadFactorsForExpId(String expId)
		{
		List<Factor> lst =  getEntityManager().createQuery("from Factor f where f.exp.expID = :expId")
				.setParameter("expId", expId).getResultList();
				
		for( Factor factor : lst)
			initializeTheKids(factor, new String[]{"exp", "levels"});
		
		return lst;
		}
	
	

	public List<FactorLevel> loadFactorLevelsForFactor(String fid)
		{
		return  getEntityManager().createQuery("from FactorLevel f where f.factor.factorId = :factorId")
				.setParameter("factorId", fid).getResultList();
		}
	
	
	public String loadLevelIdForSampleAndFactor(String sid, String facid)
		{
		Query query = getEntityManager().createNativeQuery("select cast(es.level_id as VARCHAR2(8)) "
				+ "from experiment_setup es inner join factor_levels fl on fl.level_id = es.level_id "
				+ " where es.sample_id =?1 and fl.factor_id = ?2").setParameter(1, sid).setParameter(2,  facid);
		
		List<String> controlList = query.getResultList();
		String levelId = (String) DataAccessUtils.requiredSingleResult(controlList);
		
		return levelId;
		}
	
	
	public FactorLevel 	loadLevelForSampleAndFactor(String sid, String facid)
		{
		String levelId = loadLevelIdForSampleAndFactor(sid, facid);
		return loadFactorLevelById(levelId);
		}
	
	
	public List<String> loadFactorIdsForExpId(String expId)
		{
		if (!(FormatVerifier.verifyFormat(Experiment.idFormat, expId)))
			return new ArrayList<String>();
		
		String queryString = "select cast(factor_id as VARCHAR2(7)) from experimental_factors ef where ef.experiment_id = ?1 ";
		Query query = getEntityManager().createNativeQuery(queryString).setParameter(1, expId);

		List<String> results = query.getResultList();
		
		return (results != null && results.size() > 0 ? results : new ArrayList<String>());
		}
	
			
	public List<String> loadLevelIdsForExpId(String expId)
		{
		if (!(FormatVerifier.verifyFormat(Experiment.idFormat, expId)))
			return new ArrayList<String>();
		
		String queryString = "select cast(f.level_id as VARCHAR2(8)) from factor_levels f where f.factor_id "
				+ " in (select factor_id from experimental_factors where experiment_id = ?1";
				
		Query query = getEntityManager().createNativeQuery(queryString).setParameter(1, expId);
		
		return query.getResultList();
		}
	
	
	public List<String> loadLevelIdsForFactorId(String factorId)
		{
		String queryString = "select cast(level_id as VARCHAR2(8)) from factor_levels where factor_id = ?1";
		Query query = getEntityManager().createNativeQuery(queryString).setParameter(1,factorId);
		
		return query.getResultList();
		}
	
	
	public String loadFactorIdForFactorNameAndExpId(String factorName, String expId)
		{
		List<Factor> factorsForExp = loadFactorsForExpId(expId);
		for (Factor f : factorsForExp)
			if (factorName.equals(f.getFactorName()))
				return f.getFactorId();
		
		return "";
		}
	
	
	public Factor loadFactorForNameAndExp(String eid, String fname)
		{
		if (!(FormatVerifier.verifyFormat(Experiment.fullIdFormat, eid)))
			return null;
		
		if (!StringUtils.isNonEmpty(fname))
			return null;
		
		List<Factor> lst = loadFactorsForExpId(eid);
		
		for (int i = 0; i < lst.size(); i++)
			if (StringUtils.trimEquals(lst.get(i).getFactorName(), fname))
				return lst.get(i);
				
		return null;
		}
	
	
	public int updateLevelIdForSample(String levelId, String oldLevelId, String sid)
		{
		if (StringUtils.checkEmptyOrNull(sid))
			return 0;
		if (!FormatVerifier.verifyFormat(Sample.idFormat, sid))
			return 0;
	
		String queryStr = "update experiment_setup es set level_id = ?1 " + " where sample_id = ?2 and level_id = ?3 " ;
		Query query = getEntityManager().createNativeQuery(queryStr).setParameter(1,  levelId).setParameter(2, sid).setParameter(3, oldLevelId);
		
		int nUpdates = query.executeUpdate();
		return nUpdates;
		}
	
	
	public Boolean factorValueExistsForFactor(String fid, String fvalue)
		{
		List<FactorLevel> levels = loadFactorLevelsForFactor(fid);
		
		if (levels == null || levels.size() == 0)
			return false;
		
		for (FactorLevel level :  levels)
			if (level.getValue().equals(fvalue))
				return true;
	
		return false;
		}

	
	public FactorLevel loadLevelForFactorAndValue(String facid, String fvalue)
		{
		List<FactorLevel> levels = loadFactorLevelsForFactor(facid);
		
		if (levels == null || levels.size() == 0)
			return null;
		
		for (FactorLevel level :  levels)
			if (StringUtils.trimEquals(level.getValue(), fvalue))
				return level;
	
		return null;
		}

	
	public String getLevelIdForFactorAndValue(String facid, String fvalue)
		{
		FactorLevel level = loadLevelForFactorAndValue(facid, fvalue);
		if (level == null)
			return "";
		
		return level.getLevelId();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


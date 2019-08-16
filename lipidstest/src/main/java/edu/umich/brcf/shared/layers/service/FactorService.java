///////////////////////////////////////////
// Writtten by Anu Janga
// Revisited by Jan Wigginton August 2015
///////////////////////////////////////////

package edu.umich.brcf.shared.layers.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import edu.umich.brcf.shared.layers.dao.ExperimentDAO;
import edu.umich.brcf.shared.layers.dao.FactorDAO;
import edu.umich.brcf.shared.layers.domain.Factor;
import edu.umich.brcf.shared.layers.domain.FactorLevel;
import edu.umich.brcf.shared.util.io.StringUtils;


@Transactional
public class FactorService
	{
	FactorDAO factorDao;
	ExperimentDAO experimentDao;

	public void setFactorDao(FactorDAO factorDao)
		{
		this.factorDao = factorDao;
		}

	
	public void setExperimentDao(ExperimentDAO experimentDao)
		{
		this.experimentDao = experimentDao;
		}
	

	public Boolean areFactorsForExpId(String expId, Set<String> candidateNamesSet)
		{
		List<String> candidateNameList = new ArrayList<String>();
		for (String name : candidateNamesSet)
			candidateNameList.add(name);

		return areFactorsForExpId(expId, candidateNameList);
		}

	public Integer countFactorsForExperiment(String expId)
		{
		List<String> names = this.getFactorNamesForExpId(expId);
		
		return (names == null ? 0 :  names.size());
		}	
	
	public List<String> getFactorNamesForExpId(String expId)
		{
		return experimentDao.getFactorNamesForExpId(expId);
		}


	
	public Boolean areFactorsForExpId(String expId, List<String> candidateNames)
		{
		List<String> existingFactorNames = experimentDao.getFactorNamesForExpId(expId);
		for (String name : candidateNames)
			if (!existingFactorNames.contains(name))
				return false;
		
		return true;
		}

	
	public Boolean allFactorsPresent(String expId, Set<String> candidateNamesSet)
		{
		List<String> candidateNameList = new ArrayList<String>();
		for (String name : candidateNamesSet)
			candidateNameList.add(name);

		return allFactorsPresent(expId, candidateNameList);
		}

	
	public Boolean allFactorsPresent(String expId, List<String> candidateNames)
		{
		if (candidateNames == null)
			return false;

		List<String> existingFactorNames = experimentDao.getFactorNamesForExpId(expId);

		if (candidateNames.size() != existingFactorNames.size())
			return false;

		for (String existingFactor : existingFactorNames)
			if (!candidateNames.contains(existingFactor))
				return false;
		
		return true;
		}
	

	public Factor loadFactorForNameAndExp(String eid, String fname)
		{
		return factorDao.loadFactorForNameAndExp(eid, fname);
		}

	public List<Factor> loadFactorsForExperiment(String eid)
		{
		return factorDao.loadFactorsForExpId(eid);
		}

	public List<FactorLevel> loadFactorLevelsForFactor(String fid)
		{
		return factorDao.loadFactorLevelsForFactor(fid);
		}

	public String loadFactorIdForFactorNameAndExpId(String factorName, String expId)
		{
		List<Factor> factorsForExp = factorDao.loadFactorsForExpId(expId);
		for (Factor f : factorsForExp)
			if (factorName.equals(f.getFactorName()))
				return f.getFactorId();
	
		return "";
		}

	
	public Boolean factorValueExistsForFactor(String fid, String fvalue)
		{
		List<FactorLevel> levels = loadFactorLevelsForFactor(fid);

		if (levels == null || levels.size() == 0)
			return false;

		for (FactorLevel level : levels)
			if (level.getValue().equals(fvalue))
				return true;

		return false;
		}
	

	public FactorLevel loadLevelForFactorAndValue(String facid, String fvalue)
		{
		List<FactorLevel> levels = loadFactorLevelsForFactor(facid);

		if (levels == null || levels.size() == 0)
			return null;

		for (FactorLevel level : levels)
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

	
	public FactorLevel loadLevelForSampleIdAndFactorId(String sid, String facid)
		{
		return factorDao.loadLevelForSampleAndFactor(sid, facid);
		}
	}

// SampleService.java
// Written by Anu Janga
// Revised 2016-2017 by Jan Wigginton

package edu.umich.brcf.shared.layers.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.metabolomics.layers.dao.GenusSpeciesDAO;
import edu.umich.brcf.metabolomics.layers.dao.TableAccessDAO;
import edu.umich.brcf.metabolomics.layers.domain.GenusSpecies;
import edu.umich.brcf.shared.layers.dao.AssayDAO;
import edu.umich.brcf.shared.layers.dao.ControlDAO;
import edu.umich.brcf.shared.layers.dao.ControlTypeDAO;
import edu.umich.brcf.shared.layers.dao.ExperimentDAO;
import edu.umich.brcf.shared.layers.dao.FactorDAO;
import edu.umich.brcf.shared.layers.dao.IdGeneratorDAO;
import edu.umich.brcf.shared.layers.dao.LocationDAO;
import edu.umich.brcf.shared.layers.dao.SampleAssayStatusDAO;
import edu.umich.brcf.shared.layers.dao.SampleDAO;
import edu.umich.brcf.shared.layers.dao.SampleStatusDAO;
import edu.umich.brcf.shared.layers.dao.SampleTypeDAO;
import edu.umich.brcf.shared.layers.dao.SubjectDAO;
import edu.umich.brcf.shared.layers.dao.UserDAO;
import edu.umich.brcf.shared.layers.domain.Assay;
import edu.umich.brcf.shared.layers.domain.BiologicalSample;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.ExperimentSetup;
import edu.umich.brcf.shared.layers.domain.Factor;
import edu.umich.brcf.shared.layers.domain.FactorLevel;
import edu.umich.brcf.shared.layers.domain.Location;
import edu.umich.brcf.shared.layers.domain.Preparation;
import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.domain.SampleAssay;
import edu.umich.brcf.shared.layers.domain.SampleStatus;
import edu.umich.brcf.shared.layers.domain.SampleType;
import edu.umich.brcf.shared.layers.domain.Subject;
import edu.umich.brcf.shared.layers.dto.SampleDTO;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.METWorksException;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.structures.Pair;
import edu.umich.brcf.shared.util.structures.SelectableObject;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;
import edu.umich.brcf.shared.util.datacollectors.AssaySelectionSet;
import edu.umich.brcf.shared.util.datacollectors.FactorValueSet;
import edu.umich.brcf.shared.util.datacollectors.Mrc2SubmissionSheetData;
import edu.umich.brcf.shared.util.datacollectors.SampleAssayInfo;
import edu.umich.brcf.shared.util.datacollectors.SimpleClientSampleAssaysBean;
// issue 249 import edu.umich.brcf.shared.util.io.StringUtils;


@Transactional(rollbackFor = Exception.class)
public class SampleService
	{
	SampleDAO sampleDao;
	ExperimentDAO expDao;
	GenusSpeciesDAO genusSpeciesDao;
	SampleStatusDAO statusDao;
	SampleAssayStatusDAO sampleAssayStatusDao;
	SampleTypeDAO sampleTypeDao;
	UserDAO userDao;
	IdGeneratorDAO idGeneratorDao;
	FactorDAO factorDao;
	SubjectDAO subjectDao;
	AssayDAO assayDao;
	ControlDAO controlDao;
	ControlTypeDAO controlTypeDao;
	LocationDAO locationDao;
	TableAccessDAO tableAccessDao;
	
	// Issue 297
	public Map<String, String> sampleIdToResearcherNameMapForExpId(String eid)
	    {
	    return sampleDao.sampleIdToResearcherNameMapForExpId(eid);
	    }

	// Issue 249
	public List<String> samplesAssociatedWithAssay(String expId, String assayID)
        {
	    return assayDao.samplesAssociatedWithAssay(expId, assayID);		
        }

	public List<Sample> allSamples()
		{
		return sampleDao.allSamples();
		}

	public Sample loadById(String id)
		{
		Assert.notNull(id);
		return sampleDao.loadById(id);
		}

    // issue 214
    public List<String[]> getExpiredSamples(int lowerLimit, int upperLimit)
        {
        return sampleDao.gerExpiredSamples(lowerLimit, upperLimit);
        } 
    
	public Map<String, List<String>> getFactorValueMapForExpId(String expId)
		{
		return sampleDao.getFactorValueMapForExpId(expId);
		}
	
	public List<Sample> loadBasicSamplesForExpId(String expId)
		{
		return sampleDao.loadBasicSamplesForExpId(expId);
		}
	
	public Sample loadBasicsById(String id)
		{
		Assert.notNull(id);
		return sampleDao.loadById(id);
		}

	public Sample loadWithFactorsById(String sampleId)
		{
		return sampleDao.loadWithFactorsById(sampleId);
		}

	public Sample loadSampleAlongWithExpById(String sampleID)
		{
		return sampleDao.loadSampleAlongWithExpById(sampleID);
		}

	public List<Sample> loadSampleByExperiment(Experiment exp)
		{
		return sampleDao.loadSampleByExperiment(exp);
		}

	public List<Sample> loadSampleForStatusTracking(String expId)
		{
		return sampleDao.loadSampleForStatusTracking(expId);
		}

	public List<Sample> loadSampleForAssayStatusTracking(String expId)
		{
		return sampleDao.loadSampleForAssayStatusTracking(expId);
		}

	public List<Sample> loadSampleByProject(Project proj)
		{
		return sampleDao.loadSampleByProject(proj);
		}

	public List<Sample> loadSampleByType(SampleType st)
		{
		return sampleDao.loadSampleByType(st);
		}

	public List<Sample> loadSampleByLocation(String locID)
		{
		return sampleDao.loadSampleByLocation(locID);
		}

	public String sampleNameForId(String id)
		{
		return sampleDao.sampleNameForId(id);
		}

	public List<SampleDTO> loadDTOsForExpId(String expId)
	{
	List<Sample> samples =  sampleDao.loadSampleForStatusTracking(expId);
	
	List<SampleDTO> dtos = new ArrayList<SampleDTO>();
	
	

	for (Sample sample : samples)
		dtos.add(new SampleDTO(sample.getSampleID(), sample.getSampleName(), sample.getExp().getExpID(), 
				sample.getUserDescription(), sample.getGenusOrSpecies().getGsID(), sample.getLocID(),
				sample.getUserDefSampleType(), sample.getVolume(), sample.getVolUnits(),
				sample.getStatus().getId(), sample.getSampleControlType(), 
				sample.getDateCreated(), sample.getSampleType().getSampleTypeId(), null, null));
	
	return dtos;
	}

	public int updateExistingSamples(List<SampleDTO> dtos) 
		{
		if (dtos == null) return 0;
		
		for (SampleDTO dto : dtos)
			{
			Assert.notNull(dto);
			Experiment exp = expDao.loadById(dto.getExpID());
			SampleStatus ss = statusDao.loadById(dto.getStatus() == null ? 'S' : dto.getStatus());
			SampleType st = sampleTypeDao.loadById(dto.getSampleTypeId());
			GenusSpecies gs = genusSpeciesDao.loadById(dto.getGenusOrSpeciesID());
			Subject su = subjectDao.loadSubjectById(dto.getSubjectId());
			Sample sample = null;
			try {
				sample = sampleDao.loadById(dto.getSampleId());
				sample.update(dto, exp,gs, ss, st, su);
				} 
			catch (Exception e) 
				{
				throw new RuntimeException("Unable to update sample " + dto.getSampleId()); 
				}
			}
		
		return dtos.size();
		}
	
	public List<String> getMatchingSamples(String input)
		{
		return sampleDao.getMatchingSamples(input);
		}

	public String getUserSubjectId(String sampleId)
		{
		return sampleDao.getUserSubjectId(sampleId);
		}

	public List<String> sampleIdsForExpId(String eid)
		{
		return sampleDao.sampleIdsForExpId(eid);
		}
	
	// issue 268 include assay
	public List<String> sampleIdsForExpIdAssayId(String eid, String assayId)
	    {
	    return sampleDao.sampleIdsForExpIdAssayId(eid, assayId);
	    }
	
	// JAK issue 180 allow for samples in ascending order
	public List<String> orderedSampleIdsForExpId(String eid, Boolean desc)
	    {
	return sampleDao.orderedSampleIdsForExpId(eid, desc);
	    }
	
	// issue 298
	public Map<String, String> sampleIdMapForExpId(String expId)
		{
		List<String> ids = sampleIdsForExpId(expId);		
		Map<String, String> map = new HashMap<String, String>();
		if (ids != null)
			for (String id : ids)
				map.put(id, null);		
		return map;
		}

	public List<String> sampleIdsForExpIdAndAssayId(String eid, String aid)
		{
		return sampleDao.sampleIdsForExpIdAndAssayId(eid, aid);
		}

	public List<String> sampleIdsForExpIdAndAssayIdMinusExcluded(String eid,
			String aid)
		{
		return sampleDao.sampleIdsForExpIdAndAssayIdMinusExcluded(eid, aid);
		}

	public List<String> excludedSampleIdsForExpIdAndAssayId(String eid,
			String aid)
		{
		return sampleDao.excludedSampleIdsForExpIdAndAssayId(eid, aid);
		}

	public String sampleIdForExpIdAndControlType(String eid, String ct)
		{
		return sampleDao.sampleIdForExpIdAndControlType(eid, ct);
		}
	
	public List<String> getFactorValuesById(String sampleId)
		{
		return sampleDao.getFactorValuesById(sampleId);
		}

	public List<String> getFactorValuesByIdSortedByName(String sampleId)
		{
		return sampleDao.getFactorValuesByIdSortedByName(sampleId);
		}

	
	public Map<String, String> getFactorValueMapForId(String sampleId)
		{
		return sampleDao.getFactorValueMapForId(sampleId);
		}
	
	
	public Sample updateSingleSample(SampleDTO dto, List<Pair> factorInfo, Boolean subjectIsNew)
		{
		Assert.notNull(dto);
		
		Sample sample = null;
		try 
			{
			Experiment exp = grabExperimentIfValid(dto.getExpID());
			SampleType st = grabSampleTypeIfValid(dto.getSampleTypeId());
			GenusSpecies gs = grabGenusSpeciesIfValid(dto);
			SampleStatus ss = grabSampleStatusIfValid(dto);
			grabLocationIfValid(dto);   /// just checking validity
			
			Subject subject = null;
			if (!subjectIsNew)
				subject = grabSubjectIfValid(dto);
			else
				subject = grabNewSubject(dto, gs);
			
			sample = sampleDao.loadById(dto.getSampleID());
			sample.update(dto, exp, gs, ss, st, subject);
			} 
		catch (RuntimeException e) { e.printStackTrace(); throw e; } 
		catch (Exception e) 
			{ 
			e.printStackTrace(); 
			throw new RuntimeException("Other error while updating sample " + dto.getSampleID() + " please check for invalid field sizes.");
			}

		try { updateFactorInfo(factorInfo, sample); }
		catch (RuntimeException e) { throw e; }
		catch (Exception e) { e.printStackTrace();  throw new RuntimeException("Other error while saving factor information"); }
	
		return sample;
		}	


	private void updateFactorInfo(List<Pair> factorInfo, Sample sample)
		{
		for (Pair pr : factorInfo)
			{
			String fname = pr.getId();
			String fvalue = pr.getValue();
			String sid = sample.getSampleID();
			
			if (!isValueChanged(sample, fname, fvalue))
				continue;

			Factor factor = grabFactorIfValid(sample.getExp().getExpID(), fname);
			
			// check if level already exists for this factor. If so assign sample to that levelId
			String levelId = factorDao.getLevelIdForFactorAndValue(factor.getFactorId(), fvalue);
			String oldLevelId = factorDao.loadLevelIdForSampleAndFactor(sid,factor.getFactorId());
			
			if (!StringUtils.checkEmptyOrNull(levelId))
				{
				factorDao.updateLevelIdForSample(levelId, oldLevelId, sid);
				continue;
				}

			// factor level doesn't exist already swap in the new factor level
			FactorLevel newLevel = factorDao.createFactorLevel(FactorLevel.instance(fvalue, factor));
			FactorLevel oldLevel = factorDao.loadFactorLevelById(oldLevelId);
			factorDao.substituteSampleFactorLevel(sample, newLevel, oldLevel);
			}
		}
	
	
	public Factor grabFactorIfValid(String eid, String fname)
		{		
		Factor factor = null;
		try
			{
			factor = factorDao.loadFactorForNameAndExp(eid, fname);
			factor.getFactorName();
			}
		catch (Exception e)  	
			{ 
			e.printStackTrace(); 
			throw new RuntimeException("Factor name  " + fname + " does not exist"); 
			}
	
		return factor;
		}
	
	//factorValuesForSampleMap
	public void updateFactorValue(String sid, String fname, String fvalue)
		{
		Sample sample = sampleDao.loadById(sid);

		if (!isValueChanged(sample, fname, fvalue))
			return;

		Factor factor = factorDao.loadFactorForNameAndExp(sample.getExp().getExpID(), fname);
		if (factor == null)
			return;

		// check if level already exists for this factor. If so assign sample to that levelId
		String levelId = factorDao.getLevelIdForFactorAndValue(factor.getFactorId(), fvalue);
		String oldLevelId = factorDao.loadLevelIdForSampleAndFactor(sid, factor.getFactorId());

		if (!StringUtils.checkEmptyOrNull(levelId))
			{
			factorDao.updateLevelIdForSample(levelId, oldLevelId, sid);
			return;
			}

		// factor level doesn't exist already swap in the new factor level
		FactorLevel newLevel = factorDao.createFactorLevel(FactorLevel.instance(fvalue, factor));
		FactorLevel oldLevel = factorDao.loadFactorLevelById(oldLevelId);
		factorDao.substituteSampleFactorLevel(sample, newLevel, oldLevel);
		}

	
	private boolean isValueChanged(Sample sample, String fname, String fvalue)
		{
		List<ExperimentSetup> designSet = sample.getFactorLevels();

		for (int i = 0; i < designSet.size(); i++)
			{
			FactorLevel fLevel = designSet.get(i).getLevel();
			String levelId = fLevel.getLevelId();
			FactorLevel level = factorDao.loadFactorLevelById(levelId);

			if (StringUtils.trimEquals(level.getFactor().getFactorName(), fname) && !StringUtils.trimEquals(level.getValue(), fvalue))
				return true;
			}

		return false;
		}

	
	public int saveSamples(Mrc2SubmissionSheetData data) throws METWorksException
		{
		return saveSamplesNew(data.getExpId(), data.samplesMetadata.grabDTOList(), data.pullFactorMap(), data.pullAssayMap(true));
		}
	
	
	public int saveSamplesNew(String expID, List<SampleDTO> sampleDtoList, Map<String, List<String>> factor_map, Map<String, List<String>> assay_map) throws METWorksException
		{
		Experiment exp = null;
		try { exp = grabExperimentIfValid(expID); } 
		catch (RuntimeException e) { throw new METWorksException(e.getMessage()); }
		
		Map<String, Sample> sample_map = new HashMap<String, Sample>();
		List<Sample> samples = new ArrayList<Sample>();

		for (SampleDTO dto : sampleDtoList)
			{
			Assert.notNull(dto);
			
			try 
				{
				SampleType st = grabSampleTypeIfValid(dto.getSampleTypeId());
				GenusSpecies gs = grabGenusSpeciesIfValid(dto);
			//	SampleStatus ss = grabSampleStatusIfValid(dto);
				Location loc = grabLocationIfValid(dto);
			
				Subject su = null;
				if (subjectDao.subjectExistsForExperimentAndName(dto.getSubjectId(), exp))
					su = subjectDao.loadSubjectByNameAndExp(dto.getSubjectId(), exp);
				else	
					su = this.grabNewSubject(dto, gs);
			
				Sample sample = BiologicalSample.instance(dto.getSampleID(), dto.getSampleName(), exp, su, gs, dto.getLocID(), dto.getUserDefSampleType(), dto.getUserDefGOS(),
						dto.getVolume(), dto.getVolUnits(), statusDao.loadById('S'), Calendar.getInstance(), st, null, null);

				sampleDao.createSample(sample);
				sample_map.put(sample.getSampleID(), sample);
				samples.add(sample);
				}
			catch (RuntimeException g) { throw new METWorksException("Sample save error (" + dto.getSampleID() + "). " + g.getMessage()); }
			catch (Exception f)  {  return 0;  }
			}
			
		if (samples.size() > 0)
			{
			// factorMap is factorName -> factorValues across samples
			Iterator<String> factorIterator = factor_map.keySet().iterator();
			while (factorIterator.hasNext())
				{
				String factorName = factorIterator.next();
				Map<String, FactorLevel> level_map = new HashMap<String, FactorLevel>();
				
				Factor factor = factorDao.createFactor(Factor.instance(factorName, exp));
				for (int l = 0; l < factor_map.get(factorName).size(); l++)
					{
					String level = factor_map.get(factorName).get(l);
					if (!level_map.containsKey(level))
						level_map.put(level, factorDao.createFactorLevel(FactorLevel.instance(level, factor)));
					factorDao.createSampleFactorLevel(ExperimentSetup.instance(samples.get(l), level_map.get(level)));
					}
				}

			Iterator<String> assayIterator = assay_map.keySet().iterator();
			while (assayIterator.hasNext())
				{
				String sid = assayIterator.next();
				for (String assayName : assay_map.get(sid))
					{
					Assay assay = assayDao.loadAssayByName(assayName);
					assayDao.createSampleAssay(SampleAssay.instance(sample_map.get(sid), assay, sampleAssayStatusDao.loadById('Q')));
					}
				}
			}

		return samples.size();
		}


	public Factor saveSampleFactor(FactorValueSet set)
		{
		Assert.notNull(set);
		
		Experiment experiment = expDao.loadById(set.getExpId());
		Factor factor = null;
	
		try 
			{
			factor = factorDao.createFactor(Factor.instance(set.getFactorName(), experiment));
			factor.getFactorId();
			}
		catch (Exception e) { e.printStackTrace(); throw new RuntimeException("Unable to create new factor " + set.getFactorName()); }
			
		Map<String, FactorLevel> level_map = new HashMap<String, FactorLevel>();

		for (int i = 0; i < set.getIdsAndValues().size(); i++)
			{
			String id = set.getIdsAndValues().get(i).getId();
			String value = set.getIdsAndValues().get(i).getValue();

			Sample sample = loadById(id);
			if (!level_map.containsKey(value))
				level_map.put(value, factorDao.createFactorLevel(FactorLevel.instance(value, factor)));

			try { factorDao.createSampleFactorLevel(ExperimentSetup.instance(sample, level_map.get(value))); }
			catch (Exception e) { e.printStackTrace(); throw new RuntimeException("Unable to save new factor value" + e.getMessage()); }
			}

		return factor;
		}
   
	// issue 205
	public String getNextSampleID(boolean increment, Integer incrementNumber)
	    {
	    return ((String) idGeneratorDao.getNextValue("Sample", increment, incrementNumber));
	    }

	public String getNextSampleID()
		{
		return getNextSampleID(true,1);
		}

	public String getNextSubjectID()
		{
		return ((String) idGeneratorDao.getNextValue("Subject"));
		}

	
	public String getNextControlID()
		{
		return ((String) idGeneratorDao.getNextValue("Control"));
		}


	public void updateStatus(List<SelectableObject> samples, String status)
		{
		for (SelectableObject so : samples)
			{
			if (so.isSelected())
				{
				Sample s = null;
				try { s = sampleDao.loadById(((Sample) so.getSelectionObject()).getId()); } 
				catch (Exception e) { e.printStackTrace(); }

				if (s == null) continue;

				Character temp = status.charAt(0);
				switch (temp)
					{
				//Issue 222
					case 'S': s.setStoredStatus(); break;
					case 'P': s.setPrepStatus(); break;
					case 'R': s.setProcessedStatus(); break;
					case 'I': s.setInjectedStatus(); break;
					case 'C': s.setCompletedStatus(); break;
					case 'T': s.setDiscardedStatus(); break;
					case 'B': s.setReturnedStatus(); break;
					default: throw new IllegalStateException(temp + " is not mapped!");
					}
				}
			}
		}
	
	
	public void updateVolume(List<SelectableObject> samples, Double vVolume, String volUnits, Boolean warnUnits) 
		{
		for(SelectableObject so: samples)
			{
			if(so.isSelected())
				{
				Sample s = sampleDao.loadById( ((Sample) so.getSelectionObject()).getSampleId());

				if (warnUnits && !(vVolume.equals(s.getVolUnits())))
						throw new RuntimeException("Warning : Volume units for sample " + s.getSampleId() + " do not match the original.  Please resubmit if this is intentional. ");

				Double dblValuePos = Math.abs(vVolume);
				if(dblValuePos >= Math.pow(10, 13))
				{
					throw new RuntimeException("Error : Volume is too large.  Please make sure volume conforms to number(22,9)");
					
				}
				s.setCur_volume(BigDecimal.valueOf(vVolume));
				s.setVolUnits(volUnits);
				}
			}
		}	

	// Issue 249
	public void deleteSamplesAssociatedNotChosen (AssaySelectionSet set, String expId, String assayId, List<String> sampleIdsAlreadyExist)
	    {
		//Issue 249
		List<String> sampleIds = set.getSelectedAssayIds();	
		List<String> sampleIdsExistNotChosen = new ArrayList<String> ();		
		for (String strSample : sampleIdsAlreadyExist) 
			sampleIdsExistNotChosen.add(strSample);		
		sampleIdsExistNotChosen.removeAll(sampleIds);		
		if (sampleIdsExistNotChosen.size() > 0)
		    assayDao.deleteSamplesAssociatedWithAssayNotChosen(set.getExpId(), set.getAssayId(), StringUtils.buildDatabaseTupleListFromList(sampleIdsExistNotChosen) );
	    }
	
	//Issue 249
	public List<String> removeSampleIdsAlreadyExist(List<String> sampleIds, List<String> sampleIdsAlreadyExist)
	    {
		sampleIds.removeAll(sampleIdsAlreadyExist);
		return sampleIds;
	    }
	
	public void saveSampleAssay(AssaySelectionSet set)
		{		
		List<String> sampleIds = set.getSelectedAssayIds();
		if (sampleIds.size() == 0)
	        {
			assayDao.deleteSamplesAssociatedWithAssay(set.getExpId(), set.getAssayId());
			return;
		    }
		Assay assay = assayDao.loadAssayByID(set.getAssayId());	
		List<String> sampleIdsAlreadyExist = samplesAssociatedWithAssay(set.getExpId(), set.getAssayId());	
		//Issue 249
		deleteSamplesAssociatedNotChosen(set,set.getExpId(), set.getAssayId(), sampleIdsAlreadyExist);				
		sampleIds = removeSampleIdsAlreadyExist(sampleIds, sampleIdsAlreadyExist);				
		// Issue 249	
		for (int i = 0; i < sampleIds.size(); i++)
			{
			Sample sample = sampleDao.loadById(sampleIds.get(i));
			assayDao.createSampleAssay(SampleAssay.instance(sample, assay, sampleAssayStatusDao.loadById('Q')));
			}
		}
	
	public boolean isValidSampleSearch(final String s)
		{
		Sample sample;
		if (StringUtils.isEmptyOrNull(s))
			return false;

		if (FormatVerifier.verifyFormat(Sample.idFormat, s.toUpperCase()))
			try { sample = loadById(s); } 
			catch (EmptyResultDataAccessException e) { sample = null; }
		else
			try { sample = loadById(StringParser.parseId(s)); }
			catch (EmptyResultDataAccessException e) { sample = null; }
			
		return (sample != null);
		}

	
	public void updateLocation(List<SelectableObject> samples, String location)
		{
		for (SelectableObject so : samples)
			if (so.isSelected())
				{
				Sample s = sampleDao.loadById(((Sample) so.getSelectionObject()).getId());
				String oldId = s.getLocID();
				s.setLocID(location);
				}
		}
	

	public void setExpDao(ExperimentDAO expDao)
		{
		this.expDao = expDao;
		}

	public SampleDAO getSampleDao()
		{
		return sampleDao;
		}

	public void setSampleDao(SampleDAO sampleDao)
		{
		Assert.notNull(sampleDao);
		this.sampleDao = sampleDao;
		}

	public void setGenusSpeciesDao(GenusSpeciesDAO gsDao)
		{
		this.genusSpeciesDao = gsDao;
		}

	public void setStatusDao(SampleStatusDAO statusDao)
		{
		this.statusDao = statusDao;
		}

	public void setSampleTypeDao(SampleTypeDAO stDao)
		{
		this.sampleTypeDao = stDao;
		}

	public void setUserDao(UserDAO userDao)
		{
		this.userDao = userDao;
		}

	public void setIdGeneratorDao(IdGeneratorDAO idGeneratorDao)
		{
		this.idGeneratorDao = idGeneratorDao;
		}

	public void setFactorDao(FactorDAO factorDao)
		{
		this.factorDao = factorDao;
		}

	public void setSubjectDao(SubjectDAO subjectDao)
		{
		this.subjectDao = subjectDao;
		}

	public void setAssayDao(AssayDAO assayDao)
		{
		this.assayDao = assayDao;
		}

	public void setControlDao(ControlDAO controlDao)
		{
		this.controlDao = controlDao;
		}

	
	public LocationDAO getLocationDao()
		{
		return locationDao;
		}

	public void setLocationDao(LocationDAO locationDao)
		{
		this.locationDao = locationDao;
		}

	public void setControlTypeDao(ControlTypeDAO controlTypeDao)
		{
		this.controlTypeDao = controlTypeDao;
		}

	public SampleAssayStatusDAO getSampleAssayStatusDao()
		{
		return sampleAssayStatusDao;
		}

	public void setSampleAssayStatusDao(SampleAssayStatusDAO sampleAssayStatusDao)
		{
		this.sampleAssayStatusDao = sampleAssayStatusDao;
		}

	public List<Preparation> getPrepList(Sample sample)
		{
		return null;
		}
	
	
	public Experiment grabExperimentIfValid(String expId)
		{
		Experiment exp = null; 
		try
			{
			exp = expDao.loadSimplestById(expId);
			exp.getExpDescription();
			}
		catch (Exception e) 
			{
			e.printStackTrace();
			throw new RuntimeException("Experiment missing : Unable to load experiment " + expId);
			}
		
		return exp;
		}
	
	
	public SampleType grabSampleTypeIfValid(String stid)
		{
		SampleType st = null;
		try
			{
			st = sampleTypeDao.loadById(stid);
			st.getDescription();
			}
		catch (Exception e) 
			{
			e.printStackTrace();
			throw new RuntimeException("Sample type missing : Unable to locate sample type with id " + stid);
			}
		
		return st;
		}
	
	public GenusSpecies grabGenusSpeciesIfValid(SampleDTO dto)
		{
		GenusSpecies gs = null;
		try
			{
			gs = genusSpeciesDao.loadById(dto.getGenusOrSpeciesID());
			gs.getGenusName();
			}
		catch (Exception e) 
			{
			e.printStackTrace();
			throw new RuntimeException("Genus species missing : Unable to locate genus species with id " + dto.getGenusOrSpeciesID());
			}
		return gs;
		}

	
	public SampleStatus grabSampleStatusIfValid(SampleDTO dto)
		{
		SampleStatus ss = null;	
		try
			{
			ss = statusDao.loadById(dto.getStatus());
			ss.getStatusValue();
			}
		catch (Exception e) 
			{
			e.printStackTrace();
			throw new RuntimeException("Sample status missing : Unable to locate sample status with id " + dto.getStatus());
			}
		return ss;
		}
	
	
	public Location grabLocationIfValid(SampleDTO dto)
		{
		Location loc = null;
		try
			{
			loc = locationDao.loadById(dto.getLocID());
			loc.getDescription();
			}
		catch (Exception e) 
			{
			e.printStackTrace();
			throw new RuntimeException("Location code is incorrect : Location " + dto.getLocID() + " does not exist");
			}
		return loc;
		}
	
	
	public Subject grabSubjectIfValid(SampleDTO dto)
		{
		Subject su;
		try
			{
			su = subjectDao.loadSubjectById(dto.getSubjectId());
			su.getSubjectId();
			}
		catch (Exception e)
			{
			e.printStackTrace();
			throw new RuntimeException("Subject id is incorrect : Subject " + dto.getSubjectId() + " does not exist");
			}
		
		return su;
		}
	
	
	public Subject grabNewSubject(SampleDTO dto, GenusSpecies gs)
		{
		Subject subject;
		try 
			{
			subject = Subject.instance(getNextSubjectID(), gs.getNcbiID(), dto.getSubjectName());
			subjectDao.createSubject(subject);
			}
		catch (Exception e)
			{
			e.printStackTrace();
			throw new RuntimeException("Error while creating subject  " + dto.getSubjectName());
			}
		
		return subject;
		}
	
	
	
	public List<SampleAssayInfo> loadExperimentSampleAssayNamesEfficiently(String expId)
		{
		return sampleDao.loadExperimentSampleAssayNamesEfficiently(expId);
		}

	
	public List<SimpleClientSampleAssaysBean> loadExperimentSampleAssayStatusEfficiently(String expId)
		{
		List<SimpleClientSampleAssaysBean> lst = sampleDao.loadExperimentSampleAssayStatusEfficiently(expId);
		
	/*	Map<String, String> statusMap = tableAccessDao.getStatusMap();
		statusMap.put(null, "");
		String encodedValue = null;
		
		for (SimpleClientSampleAssaysBean bean : lst)
			{
			for (Pair pair : bean.getAssayNamesAndStatuses())
				{
				encodedValue = pair.getValue();
				pair.setValue(statusMap.get(encodedValue));
				}
			}
		
		 */
		return lst;
		}
	
	
	public List<String> loadExperimentAssaysEfficiently(String expId)
		{
		return sampleDao.loadExperimentAssaysEfficiently(expId);
		}

	public TableAccessDAO getTableAccessDao()
		{
		return tableAccessDao;
		}

	public void setTableAccessDao(TableAccessDAO tableAccessDao)
		{
		this.tableAccessDao = tableAccessDao;
		}
	}



































////////////////  SCRAP CODE /////////////////////////////

// PLATEITEM_MAP
/*
public Sample save(SampleDTO dto)
	{
	Assert.notNull(dto);
	
	Experiment exp = expDao.loadById(dto.getExpID());
	GenusSpecies gs = genusSpeciesDao.loadById(dto.getGenusOrSpeciesID());
	SampleStatus ss = statusDao.loadById('S');
	SampleType st = sampleTypeDao.loadById(dto.getSampleTypeId());
	Sample parent = ((dto.getParentID() == null) || (dto.getParentID().trim().length() == 0)) ? null : loadById(dto.getParentID());
	
	Sample sample = null;
	
	if (StringUtils.isEmptyOrNull(dto.getSampleID()))
		try
			{
			sample = BiologicalSample.instance(dto.getSampleID(), dto.getSampleName(), exp, null,
					genusSpeciesDao.loadById(dto.getGenusOrSpeciesID()), dto.getLocID(), dto.getUserDefSampleType(), null,
					dto.getVolume(), dto.getVolUnits(), statusDao.loadById('S'), Calendar.getInstance(),
					sampleTypeDao.loadById(dto.getSampleTypeId()),  null, parent);
			sampleDao.createSample(sample);
			}
		catch (Exception e) { e.printStackTrace(); sample = null; }

	else
		try
			{
			sample = sampleDao.loadById(dto.getSampleID());
			sample.update(dto, exp,gs, ss, st, null);
			} 
		catch (Exception e) { e.printStackTrace(); sample = null; }
		
	
	return sample;
	} */
// sampleService.save



/*
public void saveSampleAssay(AssaySelectionSet set)
	{
	List<String> sampleIds = set.getSelectedAssayIds();
	Assay assay = assayDao.loadAssayByID(set.getAssayId());

	for (int i = 0; i < sampleIds.size(); i++)
		{
		Sample sample = sampleDao.loadById(sampleIds.get(i));
		assayDao.createSampleAssay(SampleAssay.instance(sample, assay, sampleAssayStatusDao.loadById('Q')));
		}
	}
*/
/*


public int saveSamplesNew(String expID, List<SampleDTO> sampleDtoList, Map<String, List<String>> factor_map,
	Map<String, List<String>> assay_map) throws METWorksException
	{
	Experiment exp = null; 
	try
		{
		exp = expDao.loadSimplestById(expID);
		exp.getExpDescription();
		}
	catch (Exception e) 
		{
		e.printStackTrace();
		throw new METWorksException("Experiment missing : Unable to load experiment " + expID);
		}
	
	
	Map<String, Sample> sample_map = new HashMap<String, Sample>();
	List<Sample> samples = new ArrayList<Sample>();

	for (SampleDTO dto : sampleDtoList)
		{
		String sid = dto.getSampleID();
		GenusSpecies gs = null;
		try
			{
			gs = genusSpeciesDao.loadById(dto.getGenusOrSpeciesID());
			gs.getGenusName();
			}
		catch (Exception e) { e.printStackTrace(); throw new METWorksException("Sample save error (" + sid + ") : Unable to locate genus species with id " + dto.getGenusOrSpeciesID());}
		
		
		SampleType st = null;
		try
			{
			st = sampleTypeDao.loadById(dto.getSampleTypeId());
			st.getDescription();
			}
		catch (Exception e) { e.printStackTrace(); throw new METWorksException("Sample save error (" + sid + ") : Unable to locate sample type with id " + dto.getSampleTypeId()); }

		
		Location loc = null;
		try
			{
			loc = locationDao.loadById(dto.getLocID());
			loc.getDescription();
			}
		catch (Exception e)  { e.printStackTrace(); throw new METWorksException("Sample save error (" + sid + ") : Location code is incorrect : Location " + dto.getLocID() + " does not exist"); }
		
		
		Subject su;
		if (subjectDao.subjectExistsForExperimentAndName(dto.getSubjectId(), exp))
			 su = subjectDao.loadSubjectByNameAndExp(dto.getSubjectId(), exp);
		else	
			try
				{
				su = Subject.instance(getNextSubjectID(), gs.getNcbiID(), dto.getSubjectId());
				subjectDao.createSubject(su);
				}
			catch (Exception e) { throw new METWorksException("Sample save error (" + sid + ") : Error while creating subject with name " + dto.getSubjectId()); }
	
	
		Sample sample;
		try
			{
			sample = BiologicalSample.instance(sid, dto.getSampleName(), exp, su, gs, dto.getLocID(), dto.getUserDefSampleType(), dto.getUserDefGOS(),
					dto.getVolume(), dto.getVolUnits(), statusDao.loadById('S'), Calendar.getInstance(), st, null, null);

			sampleDao.createSample(sample);
			sample_map.put(sample.getSampleID(), sample);
			samples.add(sample);
			System.out.println("Sample created " + sample.getSampleID());
			}
		
		catch (Exception e)  {  return 0;  }
		}
	

	System.out.println("Sample created "+ (samples == null ? 0 : samples.size()));

	System.out.println("Factors " + factor_map);
	if (samples.size() > 0)
		{
		// factorMap is factorName -> factorValues across samples
		Iterator<String> factorIterator = factor_map.keySet().iterator();
		while (factorIterator.hasNext())
			{
			String factorName = factorIterator.next();
			Map<String, FactorLevel> level_map = new HashMap<String, FactorLevel>();
			
			Factor factor = factorDao.createFactor(Factor.instance(factorName, exp));
			for (int l = 0; l < factor_map.get(factorName).size(); l++)
				{
				String level = factor_map.get(factorName).get(l);
				if (!level_map.containsKey(level))
					level_map.put(level, factorDao.createFactorLevel(FactorLevel.instance(level, factor)));
				// / assuming samples align with how the values are  stored...
				factorDao.createSampleFactorLevel(ExperimentSetup.instance(samples.get(l), level_map.get(level)));
				}
			}
		Iterator<String> assayIterator = assay_map.keySet().iterator();

		int s = 0;
		while (assayIterator.hasNext())
			{
			String sid = assayIterator.next();
			for (String assayName : assay_map.get(sid))
				{
				Assay assay = assayDao.loadAssayByName(assayName);
				assayDao.createSampleAssay(SampleAssay.instance(sample_map.get(sid), assay, sampleAssayStatusDao.loadById('Q')));
				}
			}
		}

	return samples.size();
	}
		*/

/*	public int saveSamples(String expID, List<SampleDTO> sampleDtoList, Map<String, List<String>> factor_map,
		Map<String, List<String>> assay_map)
		{
		
		Experiment exp = expDao.loadById(expID);
		Map<String, Sample> sample_map = new HashMap<String, Sample>();
		List<Sample> samples = new ArrayList<Sample>();
		boolean samplesCreated = false;

		for (SampleDTO dto : sampleDtoList)
			{
			System.out.println("Genus or species" + dto.getGenusOrSpeciesID());
			GenusSpecies gs = null;
			try 
				{
				gs = genusSpeciesDao.loadById(dto.getGenusOrSpeciesID());
				System.out.println("Genus species is " + (gs == null ? " null " : " not null"));
				}
			catch (Exception e)  { System.out.println("Genus species load error"); }

			Subject su;
			try
				{
				System.out.println("Dto subject is " + dto.getSubjectId());
				su = subjectDao
						.loadSubjectByNameAndExp(dto.getSubjectId(), exp);
				} catch (Exception e)
				{
				// UserRegistrationPage;
				System.out.println("Catching the exception");
				su = Subject.instance(getNextSubjectID(), gs.getNcbiID(),
						dto.getSubjectId());
				subjectDao.createSubject(su);
				}

			System.out.println("Past the dto subject save");
			Sample parent = ((dto.getParentID() == null) || (dto.getParentID()
					.trim().length() == 0)) ? null
					: loadById(dto.getParentID());
			Sample sample;
			try
				{
				System.out.println("dto sample id " + dto.toString());
				sample = sampleDao.loadById(dto.getSampleID());
				if (sample != null)
					{
					factor_map = new HashMap<String, List<String>>();
					assay_map = new HashMap<String, List<String>>();
					return 0;
					}
				} catch (Exception e)
				{
				System.out.println("Catching the sample exception");
				System.out.println("Sample type" + dto.getSampleTypeId());
				sample = BiologicalSample.instance(dto.getSampleID(),
						dto.getSampleName(), exp, su, gs, dto.getLocID(),
						dto.getUserDefSampleType(), dto.getUserDefGOS(),
						dto.getVolume(), dto.getVolUnits(),
						statusDao.loadById('S'), Calendar.getInstance(),
						sampleTypeDao.loadById(dto.getSampleTypeId()), null,
						parent);

				sampleDao.createSample(sample);
				samplesCreated = true;
				sample_map.put(sample.getSampleID(), sample);
				samples.add(sample);
				System.out.println("Sample created " + sample.getSampleID());
				}
			}

		System.out.println("Sample created "
				+ (samples == null ? 0 : samples.size()));

		System.out.println("Factors " + factor_map);
		if (samplesCreated)
			{
			// factorMap is factorName -> factorValues across samples
			Iterator<String> factorIterator = factor_map.keySet().iterator();
			while (factorIterator.hasNext())
				{
				String factorName = factorIterator.next();
				Map<String, FactorLevel> level_map = new HashMap<String, FactorLevel>();
				System.out.println("Factor name " + factorName);
				Factor factor = factorDao.createFactor(Factor.instance(
						factorName, exp));
				System.out.println("Factor name " + factorName);

				for (int l = 0; l < factor_map.get(factorName).size(); l++)
					{
					String level = factor_map.get(factorName).get(l);
					if (!level_map.containsKey(level))
						{
						level_map.put(level, factorDao
								.createFactorLevel(FactorLevel.instance(level,
										factor)));
						}
					// / assuming samples align with how the values are
					// stored...
					factorDao.createSampleFactorLevel(ExperimentSetup.instance(
							samples.get(l), level_map.get(level)));
					}
				}
			Iterator<String> assayIterator = assay_map.keySet().iterator();

			int s = 0;
			while (assayIterator.hasNext())
				{
				String sid = assayIterator.next();
				for (String assayName : assay_map.get(sid))
					{
					Assay assay = assayDao.loadAssayByName(assayName);
					assayDao.createSampleAssay(SampleAssay.instance(
							sample_map.get(sid), assay,
							sampleAssayStatusDao.loadById('Q')));
					}
				}
			}

		return samples.size();
		}

 * 
 * 
 * 
 * 
 * 
 * public int saveSamplesAndControls(String expID, List<SampleDTO> sampleDtoList, Map<String, List<String>> factor_map,
			Map<String, List<String>> assay_map, Map<String, String> assays_for_experiment)
		{
		int nSamples = saveSamples(expID, sampleDtoList, factor_map, assay_map);

		if (nSamples > 0)
			{
			Experiment exp = expDao.loadById(expID);
			Iterator<String> experimentAssayIterator = assays_for_experiment
					.keySet().iterator();
			while (experimentAssayIterator.hasNext())
				{
				String assayName = experimentAssayIterator.next();
				Assay assay = assayDao.loadAssayByName(assayName);

				String platformId = assay.getPlatformId();

				List<String> controlTypeIds = controlTypeDao
						.allControlTypeIdsForPlatformId(platformId);

				for (String controlTypeId : controlTypeIds)
					{
					Control control = Control.instance(getNextControlID(), exp,
							assay, controlTypeId);
					controlDao.createControl(control);
					// System.out.println("Created control" +
					// control.getControlId() + " with type  " +
					// control.getControlTypeId());
					}
				}
			}

		return nSamples;
		}*/
 
/*
 * Iterator<String> experimentAssayIterator =
 * assays_for_experiment.keySet().iterator(); while
 * (experimentAssayIterator.hasNext()) {
 * 
 * String assayName = experimentAssayIterator.next(); Assay
 * assay=assayDao.loadAssayByName(assayName);
 * 
 * String platformId = assay.getPlatformId();
 * 
 * List<String> controlTypeIds =
 * controlTypeDao.allControlTypeIdsForPlatformId(platformId);
 * 
 * for (String controlTypeId : controlTypeIds) { Control control =
 * Control.instance(getNextControlID(), exp, assay, controlTypeId);
 * controlDao.createControl(control); System.out.println("Created control" +
 * control.getControlId() + " with type  " + control.getControlTypeId()); } }
 */


/*
public int saveSamples(List<SampleDTO> samples, String expID, String groupID)
	{
	int sampleCount = 0;
//	Sample sample = null;
	for (SampleDTO sl : samples)
		{
		if (sl.getSampleID() != null)
			{
			sl.setExpID(expID);
			sl.setGroupID(groupID);
//			sample = save(sl);
			++sampleCount;
			// barcodesList.add(new ValueLabelBean(sample.getId(), null));
			}
		}
	return sampleCount;
	}
 */

////////////////////////////////////////////////////
// Mrc2SubmissionDataService.java
// Written by Jan Wigginton, Jun 7, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.layers.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.domain.SampleAssay;
import edu.umich.brcf.shared.layers.domain.SampleStatus;
import edu.umich.brcf.shared.layers.domain.SampleType;
import edu.umich.brcf.shared.layers.domain.Shortcode;
import edu.umich.brcf.shared.layers.domain.Subject;
import edu.umich.brcf.shared.layers.dto.SampleDTO;
import edu.umich.brcf.shared.layers.dto.ShortcodeDTO;
import edu.umich.brcf.shared.util.METWorksException;
import edu.umich.brcf.shared.util.datacollectors.Mrc2TransitionalSubmissionSheetData;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;


@Transactional(rollbackFor = Exception.class)
public class Mrc2SubmissionDataService
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
	
	
	public int saveSamples(Mrc2TransitionalSubmissionSheetData data) throws METWorksException
		{
		Experiment exp = null;
		try { exp = grabExperimentIfValid(data.getExpId()); }
		catch (RuntimeException e) { throw new METWorksException(e.getMessage()); }
		
		try
			{
			ShortcodeDTO scDTO = new ShortcodeDTO();
			scDTO.update(data.getClientInfo());
			scDTO.setExp(exp);
			
			String code = data.getClientInfo().getShortCode(); 
			scDTO.setCode(StringUtils.isEmptyOrNull(code) ? "NA" : code);
			saveShortcodeNew(scDTO);
			}
		catch (Exception e)  { throw new RuntimeException("Error while saving shortcode and grant information"); }
		List<SampleDTO> sampleDtoList = data.samplesMetadata.grabDTOList();
	// JAK comment out because we are using dto	List<SampleDTO> sampleDtoList = data.samplesMetadata.getInfoFields();
		Map<String, Sample> sample_map = new HashMap<String, Sample>();
		
		for (SampleDTO dto : sampleDtoList)
			{
			Sample sample = createNewSample(dto, exp);
			if (sample == null)
				throw new RuntimeException("Sample save error (" + dto.getSampleID() + "). "); 
			
			sample_map.put(sample.getSampleID(), sample);
			}
		
		saveFactorData(data, sample_map, exp);
		saveAssayData(data, sample_map, exp);
		return sample_map.keySet().size();
		}
		
	
	public int addUploadedSheetData(Mrc2TransitionalSubmissionSheetData data, Integer nSamplesToAdd) throws METWorksException
		{
		Assert.notNull(data);
		
		Experiment exp = null;
		try
			{
			exp = expDao.loadById(data.getExpId());
			exp.updateFromClientInfo(data.getClientInfo());
			}
		catch (Exception e) { throw new RuntimeException("Error while saving sample data.  Experiment (" + data.getExpId() + ") does not exist."); }

		if (!factorDao.factorLabelsMatch(data.getExpDesign().getFactorLabels(), data.getExpId()))
			{
			List<String> loadedNames = factorDao.loadFactorNamesForExpId(data.getExpId());
			
			throw new RuntimeException("Error while adding sample factor information.  Factor Names for current sheet ("
			+ data.getExpDesign().getFactorLabels() + ") do not match loaded factor names (" + loadedNames + "). Note : Factor names are case sensitive and must match exactly."); 
			}
		List<SampleDTO> sampleDtoList = data.samplesMetadata.grabDTOList();
	//	List<SampleDTO> sampleDtoList = data.samplesMetadata.getInfoFields();
		Map<String, Sample> sample_map = new HashMap<String, Sample>();
		
		for (SampleDTO dto : sampleDtoList)
			{
			Sample sample = createNewSample(dto, exp);
			if (sample == null)
				throw new RuntimeException("Sample save error (" + dto.getSampleID() + "). "); 
			
			sample_map.put(sample.getSampleID(), sample);
			}
		
		saveFactorData(data, sample_map, exp);
		saveAssayData(data, sample_map, exp);
		return sample_map.keySet().size();
		}
	
	
	public Sample createNewSample(SampleDTO dto, Experiment exp)
		{
		Assert.notNull(dto);
			
		try 
			{
			SampleType st = grabSampleTypeIfValid(dto.getSampleTypeId());
			GenusSpecies gs = grabGenusSpeciesIfValid(dto);
			Location loc = grabLocationIfValid(dto);
		
			Subject su = null;
			if (subjectDao.subjectExistsForExperimentAndName(dto.getSubjectId(), exp))
				su = subjectDao.loadSubjectByNameAndExp(dto.getSubjectId(), exp);
			else	
				su = this.grabNewSubject(dto, gs);
			Sample sample = BiologicalSample.instance(dto.getSampleID(), dto.getSampleName(), exp, su, gs, dto.getLocID(), dto.getUserDefSampleType(), dto.getUserDefGOS(),
					dto.getVolume(), dto.getVolUnits(), statusDao.loadById('S'), Calendar.getInstance(), st, null, null);
			sampleDao.createSample(sample);
			return sample;
			}
	
		catch (Exception g) { g.printStackTrace();  throw new RuntimeException("Sample save error (" + dto.getSampleID() + "). " + g.getMessage()); }
		}
	
	
	public void saveFactorData(Mrc2TransitionalSubmissionSheetData data, Map<String, Sample> sample_map, Experiment exp)
		{
		for (String factorName : data.getExpDesign().getFactorLabels())
			{
			Map<String, String> valueByLabelMap = data.pullValuesByLabelForFactorName(factorName);
			Factor factor = factorDao.createFactor(Factor.instance(factorName, exp));
			
			Map<String, FactorLevel> level_map = new HashMap<String, FactorLevel>();
			
			for (String sampleId : valueByLabelMap.keySet())
				{
				String value = valueByLabelMap.get(sampleId);
				if (!level_map.containsKey(value))
					level_map.put(value, factorDao.createFactorLevel(FactorLevel.instance(value, factor)));
				
				Sample sample = sample_map.get(sampleId);
				factorDao.createSampleFactorLevel(ExperimentSetup.instance(sample, level_map.get(value)));
				}
			}
		}
	

	public void saveAssayData(Mrc2TransitionalSubmissionSheetData data, Map<String, Sample> sample_map, Experiment exp)
		{
		Map<String, List<String>> assay_map = data.pullAssayMap();
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
	
	
	public void saveShortcodeNew(ShortcodeDTO sc) 
		{
		Shortcode scode = null;
		try
			{
			scode= expDao.getExperimentShortcode(sc.getCode(), sc.getExp().getExpID());
			if (scode==null)
				{
				scode = Shortcode.instance(sc.getCode(), sc.getNIH_GrantNumber(), sc.getExp(), sc.getNIH_GrantNumber_2(), sc.getNIH_GrantNumber_3());
				expDao.saveShortcode(scode);
				}
			else scode.updateGrantNums(sc);
			}
		catch (Exception e){  throw new RuntimeException("Error while saving shortcode " + (scode == null ? "" : scode.getId()));  }
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
	
	
	public String getNextSubjectID()
		{
		return ((String) idGeneratorDao.getNextValue("Subject"));
		}


	public SampleDAO getSampleDao()
		{
		return sampleDao;
		}


	public ExperimentDAO getExpDao()
		{
		return expDao;
		}


	public GenusSpeciesDAO getGenusSpeciesDao()
		{
		return genusSpeciesDao;
		}


	public SampleStatusDAO getStatusDao()
		{
		return statusDao;
		}


	public SampleAssayStatusDAO getSampleAssayStatusDao()
		{
		return sampleAssayStatusDao;
		}


	public SampleTypeDAO getSampleTypeDao()
		{
		return sampleTypeDao;
		}


	public UserDAO getUserDao()
		{
		return userDao;
		}


	public IdGeneratorDAO getIdGeneratorDao()
		{
		return idGeneratorDao;
		}


	public FactorDAO getFactorDao()
		{
		return factorDao;
		}


	public SubjectDAO getSubjectDao()
		{
		return subjectDao;
		}


	public AssayDAO getAssayDao()
		{
		return assayDao;
		}


	public ControlDAO getControlDao()
		{
		return controlDao;
		}


	public ControlTypeDAO getControlTypeDao()
		{
		return controlTypeDao;
		}


	public LocationDAO getLocationDao()
		{
		return locationDao;
		}


	public TableAccessDAO getTableAccessDao()
		{
		return tableAccessDao;
		}


	public void setSampleDao(SampleDAO sampleDao)
		{
		this.sampleDao = sampleDao;
		}


	public void setExpDao(ExperimentDAO expDao)
		{
		this.expDao = expDao;
		}


	public void setGenusSpeciesDao(GenusSpeciesDAO genusSpeciesDao)
		{
		this.genusSpeciesDao = genusSpeciesDao;
		}


	public void setStatusDao(SampleStatusDAO statusDao)
		{
		this.statusDao = statusDao;
		}


	public void setSampleAssayStatusDao(SampleAssayStatusDAO sampleAssayStatusDao)
		{
		this.sampleAssayStatusDao = sampleAssayStatusDao;
		}


	public void setSampleTypeDao(SampleTypeDAO sampleTypeDao)
		{
		this.sampleTypeDao = sampleTypeDao;
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


	public void setControlTypeDao(ControlTypeDAO controlTypeDao)
		{
		this.controlTypeDao = controlTypeDao;
		}


	public void setLocationDao(LocationDAO locationDao)
		{
		this.locationDao = locationDao;
		}


	public void setTableAccessDao(TableAccessDAO tableAccessDao)
		{
		this.tableAccessDao = tableAccessDao;
		}
	}


/////////////////////////   SCRAP CODE ////////////////////////

	
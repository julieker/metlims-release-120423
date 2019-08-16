////////////////////////////////////////////////////
//SampleRegistrationData.java
//Written by Jan Wigginton (October 2015)
////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.admin.sample_submission;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.domain.SampleAssay;
import edu.umich.brcf.shared.layers.dto.SampleDTO;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.io.StringUtils;



public class SampleRegistrationData implements Serializable
	{
	@SpringBean 
	SampleService sampleService;

	public static final int N_FACTORS_TRACKED = 7;
	public static final int N_DEFAULT_SAMPLES = 10;
	public static final int N_ASSAYS_TRACKED = 6;
	public static final int SHORT_LABEL_LEN = 35;
	
	private List<SampleDTO> sampleData;
	private List<SampleAssayMapping>  sampleAssayMappings;
	private List<SampleFactorMapping> sampleFactorMappings;
	private	Map<String, Boolean> newSamples;
	private List<String> factorNames; 
	
	private Map<String, SampleFactorMapping> factorMapsBySampleIdx;
	private Map<String, SampleAssayMapping>  assayMapsBySampleIds; 
	private String expID;
	
	
	public SampleRegistrationData(int nSamples) 
		{
		this(null, nSamples);
		
		initializeDataSpace(nSamples, N_FACTORS_TRACKED);
		}
	
	
	public SampleRegistrationData(Experiment exp)
		{
		this(exp, 0);
		}
	
	
	public SampleRegistrationData(Experiment exp, int nSamplesToAdd)
		{
		Injector.get().inject(this);
		
		List<Sample> samples =exp.getSampleList();
		initializeDataSpace(samples.size() + nSamplesToAdd, N_FACTORS_TRACKED);
		setExpID(exp.getExpID());
		
		factorNames = new ArrayList<String>();
		for (int i = 0; i < exp.getFactors().size(); i++)
			factorNames.add(exp.getFactors().get(i).getFactorName());
		
		initializeMetaData(samples);
		initializeSampleAssayMappings(samples);
		initializeFactorMappings(samples);
		}
	
	
	private void initializeFactorMappings(List<Sample> list)
		{
		//sampleFactorMappings = new ArrayList<SampleFactorMapping>();
		for (int i = 0; i < list.size(); i++)
			{
			Sample s = list.get(i);
			String sampleId = s.getSampleID();
			
			SampleFactorMapping sfm = sampleFactorMappings.get(i); //new SampleFactorMapping(s.getSampleID(), N_FACTORS_TRACKED);
			sfm.setSampleId(sampleId);
			sfm.setResearcherSampleId(s.getSampleName());
			List <String> sampleValues = sampleService.getFactorValuesById(sampleId);
			
			
			for (int j = 0; j < Math.min(N_ASSAYS_TRACKED, sampleValues.size()); j++)
				sfm.setFactorValues(j, sampleValues.get(j));
		
			this.factorMapsBySampleIdx.put(sampleId, sfm);
			
			//sfm.setSampleFactors(sampleService.getFactorValuesById(s.getSampleID()));
			//sampleFactorMappings.add(sfm);
			}
		}
	
	
	private void initializeMetaData(List<Sample> list)
		{
		//sampleData= new ArrayList<SampleDTO>();
		for (int i = 0; i < list.size(); i++)
			{
			Sample sample = list.get(i);

			SampleDTO dto = new SampleDTO(sample.getSampleID(), 
				sample.getSampleName(), expID, sample.getUserDescription(), 
				sample.getGenusOrSpecies().getGsID(), sample.getLocID(), 
				sample.getUserDefSampleType(), sample.getVolume(), sample.getVolUnits(), 
				sample.getStatus().getId(),1L, sample.getDateCreated(), "", "","");
			
			if (sample.getSubject() != null && !"".equals(sample.getSubject().getSubjectId()))
				dto.setSubjectId(sample.getSubject().getSubjectId());
			else 
				dto.setSubjectId("");
			
			String sampleId = dto.getSampleID();
			
			if (StringUtils.isNonEmpty(sampleId))
				newSamples.put(sampleId, true);
			
			sampleData.set(i, dto);
			}
		}
	
	
	public List<SampleFactorMapping> getSampleFactorMappings3()
		{
		Set<String> keys = this.factorMapsBySampleIdx.keySet();
		
		List keyList = new ArrayList<String>();
		for (String key : keys)
			keyList.add(key);
		
		Collections.sort(keyList);
	
		List <SampleFactorMapping> lst =new ArrayList<SampleFactorMapping>();
		for (int i = 0;i < keyList.size(); i++)
			lst.add(factorMapsBySampleIdx.get(keyList.get(i)));
		
		return lst;
		}
	
	
	public void initializeSampleAssayMappings(List <Sample> list)
		{
		for (int i = 0; i < list.size(); i++)
			{
			Sample s = list.get(i);
			//String sampleAssays = s.getCommaSeparatedSampleAssays();
			List<SampleAssay> sAssays = s.getSampleAssays();
			
			SampleAssayMapping sam = sampleAssayMappings.get(i);
			sam.setSampleId(s.getSampleID());
			this.assayMapsBySampleIds.put(s.getSampleID(), sam);
			for (int j = 0; j < Math.min(N_ASSAYS_TRACKED, sAssays.size()); j++)
				{
				SampleAssay sa = sAssays.get(j);
				
				String assayName = sa.getAssay().getAssayName();
				String assayNameShort = assayName.substring(0, Math.min(SHORT_LABEL_LEN, assayName.length()));
				String assayId = sa.getAssay().getAssayId();
				String fullName = assayNameShort + " (" + assayId + ")";
				sam.setSampleAssays(j, fullName);
				}
			}
		}
	
	
	public void initializeDataSpace(Integer nSamples, int nFactors)
		{
		sampleData = new ArrayList<SampleDTO>();
		sampleAssayMappings = new ArrayList<SampleAssayMapping>();
		sampleFactorMappings = new ArrayList<SampleFactorMapping>();
		this.factorMapsBySampleIdx = new HashMap<String, SampleFactorMapping>();
		this.assayMapsBySampleIds = new HashMap<String, SampleAssayMapping>();
		newSamples = new HashMap<String, Boolean>();
		
		for (int l = 0; l < nSamples; l++)
			{
			sampleData.add(new SampleDTO());
			sampleAssayMappings.add(new SampleAssayMapping("", N_ASSAYS_TRACKED));
			sampleFactorMappings.add(new SampleFactorMapping("", "", N_FACTORS_TRACKED));
			}
		
		factorNames = new ArrayList <String>();
		for (int i = 0; i < nFactors; i++)
			factorNames.add("Factor " + i); 
		}
	
	
	public List<SampleDTO> getSampleData() {
		return sampleData;
	}

	public void setSampleData(List<SampleDTO> sampleData) {
		this.sampleData = sampleData;
	}

	public String getExpID() {
		return expID;
	}

	public void setExpID(String expID) {
		this.expID = expID;
	}

	public List<SampleAssayMapping> getSampleAssayMappings() {
		return sampleAssayMappings;
	}

	public void setSampleAssayMappings(List<SampleAssayMapping> sampleAssayData) {
		this.sampleAssayMappings = sampleAssayData;
	}
	
	public List<SampleFactorMapping> getSampleFactorMappings() {
		return sampleFactorMappings;
	}

	public void setSampleFactorMappings(List<SampleFactorMapping> sampleFactorData) {
		this.sampleFactorMappings = sampleFactorData;
	}

	public List<String> getFactorNames() {
		return factorNames;
	}

	public void setFactorNames(List<String> factorNames) {
		this.factorNames = factorNames;
	}
	
	public String getFactorNames(int i)
		{
		if (i< 0 || i > factorNames.size())
			return "";
		
		return this.factorNames.get(i);
		}
	
	public Map<String, Boolean> newSamples()
		{
		return newSamples;
		}
	
	public Boolean isNewSample(String sampleId)
		{
		return !(newSamples.containsKey(sampleId));
		}
	
	public SampleAssayMapping getAssayMapFor(String sampleId)
		{
		return assayMapsBySampleIds.get(sampleId);
		}
	
	public SampleFactorMapping getFactorMapFor(String sampleId)
		{
		return factorMapsBySampleIdx.get(sampleId);
		}
	}
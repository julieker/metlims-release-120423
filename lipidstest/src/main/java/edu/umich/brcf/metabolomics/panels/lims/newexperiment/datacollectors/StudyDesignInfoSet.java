////////////////////////////////////
//StudyDesignInfoSet.java
//Written by Jan Wigginton June 2015
/////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.lims.newexperiment.datacollectors;


// Sample Details
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.FactorService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.comparator.StudyDesignInfoItemBySampleIdComparator;


public class StudyDesignInfoSet implements Serializable
	{
	@SpringBean
	ExperimentService expService;
	
	@SpringBean 
	SampleService sampleService; 
	

	@SpringBean
	FactorService factorService;
	

	public List <StudyDesignInfoItem> infoFields = new ArrayList<StudyDesignInfoItem>();

	private List <String> factorLabels = new ArrayList<String>(); 
	
	static final int nFactorsTracked = 5;
	String mode = "drcc";


	public StudyDesignInfoSet()  { }
	
	public StudyDesignInfoSet(String expId)
		{
		this(expId, "drcc");
		}

	public StudyDesignInfoSet(String expId, String mode)
		{
		this(expId, mode, false);
		}
		
	
	public StudyDesignInfoSet(String expId, String mode, Boolean useView)
		{
		Injector.get().inject(this);
	//	useView = false;
		
		setMode(mode);
		if (useView)
			initializeEfficiently(expId);
		else
			initializeForExperiment(expId);
		}

	
	public void initializeEfficiently(String expId)
		{
		Experiment exp = expService.loadById(expId);
		factorLabels = new ArrayList<String>();
		for (int i = 0; i < exp.getFactors().size(); i++)
			factorLabels.add(exp.getFactors().get(i).getFactorName());
	
		Collections.sort(factorLabels);
	    buildInfoListSimplified(exp);
		
		List<Sample> sampleList = sampleService.loadBasicSamplesForExpId(expId);
		Map<String, List<String>> sampleFactorValueMap = sampleService.getFactorValueMapForExpId(expId);

		List <StudyDesignInfoItem> sampleInfo = new ArrayList<StudyDesignInfoItem>();

		for (int i = 0; i < sampleList.size(); i++)
			{
			Sample s = sampleList.get(i);
			StudyDesignInfoItem info = new StudyDesignInfoItem();
			info.setMode(mode);
			String sampleId = s.getSampleID();
	
			info.setSampleId(sampleId);
			info.setSampleName(s.getSampleName());
			// issue 443
			info.setResearcherSampleName(s.getSampleName());
	        info.setShortSampleName(s.getSampleName().length() > 30 ? s.getSampleName().substring(0,30) : s.getSampleName());
			info.setSubjectName(s.getUserSubject());
			if (factorLabels.size() >0 )
			           info.setFactorValues(sampleFactorValueMap.get(sampleId));
			info.setUserDefinedSampleType(s.getUserDefSampleType());
			
			sampleInfo.add(info);
			}
	
		Collections.sort(sampleInfo, new StudyDesignInfoItemBySampleIdComparator());
		infoFields = sampleInfo;
		}

	
	
	public void initializeForExperiment(String expId)
		{
		Experiment exp = expService.loadById(expId);
		factorLabels = new ArrayList<String>();
		for (int i = 0; i < exp.getFactors().size(); i++)
			factorLabels.add(exp.getFactors().get(i).getFactorName());
	
		Collections.sort(factorLabels);
		buildInfoList(exp);
		}
	
	public void buildInfoList(Experiment exp)
		{
		List<Sample> sampleList = exp.getSampleList();
		List <StudyDesignInfoItem> sampleInfo = new ArrayList<StudyDesignInfoItem>();
		
		for (int i = 0; i < sampleList.size(); i++)
			{
			Sample s = sampleList.get(i);
			StudyDesignInfoItem info = new StudyDesignInfoItem();
			info.setMode(mode);
			String sampleId = s.getSampleID();
			
			info.setSampleId(sampleId);
			info.setSampleName(s.getSampleName());
			// issue 443
			info.setResearcherSampleName(s.getSampleName());
			info.setShortSampleName(s.getSampleName().length() > 30 ? s.getSampleName().substring(0,30) : s.getSampleName());
			info.setSubjectName(s.getUserSubject());
			info.setFactorValues(sampleService.getFactorValuesByIdSortedByName(sampleId));
			info.setUserDefinedSampleType(s.getUserDefSampleType());
			sampleInfo.add(info);
			}
		
		Collections.sort(sampleInfo, new StudyDesignInfoItemBySampleIdComparator());
		//Collections.sort(sampleInfo, new StudyDesignInfoItemBySampleIdComparator());
		infoFields = sampleInfo;
		}


	public void buildInfoListSimplified(Experiment exp)
	{
	List<Sample> sampleList = exp.getSampleList();
	List <StudyDesignInfoItem> sampleInfo = new ArrayList<StudyDesignInfoItem>();
	
	for (int i = 0; i < sampleList.size(); i++)
		{
		Sample s = sampleList.get(i);
		StudyDesignInfoItem info = new StudyDesignInfoItem();
		info.setMode(mode);
		String sampleId = s.getSampleID();
		
		info.setSampleId(sampleId);
		info.setSampleName(s.getSampleName());
		// issue 443
		info.setResearcherSampleName(s.getSampleName());
		info.setShortSampleName(s.getSampleName().length() > 30 ? s.getSampleName().substring(0,30) : s.getSampleName());
		info.setSubjectName(s.getUserSubject());
		//info.setFactorValues(sampleService.getFactorValuesByIdSortedByName(sampleId));
		info.setUserDefinedSampleType(s.getUserDefSampleType());
		sampleInfo.add(info);
		}
	
	Collections.sort(sampleInfo, new StudyDesignInfoItemBySampleIdComparator());
	//Collections.sort(sampleInfo, new StudyDesignInfoItemBySampleIdComparator());
	infoFields = sampleInfo;
	}
	
	
	
	public List<StudyDesignInfoItem> getInfoFields() {
		return infoFields;
	}


	public void setInfoFields(List<StudyDesignInfoItem> infoFields) {
		this.infoFields = infoFields;
	}


	public List<String> getFactorLabels() {
		return factorLabels;
	}


	public void setFactorLabels(List<String> factorLabels) {
		this.factorLabels = factorLabels;
	}
	
	public String getFactorLabels(int i) {
		return factorLabels.get(i);
	}

	public void setFactorLabels(int i, String value)
		{
		this.factorLabels.set(i, value);
		}
	

	public static int getNfactorstracked() {
		return nFactorsTracked;
	}
	
	public void setMode(String mode)
		{
		this.mode = mode;
		}
	
	public String getMode()
		{
		return mode;
		}
	}

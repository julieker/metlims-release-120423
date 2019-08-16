////////////////////////////////////////////////////
// Mrc2TransitionalExperimentDesign.java
// Written by Jan Wigginton, Jun 14, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.datacollectors;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.service.GenusSpeciesService;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.domain.SampleAssay;
import edu.umich.brcf.shared.layers.dto.SampleDTO;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.FactorService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.comparator.Mrc2TransitionalExperimentDesignItemComparator;


public class Mrc2TransitionalExperimentDesign implements Serializable
	{
	@SpringBean
	ExperimentService expService;
	
	@SpringBean 
	SampleService sampleService; 
	
	@SpringBean
	GenusSpeciesService genusSpeciesService;
	
	@SpringBean
	FactorService factorService;
	

	String expDescription = "";
	public List <Mrc2TransitionalExperimentDesignItem> infoFields = new ArrayList<Mrc2TransitionalExperimentDesignItem>();
	private List <String> factorLabels = new ArrayList<String>(); 
	Boolean fullAssayNames = true;
	
	public static int MRC2_SUBMISSION_SHEET_NFACTORS = 5;
	public static int MRC2_SUBMISSION_SHEET_NASSAYS = 6;
	public static int SHORT_LABEL_LEN = 35;
		
	public Mrc2TransitionalExperimentDesign()
		{
		Injector.get().inject(this);
		}
	
	public Mrc2TransitionalExperimentDesign(String expId)
		{
		this(expId, true);
		}
	
	public Mrc2TransitionalExperimentDesign(String expId, boolean fullAssayNames)
		{
		this();
		this.fullAssayNames = fullAssayNames;
		initializeForExperiment(expId);
		}
	
	public Mrc2TransitionalExperimentDesign(Map<String, String> sampleList, Map<String, Map<String, String>> factor_map, 
			List<String> factorNames,  Map<String, List<String>> assay_map)
		{
		this();
		//translateAssayMap(assayMap);
		initializeFromSheetRead(sampleList, factor_map, factorNames, assay_map);
		}
	
	
	//public void 
	public void initializeFromSheetRead(Map <String, String> sampleList, Map<String, Map<String, String>> factor_map, 
			List<String> factorNames,  Map<String, List<String>> assayMap)
		{
		this.setFactorLabels(factorNames);
		List <Mrc2TransitionalExperimentDesignItem> sampleInfo = new ArrayList<Mrc2TransitionalExperimentDesignItem>();
		
		for (String sampleId : sampleList.keySet())
			{
			
			Mrc2TransitionalExperimentDesignItem info = new Mrc2TransitionalExperimentDesignItem();
			//String sampleId = key; //s.getSampleID();
			
			info.setSampleId(sampleId);
			info.setSampleLabel(sampleList.get(sampleId));
			info.setFactorValueMap(factor_map.get(sampleId));
			
			List<String> sampleAssayNames = assayMap.get(sampleId);
			
		//	info.setDisplayAssayNames(sampleAssayNames);
			info.setAssaysForSample(assayMap.get(sampleId));
			
			sampleInfo.add(info);
			}
		
		Collections.sort(sampleInfo, new Mrc2TransitionalExperimentDesignItemComparator());
		infoFields = sampleInfo;
		}
	
	
	public void initializeForExperiment(String expId)
		{
		Experiment exp = expService.loadById(expId);
		
		this.expDescription = exp.getExpDescription();
		factorLabels = new ArrayList<String>();
		if (exp.getFactors() != null)
			for (int i = 0; i < exp.getFactors().size(); i++)
				factorLabels.add(exp.getFactors().get(i).getFactorName());
		
		Collections.sort(factorLabels);
		buildInfoList(expId);
		}
	

	public void buildInfoList(String expId)
		{
		List<Sample> sampleList = sampleService.loadSampleForAssayStatusTracking(expId);
		
		List <Mrc2TransitionalExperimentDesignItem> sampleInfo = new ArrayList<Mrc2TransitionalExperimentDesignItem>();
		
		for (int i = 0; i < sampleList.size(); i++)
			{
			Sample s = sampleList.get(i);
			Mrc2TransitionalExperimentDesignItem info = new Mrc2TransitionalExperimentDesignItem();
			String sampleId = s.getSampleID();
			
			info.setSampleId(sampleId);
			info.setSampleLabel(s.getSampleName());
			
			List<String> assayIds = new ArrayList<String>();
			List <SampleAssay> assays = s.getSampleAssays();
			String assayId = "";
			for (int j =0; j < assays.size(); j++)
				{
				String assayName = assays.get(j).getAssay().getAssayName();
				String assayNameShort = assayName.substring(0, Math.min(SHORT_LABEL_LEN, assayName.length()));
				assayId = assays.get(j).getAssay().getAssayId();
				String fullName = assayNameShort + " (" + assayId + ")";
				//if (fullAssayNames)
				//	sampleAssayNames.add(j, fullName);
				//else
				//	sampleAssayNames.add(assayName);
				
				assayIds.add(assayId);
				}
			
			info.setAssaysForSample(assayIds);
			info.setFactorValueMap(sampleService.getFactorValueMapForId(sampleId));
			
			sampleInfo.add(info);
			}
		
		Collections.sort(sampleInfo, new Mrc2TransitionalExperimentDesignItemComparator());
		infoFields = sampleInfo;
		}


	public String toString()
		{
		StringBuilder sb = new StringBuilder();
		
		for (Mrc2TransitionalExperimentDesignItem item : this.getInfoFields())
			sb.append(item.toString() + System.getProperty("line.separator"));
		
		return sb.toString(); 
		}
	
	
	public List<Mrc2TransitionalExperimentDesignItem> getInfoFields() {
		return infoFields;
	}


	public void setInfoFields(List<Mrc2TransitionalExperimentDesignItem> infoFields) {
		this.infoFields = infoFields;
	}


	public List<String> getFactorLabels() {
		return factorLabels;
	}

	
	public String getFactorLabels(int i) {
		return factorLabels.get(i);
	}

	public void setFactorLabels(int i, String value)
		{
		this.factorLabels.set(i, value);
		}
	
	public void setFactorLabels(List<String> labels)
		{
		factorLabels = new ArrayList<String>();
		for (int i = 0; i < labels.size(); i++)
			factorLabels.add(labels.get(i));
		}

	public String getExpDescription() {
		return expDescription;
	}

	public void setExpDescription(String expDescription) {
		this.expDescription = expDescription;
	}

	public Boolean getFullAssayNames() {
		return fullAssayNames;
	}

	public void setFullAssayNames(Boolean fullAssayNames) {
		this.fullAssayNames = fullAssayNames;
	}
	
	
	}
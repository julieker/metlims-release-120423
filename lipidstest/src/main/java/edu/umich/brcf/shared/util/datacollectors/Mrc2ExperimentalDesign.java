//Mrc2ExperimentalDesign.java
//Written by Jan Wigginton

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
import edu.umich.brcf.shared.util.comparator.Mrc2ExperimentalDesignItemComparator;


public class Mrc2ExperimentalDesign implements Serializable
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
	public List <Mrc2ExperimentalDesignItem> infoFields = new ArrayList<Mrc2ExperimentalDesignItem>();
	private List <String> factorLabels = new ArrayList<String>(); 
	Boolean fullAssayNames = true;
	
	public static int MRC2_SUBMISSION_SHEET_NFACTORS = 5;
	public static int MRC2_SUBMISSION_SHEET_NASSAYS = 6;
	public static int SHORT_LABEL_LEN = 35;
		
	public Mrc2ExperimentalDesign()
		{
		Injector.get().inject(this);
		}
	
	public Mrc2ExperimentalDesign(String expId)
		{
		this(expId, true);
		}
	
	public Mrc2ExperimentalDesign(String expId, boolean fullAssayNames)
		{
		this();
		this.fullAssayNames = fullAssayNames;
		initializeForExperiment(expId);
		}
	
	public Mrc2ExperimentalDesign(List <SampleDTO> sampleList, Map<String, List<String>> factor_map, 
			List<String> factorNames,  Map<String, List<String>> assay_map)
		{
		this();
		//translateAssayMap(assayMap);
		initializeFromSheetRead(sampleList, factor_map, factorNames, assay_map);
		}
	
	
	//public void 
	public void initializeFromSheetRead(List <SampleDTO> sampleList, Map<String, List<String>> factorMap, 
			List<String> factorNames,  Map<String, List<String>> assayMap)
		{
		this.setFactorLabels(factorNames);
		List <Mrc2ExperimentalDesignItem> sampleInfo = new ArrayList<Mrc2ExperimentalDesignItem>();
		
		for (int i = 0; i < sampleList.size(); i++)
			{
			SampleDTO s = sampleList.get(i);
			Mrc2ExperimentalDesignItem info = new Mrc2ExperimentalDesignItem();
			String sampleId = s.getSampleID();
			
			info.setSampleId(sampleId);
			info.setUserSampleId(s.getSampleName());
			
			List<String> sampleAssayNames = assayMap.get(sampleId);
			
			info.setDisplayAssayNames(sampleAssayNames);
			info.setAssayNames(sampleAssayNames, true);
			
			List<String> sampleFactorValues = new ArrayList<String>();

			for (int j  =0 ; j < factorNames.size();  j++)
				{
				String factorName = factorNames.get(j);
				List<String> valuesForFactor = factorMap.get(factorName);

				sampleFactorValues.add(valuesForFactor.get(i));
				}
			
			info.setFactorValues(sampleFactorValues);
			sampleInfo.add(info);
			}
		
		Collections.sort(sampleInfo, new Mrc2ExperimentalDesignItemComparator());
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
		
		List <Mrc2ExperimentalDesignItem> sampleInfo = new ArrayList<Mrc2ExperimentalDesignItem>();
		
		for (int i = 0; i < sampleList.size(); i++)
			{
			Sample s = sampleList.get(i);
			Mrc2ExperimentalDesignItem info = new Mrc2ExperimentalDesignItem();
			String sampleId = s.getSampleID();
			
			info.setSampleId(sampleId);
			info.setUserSampleId(s.getSampleName());
			
			List<String> sampleAssayNames = new ArrayList<String>();
			List <SampleAssay> assays = s.getSampleAssays();
			for (int j =0; j < assays.size(); j++)
				{
				String assayName = assays.get(j).getAssay().getAssayName();
				String assayNameShort = assayName.substring(0, Math.min(SHORT_LABEL_LEN, assayName.length()));
				String assayId = assays.get(j).getAssay().getAssayId();
				String fullName = assayNameShort + " (" + assayId + ")";
				if (fullAssayNames)
					sampleAssayNames.add(j, fullName);
				else
					sampleAssayNames.add(assayName);
				}
			
			info.setAssayNames(sampleAssayNames);
			
			info.setFactorValues(sampleService.getFactorValuesByIdSortedByName(sampleId));
			sampleInfo.add(info);
			}
		
		Collections.sort(sampleInfo, new Mrc2ExperimentalDesignItemComparator());
		infoFields = sampleInfo;
		}


	public String toString()
		{
		StringBuilder sb = new StringBuilder();
		
		for (Mrc2ExperimentalDesignItem item : this.getInfoFields())
			sb.append(item.toString() + System.getProperty("line.separator"));
		
		return sb.toString(); 
		}
	
	
	public List<Mrc2ExperimentalDesignItem> getInfoFields() {
		return infoFields;
	}


	public void setInfoFields(List<Mrc2ExperimentalDesignItem> infoFields) {
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



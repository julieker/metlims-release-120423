package edu.umich.brcf.metabolomics.panels.lipidshome.drcc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.service.GenusSpeciesService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.domain.Subject;
import edu.umich.brcf.shared.util.comparator.DrccSubjectInfoComparator;


public class DrccSubjectInfoSet implements Serializable
	{
	@SpringBean
	ExperimentService expService;
	
	@SpringBean 
	SampleService sampleService; 
	
	@SpringBean
	GenusSpeciesService genusSpeciesService;
	
	public List <DrccSubjectInfoItem> infoFields = new ArrayList<DrccSubjectInfoItem>();
	String mode = "drcc";

	public DrccSubjectInfoSet(String expId)
		{
		this(expId, "drcc");
		}
	
	public DrccSubjectInfoSet(String expId, String mode)
		{
		this.mode = mode;
		Injector.get().inject(this);
		initializeFromExperimentId(expId);
		}

	
	public void initializeFromExperimentId(String expId)
		{
		Experiment exp = expService.loadExperimentWithInfoForDrcc(expId);
		
		List<Sample> sampleList = exp.getSampleList();
		
		Map <String, DrccSubjectInfoItem> subjects = new HashMap<String, DrccSubjectInfoItem>();
		Subject subj;
		List <DrccSubjectInfoItem> subjectInfoList = new ArrayList<DrccSubjectInfoItem>();
		
		for (int i = 0; i < sampleList.size(); i++)
			{
			Sample sample = sampleList.get(i);
			subj = sample.getSubject();
			
			if (subj == null) 
				continue;
			
			String genusSpeciesName = ((sample != null && sample.getGenusOrSpecies() != null) ? sample.getGenusOrSpecies().getGenusName() : "");
			String genusSpeciesId = ((sample != null && sample.getGenusOrSpecies() != null ) ? sample.getGenusOrSpecies().getGsID().toString() : "");
			String taxId = ((subj == null || subj.getTaxId() ==null) ? "" : subj.getTaxId().toString());
			String userSubjId = (subj == null ? "" : subj.getUserSubjectId());
			String mrc2SubjId = (subj == null ? "" : subj.getSubjectId());
			
			String subjId = userSubjId + " / " + mrc2SubjId;
			
			//String genusSpecies = sampleList.get(i).getGenusOrSpecies().getGenusName();
	
			String subjectType = lookupSubjectType(taxId);
			String subjectSpecies = lookupSubjectSpecies(taxId);
			DrccSubjectInfoItem info = new DrccSubjectInfoItem(subjId, subjectType, genusSpeciesId, subjectSpecies, taxId);
			if (subj != null)
				subjects.put(subj.getSubjectId(), info);
			}
		
		Set<String> keys = subjects.keySet();
		for (String key : keys)
			{			
			subjectInfoList.add((DrccSubjectInfoItem) subjects.get(key));
			}
		
		Collections.sort(subjectInfoList, new DrccSubjectInfoComparator());
	
		this.infoFields = subjectInfoList;
		}
	
	
	private String lookupSubjectType(String taxId)
		{
		switch (taxId)
			{
			case "9606" : return "Human"; 
			case "10090" :
			case "9986" : 
			case "10114" : 
			case "7215" : 
			case "10116" : return "Animal";
			case "56780" : return "Cells";
			default : return "";
			}
		}

	private String lookupSubjectSpecies(String taxId)
		{
		List <String> speciesNames = genusSpeciesService.getSubjectSpeciesForTaxId(taxId);
		return (speciesNames == null ? "" : speciesNames.toString());
		}

	public List<DrccSubjectInfoItem> getInfoFields() {
		return infoFields;
	}

	public void setInfoFields(ArrayList<DrccSubjectInfoItem> infoFields) {
		this.infoFields = infoFields;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}
	
	
	
	}

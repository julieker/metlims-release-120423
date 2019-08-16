/////////////////////////////////////
//SubjectInfoSet.java
//Written by Jan Wigginton June 2015
//////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.lims.newexperiment.datacollectors;

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
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.domain.Subject;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.comparator.SubjectInfoComparator;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;


public class SubjectInfoSet implements Serializable
	{
	@SpringBean
	ExperimentService expService;
	
	@SpringBean 
	SampleService sampleService; 
	
	@SpringBean
	GenusSpeciesService genusSpeciesService;
	
	public List <SubjectInfoItem> infoFields = new ArrayList<SubjectInfoItem>();
	String mode = "drcc";

	
	public SubjectInfoSet() { } 
	
	
	public SubjectInfoSet(String expId)
		{
		this(expId, "drcc");
		}
	
	
	public SubjectInfoSet(String expId, String mode)
		{
		this.mode = mode;
		Injector.get().inject(this);
		initializeFromExperimentId(expId);
		}
	
	
	public SubjectInfoSet(Experiment experiment, String mode)
		{
		this.mode = mode;
		Injector.get().inject(this);
		initializeFromExperiment(experiment);
		}

	
	public void initializeFromExperimentId(String expId)
		{
		if (!StringUtils.isNullOrEmpty(expId))
			{
			initializeOptimizedFromExperimentId(expId);
			}
		}
			
	
	public void initializeOptimizedFromExperimentId(String expId)
		{
		if (!StringUtils.isNullOrEmpty(expId))
			{
			this.infoFields =  expService.loadOptimizedForSubjectTracking(expId);
			
			String runningTaxId = "", runningSpecies = "", runningSubjectType = "";
			for (SubjectInfoItem item : infoFields)
				{
				String taxId = item.getTaxonomyId();
				
				if (!runningTaxId.equals(taxId))
					if (!StringUtils.isNullOrEmpty(taxId))
						{
						runningSpecies = this.lookupSubjectSpecies(taxId);
						runningSubjectType = this.lookupSubjectType(taxId);
						runningTaxId = taxId;
						}
				
					item.setSubjectSpecies(runningSpecies);
					item.setSubjectType(runningSubjectType);
					}
				}
			}
	
	
	public void initializeFromExperiment(Experiment exp)
		{
		List<Sample> sampleList = exp.getSampleList();
		
		Map <String, SubjectInfoItem> subjects = new HashMap<String, SubjectInfoItem>();
		Subject subj;
		List <SubjectInfoItem> subjectInfoList = new ArrayList<SubjectInfoItem>();
		
		for (int i = 0; i < sampleList.size(); i++)
			{
			Sample sample = sampleList.get(i);
			subj = sample.getSubject();
			
			if (subj == null) 
				continue;
			
			String genusSpeciesId = ((sample != null && sample.getGenusOrSpecies() != null ) ? sample.getGenusOrSpecies().getGsID().toString() : "");
			String taxId = ((subj == null || subj.getTaxId() ==null) ? "" : subj.getTaxId().toString());
			String userSubjId = (subj == null ? "" : subj.getUserSubjectId());
			String mrc2SubjId = (subj == null ? "" : subj.getSubjectId());
			
			String subjId = userSubjId + " / " + mrc2SubjId;
			
			String subjectType = lookupSubjectType(taxId);
			String subjectSpecies = lookupSubjectSpecies(taxId);
			SubjectInfoItem info = new SubjectInfoItem(subjId, subjectType, genusSpeciesId, subjectSpecies, taxId);
			if (subj != null)
				subjects.put(subj.getSubjectId(), info);
			}
		
		Set<String> keys = subjects.keySet();
		for (String key : keys)
			subjectInfoList.add((SubjectInfoItem) subjects.get(key));
		
		Collections.sort(subjectInfoList, new SubjectInfoComparator());
	
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
			case "9612" : 
			case "10116" : return "Animal";
			case "56780" : return "Cells";
			default : return "Unknown";
			}
		}

	
	private String lookupSubjectSpecies(String taxId)
		{
		List <String> speciesNames = genusSpeciesService.getSubjectSpeciesForTaxId(taxId);
		return (speciesNames == null ? "" : speciesNames.toString());
		}
	

	public List<SubjectInfoItem> getInfoFields() 
		{
		return infoFields;
		}

	public void setInfoFields(List<SubjectInfoItem> infoFields) 
		{
		this.infoFields = infoFields;
		}

	public String getMode() 
		{
		return mode;
		}

	public void setMode(String mode) 
		{
		this.mode = mode;
		}
	}

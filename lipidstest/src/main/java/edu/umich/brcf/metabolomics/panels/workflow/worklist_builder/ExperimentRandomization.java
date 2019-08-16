////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  ExperimentRandomization.java
//  Written by Jan Wigginton
//  March 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.SampleService;


public class ExperimentRandomization implements Serializable
	{
	@SpringBean
	private SampleService sampleService;
	
	private HashMap<String, RandomizedSample> samplesHash = new HashMap<String, RandomizedSample>();
	private ArrayList<RandomizedSample> samplesArray = new ArrayList<RandomizedSample>();
	private String expId;
	
	
	public ExperimentRandomization()
		{
		this("");
		}
	
	public ExperimentRandomization(String expId)
		{
		this.expId = expId;
		
		Injector.get().inject(this);
		}
	
	public void addSample(String sampleName, RandomizedSample sample)
		{
		getSamplesHash().put(sampleName, sample);
		getSamplesArray().add(sample);
		}
	
	
	public HashMap<String, RandomizedSample> getSamplesHash() 
		{
		return samplesHash;
		}

	
	public void setSamplesHash(HashMap<String, RandomizedSample> samplesHash) 
		{
		this.samplesHash = samplesHash;
		}
	
	
	public ArrayList<RandomizedSample> getSamplesArray() 
		{
		return samplesArray;
		}

	
	public void setSamplesArray(ArrayList<RandomizedSample> samplesArray) 
		{
		this.samplesArray = samplesArray;
		}
	
	
	public void setExpId(String eid)
		{
		expId = eid;
		}
	
	
	public String getExpId()
		{
		return expId;
		}
	
	
	private List <String> grabAllExperimentSamples()
		{
		return sampleService.sampleIdsForExpId(expId);
		}
	
	// Issue 268 include assay
	private List <String> grabAllExperimentSamplesAssay(String assayId)
	    {
	    return sampleService.sampleIdsForExpIdAssayId(expId, assayId);
	    }
	
	// issue 268 include assay
	public Boolean hasAllSamplesForExpAssay(String assayId)
		{
		List <String> targetedSamples = grabAllExperimentSamplesAssay(assayId);
		printAllSamples();		
		for (int i =0; i < targetedSamples.size(); i++)
			{
			String targetSample = targetedSamples.get(i);
			
			if (!(samplesHash.containsKey(targetSample)))
				return false;
			}	
		return true;	
		}
	
	
	private void printAllSamples()
		{
		for (int i = 0; i < samplesArray.size(); i++)
			{
			RandomizedSample s = samplesArray.get(i);
			System.out.println(s.getSampleName()  + " " + s.getRandomOrder() + " " + s.getRandomValue());
			}
		}
	
	
    public void orderIfSpecified()
		{
    	Collections.sort(this.samplesArray);
		}
    
    
    public Boolean hasDuplicateOrders()
    	{
    	orderIfSpecified();
    	printAllSamples();
    	
    	Boolean hasOrderSpecified = false;
    	for (int i = 0; i < samplesArray.size(); i++)
    		{
    		hasOrderSpecified |= (samplesArray.get(i).getRandomOrder() != -1);
    		if (hasOrderSpecified) break;
    		}
    	
    	if (hasOrderSpecified)
    		for (int i = 1; i < samplesArray.size(); i++)
    			if (samplesArray.get(i).getRandomOrder().equals(samplesArray.get(i - 1).getRandomOrder()))
    				return true;
    	
    	return false;
    	}
	}

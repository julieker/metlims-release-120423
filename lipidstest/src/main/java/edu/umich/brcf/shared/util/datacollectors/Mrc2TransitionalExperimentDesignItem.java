////////////////////////////////////////////////////
// Mrc2TransitionalExperimentDesignItem.java
// Written by Jan Wigginton, Jun 14, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.datacollectors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import edu.umich.brcf.shared.util.ObjectHandler;



public class Mrc2TransitionalExperimentDesignItem implements Serializable // //IWriteConvertable 
	{
	private String sampleId, sampleLabel = "";
	private List<String> assaysForSample; 
	private Map<String, String> factorValueMap;
	
	
	public Mrc2TransitionalExperimentDesignItem()
		{
		sampleId = "";
		assaysForSample = new ArrayList<String>();
		factorValueMap = new HashMap<String, String>();
		}
	
	public String getSampleId()
		{
		return sampleId;
		}
	
	public void setSampleId(String sampleId)
		{
		this.sampleId = sampleId;
		}
	
	public List<String> getAssaysForSample()
		{
		return assaysForSample;
		}
	
	
	public void setAssaysForSample(List<String> assays)
		{
		assaysForSample = new ArrayList<String>();
		for (String assay : assays)
			addAssayForSample(assay);
		}
	
	public void addAssayForSample(String assay)
		{
		assaysForSample.add(assay);
		}
	
	
	
	public String getValueForFactor(String factorName)
		{
		return this.factorValueMap.get(factorName);
		}
	
	public void setFactorValueMap(Map<String, String> factorValueMapIn)
		{
		this.factorValueMap = new HashMap<String, String>();
		if (factorValueMapIn != null)
			factorValueMap.putAll(factorValueMapIn);
		}
	

	public Map<String, String> getFactorValueMap()
		{
		return factorValueMap;
		}
	
	public void setValueForFactor(String factor, String value)
		{
		factorValueMap.put(factor, value);
		}


	@Override
	public String toString()
		{
		Map<String, String> init = ObjectHandler.createObjectMap(this);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(init.toString());
		sb.append(" Assays : " + assaysForSample);
		sb.append(" Factors : " + factorValueMap.toString());
		
		return sb.toString();
		}
	
	
	public String getSampleLabel()
		{
		return sampleLabel;
		}


	public void setSampleLabel(String sampleLabel)
		{
		this.sampleLabel = sampleLabel;
		}
	}
	
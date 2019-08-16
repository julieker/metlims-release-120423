////////////////////////////////////////////////////
//SampleFactorMapping.java
//Written by Jan Wigginton, October 2015
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.admin.sample_submission;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SampleFactorMapping implements Serializable
	{
	private String sampleId, researcherSampleId;
	private List<String> factorValues;
	private Map<String, String> factorMaps;
		
	public SampleFactorMapping(String key, String subjectId, int nFactorValues)
		{
		factorValues = new ArrayList<String>();
		for (int i = 0; i < nFactorValues; i++)
			factorValues.add("");
		
		factorMaps = new HashMap<String, String>();
		}
	
	
	public void addFactorMap(String factorName, String factorValue)
		{
		factorMaps.put(factorName, factorValue);
		}
	
	public void addFactorValue(String factorValue)
		{
		factorValues.add(factorValue);
		}
	
	public String getSampleId() 
		{
		return sampleId;
		}

	public void setSampleId(String sampleId) 
		{
		this.sampleId = sampleId;
		} 

	public List<String> getFactorValues()
		{
		return factorValues;
		}
	
	public String getFactorValues(int i) 
		{
		try { return factorValues.get(i); }
		catch (IndexOutOfBoundsException e) { System.out.println("Factor value get failed on index "+ i); return "";  }
		}
	
	public void setFactorValues(int i, String value)
		{
		try { factorValues.set(i, value);  }
		catch (IndexOutOfBoundsException e) { System.out.println("Factor value set failed");  }
		}

	public String getResearcherSampleId() 
		{
		return researcherSampleId;
		}

	public void setResearcherSampleId(String researcherSampleId) 
		{
		this.researcherSampleId = researcherSampleId;
		}
	
	public Map<String, String> getFactorMaps()
		{
		return factorMaps;
		}
	
	public String getFactorValue(String factorName)
		{
		return factorMaps.get(factorName);
		}
	}


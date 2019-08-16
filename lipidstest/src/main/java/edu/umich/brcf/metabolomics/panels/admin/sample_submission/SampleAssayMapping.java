////////////////////////////////////////////////////
//SampleAssayMapping.java
//Written by Jan Wigginton October 2015// SampleAssayMapping.java

package edu.umich.brcf.metabolomics.panels.admin.sample_submission;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class SampleAssayMapping implements Serializable
	{
	private String sampleId;
	private List<String> sampleAssays;
	int nAssaysTracked;
	
	public SampleAssayMapping(String key, int nTracked)
		{
		sampleId = key;
		this.nAssaysTracked = nTracked;
		sampleAssays = new ArrayList<String>();
		for (int i = 0; i < nAssaysTracked; i++)
			sampleAssays.add("");
		}
	

	public String getSampleId() 
		{
		return sampleId;
		}

	public void setSampleId(String sampleId) 
		{
		this.sampleId = sampleId;
		}
	
	public List<String> getSampleAssays() 
		{
		return sampleAssays;
		}

	public String getSampleAssays(int i) 
		{
		String value = "";
		try { value = sampleAssays.get(i); }
		catch (IndexOutOfBoundsException e) { }
		return value;
		}
	
	public void setSampleAssays(int i, String value)
		{
		try {sampleAssays.set(i, value); }
		catch (IndexOutOfBoundsException e) { }
		}
	}
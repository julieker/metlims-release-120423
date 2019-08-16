////////////////////////////////////////////////////
// SimpleClientSampleAssayBean.java
// Written by Jan Wigginton, Mar 22, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.datacollectors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.umich.brcf.shared.util.structures.Pair;



public class SimpleClientSampleAssaysBean implements Serializable
	{
	private String sampleId;
	private String chearSampleId;
	private List <Pair> assayNamesAndStatuses;
	
	
	public SimpleClientSampleAssaysBean()  
		{ 
		assayNamesAndStatuses = new ArrayList<Pair>();  
		} 
	
	public SimpleClientSampleAssaysBean(String sampleId, String chearSampleId)
		{
		this.sampleId=sampleId;
		
		this.chearSampleId = chearSampleId;
		this.assayNamesAndStatuses = new ArrayList<Pair>();  
		}
	
	public String getSampleId() 
		{
		return sampleId;
		}
	
	public void setSampleId(String sampleId) 
		{
		this.sampleId = sampleId;
		}
	
	
	public String getChearSampleId()
		{
		return chearSampleId;
		}
	
	
	public void setChearSampleId(String chearSampleId)
		{
		this.chearSampleId = chearSampleId;
		}
	
	public void addAssayStatus(String aname, String status)
		{
		assayNamesAndStatuses.add(new Pair(aname, status));
		}
	
	public Integer getNAssays()
		{
		return assayNamesAndStatuses.size();
		}
	

public List<Pair> getAssayNamesAndStatuses()
	{
	return assayNamesAndStatuses;
	}


public void setAssayNamesAndStatuses(List<Pair> assayNamesAndStatuses)
	{
	this.assayNamesAndStatuses = assayNamesAndStatuses;
	}
}

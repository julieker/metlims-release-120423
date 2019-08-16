////////////////////////////////////////////////////
// ClientSampleAssayAliquotsBean.java
// Written by Jan Wigginton, Jun 5, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.layers.dto;

import java.io.Serializable;
import java.util.List;

import edu.umich.brcf.shared.layers.domain.Aliquot;



public class ClientSampleAliquotsBean implements Serializable
	{
	private String sampleId;
	private List <Aliquot> sampleAliquots;

	
	public ClientSampleAliquotsBean() 
		{
		}
	
	public ClientSampleAliquotsBean(String sampleId, List<Aliquot> sampleAliquots)
		{
		this.sampleId=sampleId;
		this.sampleAliquots = sampleAliquots;
		}
	
	public String getSampleId() 
		{
		return sampleId;
		}
	
	public void setSampleId(String sampleId) 
		{
		this.sampleId = sampleId;
		}
	
	public List<Aliquot> getSampleAliquots() 
		{
		return sampleAliquots;
		}
	
	public void setSampleAliquots(List<Aliquot> sampleAliquots) 
		{
		this.sampleAliquots = sampleAliquots;
		}

	}


/*
public class ClientSampleAssaysBean implements Serializable
	{
	private String sampleId;
	private List <SampleAssay> sampleAssays;
	
	//private List<String> assays;
	//private List<String> sampleStatus;
	
	public ClientSampleAssaysBean() 
		{
		}
	
	public ClientSampleAssaysBean(String sampleId, List<String> assays, List<String> sampleStatus)
		{
		this.sampleId=sampleId;
		this.assays=assays;
		this.sampleStatus = sampleStatus;
		}
	
	public String getSampleId() 
		{
		return sampleId;
		}
	
	public void setSampleId(String sampleId) 
		{
		this.sampleId = sampleId;
		}
	
	public List<String> getAssays() 
		{
		return assays;
		}
	
	public void setAssays(List<String> assays) 
		{
		this.assays = assays;
		}
	
	public void setSampleStatus(List <String> status)
		{
		this.sampleStatus = status;
		}	
	
	public List<String>getSampleStatus()
		{
		return sampleStatus;
		}
	}
	
*/
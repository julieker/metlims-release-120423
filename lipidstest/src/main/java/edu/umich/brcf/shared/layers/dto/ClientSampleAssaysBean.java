package edu.umich.brcf.shared.layers.dto;

import java.io.Serializable;
import java.util.List;

import edu.umich.brcf.shared.layers.domain.SampleAssay;


public class ClientSampleAssaysBean implements Serializable
	{
	private String sampleId;
	private List<SampleAssay> sampleAssays;

	public ClientSampleAssaysBean()
		{
		}

	public ClientSampleAssaysBean(String sampleId,
			List<SampleAssay> sampleAssays)
		{
		this.sampleId = sampleId;
		this.sampleAssays = sampleAssays;
		}

	public String getSampleId()
		{
		return sampleId;
		}

	public void setSampleId(String sampleId)
		{
		this.sampleId = sampleId;
		}

	public List<SampleAssay> getSampleAssays()
		{
		return sampleAssays;
		}

	public void setSampleAssays(List<SampleAssay> sampleAssays)
		{
		this.sampleAssays = sampleAssays;
		}

	}

/*
 * public class ClientSampleAssaysBean implements Serializable { private String
 * sampleId; private List <SampleAssay> sampleAssays;
 * 
 * //private List<String> assays; //private List<String> sampleStatus;
 * 
 * public ClientSampleAssaysBean() { }
 * 
 * public ClientSampleAssaysBean(String sampleId, List<String> assays,
 * List<String> sampleStatus) { this.sampleId=sampleId; this.assays=assays;
 * this.sampleStatus = sampleStatus; }
 * 
 * public String getSampleId() { return sampleId; }
 * 
 * public void setSampleId(String sampleId) { this.sampleId = sampleId; }
 * 
 * public List<String> getAssays() { return assays; }
 * 
 * public void setAssays(List<String> assays) { this.assays = assays; }
 * 
 * public void setSampleStatus(List <String> status) { this.sampleStatus =
 * status; }
 * 
 * public List<String>getSampleStatus() { return sampleStatus; } }
 */
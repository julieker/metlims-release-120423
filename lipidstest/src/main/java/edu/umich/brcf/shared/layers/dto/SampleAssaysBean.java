package edu.umich.brcf.shared.layers.dto;

import java.io.Serializable;
import java.util.List;


public class SampleAssaysBean implements Serializable
	{

	private String sampleId;
	private List<String> assays;

	public SampleAssaysBean()
		{

		}

	public SampleAssaysBean(String sampleId, List<String> assays)
		{
		this.sampleId = sampleId;
		this.assays = assays;
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
	}

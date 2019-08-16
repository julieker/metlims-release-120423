package edu.umich.brcf.shared.layers.dto;

import java.io.Serializable;


public class SampleAssayDTO implements Serializable
	{
	public static SampleAssayDTO instance(String sampleId, String assayId,
			String statusId)
		{
		return new SampleAssayDTO(sampleId, assayId, statusId);
		}

	/*
	 * public static SampleAssayDTO instance(SampleAssay sampleAssay) { return
	 * new SampleAssayDTO(sampleAssay.getSample().getSampleID(),
	 * sampleAssay.getAssay().getAssayId(), sampleAssay.getStatus().getId()); }
	 */
	String assayId;
	String statusId;
	String sampleId;

	private SampleAssayDTO(String sampleId, String assayId, String status)
		{
		this.sampleId = sampleId;
		this.assayId = assayId;
		this.statusId = status;
		}

	public SampleAssayDTO()
		{
		}

	public String getAssayId()
		{
		return assayId;
		}

	public String getStatusId()
		{
		return statusId;
		}

	public String getSampleId()
		{
		return sampleId;
		}

	public void setAssayId(String assayId)
		{
		this.assayId = assayId;
		}

	public void setStatusId(String statusId)
		{
		this.statusId = statusId;
		}

	public void setSampleId(String sampleId)
		{
		this.sampleId = sampleId;
		}

	}

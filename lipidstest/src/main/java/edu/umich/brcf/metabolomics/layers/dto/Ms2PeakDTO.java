package edu.umich.brcf.metabolomics.layers.dto;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//MS2PeakDTO.java
//Written by Jan Wigginton 04/10/15
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

import java.io.Serializable;

import edu.umich.brcf.metabolomics.layers.domain.Ms2Peak;


public class Ms2PeakDTO implements Serializable
	{
	public static Ms2PeakDTO instance(String psid, String smid, Double pa)
		{
		return new Ms2PeakDTO(null, psid, smid, pa);
		}

	public static Ms2PeakDTO instance(Ms2Peak peak)
		{
		return new Ms2PeakDTO(peak.getPeakId(), peak.getPeakSet()
				.getPeakSetId(), peak.getSampleMapId(), peak.getPeakArea());
		}

	private Long peakId;
	private String peakSetId;
	private String sampleMapId;
	private Double peakArea;

	private String tempSampleLabel;

	private Ms2PeakDTO(Long pid, String psid, String smid, Double pa)
		{
		peakId = pid;
		peakSetId = psid;
		sampleMapId = smid;
		peakArea = pa;
		tempSampleLabel = "";
		}

	public Ms2PeakDTO()
		{
		}

	public Long getPeakId()
		{
		return peakId;
		}

	public void setPeakId(Long peakId)
		{
		this.peakId = peakId;
		}

	public String getPeakSetId()
		{
		return peakSetId;
		}

	public void setPeakSetId(String peakSetId)
		{
		this.peakSetId = peakSetId;
		}

	public String getSampleMapId()
		{
		return sampleMapId;
		}

	public void setSampleMapId(String smid)
		{
		this.sampleMapId = smid;
		}

	public Double getPeakArea()
		{
		return peakArea;
		}

	public void setPeakArea(Double peakArea)
		{
		this.peakArea = peakArea;
		}

	public String getTempSampleLabel()
		{
		return tempSampleLabel;
		}

	public void setTempSampleLabel(String label)
		{
		tempSampleLabel = label;
		}
	}

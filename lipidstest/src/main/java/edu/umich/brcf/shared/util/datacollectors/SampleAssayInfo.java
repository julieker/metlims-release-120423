////////////////////////////////////////////////////
// SampleAssayInfo.java
// Written by Jan Wigginton, Mar 22, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.datacollectors;

import java.io.Serializable;

public class SampleAssayInfo implements Serializable
	{
	private String sampleId = "", chearSampleid = "", assayId = "", assayName = "", expId= "";
	
	public SampleAssayInfo() { }
	
	public SampleAssayInfo(String sampleId, String chearSampleId, String assayId, String assayName, String expId) 
		{
		this.sampleId = sampleId;
		this.chearSampleid = chearSampleId;
		this.assayId = assayId;
		this.assayName = assayName;
		this.expId = expId;
		}

	public String getSampleId()
		{
		return sampleId;
		}

	public String getChearSampleid()
		{
		return chearSampleid;
		}

	public String getAssayId()
		{
		return assayId;
		}

	public String getAssayName()
		{
		return assayName;
		}

	public String getExpId()
		{
		return expId;
		}

	public void setSampleId(String sampleId)
		{
		this.sampleId = sampleId;
		}

	public void setChearSampleid(String chearSampleid)
		{
		this.chearSampleid = chearSampleid;
		}

	public void setAssayId(String assayId)
		{
		this.assayId = assayId;
		}

	public void setAssayName(String assayName)
		{
		this.assayName = assayName;
		}

	public void setExpId(String expId)
		{
		this.expId = expId;
		}
	}
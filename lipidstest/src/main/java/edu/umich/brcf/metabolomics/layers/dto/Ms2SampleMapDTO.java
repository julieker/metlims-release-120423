///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Ms2SampleMapDTO.java
//Written by Jan Wigginton June 2015
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.layers.dto;

import java.io.Serializable;

import edu.umich.brcf.metabolomics.layers.domain.Ms2SampleMap;


public class Ms2SampleMapDTO implements Serializable
	{
	public static Ms2SampleMapDTO instance(String sid, String sampleTag, String dsid, Integer runOrderIdx, String iid, 
	String comment) 
		{
		return new Ms2SampleMapDTO(null,sid, sampleTag, dsid, runOrderIdx, iid, comment); 
		}
	
	public static Ms2SampleMapDTO instance(Ms2SampleMap map) 
		{
		return new Ms2SampleMapDTO(map.getSampleMapId(), map.getSampleId(), map.getSampleTag(), map.getDataSetId(),
		map.getRunOrderIdx(), map.getOtherId(), map.getComment()); 
		}
	
	private String sampleMapId;
	private String sampleId;
	private String sampleTag;
	private String dataSetId;
	private Integer runOrderIdx;
	private String otherId;
	private String comment;
	
	
	private Ms2SampleMapDTO(String smid, String sid, String st, String dsid, Integer roi, String iid, String comment )
		{
		this.sampleMapId = smid;
		this.sampleId = sid;
		this.sampleTag = st;
		this.dataSetId = dsid;
		this.runOrderIdx = roi;
		this.otherId = iid;
		}
	
	public String getSampleMapId()
		{
		return sampleMapId;
		}
	
	public void setSampleMapId(String sampleMapId)
		{
		this.sampleMapId = sampleMapId;
		}
	
	public String getSampleId()
		{
		return sampleId;
		}
	
	public void setSampleId(String sampleId)
		{
		this.sampleId = sampleId;
		}
	
	public String getSampleTag()
		{
		return sampleTag;
		}
	
	public void setSampleTag(String sampleTag)
		{
		this.sampleTag = sampleTag;
		}
	
	public String getDataSetId()
		{
		return dataSetId;
		}
	
	public void setDataSetId(String dataSetId)
		{
		this.dataSetId = dataSetId;
		}
	
	public Integer getRunOrderIdx() 
		{
		return runOrderIdx;
		}
	
	public void setRunOrderIdx(Integer runOrderIdx) 
		{
		this.runOrderIdx = runOrderIdx;
		}
	
	public String getOtherId() 
		{
		return otherId;
		}
	
	public void setOtherId(String otherId) 
		{
		this.otherId = otherId;
		}
	
	public String getComment()
		{
		return comment;
		}
	
	public void setComment(String comment)
		{
		this.comment = comment;
		}
	}
	

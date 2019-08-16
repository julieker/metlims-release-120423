///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Ms2DataSetDTO.java
//Written by Jan Wigginton 04/29/15
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.layers.dto;

import java.util.Calendar;

import edu.umich.brcf.metabolomics.layers.domain.Ms2DataSet;


public class Ms2DataSetDTO
	{
	public static Ms2DataSetDTO instance(String dsid, String eid, Calendar rDate, Calendar uDate, Integer rep, String upBy,
	 String ionMode, String dataNotation)
		{
		return new Ms2DataSetDTO(dsid, eid, rDate, uDate, rep, upBy, ionMode, dataNotation);
		}

	public static Ms2DataSetDTO instance(Ms2DataSet dataSet)
		{
		return new Ms2DataSetDTO(dataSet.getDataSetId(), dataSet.getExpId(), dataSet.getRunDate(), dataSet.getUploadDate(),dataSet.getReplicate(), dataSet.getUploadedBy(),
		 dataSet.getIonMode(), dataSet.getDataNotation());
		}

	private String dataSetId;
	private String expId;
	private Calendar runDate;
	private Calendar uploadDate;
	private Integer replicate;
	private String uploadedBy;
	private String ionMode;
	private String dataNotation;

	public Ms2DataSetDTO() { }

	public Ms2DataSetDTO(String dsid, String eid, Calendar rDate, Calendar uDate, Integer rep, String upBy, String ionMode, String dataNotation)
		{
		this.dataSetId = dsid;
		this.expId = eid;
		this.runDate = rDate;
		this.uploadDate = uDate;
		this.replicate = rep;
		this.uploadedBy = upBy;
		this.ionMode = ionMode;
		this.dataNotation = dataNotation;
		}

	public String getDataSetId()
		{
		return dataSetId;
		}

	public void setDataSetId(String dataSetId)
		{
		this.dataSetId = dataSetId;
		}

	public String getExpId()
		{
		return expId;
		}

	public void setExpId(String expId)
		{
		this.expId = expId;
		}

	public Calendar getRunDate()
		{
		return runDate;
		}

	public void setRunDate(Calendar runDate)
		{
		this.runDate = runDate;
		}

	public Calendar getUploadDate()
		{
		return uploadDate;
		}

	public void setUploadDate(Calendar uploadDate)
		{
		this.uploadDate = uploadDate;
		}

	public Integer getReplicate()
		{
		return replicate;
		}

	public void setReplicate(Integer replicate)
		{
		this.replicate = replicate;
		}

	public String getUploadedBy()
		{
		return this.uploadedBy;
		}

	public void setUploadedBy(String upBy)
		{
		this.uploadedBy = upBy;
		}

	public String getIonMode()
		{
		return ionMode;
		}

	public void setIonMode(String ionMode)
		{
		this.ionMode = ionMode;
		}

	public String getDataNotation()
		{
		return this.dataNotation;
		}

	public void setDataNotation(String note)
		{
		this.dataNotation = note;
		}
	}

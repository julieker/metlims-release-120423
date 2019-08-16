//////////////////////////////////////////
// ExperimentRunInfo.java
// Written by Jan Wigginton August 2015
//////////////////////////////////////////

package edu.umich.brcf.shared.util.datacollectors;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import edu.umich.brcf.shared.util.utilpackages.DateUtils;

public class ExperimentRunInfo implements Serializable
	{
	private String expId, expMode, dataNotation, uploadedFileName;
	private Date runDate;

	public ExperimentRunInfo()
		{
		}

	ExperimentRunInfo(String expId, String expMode, String dataNotation,
			String uploadedFileName, Date runDate)
		{
		this.expId = expId;
		this.expMode = expMode;
		this.dataNotation = dataNotation;
		this.uploadedFileName = uploadedFileName;
		this.runDate = runDate;
		}

	public String getExpId()
		{
		return expId;
		}

	public void setExpId(String expId)
		{
		this.expId = expId;
		}

	public String getExpMode()
		{
		return expMode;
		}

	public void setExpMode(String expMode)
		{
		this.expMode = expMode;
		}

	public String getDataNotation()
		{
		return dataNotation;
		}

	public void setDataNotation(String dataNotation)
		{
		this.dataNotation = dataNotation;
		}

	public String getUploadedFileName()
		{
		return uploadedFileName;
		}

	public void setUploadedFileName(String uploadedFileName)
		{
		this.uploadedFileName = uploadedFileName;
		}

	public Date getRunDate()
		{
		return runDate;
		}

	public void setRunDate(Date runDate)
		{
		this.runDate = runDate;
		}

	public Calendar getRunDateAsCalendar()
		{
		return DateUtils.dateAsCalendar(runDate);
		}
	}

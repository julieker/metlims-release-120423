package edu.umich.brcf.shared.layers.dto;

import java.io.Serializable;

import java.util.Calendar;

import edu.umich.brcf.shared.layers.domain.ClientReport;


public class ClientReportDTO implements Serializable
	{
	String reportId;
	String expId;
	String loadedBy;
	Calendar dateCreated;
	String fileName;
	String fileType;
	String assayId;

	public ClientReportDTO newFromExpInitializedReport(ClientReport rpt)
		{
		if (rpt.getExp() != null)
			return new ClientReportDTO(rpt);

		return new ClientReportDTO();
		}

	public ClientReportDTO(ClientReport rpt)
		{
		this.reportId = rpt.getReportId().toString(); // reportId;
		this.expId = rpt.getExp().getExpID(); // expId;
		this.loadedBy = rpt.getLoadedBy(); // loadedBy;
		this.dateCreated = rpt.getDateCreated(); // dateCreated;
		this.fileName = rpt.getFileName(); // fileName;
		this.fileType = rpt.getFileType(); // fileType;
		this.assayId = rpt.getAssayId(); // assayId;
		}

	public ClientReportDTO(String reportId, String expId, String loadedBy,
			Calendar dateCreated, String fileName, String fileType, String assayId)
		{
		this.reportId = reportId;
		this.expId = expId;
		this.loadedBy = loadedBy;
		this.dateCreated = dateCreated;
		this.fileName = fileName;
		this.fileType = fileType;
		this.assayId = assayId;
		}

	public ClientReportDTO() {  } 


	public String getReportId()
		{
		return reportId;
		}

	public void setReportId(String reportId)
		{
		this.reportId = reportId;
		}

	public String getExpId()
		{
		return expId;
		}

	public void setExpId(String expId)
		{
		this.expId = expId;
		}

	public String getLoadedBy()
		{
		return loadedBy;
		}

	public void setLoadedBy(String loadedBy)
		{
		this.loadedBy = loadedBy;
		}

	public Calendar getDateCreated()
		{
		return dateCreated;
		}

	public void setDateCreated(Calendar dateCreated)
		{
		this.dateCreated = dateCreated;
		}

	public String getFileName()
		{
		return fileName;
		}

	public void setFileName(String fileName)
		{
		this.fileName = fileName;
		}

	public String getFileType()
		{
		return fileType;
		}

	public void setFileType(String fileType)
		{
		this.fileType = fileType;
		}

	public String getAssayId()
		{
		return assayId;
		}

	public void setAssayId(String assayId)
		{
		this.assayId = assayId;
		}

	}

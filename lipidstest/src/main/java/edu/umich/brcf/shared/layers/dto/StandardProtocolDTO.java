////////////////////////////////////////////////////
// StandardProtocolDTO.java
// Written by Jan Wigginton, Dec 3, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.layers.dto;

import java.io.Serializable;
import java.util.Calendar;

import edu.umich.brcf.shared.layers.domain.StandardProtocol;


public class StandardProtocolDTO implements Serializable
	{
	public static StandardProtocolDTO instance(String protocolId, String assayId, String sampleType, String loadedBy,
			Calendar startDate, Calendar retiredDate, String fileName, String fileType)
		{
		return new StandardProtocolDTO(protocolId, assayId, sampleType, loadedBy, startDate, retiredDate, fileName, fileType);
		}

	public static StandardProtocolDTO instance(StandardProtocol protocol)
		{
		String assayId = protocol.getAssay()  == null ? "" :  protocol.getAssay().getAssayId();
		
		return new StandardProtocolDTO(protocol.getProtocolId(), assayId, protocol.getSampleType(), protocol.getLoadedBy(),
				protocol.getStartDate(), protocol.getRetiredDate(), protocol.getFileName(), protocol.getFileType());
		}
	
	private String protocolId;
	private String assayId;
	private String sampleType;
	private String loadedBy;
	private Calendar startDate;
	private Calendar retiredDate;
	private byte [] fileContents;
	private String fileName;
	private String fileType;
	
	
	public StandardProtocolDTO() { }
	
	private StandardProtocolDTO(String protocolId, String assayId, String sampleType, String loadedBy, Calendar startDate, 
			Calendar retiredDate, String fileName, String fileType)
		{
		this.protocolId = protocolId;
		this.assayId = assayId;
		this.sampleType = sampleType;
		this.loadedBy = loadedBy;
		this.startDate = startDate;
		this.retiredDate = retiredDate;
		this.fileName = fileName;
		this.fileType = fileType;
		}

	public String getProtocolId()
		{
		return protocolId;
		}

	public String getAssayId()
		{
		return assayId;
		}

	public String getSampleType()
		{
		return sampleType;
		}

	public String getLoadedBy()
		{
		return loadedBy;
		}

	public Calendar getStartDate()
		{
		return startDate;
		}

	public Calendar getRetiredDate()
		{
		return retiredDate;
		}

	public byte[] getFileContents()
		{
		return fileContents;
		}

	public String getFileName()
		{
		return fileName;
		}

	public String getFileType()
		{
		return fileType;
		}

	public void setProtocolId(String protocolId)
		{
		this.protocolId = protocolId;
		}

	public void setAssayId(String assayId)
		{
		this.assayId = assayId;
		}

	public void setSampleType(String sampleType)
		{
		this.sampleType = sampleType;
		}

	public void setLoadedBy(String loadedBy)
		{
		this.loadedBy = loadedBy;
		}

	public void setStartDate(Calendar startDate)
		{
		this.startDate = startDate;
		}

	public void setRetiredDate(Calendar retiredDate)
		{
		this.retiredDate = retiredDate;
		}

	public void setFileContents(byte[] fileContents)
		{
		this.fileContents = fileContents;
		}

	public void setFileName(String fileName)
		{
		this.fileName = fileName;
		}

	public void setFileType(String fileType)
		{
		this.fileType = fileType;
		}
	
	}
	

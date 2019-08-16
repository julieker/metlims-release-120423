////////////////////////////////////////////////////
// StandardProtocolDocument.java
// Written by Jan Wigginton, Dec 2, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.layers.domain;


import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.wicket.util.io.IClusterable;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import edu.umich.brcf.shared.util.utilpackages.DateUtils;
/*
CREATE TABLE METLIMS.STANDARD_PROTOCOLS
(
PROTOCOL_ID CHAR(6),
ASSAY_ID CHAR(4), 
SAMPLE_TYPE VARCHAR2(30),
UPLOADED_BY CHAR(6),
START_DATE DATE,
RETIRED_DATE DATE, 
PROTOCOL_FILE BLOB,
FILE_NAME VARCHAR2(300),
FILE_TYPE VARCHAR2(100),
DELETED_FLAG CHAR(1),
CONSTRAINT  standard_protocol_pk PRIMARY KEY (PROTOCOL_ID)
) */


@Entity
@Table(name = "STANDARD_PROTOCOLS")
public class StandardProtocol implements Serializable
	{
	public static StandardProtocol instance(Assay assay, String sampleType, String loadedBy, Calendar startDate, Calendar retiredDate,  
		byte[] contents, String fileName,  String fileType )
		{
		return new StandardProtocol(null, assay, sampleType, loadedBy, startDate, retiredDate,  contents, fileName, fileType);
		}
	
	public static String idFormat = "(S)(D))\\d{4}";
	@Id
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
			@Parameter(name = "idClass", value = "StandardProtocol"),
			@Parameter(name = "width", value = "6") })
	@Column(name = "PROTOCOL_ID", unique = true, nullable = false, length = 6, columnDefinition = "CHAR(6)")
	private String protocolId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ASSAY_ID", referencedColumnName = "ASSAY_ID", nullable = false)
	private Assay assay;
	
	@Basic
	@Column(name = "SAMPLE_TYPE", nullable = true, columnDefinition = "VARCHAR2(30)")
	private String sampleType;
	
	
	@Basic()
	@Column(name = "UPLOADED_BY", nullable = false, columnDefinition = "CHAR(6)")
	private String loadedBy;
	
	@Basic()
	@Column(name = "START_DATE", nullable = false, columnDefinition = "DATE")
	private Calendar startDate;
	
	@Basic()
	@Column(name = "RETIRED_DATE", nullable = false, columnDefinition = "DATE")
	private Calendar retiredDate;
	
	@Basic()
	@Column(name = "FILE_NAME", nullable = true, columnDefinition = "VARCHAR2(300)")
	private String fileName;
	
	@Basic()
	@Column(name = "FILE_TYPE", nullable = true, columnDefinition = "VARCHAR2(100)")
	private String fileType;
	
	@Lob()
	@Column(name = "PROTOCOL_FILE", nullable = false, columnDefinition = "BLOB")
	private byte[] contents;
	
	@Basic()
	@Column(name = "DELETED_FLAG", length = 1)
	private Boolean deletedFlag;
	

	public StandardProtocol() {  }
	
	
	private StandardProtocol(String protocolId, Assay assay,  String sampleType, String loadedBy, Calendar startDate, Calendar retiredDate,
	 byte[] contents, String fileName, String fileType)
		{
		this.protocolId = protocolId;
		this.assay = assay;
		this.sampleType = sampleType;
		this.loadedBy = loadedBy;
		this.startDate = startDate;
		this.retiredDate = retiredDate;
		this.contents = contents;
		this.fileName = fileName;
		this.fileType = fileType;
		}
	

	public String getProtocolId()
		{
		return protocolId;
		}

	public Assay getAssay()
		{
		return assay;
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

	public String getFileName()
		{
		return fileName;
		}

	public String getFileType()
		{
		return fileType;
		}

	public byte[] getContents()
		{
		return contents;
		}

	public Boolean getDeletedFlag()
		{
		return deletedFlag;
		}

	public void setProtocolId(String protocolId)
		{
		this.protocolId = protocolId;
		}

	public void setAssayId(Assay assay)
		{
		this.assay = assay;
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

	public void setFileName(String fileName)
		{
		this.fileName = fileName;
		}

	public void setFileType(String fileType)
		{
		this.fileType = fileType;
		}

	public void setContents(byte[] contents)
		{
		this.contents = contents;
		}

	public void setDeletedFlag(Boolean deletedFlag)
		{
		this.deletedFlag = deletedFlag;
		} 
	
	public Boolean isDeleted()
		{
		if (deletedFlag == null) 
			return false;
		
		if (deletedFlag)
			return true;
		
		return false;
		}

	public void setDeleted()
		{
		this.deletedFlag = true;
		}
	
	}

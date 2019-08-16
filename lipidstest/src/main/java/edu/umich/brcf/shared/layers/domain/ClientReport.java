package edu.umich.brcf.shared.layers.domain;

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

import edu.umich.brcf.shared.util.utilpackages.DateUtils;



@Entity
@Table(name = "METLIMS_LIBRARY.ANALYSIS_REPORT")
public class ClientReport implements IClusterable
	{
	public static ClientReport instance(Experiment exp, String loadedBy, byte[] contents, String fileName, String fileType, String assayId)
		{
		return new ClientReport(null, exp, loadedBy, Calendar.getInstance(), contents, fileName, fileType, assayId);
		}

	@Id()
	@SequenceGenerator(name = "idGenerator", sequenceName = "METLIMS_LIBRARY.REPORT_ID_SEQ")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idGenerator")
	@Column(name = "REPORT_ID", unique = true, nullable = false)
	private Long reportId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EXPERIMENT_ID", referencedColumnName = "EXP_ID", nullable = false)
	private Experiment exp;

	@Basic()
	@Column(name = "LOADED_BY", nullable = false, columnDefinition = "CHAR(6)")
	private String loadedBy;

	@Basic()
	@Column(name = "DATE_CREATED", nullable = false, columnDefinition = "DATE")
	private Calendar dateCreated;

	@Basic()
	@Column(name = "FILE_NAME", nullable = true, columnDefinition = "VARCHAR2(300)")
	private String fileName;

	@Basic()
	@Column(name = "FILE_TYPE", nullable = true, columnDefinition = "VARCHAR2(100)")
	private String fileType;

	@Lob()
	@Column(name = "REPORT_FILE", nullable = false, columnDefinition = "BLOB")
	private byte[] contents;

	@Basic
	@Column(name = "ASSAY_ID", nullable = false, columnDefinition = "CHAR(4)")
	private String assayId;
	
	// Issue 245
	@Basic()
	@Column(name = "DELETED", length = 1)
	private Boolean deletedFlag;

	private ClientReport(Long reportId, Experiment exp, String loadedBy, Calendar dateCreated, byte[] contents, 
		String fileName, String fileType, String assayId)
		{
		this.reportId = reportId;
		this.exp = exp;
		this.loadedBy = loadedBy;
		this.dateCreated = dateCreated;
		this.contents = contents;
		this.fileName = fileName;
		this.fileType = fileType;
		this.assayId = assayId;
		}

	public ClientReport() {  } 

	
	public Long getReportId()
		{
		return reportId;
		}

	public Experiment getExp()
		{
		return exp;
		}

	public String getLoadedBy()
		{
		return loadedBy;
		}

	public Calendar getDateCreated()
		{
		return dateCreated;
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

	public String getAssayId()
		{
		return assayId;
		}

	public void setAssayId(String assayId)
		{
		this.assayId = assayId;
		}

	public String getDateCreatedStr()
		{
		return DateUtils.dateStrFromCalendar("MM-dd-yy", getDateCreated());
		}
	
	// Issue 245
	public Boolean isDeleted()
	    {
	    if (deletedFlag == null) 
		    return false;	
	    if (deletedFlag)
		    return true;	
	    return false;
	    }

	// Issue 245
    public void setDeleted()
	    {
	    this.deletedFlag = true;
	    }
	
	}

////////////////////////////////////////////////////
// ProtocolSheet.java
// Written by Jan Wigginton, Oct 28, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import edu.umich.brcf.shared.layers.dto.ProtocolSheetDTO;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;


/*
CREATE TABLE METLIMS.PROTOCOL_SHEET
(
SHEET_ID CHAR(8),
PROTOCOL_DOC_ID CHAR(4),
ASSAY_ID CHAR(4), 
EXP_ID CHAR(7),
TITLE VARCHAR2(200),
RECORDED_DATE DATE,
RECORDED_BY CHAR(6	),
N_CELL_PLATES NUMBER,
N_SAMPLES NUMBER, 
EXTRACT_VOLUME NUMBER, 
EXTRACT_VOL_UNITS VARCHAR2(26),
LOC_ID CHAR(6),
NOTES VARCHAR(2000),
CONSTRAINT  protocol_sheet_pk PRIMARY KEY (SHEET_ID)
)
*/

/*
 * CREATE TABLE METLIMS.PROTOCOL_SHEET
(
SHEET_ID CHAR(8),
PROTOCOL_DOC_ID CHAR(4),
ASSAY_ID CHAR(4), 
EXP_ID CHAR(7),
TITLE VARCHAR2(200),
RECORDED_DATE DATE,
RECORDED_BY CHAR(6	),
N_CELL_PLATES NUMBER,
N_SAMPLES NUMBER, 
EXTRACT_VOLUME NUMBER, 
EXTRACT_VOL_UNITS VARCHAR2(26),
LOC_ID CHAR(6),
NOTES VARCHAR2(2000),
SAMPLE_TYPE VARCHAR2(30)
CONSTRAINT  protocol_sheet_pk PRIMARY KEY (SHEET_ID)
)
 * 
 * */

@Entity
@Table(name = "PROTOCOL_SHEET")
public class ProtocolSheet implements Serializable
	{
	public static ProtocolSheet instance(String id, String assayId, String expId, Calendar recordedDate, String recordedBy,
			Integer nCellPlates, String locationId, String notes, Integer nSamples,  BigDecimal extractVol, String extractVolUnits, 
			String sampleType, String protocolDocId)
		{
		return new ProtocolSheet(id, assayId, expId, recordedDate, recordedBy, nCellPlates, locationId, notes, nSamples, 
				extractVol, extractVolUnits, sampleType, protocolDocId);
		}
	
	public static ProtocolSheet instance(String assayId, String expId, Calendar recordedDate, String recordedBy,
			Integer nCellPlates, String locationId, String notes, Integer nSamples,  BigDecimal extractVol, String extractVolUnits,
			String sampleType, String protocolDocId)
		{
		return new ProtocolSheet(null, assayId, expId, recordedDate, recordedBy, nCellPlates, locationId, notes, nSamples, extractVol, 
				extractVolUnits, sampleType, protocolDocId);
		}
	
	public static String idFormat = "(P)(H))\\d{6}";

	@Id
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
			@Parameter(name = "idClass", value = "ProtocolSheet"),
			@Parameter(name = "width", value = "8") })
	@Column(name = "SHEET_ID", unique = true, nullable = false, length = 8, columnDefinition = "CHAR(8)")
	private String id;
	
	@Basic()
	@Column(name = "PROTOCOL_DOC_ID", nullable = false, columnDefinition = "CHAR(6)")
	private String protocolDocumentId;
	
	
	@Basic()
	@Column(name = "ASSAY_ID", nullable = false, columnDefinition = "CHAR(4)")
	private String assayId;
	
	@Basic()
	@Column(name = "EXP_ID", nullable = false, columnDefinition = "CHAR(7)")
	private String experimentId;
	
	@Basic()
	@Column(name = "SAMPLE_TYPE", nullable = false, columnDefinition = "VARCHAR2(30)")
	private String sampleType;
	
	@Basic()
	@Column(name = "TITLE", nullable = true, columnDefinition = "VARCHAR2(200)" )
	private String sheetTitle;
	
	@Basic()
	@Column(name = "RECORDED_DATE", nullable = true, columnDefinition = "DATE")
	private Calendar recordedDate;
	
	@Basic()
	@Column(name = "RECORDED_BY", unique = false, nullable = false, length = 6, columnDefinition = "CHAR(6)")
	private String recordedBy;
	
	@Basic()
	@Column(name = "N_CELL_PLATES", columnDefinition = "NUMBER")
	private Integer nCellPlates;
	
	@Basic()
	@Column(name = "N_SAMPLES", columnDefinition = "NUMBER")
	private Integer nSamples;
	
	@Basic()
	@Column(name = "EXTRACT_VOLUME", nullable = true, columnDefinition = "NUMBER")
	private BigDecimal  extractVolume;
	
	@Basic()
	@Column(name = "EXTRACT_VOL_UNITS", nullable = true, columnDefinition = "VARCHAR2(26)")
	private  String extractVolUnits;
	
	@Basic()
	@Column(name = "LOC_ID", nullable = true, columnDefinition = "CHAR(6)")
	private String locationId;
	
	@Basic()
	@Column(name = "NOTES", nullable = true, columnDefinition = "VARCHAR2(2000)" )
	private String notes;
	
	
	@Basic()
	@Column(name = "DELETED", nullable = true, columnDefinition = "CHAR(1)")
	private Boolean deleted;
	
	public ProtocolSheet() {  } 
	
	
	private ProtocolSheet(String id, String assayId, String expId, Calendar recordedDate, String recordedBy, Integer nCellPlates, 
		String locationId, String notes, Integer nSamples, BigDecimal extractVol, String extractVolUnits, String sampleType,
		String protocolDocId)
		{
		this.sheetTitle = "";
		this.id  = id;
		this.assayId = assayId;
		this.experimentId = expId;
		this.recordedDate = recordedDate;
		this.recordedBy = recordedBy;
		this.nCellPlates = nCellPlates;
		this.locationId = locationId;
		this.notes = notes;
		this.nSamples = nSamples;
		this.extractVolume = extractVol;
		this.extractVolUnits = extractVolUnits;
		this.sampleType = sampleType;
		this.protocolDocumentId = protocolDocId;
		}
	
	
	public void update(ProtocolSheetDTO dto)
		{
		this.id  = dto.getId();
		this.assayId = dto.getAssayId();
		this.experimentId = dto.getExperimentId();
		this.recordedDate = dto.getRecordedDate(); 
		this.recordedBy = dto.getRecordedBy();
		this.nCellPlates = dto.getnCellPlates();
		this.locationId = dto.getLocationId();
		this.notes = dto.getNotes();
		this.nSamples = dto.getnSamples();
		this.extractVolume = dto.getExtractVolume();
		this.extractVolUnits = dto.getExtractVolUnits();
		this.sampleType = dto.getSampleType();
		this.protocolDocumentId = dto.getProtocolDocumentId();
		}

	
	public String getProtocolDocumentId()
		{
		return protocolDocumentId;
		}

	public void setProtocolDocumentId(String protocolDocumentId)
		{
		this.protocolDocumentId = protocolDocumentId;
		}
	

	public String getId()
		{
		return id;
		}


	public void setId(String id)
		{
		this.id = id;
		}


	public String getAssayId()
		{
		return assayId;
		}
	

	public void setAssayId(String assayId)
		{
		this.assayId = assayId;
		}


	public String getSheetTitle()
		{
		return sheetTitle;
		}

	public void setSheetTitle(String sheetTitle)
		{
		this.sheetTitle = sheetTitle;
		}

	public String getExperimentId()
		{
		return experimentId;
		}


	public void setExperimentId(String experimentId)
		{
		this.experimentId = experimentId;
		}


	public Calendar getRecordedDate()
		{
		return recordedDate;
		}


	public void setRecordedDate(Calendar recordedDate)
		{
		this.recordedDate = recordedDate;
		}


	public String getRecordedBy()
		{
		return recordedBy;
		}


	public void setRecordedBy(String recordedBy)
		{
		this.recordedBy = recordedBy;
		}


	public Integer getnCellPlates()
		{
		return nCellPlates;
		}


	public void setnCellPlates(Integer nCellPlates)
		{
		this.nCellPlates = nCellPlates;
		}


	public String getLocationId()
		{
		return locationId;
		}


	public void setLocationId(String locationId)
		{
		this.locationId = locationId;
		}


	public BigDecimal getExtractVolume()
		{
		return extractVolume;
		}


	public void setExtractVolume(BigDecimal extractVolume)
		{
		this.extractVolume = extractVolume;
		}


	public String getExtractVolUnits()
		{
		return extractVolUnits;
		}


	public void setExtractVolUnits(String extractVolUnits)
		{
		this.extractVolUnits = extractVolUnits;
		}


	public String getNotes()
		{
		return notes;
		}


	public void setNotes(String notes)
		{
		this.notes = notes;
		}

	
	public Integer getnSamples()
		{
		return nSamples;
		}

	public void setnSamples(Integer nSamples)
		{
		this.nSamples = nSamples;
		}

	public String getSampleType()
		{
		return sampleType;
		}

	public void setSampleType(String sampleType)
		{
		this.sampleType = sampleType;
		}

	public String getDateCreatedStr()
		{
		return DateUtils.dateStrFromCalendar("MM-dd-yy", this.getRecordedDate());
		}
	
	public Boolean isDeleted()
		{
		if (deleted == null) 
			return false;
		
		if (deleted)
			return true;
		
		return false;
		}

	public void setDeleted()
		{
		this.deleted = true;
		}
	}
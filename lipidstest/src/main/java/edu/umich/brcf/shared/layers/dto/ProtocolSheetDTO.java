////////////////////////////////////////////////////
// ProtocolSheetDTO.java
// Written by Jan Wigginton, Oct 28, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.layers.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

import edu.umich.brcf.shared.layers.domain.ProtocolSheet;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;




public class ProtocolSheetDTO implements Serializable
	{
	public static ProtocolSheetDTO instance(String id, String protocolDocumentId, String assayId, String experimentId, Calendar recordedDate, String recordedBy, Integer nCellPlates,
			String locationId,  String notes, Integer nSamples, BigDecimal extractVol, String extractVolUnits, String sampleType)
		{
		return new ProtocolSheetDTO(id, protocolDocumentId, assayId, experimentId, recordedDate, recordedBy, nCellPlates,locationId,  notes, nSamples, extractVol, extractVolUnits,
				sampleType);
		}

	public static ProtocolSheetDTO instance(ProtocolSheet sheet)
		{
		return new ProtocolSheetDTO(sheet.getId(), sheet.getProtocolDocumentId(), sheet.getAssayId(), sheet.getExperimentId(), sheet.getRecordedDate(), sheet.getRecordedBy(), 
				sheet.getnCellPlates(),  sheet.getLocationId(),sheet.getNotes(),  sheet.getnSamples(),  sheet.getExtractVolume(), sheet.getExtractVolUnits(), sheet.getSampleType()
				);
		}

	
	private String id;
	private String protocolDocumentId;
	private String assayId;
	private String experimentId;
	private String sampleType;
	private String sheetTitle;
	private Calendar recordedDate;
	private String recordedBy;
	private Integer nCellPlates;
	private Integer nSamples;
	private String locationId;
	private BigDecimal  extractVolume;
	private String extractVolUnits;
	private String notes;
	private Character deleted;
	

	public ProtocolSheetDTO() {   }
	
	
	private ProtocolSheetDTO(String id, String protocolDocId, String assayId, String experimentId, Calendar recordedDate, String recordedBy, Integer nCellPlates,
			String locationId,  String notes, Integer nSamples, BigDecimal extractVol, String extractVolUnits,
			String sampleType)
		{
		this.id = id;
		this.protocolDocumentId = protocolDocId;
		this.assayId = assayId;
		this.experimentId = experimentId;
		this.recordedDate = recordedDate;
		this.recordedBy = recordedBy;
		this.nCellPlates = nCellPlates;
		this.locationId= locationId;
		this.notes = notes;
		this.nSamples = nSamples;
		//this.protocolVersionId = protocolVersionId;
		
		this.extractVolume = extractVol;
		this.extractVolUnits = extractVolUnits; 
		this.sampleType = sampleType;
		}
	
	
	public String getId()
		{
		return id;
		}


	public void setId(String id)
		{
		this.id = id;
		}
	
	public String getProtocolDocumentId()
		{
		return protocolDocumentId;
		}

	public void setProtocolDocumentId(String protocolDocumentId)
		{
		this.protocolDocumentId = protocolDocumentId;
		}


	public String getAssayId()
		{
		return assayId;
		}


	public void setAssayId(String assayId)
		{
		this.assayId = assayId;
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


	public String getNotes()
		{
		return notes;
		}


	public void setNotes(String notes)
		{
		this.notes = notes;
		}

	public String getSheetTitle()
		{
		return sheetTitle;
		}

	public void setSheetTitle(String sheetTitle)
		{
		this.sheetTitle = sheetTitle;
		}

	public Integer getnSamples()
		{
		return nSamples;
		}

	public void setnSamples(Integer nSamples)
		{
		this.nSamples = nSamples;
		}
	
	
	public BigDecimal getExtractVolume()
		{
		return extractVolume;
		}

	public String getExtractVolUnits()
		{
		return extractVolUnits;
		}

	public void setExtractVolume(BigDecimal extractVolume)
		{
		this.extractVolume = extractVolume;
		}

	public void setExtractVolUnits(String extractVolUnits)
		{
		this.extractVolUnits = extractVolUnits;
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

	public Character getDeleted()
		{
		return deleted;
		}

	public void setDeleted(Character deleted)
		{
		this.deleted = deleted;
		}	
	
	
	}

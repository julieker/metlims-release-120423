///////////////////////////////////////
// SampleLocationDTO.java
// Written by Jan Wigginton, May 2015
///////////////////////////////////////

package edu.umich.brcf.shared.layers.dto;


import java.util.Calendar;

import edu.umich.brcf.shared.layers.domain.SampleLocation;

public class SampleLocationDTO 
	{
	public static SampleLocationDTO instance(String sampleId, String oldLocationId, String locationId, Calendar updateDate, String updatedBy)
		{
		return new SampleLocationDTO(null, sampleId, oldLocationId, locationId, updateDate, updatedBy);
		}
	
	public static SampleLocationDTO instance(SampleLocation sampleLocation)
		{
		return new SampleLocationDTO(sampleLocation.getSampleLocationId(), sampleLocation.getSampleId(),
				sampleLocation.getOldLocationId(), sampleLocation.getLocationId(), sampleLocation.getUpdateDate(), sampleLocation.getUpdatedBy());
		}
	
	
	private String sampleLocationId;
	private String sampleId;
	private String oldLocationId;
	private String locationId;
	private Calendar updateDate;
	private String updatedBy;
	
	
	private SampleLocationDTO(String sampleLocationId, String sampleId, String oldLocationId, String locationId, 
			Calendar updateDate, String updatedBy)
		{
		this.sampleLocationId = sampleLocationId;
		this.sampleId = sampleId;
		this.oldLocationId = oldLocationId;
		this.locationId = locationId;
		this.updateDate = updateDate;
		this.updatedBy = updatedBy;
		}
	
	public SampleLocationDTO() { }
	
	
	public String getOldLocationId() { return oldLocationId; }
	public void setOldLocationId(String oldLocationId) { this.oldLocationId = oldLocationId; }
	
	public String getSampleLocationId() { return sampleLocationId; }
	public void setSampleLocationId(String sampleLocationId) { this.sampleLocationId = sampleLocationId; }
	
	public String getSampleId() { return sampleId; }
	public void setSampleId(String sampleId) { this.sampleId = sampleId; }
	
	public String getLocationId() { return locationId; }
	public void setLocationId(String locationId) { this.locationId = locationId; }
	
	public Calendar getUpdateDate() { return updateDate; }
	public void setUpdateDate(Calendar updateDate) { this.updateDate = updateDate; }
	
	public String getUpdatedBy() { return updatedBy; }
	public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
	}

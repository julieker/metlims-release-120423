////////////////////////////////////////////////////////////////////
// SampleLocation.java
// Written by Jan Wigginton June 2015
////////////////////////////////////////////////////////////////////

package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/*
CREATE TABLE METLIMS.SAMPLE_LOCATION_HISTORY
(
SAMPLE_LOCATION_HISTORY_ID CHAR(9),
SAMPLE_ID CHAR(9),
LOCATION_ID CHAR(6),
UPDATE_DATE DATE,
UPDATED_BY CHAR(6),
OLD_LOCATION_ID CHAR(6), 
CONSTRAINT  sample_location_history_pk PRIMARY KEY (SAMPLE_LOCATION_HISTORY_ID)
)
*/

@Entity()
@Table(name = "SAMPLE_LOCATION_HISTORY")
public class SampleLocation implements Serializable
	{
	public static SampleLocation instance(String sampleId, String oldLocationId, String locationId, Calendar updateDate, String updatedBy){
		return new SampleLocation(null, sampleId, oldLocationId, locationId, updateDate, updatedBy);		
	}

	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
			@Parameter(name = "idClass", value = "SampleLocation"), @Parameter(name = "width", value = "9") })
	@Column(name = "SAMPLE_LOCATION_HISTORY_ID", unique = true, nullable = false, length = 9, columnDefinition = "CHAR(9)")
	private String sampleLocationId;

	@Basic()
	@Column(name = "SAMPLE_ID", nullable = false, columnDefinition = "CHAR(9)")
	private String sampleId;
	//Protien
	
	@Basic()
	@Column(name = "OLD_LOCATION_ID", nullable = true, columnDefinition = "CHAR(6)")
	private String oldLocationId;
	
	@Basic()
	@Column(name = "LOCATION_ID", nullable = false, columnDefinition = "CHAR(6)")
	private String locationId;
	
	@Basic
	@Column(name = "UPDATE_DATE", nullable = false, columnDefinition = "DATE")
	private Calendar updateDate;
	
	@Basic
	@Column(name = "UPDATED_BY", nullable = false, columnDefinition = "CHAR(8)")
	private String updatedBy;
	
	
	public SampleLocation(String sampleLocationId, String sampleId, String oldLocationId, String locationId, Calendar updateDate, String updatedBy) 
		{
		this.sampleLocationId = sampleLocationId;
		this.sampleId = sampleId;
		this.oldLocationId = oldLocationId;
		this.locationId = locationId;
		this.updateDate = updateDate;
		this.updatedBy = updatedBy;
		}

	public SampleLocation()
		{
		}
	
	public String getOldLocationId() {
		return oldLocationId;
	}

	public void setOldLocationId(String oldLocationId) {
		this.oldLocationId = oldLocationId;
	}

	public String getSampleLocationId() {
		return sampleLocationId;
	}


	public void setSampleLocationId(String sampleLocationId) {
		this.sampleLocationId = sampleLocationId;
	}


	public String getSampleId() {
		return sampleId;
	}


	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}


	public String getLocationId() {
		return locationId;
	}


	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}


	public Calendar getUpdateDate() {
		return updateDate;
	}


	public void setUpdateDate(Calendar updateDate) {
		this.updateDate = updateDate;
	}


	public String getUpdatedBy() {
		return updatedBy;
	}


	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	
	
	}
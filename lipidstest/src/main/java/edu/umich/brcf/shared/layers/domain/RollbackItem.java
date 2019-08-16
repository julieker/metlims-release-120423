////////////////////////////////////////////////////
// RollbackItem.java
// Written by Jan Wigginton, May 31, 2017
////////////////////////////////////////////////////

package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import edu.umich.brcf.shared.layers.domain.RollbackItem.RollbackItemPK;
import edu.umich.brcf.shared.layers.dto.RollbackItemDTO;



/*
CREATE TABLE ROLLBACK_SAMPLES
 (
 SAMPLE_ID CHAR(9),
 SUBJECT_ID CHAR(9),
 RESEARCHER_SAMPLE_ID VARCHAR2(120), 
 RESEARCHER_SUBJECT_ID VARCHAR2(100),
ORIGINAL_CHECKIN_DATE DATE,
LAST_ROLLBACK_DATE  DATE ,
 IS_REPLACED CHAR(1),
 NOTE VARCHAR2(200)
 )
*/

@Entity
@Table(name = "ROLLBACK_SAMPLES")
public class RollbackItem implements Serializable
	{
	public static RollbackItem instance(RollbackItemDTO dto)
		{
		return RollbackItem.instance(dto.getExpId(), dto.getSampleId(), dto.getResearcherSampleId(), dto.getSubjectId(), dto.getResearcherSubjectId(),
				dto.getOriginalCheckinDate(), dto.getLastRollbackDate(), dto.getIsReplaced(), dto.getNote());
		}
	
	public static RollbackItem instance(String expId, String sampleId, String subjectId, String researcherSampleId, String researcherSubjectId,
	 Calendar originalCheckinDate, Calendar lastCheckinDate, Character isReplaced, String comment)
		{
		return new RollbackItem(expId, sampleId, subjectId, researcherSampleId, researcherSubjectId, originalCheckinDate, lastCheckinDate,
		 isReplaced, comment);
		}
	
	
	
	/////// JAK fix issue 177 add embeddable to insert into and update rollback_samples properly
	@Embeddable
	public static class RollbackItemPK implements Serializable 
		{
		public static RollbackItemPK instance(String sampleId, String expId) 
			{
			return new RollbackItemPK(sampleId, expId);
			}
		
		@Column(name = "SAMPLE_ID", length = 20)
		private String sampleId;

		@Column(name = "EXP_ID")
		private String expId;
		
		private RollbackItemPK (String sampleId, String expId) 
			{
			this.sampleId = sampleId;
			this.expId = expId;		
			}
		public RollbackItemPK() { }
		public boolean equals(Object o) 
		{
		if (o != null && o instanceof RollbackItemPK) 
			{
			RollbackItemPK that = (RollbackItemPK) o;
			return this.sampleId.equals(that.sampleId) && this.expId.equals(that.expId);
			}
		return false;
		}

	public int hashCode() { return sampleId.hashCode() + expId.hashCode(); }
		}
	//////
	
	@EmbeddedId
	protected RollbackItemPK id;
	
	
	@Column(name = "SAMPLE_ID", insertable = false, updatable = false)
		private String sampleId;
	
	@Column(name = "EXP_ID", insertable = false, updatable = false)
	private String expId;
	
	
	
	
	@Basic()
	@Column(name = "SUBJECT_ID", nullable = false, columnDefinition = "CHAR(9)")
	private String subjectId;
	
	
	@Basic()
	@Column(name = "RESEARCHER_SAMPLE_ID", nullable = false, columnDefinition = "VARCHAR2(100)")
	private String researcherSampleId;
	
	
	@Basic()
	@Column(name = "RESEARCHER_SUBJECT_ID", nullable = false, columnDefinition = "VARCHAR2(100)")
	private String researcherSubjectId;
	
	@Basic
	@Column(name = "ORIGINAL_CHECKIN_DATE", nullable = false, columnDefinition = "DATE")
	private Calendar originalCheckinDate;
	
	@Basic
	@Column(name = "LAST_ROLLBACK_DATE", nullable = false, columnDefinition = "DATE")
	private Calendar lastRollbackDate;
	
	@Basic()
	@Column(name = "IS_REPLACED", length = 1, nullable = true)
	private Character isReplaced;
	
	@Basic()
	@Column(name = "NOTE", nullable = true, columnDefinition = "VARCHAR2(200)")
	private String note;
	
	
	public RollbackItem() { } 
	

	public RollbackItem(String expId, String sampleId, String researcherSampleId, String subjectId, String researcherSubjectId, Calendar originalCheckinDate, 
			Calendar lastCheckinDate, Character isReplaced, String note)
		{
		this.id = RollbackItemPK.instance(sampleId, expId);
		this.expId = expId;
		this.sampleId = sampleId;
		this.researcherSampleId = researcherSampleId;
		this.subjectId = subjectId;
		this.researcherSubjectId = researcherSubjectId;
		this.originalCheckinDate = originalCheckinDate;
		this.lastRollbackDate = lastCheckinDate;
		this.isReplaced = isReplaced;
		this.note = note;
		}
	
	
	public String getExpId()
		{
		return expId;
		}

	public String getNote()
		{
		return note;
		}

	public void setExpId(String expId)
		{
		this.expId = expId;
		}

	public String getSampleId()
		{
		return sampleId;
		}


	public String getSubjectId()
		{
		return subjectId;
		}


	public String getResearcherSampleId()
		{
		return researcherSampleId;
		}


	public String getResearcherSubjectId()
		{
		return researcherSubjectId;
		}


	public Calendar getOriginalCheckinDate()
		{
		return originalCheckinDate;
		}


	public Calendar getLastRollbackDate()
		{
		return lastRollbackDate;
		}


	public Character getIsReplaced()
		{
		return isReplaced;
		}


	public String getNotet()
		{
		return note;
		}


	public void setSampleId(String sampleId)
		{
		this.sampleId = sampleId;
		}


	public void setSubjectId(String subjectId)
		{
		this.subjectId = subjectId;
		}


	public void setResearcherSampleId(String researcherSampleId)
		{
		this.researcherSampleId = researcherSampleId;
		}


	public void setResearcherSubjectId(String researcherSubjectId)
		{
		this.researcherSubjectId = researcherSubjectId;
		}


	public void setOriginalCheckinDate(Calendar originalCheckinDate)
		{
		this.originalCheckinDate = originalCheckinDate;
		}


	public void setLastRollbackDate(Calendar lastRollbackDate)
		{
		this.lastRollbackDate = lastRollbackDate;
		}


	public void setIsReplaced(Character isReplaced)
		{
		this.isReplaced = isReplaced;
		}


	public void setNote(String note)
		{
		this.note = note;
		}
	
	
	}
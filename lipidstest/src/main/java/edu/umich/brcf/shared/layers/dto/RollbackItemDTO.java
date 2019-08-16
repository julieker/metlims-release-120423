////////////////////////////////////////////////////
// RollbackItemDTO.java
// Written by Jan Wigginton, Jun 5, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.layers.dto;

import java.io.Serializable;
import java.util.Calendar;

public class RollbackItemDTO implements Serializable
	{
	private String expId; 
	private String sampleId;
	private String subjectId;
	private String researcherSampleId;
	private String researcherSubjectId;
	private Calendar originalCheckinDate;
	private Calendar lastRollbackDate;
	private Character isReplaced;
	private String note;
	

	public RollbackItemDTO() { }
	
	
	public RollbackItemDTO(String expId, String sampleId, String subjectId, String researcherSampleId,  String researcherSubjectId,  
	 Calendar originalCheckinDate, Calendar lastRollbackDate, Character isReplaced, String note)
			{
			this.expId = expId;
			this.sampleId = sampleId;
			this.subjectId = subjectId;
			this.researcherSampleId = researcherSampleId;
			this.researcherSubjectId = researcherSubjectId;
			this.originalCheckinDate = originalCheckinDate;
			this.lastRollbackDate = lastRollbackDate;
			this.isReplaced = isReplaced;
			this.note = note;
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

	public String getNote()
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


	public String getExpId()
		{
		return expId;
		}

	public void setExpId(String expId)
		{
		this.expId = expId;
		}
	}


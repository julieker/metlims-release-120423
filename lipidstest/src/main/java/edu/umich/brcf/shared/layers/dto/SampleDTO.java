package edu.umich.brcf.shared.layers.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.util.interfaces.ICheckinSampleItem;
import edu.umich.brcf.shared.util.interfaces.ISampleItem;


public class SampleDTO implements Serializable, ICheckinSampleItem, ISampleItem
	{
	public static SampleDTO instance(Sample sample)
		{
		String groupID = (sample.getGroup() == null) ? "" : sample.getGroup()
				.getGroupID();
		String parentID = (sample.getParent() == null) ? "" : sample
				.getParent().getSampleID();

		String expId = sample.getExp() == null ? "" : sample.getExp().getExpID();
		
		SampleDTO dt = new SampleDTO(sample.getSampleID(),
				sample.getSampleName(), expId, sample.getUserDescription(), sample
						.getGenusOrSpecies().getGsID(), sample.getLocID(),
				sample.getUserDefSampleType(), sample.getVolume(),
				sample.getVolUnits(), sample.getStatus().getId(),
				sample.getSampleControlType(), sample.getDateCreated(), sample
						.getSampleType().getSampleTypeId(), groupID, parentID, sample.getUserDefGOS());

		dt.setSubjectId(sample.getSubject() == null ? "" : sample.getSubject()
				.getSubjectId());

		return dt;
		}

	private String sampleID;
	private String sampleName;
	private String subjectId, subjectName;
	private String expID;
	private String userDescription;
	private Long genusOrSpeciesID;
	private String locID;
	private String UserDefSampleType;
	private String UserDefGOS;
	private BigDecimal volume, currVolume;
	private String volUnits;
	private Character status;
	private boolean sampleControlType;
	private Calendar dateCreated;
	private String sampleTypeId;
	private String groupID;
	private String parentID;

	public SampleDTO(String sampleID, String sampleName, String expID,
			String userDescription, Long genusOrSpeciesID, String locID,
			String UserDefSampleType, BigDecimal volume, String volUnits,
			Character status, Long sampleControlType, Calendar dateCreated,
			String sampleTypeId, String groupID, String parentID)
		{
		this.sampleID = sampleID;
		this.sampleName = sampleName;
		this.expID = expID;
		this.userDescription = userDescription;
		this.genusOrSpeciesID = genusOrSpeciesID;
		this.locID = locID;
		this.UserDefSampleType = UserDefSampleType;
		this.volume = volume;
		this.volUnits = volUnits;
		this.status = status;
		this.sampleControlType = (sampleControlType != null && sampleControlType
				.intValue() == 1) ? true : false;
		this.dateCreated = dateCreated;
		this.sampleTypeId = sampleTypeId;
		this.groupID = groupID;
		this.parentID = parentID;
		this.currVolume = volume;
		}

	public SampleDTO(String sampleID, String sampleName, String expID,
			String userDescription, Long genusOrSpeciesID, String locID,
			String UserDefSampleType, BigDecimal volume, String volUnits,
			Character status, Long sampleControlType, Calendar dateCreated,
			String sampleTypeId, String groupID, String parentID, String UserDefGOS)
		{
		this.sampleID = sampleID;
		this.sampleName = sampleName;
		this.expID = expID;
		this.userDescription = userDescription;
		this.genusOrSpeciesID = genusOrSpeciesID;
		this.locID = locID;
		this.UserDefSampleType = UserDefSampleType;
		this.volume = volume;
		this.volUnits = volUnits;
		this.status = status;
		this.sampleControlType = (sampleControlType != null && sampleControlType
				.intValue() == 1) ? true : false;
		this.dateCreated = dateCreated;
		this.sampleTypeId = sampleTypeId;
		this.groupID = groupID;
		this.parentID = parentID;
		this.currVolume = volume;
		this.UserDefGOS = UserDefGOS;
		
		}

	public SampleDTO(String sampleID, String sampleName, String expID,
			String userDescription, Long genusOrSpeciesID, String locID,
			String UserDefSampleType, BigDecimal volume, String volUnits,
			Character status, Long sampleControlType, Calendar dateCreated,
			String sampleTypeId, String groupID, String parentID,
			BigDecimal currVolume)
		{
		this.sampleID = sampleID;
		this.sampleName = sampleName;
		this.expID = expID;
		this.userDescription = userDescription;
		this.genusOrSpeciesID = genusOrSpeciesID;
		this.locID = locID;
		this.UserDefSampleType = UserDefSampleType;
		this.volume = volume;
		this.volUnits = volUnits;
		this.status = status;
		this.sampleControlType = (sampleControlType != null && sampleControlType
				.intValue() == 1) ? true : false;
		this.dateCreated = dateCreated;
		this.sampleTypeId = sampleTypeId;
		this.groupID = groupID;
		this.parentID = parentID;
		this.currVolume = currVolume;
		}

	public SampleDTO()
		{
		}

	// public static SampleDTO instance(String sampleName, String expID, String
	// userDescription, Long genusOrSpeciesID,
	// String locID, String UserDefSampleType, BigDecimal volume, String
	// volUnits, Character status,
	// Long sampleControlType, Calendar dateCreated, String sampleTypeId) {
	// return new SampleDTO(null, sampleName, expID, userDescription,
	// genusOrSpeciesID, locID, UserDefSampleType,
	// volume, volUnits, status, sampleControlType, dateCreated, sampleTypeId);
	// }

	public String toString()
		{
		StringBuilder sb = new StringBuilder();
		sb.append("Id is " + getSampleID());
		sb.append("Sample Name is " + getSampleName());
		sb.append("Subject id is " + getSubjectId());
		sb.append("User defined sample type is " + getUserDefSampleType());
		sb.append("User defined GOS " + getUserDefGOS());
		sb.append("Vol units is " + getVolUnits());
		sb.append("Sample type id is " + getSampleTypeId());
		sb.append("Location id is " + getLocID());

		return sb.toString();
		}

	public String getSampleID()
		{
		return sampleID;
		}

	public String getSampleName()
		{
		return sampleName;
		}

	public String getExpID()
		{
		return expID;
		}

	public String getSubjectId()
		{
		return subjectId;
		}

	public String getUserDefGOS()
		{
		return UserDefGOS;
		}

	public String getUserDescription()
		{
		return userDescription;
		}

	public Long getGenusOrSpeciesID()
		{
		return genusOrSpeciesID;
		}

	public String getLocID()
		{
		return locID;
		}

	public String getUserDefSampleType()
		{
		return UserDefSampleType;
		}

	public String getParentID()
		{
		return parentID;
		}

	public void setParentID(String parentID)
		{
		this.parentID = parentID;
		}

	public void setSampleID(String sampleID)
		{
		this.sampleID = sampleID;
		}

	public void setSampleName(String sampleName)
		{
		this.sampleName = sampleName;
		}

	public void setExpID(String expID)
		{
		this.expID = expID;
		}

	public void setSubjectId(String subjectId)
		{
		this.subjectId = subjectId;
		}

	public void setUserDescription(String userDescription)
		{
		this.userDescription = userDescription;
		}

	public void setGenusOrSpeciesID(Long genusOrSpeciesID)
		{
		this.genusOrSpeciesID = genusOrSpeciesID;
		}

	public void setGenusOrSpeciesID(String genusOrSpeciesID)
		{
		try
			{
			this.genusOrSpeciesID = Long.parseLong(genusOrSpeciesID);
			} catch (Exception e)
			{
			this.genusOrSpeciesID = null;
			}

		}

	public void setLocID(String locID)
		{
		this.locID = locID;
		}

	public void setUserDefSampleType(String userDefSampleType)
		{
		UserDefSampleType = userDefSampleType;
		}

	public void setUserDefGOS(String userDefGOS)
		{
		UserDefGOS = userDefGOS;
		}

	public BigDecimal getVolume()
		{
		return volume;
		}

	public void setVolume(BigDecimal volume)
		{
		this.volume = volume;
		}

	public String getVolUnits()
		{
		return volUnits;
		}

	public void setVolUnits(String volUnits)
		{
		this.volUnits = volUnits;
		}

	public Character getStatus()
		{
		return status;
		}

	public void setStatus(Character status)
		{
		this.status = status;
		}

	public boolean getSampleControlType()
		{
		return sampleControlType;
		}

	public void setSampleControlType(boolean sampleControlType)
		{
		this.sampleControlType = sampleControlType;
		}

	public Calendar getDateCreated()
		{
		return dateCreated;
		}

	public void setDateCreated(Calendar dateCreated)
		{
		this.dateCreated = dateCreated;
		}

	public String getSampleTypeId()
		{
		return sampleTypeId;
		}

	public void setSampleTypeId(String sampleTypeId)
		{
		this.sampleTypeId = sampleTypeId;
		}

	public String getGroupID()
		{
		return groupID;
		}

	public void setGroupID(String groupID)
		{
		this.groupID = groupID;
		}

	public BigDecimal getCurrVolume()
		{
		return currVolume;
		}

	public void setCurrVolume(BigDecimal currVolume)
		{
		this.currVolume = currVolume;

		}

	public String getSubjectName()
		{
		return subjectName;
		}

	public void setSubjectName(String subjectName)
		{
		this.subjectName = subjectName;
		}

	@Override
	public String getSampleId()
		{
		return this.sampleID;
		}

	@Override
	public Calendar getCheckinDate()
		{
		return this.getDateCreated();
		}

	@Override
	public String getResearcherSampleId()
		{
		return this.getSampleName();
		}

	@Override
	public String getResearcherSubjectId()
		{
		return this.getSubjectName();
		}


	@Override
	public void setCheckinDate(Calendar cal)
		{
		this.dateCreated = cal;
		}

	@Override
	public void setResearcherSampleId(String val)
		{
		this.sampleName = val;
		}

	@Override
	public void setResearcherSubjectId(String val)
		{
		this.subjectName = val;
		}

	@Override
	public void setSampleId(String val)
		{
		this.sampleID = val;
		}

	}

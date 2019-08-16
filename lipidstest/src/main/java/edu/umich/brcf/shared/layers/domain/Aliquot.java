package edu.umich.brcf.shared.layers.domain;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;


/*
CREATE TABLE TEST_USER.LABS
(
LAB_ID CHAR(4),
LAB_NAME VARCHAR2(50),
CONSTRAINT LABS_PK PRIMARY KEY (LAB_ID)
) */


/*
CREATE TABLE TEST_USER.ALIQUOT
(
ALIQUOT_ID CHAR(10), 
SEQ NUMBER, 
EXP_ID CHAR(7), 
CHECKIN_ID CHAR(7), 
DATE_TRANSFERRED DATE, 
VOLUME NUMBER(15,7),
VOL_UNITS VARCHAR2(26),
LOCATION_ID CHAR(6), 
STATUS CHAR(1),
PARENT_ID CHAR(10),
LAB_ID CHAR(4),
PREP_COMMENTS VARCHAR2(1000),
CONSTRAINT mc_aliquot_pk PRIMARY KEY (ALIQUOT_ID),
CONSTRAINT mc_aliquot_fk_checkinid FOREIGN KEY (CHECKIN_ID) REFERENCES TEST_USER.SAMPLE_CHECKIN (CHECKIN_ID)
)

CREATE TABLE TEST_USER.ALIQUOT_STATUS
(
);

* */
/////////////////////////////////////////////

//@Entity()
//@Table(name = "TEST_USER.ALIQUOT")

public class Aliquot implements Serializable 
	{
	public static String fullIdFormat = "(AL)\\d{8}";

	public static Aliquot instance( Integer sequence, Calendar dateTransferred, 
		BigDecimal volume, BigDecimal minVolume, String volUnits, Location location, Character status, String labId, String parentId, 
		String sampleId, String prepComments, String tubeType) 
		{
		return new Aliquot(null, sequence, dateTransferred, volume,  minVolume, volUnits,  location, status, 
		labId, parentId, sampleId, prepComments, tubeType, 1);
		}

	public static Aliquot instance( Integer sequence, Calendar dateTransferred, 
			BigDecimal volume, BigDecimal minVolume, String volUnits, Location location, Character status, String labId, String parentId, 
			String sampleId, String prepComments, String tubeType, Integer replicate) 
			{
			return new Aliquot(null, sequence,  dateTransferred, volume,  minVolume, volUnits,  location, status, 
			labId, parentId, sampleId, prepComments, tubeType, replicate);
			}

	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
	@Parameter(name = "idClass", value = "Aliquot"), @Parameter(name = "width", value = "10") })
	@Column(name = "ALIQUOT_ID", nullable = false, unique = true, length = 10, columnDefinition = "CHAR(10)")
	private String aliquotId;
	
	@Basic()
	@Column(name = "SEQ", nullable = false, columnDefinition = "NUMBER")
	private Integer sequence;
	
	
	//@ManyToOne(fetch = FetchType.LAZY)
	//@JoinColumn(name = "CHECKIN_ID", referencedColumnName = "CHECKIN_ID", nullable = false)
	//private SampleCheckin2 checkin;
	
	
	@Basic()
	@Column(name = "DATE_TRANSFERRED")
	private Calendar dateTransferred;
	
	@Basic()
	@Column(name = "MIN_VOLUME", columnDefinition = "NUMBER")
	private BigDecimal minVolume;
	
	@Basic()
	@Column(name = "VOLUME", nullable = true, columnDefinition = "NUMBER(8,3)")
	private BigDecimal volume;
	
	
	@Basic()
	@Column(name = "VOL_UNITS", nullable = true, columnDefinition = "VARCHAR2(26)")
	private String volUnits;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LOCATION_ID", referencedColumnName = "LOCATION_ID", nullable = true)
	private Location location;
	
	@Basic()
	@Column(name = "STATUS", nullable = true, columnDefinition = "CHAR(1)")
	private Character status;
	
	
	@Basic()
	@Column(name = "LAB_ID", columnDefinition = "CHAR(4)")
	private String labId;
	
	// Spot for a parent aliquot id
	@Basic()
	@Column(name = "PARENT_ID", columnDefinition = "CHAR(10)")
	private String parentId;
	
	
	@Basic()
	@Column(name = "PREP_COMMENTS", columnDefinition = "VARCHAR2(1000)")
	private String prepComments;
	
	
	@Basic()
	@Column(name = "TUBE_TYPE", columnDefinition = "VARCHAR2(30)")
	private String tubeType;
	
	
	@Basic()
	@Column(name = "SAMPLE_ID", columnDefinition = "CHAR(9)")
	private String sampleId;
	
	
	@Basic()
	@Column(name = "REPLICATE", columnDefinition = "NUMBER")
	Integer replicate;
	
	//@OneToMany(mappedBy = "aliquot", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	//@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	//List<AliquotAssay> aliquotAssays;
	
	
	
	public Aliquot() {  }
	
	private Aliquot(String aliquotId, Integer sequence, Calendar dateTransferred, 
			BigDecimal volume, BigDecimal minVolume, String volUnits, Location location, Character status,  String labId, String parentId, 
			String sampleId, String prepComments, String tubeType, Integer replicate)
			{
			this.aliquotId = aliquotId;
			this.sequence = sequence;
			this.dateTransferred = dateTransferred;
			this.volume = volume;
			this.minVolume = minVolume;
			this.volUnits = volUnits;
			this.location = location;
			this.status = status; //new Character('A');
			this.labId = labId;
			this.parentId = parentId;
			this.sampleId = sampleId;
			this.prepComments = prepComments;
			this.tubeType = tubeType;
			this.replicate = replicate;

	//		aliquotAssays = new ArrayList<AliquotAssay>();
			}
	
	
	//private Aliquot(String aliquotId, Integer sequence, SampleCheckin2 checkin, Calendar dateTransferred, 
	//BigDecimal volume, BigDecimal minVolume, String volUnits, Location location, Character status,  String labId, String parentId, 
	//String sampleId, String prepComments, String tubeType)
	//	{
	//	this(aliquotId, sequence, checkin,dateTransferred, 
	//			volume, minVolume, volUnits, location, status, labId,  parentId, 
	//			sampleId, prepComments,  tubeType, 1);
	//	}
	
	
	
	public void updateFromPrep(BigDecimal volume, String volUnits,  String comments, String tubeType,  Character status)
		{
		this.volume = volume; 
		this.volUnits = volUnits;
		this.prepComments = comments;
		this.tubeType = tubeType;
		this.status = status;
		}
	
	/*
	public void update(AliquotDTO dto, SampleCheckin2 checkin, Location location)
		{
		if (dto.getSequence() != null)
		if (!StringUtils.checkEmptyOrNull(dto.getSequence().toString()))
		this.sequence = dto.getSequence();
		

		if (dto.getDateTransferred() != null)
			this.dateTransferred = dto.getDateTransferred();
		
		if (dto.getVolume() != null)
			if (!StringUtils.checkEmptyOrNull(dto.getVolume().toString()))
				this.volume = dto.getVolume();
		
		if (dto.getMinVolume() != null)
			if (!StringUtils.checkEmptyOrNull(dto.getMinVolume().toString()))
				this.minVolume = dto.getMinVolume();
		
		
		if (!StringUtils.checkEmptyOrNull(dto.getVolUnits()))
			this.volUnits = dto.getVolUnits();
		
		if (location != null)
			this.location = location;
		
		if (dto.getStatus() != null)
			if (!StringUtils.checkEmptyOrNull(dto.getStatus().toString()))
				this.status = dto.getStatus();
		
		if (!StringUtils.checkEmptyOrNull(dto.getLabId()))
			this.labId = dto.getLabId();
		
		if (!StringUtils.checkEmptyOrNull(dto.getParentId()))
			this.parentId = dto.getParentId();
		
		if (!StringUtils.checkEmptyOrNull(dto.getSampleId()))
			this.sampleId = dto.getSampleId();
		
		if (!StringUtils.checkEmptyOrNull(dto.getPrepComments()))
			this.prepComments = dto.getPrepComments();
		//if (assay != null)
		//	this.assay = assay;
		
		if (!StringUtils.checkEmptyOrNull(dto.getTubeType()))
			this.tubeType = dto.getTubeType();
		}
	
	*/
	public void updateVolume(BigDecimal volume, String volUnits)
		{
		this.volume=volume;
		this.volUnits=volUnits;
		}
	
	public String getAliquotId()
		{
		return aliquotId;
		}
	
	public void setAliquotId(String aliquotId)
		{
		this.aliquotId = aliquotId;
		}
	
	public Integer getSequence()
		{
		return sequence;
		}
	
	public void setSequence(Integer sequence)
		{
		this.sequence = sequence;
		}
	
	
	public BigDecimal getMinVolume()
		{
		return minVolume;
		}


	public void setMinVolume(BigDecimal minVolume)
		{
		this.minVolume = minVolume;
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
	
	public Location getLocation()
		{
		return location;
		}
	
	public void setLocation(Location location)
		{
		this.location = location;
		}
	
	public Character getStatus()
		{
		return status;
		}
	
	public void setStatus(Character status)
		{
		this.status = status;
		}
	
	public Calendar getDateTransferred()
		{
		return dateTransferred;
		}
	
	public void setDateTransferred(Calendar dateTransferred)
		{
		this.dateTransferred = dateTransferred;
		}
	
	public String getLabId()
		{
		return labId;
		}
	
	public void setLabId(String labId)
		{
		this.labId = labId;
		}
	
	public String getParentId()
		{
		return parentId;
		}
	
	public void setParentId(String parentId)
		{
		this.parentId = parentId;
		}

	

	
	public String getSampleId()
		{
		return sampleId;
		}
	
	
	public void setSampleId(String sampleId)
		{
		this.sampleId = sampleId;
		}
	
	
	public String getPrepComments()
		{
		return prepComments;
		}
	
	
	public void setPrepComments(String prepComments)
		{
		this.prepComments = prepComments;
		}
	
	
	public String getTubeType()
		{
		return tubeType;
		}


	public void setTubeType(String tubeType)
		{
		this.tubeType = tubeType;
		}


	public Integer getReplicate()
		{
		return replicate;
		}

	public void setReplicate(Integer replicate)
		{
		this.replicate = replicate;
		}
/*
	public List<AliquotAssay> getAliquotAssays()
		{
		return aliquotAssays;
		}


	public void setAliquotAssays(List<AliquotAssay> aliquotAssays)
		{
		this.aliquotAssays = aliquotAssays;
		}
*/

	public String toString()
		{
		StringBuilder sb = new StringBuilder();
		
		sb.append("Aliquot Id " + getAliquotId() + System.getProperty("line.separator"));
	//	sb.append("Checkin Id" + this.getCheckin().getCheckinId() + System.getProperty("line.separator"));
		sb.append("Sample Id " + this.getSampleId() + System.getProperty("line.separator"));
		
		return sb.toString();
		}
	}
	
	

package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.umich.brcf.metabolomics.layers.domain.GenusSpecies;
import edu.umich.brcf.shared.layers.dto.SampleDTO;
import edu.umich.brcf.shared.util.interfaces.ISampleItem;
import edu.umich.brcf.shared.util.io.StringUtils;



@Entity()
@Table(name = "BIOLOGICAL_SAMPLE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DISCRIMINATOR", discriminatorType = DiscriminatorType.STRING)
public abstract class Sample implements Serializable, ISampleItem
	{
	public static String idFormat="(S)\\d{1}|(S)\\d{2}|(S)\\d{3}|(S)\\d{4}|(S)\\d{5}|(S)\\d{6}|(S)\\d{7}|(S)\\d{8}|\\d{3}|\\d{4}|\\d{5}";
	public static String _2019Format="(S)\\d{8}";// Issue 302
	@Id()
//	@GeneratedValue(generator = "IdGeneratorDAO")
//	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
//			@Parameter(name = "idClass", value = "Sample"), @Parameter(name = "width", value = "9") })
	@Column(name = "SAMPLE_ID", unique = true, nullable = false, length = 9, columnDefinition = "CHAR(9)")
	private String sampleID;

	@Basic()
	@Column(name = "SAMPLE_NAME", nullable = true, columnDefinition = "VARCHAR2(120)")
	private String sampleName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EXP_ID", referencedColumnName = "EXP_ID", nullable = false)
	private Experiment exp;
	// @Basic()
	// @Column(name = "EXP_ID", nullable = true, columnDefinition = "CHAR(7)")
	// private String expID;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUBJECT_ID", referencedColumnName = "SUBJECT_ID", nullable = true)
	private Subject subject;

	@Basic()
	@Column(name = "USER_DESCRIPTION", nullable = true, columnDefinition = "VARCHAR2(4000)")
	private String userDescription;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GENUSORSPECIES_ID", referencedColumnName = "GENUS_SPECIES_ID", nullable = false)
	private GenusSpecies genusOrSpecies;

	@Basic()
	@Column(name = "LOCID", nullable = true, columnDefinition = "CHAR(6)")
	private String locID;

	@Basic()
	@Column(name = "USER_DEFINED_SAMPLE_TYPE", nullable = true, columnDefinition = "VARCHAR2(100)")
	private String UserDefSampleType;
	
	@Basic()
	@Column(name = "USER_DEFINED_GOS", nullable = true, columnDefinition = "VARCHAR2(30)")
	private String UserDefGOS;

	@Basic()
	@Column(name = "VOLUME", nullable = true, columnDefinition = "NUMBER(22,9)")
	private BigDecimal volume;

	@Basic()
	@Column(name = "VOL_UNITS", nullable = true, columnDefinition = "VARCHAR2(26)")
	private String volUnits;

	@Basic()
	@Column(name = "CURRENT_VOLUME", nullable = true, columnDefinition = "NUMBER(22,9)")
	private BigDecimal cur_volume;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "STATUS", referencedColumnName = "ID", nullable = false)
	private SampleStatus status;

	@Basic()
	@Column(name = "SAMPLE_CONTROL_TYPE", nullable = true, columnDefinition = "NUMBER(6)")
	private Long sampleControlType;

	@Basic()
	@Column(name = "DATE_CREATED", nullable = true, columnDefinition = "DATE")
	private Calendar dateCreated;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SAMPLE_TYPE_ID", referencedColumnName = "SAMPLE_TYPE_ID", nullable = false)
	private SampleType sampleType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EXPERIMENTAL_GROUP_ID", referencedColumnName = "GROUP_ID", nullable = true)
	private ExperimentalGroup group;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_ID", referencedColumnName = "SAMPLE_ID", nullable = true, columnDefinition = "CHAR(9)")
	private Sample parent;
	
//	@OneToMany(mappedBy = "sample", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
//	List<Aliquot> aliquotList;
	
	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	List<Sample> children;
	
	@OneToMany(mappedBy = "associated", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	List<SampleDocument> docList;
	
	@OneToMany(mappedBy = "sample", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	List<ExperimentSetup> factorLevels;
	
	@OneToMany(mappedBy = "sample", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	List<SampleAssay> sampleAssays;
	

	protected Sample(String sampleID, String sampleName, Experiment exp, Subject subject, //String userDescription,
			GenusSpecies genusOrSpecies, String locID, String UserDefSampleType, String UserDefGOS, BigDecimal volume, String volUnits,
			SampleStatus status, Calendar dateCreated, SampleType sampleType, //, boolean sampleControlType
			ExperimentalGroup group, Sample parent) 
		{
		this.sampleID = sampleID;
		this.sampleName = sampleName;
		this.exp = exp;
		this.subject = subject;
		this.genusOrSpecies = genusOrSpecies;
		this.locID = locID;
		this.UserDefSampleType = UserDefSampleType;
		this.UserDefGOS = UserDefGOS;
		this.volume = volume;
		this.volUnits = volUnits;
		this.status = status;
//		this.sampleControlType = sampleControlType? new Long(1) : new Long(0);
		this.dateCreated = dateCreated;
		this.sampleType = sampleType;
		this.group = group;
		this.parent=parent;
		this.cur_volume = volume;
		this.children = new ArrayList<Sample>();
		}

	
	public void update(SampleDTO dto, Experiment exp, GenusSpecies gs, SampleStatus ss,
			SampleType st, Subject su)
		{
	//	if (!StringUtils.checkEmptyOrNull(dto.getSampleName()))
			this.sampleName = dto.getSampleName();
		
	//	if (!StringUtils.checkEmptyOrNull(dto.getVolume().toString()))
			this.volume = dto.getVolume();
		
	//	if (!StringUtils.checkEmptyOrNull(dto.getUserDescription()))
			this.setUserDescription(dto.getUserDescription());
		
//		if (!StringUtils.checkEmptyOrNull(dto.getVolUnits()))
				this.volUnits = dto.getVolUnits();
		
//		if (!StringUtils.checkEmptyOrNull(dto.getLocID()))
			this.locID = dto.getLocID();
		
	//	if (!StringUtils.checkEmptyOrNull(dto.getUserDefSampleType()))
			this.UserDefSampleType= dto.getUserDefSampleType();
		
	//	if (!StringUtils.checkEmptyOrNull(dto.getUserDefGOS()))
			this.UserDefGOS = dto.getUserDefGOS();
		
		
//		if (gs!= null)
			this.genusOrSpecies = gs;
		
	//	if (ss != null)
			this.status = ss;
		
	//	if (st != null)
			this.sampleType = st;
		
		//this.locID = dto.getLocID();
	///	if (!StringUtils.checkEmptyOrNull(dto.getCurrVolume().toString()))
			this.cur_volume = dto.getCurrVolume();
		
	//	if (dto.getDateCreated() != null)
			this.dateCreated = dto.getDateCreated();
		
	//	if (su != null)
			this.subject = su;
		}
	
	public Sample() { }


	
	public BigDecimal getCur_volume()
		{
		return cur_volume;
		}


	public void setVolume(BigDecimal volume)
		{
		this.volume = volume;
		}


	public void setVolUnits(String volUnits)
		{
		this.volUnits = volUnits;
		}


	public void setCur_volume(BigDecimal cur_volume)
		{
		this.cur_volume = cur_volume;
		}


	public void updateCurrentVolume(BigDecimal cur_volume){
		this.cur_volume = cur_volume;
	}

	public String getSampleID() {
		return sampleID;
	}

	public String getId() {
		return sampleID;
	}

	public String getSampleName() {
		return sampleName;
	}

	public String getName() {
		return getSampleName();
	}

	public Subject getSubject() {
		return subject;
	}

	public String getParentName() {
		return getExp().getExpName();
	}

	public String getUserDescription() {
		return userDescription;
	}
	
	public void setUserDescription(String userDescription) {
		this.userDescription = userDescription;
	}

	public String getUserSubject() {
		return (subject==null? "" : subject.getUserSubjectId());
	}

	public GenusSpecies getGenusOrSpecies() {
		return genusOrSpecies;
	}

	public String getLocID() 
		{
		return locID;
		}

	public void setLocID(String id)
		{
		locID = id;
		}

	public String getUserDefSampleType() {
		return UserDefSampleType;
	}

	public String getUserDefGOS() {
		return UserDefGOS;
	}

	public Experiment getExp() {
		return exp;
	}

	public BigDecimal getVolume() {
		return volume;
	}
	
	public String getVolumeAndUnits(){
		return (getVolume().doubleValue())+" "+getVolUnits();
		}
	
	public BigDecimal getCur_Volume() {
		return cur_volume;
	}
	
	public String getCurVolumeAndUnits(){
		return (getCur_Volume().doubleValue())+" "+getVolUnits();
		}

	public double getVolumeAsDouble() {
		return volume.doubleValue();
	}

	public String getVolUnits() {
		return volUnits;
	}

	public SampleStatus getStatus() {
		return status;
	}

	public Long getSampleControlType() {
		return sampleControlType;
	}
	
	public String getSampleControlValue() {
	return (getSampleControlType().intValue()==1)? "Yes" : "No";
	}

	public Calendar getDateCreated() {
		return dateCreated;
	}

	public SampleType getSampleType() {
		return sampleType;
	}

	public ExperimentalGroup getGroup() {
		return group;
	}

	public String getNodeObjectName() {
		return getSampleName();
	}

	public void setStoredStatus() {
		status = SampleStatus.instance(SampleStatus.STATUS_IN_STORAGE);
	}

	public void setCompletedStatus() {
		status = SampleStatus.instance(SampleStatus.STATUS_COMPLETE);
	}

	public void setPrepStatus() {
		status = SampleStatus.instance(SampleStatus.STATUS_IN_PREP);
	}

	public void setAnalysisStatus() {
		status = SampleStatus.instance(SampleStatus.STATUS_IN_ANALYSIS);
	}

	public void setInjectedStatus() {
		status = SampleStatus.instance(SampleStatus.STATUS_INJECTED);
	}
	
	// Issue 222
	public void setReturnedStatus() {
		status = SampleStatus.instance(SampleStatus.STATUS_RETURNED);
	}
	
	// Issue 222
	public void setDiscardedStatus() {
		status = SampleStatus.instance(SampleStatus.STATUS_DISCARDED);
	}
	
	
	public void setProcessedStatus() {
		status = SampleStatus.instance(SampleStatus.STATUS_PROCESSED);
	}

//	public List<Aliquot> getAliquotList() {
//		return aliquotList;
//	}
	
	public List<Sample> getChildren() {
		return children;
	}
	
	public List<SampleDocument> getDocList() {
		return docList;
	}
	
//	public List<Aliquot> getAvtiveAliquotList() {
//		List<Aliquot> activeAliquotList = new ArrayList<Aliquot>();
//		for (Aliquot aliquot : aliquotList)
//		{
//			if (aliquot.getStatus().equals('A'))
//				activeAliquotList.add(aliquot);
//		}
//		return activeAliquotList;
//	}
	
//	public String getNumberOfSamples() {
//		return aliquotList == null ? "0" : Integer.toString(aliquotList.size());
//	}
	
	public Sample getParent() {
		return parent;
	}

	public void setParent(Sample parent) {
		this.parent = parent;
	}

	public List<ExperimentSetup> getFactorLevels() {
		return factorLevels;
	}

	public List<SampleAssay> getSampleAssays() {
		return sampleAssays;
	}
	
	public String getCommaSeparatedSampleAssays()
		{
		String assayList="";
		for (SampleAssay sa: getSampleAssays())
			assayList+=sa.getAssay().getAssayName()+", ";
		
		return assayList.isEmpty()?assayList:assayList.substring(0, assayList.length()-2);
		}
	

}

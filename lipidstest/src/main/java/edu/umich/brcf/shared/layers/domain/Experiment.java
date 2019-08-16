package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import edu.umich.brcf.shared.layers.dto.ExperimentDTO;
import edu.umich.brcf.shared.layers.domain.ClientReport;
import edu.umich.brcf.shared.util.datacollectors.ClientDataInfo;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;

@Entity()
@Table(name = "EXPERIMENT")
public class Experiment implements Serializable
	{
	public static String idFormat = "(EX)\\d{1}|(EX)\\d{2}|(EX)\\d{3}|(EX)\\d{4}|(EX)\\d{5}|\\d{2}|\\d{3}|\\{d{4}|\\d{5}";
	public static String fullIdFormat = "(EX)\\d{5}";

	public static Experiment instance(String expName, Project proj, String expDescription, 
			Priority priority, String notes, User creator, String serviceRequest, Boolean isChear)
		{
		return new Experiment(null, expName, proj, expDescription, priority, notes, creator, serviceRequest, isChear);
		}
	
	
	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
			@Parameter(name = "idClass", value = "Experiment"), @Parameter(name = "width", value = "7") })
	@Column(name = "EXP_ID", unique = true, nullable = false, length = 7, columnDefinition = "CHAR(7)")
	private String expID;

	@Basic()
	@Column(name = "EXP_NAME", nullable = true, columnDefinition = "VARCHAR2(120)")
	private String expName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PROJECT_ID", referencedColumnName = "PROJECT_ID", nullable = false)
	private Project project;

	// Issue 206
	@Basic()
	@Column(name = "IS_CHEAR", nullable = true, columnDefinition = "CHAR(1)")
	private Boolean isChear;
  
	@Basic()
	@Column(name = "EXP_DESCRIPTION", nullable = true, columnDefinition = "VARCHAR2(4000)")
	private String expDescription;

	@Basic()
	@Column(name = "CREATIONDATE", nullable = true, columnDefinition = "DATE")
	private Calendar creationDate;
	
	//@Basic()
	///@Column(name = "COMPLETION_DATE", nullable = true, columnDefinition = "DATE")
	//private Calendar completionDate;

	@Basic()
	@Column(name = "NOTES", nullable = true)
	private String notes;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRIORITY_TYPE", nullable = false, columnDefinition = "VARCHAR2(10)")
	private Priority priority;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CREATOR", nullable = true, columnDefinition = "CHAR(6)")
	private User creator;

	@Basic()
	@Column(name = "SERVICE_REQUEST_ID", nullable = true, columnDefinition = "VARCHAR2(20)")
	private String serviceRequest;

	@OneToMany(mappedBy = "exp", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	List<Sample> sampleList;

	@OneToMany(mappedBy = "associated", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	List<ExperimentDocument> docList;
	
	@OneToMany(mappedBy = "exp", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	List<ProtocolReport> protocols;

	@OneToMany(mappedBy = "exp", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	List<ClientReport> reports;

	@OneToMany(mappedBy = "exp", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	List<Factor> factors;

	private Experiment(String expID, String expName, Project project, String expDescription, Priority priority,
		String notes, User creator, String serviceRequest, Boolean isChear) //Calendar completionDate)
		{
		this.expID = expID;
		this.expName = expName;
		this.project = project;
		this.expDescription = expDescription;
		this.creationDate = Calendar.getInstance();
		this.priority = priority;
		this.sampleList = new ArrayList<Sample>();
		this.notes = notes;
		this.creator = creator;
		this.serviceRequest = serviceRequest;
		this.isChear = isChear;
		//this.completionDate = completionDate;
		}

	public Experiment() {  }


	public void updateFromClientInfo(ClientDataInfo dto) 
		{
		this.expDescription = dto.getExperimentDescription();
		
		//this.expID = dto.getExpId();
		//this.expName = dto.getExpName();
		//this.project = proj;
		//this.priority = priority;
		//this.expDescription = dto.getExpDescription();
		//this.notes = dto.getNotes();
		//this.creator = creator;
		//this.serviceRequest = dto.getServiceRequest();
		//this.nReportedSamples = Integer.parseInt(dto.getNReportedSamples());
		//this = dto.getChearExpId();
		}

	
	public Calendar getCreationDate()
		{
		return creationDate;
		}

	public String getCreationDateAsStr()
		{
		return (creationDate == null ? "" : DateUtils.dateStrFromCalendar("MM/dd/yyyy", creationDate));
		}
	
	public Priority getPriority()
		{
		return priority;
		}

	public List<Sample> getSampleList()
		{
		return sampleList;
		}

	public List<ExperimentDocument> getDocList()
		{
		return docList;
		}

	public List<ClientReport> getReports()
		{
		return reports;
		}
	
	public List<ProtocolReport> getProtocols()
		{
		return protocols;
		}

	public void update(ExperimentDTO dto, Project proj, Priority priority, User creator)
		{
		this.expID = dto.getExpID();
		this.expName = dto.getExpName();
		this.project = proj;
		this.priority = priority;
		this.expDescription = dto.getExpDescription();
		this.notes = dto.getNotes();
		this.creator = creator;
		this.serviceRequest = dto.getServiceRequest();
		this.isChear = dto.getIsChear();
		}

	public void updateServiceRequest(String serviceRequest)
		{
		this.serviceRequest = serviceRequest;
		}

	public String getNumberOfSamples()
		{
		return sampleList == null ? "0" : Integer.toString(sampleList.size());
		}

	public String getExpID()
		{
		return expID;
		}

	public String getExpName()
		{
		return expName;
		}

	public Project getProject()
		{
		return project;
		}

	public String getExpDescription()
		{
		return expDescription;
		}

	public String getNotes()
		{
		return notes;
		}

	public User getCreator()
		{
		return creator;
		}

	public String getServiceRequest()
		{
		return serviceRequest;
		}

	public String getNodeObjectName()
		{
		return getExpName() + " (" + getNumberOfSamples() + ")";
		}

	public List<Factor> getFactors()
		{
		return factors;
		}

	public Boolean getIsChear() 
		{
		return isChear;
		}

	public void setIsChear(Boolean isChear) 
		{
		this.isChear = isChear;
		}
	
	
	}

package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import edu.umich.brcf.shared.layers.dto.ProjectDTO;
import edu.umich.brcf.shared.util.utilpackages.CalendarUtils;


@Entity()
@Table(name = "PROJECT")
public class Project implements Serializable
	{
	public static String idFormat = "(PR)\\d{1}|(PR)\\d{2}|(PR)\\d{3}|(PR)\\d{4}";
	public static String fullIdFormat = "(PR)\\d{4}";
	public static List<String> STATUS_TYPES = Arrays.asList(new String[] { "A", "I" });
	public static String PROJECT_DATE_FORMAT = "MM/dd/yy";
	@Transient
	List <Experiment> expAssayDateCritList;
	
	public static Project instance(Client client, String projectName, String description, User contactPerson, 
			String statusID, Calendar startDate, Calendar finalDeadline, String timelineID, String notes)
		{
		return new Project(null, client, projectName, description,
				contactPerson, statusID, startDate, finalDeadline, timelineID,
				notes);
		}

	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
			@Parameter(name = "idClass", value = "Project"),
			@Parameter(name = "width", value = "6") })
	@Column(name = "PROJECT_ID", unique = true, nullable = false, length = 6, columnDefinition = "CHAR(6)")
	private String projectID;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CLIENT_ID", referencedColumnName = "CLIENT_ID", nullable = true)
	private Client client;

	@Basic()
	@Column(name = "PROJECT_NAME", nullable = true)
	private String projectName;

	@Basic()
	@Column(name = "PROJECT_DESCRIPTION", nullable = true)
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CONTACTPERSON_ID", referencedColumnName = "RESEARCHER_ID", nullable = true)
	private User contactPerson;

	@Basic()
	@Column(name = "STATUS_ID", nullable = true)
	private String statusID;

	@Basic()
	@Column(name = "START_DATE", nullable = true)
	private Calendar startDate;

	@Basic()
	@Column(name = "FINAL_DEADLINE", nullable = true)
	private Calendar finalDeadline;

	@Basic()
	@Column(name = "TIMELINE_ID", nullable = true)
	private String timelineID;

	@Basic()
	@Column(name = "NOTES", nullable = true)
	private String notes;

	@OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<Experiment> experimentList;

	@OneToMany(mappedBy = "associated", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	List<ProjectDocument> docList;

	public String getProjectID()
		{
		return projectID;
		}

	public Client getClient()
		{
		return client;
		}

	public void setClient(Client c)
		{
		this.client = c;
		}

	public String getProjectName()
		{
		return projectName;
		}

	public String getDescription()
		{
		return description;
		}

	public User getContactPerson()
		{
		return contactPerson;
		}

	public String getStatusID()
		{
		return statusID;
		}

	public Calendar getStartDate()
		{
		return startDate;
		}

	public String getStartDateString()
		{
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		return (startDate == null) ? "" : sdf.format(startDate.getTime());
		}

	public Calendar getFinalDeadline()
		{
		return finalDeadline;
		}

	public String getTimelineID()
		{
		return timelineID;
		}

	public List<Experiment> getExperimentList()
		{
		return experimentList;
		}

	public List<ProjectDocument> getDocList()
		{
		return docList;
		}

	private Project(String projectID, Client client, String projectName,
			String description, User contactPerson, String statusID,
			Calendar startDate, Calendar finalDeadline, String timelineID,
			String notes)
		{
		this.projectID = projectID;
		this.projectName = projectName;
		this.client = client;
		this.description = description;
		this.contactPerson = contactPerson;
		this.statusID = statusID;
		this.startDate = startDate;
		this.finalDeadline = finalDeadline;
		this.timelineID = timelineID;
		this.notes = notes;
		// this.client.addProject(this);
		this.docList = new ArrayList<ProjectDocument>();
		this.experimentList = new ArrayList<Experiment>();
		}

	public Project()  { }

	
	public void update(ProjectDTO dto, Client client, User contactPerson)
		{
		this.projectName = dto.getProjectName();
		this.client = client;
		this.description = dto.getDescription();
		this.contactPerson = contactPerson;
		this.statusID = dto.getStatusID();
		this.startDate = CalendarUtils.calendarFromString(dto.getStartDate(),Project.PROJECT_DATE_FORMAT);
		this.finalDeadline = dto.getFinalDeadline();
		this.timelineID = dto.getTimelineID();
		this.notes = dto.getNotes();
		}

	public String getContactPersonName()
		{
		return contactPerson.getFullName();
		}

	public String getNodeObjectName()
		{
		return getProjectName();
		}

	public String getNotes()
		{
		return notes;

		}

	public static void setIdFormat(String idFormat)
		{
		Project.idFormat = idFormat;
		}

	public static void setFullIdFormat(String fullIdFormat)
		{
		Project.fullIdFormat = fullIdFormat;
		}

	public static void setSTATUS_TYPES(List<String> sTATUS_TYPES)
		{
		STATUS_TYPES = sTATUS_TYPES;
		}

	public void setProjectID(String projectID)
		{
		this.projectID = projectID;
		}

	public void setProjectName(String projectName)
		{
		this.projectName = projectName;
		}

	public void setDescription(String description)
		{
		this.description = description;
		}

	public void setContactPerson(User contactPerson)
		{
		this.contactPerson = contactPerson;
		}

	public void setStatusID(String statusID)
		{
		this.statusID = statusID;
		}

	public void setStartDate(Calendar startDate)
		{
		this.startDate = startDate;
		}

	public void setFinalDeadline(Calendar finalDeadline)
		{
		this.finalDeadline = finalDeadline;
		}

	public void setTimelineID(String timelineID)
		{
		this.timelineID = timelineID;
		}

	public void setNotes(String notes)
		{
		this.notes = notes;
		}

	public void setExperimentList(List<Experiment> experimentList)
		{
		this.experimentList = experimentList;
		}
	
	public void setDocList(List<ProjectDocument> docList)
		{
		this.docList = docList;
		}
	
	// issue 187
	@Transient
	public List <Experiment> getExpAssayDateCritList ()
		{
		return expAssayDateCritList;
		}
	
	// issue 187
	@Transient
	public void setExpAssayDateCritList (List <Experiment> vexpAssayDateCritList)
		{
		expAssayDateCritList = vexpAssayDateCritList;
		}
	
	}

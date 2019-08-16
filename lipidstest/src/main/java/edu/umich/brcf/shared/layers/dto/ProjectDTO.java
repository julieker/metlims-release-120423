package edu.umich.brcf.shared.layers.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import edu.umich.brcf.shared.layers.domain.Project;


public class ProjectDTO implements Serializable
	{

	// public static ProjectDTO instance(String clientID, String projectName,
	// String description, String contactPerson, String statusID, String
	// startDate, Calendar finalDeadline, String timelineID) {
	// return new ProjectDTO(null, clientID, projectName,
	// description, contactPerson, statusID, startDate, finalDeadline,
	// timelineID);
	// }

	public static ProjectDTO instance(Project project)
		{
		return new ProjectDTO(project.getProjectID(), project.getClient()
				.getClientID(), project.getProjectName(),
				project.getDescription(), project.getContactPerson().getId(),
				project.getStatusID(), project.getStartDate(),
				project.getFinalDeadline(), project.getTimelineID(),
				project.getNotes());
		}

	private String id;
	private String clientID;
	private String projectName;
	private String description;
	private String contactPerson;
	private String statusID;
	private String startDate;
	private Calendar finalDeadline;
	private String timelineID;
	private String notes;

	private ProjectDTO(String id, String clientID, String projectName,
			String description, String contactPerson, String statusID,
			Calendar startDate, Calendar finalDeadline, String timelineID,
			String notes)
		{
		this.id = id;
		this.clientID = clientID;
		this.projectName = projectName;
		this.description = description;
		this.contactPerson = contactPerson;
		this.statusID = statusID;
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		this.startDate = (startDate == null) ? "" : sdf.format(startDate
				.getTime());
		// this.startDate = startDate;
		this.finalDeadline = finalDeadline;
		this.timelineID = timelineID;
		this.notes = notes;
		}

	public ProjectDTO()
		{
		}

	public String getId()
		{
		return id;
		}

	public void setId(String id)
		{
		this.id = id;
		}

	public String getClientID()
		{
		return clientID;
		}

	public void setClientID(String clientID)
		{
		this.clientID = clientID;
		}

	public String getProjectName()
		{
		return projectName;
		}

	public void setProjectName(String projectName)
		{
		this.projectName = projectName;
		}

	public String getDescription()
		{
		return description;
		}

	public void setDescription(String description)
		{
		this.description = description;
		}

	public String getContactPerson()
		{
		return contactPerson;
		}

	public void setContactPerson(String contactPerson)
		{
		this.contactPerson = contactPerson;
		}

	public String getStatusID()
		{
		return statusID;
		}

	public void setStatusID(String statusID)
		{
		this.statusID = statusID;
		}

	public String getStartDate()
		{
		return startDate;
		}

	public void setStartDate(String startDate)
		{
		this.startDate = startDate;
		}

	public Calendar getFinalDeadline()
		{
		return finalDeadline;
		}

	public void setFinalDeadline(Calendar finalDeadline)
		{
		this.finalDeadline = finalDeadline;
		}

	public String getTimelineID()
		{
		return timelineID;
		}

	public void setTimelineID(String timelineID)
		{
		this.timelineID = timelineID;
		}

	public String getNotes()
		{
		return notes;
		}

	public void setNotes(String notes)
		{
		this.notes = notes;
		}
	}

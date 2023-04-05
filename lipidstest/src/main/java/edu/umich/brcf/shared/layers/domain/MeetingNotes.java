package edu.umich.brcf.shared.layers.domain;
/***************************
 *Created by Julie Keros aug 8 2022 issue 210
 * for LIMS progress tracking
 * 
 ********************/

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Entity;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import edu.umich.brcf.shared.layers.dto.MeetingNotesDTO;
import edu.umich.brcf.shared.util.utilpackages.CalendarUtils;
//import edu.umich.brcf.shared.layers.dto.MeetingNotesDTO;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;

import javax.persistence.Table;

@Entity()
@Table(name = "MEETING_NOTES")

// issue 61
public class MeetingNotes implements Serializable 
	{
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CREATED_BY", nullable = true, columnDefinition = "CHAR(6)")
	private User createdBy;
	

	public static MeetingNotes instance(  Calendar dateEntered, String notes,  User createdBy) 
		{
		return new MeetingNotes(null, dateEntered, notes,  createdBy);
		}

	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
	@Parameter(name = "idClass", value = "MeetingNotes"), @Parameter(name = "width", value = "10") })
	@Column(name = "NOTE_ID", nullable = false, unique = true, length = 10, columnDefinition = "CHAR(10)")
	private String noteID;
	
	
	
	@Basic()
	@Column(name = "DATE_ENTERED", nullable = true)
	private Calendar dateEntered;
	
	@Basic()
	@Column(name = "NOTES", nullable = true)
	private String notes;
	
	public MeetingNotes() {  }
	
	private MeetingNotes(String noteID, Calendar dateEntered, String notes,  User createdBy)
		{
		this.noteID = noteID;
		this.dateEntered = dateEntered;
		this.notes = notes;
		this.createdBy = createdBy;

	 //issue 196
		}
	
	public void update(MeetingNotesDTO meetingNotesDto, User createdBy, Calendar dateEntered)
		{		
	/*	this.comments = MeetingNotesDto.getComments();
		System.out.println("HERE before date complated:->" + MeetingNotesDto.getDateCompleted());
		this.dateCompleted = CalendarUtils.calendarFromString(MeetingNotesDto.getDateCompleted(),ProcessTracking.ProcessTracking_DATE_FORMAT);
		this.dateStarted = CalendarUtils.calendarFromString(MeetingNotesDto.getDateStarted(),ProcessTracking.ProcessTracking_DATE_FORMAT);
		this.assignedTo = assignedToUser;
		this.dateAssigned = CalendarUtils.calendarFromString(MeetingNotesDto.getDateAssigned(),ProcessTracking.ProcessTracking_DATE_FORMAT);
		*/
		this.dateEntered = CalendarUtils.calendarFromString(meetingNotesDto.getDateEntered(),ProcessTracking.ProcessTracking_DATE_FORMAT);
		this.notes = meetingNotesDto.getNotes();
		this.createdBy = createdBy;
		//this.assignedTo = assignedToUser;
		//this.dateCompleted = dateCompleted;
		} 

	public String getNoteID()
		{
		return noteID;
		}
	
	public void setNoteID(String noteID)
		{
		this.noteID = noteID;
		}

	
	public Calendar getDateEntered()
		{
		return dateEntered;
		}

	public void setDateEntered(Calendar dateEntered)
		{
		this.dateEntered = dateEntered;
		}
	

	public String getNotes()
		{
		return notes;
		}

	public void setNotes(String notes)
		{
		this.notes = notes;
		}
	
	public User getCreatedBy()
		{
		return createdBy;
		}

	public void setCreatedBy(User createdBy)
		{
		this.createdBy = createdBy;
		}

	
	public String convertToDateString(Calendar dateToConvert)
		{
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		return (dateToConvert == null) ? "" : sdf.format(dateToConvert.getTime());
		}
	
	
	
	}
	
	

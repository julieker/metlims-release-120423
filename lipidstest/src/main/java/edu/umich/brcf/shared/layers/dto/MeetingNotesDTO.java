
////////////////////////////////////////////////////
// MeetingNotesDTO.java

// Created by by Julie Keros July 24 2022 
// issue 210
////////////////////////////////////////////////////
// Updated by Julie Keros May 28, 2020

package edu.umich.brcf.shared.layers.dto;

// issue 61 2020
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.brcf.metabolomics.panels.lims.mixtures.MixAliquotInfo;
import edu.umich.brcf.shared.layers.domain.Mixture;
import edu.umich.brcf.shared.layers.domain.User;
import edu.umich.brcf.shared.layers.domain.MeetingNotes;

// issue 61 2020
// issue 31 2020
public class MeetingNotesDTO implements Serializable
	{
	String noteID;
	String dateEntered;
	String notes;
	String createdBy;
	
	public MeetingNotesDTO() { }	;
	
	public static MeetingNotesDTO instance(String noteId, String dateEntered, String notes, String createdBy) 
	    {
	    return new MeetingNotesDTO(noteId, dateEntered, notes, createdBy);
		}
	
	public static MeetingNotesDTO instance(MeetingNotes meetingNotes)
		{
		SimpleDateFormat format1 = new SimpleDateFormat("mm/dd/yyyy");
		//String formattedDateStarted = format1.format(MeetingNotes.convertToDateString(MeetingNotes.getDateStarted()));
		String dateEntered = meetingNotes.convertToDateString(meetingNotes.getDateEntered());
		//String formattedDateCompleted = format1.format(MeetingNotes.convertToDateString(MeetingNotes.getDateCompleted()));
		// Output "Wed Sep 26 14:23:28 EST 2012"		
		return new MeetingNotesDTO(meetingNotes.getNoteID(), dateEntered,  meetingNotes.getCreatedBy().getId(), dateEntered);
		}	
	// issue 61 2020
	

    
    
    // issue 123
    Map<String, List<String>> mixtureAliquotMap = new HashMap<String, List<String>>(); 
    Map<String, List<MixAliquotInfo>> mixtureAliquotInfoMap = new HashMap<String, List<MixAliquotInfo>>();
    // issue 116
	private MeetingNotesDTO(String noteID, String dateEntered, String notes,  String createdBy)
		{
		this.noteID = noteID;	
	    this.dateEntered = dateEntered;
	    this.notes = notes;
	    this.createdBy = createdBy;
	    
		} 
	
	


	
	public String getNoteID()
		{
		return noteID;
		}

	public void setNoteID(String noteID)
		{
		this.noteID= noteID;
		}
	
	public String getDateEntered()
		{
		return dateEntered;
		}

	public void setDateEntered(String dateEntered)
		{
		this.dateEntered= dateEntered;
		}
	
	public String getNotes()
		{
		return notes;
		}

	public void setNotes(String notes)
		{
		this.notes= notes;
		}
	
	public String getCreatedBy()
		{
		return createdBy;
		}

	//issue 196
	public void setCreatedBy(String createdBy)
		{
		this.createdBy= createdBy;
		}
	
	}

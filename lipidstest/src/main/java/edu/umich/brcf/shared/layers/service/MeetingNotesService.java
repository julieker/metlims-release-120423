// Updated by Julie Keros June 2 2020
package edu.umich.brcf.shared.layers.service;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;
import edu.umich.brcf.metabolomics.panels.lims.mixtures.AliquotInfo;
import edu.umich.brcf.metabolomics.panels.lims.mixtures.MixAliquotInfo;
import edu.umich.brcf.shared.layers.dao.AliquotDAO;
import edu.umich.brcf.shared.layers.dao.MixtureDAO;
import edu.umich.brcf.shared.layers.dao.MeetingNotesDAO;
import edu.umich.brcf.shared.layers.domain.Aliquot;
import edu.umich.brcf.shared.layers.domain.Mixture;
import edu.umich.brcf.shared.layers.domain.MixtureAliquot;
import edu.umich.brcf.shared.layers.domain.MixtureAliquotPK;
import edu.umich.brcf.shared.layers.domain.MixtureChildren;
import edu.umich.brcf.shared.layers.domain.MixtureChildrenAliquot;
import edu.umich.brcf.shared.layers.domain.MixtureChildrenAliquotPK;
import edu.umich.brcf.shared.layers.domain.MixtureChildrenPK;
import edu.umich.brcf.shared.layers.domain.MeetingNotes;
import edu.umich.brcf.shared.layers.domain.User;
import edu.umich.brcf.shared.layers.dto.AliquotDTO;
import edu.umich.brcf.shared.layers.dto.MixtureDTO;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;
import edu.umich.brcf.shared.layers.dto.MeetingNotesDTO;

@Transactional(rollbackFor = Exception.class)
public class MeetingNotesService 
    {	
	
	MeetingNotesDAO meetingNotesDao;
	
	
	public MeetingNotes loadById(String jobid)
		{		
		return meetingNotesDao.loadById(jobid);
		}
	
	/*
	public List<MeetingNotes> loadByAssignedTo(String assignedTo)
		{
		return meetingNotesDao.loadByAssignedTo(assignedTo);
		}
	*/
	
	public List<MeetingNotes> loadAllMeetings()
		{
		return meetingNotesDao.loadAllMeetings();
		}	

	// issue 61	

	public MeetingNotesDAO getMeetingNotesDao()
		{
		return meetingNotesDao;
		}
	
	public void setMeetingNotesDao(MeetingNotesDAO meetingNotesDao) 
		{
		this.meetingNotesDao = meetingNotesDao;
		}
	
	public MeetingNotes save (MeetingNotesDTO dto, User user)
    	{
		try
			{
			MeetingNotes mn = MeetingNotes.instance(DateUtils.calendarFromDateStr(dto.getDateEntered(), Aliquot.ALIQUOT_DATE_FORMAT), dto.getNotes(), user);	
			if (dto.getNoteID() == null || dto.getNoteID().equals("to be assigned"))
				meetingNotesDao.createMeetingNote(mn);
			else
				{
				mn = meetingNotesDao.loadById(dto.getNoteID());	
				mn.update(dto, user, mn.getDateEntered());
				}
			return mn;
			}
		catch (Exception e)
			{
			e.printStackTrace();
			throw new RuntimeException("Error when saving note");
			}
    	}
	
}

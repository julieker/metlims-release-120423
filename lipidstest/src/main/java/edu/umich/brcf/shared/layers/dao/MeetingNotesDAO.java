/**********************
 * Updated by Julie Keros Sept 2000
 * Added aliquotIdsForExpId ,
 * getMatchingAliquotIds,loadByCid and other routines for Aliquots.
 **********************/
package edu.umich.brcf.shared.layers.dao;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
//import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.stereotype.Repository;

import com.mysql.jdbc.StringUtils;

import edu.umich.brcf.shared.layers.domain.Aliquot;
import edu.umich.brcf.shared.layers.domain.AssayAliquot;
import edu.umich.brcf.shared.layers.domain.ExperimentAliquot;
import edu.umich.brcf.shared.layers.domain.MeetingNotes;
import edu.umich.brcf.shared.layers.domain.MixtureAliquot;
import edu.umich.brcf.shared.layers.domain.ProcessTrackingDetails;
import edu.umich.brcf.shared.layers.domain.VolumeUnits;

// issue 61

@Repository
public class MeetingNotesDAO extends BaseDAO 
	{	

	// issue 210
	public void createMeetingNote(MeetingNotes mn)
		{
		getEntityManager().persist(mn);
		}
		
	// issue 61 2020 
	public MeetingNotes loadById(String noteID)
		{
		MeetingNotes mn = getEntityManager().find(MeetingNotes.class, noteID);
		return mn;
		}
	
	public List<MeetingNotes> loadAllMeetings()
		{
		List<MeetingNotes> mnList =  getEntityManager().createQuery("from MeetingNotes order by 1")
				.getResultList();	
	   
		return mnList;
		}
	}

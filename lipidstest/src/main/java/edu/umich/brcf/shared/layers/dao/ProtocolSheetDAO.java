////////////////////////////////////////////////////
// ProtocolSheetDAO.java
// Written by Jan Wigginton, Oct 28, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.layers.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.stereotype.Repository;

import edu.umich.brcf.shared.layers.dao.BaseDAO;
import edu.umich.brcf.shared.layers.domain.Document;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.ProtocolSheet;
import edu.umich.brcf.shared.layers.domain.StandardProtocol;
import edu.umich.brcf.shared.util.FormatVerifier;


@Repository
public class ProtocolSheetDAO extends  BaseDAO
	{
	public void createProtocolSheet(ProtocolSheet sheet) 
		{
		getEntityManager().persist(sheet);
		}
	
	
	public ProtocolSheet loadById(String string)
		{
		ProtocolSheet item = getEntityManager().find(ProtocolSheet.class, string);
		return item;
		}
	
	
	public void deleteSheet(String sheetId)
		{
		ProtocolSheet sheet = getEntityManager().find(ProtocolSheet.class, sheetId);
		sheet.setDeleted();
		}
	
	
	public List<ProtocolSheet> loadForUploadDateRange(Calendar fromDate, Calendar toDate)
		{
		fromDate.set(Calendar.HOUR_OF_DAY, 0);
		fromDate.set(Calendar.MINUTE, 0);
		fromDate.set(Calendar.SECOND, 0);
	
		toDate.set(Calendar.HOUR_OF_DAY, 0);
		toDate.set(Calendar.MINUTE, 0);
		toDate.set(Calendar.SECOND, 0);
		toDate.roll(Calendar.DAY_OF_YEAR, 1);
		
		List<ProtocolSheet> lst = getEntityManager().createQuery("from ProtocolSheet r where r.recordedDate >= ?1  and r.recordedDate < ?2  and r.deleted IS NULL order by r.recordedDate desc")
				.setParameter(1, fromDate).setParameter(2, toDate).getResultList();
		
		return lst;
		}
	
	// Issue 245
	public List<ProtocolSheet> loadForExpId(String expId, boolean excludeDeleteFlag)
	    {
		List<ProtocolSheet> lst = null;
		if (!FormatVerifier.verifyFormat(Experiment.fullIdFormat, expId))
			return new ArrayList<ProtocolSheet>();
		if (!excludeDeleteFlag)	
		     {
		     lst = getEntityManager().createQuery("from ProtocolSheet r where r.experimentId = ?1 and r.deleted IS NULL order by r.recordedDate desc")
				.setParameter(1, expId).getResultList();
		     }
		else
	         {
	         lst = getEntityManager().createQuery("from ProtocolSheet r where r.experimentId = ?1 and r.deleted IS NULL and (r.deleted is null or r.deleted = false) order by r.recordedDate desc")
			.setParameter(1, expId).getResultList();
	         }
		return lst;
	    }
	
	// Issue 245
	public List<ProtocolSheet> loadForExpId(String expId)
		{
		return loadForExpId(expId, false);
		}

	}

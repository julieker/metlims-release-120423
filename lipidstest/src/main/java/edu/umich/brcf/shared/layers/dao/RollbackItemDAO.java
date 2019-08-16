////////////////////////////////////////////////////
// RollbackItemDAO.java
// Written by Jan Wigginton, Jun 5, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.layers.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import edu.umich.brcf.shared.layers.domain.RollbackItem;
import edu.umich.brcf.shared.layers.dto.RollbackItemDTO;
import edu.umich.brcf.shared.util.FieldLengths;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;


@Repository
public class RollbackItemDAO extends BaseDAO
	{
	public void createRollbackItem(RollbackItem item) 
		{
		getEntityManager().persist(item);
		}

	public void deleteRollbackItem(RollbackItem item) 
		{
		getEntityManager().remove(item);
		}
	
	public RollbackItem loadBySampleId(String id) 
		{
		RollbackItem item = getEntityManager().find(RollbackItem.class, id);
		return item;
		}
	
	
	public List<RollbackItem> loadForSampleIds(List<String> sampleIds)
		{
		String queryStr;
		if (sampleIds.size() < FieldLengths.ORACLE_MAX_LIST_LENGTH)
			{
			String idSet = StringUtils.buildDatabaseListFromList(sampleIds);
			queryStr = "from RollbackItem r where r.sampleId in " + idSet + " order by r.sampleId";
			}
		else 
			{   
			String idSet = StringUtils.buildDatabaseOrListFromList("sampleId", sampleIds);
			queryStr = "from RollbackItem where " + idSet + " order by sampleId";
			}
		
		
		List<RollbackItem> items = getEntityManager().createQuery(queryStr).getResultList();
		return items;
		}
	
	
	public List<RollbackItem> loadForExpId(String expId)
		{
		String queryStr = "from RollbackItem r where expId = ?1 order by r.sampleId";
		List<RollbackItem> lst = getEntityManager().createQuery(queryStr).setParameter(1, expId).getResultList();
		
		return lst;
		}

	
	public List<RollbackItemDTO> loadRollbackInfoForLogging(String expId, Map<String, String> itemsToExclude)
		{
		String queryStr = "select exp_id, sample_id, researcher_sample_id, subject_id, researcher_subject_id,"
				+ " cast(date_created AS DATE) from VW_SAMPLE_ROLLBACK_INFO where exp_id = ?1 order by sample_id";
		Query query = getEntityManager().createNativeQuery(queryStr).setParameter(1, expId);
		
		List<Object[]> resultList  = query.getResultList();
		List<String> returnList = new ArrayList<String>();
		
		List<RollbackItemDTO> rollbackItemList = new ArrayList<RollbackItemDTO>();
		for (Object [] obj : resultList)
			{	
			if (obj.length < 6) 
				continue;
			String sampleId = (String) obj[1];
			
			if (itemsToExclude == null || itemsToExclude.containsKey(sampleId))
				continue;
			
			String eid  = (String) obj[0];
			
			String researcherSampleId = (String) obj[2];
			String subjectId = (String) obj[3];
			String researcherSubjectId = (String) obj[4];
			
			Calendar dateCreated = Calendar.getInstance();
			Timestamp t =  (Timestamp) obj[5];
			if (t != null)
				{
				dateCreated.setTimeInMillis(t.getTime());
			//	dateCreated = DateUtils.dateStrFromCalendar("MM/dd/yy",dateCreatedCal);
				}
			
			rollbackItemList.add(new RollbackItemDTO(eid, sampleId, subjectId, researcherSampleId,  researcherSubjectId, dateCreated, null, null, null));
			}
		
		System.out.println("Gathered info on " + (rollbackItemList == null ? 0 : rollbackItemList.size()));
		
		return rollbackItemList;
		}
	}



////////////////////////////////////////////////////
// RollbackItemService.java
// Written by Jan Wigginton, Jun 5, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.layers.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import edu.umich.brcf.shared.layers.dao.RollbackItemDAO;
import edu.umich.brcf.shared.layers.dao.SampleDAO;
import edu.umich.brcf.shared.layers.domain.RollbackItem;
import edu.umich.brcf.shared.layers.dto.RollbackItemDTO;
import edu.umich.brcf.shared.util.SampleSheetIOException;
import edu.umich.brcf.shared.util.interfaces.ICheckinSampleItem;
import edu.umich.brcf.shared.util.interfaces.ISavableSampleData;



@Transactional
public class RollbackItemService 
	{
	RollbackItemDAO rollbackItemDao;
	SampleDAO sampleDao;
	
	
	public void createRollbackItem(RollbackItem item) 
		{
		rollbackItemDao.createRollbackItem(item);
		}

		
	public RollbackItem loadBySampleId(String id) 
		{
		return rollbackItemDao.loadBySampleId(id);
		}
	
	
	public void deleteRollbackItem(RollbackItem item) 
		{
		rollbackItemDao.deleteRollbackItem(item);
		}
	
	
	public List<RollbackItem> loadForSampleIds(List<String> sampleIds)
		{
		return rollbackItemDao.loadForSampleIds(sampleIds);
		}
	
	
	public Map<String, RollbackItem> loadRetrievalMapForExpId(String expId)
		{
		List<RollbackItem> items = loadForExpId(expId);
		
		Map<String, RollbackItem> map  = new HashMap<String, RollbackItem>();
		for (RollbackItem item : items)
			{
			// no redundant researcher ids allowed
			if (map.get(item.getResearcherSampleId()) != null)
				return null;
			map.put(item.getResearcherSampleId(), item);
			}
		
		return map;
		}
	
	
	public List<RollbackItem> loadForExpId(String expId)
		{
		return rollbackItemDao.loadForExpId(expId);
		}
	
	
	public List<RollbackItemDTO> loadRollbackInfoForLogging(String expId)
		{
		return rollbackItemDao.loadRollbackInfoForLogging(expId, null);
		}
	
	// ignoreRollbacks (for epi) , just grab old sample ids.
    // JAK deleted UpdateDataFromRollbackIDs for successful mvn build issue 19
	
	public List<RollbackItemDTO> gatherInfoForLogging(String expId)
		{
		
		List<RollbackItem> loggedItems = loadForExpId(expId);
		Map<String, String> alreadyLoggedItemIds = new HashMap<String, String>();
		
		for (RollbackItem item : loggedItems)
			alreadyLoggedItemIds.put(item.getSampleId(), null);
	
		List<RollbackItemDTO> remainingItemsToLog = rollbackItemDao.loadRollbackInfoForLogging(expId, alreadyLoggedItemIds);
		
		return remainingItemsToLog;
		}
	
	
	public int createOrUpdateByExpId(String expId, List<RollbackItemDTO> newItemsToLog)
		{
		List<RollbackItem> loggedItems = loadForExpId(expId);
		
		for (RollbackItem item : loggedItems)
			item.setLastRollbackDate(Calendar.getInstance());
		
		int nNewItems = 0;
		for (RollbackItemDTO dto : newItemsToLog)
			{
			dto.setLastRollbackDate(Calendar.getInstance());
			RollbackItem item = RollbackItem.instance(dto); 
			rollbackItemDao.createRollbackItem(item);
		//	System.out.println("Logged " + item);
	
			nNewItems++;
			}
		return nNewItems;
		}
	
	
	public RollbackItemDAO getRollbackItemDao()
		{
		return rollbackItemDao;
		}
	

	public void setRollbackItemDao(RollbackItemDAO rollbackItemDao)
		{
		this.rollbackItemDao = rollbackItemDao;
		}


	public SampleDAO getSampleDao()
		{
		return sampleDao;
		}


	public void setSampleDao(SampleDAO sampleDao)
		{
		this.sampleDao = sampleDao;
		}
	}

///////////////////////////////////////
// GeneratedWorklistService.java
// Written by Jan Wigginton July 2015
///////////////////////////////////////

package edu.umich.brcf.metabolomics.layers.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.metabolomics.layers.dao.GeneratedWorklistDAO;
import edu.umich.brcf.metabolomics.layers.dao.GeneratedWorklistItemDAO;
import edu.umich.brcf.metabolomics.layers.domain.GeneratedWorklist;
import edu.umich.brcf.metabolomics.layers.domain.GeneratedWorklistItem;
import edu.umich.brcf.metabolomics.layers.dto.GeneratedWorklistDTO;
import edu.umich.brcf.metabolomics.layers.dto.GeneratedWorklistItemDTO;



@Transactional
public class GeneratedWorklistService 
	{
	GeneratedWorklistDAO generatedWorklistDao;
	GeneratedWorklistItemDAO generatedWorklistItemDao;
	
	
	public GeneratedWorklist save(GeneratedWorklistDTO dto, List<GeneratedWorklistItemDTO> itemDtos)
		{
		Assert.notNull(dto);
		GeneratedWorklist worklist = null;
		
		if (dto.getWorklistId() != null)
			try
				{
				worklist = generatedWorklistDao.loadById(dto.getWorklistId());
				worklist.update(dto);
				}
			catch(Exception e) { e.printStackTrace(); return null; }
		else 
			try
				{
				worklist = GeneratedWorklist.instance(dto);
				generatedWorklistDao.createGeneratedWorklist(worklist);
				}
			catch(Exception e) { e.printStackTrace(); return null; }
		
		
		for (GeneratedWorklistItemDTO itemDto : itemDtos)
			{
			Assert.notNull(itemDto);
			
			GeneratedWorklistItem item = null;
		
			if (itemDto.getItemId() != null)
				try
					{
					item = generatedWorklistItemDao.loadById(itemDto.getItemId());
					item.update(itemDto, worklist);
					}
				catch(Exception e) { e.printStackTrace(); worklist = null; }
			
			else
				try
					{
					item = GeneratedWorklistItem.instance(itemDto.getFileName(), itemDto.getSampleName(), itemDto.getSampleOrControlId(),
							itemDto.getComments(), itemDto.getInjVolume(), itemDto.getPlatePos(), itemDto.getPlateCode(), worklist);
					
					generatedWorklistItemDao.createGeneratedWorklistItem(item);
					worklist.getItems().add(item);
					}
				catch(Exception e) { e.printStackTrace(); worklist = null; }
			}
	
		return worklist;
		}
	
	
	
	public List <GeneratedWorklist> loadAll()
		{
		return this.generatedWorklistDao.loadAll();
		}
	
	public List <GeneratedWorklist> loadByExpId(String id)
		{
		return generatedWorklistDao.loadByExpId(id);
		}
	
	public List <GeneratedWorklist> loadByExpIdAndAssayId(String expId, String assayId)
		{
		return generatedWorklistDao.loadByExpIdAndAssayId(expId, assayId);
		}
	
	public List<String> loadRunDatesByExpIdAndAssayId(String expId, String assayId)
		{
		List <GeneratedWorklist> lists = loadByExpIdAndAssayId(expId, assayId);
		
		List <String> runDates = new ArrayList<String>();
		
		Map<String, String> allDates = new HashMap<String, String>();
		
		for (GeneratedWorklist list : lists)
			allDates.put(list.getDateGeneratedAsStr("MM/dd/yyyy"), "");
		
		for (String key : allDates.keySet())
			runDates.add(key);
		
		return runDates;
		}
	
	public void updateItems(List <GeneratedWorklistItemDTO> itemDtos)
		{
		for (int i = 0; i < itemDtos.size(); i++)
			{
			GeneratedWorklistItem item;
			GeneratedWorklistItemDTO itemDto = itemDtos.get(i);
			
			Assert.notNull(itemDto);
			GeneratedWorklist worklist = generatedWorklistDao.loadById(itemDto.getWorklistId());
			
			try
				{
				item = generatedWorklistItemDao.loadById(itemDto.getItemId());
				item.update(itemDto, worklist);
				}
			catch(Exception e)
				{
				item = GeneratedWorklistItem.instance(itemDto.getFileName(), itemDto.getSampleName(), itemDto.getSampleOrControlId(),
						itemDto.getComments(), itemDto.getInjVolume(), itemDto.getPlatePos(), itemDto.getPlateCode(), worklist);
				
				generatedWorklistItemDao.createGeneratedWorklistItem(item);
				}	
			}
		}
	
	
	///  Getters/Setters//////////////////////////////////////////////
	public GeneratedWorklistDAO getGeneratedWorklistDao() 
		{
		return generatedWorklistDao;
		}

	public void setGeneratedWorklistDao(GeneratedWorklistDAO generatedWorklistDao) 
		{
		this.generatedWorklistDao = generatedWorklistDao;
		}

	public GeneratedWorklistItemDAO getGeneratedWorklistItemDao() {
		return generatedWorklistItemDao;
	}

	public void setGeneratedWorklistItemDao(
			GeneratedWorklistItemDAO generatedWorklistItemDao) {
		this.generatedWorklistItemDao = generatedWorklistItemDao;
	}
	
	
	}

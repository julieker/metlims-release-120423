///////////////////////////////////////
// GeneratedWorklistItemService.java
// Written by Jan Wigginton July 2015
///////////////////////////////////////
package edu.umich.brcf.metabolomics.layers.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import edu.umich.brcf.metabolomics.layers.dao.GeneratedWorklistItemDAO;
import edu.umich.brcf.metabolomics.layers.domain.GeneratedWorklistItem;


@Transactional
public class GeneratedWorklistItemService 
	{
	GeneratedWorklistItemDAO generatedWorklistItemDao;

	public GeneratedWorklistItemDAO getGeneratedWorklistItemDao() 
		{
		return generatedWorklistItemDao;
		}

	public void setGeneratedWorklistItemDao(GeneratedWorklistItemDAO generatedWorklistItemDao) 
		{
		this.generatedWorklistItemDao = generatedWorklistItemDao;
		}
	
	public List <GeneratedWorklistItem> loadByWorklistId(String worklistId)
		{
		return generatedWorklistItemDao.loadByWorklistId(worklistId);
		}
	
	public List <GeneratedWorklistItem> loadCommentedByWorklistId(String worklistId)
		{
		return generatedWorklistItemDao.loadCommentedByWorklistId(worklistId);
		}
	}
	

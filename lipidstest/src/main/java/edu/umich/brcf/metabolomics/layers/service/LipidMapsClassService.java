///////////////////////////////////////
// LipidMapsClassService.java
// Written by Jan Wigginton May 2015
///////////////////////////////////////


package edu.umich.brcf.metabolomics.layers.service;

import org.springframework.util.Assert;

import edu.umich.brcf.metabolomics.layers.dao.LipidMapsClassDAO;
import edu.umich.brcf.metabolomics.layers.domain.LipidMapsClass;


public class LipidMapsClassService
	{
	private LipidMapsClassDAO lipidMapsClassDao;
	
	public LipidMapsClass loadById(String id)
		{
		Assert.notNull(id);
		return lipidMapsClassDao.loadById(id);
		}

	public LipidMapsClassDAO getLipidMapsClassDao()
		{
		return lipidMapsClassDao;
		}

	public void setLipidMapsClassDao(LipidMapsClassDAO lipidMapsClassDao)
		{
		this.lipidMapsClassDao = lipidMapsClassDao;
		}
	}

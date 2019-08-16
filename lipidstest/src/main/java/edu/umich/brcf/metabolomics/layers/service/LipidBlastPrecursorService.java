///////////////////////////////////////
// LipidBlastPrecursorService.java
// Written by Jan Wigginton May 2015
///////////////////////////////////////


package edu.umich.brcf.metabolomics.layers.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.metabolomics.layers.dao.LipidBlastPrecursorDAO;
import edu.umich.brcf.metabolomics.layers.domain.LipidBlastPrecursor;


@Transactional
@Service
public class LipidBlastPrecursorService
	{
	private LipidBlastPrecursorDAO lipidBlastPrecursorDao;
	
	public LipidBlastPrecursor loadById(String id)
		{
		Assert.notNull(id);
		return lipidBlastPrecursorDao.loadById(id);
		}

	public LipidBlastPrecursorDAO getLipidBlastPrecursorDao()
		{
		return lipidBlastPrecursorDao;
		}

	public void setLipidBlastPrecursorDao(LipidBlastPrecursorDAO lipidBlastPrecursorDao)
		{
		this.lipidBlastPrecursorDao = lipidBlastPrecursorDao;
		}
	
	public List <LipidBlastPrecursor> getLipidInfoRelatedTo(String codeName, String adduct)
		{
		return lipidBlastPrecursorDao.getLipidInfoRelatedTo(codeName, adduct);
		}
	}

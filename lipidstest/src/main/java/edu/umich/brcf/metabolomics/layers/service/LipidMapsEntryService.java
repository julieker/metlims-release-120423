// LipidMapsEntryService.java
// Written by Jan Wigginton May 2015

package edu.umich.brcf.metabolomics.layers.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.metabolomics.layers.dao.LipidMapsEntryDAO;
import edu.umich.brcf.metabolomics.layers.domain.LipidMapsEntry;
import edu.umich.brcf.shared.util.interfaces.IWriteConvertable;


@Transactional
@Service
public class LipidMapsEntryService
	{
	private LipidMapsEntryDAO lipidMapsEntryDao;
	
	public LipidMapsEntry loadById(String id)
		{
		Assert.notNull(id);
		return lipidMapsEntryDao.loadById(id);
		}

	public LipidMapsEntryDAO getLipidMapsEntryDao()
		{
		return lipidMapsEntryDao;
		}

	public void setLipidMapsEntryDao(LipidMapsEntryDAO lipidMapsEntryDao)
		{
		this.lipidMapsEntryDao = lipidMapsEntryDao;
		}
	
	
	public List <LipidMapsEntry> loadAllForSubClass(String subClass)
		{
		return lipidMapsEntryDao.loadAllForSubClass(subClass);
		}
	
	public List <IWriteConvertable> loadAllForSubClassAsWriteable(String subClass)
		{
		return lipidMapsEntryDao.loadAllForSubClassAsWriteable(subClass);
		}
	}

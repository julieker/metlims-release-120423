// ControlTypeService.java
// Written by Jan Wigginton May 2015

package edu.umich.brcf.shared.layers.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.shared.layers.dao.ControlTypeDAO;


@Transactional
public class ControlTypeService
	{
	ControlTypeDAO controlTypeDao;

	public List<String> allControlTypeIds()
		{
		return controlTypeDao.allControlTypeIds();
		}

	public List<String> allControlTypeIdsForAgilent()
		{
		return controlTypeDao.allControlTypeIdsForPlatformId("PL001");
		}

	public List<String> allControlTypeIdsForAbsciex()
		{
		return controlTypeDao.allControlTypeIdsForPlatformId("PL002");
		}

	public List<String> allControlTypeNames()
		{
		return controlTypeDao.allControlTypeNames();
		}

	public List<String> allControlTypeNamesForAgilent()
		{
		return controlTypeDao.allControlTypeNamesForPlatformId("PL001");
		}

	public List<String> allControlTypeNamesForAbsciex()
		{
		return controlTypeDao.allControlTypeNamesForPlatformId("PL002");
		}

	public void setControlTypeDao(ControlTypeDAO controlTypeDao)
		{
		Assert.notNull(controlTypeDao);
		this.controlTypeDao = controlTypeDao;
		}
	}

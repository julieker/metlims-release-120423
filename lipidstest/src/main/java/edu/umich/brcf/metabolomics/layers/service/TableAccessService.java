package edu.umich.brcf.metabolomics.layers.service;

import java.util.List;

import edu.umich.brcf.metabolomics.layers.dao.TableAccessDAO;


public class TableAccessService
	{
	TableAccessDAO tableAccessDao;

	public TableAccessDAO getTableAccessDao()
		{
		return tableAccessDao;
		}

	public List<String> getVolumeUnits()
		{
		return tableAccessDao.getVolumeUnits();
		}


	public void setTableAccessDao(TableAccessDAO tableAccessDao)
		{
		this.tableAccessDao = tableAccessDao;
		}

	}

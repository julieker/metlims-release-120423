package edu.umich.brcf.shared.layers.service;

import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import edu.umich.brcf.shared.layers.dao.SystemConfigDAO;


//@Service
@Transactional
public class SystemConfigService
	{
	private SystemConfigDAO systemConfigDao;

	public Map<String, Object> getSystemConfigMap()
		{
		return systemConfigDao.getSystemConfigurationMap();
		}

	public String getDefaultAliquotVolume()
		{
		return systemConfigDao.getDefaultAliquotVolume();
		}

	public String getDefaultAliquotVolumeUnits()
		{
		return systemConfigDao.getDefaultAliquotVolumeUnits();
		}

	public String getDefaultAliquotLocation()
		{
		return systemConfigDao.getDefaultAliquotLocation();
		}

	public String getUploadFolder()
		{
		return systemConfigDao.getUploadFolder();
		}

	public String getProcessedFilesFolder()
		{
		return systemConfigDao.getProcessedFilesFolder();
		}

	public void setSystemConfigDao(SystemConfigDAO systemConfigDao)
		{
		this.systemConfigDao = systemConfigDao;
		}

	public String getGrobMixId()
		{
		return systemConfigDao.getGrobMixId();
		}

	public String getStandardMatrixSampleId()
		{
		return systemConfigDao.getStandardMatrixSampleId();
		}

	public Integer getNumberOfNumberStandardMatrixSamplesPerRun()
		{
		return systemConfigDao.getNumberStandardMatrixSamplePerRun();
		}

	public String getStandardBlankSampleId()
		{
		return systemConfigDao.getBlankBlankSampleId();
		}

	public String getSolventBlankSampleId()
		{
		return systemConfigDao.getBlankSolventSampleId();
		}

	public String getProcessBlankSampleId()
		{
		return systemConfigDao.getBlankProcessSampleId();
		}

	public String getGrobPrepID()
		{
		return systemConfigDao.getGrobPrepID();
		}

	public String getWarmUpPrepID()
		{
		return systemConfigDao.getWarmUpPrepID();
		}

	public String getDefaultSampleRunMode_LC()
		{
		return systemConfigDao.getDefaultSampleRunMode_LC();
		}

	public String getDefaultSampleRunMode_GC()
		{
		return systemConfigDao.getDefaultSampleRunMode_GC();
		}

	public Integer getNumberOfBlanksPerRun()
		{
		return systemConfigDao.getNumberBlankSamplePerRun();
		}

	public String getSystemServerQueue()
		{
		return systemConfigDao.getServerQueue();
		}

	public String getClientMessageQueue()
		{
		return systemConfigDao.getClientMessageQueue();
		}

	public String getJmsBrokerUrl()
		{
		return systemConfigDao.getJmsBrokerUrl();
		}

	public String getProductionServerName()
		{
		return systemConfigDao.getProductionServerName();
		}

	// public void setInstrumentService(InstrumentService instrumentService) {
	// this.instrumentService = instrumentService;
	}

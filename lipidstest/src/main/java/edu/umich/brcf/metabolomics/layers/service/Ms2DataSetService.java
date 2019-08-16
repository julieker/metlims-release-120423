package edu.umich.brcf.metabolomics.layers.service;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Ms2DataSetService.java
//Written by Jan Wigginton 04/29/15
//Rewritten 06/21/15 -- consolidated dataset entire save to a transaction + incorporated sampleMapId
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

import java.util.Calendar;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.metabolomics.layers.dao.Ms2DataSetDAO;
import edu.umich.brcf.metabolomics.layers.dao.Ms2PeakDAO;
import edu.umich.brcf.metabolomics.layers.dao.Ms2PeakSetDAO;
import edu.umich.brcf.metabolomics.layers.dao.Ms2SampleMapDAO;
import edu.umich.brcf.metabolomics.layers.domain.Ms2DataSet;
import edu.umich.brcf.metabolomics.layers.domain.Ms2Peak;
import edu.umich.brcf.metabolomics.layers.domain.Ms2PeakSet;
import edu.umich.brcf.metabolomics.layers.domain.Ms2SampleMap;
import edu.umich.brcf.metabolomics.layers.dto.Ms2DataSetDTO;
import edu.umich.brcf.metabolomics.layers.dto.Ms2PeakDTO;
import edu.umich.brcf.metabolomics.layers.dto.Ms2PeakSetDTO;
import edu.umich.brcf.metabolomics.layers.dto.Ms2SampleMapDTO;



@Transactional
@Service
public class Ms2DataSetService
	{
	Ms2DataSetDAO ms2DataSetDao;
	Ms2PeakSetDAO ms2PeakSetDao;
	Ms2PeakDAO ms2PeakDao;
	Ms2SampleMapDAO ms2SampleMapDao;

	public Ms2DataSet loadById(String id)
		{
		Assert.notNull(id);
		return ms2DataSetDao.loadById(id);
		}
	

	public Ms2DataSet save(Ms2DataSetDTO dto)
		{
		Assert.notNull(dto);

		Ms2DataSet dataSet = null;
		if (dto.getDataNotation() != null)
			try
				{
				dataSet = ms2DataSetDao.loadById(dto.getDataSetId());
				dataSet.update(dto);
				} 
			catch (Exception e) { e.printStackTrace(); dataSet = null; }
		else
			try
				{
				dataSet = Ms2DataSet.instance(dto.getExpId(), dto.getRunDate(), dto.getUploadDate(), dto.getReplicate(),
						dto.getUploadedBy(), dto.getIonMode(), dto.getDataNotation());
	
				ms2DataSetDao.createMs2DataSet(dataSet);
				}
			catch (Exception e) { e.printStackTrace(); dataSet = null; }

		return dataSet;
		}

	
	
	public Ms2DataSet saveDataSet(Ms2DataSetDTO dto, List<Ms2PeakSetDTO> psDtos, List<List<Ms2PeakDTO>> pDtos, List<Ms2SampleMapDTO> smDtos)
		{
		Assert.notNull(dto);

		Ms2DataSet dataSet = null;
	
		if (dataSet.getDataSetId() != null)
			
			try
				{
				dataSet = ms2DataSetDao.loadById(dto.getDataSetId());
				dataSet.update(dto);
				} 
			catch (Exception e) { e.printStackTrace(); dataSet = null;  }
		else
			try
				{
				dataSet = Ms2DataSet.instance(dto.getExpId(), dto.getRunDate(), dto.getUploadDate(), dto.getReplicate(),
						dto.getUploadedBy(), dto.getIonMode(), dto.getDataNotation());
	
				ms2DataSetDao.createMs2DataSet(dataSet);
				}
			catch (Exception e) { e.printStackTrace(); dataSet = null; }
		

		for (Ms2SampleMapDTO smDto : smDtos)
			{
			Ms2SampleMap ms2SampleMap = null;
			
			if (smDto.getSampleMapId() != null)
				try
					{
					ms2SampleMap = ms2SampleMapDao.loadById(smDto.getSampleMapId());
					ms2SampleMap.update(smDto);
					} 
				catch (Exception e) {  e.printStackTrace(); return null;}
			
				
			else
				try
					{
					ms2SampleMap = Ms2SampleMap.instance(smDto.getSampleMapId(), smDto.getSampleId(), smDto.getSampleTag(),
						dataSet.getDataSetId(), smDto.getRunOrderIdx(), smDto.getOtherId(), smDto.getComment());

					ms2SampleMapDao.createMs2SampleMap(ms2SampleMap);
					smDto.setSampleMapId(ms2SampleMap.getSampleMapId());
					}
				catch (Exception e) {  e.printStackTrace(); return null;}
			}
			

		
		for (int i = 0; i < psDtos.size(); i++)
			{
			Ms2PeakSet peakSet;
			Ms2PeakSetDTO psDto = psDtos.get(i);
			
			if (psDto.getPeakSetId() != null)
				try {peakSet = ms2PeakSetDao.loadById(psDto.getPeakSetId()); }
				catch (Exception e) {  e.printStackTrace(); return null;}
			else
				try
					{
					peakSet = Ms2PeakSet.instance(psDto.getLipidName(), psDto.getExpectedRt(), psDto.getStartMass(),
							psDto.getEndMass(), psDto.getLipidClass(), psDto.getKnownStatus(), dataSet);
	
					ms2PeakSetDao.createMs2PeakSet(peakSet);
					}
				catch (Exception e) { e.printStackTrace(); return null; }
			
	
			for (int j = 0; j < pDtos.get(i).size(); j++)
				{
				Ms2Peak peak;
				Ms2PeakDTO pDto = pDtos.get(i).get(j);
	
				if (pDto.getPeakId() != null)
					
					try { peak = ms2PeakDao.loadById(pDto.getPeakId());} 
					catch (Exception e) {  e.printStackTrace(); return null;}
				else
					try
						{
						peak = Ms2Peak.instance(peakSet, smDtos.get(j).getSampleMapId(), pDto.getPeakArea());
						ms2PeakDao.createMs2Peak(peak);
						}
				catch (Exception e) {  e.printStackTrace(); return null;}
				
					}
			}

		return dataSet;
		}



	public Ms2DataSetDAO getMs2DataSetDao()
		{
		return ms2DataSetDao;
		}

	public void setMs2DataSetDao(Ms2DataSetDAO ms2DataSetDao)
		{
		this.ms2DataSetDao = ms2DataSetDao;
		}

	public Ms2PeakSetDAO getMs2PeakSetDao()
		{
		return ms2PeakSetDao;
		}

	public void setMs2PeakSetDao(Ms2PeakSetDAO ms2PeakSetDao)
		{
		this.ms2PeakSetDao = ms2PeakSetDao;
		}

	public Ms2PeakDAO getMs2PeakDao()
		{
		return ms2PeakDao;
		}

	public void setMs2PeakDao(Ms2PeakDAO ms2PeakDao)
		{
		this.ms2PeakDao = ms2PeakDao;
		}

	public Ms2SampleMapDAO getMs2SampleMap()
		{
		return ms2SampleMapDao;
		}

	public void setMs2SampleMapDao(Ms2SampleMapDAO ms2SampleMapDao)
		{
		this.ms2SampleMapDao = ms2SampleMapDao;
		}

	public List<Ms2DataSet> loadByExpId(String expID)
		{
		return ms2DataSetDao.loadByExpId(expID);
		}

	public List<Ms2DataSet> loadForRunDateRange(Calendar fromDateCalendar,Calendar toDateCalendar)
		{
		return ms2DataSetDao.loadForRunDateRange(fromDateCalendar, toDateCalendar);
		}

	public List<Ms2DataSet> loadForUploadDateRange(Calendar fromDateCalendar,Calendar toDateCalendar)
		{
		return ms2DataSetDao.loadForUploadDateRange(fromDateCalendar, toDateCalendar);
		}

	public List<Ms2DataSet> loadAll()
		{
		return ms2DataSetDao.loadAll();
		}
	}

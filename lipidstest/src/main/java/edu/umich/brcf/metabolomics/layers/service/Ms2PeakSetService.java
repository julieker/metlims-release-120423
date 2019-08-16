///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//MS2PeakSetService.java
//Written by Jan Wigginton 04/10/15
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.layers.service;


import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.metabolomics.layers.dao.Ms2DataSetDAO;
import edu.umich.brcf.metabolomics.layers.dao.Ms2PeakDAO;
import edu.umich.brcf.metabolomics.layers.dao.Ms2PeakSetDAO;
import edu.umich.brcf.metabolomics.layers.domain.Ms2DataSet;
import edu.umich.brcf.metabolomics.layers.domain.Ms2Peak;
import edu.umich.brcf.metabolomics.layers.domain.Ms2PeakSet;
import edu.umich.brcf.metabolomics.layers.dto.Ms2PeakDTO;
import edu.umich.brcf.metabolomics.layers.dto.Ms2PeakSetDTO;

@Transactional
@Service
public class Ms2PeakSetService
	{
	Ms2PeakSetDAO ms2PeakSetDao;
	Ms2PeakDAO ms2PeakDao;
	Ms2DataSetDAO ms2DataSetDao;

	public Ms2PeakSet loadById(String id)
		{
		Assert.notNull(id);
		return ms2PeakSetDao.loadById(id);
		}

	public Ms2PeakSet save(Ms2PeakSetDTO dto)
		{
		Assert.notNull(dto);

		Ms2PeakSet peakSet = null;
		Ms2DataSet dataSet = ms2DataSetDao.loadById(dto.getDataSetId());
		
		if (dto.getPeakSetId() == null)
			try
				{
				peakSet = ms2PeakSetDao.loadById(dto.getPeakSetId());
				peakSet.update(dto, dataSet);
				}
			catch (Exception e)
				{
				e.printStackTrace();
				peakSet = null;
				}
		else
			try
				{
				peakSet = Ms2PeakSet.instance(dto.getLipidName(),
						dto.getExpectedRt(), dto.getStartMass(), dto.getEndMass(),
						dto.getLipidClass(), dto.getKnownStatus(), dataSet);
				
				ms2PeakSetDao.createMs2PeakSet(peakSet);
				}
			catch (Exception e)
				{
				e.printStackTrace();
				peakSet = null;
				}
	
		return peakSet;
		}
	

	public List<Ms2PeakSet> savePeakSets(List<Ms2PeakSetDTO> dtos, String dataSetId)
		{
		List<Ms2PeakSet> peaks = new ArrayList<Ms2PeakSet>();

		Ms2DataSet dataSet = ms2DataSetDao.loadById(dataSetId);

		for (Ms2PeakSetDTO dto : dtos)
			{
			Assert.notNull(dto);
			
			Ms2PeakSet peakSet = null;
				
			if (dto.getPeakSetId() != null)
				try
					{
					peakSet = ms2PeakSetDao.loadById(dto.getPeakSetId());
					peakSet.update(dto, dataSet);
					}
				catch (Exception e) { e.printStackTrace(); return null; }
					
			else
				try
					{
					peakSet = Ms2PeakSet.instance(dto.getLipidName(), dto.getExpectedRt(), dto.getStartMass(),
							dto.getEndMass(), dto.getLipidClass(), dto.getKnownStatus(), dataSet);
					
					ms2PeakSetDao.createMs2PeakSet(peakSet);
					}
				catch (Exception e) { e.printStackTrace(); return null; }

			
			peaks.add(peakSet);
			}
			
		return peaks;
		}
	

	public Ms2PeakSet savePeaks(List<Ms2PeakDTO> dtos, String peakSetId)
		{
		Ms2PeakSet peakSet;
		
		try { peakSet = ms2PeakSetDao.loadById(peakSetId); }
		catch (Exception e) { e.printStackTrace(); return null; }
			
		for (Ms2PeakDTO dto : dtos)
			{
			Assert.notNull(dto);
			Ms2Peak peak = null;

			if (dto.getPeakId() != null)
				try
					{
					peak = ms2PeakDao.loadById(dto.getPeakId());
					peak.update(dto, peakSet);
					}
				catch (Exception e) { e.printStackTrace(); return null; }
			else	
				try
					{
					peak = Ms2Peak.instance(peakSet, dto.getSampleMapId(), dto.getPeakArea());
					ms2PeakDao.createMs2Peak(peak);
					}
				catch (Exception e) { e.printStackTrace(); return null; } 

			peakSet.getSamplePeaks().add(peak);
			}

		return peakSet;
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

	List<Ms2PeakSet> loadForExpIdAndDate(String expId, String rDate)
		{
		return new ArrayList<Ms2PeakSet>(); // ms2PeakSetDao.loadForExpIdAndDate(expId,
											// rDate);
		}

	public Ms2DataSetDAO getMs2DataSetDao()
		{
		return ms2DataSetDao;
		}

	public void setMs2DataSetDao(Ms2DataSetDAO ms2DataSetDao)
		{
		this.ms2DataSetDao = ms2DataSetDao;
		}

	public List<Ms2PeakSet> loadInitializedForDataSetId(String dataSetId)
		{
		return ms2PeakSetDao.loadInitializedForDataSetId(dataSetId);
		}

	public String getLipidDescriptor(String name)
		{
		return ms2PeakSetDao.getLipidDescriptor(name);
		}
	}

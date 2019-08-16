/////////////////////////////////////////
//Ms2PeakService.java
//Written by Jan Wigginton May 2015
/////////////////////////////////////////

package edu.umich.brcf.metabolomics.layers.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.metabolomics.layers.dao.Ms2PeakDAO;
import edu.umich.brcf.metabolomics.layers.dao.Ms2PeakSetDAO;
import edu.umich.brcf.metabolomics.layers.domain.Ms2Peak;
import edu.umich.brcf.metabolomics.layers.domain.Ms2PeakSet;
import edu.umich.brcf.metabolomics.layers.dto.Ms2PeakDTO;



@Transactional
@Service
public class Ms2PeakService 
	{
	Ms2PeakDAO ms2PeakDao;
	Ms2PeakSetDAO ms2PeakSetDao;
	
	
	public Ms2Peak save(Ms2PeakDTO dto)
		{
		Assert.notNull(dto);
		
		Ms2PeakSet peakSet = ms2PeakSetDao.loadById(dto.getPeakSetId());
		Ms2Peak peak = null;
		
		if (dto.getPeakId() != null)
			try
				{
				peak = ms2PeakDao.loadById(dto.getPeakId());
				peak.update(dto, peakSet);
				}
			catch (Exception e) {  e.printStackTrace(); return null;}
		else
			try
				{
				Double pa = dto.getPeakArea();
				peak = Ms2Peak.instance(peakSet, dto.getSampleMapId(), pa == Double.NaN ? null : dto.getPeakArea());
				ms2PeakDao.createMs2Peak(peak);
				}
			catch (Exception e) {  e.printStackTrace(); return null;}
			
		return peak;
		}
	
	
	public Ms2Peak loadById(Long id)
		{
		return ms2PeakDao.loadById(id);
		}
	
	public Ms2PeakDAO getMs2PeakDao()
		{
		return this.ms2PeakDao;
		}
	
	public void setMs2PeakDao(Ms2PeakDAO ms2PeakDao) 
		{
		this.ms2PeakDao = ms2PeakDao;
		}
	
	
	public Ms2PeakSetDAO getMs2PeakSetDao()
		{
		return this.ms2PeakSetDao;
		}
		
	
	public void setMs2PeakSetDao(Ms2PeakSetDAO ms2PeakSetDao) 
		{
		this.ms2PeakSetDao = ms2PeakSetDao;
		}
		
	public List<Ms2Peak> loadForPeakSetId(String peakSetId)
		{
		return ms2PeakDao.loadInitializedForPeakSetId(peakSetId); //loadForPeakSetId(peakSetId);
		}
	}



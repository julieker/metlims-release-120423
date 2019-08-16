/////////////////////////////////////////
//SampleLocationService.java
//Written by Jan Wigginton May 2015
/////////////////////////////////////////

package edu.umich.brcf.shared.layers.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.shared.layers.dao.SampleDAO;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.dao.SampleLocationDAO;
import edu.umich.brcf.shared.layers.domain.SampleLocation;
import edu.umich.brcf.shared.layers.dto.SampleLocationDTO;
import edu.umich.brcf.shared.util.utilpackages.ListUtils;



@Transactional
public class SampleLocationService 
	{
	private SampleLocationDAO sampleLocationDao;
	private SampleDAO sampleDao;
	
	
	public void createSampleLocation(SampleLocation sampleLocation) 
		{
		sampleLocationDao.createSampleLocation(sampleLocation);
		}
	
	public SampleLocation loadSampleLocationById(String sampleLocationId) 
		{
		return sampleLocationDao.loadSampleLocationById(sampleLocationId);
		}
	
	public List<SampleLocation> getLocationHistoryForSample(String sampleId)
		{
		List<SampleLocation> lst = sampleLocationDao.getLocationHistoryForSampleId(sampleId);
		
		// Handle/update old entries without a recorded history on first access.
		if (!ListUtils.isNonEmpty(lst))
			{
			Sample s = sampleDao.loadSampleById(sampleId);
			SampleLocation loc = new SampleLocation("", sampleId, "-", s.getLocID(), s.getDateCreated(), "");
			SampleLocationDTO dto = SampleLocationDTO.instance(loc);
			logUpdate(dto);
			lst.add(loc);
			}
		
		return lst;
		}
	
	
	public SampleLocation logUpdate(SampleLocationDTO dto)
		{
		Assert.notNull(dto);
		SampleLocation sampleLocation;
		
		try { sampleLocation = sampleLocationDao.loadById(dto.getSampleLocationId());  } 
		catch (Exception e) 
			{
			sampleLocation = SampleLocation.instance(dto.getSampleId(), dto.getOldLocationId(), dto.getLocationId(), dto.getUpdateDate(), dto.getUpdatedBy());
			sampleLocationDao.createSampleLocation(sampleLocation);
			}
		
		return sampleLocation;
		}
		

	public int logUpdates(List<SampleLocationDTO> sampleLocations) 
		{
		int sampleLocationCount = 0;
		SampleLocation sampleLocation = null;
		for (SampleLocationDTO sl : sampleLocations) 
			{
			sampleLocation = logUpdate(sl);
			++sampleLocationCount;
			}
		
		return sampleLocationCount;
		}

	
	public SampleLocationDAO getSampleLocationDao()  { return sampleLocationDao; }
	public void setSampleLocationDao(SampleLocationDAO sampleLocationDao)  { this.sampleLocationDao = sampleLocationDao; }
	public  SampleDAO getSampleDao() {  return sampleDao; }
	public void setSampleDao( SampleDAO sampleDao) { this.sampleDao = sampleDao; }
	}


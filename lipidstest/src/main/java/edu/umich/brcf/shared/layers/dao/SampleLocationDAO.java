////////////////////////////////////
//SampleLocationDAO
//Written by Jan Wigginton May 2015
///////////////////////////////////

package edu.umich.brcf.shared.layers.dao;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import edu.umich.brcf.shared.layers.domain.SampleLocation;


@Repository
public class SampleLocationDAO extends BaseDAO
	{
	public SampleLocation loadById(String sampleLocationId)
		{
		Assert.notNull(sampleLocationId);
		Boolean isEmpty = (sampleLocationId.trim().length() == 0);
		Assert.isTrue(!isEmpty);
		return (getEntityManager().find(SampleLocation.class, sampleLocationId));
		}

	
	public List<SampleLocation> getLocationHistoryForSampleId(String sampleId)
		{
		List<SampleLocation> lst = getEntityManager().createQuery("from SampleLocation l where l.sampleId = :sampleId order by updateDate desc")
			.setParameter("sampleId", sampleId).getResultList();
		
		return lst;
		}
	
	//getLocationsForSamples

	public SampleLocation getSampleLocationById(String sampleLocationId)
		{
		return (getEntityManager().find(SampleLocation.class, sampleLocationId));
		}

	
	public void createSampleLocation(SampleLocation sampleLocation)
		{
		getEntityManager().persist(sampleLocation);
		}

	
	public SampleLocation loadSampleLocationById(String sampleLocationId)
		{
		return (getEntityManager().find(SampleLocation.class, sampleLocationId));
		}
	}

package edu.umich.brcf.shared.layers.dao;

import org.springframework.stereotype.Repository;

import edu.umich.brcf.shared.layers.domain.SampleAssayStatus;


@Repository
public class SampleAssayStatusDAO extends BaseDAO
	{
	public SampleAssayStatus loadById(Character ID)
		{
		SampleAssayStatus sampleAssayStatus = getEntityManager().find(SampleAssayStatus.class, ID);
		return sampleAssayStatus;
		}
	}

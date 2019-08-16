package edu.umich.brcf.shared.layers.dao;


import org.springframework.stereotype.Repository;

import edu.umich.brcf.shared.layers.dao.BaseDAO;
import edu.umich.brcf.shared.layers.domain.SampleStatus;

@Repository
public class SampleStatusDAO extends BaseDAO
	{
	public SampleStatus loadById(Character ID)
		{
		SampleStatus sampleStatus = getEntityManager().find( SampleStatus.class, ID);
		return sampleStatus;
		}
	}

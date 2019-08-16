///////////////////////////////////////
// TableAccessDAO.java
// Written by Jan Wigginton, March 2016
////////////////////////////////////////
package edu.umich.brcf.metabolomics.layers.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import edu.umich.brcf.shared.layers.dao.BaseDAO;


public class TableAccessDAO extends BaseDAO
	{
	public List<String> getVolumeUnits()
		{
		Query query = getEntityManager().createNativeQuery("select cast(units as VARCHAR2(2)) from volume_units");
		return query.getResultList();
		}
	
	
	public Map<String, String> getStatusMap()
		{
		Query query = getEntityManager().createNativeQuery("select cast(id as VARCHAR2(1)), cast(status_value as VARCHAR2(32)) from sample_assay_status");
		List<Object[]> resultList  = query.getResultList();
		
		Map<String, String> statusMap = new HashMap<String, String>();
		for (Object[] result : resultList)
			{
			if (result.length < 2)
				continue;
	
			String id = (String) result[0];
			String description = (String) result[1];
			statusMap.put(id, description);
			}
		return statusMap;
		}
	}

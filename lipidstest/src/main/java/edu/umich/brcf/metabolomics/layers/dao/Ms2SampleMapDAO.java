//Ms2SampleMapDAO.java
//Written by Jan Wigginton 04/29/15
package edu.umich.brcf.metabolomics.layers.dao;


import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import edu.umich.brcf.metabolomics.layers.domain.Ms2SampleMap;
import edu.umich.brcf.shared.layers.dao.BaseDAO;


@Repository
public class Ms2SampleMapDAO extends BaseDAO
	{
	
	public void createMs2SampleMap(Ms2SampleMap map)
		{
		getEntityManager().persist(map);
		//initializeTheKids(map, new String [] {"dataSet"});
		}

	
	public void deleteMs2SampleMap(Ms2SampleMap map) 
		{
		getEntityManager().remove(map);
		}
	
	
	public Ms2SampleMap loadById(String id)
		{
		Ms2SampleMap map =  getEntityManager().find(Ms2SampleMap.class, id);
		//initializeTheKids(map, new String [] {"dataSet"});
		return map;
		}

	
	public List <Ms2SampleMap> loadForDataSetId(String dataSetId)
		{
		List<Ms2SampleMap> sampleMap =  getEntityManager().createQuery("from Ms2SampleMap s where s.dataSetId = :dataSetId")
				.setParameter("dataSetId", dataSetId).getResultList();
		
		return sampleMap;
		}
	
	
	public Ms2SampleMap loadForDataSetIdAndSampleId(String dataSetId, String sampleId)
		{
		List<Ms2SampleMap> sampleMap =   getEntityManager().createQuery("from Ms2SampleMap s where s.dataSetId = :dataSetId and s.sampleId = :sampleId")
				.setParameter("dataSetId", dataSetId).setParameter("sampleId", sampleId).getResultList();
		
		if (sampleMap.size() == 1)
			return sampleMap.get(0);
		
		return null;
		}
	
	
	public List <String> loadSampleTagsForDataSetId(String dataSetId)
		{
		Query query = getEntityManager().createNativeQuery("select cast(sample_tag as CHAR(12)) from Ms2_Sample_Maps3 where "
				+ " data_set_id = ?1").setParameter(1, dataSetId);
	
		return query.getResultList();
		}
	}
	
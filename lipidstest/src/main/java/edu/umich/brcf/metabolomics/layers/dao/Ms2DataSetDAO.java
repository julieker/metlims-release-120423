//Ms2DataSetDAO.java
//Written by Jan Wigginton 04/29/15

package edu.umich.brcf.metabolomics.layers.dao;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import edu.umich.brcf.metabolomics.layers.domain.Ms2DataSet;
import edu.umich.brcf.shared.layers.dao.BaseDAO;



@Repository
public class Ms2DataSetDAO extends BaseDAO
	{
	public void createMs2DataSet(Ms2DataSet dataSet)
		{
		getEntityManager().persist(dataSet);
		
		initializeTheKids(dataSet, new String [] {"peakSets"});
		}
	
	
	public void deleteMs2DataSet(Ms2DataSet dataSet) 
		{
		getEntityManager().remove(dataSet);
		}
	
	
	public Ms2DataSet loadById(String id)
		{
		Ms2DataSet dataSet = getEntityManager().find(Ms2DataSet.class, id);
		
		initializeTheKids(dataSet, new String [] { "peakSets" });
		
		return dataSet;
		}
	
	
	public List<Ms2DataSet> loadAll()
		{
		List<Ms2DataSet> dataSetList = getEntityManager().createQuery("from Ms2DataSet order by dataSetId desc").getResultList();
		
		for( Ms2DataSet set : dataSetList)
			initializeTheKids(set, new String[]{"peakSets"});
		
		return dataSetList;
		}
	
	
	public List<Ms2DataSet> loadByExpId(String id)
		{
		List<Ms2DataSet> lst =  getEntityManager().createQuery("from Ms2DataSet d where d.expId = :id").setParameter("id", id) .getResultList();
		
		if (lst != null)
			for( Ms2DataSet set : lst)
				initializeTheKids(set, new String[]{"peakSets"});
		
		return lst;
		}


	public List <Ms2DataSet> loadForUploadDateRange(Calendar fromDate, Calendar toDate)
		{
		Map<String, Object> parms = new HashMap<String, Object>();
		
		fromDate.set(Calendar.HOUR_OF_DAY, 0);
		fromDate.set(Calendar.MINUTE, 0);
		fromDate.set(Calendar.SECOND,0);
		
		toDate.set(Calendar.HOUR_OF_DAY, 0);
		toDate.set(Calendar.MINUTE, 0);
		toDate.set(Calendar.SECOND,0);
		toDate.roll(Calendar.DAY_OF_YEAR, 1);
	
		parms.put("fromDate", fromDate);
		parms.put("toDate", toDate);
		List<Ms2DataSet> lst =  getEntityManager().createQuery(
				"from Ms2DataSet r where r.uploadDate >= :fromDate and r.uploadDate < :toDate order by r.uploadDate desc")
               .setParameter("fromDate", fromDate).setParameter("toDate", toDate)	.getResultList();
		
		for( Ms2DataSet set : lst)
			initializeTheKids(set, new String[]{"peakSets"});
		
		return lst;
		}

	
	
	public List <Ms2DataSet> loadForRunDateRange(Calendar fromDate, Calendar toDate)
		{
		List<Ms2DataSet> lst =  getEntityManager().createQuery(
				"from Ms2DataSet r where r.runDate >= :fromDate and r.runDate <= :toDate order by r.runDate desc")
				.setParameter("fromDate", fromDate).setParameter("toDate", toDate).getResultList();
		
		for( Ms2DataSet set : lst)
			initializeTheKids(set, new String[]{"peakSets"});
		
		return lst;
		}
	}

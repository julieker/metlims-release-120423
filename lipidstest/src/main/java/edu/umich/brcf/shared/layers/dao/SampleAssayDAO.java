////////////////////////////////////////////////////////////////////
// SampleAssayDAO.java
// Written by Jan Wigginton July 2015
////////////////////////////////////////////////////////////////////

package edu.umich.brcf.shared.layers.dao;

import java.util.List;

import javax.persistence.Query;

//import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.util.Assert;

import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.domain.SampleAssay;
import edu.umich.brcf.shared.layers.domain.SampleAssay.SampleAssayPK;


public class SampleAssayDAO extends BaseDAO 
	{
	public void createSampleAssay(SampleAssay sampleAssay) 
		{
		getEntityManager().persist(sampleAssay);
		}
	
	
	public void deleteSampleAssay(SampleAssay sampleAssay) 	
		{
		getEntityManager().remove(sampleAssay);
		}
	
	
	public void updateStatusNatively(String sampleId, String assayId, Character status)
		{
		String queryStr = "update Sample_Assays sa set status = ?1 where sample_id = ?2 and assay_id = ?3 " ;
		Query query = getEntityManager().createNativeQuery(queryStr).setParameter(1, status).setParameter(2, sampleId ).setParameter(3, assayId);
		
		int nUpdates = query.executeUpdate();
		}
	
	// JAK fix issues 155 and 159
	public void updateStatusNativelyEfficiently(String status, String assayId, String expId)
	{
	String queryStr = "update Sample_Assays sa set status = ?1 where assay_id = ?2 and sample_id in (select t2.sample_id from sample t2 where exp_id = ?3) " ;

	Query query = getEntityManager().createNativeQuery(queryStr).setParameter(1, status).setParameter(2, assayId).setParameter(3, expId );
	
	  try
	  {
	    int nUpdates = query.executeUpdate();
	    
	  }
	  catch (Exception e)
	  {
		e.printStackTrace();
		
	   }
	
	
	}
	
	public SampleAssay loadById(SampleAssayPK sampleAssayId) 
		{
		Assert.notNull(sampleAssayId);
	
		SampleAssay sa = getEntityManager().find(SampleAssay.class, sampleAssayId);
		initializeTheKids(sa, new String[] { "status", "assay", "sample" });
		//Hibernate.initialize(sample.getExp().getProject());
		//Hibernate.initialize(sample.getExp().getPriority());
		return sa;
		}
	
	
	public List<SampleAssay> loadAssaysForSample(Sample s)
		{
		List<SampleAssay> lst =  getEntityManager().createQuery("from SampleAssay sa where sa.sample = :sample")
				.setParameter("sample", s).getResultList();
		
		for (SampleAssay sa : lst)
			initializeTheKids(sa, new String [] {"sample", "assay", "status"});
			
		return lst;
		}
	}

	

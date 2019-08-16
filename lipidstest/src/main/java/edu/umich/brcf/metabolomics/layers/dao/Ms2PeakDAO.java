///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//MS2PeakDAO.java
//Written by Jan Wigginton 04/10/15
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.layers.dao;

import java.util.ArrayList;
import java.util.List;

//import org.springframework.orm.jpa.JpaTemplate;




import org.springframework.stereotype.Repository;

import edu.umich.brcf.metabolomics.layers.domain.Ms2Peak;
import edu.umich.brcf.metabolomics.layers.domain.Ms2PeakSet;
import edu.umich.brcf.shared.layers.dao.BaseDAO;


@Repository
public class Ms2PeakDAO extends BaseDAO 
	{
	public void createMs2Peak(Ms2Peak peak)
		{
		getEntityManager().persist(peak);
		initializeTheKids(peak, new String [] {"peakSet"});
		}
	
	
	public void deleteMs2Peak(Ms2Peak peak) 
		{
		getEntityManager().remove(peak);
		}
	
	
	public Ms2Peak loadById(Long long1)
		{
		Ms2Peak peak2 =  getEntityManager().find(Ms2Peak.class, long1);
		initializeTheKids(peak2, new String [] {"peakSet"});
		return peak2;
		}
	
	public List <Ms2Peak> loadInitializedForPeakSetId(String peakSetId)
		{
		Ms2PeakSet peakSet =  getEntityManager().find(Ms2PeakSet.class, peakSetId);
		
		if (peakSet == null) return new ArrayList<Ms2Peak>();
		
		initializeTheKids(peakSet, new String[]{"samplePeaks"});
		for( Ms2Peak  peak: peakSet.getSamplePeaks())
			initializeTheKids(peak, new String[]{"peakSet"});
		
		return peakSet.getSamplePeaks();
		}
	}

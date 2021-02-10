// Updated by Julie Keros June 2 2020
package edu.umich.brcf.shared.layers.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
import edu.umich.brcf.metabolomics.layers.dao.CompoundDAO;
import edu.umich.brcf.shared.layers.dao.AliquotDAO;
import edu.umich.brcf.shared.layers.dao.ExperimentDAO;
import edu.umich.brcf.shared.layers.dao.InventoryDAO;
import edu.umich.brcf.shared.layers.dao.LocationDAO;
import edu.umich.brcf.shared.layers.dao.MixtureDAO;
import edu.umich.brcf.shared.layers.dao.SampleDAO;
import edu.umich.brcf.shared.layers.dao.UserDAO;
import edu.umich.brcf.shared.layers.domain.Aliquot;
import edu.umich.brcf.shared.layers.domain.Mixture;
import edu.umich.brcf.shared.layers.domain.MixtureAliquot;
import edu.umich.brcf.shared.layers.domain.MixtureAliquotPK;
import edu.umich.brcf.shared.layers.domain.MixtureChildren;
import edu.umich.brcf.shared.layers.domain.MixtureChildrenPK;

@Transactional
public class MixtureService 
    {	
	AliquotDAO aliquotDao;
	SampleDAO sampleDao;
	UserDAO userDao;
	CompoundDAO compoundDao;
	LocationDAO locationDao;
	InventoryDAO inventoryDao;
	ExperimentDAO experimentDao;
	MixtureDAO mixtureDao;
	// issue 94
	public List<Mixture> loadAllMixtures()
		{		
		return mixtureDao.loadAllMixtures();
		}
	
	// issue 94
	public MixtureAliquot loadMixtureAliquotById(MixtureAliquotPK mixtureAliquotPK)
		{		
		return mixtureDao.loadMixtureAliquotById(mixtureAliquotPK);
		}
	
	public MixtureChildren loadMixtureChildrenById(MixtureChildrenPK mixtureChildrenPK)
		{		
		return mixtureDao.loadMixtureChildrenById(mixtureChildrenPK);
		}
	
	// issue 61	
	public MixtureDAO getMixtureDao()
		{
		return mixtureDao;
		}
	
	public void setMixtureDao(MixtureDAO mixtureDao) 
		{
		this.mixtureDao = mixtureDao;
		}
	
	// issue 110
	public Map<String, String> allMixtureIdsForMap()
		{
		List<String> ids = allMixtureIds();
		Map<String, String> map = new HashMap<String, String>();
		if (ids != null)
			for (String id : ids)
				map.put(id, null);		
		return map;
		}
	
	//issue 110
	public List<String> allMixtureIds()
		{
		return mixtureDao.allMixtureIds();
		}
	
	//issue 118
	public List<String> allMixtureNames()
		{
		return mixtureDao.allMixtureNames();
		}
	
	// Issue 118
	public Map<String, String> allMixtureIdsNamesMap()
		{
		List<String> ids = allMixtureNames();
		Map<String, String> map = new HashMap<String, String>();
		if (ids != null)
			for (String id : ids)
				map.put(id, null);		
		return map;
		}
	
	// issue 110
	public Map<String, String> allComplexMixtureIdsForMap()
		{
		List<String> ids = getComplexMixtureIds();
		Map<String, String> map = new HashMap<String, String>();
		if (ids != null)
			for (String id : ids)
				map.put(id, null);		
		return map;
		}
	
	// issue 110
	public List<String> getComplexMixtureIds()
		{
		return mixtureDao.getComplexMixtureIds();
		}
	
	//issue 110
	public List<Mixture> mixtureChildrenForMixtureId(String mid)
		{
		List<Mixture> mixtureIdList =  mixtureDao.mixtureChildrenForMixtureId(mid);
		return  mixtureIdList;
		}
	
	// issue 61
	
    }

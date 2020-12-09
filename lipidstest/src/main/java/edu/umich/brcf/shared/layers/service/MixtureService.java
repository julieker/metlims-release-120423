// Updated by Julie Keros June 2 2020
package edu.umich.brcf.shared.layers.service;


import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import edu.umich.brcf.metabolomics.layers.dao.CompoundDAO;
import edu.umich.brcf.shared.layers.dao.AliquotDAO;
import edu.umich.brcf.shared.layers.dao.ExperimentDAO;
import edu.umich.brcf.shared.layers.dao.InventoryDAO;
import edu.umich.brcf.shared.layers.dao.LocationDAO;
import edu.umich.brcf.shared.layers.dao.MixtureDAO;
import edu.umich.brcf.shared.layers.dao.SampleDAO;
import edu.umich.brcf.shared.layers.dao.UserDAO;
import edu.umich.brcf.shared.layers.domain.Mixture;
import edu.umich.brcf.shared.layers.domain.MixtureAliquot;
import edu.umich.brcf.shared.layers.domain.MixtureAliquotPK;

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
	
	// issue 61	
	public MixtureDAO getMixtureDao()
		{
		return mixtureDao;
		}
	
	public void setMixtureDao(MixtureDAO mixtureDao) 
		{
		this.mixtureDao = mixtureDao;
		}
	
	// issue 61
	
    }

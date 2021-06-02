////////////////////////////////////////////////////
// Mrc2MixtureDataService.java
// Written by Julie Keros, dec 1, 2020
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.layers.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.wicket.Session;
import org.springframework.transaction.annotation.Transactional;
import edu.umich.brcf.metabolomics.layers.dao.CompoundDAO;
import edu.umich.brcf.metabolomics.panels.lims.mixtures.MixAliquotInfo;
import edu.umich.brcf.shared.layers.dao.AliquotDAO;
import edu.umich.brcf.shared.layers.dao.InventoryDAO;
import edu.umich.brcf.shared.layers.dao.LocationDAO;
import edu.umich.brcf.shared.layers.dao.MixtureDAO;
import edu.umich.brcf.shared.layers.dao.UserDAO;
import edu.umich.brcf.shared.layers.domain.Aliquot;
import edu.umich.brcf.shared.layers.domain.Mixture;
import edu.umich.brcf.shared.layers.domain.MixtureAliquot;
import edu.umich.brcf.shared.layers.domain.MixtureChildren;
import edu.umich.brcf.shared.layers.domain.MixtureChildrenAliquot;
import edu.umich.brcf.shared.layers.domain.User;
import edu.umich.brcf.shared.layers.dto.MixtureDTO;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.METWorksException;
import edu.umich.brcf.shared.util.datacollectors.Mrc2TransitionalMixtureSheetData;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;

@Transactional(rollbackFor = Exception.class)
public class Mrc2MixtureDataService
	{
	UserDAO userDao;
    MixtureDAO mixtureDao;
    AliquotDAO aliquotDao;
    Mixture mixture;
    User user;
    CompoundDAO compoundDao;
    InventoryDAO inventoryDao;
    LocationDAO  locationDao;
    
    @Transactional(rollbackFor = Exception.class)
	public int saveMixtures(Mrc2TransitionalMixtureSheetData data) throws METWorksException
		{	
		List <MixtureDTO> dtoList = new ArrayList <MixtureDTO> ();
		dtoList =  data.mixtureMetadata.grabDTOList();
		try
			{
			boolean calledFromMetlimsInterface = false;
			createNewMixture(dtoList.get(0), calledFromMetlimsInterface);			
			}
		catch (Exception e)
			{
			System.out.println("in exception ");
			e.printStackTrace();
			}			
		return dtoList.size();
		}
	public int addUploadedSheetData(Mrc2TransitionalMixtureSheetData data, Integer nSamplesToAdd) throws METWorksException
		{
		// Issue 94
		return 0;
		} 
	
	// issue 123
	public Mixture createNewMixture(MixtureDTO dto, boolean calledFromMetlimsInterface )
		{
		int index =  0;
		try 
			{			
			user = userDao.loadById(((MedWorksSession) Session.get()).getCurrentUserId());
			mixture = Mixture.instance (Calendar.getInstance(), user, StringUtils.isEmptyOrNull(dto.getVolumeSolventToAdd()) ? null : new BigDecimal (dto.getVolumeSolventToAdd()),        StringUtils.isEmptyOrNull(dto.getDesiredFinalVolume()) ? null : new BigDecimal (dto.getDesiredFinalVolume()), dto.getMixtureName()); // issue 118		
			mixtureDao.createMixture(mixture);
		    List<String> aliquotList = new ArrayList <String> ();
		    if (dto.getAliquotList() == null)
		    	return mixture;
		    aliquotList.addAll(dto.getAliquotList());	
			for (String aliquotStr : aliquotList) 
			    {
				// JAK do a wrapper for this
		        Aliquot aliquot = aliquotDao.loadByIdForMixture(aliquotStr);
		        mixtureDao.createMixtureAliquot(MixtureAliquot.instance(mixture, aliquot, dto.getAliquotVolumeList().get(index), dto.getAliquotConcentrationList().get(index)));
				index++;
				}
			// issue 110
			index= 0;
			
			if (dto.getMixtureList()  == null)
				return mixture;
			if (calledFromMetlimsInterface)
				{
				for  (String mixtureStr : dto.getMixtureList()) 
					{
					Mixture childMixture = mixtureDao.loadById(mixtureStr);
					mixtureDao.createMixtureChild(MixtureChildren.instance(childMixture,mixture,  dto.getMixtureVolumeList() == null ? null : dto.getMixtureVolumeList().get(index), dto.getMixtureConcentrationList() == null ? null :dto.getMixtureConcentrationList().get(index)));
					////////////// null pointer error
					for (MixAliquotInfo singleMixAliquotInfo : dto.getMixtureAliquotInfoMap().get(mixtureStr))
						{
						Aliquot aliquot = aliquotDao. loadByIdForMixture(singleMixAliquotInfo.getAliquotId());
						mixtureDao.createMixtureChildAliquot(MixtureChildrenAliquot.instance(childMixture, mixture, aliquot, singleMixAliquotInfo.getMixAliquotConcentrationFinal()));
						} 
					index++;
					}
				}
			else
				{
				for  (String mixtureStr : dto.getMixtureList()) 
					{
					Mixture childMixture = mixtureDao.loadById(mixtureStr);
					mixtureDao.createMixtureChild(MixtureChildren.instance(childMixture,mixture,  dto.getMixtureVolumeList() == null ? null : dto.getMixtureVolumeList().get(index), dto.getMixtureConcentrationList() == null ? null :dto.getMixtureConcentrationList().get(index)));
					
					List <Object []> aliquotObjects = mixtureDao.aliquotsForMixtureId(mixtureStr);
					for (Object[] result : aliquotObjects)
						{
						MixAliquotInfo mAliquotInfo = new MixAliquotInfo();
						mAliquotInfo.setAliquotId ((String) result[2]);
						mAliquotInfo.setMixtureId(result[0].toString());
						mAliquotInfo.setMixAliquotConcentration ( result[3].toString());
						mAliquotInfo.setMixAliquotConUnits(result[4].toString());						
						Double conFinal =  (Double.parseDouble(dto.getMixtureVolumeList() == null ? null : dto.getMixtureVolumeList().get(index))  * Double.parseDouble(mAliquotInfo.getMixAliquotConcentration()))/Double.parseDouble(dto.getDesiredFinalVolume());						
						mAliquotInfo.setMixAliquotConcentrationFinal(conFinal.toString());
						Aliquot aliquot = aliquotDao. loadByIdForMixture(mAliquotInfo.getAliquotId());
						mixtureDao.createMixtureChildAliquot(MixtureChildrenAliquot.instance(childMixture, mixture, aliquot, mAliquotInfo.getMixAliquotConcentrationFinal()));
						}
					index++;
					}
				}
			}
		catch (Exception e)
		    {
			e.printStackTrace();
			return null;
		    }
		return mixture;
		}
	
	// issue 94
	
	public MixtureDAO getMixtureDao()
		{
		return mixtureDao;
		}
	
	public void setMixtureDao(MixtureDAO mixtureDao)
		{
		this.mixtureDao = mixtureDao;
		}

	public UserDAO getUserDao()
		{
		return userDao;
		}

	public void setUserDao(UserDAO userDao)
		{
		this.userDao = userDao;
		}
	
	public AliquotDAO getAliquotDao()
		{
		return aliquotDao;
		}

	public void setAliquotDao(AliquotDAO aliquotDao) 
		{
		this.aliquotDao = aliquotDao;
		}
	
	// issue 94
	//issue 94
	public CompoundDAO getCompoundDao()
		{
		return compoundDao;
		}
	public void setCompoundDao(CompoundDAO compoundDao) 
		{
		this.compoundDao = compoundDao;
		}

	public void setLocationDao(LocationDAO locationDao) 
		{
		this.locationDao = locationDao;
		}	
	public LocationDAO getLocationDao()
		{
		return locationDao;
		}

	public void setInventoryDao(InventoryDAO inventoryDao) 
		{
		this.inventoryDao = inventoryDao;
		}
	public InventoryDAO getInventoryDao()
		{
		return inventoryDao;
		}
		
	}



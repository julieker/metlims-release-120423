// Updated by Julie Keros June 2 2020
package edu.umich.brcf.shared.layers.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;
import edu.umich.brcf.metabolomics.panels.lims.mixtures.AliquotInfo;
import edu.umich.brcf.metabolomics.panels.lims.mixtures.MixAliquotInfo;
import edu.umich.brcf.shared.layers.dao.AliquotDAO;
import edu.umich.brcf.shared.layers.dao.MixtureDAO;
import edu.umich.brcf.shared.layers.domain.Aliquot;
import edu.umich.brcf.shared.layers.domain.Mixture;
import edu.umich.brcf.shared.layers.domain.MixtureAliquot;
import edu.umich.brcf.shared.layers.domain.MixtureAliquotPK;
import edu.umich.brcf.shared.layers.domain.MixtureChildren;
import edu.umich.brcf.shared.layers.domain.MixtureChildrenAliquot;
import edu.umich.brcf.shared.layers.domain.MixtureChildrenAliquotPK;
import edu.umich.brcf.shared.layers.domain.MixtureChildrenPK;
import edu.umich.brcf.shared.layers.dto.MixtureDTO;

@Transactional(rollbackFor = Exception.class)
public class MixtureService 
    {	
	AliquotDAO aliquotDao;
	MixtureDAO mixtureDao;
	
	// issue 123
	public Mixture loadById(String id) 
		{
		return mixtureDao.loadById(id);
		}
		
	// issue 94
	public List<Mixture> loadAllMixtures()
		{		
		return mixtureDao.loadAllMixtures();
		}
	// issue 123
	public List<Object[]> aliquotsForMixtureId(String mId)
		{
		return mixtureDao.aliquotsForMixtureId(mId);
		}
	

	// issue 196
	public List<Object[]> tooltipsListForMixtureMap()
		{
		return mixtureDao.tooltipsListForMixtureMap();
		}
	
	
	// issue 138
	public boolean isMixturesSecondaryMixture(String mixtureId )
		{
		return mixtureDao.isMixturesSecondaryMixture(mixtureId);
		}
		
	// issue 138
	public List<Object[]> aliquotsForSecondaryMixtures(String secondaryMid, String mId)
		{
		return mixtureDao.aliquotsForSecondaryMixtures(secondaryMid, mId);
		}
	
	// issue 138
	public List<Object[]> secondaryMixturesForMixture(String mId)
		{
		return mixtureDao.secondaryMixturesForMixture(mId);
		}
	
	// issue 94
	public MixtureAliquot loadMixtureAliquotById(MixtureAliquotPK mixtureAliquotPK)
		{		
		return mixtureDao.loadMixtureAliquotById(mixtureAliquotPK);
		}
	
	// issue 199
	public List<Object[]> loadRetiredDryAliquot(String mixtureId, String aliquotId)
		{		
		return mixtureDao.loadRetiredDryAliquot(mixtureId, aliquotId);
		}
	
	public MixtureChildren loadMixtureChildrenById(MixtureChildrenPK mixtureChildrenPK)
		{		
		return mixtureDao.loadMixtureChildrenById(mixtureChildrenPK);
		}
	
	public MixtureChildrenAliquot loadMixtureChildrenAliquotById(MixtureChildrenAliquotPK mixtureChildrenAliquotPK)
		{		
		return mixtureDao.loadMixtureChildrenAliquotById(mixtureChildrenAliquotPK);
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
	
	// issue 138
	public List<String> allMixtureNamesExcludingCurrent(String mixtureId)
		{
		return mixtureDao.allMixtureNamesExcludingCurrent(mixtureId);
		}
	
	// issue 138
	public Map<String, String> allMixtureIdsNamesMap()
		{
		return allMixtureIdsNamesMap(null);
		}
	
	// Issue 138
	public Map<String, String> allMixtureIdsNamesMap(Mixture mixture)
		{
		List <String> ids = new ArrayList <String> ();
		if (mixture != null)
			ids = allMixtureNamesExcludingCurrent(mixture.getMixtureId());
		else
			ids = allMixtureNames();
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
	
	// issue 123
	public List<String> getNonComplexMixtureIds(Mixture mixtureToEdit)
		{
		return mixtureDao.getNonComplexMixtureIds(mixtureToEdit);
		}
	
	//issue 110
	public List<Mixture> mixtureChildrenForMixtureId(String mid)
		{
		List<Mixture> mixtureIdList =  mixtureDao.mixtureChildrenForMixtureId(mid);
		return  mixtureIdList;
		}
	
	// issue 123
	public boolean isMixtureNameInDatabase(String mName, Map<String, String> mixtureNamesAlreadyInDatabase) 
		{
		if (mixtureNamesAlreadyInDatabase.containsKey(mName))
	   		return true;
		return false;
		}
	
	public Mixture updateMixture (MixtureDTO mixtureDto, String mId)
		{
		Mixture mix = loadById(mId);
		mix.update(mixtureDto);
		return mix;
		}
	// issue 61
	
	// issue 138
	// issue 199
	public void updateMixtureAliquot (List <AliquotInfo> aliquotInfoList, Mixture mix)
		{
		int i = 0;
		mixtureDao.removeMixtureAliquots(mix.getMixtureId());
		List <String> aliquotBuildMixtureList = new ArrayList <String> ();
		for (AliquotInfo lilAliquotInfo : aliquotInfoList)
			{			
			Aliquot alq = aliquotDao.loadById(lilAliquotInfo.getAliquotId());
		    Character dryAliquotRetired =  alq.getDry();
		    mixtureDao.createMixtureAliquot(MixtureAliquot.instance(mix, alq, lilAliquotInfo.getVolumeTxt(), lilAliquotInfo.getConcentrationTxtFinal(), lilAliquotInfo.getVolumeAliquotUnits(),dryAliquotRetired));		 
			}
		}
	
	// issue 138
	    
		public void updateMixtureAndChildrenAliquotInfo (Mixture mix, MixtureDTO mixtureDto, List <AliquotInfo> aliquotInfoList)
			{
			updateMixture(mixtureDto, mix.getMixtureId());
			updateMixtureAliquot(aliquotInfoList,mix);              
	        mixtureDao.removeSecondaryMixtureAliquots(mix.getMixtureId());
	        mixtureDao.removeSecondaryMixture(mix.getMixtureId());	
	        int index = 0;
	        for  (String mixtureStr : mixtureDto.getMixtureList()) 
				{
				Mixture childMixture = mixtureDao.loadById(mixtureStr);
				// issue 196
				mixtureDao.createMixtureChild(MixtureChildren.instance(childMixture,mix,  mixtureDto.getMixtureVolumeList() == null ? null : mixtureDto.getMixtureVolumeList().get(index), mixtureDto.getMixtureConcentrationList() == null ? null :mixtureDto.getMixtureConcentrationList().get(index), mixtureDto.getMixtureVolumeUnitList().get(index)   ));
				////////////// null pointer error
				for (MixAliquotInfo singleMixAliquotInfo : mixtureDto.getMixtureAliquotInfoMap().get(mixtureStr))
					{
					Aliquot aliquot = aliquotDao. loadByIdForMixture(singleMixAliquotInfo.getAliquotId());
					mixtureDao.createMixtureChildAliquot(MixtureChildrenAliquot.instance(childMixture, mix, aliquot, singleMixAliquotInfo.getMixAliquotConcentrationFinal()));
					} 
				index++;
				}
			}
	
	public void setAliquotDao(AliquotDAO aliquotDao) { this.aliquotDao = aliquotDao; }
    }

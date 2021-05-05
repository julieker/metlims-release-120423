/**********************
 * Updated by Julie Keros Sept 2000
 * Added aliquotIdsForExpId ,
 * getMatchingAliquotIds,loadByCid and other routines for Aliquots.
 **********************/
package edu.umich.brcf.shared.layers.dao;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
//import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.stereotype.Repository;
import edu.umich.brcf.shared.layers.domain.Aliquot;
import edu.umich.brcf.shared.layers.domain.AssayAliquot;
import edu.umich.brcf.shared.layers.domain.ExperimentAliquot;
import edu.umich.brcf.shared.layers.domain.MixtureAliquot;
import edu.umich.brcf.shared.layers.domain.VolumeUnits;

// issue 61

@Repository
public class AliquotDAO extends BaseDAO 
	{	
	// issue 86
	public List<String> aliquotIdsForExpId(String eid)
		{
		Query query = getEntityManager().createNativeQuery("select cast(aliquot_id as char(9)) from experiment_aliquot where exp_id = ?1 "
				+ " order by  1 desc").setParameter(1, eid);
		List<String> aliquotList = query.getResultList();
		return aliquotList;
		}
	
	// issue 94
	public List<String> allAliquotIds()
		{
		Query query = getEntityManager().createNativeQuery("select cast(aliquot_id as char(9)) from aliquot order by 1 "
				);
		List<String> aliquotList = query.getResultList();
		return aliquotList;
		}
		
	public List<Aliquot> aliquotIdsForMixtureId (String mid)
		{
		List<MixtureAliquot> malqLst =  getEntityManager().createQuery("from MixtureAliquot where mixture_id = ?1  order by aliquot_id")
				.setParameter(1, mid).getResultList();	
		List <Aliquot> alqList = new ArrayList <Aliquot> ();
	    for (MixtureAliquot malq : malqLst)
			{
	    	Aliquot alq = malq.getAliquot();
		    initializeTheKids(alq, new String[] { "location", "inventory" , "compound"});
			alqList.add(alq);
			}
		return alqList;
		}
	
	// issue 61 2020
	public List<String> getMatchingAliquotIds(String input)
		{
		Query query = getEntityManager().createQuery("select distinct a.aliquotId from Aliquot a where a.aliquotId like '"+input+"%'");
		List<String> alqIdList = query.getResultList();
		return alqIdList;
		}	
		
	// issue 61 2020 
	public Aliquot loadById(String aliquotId)
		{
		Aliquot alq = getEntityManager().find(Aliquot.class, aliquotId);
		initializeTheKids(alq, new String[] { "location", "inventory", "compound" });
		return alq;
		}
	
	public Aliquot loadByIdForMixture(String aliquotId)
		{
		Aliquot alq = getEntityManager().find(Aliquot.class, aliquotId);
		return alq;
		}
	
	// issue 123 
	public String getCompoundIdFromAliquot (String aliquotId)
		{
		Query query = getEntityManager().createNativeQuery("select cast(t1.cid as CHAR(6)) from aliquot t1 where aliquot_id = ?1").setParameter(1, aliquotId);	
		String cIdStr = query.getResultList().get(0).toString();
		return cIdStr;
		}
	
	public VolumeUnits loadVolUnitsById(String unitsID) 
		{
		VolumeUnits units = getEntityManager().find(VolumeUnits.class, unitsID);
		return units;
		}

	public List<String> getVolUnitsDDList(String unitsID) 
		{
		VolumeUnits unit = loadVolUnitsById(unitsID);
		Query query = getEntityManager().createQuery("select v.units from VolumeUnits v where v.type='" + unit.getType() 
		+ "' and v.priority <= " + unit.getPriority());		
		List<String> unitsList = query.getResultList();
		return unitsList;
		}

	public List<String> getAllVolUnits() 
		{
		Query query = getEntityManager().createQuery("select distinct v.units from VolumeUnits v");
		List<String> unitsList = query.getResultList();
		return unitsList;
		}
	
	// issue 79
	public void deleteAndSetReason(String aliquotId, String deleteReason)
		{
		Aliquot a = getEntityManager().find(Aliquot.class, aliquotId);
		a.setDeleted();
		a.setDeleteReason(deleteReason);
		}
		
	public List<String> getWellDataFile(Long wellId) 
		{
		Query query = getEntityManager().createNativeQuery("select i.file_name from injection_name i where i.well_id = ?1").setParameter(1, wellId)	;	
		List<String> items = query.getResultList();
		return items;
		}
	
	public void createAliquot(Aliquot alqt)
		{
		getEntityManager().persist(alqt);
		initializeTheKids(alqt, new String[] { "location", "inventory" , "compound"});
		}
	
	// issue 61
	public List<Aliquot> loadByCid(String cid)
		{
		List<Aliquot> alqLst =  getEntityManager().createQuery("from Aliquot where cid = ?1 and (deletedFlag is null or deletedFlag = false) order by aliquot_id")
				.setParameter(1, cid).getResultList();	
	    for (Aliquot alq : alqLst)
			{
			initializeTheKids(alq, new String[] { "location", "inventory" , "compound"});
			}
		return alqLst;
		}
	
	// issue 79
	public List<String> loadAllAliquotsNotChosen(String expId)
		{
		Query query = getEntityManager().createNativeQuery("select cast(aliquot_id as VARCHAR2(9)) from aliquot a where deleted is null and not exists (select * from experiment_aliquot ae where exp_id = ?1 and ae.aliquot_id = a.aliquot_id ) order by aliquot_id desc").setParameter(1, expId);	
		List<String> orgList = query.getResultList();	
		return (orgList == null ? new ArrayList<String>() : orgList);
		}
	
	// issue 79
	public List<String> loadByEid(String expid)
		{
		Query query = getEntityManager().createNativeQuery("select cast(aliquot_id as VARCHAR2(9)) from experiment_aliquot where exp_id = ?1  order by 1 desc").setParameter(1,expid);		
		List<String> alqList = query.getResultList();	
		return (alqList == null ? new ArrayList<String>() : alqList);
		}
		
	// issue 61
	public List<Aliquot> loadByCidDeleted(String cid)
		{
		List<Aliquot> alqLst =  getEntityManager().createQuery("from Aliquot where cid = ?1 and deletedFlag =true order by aliquot_id")
				.setParameter(1, cid).getResultList();	
	    for (Aliquot alq : alqLst)
			{
			initializeTheKids(alq, new String[] { "location", "inventory" , "compound"});
			}
		return alqLst;
		}
	
	// issue 79
	public void createExperimentAliquot(ExperimentAliquot experimentAliquot)
		{
		getEntityManager().persist(experimentAliquot);
		}
	
	// issue 79
	public void deleteExperimentAliquot (String expId, String aliquotId)
	    {	
	    Query query = getEntityManager().createNativeQuery(" delete from experiment_aliquot where exp_id = ?1 and aliquot_id = ?2  " ).setParameter(1, expId).setParameter(2,aliquotId);
	    query.executeUpdate();			
	    }
		
	// issue 100
	public void deleteAssayAliquot ( String aliquotId)
	    {	
	    Query query = getEntityManager().createNativeQuery(" delete from assay_aliquot where aliquot_id = ?1  " ).setParameter(1,aliquotId);
	    query.executeUpdate();			
	    }
	
	public List<String> retrieveAssayNames(String aliquotId)
		{
		Query query = getEntityManager().createNativeQuery("select a.assay_name||' ('||a.assay_id||')' from assays a, assay_aliquot al where a.assay_id = al.assay_id and aliquot_id = ?1").setParameter(1,aliquotId);
		List<String> assayList = query.getResultList();
		return assayList;
		}
	
	// issue 100	
	public void createAssayAliquot(AssayAliquot assayAliquot)
		{
		getEntityManager().persist(assayAliquot);
		}	
	
	// issue 123
	public List<String> loadAliquotListNoAssay()
		{
		Query query = getEntityManager().createNativeQuery("select cast(t1.aliquot_id as VARCHAR2(9)) from aliquot t1 where dry = '0' and deleted is null order by 1 ");		
		List<String> alqList = query.getResultList();	
		return (alqList == null ? new ArrayList<String>() : alqList);
		}
	
	// issue 123 
	public List<String> loadAliquotList(String assayId)
		{
		Query query = getEntityManager().createNativeQuery("select cast(t1.aliquot_id as VARCHAR2(9)) from assay_aliquot t1, aliquot t2 where t1.aliquot_id = t2.aliquot_id and dry = '0' and assay_id = ?1 and deleted is null order by 1 ").setParameter(1, assayId);		
		List<String> alqList = query.getResultList();	
		return (alqList == null ? new ArrayList<String>() : alqList);
		}
	
	// issue 100
	public List<Aliquot> getAliquotsFromAssay (String assayId)
		{
		List<String> alqStringLst =  getEntityManager().createNativeQuery("select distinct t1.aliquot_id from assay_aliquot t1, aliquot t2 where t1.aliquot_id = t2.aliquot_id and t1.assay_id = ?1 and deleted is null order by aliquot_id")
				.setParameter(1, assayId).getResultList();	
		List<Aliquot> alqList = new ArrayList <Aliquot> ();
	    for (String alqString : alqStringLst)
			{
	    	Aliquot alq = loadById(alqString);
			initializeTheKids(alq, new String[] { "location", "inventory" , "compound"});
			alqList.add(alq);
			}
		return alqList; 
		}
	
	}

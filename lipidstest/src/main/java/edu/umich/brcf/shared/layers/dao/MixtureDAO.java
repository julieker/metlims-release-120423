/**********************
 * created by Julie Keros issue 94 oct 2020 
 **********************/
package edu.umich.brcf.shared.layers.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import edu.umich.brcf.shared.layers.domain.Aliquot;
import edu.umich.brcf.shared.layers.domain.Mixture;
import edu.umich.brcf.shared.layers.domain.MixtureAliquot;
import edu.umich.brcf.shared.layers.domain.MixtureAliquotPK;
import edu.umich.brcf.shared.layers.domain.MixtureChildren;
import edu.umich.brcf.shared.layers.domain.MixtureChildrenAliquot;
import edu.umich.brcf.shared.layers.domain.MixtureChildrenAliquotPK;
import edu.umich.brcf.shared.layers.domain.MixtureChildrenPK;

// issue 61

@Repository
public class MixtureDAO extends BaseDAO 
	{	
	public Mixture loadById(String mixtureId)
		{
		Mixture mixture = getEntityManager().find(Mixture.class, mixtureId);
		initializeTheKids(mixture, new String[] { "createdBy" });
		return mixture;
		}
	
	public void createMixture(Mixture mixture)
		{
		getEntityManager().persist(mixture);
		initializeTheKids(mixture, new String[] { "createdBy"});
		}
	
	// issue 94
	public List<Mixture> loadAllMixtures()
		{
		List<Mixture> mixLst =  getEntityManager().createQuery("from Mixture order by mixture_id")
				.getResultList();	
		return mixLst;
		}
	
	// issue 110
	public List<String> allMixtureIds()
		{
		Query query = getEntityManager().createNativeQuery("select cast(t1.mixture_id as char(9)) from mixture t1");
		List<String> mixtureList = query.getResultList();
		return mixtureList;
		}
	
	// issue 118
	public List<String> allMixtureNames()
		{
		Query query = getEntityManager().createNativeQuery("select distinct mixture_name from mixture t1");
		List<String> mixtureNameList = query.getResultList();
		return mixtureNameList;
		}
	
	// issue 138
	public List<String> allMixtureNamesExcludingCurrent(String mId)
		{
		Query query = getEntityManager().createNativeQuery("select distinct mixture_name from mixture t1 where mixture_id != ?1").setParameter(1, mId);
		List<String> mixtureNameList = query.getResultList();
		return mixtureNameList;
		}
	
	// issue 94	
	public void createMixtureAliquot(MixtureAliquot mixtureAliquot)
		{
		try
			{			
			getEntityManager().persist(mixtureAliquot);
			}
			catch (Exception e)
			{
		    e.printStackTrace();		    
			}		
		}
	
	// issue 199;
	public List<Object[]> loadRetiredDryAliquot(String mixtureId, String aliquotId)
		{
		//Query query = getEntityManager().createNativeQuery("select mixture_id, aliquot_id, dry_aliquot_retired from mixture_aliquot where mixture_id = ?1 and aliquot_id = ?2 ").setParameter(1, mixtureId).setParameter(2, aliquotId);
		Query query = getEntityManager().createNativeQuery("select aliquot_id from mixture_aliquot where mixture_id ='" + mixtureId + "'"  + " and aliquot_id ='" +  aliquotId + "'");
		List<Object[]> mixtureNameList = query.getResultList();
		
		System.out.println("Here is the queryyyyy:" + "select mixture_id, aliquot_id, dry_aliquot_retired from mixture_aliquot where mixture_id ='" + mixtureId + "'"  + " and aliquot_id ='" +  aliquotId + "'");
		System.out.println("here is the size of the query list:" + mixtureNameList.size());
		System.out.println("here is the mixtureid:" +  "'" + mixtureId + "'" + " " + "here is aiquotid:" + "'" + aliquotId + "'");
		return query.getResultList();
		}
	
	public MixtureAliquot loadMixtureAliquotById(MixtureAliquotPK mixtureAliquotPK)
		{
		try
			{
			MixtureAliquot mixtureAliquot = getEntityManager().find(MixtureAliquot.class, mixtureAliquotPK	);
			return mixtureAliquot;
			}
		catch (Exception e)
			{	
			e.printStackTrace();
			return null;
			}
		}
	// issue 110
	public void createMixtureChild(MixtureChildren mixtureChildren)
		{
		getEntityManager().persist(mixtureChildren);
		}
	
	// issue 138
	public void removeSecondaryMixtureAliquots(String mixtureId)
		{
		Query query = getEntityManager().createNativeQuery("delete mixture_children_aliquot where parent_mixture_id =  ?1").setParameter(1, mixtureId);
		query.executeUpdate();
		}
	
	// issue 138
	public void removeSecondaryMixture(String mixtureId)
		{
		Query query = getEntityManager().createNativeQuery("delete mixture_children where parent_mixture_id =  ?1").setParameter(1, mixtureId);
		query.executeUpdate();
		}
	
	// issue 138
	public void removeMixtureAliquots(String mixtureId)
		{
		Query query = getEntityManager().createNativeQuery("delete mixture_aliquot where mixture_id = ?1").setParameter(1, mixtureId);
		query.executeUpdate();
		}
	// issue 201
	public void removeMixtureAliquots(MixtureAliquot ma)
		{
		getEntityManager().remove(ma);
		//getEntityManager().getTransaction().commit();
		}
	
	// issue 123
	public void createMixtureChildAliquot(MixtureChildrenAliquot mixtureChildrenAliquot)
		{
		getEntityManager().persist(mixtureChildrenAliquot);
		}
	
	public MixtureChildren loadMixtureChildrenById(MixtureChildrenPK mixtureChildrenPK)
		{
		MixtureChildren mixtureChildren = getEntityManager().find(MixtureChildren.class, mixtureChildrenPK	);
		return mixtureChildren;
		}	
	
	// issue 123
	public MixtureChildrenAliquot loadMixtureChildrenAliquotById(MixtureChildrenAliquotPK mixtureChildrenAliquotPK)
		{
		MixtureChildrenAliquot mixtureAliquotChildren = getEntityManager().find(MixtureChildrenAliquot.class, mixtureChildrenAliquotPK	);
		return mixtureAliquotChildren;
		}
	
	// issue 110
	public List<Mixture> mixtureChildrenForMixtureId (String mid)
		{
		List<MixtureChildren> mixChildList =  getEntityManager().createQuery("from MixtureChildren where parent_mixture_id = ?1  order by mixture_id")
				.setParameter(1, mid).getResultList();	
		List <Mixture> mixList = new ArrayList <Mixture> ();
	    for (MixtureChildren mchild : mixChildList)
			{
	    	Mixture mix = mchild.getMixture();
		    initializeTheKids(mix, new String[] { "createdBy"});
			mixList.add(mix);
			}
		return mixList;
		}
	
	// issue 110
	public List<String> getComplexMixtureIds()
		{
		Query query = getEntityManager().createNativeQuery("select cast(t1.parent_mixture_id as char(9)) from mixture_children t1");
		List<String> mixtureList = query.getResultList();
		return mixtureList;
		}
	
	// issue 123
	public List<String> getNonComplexMixtureIds(Mixture mixtureToEdit)
		{
		Query query;
		if (mixtureToEdit == null)
			query = getEntityManager().createNativeQuery("select distinct cast(t1.mixture_id as char(9)) from mixture t1 where mixture_id not in (select parent_mixture_id from mixture_children) order by 1");	
		else
			query = getEntityManager().createNativeQuery("select distinct cast(t1.mixture_id as char(9)) from mixture t1 where t1.mixture_id <> ?1 and mixture_id not in (select parent_mixture_id from mixture_children) order by 1").setParameter(1, mixtureToEdit.getMixtureId());
		List<String> mixtureList = query.getResultList();
		return mixtureList;
		}
	
	// issue 138
	
	
	// issue 123
	// issue 196
	public List<Object[]> aliquotsForMixtureId(String mId)
		{
		// issue 196 include dry
		Query query = getEntityManager().createNativeQuery("select distinct t3.mixture_id, t3.mixture_name,  t1.aliquot_id, decode(neat,'1', desired_concentration_neat, desired_concentration) desired_concentration , decode(neat,'1',DESIRED_CONCENTRATION_UNITS, NEAT_SOL_VOL_UNITS), CONCENTRATION_ALIQUOT, volume_aliquot , t2.WEIGHTED_AMOUNT, t2.MOLECULAR_WEIGHT, t1.VOLUME_ALIQUOT_UNITS, t2.WEIGHTED_AMOUNT_UNITS from mixture_aliquot t1, aliquot t2, mixture t3 where t1.mixture_id = t3.mixture_id and t1.aliquot_id = t2.aliquot_id and t1.mixture_id = ?1  order by 3").setParameter(1, mId);
		return query.getResultList();
		}
	
	// issue 196
	public List<Object[]> tooltipsListForMixtureMap()
		{
		Query query = getEntityManager().createNativeQuery("select distinct mixture_id, mixture_name, first_name || ' ' || last_name, create_date from mixture t1," + 
				"researcher t2 where t1.created_by = researcher_id order by 1");
		return query.getResultList();
		}
	
	// issue 138
	public List<Object[]> secondaryMixturesForMixture (String mId)
		{
		Query query = getEntityManager().createNativeQuery("select t1.mixture_id , volume_mixture, volume_mixture_units from mixture_children t1 where t1.parent_mixture_id = ?1 order by 1").setParameter(1, mId);
		return query.getResultList();
		}
	
	// issue 138
	public List<Object[]> aliquotsForSecondaryMixtures(String secondaryMid,String mId )
		{
		Query query = getEntityManager().createNativeQuery("select t1.mixture_id , volume_mixture, t3.aliquot_id, concentration_final, decode(neat,'1', desired_concentration_neat, desired_concentration) desired_concentration , decode(neat,'1',DESIRED_CONCENTRATION_UNITS, NEAT_SOL_VOL_UNITS), t4.mixture_name,t3.molecular_weight, t3.weighted_amount, t3.weighted_amount_units from mixture_children t1,mixture_children_aliquot t2 , aliquot t3, mixture t4 where t1.mixture_id = t2.mixture_id and t1.mixture_id = ?1 and t4.mixture_id = t2.mixture_id and t1.parent_mixture_id = t2.parent_mixture_id and t1.parent_mixture_id = ?2 and t2.aliquot_id = t3.aliquot_id order by 3").setParameter(1, secondaryMid).setParameter(2, mId);
		return query.getResultList();
		}
	
	// issue 138
	public boolean isMixturesSecondaryMixture(String mixtureId )
		{
		Query query = getEntityManager().createNativeQuery("select parent_mixture_id from mixture_children where mixture_id = ?1 ").setParameter(1, mixtureId);
		if (query.getResultList().size() > 0)
		    return true;
		return false;
		}
		
	}

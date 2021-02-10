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
	 /*   for (Mixture mixture : mixLst)
			{
			initializeTheKids(mixture, new String[] { "createdBy"});
			}*/
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
	
	// issue 94	
	public void createMixtureAliquot(MixtureAliquot mixtureAliquot)
		{
		getEntityManager().persist(mixtureAliquot);
		}
	
	public MixtureAliquot loadMixtureAliquotById(MixtureAliquotPK mixtureAliquotPK)
		{
		MixtureAliquot mixtureAliquot = getEntityManager().find(MixtureAliquot.class, mixtureAliquotPK	);
		return mixtureAliquot;
		}
	// issue 110
	public void createMixtureChild(MixtureChildren mixtureChildren)
		{
		getEntityManager().persist(mixtureChildren);
		}
	public MixtureChildren loadMixtureChildrenById(MixtureChildrenPK mixtureChildrenPK)
		{
		MixtureChildren mixtureChildren = getEntityManager().find(MixtureChildren.class, mixtureChildrenPK	);
		return mixtureChildren;
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
	
	}

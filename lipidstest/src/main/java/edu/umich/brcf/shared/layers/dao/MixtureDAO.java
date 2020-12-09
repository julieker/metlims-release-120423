/**********************
 * created by Julie Keros issue 94 oct 2020 
 **********************/
package edu.umich.brcf.shared.layers.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import edu.umich.brcf.shared.layers.domain.Mixture;
import edu.umich.brcf.shared.layers.domain.MixtureAliquot;
import edu.umich.brcf.shared.layers.domain.MixtureAliquotPK;

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
	    for (Mixture mixture : mixLst)
			{
			initializeTheKids(mixture, new String[] { "createdBy"});
			}
		return mixLst;
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
	
	}

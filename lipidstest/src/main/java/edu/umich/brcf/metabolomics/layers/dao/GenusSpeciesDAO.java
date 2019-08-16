///////////////////////////////////////////
// Writtten by Anu Janga
///////////////////////////////////////////

package edu.umich.brcf.metabolomics.layers.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import edu.umich.brcf.metabolomics.layers.domain.GenusSpecies;
import edu.umich.brcf.shared.layers.dao.BaseDAO;


@Repository
public class GenusSpeciesDAO extends BaseDAO
	{
	public List<GenusSpecies> allGenusSpecies()
		{
		List<GenusSpecies> gsList =getEntityManager().createQuery("from GenusSpecies").getResultList();
		return gsList;
		}

	
	public GenusSpecies loadById(Long ID)
		{
		GenusSpecies gs =getEntityManager().find(GenusSpecies.class, ID);
		return gs;
		}

	
	public List limitedGenusSpecies(String str)
		{
		Query query = getEntityManager().createQuery("from GenusSpecies g where g.genusName like '" + str + "%' order by g.genusName");
		query.setMaxResults(50);
		List<GenusSpecies> list = query.getResultList();
		return list;
		}
	

	public List<String> getSubjectSpeciesForTaxId(String taxId)
		{
		Query query2 = getEntityManager().createNativeQuery( "select cast(t.name_txt as VARCHAR2(1000)) from Taxonomy_Names t"
						+ " where t.name_class =?1 and t.tax_id = ?2" ).setParameter(1, "scientific name").setParameter(2, taxId);
		
		List<String> nameList = query2.getResultList();
		return nameList;
		}
	

	public GenusSpecies loadByName(String name)
		{
		List<GenusSpecies> lst =getEntityManager().createQuery("from GenusSpecies g where trim(g.genusName) = :name")
				.setParameter("name", name.trim()).getResultList();
		
		if (lst.size() >= 1)
			return lst.get(0);
		
		return lst.get(0);
		}
	}


///////////////////////////////////////////
// Writtten by Anu Janga
// Revisited by Jan Wigginton August 2015
///////////////////////////////////////////

package edu.umich.brcf.shared.layers.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import edu.umich.brcf.shared.layers.domain.SampleType;


@Repository
public class SampleTypeDAO extends BaseDAO
	{
	public List<SampleType> allSampleTypes()
		{
		List<SampleType> stList = getEntityManager().createQuery("from SampleType").getResultList();
		return stList;
		}

	
	public SampleType loadByDescriptionAndUsage(String name, String usage)
		{
		return (SampleType) DataAccessUtils.requiredSingleResult(getEntityManager()
				.createQuery("from SampleType st where st.description = :name and st.usage = :usage")
				.setParameter("name", name).setParameter("usage", usage).getResultList());
		}

	
	public SampleType loadById(String ID)
		{
		SampleType st = getEntityManager().find(SampleType.class, ID);
		return st;
		}

	public List<SampleType> getMatchingTypes(String str)
		{
		Query query = getEntityManager().createQuery("from SampleType st where lower(st.description) like '"
				+ str.toLowerCase() + "%' order by st.description");
	
		query.setMaxResults(50);
		List<SampleType> typeList = query.getResultList();
		return typeList;
		}

	
	public SampleType loadByDescription(String desc)
		{
		List<SampleType> lst = getEntityManager().createQuery("from SampleType st where trim(st.description) = :desc")
				.setParameter("desc", desc.trim()).getResultList();
		
		SampleType sType;
		
		try { sType = (SampleType) DataAccessUtils.requiredSingleResult(lst); } 
		catch (Exception e) { sType = null; } 
		
		return sType;
		}
	}

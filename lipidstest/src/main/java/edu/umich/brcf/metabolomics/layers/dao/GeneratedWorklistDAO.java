////////////////////////////////////////////////////////////////////
// GeneratedWorklistDAO.java
// Written by Jan Wigginton July 2015
////////////////////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.layers.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import edu.umich.brcf.metabolomics.layers.domain.GeneratedWorklist;
import edu.umich.brcf.shared.layers.dao.BaseDAO;


@Repository
public class GeneratedWorklistDAO extends BaseDAO
	{
	public void createGeneratedWorklist(GeneratedWorklist worklist)
		{
		getEntityManager().persist(worklist);
		initializeTheKids(worklist, new String[] { "items" });
		}

	
	public void deleteGeneratedWorklist(GeneratedWorklist worklist)
		{
		getEntityManager().remove(worklist);
		}

	
	public GeneratedWorklist loadById(String id)
		{
		GeneratedWorklist info = getEntityManager().find(GeneratedWorklist.class, id);
		initializeTheKids(info, new String[] { "items" });
		return info;
		}

	
	public List<GeneratedWorklist> loadAll()
		{
		List<GeneratedWorklist> dataSetList = getEntityManager().createQuery("from GeneratedWorklist order by worklistId desc").getResultList();

		for (GeneratedWorklist worklist : dataSetList)
			initializeTheKids(worklist, new String[] { "items" });
	
		return dataSetList;
		}

	
	public List<GeneratedWorklist> loadByExpId(String id)
		{
		List<GeneratedWorklist> lst = getEntityManager().createQuery("from GeneratedWorklist d where d.expId = :id order by worklistId desc")
		 .setParameter("id", id).getResultList();
		
		for (GeneratedWorklist worklist : lst)
			initializeTheKids(worklist, new String[] { "items" });

		return lst;
		}


	public List<GeneratedWorklist> loadByExpIdAndAssayId(String expId, String assayId)
		{
		List<GeneratedWorklist> lst = getEntityManager().createQuery("from GeneratedWorklist d where d.expId = :expId and d.assayId = :assayId order by worklistId desc")
				    .setParameter("expId", expId).setParameter("assayId", assayId).getResultList();
		
		for (GeneratedWorklist worklist : lst)
			initializeTheKids(worklist, new String[] { "items" });
		
		return lst;
		}
	}

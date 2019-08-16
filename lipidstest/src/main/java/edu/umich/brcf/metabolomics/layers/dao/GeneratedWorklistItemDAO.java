//GeneratedWorklistItemDAO.java
//Written by Jan Wigginton, July 2015

package edu.umich.brcf.metabolomics.layers.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.stereotype.Repository;

import edu.umich.brcf.metabolomics.layers.domain.GeneratedWorklistItem;
import edu.umich.brcf.shared.layers.dao.BaseDAO;



@Repository
public class GeneratedWorklistItemDAO extends BaseDAO 
	{
	public void createGeneratedWorklistItem(GeneratedWorklistItem item)
		{
		getEntityManager().persist(item);
		initializeTheKids(item, new String [] { "worklist" });
		}

	
	public void deleteGeneratedWorklistItem(GeneratedWorklistItem item)
		{
		getEntityManager().remove(item);
		}
	
	
	public GeneratedWorklistItem loadById(Long id)
		{
		GeneratedWorklistItem item = getEntityManager().find(GeneratedWorklistItem.class, id);
		initializeTheKids(item, new String [] { "worklist" });
		
		return item;
		}
	
	
	public List<GeneratedWorklistItem> loadByWorklistId(String worklistId)
		{
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("id", worklistId);
		List<GeneratedWorklistItem> lst =  getEntityManager().createQuery("from GeneratedWorklistItem d where d.worklist.worklistId = :id order by itemId")
				.setParameter("id", worklistId).getResultList();
		
		for( GeneratedWorklistItem item : lst)
			initializeTheKids(item, new String[]{"worklist"});
		
		return lst;
		}

	
	public List<GeneratedWorklistItem> loadCommentedByWorklistId(String worklistId)
		{
		List<GeneratedWorklistItem> lst =  getEntityManager().createQuery("from GeneratedWorklistItem d where d.worklist.worklistId = :id and d.comments is not null order by itemId")
				.setParameter("id", worklistId).getResultList();
		
		for( GeneratedWorklistItem item : lst)
			initializeTheKids(item, new String[]{"worklist"});
		
		return lst;
		}

	
	public List<GeneratedWorklistItem> loadAll()
		{
		List <GeneratedWorklistItem> lst = new ArrayList<GeneratedWorklistItem>();
		return lst;
		}
	
	
	public List<GeneratedWorklistItem> loadForExperimentId()
		{
		List <GeneratedWorklistItem> lst = new ArrayList<GeneratedWorklistItem>();
		return lst;
		}
	}

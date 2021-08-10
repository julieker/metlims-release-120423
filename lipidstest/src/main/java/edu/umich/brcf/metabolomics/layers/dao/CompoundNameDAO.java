///////////////////////////////////////////
// Writtten by Anu Janga
// Revisited by Julie Keros November 2016
///////////////////////////////////////////


package edu.umich.brcf.metabolomics.layers.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.Hibernate;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import edu.umich.brcf.metabolomics.layers.domain.CompoundName;
import edu.umich.brcf.shared.layers.dao.BaseDAO;

@Repository
public class CompoundNameDAO extends BaseDAO
	{
	public void save(CompoundName cname)
		{
		getEntityManager().persist(cname);
		}
		
	public void delete(CompoundName cname)
		{
		getEntityManager().remove(cname);
		getEntityManager().getTransaction().commit();
		}
	
	public List<CompoundName> allCompoundNames()
		{
		List<CompoundName> nameList = getEntityManager().createQuery("from CompoundName cn where cn.compound.cid like 'C%'").getResultList();
		
		for( CompoundName name : nameList)
			initializeTheKids(name, new String[]{"compound"});		
		return nameList;
		}
	
	public List<CompoundName> getMatchingNames(String str)
		{
		Query query = getEntityManager().createQuery("from CompoundName cn where cn.compound.cid like 'C%' and lower(cn.name) like '%"+str.toLowerCase()+"%' order by length(cn.name)");
		query.setMaxResults(50);
		List<CompoundName> nameList = query.getResultList();		
		for( CompoundName name : nameList)
			initializeTheKids(name, new String[]{"compound"});		
		return nameList;
		}
	
	// issue 48
	public List<String> getMatchingNamesCompoundId(String str)
		{
		// issue 158
		Query query = getEntityManager().createNativeQuery("select name || ' CID:' || cid from names where (cid like 'C%' or cid like 'D%' ) and lower(name) like '%"+str.toLowerCase()+"%' order by length(name)");
		query.setMaxResults(50);		
		List<String> nameList = query.getResultList();		
		/*for( CompoundName name : nameList)
			initializeTheKids(name, new String[]{"compound"});	*/	
		return nameList;
		}
	
	public CompoundName loadByName(String name)
		{
		List<CompoundName> lst =  getEntityManager().createQuery("from CompoundName cn where cn.compound.cid like 'C%' and trim(cn.name) = :name")
			    .setParameter("name", name.trim()).getResultList();
		
		CompoundName cmpdName;
		try
			{
			cmpdName = (CompoundName)DataAccessUtils.requiredSingleResult(lst);
			initializeTheKids(cmpdName, new String[]{"compound"});
			Hibernate.initialize(cmpdName.getCompound().getParent());
			}
		catch(Exception e){ cmpdName=null; }
	
		return cmpdName;
		}
	
	// issue 48
	// issue 158
	public CompoundName loadByNameCompoundId(String name)
	    {
		if (name.indexOf("CID:") < 0)
			return null;
		String parsedName = name.substring(0,name.lastIndexOf("CID:"));
		// issue 158
		parsedName = parsedName.replaceAll("''", "'");
		String parsedCid = name.substring(name.lastIndexOf("CID:")+4);
		List<CompoundName> lst =  getEntityManager().createQuery("from CompoundName cn where (cn.compound.cid like 'C%' or cn.compound.cid like 'D%') and trim(cn.name) = trim(?1) and trim(cn.compound.cid) = trim(?2)")
			    .setParameter(1, parsedName).setParameter(2, parsedCid).getResultList();		
		CompoundName cmpdName;
		try
			{
			cmpdName = (CompoundName)DataAccessUtils.requiredSingleResult(lst);
			initializeTheKids(cmpdName, new String[]{"compound"});
			Hibernate.initialize(cmpdName.getCompound().getParent());
			}
		catch(Exception e){ cmpdName=null; }
		return cmpdName;
		}
	
	public List<CompoundName> loadByCid(String cid)
		{
		List<CompoundName> lst =  getEntityManager().createQuery("from CompoundName cn where cn.compound.cid = :cid")
				.setParameter("cid", cid).getResultList();
		
		for( CompoundName cname : lst)
			initializeTheKids(cname, new String[]{"compound"});
		
		return lst;
		}
	
	// issue 16
	public void updateName(String cid, String name, String oldName, String type, String html)
		{
		Query query = getEntityManager().createNativeQuery("update names set name= ?2 , type = ?4, html=?5 where cid= ?1 and name=?3 "  )
				.setParameter(1, cid).setParameter(2, name).setParameter(3, oldName).setParameter(4, type).setParameter(5,  html);
		int i = query.executeUpdate();
		}
	
	
	public CompoundName loadName(String cid, String name)
		{
		List<CompoundName> lst =  getEntityManager().createQuery("from CompoundName cn where cn.compound.cid = :cid and cn.name = :name")
				.setParameter("cid", cid).setParameter("name", name).getResultList();				
		CompoundName cmpdName = (CompoundName)DataAccessUtils.requiredSingleResult(lst);
		initializeTheKids(cmpdName, new String[]{"compound"});
		return cmpdName;
		}
	}

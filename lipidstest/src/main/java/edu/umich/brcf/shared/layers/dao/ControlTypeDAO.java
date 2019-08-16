/////////////////////////////////////////
// ControlTypeDAO.java
// Written by Jan Wigginton May 2015
/////////////////////////////////////////

package edu.umich.brcf.shared.layers.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

//import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.stereotype.Repository;

import edu.umich.brcf.shared.layers.domain.ControlType;


@Repository
public class ControlTypeDAO extends BaseDAO
	{
	public ControlType loadById(String controlTypeId) 
		{
		return (getEntityManager().find(ControlType.class, controlTypeId));
		}

	
	public String descriptionForId(String controlTypeId)
		{
		return loadById(controlTypeId).getDescription();
		}
	
	
	public String platformForId(String controlTypeId)
		{
		return loadById(controlTypeId).getPlatformId();
		}
	
	
	public List<String> descriptionsForPlatformId(String platformId)
		{
		List <String> descriptions = new ArrayList<String>();
		return descriptions;
		}
	
	
	public List<String> descriptionsForAbsciex()
		{
		return descriptionsForPlatformId("PL002");
		}
	
	
	public List<String> descriptionsForAgilent()
		{
		return descriptionsForPlatformId("PL001");
		}
	
	
	public List<String> allControlTypeIds()
		{
		Query query2 = getEntityManager().createNativeQuery("select cast(s.control_type_id as VARCHAR2(6)) from Control_Type s "
				+ " order by s.control_type_id desc");
	
		List<String> idList =  query2.getResultList();
		return idList;
		}
	
	
	public List <String> allControlTypeIdsForPlatformId(String platId)
		 {
		 Query query2 = getEntityManager().createNativeQuery("select cast(s.control_type_id as VARCHAR2(6)) from Control_Type s "
				   		+ "where platform_id = ?1 "
				   		+ "order by s.control_type_id desc").setParameter(1, platId);
		 
		List<String> idList =  query2.getResultList();
		return idList;	 
		}

	 
	 public List<String> allControlTypeNames()
		{
		Query query2 = getEntityManager().createNativeQuery("select cast(s.control_type_name as VARCHAR2(50)) from Control_Type s "
				+ " order by s.control_type_id desc");
	
		List<String> idList =  query2.getResultList();
		
		return idList;
		}
	
		
	 public List <String> allControlTypeNamesForPlatformId(String platId)
	 	{
		Query query2 = getEntityManager().createNativeQuery("select cast(s.control_type_name as VARCHAR2(50)) from Control_Type s "
				   		+ " where platform_id = ?1  order by s.control_type_id desc").setParameter(1, platId);
		
		List<String> idList =  query2.getResultList();
		return idList;	 
	 	} 
	}
	
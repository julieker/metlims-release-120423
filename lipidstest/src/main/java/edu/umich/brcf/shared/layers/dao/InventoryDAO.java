////////////////////////////////////////////////////
// InventoryDAO.java
// Written by Jan Wigginton, Jul 11, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.layers.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.Hibernate;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import edu.umich.brcf.shared.layers.domain.Inventory;
import edu.umich.brcf.shared.layers.domain.Location;



@Repository
public class InventoryDAO extends BaseDAO
	{
	public Inventory createInventory(Inventory inv)
		{
		getEntityManager().persist(inv);
		Query query = getEntityManager().createQuery("select max(inventoryId) from Inventory");
		String invId = (String) query.getSingleResult();
		return loadByIdForAdd(invId);
		}
	
	
	public Inventory loadById(String id)
		{
		List<Inventory> lst =  getEntityManager()
				.createQuery("from Inventory i where i.active not in ('I') and i.inventoryId = :id")//Inventory.class)
				.setParameter("id", id).getResultList();
		
		Inventory inv  = (Inventory) DataAccessUtils.requiredSingleResult(lst);
		initializeTheKids(inv, new String[]{"compound"});
		initializeTheKids(inv, new String[]{"location"});
		return inv;
		}
	
	public Inventory loadByIdForAdd(String id)
		{
		List<Inventory> lst =  getEntityManager().createQuery("from Inventory i where  i.inventoryId = :id")//Inventory.class)
				.setParameter("id", id).getResultList();
		
		Inventory inv  = (Inventory) DataAccessUtils.requiredSingleResult(lst);
		initializeTheKids(inv, new String[]{"compound"});
		initializeTheKids(inv, new String[]{"location"});
		return inv;
		}
	
	
	public List<Inventory> loadByCid(String cid)
		{
		List<Inventory> lst =  getEntityManager()
				.createQuery("from Inventory i where i.active not in ('I') and i.compound.cid = :cid")
				.setParameter("cid", cid).getResultList();
		
		for( Inventory inv : lst)
			{
			initializeTheKids(inv, new String[]{"compound"});
			initializeTheKids(inv, new String[]{"location"});
			}
		return lst;
		}
	
	
	public List<Inventory> loadByCan(String can)
		{
		List<Inventory> lst =  getEntityManager()
				.createQuery("from Inventory i where i.active not in ('I') and i.compound.chem_abs_number = :can")
				.setParameter("can", can).getResultList();
		
		for( Inventory inv : lst)
			{
			initializeTheKids(inv, new String[]{"compound"});
			initializeTheKids(inv, new String[]{"location"});
			}
		return lst;
		}
	
	public List<String> getMatchingInvIds(String input)
		{
		Query query = getEntityManager().createQuery("select distinct i.inventoryId from Inventory i where i.inventoryId like '"+input+"%'");
		List<String> invIdList = query.getResultList();
		return invIdList;
		}
	
	public Location getLocationById(String id) 
		{
		Location loc =  getEntityManager().find(Location.class, id);
		//Hibernate.initialize(inv.getLocation());//initializeTheKids(inv, new String[]{"location"});
		return loc;//inv.getLocation();
		}
	
	
	public List<String> allSuppliers() 
		{
		Query query = getEntityManager().createNativeQuery("select suppliername from supplier");
		query.setMaxResults(50);
		List<String> supList = query.getResultList();
		return supList;
		}

	
	public List<Inventory> getActiveInventory() 
		{
		List<Inventory> inventoryLst = getEntityManager().createQuery("from Inventory i ").getResultList();
		
		for (Inventory inv : inventoryLst) 
			{
			initializeTheKids(inv, new String[] { "compound", "location" });
			Hibernate.initialize(inv.getCompound().getNames());
			}
		return inventoryLst;
		}
	}

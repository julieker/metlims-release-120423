///////////////////////////////////////
// InventoryService.java
// Rewritten by Jan Wigginton 10/26/16
///////////////////////////////////////

package edu.umich.brcf.metabolomics.layers.service;


import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.metabolomics.layers.dao.CompoundDAO;
import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.shared.layers.dao.InventoryDAO;
import edu.umich.brcf.shared.layers.domain.Inventory;
import edu.umich.brcf.shared.layers.domain.Location;
import edu.umich.brcf.shared.layers.dto.InventoryDTO;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.utilpackages.NumberUtils;



@Transactional
public class InventoryService 
	{
	InventoryDAO inventoryDao;
	CompoundDAO compoundDao;
	
	
	public Inventory save(InventoryDTO invDto, String assignedInvId)
		{
		Assert.notNull(invDto);
		Compound cmpd = null;
		// issue 53
		if (!StringUtils.isEmptyOrNull(assignedInvId))
			invDto.setInventoryId(assignedInvId);
		try
			{
			cmpd=compoundDao.loadCompoundById(invDto.getCid());
			cmpd.getCid();
			}
		catch (Exception e)
			{
			throw new RuntimeException("Compound id must refer to an existing compound");
			}
		
		Location loc = null;
		try
			{
			loc=inventoryDao.getLocationById(invDto.getLocId());
			loc.getDescription();
			}
		catch (Exception e) 
			{
			throw new RuntimeException("Location id " + invDto.getLocId() + " isn't a valid location id");
			}
		
		String sPur = invDto.getPurity().toString();
		 // issue 56
		if (!NumberUtils.verifyDecimalRange(sPur, 3, 2)) 
	    	{
		    throw new RuntimeException("The purity: "  + sPur +
    	            " must have a whole number of no more than 3 digits and a decimal of no more than 2 digits");
	    	}		
		Inventory inv = null;
		if (invDto.getInventoryId() != null && !"to be assigned".equals(invDto.getInventoryId()))
		    try	
			    {
				inv = inventoryDao.loadById(invDto.getInventoryId());
				inv.update(invDto, cmpd,loc);
				}
			catch(Exception e) 
				{
				e.printStackTrace(); 
				inv = null;
				throw new RuntimeException("Can't load inventory by id " + invDto.getInventoryId());
				}
		else
			try
				{
				inv = Inventory.instance(invDto.getActive().charAt(0),
						invDto.getBotSize(), invDto.getCatNum(), cmpd, loc, invDto.getSupplier(), invDto.getPurity());
				inv=inventoryDao.createInventory(inv);
				}
			catch(Exception e) { e.printStackTrace(); inv = null; }

		return inv;
		}
	
	
	public List<String> allSuppliers()
		{
		return inventoryDao.allSuppliers();
		}

	public List<Inventory> getActiveInventory()
		{
		return inventoryDao.getActiveInventory();
		}
	
	public void setCompoundDao(CompoundDAO compoundDao) 
		{
		this.compoundDao = compoundDao;
		}

	public Inventory loadById(String id)
		{
		Assert.notNull(id);
		return inventoryDao.loadById(id);
		}

	public List<Inventory> loadByCid(String cid)
		{
		Assert.notNull(cid);
		return inventoryDao.loadByCid(cid);
		}
	
	public List<Inventory> loadByCan(String can)
		{
		Assert.notNull(can);
		return inventoryDao.loadByCan(can);
		}
	
	public List<String> getMatchingInvIds(String input)
		{
		return inventoryDao.getMatchingInvIds(input);
		}
	
	public void setInventoryDao(InventoryDAO inventoryDao) 
		{
		this.inventoryDao = inventoryDao;
		}

	}

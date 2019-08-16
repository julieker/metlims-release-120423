package edu.umich.brcf.shared.layers.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import edu.umich.brcf.shared.layers.domain.Inventory;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;



public class InventoryDTO implements Serializable
	{
	private String inventoryId;
	private String active;//Character active;
	private String botSize;
	private String catNum;
	private String cid;
	private String invDate;//Calendar invDate;
	private String locId;
	private String supplier;
	private BigDecimal purity;
	
	private InventoryDTO(String code, Character active, String contSize, String catalogNumber, String cid,
		Calendar inventoryDate, String locid, String supplier, BigDecimal purity) 
		{
		this.inventoryId = code;
		this.active = active+"";
		this.botSize = contSize;
		this.catNum = catalogNumber;
		this.cid = cid;
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		this.invDate = sdf.format(inventoryDate.getTime());
		this.locId = locid;
		this.supplier = supplier;
		this.purity = purity;
		}
	
	public InventoryDTO() {  }
	
	public static InventoryDTO instance(Inventory inv)
		{
		return new InventoryDTO(inv.getInventoryId(), inv.getActive(), inv.getContainerSize(), inv.getCatalogueNumber(), inv.getCompound().getCid(),
		inv.getInventoryDate(), inv.getLocation().getLocationId(), inv.getSupplier(), inv.getPurity());
		}
	
	public String getInventoryId() 
		{
		return inventoryId;
		}
	
	
	public void setInventoryId(String inventoryId) 
		{
		this.inventoryId = inventoryId;
		}
	
	public String getBotSize() 
		{
		return botSize;
		}
	
	public void setBotSize(String botSize) 
		{
		this.botSize = botSize;
		}
	
	public String getCatNum() 
		{
		if (!StringUtils.isNullOrEmpty(catNum))
			return catNum.toUpperCase();
	
		return catNum;
		}
	
	public void setCatNum(String catNum) 
		{
		this.catNum = catNum;
		}
	
	public String getCid() 
		{
		return cid;
		}
	
	public void setCid(String cid) 
		{
		this.cid = cid;
		}
	
	public String getLocId() 
		{
		if (!StringUtils.isNullOrEmpty(locId))
			return locId.toUpperCase();
		
		return locId;
		}
	
	
	public void setLocId(String locId) 
		{
		this.locId = locId;
		}
	
	
	public String getSupplier() 
		{
		return supplier;
		}
	
	
	public void setSupplier(String supplier) 
		{
		this.supplier = supplier;
		}

	
	public String getActive() 
		{
		return active;
		}

	
	public void setActive(String active) 
		{
		this.active = active;
		}

	
	public String getInvDate() 
		{
		return invDate;
		}

	
	public void setInvDate(String invDate) 
		{
		this.invDate = invDate;
		}

	
	public BigDecimal getPurity() 
		{
		return purity;
		}

	
	public void setPurity(BigDecimal purity) 
		{
		this.purity = purity;
		}
	}

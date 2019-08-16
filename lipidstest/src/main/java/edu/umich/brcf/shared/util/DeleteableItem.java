package edu.umich.brcf.shared.util;

import java.io.Serializable;


public class DeleteableItem implements Serializable
	{
	private String itemName;
	
	
	public DeleteableItem(String name)
		{
		this.itemName = name;
		}
	
	
	public String getItemName()
		{
		return itemName;
		}
	
	
	public void setItemName(String name)
		{
		this.itemName = name;
		}
	
	
	public boolean equals(DeleteableItem item)
		{
		if (item == null || item.getItemName() == null) return false;
		
		return (item.getItemName().equals((this.itemName)));
		}
	}
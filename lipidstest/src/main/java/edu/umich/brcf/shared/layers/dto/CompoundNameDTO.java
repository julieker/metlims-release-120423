////////////////////////////////////////////////////
// CompoundNameDTO.java
// Written by Jan Wigginton, Jul 11, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.layers.dto;

import java.io.Serializable;

import edu.umich.brcf.metabolomics.layers.domain.CompoundName;



public class CompoundNameDTO implements Serializable
	{
	private String cid;
	private String newName;
	private String name;
	private String type;
	private String html;
	
	private CompoundNameDTO(String cid, String newName, String name, String type, String html)
		{
		this.cid=cid;
		this.newName=newName;
		this.name=name;
		this.type=type;
		this.html=html;
		}
	
	public static CompoundNameDTO instance(String cid, String newName, String name, String type, String html)
		{
		return new CompoundNameDTO(cid, newName, name, type, html);
		}
	
	public static CompoundNameDTO instance(CompoundName cname)
		{
		return new CompoundNameDTO(cname.getCompound().getCid(), cname.getName(), cname.getName(), cname.getNameType(), cname.getHtml());
		}
	
	public CompoundNameDTO()   {   }
	
	public String getCid() 
		{
		return cid;
		}
	
	public void setCid(String cid) 
		{
		this.cid = cid;
		}
	
	public String getNewName() 
		{
		return newName;
		}
	
	public void setNewName(String newName) 
		{
		this.newName = newName;
		}
	
	public String getName() 
		{
		return name;
		}
	
	public void setName(String name) 
		{
		this.name = name;
		}
	
	public String getType() 
		{
		return type;
		}
	
	public void setType(String type) 
		{
		this.type = type;
		}
	
	public String getHtml() 
		{
		return html;
		}
	
	public void setHtml(String html) 
		{
		this.html = html;
		}
	}

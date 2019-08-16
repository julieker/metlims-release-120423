package edu.umich.brcf.shared.layers.dto;

import java.io.Serializable;

import edu.umich.brcf.shared.layers.domain.Client;


public class ClientDTO implements Serializable
	{
	public static ClientDTO instance(String dept, String lab, String organizationID, String investigatorID, String contact)
		{
		return new ClientDTO(null, dept, lab, organizationID, investigatorID, contact);
		}

	
	public static ClientDTO instance(Client client)
		{
		return new ClientDTO(client.getClientID(), client.getDept(), client.getLab(), 
				client.getOrganizationID(), client.getInvestigator().getId(), client.getContact().getId());
		}

	
	private String id;
	private String dept;
	private String lab;
	private String organizationID;
	private String investigatorID;
	private String contact;

	
	private ClientDTO(String id, String dept, String lab, String organizationID, String investigatorID, String contact)
		{
		this.id = id;
		this.dept = dept;
		this.lab = lab;
		this.organizationID = organizationID;
		this.investigatorID = investigatorID;
		this.contact = contact;
		}

	
	public ClientDTO() { } 

	
	public String getId()
		{
		return id;
		}

	public void setId(String id)
		{
		this.id = id;
		}

	public String getDept()
		{
		return dept;
		}

	public void setDept(String dept)
		{
		this.dept = dept;
		}

	public String getLab()
		{
		return lab;
		}

	public void setLab(String lab)
		{
		this.lab = lab;
		}

	public String getOrganizationID()
		{
		return organizationID;
		}

	public void setOrganizationID(String organizationID)
		{
		this.organizationID = organizationID;
		}

	public String getInvestigatorID()
		{
		return investigatorID;
		}

	public void setInvestigatorID(String investigatorID)
		{
		this.investigatorID = investigatorID;
		}

	public String getContact()
		{
		return contact;
		}

	public void setContact(String contact)
		{
		this.contact = contact;
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//OrganizationDTO.java
//Written by Jan Wigginton September 2015
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.shared.layers.dto;

import java.io.Serializable;

import edu.umich.brcf.shared.layers.domain.Organization;


public class OrganizationDTO implements Serializable
	{
	private String org_id;
	private String orgName;
	private String orgAddress;

	public static OrganizationDTO instance(String orgName, String orgAddress)
		{
		return new OrganizationDTO(null, orgName, orgAddress);
		}

	private OrganizationDTO(String org_id, String orgName, String orgAddress)
		{
		this.org_id = org_id;
		this.orgName = orgName;
		this.orgAddress = orgAddress;
		}

	public OrganizationDTO(Organization org)
		{
		this.org_id = org.getOrganizationId();
		this.orgName = org.getOrgName();
		this.orgAddress = org.getOrgAddress();
		}

	public OrganizationDTO()
		{
		}

	public String getOrg_id()
		{
		return org_id;
		}

	public void setOrg_id(String org_id)
		{
		this.org_id = org_id;
		}

	public String getOrgName()
		{
		return orgName;
		}

	public void setOrgName(String orgName)
		{
		this.orgName = orgName;
		}

	public String getOrgAddress()
		{
		return orgAddress;
		}

	public void setOrgAddress(String orgAddress)
		{
		this.orgAddress = orgAddress;
		}
	}

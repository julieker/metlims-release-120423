package edu.umich.brcf.shared.layers.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.shared.layers.dao.OrganizationDAO;
import edu.umich.brcf.shared.layers.domain.Organization;
import edu.umich.brcf.shared.layers.dto.OrganizationDTO;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.io.StringUtils;


@Transactional
public class OrganizationService
	{
	OrganizationDAO organizationDao;

	
	// Specifically updates organization only when called from an edit
	public Organization update(OrganizationDTO orgDto)
		{
		Assert.notNull(orgDto);
		
		String orgName = orgDto.getOrgName();
		
		if (StringUtils.isEmptyOrNull(orgName)) 
			return null;
		
		if (organizationDao.checkOrgNameExistsAndIsNotSameItem(orgDto))
			throw new RuntimeException("Duplicate : An organization with this name alreday exists in database. Please use existing entry, or choose another name");

			
		Organization org = null;
		
		try
			{
			org  = organizationDao.loadById(orgDto.getOrg_id());
			org.update(orgDto);
			}
		catch (Exception e) { e.printStackTrace(); org = null; }

		
		return org;
		}
	
	
	// Called from "create".  Want to save only if doesn't already exist. Error thrown (without update) if duplicate exists.
	public Organization save(OrganizationDTO orgDto)
		{
		Assert.notNull(orgDto);
		
		String orgName = orgDto.getOrgName();
		
		if (StringUtils.isEmptyOrNull(orgName)) 
			return null;
	
		Organization org = null;
		
		if (!orgExists(orgName))
			try
				{
				org = Organization.instance(null, orgName, orgDto.getOrgAddress());
				org.getOrgAddress();
				organizationDao.createOrganization(org);
				}
			catch (Exception ee)  { ee.printStackTrace();  org = null; }
		else
			throw new RuntimeException("Duplicate organization exists in database. Please use existing entry, or choose another name");

		return org;
		}

	
	public Boolean orgExists(String name)
		{
		List<Organization> orgList = organizationDao.loadByName(name);
		return (orgList.size() > 0);
		}
	

	public List<String> allOrganizations()
		{
		return organizationDao.allOrganizations();
		}

	
	public List<Organization> allOrganizationObjects()
		{
		return organizationDao.allOrganizationObjects();
		}
	
	
	public Organization loadById(String org_id)
		{
		return organizationDao.loadById(org_id);
		}

	
	public void setOrganizationDao(OrganizationDAO organizationDao)
		{
		this.organizationDao = organizationDao;
		}
	

	public boolean isValidOrganizationSearch(String org)
		{
		Organization organization;
		if (StringUtils.isEmptyOrNull(org))
			return false;

		if (FormatVerifier.verifyFormat(Organization.idFormat, org.toUpperCase()))
			try
				{
				organization = organizationDao.loadById(org);
				organization.getOrgAddress();
				} 
			catch (Exception e) { organization = null; } 
		else
			try
				{
				organization = organizationDao.loadById(StringParser.parseId(org));
				organization.getOrgAddress();
				} 
			catch (Exception e) { organization = null; } 
		
		return (organization != null);
		}
	
	
	public String getOrganizationIdForSearchString(String str, String label)
		{
		if (str == null) 
			throw new RuntimeException("Experiment string string can't be null");
		
		
		String orgId = str;
		if(!FormatVerifier.verifyFormat(Organization.idFormat,str.toUpperCase()))
			orgId = StringParser.parseId(str);
		
		if(!FormatVerifier.verifyFormat(Organization.idFormat,orgId.toUpperCase()))
			{
			try  
				{
				List<Organization> org = organizationDao.loadByName(str);
				if (org.size() < 1)
					throw new RuntimeException("Experiment load error : cannot find organization  " + label + " "  + str);
						
				orgId = org.get(0).getOrganizationId();
				}
			catch (Exception e)
				{
				throw new RuntimeException("Experiment load error : cannot find organization  " + label + " "  + str);
				}
			}
		
		return orgId;
		} 
	}

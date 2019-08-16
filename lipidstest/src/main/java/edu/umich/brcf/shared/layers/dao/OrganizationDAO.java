package edu.umich.brcf.shared.layers.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import edu.umich.brcf.shared.layers.domain.Organization;
import edu.umich.brcf.shared.layers.dto.OrganizationDTO;
import edu.umich.brcf.shared.util.io.StringUtils;



@Repository
public class OrganizationDAO extends BaseDAO 
	{
	public void createOrganization(Organization org) 
		{
		getEntityManager().persist(org);
		}

	
	public List<String> allOrganizations() 
		{
		Query query = getEntityManager().createNativeQuery("select t.name||' ('||t.organization_id||')' from organization t order by t.name");
		return query.getResultList();
		}

	
	public List<Organization> allOrganizationObjects()
		{
		List<Organization> orgList = getEntityManager().createQuery("from Organization").getResultList();
		return orgList;
		}
	
	
	public boolean checkOrgNameExistsAndIsNotSameItem(OrganizationDTO dto)
		{
		if (dto == null)
			return false;
		
		String currentId = dto.getOrg_id();
		String name = dto.getOrgName();
		
		Query query = getEntityManager().createNativeQuery("select * from organization e where e.name = ?1 and e.organization_id <> ?2").setParameter(1, name).setParameter(2, currentId);
		return (query.getResultList().size() > 0);
		}
	
	
	public Organization loadById(String org_id) 
		{
		Organization org = getEntityManager().find(Organization.class, org_id);
		return org;
		}
	
	
	public List<Organization> loadByName(String name) 
		{
		if (StringUtils.isEmptyOrNull(name))
			return new ArrayList<Organization>();
		
		List<Organization> orgList =   getEntityManager().createQuery("from Organization o where o.orgName = ?1").setParameter(1, name).getResultList();
		
		return orgList;
		}
	}

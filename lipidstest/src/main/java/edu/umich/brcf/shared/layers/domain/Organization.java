package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import edu.umich.brcf.shared.layers.dto.OrganizationDTO;


@Entity()
@Table(name = "ORGANIZATION")
public class Organization implements Serializable
	{

	public static String idFormat = "(OR)\\d{4}";

	public static Organization instance(String organizationId, String orgName, String orgAddress)
		{
		return new Organization(organizationId, orgName, orgAddress);
		}

	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
			@Parameter(name = "idClass", value = "Organization"), @Parameter(name = "width", value = "6") })
	@Column(name = "ORGANIZATION_ID", unique = true, nullable = false, length = 6, columnDefinition = "CHAR(6)")
	private String organizationId;

	@Basic()
	@Column(name = "NAME", nullable = false, columnDefinition = "VARCHAR2(120)")
	private String orgName;

	@Basic()
	@Column(name = "ADDRESS", nullable = true, columnDefinition = "VARCHAR2(100)")
	private String orgAddress;

	public Organization()  {  }

	
	private Organization(String organizationId, String orgName, String orgAddress)
		{
		this.organizationId = organizationId;
		this.orgName = orgName;
		this.orgAddress = orgAddress;
		}

	
	public void update(OrganizationDTO dto)
		{
		this.orgAddress = dto.getOrgAddress();
		this.orgName = dto.getOrgName();
		}
	
	public static String getIdFormat()
		{
		return idFormat;
		}

	public static void setIdFormat(String idFormat)
		{
		Organization.idFormat = idFormat;
		}

	public String getOrganizationId()
		{
		return organizationId;
		}

	public void setOrganizationId(String organizationId)
		{
		this.organizationId = organizationId;
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

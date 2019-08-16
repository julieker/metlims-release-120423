///////////////////////////////////////////
//ClientDataInfo.java/
//Written by Jan Wigginton, December 2015
////////////////////////////////////////////

package edu.umich.brcf.shared.util.datacollectors;

import java.io.Serializable;
import edu.umich.brcf.shared.util.ObjectHandler;


public class ClientDataInfo implements Serializable
	{
	private String organizationName = "", organizationAddress = "", labName = "", deptName = "", contactPerson = "", contactEmail = "";
	private String contactPhone = "", piName = "", piEMail = "", piPhone = "";
	private String experimentDate = "", experimentName = "", experimentId = "", experimentDescription = "";
	private String projectName = "", projectDescription = "", shortCode = "", umServiceRequestId = "";
	private String nihGrantNumber = "", nihGrantNumber2 = "", nihGrantNumber3 = "";
	
	public ClientDataInfo()
		{
		}
	
	
	public String getOrganizationName() 
		{
		return organizationName;
		}
	
	
	public String getLabName() 
		{
		return labName;
		}
	
	
	public String getDeptName() 
		{
		return deptName;
		}
	
	
	public String getContactPerson() 
		{
		return contactPerson;
		}
	
	
	public String getContactEmail() 
		{
		return contactEmail;
		}
	
	
	public String getContactPhone() 
		{
		return contactPhone;
		}
	
	
	public String getPiName() 
		{
		return piName;
		}
	
	
	public String getPiEMail() 
		{
		return piEMail;
		}
	
	
	public String getPiPhone() 
		{
		return piPhone;
		}
	
	public String getExperimentDate() 
		{
		return experimentDate;
		}
	
	
	public String getExperimentName() 
		{
		return experimentName;
		}
	
	
	public String getExperimentId() 
		{
		return experimentId;
		}
	
	
	public String getProjectName() 
		{
		return projectName;
		}
	
	
	public String getProjectDescription() 
		{
		return projectDescription;
		}
	
	
	public String getShortCode() 
		{
		return shortCode;
		}
	
	
	public String getNihGrantNumber() 
		{
		return nihGrantNumber;
		}
	
	
	public String getUmServiceRequestId() 
		{
		return umServiceRequestId;
		}
	
	
	public void setOrganizationName(String organizationName) 
		{
		this.organizationName = organizationName;
		}
	
	
	public void setLabName(String labName) 
		{
		this.labName = labName;
		}
	
	
	public void setDeptName(String deptName) 
		{
		this.deptName = deptName;
		}
	
	
	public void setContactPerson(String contactPerson) 
		{
		this.contactPerson = contactPerson;
		}
	
	
	public void setContactEmail(String contactEmail) 
		{
		this.contactEmail = contactEmail;
		}
	
	
	public void setContactPhone(String contactPhone) 
		{
		this.contactPhone = contactPhone;
		}
	
	
	public void setPiName(String piName) 
		{
		this.piName = piName;
		}
	
	
	public void setPiEMail(String piEMail) 
		{
		this.piEMail = piEMail;
		}
	
	
	public void setPiPhone(String piPhone) 
		{
		this.piPhone = piPhone;
		}
	
	
	public void setExperimentDate(String date) 
		{
		this.experimentDate = date;
		}
	
	
	public void setExperimentName(String experimentName) 
		{
		this.experimentName = experimentName;
		}
	
	
	public void setExperimentId(String experimentId) 
		{
		this.experimentId = experimentId;
		}
	
	
	public void setProjectName(String projectName) 
		{
		this.projectName = projectName;
		}
	
	
	public void setProjectDescription(String projectDescription) 
		{
		this.projectDescription = projectDescription;
		}
	
	
	public void setShortCode(String shortCode) 
		{
		this.shortCode = shortCode;
		}
	
	
	public void setNihGrantNumber(String nihGrantNumber) 
		{
		this.nihGrantNumber = nihGrantNumber;
		}
	
	
	public void setUmServiceRequestId(String UmServiceRequestId) 
		{
		this.umServiceRequestId = umServiceRequestId;
		}
	
	
	
	public String getNihGrantNumber2()
		{
		return nihGrantNumber2;
		}


	public void setNihGrantNumber2(String nihGrantNumber2)
		{
		this.nihGrantNumber2 = nihGrantNumber2;
		}


	public String getNihGrantNumber3()
		{
		return nihGrantNumber3;
		}


	public void setNihGrantNumber3(String nihGrantNumber3)
		{
		this.nihGrantNumber3 = nihGrantNumber3;
		}

	

	public String getOrganizationAddress()
		{
		return organizationAddress;
		}


	public String getExperimentDescription()
		{
		return experimentDescription;
		}


	public void setOrganizationAddress(String organizationAddress)
		{
		this.organizationAddress = organizationAddress;
		}


	public void setExperimentDescription(String experimentDescription)
		{
		this.experimentDescription = experimentDescription;
		}


	/*public Map<String, String> getValueMap()
		{
		Map<String, String> valueMap = new HashMap<String, String>();
		
		valueMap.put("Organization", getOrganizationName());
		valueMap.put("Lab", getLabName());
		valueMap.put("Department", getDeptName());
		valueMap.put("Contact", getContactPerson());
		valueMap.put("Contact E-mail", getContactEmail());
		valueMap.put("Contact Phone", getContactPhone());
		valueMap.put("Principal Investigator", getPiName());
		valueMap.put("PI E-mail", getPiPhone());
		valueMap.put("PI Phone", getOrganizationName());
		valueMap.put("Date", getExperimentDate());
		valueMap.put("Experiment Name", getExperimentName());
		valueMap.put("Experiment Id", getExperimentId());
		valueMap.put("Project Name", getProjectName());
		valueMap.put("Project Description", getProjectDescription());
		valueMap.put("Shortcode", getShortCode());
		valueMap.put("NIH Grant Number", getNihGrantNumber());
		valueMap.put("NIH Grant Number2", getNihGrantNumber2());
		valueMap.put("NIH Grant Number2", getNihGrantNumber3());
		valueMap.put("Service Request Id", getUmServiceRequestId());
		
		return valueMap;
		} */
	
	public String toString()
		{
		
		return ObjectHandler.printObject(this);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
















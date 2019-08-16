// Mrc2ClientDataInfo.java
// Written by Jan Wigginton

package edu.umich.brcf.shared.util.datacollectors;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.ClientService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.OrganizationService;
import edu.umich.brcf.shared.layers.service.ProjectService;
import edu.umich.brcf.shared.layers.service.UserService;





public class Mrc2ClientDataInfo extends ClientDataInfo implements Serializable
	{
	@SpringBean
	ExperimentService experimentService;
	
	@SpringBean
	ProjectService projectService;
	
	@SpringBean
	ClientService clientService;
	
	@SpringBean
	UserService userService;
	
	@SpringBean
	OrganizationService organizationService;
	
	//String organizationName = "", labName = "", deptName = "", contactPerson = "", contactEmail = "";
	//String contactPhone = "", piName = "", piEMail = "", piPhone = "", experimentDate = "", experimentName = "", experimentId = "";
	//String projectName = "", projectDescription = "", shortCode = "", nihGrantNumber = "";
	String serviceRequestId = "";
	

	public Mrc2ClientDataInfo()
		{
		super();
		
		//Injector.get().inject(this);
		}
	
	//public Mrc2ClientDataInfo(String expId) 
	//	{
	//	super(expId);
	//	//initializeFromExpId(expId);
	//	}

 /*
	// this mimics the only changes that are actually made in the submission sheet load
	public void fillInBillingInfoFromSubmissionData(String shortCode, String nihGrantNumbers, String serviceRequestId)
		{
		this.setShortCode(shortCode);
		/// TO DO :  Clean this up -> map multiple to single
		this.setNihGrantNumber(nihGrantNumbers);
		this.setServiceRequestId(serviceRequestId);
		}
	
	public void initializeFromExpId(String expId)
		{
		Experiment exp = experimentService.loadExperimentWithInfoForDrcc(expId);	
		String projectId = exp == null ? "PR0253" : exp.getProject().getProjectID();
		Project  project = projectService.loadById(projectId);
		
		this.setProjectName(project == null ? "" : project.getProjectName());
		this.setProjectDescription(project.getDescription());
		
		String clientId = project.getClient().getClientID();
		Client client = clientService.loadById(clientId);
		String dept = (client == null ? "" : client.getDept());
		this.setDeptName(dept == null ? "" : dept);
		
		final String investigatorUsername = client.getInvestigator().getUserName();
		LoadableDetachableModel<User> investigator = new LoadableDetachableModel<User>()
			{
			@Override
			protected User load() { return (userService.getUserByUserName(investigatorUsername));}
			};
			
		this.setLabName(investigator == null ? "" : investigator.getObject().getLab());
		
		String orgId = client.getOrganizationID();
		Organization organization = organizationService.loadById(orgId);
		
		String orgName = organization == null ? "" : organization.getOrgName();
		this.setOrganizationName(orgName); // == null ? "" : orgName);
		
		List <String> grantSources = experimentService.getGrantInfo(expId);
		this.setNihGrantNumber(grantSources.toString());
		
		this.setPiEMail(investigator == null ? "" : investigator.getObject().getEmail());
		this.setPiPhone(investigator == null ? "" : investigator.getObject().getPhone());
		String piname = (investigator == null ? "" : investigator.getObject().getFullName());
		this.setPiName(piname);
		
		final String contactPersonName = project.getContactPerson().getUserName();
		LoadableDetachableModel<User> contactPerson = new LoadableDetachableModel<User>()
			{
			@Override
			protected User load() { return (userService.getUserByUserName(contactPersonName));}
			};
		
		this.setExperimentName(exp.getExpName());
		this.setContactPerson(contactPerson == null ? "" : contactPerson.getObject().getFullName());
		this.setContactEmail(contactPerson == null ? "" : contactPerson.getObject().getEmail());
		this.setContactPhone(contactPerson == null ? "" : contactPerson.getObject().getPhone());
		
		List<String> shortcodes = experimentService.getShortcodeIdsForExpId(expId);
		this.setShortCode(shortcodes.toString());
		
		this.setServiceRequestId(exp.getServiceRequest());
		
		this.setExperimentId(exp.getExpID());
		this.setExperimentDate(DateUtils.dateStrFromCalendar("MM/dd/yyyy", exp.getCreationDate()));
		}


	public String getOrganizationName() {
		return organizationName;
	}


	public String getLabName() {
		return labName;
	}


	public String getDeptName() {
		return deptName;
	}


	public String getContactPerson() {
		return contactPerson;
	}


	public String getContactEmail() {
		return contactEmail;
	}


	public String getContactPhone() {
		return contactPhone;
	}


	public String getPiName() {
		return piName;
	}


	public String getPiEMail() {
		return piEMail;
	}


	public String getPiPhone() {
		return piPhone;
	}


	public String getExperimentDate() {
		return experimentDate;
	}


	public String getExperimentName() {
		return experimentName;
	}


	public String getExperimentId() {
		return experimentId;
	}


	public String getProjectName() {
		return projectName;
	}


	public String getProjectDescription() {
		return projectDescription;
	}


	public String getShortCode() {
		return shortCode;
	}


	public String getNihGrantNumber() {
		return nihGrantNumber;
	}

 */
	public String getServiceRequestId() {
		return serviceRequestId;
	}

 /*
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}


	public void setLabName(String labName) {
		this.labName = labName;
	}


	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}


	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}


	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}


	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}


	public void setPiName(String piName) {
		this.piName = piName;
	}


	public void setPiEMail(String piEMail) {
		this.piEMail = piEMail;
	}


	public void setPiPhone(String piPhone) {
		this.piPhone = piPhone;
	}


	public void setExperimentDate(String date) {
		this.experimentDate = date;
	}


	public void setExperimentName(String experimentName) {
		this.experimentName = experimentName;
	}


	public void setExperimentId(String experimentId) {
		this.experimentId = experimentId;
	}


	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}


	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}


	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}


	public void setNihGrantNumber(String nihGrantNumber) {
		this.nihGrantNumber = nihGrantNumber;
	}

  */
	
	public void setServiceRequestId(String serviceRequestId) {
		this.serviceRequestId = serviceRequestId;
	}
	
	public Map<String, String> getValueMap()
		{
		String organizationName = "", labName = "", deptName = "", contactPerson = "", contactEmail = "";
		String contactPhone = "", piName = "", piEMail = "", piPhone = "", experimentDate = "", experimentName = "", experimentId = "";
		String projectName = "", projectDescription = "", shortCode = "", nihGrantNumber = "", serviceRequestId = "";
		
		Map<String, String> valueMap = new HashMap<String, String>();
		
		valueMap.put("Organization", getOrganizationName());
		valueMap.put("Lab", getLabName());
		valueMap.put("Department", getDeptName());
		valueMap.put("Contact", getContactPerson());
		valueMap.put("Contact E-mail", getContactEmail());
		valueMap.put("Contact Phone", getContactPhone());
		valueMap.put("Principal Investigator", getPiName());
		valueMap.put("PI E-mail", getPiEMail());
		valueMap.put("PI Phone", getPiPhone());
		valueMap.put("Date", getExperimentDate());
		valueMap.put("Experiment Name", getExperimentName());
		valueMap.put("Experiment Id", getExperimentId());
		valueMap.put("Project Name", getProjectName());
		valueMap.put("Project Description", getProjectDescription());
		valueMap.put("Shortcode", getShortCode());
		valueMap.put("NIH Grant Number", getNihGrantNumber());
		valueMap.put("Service Request Id", getServiceRequestId());
		
		return valueMap;
		}
	
	public String toString()
		{
		return getValueMap().toString();
		}
	}






































/*
public class Mrc2ClientDataInfo implements Serializable
	{
	@SpringBean
	ExperimentService experimentService;
	
	@SpringBean
	ProjectService projectService;
	
	@SpringBean
	ClientService clientService;
	
	@SpringBean
	UserService userService;
	
	@SpringBean
	OrganizationService organizationService;
	
	ArrayList <DrccInfoField> infoFields = new ArrayList<DrccInfoField>();
	
	public static final int IDX_MRC2_ORGANIZATION_NAME_FIELD = 0; 
	public static final int IDX_MRC2_LAB_NAME_FIELD = 1;
	public static final int IDX_MRC2_DEPARTMENT_NAME_FIELD = 2;
	public static final int IDX_MRC2_CONTACT_PERSON_FIELD = 3;
	public static final int IDX_MRC2_CONTACT_EMAIL_FIELD = 4;
	public static final int IDX_MRC2_CONTACT_PHONE_FIELD = 5;
	public static final int IDX_MRC2_PI_NAME_FIELD = 6;
	public static final int IDX_MRC2_PI_EMAIL_FIELD = 7;
	public static final int IDX_MRC2_PI_PHONE_FIELD = 8;
	public static final int IDX_MRC2_EXP_DATE_FIELD = 9;
	public static final int IDX_MRC2_EXP_NAME_FIELD = 10;
	public static final int IDX_MRC2_EXP_ID_FIELD = 11;
	public static final int IDX_MRC2_PROJ_NAME_FIELD = 12;
	public static final int IDX_MRC2_PROJ_DESCRIPTION_FIELD = 13;
	public static final int IDX_MRC2_SHORTCODE_FIELD = 14;
	public static final int IDX_MRC2_NIH_GRANT_NUMBER_FIELD = 15;
	public static final int IDX_MRC2_SERVICE_REQUEST_ID_FIELD = 16;
	
	
	public Mrc2ClientDataInfo(String expId) 
		{
		Injector.get().inject(this);

		this.infoFields.add(new DrccInfoField("Organization Name", "organizationName", ""));
		this.infoFields.add(new DrccInfoField("Lab Name", "labName", ""));
		this.infoFields.add(new DrccInfoField("Department Name", "departmentName", ""));
		this.infoFields.add(new DrccInfoField("Contact Person", "contactPerson", ""));
		this.infoFields.add(new DrccInfoField("Contact Email", "contactEMail", ""));
		this.infoFields.add(new DrccInfoField("Contact Phone", "contactPhone", ""));
		this.infoFields.add(new DrccInfoField("Principal Investigator", "pi", ""));
		this.infoFields.add(new DrccInfoField("Principal Investigator Email", "piEMail", ""));
		this.infoFields.add(new DrccInfoField("Principal Investigator Phone", "piPhone", ""));
		this.infoFields.add(new DrccInfoField("Date", "Date", ""));
		this.infoFields.add(new DrccInfoField("Experiment Name", "experimentName", ""));
		this.infoFields.add(new DrccInfoField("Experiment ID", "experimentID", ""));
		this.infoFields.add(new DrccInfoField("Project Name", "projectName", ""));
		this.infoFields.add(new DrccInfoField("Project Description", "projectDescription", ""));
		this.infoFields.add(new DrccInfoField("Shortcode", "shortcode", ""));
		this.infoFields.add(new DrccInfoField("NIH Grant Number", "nihGrantNumber", ""));
		this.infoFields.add(new DrccInfoField("Service Request Id", "serviceRequestID", ""));
		
		initializeFromExpId(expId);
		}

	
	
	/*
	 *  ShortcodeDTO scDto = new ShortcodeDTO();
		                    row=sheet.getRow(14); rowCount=15;
		                    scDto.setCode(row.getCell((short) 1).toString().trim());
		                    row=sheet.getRow(15); rowCount=16;
		                    scDto.setNIH_GrantNumber(row.getCell((short) 1).toString().trim());
		                    scDto.setExp(exp);
		                    if((scDto.getCode()!=null) && (scDto.getCode().length()>0)){
		                    	expService.saveShortcode(scDto);
			                }
		                    row=sheet.getRow(16); rowCount=17;
		                    String serviceRequest = row.getCell((short) 1).toString().trim();
		                    if((serviceRequest!=null) && (serviceRequest.length()>0)){
		                    	expService.updateServiceRequestForExperiment(exp, serviceRequest);
			                }
	 

	// this mimics the only changes that are actually made in the submission sheet load
	public void fillInBillingInfoFromSubmissionData(String shortCode, String nihGrantNumbers, String serviceRequestId)
		{
		this.setShortcode(shortCode);
		/// TO DO :  Clean this up -> map multiple to single
		this.setNihGrantNumber(nihGrantNumbers);
		this.setServiceRequestId(serviceRequestId);
		}
	
	public void initializeFromExpId(String expId)
		{
		Experiment exp = experimentService.loadExperimentWithInfoForDrcc(expId);	
		String projectId = exp == null ? "PR0253" : exp.getProject().getProjectID();
		Project  project = projectService.loadById(projectId);
		
		this.setProjectName(project == null ? "" : project.getProjectName());
		this.setProjectDescription(project.getDescription());
		
		String clientId = project.getClient().getClientID();
		Client client = clientService.loadById(clientId);
		String dept = (client == null ? "" : client.getDept());
		this.setDepartmentName(dept == null ? "" : dept);
		
		final String investigatorUsername = client.getInvestigator().getUserName();
		LoadableDetachableModel<User> investigator = new LoadableDetachableModel<User>()
			{
			@Override
			protected User load() { return (userService.getUserByUserName(investigatorUsername));}
			};
			
		this.setLabName(investigator == null ? "" : investigator.getObject().getLab());
		
		String orgId = client.getOrganizationID();
		Organization organization = organizationService.loadById(orgId);
		
		String orgName = organization.getOrgName();
		this.setOrganizationName(orgName == null ? "" : orgName);
		
		List <String> grantSources = experimentService.getGrantInfo(expId);
		this.setNihGrantNumber(grantSources.toString());
		
		// email
		this.setPiEMail(investigator == null ? "" : investigator.getObject().getEmail());
		this.setPiPhone(investigator == null ? "" : investigator.getObject().getPhone());
		String piname = (investigator == null ? "" : investigator.getObject().getFullName());
		this.setPiName(piname);
		
		// erviceRequest
		
		final String contactPersonName = project.getContactPerson().getUserName();
		LoadableDetachableModel<User> contactPerson = new LoadableDetachableModel<User>()
			{
			@Override
			protected User load() { return (userService.getUserByUserName(contactPersonName));}
			};
		
		this.setExperimentName(exp.getExpName());
		this.setContactPerson(contactPerson == null ? "" : contactPerson.getObject().getFullName());
		this.setContactEMail(contactPerson == null ? "" : contactPerson.getObject().getEmail());
		this.setContactPhone(contactPerson == null ? "" : contactPerson.getObject().getPhone());
		
		List<String> shortcodes = experimentService.getShortcodeIdsForExpId(expId);
		this.setShortcode(shortcodes.toString());
		
		this.setServiceRequestId(exp.getServiceRequest());
		
		this.setExperimentId(exp.getExpID());
		this.setExperimentDate(DateUtils.dateStrFromCalendar("MM/dd/yyyy", exp.getCreationDate()));
		}

	//String organizationName, labName, departmentName, contactPerson, contactEMail, contactPhone;
	public String getOrganizationName() {
		return infoFields.get(IDX_MRC2_ORGANIZATION_NAME_FIELD).getFieldValues(0);
	}


	public void setOrganizationName(String organizationName) {
		infoFields.get(IDX_MRC2_ORGANIZATION_NAME_FIELD).setFieldValues(0, organizationName);
	}


	public String getLabName() {
		return infoFields.get(IDX_MRC2_LAB_NAME_FIELD).getFieldValues(0);
	}


	public void setLabName(String labName) {
		infoFields.get(IDX_MRC2_LAB_NAME_FIELD).setFieldValues(0, labName);
	}


	public String getDepartmentName() {
		return infoFields.get(IDX_MRC2_DEPARTMENT_NAME_FIELD).getFieldValues(0);
	}


	public void setDepartmentName(String departmentName) {
		infoFields.get(IDX_MRC2_DEPARTMENT_NAME_FIELD).setFieldValues(0, departmentName);
	}


	public String getContactPerson() {
		return infoFields.get(IDX_MRC2_CONTACT_PERSON_FIELD).getFieldValues(0);
	}


	public void setContactPerson(String contactPerson) {
		infoFields.get(IDX_MRC2_CONTACT_PERSON_FIELD).setFieldValues(0, contactPerson);
	}


	public String getContactEMail() {
		return infoFields.get(IDX_MRC2_CONTACT_EMAIL_FIELD).getFieldValues(0);
	}


	public void setContactEMail(String contactEMail) {
		infoFields.get(IDX_MRC2_CONTACT_EMAIL_FIELD).setFieldValues(0, contactEMail);
	}


	public String getContactPhone() {
		return infoFields.get(IDX_MRC2_CONTACT_PHONE_FIELD).getFieldValues(0);
	}


	public void setContactPhone(String contactPhone) {
		infoFields.get(IDX_MRC2_CONTACT_PHONE_FIELD).setFieldValues(0, contactPhone);
	}


	public String getPiName() {
		return infoFields.get(IDX_MRC2_PI_NAME_FIELD).getFieldValues(0);
	}


	public void setPiName(String piName) {
		infoFields.get(IDX_MRC2_PI_NAME_FIELD).setFieldValues(0, piName);
	}


	public String getPiEMail() {
		return infoFields.get(IDX_MRC2_PI_EMAIL_FIELD).getFieldValues(0);
	}


	public void setPiEMail(String piEMail) {
		infoFields.get(IDX_MRC2_PI_EMAIL_FIELD).setFieldValues(0, piEMail);
	}


	public String getPiPhone() {
		return infoFields.get(IDX_MRC2_PI_PHONE_FIELD).getFieldValues(0);
	}


	public void setPiPhone(String piPhone) {
		infoFields.get(IDX_MRC2_PI_PHONE_FIELD).setFieldValues(0, piPhone);
	}


	public String getExperimentDate() {
		return infoFields.get(IDX_MRC2_EXP_DATE_FIELD).getFieldValues(0); 
	}


	public void setExperimentDate(String experimentDate) {
		infoFields.get(IDX_MRC2_EXP_DATE_FIELD).setFieldValues(0, experimentDate);
	}


	public String getExperimentName() {
		return infoFields.get(IDX_MRC2_EXP_NAME_FIELD).getFieldValues(0);
	}


	public void setExperimentName(String experimentName) {
		infoFields.get(IDX_MRC2_EXP_NAME_FIELD).setFieldValues(0, experimentName);
	}


	public String getExperimentId() {
		return infoFields.get(IDX_MRC2_EXP_ID_FIELD).getFieldValues(0);
	}


	public void setExperimentId(String experimentId) {
		infoFields.get(IDX_MRC2_EXP_ID_FIELD).setFieldValues(0, experimentId);
	}

	public String getProjectName() {
		return infoFields.get(IDX_MRC2_PROJ_NAME_FIELD).getFieldValues(0);
	}

	
	public void setProjectName(String projectName) {
		infoFields.get(IDX_MRC2_PROJ_NAME_FIELD).setFieldValues(0, projectName);
	}

	
	public void setProjectDescription(String projectDescription) {
		infoFields.get(IDX_MRC2_PROJ_DESCRIPTION_FIELD).setFieldValues(0, projectDescription);
	}
	
	public String getProjectDescription() {
		return infoFields.get(IDX_MRC2_PROJ_DESCRIPTION_FIELD).getFieldValues(0);
	}
	public String getShortcode() {
		return infoFields.get(IDX_MRC2_SHORTCODE_FIELD).getFieldValues(0);
	}


	public void setShortcode(String shortcode) {
		infoFields.get(IDX_MRC2_SHORTCODE_FIELD).setFieldValues(0,shortcode );
	}

	public String getNihGrantNumber() {
		return infoFields.get(IDX_MRC2_NIH_GRANT_NUMBER_FIELD).getFieldValues(0);
	}


	public void setNihGrantNumber(String nihGrantNumber) {
		infoFields.get(IDX_MRC2_NIH_GRANT_NUMBER_FIELD).setFieldValues(0, nihGrantNumber );
	}


	public String getServiceRequestId() {
		return infoFields.get(IDX_MRC2_SERVICE_REQUEST_ID_FIELD).getFieldValues(0);
	}


	public void setServiceRequestId(String serviceRequestId) {
		infoFields.get(IDX_MRC2_SERVICE_REQUEST_ID_FIELD).setFieldValues(0, serviceRequestId);
	}
	
	
	///////////////////
	public List <DrccInfoField> getInfoFields()
		{
		return infoFields;
		}

	public List <DrccInfoField> getInfoValue()
		{
		return infoFields;
		}
	
	public String getInfoValue(int i)
		{
		return infoFields.get(i).getFieldValues(0);
		}
	
	public void setInfoValue(int i, String value)
		{
		infoFields.get(i).setFieldValues(0, value);
		}
	}
*/

/*
public class Mrc2ClientDataInfo implements Serializable
	{
	@SpringBean
	ExperimentService experimentService;
	
	@SpringBean
	ProjectService projectService;
	
	@SpringBean
	ClientService clientService;
	
	@SpringBean
	UserService userService;
	
	@SpringBean
	OrganizationService organizationService;
	
	String organizationName = "", labName = "", deptName = "", contactPerson = "", contactEmail = "";
	String contactPhone = "", piName = "", piEMail = "", piPhone = "", experimentDate = "", experimentName = "", experimentId = "";
	String projectName = "", projectDescription = "", shortCode = "", nihGrantNumber = "", serviceRequestId = "";
	

	public Mrc2ClientDataInfo()
		{
		Injector.get().inject(this);
		}
	
	public Mrc2ClientDataInfo(String expId) 
		{
		this();
		initializeFromExpId(expId);
		}


	// this mimics the only changes that are actually made in the submission sheet load
	public void fillInBillingInfoFromSubmissionData(String shortCode, String nihGrantNumbers, String serviceRequestId)
		{
		this.setShortCode(shortCode);
		/// TO DO :  Clean this up -> map multiple to single
		this.setNihGrantNumber(nihGrantNumbers);
		this.setServiceRequestId(serviceRequestId);
		}
	
	public void initializeFromExpId(String expId)
		{
		Experiment exp = experimentService.loadExperimentWithInfoForDrcc(expId);	
		String projectId = exp == null ? "PR0253" : exp.getProject().getProjectID();
		Project  project = projectService.loadById(projectId);
		
		this.setProjectName(project == null ? "" : project.getProjectName());
		this.setProjectDescription(project.getDescription());
		
		String clientId = project.getClient().getClientID();
		Client client = clientService.loadById(clientId);
		String dept = (client == null ? "" : client.getDept());
		this.setDeptName(dept == null ? "" : dept);
		
		final String investigatorUsername = client.getInvestigator().getUserName();
		LoadableDetachableModel<User> investigator = new LoadableDetachableModel<User>()
			{
			@Override
			protected User load() { return (userService.getUserByUserName(investigatorUsername));}
			};
			
		this.setLabName(investigator == null ? "" : investigator.getObject().getLab());
		
		String orgId = client.getOrganizationID();
		Organization organization = organizationService.loadById(orgId);
		
		String orgName = organization.getOrgName();
		this.setOrganizationName(orgName == null ? "" : orgName);
		
		List <String> grantSources = experimentService.getGrantInfo(expId);
		this.setNihGrantNumber(grantSources.toString());
		
		this.setPiEMail(investigator == null ? "" : investigator.getObject().getEmail());
		this.setPiPhone(investigator == null ? "" : investigator.getObject().getPhone());
		String piname = (investigator == null ? "" : investigator.getObject().getFullName());
		this.setPiName(piname);
		
		final String contactPersonName = project.getContactPerson().getUserName();
		LoadableDetachableModel<User> contactPerson = new LoadableDetachableModel<User>()
			{
			@Override
			protected User load() { return (userService.getUserByUserName(contactPersonName));}
			};
		
		this.setExperimentName(exp.getExpName());
		this.setContactPerson(contactPerson == null ? "" : contactPerson.getObject().getFullName());
		this.setContactEmail(contactPerson == null ? "" : contactPerson.getObject().getEmail());
		this.setContactPhone(contactPerson == null ? "" : contactPerson.getObject().getPhone());
		
		List<String> shortcodes = experimentService.getShortcodeIdsForExpId(expId);
		this.setShortCode(shortcodes.toString());
		
		this.setServiceRequestId(exp.getServiceRequest());
		
		this.setExperimentId(exp.getExpID());
		this.setExperimentDate(DateUtils.dateStrFromCalendar("MM/dd/yyyy", exp.getCreationDate()));
		}


	public String getOrganizationName() {
		return organizationName;
	}


	public String getLabName() {
		return labName;
	}


	public String getDeptName() {
		return deptName;
	}


	public String getContactPerson() {
		return contactPerson;
	}


	public String getContactEmail() {
		return contactEmail;
	}


	public String getContactPhone() {
		return contactPhone;
	}


	public String getPiName() {
		return piName;
	}


	public String getPiEMail() {
		return piEMail;
	}


	public String getPiPhone() {
		return piPhone;
	}


	public String getExperimentDate() {
		return experimentDate;
	}


	public String getExperimentName() {
		return experimentName;
	}


	public String getExperimentId() {
		return experimentId;
	}


	public String getProjectName() {
		return projectName;
	}


	public String getProjectDescription() {
		return projectDescription;
	}


	public String getShortCode() {
		return shortCode;
	}


	public String getNihGrantNumber() {
		return nihGrantNumber;
	}


	public String getServiceRequestId() {
		return serviceRequestId;
	}


	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}


	public void setLabName(String labName) {
		this.labName = labName;
	}


	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}


	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}


	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}


	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}


	public void setPiName(String piName) {
		this.piName = piName;
	}


	public void setPiEMail(String piEMail) {
		this.piEMail = piEMail;
	}


	public void setPiPhone(String piPhone) {
		this.piPhone = piPhone;
	}


	public void setExperimentDate(String date) {
		this.experimentDate = date;
	}


	public void setExperimentName(String experimentName) {
		this.experimentName = experimentName;
	}


	public void setExperimentId(String experimentId) {
		this.experimentId = experimentId;
	}


	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}


	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}


	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}


	public void setNihGrantNumber(String nihGrantNumber) {
		this.nihGrantNumber = nihGrantNumber;
	}


	public void setServiceRequestId(String serviceRequestId) {
		this.serviceRequestId = serviceRequestId;
	}
	
	public Map<String, String> getValueMap()
		{
		String organizationName = "", labName = "", deptName = "", contactPerson = "", contactEmail = "";
		String contactPhone = "", piName = "", piEMail = "", piPhone = "", experimentDate = "", experimentName = "", experimentId = "";
		String projectName = "", projectDescription = "", shortCode = "", nihGrantNumber = "", serviceRequestId = "";
		
		Map<String, String> valueMap = new HashMap<String, String>();
		
		valueMap.put("Organization", getOrganizationName());
		valueMap.put("Lab", getLabName());
		valueMap.put("Department", getDeptName());
		valueMap.put("Contact", getContactPerson());
		valueMap.put("Contact E-mail", getContactEmail());
		valueMap.put("Contact Phone", getContactPhone());
		valueMap.put("Principal Investigator", getPiName());
		valueMap.put("PI E-mail", getPiEMail());
		valueMap.put("PI Phone", getPiPhone());
		valueMap.put("Date", getExperimentDate());
		valueMap.put("Experiment Name", getExperimentName());
		valueMap.put("Experiment Id", getExperimentId());
		valueMap.put("Project Name", getProjectName());
		valueMap.put("Project Description", getProjectDescription());
		valueMap.put("Shortcode", getShortCode());
		valueMap.put("NIH Grant Number", getNihGrantNumber());
		valueMap.put("Service Request Id", getServiceRequestId());
		
		return valueMap;
		}
	}






































/*
public class Mrc2ClientDataInfo implements Serializable
	{
	@SpringBean
	ExperimentService experimentService;
	
	@SpringBean
	ProjectService projectService;
	
	@SpringBean
	ClientService clientService;
	
	@SpringBean
	UserService userService;
	
	@SpringBean
	OrganizationService organizationService;
	
	ArrayList <DrccInfoField> infoFields = new ArrayList<DrccInfoField>();
	
	public static final int IDX_MRC2_ORGANIZATION_NAME_FIELD = 0; 
	public static final int IDX_MRC2_LAB_NAME_FIELD = 1;
	public static final int IDX_MRC2_DEPARTMENT_NAME_FIELD = 2;
	public static final int IDX_MRC2_CONTACT_PERSON_FIELD = 3;
	public static final int IDX_MRC2_CONTACT_EMAIL_FIELD = 4;
	public static final int IDX_MRC2_CONTACT_PHONE_FIELD = 5;
	public static final int IDX_MRC2_PI_NAME_FIELD = 6;
	public static final int IDX_MRC2_PI_EMAIL_FIELD = 7;
	public static final int IDX_MRC2_PI_PHONE_FIELD = 8;
	public static final int IDX_MRC2_EXP_DATE_FIELD = 9;
	public static final int IDX_MRC2_EXP_NAME_FIELD = 10;
	public static final int IDX_MRC2_EXP_ID_FIELD = 11;
	public static final int IDX_MRC2_PROJ_NAME_FIELD = 12;
	public static final int IDX_MRC2_PROJ_DESCRIPTION_FIELD = 13;
	public static final int IDX_MRC2_SHORTCODE_FIELD = 14;
	public static final int IDX_MRC2_NIH_GRANT_NUMBER_FIELD = 15;
	public static final int IDX_MRC2_SERVICE_REQUEST_ID_FIELD = 16;
	
	
	public Mrc2ClientDataInfo(String expId) 
		{
		Injector.get().inject(this);

		this.infoFields.add(new DrccInfoField("Organization Name", "organizationName", ""));
		this.infoFields.add(new DrccInfoField("Lab Name", "labName", ""));
		this.infoFields.add(new DrccInfoField("Department Name", "departmentName", ""));
		this.infoFields.add(new DrccInfoField("Contact Person", "contactPerson", ""));
		this.infoFields.add(new DrccInfoField("Contact Email", "contactEMail", ""));
		this.infoFields.add(new DrccInfoField("Contact Phone", "contactPhone", ""));
		this.infoFields.add(new DrccInfoField("Principal Investigator", "pi", ""));
		this.infoFields.add(new DrccInfoField("Principal Investigator Email", "piEMail", ""));
		this.infoFields.add(new DrccInfoField("Principal Investigator Phone", "piPhone", ""));
		this.infoFields.add(new DrccInfoField("Date", "Date", ""));
		this.infoFields.add(new DrccInfoField("Experiment Name", "experimentName", ""));
		this.infoFields.add(new DrccInfoField("Experiment ID", "experimentID", ""));
		this.infoFields.add(new DrccInfoField("Project Name", "projectName", ""));
		this.infoFields.add(new DrccInfoField("Project Description", "projectDescription", ""));
		this.infoFields.add(new DrccInfoField("Shortcode", "shortcode", ""));
		this.infoFields.add(new DrccInfoField("NIH Grant Number", "nihGrantNumber", ""));
		this.infoFields.add(new DrccInfoField("Service Request Id", "serviceRequestID", ""));
		
		initializeFromExpId(expId);
		}

	
	
	/*
	 *  ShortcodeDTO scDto = new ShortcodeDTO();
		                    row=sheet.getRow(14); rowCount=15;
		                    scDto.setCode(row.getCell((short) 1).toString().trim());
		                    row=sheet.getRow(15); rowCount=16;
		                    scDto.setNIH_GrantNumber(row.getCell((short) 1).toString().trim());
		                    scDto.setExp(exp);
		                    if((scDto.getCode()!=null) && (scDto.getCode().length()>0)){
		                    	expService.saveShortcode(scDto);
			                }
		                    row=sheet.getRow(16); rowCount=17;
		                    String serviceRequest = row.getCell((short) 1).toString().trim();
		                    if((serviceRequest!=null) && (serviceRequest.length()>0)){
		                    	expService.updateServiceRequestForExperiment(exp, serviceRequest);
			                }
	 

	// this mimics the only changes that are actually made in the submission sheet load
	public void fillInBillingInfoFromSubmissionData(String shortCode, String nihGrantNumbers, String serviceRequestId)
		{
		this.setShortcode(shortCode);
		/// TO DO :  Clean this up -> map multiple to single
		this.setNihGrantNumber(nihGrantNumbers);
		this.setServiceRequestId(serviceRequestId);
		}
	
	public void initializeFromExpId(String expId)
		{
		Experiment exp = experimentService.loadExperimentWithInfoForDrcc(expId);	
		String projectId = exp == null ? "PR0253" : exp.getProject().getProjectID();
		Project  project = projectService.loadById(projectId);
		
		this.setProjectName(project == null ? "" : project.getProjectName());
		this.setProjectDescription(project.getDescription());
		
		String clientId = project.getClient().getClientID();
		Client client = clientService.loadById(clientId);
		String dept = (client == null ? "" : client.getDept());
		this.setDepartmentName(dept == null ? "" : dept);
		
		final String investigatorUsername = client.getInvestigator().getUserName();
		LoadableDetachableModel<User> investigator = new LoadableDetachableModel<User>()
			{
			@Override
			protected User load() { return (userService.getUserByUserName(investigatorUsername));}
			};
			
		this.setLabName(investigator == null ? "" : investigator.getObject().getLab());
		
		String orgId = client.getOrganizationID();
		Organization organization = organizationService.loadById(orgId);
		
		String orgName = organization.getOrgName();
		this.setOrganizationName(orgName == null ? "" : orgName);
		
		List <String> grantSources = experimentService.getGrantInfo(expId);
		this.setNihGrantNumber(grantSources.toString());
		
		// email
		this.setPiEMail(investigator == null ? "" : investigator.getObject().getEmail());
		this.setPiPhone(investigator == null ? "" : investigator.getObject().getPhone());
		String piname = (investigator == null ? "" : investigator.getObject().getFullName());
		this.setPiName(piname);
		
		// erviceRequest
		
		final String contactPersonName = project.getContactPerson().getUserName();
		LoadableDetachableModel<User> contactPerson = new LoadableDetachableModel<User>()
			{
			@Override
			protected User load() { return (userService.getUserByUserName(contactPersonName));}
			};
		
		this.setExperimentName(exp.getExpName());
		this.setContactPerson(contactPerson == null ? "" : contactPerson.getObject().getFullName());
		this.setContactEMail(contactPerson == null ? "" : contactPerson.getObject().getEmail());
		this.setContactPhone(contactPerson == null ? "" : contactPerson.getObject().getPhone());
		
		List<String> shortcodes = experimentService.getShortcodeIdsForExpId(expId);
		this.setShortcode(shortcodes.toString());
		
		this.setServiceRequestId(exp.getServiceRequest());
		
		this.setExperimentId(exp.getExpID());
		this.setExperimentDate(DateUtils.dateStrFromCalendar("MM/dd/yyyy", exp.getCreationDate()));
		}

	//String organizationName, labName, departmentName, contactPerson, contactEMail, contactPhone;
	public String getOrganizationName() {
		return infoFields.get(IDX_MRC2_ORGANIZATION_NAME_FIELD).getFieldValues(0);
	}


	public void setOrganizationName(String organizationName) {
		infoFields.get(IDX_MRC2_ORGANIZATION_NAME_FIELD).setFieldValues(0, organizationName);
	}


	public String getLabName() {
		return infoFields.get(IDX_MRC2_LAB_NAME_FIELD).getFieldValues(0);
	}


	public void setLabName(String labName) {
		infoFields.get(IDX_MRC2_LAB_NAME_FIELD).setFieldValues(0, labName);
	}


	public String getDepartmentName() {
		return infoFields.get(IDX_MRC2_DEPARTMENT_NAME_FIELD).getFieldValues(0);
	}


	public void setDepartmentName(String departmentName) {
		infoFields.get(IDX_MRC2_DEPARTMENT_NAME_FIELD).setFieldValues(0, departmentName);
	}


	public String getContactPerson() {
		return infoFields.get(IDX_MRC2_CONTACT_PERSON_FIELD).getFieldValues(0);
	}


	public void setContactPerson(String contactPerson) {
		infoFields.get(IDX_MRC2_CONTACT_PERSON_FIELD).setFieldValues(0, contactPerson);
	}


	public String getContactEMail() {
		return infoFields.get(IDX_MRC2_CONTACT_EMAIL_FIELD).getFieldValues(0);
	}


	public void setContactEMail(String contactEMail) {
		infoFields.get(IDX_MRC2_CONTACT_EMAIL_FIELD).setFieldValues(0, contactEMail);
	}


	public String getContactPhone() {
		return infoFields.get(IDX_MRC2_CONTACT_PHONE_FIELD).getFieldValues(0);
	}


	public void setContactPhone(String contactPhone) {
		infoFields.get(IDX_MRC2_CONTACT_PHONE_FIELD).setFieldValues(0, contactPhone);
	}


	public String getPiName() {
		return infoFields.get(IDX_MRC2_PI_NAME_FIELD).getFieldValues(0);
	}


	public void setPiName(String piName) {
		infoFields.get(IDX_MRC2_PI_NAME_FIELD).setFieldValues(0, piName);
	}


	public String getPiEMail() {
		return infoFields.get(IDX_MRC2_PI_EMAIL_FIELD).getFieldValues(0);
	}


	public void setPiEMail(String piEMail) {
		infoFields.get(IDX_MRC2_PI_EMAIL_FIELD).setFieldValues(0, piEMail);
	}


	public String getPiPhone() {
		return infoFields.get(IDX_MRC2_PI_PHONE_FIELD).getFieldValues(0);
	}


	public void setPiPhone(String piPhone) {
		infoFields.get(IDX_MRC2_PI_PHONE_FIELD).setFieldValues(0, piPhone);
	}


	public String getExperimentDate() {
		return infoFields.get(IDX_MRC2_EXP_DATE_FIELD).getFieldValues(0); 
	}


	public void setExperimentDate(String experimentDate) {
		infoFields.get(IDX_MRC2_EXP_DATE_FIELD).setFieldValues(0, experimentDate);
	}


	public String getExperimentName() {
		return infoFields.get(IDX_MRC2_EXP_NAME_FIELD).getFieldValues(0);
	}


	public void setExperimentName(String experimentName) {
		infoFields.get(IDX_MRC2_EXP_NAME_FIELD).setFieldValues(0, experimentName);
	}


	public String getExperimentId() {
		return infoFields.get(IDX_MRC2_EXP_ID_FIELD).getFieldValues(0);
	}


	public void setExperimentId(String experimentId) {
		infoFields.get(IDX_MRC2_EXP_ID_FIELD).setFieldValues(0, experimentId);
	}

	public String getProjectName() {
		return infoFields.get(IDX_MRC2_PROJ_NAME_FIELD).getFieldValues(0);
	}

	
	public void setProjectName(String projectName) {
		infoFields.get(IDX_MRC2_PROJ_NAME_FIELD).setFieldValues(0, projectName);
	}

	
	public void setProjectDescription(String projectDescription) {
		infoFields.get(IDX_MRC2_PROJ_DESCRIPTION_FIELD).setFieldValues(0, projectDescription);
	}
	
	public String getProjectDescription() {
		return infoFields.get(IDX_MRC2_PROJ_DESCRIPTION_FIELD).getFieldValues(0);
	}
	public String getShortcode() {
		return infoFields.get(IDX_MRC2_SHORTCODE_FIELD).getFieldValues(0);
	}


	public void setShortcode(String shortcode) {
		infoFields.get(IDX_MRC2_SHORTCODE_FIELD).setFieldValues(0,shortcode );
	}

	public String getNihGrantNumber() {
		return infoFields.get(IDX_MRC2_NIH_GRANT_NUMBER_FIELD).getFieldValues(0);
	}


	public void setNihGrantNumber(String nihGrantNumber) {
		infoFields.get(IDX_MRC2_NIH_GRANT_NUMBER_FIELD).setFieldValues(0, nihGrantNumber );
	}


	public String getServiceRequestId() {
		return infoFields.get(IDX_MRC2_SERVICE_REQUEST_ID_FIELD).getFieldValues(0);
	}


	public void setServiceRequestId(String serviceRequestId) {
		infoFields.get(IDX_MRC2_SERVICE_REQUEST_ID_FIELD).setFieldValues(0, serviceRequestId);
	}
	
	
	///////////////////
	public List <DrccInfoField> getInfoFields()
		{
		return infoFields;
		}

	public List <DrccInfoField> getInfoValue()
		{
		return infoFields;
		}
	
	public String getInfoValue(int i)
		{
		return infoFields.get(i).getFieldValues(0);
		}
	
	public void setInfoValue(int i, String value)
		{
		infoFields.get(i).setFieldValues(0, value);
		}
	}
*/
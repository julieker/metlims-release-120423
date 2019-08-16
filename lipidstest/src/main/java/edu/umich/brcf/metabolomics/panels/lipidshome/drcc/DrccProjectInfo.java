package edu.umich.brcf.metabolomics.panels.lipidshome.drcc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Client;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Organization;
import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.layers.domain.User;
import edu.umich.brcf.shared.layers.service.ClientService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.OrganizationService;
import edu.umich.brcf.shared.layers.service.ProjectService;
import edu.umich.brcf.shared.layers.service.UserService;



public class DrccProjectInfo implements Serializable
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
	
	public static final int IDX_DRCC_PROJ_TITLE_FIELD = 0;
	public static final int IDX_DRCC_PROJ_TYPE_FIELD = 1;
	public static final int IDX_DRCC_PROJ_SUMMARY_FIELD = 2;
	public static final int IDX_DRCC_PROJ_INSTITUTE_FIELD = 3;
	public static final int IDX_DRCC_PROJ_DEPT_FIELD = 4;
	public static final int IDX_DRCC_PROJ_LAB_FIELD = 5;
	public static final int IDX_DRCC_PROJ_FUNDING_FIELD = 6;
	public static final int IDX_DRCC_PROJ_PILASTNAME_FIELD = 7;
	public static final int IDX_DRCC_PROJ_PIFIRSTNAME_FIELD = 8;
	public static final int IDX_DRCC_PROJ_ADDRESS_FIELD = 9;
	public static final int IDX_DRCC_PROJ_EMAIL_FIELD = 10;
	public static final int IDX_DRCC_PROJ_PHONE_FIELD = 11;
	
	
	public DrccProjectInfo(String expId)
		{
		Injector.get().inject(this);
		
		this.infoFields.add(new DrccInfoField("Project Title*", "projectName", ""));
		this.infoFields.add(new DrccInfoField("Project Type", "projectType", ""));
		this.infoFields.add(new DrccInfoField("Project Summary", "projectDescription", ""));
		this.infoFields.add(new DrccInfoField("Institute*", "institute", ""));
		this.infoFields.add(new DrccInfoField("Department*", "department", ""));
		this.infoFields.add(new DrccInfoField("Laboratory", "laboratory", ""));
		this.infoFields.add(new DrccInfoField("Funding Source", "fundingSource", ""));
		this.infoFields.add(new DrccInfoField("Principal Investigator Last Name*", "piLastName", ""));
		this.infoFields.add(new DrccInfoField("Principal Investigator First Name*", "piFirstName", ""));
		this.infoFields.add(new DrccInfoField("Address*", "piAddress", ""));
		this.infoFields.add(new DrccInfoField("EMail*", "piEMail", ""));
		this.infoFields.add(new DrccInfoField("Phone", "piPhone", ""));
		
		initializeFromExpId(expId);
		}
	
	
	public void initializeFromExpId(String expId)
		{
		Experiment exp = experimentService.loadExperimentWithInfoForDrcc(expId);	
		String projectId = exp == null ? "PR0253" : exp.getProject().getProjectID();
		Project  project = projectService.loadById(projectId);
		

		// project title
		this.setProjectName(project == null ? "" : project.getProjectName());
		
		// project summary
		this.setProjectDescription(project.getDescription());
		
		String clientId = project.getClient().getClientID();
		Client client = clientService.loadById(clientId);
		
		String dept = (client == null ? "" : client.getDept());
		// dept
		this.setDepartment(dept == null ? "" : dept);
		
		final String investigatorUsername = client.getInvestigator().getUserName();
		LoadableDetachableModel<User> investigator = new LoadableDetachableModel<User>()
			{
			@Override
			protected User load() { return (userService.getUserByUserName(investigatorUsername));}
			};
			
		// lab
		this.setLaboratory(investigator == null ? "" : investigator.getObject().getLab());
		String orgId = client.getOrganizationID();
		Organization organization = organizationService.loadById(orgId);
		
		String orgName = organization.getOrgName();
		// institute
		this.setInstitute(orgName == null ? "" : orgName);
		
		List <String> grantSources = experimentService.getGrantInfo(expId);
		// funding source
		this.setFundingSource(grantSources.toString());
		
		// email
		this.setEMail(investigator == null ? "" : investigator.getObject().getEmail());
		
		
		// phone
		this.setPhone(investigator == null ? "" : investigator.getObject().getPhone());
		
		// PI Last Name
		String ln = (investigator == null ? "" : investigator.getObject().getLastName());
		this.setPiLastName(ln);
		
		// PI First Name
		String fn = (investigator == null ? "" : investigator.getObject().getFirstName());
		this.setPiFirstName(fn);
		
		// Address
		String address = (organization == null ? "" : organization.getOrgAddress());
		this.setAddress(address);
		}
	
	
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



	public String getProjectName() {
		return infoFields.get(IDX_DRCC_PROJ_TITLE_FIELD).getFieldValues(0);
	}



	public void setProjectName(String projectName) {
		infoFields.get(IDX_DRCC_PROJ_TITLE_FIELD).setFieldValues(0, projectName);
	}



	public String getProjectType() {
		return infoFields.get(IDX_DRCC_PROJ_TYPE_FIELD).getFieldValues(0);
	}



	public void setProjectType(String projectType) {
		infoFields.get(IDX_DRCC_PROJ_TYPE_FIELD).setFieldValues(0, projectType);
	}



	public String getProjectDescription() {
		return infoFields.get(IDX_DRCC_PROJ_SUMMARY_FIELD).getFieldValues(0);

	}



	public void setProjectDescription(String projectSummary) {
		infoFields.get(IDX_DRCC_PROJ_SUMMARY_FIELD).setFieldValues(0, projectSummary);
	}



	public String getInstitute() {
		return infoFields.get(IDX_DRCC_PROJ_INSTITUTE_FIELD).getFieldValues(0);

	}



	public void setInstitute(String institute) {
		infoFields.get(IDX_DRCC_PROJ_INSTITUTE_FIELD).setFieldValues(0, institute);
	}



	public String getDepartment() {
		return infoFields.get(IDX_DRCC_PROJ_DEPT_FIELD).getFieldValues(0);
	}



	public void setDepartment(String department) {
		infoFields.get(IDX_DRCC_PROJ_DEPT_FIELD).setFieldValues(0, department);
	}



	public String getLaboratory() {
		return infoFields.get(IDX_DRCC_PROJ_LAB_FIELD).getFieldValues(0);

	}



	public void setLaboratory(String laboratory) {
		infoFields.get(IDX_DRCC_PROJ_LAB_FIELD).setFieldValues(0, laboratory);
	}



	public String getFundingSource() {
		return infoFields.get(IDX_DRCC_PROJ_FUNDING_FIELD).getFieldValues(0);
	}



	public void setFundingSource(String fundingSource) {
		infoFields.get(IDX_DRCC_PROJ_FUNDING_FIELD).setFieldValues(0,fundingSource);
	}



	public String getPiLastName() {
		return infoFields.get(IDX_DRCC_PROJ_PILASTNAME_FIELD).getFieldValues(0);

	}



	public void setPiLastName(String piLastName) {
		infoFields.get(IDX_DRCC_PROJ_PILASTNAME_FIELD).setFieldValues(0, piLastName);
	}



	public String getPiFirstName() {
		return infoFields.get(IDX_DRCC_PROJ_PIFIRSTNAME_FIELD).getFieldValues(0);

	}



	public void setPiFirstName(String piFirstName) {
		infoFields.get(IDX_DRCC_PROJ_PIFIRSTNAME_FIELD).setFieldValues(0, piFirstName);
	}



	public String getAddress() {
		return infoFields.get(IDX_DRCC_PROJ_ADDRESS_FIELD).getFieldValues(0);
	}



	public void setAddress(String address) {
		infoFields.get(IDX_DRCC_PROJ_ADDRESS_FIELD).setFieldValues(0, address);
	}



	public String getEMail() {
		return infoFields.get(IDX_DRCC_PROJ_EMAIL_FIELD).getFieldValues(0);
	}



	public void setEMail(String eMail) 
		{
		infoFields.get(IDX_DRCC_PROJ_EMAIL_FIELD).setFieldValues(0, eMail);
		}



	public String getPhone() 
		{
		return infoFields.get(IDX_DRCC_PROJ_PHONE_FIELD).getFieldValues(0);
		}



	public void setPhone(String phone) 
		{
		infoFields.get(IDX_DRCC_PROJ_PHONE_FIELD).setFieldValues(0, phone);
		}


	public void setInfoFields(ArrayList<DrccInfoField> infoFields) 
		{
		this.infoFields = infoFields;
		}
	}
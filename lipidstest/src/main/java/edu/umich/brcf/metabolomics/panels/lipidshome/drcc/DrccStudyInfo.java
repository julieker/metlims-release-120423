package edu.umich.brcf.metabolomics.panels.lipidshome.drcc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.ClientService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.OrganizationService;
import edu.umich.brcf.shared.layers.service.ProjectService;
import edu.umich.brcf.shared.layers.service.SubjectService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.layers.domain.Client;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Organization;
import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.layers.domain.User;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;



public class DrccStudyInfo implements Serializable
	{
	@SpringBean
	ProjectService projectService;
	
	@SpringBean
	ExperimentService experimentService;
	
	@SpringBean
	ClientService clientService;
	
	@SpringBean 
	UserService userService;
	
	@SpringBean
	OrganizationService organizationService;
	
	@SpringBean
	SubjectService subjectService;
	
	ArrayList <DrccInfoField> infoFields = new ArrayList<DrccInfoField>();
	
	public static final int IDX_DRCC_STUDY_TITLE_FIELD = 0;
	public static final int IDX_DRCC_STUDY_TYPE_FIELD = 1;
	public static final int IDX_DRCC_STUDY_SUMMARY_FIELD = 2;
	public static final int IDX_DRCC_STUDY_INSTITUTE_FIELD = 3;
	public static final int IDX_DRCC_STUDY_DEPT_FIELD = 4;
	public static final int IDX_DRCC_STUDY_LAB_FIELD = 5;
	public static final int IDX_DRCC_STUDY_CONTACTLAST_FIELD = 6;
	public static final int IDX_DRCC_STUDY_CONTACTFIRST_FIELD = 7;

	public static final int IDX_DRCC_STUDY_ADDRESS_FIELD = 8;
	public static final int IDX_DRCC_STUDY_EMAIL_FIELD = 9;
	public static final int IDX_DRCC_STUDY_PHONE_FIELD = 10;

	public static final int IDX_DRCC_STUDY_SUBMISSION_DATE_FIELD = 11;
	public static final int IDX_DRCC_STUDY_NUMGROUPS_FIELD = 12;
	public static final int IDX_DRCC_STUDY_NUMSUBJECTS_FIELD = 13;
	public static final int IDX_DRCC_STUDY_NUMSAMPLES_FIELD = 14;
	public static final int IDX_DRCC_STUDY_COMMENTS_FIELD = 15;

	
	public DrccStudyInfo(String selectedExperiment)
		{
		Injector.get().inject(this);
		
		this.infoFields.add(new DrccInfoField("Study Title*", "studyName", ""));
		this.infoFields.add(new DrccInfoField("Study Type*", "studyType", ""));
		this.infoFields.add(new DrccInfoField("Study Summary*", "studyDescription", ""));
		this.infoFields.add(new DrccInfoField("Institute*", "institute", ""));
		this.infoFields.add(new DrccInfoField("Department*", "department", ""));
		this.infoFields.add(new DrccInfoField("Laboratory", "laboratory", ""));
		this.infoFields.add(new DrccInfoField("Study Contact Last Name*", "contactLastName", ""));
		this.infoFields.add(new DrccInfoField("Study Contact First Name*", "contactFirstName", ""));
		this.infoFields.add(new DrccInfoField("Address*", "contactAddress", ""));
		this.infoFields.add(new DrccInfoField("EMail*", "contactEmail", ""));
		this.infoFields.add(new DrccInfoField("Phone", "contactPhone", ""));
		
		this.infoFields.add(new DrccInfoField("Submission Date*", "submissionDate", ""));
		this.infoFields.add(new DrccInfoField("Number of Groups", "studyNumGroups", ""));
		this.infoFields.add(new DrccInfoField("Number of Subjects", "studyNumSubjects", ""));
		this.infoFields.add(new DrccInfoField("Number of Samples", "studyNumSamples", ""));
		this.infoFields.add(new DrccInfoField("Study Comments", "studyComments", ""));
	
		initializeFromExpId(selectedExperiment);
		}
	
	
	public void initializeFromExpId(String expId)
		{
		Experiment exp = experimentService.loadById(expId);
		
		Project project = exp.getProject();
		String projectId = project.getProjectID();
		Project initialized = projectService.loadById(projectId);
		
		//Integer nSubjects = subjectService.getSubjectCountForExperiment(expId);
		//this.setNumSubjects(nSubjects == null ? "0" : nSubjects.toString());

		String clientId = initialized.getClient().getClientID();
		Client client = clientService.loadById(clientId);
		
		final String investigatorUsername = client.getInvestigator().getUserName();
		LoadableDetachableModel<User> investigator =  new LoadableDetachableModel<User>()
			{
			@Override
			protected User load() 
				{
				return userService.getUserByUserName(investigatorUsername);
				}
			};
			
		String orgId = client.getOrganizationID();
		Organization organization = organizationService.loadById(orgId);
	
				
		// study title
		String sn = (exp == null ? "" : exp.getExpName());
		this.setStudyName(sn);
		
		/// study type
		this.setStudyType("");

		// study description
		String ed = (exp == null ? "" : exp.getExpDescription());
		this.setStudyDescription(ed);
		
		// study instit
		String inst = (organization == null ? "" : organization.getOrgName());
		this.setInstitute(inst);
		
		//  study dept
		String dept = (client == null ? "" : client.getDept());
		this.setDepartment(dept);
		
		// lab
		String lab =  (investigator == null ? "" : investigator.getObject().getLab());
		this.setLaboratory(lab);
		
		User contact = initialized.getContactPerson();
		
		// contact first
		String fn = (contact == null ? "" : contact.getFirstName());
		this.setContactFirstName(fn);
		
		// contact last
		String ln = (contact == null ? "" : contact.getLastName());
		this.setContactLastName(ln);
		
		// address
		this.setContactAddress(organization.getOrgAddress());
		
		// email
		String em = (contact == null ? "" : contact.getEmail());
		this.setContactEmail(em);

		// phone
		String ph = (contact == null ? "" : contact.getPhone());
		this.setContactPhone(ph);

		// submission date
		Calendar cal = exp.getCreationDate();
		String creationDate = DateUtils.dateStrFromCalendar("MM-dd-YYYY", cal);
		this.setSubmissionDate(creationDate);
		
		
		// numGroups
		this.setStudyNumGroups("");
		
		
		// nSamples
		String ns = (exp == null ? "" : exp.getNumberOfSamples());
		this.setStudyNumSamples(ns);
		
		// nSubjects
		Integer nsub = subjectService.getSubjectCountForExperiment(expId);
		String sns = (nsub == null ? "0" : nsub.toString());
		this.setStudyNumSubjects(sns);
		
		
		// comments
		String com = (exp == null ? "" : exp.getNotes());
		this.setStudyComments(com);
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



	public String getStudyName() {
		return infoFields.get(IDX_DRCC_STUDY_TITLE_FIELD).getFieldValues(0);
	}

	public void setStudyName(String projectName) {
		infoFields.get(IDX_DRCC_STUDY_TITLE_FIELD).setFieldValues(0, projectName);
	}



	public String getStudyType() 
		{
		return infoFields.get(IDX_DRCC_STUDY_TYPE_FIELD).getFieldValues(0);
		}

	public void setStudyType(String studyType) 
		{
		infoFields.get(IDX_DRCC_STUDY_TYPE_FIELD).setFieldValues(0, studyType);
		}


	public String getStudyDescription() 
		{
		return infoFields.get(IDX_DRCC_STUDY_SUMMARY_FIELD).getFieldValues(0);
		}

	public void setStudyDescription(String projectSummary) 
		{
		infoFields.get(IDX_DRCC_STUDY_SUMMARY_FIELD).setFieldValues(0, projectSummary);
		}



	public String getInstitute() {
		return infoFields.get(IDX_DRCC_STUDY_INSTITUTE_FIELD).getFieldValues(0);

	}



	public void setInstitute(String institute) {
		infoFields.get(IDX_DRCC_STUDY_INSTITUTE_FIELD).setFieldValues(0, institute);
	}



	public String getDepartment() {
		return infoFields.get(IDX_DRCC_STUDY_DEPT_FIELD).getFieldValues(0);
	}



	public void setDepartment(String department) {
		infoFields.get(IDX_DRCC_STUDY_DEPT_FIELD).setFieldValues(0, department);
	}



	public String getLaboratory() {
		return infoFields.get(IDX_DRCC_STUDY_LAB_FIELD).getFieldValues(0);

	}



	public void setLaboratory(String laboratory) {
		infoFields.get(IDX_DRCC_STUDY_LAB_FIELD).setFieldValues(0, laboratory);
	}



	public String getSubmissionDate() 
		{
		return infoFields.get(IDX_DRCC_STUDY_SUBMISSION_DATE_FIELD).getFieldValues(0);
		}

	public void setSubmissionDate(String date) 
		{
		infoFields.get(IDX_DRCC_STUDY_SUBMISSION_DATE_FIELD).setFieldValues(0, date);
		}


	public String getContactLastName() 
		{
		return infoFields.get(IDX_DRCC_STUDY_CONTACTLAST_FIELD).getFieldValues(0);
		}

	public void setContactLastName(String piLastName) 
		{
		infoFields.get(IDX_DRCC_STUDY_CONTACTLAST_FIELD).setFieldValues(0, piLastName);
		}


	public String getContactFirstName() 
		{
		return infoFields.get(IDX_DRCC_STUDY_CONTACTFIRST_FIELD).getFieldValues(0);
		}

	public void setContactFirstName(String piFirstName) 
		{
		infoFields.get(IDX_DRCC_STUDY_CONTACTFIRST_FIELD).setFieldValues(0, piFirstName);
		}


	public String getContactAddress() 
		{
		return infoFields.get(IDX_DRCC_STUDY_ADDRESS_FIELD).getFieldValues(0);
		}

	
	public void setContactAddress(String address) 
		{
		infoFields.get(IDX_DRCC_STUDY_ADDRESS_FIELD).setFieldValues(0, address);
		}



	public String getContactEmail() {
		return infoFields.get(IDX_DRCC_STUDY_EMAIL_FIELD).getFieldValues(0);
	}



	public void setContactEmail(String eMail) 
		{
		infoFields.get(IDX_DRCC_STUDY_EMAIL_FIELD).setFieldValues(0, eMail);
		}


	public String getContactPhone() 
		{
		return infoFields.get(IDX_DRCC_STUDY_PHONE_FIELD).getFieldValues(0);
		}



	public void setContactPhone(String phone) 
		{
		infoFields.get(IDX_DRCC_STUDY_PHONE_FIELD).setFieldValues(0, phone);
		}

	
	public String getStudyNumGroups()
		{
		return this.infoFields.get(IDX_DRCC_STUDY_NUMGROUPS_FIELD).getFieldValues(0);
		}
	
	public void setStudyNumGroups(String ng)
		{
		this.infoFields.get(IDX_DRCC_STUDY_NUMGROUPS_FIELD).setFieldValues(0, ng);
		}
	
	public String getStudyNumSubjects()
		{
		return this.infoFields.get(IDX_DRCC_STUDY_NUMSUBJECTS_FIELD).getFieldValues(0);
		}

	public void setStudyNumSubjects(String ng)
		{
		this.infoFields.get(IDX_DRCC_STUDY_NUMSUBJECTS_FIELD).setFieldValues(0, ng);
		}

	public String getStudyNumSamples()
		{
		return this.infoFields.get(IDX_DRCC_STUDY_NUMSAMPLES_FIELD).getFieldValues(0);
		}
	
	public void setStudyNumSamples(String ng)
		{
		this.infoFields.get(IDX_DRCC_STUDY_NUMSAMPLES_FIELD).setFieldValues(0, ng);
		}

	public String getStudyComments()
		{
		return this.infoFields.get(IDX_DRCC_STUDY_COMMENTS_FIELD).getFieldValues(0);
		}

	public void setStudyComments(String ng)
		{
		this.infoFields.get(IDX_DRCC_STUDY_COMMENTS_FIELD).setFieldValues(0, ng);
		}


	public void setInfoFields(ArrayList<DrccInfoField> infoFields) 
		{
		this.infoFields = infoFields;
		}
	
	}

package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import edu.umich.brcf.shared.layers.dto.ClientDTO;
import edu.umich.brcf.shared.util.interfaces.IWriteConvertable;

@Entity()
@Table(name = "CLIENT")
public class Client implements Serializable, IWriteConvertable
	{
	public static String idFormat = "(CL)\\d{1}|(CL)\\d{2}|(CL)\\d{3}|(CL)\\d{4}";
	public static String fullIdFormat = "(CL)\\d{4}";

	public static Client instance(String dept, String lab, String organizationID, User investigator, User contact)
		{
		return new Client(null, dept, lab, organizationID, investigator,contact);
		}


	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
			@Parameter(name = "idClass", value = "Client"),
			@Parameter(name = "width", value = "6") })
	@Column(name = "CLIENT_ID", unique = true, nullable = false, length = 6, columnDefinition = "CHAR(6)")
	private String clientID;

	@Basic()
	@Column(name = "DEPTORDIV", nullable = false, columnDefinition = "VARCHAR2(50)")
	private String dept;

	@Basic()
	@Column(name = "LAB", nullable = true, columnDefinition = "VARCHAR2(100)")
	private String lab;

	@Basic()
	@Column(name = "ORGANIZATION_ID", nullable = true, columnDefinition = "CHAR(6)")
	private String organizationID;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRINCIPAL_INVESTIGATOR_ID", referencedColumnName = "RESEARCHER_ID", nullable = false)
	private User investigator;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CONTACT_PERSON_ID", referencedColumnName = "RESEARCHER_ID", nullable = false)
	private User contact;

	@OneToMany(mappedBy = "client", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	List<Project> projectList;

	@OneToMany(mappedBy = "associated", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	List<ClientDocument> docList;

	
	public Client(String clientID, String dept, String lab, String organizationID, User investigator, User contact)
		{
		this.clientID = clientID;
		this.dept = dept;
		this.lab = lab;
		this.organizationID = organizationID;
		this.investigator = investigator;
		this.contact = contact;
		this.projectList = new ArrayList<Project>();
		}

	
	public Client() {   }

	
	public void update(ClientDTO dto, User investigator, User contact)
		{
		this.dept = dto.getDept();
		this.lab = dto.getLab();
		this.organizationID = dto.getOrganizationID();
		this.investigator = investigator;
		this.contact = contact;
		}

	
	public String getInvestigatorName()
		{
		if (getInvestigator() == null) 
			return "";
		
		return getInvestigator().getFullName();
		}
	
	public String getInvestigatorNameByLast()
		{
		if (getInvestigator() == null) 
			return "";
		
		return getInvestigator().getFullNameByLast();
		}

	public String getInvestigatorNameForTable()
		{
		String name = getInvestigatorName();

		if (name == null)
			return "";

		if (name.trim().startsWith("null"))
			return "";

		return name;
		}

	public String getContactName()
		{
		if (getContact() == null)
			return "";
		
		return getContact().getFullName();
		}
	
	public String getContactNameByLast()
		{
		if (getContact() == null)
			return "";
		
		return getContact().getFullNameByLast();
		}


	public String getContactNameForTable()
		{
		String name = getContactName();

		if (name == null)
			return "";

		if (name.trim().startsWith("null"))
			return "";

		return name;
		}

	public String getClientID()
		{
		return clientID;
		}

	public String getDept()
		{
		return dept;
		}

	public String getLab()
		{
		return lab;
		}

	public String getOrganizationID()
		{
		return organizationID;
		}

	public User getInvestigator()
		{
		return investigator;
		}

	public User getContact()
		{
		return contact;
		}

	public void addProject(Project project)
		{
		this.projectList.add(project);
		}

	public List<Project> getProjectList()
		{
		return this.projectList;
		}

	public List<ClientDocument> getDocList()
		{
		return docList;
		}

	public String getNodeObjectName()
		{
		return getLab() + "-" + getContact().getFirstName() + " " + getContact().getLastName();
		}

	@Override
	public String toExcelRow()
		{
		return null;
		}

	@Override
	public String toCharDelimited(String separator)
		{
		StringBuilder sb = new StringBuilder();
		if (this.getDept() != null)
			sb.append(this.getDept() + separator);
		else
			sb.append("-" + separator);

		if (this.lab != null)
			sb.append(this.lab + separator);
		else
			sb.append("-" + separator);

		if (this.getInvestigatorName() != null
				&& !this.getInvestigatorName().trim().startsWith("null"))
			sb.append(getInvestigatorName() + separator);
		else
			sb.append("-" + separator);

		System.out.println("Contact Name" + this.getContactName());

		if (this.getContactName() != null
				&& !this.getContactName().trim().startsWith("null"))
			sb.append(getContactName() + separator);
		else
			sb.append("-" + separator);

		return sb.toString();
		}
	}

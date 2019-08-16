package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.wicket.util.io.IClusterable;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import edu.umich.brcf.shared.layers.dto.UserDTO;
import edu.umich.brcf.shared.util.BCryptEncrypter;
import edu.umich.brcf.shared.util.io.StringUtils;


@Entity()
@Table(name = "RESEARCHER", uniqueConstraints = @UniqueConstraint(columnNames = {"RESEARCHER_ID", "USERNAME" }))
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements Serializable, IClusterable 
	{
	private static final long serialVersionUID = 7481346692646863316L;
	
	public static String fullIdFormat = "(U)\\d{5}";
	public static int USERNAME_LEN = 15;
	public static int USERPW_LEN = 15;
	
	public static User instance(String lastName, String firstName, String lab, String email, String phone,
	 String userName, String password, String faxNumber, Viewpoint viewpoint, String key) 
		{
		return new User(null, lastName, firstName, lab, email, phone, userName, password, faxNumber, viewpoint, key);
		}
	
	public static User instance(UserDTO dto, String key) 
		{
		return new User(null, dto.getLastName(), dto.getFirstName(), dto.getLab(), dto.getEmail(), dto.getPhone(), dto
				.getUserName(), dto.getPassword1(), dto.getFaxNumber(), dto.getViewpoint(), key);
		}
	
	public static User instance(UserDTO dto) 
		{
		return new User(dto.getId(), null, null, null, null, null, null, null, null, null, null);
		}
	
	public static User instance(String userId) 
		{
		return new User(userId);
		}
	
	
	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
			@Parameter(name = "idClass", value = "User"), @Parameter(name = "width", value = "6") })
	@Column(name = "RESEARCHER_ID", unique = true, nullable = false, length = 6, columnDefinition = "CHAR(6)")
	private String id;
	
	@Basic()
	@Column(name = "LAST_NAME", nullable = false, length = 30, columnDefinition = "VARCHAR2(30)")
	private String lastName;
	
	@Basic()
	@Column(name = "FIRST_NAME", nullable = false, length = 20, columnDefinition = "VARCHAR2(20)")
	private String firstName;
	
	@Basic()
	@Column(name = "LAB", length = 50, nullable = false, columnDefinition = "VARCHAR2(50)")
	private String lab;
	
	@Basic()
	@Column(name = "EMAIL", length = 50, nullable = false, columnDefinition = "VARCHAR2(50)")
	private String email;
	
	@Basic()
	@Column(name = "PHONE", length = 26, columnDefinition = "VARCHAR2(26)")
	private String phone;
	
	@Basic()
	@Column(name = "USERNAME", unique = true, nullable = false, length = 15, columnDefinition = "VARCHAR2(15)")
	private String userName;
	
	@Basic()
	@Column(name = "PASSWORD_NEW", nullable = true, length = 60, columnDefinition = "CHAR(60)")
	private String passwordNew;
	
	@Basic()
	@Column(name = "FAX_NUMBER", length = 26, columnDefinition = "VARCHAR2(26)")
	private String faxNumber;
	
	@Basic()
	@Column(name = "DELETED", length = 1)
	private Boolean deletedFlag;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DEFAULT_VIEWPOINT", referencedColumnName = "VIEWPOINT_ID")
	private Viewpoint viewpoint;
	
	
	public User() {     } 
	
	
	private User(String id) { this.id = id;  }

	
	protected User(String id, String lastName, String firstName, String lab, String email, String phone, String userName, String password,
			String faxNumber, Viewpoint viewpoint, String key)
		{
		super();
		this.id = id;
		this.lastName = lastName;
		this.firstName = firstName;
		this.lab = lab;
		this.email = email;
		this.phone = phone;
		this.userName = userName;
		this.passwordNew = BCryptEncrypter.encrypt(password);
		this.faxNumber = faxNumber;
		this.viewpoint = viewpoint;
		}
	
	
	public void update(UserDTO dto)
		{
		this.lastName = dto.getLastName();
		this.firstName = dto.getFirstName();
		this.lab = dto.getLab();
		this.email = dto.getEmail();
		this.phone = dto.getPhone();
		this.userName = dto.getUserName();
		this.passwordNew = BCryptEncrypter.encrypt(dto.getPassword1());
		this.faxNumber = dto.getFaxNumber();
		this.viewpoint = dto.getViewpoint();
		}
	
	
	public void updateWithoutPassword(UserDTO dto)
		{
		this.lastName = dto.getLastName();
		this.firstName = dto.getFirstName();
		this.lab = dto.getLab();
		this.email = dto.getEmail();
		this.phone = dto.getPhone();
		this.userName = dto.getUserName();
	
		// Just transfer -- already encrypted
		if (!StringUtils.isEmptyOrNull(dto.getPassword1()))
			this.passwordNew = dto.getPassword1(); 
		
		this.faxNumber = dto.getFaxNumber();
		this.viewpoint = dto.getViewpoint();
		}
	
	public void updatePassword(String password)
		{
		this.passwordNew = BCryptEncrypter.encrypt(password);
		}
	
	
	public String getFullName() 
		{
		return firstName + " " + lastName;
		}
	

	public String getFullNameByLast()
		{
		return lastName + ", " + firstName;
		}

	
	public String getId() 
		{
		return id;
		}
	
	public String getLastName() 
		{
		return lastName;
		}
	
	public String getFirstName() 
		{
		return firstName;
		}
	
	public String getLab() 
		{
		return lab;
		}
	
	public String getEmail() 
		{
		return email;
		}
	
	public String getPhone() 
		{
		return phone;
		}
	
	public String getUserName() 
		{
		return userName;
		}
	
	
	public String getFaxNumber() 
		{
		return faxNumber;
		}
	
	public Viewpoint getViewpoint() 
		{
		return viewpoint;
		}
	
	public Boolean isDeleted() 
		{
		if (deletedFlag ==null)
			return false;
		
		if (deletedFlag)
			return true;
		
		return false;
		}
	
	
	public void setDeleted()
		{
		this.deletedFlag = true;
		}
	
	public void setEmail(String email)
		{
		this.email = email;
		}
	
	public String getPasswordNew()
		{
		return passwordNew;
		}
	
	public void setPasswordNew(String passwd)
		{
		this.passwordNew = passwd;
		}
	}



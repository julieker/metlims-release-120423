package edu.umich.brcf.shared.layers.dto;


/*
import org.apache.wicket.util.io.IClusterable;

import edu.umich.brcf.shared.layers.domain.SuperUser;
import edu.umich.brcf.shared.layers.domain.User;
import edu.umich.brcf.shared.layers.domain.Viewpoint;
*/

public class OldUserDTO //implements IClusterable 
	{ 
	}
	/*
	public static UserDTO instance(String id, String lastName, String firstName, String lab, String email,
			String phone, String userName, String password, String faxNumber, Viewpoint viewpoint, boolean superUser) {
		// boolean isSuperUser = (user instanceof SuperUser)?true:false;
		return new UserDTO(id, lastName, firstName, lab, email, phone, userName, password, faxNumber, viewpoint,
				superUser);
	}

	public static UserDTO instance(User user) {
		return new UserDTO(user);
	}

	
	private String id;
	private String lastName;
	private String firstName;
	private String lab;
	private String email;
	private String phone;
	private String userName;
	private String password1;
	private String password2;
	private String faxNumber;
	private Viewpoint viewpoint;
	private Boolean superUser;

	public UserDTO() {
		this.superUser = false;
	}

	private UserDTO(User user) {
		super();
		this.id = user.getId();
		this.lastName = user.getLastName();
		this.firstName = user.getFirstName();
		this.lab = user.getLab();
		this.email = user.getEmail();
		this.phone = user.getPhone();
		this.userName = user.getUserName();
		this.password1 = user.getPassword();
		this.password2 = user.getPassword();
		this.faxNumber = user.getFaxNumber();
		this.viewpoint = user.getViewpoint();
		this.superUser = (user instanceof SuperUser);
	}

	private UserDTO(String id, String lastName, String firstName, String lab, String email, String phone,
			String userName, String password, String faxNumber, Viewpoint viewpoint, boolean superUser) {
		super();
		this.id = id;
		this.lastName = lastName;
		this.firstName = firstName;
		this.lab = lab;
		this.email = email;
		this.phone = phone;
		this.userName = userName;
		this.password1 = password;
		this.password2 = password;
		this.faxNumber = faxNumber;
		this.viewpoint = viewpoint;
		this.superUser = superUser;
	}

	public String getFullName() {
		return firstName + " " + lastName;
	}

	public String getId() {
		return id;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLab() {
		return lab;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword1() {
		return password1;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public Viewpoint getViewpoint() {
		return viewpoint;
	}

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLab(String lab) {
		this.lab = lab;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword1(String password1) {
		this.password1 = password1;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	public void setViewpoint(Viewpoint viewpoint) {
		this.viewpoint = viewpoint;
	}

	public Boolean getSuperUser() {
		return superUser;
	}

	public void setSuperUser(Boolean superUser) {
		this.superUser = superUser;
	}
}


/*
import org.apache.wicket.IClusterable;

import edu.umich.brcf.shared.layers.domain.User;
import edu.umich.brcf.shared.layers.domain.Viewpoint;
import edu.umich.brcf.shared.util.DesEncrypter;



public class UserDTO implements IClusterable {
	public static UserDTO instance(String id, String lastName, String firstName, String lab, String email,
			String phone, String userName, String password, String faxNumber, Viewpoint viewpoint, boolean superUser) {
	
		return new UserDTO(id, lastName, firstName, lab, email, phone, userName, password, faxNumber, viewpoint, false);
	}

	public static UserDTO instance(User user) {
		return new UserDTO(user);
	}

	
	private String id;
	private String lastName;
	private String firstName;
	private String lab;
	private String email;
	private String phone;
	private String userName;
	private String password1;
	private String password2;
	private String faxNumber;
	private Viewpoint viewpoint;
	private Boolean superUser;

	public UserDTO() {
		this.superUser = false;
	}

	private UserDTO(User user) {
		super();
		this.id = user.getId();
		this.lastName = user.getLastName();
		this.firstName = user.getFirstName();
		this.lab = user.getLab();
		this.email = user.getEmail();
		this.phone = user.getPhone();
		this.userName = user.getUserName();
		this.password1 = user.getPassword();
		this.password2 = user.getPassword();
		this.faxNumber = user.getFaxNumber();
		this.viewpoint = user.getViewpoint();
		this.superUser = false; //(user instanceof SuperUser);
		}

	private UserDTO(String id, String lastName, String firstName, String lab, String email, String phone,
			String userName, String password, String faxNumber, Viewpoint viewpoint, boolean superUser) {
		super();
		this.id = id;
		this.lastName = lastName;
		this.firstName = firstName;
		this.lab = lab;
		this.email = email;
		this.phone = phone;
		this.userName = userName;
		this.password1 = password;
		this.password2 = password;
		this.faxNumber = faxNumber;
		this.viewpoint = viewpoint;
		this.superUser = false; //superUser;
		}

	
	
	public String getFullName() {
		return firstName + " " + lastName;
	}

	public String getId() {
		return id;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLab() {
		return lab;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword1() {
		return password1;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public Viewpoint getViewpoint() {
		return viewpoint;
	}

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLab(String lab) {
		this.lab = lab;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword1(String password1) {
		this.password1 = password1;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	public void setViewpoint(Viewpoint viewpoint) {
		this.viewpoint = viewpoint;
	}

	public Boolean getSuperUser() {
		return superUser;
	}

	public void setSuperUser(Boolean superUser) {
		this.superUser = superUser;
	}
}
*/
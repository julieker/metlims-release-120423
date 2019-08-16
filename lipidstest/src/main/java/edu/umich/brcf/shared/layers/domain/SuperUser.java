
package edu.umich.brcf.shared.layers.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity()
@Table(name = "SUPER_USER")
public class SuperUser extends User 
	{
	public static User instance(String lastName, String firstName, String lab, String email, String phone,
		String userName, String password, String faxNumber, Viewpoint viewpoint, String key) 
		{
		return new SuperUser(lastName, firstName, lab, email, phone, userName, password, faxNumber, viewpoint, key);
		}

	public SuperUser() {    } 

	private SuperUser(String lastName, String firstName, String lab, String email, String phone, String userName,
		String password, String faxNumber, Viewpoint viewpoint, String key) 
		{
		super(null, lastName, firstName, lab, email, phone, userName, password, faxNumber, viewpoint, key);
		}
	}

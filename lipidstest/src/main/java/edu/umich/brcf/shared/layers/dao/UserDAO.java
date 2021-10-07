///////////////////////////////////////////
// Written by Anu Janga
// Revisited by Jan Wigginton April 2015, August 2016
///////////////////////////////////////////


package edu.umich.brcf.shared.layers.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Repository;

import edu.umich.brcf.shared.layers.domain.Client;
import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.layers.domain.User;
import edu.umich.brcf.shared.layers.domain.Viewpoint;
import edu.umich.brcf.shared.layers.dto.UserDTO;




@Repository
public class UserDAO extends BaseDAO
	{
	Logger log = Logger.getLogger(UserDAO.class);
	private String godUser = "God";
	private String godPassword = "iamlatein8ru2?";
    private String keyString;
	private IdGeneratorDAO idGeneratorDAO;

	
	private List<UserDTO> initializeUserListToUserDTOList(List<User> list)
		{
		List<UserDTO> dtoList = new ArrayList<UserDTO>();
		for (User user : list)
			{
			Hibernate.initialize(user.getViewpoint());
			dtoList.add(putUserIntoUserDTO(user));
			}
		return dtoList;
		}

	
	public List<UserDTO> allAdmins()
		{
		List<User> userList = getEntityManager().createQuery("from User u left join fetch u.viewpoint v where v.name='Admin' and u.deletedFlag is null or u.deletedFlag = false").getResultList();
		return initializeUserListToUserDTOList(userList);
		}

	
	public List<UserDTO> allUsers()
		{
		List<User> userList = getEntityManager().createQuery("from User u where u.deletedFlag is null or u.deletedFlag = false order by u.lastName").getResultList();
		return initializeUserListToUserDTOList(userList);
		}

	
	// ALTER TABLE RESEARCHER_NEW ADD
	// PASSWORD_OLD 
	public List<User> allUsersWithNullOldPasswords()
		{
		List<User> userPList = getEntityManager().createQuery("from User u where  passwordNew is null order by u.lastName").getResultList();
		return userPList;
		}

	
	public List<User> allUsersWithNullNewPasswords()
		{
		List<User> userPList = getEntityManager().createQuery("from User u where  passwordNew is null order by u.lastName").getResultList();
		return userPList;
		}

	
	
	
	public void populateOldUserPasswords(List<User> user)
		{
//		for(User user : user:Plist)
//			{
//			String tempPassword = "ru4real??";
//			String encrypedTemp = DesEncrypter.encrypt(encryptedTemp);
		}

	
	private List<String> allUserNames(boolean withIds)
		{
		Query query = getEntityManager().createNativeQuery("select cast(u.last_name as VARCHAR2(30)), cast(u.first_name as"
					+ " VARCHAR2(20)), cast(u.researcher_id AS VARCHAR2(6)) from Researcher  u order by u.last_name");

		List<Object[]> userList = query.getResultList();

		ArrayList<String> fullNames = new ArrayList<String>();
		for (Object[] user : userList)
			{
			String firstName = (String) user[1];
			String lastName = (String) user[0];
			String id = "(" + (String) user[2] + ")";
			String name = lastName + ", " + firstName;

			if (withIds)
				name += id;

			fullNames.add(name);
			}

		return fullNames;
		}
	
	// issue 181
	public List<String> allUserNamesContactSearch(String input)
		{
		Query query = getEntityManager().createNativeQuery("select cast(u.last_name as VARCHAR2(30)), cast(u.first_name as"
					+ " VARCHAR2(20)), cast(u.researcher_id AS VARCHAR2(6)) from Researcher  u where (upper(last_name) like '%" + input + "%' or upper(first_name) like '%" + input + "%') order by u.last_name");
	
		List<Object[]> userList = query.getResultList();
	
		ArrayList<String> fullNames = new ArrayList<String>();
		for (Object[] user : userList)
			{
			String firstName = (String) user[1];
			String lastName = (String) user[0];
			String id = "(" + (String) user[2] + ")";
			String name = lastName + ", " + firstName;
	
			fullNames.add(name);
			}
	
		return fullNames;
		}

	public List<String> allUserNames()
		{
		return allUserNames(false);
		}
	

	public List<String> allUserNamesAndIds()
		{
		return allUserNames(true);
		}
	

	public List<String> allAdminNames()
		{
		Query query = getEntityManager().createNativeQuery("select cast(u.last_name as VARCHAR2(30)), cast(u.first_name as"
			+ " VARCHAR2(20)), cast(u.researcher_id as VARCHAR(6)) from Researcher  u where u.default_viewpoint = '99' order by u.last_name");

		List<Object[]> userList = query.getResultList();

		ArrayList<String> fullNames = new ArrayList<String>();
		for (Object[] user : userList)
			{
			String userId = (String) user[2];
			String firstName = (String) user[1];
			String lastName = (String) user[0];

			fullNames.add(firstName + " " + lastName + " (" + userId + ")");
			}

		return fullNames;
		}
	


	public UserDTO authenticate(String username, String password)
		{
		User user;
		String bcrypt_pw = null;
		try
			{
			user = loadUserByUserName(username);  // stored password
			
			bcrypt_pw = user.getPasswordNew();
			if (BCrypt.checkpw(password, bcrypt_pw.substring(0, 60)))
				return putUserIntoUserDTO(user);
			}
		catch (Exception e) 
			{ 
			System.out.println("Catching exception for user name " +  username + System.getProperty("line.separator"));
			e.printStackTrace();
			}
		return null;
		}
			
	
	public void delete(UserDTO user)
		{
		User u = getEntityManager().find(User.class, user.getId());
		u.setDeleted();
		}

	
	public UserDTO save(UserDTO user)
		{
		User u = User.instance(user.getLastName(), user.getFirstName(),user.getLab(), user.getEmail(), user.getPhone(),
				user.getUserName(), user.getPassword1(), user.getFaxNumber(),user.getViewpoint(), "");

		getEntityManager().persist(u);
		return putUserIntoUserDTO(u);
		}

	
	public UserDTO putUserIntoUserDTO(User u)
		{
		return UserDTO.instance(u.getId(), u.getLastName(), u.getFirstName(),u.getLab(), u.getEmail(), u.getPhone(), u.getUserName(),
				u.getPasswordNew(), u.getFaxNumber(), u.getViewpoint(), false);
		}

	
	public User loadById(String id)
		{
		User user = getEntityManager().find(User.class, id);
		return user;
		}
	

	public User loadUserByUserName(String userName)
		{
		List<User> userList = getEntityManager().createQuery("from User u where u.userName = :userName and  (u.deletedFlag is null or u.deletedFlag = false)")
				.setParameter("userName", userName).getResultList();
		
		User user = (User) DataAccessUtils.requiredSingleResult(userList);
		Hibernate.initialize(user.getViewpoint());
		return user;
		}

	
	public String getUserIdByUserName(String userName)
		{
		List<User> userList = getEntityManager().createQuery("from User u where u.id = :userId and (u.deletedFlag is null or u.deletedFlag = false)")
				.setParameter("userId", userName).getResultList();
		
		User user = (User) DataAccessUtils.requiredSingleResult(userList);
		return user.getFullName();
		}

	
	public String getUserNameByUserId(String userId)
		{
		List<User> userList = getEntityManager().createQuery("from User u where u.id = :userId and (u.deletedFlag is null or u.deletedFlag = false)")
				.setParameter("userId", userId).getResultList();
		
		User user = (User) DataAccessUtils.requiredSingleResult(userList);
		return user.getUserName();
		}

	
	public String getFullNameByUserId(String userId)
		{
		List<User> userList = getEntityManager().createQuery("from User u where u.id = :userId and (u.deletedFlag is null or u.deletedFlag = false)")
				.setParameter("userId", userId).getResultList();
		
		User user = (User) DataAccessUtils.requiredSingleResult(userList);
		return user.getFullName();
		}

	
	public List<Project> getUserProjectsAndExperiments(String id)
		{
		User user = loadById(id);
		List<Project> projlist = new ArrayList<Project>();
	
		List<Client> clientList = getEntityManager().createQuery("from Client c where c.investigator = :user or c.contact = :user")
				.setParameter("user", user).getResultList();
		
		for (Client cl : clientList)
			{
			Hibernate.initialize(cl.getProjectList());
			projlist.addAll(cl.getProjectList());
			}
		for (Project pr : projlist)
			Hibernate.initialize(pr.getExperimentList());
			
		return projlist;
		}

	
	public List<Project> getUserProjectsAndExperiments(User user)
		{
		List<Client> clientList = getEntityManager().createQuery("from Client c where c.investigator = :user or c.contact = :user")
				.setParameter("user", user).getResultList();
		
		List<Project> projlist = new ArrayList<Project>();
		for (Client cl : clientList)
			{
			Hibernate.initialize(cl.getProjectList());
			projlist.addAll(cl.getProjectList());
			}
		for (Project pr : projlist)
			Hibernate.initialize(pr.getExperimentList());
	
		return projlist;
		}

	
	public List<Project> getUserProjectsAndExperimentsByStartDate(String id)
		{
		User user = loadById(id);
		List<Client> clientList = getEntityManager().createQuery("from Client c where c.investigator = :user or c.contact = :user")
				.setParameter("user", user).getResultList();
		
		List<Project> projlist = new ArrayList<Project>();
		for (Client cl : clientList)
			{
			Hibernate.initialize(cl.getProjectList());
			projlist.addAll(cl.getProjectList());
			}
		for (Project pr : projlist)
			Hibernate.initialize(pr.getExperimentList());
	
		return projlist;
		}
	

	public boolean checkUserNameExists(String name)
		{
		Query query = getEntityManager().createNativeQuery("select * from researcher u where u.username = ?1").setParameter(1, name);
		return (query.getResultList().size() > 0);
		}
	
	
	public List<Viewpoint> allViewpoints()
		{
		return getEntityManager().createQuery("from Viewpoint").getResultList();
		}
	

	public String getGodUser()
		{
		return godUser;
		}


	public void setGodUser(String godUser)
		{
		this.godUser = godUser;
		}

	public String getGodPassword()
		{
		return godPassword;
		}

	public void setGodPassword(String godPassword)
		{
		this.godPassword = godPassword;
		}

	public void setIdGeneratorDAO(IdGeneratorDAO idGeneratorDAO)
		{
		this.idGeneratorDAO = idGeneratorDAO;
		}

	}

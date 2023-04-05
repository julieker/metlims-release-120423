package edu.umich.brcf.shared.layers.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.layers.dao.UserDAO;
import edu.umich.brcf.shared.layers.domain.User;
import edu.umich.brcf.shared.layers.domain.Viewpoint;
import edu.umich.brcf.shared.layers.dto.UserDTO;
import edu.umich.brcf.shared.util.BCryptEncrypter;
import edu.umich.brcf.shared.util.METWorksException;
import edu.umich.brcf.shared.util.SecurityOptions;
import edu.umich.brcf.shared.util.io.StringUtils;



@Transactional
public class UserService 
	{
	UserDAO userDao;

	public User loadById(String id)
		{
		Assert.notNull(id);
		return userDao.loadById(id);
		}
	
	
	public LoadableDetachableModel<User> getUserModel(final String userName)
		{
		return new LoadableDetachableModel<User>()
			{
			@Override
			protected User load() 
				{ 
				User user = userDao.loadUserByUserName(userName);
				return (user == null ? new User() : user); 
				}
			};
		}
	
	// issue 181
	public List<String> allUserNamesContactSearch(String input) 
		{
		return userDao.allUserNamesContactSearch(input.toUpperCase());
		}
	
	public List<UserDTO> allAdmins() 
		{
		return userDao.allAdmins();
		}
	
	
	public List<String> allAdminNames() 
		{
		return userDao.allAdminNames();
		}
	
	public List<String> allAdminNames(boolean isNotTracking )
		{
		return userDao.allAdminNames(isNotTracking);
		}
	
	public boolean isTrustedAdmin(String userId)
		{
		User user = loadById(userId);
		
		if (user == null) return false;
		String userName = user.getUserName();
		return SecurityOptions.isTrustedName(userName) && isAdmin(userId);
		}

	
	public boolean isAdmin(String userId)
		{
		if (isAccountAdmin(userId))
			return true;
					
		User u = loadById(userId);
		if (u == null || (u.isDeleted() != null && u.isDeleted() == true)) return false;
		
		return (u.getViewpoint().getId() == 99L || u.getViewpoint().getId() == 98L);
		}
	
	// Issue 79
	public boolean isAliquotAdmin(String userId)
		{
		User u = loadById(userId);
		return (u.isAliquotAdmin() != null && u.isAliquotAdmin() == true);
		}
	
	
	public boolean isAccountAdmin(String userId)
		{
		User u = loadById(userId);
		if (u == null || (u.isDeleted() != null && u.isDeleted() == true)) return false;
		
		boolean isadmin = (u.getViewpoint().getId() == 97L ); ///|| u.getViewpoint().getId() == 98L);
		return isadmin;
		}

	
	public UserDTO isGodUser(String userName, String password) 
		{
		return null;
		}
		
	public boolean updatePassword(String oldPassword, UserDTO accountDto, String currentUserId )
		{
		if (!(accountDto.getPassword1().equals(accountDto.getPassword2())))
			throw new RuntimeException("Password Error : User passwords do not match");

		boolean updated = false;
		try 
			{
			User u = userDao.loadUserByUserName(accountDto.getUserName());
			String bcrypt_pw = u.getPasswordNew();

			if (!isAccountAdmin(currentUserId))
				{
				if (!BCrypt.checkpw(oldPassword, bcrypt_pw.substring(0, 60)))
					{
					throw new RuntimeException("Password Error : Old password is incorrect.  Cannot do update");
					}
			//	else
			//		System.out.println("Password was correct" + oldPassword);
				}
			u.updatePassword(accountDto.getPassword1());
			updated = true;
			} 
		catch (Exception e)
			{
			updated = false;
			throw e;
			}
		
		return updated;
		}
	
	
	public UserDTO updateWithoutPassword(UserDTO dto)
		{
		Assert.notNull(dto);
		UserDTO myDto = null;
		
		try 
			{ 
			User u = userDao.loadUserByUserName(dto.getUserName());
			u.updateWithoutPassword(dto);
			myDto = userDao.putUserIntoUserDTO(u);
			}
		catch (Exception e) { myDto = null; }
		
		return myDto;
		}
		
		
	public UserDTO saveOrUpdateUser(UserDTO dto) throws METWorksException
		{
		Assert.notNull(dto);
	
		UserDTO myDto = null;
		
		if (!(dto.getPassword1().equals(dto.getPassword2())))
			throw new RuntimeException ("Password Error : User passwords do not match");
		
		if (StringUtils.isEmptyOrNull(dto.getId()) || (dto.getId().equals("to be assigned")))
			{
			if (userDao.checkUserNameExists(dto.getUserName()))
				throw new RuntimeException ("Duplicate User : Please choose another user name");
			
			try {  myDto = userDao.save(dto);    }
			catch (Exception e) { myDto = null;  }
			}
		else
			{
			try 
				{
				User u = userDao.loadUserByUserName(dto.getUserName());
				u.update(dto);
				myDto = userDao.putUserIntoUserDTO(u);
				} 
			catch (Exception e)  { myDto = null; }
			}
		
		return myDto;
		}
	
	
	public List<Project> getUserProjectsAndExperiments(String id)
		{
		return userDao.getUserProjectsAndExperiments(id);
		}
	
	
	public List<Project> getUserProjectsAndExperiments(User user)
		{
		return userDao.getUserProjectsAndExperiments(user);
		}

	public UserDTO authenticate(String username, String password) 
		{
		return userDao.authenticate(username, password);
		}
	
	public void delete(UserDTO user) 
		{
		userDao.delete(user);
		}

	public User getUserByUserName(String username) 
		{
		return userDao.loadUserByUserName(username);
		}
	
	// issue 210
	public User loadUserByFullName(String fullName) 
		{
		return userDao.loadUserByFullName(fullName);
		}
	
	public String getFullNameByUserId(String userId) 
		{
		return userDao.getFullNameByUserId(userId);
		}
	
	public String getUserNameByUserId(String userId)
		{
		if (StringUtils.isEmptyOrNull(userId))
			return "";
		
		return userDao.getUserNameByUserId(userId);
		}

	
	public String getUserIdByUserName(String userName) 
		{
		if (StringUtils.isEmptyOrNull(userName))
			return "";
		
		return userDao.getUserIdByUserName(userName);
		} 
	
	
	public List<UserDTO> allUsers() 
		{
		return userDao.allUsers();
		}
	
	
	public List<String> allUserNames()
		{
		return userDao.allUserNames();
		}
	
	
	public List<String> allUserNamesAndIds()
		{
		return userDao.allUserNamesAndIds();
		}

	
	public List<Viewpoint> allViewpoints() 
		{
		return userDao.allViewpoints();
		}
	

	@Required
	public void setUserDao(UserDAO userDao) 
		{
		this.userDao = userDao;
		}
	
	
	public UserDAO getUserDao()
		{
		return this.userDao;
		}

	
	public List getUsersForInstrumentAssociations()
		{
		return null;
		}
	}

package edu.umich.brcf.shared.panels.login;


import org.wicketstuff.security.authentication.LoginException;
import org.wicketstuff.security.hive.authentication.DefaultSubject;
import org.wicketstuff.security.hive.authentication.LoginContext;
import org.wicketstuff.security.hive.authentication.Subject;

import edu.umich.brcf.shared.layers.dto.UserDTO;
import edu.umich.brcf.shared.layers.service.UserService;



/**
 * A login context. these are throw away objects.
 * 
 * @author marrink
 */
public final class MedWorksLoginContext extends LoginContext {
	private final String username;
	private final String password;
	private UserService userService;

	/**
	 * 
	 * Constructor for logGing in.
	 * 
	 * @param username
	 * @param password
	 * 
	 * 
	 */
	
	public MedWorksLoginContext(String username, String password, UserService userService) 
		{
		this.username = username;
		this.password = password;
		this.userService = userService;
		}
	
	public MedWorksLoginContext() 
		{
		username = null;
		password = null;
		}

	public Subject login() throws LoginException 
		{
		UserDTO user;
		
		if ((user = userService.isGodUser(username, password)) == null) 
			{
			user = userService.authenticate(username, password);
			//user.setEmail("EMAIL 2");
			//userService.saveOrUpdateUser(user);
			}
		
		// if (username != null && Objects.equal(username, password)) {
		if (user != null) 
			{
			DefaultSubject subject = new DefaultSubject();
			subject.addPrincipal(new MedWorksPrincipal(user.getViewpoint().getName()));
			return subject;
			}
		throw new LoginException("Username and password do not match any known user.");
		}
	}
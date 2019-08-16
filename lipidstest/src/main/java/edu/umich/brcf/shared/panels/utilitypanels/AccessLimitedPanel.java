////////////////////////////////////////////////////
// AccessLimitedPanel.java
// Written by Jan Wigginton, Mar 21, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.panels.utilitypanels;


import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;


public class AccessLimitedPanel extends Panel
	{
	@SpringBean 
	private UserService userService;
	
	private boolean isAccountAdminOnly = false;
	private boolean isAdminOnly = false;

	public AccessLimitedPanel(String id)
		{
		super(id);
		}
	
	@Override
	public boolean isEnabled()
		{
		String userId = (((MedWorksSession) getSession()).getCurrentUserId());
		if (isAccountAdminOnly)
			return userService.isAccountAdmin(userId);
		
		if (isAdminOnly) 
			return userService.isAdmin(userId);
		
		return true;
		}


	public boolean isAccountAdminOnly()
		{
		return isAccountAdminOnly;
		}


	public boolean isAdminOnly()
		{
		return isAdminOnly;
		}


	public void setAccountAdminOnly(boolean isAccountAdminOnly)
		{
		this.isAccountAdminOnly = isAccountAdminOnly;
		}


	public void setAdminOnly(boolean isAdminOnly)
		{
		this.isAdminOnly = isAdminOnly;
		}
	}

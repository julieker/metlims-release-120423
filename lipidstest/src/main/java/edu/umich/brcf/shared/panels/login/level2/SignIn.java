package edu.umich.brcf.shared.panels.login.level2;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.security.WaspSession;
import org.wicketstuff.security.authentication.LoginException;
import org.wicketstuff.security.hive.authentication.LoginContext;

import edu.umich.brcf.shared.panels.login.SignInPanel;

// Authorization
public final class SignIn extends WebPage 
	{
	public SignIn() { this(null); }

	public SignIn(final PageParameters parameters) 
		{
		// God
		add(new SignInPanel("signInPanel") 
			{
			public boolean signIn(String username, String password) 
				{
				LoginContext ctx = new MedWorksLevel0Context(username, password);
				try 
					{
					WaspSession w;
					((WaspSession) getSession()).login(ctx);
					} 
				catch (LoginException e) { return false; }
				return true;
				}
			});
		}
	}

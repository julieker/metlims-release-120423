package edu.umich.brcf.shared.panels.login;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.request.Request;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.request.cycle.RequestCycle;

import edu.umich.brcf.MChearApplication;


public class HeaderPanel extends Panel 
	{
	public HeaderPanel(String id, WebPage page) 
		{
		super(id);
		

		
		MedWorksSession session = ((MedWorksSession) getSession());
		add(new Label("userInfo", ((MedWorksSession) getSession()).getCurrentUserName()));
		//((MedWorksSession) getSession()).setUserInfo(user);
		//System.out.println("Name is " + ((MedWorksSession) getSession()).getCurrentUserName());
		
		add(new Label("userLevel", ((MedWorksSession) getSession()).getLevel().getName()));
		add(buildSignoutLink("signOut"));
		}

	
	private Link buildSignoutLink(String id) // issue 464
		{
		// issue 39
		final Link signOutLink = new Link <Void> (id, new Model("")) 
			{
			public void onClick() { invalidateSession(); }
			};

		signOutLink.add(new AbstractAjaxTimerBehavior(Duration.minutes(MedWorksBasePage.TIMER_INTERVAL_MINUTES)) 
			{
			protected void onTimer(AjaxRequestTarget target) 
				{
				if (!HeaderPanel.this.isVisible())
					return;
				
				MedWorksSession session = (MedWorksSession) getSession();
				session.setMinutesToTimeout(session.getMinutesToTimeout() - MedWorksBasePage.TIMER_INTERVAL_MINUTES);
				
				if (session.getMinutesToTimeout() < 0)
					invalidateSession();
				}
			});
		
		return signOutLink;
		}
	
	//UserRegistrationPage

	//private String getClientIpAddress() 
	//	{
	//	HttpServletRequest  request = (HttpServletRequest)getRequestCycle().getRequest()..getContainerRequest(); 
	//	String ipAddress=request.getHeader("X-Forwarded-For"); 
		
	//	return ipAddress;
		//String hostName = ((WebRequest) RequestCycle.get().getRequest()).  ; getHttpServletRequest().getRemoteHost();
		///return ((WebRequest) RequestCycle.get().getRequest()).getHttpServletRequest().getRemoteAddr();
	//	}

	
	protected void onBeforeRender() 
		{
		super.onBeforeRender();
		((MedWorksSession) getSession()).resetTimeoutClock();
		}

	
	
	// Solution for setRedirect(true) --- Try throwing RestartResponseException instead of calling setResponsePage. 
	// This will stop the construction of your StopPage immediately. 

	// 
	private void invalidateSession() 
		{
		getSession().invalidateNow();
		//getRequestCycle().setRedirect(true);
		getSession().clear();
		//getSession().getPageMaps().clear();
		//getSession().getPageManager().getPage(.)
	
		((MedWorksSession) getSession()).logoff(((MChearApplication) getApplication()).getLogoffContext()); // issue 464
		
		setResponsePage(MedWorksLoginPage.class);
		}
	}




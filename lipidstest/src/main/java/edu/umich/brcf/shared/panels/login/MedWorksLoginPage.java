package edu.umich.brcf.shared.panels.login;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**  @author marrink */

import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.wicketstuff.security.hive.authentication.LoginContext;

import edu.umich.brcf.shared.layers.service.UserService;


public class MedWorksLoginPage extends WebPage
	{
	private static final long serialVersionUID = 1L;

	@SpringBean
	private UserService userService;

	public void setUserService(UserService userService)
		{
		this.userService = userService;
		}

	// stateless so the login page will not throw a timeout exception. Note that is only a hint we need to have stateless components on the			
	// page for this to work, like a statelessform
	public MedWorksLoginPage()
		{
		setStatelessHint(true);
	//	add(new FeedbackPanel("feedback")
	//		{
	//		private static final long serialVersionUID = 1L;//

	//		public boolean isVisible() { return anyMessage(); }
	//		});
		
		newUserPasswordSignInPanel("signInPanel");
		
		add(new AbstractAjaxTimerBehavior(Duration.minutes(MedWorksBasePage.TIMER_INTERVAL_MINUTES))
			{
			protected void onTimer(AjaxRequestTarget target) {   }
			});
		}


	protected void newUserPasswordSignInPanel(String panelId)
		{
		add(new SignInPanel(panelId)
			{
			private static final long serialVersionUID = 1L;

			public boolean signIn(final String username, final String password)
				{
				LoginContext context = new MedWorksLoginContext(username, password, userService);
				try
					{
					if ((MedWorksSession) getSession() != null)
						((MedWorksSession) getSession()).login(context);
					
					if (!username.equals("God"))
						((MedWorksSession) getSession()).setUserInfo(userService.getUserByUserName(username));

					try { System.out.println("============" + ((MedWorksSession) getSession()).getBrowserHeight());   } 
					catch (Exception e)  {    }
					} 
				catch (Exception e)  {  return false;   }

				return true;
				}
			});
		}
	}

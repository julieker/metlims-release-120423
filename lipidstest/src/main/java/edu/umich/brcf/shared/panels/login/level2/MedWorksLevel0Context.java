package edu.umich.brcf.shared.panels.login.level2;

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

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Objects;
import org.wicketstuff.security.authentication.LoginException;
import org.wicketstuff.security.hive.authentication.DefaultSubject;
import org.wicketstuff.security.hive.authentication.LoginContext;
import org.wicketstuff.security.hive.authentication.Subject;

import edu.umich.brcf.shared.panels.login.IMedWorksSecurePage;
import edu.umich.brcf.shared.panels.login.MedWorksPrincipal;


/**
 * Context for primary login. It will let you use the home, balance and transfer
 * pages. just not the commit page. you will need a secondary login for that.
 * 
 * @author marrink ERROR
 * 
 */
public class MedWorksLevel0Context extends LoginContext
	{
	/**
	 * Subject for primary login. Only authenticates non
	 * {@link METWorksSecuredPageInterface}s.
	 * 
	 * @author marrink
	 */
	private static final class MyPrimarySubject extends DefaultSubject
		{
		private static final long serialVersionUID = 1L;

		/**
		 * auth
		 * 
		 * @see Subject#isClassAuthenticated(java.lang.Class)
		 */
		public boolean isClassAuthenticated(Class class1)
			{
			// only authenticate non topsecret pages
			return !IMedWorksSecurePage.class.isAssignableFrom(class1);
			}

		/**
		 * @see Subject#isComponentAuthenticated(org.apache.wicket.Component)
		 */
		public boolean isComponentAuthenticated(Component component)
			{
			// we only care about pages
			if (component instanceof Page)
				return isClassAuthenticated(component.getClass());
			return true;
			}

		/**
		 * @see Subject#isModelAuthenticated(org.apache.wicket.model.IModel,
		 *      org.apache.wicket.Component)
		 */
		public boolean isModelAuthenticated(IModel model, Component component)
			{
			return true;
			}
		}

	private final String username;
	private final String password;

	/**
	 * 
	 * Constructor for logoff.
	 */
	public MedWorksLevel0Context()
		{
		super(0, true);
		username = null;
		password = null;
		}

	/**
	 * Constructor for login.
	 * 
	 * @param username
	 * @param password
	 */
	public MedWorksLevel0Context(String username, String password)
		{
		super(0, true);
		this.username = username;
		this.password = password;
		}

	/**
	 * @see org.apache.wicket.security.hive.authentication.LoginContext#login()
	 */
	public Subject login() throws LoginException
		{
		if (Objects.equal(username, password))
			{
			// usually there will be a db call to verify the credentials
			DefaultSubject subject = new MyPrimarySubject();
			// add principals as required, usually these come from a db
			subject.addPrincipal(new MedWorksPrincipal("basic"));
			return subject;
			}
		throw new LoginException("username does not match password");
		}

	/**
	 * 
	 * @see org.apache.wicket.security.hive.authentication.LoginContext#preventsAdditionalLogins()
	 */
	public boolean preventsAdditionalLogins()
		{
		return false;
		}

	}

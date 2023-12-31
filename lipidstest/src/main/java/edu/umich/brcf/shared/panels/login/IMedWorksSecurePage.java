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
/**
 * Tagging interface to differentiate between page where 1 login is sufficient
 * and pages where a secondary login is required.
 * 
 * @author marrink
 */

package edu.umich.brcf.shared.panels.login;

import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.wicketstuff.security.actions.WaspAction;
import org.wicketstuff.security.checks.ClassSecurityCheck;
import org.wicketstuff.security.checks.ISecurityCheck;

//login

public interface IMedWorksSecurePage
	{
	// if required you can place a similar check on all implementations of this
	// page

	static final ISecurityCheck customcheck = new ClassSecurityCheck(
			IMedWorksSecurePage.class)
		{
			private static final long serialVersionUID = 1L;

			/**
			 * @see org.apache.wicket.security.checks.ClassSecurityCheck#isActionAuthorized(org.apache.wicket.security.actions.WaspAction)
			 */

			public boolean isActionAuthorized(WaspAction action)
				{
				// if not authenticated for topsecret pages go to the secondary
				// login page.
				if (isAuthenticated())
					return getStrategy().isClassAuthorized(getClazz(), action);

				throw new RestartResponseAtInterceptPageException(
						MedWorksLoginPage.class);
				}
		};
	}

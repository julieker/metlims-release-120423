package edu.umich.brcf;

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

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.security.WaspSession;
import org.wicketstuff.security.components.SecureWebPage;
import org.wicketstuff.security.hive.authentication.LoginContext;
import org.wicketstuff.security.swarm.SwarmWebApplication;

import edu.umich.brcf.shared.panels.login.HeaderPanel;


/**
 * Base page for all my other pages.
 * 
 * @author marrink
 * METWorksLoginContext
 */

public abstract class MedWorksSecurePage extends SecureWebPage {

	private static final long serialVersionUID = 1L;
	protected Border border;

	/**
	 * 
	 */
	public MedWorksSecurePage() 
		{
		init();
		}

	public MedWorksSecurePage(PageParameters parameters) 
		{
		super();
		init();
		}

	public MedWorksSecurePage(IModel model) 
		{
		super();
		init();
		}

	protected void init() 
		{
		add(new HeaderPanel("header", this));
		explain();
		}

	protected final WaspSession getSecureSession() 
		{	
		return (WaspSession) Session.get();
		}

	protected final SwarmWebApplication getSecureApplication() 
		{
		return (SwarmWebApplication) Application.get();
		}

	/**
	 * Allows subclasses to specify which context should be used when logging
	 * off
	 * 
	 * @return the context
	 */
	protected final LoginContext getLogoffContext() 
		{
		Application app = Application.get();
		if (app instanceof MChearApplication)
			return ((MChearApplication) app).getLogoffContext();
		throw new WicketRuntimeException("Application is not a subclass of " + MChearApplication.class);
		}

	protected void explain() {
	}
}

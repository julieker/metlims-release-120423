package edu.umich.brcf;

import java.net.MalformedURLException;

import org.apache.wicket.Page;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.WicketRuntimeException;
import org.wicketstuff.security.WaspSession;
import org.wicketstuff.security.hive.HiveMind;
import org.wicketstuff.security.hive.authentication.LoginContext;
import org.wicketstuff.security.hive.config.PolicyFileHiveFactory;
import org.wicketstuff.security.hive.config.SwarmPolicyFileHiveFactory;
import org.wicketstuff.security.swarm.SwarmWebApplication;

import edu.umich.brcf.shared.panels.login.MedWorksLoginContext;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.panels.login.MedWorksLoginPage;



public class MChearApplication extends SwarmWebApplication
	{    
	public MChearApplication() 
		{
		super();
		}

	protected void init() 
		{
		super.init();
		// getMarkupSettings().setCompressWhitespace(true);
		// getMarkupSettings().setStripComments(true);
		// getMarkupSettings().setStripWicketTags(true);
		getRequestCycleSettings().setResponseRequestEncoding("UTF-8");
		getDebugSettings().setAjaxDebugModeEnabled(false);
		getComponentInstantiationListeners().add(new SpringComponentInjector(this));
		getRequestCycleSettings().setGatherExtendedBrowserInfo(true);
		getResourceSettings().setResourcePollFrequency(Duration.ONE_HOUR);
		mountPage("howdy", MedWorksMasterPage.class);
		}
//PoiUtil

	public WaspSession newSession(Request request, Response response) 
		{
		return new MedWorksSession(this, request);
		}
	

	@Override
	protected void setUpHive() 
		{
		PolicyFileHiveFactory factory = new SwarmPolicyFileHiveFactory(getActionFactory());
		try {
			// this example uses 1 policy file but you can add as many as you
			// like
			factory.addPolicyFile(getServletContext().getResource("WEB-INF/medworks.hive"));
			factory.setAlias("myRoot", "edu.umich.brcf");
			} 
		catch (MalformedURLException e) { throw new WicketRuntimeException(e); }
		HiveMind.registerHive(getHiveKey(), factory);
		}
	
	
    //@Override
	public LoginContext getLogoffContext() 
		{
		return new MedWorksLoginContext();
		}
	//login
	
	// HomePage
	@Override
	public Class<? extends Page> getLoginPage()
		{
		return MedWorksLoginPage.class;
		}

	@Override
	protected Object getHiveKey()
		{
		return "tabs";
		}
    

	@Override
	public Class<? extends Page> getHomePage()
		{
		return MedWorksMasterPage.class;
		}
	}




////////////////////////CODE SCRAP /////////////////////////////////////////

	




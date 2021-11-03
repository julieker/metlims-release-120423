package edu.umich.brcf.shared.panels.login;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Session;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.protocol.http.ClientProperties;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.wicketstuff.security.WaspSession;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.panels.workflow.worklist_builder.ExperimentRandomization;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.User;
import edu.umich.brcf.shared.layers.domain.Viewpoint;
import edu.umich.brcf.shared.layers.service.UserService;



public final class MedWorksSession extends WaspSession {
	public static final int SESSION_TIME_OUT_MINUTES = 240;

	private volatile boolean uploading, uploadComplete;

	@SpringBean(name = "userService")
	private UserService userService;

	//@SpringBean
	//private SystemConfigService systemConfigService;

	private  boolean tester = true, developer = true;
	
	/** Current User  */
	private String currentUserId;
	private String currentUserName;
	private Viewpoint currentUserViewPoint;
	private String currentUserFirstName;
	private String currentUserLastName;
	private Boolean superUser;
	private Viewpoint level;
	private int minutesToTimeout = -1;
	private ClientProperties clientProperties;
	
	private String saveValue;
	private List<String> saveValues; 
	// issue 187 
	private Map <String, List<Experiment> > expProjmap = new HashMap<String, List <Experiment> >();
	
	
	// issue 432
	// issue 432
	// issue 56 get rid of getNCE session variables and get rid of pools after and before session variables 
	
	public Viewpoint getLevel() {
		return level;
	}

	public void setLevel(Viewpoint level) {
		this.level = level;
	}

	public Boolean isSuperUser() {
		return superUser;
	}

	public String getCurrentUserId() {
		return currentUserId;
	}

	public String getCurrentUserName() {
		return currentUserName;
	}
	
	public Viewpoint getCurrentUserViewPoint() {
		return currentUserViewPoint;
	}

	public String getCurrentUserFirstName() {
		return currentUserFirstName;
	}

	public String getCurrentUserLastName() {
		return currentUserLastName;
	}

	public MedWorksSession(final WebApplication application, Request request) 
		{
		super( (org.wicketstuff.security.WaspApplication) application, request);
		Injector.get().inject(this);
		}

	public static MedWorksSession get() {
		return (MedWorksSession) Session.get();
	}

	public void setUserInfo(User user) {
		this.currentUserId = user.getId();
		this.currentUserName = user.getUserName();
		this.currentUserViewPoint = user.getViewpoint();
		this.currentUserFirstName = user.getFirstName();
		this.currentUserLastName = user.getLastName();
		this.superUser = false; //(user instanceof SuperUser);
		this.level = user.getViewpoint();
	}

	public boolean isUploading() {
		return uploading;
	}

	public void resetTimeoutClock() {
		this.minutesToTimeout = SESSION_TIME_OUT_MINUTES;
	}

	/**
	 * Set when the upload thread starts, and reset when the upload ends or
	 * fails.
	 */
	public void setIsUploading(boolean uploading) {
		this.uploading = uploading;
	}

	public boolean isUploadComplete() {
		return uploadComplete;
	}

	/**
	 * Set when the upload thread succeeds, and reset when the upload page is
	 * reloaded.
	 */
	public void setUploadComplete(boolean uploadComplete) {
		this.uploadComplete = uploadComplete;
	}

	public int getMinutesToTimeout() {
		return minutesToTimeout;
	}

	public void setMinutesToTimeout(int minutesToTimeout) {
		this.minutesToTimeout = minutesToTimeout;
	}

	public String getServerName() throws UnknownHostException {
		// ipAddress = InetAddress.getLocalHost().getHostAddress();
		return InetAddress.getLocalHost().getHostName();
	}

//	public Boolean isProductionEnvironment() throws UnknownHostException {
//		return getServerName().equals(systemConfigService.getProductionServerName());
//	}

//	public void setSystemConfigService(SystemConfigService systemConfigService) {//
//		this.systemConfigService = systemConfigService;
//	}

	public ClientProperties getClientProperties() 
		{
		// New for wicket 6
		return WebSession.get().getClientInfo().getProperties(); //clientProperties;
		}

	public void setClientProperties(ClientProperties clientProperties) {
		this.clientProperties = clientProperties;
	}
	
	public int getBrowserHeight()
		{
		return clientProperties.getBrowserHeight();
		}
	
	public int getBrowserWidth()
		{
		return clientProperties.getBrowserWidth();
		}
	
	public boolean isTester()
		{
		return this.tester;
		}
	
	public boolean IsDeveloper()
		{
		return this.developer;
		}

	public String getSaveValue()
		{
		return saveValue;
		}

	public void setSaveValue(String saveValue)
		{
		this.saveValue = saveValue;
		}
	
	// issue 187
	public Map<String, List <Experiment>> getExpProjmap()
		{
		return expProjmap;
		}

	// issue 187
	public void setExpProjmap(Map<String, List <Experiment>> expProjmap)
		{
		this.expProjmap = expProjmap;
		}
	
	public List<String> getSaveValues()
		{
		return this.saveValues;
		}
	
	public void setSaveValues(List<String> values)
		{
		saveValues = new ArrayList<String>();
		for (String val : values)
			saveValues.add(val);
		}
	
	

	public boolean getTestMode()
		{
		// TODO Auto-generated method stub
		return false;
		}
	// issue 56 get rid of before and after pool session variables
        // issue 6 get rid of motrPAC session variables	

	


	}


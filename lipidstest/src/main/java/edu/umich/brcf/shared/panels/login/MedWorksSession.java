package edu.umich.brcf.shared.panels.login;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Session;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.protocol.http.ClientProperties;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.wicketstuff.security.WaspSession;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.panels.workflow.worklist_builder.ExperimentRandomization;
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
	
	// issue 391
	private int nMasterPoolsBefore;
	private int nMasterPoolsAfter;
	private int nBatchPoolsBefore;
	private int nBatchPoolsAfter;	
	
	// Issue 422
	private int nGastroExercise;
	private int nGastroSedentary;
	private int nLiverExercise;
	private int nLiverSedentary;
	private int nAdiposeExercise;
	private int nAdiposeSedentary;
	private int nPlasmaExercise;
	private int nPlasmaSedentary;
	private int nRatPlasma;
		
	// issue 22
    private int nLungExercise;
    private int nLungSedentary;
    private int nKidneyExercise;
    private int nKidneySedentary;
    private int nHeartExercise;
    private int nHeartSedentary;
    private int nBrownAdiposeExercise;
    private int nBrownAdiposeSedentary;
    private int nHippoCampusExercise;
    private int nHippoCampusSedentary;
	
	// Issue 427
	private int nRatG;
	private int nRatL;
	private int nRatA;
	private int nCE10Reps;
	private int nCE20Reps;
	private int nCE40Reps;
	
	// issue 432
	// issue 432
	public Integer getNCE10Reps() 
		{
		return nCE10Reps;
		}

	public void setNCE10Reps(Integer nnCE10Reps) 
		{
		nCE10Reps = nnCE10Reps;
		}
	
	// issue 432
	public Integer getNCE20Reps() 
		{
		return nCE20Reps;
		}

	public void setNCE20Reps(Integer nnCE20Reps) 
		{
		nCE20Reps = nnCE20Reps;
		}
	
	// issue 432
	public Integer getNCE40Reps() 
		{
		return nCE40Reps;
		}

	public void setNCE40Reps(Integer nnCE40Reps) 
		{
		nCE40Reps = nnCE40Reps;
		}
	
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
	
	
	// Issue 391 	
	public int getNMasterPoolsBefore() 
	    {
	    return nMasterPoolsBefore;
	    }

	public void setNMasterPoolsBefore(int nMasterPoolsBefore) 
		{
		this.nMasterPoolsBefore = nMasterPoolsBefore;
		}
	
	public int getNMasterPoolsAfter() 
		{
		return nMasterPoolsAfter;
		}
	
	public void setNMasterPoolsAfter(int nMasterPoolsAfter) 
		{
		this.nMasterPoolsAfter = nMasterPoolsAfter;
		}
	
	public int getNBatchPoolsBefore() 
		{
		return nBatchPoolsBefore;
		}
	
	public void setNBatchPoolsBefore(int nBatchPoolsBefore) 
		{
		this.nBatchPoolsBefore = nBatchPoolsBefore;
		}
	
	public int getNBatchPoolsAfter() 
		{
		return nBatchPoolsAfter;
		}
	
	public void setNBatchPoolsAfter(int nBatchPoolsAfter) 
		{
		this.nBatchPoolsAfter = nBatchPoolsAfter;
		}
	
	// issue 22
	public int getNLungExercise() 
		{
		return nLungExercise;
		}

	public void setNLungExercise(int nLungExercise) 
		{
		this.nLungExercise = nLungExercise;
		}
	
	// issue 22
	public int getNLungSedentary() 
		{
		return nLungSedentary;
		}

	public void setNLungSedentary(int nLungSedentary) 
		{
		this.nLungSedentary = nLungSedentary;
		}
	
	// issue 22
	public int getNKidneyExercise() 
		{
		return nKidneyExercise;
		}

	public void setNKidneyExercise(int nKidneyExercise) 
		{
		this.nKidneyExercise = nKidneyExercise;
		}
	
	// issue 22
	public int getNKidneySedentary() 
		{
		return nKidneySedentary;
		}

	public void setNKidneySedentary(int nKidneySedentary) 
		{
		this.nKidneySedentary = nKidneySedentary;
		}
		
	// issue 22
	public int getNHeartExercise() 
		{
		return nHeartExercise;
		}

	public void setNHeartExercise(int nHeartExercise) 
		{
		this.nHeartExercise = nHeartExercise;
		}
	
	// issue 22
	public int getNHeartSedentary() 
		{
		return nHeartSedentary;
		}

	public void setNHeartSedentary(int nHeartSedentary) 
		{
		this.nHeartSedentary = nHeartSedentary;
		}
	
	// issue 22
	public int getNBrownAdiposeExercise() 
		{
		return nBrownAdiposeExercise;
		}

	public void setNBrownAdiposeExercise(int nBrownAdiposeExercise) 
		{
		this.nBrownAdiposeExercise = nBrownAdiposeExercise;
		}
	
	// issue 22
	public int getNBrownAdiposeSedentary() 
		{
		return nBrownAdiposeSedentary;
		}
	
    public void setNBrownAdiposeSedentary(int nBrownAdiposeSedentary) 
		{
		this.nBrownAdiposeSedentary = nBrownAdiposeSedentary;
		}
       
	// issue 22
	public int getNHippoCampusExercise() 
		{
		return nHippoCampusExercise;
		}

	public void setNHippoCampusExercise(int nHippoCampusExercise) 
		{
		this.nHippoCampusExercise = nHippoCampusExercise;
		}
	
	// issue 22
	public int getNHippoCampusSedentary() 
		{
		return nHippoCampusSedentary;
		}

	public void setNHippoCampusSedentary(int nHippoCampusSedentary) 
		{
		this.nHippoCampusSedentary = nHippoCampusSedentary;
		}
	
	// issue 422
	public int getNGastroExercise() 
		{
		return nGastroExercise;
		}

	public void setNGastroExercise(int nGastroExercise) 
		{
		this.nGastroExercise = nGastroExercise;
		}
	
	public int getNGastroSedentary() 
		{
		return nGastroSedentary;
		}

	public void setNGastroSedentary(int nGastroSedentary) 
		{
		this.nGastroSedentary = nGastroSedentary;
		}
	
	public int getNLiverExercise() 
		{
		return nLiverExercise;
		}
	
	public void setNLiverExcercise(int nLiverExercise) 
		{
		this.nLiverExercise = nLiverExercise;
		}

	public int getNLiverSedentary() 
		{
		return nLiverSedentary;
		}
	
    public void setNLiverSedentary(int nLiverSedentary) 
		{
		this.nLiverSedentary = nLiverSedentary;
		}
    
	public int getNAdiposeExercise() 
		{
		return nAdiposeExercise;
		}
	
	public void setNAdiposeExercise(int nAdiposeExercise) 
	    {
		this.nAdiposeExercise = nAdiposeExercise;
		}
    
	public int getNAdiposeSedentary() 
		{
		return nAdiposeSedentary;
		}
	
	public void setNAdiposeSedentary(int nAdiposeSedentary) 
		{
		this.nAdiposeSedentary = nAdiposeSedentary;
		}
	
	public int getNPlasmaExercise() 
		{
		return nPlasmaExercise;
		}
	
	public void setNPlasmaExercise(int nPlasmaExercise) 
		{
		this.nPlasmaExercise = nPlasmaExercise;
		}

	public int getNPlasmaSedentary() 
		{
		return nPlasmaSedentary;
		}
	
	public void setNPlasmaSedentary(int nPlasmaSedentary )
		{
		this.nPlasmaSedentary = nPlasmaSedentary;
		}
	
	public int getNRatPlasma() 
		{
		return nRatPlasma;
		}

	public void setNRatPlasma(int nRatPlasma) 
		{
		this.nRatPlasma = nRatPlasma;
		}
	
	// issue 427
	public int getNRatG() 
		{
		return nRatG;
		}

	public void setNRatG(int nRatG) 
		{
		this.nRatG = nRatG;
		}
	
	// issue 427
	public int getNRatL() 
		{
		return nRatL;
		}

	public void setNRatL(int nRatL) 
		{
		this.nRatL = nRatL;
		}
	
	// issue 427
	public int getNRatA() 
		{
		return nRatA;
		}

	public void setNRatA(int nRatA) 
		{
		this.nRatA = nRatA;
		}
	}

/*
package edu.umich.brcf.shared.panels.login;


import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.wicket.Session;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.protocol.http.ClientProperties;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.security.WaspApplication;
import org.wicketstuff.security.WaspSession;

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
	
	/** Current User  
	private String currentUserId;
	private String currentUserName;
	private Viewpoint currentUserViewPoint;
	private String currentUserFirstName;
	private String currentUserLastName;
	private Boolean superUser;
	private Viewpoint level;
	private int minutesToTimeout = -1;
	private ClientProperties clientProperties;

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
		super((WaspApplication) application, request);
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
	 
	public void setIsUploading(boolean uploading) {
		this.uploading = uploading;
	}

	public boolean isUploadComplete() {
		return uploadComplete;
	}

	/**
	 * Set when the upload thread succeeds, and reset when the upload page is
	 * reloaded.
	 
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

	public ClientProperties getClientProperties() {
		return clientProperties;
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
	}



 */

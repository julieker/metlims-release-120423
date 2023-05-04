////////////////////////////////////////////////////
// MasterPanel.java
// Written by Jan Wigginton, Jul 24, 2016 (To upgrade to Wicket 1.5, 6.0
////////////////////////////////////////////////////
package edu.umich.brcf;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.security.checks.ContainerSecurityCheck;
import org.wicketstuff.security.components.SecureComponentHelper;
import org.wicketstuff.security.extensions.markup.html.tabs.ISecureTab;
import org.wicketstuff.security.extensions.markup.html.tabs.SecureTabbedPanel;

import edu.umich.brcf.metabolomics.panels.admin.AdminPanel;
import edu.umich.brcf.metabolomics.panels.admin.progresstracking.LaunchProgressTrackingToolsPanel;
import edu.umich.brcf.metabolomics.panels.lims.LimsPanel;
import edu.umich.brcf.metabolomics.panels.lipidshome.LimsPanel2;
import edu.umich.brcf.metabolomics.panels.lipidshome.LipidsMainTabPanel;
import edu.umich.brcf.metabolomics.panels.workflow.WorkflowMainPanel;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.panels.utilitypanels.IllegalBrowserPanel;

public class MasterPanel extends Panel
	{
	private static final long serialVersionUID = 1L;


	public MasterPanel(String id, final PageParameters parameters) 
		{
		super(id);
		add(createTabs("tabs", parameters));
		}

	
	private SecureTabbedPanel createTabs(String id, PageParameters parameters)
		{
		List <ITab>tabs = new ArrayList<ITab>();
		
		// Issue 464
		final SecureTabbedPanel stp = (SecureTabbedPanel) new SecureTabbedPanel(id, tabs).add(new AttributeModifier(
		"class",  MasterPanel.this.getDefaultModel()));
		
				
		boolean isFireFox = (((MedWorksSession) getSession()).getClientProperties().isBrowserMozillaFirefox());
		boolean isInternetExplorer = (((MedWorksSession) getSession()).getClientProperties().isBrowserInternetExplorer());
		boolean isGoodBrowser = !isFireFox && !isInternetExplorer;
				
		tabs.add(getWorkflowPanel(isGoodBrowser ? " WORKFLOW " : "BROWSER ERROR"));
		 	
		 if (isGoodBrowser)
			 {			
			 tabs.add(getLimsPanel());
			 //tabs.add(getLipidsMainTabPanel(stp));
			 tabs.add(getAdminPanel());
			 tabs.add(getLimsPanel2());
			 // issue 262
			 String ptrackString = "PROGRESS-TRACKING";
			 ptrackString = StringUtils.leftPad(ptrackString, 21);
			  tabs.add(new AbstractTab(new Model(ptrackString)) 
				 { 
					 public Panel getPanel(String panelId) {  return new LaunchProgressTrackingToolsPanel(panelId); }
					 }); 
				 } 
		
		stp.setSelectedTab(0);
		
		return stp; 
		}

	
	private ISecureTab getErrorPanel()
		{
		return new ISecureTab() 
			{
			private static final long serialVersionUID = 1L;
			
			@Override
			public Class<? extends Panel> getPanel() { return null;  }
			
			public IModel<String> getTitle() { 	return new Model<String>("ILLEGAL BROWSER"); }
		
			public boolean isVisible() { return true; }
	
			@Override
			public Panel getPanel(String panelId)
				{
				Panel panel = new IllegalBrowserPanel(panelId);
				
				SecureComponentHelper.setSecurityCheck(panel, new ContainerSecurityCheck(panel));
				return panel;
				}
			};
		}
	
	
	//// issue 262
	
	
	private ISecureTab getLipidsMainTabPanel(final SecureTabbedPanel parent)
		{
		return new ISecureTab() 
			{
			private static final long serialVersionUID = 1L;
	
			public Panel getPanel(String panelId) 
				{
				LipidsMainTabPanel panel = new LipidsMainTabPanel(panelId, parent);
				SecureComponentHelper.setSecurityCheck(panel, new ContainerSecurityCheck(panel));
				return panel;
				}
	
			public IModel getTitle() { return new Model("    DATA       "); }
	
			public Class getPanel() { return LipidsMainTabPanel.class; }
			
			public boolean isVisible() { return true; }
			};
		}
	
	
	private ISecureTab getWorkflowPanel(final String title)
		{
		return new ISecureTab()
			{
			private static final long serialVersionUID = 1L;

			public Panel getPanel(String panelId)
				{
				WorkflowMainPanel panel = new WorkflowMainPanel(panelId);
				SecureComponentHelper.setSecurityCheck(panel,
						new ContainerSecurityCheck(panel));
				return panel;
				}

			public IModel getTitle() { return new Model(title); }

			public Class getPanel() { return WorkflowMainPanel.class; }

			public boolean isVisible() { return true; }
			};
		}
		
	private ISecureTab getLimsPanel()
		{
		return new ISecureTab() 
			{
			private static final long serialVersionUID = 1L;
	
			public Panel getPanel(String panelId) 
				{
				LimsPanel panel = new LimsPanel(panelId);
				SecureComponentHelper.setSecurityCheck(panel, new ContainerSecurityCheck(panel));
				return panel;
				}
	
			public IModel getTitle() { return new Model("     LIMS     "); }
	
			public Class getPanel() { return LimsPanel.class; }
			
			public boolean isVisible() { return true; }
			};
		}
	
	
	private ISecureTab getAdminPanel()
		{
		return new ISecureTab() 
			{
			private static final long serialVersionUID = 1L;
	
			public Panel getPanel(String panelId) 
				{
				AdminPanel panel = new AdminPanel(panelId);
				SecureComponentHelper.setSecurityCheck(panel, new ContainerSecurityCheck(panel));
				return panel;
				}
	
			public IModel getTitle() { return new Model("    ADMIN    "); }
			
			public Class getPanel() { return AdminPanel.class; }
					
			public boolean isVisible() { return true; }
			};
		}
	  	
	
	private ISecureTab getLimsPanel2()
		{
		return new ISecureTab() 
			{
			private static final long serialVersionUID = 1L;
	
			public Panel getPanel(String panelId) 
				{
				LimsPanel2 panel = new LimsPanel2(panelId);
				SecureComponentHelper.setSecurityCheck(panel, new ContainerSecurityCheck(panel));
				return panel;
				}
	
			public IModel getTitle() { return new Model("     DATA       "); }
	
			public Class getPanel() { return LimsPanel2.class; }
			
			public boolean isVisible() { return true; }
			};
		}
	}

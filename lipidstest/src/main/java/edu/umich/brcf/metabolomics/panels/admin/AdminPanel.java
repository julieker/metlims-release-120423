package edu.umich.brcf.metabolomics.panels.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.wicketstuff.security.checks.ContainerSecurityCheck;
import org.wicketstuff.security.components.SecureComponentHelper;
import org.wicketstuff.security.extensions.markup.html.tabs.ISecureTab;

import edu.umich.brcf.metabolomics.panels.admin.accounts.UsersPanel;
import edu.umich.brcf.metabolomics.panels.admin.database_utility.LaunchDatabaseToolsPanel;
import edu.umich.brcf.metabolomics.panels.admin.instruments.InstrumentsPanel;
import edu.umich.brcf.metabolomics.panels.admin.organization.SortableOrganizationPanel;
import edu.umich.brcf.metabolomics.panels.admin.sample_submission.LaunchMixtureToolsPanel;
import edu.umich.brcf.metabolomics.panels.admin.sample_submission.LaunchSampleToolsPanel;
import edu.umich.brcf.metabolomics.panels.admin.system_info.SystemInfoPanel;
import edu.umich.brcf.metabolomics.panels.admin.messaging.METWorksMessagePanel;


	
@AuthorizeAction(action = Action.RENDER, roles = "Admin")
@AuthorizeInstantiation("Admin")
public class AdminPanel extends Panel
	{
	public AdminPanel(String id)
		{
		super(id);
		setDefaultModel(new Model("tabpanel1")); // issue 464
		add(new TabbedPanel("adminTabs", makeTabs()).add(new AttributeModifier("class",  AdminPanel.this.getDefaultModel())));
		}

	private List<ITab> makeTabs()
		{
		List<ITab> tabs = new ArrayList<ITab>();

		tabs.add(new AbstractTab(new Model("SAMPLE TOOLS")) 
			{ 
			public Panel getPanel(String panelId) { return new LaunchSampleToolsPanel(panelId); } 
			});
		
		// issue 94
		tabs.add(new AbstractTab(new Model("MIXTURE TOOLS"))
	        {
	        public Panel getPanel(String panelId) { return new LaunchMixtureToolsPanel(panelId); }
	        });
		tabs.add(new AbstractTab(new Model("DATABASE TOOLS")) 
			{ 
			public Panel getPanel(String panelId) { return new LaunchDatabaseToolsPanel(panelId); } 
			});
		
		 tabs.add(new AbstractTab(new Model("USERS")) 
			 { 
			 public Panel getPanel(String panelId) { return new UsersPanel(panelId); } 
			 });
		  
		 tabs.add(new AbstractTab(new Model("ORGANIZATIONS")) 
			 { 
			 public Panel getPanel(String panelId) { return new SortableOrganizationPanel(panelId); }
			 });
		  
		 tabs.add(new AbstractTab(new Model("INSTRUMENTS")) 
			 { 
			 public Panel getPanel(String panelId) { return new InstrumentsPanel(panelId); }
			 });
		
		 tabs.add(new AbstractTab(new Model("SYSTEM INFO")) 
			 { 
			 public Panel getPanel(String panelId) { return new SystemInfoPanel(panelId); } 
			 });
		
		 tabs.add(new ISecureTab() 
			{ 
			private static final long serialVersionUID = 1L;
			 
			 public Panel getPanel(String panelId) 
				 { 
				 METWorksMessagePanel panel = new METWorksMessagePanel(panelId);
				 SecureComponentHelper.setSecurityCheck(panel, new ContainerSecurityCheck(panel)); return panel; 
				 }
		 
			 public IModel getTitle() { return new Model("MESSAGING"); }
		  
			 public Class getPanel() { return METWorksMessagePanel.class; }
		  
			 public boolean isVisible() { return true; }
			 });
		 
		return tabs;
		}
	}

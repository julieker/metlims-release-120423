////////////////////////////////////////////////////
// WorkflowMainPanel.java
// Written by Jan Wigginton, August 2015
////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import edu.umich.brcf.metabolomics.panels.workflow.workflow_tracking.WorkflowTrackingMainPanel;
import edu.umich.brcf.metabolomics.panels.workflow.worklist_builder.WorklistBuilderPanel;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.panels.utilitypanels.IllegalBrowserPanel;



public class WorkflowMainPanel extends Panel
	{
	public WorkflowMainPanel(String id)
		{
		super(id);
		List<AbstractTab> tabs = new ArrayList<AbstractTab>();
	
		boolean isFireFox = (((MedWorksSession) getSession()).getClientProperties().isBrowserMozillaFirefox());
		boolean isInternetExplorer = (((MedWorksSession) getSession()).getClientProperties().isBrowserInternetExplorer());

		boolean isGoodBrowser = !isFireFox && !isInternetExplorer;
		
		if (isGoodBrowser)
			tabs.add(new AbstractTab(new Model("TRACKING"))
				{
				public Panel getPanel(String panelId)
					{
					return new WorkflowTrackingMainPanel(panelId);
					}
				});

		if (isGoodBrowser)
			tabs.add(new AbstractTab(new Model("WORKLIST BUILDER"))
				{
				public Panel getPanel(String panelId)
					{
					return new WorklistBuilderPanel(panelId);
					}
				});

		
		if (!isGoodBrowser)
			tabs.add(new AbstractTab(new Model(""))
				{
				public Panel getPanel(String panelId)
					{
					return new IllegalBrowserPanel(panelId);
					}
				});

		add(new TabbedPanel("workflowTabs", tabs));
		}
	}

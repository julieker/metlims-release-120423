////////////////////////////////////////////////////
// WorklistSettingsPanel.java
// Written by Jan Wigginton, Mar 19, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;


public class WorklistSettingsPanel extends Panel
	{
	private List<WebMarkupContainer> sibContainers;
	private AddSamplesPanel addSamplesPanel;
	private AddControlsPanel addControlsPanel;
	private AutoAddControlsPanel autoAddControlsPanel;
	
	private TabbedPanel tabbedPanel;
	WorklistSimple originalWorklist;
	
	public WorklistSettingsPanel(String id, WorklistSimple worklist) 
		{
		super(id);
		
		
		sibContainers = new ArrayList<WebMarkupContainer>();
		
		final List tabs = buildTabs(worklist);
		add(tabbedPanel = new TabbedPanel("tabs", tabs));
	
		tabbedPanel.setSelectedTab(2);
		tabbedPanel.setSelectedTab(1);
	    tabbedPanel.setSelectedTab(0);
	    tabbedPanel.setOutputMarkupId(true);
	    
	    for (WebMarkupContainer container : sibContainers)
	    	addSamplesPanel.addSibContainer(container);
	
	    add(tabbedPanel);
		}
	

	private List<AbstractTab> buildTabs(final WorklistSimple worklist)
		{
	    List<AbstractTab> tabs = new ArrayList<AbstractTab>();
	    
	    tabs.add(new AbstractTab(new Model<String>("ADD SAMPLES")) 
			{
	    	public Panel getPanel(String panelId)
	    		{
	    		return (addSamplesPanel = new AddSamplesPanel(panelId, worklist));
	    		}
			});
	    
	    tabs.add(new AbstractTab(new Model<String>("ADD CONTROLS")) 
			{
	    	public Panel getPanel(String panelId)
	    		{
	    		return (addControlsPanel = new AddControlsPanel(panelId, worklist));
	    		}
			});
	    
	    
	    tabs.add(new AbstractTab(new Model<String>("ADD CONTROL GROUPS")) 
			{
	    	public Panel getPanel(String panelId)
	    		{
	    		return (autoAddControlsPanel = buildAutoControlsPanel(panelId, worklist));
	    		}
			});
	    
	    return tabs;
		}
	
	
	private AddSamplesPanel buildAddSamplesPanel(String id, WorklistSimple worklist)
		{
		return new AddSamplesPanel(id, worklist)
			{
			@Override
			public boolean isVisible() { return true; }

			@Override
			public boolean isEnabled() { return true; }
			};
		}

	
	private AddControlsPanel buildAddControlsPanel(String id, WorklistSimple worklist)
		{
		return new AddControlsPanel(id, worklist)
			{
			@Override
			public boolean isVisible() { return true; }

			@Override
			public boolean isEnabled() { return true; }
			};
		}
	
	
	private AutoAddControlsPanel buildAutoControlsPanel(String id, WorklistSimple worklist)
		{
		return new AutoAddControlsPanel(id, worklist)
			{
			@Override
			public boolean isVisible() { return true; }

			@Override
			public boolean isEnabled() { return true; }
			};
		}
	
	
	public void addSibContainer(WebMarkupContainer c)
		{
		sibContainers.add(c);
		}
	}
	

package edu.umich.brcf.metabolomics.panels.lims;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import edu.umich.brcf.metabolomics.panels.lims.compounds.CompoundsPanel;
import edu.umich.brcf.metabolomics.panels.lims.locations.LocationsPanel;
import edu.umich.brcf.metabolomics.panels.lims.mixtures.MixtureDetailPanel;
import edu.umich.brcf.metabolomics.panels.lims.newestprep.LaunchPrepTools;
import edu.umich.brcf.metabolomics.panels.lims.newexperiment.SmallExperimentSearchPanel;
import edu.umich.brcf.metabolomics.panels.lims.project.SortableProjectPanel;
import edu.umich.brcf.metabolomics.panels.lims.assays.AssayAliquotDetailPanel;
import edu.umich.brcf.metabolomics.panels.lims.client.SortableClientPanel;



public class LimsPanel extends Panel 
	{
	private static final long serialVersionUID = 1L;

	public LimsPanel(String id) 
		{
		super(id);
		add(new TabbedPanel<AbstractTab>("limsTabs", makeTabs()));
		}

	private List<AbstractTab> makeTabs() 
		{
		List<AbstractTab> tabs = new ArrayList<AbstractTab>();

		tabs.add(new AbstractTab(new Model("EXPERIMENTS")) 
			{
			public Panel getPanel(String panelId)  { return new SmallExperimentSearchPanel(panelId); }
			});
		
		
		tabs.add(new AbstractTab(new Model("CLIENTS")) 
			{
			public Panel getPanel(String panelId) { return new SortableClientPanel(panelId); }
			});
		
		
		tabs.add(new AbstractTab(new Model("PROJECTS")) 
			{
			public Panel getPanel(String panelId) { return new SortableProjectPanel(panelId); }
			});
		
		
		tabs.add(new AbstractTab(new Model("PREP")) 
			{
			public Panel getPanel(String panelId) {return new LaunchPrepTools(panelId); }
			});
		
		tabs.add(new AbstractTab(new Model("LOCATIONS")) 
			{
			public Panel getPanel(String panelId) { return new LocationsPanel(panelId); }
			});
		
		tabs.add(new AbstractTab(new Model("COMPOUNDS")) 
			{
			public Panel getPanel(String panelId) { return new CompoundsPanel(panelId); }
			});
		
		//issue 94
        tabs.add(new AbstractTab(new Model("MIXTURES"))
	        {
	        public Panel getPanel(String panelId) { return new MixtureDetailPanel(panelId); }
	        });
		
		// issue 100
		tabs.add(new AbstractTab(new Model("ASSAYS & ALIQUOTS")) 
			{
			public Panel getPanel(String panelId) { return new AssayAliquotDetailPanel(panelId); }
			});
	
		return tabs;
		}
	}

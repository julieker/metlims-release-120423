////////////////////////////////////////////////////
// LimsPanel2.java
// Written by Jan Wigginton, June 2016
////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.lipidshome;

import java.util.ArrayList;
import java.util.List;


import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import edu.umich.brcf.metabolomics.panels.lipidshome.browse.LipidsMainPanel;
import edu.umich.brcf.metabolomics.panels.lipidshome.clientdata.ClientAnalysisDataPanel;


public class LimsPanel2 extends Panel 
	{
	private static final long serialVersionUID = 1L;

	public LimsPanel2(String id) 
		{
		super(id);
		add(new TabbedPanel("limsTabs", makeTabs()));
		}
	
	private List<AbstractTab> makeTabs() 
		{
		List<AbstractTab> tabs = new ArrayList<AbstractTab>();

		tabs.add(new AbstractTab(new Model("LIPIDS DATA")) 
			{
			public Panel getPanel(String panelId) 
				{
				return new LipidsMainPanel(panelId);
				}
			});
		
		tabs.add(new AbstractTab(new Model("TEST/RESEARCH DATA")) 
			{
			public Panel getPanel(String panelId) 
				{
				return new ClientAnalysisDataPanel(panelId, true);
				}
			});

		return tabs;
		}
	}


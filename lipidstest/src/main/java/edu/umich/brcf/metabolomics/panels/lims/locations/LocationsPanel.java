package edu.umich.brcf.metabolomics.panels.lims.locations;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;


public class LocationsPanel extends Panel 
	{
	public LocationsPanel(String id) 
		{
		super(id);
		add(new LocationSearchPanel("locationSearchPanel"));
		}

	
	public LocationsPanel(String id, IModel model) 
		{
		super(id, model);
		}
	}

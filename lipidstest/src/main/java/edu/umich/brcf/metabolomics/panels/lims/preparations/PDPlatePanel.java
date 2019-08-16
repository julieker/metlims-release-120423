package edu.umich.brcf.metabolomics.panels.lims.preparations;

import org.apache.wicket.markup.html.panel.Panel;

import edu.umich.brcf.metabolomics.panels.lims.prep.SelectablePlateMapPanel;


public class PDPlatePanel extends Panel
	{
	public PDPlatePanel(String id, String prep)
		{
		super(id);
		add(new SelectablePlateMapPanel("plateMap", prep,  true));
		add(new SelectablePlateMapPanel("plateMap2", prep,  true));
		}

}

/*
new SelectablePlateMapPanel(panelId, pMap.getPreparation(), true));
*/
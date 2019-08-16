package edu.umich.brcf.metabolomics.panels.lims.preparations;

import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;


public class PDNotesPanel extends Panel
	{
	public PDNotesPanel(String id)
		{
		super(id);
		add(new TextArea("notes"));
		}

	}

/*
new SelectablePlateMapPanel(panelId, pMap.getPreparation(), true));
*/
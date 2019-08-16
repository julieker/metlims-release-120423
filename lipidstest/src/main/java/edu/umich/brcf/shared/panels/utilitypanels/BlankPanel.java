package edu.umich.brcf.shared.panels.utilitypanels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class BlankPanel extends Panel 
	{
	public BlankPanel(String id) 
		{
		super(id);
		add(new Label("blank", "  "));
		setOutputMarkupId(true);
		setOutputMarkupPlaceholderTag(true);
		}
	}

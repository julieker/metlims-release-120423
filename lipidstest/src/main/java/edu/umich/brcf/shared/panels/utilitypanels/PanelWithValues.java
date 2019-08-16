package edu.umich.brcf.shared.panels.utilitypanels;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;


public class PanelWithValues extends Panel
	{
	private String values = "";

	public PanelWithValues(String id)
		{
		super(id);
		// TODO Auto-generated constructor stub
		}

	public PanelWithValues(String id, IModel<?> model)
		{
		super(id, model);
		// TODO Auto-generated constructor stub
		}

	public String getValues()
		{
		return values;
		}

	public void setValues(String s)
		{
		values = s;
		}
	}
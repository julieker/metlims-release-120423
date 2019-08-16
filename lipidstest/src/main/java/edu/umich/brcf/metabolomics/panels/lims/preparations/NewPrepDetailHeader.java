package edu.umich.brcf.metabolomics.panels.lims.preparations;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

public class NewPrepDetailHeader extends Panel{
	
	public NewPrepDetailHeader(String id, CompoundPropertyModel prepModel) {
		super(id, prepModel);
		
		add(new Label("prepID"));
		add(new Label("title"));
		add(new Label("prepDateStr"));
		add(new Label("creatorName"));
		
	}

}

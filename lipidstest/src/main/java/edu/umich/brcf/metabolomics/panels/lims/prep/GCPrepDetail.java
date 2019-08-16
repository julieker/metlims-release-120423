package edu.umich.brcf.metabolomics.panels.lims.prep;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import edu.umich.brcf.metabolomics.layers.domain.GCDerivatizationMethod;



public class GCPrepDetail extends WebPage{
	
	public GCPrepDetail(IModel prepModel){
		GCDerivatizationMethod sop = (GCDerivatizationMethod) prepModel.getObject();
		add(new Label("reagentComposition", sop.getReagentComposition()));
		add(new Label("incubationConditions", sop.getIncubationConditions()));
		add(new Label("derivatizationVolume", sop.getDerivatizationVolume()+" µL"));
	}
}

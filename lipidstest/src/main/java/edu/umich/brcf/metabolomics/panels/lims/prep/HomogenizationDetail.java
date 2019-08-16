package edu.umich.brcf.metabolomics.panels.lims.prep;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import edu.umich.brcf.metabolomics.layers.domain.HomogenizationSOP;



public class HomogenizationDetail extends WebPage{
	
	public HomogenizationDetail(IModel prepModel){
		HomogenizationSOP sop = (HomogenizationSOP) prepModel.getObject();
		add(new Label("beadType", sop.getBeadType()));
		add(new Label("beadSize", sop.getBeadSize()));
		add(new Label("beadVolume", sop.getBeadVolume()));
		add(new Label("vortex", sop.getVortex()));
		add(new Label("time", sop.getTime()));
		add(new Label("temp", sop.getTemp()));
	}

}

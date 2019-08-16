package edu.umich.brcf.metabolomics.panels.lims.prep;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import edu.umich.brcf.metabolomics.layers.domain.ProtienDeterminationSOP;



public class ProteinDeterminationDetail extends WebPage{
	
	public ProteinDeterminationDetail(IModel prepModel){
		ProtienDeterminationSOP sop = (ProtienDeterminationSOP) prepModel.getObject();
//		add(new Label("sampleVolume", sop.getSampleVolume()));
		add(new Label("bradfordAgent", sop.getBradfordAgent()));
		add(new Label("wavelength", sop.getWavelength()));
		add(new Label("incubationTime", sop.getIncubationTime()));
	}
}

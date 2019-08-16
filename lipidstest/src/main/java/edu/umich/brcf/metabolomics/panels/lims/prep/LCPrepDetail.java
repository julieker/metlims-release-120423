package edu.umich.brcf.metabolomics.panels.lims.prep;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import edu.umich.brcf.metabolomics.layers.domain.LCReconstitutionMethod;



public class LCPrepDetail extends WebPage
	{
	
	public LCPrepDetail(IModel prepModel)
		{
		LCReconstitutionMethod sop = (LCReconstitutionMethod) prepModel.getObject();
		add(new Label("reconSolvent", sop.getReconSolvent()));
		add(new Label("reconVolume", sop.getReconVolume()+" µL"));
		}
	}

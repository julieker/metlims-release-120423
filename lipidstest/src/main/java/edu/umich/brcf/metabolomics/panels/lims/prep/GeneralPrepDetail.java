package edu.umich.brcf.metabolomics.panels.lims.prep;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import edu.umich.brcf.metabolomics.layers.domain.GeneralPrepSOP;



public class GeneralPrepDetail extends WebPage{
	
	public GeneralPrepDetail(IModel prepModel)
		{
		GeneralPrepSOP sop = (GeneralPrepSOP) prepModel.getObject();
		add(new Label("sampleVolume", sop.getSampleVolume()+" µL"));
		add(new Label("crashSolvent", sop.getCrashSolvent()));
		add(new Label("recoveryStandardContent", sop.getRecoveryStandardContent()));
		add(new Label("crashVolume", sop.getCrashVolume()+" µL"));
		add(new Label("vortex", sop.getVortex()));
		add(new Label("spin", sop.getSpin()));
		add(new Label("nitrogenBlowdownTime", sop.getNitrogenBlowdownTime()));
		add(new Label("lyophilizerTime",sop.getLyophilizerTime()));
		add(new Label("gcVolume", sop.getGCVolume()+" µL"));
		add(new Label("lcVolume", sop.getLCVolume()+" µL"));
		}
	}

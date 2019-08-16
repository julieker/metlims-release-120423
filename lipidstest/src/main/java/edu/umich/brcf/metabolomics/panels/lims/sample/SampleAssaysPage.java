package edu.umich.brcf.metabolomics.panels.lims.sample;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;

import edu.umich.brcf.shared.layers.domain.Experiment;


public class SampleAssaysPage extends WebPage{
	
	Experiment experiment;
	
	public SampleAssaysPage(Page backPage, Experiment experiment) {
		setExperiment(experiment);
		add(new SampleAssaysPanel("saPanel", getExperiment()));
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}

}

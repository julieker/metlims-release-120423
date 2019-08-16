package edu.umich.brcf.metabolomics.panels.lims.newexperiment.obsolete;


import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import edu.umich.brcf.metabolomics.panels.lims.newexperiment.ExperimentDetail;
import edu.umich.brcf.shared.layers.domain.Experiment;


public class ExperimentDetailWithSamples extends Panel
	{
	public ExperimentDetail edPanel;
	public ExperimentSamplesPanel esPanel;
	Experiment experiment;

	
	public ExperimentDetailWithSamples(String id, Experiment experiment) 
		{
		super(id);
		
		setOutputMarkupId(true);
		setExperiment(experiment);
		String expId = getExperiment() == null ? "EX00417" : getExperiment().getExpID();

		setDefaultModel(new CompoundPropertyModel(getExperiment()));
		add(edPanel=new ExperimentDetail("edPanel", getExperiment()));
	//	add(esPanel=new ExperimentSamplesPanel("esPanel", getExperiment()));
	//	esPanel.setVisible(false);
		
		updatePanels(experiment);
		}

	
	public Experiment getExperiment() 
		{
		return experiment;
		}

	
	public void setExperiment(Experiment experiment) 
		{
		this.experiment = experiment;
		}

	
	public void updatePanels(Experiment experiment)
		{
		if(experiment!=null && !experiment.equals(null))
			{
			edPanel.updateData(experiment);
//			esPanel.setExperiment(experiment);
			}
		}
	
	
	public ExperimentSamplesPanel getEsPanel()
		{
		return esPanel;
		}

	
	public ExperimentDetail getEdPanel() 
		{
		return edPanel;
		}
	
	
	public void setEdPanel(ExperimentDetail edPanel) 
		{
		this.edPanel = edPanel;
		}
	}


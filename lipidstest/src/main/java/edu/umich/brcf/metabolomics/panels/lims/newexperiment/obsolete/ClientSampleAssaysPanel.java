////////////////////////////////////////////////////
// ClientSampleAssaysPanel.java
// Written by Jan Wigginton, July 2015
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.lims.newexperiment.obsolete;


import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.SampleAssay;
import edu.umich.brcf.shared.layers.dto.ClientSampleAssaysBean;
import edu.umich.brcf.shared.layers.service.ExperimentService;


public class ClientSampleAssaysPanel extends Panel
	{
	Experiment experiment;
	List<ClientSampleAssaysBean> sampleAssayList;
	
	@SpringBean 
	private ExperimentService experimentService;
	
	public ClientSampleAssaysPanel(String id, Experiment experiment) 
		{
		super(id);
		setOutputMarkupId(true);
		setExperiment(experiment);
			
		add(new Label("head", new Model(getExperimentName())));
		
		add(new ListView("sampleAssayList", new PropertyModel <ClientSampleAssaysBean>(this, "sampleAssayList")) 
			{
			public void populateItem(final ListItem listItem) 
				{
				ClientSampleAssaysBean sab = (ClientSampleAssaysBean) listItem.getModelObject();
				listItem.add(new Label("sampleID", sab.getSampleId()));
				
				listItem.add(new ListView("assays", sab.getSampleAssays()) 
					{
					public void populateItem(final ListItem listItem2) 
						{
						SampleAssay sampleAssay=(SampleAssay) listItem2.getModelObject();
					
						listItem2.add(new Label("assay", new PropertyModel<String>(sampleAssay, "assay.assayName") ));
						listItem2.add(new Label("status", new PropertyModel<String>(sampleAssay, "status.statusValue") ));
						}
					});				
				}	
			});
		}

	
	private String getExperimentName() 
		{
		if (getExperiment()!=null)
			return (getExperiment().getExpName()+" ("+experiment.getExpID()+")");
		
		return "";
		}
	
	
	public Experiment getExperiment() 
		{
		return experiment;
		}

	
	public void setExperiment(Experiment experiment) 
		{
		this.experiment = experiment;
		if (experiment!=null && !experiment.equals(null))
			setSampleAssayList(experimentService.getExperimentAssaysForClient(experiment));
		}

	
	public List<ClientSampleAssaysBean> getSampleAssayList() 
		{
		return sampleAssayList;
		}

	
	public void setSampleAssayList(List<ClientSampleAssaysBean> sampleAssayList) 
		{
		this.sampleAssayList = sampleAssayList;
		}
	}

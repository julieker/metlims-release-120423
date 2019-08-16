////////////////////////////////////////////////////
// ClientSampleAssaysPanelNew.java
// Written by Jan Wigginton, Mar 22, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.lims.newexperiment;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.datacollectors.SimpleClientSampleAssaysBean;
import edu.umich.brcf.shared.util.structures.Pair;


public class ClientSampleAssaysPanelNew extends Panel
	{
	@SpringBean 
	ExperimentService experimentService;
	
	@SpringBean
	SampleService sampleService;
	
	private Experiment experiment;
	private List<SimpleClientSampleAssaysBean> sampleAssayList;
	
	public ClientSampleAssaysPanelNew(String id,Experiment experiment) 
		{
		super(id);
		setOutputMarkupId(true);
		setExperiment(experiment);
		
		add(new Label("head", new PropertyModel<String>(this, "experimentLabel")));
		
		add(new ListView("sampleAssayList", new PropertyModel <SimpleClientSampleAssaysBean>(this, "sampleAssayList")) 
			{
			public void populateItem(final ListItem listItem) 
				{
				SimpleClientSampleAssaysBean sab = (SimpleClientSampleAssaysBean) listItem.getModelObject();
				listItem.add(new Label("sampleID", sab.getSampleId()));
				
				listItem.add(new ListView("assays", sab.getAssayNamesAndStatuses()) 
					{
					public void populateItem(final ListItem listItem2) 
						{
						Pair sampleAssay=(Pair) listItem2.getModelObject();
					 
						listItem2.add(new Label("assay", new PropertyModel<String>(sampleAssay, "id") ));
						listItem2.add(new Label("status", new PropertyModel<String>(sampleAssay, "value") ));
						}
					});				
				}	
			});
		}

	
	public String getExperimentLabel() 
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
			setSampleAssayList(sampleService.loadExperimentSampleAssayStatusEfficiently(experiment.getExpID()));
		}

	
	public List<SimpleClientSampleAssaysBean> getSampleAssayList() 
		{
		return sampleAssayList;
		}

	
	public void setSampleAssayList(List<SimpleClientSampleAssaysBean> sampleAssayList) 
		{
		this.sampleAssayList = sampleAssayList;
		}
	}

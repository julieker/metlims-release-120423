////////////////////////////////////////////////////
// PrepSearchResultsPanel.java
// Written by Jan Wigginton, Sep 8, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.lims.newestprep;


import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.panels.lims.sample.ExperimentalDesignPage;
import edu.umich.brcf.metabolomics.panels.lims.sample.SampleAssaysPage;
import edu.umich.brcf.metabolomics.panels.lims.sample.SampleDetailPage;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.SamplePrepService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;



public class PrepSearchResultsPanel extends Panel
	{
	@SpringBean
	SampleService sampleService;
	
	@SpringBean
	ExperimentService experimentService;
	
	@SpringBean
	SamplePrepService samplePrepService;
	
	String highlightedSample = null;
	
	
	public PrepSearchResultsPanel(String id,  List<String> eids, final ModalWindow modal) 
		{
		super(id);
		setOutputMarkupId(true);
		add(new FeedbackPanel("feedback"));
		add(new PrepSearchResultsForm("sampleSearchForm", eids, modal));
		}

	
	public final class PrepSearchResultsForm extends Form 
		{
		List<Experiment> experiments = new ArrayList<Experiment>();
		
		public PrepSearchResultsForm(final String id, List<String> eids, ModalWindow modal)
			{
			super(id);
			setOutputMarkupId(true);
			
			initializeExperiments(eids);
		
			final ModalWindow modal1= buildModal("modal1");
	        add(modal1);
			add(buildExperimentSamplesListView("experiments", modal1));
			add(new AjaxCancelLink("closeButton", modal));
			}
		
			
		private ModalWindow buildModal(String id)
			{
			final ModalWindow modal1 = ModalCreator.createModalWindow("modal1", 600, 200);
		    modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
	        	{
	            public void onClose(AjaxRequestTarget target)
	            	{
	            	((ListView)(modal1.getParent().get("experiments"))).setList(getExperiments());
	            	target.add(modal1.getParent());
	            	target.add(modal1.getParent().getParent());
	            	}
	        	});
		    return modal1;
		    }
		
		
		private void initializeExperiments(List<String> eids)
			{
			experiments = new ArrayList<Experiment>();
			
			for (String id : eids)
				experiments.add(experimentService.loadExperimentWithInfoForDrcc(id));
				
			setExperiments(experiments);	
			}
		
		private ListView buildExperimentSamplesListView(String id, final ModalWindow modal1)
			{
			return new ListView(id, new PropertyModel<List<Experiment>>(this, "experiments")) 
				{
				public void populateItem(final ListItem listItem) 
					{
					final Experiment experiment = (Experiment) listItem.getModelObject();
					listItem.add(new Label("expName", experiment.getExpName()+" ("+experiment.getSampleList().size()+" samples)"));
					
					listItem.add(buildLinkToModal("setup",modal1, experiment.getExpID()));
					listItem.add(buildLinkToModal("assays",modal1, experiment.getExpID()));
					
					listItem.add(new ListView("sampleList", new PropertyModel<List<Sample>>(experiment, "sampleList")) 
						{
						public void populateItem(final ListItem sListItem) 
							{
							final Sample sample = (Sample) sListItem.getModelObject();

							sListItem.add(buildLinkToModal("detail", modal1, sample.getSampleID()).add(new Label("sampleID", sample.getSampleID())));
							
							sListItem.add(new Label("sampleName", sample.getSampleName()));
							sListItem.add(new Label("userSubj", new PropertyModel<String>(sample, "userSubject")));
							sListItem.add(new Label("amount", sample.getCurVolumeAndUnits()));
							sListItem.add(new Label("locID", new Model(sample.getLocID())));

							sListItem.add(OddEvenAttributeModifier.create(sListItem));
							
							if( highlightedSample!=null &&  sample.getSampleID().equals(highlightedSample))
								sListItem.add(new AttributeModifier("class", "selected"));
							}
						});
					
					}
				};
			}
		
		
		private AjaxLink buildLinkToModal(final String linkID, final ModalWindow modal1, final String parameter) 
			{
			return new AjaxLink(linkID)
	        	{
	            @Override
	            public void onClick(final AjaxRequestTarget target)
	            	{
	            	modal1.setInitialHeight(grabHeight(linkID));
            		modal1.setInitialWidth(grabWidth(linkID));
	            	
            		modal1.setPageCreator(new ModalWindow.PageCreator()
		                 {
	            	     public Page createPage()
		                     {
		                	 return grabPage(linkID, parameter, modal1);
		                     }
	                     });
		            	modal1.show(target);
					}
	            @Override // issue 464
				public MarkupContainer setDefaultModel(IModel model) 
				    {
					// TODO Auto-generated method stub
					return this;
				    }
	        	};
			}
		
		
		private int grabHeight(String linkID)
			{
			switch (linkID)
				{
				case "assays" :
				case "setup" : return 700;
				case "detail" : return 500;
				default : return 200;
				}
			}
		
		
		private int grabWidth(String linkID)
			{
			switch (linkID)
				{
				case "detail" : return 500;
				case "assays"  : 
				case "setup" : 
				default : return 700;
				}
			}
		
		
		public Page grabPage(String linkID, String parameter, final ModalWindow modal1)
			{
			if(linkID.startsWith("de"))
				return new SampleDetailPage(parameter); 
       	 
		    if (linkID.startsWith("se"))
       	    	return (new ExperimentalDesignPage(getPage(), experimentService.loadById(parameter)));
       	 	
		    return (new SampleAssaysPage(getPage(), experimentService.loadById(parameter)));
       	  	}		
		
		
		public List<Experiment> getExperiments() 
			{
			return experiments;
			}

		public void setExperiments(List<Experiment> experiments) 
			{
			this.experiments = experiments;
			}
		}
	}

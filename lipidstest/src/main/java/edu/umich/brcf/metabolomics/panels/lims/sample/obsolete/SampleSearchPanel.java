package edu.umich.brcf.metabolomics.panels.lims.sample.obsolete;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.SamplePrepService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;


public class SampleSearchPanel extends Panel 
	{
	@SpringBean
	SampleService sampleService;
	
	@SpringBean
	ExperimentService experimentService;
	
	@SpringBean
	SamplePrepService samplePrepService;
	
	String highlightedSample = null;
	
	
	public SampleSearchPanel(String id) 
		{
		super(id);
		setOutputMarkupId(true);
		add(new FeedbackPanel("feedback"));
		add(new SampleSearchForm("sampleSearchForm"));
		}

	public final class SampleSearchForm extends Form 
		{
		int searchModalWidth; 
		
		List<Experiment> experiments = new ArrayList<Experiment>();
		
		public SampleSearchForm(final String id)
			{
			super(id);
			setOutputMarkupId(true);
//			sampleListPanel=new SampleListPanel("sampleListPanel", new ArrayList<Sample>());
			
			int pageWidth =  WebSession.get().getClientInfo().getProperties().getBrowserWidth(); //; //getSession().getC.getClientProperties().getBrowserWidth();
			int pageHeight = ((MedWorksSession) getSession()).getClientProperties().getBrowserHeight();
			
			searchModalWidth = (int) Math.round(0.4 *pageWidth);
			

			final ModalWindow modal1= ModalCreator.createModalWindow("modal1", searchModalWidth, 200);
	        add(modal1);
	        modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
	        	{
	            public void onClose(AjaxRequestTarget target)
	            	{
	            	((ListView)(modal1.getParent().get("experiments"))).setList(getExperiments());
	            	target.add(modal1.getParent());
	            	target.add(modal1.getParent().getParent());
	            	}
	        	});
			
	        add(buildLinkToModal("sampleSrchLink", modal1, null));
	        add(buildLinkToModal("experimentSrchLink", modal1, null));
	        add(buildLinkToModal("projectSrchLink", modal1, null));
	        add(buildLinkToModal("contactSrchLink", modal1, null));
	      //  add(buildLinkToModal("prepSrchLink", modal1, null));
			
//			add(emptyPanel=new EmptyPanel("emptyPanel", "Please use one of the above search criteria to search for samples."));
			add(new ListView("experiments", new PropertyModel(this, "experiments")) {
				public void populateItem(final ListItem listItem) 
					{
					final Experiment experiment = (Experiment) listItem.getModelObject();
//					
					String eName = experiment.getExpName()+" ("+experiment.getSampleList().size()+" samples)";
					listItem.add(new Label("expName", experiment.getExpName()+" ("+experiment.getSampleList().size()+" samples)"));
					listItem.add(buildLinkToModal("setup",modal1, experiment.getExpID()));
					listItem.add(buildLinkToModal("assays",modal1, experiment.getExpID()));
					
					listItem.add(new ListView("sampleList", new PropertyModel(experiment, "sampleList")) 
						{
						public void populateItem(final ListItem sListItem) 
							{
							final Sample sample = (Sample) sListItem.getModelObject();
							sListItem.add(buildLinkToModal("detail", modal1, sample.getSampleID()).add(new Label("sampleID", sample.getSampleID())));
//							sListItem.add(new Label("sampleID", sample.getSampleID()));
							sListItem.add(new Label("sampleName", sample.getSampleName()));
							sListItem.add(new Label("userSubj", sample.getUserSubject()));
							sListItem.add(new Label("amount", sample.getCurVolumeAndUnits()));
							sListItem.add(new Label("locID", new Model(sample.getLocID())));
							sListItem.add(OddEvenAttributeModifier.create(sListItem));
							if((highlightedSample!=null)&&(sample.getSampleID().equals(highlightedSample)))
								sListItem.add(new AttributeModifier("class", "selected"));
							}});//SampleListPanel("sampleListPanel", experiment.getSampleList()));
				}});
			updatePanels(null);
			}
		
		private void updatePanels(List<Sample> smplList){
//			sampleDetailPanel.setSamples(smplList);
//			emptyPanel.setVisible(smplList==null);
//			sampleDetailPanel.setVisible(smplList!=null);
		}
		
		private AjaxLink buildLinkToModal(final String linkID, final ModalWindow modal1, final String parameter) 
			{
			
			AjaxLink link=new AjaxLink(linkID)
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
	        	};
			return link;
			}
		
		
		private int grabHeight(String linkID)
			{
			switch (linkID)
				{
				case "assays" :
				case "setup" : return 700;
				case "detail" : return 500;
				}
			
			return 200;
			}
		
		
		private int grabWidth(String linkID)
			{
			switch (linkID)
				{
				case "detail" : return 500;
				case "assays"  : 
				case "setup" : return 700;
				}
			
			return searchModalWidth;
			}
		
		
		public Page grabPage(String linkID, String parameter, final ModalWindow modal1)
			{
		//	if(linkID.startsWith("de"))
		//		return new SampleDetailPage(parameter); 
       	 
			if(linkID.startsWith("sa"))
				return grabExperimentSearchBySamplePage(modal1);
		 
       	    if(linkID.startsWith("ex"))
       	    	return grabExperimentSearchByExperimentPage(modal1);
       	    	 
       	    if (linkID.startsWith("pro"))
       	    	return grabExperimentSearchByProjectPage(modal1);
       	    	
       	    //if (linkID.startsWith("pre"))
       	    ///	return grabExperimentSearchByPrepPage(modal1);
       		 
       	//    if (linkID.startsWith("se"))
       	 //   	return (new ExperimentalDesignPage(getPage(), experimentService.loadById(parameter)));
       	 
       	//    if (linkID.startsWith("as"))
       	 //   	return (new SampleAssaysPage(getPage(), experimentService.loadById(parameter)));
       	  
       	    return grabExperimentSearchByContact(modal1);
			}		
		
		
		private Page grabExperimentSearchByContact(final ModalWindow modal1)
			{
	    	return new ExperimentSearchByContact(getPage())
	    		{
				@Override
				protected void onSave(String contact, AjaxRequestTarget target1) 
					{
					modal1.close(target1);
					experiments = experimentService.loadExperimentsByClientContact(contact);
					}
	    		};
			}
		
		
		private Page grabExperimentSearchByPrepPage(final ModalWindow modal1)
			{
			return (new ExperimentSearchByPrep(getPage())
				{
				@Override
				protected void onSave(String prep, AjaxRequestTarget target1) 
					{
					modal1.close(target1);
					experiments = samplePrepService.loadExperimentsByPrep(prep);
					}
				});
			}
		
		private Page grabExperimentSearchByProjectPage(final ModalWindow modal1)
			{
			return (new ExperimentSearchByProject(getPage())
				{
				@Override
				protected void onSave(String project, AjaxRequestTarget target1) 
					{
					modal1.close(target1);
					experiments = experimentService.loadExperimentsByProjectID(StringParser.parseId(project));
					}
       		 	});
			}
		
		
		private Page grabExperimentSearchByExperimentPage(final ModalWindow modal1)
			{
			return (new ExperimentSearchByExperiment(getPage())
				{
				@Override
				protected void onSave(String exp, AjaxRequestTarget target1) 
					{
					modal1.close(target1);
					experiments.clear();
					if(FormatVerifier.verifyFormat(Experiment.idFormat, exp))
						experiments.add(experimentService.loadSimpleExperimetById(exp));
					else
						experiments.add(experimentService.loadSimpleExperimetById(StringParser.parseId(exp)));
					}
				});
			}
			
		
		private Page grabExperimentSearchBySamplePage(final ModalWindow modal1)
			{
			return (new ExperimentSearchBySample(getPage())
				{
				@Override
				protected void onSave(String sample, AjaxRequestTarget target1) 
					{
					modal1.close(target1);
					Sample s = sampleService.loadSampleAlongWithExpById(sample);
					experiments.clear();
					experiments.add(experimentService.loadSimpleExperimetById(s.getExp().getExpID()));
					highlightedSample=sample;
					}
				});
			}

		
		public List<Experiment> getExperiments() {
			return experiments;
		}

		public void setExperiments(List<Experiment> experiments) {
			this.experiments = experiments;
		}

	}
	
}

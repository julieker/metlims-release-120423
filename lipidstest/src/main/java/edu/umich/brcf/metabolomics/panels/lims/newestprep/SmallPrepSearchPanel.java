////////////////////////////////////////////////////
// SmallPrepSearchPanel.java
// Written by Jan Wigginton, Sep 8, 2016
////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.lims.newestprep;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.panels.lims.preparations.NewDataEntryTypePage;
import edu.umich.brcf.metabolomics.panels.lims.preparations.NewEditPrepPlate;
import edu.umich.brcf.metabolomics.panels.lims.preparations.NewPrepSheetUpload;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.ProjectService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;



public class SmallPrepSearchPanel extends Panel 
		{
		@SpringBean
		private SampleService sampleService;
		
		@SpringBean 
		private ExperimentService experimentService;
		
		@SpringBean 
		private ProjectService projectService;
		
		
		public SmallPrepSearchPanel(String id) 
			{
			super(id);
			setOutputMarkupId(true);
			add(new FeedbackPanel("feedback"));
			
			PrepSearchForm form;
			add(form = new PrepSearchForm("sampleSearchForm"));
			form.setOutputMarkupId(true);
			}
		
		
		public final class PrepSearchForm extends Form 
			{
			public PrepSearchForm(final String id)
				{
				super(id);
				setOutputMarkupId(true);
				
				final WebMarkupContainer container = new WebMarkupContainer("container");
				container.setOutputMarkupId(true);
				add(container);
				
				
				final ModalWindow modal1= ModalCreator.createModalWindow("modal1", 650, 400);
				container.add(modal1); 
			
				container.add(buildLinkToModal("newPrepSearch", modal1).setOutputMarkupId(true));
				container.add(buildLinkToModal("createPrepLink", modal1));
				}
			
		// issue 464
		private AjaxLink buildLinkToModal(final String linkID, final ModalWindow modal1) 
			{
			// issue 39
			return new AjaxLink <Void>(linkID)
				{
				@Override
				public void onClick(final AjaxRequestTarget target)
					{
					doClick(modal1, target, linkID);
					}
				};
			}
		
		
		public void doClick(final ModalWindow modal1, AjaxRequestTarget target, final String linkID)
			{
			modal1.setPageCreator(new ModalWindow.PageCreator()
				{
				public Page createPage()
					{
					switch (linkID)
						{
						case "newPrepSearch" : 
							return new PrepSearchByAnythingPage(getPage()); 
						case "createPrepLink" :
						default :
		           			return buildCreatePrep(modal1);
						}
					}
				});
			
			int initWidth, initHeight;
			switch (linkID)
				{
				case "newPrepSearch" : initWidth = 800; initHeight = 250; break;
				default : initWidth = 1000; initHeight = 900; break;
				}
			
			modal1.setInitialHeight(initHeight);
			modal1.setInitialWidth(initWidth);
			modal1.show(target);
			//buildCreatePrep
			}
		
		NewDataEntryTypePage buildCreatePrep(final ModalWindow modal1)
			{
			return (new NewDataEntryTypePage(getPage(), modal1)
				{
				@Override
				protected void onSave(String choice1, String choice2, String prepTitle) 
					{
					System.out.println("The prep title is " + prepTitle);
					if (choice1.equals("Manual"))
						{
						//modal1.close(target);
						setResponsePage(new NewEditPrepPlate(getPage(), prepTitle, null, choice2));
						}
					else
						setResponsePage(new NewPrepSheetUpload(getPage(), prepTitle, null, choice2));
					}
				});
			}	
		}
	}
			
		

/*
 * 
 * private AjaxLink buildLinkToModal(final String linkID, final METWorksPctSizableModal modal1) 
		{
		return new AjaxLink(linkID)
	    	{
	        @Override
	        public void onClick(final AjaxRequestTarget target)
	        	{
	        	doClick(modal1, target, linkID);
	        	}
			};
		}
	
	
	public void doClick(final METWorksPctSizableModal modal2, AjaxRequestTarget target, final String linkID)
		{
		boolean isCreate = (linkID != null && "createPrepLink".equals(linkID));
		modal2.setPageDimensions(isCreate ? 0.9 : 0.35, isCreate ? 0.7 : 0.2);
	
		modal2.setPageCreator(new ModalWindow.PageCreator()
	        {
	        public Page createPage()
	        	{
	        	switch(linkID)
		        	{
		        	case "prepSearchLink":
		        		return buildSearchByPrep(modal2);
		        	case "experimentSearchLink" :
		        		return buildSearchByExperiment(modal2);
		        	case "clientSearchLink" :
		        		return buildSearchByClient(modal2);
		        	case "dateSearchLink" :
		        		return buildSearchByDate(modal2);
		        	case "creatorSearchLink" : 
		           		return buildSearchByCreator(modal2);
		        	case "assaySearchLink" : 
		           		return buildSearchByAssay(modal2);
		        	case "sampleSearchLink" : 
		           		return buildSearchBySample(modal2);
		        	default :
		           		return buildCreatePrep(modal2);
		        	}
	        	}
	        });
		modal2.show(target);
		}
	}
*/
		
		
		

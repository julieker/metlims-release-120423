package edu.umich.brcf.metabolomics.panels.lims.preparations;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Preparation;
import edu.umich.brcf.shared.layers.service.SamplePrepService;
import edu.umich.brcf.shared.util.StringParser;


public abstract class NewPreparationsListPanel extends Panel{
	
	@SpringBean
	SamplePrepService samplePrepService;

	List <String> preps;
	
	public NewPreparationsListPanel(String id) 
		{
		super(id);
		
		preps = samplePrepService.allSamplePreparationsSortedByDate();
		
		add(new ListView("preps", new PropertyModel(this, "preps"))
			{
			public void populateItem(final ListItem listItem)
				{
				final String samplePrep = (String)listItem.getModelObject();
				
				AjaxLink idLink;
				listItem.add(idLink=new AjaxLink("idLink")
					{
					@Override
					public void onClick(AjaxRequestTarget target) 
						{
						NewPreparationsListPanel.this.onIdClick(StringParser.parseId(samplePrep), target);
						}
					@Override // issue 464
					public MarkupContainer setDefaultModel(IModel model) 
					    {
						// TODO Auto-generated method stub
						return this;
					    }
					});

				idLink.add(new Label("id", new Model(StringParser.parseId(samplePrep))));
				idLink.add(new Label("title", new Model(StringParser.parseName(samplePrep))));
				//listItem.add(OddEvenAttributeModifier.create(listItem));
				}
			});
		}

	public List<String> getPreps()
		{
		return preps;
		}
	
	public void setPrepsByString(List <String> p)
		{
		preps = p;
		}
	
	public void setPrepsByPrepList(List <Preparation> p)
		{
		preps = new ArrayList<String>();
		for (int i = 0; i < p.size(); i++)
			preps.add(p.get(i).getTitle() + " (" + p.get(i).getPrepID() + ")");
		}
	
	protected abstract void onIdClick(String prep, AjaxRequestTarget target);
	}



/*
import java.util.Date;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.metworks.lims.domain.Experiment;
import edu.umich.metworks.lims.service.SamplePrepService;
import edu.umich.metworks.util.DateUtils;
import edu.umich.metworks.util.StringParser;
import edu.umich.metworks.web.panels.lims.preparations.EditPrepPlate;
import edu.umich.metworks.web.panels.lims.preparations.PrepSheetUpload;
import edu.umich.metworks.web.utils.OddEvenAttributeModifier;
import edu.umich.metworks.web.utils.dialog.METWorksPctSizableModal;
import edu.umich.metworks.web.utils.dialog.PrepSearchByClient;
import edu.umich.metworks.web.utils.dialog.PrepSearchByCreator;
import edu.umich.metworks.web.utils.dialog.PrepSearchByDate;
import edu.umich.metworks.web.utils.dialog.PrepSearchByExperiment;
import edu.umich.metworks.web.utils.dialog.PrepSearchByPrep;

public abstract class NewPreparationsListPanel extends Panel{
	
	@SpringBean
	SamplePrepService samplePrepService;

	ListView prepList;
	String searchType = "allPreps";
	
	private String selectedExperiment, selectedAssay;
	
	public String getSelectedExperiment() {
		return selectedExperiment;
	}

	public void setSelectedExperiment(String selectedExperiment) {
		this.selectedExperiment = selectedExperiment;
	}

	public String getSelectedAssay() {
		return selectedAssay;
	}

	public void setSelectedAssay(String selectedAssay) {
		this.selectedAssay = selectedAssay;
	}

	public NewPreparationsListPanel(String id) 
		{
		super(id);
	
		 final WebMarkupContainer container = new WebMarkupContainer("container");
	     container.setOutputMarkupId(true);
	        
		METWorksPctSizableModal modal1= new METWorksPctSizableModal("modal1", .35, .2);
        modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
        	{
            public void onClose(AjaxRequestTarget target)
            	{
            	String date = DateUtils.dateAsString(new Date(), "MM/dd/yyyy");
				//String prepTitle = selectedExperiment + "_" + StringParser.parseId(selectedAssay) + "_" + date; 
				//target.add(prepTitleFld);
				
            	//target.appendJavaScript("alert('Closed with title " + prepTitle + " ');");
            	target.add(container);
//            	System.out.println("Experiment on close was " + selectedExperiment);
            	//System.out.println("Experiment returned is " + selectedExperiment + " and assay returned is " + selectedAssay);
            	}
        	});
       
        add(modal1);
        
        add(buildLinkToModal("creatorSearchLink", modal1));
        add(buildLinkToModal("dateSearchLink", modal1));
        add(buildLinkToModal("clientSearchLink", modal1));
        add(buildLinkToModal("experimentSearchLink", modal1));
        add(buildLinkToModal("prepSearchLink", modal1));
        add(buildLinkToModal("createPrepLink", modal1));
       
		container.add(prepList = new ListView("preps", new PropertyModel(this, "preps"))
			{
			public void populateItem(final ListItem listItem)
				{
				final String samplePrep = (String)listItem.getModelObject();
				
				AjaxLink idLink;
				listItem.add(idLink=new AjaxLink("idLink")
					{
					@Override
					public void onClick(AjaxRequestTarget target) 
						{
						NewPreparationsListPanel.this.onIdClick(StringParser.parseId(samplePrep), target);
						}
					});

				idLink.add(new Label("id", new Model(StringParser.parseId(samplePrep))));
				
				listItem.add(new Label("title", new Model(StringParser.parseName(samplePrep))));
				listItem.add(OddEvenAttributeModifier.create(listItem));
				}
			});
			prepList.setOutputMarkupId(true);
			add(container);
		}

	public List<String> getPreps()
		{
		switch(getSearchType())
			{
			case "allPreps" : 
				return  samplePrepService.allSamplePreparationsSortedByDate();
				
			default : 
				return  samplePrepService.allSamplePreparationsSortedByDateButShort();
				
			}
		}
	
	
	private String getSearchType()
		{
		return this.searchType;
		}
	
	private void setSearchType(String value)
		{
		searchType = value;
		}
	
	protected abstract void onIdClick(String prep, AjaxRequestTarget target);
	
	
	PrepSearchByPrep buildSearchByPrep(final ModalWindow modal1)
		{
		return (new PrepSearchByPrep(getPage())
			{
			@Override
			protected void onSave(String prep, AjaxRequestTarget target1) 
				{
				modal1.close(target1);
				//setProjectList(samplePrepService.loadProjectExperimentByPrep(prep));
				//updatePanels(getProjectList());
				//setExperiment(null);
				}
			});
		}
	
	
	PrepSearchByExperiment buildSearchByExperiment(final ModalWindow modal1)
		{
		return (new PrepSearchByExperiment(getPage())
			{
			@Override
			protected void onSave(String prep, AjaxRequestTarget target1) 
				{
				modal1.close(target1);
				//setProjectList(samplePrepService.loadProjectExperimentByPrep(prep));
				//updatePanels(getProjectList());
				//setExperiment(null);
				}
			});
		}
	
	PrepSearchByClient buildSearchByClient(final ModalWindow modal1)
		{
		return (new PrepSearchByClient(getPage())
			{
			@Override
			protected void onSave(String prep, AjaxRequestTarget target1) 
				{
				modal1.close(target1);
				//setProjectList(samplePrepService.loadProjectExperimentByPrep(prep));
				//updatePanels(getProjectList());
				//setExperiment(null);
				}
			});
		}
	
	PrepSearchByCreator buildSearchByCreator(final ModalWindow modal1)
		{
		return (new PrepSearchByCreator(getPage())
			{
			@Override
			protected void onSave(String prep, AjaxRequestTarget target1) 
				{
				modal1.close(target1);
				//setProjectList(samplePrepService.loadProjectExperimentByPrep(prep));
				//updatePanels(getProjectList());
				//setExperiment(null);
				}
			});
		}
	
	PrepSearchByDate buildSearchByDate(final ModalWindow modal1)
		{
		return (new PrepSearchByDate(getPage())
			{
			@Override
			protected void onSave(String prep, AjaxRequestTarget target1) 
				{
				modal1.close(target1);
				//setProjectList(samplePrepService.loadProjectExperimentByPrep(prep));
				//updatePanels(getProjectList());
				//setExperiment(null);
				}
			});
		}
	
	NewDataEntryTypePage buildCreatePrep(final METWorksPctSizableModal modal1)
		{
		return (new NewDataEntryTypePage(getPage())
			{
			//@Override
			//protected void doSave(String selectedExperiment, String selectedAssay, AjaxRequestTarget target) 
			//	{
			//	System.out.println("We've created a new prep");
			//	System.out.println("Selected experiment is " + selectedExperiment);
			//	setSelectedExperiment(selectedExperiment);
			//	setSelectedAssay(selectedAssay);
			//	System.out.println("Selected experiment is " + this.selectedExperiment);
			//	}

			//@Override
			//protected void onSave(String exp, String assay, AjaxRequestTarget target) 
			//	{
			//	// TODO Auto-generated method stub
			//	System.out.println("Experiment returned is " + exp + " and assay returned is " + assay);
			//	setSelectedExperiment(exp);
			//	setSelectedAssay(assay);
			//	}

			@Override
			protected void onSave(String choice1, String choice2, String prepTitle) 
				{
				if (choice1.equals("Manual"))
				{
						setResponsePage(new EditPrepPlate(getPage(), prepTitle, pp, choice2));
							//new EditPrepPlate(getPage(), titleTxt, pp));
				}
				else
					setResponsePage(new PrepSheetUpload(getPage(), prepTitle, pp, choice2));
				}
			});
		}	
	
	private AjaxLink buildLinkToModal(final String linkID, final METWorksPctSizableModal modal1) 
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


	public void doClick(final METWorksPctSizableModal modal1, AjaxRequestTarget target, final String linkID)
		{
		boolean isCreate = (linkID != null && "createPrepLink".equals(linkID));
		modal1.setPageDimensions(isCreate ? 0.6 : 0.35, isCreate ? 0.25 : 0.2);

		modal1.setPageCreator(new ModalWindow.PageCreator()
	        {
	        public Page createPage()
	        	{
	        	switch(linkID)
		        	{
		        	case "prepSearchLink":
		        		return buildSearchByPrep(modal1);
		        	case "experimentSearchLink" :
		        		return buildSearchByExperiment(modal1);
		        	case "clientSearchLink" :
		        		return buildSearchByClient(modal1);
		        	case "dateSearchLink" :
		        		return buildSearchByDate(modal1);
		        	case "creatorSearchLink" : 
		           		return buildSearchByCreator(modal1);
		           	default :
		           		return buildCreatePrep(modal1);
		        	}
	        	}
	      });
	  	
		modal1.show(target);
		}
	}


*/

////////////////SCRAP CODE ///////

//listItem.add(new Label("date", new Model(CalendarUtils.dateTimeDisplayStringFrom(samplePrep.getPrepDate().getTime()))));

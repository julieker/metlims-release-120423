////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  WorklistOverviewPanel.java
//  Written by Jan Wigginton
//  July 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.workflow_tracking;

import org.apache.wicket.markup.html.panel.Panel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.domain.GeneratedWorklist;
import edu.umich.brcf.metabolomics.layers.domain.GeneratedWorklistItem;
import edu.umich.brcf.metabolomics.layers.service.GeneratedWorklistItemService;
import edu.umich.brcf.metabolomics.layers.service.GeneratedWorklistService;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.widgets.AjaxBackButton;



public class WorklistOverviewPanel extends Panel
	{
	@SpringBean
	GeneratedWorklistService generatedWorklistService;
	
	@SpringBean
	GeneratedWorklistItemService generatedWorklistItemService;
	
	@SpringBean
	UserService userService;
	
	@SpringBean 
	AssayService assayService;

	@SpringBean
	ExperimentService experimentService;

	public WorklistOverviewPanel(String id, WebPage backPage, String expId, String assayId, Boolean ifEdit, Boolean useRunDate)
		{
		super(id);
		add(new FeedbackPanel("feedback"));
		
		DataSetForm luf = new DataSetForm( "dataSetForm", backPage, expId, assayId, null, null, ifEdit, useRunDate);
		luf.setMultiPart(true);
		add(luf);
		}

	
	
	public final class DataSetForm extends Form 
		{
		private WebMarkupContainer container; 
		
		protected PageableListView dataSetView, commentListView;
		List <GeneratedWorklist> dataSets = new ArrayList <GeneratedWorklist>();
		LoadableDetachableModel<List <GeneratedWorklistItem>> commentedItems;
		
		String searchTitle, expId, assayId;
		Boolean ifEdit;
	
		
		public DataSetForm(String id,  WebPage backPage, String expId, String assayId, Calendar fromDateCalendar, Calendar toDateCalendar, Boolean ifEdit, Boolean useRunDate)
			{
			super(id);
			
			this.ifEdit = ifEdit;
			this.assayId = assayId;
			this.expId = expId;
			
			dataSets = grabDataSets(expId, assayId);
			commentedItems = new LoadableDetachableModel<List<GeneratedWorklistItem>>()
				{
				@Override
				protected List<GeneratedWorklistItem> load()  { return getCommentedItemsList(dataSets); }
				};
		
			String assayName = assayService.getNameForAssayId(assayId);
			Experiment exp = experimentService.loadById(expId);
			String experimentName = exp != null ? exp.getExpName() : "";
			
			add(new Label("experimentName", experimentName));
			add(new Label("assayName", assayName));
			add(new Label("assay", assayId));
			add(new Label("expId", expId));
			
			add(new AjaxBackButton("backButton", backPage));
	
			container = (WebMarkupContainer) (new WebMarkupContainer("container")).setOutputMarkupId(true);
			container.add(dataSetView = buildListView("dataSetListView"));
			container.add(commentListView = buildCommentedListView("commentListView"));
			dataSetView.setOutputMarkupId(true);
			
			add(container);
			}
		
		
		List <GeneratedWorklist> grabDataSets(String expId, String assayId) //Calendar fromDateCalendar, Calendar toDateCalendar, Boolean useRunDate)
			{
			if (expId == null || expId.equals("") || expId.toLowerCase().equals("all"))
				return generatedWorklistService.loadAll();
			
			if (assayId == null || assayId.trim().equals("") || assayId.toLowerCase().equals("all"))
				return  generatedWorklistService.loadByExpId(expId);
			
			return  generatedWorklistService.loadByExpIdAndAssayId(expId, assayId);
			}
		
		// View Details...
		
		List <GeneratedWorklistItem> getCommentedItemsList(List <GeneratedWorklist> worklists)
			{
			List <GeneratedWorklistItem> commentedItemsList = new ArrayList<GeneratedWorklistItem>();
			
			for (int i = 0; i < worklists.size(); i++)
				{
				String worklistId = worklists.get(i).getWorklistId();
				List <GeneratedWorklistItem> newItems = generatedWorklistItemService.loadCommentedByWorklistId(worklistId);
				for (int j = 0; j < newItems.size(); j++)
					commentedItemsList.add(newItems.get(j));
				}
			
			return commentedItemsList;
			}
		

		public PageableListView buildCommentedListView(String id)
		{
		return new PageableListView(id, commentedItems, 2000) // new PropertyModel<List<GeneratedWorklistItem>>(this, "commentedItems"), 2000)
			{	
			public void populateItem(ListItem listItem) 
				{
				final GeneratedWorklistItem item =  (GeneratedWorklistItem) listItem.getModelObject();
				
				listItem.add(new Label("worklistIdForComment", new PropertyModel<String>(item, "worklistIdForComment")));
				
				listItem.add(new Label("sampleName", new PropertyModel<String>(item, "sampleName")));
				listItem.add(new Label("sampleOrControlId", new PropertyModel<String>(item, "sampleOrControlId")));
				listItem.add(new Label("fileName", new PropertyModel<String>(item, "fileName")));
				listItem.add(new Label("platePos", new PropertyModel<String>(item, "platePos")));
				listItem.add(new Label("injVol", new PropertyModel<String>(item, "injVol")));
				listItem.add(new Label("comments", new PropertyModel<String>(item, "comments")));

				listItem.add(OddEvenAttributeModifier.create(listItem));
				}
			};
		}

		
		
		public PageableListView buildListView(String id)
			{
			return new PageableListView(id, new PropertyModel(this, "dataSets"), 2000)
				{	
				public void populateItem(ListItem listItem) 
					{
					final GeneratedWorklist item =  (GeneratedWorklist) listItem.getModelObject();
					
					listItem.add(new Label("worklistId", new PropertyModel(item, "worklistId")));
					listItem.add(new Label("expId", new PropertyModel(item, "expId")));
					
					String assayId = item.getAssayId();
					String assayName =  assayId == null ? "" : assayService.getNameForAssayId(assayId);
					
					listItem.add(new Label("assayName", assayName));
					listItem.add(new Label("injectionMode", new PropertyModel<String>(item, "injectionMode")));
					//listItem.add(new Label("runDate", new PropertyModel<Calendar>(item, "dateGeneratedAsStr")));
					listItem.add(new Label("generatedDate", new PropertyModel<Calendar>(item, "dateGeneratedAsStr")));
					
					String userId = item.getGeneratedBy();
					String fullName = userId == null ? "-" : userService.getFullNameByUserId(userId);
					listItem.add(new Label("generatedBy", fullName));
					listItem.add(buildLinkToData("dataSetLink", item.getWorklistId(), item.getDateGeneratedAsStr(), 
							new  PropertyModel<String>(item, "injectionMode"), fullName));
					
					listItem.add(OddEvenAttributeModifier.create(listItem));
					}
				};
			}
				
		
		private IndicatingAjaxButton buildLinkToData(String id, final String dataSetId, final String date, 
				final PropertyModel<String> mode, final String uploader)
			{
			return new IndicatingAjaxButton(id)
				{
				@Override
				protected void onComponentTag(ComponentTag tag)
		    		{
		    		super.onComponentTag(tag);
		    		String displayTitle = "   View/Edit Details...   ";
		    		tag.put("value", displayTitle);
		    		}
	
				@Override
				protected void onSubmit(AjaxRequestTarget arg0) // issue 464
					{
					setResponsePage(new WorklistDetailViewerPage("searchResults", (WebPage) getPage(),
							expId, assayId, dataSetId, date, mode.getObject(), uploader));
					}

				@Override
				protected void onError(AjaxRequestTarget target) { } // issue 464
				};
			}
		
		private IndicatingAjaxButton buildBackButton(String id, final WebPage backPage)
			{
			return new IndicatingAjaxButton(id)
				{
				@Override
				protected void onSubmit(AjaxRequestTarget arg0) // issue 464
					{
					setResponsePage(backPage); 
					}

				@Override
				protected void onError(AjaxRequestTarget target) { } // issue 464
				};
			}
	
		public List<GeneratedWorklist> getDataSets()
			{
			return dataSets;
			}
	
		public void setDataSets(List<GeneratedWorklist> dataSets)
			{
			this.dataSets = dataSets;
			}
			
		public WebMarkupContainer getContainer()
			{
			return container;
			}
	
		public void setDataSetView(PageableListView view)
			{
			dataSetView = view;
			}
		
		public PageableListView getDataSetView()
			{
			return dataSetView;
			}
		
		public String getSearchTitle()
			{
			return searchTitle;
			}
		}
	}	





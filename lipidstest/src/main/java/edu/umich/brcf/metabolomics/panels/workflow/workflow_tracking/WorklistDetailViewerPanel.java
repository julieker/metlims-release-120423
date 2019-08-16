////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  WorklistCommentViewerPanel.java
//  Written by Jan Wigginton
//  July 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.workflow_tracking;

import org.apache.wicket.markup.html.panel.Panel;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.domain.GeneratedWorklistItem;
import edu.umich.brcf.metabolomics.layers.dto.GeneratedWorklistItemDTO;
import edu.umich.brcf.metabolomics.layers.service.GeneratedWorklistItemService;
import edu.umich.brcf.metabolomics.layers.service.GeneratedWorklistService;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.widgets.AjaxBackButton;


public class WorklistDetailViewerPanel extends Panel
	{
	@SpringBean
	GeneratedWorklistService generatedWorklistService;

	@SpringBean
	GeneratedWorklistItemService generatedWorklistItemService;

	@SpringBean
	UserService userService;

	@SpringBean
	AssayService assayService;
	

	public WorklistDetailViewerPanel(String id, WebPage backPage, String expId, String assayId, String worklistId,
		String date, String mode,String uploadedBy)
		{
		super(id);

		String assayName = assayService.getNameForAssayId(assayId);
	
		add(new Label("expId", expId));
		add(new Label("assay", assayName));
		add(new Label("mode", mode));
		add(new Label("date", date));
		add(new Label("uploadedBy", uploadedBy));

		DataSetForm luf = new DataSetForm("dataSetForm", backPage, expId, assayId, worklistId, date)
			{
			public void onSave(List<GeneratedWorklistItem> items, AjaxRequestTarget target)
				{
				List<GeneratedWorklistItemDTO> dtos = new ArrayList<GeneratedWorklistItemDTO>();

				for (int i = 0; i < items.size(); i++)
					{
					GeneratedWorklistItemDTO dto = GeneratedWorklistItemDTO.instance(items.get(i));
					dtos.add(dto);
					}

				generatedWorklistService.updateItems(dtos);
				}
		};

		luf.setMultiPart(true);
		add(luf);
		}

	
	public abstract class DataSetForm extends Form
		{
		private WebMarkupContainer container;
		protected ListView dataSetView;
		List<GeneratedWorklistItem> items = new ArrayList<GeneratedWorklistItem>();

		String searchTitle;
		Boolean ifEdit, changesMade = false;
		AjaxSubmitLink saveButton;

		public DataSetForm(String id, WebPage backPage, String expId, String assayId, String worklistId, String date)
			{
			super(id);

			this.ifEdit = ifEdit;

			searchTitle = "";
			if (expId != null && expId.equals("All"))
				searchTitle = assayId.equals("All") ? "Worklist Items" : "Worklist Items for Assay " + assayService.getNameForAssayId(assayId);
			else if (assayId != null)
				searchTitle = assayId.equals("All") ? "All Worklist Items for Experiment : " + expId + ", Date :" + date
					: "Worklist Items for Experiment : " + expId + ",  Assay :" + assayService.getNameForAssayId(assayId) + ", Date : " + date;

			items = grabCommentedItems(worklistId, true);

			add(new FeedbackPanel("feedback").setOutputMarkupId(true));

			add(new AjaxBackButton("backButton", backPage)
				{
				boolean warned = false;

				@Override
				public void onClick(AjaxRequestTarget target) 
					{
					if (changesMade && !warned)
						{
						target.appendJavaScript("alert('Warning : your changes have not been saved.  If you would like to, press Save before leaving the page');");
						warned = true;
						} 
					else
						super.onClick(target);
					}
				});
			
			add(saveButton = new AjaxSubmitLink("saveChanges", this)
				{
				@Override
				public boolean isEnabled() { return changesMade; }

				@Override
				protected void onSubmit(AjaxRequestTarget target) // issue 464
					{
					try
						{
						target.add(DataSetForm.this.get("feedback"));
						DataSetForm.this.onSave(items, target);
						changesMade = false;
						target.add(this);
						} 
					catch (Exception e)
						{
						e.printStackTrace();
						DataSetForm.this.error("Save unsuccessful. Please re-check values entered.");
						target.add(DataSetForm.this.get("feedback"));
						}
					}
				});

			container = (WebMarkupContainer) (new WebMarkupContainer("container")).setOutputMarkupId(true);
			container.add(dataSetView = buildListView("dataSetListView"));
			dataSetView.setOutputMarkupId(true);

			add(container);
			}

		
		List<GeneratedWorklistItem> grabCommentedItems(String worklistId, Boolean grabAll) 
			{
			if (grabAll)
				return generatedWorklistItemService.loadByWorklistId(worklistId);

			return generatedWorklistItemService.loadCommentedByWorklistId(worklistId);
			}

		
		public ListView buildListView(String id)
			{
			return new ListView(id, new PropertyModel(this, "items"))
				{
				public void populateItem(ListItem listItem)
					{
					final GeneratedWorklistItem item = (GeneratedWorklistItem) listItem.getModelObject();

					listItem.add(new Label("itemId", new PropertyModel<Long>(item, "itemId")));
					listItem.add(new Label("sampleName", new PropertyModel<String>(item, "sampleName")));
					listItem.add(new Label("sampleOrControlId", new PropertyModel<String>(item, "sampleOrControlId")));
					listItem.add(new Label("fileName", new PropertyModel<String>(item, "fileName")));
					listItem.add(buildCommentField("comments", item));

					listItem.add(new Label("injVol", new PropertyModel<String>(item, "injVol")));
					listItem.add(new Label("platePos", new PropertyModel<String>(item, "platePos")));
				
					listItem.add(OddEvenAttributeModifier.create(listItem));
					}
				};
			}

		
		private TextField buildCommentField(String id, GeneratedWorklistItem item)
			{
			TextField t = new TextField("comments", new PropertyModel<String>(item, "comments"));
			t.add(new AjaxFormComponentUpdatingBehavior("change")
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target)
					{
					changesMade = true;
					target.add(saveButton);
					}
				});
			return t;
			}


		private IndicatingAjaxButton buildLinkToData(String id, final String dataSetId)
			{
			return new IndicatingAjaxButton(id)
				{
				@Override
				protected void onComponentTag(ComponentTag tag)
					{
					super.onComponentTag(tag);
					String displayTitle = "&nbsp;&nbsp;View Comments&nbsp;&nbsp;";
					tag.put("value", displayTitle);
					}

				@Override
				protected void onSubmit(AjaxRequestTarget arg0) {  } // issue 464
				};
			}
		

		public List<GeneratedWorklistItem> getItems()
			{
			return items;
			}

		public void setItems(List<GeneratedWorklistItem> items)
			{
			this.items = items;
			}

		public void setDataSetView(PageableListView view)
			{
			dataSetView = view;
			}

		public String getSearchTitle()
			{
			return searchTitle;
			}
		
		public abstract void onSave(List<GeneratedWorklistItem> items, AjaxRequestTarget target);
		}
	}

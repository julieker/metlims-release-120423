///////////////////////////////////////
//ScanBarcodesPage.java
//Written by Jan Wigginton March 2016
///////////////////////////////////////

package edu.umich.brcf.shared.panels.utilitypanels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.util.DeleteableItem;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;


public abstract class ScanBarcodesPage extends WebPage
	{
	@SpringBean
	UserService userService;

	@SpringBean
	ExperimentService experimentService;

	
	List<DeleteableItem> itemsList;
	List<String> namesForSearch;
	String pageTitle, buttonLabel = "&nbsp;&nbsp;&nbsp;Add&nbsp;&#10140;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
	Boolean checkForDuplicates = true;

	
	public ScanBarcodesPage(String id, String pageTitle, String secondary, final ModalWindow modal)
		{
		this.pageTitle = pageTitle + secondary;
		itemsList = new ArrayList<DeleteableItem>();
		namesForSearch = new ArrayList<String>();

		add(new Label("pageTitle", new PropertyModel<String>(this, "pageTitle")));
		add(new ScanBarcodesForm("scanBarcodeForm", modal));
		}

	
	private class ScanBarcodesForm extends Form
		{
		String currentScan = "";
		Integer currentCount = 0;
		TextField<String> scanField, countField;
		WebMarkupContainer container;

		public ScanBarcodesForm(String id, final ModalWindow modal)
			{
			super(id);

			container = new WebMarkupContainer("container");

			scanField = buildScanField("scanField");
			container.add(scanField);

			countField = buildCountField("scanCount");
			container.add(countField);

			container.add(buildNextScanButton("nextScanButton", container));
			container.add(new AjaxCancelLink("closeButton", modal));
			container.add(buildSubmitBatchButton("submitBatchButton"));
			container.add(buildItemsListView("itemsListView", container));

			container.setOutputMarkupId(true);
			container.setOutputMarkupPlaceholderTag(true);
			add(container);
			}

		private AjaxSubmitLink buildSubmitBatchButton(String id)
			{
			AjaxSubmitLink link = new AjaxSubmitLink(id)
				{
				@Override
				protected void onComponentTag(ComponentTag tag)
					{
					super.onComponentTag(tag);
					String label = getButtonLabel();
					tag.put("value", "Submit Batch");
					}

				@Override //issue 464
				protected void onSubmit(AjaxRequestTarget target)
					{
					doSubmit(target, namesForSearch);
					}

				@Override
				protected void onError(AjaxRequestTarget arg0) { } 
				};

			link.setOutputMarkupId(true);
			return link;
			}

		
		public TextField<String> buildCountField(String id)
			{
			TextField<String> fld = new TextField<String>(id,
					new PropertyModel<String>(this, "currentCount"));
			fld.add(this.buildSpecialFormComponentUpdateBehavior("change",
					"updateForNewCount", fld));
			return fld;
			}

		
		public TextField<String> buildScanField(String id)
			{
			TextField<String> fld = new TextField<String>(id,
					new PropertyModel<String>(this, "currentScan"));
			fld.add(this.buildStandardFormComponentUpdateBehavior("change",
					"updateForNewScan"));
			return fld;
			}

		
		private AjaxFormComponentUpdatingBehavior buildSpecialFormComponentUpdateBehavior(final String event, final String response, final TextField fld)
			{
			return new AjaxFormComponentUpdatingBehavior(event)
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target)
					{
					currentCount = itemsList.size();

					switch (response)
						{
						case "updateForNewCount":
							target.add(fld);
							break;
						case "updateForLeaveListField":
							target.add(fld);
						}
					}
				};
			}

		
		private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(final String event, final String response)
			{
			return new AjaxFormComponentUpdatingBehavior(event)
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target)
					{
					switch (response)
						{
						case "updateForNewScan":
							checkForDuplicates = true;
							buttonLabel = "&nbsp;&nbsp;&nbsp;Add&nbsp;&#10140;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
							currentCount = itemsList.size();
							target.add(container);
							break;
						default : 
						}
					}
				};
			}

		
		public AjaxLink buildNextScanButton(String id, final WebMarkupContainer container)
			{
			return new AjaxLink <Void>(id)
			// issue 39
				{
				@Override
				public void onClick(AjaxRequestTarget target)
					{
					if (checkForDuplicates && haveItemAlready(currentScan))
						{
						target.appendJavaScript(StringUtils.makeAlertMessage("Item with code " + currentScan
						 + " has already has been scanned. "));
						}

					else if (currentScan != null && !"".equals(currentScan.trim()))
						{
						DeleteableItem item = new DeleteableItem(currentScan);
						itemsList.add(item);
						namesForSearch.add(currentScan);
						buttonLabel = "&nbsp;&nbsp;&nbsp;Add&nbsp;&#10140;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
						currentCount = itemsList.size();
						currentScan = "";
						}

					target.add(container);
					}
				@Override
				protected void onComponentTag(ComponentTag tag)
					{
					super.onComponentTag(tag);
					tag.put("value", getButtonLabel());
					}
				};
			}

		
		private boolean haveItemAlready(String name)
			{
			return namesForSearch.contains(name);
			}
		

		private ListView buildItemsListView(String id,
				final WebMarkupContainer container)
			{
			ListView lstView = new ListView(id, new PropertyModel(this, "itemsList"))
				{
				@Override
				public void populateItem(ListItem listItem)
					{
					final DeleteableItem item = (DeleteableItem) listItem.getModelObject();

					TextField label;
					AjaxLink link;
					listItem.add(link = buildDeleteButton("deleteButton", item, container));
					listItem.add(label = new TextField("itemName", new PropertyModel(item, "itemName")));

					listItem.add(OddEvenAttributeModifier.create(listItem));
					label.setOutputMarkupId(true);
					label.add(buildSpecialFormComponentUpdateBehavior("onmouseout", "updateForLeaveListField", label));
					}
				};

			lstView.setOutputMarkupId(true);
			return lstView;
			}

		
		private AjaxLink buildDeleteButton(String id, final DeleteableItem item, final WebMarkupContainer container)
			{
			// issue 39
			return new AjaxLink <Void>(id)
				{
				@Override
				public void onClick(AjaxRequestTarget target)
					{
					itemsList.remove(item);
					currentCount = itemsList.size();
					target.add(container);
					}
				};
			}

		
		public List<DeleteableItem> getItemsList()
			{
			return itemsList;
			}

		
		public void setItemsList(List<DeleteableItem> lst)
			{
			itemsList = lst;
			}

		
		public String getCurrentScan()
			{
			return currentScan;
			}

		
		public void setCurrentScan(String currentScan)
			{
			this.currentScan = currentScan;
			}
		}

	
	public String getButtonLabel()
		{
		return buttonLabel;
		}

	
	public void setButtonLabel(String buttonLabel)
		{
		this.buttonLabel = buttonLabel;
		}
	

	public abstract void doSubmit(AjaxRequestTarget target, List<String> scannedIds);
	}

////////////////////////////////////////////////////
// SampleSelectorPanel.java
// Written by Jan Wigginton, Apr 14, 2016
////////////////////////////////////////////////////

package edu.umich.brcf.shared.panels.utilitypanels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.interfaces.ISampleItem;
import edu.umich.brcf.shared.util.structures.SelectableObject;
import edu.umich.brcf.shared.util.widgets.AjaxBackButton;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;



public abstract class SampleSelectorPanel extends Panel
	{
	boolean useCheckins = false;
	String pageTitle = "Select Samples", filterLabel = "In Prep Cue", actionLabel = "Show Prep Sheet";
	String buttonLabel = "View Details";
	
	
	public  SampleSelectorPanel(String id, WebPage backPage, List <? extends ISampleItem> itemsList, String setId, String title)
		{
		this(id, backPage, itemsList, setId, title, null);
		}
	
	
	public  SampleSelectorPanel(String id, ModalWindow modal, List <? extends ISampleItem> itemsList, String setId, String title)
		{
		this(id, null, itemsList, setId, title, modal);
		}
	
	
	public  SampleSelectorPanel(String id, WebPage backPage, List <? extends ISampleItem> itemsList, String setId, String title, ModalWindow modal)
		{
		super(id);
		
		pageTitle = title;
		add(new Label("pageTitle", new PropertyModel<String>(this, "pageTitle")));
		
		add(new FeedbackPanel("feedback"));
		add(new SampleSelectorForm("sampleSelectorForm", backPage, itemsList, setId, modal));
		}
	
	
	class SampleSelectorForm extends Form
		{
		List<SelectableObject> selectableList; 
		boolean allSelected = false, filteredSet = false;
		WebMarkupContainer container; 
		String setIdType = "Batch Id", setId = "";
		
		
		public SampleSelectorForm(String id,  WebPage backPage, 
				List<? extends ISampleItem> itemsList, String setId, ModalWindow modal)
			{
			super(id);
			IndicatingAjaxLink saveLink; 
			
			setIdType = useCheckins ? "Batch Id" : "Experiment Id";
			this.setId = setId;
			
			container = new WebMarkupContainer("container");
			container.setOutputMarkupId(true);
			add(container);
			
			container.add(new Label("setIdType", new PropertyModel<String>(this, "setIdType")));
			container.add(new Label("setId", setId));
			
			container.add(new Label("actionLabel", new PropertyModel<String>(this, "actionLabel")));
			selectableList  = createSelectableList(itemsList);
			container.add(buildSelectionView("selectionListView", selectableList));
			if (modal == null)
				
				container.add(new AjaxBackButton("backButton",  backPage));
			else
				container.add(new AjaxCancelLink("backButton", modal));
			
			container.add(buildSelectAllCheck("selectAll"));
			container.add(saveLink = buildSubmitLink("submitLink"));
			container.add(new AjaxCheckBox("filteredSet", new PropertyModel<Boolean>(this, "filteredSet"))
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target) { target.add(container);  }
				
				@Override
				public boolean isVisible() { return filterLabel != null; }
				});
			
			
			container.add(new AjaxCheckBox("unfilteredSet", new PropertyModel<Boolean>(this, "unfilteredSet"))
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target) { target.add(container); }
				
				@Override
				public boolean isVisible() { return filterLabel != null; }
				});
			
			container.add(new Label("filterLabel", new PropertyModel<String>(this, "filterLabel"))
				{
				@Override
				public boolean isVisible() { return filterLabel != null; }
				});
			
			saveLink.setOutputMarkupId(true);
			add(container);
			}
		
		
		public IndicatingAjaxLink buildSubmitLink(String id)
			{
			// issue 39
			return new IndicatingAjaxLink <Void>(id)
				{
				@Override
				public boolean isEnabled()
					{
					return true; //(getAllSelected() != null && getAllSelected().size() > 0);
					}
				
				@Override
				public void onClick(AjaxRequestTarget target)
					{
					doSubmit(target, selectableList);
					}
				
				@Override
				protected void onComponentTag(ComponentTag tag)
					{
					super.onComponentTag(tag);
					String label = getButtonLabel();
					tag.put("value", label);
					}	
				 };
			}
		
		ListView buildSelectionView(String id, List<SelectableObject> selectionList)
			{
			return new ListView(id, new PropertyModel<List<SelectableObject>>(this, "selectableList"))
				{
				@Override
				protected void populateItem(ListItem item)
					{
					SelectableObject entry = (SelectableObject) item.getModelObject();
					ISampleItem sItem;
					
					sItem = (Sample) entry.getSelectionObject();
					
					item.add(new Label("sampleId", new PropertyModel<String> (sItem, "sampleId")));
					item.add(new AjaxCheckBox("selectSample", new PropertyModel<Boolean>(entry, "selected"))
						{
						@Override
						protected void onUpdate(AjaxRequestTarget target) { /*target.add(saveButton); */  }
						});
					
					item.add(OddEvenAttributeModifier.create(item));
					}
					
				};
			}
		
		
		public AjaxCheckBox buildSelectAllCheck(String id)
			{
			AjaxCheckBox check = new AjaxCheckBox(id, new PropertyModel<Boolean>(this, "allSelected"))
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target) 
					{
					for (SelectableObject object : getSelectableList())
						{
						object.setSelected(getAllSelected());
						}
					//assaySelection.updateSelectionForAll(assaySelection.getAllSelected());
					target.add(container);
					} 
				};
			
			check.setOutputMarkupId(true);
			
			return check;
			}
		
		public List<SelectableObject> createSelectableList(List<? extends ISampleItem> itemList)
			{
			List<SelectableObject> list = new ArrayList<SelectableObject>();
			
			for (ISampleItem item : itemList)
				{
				list.add(new SelectableObject(item));
				}
			
			return list;
			}
		
		public List<SelectableObject> getSelectableList()
			{
			return this.selectableList;
			}


		public boolean getAllSelected()
			{
			return allSelected;
			}


		public void setAllSelected(boolean allSelected)
			{
			this.allSelected = allSelected;
			}


		public boolean isUnfilteredSet()
			{
			return !filteredSet;
			}


		public void setUnfilteredSet(boolean filteredSet)
			{
			this.filteredSet = !filteredSet;
			}
		
		
		public boolean isFilteredSet()
			{
			return filteredSet;
			}


		public void setFilteredSet(boolean filteredSet)
			{
			this.filteredSet = filteredSet;
			}


		public void setSelectableList(List<SelectableObject> selectableList)
			{
			this.selectableList = selectableList;
			}


		public String getSetIdType()
			{
			return setIdType;
			}


		public void setSetIdType(String setIdType)
			{
			this.setIdType = setIdType;
			}
		
		
		public String getFilterLabel()
			{
			return filterLabel;
			}


		public void setFilterLabel(String label)
			{
			filterLabel = label;
			}
		
		public String getActionLabel()
			{
			return actionLabel;
			}
		
		public void setActionLabel(String label)
			{
			actionLabel = label;
			}
		}
	

	public String getPageTitle()
		{
		return pageTitle;
		}


	public void setPageTitle(String pageTitle)
		{
		this.pageTitle = pageTitle;
		}
	
	
	
	public String getFilterLabel()
		{
		return filterLabel;
		}


	public void setFilterLabel(String filterLabel)
		{
		this.filterLabel = filterLabel;
		}
	


	public String getActionLabel()
		{
		return actionLabel;
		}


	public void setActionLabel(String actionLabel)
		{
		this.actionLabel = actionLabel;
		}

	public String getButtonLabel()
		{
		return buttonLabel;
		}


	public void setButtonLabel(String buttonLabel)
		{
		this.buttonLabel = buttonLabel;
		}


	public abstract void doSubmit(AjaxRequestTarget target, List<SelectableObject> list);
	}

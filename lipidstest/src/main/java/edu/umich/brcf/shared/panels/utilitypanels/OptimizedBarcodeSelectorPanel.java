////////////////////////////////////////////////////
// SampleSelectorPanel.java
// Written by Jan Wigginton, Apr 14, 2016
////////////////////////////////////////////////////

package edu.umich.brcf.shared.panels.utilitypanels;

import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
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
import org.apache.wicket.model.PropertyModel;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.interfaces.ISampleItem;
import edu.umich.brcf.shared.util.structures.Pair;
import edu.umich.brcf.shared.util.structures.SelectableObject;
import edu.umich.brcf.shared.util.utilpackages.ListUtils;
import edu.umich.brcf.shared.util.widgets.AjaxBackButton;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;


public abstract class OptimizedBarcodeSelectorPanel extends Panel
	{
	boolean useCheckins = false, usePairs = false, newSamples = false, distributeSamples = false, filteredSet = true;
	String pageTitle = "Select Samples", filterLabel = "In Prep Cue", actionLabel = "Show Prep Sheet";
	String buttonLabel = "View Details", criteriaLabel = "Id";
	String header1Label = "Sample ID", header2Label = "";
	
	public OptimizedBarcodeSelectorPanel(String id) { super(id); }
	
	
	public  OptimizedBarcodeSelectorPanel(String id, WebPage backPage, List <? extends ISampleItem> itemsList, String setId, String title, ModalWindow modal)
		{
		this(id, backPage, itemsList, setId, title, modal, false);
		}
	
	
	public  OptimizedBarcodeSelectorPanel(String id, WebPage backPage, List <? extends ISampleItem> itemsList, String setId, String title, ModalWindow modal, boolean areNewSamples)
		{
		this(id, backPage, itemsList, setId, title, modal, areNewSamples, false);
		}
	
	public  OptimizedBarcodeSelectorPanel(String id, WebPage backPage, List <? extends ISampleItem> itemsList, String setId, String title, ModalWindow modal, boolean areNewSamples,
			boolean areDistributeSamples)
		{
		super(id);
		
		useCheckins =  false; //(itemsList.get(0) instanceof SampleCheckin2);
		usePairs = (itemsList.get(0) instanceof Pair);
		pageTitle = title;
		newSamples = areNewSamples;
		distributeSamples = areDistributeSamples;
		
		add(new Label("pageTitle", new PropertyModel<String>(this, "pageTitle")));
		add(new FeedbackPanel("feedback"));
		add(new BarcodeSelectorForm("sampleSelectorForm", backPage, itemsList, setId, modal));
		}
	
	class BarcodeSelectorForm extends Form
		{
		List<SelectableObject> selectableList = null; 
		boolean allSelected = false;
		WebMarkupContainer container; 
		String setIdType = "Batch Id", setId = "";
		AjaxButton saveButton;  // issue 464
		
		
		public BarcodeSelectorForm(String id,  WebPage backPage, final List<? extends ISampleItem> itemsList, 
				String setId, ModalWindow modal)
			{
			super(id);
			
			setIdType = useCheckins ? "Batch Id" : "Experiment Id";
			this.setId = setId;
			selectableList  = createSelectableList(itemsList);				
			container = new WebMarkupContainer("container");
			container.setEscapeModelStrings(true);
			container.setOutputMarkupId(true);
			add(container);
			container.setEscapeModelStrings(false); // issue 120
			container.add(new Label("criteriaLabel", new PropertyModel<String>(this, "criteriaLabel")));			
			container.add(new Label("setIdType", new PropertyModel<String>(this, "setIdType")));
			container.add(new Label("setId", setId));			
			container.add(new Label("actionLabel", new PropertyModel<String>(this, "actionLabel")));
			// issue 120
			container.add(buildSelectionView("selectionListView", selectableList).setEscapeModelStrings(false));		    
			if (modal == null)
				container.add(new AjaxBackButton("backButton",  backPage));
			else
				container.add(new AjaxCancelLink("backButton", modal));			
			container.setEscapeModelStrings(false);		
			container.add(buildSelectAllCheck("selectAll"));
			container.add(saveButton = buildSubmitButton("submitButton")); // issue 464
			container.add(new AjaxCheckBox("filteredSet", new PropertyModel<Boolean>(this, "filteredSet"))
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target) 
					{
					selectableList = createSelectableList(itemsList);
					target.add(container);  
					}				
				@Override
				public boolean isVisible() { return filterLabel != null; }
				});
						
			container.add(new AjaxCheckBox("unfilteredSet", new PropertyModel<Boolean>(this, "unfilteredSet"))
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target) 
					{
					selectableList = createSelectableList(itemsList);
					target.add(container); 
					}				
				@Override
				public boolean isVisible() { return filterLabel != null; }
				});			
			container.add(new Label("header1Label", new PropertyModel<String>(this, "header1Label")).setEscapeModelStrings(false)); // issue 120
			container.add(new Label("header2Label", new PropertyModel<String>(this, "header2Label")).setEscapeModelStrings(false)); // issue 120
			container.add(new Label("filterLabel", new PropertyModel<String>(this, "filterLabel"))
				{
				@Override
				public boolean isVisible() { return filterLabel != null; }
				}).setEscapeModelStrings(false); // issue 120
			
			saveButton.setOutputMarkupId(true);
			saveButton.setEscapeModelStrings(false);
			add(container);
			}
				
		public String getHeader1Label()
			{
			return header1Label;
			}
		
		public String getHeader2Label()
			{
			return header2Label;
			}
		
		public void setHeader1Label(String h1)
			{
			header1Label = h1;
			}

		public void setHeader2Label(String h2)
			{
			header2Label = h2;
			}
			
		public AjaxButton buildSubmitButton(String id) //issue 464
			{
			this.setEscapeModelStrings(false); // issue 120
			return new AjaxButton(id)
				{
				@Override
				public boolean isEnabled()
					{
					return somethingIsSelected();   //ListUtils.isNonEmpty(selectableList);
					}
				
				@Override
				public void onSubmit(AjaxRequestTarget target)
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
				// issue 39
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
					Pair sItem = (Pair) entry.getSelectionObject();
					item.add(new Label("sampleId", new PropertyModel<String> (sItem, "sampleId")).setEscapeModelStrings(false));
					item.add(new Label("chearSampleId", new PropertyModel<String>(sItem, "value")));					
					item.add(new AjaxCheckBox("selectSample", new PropertyModel<Boolean>(entry, "selected"))
						{
						@Override
						protected void onUpdate(AjaxRequestTarget target) { target.add(saveButton); } // issue 464
						});
					item.setEscapeModelStrings(false); // issue 120
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
						object.setSelected(getAllSelected());
			
					target.add(container);
					} 
				};			
			check.setOutputMarkupId(true);
			check.setEscapeModelStrings(false); // issue 120
			return check;
			}
			
		public List<SelectableObject> createSelectableList(List<? extends ISampleItem> itemList)
			{		
			if (selectableList != null)
				return selectableList;			
			List<SelectableObject> list = new ArrayList<SelectableObject>();			
			for (ISampleItem item : itemList)
				{
				if (selectableList == null)
					{
					list.add(new SelectableObject(item));
					continue;
					}
				}	
			return list;
			}
			
		public boolean somethingIsSelected()
			{
			if (!ListUtils.isNonEmpty(selectableList))
				return false;
			
			for (SelectableObject obj : getSelectableList())
				if (obj.isSelected()) 
					return true;
			
			return false;
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

		public void setUnfilteredSet(boolean f)
			{
			filteredSet = !f;
			}
			
		public boolean isFilteredSet()
			{
			return filteredSet;
			}

		public void setFilteredSet(boolean f)
			{
			filteredSet = f;
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
		
		public String getCriteriaLabel()
			{
			return criteriaLabel;
			}

		public void setCriteriaLabel(String cl)
			{
			criteriaLabel = cl;
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
		
	public boolean isFilteredSet()
		{
		return filteredSet;
		}

	public void setFilteredSet(boolean filteredSet)
		{
		this.filteredSet = filteredSet;
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
	
	public String getCriteriaLabel()
		{
		return criteriaLabel;
		}


	public String getHeader1Label()
		{
		return header1Label;
		}


	public String getHeader2Label()
		{
		return header2Label;
		}

	public void setCriteriaLabel(String criteriaLabel)
		{
		this.criteriaLabel = criteriaLabel;
		}

	public void setHeader1Label(String header1Label)
		{
		this.header1Label = header1Label;
		}

	public void setHeader2Label(String header2Label)
		{
		this.header2Label = header2Label;
		}

	public boolean isNewSamples()
		{
		return newSamples;
		}

	public void setNewSamples(boolean newSamples)
		{
		this.newSamples = newSamples;
		}

	public abstract void doSubmit(AjaxRequestTarget target, List<SelectableObject> list);
	}


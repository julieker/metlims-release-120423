////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  AddControlsPanel.java
//  Written by Jan Wigginton
//  February 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.io.StringUtils;


public class AddControlsPanel extends Panel
	{
	WorklistSimple originalWorklist;
	ModalWindow modal1;

	// TO DO Clean up all the reference here to point to worklist object's list
	List<WorklistControlGroup> controlGroupsList;
	ListView<WorklistControlGroup> controlGroupsListView;

	List<String> availableDirections = Arrays.asList(new String[] { "Before", "After" });
	List<String> availableQuantities = Arrays.asList(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14","15" });
	IndicatingAjaxLink buildButton, clearButton;
	AjaxLink addButton, deleteButton;

	DropDownChoice<String> controlTypeDrop, directionDrop, quantityDrop, relatedSampleDrop;

	private Boolean needsRebuild = false,  controlTypeChangeWarningShownOnce = false, controlTypeChangeWarningShownTwice = false;

	WebMarkupContainer container = new WebMarkupContainer("container");
	List<WebMarkupContainer> sibContainers = new ArrayList<WebMarkupContainer>();
    
	public AddControlsPanel(String id, WorklistSimple worklist)
		{
		super(id);

		modal1 = ModalCreator.createModalWindow("modal1", 800, 320);
		add(modal1);

		originalWorklist = worklist;
		originalWorklist.initializeControls();
			
		container.setOutputMarkupId(true);
		container.setOutputMarkupPlaceholderTag(true);
		add(container);

		container.add(controlGroupsListView = new ListView("controlGroupsListView", new PropertyModel(originalWorklist, "controlGroupsList"))
			{
			public void populateItem(ListItem listItem)
				{
				final WorklistControlGroup item = (WorklistControlGroup) listItem.getModelObject();

				listItem.add(deleteButton = buildDeleteButton("deleteButton", item, container));
				listItem.add(addButton = buildAddButton("addButton", item, container));

				listItem.add(controlTypeDrop = buildControlTypeDropdown("controlTypeDrop", item, "controlType"));
				listItem.add(quantityDrop = buildQuantityDropdown("quantityDrop", item, "quantity"));
				listItem.add(directionDrop = buildDirectionDropdown("directionDrop", item, "direction"));
				listItem.add(relatedSampleDrop = buildRelatedSampleDropdown("relatedSampleDrop", item, "relatedSample"));

				listItem.add(buildLinkToInfoModal("questionButton", item, modal1));
				listItem.add(buildButton = buildBuildButton("buildButton", item, container));
				listItem.add(clearButton = buildClearButton("clearButton", item, container));
				}
			});

		controlGroupsListView.setOutputMarkupId(true);
		}

	public WebMarkupContainer getContainer()
		{
		return container;
		}

	
	private Label buildDisappearingLabel(String id, final WorklistControlGroup item, String labelText)
		{
		return new Label(id, new Model(labelText + ":"))
			{
			@Override
			public boolean isVisible() { return (item == controlGroupsList.get(0)); }
			};
		}

	
	private DropDownChoice buildRelatedSampleDropdown(final String id, final WorklistControlGroup item, String propertyName)
		{
		DropDownChoice drp = new DropDownChoice(id, new PropertyModel(item, propertyName), new LoadableDetachableModel<List<String>>()
			{
			@Override
			protected List<String> load() { return originalWorklist.getSampleNamesArrayWithoutGroup(item); }
			})
			{
			@Override
			public boolean isEnabled()
				{
				if (!originalWorklist.getOpenForUpdates())
					return false;

				return originalWorklist.getItems().size() > 0;
				}
			};

		drp.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForSampleDrop", item));
		return drp;
		}

	
	private DropDownChoice buildControlTypeDropdown(final String id, final WorklistControlGroup item, String propertyName)
		{
		DropDownChoice drp = new DropDownChoice(id, new PropertyModel(item, propertyName), new LoadableDetachableModel<List<String>>()
			{
			@Override
			protected List<String> load() { return originalWorklist.getControlIds(); }
			}) 
			{
			private Boolean isAlreadyInitialized = false;

			public boolean isEnabled()
				{
				if (!originalWorklist.getOpenForUpdates()) return false;

				return originalWorklist.getItems().size() > 0;
				}

			public Boolean getIsAlreadyInitialized() { return isAlreadyInitialized; }

			public void setIsAlreadyInitialized(Boolean isAlreadyInitialized) { this.isAlreadyInitialized = isAlreadyInitialized; }
			};

		drp.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForControlDrop", item));
		return drp;
		}

	
	private DropDownChoice buildQuantityDropdown(final String id, final WorklistControlGroup item, String propertyName)
		{
		DropDownChoice drp = new DropDownChoice(id, new PropertyModel(item, propertyName), new LoadableDetachableModel<List<String>>()
			{
			@Override
			protected List<String> load() { return availableQuantities; }
			})
				{
				public boolean isEnabled()
					{
					if (!originalWorklist.getOpenForUpdates()) return false;

					return originalWorklist.getItems().size() > 0;
					}
				};

		drp.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForQuantityDrop", item));
		return drp;
		}

	
	private DropDownChoice buildDirectionDropdown(final String id, final WorklistControlGroup item, String propertyName)
		{
		DropDownChoice drp = new DropDownChoice(id, new PropertyModel(item, propertyName), new LoadableDetachableModel<List<String>>()
			{
			@Override
			protected List<String> load() { return availableDirections; }
			})
			{
			public boolean isEnabled()
				{
				if (!originalWorklist.getOpenForUpdates())
					return false;

				return originalWorklist.getItems().size() > 0;
				}
			};

		drp.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForDirectionDrop", item));
		return drp;
		}

	// issue 22
	private int getTotalNullControlTypes(List<WorklistControlGroup> controlGroupList) 
		{
		int totNull = 0;
		for (WorklistControlGroup grp : controlGroupList)
			{
			if (grp.getControlType() == null) // issue 22
				totNull++;
			}
		return totNull + 1;
		}
		
	// issue 17
	private boolean doBothQCMPandMPExist (WorklistSimple originalWorklist)
	    {
		boolean MPExists = false;
		boolean QCMPExists = false;
		for (WorklistControlGroup grp : originalWorklist.getControlGroupsList())
			{
			if (grp.getControlType() != null) // issue 22
				{
				if (grp.getControlType().indexOf("CS00000MP") > -1 && (!MPExists))
					MPExists = true;
				if (grp.getControlType().indexOf("CS000QCMP") > -1 && (!QCMPExists))
					QCMPExists = true;
				if (MPExists && QCMPExists)
					return true;
				}
			}
		return false;
	    }
	private AjaxLink buildDeleteButton(String id, final WorklistControlGroup item, final WebMarkupContainer container)
		{
		return new AjaxLink(id)
			{
			public boolean isEnabled() { return originalWorklist.getOpenForUpdates(); }

			@Override
			public void onClick(AjaxRequestTarget target)
				{
				// issue 17	
				if (originalWorklist.countGroups(true) > 1)
					{	
					CountPair countPair = originalWorklist.getLargestControlTypeTotal();
					// issue 16
		        	// issue 19
					if (countPair.getCount() - item.getIntQuantity() > 99)
			        	{
		        		target.appendJavaScript(StringUtils.makeAlertMessage("The control type:" + countPair.getTag() + " will have " + (countPair.getCount() - item.getIntQuantity()) + " entries. Please limit this to " + originalWorklist.getLimitNumberControls() + " before deleting. "));
		        		return;	
			        	}			
					originalWorklist.deleteControlItem(item);
					if (! doBothQCMPandMPExist(originalWorklist))
						originalWorklist.setBothQCMPandMP(false);
					originalWorklist.rebuildEverything();
					target.appendJavaScript("alert('After deleting a control group, please verify that remaining control groups still"
							+ " have a valid insertion point selected.  If not, select a new insertion point (for each group missing one) and click Update Controls to refresh your worklist');");
					} 
				else
					originalWorklist.clearControlGroups();

				originalWorklist.updateSampleNamesArray();
				refreshPage(target);
				}
			@Override // issue 464
			public MarkupContainer setDefaultModel(IModel model) 
			    {
				// TODO Auto-generated method stub
				return this;
			    }
			};
		}

	private AjaxLink buildAddButton(String id, final WorklistControlGroup item,
			final WebMarkupContainer container)
		{
		return new AjaxLink(id)
			{
			public boolean isEnabled()
				{
				if (!originalWorklist.getOpenForUpdates()) { return false; }
								
				return !StringUtils.isEmptyOrNull(item.getControlType()) && !item.getQuantity().equals("0")
						&& !item.getDirection().equals("") && !StringUtils.isEmptyOrNull(item.getRelatedSample());
				}
			@Override
			public void onClick(AjaxRequestTarget target)
				{
			  	// issue 22
	        	Map<String, Integer> controlTypeMap = originalWorklist.buildControlTypeMap();
	        	// issue 22
	        	int numberDistinctControls = controlTypeMap.size()  ;
	        	int numberNullControls = getTotalNullControlTypes(controlGroupsList);
	        	if ( numberDistinctControls + numberNullControls - (numberNullControls > 1 ? 1 : 0 ) > originalWorklist.getMaxItemsAsInt())
	        	    {		        		
	        		target.appendJavaScript(StringUtils.makeAlertMessage("By adding a control there will be: " + (numberDistinctControls + numberNullControls  - (numberNullControls > 1 ? 1 : 0 )) + " total controls.  Please delete controls before adding to bring the number down to: " + originalWorklist.getMaxItemsAsInt() +  " or less") );
	        		return;
	        	    }		
				originalWorklist.addControlGroup();
				target.add(container);
				}
			
			@Override // issue 464
			public MarkupContainer setDefaultModel(IModel model) 
			    {
				// TODO Auto-generated method stub
				return this;
			    }
			};
		}

	
	private Label buildTopLabel(String id, String label, final WorklistControlGroup item)
		{
		return new Label(id, label)
			{
			@Override
			public boolean isVisible() { return (item == controlGroupsList.get(0)); }
		
			@Override
			protected void onComponentTag(ComponentTag tag)
	    		{
	    		super.onComponentTag(tag);
	    		if (!(item == controlGroupsList.get(0)))
	    				tag.put("style", "display : none; height : 0px");
	    		}	
			};
		}
	
	
	private AjaxLink buildLinkToInfoModal(final String linkID, final WorklistControlGroup item, final ModalWindow modal1)
		{
		return new AjaxLink(linkID)
			{
			public boolean isEnabled() { return (originalWorklist.getOpenForUpdates()); }
			
			public boolean isVisible() { return (item == controlGroupsList.get(0) && originalWorklist.isPlatformChosenAs("agilent")); }

			@Override
			public void onClick(final AjaxRequestTarget target)
				{
				modal1.setPageCreator(new ModalWindow.PageCreator()
					{
					public Page createPage()
						{
						return ((Page) (new ControlInfoPage(getPage(), modal1)));
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

	
	private IndicatingAjaxLink buildBuildButton(String id, final WorklistControlGroup item, final WebMarkupContainer container)
		{
		return new IndicatingAjaxLink(id)
			{
			public boolean isVisible()
				{
				if (!originalWorklist.getOpenForUpdates()) return false;

				return item == controlGroupsList.get(controlGroupsList.size() - 1);
				}

			public boolean isEnabled()
				{
				if (!originalWorklist.getOpenForUpdates()) return false;

				// issue 285
				//if (originalWorklist.countGroups(true) > 100) return false;
				
				boolean enabled = true;
				for (int i = 0; i < originalWorklist.countGroups(true); i++)
					{
					WorklistControlGroup curr = originalWorklist.getControlGroup(i);

					boolean first_blank = StringUtils.isEmptyOrNull(curr.getControlType()); 
					boolean second_blank = StringUtils.isEmptyOrNull(curr.getDirection()); 
					boolean third_blank = StringUtils.isEmptyOrNull(curr.getRelatedSample());
					boolean fourth_blank = curr.getQuantity().toString().equals("");

					enabled &= (!first_blank && !second_blank && !third_blank && !fourth_blank);
					if (!needsRebuild) return false;
					}

				return enabled;
				}
			@Override // issue 464
			public MarkupContainer setDefaultModel(IModel model) 
			    {
				// TODO Auto-generated method stub
				return this;
			    }

			@Override
			protected void onComponentTag(final ComponentTag tag)
				{
				super.onComponentTag(tag);
				if (originalWorklist.countGroups(true) > 1)
					tag.put("value", "Update Controls");
				}

			@Override
			public void onClick(AjaxRequestTarget target)
				{
				if (! doBothQCMPandMPExist(originalWorklist))
					originalWorklist.setBothQCMPandMP(false); // issue 17
	        	CountPair countPair = originalWorklist.getLargestControlTypeTotal();
	        	// issue 16
	        	// issue 19
		        	if (countPair.getCount() > 99)
			        	{
		        		target.appendJavaScript(StringUtils.makeAlertMessage("The control type:" + countPair.getTag() + " has " + countPair.getCount() + " entries. Please limit this to " + originalWorklist.getLimitNumberControls()));
		        		return;	
			        	}
		          	// issue 404 issue 8
		        	Map<String, Integer> controlTypeMap = originalWorklist.buildControlTypeMap();
		        	int numberDistinctControls = controlTypeMap.size()  ;
		        	if ( numberDistinctControls > originalWorklist.getMaxItemsAsInt())
		        	    {
		        		target.appendJavaScript(StringUtils.makeAlertMessage("There are currently: " + numberDistinctControls + " total User Defined and standard controls.  Please add fewer standard controls to keep this number at " + originalWorklist.getMaxItemsAsInt() +  " or less") );
		        		return;
		        	    }
				originalWorklist.rebuildEverything();
				// issue 426
				String circularSample = originalWorklist.isThereCircular();
				if (circularSample != null)
					target.appendJavaScript("alert('There is a circular relationship "
					+ " Starting with sample: " +  circularSample +  "');");
				originalWorklist.updateSampleNamesArray();
				refreshPage(target);
				}
			};
		}

	
	public void addSibContainer(WebMarkupContainer c)
		{
		sibContainers.add(c);
		}

	
	private IndicatingAjaxLink buildClearButton(String id, final WorklistControlGroup item, final WebMarkupContainer container)
		{
		return new IndicatingAjaxLink(id)
			{
			public boolean isVisible()
				{
				return item == originalWorklist.getControlGroup(originalWorklist.countGroups(true) - 1); // controlGroupsList.get(controlGroupsList.size()
				}

			public boolean isEnabled()
				{
				return true; // return item != sampleGroupsList.get(0);
				}

			@Override
			public void onClick(AjaxRequestTarget target)
				{
				originalWorklist.clearControlGroups();
				originalWorklist.clearOutPoolIDDAControls();// issue 11
				originalWorklist.updateSampleNamesArray();
				originalWorklist.setOpenForUpdates(true);
				originalWorklist.updatePlatePositions(); // issue 417 issue 409
				refreshPage(target);
				}
			@Override // issue 464
			public MarkupContainer setDefaultModel(IModel model) 
			    {
				// TODO Auto-generated method stub
				return this;
			    }
			};
		}
	

	private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior( final String event, final String response, final WorklistControlGroup item)
		{
		return new AjaxFormComponentUpdatingBehavior(event)
			{
			@Override
			protected void onUpdate(AjaxRequestTarget target)
				{
				switch (response)
					{
					case "updateForControlDrop":
					
						if (!StringUtils.isEmptyOrNull(item.getRelatedSample()) && controlTypeChangeWarningShownTwice == false)
							{
							target.appendJavaScript("alert('If you change a control group type, you may need to rebuild your worklist twice -- once to update the control type"
									+ " and a second time to reselect insertion points that no longer make sense.');");

							if (controlTypeChangeWarningShownOnce) controlTypeChangeWarningShownTwice = true;

							controlTypeChangeWarningShownOnce = true;
							}

					case "updateForDirectionDrop":
					case "updateForSampleDrop":
					case "updateForQuantityDrop":
						needsRebuild = true;
						break;
					}

				target.add(buildButton);
				}
			};
		}

	private void refreshPage(AjaxRequestTarget target)
		{
		originalWorklist.updateIndices();

		if (sibContainers != null)
			for (int i = 0; i < sibContainers.size(); i++)
				target.add(sibContainers.get(i));

		if (container != null)
			target.add(container);
		}
	}




/////////////////////////  CODE SCRAP ////////////////////////////////////
/*
 * 
 * private Select buildControlTypeDropWithSections(final String id, String propertyName)
		{
		SelectOption opt1, opt2;
		Select languages = new Select(id, new PropertyModel<String>(this, propertyName));
		languages.add(opt1 = new SelectOption<String>("framework1", new Model<String>("Wicket")));
		languages.add(opt2 = new SelectOption<String>("framework2", new Model<String>("Spring MVC")));
		languages.add(new SelectOption<String>("framework3", new Model<String>("JSF 2.0")));
		languages.add(new SelectOption<String>("Script1", new Model<String>("jQuery")));
		languages.add(new SelectOption<String>("Script2", new Model<String>("prototype")));
		
		opt1.setOutputMarkupId(true);
		opt2.setOutputMarkupId(true);
		opt1.setVisible(false);
		opt2.setVisible(false);
		return languages;
		}
	
 * 
 * 
 * 
SelectOptions<String> fruitOptions = new SelectOptions<String>(
        "fruits", Arrays.asList(new String [] { "s1", "s2", "s3"}));
        

SelectOptions<Produce> vegetableOptions = new SelectOptions<Produce>(
            "vegetables",
            vegetableCollection, 
            new VegetableRenderer());

Select select = new Select("produceSelect", 
     new PropertyModel<Produce>(model, "favProduce"));
select.add(fruitOptions);
select.add(vegetableOptions);
}

*/
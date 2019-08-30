////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  AddSamplesPanel.java
//  Written by Jan Wigginton
//  February 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.domain.GCDerivatizationMethod;
import edu.umich.brcf.metabolomics.layers.dto.GCDerivatizationDTO;
import edu.umich.brcf.metabolomics.panels.lims.prep.EditGCPrep;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.ControlService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.layers.dto.ExperimentDTO;



public class AddSamplesPanel extends Panel
	{
	@SpringBean
	private AssayService assayService;

	@SpringBean
	ExperimentService experimentService; 

	@SpringBean
	private ControlService controlService;	
	WorklistSimple originalWorklist;
  
	List<WorklistSampleGroup> sampleGroupsList;
	ListView<WorklistSampleGroup> sampleGroupsListView;

	List<String> availableRandomizations = Arrays.asList(new String[] { "None", "Simple", "Custom" });
	String defaultAssay = "";

	DropDownChoice<String> selectedExperimentDrop, selectedAssayDrop, randomizationDrop;

	final WebMarkupContainer container = new WebMarkupContainer("container");
	final ModalWindow modal1 = ModalCreator.createModalWindow("modal1", 500, 300);
	List<WebMarkupContainer> sibContainers = new ArrayList<WebMarkupContainer>();

	List<String> availableExperiments = experimentService.expIdsByInceptionDate();
	List<String> absciexExperiments = experimentService.allExpIdsForAbsciex();
	List<String> agilentExperiments = experimentService.allExpIdsForAgilent();
	
	List<String> availableAssays;

	boolean excludedSamplesWarningGiven = false, secondBuild = false, buildWarningGiven = false;

    
	public AddSamplesPanel(String id)
		{		
		super(id);
		}

	public AddSamplesPanel(String id, WorklistSimple worklist)
		{
		super(id);	
		
		originalWorklist = worklist;
		add(modal1);
		sampleGroupsList = originalWorklist.getSampleGroupsList();
		for (int i = 0; i < 1; i++)
			sampleGroupsList.add(new WorklistSampleGroup(getDefaultAssay(), originalWorklist));
		container.setOutputMarkupId(true);
		container.setOutputMarkupPlaceholderTag(true);
		add(container);
		container.add(sampleGroupsListView = new ListView("sampleGroupsListView", new PropertyModel(worklist, "sampleGroupsList"))
			{
			public void populateItem(ListItem listItem)
				{
				WorklistSampleGroup item = (WorklistSampleGroup) listItem.getModelObject();
				listItem.add(selectedExperimentDrop = buildExperimentDropdown("experimentDropdown", item, "experimentId"));
				listItem.add(buildAssayDropdown("assayDropdown", item, "assayType"));
				listItem.add(randomizationDrop = buildRandomizationDropdown( "randomizationDropdown", item, "randomizationType", availableRandomizations));
				listItem.add(buildLinkToRandomizationModal("runOrderButton", item, modal1));
				listItem.add(buildDeleteButton("deleteButton", item,container));
				listItem.add(buildAddButton("addButton", item, container));
				listItem.add(buildBuildButton("buildButton", item, container));
				listItem.add(buildClearButton("clearButton", item, container));
				listItem.add(buildRandomizationLabel("randomizationLabel"));
				}
			});
		this.sampleGroupsListView.setOutputMarkupId(true);
		setOutputMarkupId(true);		
		}

	
	private Label buildRandomizationLabel(String id)
		{
		return new Label(id, "Plate Order") // issue 388
			{
			public boolean isVisible()
				{
				// Issue 268
				return true;
				//return originalWorklist.isPlatformChosenAs("absciex");
				}
			};
		}

	private AjaxLink buildLinkToRandomizationModal(final String linkID, final WorklistSampleGroup item, final ModalWindow modal1)
		{		
		final String failMessage = "alert('WARNING :  Your custom randomization did not load.  Resetting randomization type to None')";
		// issue 387
		item.expRandom = ((MedWorksSession) Session.get()).getExpRand();
		modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
			{		
			@Override
			// Issue 268
			public void onClose(AjaxRequestTarget target)
				{	
				// issue 387
				item.expRandom = ((MedWorksSession) Session.get()).getExpRand();
				item.setIsRandomized(item.expRandom != null); 
				if (item.expRandom == null)
				    {
				    item.setRandomizationType("None");
				    target.appendJavaScript(failMessage);
				    }		
				target.add(modal1.getParent().getParent());
				target.add(modal1.getParent());					
				//refreshPage(target);
				refreshCurrentPage(target);
				}
			});
	
		
        // Issue 268
		// issue 464
		AjaxLink link = new AjaxLink(linkID) // issue 307
			{
			public boolean isEnabled() 
			    { 
				// issue 268
				return item.getRandomizationType().equals("Custom") && originalWorklist.getOpenForUpdates() && item.getAssayType() != null && item.expRandom ==  null; // issue 398 issue 268 
				}
			@Override
			public void onClick(final AjaxRequestTarget target)
				{
				item.setExpRandom(null);// issue 268 
				item.setIsRandomized(false);// issue 268 				
				modal1.setInitialWidth(550);
				modal1.setInitialHeight(175);
				modal1.setPageCreator(new ModalWindow.PageCreator()
					{
					public Page createPage()
						{
						// issue 268 
						return (new RandomizationLoaderPage(getPage(), item.getExperimentId(), modal1, originalWorklist)
              			    {
							@Override
							protected void saveRandomization(ExperimentRandomization gErRandomization)
								{					
								item.setExpRandom(gErRandomization);
								// issue 387
								((MedWorksSession) Session.get()).setExpRand(gErRandomization);
								}
              			    });
						}
					});               
				modal1.show(target);
				}
			@Override
			public MarkupContainer setDefaultModel(IModel arg0) {
				// TODO Auto-generated method stub
				return this;
			}
			};
		link.setOutputMarkupId(true);
		return link;
		}

	
	private DropDownChoice buildAssayDropdown(final String id, final WorklistSampleGroup item, String propertyName)
		{
		DropDownChoice drp = new DropDownChoice(id, new PropertyModel(item, propertyName), new LoadableDetachableModel<List<String>>()
			{
			@Override
			protected List<String> load()
				{
				if (availableAssays != null) return availableAssays;

				return new ArrayList<String>();
				}
			});
        // issue 464
		drp.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForAssayDrop", item));

		return drp;
		}

	// issue 307
	private DropDownChoice buildRandomizationDropdown(final String id, final WorklistSampleGroup item, String propertyName,
		List<String> choices)
		{
		DropDownChoice drp = new DropDownChoice(id, new PropertyModel(item, propertyName), choices)
			{
			public boolean isEnabled() { return (item != null && !StringUtils.isEmptyOrNull(item.getExperimentId())); }
			};

		drp.setOutputMarkupId(true);
		// Issue 464
		drp.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForRandomDrop", item));
		return drp;
		}

	
	private DropDownChoice buildExperimentDropdown(final String id, final WorklistSampleGroup item, String propertyName)
		{
		selectedExperimentDrop = new DropDownChoice(id, new PropertyModel(item, propertyName), new LoadableDetachableModel<List<String>>()
			{
			@Override
			protected List<String> load()
				{
				return originalWorklist.getSelectedPlatform().equals("absciex") ? absciexExperiments : agilentExperiments;
				}
			});
        // issue 464
		selectedExperimentDrop.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForExperimentDrop", item));
		return selectedExperimentDrop;
		}

	// issue 464
	private AjaxLink buildDeleteButton(String id, final WorklistSampleGroup item, final WebMarkupContainer container)
		{
		return new AjaxLink(id)
			{
			public boolean isEnabled() { return item != sampleGroupsList.get(0); }

			@Override
			public void onClick(AjaxRequestTarget target)
				{
				originalWorklist.deleteSampleItem(item);

				if (originalWorklist.countGroups(false) == 0)
					originalWorklist.addSampleGroup();

				target.add(container);
				}

			@Override
			public MarkupContainer setDefaultModel(IModel arg0) {
				// TODO Auto-generated method stub
				return (MarkupContainer) this.getDefaultModel();
			}
			};
		}

	// Issue 464
	private AjaxLink buildAddButton(String id, final WorklistSampleGroup item, final WebMarkupContainer container)
		{
		return new AjaxLink(id)
			{
			public boolean isEnabled()
				{
				return false;
				}

			@Override
			public void onClick(AjaxRequestTarget target)
				{
				originalWorklist.addSampleGroup();
				target.add(container);
				}

			@Override
			public MarkupContainer setDefaultModel(IModel arg0) {
				// TODO Auto-generated method stub
				return (MarkupContainer) this.getDefaultModel();
			}
			};
		}

	// issue 464
	private AjaxLink buildBuildButton(String id, final WorklistSampleGroup item, final WebMarkupContainer container)
		{
		return new IndicatingAjaxLink <Void> (id)
			{
			public boolean isVisible()
				{
				return item == sampleGroupsList
						.get(sampleGroupsList.size() - 1);
				}
			
			public boolean isEnabled()
				{
				boolean enabled = false;
				// issue 407
				if (!item.getRandomizationType().equals("None") &&  item.expRandom ==null && originalWorklist.getSelectedPlatform() .equalsIgnoreCase("agilent"))
					return false;
				if (originalWorklist.getSelectedPlatform() .equalsIgnoreCase("absciex"))
					if (StringUtils.isEmptyOrNull(originalWorklist.getSelectedInstrument()) || originalWorklist.getSelectedInstrument().equals("Unknown"))
						return false;
				for (WorklistSampleGroup curr : sampleGroupsList)
					{
					if (curr == null) continue;
					boolean second_blank = true, first_blank = StringUtils.isEmptyOrNull(curr.getExperimentId());
					if (curr.getAssayType()  != null)
						second_blank = StringUtils.isEmptyOrNull(curr.getAssayType().trim());
					enabled |= !first_blank && !second_blank;
					if (!first_blank && second_blank || first_blank && !second_blank)
						return false;				
					}
	
				return enabled;
				}

			public void onClick(AjaxRequestTarget target)
				{
				originalWorklist.rebuildEverything();
				int excludedCount = originalWorklist.countExcludedSamples();
				if (!buildWarningGiven && secondBuild && originalWorklist.getControlGroupsList() != null && originalWorklist.getControlGroupsList().size() > 1) 
					{
					secondBuild = false;
					buildWarningGiven = true;
					String msg =  "alert('Warning : Rebuilding the worklist will remove all controls and samples. Please submit again to confirm.')";
					target.appendJavaScript(msg); 
					return;
					}				
				if (excludedCount > 0 && !excludedSamplesWarningGiven)
					{
					String msg =  "alert('Warning : Some experiment samples were excluded from worklists. To reinclude them, update their status on the Assay Status panel under the Tracking tab.')";
					target.appendJavaScript(msg); 
					excludedSamplesWarningGiven = true;
					}
				originalWorklist.updateSampleNamesArray();
				WorklistSampleGroup curr = sampleGroupsList.get(0);
				refreshCurrentPage(target);// issue 464
				secondBuild = true;
				}

			protected void onComponentTag(final ComponentTag tag)
				{
				super.onComponentTag(tag);
				if (item == null || item.parent == null || StringUtils.isEmptyOrNull(item.parent.getSelectedInstrument())
					|| item.parent.getSelectedInstrument().equals("Unknown"))
						tag.put("title","Please select an instrument before building your worklist");
				}

			
			};
		}

	// issue 464
	private AjaxLink buildClearButton(String id, final WorklistSampleGroup item, final WebMarkupContainer container)
		{
		return new IndicatingAjaxLink(id)
			{
			@Override
			public boolean isVisible() { return item == sampleGroupsList.get(sampleGroupsList.size() - 1); }

			@Override
			public boolean isEnabled() { return true;  } //return item != sampleGroupsList.get(0); }

			@Override
			public void onClick(AjaxRequestTarget target)
				{
				originalWorklist.clearAll();
				originalWorklist.updateSampleNamesArray();
				secondBuild = false;
				refreshPage(target);
				}

			@Override
			public MarkupContainer setDefaultModel(IModel arg0) {
				// TODO Auto-generated method stub
				return (MarkupContainer) this.getDefaultModel();
			}
			};
		}

	
	private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(final String event, final String response,
	final WorklistSampleGroup item)
		{
		return new AjaxFormComponentUpdatingBehavior(event)
			{
			@Override
			protected void onUpdate(AjaxRequestTarget target)
				{
				switch (response)
					{
					case "updateForRandomDrop":
                        
						item.setIsRandomized(false);
						// issue 387
						((MedWorksSession) Session.get()).setExpRand(null);						
						if (originalWorklist != null) originalWorklist.clearAllItems();
	
						//issue 268
						//if (originalWorklist.getSelectedPlatform().equalsIgnoreCase("agilent") && item.getRandomizationType().equals("Custom"))
							//{
							//target.appendJavaScript("alert('Custom randomization has been disabled for Agilent platform.  If you would like to use this feature, please contact wiggie@umich.edu for an introduction.')");
							//item.setRandomizationType("None");
							//}
	
						if (item.getRandomizationType().equals("Simple"))
							{
							String expDescript = originalWorklist != null && !originalWorklist.getDefaultExperimentId().equals("") ? "for experiment "
									+ originalWorklist.getDefaultExperimentId()+ " " : "for this experiment ";
	
							target.appendJavaScript("alert('WARNING :  Samples " + expDescript
									+ "will be randomized. If your experiment requires multiple batches, please be sure to save and use this worklist for all of them.  If you re-randomize, you may run some samples twice and miss others entirely.')");
							}	
						item.setExpRandom(null);
						originalWorklist.clearControlGroups(); break;

					case "updateForAssayDrop":
						String val = item.getAssayType();
						clearOutMotorPacControls(); // issue 422
						clearOutPoolIDDAControls();// issue 432
						String[] tokens = val != null ? val.split("\\(") : null;
						item.setIsRandomized(false);
						originalWorklist.setOpenForUpdates(true); // issue 329
						item.setRandomizationType("None"); //issue 384
						// issue 387
						((MedWorksSession) Session.get()).setExpRand(null);
						if (originalWorklist != null) originalWorklist.clearAllItems();// issue 268
				        originalWorklist.clearControlGroups();// issue 268
						int target_token = tokens.length - 1;
						String token = (tokens != null && tokens.length > 1) ? tokens[target_token] : null;
						String aid = token != null ? token.replace(')', ' ').trim() : "Error";
						// TO PUT IN SYSTEM SETTING FOR ERROR CONTACT PERSON
						if (aid.equals("Error"))
							target.appendJavaScript("warning('Unable to determine assay name -- labelling output file with assay tag 'Error'.\n  "
									+ "Please e-mail wiggie@umich.edu to report this error')");
						originalWorklist.setDefaultAssayId(aid);

						break;

					case "updateForExperimentDrop":
						item.setIsRandomized(false);
						if (originalWorklist == null)
							return;
						// issue 387
						((MedWorksSession) Session.get()).setExpRand(null);
						clearOutMotorPacControls();// issue 422
						clearOutPoolIDDAControls();// issue 432
						item.setRandomizationType("None"); //issue 384
						String eid = item.getExperimentId();
						try
							{
							String platform = originalWorklist.getSelectedPlatform();
							ArrayList<String> newControls = (ArrayList<String>) controlService.allControlNamesAndIdsForPlatformAndExpId(eid, platform);
                            originalWorklist.setOpenForUpdates(true); // issue 329
							originalWorklist.rebuildForNewExperiment(eid, newControls);
							availableAssays = assayService.allAssayNamesForPlatformAndExpId(platform, eid);
							// issue 464
							item.setAssayType((availableAssays.size() == 1 ? availableAssays.get(0) : null));
							} 
						catch (Exception e)
							{
							target.appendJavaScript("alert('Experiment " + eid + " has missing information and cannot be accessed at this time');");
							}

						break;

					case "registerEvent":
						break;
					}

					refreshPage(target);
					}
			};
		}

	public WebMarkupContainer getContainer()
		{
		return container;
		}

	public void addSibContainer(WebMarkupContainer c)
		{
		sibContainers.add(c);
		}

	public String getDefaultAssay()
		{
		if (originalWorklist.getSelectedPlatform().equals("absciex"))
			defaultAssay = "Shotgun lipidomics (A004)";

		return defaultAssay;
		}

	
	public void setDefaultAssay(String s)
		{
		defaultAssay = s;
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

	// issue 464
		private void refreshCurrentPage(AjaxRequestTarget target)
			{
			originalWorklist.updateIndices();
		
			
		//	for (int i = 0; i < sibContainers.size(); i++)			  
			//		target.add(sibContainers.get(i)); 	
			if (container != null)
				target.add(container);		
			}
		
		
	public void reinitializeAssays()
		{
		if (availableAssays != null)
			{
			availableAssays.clear();
			availableAssays.add("Choose experiment first");
			}
		}
	
	// Issue 422 issue 427
	public void clearOutMotorPacControls ()
		{
		((MedWorksSession) Session.get()).setNGastroExercise(0);	
		((MedWorksSession) Session.get()).setNGastroSedentary(0);	
		((MedWorksSession) Session.get()).setNLiverExcercise(0);	
		((MedWorksSession) Session.get()).setNLiverSedentary(0);	
		((MedWorksSession) Session.get()).setNAdiposeExercise(0);	
		((MedWorksSession) Session.get()).setNAdiposeSedentary(0);	
		((MedWorksSession) Session.get()).setNPlasmaExercise(0);	
		((MedWorksSession) Session.get()).setNPlasmaSedentary(0);
		((MedWorksSession) Session.get()).setNRatPlasma(0);
		((MedWorksSession) Session.get()).setNRatA(0);
		((MedWorksSession) Session.get()).setNRatG(0);
		((MedWorksSession) Session.get()).setNRatL(0);
		((MedWorksSession) Session.get()).setNRatA(0);
		}
	
	// issue 432
	public void clearOutPoolIDDAControls ()
		{
		((MedWorksSession) Session.get()).setNBatchPoolsAfter(0);
		((MedWorksSession) Session.get()).setNBatchPoolsBefore(0);
		((MedWorksSession) Session.get()).setNMasterPoolsBefore(0);
		((MedWorksSession) Session.get()).setNMasterPoolsAfter(0);
		((MedWorksSession) Session.get()).setNCE10Reps(0);
		((MedWorksSession) Session.get()).setNCE20Reps(0);
		((MedWorksSession) Session.get()).setNCE40Reps(0);	
		}	
	}



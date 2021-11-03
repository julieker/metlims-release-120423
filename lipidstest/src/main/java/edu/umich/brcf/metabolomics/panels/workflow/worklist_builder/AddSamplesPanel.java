////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  AddSamplesPanel.java
//  Written by Jan Wigginton
//  February 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.domain.GCDerivatizationMethod;
import edu.umich.brcf.metabolomics.layers.dto.GCDerivatizationDTO;
import edu.umich.brcf.metabolomics.panels.lims.prep.EditGCPrep;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.ControlService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.panels.utilitypanels.ConfirmBox;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.utilpackages.ListUtils;
import edu.umich.brcf.shared.layers.dto.ExperimentDTO;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;
import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButtons;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogIcon;
import com.googlecode.wicket.jquery.ui.widget.dialog.InputDialog;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;
import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.jquery.ui.form.button.IndicatingAjaxButton;

public class AddSamplesPanel extends Panel
	{
	@SpringBean
	private AssayService assayService;

	@SpringBean
	ExperimentService experimentService; 
	
	@SpringBean
	SampleService sampleService; 

	@SpringBean
	private ControlService controlService;	
	WorklistSimple originalWorklist;
	ExperimentRandomization globalRand = null;
	// issue 46
	final Form<Void> form = new Form<Void>("form");
	final FeedbackPanel feedback = new JQueryFeedbackPanel("feedback");
	String buttonString = "";

	public final class AddSamplesRandomizationLoaderForm extends Form
	    {
		public AddSamplesRandomizationLoaderForm (String id)
			{
			super(id);			
			add(modal1);	
			
			}
	    }
	List<WorklistSampleGroup> sampleGroupsList;
	ListView<WorklistSampleGroup> sampleGroupsListView;

	List<String> availableRandomizations = Arrays.asList(new String[] { "None", "Simple", "Custom" });
	String defaultAssay = "";

	DropDownChoice<String> selectedExperimentDrop, selectedAssayDrop, randomizationDrop;

	final WebMarkupContainer container = new WebMarkupContainer("container");
	final ModalWindow modal1 = ModalCreator.createModalWindow("modal1", 500, 300);
	final ModalWindow modalRandom = ModalCreator.createModalWindow("modalRandoms", 500, 300); // issue 464
	List<WebMarkupContainer> sibContainers = new ArrayList<WebMarkupContainer>();

	List<String> availableExperiments = experimentService.expIdsByInceptionDate();
	List<String> absciexExperiments = experimentService.allExpIdsForAbsciex();
	List<String> agilentExperiments = experimentService.allExpIdsForAgilent();
	
	List<String> availableAssays;
	List<String> sampleInvalidIds = new ArrayList<String> ();
	boolean excludedSamplesWarningGiven = false, secondBuild = false, buildWarningGiven = false;
    String userDefinedSamplesLongStr = "";
    Model<String> userDefinedSamplesModel = Model.of("");
    
	public AddSamplesPanel(String id)
		{		
		super(id);
		}

	public AddSamplesPanel(String id, WorklistSimple worklist)
		{
		super(id);	
		// issue 46
		AddSamplesRandomizationLoaderForm arlf = new AddSamplesRandomizationLoaderForm ("addSamplesRandomizationForm");
		container.add(arlf);
		originalWorklist = worklist;		
		// issue 46
		final UserDefinedControlsDialog dialogUserDefinedControls = new UserDefinedControlsDialog("dialogUserDefinedControls", "Please cofirm", userDefinedSamplesModel) 
			{
			private static final long serialVersionUID = 1L;
			
			@Override
			public void onClick(AjaxRequestTarget target, DialogButton button)
			    {		
				if (button.toString().equals("Cancel"))
					{	
					buttonString = button.toString();
					globalRand = null;
					originalWorklist.getSampleGroup(0).setRandomizationType("None");
					target.appendJavaScript("alert('Randomization upload was cancelled.   Setting Randomization Type to none.');");
					target.add (container);
					super.onClick(target, button);
					}
				else if (button.toString().equals("Ok"))
				    {
					buttonString = button.toString();
					target.appendJavaScript("alert('Randomization upload was successful.');");
				    target.add (container);
				    super.onClick(target, button);
				    }
				else
					target.appendJavaScript("alert('Randomization upload was successful.');");
			        target.add (container);
					super.onClick(target, button); //will close the dialog
			    }
			@Override
			public boolean isDefaultCloseEventEnabled()
				{
				return true;
				}			
			@Override
			public boolean isEscapeCloseEventEnabled()
				{
				return true;
				}			
			@Override
			public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
				// TODO Auto-generated method stub
				handler.add(feedback);
				if (buttonString.equals(""))
					{
					AjaxRequestTarget target = (AjaxRequestTarget) handler;
					globalRand = null;
					originalWorklist.getSampleGroup(0).setRandomizationType("None");
					target.appendJavaScript("alert('Randomization upload was cancelled.   Setting Randomization Type to none.');");
					target.add (container);
					}
				}			
			@Override
			protected void onOpen(IPartialPageRequestHandler handler)
				{ 
				buttonString = "";	
				}
			@Override
			public void onConfigure(JQueryBehavior behavior)
			    {
				// class options //
				behavior.setOption("autoOpen", false);
				behavior.setOption("modal", this.isModal());
				behavior.setOption("resizable", this.isResizable());
				behavior.setOption("width", 850);
				behavior.setOption("title", Options.asString(this.getTitle().getObject()));
				//behavior.setOption("height", 900);
			    behavior.setOption("autofocus", false);
			    }		
			};
			
		// issue 46
		final UploadDialog dialogUpload = new UploadDialog("dialogUpload", "Upload file", originalWorklist) 
		    { 
		    @Override
			public void onClick(AjaxRequestTarget target, DialogButton button)
				{
		    	if (button.toString().equals("Upload!"))
			        {
		    		if (sampleInvalidIds != null)
		    		     sampleInvalidIds.clear();
		    		sampleInvalidIds=this.doUpload(target);	
	    	        if (sampleInvalidIds == null)
	    	        	return;
			        globalRand = this.gEr;
			        originalWorklist.getSampleGroup(0).setExpRandom(globalRand);
			        if (sampleInvalidIds.size() == 0)
			            {
				        if (this.gEr != null)
					        target.appendJavaScript("alert('Randomization upload was successful!');");
					    else 
					        {
						    target.appendJavaScript("alert('Randomization upload was not successful!  Setting Randomization Type to none');");				        
						    originalWorklist.getSampleGroup(0).setRandomizationType("None");
						    super.close(target, button);
					        }
			            }			            
			        else
				        {			        	
			        	userDefinedSamplesModel.setObject("Are you sure that you want to do this randomization?.  The following samples are not in the correct format:" + System.getProperty("line.separator") + ListUtils.prettyPrint(sampleInvalidIds));
			        	target.add(dialogUserDefinedControls);
			        	dialogUserDefinedControls.open(target);			        	
				        }
			        target.add(getParent());
			        }
		        else 
		            {
		        	target.appendJavaScript("alert('Randomization upload was not successful!  Setting Randomization Type to none');");				        
					originalWorklist.getSampleGroup(0).setRandomizationType("None");
					target.add(container);
					super.close(target, button);
		            }
				}
		    @Override
			public Form<?> getForm() {
				// TODO Auto-generated method stub
			//// put back	form.setMultiPart(true);
				return this.form;
			    }
			@Override
			public DialogButton getSubmitButton() {
				// TODO Auto-generated method stub
				return this.btnUpload;
			    }
			//@Override
			protected void onError(AjaxRequestTarget target, DialogButton button) {
				// TODO Auto-generated method stub
			    }
			@Override
			public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
				// TODO Auto-generated method stub
				handler.add(feedback);
			    }
			@Override
			protected void onSubmit(AjaxRequestTarget target, DialogButton button) {
				// TODO Auto-generated method stub			
			    }
		    };		
			
		sampleGroupsList = originalWorklist.getSampleGroupsList();
		for (int i = 0; i < 1; i++)
			sampleGroupsList.add(new WorklistSampleGroup(getDefaultAssay(), originalWorklist));
		container.setOutputMarkupId(true);
		container.setOutputMarkupPlaceholderTag(true);
		add(container);		
		// issue 46
		container.add(dialogUpload);
		container.add(modalRandom);
		container.add(dialogUserDefinedControls);
		
		container.add(feedback.setOutputMarkupId(true));	
		container.add(sampleGroupsListView = new ListView("sampleGroupsListView", new PropertyModel(worklist, "sampleGroupsList"))
			{
			public void populateItem(ListItem listItem)
				{
				WorklistSampleGroup item = (WorklistSampleGroup) listItem.getModelObject();
				listItem.add(selectedExperimentDrop = buildExperimentDropdown("experimentDropdown", item, "experimentId"));
				listItem.add(buildAssayDropdown("assayDropdown", item, "assayType"));
				listItem.add(randomizationDrop = buildRandomizationDropdown( "randomizationDropdown", item, "randomizationType", availableRandomizations));
				listItem.add(buildDeleteButton("deleteButton", item,container));
				listItem.add(buildAddButton("addButton", item, container));
				listItem.add(buildBuildButton("buildButton", item, container));
				listItem.add(buildClearButton("clearButton", item, container));
				listItem.add(buildRandomizationLabel("randomizationLabel"));
				listItem.add(new AjaxButton("openUpload") {			
					private static final long serialVersionUID = 1L;		           
					@Override
					//private static final long serialVersionUID = 1L;
					public boolean isEnabled() 
					    { 
						// issue 46
						return originalWorklist.getSampleGroup(0).getRandomizationType().equals("Custom") && originalWorklist.getOpenForUpdates() && originalWorklist.getSampleGroup(0).getAssayType() != null && globalRand ==  null; // issue 398 issue 268 
						}
					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						{				
						dialogUpload.open(target);
						}
				}});
				
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
		return new AjaxLink <Void> (id)
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
			};
		}

	// Issue 464
	private AjaxLink buildAddButton(String id, final WorklistSampleGroup item, final WebMarkupContainer container)
		{
		return new AjaxLink<Void>(id)
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
			};
		}

	// issue 464
	private IndicatingAjaxLink buildBuildButton(String id, final WorklistSampleGroup item, final WebMarkupContainer container)
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
				//issue 166				
				originalWorklist.getSampleGroup(0).setExpRandom(globalRand);
				originalWorklist.rebuildEverything();
				Map<String, String> idsVsReasearcherNameMap =
				        sampleService.sampleIdToResearcherNameMapForExpId(originalWorklist.getSampleGroup(0).getExperimentId());								
				originalWorklist.populateSampleName(originalWorklist,idsVsReasearcherNameMap );
				if (originalWorklist.countOfSamplesForItems(originalWorklist.getItems())+  (originalWorklist.buildControlTypeMap().get(null) != null ? originalWorklist.buildControlTypeMap().size()-1 : originalWorklist.buildControlTypeMap().size()  ) > (originalWorklist.getCyclePlateLimit() * originalWorklist.getMaxItemsAsInt()))
					{
					String msg =  "alert('This worklist currently contains more than:" + originalWorklist.getCyclePlateLimit() + " plates.  Therefore plate cycling will not be used." +  "')";
					target.appendJavaScript(msg); 
					}
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
				refreshPage(target);// issue 464				
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
	// issue 39
	private AjaxLink buildClearButton(String id, final WorklistSampleGroup item, final WebMarkupContainer container)
		{
		return new IndicatingAjaxLink <Void>(id)
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
				// issue 32
				originalWorklist.setCustomDirectoryStructureName("<custom directory>");
				originalWorklist.setIsCustomDirectoryStructure(false);
				refreshPage(target);
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
						originalWorklist.setChearBlankType("Plasma"); // issue 186
						originalWorklist.setRandomizeByPlate(false);// issue 179
						originalWorklist.setDefaultPool(true); // issue 169
						originalWorklist.clearOutMotorPacControls(); // issue 422
						originalWorklist.clearOutPoolIDDAControls();// issue 432
						originalWorklist.setChosenOtherSample(true);// issue 6
						originalWorklist.setChosenOtherSampleMotrPAC(true); // issue 6
						// issue 387
						// issue 46
						globalRand = null;
						if (originalWorklist != null) originalWorklist.clearAllItems();	
						originalWorklist.setChosenOtherSample(true);
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
						originalWorklist.setChearBlankType("Plasma"); // issue 186
						originalWorklist.setRandomizeByPlate(false);// issue 179
						originalWorklist.setDefaultPool(true); // issue 169
						originalWorklist.clearOutMotorPacControls(); // issue 422
						originalWorklist.clearOutPoolIDDAControls();// issue 432
						originalWorklist.setChosenOtherSample(true);// issue 6
						originalWorklist.setChosenOtherSampleMotrPAC(true); // issue 6
						String[] tokens = val != null ? val.split("\\(") : null;
						item.setIsRandomized(false);
						originalWorklist.setOpenForUpdates(true); // issue 329
						item.setRandomizationType("None"); //issue 384
						// issue 387			
						// issue 46
						globalRand = null;
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
						// issue 32
						originalWorklist.setCustomDirectoryStructureName("<custom directory>");
						originalWorklist.setIsCustomDirectoryStructure(false);
						break;

					case "updateForExperimentDrop":
						originalWorklist.setChearBlankType("Plasma"); // issue 186
						originalWorklist.setRandomizeByPlate(false);// issue 179
						item.setIsRandomized(false);
						originalWorklist.setDefaultPool(true); // issue 169
						if (originalWorklist == null)
							return;
						// issue 387
						// issue 46
						globalRand = null;
						originalWorklist.clearOutMotorPacControls();// issue 422
						originalWorklist.clearOutPoolIDDAControls();// issue 432
						originalWorklist.setChosenOtherSample(true); // issue 6
						originalWorklist.setChosenOtherSampleMotrPAC(true); // issue 6
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
							// issue 32
							originalWorklist.setCustomDirectoryStructureName("<custom directory>");
							originalWorklist.setIsCustomDirectoryStructure(false);
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
	
	public void reinitializeAssays()
		{
		if (availableAssays != null)
			{
			availableAssays.clear();
			availableAssays.add("Choose experiment first");
			}
		}
	
	}



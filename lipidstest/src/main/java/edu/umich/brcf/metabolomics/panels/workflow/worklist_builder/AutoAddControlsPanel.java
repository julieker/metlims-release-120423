////////////////////////////////////////////////////
// AutoAddControlsPanel.java
// Written by Jan Wigginton, Mar 19, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import edu.umich.brcf.shared.panels.login.MedWorksSession;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.service.ControlService;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.io.StringUtils;


public class AutoAddControlsPanel extends Panel
	{
	@SpringBean
	ControlService controlService;
	
	WorklistSimple originalWorklist;
	ModalWindow modal1;

	List<WorklistControlGroup> controlGroupsList;
	ListView<WorklistControlGroup> controlGroupsListView;

	List<String> availableStrQuantities = Arrays.asList(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" });
	List availableSpacingQuantities = Arrays.asList(new String[] {"0 (NO POOLS)", "1", "2", "3", "4", "5", "6", "7",
			"8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"}); // issue 315
	List<String> availableChearBlankTypes = Arrays.asList(new String[] {"Urine", "Plasma"});
	List<String> poolTypes = Arrays.asList(new String[] {"Master Pool   (CS00000MP)", "Batch Pool.M1 (CS000QCMP)",  "Batch Pool.M2 (CS000BPM2)", "Batch Pool.M3 (CS000BPM3)", "Batch Pool.M4 (CS000BPM4)", "Batch Pool.M5 (CS000BPM5)"});
	List<String> poolTypesB = Arrays.asList(new String[] {"Batch Pool.M1 (CS000QCMP)",  "Batch Pool.M2 (CS000BPM2)", "Batch Pool.M3 (CS000BPM3)", "Batch Pool.M4 (CS000BPM4)", "Batch Pool.M5 (CS000BPM5)"});
	IndicatingAjaxLink buildButton,clearButton;

	AjaxLink customizeButton;
    AjaxLink motrpacButton;
	// Issue 302
	DropDownChoice<String> standardsDrop, poolsDropA, poolsDropB, blanksDrop, qcDrop1, qcDrop2, chearBlankTypeDrop, poolTypeADrop, poolTypeBDrop;  // issue 13
	String nStandardsStr = "1", poolSpacingStrA = "0 (NO POOLS)", poolSpacingStrB = "0 (NO POOLS)", nBlanksStr = "1", nMatrixBlanksStr = "0", nChearBlanksStr= "0";
	String chearBlankType = "Urine";
	String poolTypeA =  "Master Pool   (CS00000MP)"; // issue 13
	String poolTypeB =  "Batch Pool.M1 (CS000QCMP)"; // issue 13
	Integer nStandards = 1, nBlanks = 1, nMatrixBlanks = 0, nChearBlanks = 0;
    public Integer poolSpacingA = 0, poolSpacingB = 0;
	private Boolean needsRebuild = false;

	WebMarkupContainer container = new WebMarkupContainer("container");
	List<WebMarkupContainer> sibContainers = new ArrayList<WebMarkupContainer>();
	String example = "";
    
	public AutoAddControlsPanel(String id, final WorklistSimple worklist)
		{
		super(id);
		modal1 = ModalCreator.createModalWindow("modal1", 800, 320);
		add(modal1);
		
		originalWorklist = worklist;
		originalWorklist.initializeControls();

		controlGroupsList = originalWorklist.getControlGroupsList();

		container.setOutputMarkupId(true);
		container.setOutputMarkupPlaceholderTag(true);
		add(container);
	
		container.add(standardsDrop = buildQuantityDropdown("standardsDrop","nStandardsStr"));
		container.add(poolsDropA = buildQuantityDropdown("poolsDropA","poolSpacingStrA"));
		container.add(poolsDropB = buildQuantityDropdown("poolsDropB","poolSpacingStrB"));
				
		container.add(blanksDrop = buildQuantityDropdown("blanksDrop","nBlanksStr"));
		container.add(qcDrop1 = buildQuantityDropdown("qcDrop1","nMatrixBlanksStr"));
		container.add(qcDrop2 = buildQuantityDropdown("qcDrop2","nChearBlanksStr"));
		container.add(chearBlankTypeDrop = buildChearBlankTypeDropdown("chearBlankTypeDrop","chearBlankType"));
		//customizeButton
		// issue 13
		container.add(poolTypeADrop = buildPoolTypeDropdownA("poolTypeADrop","poolTypeA"));
		container.add(poolTypeBDrop = buildPoolTypeDropdownB("poolTypeBDrop","poolTypeB"));
		container.add(customizeButton = buildLinkToCustomizeModal("customizeButton", modal1, worklist ));
		container.add (motrpacButton = buildLinkToMotrPacModal("motrpacButton", modal1, worklist ));
		container.add(buildButton = buildBuildButton("buildButton",container, worklist));
		//buildButton.setDefaultFormProcessing(false);
	
		container.add(clearButton = buildClearButton("clearButton",container));
		}
	
	
	private DropDownChoice buildChearBlankTypeDropdown(final String id,  final String propertyName)
		{
		DropDownChoice drp = new DropDownChoice(id, new PropertyModel(this, propertyName), new LoadableDetachableModel<List<String>>()
			{
			@Override
			protected List<String> load() { return availableChearBlankTypes; }
			})
				{
				public boolean isEnabled()
					{
					if (!originalWorklist.getOpenForUpdates()) return false;
					
					
					return originalWorklist.getItems().size() > 0;
					}
				};

		drp.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForChearBlankTypeDrop", null));
		return drp;
		}
	
	
	// issue 13
	private DropDownChoice buildPoolTypeDropdownA(final String id,  final String propertyName)
		{
		DropDownChoice drp = new DropDownChoice(id, new PropertyModel(this, propertyName), new LoadableDetachableModel<List<String>>()
			{
			@Override
			protected List<String> load() 
		        { 
				ArrayList tempArrayList = new ArrayList<>(poolTypes );
				tempArrayList.remove(poolTypeB);
				return tempArrayList ; 
			    }
			})
			{
			public boolean isEnabled()
				{
				if (!originalWorklist.getOpenForUpdates()) return false;					
				return originalWorklist.getItems().size() > 0;
				}
			};	
		drp.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForPoolTypeDrop", null));
		return drp;
		}
	
	// issue 13
	private DropDownChoice buildPoolTypeDropdownB(final String id,  final String propertyName)
		{
		DropDownChoice drp = new DropDownChoice(id, new PropertyModel(this, propertyName), new LoadableDetachableModel<List<String>>()
			{
			@Override
			protected List<String> load() { return poolTypesB ; }
			})
			{
			public boolean isEnabled()
				{
				if (!originalWorklist.getOpenForUpdates()) return false;					
				return originalWorklist.getItems().size() > 0;
				}
			};	
		drp.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForPoolTypeDropB", null));
		return drp;
		}
		
	// Issue 302
	private DropDownChoice buildQuantityDropdown(final String id,  final String propertyName)
	    {
	    LoadableDetachableModel<List<String>> quantityModel = new LoadableDetachableModel<List<String>>()
		    {
		    @Override
		    protected List<String> load() 
			    { 
			    Boolean doPoolSpacing = (propertyName != null && propertyName.startsWith("poolSpacingStr"));			
			    return doPoolSpacing ? availableSpacingQuantities : availableStrQuantities; 
			    }
		    };	
	    DropDownChoice drp = new DropDownChoice(id, new PropertyModel(this, propertyName), quantityModel)
		    {
		    public boolean isEnabled()
			    {
			    if (!originalWorklist.getOpenForUpdates()) return false;
			    return originalWorklist.getItems().size() > 0;
			    }
		    };
	    drp.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForQuantityDrop", null));
	    return drp;
	    }

	// issue 394
	private IndicatingAjaxLink buildBuildButton(String id, final WebMarkupContainer container , final WorklistSimple worklist )
	    {
	    return new IndicatingAjaxLink <Void> (id)
		    {
		    public boolean isEnabled()
			    {
		    	// issue 431
		    	if (originalWorklist.getItems().size() == 0)
			    	return false;		    	
		    	return true;
			    }
		    @Override
		    protected void onComponentTag(final ComponentTag tag)
			    {
			    super.onComponentTag(tag);
			    if (worklist.countGroups(true) > 1)
				    tag.put("value", "Update Controls");
		        }
	        @Override
	        public void onClick(AjaxRequestTarget target)
		        {
	        	// issue 509
	        	if (!(poolSpacingA > 0) &&  ( ((MedWorksSession) Session.get()).getNMasterPoolsAfter()  > 0  ||  ((MedWorksSession) Session.get()).getNMasterPoolsBefore()  > 0))	        		
	        		{
	        		target.appendJavaScript(StringUtils.makeAlertMessage("There is customization for Pool A.  Please choose a value for Pool Spacing A "));
	        		return;
	        		}
	        	// issue 509
	        	if (!(poolSpacingB > 0) &&  ( ((MedWorksSession) Session.get()).getNBatchPoolsAfter()  > 0  ||  ((MedWorksSession) Session.get()).getNBatchPoolsBefore()  > 0))	        		
	        		{
	        		target.appendJavaScript(StringUtils.makeAlertMessage("There is customization for Pool B.  Please choose a value for Pool Spacing B "));
	        		return;
	        		}	
	        	if (StringParser.parseId(poolTypeA).equals(StringParser.parseId(poolTypeB)))
	        		{
	        		target.appendJavaScript(StringUtils.makeAlertMessage("Pool A and Pool B are both :" + StringParser.parseId(poolTypeB) + " Please make sure you choose a different pool for Pool A and Pool B"));
	        		return;
	        		}	
	        	originalWorklist.clearControlGroups(); // issue 431
	        	if (worklist.getSelectedPlatform() == null || "agilent".equals(worklist.getSelectedPlatform().toLowerCase()))
			        addStandardsToAgilentList(worklist);
		        else
			        addStandardsToList(worklist);
		        worklist.rebuildEverything();
		        worklist.updateSampleNamesArray();
		        refreshPage(target);
	        	
		        }
	        };
        }
	
	
	private IndicatingAjaxLink buildClearButton(String id, final WebMarkupContainer container)
		{
		return new IndicatingAjaxLink<Void>(id)
			{
			@Override
			public void onClick(AjaxRequestTarget target)
				{
				originalWorklist.clearControlGroups();
				originalWorklist.clearOutPoolIDDAControls(); // issue 11 metlims 2019
				originalWorklist.updateSampleNamesArray();
				originalWorklist.setOpenForUpdates(true);
				originalWorklist.updatePlatePositions(); // issue 417 and 409
				refreshPage(target);
				}
			};
		}
	
	
	// issue 394 391 324
    private void addStandardsToAgilentList(WorklistSimple worklist)
		{
    	originalWorklist.setPoolTypeA(StringParser.parseId(poolTypeA)); // issue 13
		if (worklist.getItems().size() == 0)
			return;			
		worklist.updatePoolReplicates(((MedWorksSession) Session.get()).getNMasterPoolsBefore(), 
				((MedWorksSession) Session.get()).getNMasterPoolsAfter(), 
				((MedWorksSession) Session.get()).getNBatchPoolsBefore(),
				((MedWorksSession) Session.get()).getNBatchPoolsAfter());	
		originalWorklist.getControlGroupsList().clear();	
		int nItems = worklist.getItems().size();
		String firstSample =  nItems <= 0 ? null : worklist.getItem(0).getSampleName();
		String lastSample =  nItems <= 0 ? null : worklist.getItem(nItems -1).getSampleName();
		String id = "", finalLabel = "";
		
		for (int i = 0; i < nBlanks; i++)
			{
			id = controlService.controlIdForNameAndAgilent("Solvent Blank");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}	
		for (int i = 0; i < nStandards ; i++)
			{
			id = controlService.controlIdForNameAndAgilent("Standard." + i);
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group);
			}
	
		for (int i = 0; i < nBlanks; i++)
			{
			id = controlService.controlIdForNameAndAgilent("Solvent Blank");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
	
		for (int i = 0; i < nMatrixBlanks; i++)
			{
			id = controlService.controlIdForNameAndAgilent("Red Cross");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
	
		for (int i = 0; i < nChearBlanks; i++)
			{
			if ("Urine".equals(chearBlankType))
				id = controlService.controlIdForNameAndAgilent("Reference 1 - urine");
			else if ("Plasma".equals(chearBlankType))
				id = controlService.controlIdForNameAndAgilent("Reference 1 - plasma");
			
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}		
		// Issue 422
		for (int i = 0; i < ((MedWorksSession) Session.get()).getNGastroExercise(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Gastrocnemius, Exercise");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// Issue 422
		for (int i = 0; i < ((MedWorksSession) Session.get()).getNGastroSedentary(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Gastrocnemius, Sedentary");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// Issue 422
		for (int i = 0; i < ((MedWorksSession) Session.get()).getNLiverExercise(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Liver, Exercise");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// Issue 422
		for (int i = 0; i < ((MedWorksSession) Session.get()).getNLiverSedentary(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Liver, Sedentary");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		// Issue 422
		for (int i = 0; i < ((MedWorksSession) Session.get()).getNAdiposeExercise(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Adipose, Exercise");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
				
		// Issue 422
		for (int i = 0; i < ((MedWorksSession) Session.get()).getNAdiposeSedentary(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Adipose, Sedentary");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// Issue 422
		for (int i = 0; i < ((MedWorksSession) Session.get()).getNPlasmaExercise(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Plasma, Exercise");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// Issue 422
		for (int i = 0; i < ((MedWorksSession) Session.get()).getNPlasmaSedentary(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Plasma, Sedentary");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
	
		// Issue 422 		
		// issue 427
		
		for (int i = 0; i < ((MedWorksSession) Session.get()).getNRatG(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("UM rat   gastrocnemius control");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// issue 427
		for (int i = 0; i < ((MedWorksSession) Session.get()).getNRatL(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("UM rat   liver control");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// issue 427
		for (int i = 0; i < ((MedWorksSession) Session.get()).getNRatA(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("UM rat   adipose control");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		for (int i = 0; i < ((MedWorksSession) Session.get()).getNRatPlasma(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("UM rat   plasma control");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// issue 13
		if (poolSpacingA > 0) 
			{			
			for (int i = 0; i < 1; i++)	
			    {
			    //id = controlService.controlIdForNameAndAgilent("Master Pool");
				id = StringParser.parseId(poolTypeA);
			    finalLabel = controlService.dropStringForIdAndAgilent(id);
			    WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, worklist.getMasterPoolsBefore().toString(), "Before", firstSample, worklist);
			    group3.setStandardNotAddedControl(true);
			    originalWorklist.addControlGroup(group3);
			    }
			}

		if (poolSpacingB > 0) 
		    {
		    for (int i = 0; i < 1; i++)
			    {
			    //id = controlService.controlIdForNameAndAgilent("Batch Pool.M1");
		    	id = StringParser.parseId(poolTypeB);
			    finalLabel = controlService.dropStringForIdAndAgilent(id);
			    WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel,  worklist.getBatchPoolsBefore().toString(), "Before", firstSample, worklist);
			    group3.setStandardNotAddedControl(true);
			    originalWorklist.addControlGroup(group3);
			    }
		    }	
	
		// issue 422 for MotrPac		
		// Issue 422
		// Issue 427		
		for (int i = 0; i < ((MedWorksSession) Session.get()).getNRatPlasma(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("UM rat   plasma control");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		for (int i = 0; i < ((MedWorksSession) Session.get()).getNRatA(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("UM rat   adipose control");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		for (int i = 0; i < ((MedWorksSession) Session.get()).getNRatL(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("UM rat   liver control");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		for (int i = 0; i < ((MedWorksSession) Session.get()).getNRatG(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("UM rat   gastrocnemius control");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		
		// issue 422
		for (int i = 0; i < ((MedWorksSession) Session.get()).getNPlasmaSedentary(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Plasma, Sedentary");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// Issue 422
		for (int i = 0; i < ((MedWorksSession) Session.get()).getNPlasmaExercise(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Plasma, Exercise");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// Issue 422
		for (int i = 0; i < ((MedWorksSession) Session.get()).getNAdiposeSedentary(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Adipose, Sedentary");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// Issue 422
		for (int i = 0; i < ((MedWorksSession) Session.get()).getNAdiposeExercise(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Adipose, Exercise");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// Issue 422
		for (int i = 0; i < ((MedWorksSession) Session.get()).getNLiverSedentary(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Liver, Sedentary");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// Issue 422
		for (int i = 0; i < ((MedWorksSession) Session.get()).getNLiverExercise(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Liver, Exercise");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// Issue 422
		for (int i = 0; i < ((MedWorksSession) Session.get()).getNGastroSedentary(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Gastrocnemius, Sedentary");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// Issue 422
		for (int i = 0; i < ((MedWorksSession) Session.get()).getNGastroExercise(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Gastrocnemius, Exercise");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		for (int i = 0; i < nBlanks; i++)
			{
			id = controlService.controlIdForNameAndAgilent("Solvent Blank");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group4 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group4.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group4);
			}	
		for (int i = nStandards - 1; i >= 0; i--)
			{
			id = controlService.controlIdForNameAndAgilent("Standard." + i);
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group2 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group2.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group2);
			}
		for (int i = 0; i < nBlanks; i++)
			{
			id = controlService.controlIdForNameAndAgilent("Solvent Blank");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group4 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group4.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group4);
			}	
		for (int i = 0; i < nChearBlanks; i++)
			{
			if ("Urine".equals(chearBlankType))
				id = controlService.controlIdForNameAndAgilent("Reference 1 - urine");
			if ("Plasma".equals(chearBlankType))
				id = controlService.controlIdForNameAndAgilent("Reference 1 - plasma");			
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		for (int i = 0; i < nMatrixBlanks; i++)
			{
			id = controlService.controlIdForNameAndAgilent("Red Cross");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}	
		
		// issue 13
		if (poolSpacingA > 0) 
		    {
		    for (int i = 0; i < 1; i++)
			    {
			    //id = controlService.controlIdForNameAndAgilent("Master Pool");
		    	id = StringParser.parseId(poolTypeA);
			    finalLabel = controlService.dropStringForIdAndAgilent(id);
			    WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, worklist.getMasterPoolsAfter().toString(), "After", lastSample, worklist);
			    group3.setStandardNotAddedControl(true);
			    originalWorklist.addControlGroup(group3);
			    }
			}	
		// issue 302
		if (poolSpacingB > 0) 
		    {
		    for (int i = 0; i < 1; i++)
			    {
			    //id = controlService.controlIdForNameAndAgilent("Batch Pool.M1");
		    	id = StringParser.parseId(poolTypeB);
		    	finalLabel = controlService.dropStringForIdAndAgilent(id);
			    WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel,  worklist.getBatchPoolsAfter().toString(), "After", lastSample, worklist);
			    group3.setStandardNotAddedControl(true);
			    originalWorklist.addControlGroup(group3);
			    }
		    }	
		// issue 302
		// issue 13
		if (poolSpacingA > 0) 
			{
			List<String> insertionPoints =  getPoolInsertionPoints(poolSpacingA);
			originalWorklist.setLastPoolBlockNumber(insertionPoints.size());
			//id = controlService.controlIdForNameAndAgilent("Master Pool");
			id = StringParser.parseId(poolTypeA);
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			for (String pt : insertionPoints)
				{
				WorklistControlGroup group4 = new WorklistControlGroup(null, finalLabel, "1", "Before", pt, worklist);
				group4.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group4);
				}
			}	
		// issue 302
		// issue 13
		if (poolSpacingB > 0) 
		    {
		    List<String> insertionPoints =  getPoolInsertionPoints(poolSpacingB);
		    //id = controlService.controlIdForNameAndAgilent("Batch Pool.M1");
		    id = StringParser.parseId(poolTypeB);// issue 13
		    finalLabel = controlService.dropStringForIdAndAgilent(id);
		    for (String pt : insertionPoints)
			    {
			    WorklistControlGroup group4 = new WorklistControlGroup(null, finalLabel, "1", "Before", pt, worklist);
			    group4.setStandardNotAddedControl(true);
			    originalWorklist.addControlGroup(group4);
			    }
		    }		   
		}

     // issue 324 
    private AjaxLink buildLinkToCustomizeModal(final String linkID,  final ModalWindow modal1, final WorklistSimple worklist)
 	    {		
 	    modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
 		    {		
 		    @Override
 		    public void onClose(AjaxRequestTarget target)
 			    {	
                // refresh worklist on callback ,  will need to update the page too..
                worklist.rebuildEverything();    
 			    }		
 		    });

 	    // issue 432
 	    AjaxLink link = new AjaxLink<Void>(linkID) 
 		    {
 	    	@Override
 	    	public boolean isEnabled()
			    {
	    		// issue 431
	    		if (originalWorklist.getItems().size() == 0)
	    		    return false;
	    		return true;	    		 
			    }
 		    @Override
 		    public void onClick(final AjaxRequestTarget target)
 			    {
	 			modal1.setInitialWidth(1100);
	 			modal1.setInitialHeight(450);
	 			modal1.setPageCreator(new ModalWindow.PageCreator()
 				{
 				public Page createPage()
 					{
 					// issue 432
 					return new CustomizeControlGroupPage(modal1)
           			    {
 						@Override
 						protected void onSave(Integer nMasterBefore, Integer nMasterAfter, Integer nBatchBefore, Integer nBatchAfter, Integer nCE10Reps, Integer nCE20Reps, Integer nCE40Reps ) 
 							{
 							((MedWorksSession) Session.get()).setNMasterPoolsBefore(nMasterBefore);		
 							((MedWorksSession) Session.get()).setNMasterPoolsAfter(nMasterAfter);	
 							((MedWorksSession) Session.get()).setNBatchPoolsBefore(nBatchBefore);	
 							((MedWorksSession) Session.get()).setNBatchPoolsAfter(nBatchAfter);	
 							((MedWorksSession) Session.get()).setNCE10Reps(nCE10Reps);	
 							((MedWorksSession) Session.get()).setNCE20Reps(nCE20Reps);	
 							((MedWorksSession) Session.get()).setNCE40Reps(nCE40Reps);	
 			  			    }
           			    };
 					}
 				});  
 			modal1.show(target);
 			
 			}
 		};
 	    link.setOutputMarkupId(true);
 	    return link;
 	    }

     //Issue 422
    
    private AjaxLink buildLinkToMotrPacModal(final String linkID,  final ModalWindow modal1, final WorklistSimple worklist)
	    {		
	    modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
		    {		
		    @Override
		    public void onClose(AjaxRequestTarget target)
			    {	
            // refresh worklist on callback ,  will need to update the page too..
                worklist.rebuildEverything();
			    }		
		    });

	    AjaxLink link = new AjaxLink<Void>(linkID) 
		    {
	    	@Override
	    	 public boolean isEnabled()
			    {
	    		// issue 431
	    		if (originalWorklist.getItems().size() == 0)
	    		    return false;
	    		return true;
	    		 
			    }
		    @Override
		    public void onClick(final AjaxRequestTarget target)
			    {
			    modal1.setInitialWidth(1100);
			    modal1.setInitialHeight(550);// issue 427
			    modal1.setPageCreator(new ModalWindow.PageCreator()
			    
					{
					public Page createPage()
						{
						return new MotrpacOptionsPage(modal1)
	       			        {
							@Override
							// issue 427
							protected void onSave(Integer nGastroExercise, Integer nGastroSedentary, Integer nLiverExcercise, Integer nLiverSedentary, Integer nAdiposeExercise, Integer nAdiposeSedentary, Integer nPlasmaExercise, Integer nPlasmaSedentary, Integer nRatPlasma, Integer nRatG, Integer nRatL, Integer nRatA ) 
								{
								((MedWorksSession) Session.get()).setNGastroExercise(nGastroExercise);	
								((MedWorksSession) Session.get()).setNGastroSedentary(nGastroSedentary);	
								((MedWorksSession) Session.get()).setNLiverExcercise(nLiverExcercise);	
								((MedWorksSession) Session.get()).setNLiverSedentary(nLiverSedentary);	
								((MedWorksSession) Session.get()).setNAdiposeExercise(nAdiposeExercise);	
								((MedWorksSession) Session.get()).setNAdiposeSedentary(nAdiposeSedentary);	
								((MedWorksSession) Session.get()).setNPlasmaExercise(nPlasmaExercise);	
								((MedWorksSession) Session.get()).setNPlasmaSedentary(nPlasmaSedentary);
								((MedWorksSession) Session.get()).setNRatPlasma(nRatPlasma);
								((MedWorksSession) Session.get()).setNRatG(nRatG);
								((MedWorksSession) Session.get()).setNRatL(nRatL);
								((MedWorksSession) Session.get()).setNRatA(nRatA);
				  			    }
	       			        };
						}
					});   
		    
		    modal1.show(target); 
			}
		};
	    link.setOutputMarkupId(true);
	    return link;
	    }
    
  
	//WorklistControlGroup(String eid, String type, String q, String dir, String rs, WorklistSimple w)
	private void addStandardsToList(WorklistSimple worklist)
		{
		// TO DO : Actually count standards, blanks and pools -- this is rough catch requireing clear between builds
		// if a differnt combination strikes the same value as previous
		//	if (worklist.getControlGroupsList().size() == 2 * (nStandards + nBlanks))
		//		return; 
		if (worklist.getItems().size() == 0)
			return;		
		originalWorklist.getControlGroupsList().clear();	
		int nItems = worklist.getItems().size();
		String firstSample =  nItems <= 0 ? null : worklist.getItem(0).getSampleName();
		String lastSample =  nItems <= 0 ? null : worklist.getItem(nItems -1).getSampleName();
		String id = "", finalLabel = "";
		// issue 393
		for (int i = 0; i < nBlanks; i++)
			{
			id = controlService.controlIdForNameAndAgilent("Process Blank");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		for (int i = 0; i < nStandards ; i++)
			{
			id = controlService.controlIdForNameAndAgilent("Standard " + i);
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group);
			}
		
		for (int i = 0; i < nBlanks; i++)
			{
			id = controlService.controlIdForNameAndAgilent("Process Blank");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		
		// Figure out spacing here...
		for (int i = 0; i < nMatrixBlanks; i++)
			{
			id = controlService.controlIdForNameAndAgilent("Matrix Blank");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// Figure out spacing here...
		for (int i = 0; i < nChearBlanks; i++)
			{
			if ("Urine".equals(chearBlankType))
				id = controlService.controlIdForNameAndAgilent("Reference 1 - urine");
			else if ("Plasma".equals(chearBlankType))
				id = controlService.controlIdForNameAndAgilent("Reference 1 - plasma");
			
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// issue 314
		// issue 13
		if (poolSpacingA > 0) 
		    {
		    for (int i = 0; i < 1; i++)
			    {
			    //id = controlService.controlIdForNameAndAgilent("Pool.1");
		    	id = StringParser.parseId(poolTypeA);
			    finalLabel = controlService.dropStringForIdAndAgilent(id);
			    WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "3", "Before", firstSample, worklist);
			    group3.setStandardNotAddedControl(true);
			    originalWorklist.addControlGroup(group3);
			    }
		    }
		  
		// issue 13
	    if (poolSpacingB > 0) 
	        {
		    for (int i = 0; i < 1; i++)
			    {
		    	//id = controlService.controlIdForNameAndAgilent("Pool.1");
		    	id = StringParser.parseId(poolTypeB); // issue 13
			    //id = controlService.controlIdForNameAndAgilent("Pool.1b");
			    finalLabel = controlService.dropStringForIdAndAgilent(id);
			    WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			    group3.setStandardNotAddedControl(true);
			    originalWorklist.addControlGroup(group3);
			    }
		    }
		
		for (int i = 0; i < nBlanks; i++)
			{
			id = controlService.controlIdForNameAndAgilent("Process Blank");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group4 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group4.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group4);
			}
		
		for (int i = nStandards - 1; i >= 0; i--)
			{
			id = controlService.controlIdForNameAndAgilent("Standard " + i);
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group2 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group2.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group2);
			}
		
		
		for (int i = 0; i < nBlanks; i++)
			{
			id = controlService.controlIdForNameAndAgilent("Process Blank");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group4 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group4.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group4);
			}
		
		
		// Figure out spacing here...
		for (int i = 0; i < nChearBlanks; i++)
			{
			if ("Urine".equals(chearBlankType))
				id = controlService.controlIdForNameAndAgilent("Reference 1 - urine");
			if ("Plasma".equals(chearBlankType))
				id = controlService.controlIdForNameAndAgilent("Reference 1 - plasma");
			
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
	
		
		for (int i = 0; i < nMatrixBlanks; i++)
			{
			id = controlService.controlIdForNameAndAgilent("Matrix Blank");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// issue 302
		if (poolSpacingA > 0) 
		    {
		    for (int i = 0; i < 1; i++)
			    {
			    //id = controlService.controlIdForNameAndAgilent("Pool.1");
		    	id = StringParser.parseId(poolTypeA);
			    finalLabel = controlService.dropStringForIdAndAgilent(id);
			    WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			    group3.setStandardNotAddedControl(true);
			    originalWorklist.addControlGroup(group3);
			    }
			}
		
		// issue 302
		if (poolSpacingB > 0) 
		    {
		    for (int i = 0; i < 1; i++)
			    {
			    //id = controlService.controlIdForNameAndAgilent("Pool.1b");
		    	id = StringParser.parseId(poolTypeB); // issue 13
			    finalLabel = controlService.dropStringForIdAndAgilent(id);
			    WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			    group3.setStandardNotAddedControl(true);
			    originalWorklist.addControlGroup(group3);
			    }
		    }

		// issue 302
		// issue 13
		if (poolSpacingA > 0) 
			{
			List<String> insertionPoints =  getPoolInsertionPoints(poolSpacingA);
		
			//id = controlService.controlIdForNameAndAgilent("Pool.1");
			id = StringParser.parseId(poolTypeA);
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			for (String pt : insertionPoints)
				{
				WorklistControlGroup group4 = new WorklistControlGroup(null, finalLabel, "1", "Before", pt, worklist);
				group4.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group4);
				}
			}
	
		// issue 302
		if (poolSpacingB > 0) 
		    {
		    List<String> insertionPoints =  getPoolInsertionPoints(poolSpacingB);
		    //id = controlService.controlIdForNameAndAgilent("Pool.1b");
		    id = StringParser.parseId(poolTypeB); // issue 13
		    finalLabel = controlService.dropStringForIdAndAgilent(id);
		    for (String pt : insertionPoints)
			    {
			    WorklistControlGroup group4 = new WorklistControlGroup(null, finalLabel, "1", "Before", pt, worklist);
			    group4.setStandardNotAddedControl(true);
			    originalWorklist.addControlGroup(group4);
			    }
		    }
		}
	
	
	// Issue 302
	List<String> getPoolInsertionPoints(int spacing)
	    {
	    List<String> insertionPoints = new ArrayList<String>();
	    List<String> sampleIds = originalWorklist.getSampleIds();	
	    List<String> prunedSampleIds = new ArrayList<String>();
	    for (int i = 0; i < sampleIds.size(); i++)
		    if (FormatVerifier.verifyFormat(Sample._2019Format, sampleIds.get(i).toUpperCase()) )
			    prunedSampleIds.add(sampleIds.get(i));	
	    for (int i = 1; i < prunedSampleIds.size(); i++)
		    if ( i % spacing == 0)
			    insertionPoints.add(prunedSampleIds.get(i));
	    // issue 432
	    return insertionPoints;
	    }
	
	public void addSibContainer(WebMarkupContainer c)
		{
		sibContainers.add(c);
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
					case "updateForQuantityDrop":
						if (!StringUtils.isEmptyOrNull(nStandardsStr))
							nStandards = Integer.parseInt(nStandardsStr);
						if (!StringUtils.isEmptyOrNull(poolSpacingStrA))
							poolSpacingA = Integer.parseInt(StringParser.parseName(poolSpacingStrA));
						if (!StringUtils.isEmptyOrNull(poolSpacingStrB))
							poolSpacingB= Integer.parseInt(StringParser.parseName(poolSpacingStrB));
						if (!StringUtils.isEmptyOrNull(nBlanksStr))
							nBlanks = Integer.parseInt(nBlanksStr);						
						if (!StringUtils.isEmptyOrNull(nMatrixBlanksStr))
							nMatrixBlanks = Integer.parseInt(nMatrixBlanksStr);						
						if (!StringUtils.isEmptyOrNull(nChearBlanksStr))
							nChearBlanks = Integer.parseInt(nChearBlanksStr);						
						needsRebuild = true;
						break;	
						// issue 13
					case "updateForPoolTypeDropB" :
						if (poolTypeA.equals(poolTypeB))
							poolTypeA = "Master Pool   (CS00000MP)";
					    refreshPage(target);
					    break;
					}
				

			////	target.add(buildButton);
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


	


	public Integer getNStandards()
		{
		return nStandards;
		}


	public Integer getPoolSpacingA()
		{
		return poolSpacingA;
		}
	
	// Issue 302
	public Integer getPoolSpacingB()
	    {
	    return poolSpacingB;
	    }

	public Integer getnBlanks()
		{
		return nBlanks;
		}


	
	
	public void setNStandards(Integer nStandards)
		{
		this.nStandards = nStandards;
		}


	public void setPoolSpacingA(Integer poolSpacingA)
		{
		this.poolSpacingA = poolSpacingA;
		}


	public void setPoolSpacingB(Integer poolSpacingB)
	    {
	    this.poolSpacingB = poolSpacingB;
	    }
	
	public void setnBlanks(Integer nBlanks)
		{
		this.nBlanks = nBlanks;
		}
	

	public WebMarkupContainer getContainer()
		{
		return container;
		}


	public String getnMatrixBlanksStr()
		{
		return nMatrixBlanksStr;
		}


	public String getnChearBlanksStr()
		{
		return nChearBlanksStr;
		}


	public String getChearBlankType()
		{
		return chearBlankType;
		}

	// issue 13
	public String getPoolTypeA ()
		{
		return  poolTypeA ;
		}
	
	// issue 13
	public void setPoolTypeA (String strPoolTypeA)
		{
		poolTypeA =  strPoolTypeA;
		}

	public void setnMatrixBlanksStr(String nMatrixBlanksStr)
		{
		this.nMatrixBlanksStr = nMatrixBlanksStr;
		}


	public void setnChearBlanksStr(String nChearBlanksStr)
		{
		this.nChearBlanksStr = nChearBlanksStr;
		}


	public void setChearBlankType(String chearBlankType)
		{
		this.chearBlankType = chearBlankType;
		}
	
	
	}

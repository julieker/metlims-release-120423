////////////////////////////////////////////////////
// AutoAddControlsPanel.java
// Written by Jan Wigginton, Mar 19, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.awt.Event;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import edu.umich.brcf.shared.panels.login.MedWorksSession;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.service.ControlService;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.io.StringUtils;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;


public class AutoAddControlsPanel extends Panel
	{
	@SpringBean
	ControlService controlService;
	
	WorklistSimple originalWorklist;
	ModalWindow modal1;
	String gMasterPoolBefore = "";
	List<WorklistControlGroup> controlGroupsList;
	ListView<WorklistControlGroup> controlGroupsListView;
	List<String> availableStrQuantities = Arrays.asList(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" });
	List availableSpacingQuantities = Arrays.asList(new String[] {"0 (NO POOLS)", "1", "2", "3", "4", "5", "6", "7",
			"8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"}); // issue 315
	List<String> availableChearBlankTypes = Arrays.asList(new String[] {"Urine", "Plasma"});
	List<String> poolTypes = Arrays.asList(new String[] {"Master Pool   (CS00000MP)", "Batch Pool.M1 (CS000BPM1)",  "Batch Pool.M2 (CS000BPM2)", "Batch Pool.M3 (CS000BPM3)", "Batch Pool.M4 (CS000BPM4)", "Batch Pool.M5 (CS000BPM5)"});
	List<String> poolTypesB = Arrays.asList(new String[] {"Master Pool.QCMP (CS000QCMP)", "Batch Pool.M1 (CS000BPM1)", "Batch Pool.M2 (CS000BPM2)", "Batch Pool.M3 (CS000BPM3)", "Batch Pool.M4 (CS000BPM4)", "Batch Pool.M5 (CS000BPM5)"});
	IndicatingAjaxLink buildButton,clearButton;			   
	AjaxLink customizeButton;
    AjaxLink motrpacButton;
	// Issue 302
	DropDownChoice<String> standardsDrop, poolsDropA, poolsDropB, blanksDrop, qcDrop1, qcDrop2, chearBlankTypeDrop, poolTypeADrop, poolTypeBDrop;  // issue 13
	String nStandardsStr = "1", poolSpacingStrA = "0 (NO POOLS)", poolSpacingStrB = "0 (NO POOLS)", nBlanksStr = "1", nMatrixBlanksStr = "0", nChearBlanksStr= "0";
	String tNStandardsStr = "1", tPoolSpacingStrA = "0 (NO POOLS)", tPoolSpacingStrB = "0 (NO POOLS)", tNBlanksStr = "1", tNMatrixBlanksStr = "0", tNChearBlanksStr= "0";
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
		//tOriginalWorklist = worklist;
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
		container.add(buildButton = buildBuildButton("buildButton",container, worklist));
		container.add(clearButton = buildClearButton("clearButton",container));
		// issue 56
		
		final MotrpacOptionsDialog motrpacOptionsDialog = new MotrpacOptionsDialog ("motrpacOptionsDialog",  "MoTrPAC Controls", originalWorklist)		
		    { // NOSONAR
			private static final long serialVersionUID = 1L;
		    @Override
			public void onClick(AjaxRequestTarget target, DialogButton button)
				{	
		    	super.close(target, button);
				}    		    
			    @Override
				public Form<?> getForm() 
					{
					// TODO Auto-generated method stub
				//// put back	form.setMultiPart(true);
					return this.form;
					}
			    @Override
				public DialogButton getSubmitButton() 
			    	{
					// TODO Auto-generated method stub
					return this.submitButton;
			    	}	 
			    
				@Override
				protected void onOpen(IPartialPageRequestHandler handler)
					{ 					
					AjaxRequestTarget target = (AjaxRequestTarget) handler;
				    target.add(this);
				    target.add(this.getParent());
				    handler.add(form); 
				    target.add(feedback);
				    target.add(form);
				    // issue 6
				    if (originalWorklist.getChosenOtherSampleMotrPAC())
				        {
				    	target.appendJavaScript(buildHTMLClearString(1,22));
				    	this.clearPrevValues();
				        }
					}
			    
				@Override
				public void onClose(IPartialPageRequestHandler handler, DialogButton button) 
				    {
					// TODO Auto-generated method stub
					//AjaxRequestTarget target = (AjaxRequestTarget) handler;
					this.nGastroExercisePrev=originalWorklist.getNGastroExercise();
					this.nGastroSedentaryPrev=originalWorklist.getNGastroSedentary();
					this.nLiverExercisePrev=originalWorklist.getNLiverExercise();
					this.nLiverSedentaryPrev=originalWorklist.getNLiverSedentary();
					this.nAdiposeExercisePrev=originalWorklist.getNAdiposeExercise();
					this.nAdiposeSedentaryPrev=originalWorklist.getNAdiposeSedentary();					
					this.nPlasmaExercisePrev=originalWorklist.getNPlasmaExercise();
					this.nPlasmaSedentaryPrev=originalWorklist.getNPlasmaSedentary();
					this.nRatPlasmaPrev=originalWorklist.getNRatPlasma();
					this.nRatGPrev=originalWorklist.getNRatG();
					this.nRatLPrev=originalWorklist.getNRatL();
					this.nRatAPrev=originalWorklist.getNRatA();	
					this.nLungExercisePrev=originalWorklist.getNLungExercise();
					this.nLungSedentaryPrev=originalWorklist.getNLungSedentary();
					this.nKidneyExercisePrev=originalWorklist.getNKidneyExercise();
					this.nKidneySedentaryPrev=originalWorklist.getNKidneySedentary();
					this.nHeartExercisePrev=originalWorklist.getNHeartExercise();
					this.nHeartSedentaryPrev=originalWorklist.getNHeartSedentary();
					this.nBrownAdiposeExercisePrev=originalWorklist.getNBrownAdiposeExercise();
					this.nBrownAdiposeSedentaryPrev=originalWorklist.getNBrownAdiposeSedentary();
					this.nHippoCampusExercisePrev=originalWorklist.getNHippoCampusExercise();
					this.nHippoCampusSedentaryPrev=originalWorklist.getNHippoCampusSedentary();
					handler.add(form);
					
				    }
				
				@Override
				public void onConfigure(JQueryBehavior behavior)
				    {
					// class options //
					behavior.setOption("autoOpen", false);
					behavior.setOption("modal", this.isModal());
					behavior.setOption("resizable", this.isResizable());
					behavior.setOption("width", 2000);
					behavior.setOption("title", Options.asString(this.getTitle().getObject()));
					behavior.setOption("height", 600);
				    behavior.setOption("autofocus", false);
				    }	
			    @Override
				protected void onSubmit(AjaxRequestTarget target, DialogButton button) 
				    {
			    	// TODO Auto-generated method stub	
			    	target.add(feedback);
			    	target.add(this);
			    	target.add(container);
			    	target.add(form);
			    	if (originalWorklist.getChosenOtherSampleMotrPAC())
			    	   originalWorklist.setChosenOtherSampleMotrPAC(false);
				    }
				@Override
				protected void onError(AjaxRequestTarget target, DialogButton button) 
				    {
					// TODO Auto-generated method stub				
				    }			
				@Override
				protected List<DialogButton> getButtons()
				    {
					List <DialogButton> dialogButtonList = new ArrayList <DialogButton> ();
					dialogButtonList.add(new DialogButton("submit", "Done")) ;
					return dialogButtonList;
				    }			
			    };
				
			    motrpacOptionsDialog.add(new AjaxEventBehavior("keydown")
				    {        
					@Override
					protected void onEvent(AjaxRequestTarget target) 
					    {
						// TODO Auto-generated method stub
					    originalWorklist.setNGastroExercise(motrpacOptionsDialog.nGastroExercisePrev);
				        originalWorklist.setNGastroSedentary(motrpacOptionsDialog.nGastroSedentaryPrev);
				        originalWorklist.setNLiverExercise(motrpacOptionsDialog.nLiverExercisePrev);
				        originalWorklist.setNLiverSedentary(motrpacOptionsDialog.nLiverSedentaryPrev);
				        originalWorklist.setNAdiposeExercise(motrpacOptionsDialog.nAdiposeExercisePrev);
				        originalWorklist.setNAdiposeSedentary(motrpacOptionsDialog.nAdiposeSedentaryPrev);
				        originalWorklist.setNPlasmaExercise(motrpacOptionsDialog.nPlasmaExercisePrev);
				        originalWorklist.setNPlasmaSedentary(motrpacOptionsDialog.nPlasmaSedentaryPrev);
				        originalWorklist.setNRatPlasma(motrpacOptionsDialog.nRatPlasmaPrev);
				        originalWorklist.setNRatG(motrpacOptionsDialog.nRatGPrev);
				        originalWorklist.setNRatL(motrpacOptionsDialog.nRatLPrev);
				        originalWorklist.setNRatA(motrpacOptionsDialog.nRatAPrev);
				        originalWorklist.setNLungExercise(motrpacOptionsDialog.nLungExercisePrev);
				        originalWorklist.setNLungSedentary(motrpacOptionsDialog.nLungSedentaryPrev);
				        originalWorklist.setNKidneyExercise(motrpacOptionsDialog.nKidneyExercisePrev);
				        originalWorklist.setNKidneySedentary(motrpacOptionsDialog.nKidneySedentaryPrev);
				        originalWorklist.setNHeartExercise(motrpacOptionsDialog.nHeartExercisePrev);
				        originalWorklist.setNHeartSedentary(motrpacOptionsDialog.nHeartSedentaryPrev);
				        originalWorklist.setNBrownAdiposeExercise(motrpacOptionsDialog.nBrownAdiposeExercisePrev);
				        originalWorklist.setNBrownAdiposeSedentary(motrpacOptionsDialog.nBrownAdiposeSedentaryPrev);
				        originalWorklist.setNHippoCampusExercise(motrpacOptionsDialog.nHippoCampusExercisePrev);
				        originalWorklist.setNHippoCampusSedentary(motrpacOptionsDialog.nHippoCampusSedentaryPrev);
					    }
				    });
			    
	////////////////////////////////////////////////////			
		final CustomizeControlGroupPageDialog customizeControlGroupPageDialog = new CustomizeControlGroupPageDialog("customizeControlGroupPageDialog", "Customize pool settings", originalWorklist) 
		    { // NOSONAR
			private static final long serialVersionUID = 1L;
		   // @Override
			public void onClick(AjaxRequestTarget target, DialogButton button)
				{	
				if ( ( nCE10Reps > 0  || nCE20Reps > 0 || nCE40Reps > 0 ) &&  masterPoolsAfter == 0    )
				    {
	        		target.appendJavaScript(StringUtils.makeAlertMessage("There are NCE values for an IDDA run.  Please choose an after amount for Pool A "));
		        	}
				else 
					super.close(target, button);
				}	    		    
		    @Override
			public Form<?> getForm() 
				{
				// TODO Auto-generated method stub
			//// put back	form.setMultiPart(true);
				return this.form;
				}
		    @Override
			public DialogButton getSubmitButton() 
		    	{
				// TODO Auto-generated method stub
				return this.submitButton;
		    	}	 
		    
			@Override
			protected void onOpen(IPartialPageRequestHandler handler)
				{ 
			    AjaxRequestTarget target = (AjaxRequestTarget) handler;
		        target.add(this);
		        target.add(this.getParent());
		        handler.add(form); 
		        target.add(feedback);
		        target.add(form);
		        if (originalWorklist.getChosenOtherSample())
			        {
			    	target.appendJavaScript(buildHTMLClearString(23,29));
			    	this.clearPrevValues();
			        }
				}
		    
			@Override
			public void onClose(IPartialPageRequestHandler handler, DialogButton button) 
			    {
				// TODO Auto-generated method stub
				//AjaxRequestTarget target = (AjaxRequestTarget) handler;	
				this.masterPoolsAfterPrev= originalWorklist.getMasterPoolsAfter();	
				this.masterPoolsBeforePrev= originalWorklist.getMasterPoolsBefore();
				this.batchPoolsAfterPrev= originalWorklist.getBatchPoolsAfter();	
				this.batchPoolsBeforePrev= originalWorklist.getBatchPoolsBefore();
		    	this.nCE10RepsPrev= originalWorklist.getNCE10Reps();
		    	this.nCE20RepsPrev= originalWorklist.getNCE20Reps();
		    	this.nCE40RepsPrev= originalWorklist.getNCE40Reps();
				handler.add(form); 
				
			    }
			
			@Override
			public void onConfigure(JQueryBehavior behavior)
			    {
				// class options //
				behavior.setOption("autoOpen", false);
				behavior.setOption("modal", this.isModal());
				behavior.setOption("resizable", this.isResizable());
				behavior.setOption("width", 2000);
				behavior.setOption("title", Options.asString(this.getTitle().getObject()));
				behavior.setOption("height", 400);
			    behavior.setOption("autofocus", false);
			    }	
		    @Override
			protected void onSubmit(AjaxRequestTarget target, DialogButton button) 
			    {
		    	// TODO Auto-generated method stub	
		    	target.add(feedback);
		    	target.add(this);
		    	target.add(container);
		    	target.add(form);
		    	if (originalWorklist.getChosenOtherSample())
			        originalWorklist.setChosenOtherSample(false);
			    }
			@Override
			protected void onError(AjaxRequestTarget target, DialogButton button) 
			    {
				// TODO Auto-generated method stub				
			    }			
			@Override
			protected List<DialogButton> getButtons()
			    {
				List <DialogButton> dialogButtonList = new ArrayList <DialogButton> ();
				dialogButtonList.add(new DialogButton("submit", "Done")) ;
				return dialogButtonList;
			    }			
		    };			    
		    // issue 46
		//	container.add(new AjaxButton("opendropdown") 
		    container.add(new IndicatingAjaxLink <Void>("opendropdown") 
			    {			
				private static final long serialVersionUID = 1L;       
				@Override
				public void onClick(AjaxRequestTarget target) 			     
				    {	
					customizeControlGroupPageDialog.open(target);
				    }
			    });
		    container.add(customizeControlGroupPageDialog);
		    
		    // issue 53
		    container.add(new IndicatingAjaxLink <Void>("openMotrpac") 
		    {			
			private static final long serialVersionUID = 1L;       
			@Override
			public void onClick(AjaxRequestTarget target) 			     
			    {	
				motrpacOptionsDialog.open(target);
			    }
		    });
		    customizeControlGroupPageDialog.add(new AjaxEventBehavior("keydown")
		    {        
			@Override
			protected void onEvent(AjaxRequestTarget target) 
			    {
				// TODO Auto-generated method stub
				originalWorklist.setMasterPoolsBefore(customizeControlGroupPageDialog.masterPoolsBeforePrev);
		    	originalWorklist.setMasterPoolsAfter(customizeControlGroupPageDialog.masterPoolsAfterPrev);
		    	originalWorklist.setBatchPoolsBefore(customizeControlGroupPageDialog.batchPoolsBeforePrev);
		    	originalWorklist.setBatchPoolsAfter(customizeControlGroupPageDialog.batchPoolsAfterPrev);
		    	originalWorklist.setNCE10Reps(customizeControlGroupPageDialog.nCE10RepsPrev);
		    	originalWorklist.setNCE20Reps(customizeControlGroupPageDialog.nCE20RepsPrev);
		    	originalWorklist.setNCE40Reps(customizeControlGroupPageDialog.nCE40RepsPrev);		        
			    }
		    });
			container.add(motrpacOptionsDialog);
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
	        	if (!(poolSpacingA > 0) &&  ( worklist.getMasterPoolsAfter() > 0  ||  worklist.getMasterPoolsBefore()  > 0))	        		
	        		{
	        		target.appendJavaScript(StringUtils.makeAlertMessage("There is customization for Pool A.  Please choose a value for Pool Spacing A "));
	        		return;
	        		}
	        	// issue 509
	        	if (!(poolSpacingB > 0) &&  ( worklist.getBatchPoolsAfter()  > 0  ||  worklist.getBatchPoolsBefore()  > 0))	        		
	        		{
	        		target.appendJavaScript(StringUtils.makeAlertMessage("There is customization for Pool B.  Please choose a value for Pool Spacing B "));
	        		return;
	        		}
	        	
	        	// issue 17
	        	worklist.setBothQCMPandMP (StringParser.parseId(poolTypeA).equals("CS00000MP") && StringParser.parseId(poolTypeB).equals("CS000QCMP") && poolSpacingA > 0 && poolSpacingB > 0);    		
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
	        	// issue 22
	        	Map<String, Integer> controlTypeMap = worklist.buildControlTypeMap();
	        	int numberDistinctControls = controlTypeMap.size()  ;
	        	if ( numberDistinctControls > worklist.getMaxItemsAsInt())
	        	    {
	        		target.appendJavaScript(StringUtils.makeAlertMessage("There are currently: " + numberDistinctControls + " total User Defined and standard controls.  Please add fewer standard controls to keep this number at " + worklist.getMaxItemsAsInt() +  " or less") );
	        		originalWorklist.clearControlGroups();
	        	    }
	        	// issue 17
	        	// issue 19
	        	CountPair countPair = originalWorklist.getLargestControlTypeTotal();
	        	// issue 16
		        	if (countPair.getCount() > 99)
			        	{
		        		target.appendJavaScript(StringUtils.makeAlertMessage("The control type:" + countPair.getTag() + " has " + countPair.getCount() + " entries. Please redo the controls and limit this to " + originalWorklist.getLimitNumberControls()));
		        		originalWorklist.clearOutPoolIDDAControls();
		        		refreshPage(target);
		        		return;	
			        	}	
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
		originalWorklist.getControlGroupsList().clear();	
		int nItems = worklist.getItems().size();
		String firstSample =  nItems <= 0 ? null : worklist.getItem(0).getSampleName();
		String lastSample =  nItems <= 0 ? null : worklist.getItem(nItems -1).getSampleName();
		worklist.setLastSample(lastSample); // issue 29
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
		for (int i = 0; i < worklist.getNGastroExercise(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Gastrocnemius, Exercise");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// Issue 422
		for (int i = 0; i < worklist.getNGastroSedentary(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Gastrocnemius, Sedentary");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// Issue 422
		for (int i = 0; i < worklist.getNLiverExercise(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Liver, Exercise");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// Issue 422
		for (int i = 0; i < worklist.getNLiverSedentary(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Liver, Sedentary");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		// Issue 422
		for (int i = 0; i < worklist.getNAdiposeExercise(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Adipose, Exercise");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
				
		// Issue 422
		for (int i = 0; i < worklist.getNAdiposeSedentary(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Adipose, Sedentary");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// Issue 422
		for (int i = 0; i < worklist.getNPlasmaExercise(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Plasma, Exercise");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// Issue 422
		for (int i = 0; i < worklist.getNPlasmaSedentary(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Plasma, Sedentary");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
         // issue 22
		for (int i = 0; i < worklist.getNLungExercise(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Lung, Exercise");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}	
		for (int i = 0; i < worklist.getNLungSedentary(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Lung, Sedentary");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		for (int i = 0; i < worklist.getNKidneyExercise(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Kidney, Exercise");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}	
		for (int i = 0; i < worklist.getNKidneySedentary(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Kidney, Sedentary");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		for (int i = 0; i < worklist.getNHeartExercise(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Heart, Exercise");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}	
		for (int i = 0; i < worklist.getNHeartSedentary(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Heart, Sedentary");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		for (int i = 0; i < worklist.getNBrownAdiposeExercise(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Brown Adipose, Exercise");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}	
	    for (int i = 0; i < worklist.getNBrownAdiposeSedentary(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Brown Adipose, Sedentary");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		for (int i = 0; i < worklist.getNHippoCampusExercise(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Hippocampus, Exercise");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}	
		for (int i = 0; i < worklist.getNHippoCampusSedentary(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Hippocampus, Sedentary");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		// Issue 422 		
		// issue 427
		
		for (int i = 0; i < worklist.getNRatG(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("UM rat   gastrocnemius control");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// issue 427
		for (int i = 0; i < worklist.getNRatL(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("UM rat   liver control");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// issue 427
		for (int i = 0; i < worklist.getNRatA(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("UM rat   adipose control");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		for (int i = 0; i < worklist.getNRatPlasma(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("UM rat   plasma control");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
						
		// issue 13 issue 17 issue 19
		if (poolSpacingA > 0 &&  worklist.getMasterPoolsBefore()> 0 ) 
			{			
			for (int i = 0; i < 1; i++)	
			    {
				id = StringParser.parseId(poolTypeA);
			    finalLabel = controlService.dropStringForIdAndAgilent(id);
			    WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, worklist.getMasterPoolsBefore().toString(), "Before", firstSample, worklist);
			    group3.setStandardNotAddedControl(true);
			    originalWorklist.addControlGroup(group3);
			    }
			}
		// issue 13 issue 17 issue 19
		if (poolSpacingB > 0 &&  worklist.getBatchPoolsBefore()> 0) 
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
		for (int i = 0; i < worklist.getNRatPlasma(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("UM rat   plasma control");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		for (int i = 0; i < worklist.getNRatA(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("UM rat   adipose control");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		for (int i = 0; i < worklist.getNRatL(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("UM rat   liver control");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		for (int i = 0; i < worklist.getNRatG(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("UM rat   gastrocnemius control");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}		
        // issue 22		
		for (int i = 0; i < worklist.getNHippoCampusSedentary(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Hippocampus, Sedentary");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
        for (int i = 0; i < worklist.getNHippoCampusExercise(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Hippocampus, Exercise");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
        for (int i = 0; i < worklist.getNBrownAdiposeSedentary(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Brown Adipose, Sedentary");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
        for (int i = 0; i < worklist.getNBrownAdiposeExercise(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Brown Adipose, Exercise");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}       
        for (int i = 0; i < worklist.getNHeartSedentary(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Heart, Sedentary");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
        for (int i = 0; i < worklist.getNHeartExercise(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Heart, Exercise");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
    	for (int i = 0; i < worklist.getNKidneySedentary(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Kidney, Sedentary");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
    	for (int i = 0; i < worklist.getNKidneyExercise(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Kidney, Exercise");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}  
        for (int i = 0; i < worklist.getNLungSedentary(); i++)
   			{
   			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Lung, Sedentary");
   			finalLabel = controlService.dropStringForIdAndAgilent(id);
   			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
   			group3.setStandardNotAddedControl(true);
   			originalWorklist.addControlGroup(group3);
            }
        for (int i = 0; i < worklist.getNLungExercise(); i++)
   			{
   			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Lung, Exercise");
   			finalLabel = controlService.dropStringForIdAndAgilent(id);
   			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
   			group3.setStandardNotAddedControl(true);
   			originalWorklist.addControlGroup(group3);
            } 
		
		// issue 422
		for (int i = 0; i < worklist.getNPlasmaSedentary(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Plasma, Sedentary");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// Issue 422
		for (int i = 0; i < worklist.getNPlasmaExercise(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Plasma, Exercise");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// Issue 422
		for (int i = 0; i < worklist.getNAdiposeSedentary(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Adipose, Sedentary");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// Issue 422
		for (int i = 0; i < worklist.getNAdiposeExercise(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Adipose, Exercise");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// Issue 422
		for (int i = 0; i < worklist.getNLiverSedentary(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Liver, Sedentary");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// Issue 422
		for (int i = 0; i < worklist.getNLiverExercise(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Liver, Exercise");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// Issue 422
		for (int i = 0; i < worklist.getNGastroSedentary(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Gastrocnemius, Sedentary");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// Issue 422
		for (int i = 0; i < worklist.getNGastroExercise(); i++)
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
		// issue 17
		// issue 19
		if (poolSpacingA > 0 && worklist.getMasterPoolsAfter() > 0) 
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
		// issue 17
		// issue 19
		if (poolSpacingB > 0 && worklist.getBatchPoolsAfter() > 0) 
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
     //Issue 422
    
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
	
	// issue 6	
	private String buildHTMLClearString(int start, int end)
		{
		//"document.getElementById(\"1\").selectedIndex = 0; alert('hi');"
		int i;
		String htmlStr = "";
		for (i=start;i<=end;i++)
			{
			htmlStr = htmlStr + "document.getElementById(" + "\"" + Integer.toString(i) + "\""+ ").selectedIndex = 0;"	;
			}
		return htmlStr;
		}
	
		
	}

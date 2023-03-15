////////////////////////////////////////////////////
// AutoAddControlsPanel.java

// Written by Jan Wigginton, Mar 19, 2017
// Updated by Julie Keros Feb 10, 2020
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.service.ControlService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.sheetwriters.MsWorklistWriter;
import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;


public class AutoAddControlsPanel extends Panel
	{
	@SpringBean
	private ControlService controlService;
	
	// issue 166
	@SpringBean
	SampleService sampleService;
	AutoAddControlsPanel autoAddControlsPanel = this;
	IndicatingAjaxLink motrPacLink;
	IndicatingAjaxLink customLink;
	final CustomizeControlGroupPageDialog customizeControlGroupPageDialog;
	List <WorklistItemSimple> lgetItems = new ArrayList <WorklistItemSimple>  ();
	Map<String, String> idsVsReasearcherNameMap = new HashMap<String, String> ();
	boolean alreadyBlank = false;
	AjaxCheckBox defaultPoolBox ;
	private WorklistSimple originalWorklist;
	private ModalWindow modal1;
	private List<String> availableStrQuantities = Arrays.asList(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" });
	// issue 212
	private List<String> availableQ96StrQuantities = Arrays.asList(new String[] { "0", "1", "2", "3", "4", "5", "6"});
	private List<String> availableInjectionQuantities = Arrays.asList(new String[] { "0 (NO INJECTIONS)", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" });	
	private List availableSpacingQuantities = Arrays.asList(new String[] {"0 (NO POOLS)", "1", "2", "3", "4", "5", "6", "7",
			"8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"}); // issue 315
	private List<String> availableChearBlankTypes = Arrays.asList(new String[] {"Urine", "Plasma"});
	private List<String> poolTypes = Arrays.asList(new String[] {"Master Pool   (CS00000MP)", "Batch Pool.M1 (CS000BPM1)",  "Batch Pool.M2 (CS000BPM2)", "Batch Pool.M3 (CS000BPM3)", "Batch Pool.M4 (CS000BPM4)", "Batch Pool.M5 (CS000BPM5)"});
	private List<String> poolTypesB = Arrays.asList(new String[] {"Master Pool.QCMP (CS000QCMP)", "Batch Pool.M1 (CS000BPM1)", "Batch Pool.M2 (CS000BPM2)", "Batch Pool.M3 (CS000BPM3)", "Batch Pool.M4 (CS000BPM4)", "Batch Pool.M5 (CS000BPM5)"});
	// issue 215
	public String poolTypeA =  "Master Pool   (CS00000MP)"; // issue 13
	public String poolTypeB =  "Batch Pool.M1 (CS000QCMP)"; // issue 13
	// issue 13 2020
	// issue 255
	public Integer nStandards = 0, nProcessBlanks = 1, nBlanks = 1, nMatrixBlanks = 0, nChearBlanks = 0;
    public  Integer poolSpacingA = 0, poolSpacingB = 0, numberInjections = 0, numberInjectionsSB = 2, numberInjectionsPB = 0, numberInjectionsPool = 0 ; // issue 207 issue 201	   
	// Issue 302
    public String nBlanksStr = "1", nProcessBlanksStr = "1";
    // issue 253
	public DropDownChoice<String> standardsDrop, poolsDropA, poolsDropB, blanksDrop,  qcDrop1, qcDrop2, chearBlankTypeDrop, poolTypeADrop, poolTypeBDrop, numberInjectionsDrop, numberInjectionsDropPool, numberInjectionsDropSB, numberInjectionsDropPB;// issue 13
	
	// issue 212
	// issue 255
	String numberInjectionsStr = "0 (NO INJECTIONS)", nStandardsStr = "0", poolSpacingStrA = "0 (NO POOLS)", poolSpacingStrB = "0 (NO POOLS)",  nMatrixBlanksStr = "0", nChearBlanksStr= "0";
	private String numberInjectionsPoolStr = "0 (NO INJECTIONS)";
	private String numberInjectionsSBStr = "2";
	String numberInjectionsPBStr = "0 (NO INJECTIONS)";
	// issue 13 2020 
	//private String nProcessBlanksStr = "1";
	private WebMarkupContainer container = new WebMarkupContainer("container");
	private List<WebMarkupContainer> sibContainers = new ArrayList<WebMarkupContainer>();
	private String example = "";
	private boolean prevDefaultPool = true ; // issue 169
	PlateListHandler plateListHandler ;
	MsWorklistWriter msWorklistWriter; 
    WorklistBuilderPanel gWp;
	// issue 255
    protected void onBeforeRender() 
	    {
	     if (gWp != null && originalWorklist != null && originalWorklist.getItems().size() > 0)                          
	     	gWp.form.plateListHandler.addLastControlRepeater(originalWorklist);     
	     super.onBeforeRender();         
	    }
	
	public AutoAddControlsPanel(String id, final WorklistSimple worklist, WorklistBuilderPanel wp)
		{
		super(id);
		gWp = wp;
		IndicatingAjaxLink buildButton,clearButton;	
		modal1 = ModalCreator.createModalWindow("modal1", 800, 320);
		add(modal1);
		originalWorklist = worklist;
		//tOriginalWorklist = worklist;
		originalWorklist.initializeControls();
		container.setOutputMarkupId(true);
		container.setOutputMarkupPlaceholderTag(true);
		add(container);
	 
		container.add(defaultPoolBox = buildDefaultPool("defaultPool"));// issue 416
		container.add(standardsDrop = buildQuantityDropdown("standardsDrop","nStandardsStr"));
		container.add(poolsDropA = buildQuantityDropdown("poolsDropA","poolSpacingStrA"));
		container.add(poolsDropB = buildQuantityDropdown("poolsDropB","poolSpacingStrB"));
		container.add(numberInjectionsDrop = buildQuantityDropdown("numberInjectionsDrop", "numberInjectionsStr")); // issue 201
		container.add(numberInjectionsDropPool = buildQuantityDropdown("numberInjectionsDropPool", "numberInjectionsPoolStr")); // issue 201
		container.add(numberInjectionsDropSB = buildQuantityDropdown("numberInjectionsDropSB", "numberInjectionsSBStr")); // issue 207
		container.add(numberInjectionsDropPB = buildQuantityDropdown("numberInjectionsDropPB", "numberInjectionsPBStr")); // issue 207
		//issue 255
		//container.add(blanksDrop = buildQuantityDropdown("blanksDrop","nBlanksStr"));
		
		container.add(qcDrop1 = buildQuantityDropdown("qcDrop1","nMatrixBlanksStr"));
		container.add(qcDrop2 = buildQuantityDropdown("qcDrop2","nChearBlanksStr"));
		container.add(chearBlankTypeDrop = buildChearBlankTypeDropdown("chearBlankTypeDrop"));
		//customizeButton
		// issue 13
		container.add(poolTypeADrop = buildPoolTypeDropdownA("poolTypeADrop","poolTypeA"));
		container.add(poolTypeBDrop = buildPoolTypeDropdownB("poolTypeBDrop","poolTypeB"));
		container.add(buildButton = buildBuildButton("buildButton",container, worklist, wp));
		container.add(clearButton = buildClearButton("clearButton",container, wp));
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
				prevDefaultPool  = autoAddControlsPanel.originalWorklist.getDefaultPool(); //issue 169
			    target.add(this);
			    target.add(this.getParent());
			    handler.add(form); 
			    target.add(feedback);
			    target.add(form);
			    // issue 6
			    if (originalWorklist.getChosenOtherSampleMotrPAC())
			        {
			    	target.appendJavaScript(buildHTMLSetString(1,22,"0"));
			    	target.appendJavaScript(buildHTMLSetString(30,39, "0")); // issue 126 issue 193
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
				// issue 126
				this.nPlasmaHumanFemalePrev=originalWorklist.getNPlasmaHumanFemale();
				this.nPlasmaHumanMalePrev=originalWorklist.getNPlasmaHumanMale();
				// issue 193
				this.nMuscleHumanFemalePrev=originalWorklist.getNMuscleHumanFemale();
				this.nMuscleHumanMalePrev=originalWorklist.getNMuscleHumanMale();
				
				this.nHumanMuscleCntrlPrev=originalWorklist.getNHumanMuscleCntrl();
				
				this.nRefStdAPrev=originalWorklist.getNRefStdA();
				this.nRefStdBPrev=originalWorklist.getNRefStdB();
				this.nRefStdCPrev=originalWorklist.getNRefStdC();
				this.nRefStdDPrev=originalWorklist.getNRefStdD();
				this.nRefStdEPrev=originalWorklist.getNRefStdE();

				// issue 169
				autoAddControlsPanel.defaultPoolBox.setDefaultModelObject(true);
				worklist.setDefaultPool(true);
				autoAddControlsPanel.defaultPoolBox.setDefaultModelObject(prevDefaultPool);
				worklist.setDefaultPool(prevDefaultPool);
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
				behavior.setOption("height", 550);
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
		        originalWorklist.setNMuscleHumanFemale(motrpacOptionsDialog.nMuscleHumanFemalePrev);
		        originalWorklist.setNMuscleHumanMale(motrpacOptionsDialog.nMuscleHumanMalePrev);
		        originalWorklist.setNHumanMuscleCntrl(motrpacOptionsDialog.nHumanMuscleCntrlPrev);
		        // issue 193
		        originalWorklist.setNPlasmaHumanFemale(motrpacOptionsDialog.nPlasmaHumanFemalePrev);
		        originalWorklist.setNPlasmaHumanMale(motrpacOptionsDialog.nPlasmaHumanMalePrev);
			    // issue 235	        
		        originalWorklist.setNRefStdA(motrpacOptionsDialog.nRefStdAPrev);
		        originalWorklist.setNRefStdB(motrpacOptionsDialog.nRefStdBPrev);
		        originalWorklist.setNRefStdC(motrpacOptionsDialog.nRefStdCPrev);
		        originalWorklist.setNRefStdD(motrpacOptionsDialog.nRefStdDPrev);
		        originalWorklist.setNRefStdE(motrpacOptionsDialog.nRefStdEPrev);
			    }
		    });			
		
		customizeControlGroupPageDialog = new CustomizeControlGroupPageDialog("customizeControlGroupPageDialog", "Customize pool settings", originalWorklist) 
		    { // NOSONAR
			private static final long serialVersionUID = 1L;
		    @Override
			public void onClick(AjaxRequestTarget target, DialogButton button)
				{		
		    	// issue 217
				if ( ( worklist.getNCE10Reps() > 0  || worklist.getNCE20Reps() > 0 || worklist.getNCE40Reps() > 0 ) &&  (worklist.getMasterPoolsAfter() == 0 && !worklist.getIs96Well() )  )
				    {
					originalWorklist.setMasterPoolsBefore(this.masterPoolsBeforePrev);
			    	originalWorklist.setMasterPoolsAfter(this.masterPoolsAfterPrev);
			    	originalWorklist.setBatchPoolsBefore(this.batchPoolsBeforePrev);
			    	originalWorklist.setBatchPoolsAfter(this.batchPoolsAfterPrev);
			    	originalWorklist.setNCE10Reps(this.nCE10RepsPrev);
			    	originalWorklist.setNCE20Reps(this.nCE20RepsPrev);
			    	originalWorklist.setNCE40Reps(this.nCE40RepsPrev);		        
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
				if (originalWorklist.getIs96Well())
					this.nce10.setEnabled(false);
				else
					this.nce10.setEnabled(true);
				if (originalWorklist.getIs96Well())
					this.nce20.setEnabled(false);
				else
					this.nce20.setEnabled(true);
				if (originalWorklist.getIs96Well())
					this.nce40.setEnabled(false);
				else
					this.nce40.setEnabled(true);
				if (originalWorklist.getIs96Well())
					this.bpBefore.setEnabled(false);
				else
					this.bpBefore.setEnabled(true);
				if (originalWorklist.getIs96Well())
					this.bpAfter.setEnabled(false);
				else
					this.bpAfter.setEnabled(true);
				AjaxRequestTarget target = (AjaxRequestTarget) handler;
				prevDefaultPool  = autoAddControlsPanel.originalWorklist.getDefaultPool(); //issue 169
				if (originalWorklist.getChosenOtherSample())
			        {
			    	target.appendJavaScript(buildHTMLSetString(23,29,"0"));
			    	this.clearPrevValues();
			        }
				// issue 169
				if (originalWorklist.getDefaultPool())
					{
					// issue 253
					if (poolSpacingA > 0 &&  originalWorklist.getMasterPoolsAfter() <= 2)
						{
						target.appendJavaScript(buildHTMLSetString(24,24,originalWorklist.getMasterPoolsAfter() ==1 ? "1" : "2"));
						originalWorklist.setMasterPoolsAfter(2);
						}
					if (poolSpacingA > 0 &&  originalWorklist.getMasterPoolsBefore() <= 2)
						{
						target.appendJavaScript(buildHTMLSetString(23,23,originalWorklist.getMasterPoolsBefore() ==1 ? "1" : "2"));
						originalWorklist.setMasterPoolsBefore(2);
						}
					if (poolSpacingB > 0 &&  originalWorklist.getBatchPoolsAfter() <= 1)
				  		{
				  		target.appendJavaScript(buildHTMLSetString(26,26,"1"));
				  		originalWorklist.setBatchPoolsAfter(1);
				  		}
					if (poolSpacingB > 0 &&  originalWorklist.getBatchPoolsBefore() <= 1)
				  		{
				  		target.appendJavaScript(buildHTMLSetString(25,25,"1"));
				  		originalWorklist.setBatchPoolsBefore(1);
				  		}
					}	
				target.add(this);
				}
		    
			@Override
			public void onClose(IPartialPageRequestHandler handler, DialogButton button) 
			    {
				this.masterPoolsAfterPrev= originalWorklist.getMasterPoolsAfter();	
				this.masterPoolsBeforePrev= originalWorklist.getMasterPoolsBefore();
				this.batchPoolsAfterPrev= originalWorklist.getBatchPoolsAfter();	
				this.batchPoolsBeforePrev= originalWorklist.getBatchPoolsBefore();
		    	this.nCE10RepsPrev= originalWorklist.getNCE10Reps();
		    	this.nCE20RepsPrev= originalWorklist.getNCE20Reps();
		    	this.nCE40RepsPrev= originalWorklist.getNCE40Reps();
		    	// issue 169
		    	autoAddControlsPanel.defaultPoolBox.setDefaultModelObject(true);
				worklist.setDefaultPool(true);
		    	autoAddControlsPanel.defaultPoolBox.setDefaultModelObject(prevDefaultPool);
				worklist.setDefaultPool(prevDefaultPool);
				handler.add(feedback); 			
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
	    container.add(customLink =  new IndicatingAjaxLink <Void>("opendropdown") 
		    {			
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isEnabled()
				{
				if (originalWorklist.getItems().size() == 0)
					return false;
				else 
					//return !originalWorklist.getIs96Well();
					return true;
				}
			@Override
			public void onClick(AjaxRequestTarget target) 			     
			    {	
				customizeControlGroupPageDialog.open(target);
			    }
		    });
	    container.add(customizeControlGroupPageDialog);
		    
		    // issue 53
	    container.add(motrPacLink = new IndicatingAjaxLink <Void>("openMotrpac") 
		    {			
			private static final long serialVersionUID = 1L; 
			@Override
			public boolean isEnabled()
				{
				if (originalWorklist.getItems().size() == 0)
					return false;
				else
			        return !worklist.getIs96Well();
				}
		
			
			@Override
			public void onClick(AjaxRequestTarget target) 			     
			    {	
				motrpacOptionsDialog.open(target);
			    }
		    });
		    /////////////////////////////////////////////////////
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
	
	private DropDownChoice buildChearBlankTypeDropdown(final String id)
		{
		DropDownChoice drp = new DropDownChoice(id, new PropertyModel(originalWorklist, "chearBlankType"), new LoadableDetachableModel<List<String>>()
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
				return originalWorklist.getItems().size() > 0 && !originalWorklist.getIs96Well();
				
				}
			};	
		drp.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForPoolTypeDropB", null));
		return drp;
		}
	
	// issue 169 
	protected AjaxCheckBox buildDefaultPool(String id)
	    {
	    AjaxCheckBox box = new AjaxCheckBox("defaultPool", new PropertyModel(originalWorklist, "defaultPool"))
		    {
		    @Override
		    public void onUpdate(AjaxRequestTarget target)
			    {
		    	// issue 253
		    	if (!originalWorklist.getDefaultPool())
			    	{
			    	if ( poolSpacingA > 0 && originalWorklist.getMasterPoolsAfter() == 2)
			    		originalWorklist.setMasterPoolsAfter(0);
			    	if (poolSpacingA > 0 &&  originalWorklist.getMasterPoolsBefore() == 2)
			    		originalWorklist.setMasterPoolsBefore(0);
			    	if (poolSpacingB > 0 &&  originalWorklist.getBatchPoolsAfter() == 1)
			    		originalWorklist.setBatchPoolsAfter(0);
			    	if (poolSpacingB > 0 &&  originalWorklist.getBatchPoolsBefore() == 1)
			    		originalWorklist.setBatchPoolsBefore(0);
			    	}
		    	else
		    		// issue 253
		    		{
				    if (poolSpacingA > 0 &&  (originalWorklist.getMasterPoolsAfter() == 2 || originalWorklist.getMasterPoolsAfter() == 0))
				        originalWorklist.setMasterPoolsAfter(2);
				    if (poolSpacingA > 0 &&  (originalWorklist.getMasterPoolsBefore() == 2 ||  originalWorklist.getMasterPoolsBefore() == 0 ))
				    	originalWorklist.setMasterPoolsBefore(2);
				    if (poolSpacingB > 0 &&  (originalWorklist.getBatchPoolsAfter() == 1 || originalWorklist.getBatchPoolsAfter() == 0))
				    	originalWorklist.setBatchPoolsAfter(1);
				    if (poolSpacingB > 0 &&  (originalWorklist.getBatchPoolsBefore() == 1 || originalWorklist.getBatchPoolsBefore() == 0))
				    	originalWorklist.setBatchPoolsBefore(1);
		    		}
		        if (originalWorklist.getMasterPoolsAfter() == 0  && ( originalWorklist.getNCE10Reps() > 0  || originalWorklist.getNCE20Reps() > 0 || originalWorklist.getNCE40Reps() > 0 ))
		    		target.appendJavaScript(StringUtils.makeAlertMessage("There are NCE values for an IDDA run but no after amount for Pool A.   The IDDA will not appear.  Please go to the customization box and set an after amount for Pool A."));
		    	target.add(this);
			    }		    
		    // issue 128
			@Override
			public boolean isEnabled() 
			    { 
				return originalWorklist.getOpenForUpdates(); 
			    }	
		    };
	    return box;
	    }
	
	// Issue 302
	// issue 207
	private DropDownChoice buildQuantityDropdown(final String id,  final String propertyName)
	    {
	    LoadableDetachableModel<List<String>> quantityModel = new LoadableDetachableModel<List<String>>()
		    {
	    	// issue 201
		    @Override
		    protected List<String> load() 
			    { 
			    Boolean doPoolSpacing = (propertyName != null && propertyName.startsWith("poolSpacingStr"));			
			    Boolean doInjections  = (propertyName != null && propertyName.startsWith("numberInjectionsStr"));
			    Boolean doInjectionsPool = (propertyName != null && propertyName.startsWith("numberInjectionsPoolStr"));
			    Boolean doInjectionsSB = (propertyName != null && propertyName.startsWith("numberInjectionsSBStr"));
			    Boolean doInjectionsPB = (propertyName != null && propertyName.startsWith("numberInjectionsPBStr"));
			    // issue 207
			    if (doPoolSpacing) 
			    	return availableSpacingQuantities;
			    else if (propertyName.contains("Injection"))
			    	return availableInjectionQuantities;
			    else if (originalWorklist.getIs96Well() && id.equals("standardsDrop"))
			    	{
			    	return availableQ96StrQuantities;
			    	}
			    else 
			        return availableStrQuantities;
			    }
		    };	
	    DropDownChoice drp = new DropDownChoice(id, new PropertyModel(this, propertyName), quantityModel)
		    {
		    public boolean isEnabled()
			    {
			    if (!originalWorklist.getOpenForUpdates()) return false;
			    // issue 215
			    // issue 255
			    if (id.equals("numberInjectionsDropPB") || id.equals("qcDrop1")  || id.equals("poolsDropB"))
			       return (originalWorklist.getItems().size() > 0  && !originalWorklist.getIs96Well());		   
			    else 
			    	return originalWorklist.getItems().size() > 0;
			    }
		    };
	    drp.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForQuantityDrop", null));
	    return drp;
	    }
	
	// issue 394
	private IndicatingAjaxLink buildBuildButton(String id, final WebMarkupContainer container , final WorklistSimple worklist , WorklistBuilderPanel wp)
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
	        	// issue 233
	        	wp.form.populateUnusedInj ();
	            originalWorklist.getControlCommentsMap().clear();
	        	worklist.getIddaStrList().clear();
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
	        	// issue 169
	        	// issue 253
	        	if (originalWorklist.getDefaultPool())
		        	{
		        	if (poolSpacingA > 0 &&  (originalWorklist.getMasterPoolsAfter() == 2 || originalWorklist.getMasterPoolsAfter() == 0))
		        		{
		        		originalWorklist.setMasterPoolsAfter(2);
		        		customizeControlGroupPageDialog.setMasterPoolsAfter(originalWorklist.getMasterPoolsAfter());
		        		}
		        	if (poolSpacingA > 0 &&  (originalWorklist.getMasterPoolsBefore() == 2 ||  originalWorklist.getMasterPoolsBefore() == 0 ))
		        		{
		        		originalWorklist.setMasterPoolsBefore(2);
		        		customizeControlGroupPageDialog.setMasterPoolsBefore(originalWorklist.getMasterPoolsBefore());
		        		}
		        	if (poolSpacingB > 0 &&  (originalWorklist.getBatchPoolsAfter() == 1 || originalWorklist.getBatchPoolsAfter() == 0))
		        		{
		        		originalWorklist.setBatchPoolsAfter(1);
		        		customizeControlGroupPageDialog.setBatchPoolsAfter(originalWorklist.getBatchPoolsAfter());
		        		}
		        	if (poolSpacingB > 0 &&  (originalWorklist.getBatchPoolsBefore() == 1 || originalWorklist.getBatchPoolsBefore() == 0))
		        		{
		        		originalWorklist.setBatchPoolsBefore(1);
		        		customizeControlGroupPageDialog.setBatchPoolsBefore(originalWorklist.getBatchPoolsBefore());
		        		}
		        	}
	        	else
	        		// issue 253
		    		{
	        		customizeControlGroupPageDialog.setMasterPoolsAfter(originalWorklist.getMasterPoolsAfter());
	        		customizeControlGroupPageDialog.setMasterPoolsBefore(originalWorklist.getMasterPoolsBefore());
	        		customizeControlGroupPageDialog.setBatchPoolsAfter(originalWorklist.getBatchPoolsAfter());
	        		customizeControlGroupPageDialog.setBatchPoolsBefore(originalWorklist.getBatchPoolsBefore());
		    		}
	        		// issue 17
	        	
	        	// issue 205 96wells
	        	worklist.setBothQCMPandMP (StringParser.parseId(poolTypeA).equals("CS00000MP") && StringParser.parseId(poolTypeB).equals("CS000QCMP") && poolSpacingA > 0 && poolSpacingB > 0 && !worklist.getIs96Well());    		
	        	if (StringParser.parseId(poolTypeA).equals(StringParser.parseId(poolTypeB)))
	        		{
	        		target.appendJavaScript(StringUtils.makeAlertMessage("Pool A and Pool B are both :" + StringParser.parseId(poolTypeB) + " Please make sure you choose a different pool for Pool A and Pool B"));
	        		return;
	        		}
	        	// issue 229
	        	for (WorklistItemSimple wi : originalWorklist.getItems())
	        		{
	        		if (wi.getRepresentsControl())
	        			originalWorklist.getControlCommentsMap().put(wi.getSampleName(), wi.getComments());
	        		}
	        	originalWorklist.clearControlGroups(); // issue 431
			    addStandardsToAgilentList(worklist);
			    		   
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
	        	List <WorklistItemSimple> tWI = new ArrayList <WorklistItemSimple> ();
		        try
		        	{
		           	for (WorklistItemSimple wi : originalWorklist.getItems())
		        		{
		        		if (!wi.getRepresentsControl())
		        			tWI.add(wi);
		        		}
		           	originalWorklist.rebuildEverything();	
		        	}
		        catch (Exception e)
		        	{
		        	e.printStackTrace();
		        	originalWorklist.getItems().clear();
		        	originalWorklist.clearControlGroups();
		        	originalWorklist.getItems().addAll(tWI);
		        	target.appendJavaScript(StringUtils.makeAlertMessage("This worklist will contain more than 6 plates with the added controls.  Controls have been cleared. Please start over using fewer controls"));
		        	refreshPage(target);
		        	return;
		        	}
		        
               if (wp.form.doesContainGivenPosition(worklist.getItems(), "P7"))
           	        {   
            		originalWorklist.getItems().clear();
		        	originalWorklist.getItems().addAll(tWI);
		        	target.appendJavaScript(StringUtils.makeAlertMessage("This worklist will contain more than 6 plates with the added controls.  The controls have been cleared. Please start over using fewer controls"));
		        	refreshPage(target);
		        	return;
		        	}   
		   	 // issue 229
			    for (WorklistItemSimple wi : originalWorklist.getItems())
	        		{
	        		if (wi.getRepresentsControl())
	        			wi.setComments(originalWorklist.getControlCommentsMap().get(wi.getSampleName()));
	        		}
		        		        
		        // issue 166
		      	// issue 153
	        	if (originalWorklist.countOfSamplesForItems(originalWorklist.getItems())+  (originalWorklist.buildControlTypeMap().get(null) != null ? originalWorklist.buildControlTypeMap().size()-1 : originalWorklist.buildControlTypeMap().size()  ) > (originalWorklist.getMaxStartPlate() * originalWorklist.getMaxItemsAsInt()))
					{
	        		String msg =  "alert('This worklist currently contains more than:" + originalWorklist.getMaxStartPlate() + " plates.  Therefore plate cycling will not be used." +  "')";
					target.appendJavaScript(msg); 
					}
			    worklist.updateSampleNamesArray();
			    // issue 212
			    
			    if (worklist.getIs96Well())
					{
				    int nPlateRows = 8, nPlateCols = 12;
				    plateListHandler = new PlateListHandler(nPlateRows, nPlateCols,false);	
					plateListHandler.condenseSortAndSpace(worklist.getItems());
					lgetItems = new ArrayList <WorklistItemSimple>  ();
					//lgetItems.addAll(worklist.getItems());
					int i = 0;
					// issue 212	
					///////////////////////////////////						
					plateListHandler.check96WellsUpdate(worklist.getItems());								
					// issue 212
					idsVsReasearcherNameMap =
				        sampleService.sampleIdToResearcherNameMapForExpId(worklist.getSampleGroup(0).getExperimentId());								
				    worklist.populateSampleName(worklist,idsVsReasearcherNameMap );						
					////////////////////////////////		
					} 
			    else 
			    	{
			    	int nPlateRows = 6, nPlateCols = 9;
				    plateListHandler = new PlateListHandler(nPlateRows, nPlateCols,false);
			    	}
			    plateListHandler.updateWorkListItemsMoved(worklist);
			    msWorklistWriter = new MsWorklistWriter (worklist, null);
			    worklist.getIddaStrList().clear();
			    if (!worklist.getIs96Well())
					msWorklistWriter.printOutIDDA(null, null, 0, worklist.getSelectedMode(), worklist.getItems().get(0).getOutputFileName(), false ); 
			    wp.form.agPanel.updateIddaList();
			    wp.form.grabUnusedInj ();
			    refreshPage(target);	
			    target.add(wp.form.agPanel.textAreaIdda);
		        }
	        };
        }
	
	private IndicatingAjaxLink buildClearButton(String id, final WebMarkupContainer container, WorklistBuilderPanel wp)
		{
		return new IndicatingAjaxLink<Void>(id)
			{
			@Override
			public void onClick(AjaxRequestTarget target)
				{
				originalWorklist.clearControlGroups();
				originalWorklist.updateSampleNamesArray();
				originalWorklist.setOpenForUpdates(true);
				originalWorklist.updatePlatePositions(); // issue 417 and 409
				originalWorklist.getIddaStrList().clear();
				wp.form.agPanel.getContainer().remove(wp.form.agPanel.textAreaIdda);
				wp.form.agPanel.getContainer().add( wp.form.agPanel.textAreaIdda = wp.form.agPanel.initIDDA(originalWorklist.getIddaStrList()));
				wp.form.agPanel.textAreaIdda.setOutputMarkupId(true);
				refreshPage(target);;
				}
			};
		}
	
	// issue 394 391 324
    protected void addStandardsToAgilentList(WorklistSimple worklist)
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
		// issue 207
		for (int i = 0; i < numberInjectionsSB; i++)
			{
			id = controlService.controlIdForNameAndAgilent("Injection - Solvent Blank (CS00000SB-Pre)");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// issue 255
		for (int i = 0; i < numberInjectionsPB; i++)
			{
			id = controlService.controlIdForNameAndAgilent("Injection - Process Blank (CS00000PB-Pre)");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		// issue 201
		for (int i = 0; i < numberInjections; i++)
			{
			if ("Urine".equals(originalWorklist.getChearBlankType()))
				id = controlService.controlIdForNameAndAgilent("Injection - urine");
			else if ("Plasma".equals(originalWorklist.getChearBlankType()))
				id = controlService.controlIdForNameAndAgilent("Injection - plasma");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		// issue 201
		for (int i = 0; i < numberInjectionsPool; i++)
			{	
			if (originalWorklist.getPoolTypeA().indexOf("CS00000MP") > -1)
				id = controlService.controlIdForNameAndAgilent("Injection - pool   (CS00000MP-Pre)");
			else if (originalWorklist.getPoolTypeA().indexOf("CS000BPM1") > -1)
				id = controlService.controlIdForNameAndAgilent("Injection - pool   (CS000BPM1-Pre)");
			else if (originalWorklist.getPoolTypeA().indexOf("CS000BPM2") > -1)
				id = controlService.controlIdForNameAndAgilent("Injection - pool   (CS000BPM2-Pre)");
			else if (originalWorklist.getPoolTypeA().indexOf("CS000BPM3") > -1)
				id = controlService.controlIdForNameAndAgilent("Injection - pool   (CS000BPM3-Pre)");
			else if (originalWorklist.getPoolTypeA().indexOf("CS000BPM4") > -1)
				id = controlService.controlIdForNameAndAgilent("Injection - pool   (CS000BPM4-Pre)");
			else 
				id = controlService.controlIdForNameAndAgilent("Injection - pool   (CS000BPM5-Pre)");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
	
		
		// issue 191
		// issue 212
		// issue 255
 		 alreadyBlank = false;
		 if (nStandards > 0)
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
		
		 if (nStandards > 0 )
		 	{
			id = controlService.controlIdForNameAndAgilent("Solvent Blank");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			alreadyBlank = true;
		 	}
		 
		// issue 13 2020
		//if (!worklist.getIs96Well())
		  	
		// if (nChearBlanks > 0 )
		//	 alreadyBlank = true;
		
		 if (nMatrixBlanks > 0 && !alreadyBlank)
		 	{
			id = controlService.controlIdForNameAndAgilent("Solvent Blank");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
		 	}
		 
		//if (!worklist.getIs96Well())
			for (int i = 0; i < nMatrixBlanks; i++)
				{
				id = controlService.controlIdForNameAndAgilent("Red Cross");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				alreadyBlank = false;
				}
	
			 if (nMatrixBlanks > 0 && !alreadyBlank)
			 	{
				id = controlService.controlIdForNameAndAgilent("Solvent Blank");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
			 	alreadyBlank = true;
			 	}
			 
	  // issue 255

            if  (nChearBlanks >= 1 && !alreadyBlank)
	            {
				id = controlService.controlIdForNameAndAgilent("Solvent Blank");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = 
						new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
	            }
			
		for (int i = 0; i < nChearBlanks; i++)
			{
			// issue 186
			if ("Urine".equals(originalWorklist.getChearBlankType()))
				id = controlService.controlIdForNameAndAgilent("Reference 1 - urine");
			else if ("Plasma".equals(originalWorklist.getChearBlankType()))
				id = controlService.controlIdForNameAndAgilent("Reference 1 - plasma");
			
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			alreadyBlank = false;
			}	
		 
		  if  (nChearBlanks >= 1 && !alreadyBlank)
          	{
			  
			id = controlService.controlIdForNameAndAgilent("Solvent Blank");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = 
					new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			alreadyBlank = true;
			
          	}
		  
// issue 255 first part of motrpac
		  if (!alreadyBlank && (worklist.getNGastroExercise() > 0 || 
				  worklist.getNGastroSedentary() > 0 ||
				 worklist.getNLiverExercise() > 0 ||
				 worklist.getNLiverSedentary() > 0 || 
				 worklist.getNAdiposeExercise() > 0 || 
				 worklist.getNAdiposeSedentary() > 0 || 
				 worklist.getNPlasmaExercise() > 0 || 
				 worklist.getNPlasmaSedentary() > 0 || 
				 worklist.getNLungExercise() > 0 ||
				 worklist.getNLungSedentary() > 0 || 
				 worklist.getNKidneyExercise() > 0 ||
				 worklist.getNKidneySedentary() > 0 || 
				 worklist.getNBrownAdiposeExercise() > 0 ||
				 worklist.getNBrownAdiposeSedentary() > 0 ||
				 worklist.getNHeartExercise() > 0 || 
				 worklist.getNHeartSedentary() > 0 || 
				 worklist.getNHippoCampusExercise() > 0 ||
				 worklist.getNHippoCampusSedentary() > 0  ||
				 worklist.getNMuscleHumanMale() > 0 ||
				 worklist.getNMuscleHumanFemale() > 0 ||
				 worklist.getNPlasmaHumanMale() > 0 || 
				 worklist.getNPlasmaHumanFemale() > 0 || 
				 worklist.getNRatG() > 0 ||
				 worklist.getNRatL() > 0 || 
				 worklist.getNRatA() > 0 ||
				 worklist.getNRatPlasma() > 0 ||
				 worklist.getNHumanMuscleCntrl() > 0 ||
				 worklist.getNRefStdA() > 0 ||
				 worklist.getNRefStdB() > 0 ||
				 worklist.getNRefStdC() > 0 || 
				  worklist.getNRefStdD() > 0 || 
				 worklist.getNRefStdE() > 0 ))
             {
			 id = controlService.controlIdForNameAndAgilent("Solvent Blank");
			 finalLabel = controlService.dropStringForIdAndAgilent(id);
			 WorklistControlGroup group3 = 
			     new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
			 group3.setStandardNotAddedControl(true);
			 originalWorklist.addControlGroup(group3);
             } 
		// issue 212
	//	if (!worklist.getIs96Well())
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
		// issue 212
		//if (!worklist.getIs96Well())
			for (int i = 0; i < worklist.getNGastroSedentary(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Gastrocnemius, Sedentary");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}
			
			// Issue 422
		// issue 212
	//	if (!worklist.getIs96Well())
			for (int i = 0; i < worklist.getNLiverExercise(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Liver, Exercise");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}
			
			// Issue 422
		// issue 212
		// if (!worklist.getIs96Well())
			for (int i = 0; i < worklist.getNLiverSedentary(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Liver, Sedentary");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}
			// Issue 422
		// issue 212
		// if (!worklist.getIs96Well())
			for (int i = 0; i < worklist.getNAdiposeExercise(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Adipose, Exercise");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}
					
			// Issue 422
		// issue 212
		// if (!worklist.getIs96Well())
			for (int i = 0; i < worklist.getNAdiposeSedentary(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Adipose, Sedentary");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}
			
			// Issue 422
		// issue 212
		// if (!worklist.getIs96Well())
			for (int i = 0; i < worklist.getNPlasmaExercise(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Plasma, Exercise");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}
			
			// Issue 422
		// issue 212
		//if (!worklist.getIs96Well())
			for (int i = 0; i < worklist.getNPlasmaSedentary(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Plasma, Sedentary");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}
	         // issue 22
		// issue 212
		//if (!worklist.getIs96Well())
			for (int i = 0; i < worklist.getNLungExercise(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Lung, Exercise");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}	
		// issue 212
		// if (!worklist.getIs96Well())
			for (int i = 0; i < worklist.getNLungSedentary(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Lung, Sedentary");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}
		// issue 212
		// if (!worklist.getIs96Well())
			for (int i = 0; i < worklist.getNKidneyExercise(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Kidney, Exercise");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}	
		// issue 212
		// if (!worklist.getIs96Well())
			for (int i = 0; i < worklist.getNKidneySedentary(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Kidney, Sedentary");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}
		
			//issue 33
		// issue 212
		//if (!worklist.getIs96Well())
			for (int i = 0; i < worklist.getNBrownAdiposeExercise(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Brown Adipose, Exercise");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}
		// issue 212
		//  if (!worklist.getIs96Well())
		    for (int i = 0; i < worklist.getNBrownAdiposeSedentary(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Brown Adipose, Sedentary");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}
		    
		// issue 212
		// if (!worklist.getIs96Well())
			for (int i = 0; i < worklist.getNHeartExercise(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Heart, Exercise");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}	
		
		// issue 212
		// if (!worklist.getIs96Well())
			for (int i = 0; i < worklist.getNHeartSedentary(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Heart, Sedentary");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}
		
		// issue 212
		// if (!worklist.getIs96Well())
			for (int i = 0; i < worklist.getNHippoCampusExercise(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Hippocampus, Exercise");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}	
		
		// issue 212
		// if (!worklist.getIs96Well())
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
					
			// issue 126
		// issue 212
		// if (!worklist.getIs96Well())
			for (int i = 0; i < worklist.getNMuscleHumanMale(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Muscle-Human : Male");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}
			
			// issue 126
		// issue 212
		//if (!worklist.getIs96Well())
			for (int i = 0; i < worklist.getNMuscleHumanFemale(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Muscle-Human : Female");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}
			
			// issue 193
		// issue 212
		//if (!worklist.getIs96Well())
			for (int i = 0; i < worklist.getNPlasmaHumanMale(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Plasma-Human: Male (CSMR81040)");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}
		
			// issue 193
		// issue 212
		//if (!worklist.getIs96Well())
			for (int i = 0; i < worklist.getNPlasmaHumanFemale(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Plasma-Human: Female (CSMR81030)");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}
			
		// issue 212
		// if (!worklist.getIs96Well())
			for (int i = 0; i < worklist.getNRatG(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("UM rat   gastrocnemius control");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}
			
			// issue 427
		// issue 212
		//if (!worklist.getIs96Well())
			for (int i = 0; i < worklist.getNRatL(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("UM rat   liver control");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}
			
			// issue 427
		// issue 212
		// if (!worklist.getIs96Well())
			for (int i = 0; i < worklist.getNRatA(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("UM rat   adipose control");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}
			
		// issue 212
		//if (!worklist.getIs96Well())
			for (int i = 0; i < worklist.getNRatPlasma(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("UM rat   plasma control");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}
			
			// issue 126
		// issue 212
		// if (!worklist.getIs96Well())
			for (int i = 0; i < worklist.getNHumanMuscleCntrl(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("UM Human muscle control");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}
			
			// issue 235
			/******************************/
			
			for (int i = 0; i < worklist.getNRefStdA(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("Adi RefStdA");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}
			
			for (int i = 0; i < worklist.getNRefStdB(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("Adi RefStdB");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}
			
			for (int i = 0; i < worklist.getNRefStdC(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("Adi RefStdC");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}
			
			for (int i = 0; i < worklist.getNRefStdD(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("Adi RefStdD");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}
			
			for (int i = 0; i < worklist.getNRefStdE(); i++)
				{
				id = controlService.controlIdForNameAndAgilent("Adi RefStdE");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				}
			/*******************************/	
			
			// issue 255 last part of motrpac
		     if ( (worklist.getNGastroExercise() > 0 || 
					  worklist.getNGastroSedentary() > 0 ||
						 worklist.getNLiverExercise() > 0 ||
						 worklist.getNLiverSedentary() > 0 || 
						 worklist.getNAdiposeExercise() > 0 || 
						 worklist.getNAdiposeSedentary() > 0 || 
						 worklist.getNPlasmaExercise() > 0 || 
						 worklist.getNPlasmaSedentary() > 0 || 
						 worklist.getNLungExercise() > 0 ||
						 worklist.getNLungSedentary() > 0 || 
						 worklist.getNKidneyExercise() > 0 ||
						 worklist.getNKidneySedentary() > 0 || 
						 worklist.getNBrownAdiposeExercise() > 0 ||
						 worklist.getNBrownAdiposeSedentary() > 0 ||
						 worklist.getNHeartExercise() > 0 || 
						 worklist.getNHeartSedentary() > 0 || 
						 worklist.getNHippoCampusExercise() > 0 ||
						 worklist.getNHippoCampusSedentary() > 0  ||
						 worklist.getNMuscleHumanMale() > 0 ||
						 worklist.getNMuscleHumanFemale() > 0 ||
						 worklist.getNPlasmaHumanMale() > 0 || 
						 worklist.getNPlasmaHumanFemale() > 0 || 
						 worklist.getNRatG() > 0 ||
						 worklist.getNRatL() > 0 || 
						 worklist.getNRatA() > 0 ||
						 worklist.getNRatPlasma() > 0 ||
						 worklist.getNHumanMuscleCntrl() > 0 ||
						 worklist.getNRefStdA() > 0 ||
						 worklist.getNRefStdB() > 0 ||
						 worklist.getNRefStdC() > 0 || 
						  worklist.getNRefStdD() > 0 || 
						 worklist.getNRefStdE() > 0 ))
	             {
		    	 
				 id = controlService.controlIdForNameAndAgilent("Solvent Blank");
				 finalLabel = controlService.dropStringForIdAndAgilent(id);
				 WorklistControlGroup group3 = 
				     new WorklistControlGroup(null, finalLabel, "1", "Before", firstSample, worklist);
				 group3.setStandardNotAddedControl(true);
				 originalWorklist.addControlGroup(group3);
	             alreadyBlank = true;
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
		
		
		// issue 255
		
		try 
			{
			for (int i = 0; i < numberInjectionsSB; i++)
				{
				id = controlService.controlIdForNameAndAgilent("Injection - Solvent Blank (CS00000SB-Post)");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				} 
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		
		
		// issue 255
		try 
			{
			for (int i = 0; i < numberInjectionsPB; i++)
				{
				id = controlService.controlIdForNameAndAgilent("Injection - Process Blank (CS00000PB-Post)");
				finalLabel = controlService.dropStringForIdAndAgilent(id);
				WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
				group3.setStandardNotAddedControl(true);
				originalWorklist.addControlGroup(group3);
				} 
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		
		// issue 422 for MotrPac		
		// Issue 422
		// Issue 427		
		// issue 126
		// issue 235
		// issue 13 20202
		// issue 191
		
		alreadyBlank = false;
		
				 // issue 255
		 if (nStandards > 0 )
		 	{
			id = controlService.controlIdForNameAndAgilent("Solvent Blank");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			
		 	}
		
		for (int i = nStandards - 1; i >= 0; i--)
			{
			id = controlService.controlIdForNameAndAgilent("Standard." + i);
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group2 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group2.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group2);
			}
		
		 if (nStandards > 0)
		 	{
			id = controlService.controlIdForNameAndAgilent("Solvent Blank");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			alreadyBlank = true;
		 	}
		
		
		if  (nMatrixBlanks >= 1 && !alreadyBlank)
	        {
			id = controlService
					.controlIdForNameAndAgilent("Solvent Blank");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = 
					new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
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
		    alreadyBlank = false;	
			}	
		
		if  (nMatrixBlanks >= 1 && !alreadyBlank)
	        {
			id = controlService.controlIdForNameAndAgilent("Solvent Blank");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = 
					new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			alreadyBlank = true;
	        }
		
		// issue 255
		if  (nChearBlanks >= 1 && !alreadyBlank)
	        {
			id = controlService.controlIdForNameAndAgilent("Solvent Blank");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = 
					new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			alreadyBlank = true;
	        }
		
		for (int i = 0; i < nChearBlanks; i++)
			{
			if ("Urine".equals(originalWorklist.getChearBlankType()))
				id = controlService.controlIdForNameAndAgilent("Reference 1 - urine");
			if ("Plasma".equals(originalWorklist.getChearBlankType()))
				id = controlService.controlIdForNameAndAgilent("Reference 1 - plasma");			
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			alreadyBlank = false;
			}
		
		
		
		if  (nChearBlanks >= 1 && !alreadyBlank)
	        {
			alreadyBlank = true;
			id = controlService.controlIdForNameAndAgilent("Solvent Blank");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = 
					new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
	        }
		
		////// where to move the motrpac stuff		
		/*********************************/
		/// MotrPac After
		 if (!alreadyBlank && (worklist.getNGastroExercise() > 0 || 
				  worklist.getNGastroSedentary() > 0 ||
					 worklist.getNLiverExercise() > 0 ||
					 worklist.getNLiverSedentary() > 0 || 
					 worklist.getNAdiposeExercise() > 0 || 
					 worklist.getNAdiposeSedentary() > 0 || 
					 worklist.getNPlasmaExercise() > 0 || 
					 worklist.getNPlasmaSedentary() > 0 || 
					 worklist.getNLungExercise() > 0 ||
					 worklist.getNLungSedentary() > 0 || 
					 worklist.getNKidneyExercise() > 0 ||
					 worklist.getNKidneySedentary() > 0 || 
					 worklist.getNBrownAdiposeExercise() > 0 ||
					 worklist.getNBrownAdiposeSedentary() > 0 ||
					 worklist.getNHeartExercise() > 0 || 
					 worklist.getNHeartSedentary() > 0 || 
					 worklist.getNHippoCampusExercise() > 0 ||
					 worklist.getNHippoCampusSedentary() > 0  ||
					 worklist.getNMuscleHumanMale() > 0 ||
					 worklist.getNMuscleHumanFemale() > 0 ||
					 worklist.getNPlasmaHumanMale() > 0 || 
					 worklist.getNPlasmaHumanFemale() > 0 || 
					 worklist.getNRatG() > 0 ||
					 worklist.getNRatL() > 0 || 
					 worklist.getNRatA() > 0 ||
					 worklist.getNRatPlasma() > 0 ||
					 worklist.getNHumanMuscleCntrl() > 0 ||
					 worklist.getNRefStdA() > 0 ||
					 worklist.getNRefStdB() > 0 ||
					 worklist.getNRefStdC() > 0 || 
					  worklist.getNRefStdD() > 0 || 
					 worklist.getNRefStdE() > 0 ))
            {
			id = controlService.controlIdForNameAndAgilent("Solvent Blank");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
		    WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			alreadyBlank = true;
            }
		for (int i = 0; i < worklist.getNRefStdE(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("Adi RefStdE");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		for (int i = 0; i < worklist.getNRefStdD(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("Adi RefStdD");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		for (int i = 0; i < worklist.getNRefStdC(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("Adi RefStdC");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		
		for (int i = 0; i < worklist.getNRefStdB(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("Adi RefStdB");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		for (int i = 0; i < worklist.getNRefStdA(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("Adi RefStdA");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		/*********************************/
		for (int i = 0; i < worklist.getNHumanMuscleCntrl(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("UM Human muscle control");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
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
		
	// issue 193
		for (int i = 0; i < worklist.getNPlasmaHumanFemale(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Plasma-Human: Female (CSMR81030)");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		for (int i = 0; i < worklist.getNPlasmaHumanMale(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Plasma-Human: Male (CSMR81040)");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
				
		// issue 126
		for (int i = 0; i < worklist.getNMuscleHumanFemale(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Muscle-Human : Female");
			finalLabel = controlService.dropStringForIdAndAgilent(id);
			WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			group3.setStandardNotAddedControl(true);
			originalWorklist.addControlGroup(group3);
			}
		
		for (int i = 0; i < worklist.getNMuscleHumanMale(); i++)
			{
			id = controlService.controlIdForNameAndAgilent("MoTrPAC -   Muscle-Human : Male");
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
        
        // issue 33
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
		
		/// MotrPac After
		 if (worklist.getNGastroExercise() > 0 || 
				  worklist.getNGastroSedentary() > 0 ||
					 worklist.getNLiverExercise() > 0 ||
					 worklist.getNLiverSedentary() > 0 || 
					 worklist.getNAdiposeExercise() > 0 || 
					 worklist.getNAdiposeSedentary() > 0 || 
					 worklist.getNPlasmaExercise() > 0 || 
					 worklist.getNPlasmaSedentary() > 0 || 
					 worklist.getNLungExercise() > 0 ||
					 worklist.getNLungSedentary() > 0 || 
					 worklist.getNKidneyExercise() > 0 ||
					 worklist.getNKidneySedentary() > 0 || 
					 worklist.getNBrownAdiposeExercise() > 0 ||
					 worklist.getNBrownAdiposeSedentary() > 0 ||
					 worklist.getNHeartExercise() > 0 || 
					 worklist.getNHeartSedentary() > 0 || 
					 worklist.getNHippoCampusExercise() > 0 ||
					 worklist.getNHippoCampusSedentary() > 0  ||
					 worklist.getNMuscleHumanMale() > 0 ||
					 worklist.getNMuscleHumanFemale() > 0 ||
					 worklist.getNPlasmaHumanMale() > 0 || 
					 worklist.getNPlasmaHumanFemale() > 0 || 
					 worklist.getNRatG() > 0 ||
					 worklist.getNRatL() > 0 || 
					 worklist.getNRatA() > 0 ||
					 worklist.getNRatPlasma() > 0 ||
					 worklist.getNHumanMuscleCntrl() > 0 ||
					 worklist.getNRefStdA() > 0 ||
					 worklist.getNRefStdB() > 0 ||
					 worklist.getNRefStdC() > 0 || 
					 worklist.getNRefStdD() > 0 || 
					 worklist.getNRefStdE() > 0 )
	           {
			   id = controlService.controlIdForNameAndAgilent("Solvent Blank");
			   finalLabel = controlService.dropStringForIdAndAgilent(id);
			   WorklistControlGroup group3 = new WorklistControlGroup(null, finalLabel, "1", "After", lastSample, worklist);
			   group3.setStandardNotAddedControl(true);
			   originalWorklist.addControlGroup(group3);
	           }
		 
		 
		 
		/**************  end of motrpac after ******/
		///// move motorpac here
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
						// issue 13 2020
						if (!StringUtils.isEmptyOrNull(nProcessBlanksStr))
							nProcessBlanks = Integer.parseInt(nProcessBlanksStr);		
						if (!StringUtils.isEmptyOrNull(nMatrixBlanksStr))
							nMatrixBlanks = Integer.parseInt(nMatrixBlanksStr);						
						if (!StringUtils.isEmptyOrNull(nChearBlanksStr))
							nChearBlanks = Integer.parseInt(StringParser.parseName(nChearBlanksStr));
						if (!StringUtils.isEmptyOrNull(numberInjectionsStr))
							numberInjections = Integer.parseInt(StringParser.parseName(numberInjectionsStr));
						if (!StringUtils.isEmptyOrNull(numberInjectionsPoolStr))
							numberInjectionsPool = Integer.parseInt(StringParser.parseName(numberInjectionsPoolStr));
						if (!StringUtils.isEmptyOrNull(numberInjectionsSBStr))
							numberInjectionsSB = Integer.parseInt(StringParser.parseName(numberInjectionsSBStr));
						if (!StringUtils.isEmptyOrNull(numberInjectionsPBStr))
							numberInjectionsPB = Integer.parseInt(StringParser.parseName(numberInjectionsPBStr));
						// issue 169
						if (originalWorklist.getDefaultPool())
							{
							if (poolSpacingA == 0)
								{						
								if (originalWorklist.getMasterPoolsAfter() <= 1 )	
									originalWorklist.setMasterPoolsAfter(0);
								if (originalWorklist.getMasterPoolsBefore() <= 1 )	
									originalWorklist.setMasterPoolsBefore(0);		
								}
							if (poolSpacingB == 0)
								{						
								if (originalWorklist.getBatchPoolsAfter() <= 1 )	
									originalWorklist.setBatchPoolsAfter(0);
								if (originalWorklist.getBatchPoolsBefore() <= 1 )	
									originalWorklist.setBatchPoolsBefore(0);		
								}
							}
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
	     target.add();
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
	
// issue 170	
	private String buildHTMLSetString(int start, int end, String valStr)
		{
		int i;
		String htmlStr = "";
		for (i=start;i<=end;i++)
			htmlStr = htmlStr + "document.getElementById(" + "\"" + Integer.toString(i) + "\""+ ").selectedIndex =" + valStr  + ";";
		return htmlStr;
		} 
	}

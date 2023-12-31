////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  WorklistBuilderPanel.java
//  Written by Jan Wigginton
//  February 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

import edu.umich.brcf.metabolomics.layers.dto.GeneratedWorklistDTO;
import edu.umich.brcf.metabolomics.layers.dto.GeneratedWorklistItemDTO;
import edu.umich.brcf.metabolomics.layers.service.GeneratedWorklistService;
import edu.umich.brcf.metabolomics.layers.service.InstrumentService;
import edu.umich.brcf.shared.layers.service.SampleAssayService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.panels.utilitypanels.ValidatingAjaxExcelDownloadLink;
import edu.umich.brcf.shared.util.METWorksException;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.behavior.FocusOnLoadBehavior;
import edu.umich.brcf.shared.util.interfaces.IWriteableSpreadsheetReturnStream;
import edu.umich.brcf.shared.util.sheetwriters.MsWorklistWriter;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;
import edu.umich.brcf.shared.util.widgets.METWorksAjaxUpdatingDateTextField;

public class WorklistBuilderPanel extends Panel
	{
	@SpringBean
	InstrumentService instrumentService;
	
	@SpringBean
	GeneratedWorklistService generatedWorklistService;
	
	@SpringBean
	SampleAssayService sampleAssayService;
	@SpringBean
	SampleService sampleService;
	String prevStartPlate;
	private static final long serialVersionUID = -2719126649022550590L;
	private FeedbackPanel feedback;
    private int controlsLimit = 495;
	static final String WORKLIST_DATE_FORMAT = "MM/dd/yy";
    AjaxCheckBox randomizationTypeBox ;
    AjaxCheckBox changeDefaultInjVolumeBox ;
    AjaxCheckBox change96WellBox ;
    WorklistBuilderPanel worklistBuilder = this;
    public List <String> workListDataW = new ArrayList <String> ();
    WorklistBuilderPanel wp = this;
    List <WorklistItemSimple> lgetItems;
    String prevStandardString = "";
    public WorklistSimple worklistg;
    public WorklistSimple worklist;
	public List <WorklistItemSimple> lilmovedlist = new ArrayList<WorklistItemSimple> ();
    Map<String, String> idsVsReasearcherNameMap = new HashMap<String, String> ();
   // PlatePreviewPage plateListView;
    PlatePreviewPage platePreviewPage;
   // PageableListView plateListViewWorkList;
    AjaxPagingNavigator ajaxPagingNavigatorWorkList;
    WorklistBuilderForm form;
    IndicatingAjaxLink pPreview;
    List <String> itemsUpdateInj = new ArrayList <String> ();
    List <String> titemsUpdateInj = new ArrayList <String> ();

	public WorklistBuilderPanel()  { this(""); }
	
	public WorklistBuilderPanel(String id)
		{
		this(id, null);
		}

	public WorklistBuilderPanel(String id, WebPage pg)
		{
		this(id, pg, null);
		}
	
	public WorklistBuilderPanel(String id, WebPage pg, PrepData prepData)
		{
		super(id);
		feedback = new FeedbackPanel("feedback");
		feedback.setOutputMarkupId(true);
		add(feedback);
		form = new WorklistBuilderForm("worklistForm", prepData);
		
		add(form);
		}

	public final class WorklistBuilderForm extends Form
		{		
		// issue 416
		String selectedPlatform = null, selectedInstrument = null ;		
		// issue 153
		DropDownChoice<String> selectedPlatformDrop, selectedInstrumentDrop, selectedModeDrop, startPlateDrop;
		//List<String> availablePlates  = Arrays.asList(new String[] {"1", "2", "3", "4"});
		List<String> availablePlatforms = Arrays.asList(new String[] {"Agilent", "ABSciex" });
		List<String> availableModes = Arrays.asList(new String[] { "Positive", "Negative", "Positive + Negative" });
		List<String> agilentInstruments,  absciexInstruments; 		
		METWorksAjaxUpdatingDateTextField dateFld;
		TextField <String> defaultMethodFld, defaultItemsPerListFld;
		TextField <Integer> defaultInjectionVolFld;
		// issue 166
		TextField <Integer> startSequenceFld;// issue 166
		WorklistAgilentPanel agPanel;
		WorklistABSciexPanel abPanel;
		AddSamplesPanel addSamplesPanel;
		AddControlsPanel addControlsPanel;
		AutoAddControlsPanel addGroupsPanel;
		PlateListHandler plateListHandler;
		WebMarkupContainer containerDefault, containerOther;
		private Boolean  controlsVisible = false, samplesVisible = false, groupsVisible = false; 
		private IndicatingAjaxLink hideAllButton, controlsVisibleButton, samplesVisibleButton, groupsVisibleButton;
		private IndicatingAjaxLink randomizeSamplesButton;
		private ValidatingAjaxExcelDownloadLink downloadListButton;
		String msg = "";
		private ModalWindow modal1; 
		final PlatePreviewPage platePreviewPageDialog;	
		List <String> availableInstruments= new ArrayList <String> ();
		WorklistBuilderForm(String id, PrepData prepData)
			{			
			super(id);
			setMultiPart(true);
			setOutputMarkupId(true);
			agilentInstruments = instrumentService.getLabelledListOfAnalyticalForAgilent();
			absciexInstruments = instrumentService.getLabelledListOfAnalyticalForAbsciex();			
			// DO NOT MOVE THESE
			worklist = prepData == null ? new WorklistSimple("Mock Worklist", "ABSciex") : prepData.grabAsWorklist();
			setSelectedPlatform("");
			///////////////////////////////			
			 platePreviewPageDialog = new PlatePreviewPage (worklist.getBothQCMPandMP(),  worklist.getItems(),  worklist.getUseCarousel(), wp, "platePreviewPageDialog", "Plate Preview", worklist)		
			    { // NOSONAR
				private static final long serialVersionUID = 1L;	
			    @Override
				public void onClick(AjaxRequestTarget target, DialogButton button)
					{	
			    	// issue 205
			    	if (button.getName().equals("submit2") )
			    		{
			    		worklist.getControlsMovedMap().clear();
			    		plateListHandler.updateWorkListItemsMoved(worklist);
			    		if (worklist.getIs96Well())
			    			plateListHandler.check96WellsUpdate(worklist.getItems());
			    		else
			    			worklist.rebuildEverything();	
			    		}	    		
			    	super.close(target, button);
					}    		    
				@Override
				public Form<?> getForm() 
					{
					// TODO Auto-generated method stub
				    form.setMultiPart(true);
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
					populateUnusedInj ();
					colorMap.clear();
				    this.rlf.clearHolding ();               	
					firstTimeOpening = true;
					rlf.setMultiPart(true);
					worklistg = worklist;		    
					AjaxRequestTarget target = (AjaxRequestTarget) handler;
					nRowsNeeded = (int) Math.ceil((spacedg.size() > 0 ? spacedg.size() : worklist.getItems().size()) / (nItemsPerRow * 1.0));
					nPlatesNeeded = (int) Math.ceil(nRowsNeeded / (nItemsPerCol * 1.0));
					nRowsToCreate = nPlatesNeeded * nItemsPerCol;					
					itemMatrix = null;
					itemMatrix = new WorklistItemSimpleMatrix(nRowsToCreate, nItemsPerRow, spacedg); 
					itemMatrix.rows.clear();
					target.add(this.form);
					target.add(this);
					// issue 205				
					if (worklist.getIs96Well())
						{
						nItemsPerRow = worklist.getUseCarousel() ? 10 : 12;
						nItemsPerCol = worklist.getUseCarousel() ? 10 : 8;
						}
					else
						{
						nItemsPerRow = worklist.getUseCarousel() ? 10 : 9;
						nItemsPerCol = worklist.getUseCarousel() ? 10 : 6;
						}
					if (plateListView == null)
						{							
						try
							{
							plateListView.remove();
							}
						catch (Exception e)
							{
							}
						rlf.add (plateListView);					
						plateListView.setOutputMarkupId(true);						
						try
							{
							rlf.add(ajaxPagingNavigator = new AjaxPagingNavigator("navigator", plateListView));// Issue 283
							}
						catch (Exception e)
							{
							rlf.replace(ajaxPagingNavigator = new AjaxPagingNavigator("navigator", plateListView));
							}
						}
					else 
						{	
						try
							{
							plateListView.remove();				
							}
						catch (Exception e)
							{
							}
						rlf.buildPlateListView(nItemsPerRow, nItemsPerCol, worklist.getItems(), worklist);
						rlf.add  (plateListView);
						plateListView.setOutputMarkupId(true);
						try
							{
							rlf.add(ajaxPagingNavigator = new AjaxPagingNavigator("navigator", plateListView));// Issue 283
							}
						catch (Exception e)
							{
							rlf.remove(ajaxPagingNavigator);				        
							rlf.add(ajaxPagingNavigator = new AjaxPagingNavigator("navigator", plateListView));
							}
												
						}
					target.add(this.form);				
					target.add(this);
					hasBeenOpened = true;
					} 
				@Override
				public void onClose(IPartialPageRequestHandler handler, DialogButton button) 
				    {
					setMultiPart(true);
				    AjaxRequestTarget target = (AjaxRequestTarget) handler;
				    target.add(wpMain);
				    if (!worklist.getIs96Well())
				    	{
				    	plateListHandler.addLastControlRepeater(worklist);
				    	plateListHandler.updateWorkListItemsMoved(worklist);
				    	// issue 229
				    	agPanel.updateIddaList();
				    	
				    	}
				    // issue 205 get rid of 910 911 and 912
				    else 
				    	plateListHandler.check96WellsUpdate(worklist.getItems());
				    grabUnusedInj ();
				    }
					
				@Override
				public void onConfigure(JQueryBehavior behavior)
				    {
					// class options //
					behavior.setOption("autoOpen", false);
					behavior.setOption("modal", this.isModal());
					//behavior.setOption("resizable", this.isResizable());
					behavior.setOption("resizable", true);
					behavior.setOption("width", 1050);
					behavior.setOption("title", Options.asString(this.getTitle().getObject()));
					behavior.setOption("height", 650);
					//behavior.setOption("height", 1000);
				    behavior.setOption("autofocus", false);
				    }	
			    @Override
				protected void onSubmit(AjaxRequestTarget target, DialogButton button) 
				    {
			    	// TODO Auto-generated method stub	
			    	target.add(feedback);
			    	target.add(this);
			    	target.add(this.form);
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
					dialogButtonList.add( new DialogButton("submit2", "ResetDefault")
							{
							@Override
							public boolean isEnabled()
								{
								// issue 229
								return worklist.getOpenForUpdates() && isPlatformChosenAs("agilent");
								}						
							}
							) ;
					dialogButtonList.add( new DialogButton("submit3", "Done")) ;
					//dialogButtonList.get(0).setEnabled(worklist.getOpenForUpdates());
					return dialogButtonList;
				    }				
			    };			
		    add(platePreviewPageDialog);	
				
		    add(pPreview = new IndicatingAjaxLink <Void>("openPreview") 
			    {			
				private static final long serialVersionUID = 1L; 
				@Override
				public boolean isEnabled()
					{
					// issue 229
					return (worklist.getItems().size()> 0) && isPlatformChosenAs("agilent") && !worklist.getUseCarousel();
					}
				@Override
				public void onClick(AjaxRequestTarget target) 			     
				    {	
					platePreviewPageDialog.open(target);
				    }
			    });
		    pPreview.setOutputMarkupId(true);
			//modal1 = ModalCreator.createModalWindow("modal1", 800, 320);
			//add(modal1);
			
			int nPlateRows = 8, nPlateCols = 12;
			plateListHandler = new PlateListHandler(nPlateRows, nPlateCols,false);
			containerDefault = new WebMarkupContainer("containerDefault");

			// Platform fields		
			containerDefault.add(selectedPlatformDrop = buildPlatformDropdown("selectedPlatformDrop"));
			containerDefault.add(selectedInstrumentDrop = buildInstrumentDropdown("selectedInstrumentDrop"));
			containerDefault.add(selectedModeDrop = buildModeDropdown("selectedModeDrop"));
			containerDefault.add(buildPlatePosDropdown("startPos", "startPos"));
			containerDefault.add(buildPlatePosDropdown("endPos", "endPos"));
			containerDefault.add(startPlateDrop = buildStartPlateDropDown("selectedStartPlateDropDown"));
			containerDefault.add(startSequenceFld = buildStartSequenceFld("startSequence"));//issue 166			
			containerDefault.add(randomizationTypeBox = buildRandomizeByPlate("randomizeByPlate"));// issue 416
			containerDefault.add(change96WellBox = build96Well("96Well"));// issue 416			
			containerDefault.add(changeDefaultInjVolumeBox = buildChangeDefaultInjVolume("changeDefaultInjVolume"));// issue 179			
			selectedPlatformDrop.add(new FocusOnLoadBehavior());
			// Default fields
			containerDefault.add(defaultInjectionVolFld = buildDefaultInjectionVolFld("defaultInjectionVol"));
			containerDefault.add(defaultMethodFld = buildDefaultMethodFld("defaultMethod"));
			containerDefault.add(dateFld = buildDateField("runDate"));
			dateFld.setRequired(true);
			add(containerDefault);
			containerDefault.setOutputMarkupId(true);
			
			// Agilent
			add(agPanel = buildAgilentPanel("worklist_ag"));
			agPanel.setOutputMarkupId(true);
			agPanel.setOutputMarkupPlaceholderTag(true);

			// Absciex
			add(abPanel = buildABSciexPanel("worklist_ab"));
			abPanel.setOutputMarkupId(true);
			abPanel.setOutputMarkupPlaceholderTag(true);
			containerOther = new WebMarkupContainer("containerOther");
			containerOther.add(addSamplesPanel = buildAddSamplesPanel("add_samples"));
			addSamplesPanel.setOutputMarkupId(true);
			addSamplesPanel.addSibContainer(abPanel.getContainer());
			addSamplesPanel.addSibContainer(agPanel.getContainer());
			addSamplesPanel.addSibContainer(this);// issue 329
			// Add controls
			containerOther.add(addControlsPanel = buildAddControlsPanel("add_controls"));
			addControlsPanel.setOutputMarkupId(true);
			addControlsPanel.addSibContainer(abPanel.getContainer());
			addControlsPanel.addSibContainer(agPanel.getContainer());

			containerOther.add(addGroupsPanel = buildAddControlGroupsPanel("add_groups"));
			addGroupsPanel.setOutputMarkupId(true);
			addGroupsPanel.addSibContainer(abPanel.getContainer());
			addGroupsPanel.addSibContainer(agPanel.getContainer());
			addGroupsPanel.addSibContainer(addControlsPanel);
			addGroupsPanel.addSibContainer(addControlsPanel.getContainer());
			addSamplesPanel.addSibContainer(addControlsPanel.getContainer());			
			add(containerOther);
			containerOther.setOutputMarkupId(true);			
			add(hideAllButton = buildVisibilityButton("hideAllButton", "hideall"));
			add(samplesVisibleButton = buildVisibilityButton("samplesVisibleButton", "samples"));
			add(controlsVisibleButton = buildVisibilityButton("controlsVisibleButton", "controls"));
			add(groupsVisibleButton = buildVisibilityButton("groupsVisibleButton", "groups"));
			add(randomizeSamplesButton = buildRandomizeButton("randomizeButton"));
			add(downloadListButton = buildExcelDownloadLink("downloadLink", worklist));
			addGroupsPanel.addSibContainer(controlsVisibleButton);			
			agPanel.addSibContainer(addControlsPanel);
			abPanel.addSibContainer(addControlsPanel);	
			add (confirmBehavior);
			}
	
		// Issue 464
		private IndicatingAjaxLink buildVisibilityButton(String id, final String property)
			{
			// Issue 39
			IndicatingAjaxLink link = new IndicatingAjaxLink <Void>(id)
				{
				@Override
				public boolean isEnabled() 
					{
					Boolean isOpen = ((isPlatformChosenAs("agilent") && worklist.getOpenForUpdates()) || "samples".equals(property));
					// Issue 285
					if ("controls".equals(property) && worklist.getIs96Well())	
						return false;
						
					if ("groups".equals(property) && isPlatformChosenAs("absciex"))
						isOpen = false;
					if ("controls".equals(property) && isPlatformChosenAs("absciex"))
						isOpen = true;
					
					if ("hideall".equals(property))
						isOpen = true;
					// issue 126
					if (!worklist.getOpenForUpdates())
						isOpen = false;					
					return isOpen;
					}
			        				
				@Override 
				public boolean isVisible() { return true; } 
				//return (isPlatformChosenAs("agilent") || "controls".equals(property)  
				//|| "samples".equals(property)); }

				@Override
				public void onClick(AjaxRequestTarget target)
					{
					switch (property)
						{
						case "controls" :
							// issue 285
							if (worklist.countGroups(true) > controlsLimit)
							    {
								target.appendJavaScript(StringUtils.makeAlertMessage("The number of controls:" + Integer.toString(worklist.countGroups(true)) + " is greater than the threshold limit of: " + Integer.toString(controlsLimit) + " If you have any questions contact Julie Keros julieker@umich.edu"));
							    return;
							    }
							controlsVisible = true; samplesVisible = false; groupsVisible = false;
							addControlsPanel.controlGroupsList = worklist.getControlGroupsList();
							// issue 212
							target.add(change96WellBox);
							break;
					
						case "samples" : 
							controlsVisible = false; samplesVisible = true; groupsVisible = false; 
							addControlsPanel.controlGroupsList = worklist.getControlGroupsList();					
							target.add(change96WellBox);
							break;
							
						case "groups" :					
							controlsVisible = false; samplesVisible = false; groupsVisible = true;
							addControlsPanel.controlGroupsList = worklist.getControlGroupsList();
							target.add(change96WellBox);
							break;
						
						default :
							controlsVisible = false; samplesVisible = false; groupsVisible = false;
						}
					
					target.add(addControlsPanel);
					target.add(addSamplesPanel);
					target.add(controlsVisibleButton);
					target.add(samplesVisibleButton);
					target.add(groupsVisibleButton);
					target.add(hideAllButton);
					target.add(containerOther);
					}
								
				@Override
				protected void onComponentTag(ComponentTag tag)
					{
					super.onComponentTag(tag);					
					Boolean isOpen = ((isPlatformChosenAs("agilent") && worklist.getOpenForUpdates()));
					if ("groups".equals(property) && isPlatformChosenAs("absciex"))
						isOpen = false;
					if ("controls".equals(property) && isPlatformChosenAs("absciex"))
						isOpen = true;
					 if ("samples".equals(property) || "hideall".equals(property))
					    isOpen = true;

					 String label = isOpen ? " color : black" : "color : grey";
					
					if (isOpen)
						switch (property)
							{
							case "controls" :
								if (controlsVisible)
							    	label = "color : green";
								break;
								
							case "samples" : 
								if (samplesVisible)
									label = "color : green";
								break;
								
								
							case "groups" :
								if (groupsVisible)
							    	label = "color : green";
								break;	
							
							case "hideall" : 
								if (!groupsVisible && !samplesVisible && !controlsVisible)
									label = "color : green";
								break;
									
							default : 
							}
					
					tag.put("style", label);
					}
				};
				
			link.setOutputMarkupId(true);	
			return link;
			}
		
		// issue 416		
		protected AjaxCheckBox buildRandomizeByPlate(String id)
		    {
		    AjaxCheckBox box = new AjaxCheckBox("randomizeByPlate", new PropertyModel(worklist, "randomizeByPlate"))
			    {
			    @Override
			    public void onUpdate(AjaxRequestTarget target)
				    {
				    }
			    
			    // issue 128
				@Override
				public boolean isEnabled() 
				    { 
					return worklist.getOpenForUpdates(); 
				    }	
			    };
		    return box;
		    }
		
		// issue 212
		protected AjaxCheckBox build96Well(String id)
		    {
		    AjaxCheckBox box = new AjaxCheckBox(id, new PropertyModel(worklist, "is96Well"))
			    {
			    @Override
			    public void onUpdate(AjaxRequestTarget target)
				    {
				    }
			    
			    // issue 128
				@Override
				public boolean isEnabled() 
				    { 
					return  worklist.getOpenForUpdates() && worklist.getItems().size() > 0 && worklist.getSelectedPlatform().equals("agilent") && !controlsVisible && !worklist.getUseCarousel(); // issue 212;
				    }	
			    };
				box.add(this.buildStandardFormComponentUpdateBehavior("change", "update96Well"));
				
				return box;
		    }
		
		// issue 179		
		protected AjaxCheckBox buildChangeDefaultInjVolume(String id)
		    {
		    AjaxCheckBox box = new AjaxCheckBox("changeDefaultInjVolume", new PropertyModel(worklist, "changeDefaultInjVolume"))
			    {
			    @Override
			    public void onUpdate(AjaxRequestTarget target)
				    {
				    }
			    
			    // issue 128
				@Override
				public boolean isEnabled() 
				    { 
					return worklist.getOpenForUpdates(); 
				    }	
			    };
			box.add(this.buildStandardFormComponentUpdateBehavior("change", "updateChangeDefaultInjVol"));
			return box;
		    }
		
			// Issue 464
		private void setPageDimensions(ModalWindow modal1, double pctWidth, double pctHeight)
			{
			int pageHeight = ((MedWorksSession) getSession()).getClientProperties().getBrowserHeight();
			modal1.setInitialHeight(((int) Math.round(pageHeight * pctHeight)));
			int pageWidth = ((MedWorksSession) getSession()).getClientProperties().getBrowserWidth();
			modal1.setInitialWidth(((int) Math.round(pageWidth * pctWidth)));
			}
		
		
		private DropDownChoice buildPlatformDropdown(String id)
			{
			DropDownChoice drp = new DropDownChoice(id, new PropertyModel(this, "selectedPlatform"), availablePlatforms)
				{
				// issue 128
				@Override
				public boolean isEnabled() 
				    { 
					return worklist.getOpenForUpdates(); 
				    }	
				};
			setSelectedPlatform("Agilent");
			drp.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForPlatform"));
			return drp;
			}

		// issue 153
		private DropDownChoice buildStartPlateDropDown(String id)
			{
			DropDownChoice drp = new DropDownChoice(id, new PropertyModel(worklist, "startPlate"), populateAvailablePlates())
				{
				// issue 128
				//@Override
				@Override
				public boolean isEnabled() 
				    { 
					return worklist.getOpenForUpdates() && !worklist.getIs96Well(); 
				    }	
								
				};
			drp.add(this.buildStandardFormComponentUpdateBehavior("change", "updateStartPlate"));	
			return drp;
		}
		
		public void setSelectedPlatform(String p)
			{
			selectedPlatform = p;
			if (worklist != null)
				{
				worklist.setSelectedPlatform(p);
				if (isPlatformChosenAs("absciex")) setSelectedInstrument("IN0024 (LIPIDS)");
				}
			}
	
		public String getSelectedPlatform()
			{
			return selectedPlatform;
			}
		
		private boolean isPlatformChosen()
			{
			return (isPlatformChosenAs("absciex") || isPlatformChosenAs("agilent"));
			}
		
		private boolean isPlatformChosenAs(String platform)
			{
			String plat = worklist.getSelectedPlatform().trim();
			return plat.equalsIgnoreCase(platform);
			}

		// issue 128
		private DropDownChoice buildInstrumentDropdown(String id)
			{
			DropDownChoice drp = new DropDownChoice(id, new PropertyModel<String>(this, "selectedInstrument"), new PropertyModel(this, "availableInstruments"))
			    {
				@Override
				public boolean isEnabled() 
				    { 
					return worklist.getOpenForUpdates(); 
				    }				
				};		
			drp.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForInstrument"));
			return drp;
			}
	
		// issue 128
		private DropDownChoice buildModeDropdown(String id)
			{
			DropDownChoice drp = new DropDownChoice(id, new PropertyModel<String>(worklist, "selectedMode"), new PropertyModel(this, "availableModes"))
			    {
				@Override
				public boolean isEnabled() 
				    { 
					return worklist.getOpenForUpdates(); 
				    }				
				};	
			drp.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForMode"));
			return drp;
			}
		
		public List<String> getAvailableInstruments()
			{
			return (isPlatformChosenAs("agilent") ? agilentInstruments : absciexInstruments);
			}
		
		// issue 247
		public void setAvailableInstruments( List<String> availableInstruments)
			{
			this.availableInstruments =  availableInstruments ;
			}
		
		private boolean isInstrumentChosen()
			{
			return (this.getSelectedInstrument() != null && !this.getSelectedInstrument().equals(""));
			}

		public void setSelectedInstrument(String p)
			{
			// TO DO VALIDATE HERE
			selectedInstrument = p;
			if (worklist != null)
				worklist.setSelectedInstrument(p);
			}

		public String getSelectedInstrument()
			{
			return selectedInstrument;
			}
		
		// Defaults
		private METWorksAjaxUpdatingDateTextField buildDateField(String id)
			{
			METWorksAjaxUpdatingDateTextField dateFld =  new METWorksAjaxUpdatingDateTextField(id, new PropertyModel<String>(worklist, "runDate"), "change")
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target)  { }
			
				@Override
				protected void onInvalid() { worklist.setRunDate(DateUtils.dateAsFullString(DateUtils.todaysDate())); }
				
				// issue 128
				public boolean isEnabled() 
					{ 
					return (isPlatformChosen() && worklist.getOpenForUpdates());
					}
				};
						
			dateFld.setRequired(true);
			dateFld.setDefaultStringFormat(WorklistBuilderPanel.WORKLIST_DATE_FORMAT);
			dateFld.setLabel(new Model<String>("Date"));
			dateFld.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForDate"));
			return dateFld;
			}
	
		private TextField<Integer> buildDefaultInjectionVolFld(String id)
			{
			// issue 128
			TextField<Integer> fld = new TextField<Integer>(id, new PropertyModel<Integer>(worklist, "defaultInjectionVol"))
			    {
				@Override
				public boolean isEnabled() 
				    { 
					return worklist.getOpenForUpdates() && worklist.getChangeDefaultInjVolume(); 
				    }				
				};
			fld.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForInjectionVol"));
			return fld;
			}
		
		// issue 166
		private TextField<Integer> buildStartSequenceFld(String id)
			{
			// issue 128
			TextField<Integer> fld = new TextField<Integer>(id, new PropertyModel<Integer>(worklist, "startSequence"))
			    {
				@Override
				public boolean isEnabled() 
				    { 
					return worklist.getOpenForUpdates(); 
				    }				
				};
			fld.add(this.buildStandardFormComponentUpdateBehavior("change", "updateStartSequence"));
			return fld;		
			}
	
		private TextField<String> buildDefaultMaxItemsFld(String id)
			{
			TextField<String> fld = new TextField<String>(id, new PropertyModel<String>(worklist, "maxItems"));
			fld.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForMaxItems"));

			return fld;
			}
	
		private TextField <String> buildDefaultMethodFld(String id)
			{
			TextField<String> fld = new TextField<String>(id, new PropertyModel(worklist, "defaultMethodFileName"))
			    {
				@Override
				public boolean isEnabled() 
				    { 
					return worklist.getOpenForUpdates(); 
				    }	
				};
			fld.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForMethodFile"));
			return fld;
			}
		
		private DropDownChoice buildPlatePosDropdown(String id, String property)
			{
			DropDownChoice drp = new DropDownChoice(id, new PropertyModel<String>(plateListHandler, property), new PropertyModel(plateListHandler, "possiblePlatePositions"));

			drp.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForPlatePos"));
			
			return drp;
			}

		private WorklistABSciexPanel buildABSciexPanel(String id)
			{
			return new WorklistABSciexPanel(id, worklist)
				{
				@Override
				public boolean isVisible() { return isPlatformChosenAs("absciex"); }
				};
			}

		private WorklistAgilentPanel buildAgilentPanel(String id)
			{
			return new WorklistAgilentPanel(id, worklist)
				{
				@Override
				public boolean isVisible()	{ return isPlatformChosenAs("agilent"); }
				@Override
				public boolean isEnabled() 
				    { 
					// issue 126
					return worklist.getOpenForUpdates(); 
				    }	
				};
			}

		private AddSamplesPanel buildAddSamplesPanel(String id)
			{	
			return new AddSamplesPanel(id, worklist,wp)
				{
				@Override
				public boolean isVisible() { return isPlatformChosen() && samplesVisible; }

				@Override
				public boolean isEnabled() 
				    { 
					return isPlatformChosen() && worklist.getOpenForUpdates(); 
					}
							
				};
			}
	
		private AutoAddControlsPanel buildAddControlGroupsPanel(String id)
			{
			return new AutoAddControlsPanel(id, worklist, wp )
				{
				@Override
				public boolean isVisible() { return isPlatformChosen() && groupsVisible; }

				@Override
				public boolean isEnabled() { return isPlatformChosen(); }
				};
			}
		
		private AddControlsPanel buildAddControlsPanel(String id)
			{
			return new AddControlsPanel(id, worklist, wp)
				{
				@Override
				public boolean isVisible()  {  return isPlatformChosen() && controlsVisible; }

				@Override
				public boolean isEnabled() 
					{ 
					return isPlatformChosen(); 
					}
				};
			}
		
		private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(final String event, final String response)
			{
			
			return new AjaxFormComponentUpdatingBehavior(event)
				{
				
				// issue 212
					/* (non-Javadoc)
					 * @see org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior#onUpdate(org.apache.wicket.ajax.AjaxRequestTarget)
					 */
					@Override
					protected void onUpdate(AjaxRequestTarget target)
						{	
						switch (response)
							{
						// issue 212
							case "update96Well" :
								List <WorklistItemSimple> tWI = new ArrayList <WorklistItemSimple> ();	
								for (WorklistItemSimple wi : worklist.getItems())
					        		{
					        		if (!wi.getRepresentsControl())
					        			tWI.add(wi);
					        		}
								
								if (Integer.valueOf(addGroupsPanel.nStandardsStr) > 6 && worklist.getIs96Well())
									{
									prevStandardString = addGroupsPanel.nStandardsStr;
									addGroupsPanel.nStandardsStr = "6";
									}	
								// issue 205 96wells
								if (worklist.getIs96Well())
									worklist.setBothQCMPandMP(false);
								else
									{
									if (worklist.getPoolTypeA() == null || addGroupsPanel == null || addGroupsPanel.getPoolTypeA() == null || addGroupsPanel.poolTypeB  == null || addGroupsPanel.poolSpacingA == null || addGroupsPanel.poolSpacingB == null )
										worklist.setBothQCMPandMP(false);
									else
										worklist.setBothQCMPandMP ((StringParser.parseId(addGroupsPanel.getPoolTypeA()).equals("CS00000MP")  || worklist.getPoolTypeA().equals("CS00000MP")) && StringParser.parseId(addGroupsPanel.poolTypeB).equals("CS000QCMP") && addGroupsPanel.poolSpacingA > 0 && addGroupsPanel.poolSpacingB > 0);
									}
								addGroupsPanel.nStandards = Integer.valueOf(addGroupsPanel.nStandardsStr);
								target.add (change96WellBox);								
								target.add(addGroupsPanel.motrPacLink);
								//// issue 255 target.add(addGroupsPanel.blanksDrop);
								// issue 257
								//target.add(addGroupsPanel.processBlanksDrop);
								target.add(addGroupsPanel.poolTypeBDrop);
								target.add(addGroupsPanel.standardsDrop);
								target.add(addGroupsPanel.customLink);
								target.add(addGroupsPanel.qcDrop1);
								target.add(addGroupsPanel.poolsDropB);
								target.add(addGroupsPanel.numberInjectionsDropSB);
								target.add(controlsVisibleButton);	
								target.add(agPanel);
								// issue 255
								target.add(addGroupsPanel.numberInjectionsDropPB);
								try 
									{
									worklist.controlsMovedMap.clear();
									plateListHandler.updatePlatePositionsForAgilent(worklist);
									worklist.rebuildEverything();  // issue 212						
									if (worklist.getIs96Well())
										{
								
										plateListHandler.check96WellsUpdate(worklist.getItems());
										}
									} 
								
								catch (METWorksException e1) 
									{
									// TODO Auto-generated catch block
									e1.printStackTrace();
								    }
								// issue 212 // issue 212
								plateListHandler.check96WellsUpdate(worklist.getItems());								
								// issue 212
								Map<String, String> idsVsReasearcherNameMap =
							    sampleService.sampleIdToResearcherNameMapForExpId(worklist.getSampleGroup(0).getExperimentId());								
							    worklist.populateSampleName(worklist,idsVsReasearcherNameMap );
							    agPanel.updateIddaList();
							    target.add(agPanel.textAreaIdda);
							    
							    if (doesContainGivenPosition(worklist.getItems(), "P7"))
					       	        {   
						            target.appendJavaScript(StringUtils.makeAlertMessage("This worklist will contain more than 6 plates with the added controls.  The controls have been cleared. Please start over using fewer controls"));
						        	worklist.clearControlGroups();
						        	worklist.getItems().clear();
						        	worklist.getItems().addAll(tWI);
						        	updatePage(target);
						        	return;
						        	}
							    // issue 255
							    redoPlatePListView ();
							    plateListHandler.check96WellsUpdate(worklist.getItems());
							    plateListHandler.addLastControlRepeater(worklist);
							    
							    //target.add(platePreviewPageDialog);
						    case "updateStartPlate":
						    	prevStartPlate = worklist.getStartPlate();
						    	// issue 205 clear anything that is moved when starting from a new plate
						    	worklist.getControlsMovedMap().clear();
						    	break;
						    case "updateChangeDefaultInjVol" :
						    	target.add(defaultInjectionVolFld);
							    target.add(changeDefaultInjVolumeBox);
						        break;
						    case "updateForDate":
								if (worklist != null)
									worklist.rebuildEverything();
								//		worklist.updateOutputFileNames();
								worklist.updateSampleNamesArray();	
							    agPanel.updateIddaList();
							    target.add(agPanel.textAreaIdda);
								break;

							case "updateForInjectionVol":
								target.add(defaultInjectionVolFld);
								if (worklist != null)
									worklist.updateInjectionVolumes();
									break;

							case "updateForMethodFile":
								if (worklist != null)
									worklist.updateMethodFileNames();
									break;
									
							case "registerEvent":
								break;

							case "updateForMaxItems":
								if (worklist != null && worklist.sampleExperimentIsSelected())
									{
									worklist.rebuildEverything();
									worklist.updateIndices();
									worklist.updateInjectionVolumes();
									}
								// account
								target.add(defaultItemsPerListFld);
								break;
								
							case "updateForPlatform":
								worklist.setSelectedPlatform(getSelectedPlatform());
								if (worklist != null)
									worklist.clearAll();
								
	                            // issue 450
								if (isPlatformChosenAs("absciex"))
									{
									availableModes   = Arrays.asList(new String[] { "Positive", "Negative" });
									worklist.setSelectedMode("Positive");
									worklist.setIs96Well(false);  // issue 212
									target.add (change96WellBox); // issue 212
									}
								else 
									availableModes = Arrays.asList(new String[] { "Positive", "Negative", "Positive + Negative" });
								
								if (isPlatformChosenAs("absciex"))
									setSelectedInstrument("IN023 (LIPIDS)");
	
								addSamplesPanel.reinitializeAssays();
								worklist.setMaxItems(worklist.getUseCarousel() ? "100": "54");
								target.add(selectedInstrumentDrop);
								target.add(selectedModeDrop);
								target.add(addGroupsPanel);// issue 458
								agPanel.updateIddaList();
								target.add(agPanel.textAreaIdda);
								target.add(pPreview);
								break;

							case "updateForInstrument":
								if (worklist != null)
									{	
									worklist.setSelectedInstrument(getSelectedInstrument());
									// JAK new preview
									worklist.setMaxItems(worklist.getUseCarousel() ? 100 : 96);
									worklist.updatePlatePositions();
									worklist.updateIndices();
									// worklist.setOpenForUpdates(true);
									worklist.updateOutputFileNames();
									
									// issue 242
									if (worklist.getIs96Well())
										{
										int nPlateRows = 8, nPlateCols = 12;
										plateListHandler = new PlateListHandler(nPlateRows, nPlateCols,false);
										plateListHandler.check96WellsUpdate(worklist.getItems());
										}
									setupLC9LC10ForPlate ();// issue 217
									List <String> seeit = populateAvailablePlates ();
									startPlateDrop.setChoices(seeit);
									target.add(startPlateDrop);
									// issue 217
									if (worklist.getUseCarousel())
										{
										worklist.setIs96Well(false);
										change96WellBox.setDefaultModelObject(false);									
										}	
									target.add(change96WellBox);
									// issue 217
									if (prevStartPlate != null &&(Integer.parseInt(prevStartPlate) > 4) && !(selectedInstrument.contains("LC9") || selectedInstrument.contains("LC10")))
										{
										msg = "alert('Instrument: " + worklist.getSelectedInstrument() 
												+ " has only 4 plates.  Please be sure to click update to update the worklist.')";
										target.appendJavaScript(msg);
										}
									// issue 229
									else if ( worklist.getItems().size() > 0 &&  (doesContainP5orP6(worklist.getItems())  &&  !(selectedInstrument.contains("LC9") || selectedInstrument.contains("LC10"))))
										{
										msg = "alert('Instrument: " + worklist.getSelectedInstrument() 
										+ " has only 4 plates.  Please be sure to click update so that the worklist only uses up to plate 4.')";
										target.appendJavaScript(msg);	
										}
									
									// issue 217
									if (worklist.getUseCarousel())
										{
										msg = "alert('Instrument: " + worklist.getSelectedInstrument() 
												+ " is a carousel.  Please be sure to click update to update the worklist.')";
										target.appendJavaScript(msg);
										}
									prevStartPlate = worklist.getStartPlate();
									// issue 205 instruments
									plateListHandler.updateWorkListItemsMoved(worklist);
									}
								agPanel.updateIddaList();
							    target.add(agPanel.textAreaIdda);
							    target.add(pPreview); // issue 233
								/// issue 217
								break;
								// issue 166	
							case "updateStartSequence":
							    target.add(startSequenceFld);
							    idsVsReasearcherNameMap =
								    sampleService.sampleIdToResearcherNameMapForExpId(worklist.getSampleGroup(0).getExperimentId());
							    worklist.populateSampleName(worklist,idsVsReasearcherNameMap );
							   // issue 229 
							    for (WorklistItemSimple wi : worklist.getItems())
							    	wi.setComments(wi.calcCommentToolTip(worklist, wi));					    
							    break;
							case "updateForMode":
								if (worklist != null)
									worklist.updateOutputFileNames();
								agPanel.updateIddaList();
							    target.add(agPanel.textAreaIdda);
								break;
							case "updateForPlatePos":
								agPanel.updateIddaList();
							    target.add(agPanel.textAreaIdda);
								break;
							}
						
						updatePage(target);
						}
					};
				}
	
		private void updatePage(AjaxRequestTarget target)
			{
			target.add(abPanel);
			target.add(agPanel);
			target.add(addSamplesPanel.getContainer());
			target.add(addSamplesPanel);
			target.add(addControlsPanel);
			target.add(hideAllButton);
			target.add(controlsVisibleButton);
			target.add(samplesVisibleButton);
			target.add(groupsVisibleButton);
			target.add(randomizeSamplesButton);
			target.add(downloadListButton);
			// issue 128
			target.add(selectedPlatformDrop);
			target.add(selectedInstrumentDrop); 
			target.add(selectedModeDrop);
			target.add(dateFld);
			target.add(randomizationTypeBox);
			target.add(defaultInjectionVolFld);
			target.add(defaultMethodFld);
			target.add(startPlateDrop); // issue 153
			target.add(startSequenceFld);// issue 166
			target.add(changeDefaultInjVolumeBox);// issue 179
			}

		//issue 348
		final AbstractDefaultAjaxBehavior confirmBehavior = new 
	        AbstractDefaultAjaxBehavior() 
		        { 
		        @Override 
				protected void respond(AjaxRequestTarget target) 
		            {           
				    try 
				        { 
				    	// issue 233 
				        doRandomization(target);
				        plateListHandler.updateWorkListItemsMoved(worklist);
				        } 
				    catch (Exception e) 
				        { 				        
				        } 
				     } 
				 }; 
		
	 // issue 464
		private IndicatingAjaxLink buildRandomizeButton(final String linkID)
			{
			IndicatingAjaxLink link = new IndicatingAjaxLink <Void>(linkID)
				{
				@Override
				public boolean isEnabled()
					{
					return isPlatformChosenAs("agilent");  // && worklist.getItems() != null && !worklist.getItems().isEmpty(); //worklist.getOpenForUpdates() && isPlatformChosenAs("agilent") && !worklist.getItems().isEmpty();
					}
                
				// issue 348
				@Override
				public void onClick(final AjaxRequestTarget target)
					{	
				    setMultiPart(true);
					// Issue 268 issue 311 take out disabling of randomization after customized upload
					if (addSamplesPanel.originalWorklist.getSampleGroupsList().get(0).expRandom != null && worklist.getOpenForUpdates() && worklist.getItems() != null  && worklist.getItems().size() > 0) 
					       target.appendJavaScript("if (confirm('Are you sure you want to randomize the injections for this study?" 
							                           + "?')) { " +  confirmBehavior.getCallbackScript() + " }"  );							 
					else
						doRandomization(target);
					// issue 205
					plateListHandler.updateWorkListItemsMoved(worklist);
					target.add(change96WellBox);
					}							
				@Override
				protected void onComponentTag(ComponentTag tag)
					{
					super.onComponentTag(tag);	
					String label = worklist.getOpenForUpdates() ? "4. Randomize =>" : "4. UnRandomize =>";					
					tag.put("value", label);
					tag.put("title", label);					
					}
				};
			link.setOutputMarkupId(true);
			return link;
			}
		
		private void doRandomization(AjaxRequestTarget target) 
		    { 
			
			Map<String, String> idsVsReasearcherNameMap =
				    sampleService.sampleIdToResearcherNameMapForExpId(worklist.getSampleGroup(0).getExperimentId());
			if (worklist.getItems() == null || worklist.getItems().size() == 0)
				return;		
		    List <WorklistItemSimple> tWI = new ArrayList <WorklistItemSimple> ();	
			if (worklist.getOpenForUpdates())
				{
				titemsUpdateInj = new ArrayList <String> ();
				for (WorklistItemSimple wi : worklist.getItems())
		    		{
		    	    if (wi.isSelected())
		    	    	titemsUpdateInj.add(wi.getSampleName());    
		    		} 
				itemsUpdateInj.clear();
				itemsUpdateInj.addAll(titemsUpdateInj);
				// issue 416
				try 
					{
					for (WorklistItemSimple wi : worklist.getItems())
		        		{
		        		if (!wi.getRepresentsControl())
		        			tWI.add(wi);
		        		}
					worklist.rebuildEverything();
					}
				catch (Exception e)
					{
					target.appendJavaScript(StringUtils.makeAlertMessage("This worklist will contain more than 6 plates with the added controls.  Controls have been cleared. Please start over using fewer controls"));
		        	worklist.clearControlGroups();		      
		        	worklist.getItems().clear();
		        	worklist.getItems().addAll(tWI);
		        	updatePage(target);
		        	return;
					}
			    if (doesContainGivenPosition(worklist.getItems(), "P7"))
	       	        {     
		        	//target.appendJavaScript(StringUtils.makeAlertMessage("This worklist will contain more than 6 plates with the added controls.  The controls have been cleared. Please start over using fewer controls"));
		        	worklist.clearControlGroups();
		        	worklist.getItems().clear();
		        	worklist.getItems().addAll(tWI);
		        	updatePage(target);
		        	//return;
		        	}
				// issue 166
				plateListHandler.check96WellsUpdate(worklist.getItems());
				idsVsReasearcherNameMap =
				        sampleService.sampleIdToResearcherNameMapForExpId(worklist.getSampleGroup(0).getExperimentId());								
				    worklist.populateSampleName(worklist,idsVsReasearcherNameMap );  
				    
				if (worklist.getRandomizeByPlate())
				    PlateWiseRandomizer.randomizeByPlate(worklist.getItems());
				else
				    PlateWiseRandomizer.randomizeByWorklist(worklist.getItems());
				worklist.updateIndices();
				worklist.setOpenForUpdates(false);
				samplesVisible = true;
				controlsVisible = false;
				groupsVisible = false;				
				target.add(containerDefault);
				target.add(containerOther);
				updatePage(target);	
				if (!worklist.isPlateWarningGivenTwice())
					{
					String msg = "alert('In order to randomize your samples, control updates have been disabled.  To add (or update) controls, click Add Controls, make your changes "
							+ " and then click Randomize Samples again.')";
					target.appendJavaScript(msg);
					if (worklist.isPlateWarningGiven())
						worklist.setPlateWarningGivenTwice(true);
					worklist.setPlateWarningGiven(true);
					}
				
				 for (WorklistItemSimple wi : worklist.getItems())
		        	{
		        	if (titemsUpdateInj.contains(wi.getSampleName()))
		        		wi.setSelected(true);
		        	}
				 if (titemsUpdateInj.size()> 0 )
					 worklist.updateOutputFileNames();
				}
			else
				{
				// issue 128
				
				worklist.rebuildEverything();
				// issue 166
				plateListHandler.check96WellsUpdate(worklist.getItems());								
				// issue 212
				idsVsReasearcherNameMap =
			        sampleService.sampleIdToResearcherNameMapForExpId(worklist.getSampleGroup(0).getExperimentId());								
			    worklist.populateSampleName(worklist,idsVsReasearcherNameMap );   
				worklist.setOpenForUpdates(true);
				
				// issue 212
				// issue 233
				for (WorklistItemSimple wi : worklist.getItems())
		            {
		        	if (titemsUpdateInj.contains(wi.getSampleName()))
		        		wi.setSelected(true);
		        	}
				if (titemsUpdateInj.size()> 0 )
				    worklist.updateOutputFileNames();
				titemsUpdateInj = new ArrayList <String> (); 
				updatePage(target);
				}
		    }
		
		protected ValidatingAjaxExcelDownloadLink buildExcelDownloadLink(String linkId, final WorklistSimple worklist)
			{
			String ii = "2";
			IWriteableSpreadsheetReturnStream writer = new MsWorklistWriter( worklist, wp );// issue 450
			// issue 39
			ValidatingAjaxExcelDownloadLink link = new ValidatingAjaxExcelDownloadLink(linkId, writer)
				{
				@Override
				public boolean isEnabled() 
					{ 
					//return true; 
					return worklist.getSelectedInstrument() != null;
					} //!worklist.getItems().isEmpty(); }

				// Artifact of upgrade -- not called by validating button
				@Override
				// issue 207
				public boolean validate(AjaxRequestTarget target, IWriteableSpreadsheetReturnStream report)
					{
				
					if (worklist.getItems() == null || worklist.getItems().size() == 0 )
						return false;

					//persistWorksheetToDatabase();
					return true;
					}

				@Override
				public boolean validate()
					{
					// issue 212
				
					idsVsReasearcherNameMap =
					     sampleService.sampleIdToResearcherNameMapForExpId(worklist.getSampleGroup(0).getExperimentId());								
					worklist.populateSampleName(worklist,idsVsReasearcherNameMap );
					if (worklist.getItems() == null || worklist.getItems().size() == 0 || worklist.getSelectedInstrument() == null)
						return false;
					persistWorksheetToDatabase();
					worklist.setWorksheetTitle("");
					return true;
					}
				};		
			link.setOutputMarkupId(true);
			return link;				
			}
				
		protected void persistWorksheetToDatabase()
			{
			GeneratedWorklistItemDTO a;
			GeneratedWorklistDTO listDto = GeneratedWorklistDTO.instance(worklist);
			List<GeneratedWorklistItemDTO> itemDtos = new ArrayList<GeneratedWorklistItemDTO>();

			for (int i = 0; i < worklist.getItems().size(); i++)
				{
				GeneratedWorklistItemDTO itemDto = GeneratedWorklistItemDTO.instance(worklist.getItems().get(i));
				itemDtos.add(itemDto);
				}

			generatedWorklistService.save(listDto, itemDtos);
			// JAK fix issue 155 and 159
			sampleAssayService.updateStatusForExpAndAssayIdEfficiently(worklist.getDefaultExperimentId(), worklist.getDefaultAssayId(), "P");
			}
		
		public WorklistSimple getWorklist()
			{
			return worklist;
			}
	
		public void setWorklist(WorklistSimple w)
			{
			worklist = w;
			}
		
		// issue 217
		public List <String> populateAvailablePlates ()
			{
			
			List <String> availablePlates = new ArrayList <String> ();
			for (int i = 1; i <= plateListHandler.maxStartPlate ; i++)
				availablePlates.add(String.valueOf(i));
			return availablePlates;
			}
		
		public void setupLC9LC10ForPlate ()
			{
			// issue 217
			if (!StringUtils.isNullOrEmpty(selectedInstrument) && (selectedInstrument.contains("LC9") || selectedInstrument.contains("LC10")))
				{
				plateListHandler.maxStartPlate= 6;
				worklist.setMaxStartPlate(6);
				}
			else
				{
				plateListHandler.maxStartPlate= 4;
				worklist.setMaxStartPlate(4);
				if (Integer.parseInt(worklist.getStartPlate()) > 4)
		            worklist.setStartPlate("1");			
				}
			}	
		
		// issue 229
		public boolean doesContainP5orP6 (List <WorklistItemSimple> wiList)
			{
			for (WorklistItemSimple wi : wiList)
				{
				if (!StringUtils.isEmptyOrNull(wi.getSamplePosition()) && (wi.getSamplePosition().contains("P5") || wi.getSamplePosition().contains("P6")))
                     return true;
				}
			return false;
			}
		
		public boolean doesContainGivenPosition (List <WorklistItemSimple> wiList, String givenPosition )
			{
			for (WorklistItemSimple wi : wiList)
				{
				if (!StringUtils.isEmptyOrNull(wi.getSamplePosition()) && (wi.getSamplePosition().contains(givenPosition) ))
	                 return true;
				}
			return false;
			}
		
		/// issue 229 
		public void redoPlatePListView ()
			{
			int nItemsPerRow,  nItemsPerCol;
		    if (platePreviewPageDialog.plateListView != null &&  worklist.getItems().size() > 0 ) 
				{
				platePreviewPageDialog.plateListView.remove();
				if (worklist.getIs96Well())
					{
					nItemsPerRow = worklist.getUseCarousel() ? 10 : 12;
					nItemsPerCol = worklist.getUseCarousel() ? 10 : 8;
					}
				else
					{
					nItemsPerRow = worklist.getUseCarousel() ? 10 : 9;
					nItemsPerCol = worklist.getUseCarousel() ? 10 : 6;
					}	              
				form.platePreviewPageDialog.rlf.buildPlateListView(nItemsPerRow, nItemsPerCol,worklist.getItems(), worklist);
				form.platePreviewPageDialog.rlf.add  (form.platePreviewPageDialog.plateListView);
				}		
			}
	// issue 233	
		public void populateUnusedInj ()
			{
			try
				{
				if (worklist.getOpenForUpdates())
					{
					itemsUpdateInj = new ArrayList <String> ();
					for (WorklistItemSimple wi : worklist.getItems())
			    		{
			    	    if (wi.isSelected())
			    	    	itemsUpdateInj.add(wi.getSampleName());
			    		} 
		            }
				}
			catch (Exception e)
				{
				itemsUpdateInj = new ArrayList <String> ();
				e.printStackTrace();
				}
			}
		
     // issue 233
		 public void grabUnusedInj ()
		 	{
			try
				{
				for (WorklistItemSimple wi : worklist.getItems())
		        	{
		        	if (itemsUpdateInj.contains(wi.getSampleName()))	        		
		        		wi.setSelected(true);
		        	}
				if (itemsUpdateInj.size()> 0 )
					worklist.updateOutputFileNames();
	            itemsUpdateInj = new ArrayList <String> ();
			 	}
			catch (Exception e)
				{
				itemsUpdateInj = new ArrayList <String> ();
				e.printStackTrace();
				}
		 	}
		
		}

	}


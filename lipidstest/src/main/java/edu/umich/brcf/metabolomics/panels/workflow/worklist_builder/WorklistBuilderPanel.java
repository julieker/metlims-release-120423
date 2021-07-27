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

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.dto.GeneratedWorklistDTO;
import edu.umich.brcf.metabolomics.layers.dto.GeneratedWorklistItemDTO;
import edu.umich.brcf.metabolomics.layers.service.GeneratedWorklistService;
import edu.umich.brcf.metabolomics.layers.service.InstrumentService;
import edu.umich.brcf.shared.layers.service.SampleAssayService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.panels.utilitypanels.ValidatingAjaxExcelDownloadLink;
import edu.umich.brcf.shared.util.behavior.FocusOnLoadBehavior;
import edu.umich.brcf.shared.util.interfaces.IWriteableSpreadsheet;
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
	
	private static final long serialVersionUID = -2719126649022550590L;
	private WebPage backPage;
	private FeedbackPanel feedback;
	private String selectedRand = null;
    private int controlsLimit = 495;
	static final String WORKLIST_DATE_FORMAT = "MM/dd/yy";
    AjaxCheckBox randomizationTypeBox ;
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

		backPage = pg;
		feedback = new FeedbackPanel("feedback");
		feedback.setOutputMarkupId(true);
		add(feedback);

		WorklistBuilderForm form = new WorklistBuilderForm("worklistForm", prepData);

		add(form);
		}


	public final class WorklistBuilderForm extends Form
		{
		WorklistSimple worklist;
		
		// issue 416
		String selectedPlatform = null, selectedInstrument = null ;
		
		// issue 153
		DropDownChoice<String> selectedPlatformDrop, selectedInstrumentDrop, selectedModeDrop, startPlateDrop;
		
		List<String> availablePlates  = Arrays.asList(new String[] {"1", "2", "3", "4"});
		List<String> availablePlatforms = Arrays.asList(new String[] {"Agilent", "ABSciex" });
		List<String> availableModes = Arrays.asList(new String[] { "Positive", "Negative", "Positive + Negative" });
		List<String> agilentInstruments,  absciexInstruments; 
		
		METWorksAjaxUpdatingDateTextField dateFld;
		TextField <String> defaultMethodFld, defaultItemsPerListFld;
		TextField <Integer> defaultInjectionVolFld;
		
		WorklistAgilentPanel agPanel;
		WorklistABSciexPanel abPanel;
		AddSamplesPanel addSamplesPanel;
		AddControlsPanel addControlsPanel;
		AutoAddControlsPanel addGroupsPanel;
		PlateListHandler plateListHandler;
		WebMarkupContainer containerDefault, containerOther;
		private Boolean  controlsVisible = false, samplesVisible = false, groupsVisible = false; 
		private IndicatingAjaxLink hideAllButton, controlsVisibleButton, samplesVisibleButton, groupsVisibleButton;
		private IndicatingAjaxLink randomizeSamplesButton, previewPlateButton; 
		private ValidatingAjaxExcelDownloadLink downloadListButton;
		
		private ModalWindow modal1; 
		
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

			modal1 = ModalCreator.createModalWindow("modal1", 800, 320);
			add(modal1);
			
			int nPlateRows = 6, nPlateCols = 9;
			plateListHandler = new PlateListHandler(nPlateRows, nPlateCols,false);
			containerDefault = new WebMarkupContainer("containerDefault");

			// Platform fields		
			containerDefault.add(selectedPlatformDrop = buildPlatformDropdown("selectedPlatformDrop"));
			containerDefault.add(selectedInstrumentDrop = buildInstrumentDropdown("selectedInstrumentDrop"));
			containerDefault.add(selectedModeDrop = buildModeDropdown("selectedModeDrop"));
			containerDefault.add(buildPlatePosDropdown("startPos", "startPos"));
			containerDefault.add(buildPlatePosDropdown("endPos", "endPos"));
			containerDefault.add(startPlateDrop = buildStartPlateDropDown("selectedStartPlateDropDown"));
			
			containerDefault.add(randomizationTypeBox = buildRandomizeByPlate("randomizeByPlate"));// issue 416
			
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
			add(previewPlateButton = buildLinkToPlatePreview("platePreviewLink",  modal1));	
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
					//if ("controls".equals(property))
					//	isOpen = !(worklist != null && worklist.countGroups(true) > 50);
					
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
							break;
					
						case "samples" : 
							controlsVisible = false; samplesVisible = true; groupsVisible = false; 
							addControlsPanel.controlGroupsList = worklist.getControlGroupsList();					
							break;
							
						case "groups" :					
							controlsVisible = false; samplesVisible = false; groupsVisible = true;
							addControlsPanel.controlGroupsList = worklist.getControlGroupsList();
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
		
			// Issue 464
		private IndicatingAjaxLink buildLinkToPlatePreview(final String linkID, final ModalWindow modal1)
			{
			// issue 39
			IndicatingAjaxLink link = new IndicatingAjaxLink <Void>(linkID)
				{
				@Override
				public boolean isEnabled()
					{
					return worklist.isPlatformChosenAs("agilent") && !worklist.getUseCarousel();
					}

				@Override
				public void onClick(final AjaxRequestTarget target)
					{
					if (worklist.getItems() == null || worklist.getItems().size() == 0)
						return;
					
					setPageDimensions(modal1, 0.7, 0.6);
					modal1.setPageCreator(new ModalWindow.PageCreator()
						{
						// issue 17
						public Page createPage() { return ((Page) (new PlatePreviewPage(worklist.getBothQCMPandMP(), getPage(), worklist.getItems(), modal1, worklist.getUseCarousel()))); }
						});

					modal1.show(target);
					}
				};
				
			link.setOutputMarkupId(true);
			return link;
			}
		
		
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
			DropDownChoice drp = new DropDownChoice(id, new PropertyModel(worklist, "startPlate"), availablePlates)
				{
				// issue 128
				//@Override
				@Override
				public boolean isEnabled() 
				    { 
					return worklist.getOpenForUpdates(); 
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
					return worklist.getOpenForUpdates(); 
				    }				
				};
			fld.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForInjectionVol"));
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
			return new AddSamplesPanel(id, worklist)
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
			return new AutoAddControlsPanel(id, worklist)
				{
				@Override
				public boolean isVisible() { return isPlatformChosen() && groupsVisible; }

				@Override
				public boolean isEnabled() { return isPlatformChosen(); }
				};
			}

		
		private AddControlsPanel buildAddControlsPanel(String id)
			{
			return new AddControlsPanel(id, worklist)
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
					@Override
					protected void onUpdate(AjaxRequestTarget target)
						{					    
						switch (response)
							{
						    case "updateStartPlate":
							    break;
						    case "updateForDate":
								if (worklist != null)
									worklist.rebuildEverything();
								//		worklist.updateOutputFileNames();
								worklist.updateSampleNamesArray();								
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
								break;

							case "updateForInstrument":

								if (worklist != null)
									{
									worklist.setSelectedInstrument(getSelectedInstrument());
									worklist.setMaxItems(worklist.getUseCarousel() ? 100 : 54);
									worklist.updatePlatePositions();
									worklist.updateIndices();
									// worklist.setOpenForUpdates(true);
									worklist.updateOutputFileNames();
	
									}
								break;
							case "updateForMode":

								if (worklist != null)
									worklist.updateOutputFileNames();
								break;

							case "updateForPlatePos":
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
			target.add(previewPlateButton);
			// issue 128
			target.add(selectedPlatformDrop);
			target.add(selectedInstrumentDrop); 
			target.add(selectedModeDrop);
			target.add(dateFld);
			target.add(randomizationTypeBox);
			target.add(defaultInjectionVolFld);
			target.add(defaultMethodFld);
			target.add(startPlateDrop); // issue 153
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
				        doRandomization(target);
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
				
					// Issue 268 issue 311 take out disabling of randomization after customized upload
					if (addSamplesPanel.originalWorklist.getSampleGroupsList().get(0).expRandom != null && worklist.getOpenForUpdates() && worklist.getItems() != null  && worklist.getItems().size() > 0) 
					       target.appendJavaScript("if (confirm('Are you sure you want to randomize the injections for this study?" 
							                           + "?')) { " +  confirmBehavior.getCallbackScript() + " }"  );							 
					else
						doRandomization(target);								
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
			if (worklist.getItems() == null || worklist.getItems().size() == 0)
				return;			
			if (worklist.getOpenForUpdates())
				{	
				// issue 416
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
				}
			else
				{
				// issue 128
				
				worklist.rebuildEverything();
				worklist.setOpenForUpdates(true);
				updatePage(target);
				}
		    }
		
		protected ValidatingAjaxExcelDownloadLink buildExcelDownloadLink(String linkId, final WorklistSimple worklist)
			{
			IWriteableSpreadsheet writer = new MsWorklistWriter( worklist);// issue 450
			// issue 39
			ValidatingAjaxExcelDownloadLink link = new ValidatingAjaxExcelDownloadLink(linkId, writer)
				{
				@Override
				public boolean isEnabled() 
					{ 
					return true; 
					} //!worklist.getItems().isEmpty(); }

				// Artifact of upgrade -- not called by validating button
				@Override
				public boolean validate(AjaxRequestTarget target, IWriteableSpreadsheet report)
					{
				
					if (worklist.getItems() == null || worklist.getItems().size() == 0)
						return false;

					//persistWorksheetToDatabase();
					return true;
					}

				@Override
				public boolean validate()
					{
				
					if (worklist.getItems() == null || worklist.getItems().size() == 0)
						return false;

					persistWorksheetToDatabase();
					return true;
					}
				};
			
			link.setOutputMarkupId(true);
			return link;	
			
			}
		
		
		protected void persistWorksheetToDatabase()
			{
			GeneratedWorklistItemDTO a;
			GeneratedWorklistDTO listDto = GeneratedWorklistDTO.instance(this.worklist);
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

		}

	}


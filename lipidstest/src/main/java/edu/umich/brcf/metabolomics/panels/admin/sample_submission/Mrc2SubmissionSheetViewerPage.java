////////////////////////////////////////////////////
// Mrc2SubmissionSheetViewerPage.java
// Written by Jan Wigginton, Oct 21, 2015
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.admin.sample_submission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.LocationService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.layers.service.SampleTypeService;
import edu.umich.brcf.shared.layers.service.SubjectService;
import edu.umich.brcf.shared.panels.utilitypanels.AjaxGenusSpeciesField;
import edu.umich.brcf.shared.panels.utilitypanels.AjaxSampleTypeField;
import edu.umich.brcf.shared.util.datacollectors.Mrc2ExperimentalDesign;
import edu.umich.brcf.shared.util.datacollectors.Mrc2ExperimentalDesignItem;
import edu.umich.brcf.shared.util.datacollectors.Mrc2SampleInfoItem;
import edu.umich.brcf.shared.util.datacollectors.Mrc2SubmissionSheetData;
import edu.umich.brcf.shared.util.widgets.AjaxBackButton;
import edu.umich.brcf.shared.util.widgets.AjaxExperimentField;


public class Mrc2SubmissionSheetViewerPage extends WebPage 
	{
	@SpringBean
	SampleService sampleService;
	
	@SpringBean
	SampleTypeService sampleTypeService;

	@SpringBean
	ExperimentService expService;
	
	@SpringBean
	AssayService assayService;
	
	@SpringBean
	LocationService locationService;
	
	@SpringBean
	SubjectService subjectService;
	
	
	public static List<String> volumeUnits = Arrays.asList(new String[] { "ul", "ml", "mg", "pl", "ea" });
	private List<String> locationsForSamples;
	private List<String> possibleSampleTypeIds;
	private List<String> possibleGsIds;
	private List<String> availableAssays; 
	private List<String> availableSubjectIds;
	
	AjaxSubmitLink saveLink;
	Mrc2SubmissionSheetData inputData; 
	
	WebMarkupContainer container = new WebMarkupContainer("container");
	Boolean editMode = false;
	String pageTitle = "";
	String defaultLocationId = "", defaultSampleTypeId = "", defaultGsId = "";
	String defaultSubjectId = "TBD";

	
	public Mrc2SubmissionSheetViewerPage(Page backPage, String selectedExperiment, int nSamplesToAdd)
		{
		super();
		
		editMode = (nSamplesToAdd == 0);
		inputData = new Mrc2SubmissionSheetData(selectedExperiment); //SampleRegistrationData(exp, nSamplesToAdd);
		pageTitle = (editMode == true ? "Review Sample Submission Samples" : "Add Samples");

		add(new Label("pageTitle", pageTitle));
		//add(new TextField<Integer>("numSamples", new PropertyModel<Integer>(this, "numSamples")));
	//	add(new TextField<Integer>("numSamples")); 
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new Mrc2SubmissionSheetViewerForm("registerSampleForm", backPage));
		}
	
	
	public Mrc2SubmissionSheetViewerPage(Page backPage, Mrc2SubmissionSheetData data, boolean mode)
		{
		super();
		
		inputData = data;
		editMode = mode;
		pageTitle = (editMode == false ? "Review Sample Submission" : "Add Samples");
		
		add(new Label("pageTitle", pageTitle));
	//	add(new TextField<Integer>("numSamples")); //new PropertyModel<Integer>(this, "numSamples")));
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new Mrc2SubmissionSheetViewerForm("registerSampleForm", backPage));
		}

	
	public class Mrc2SubmissionSheetViewerForm extends Form 
		{
		public Mrc2SubmissionSheetViewerForm(final String id, Page backPage)
			{
			super(id); 
			
			locationsForSamples = locationService.getLocationsForSamples();
			availableAssays = assayService.allAssayNamesAndIdsShortened(Mrc2ExperimentalDesign.SHORT_LABEL_LEN);
			availableSubjectIds = subjectService.getSubjectIdsForExperimentId(inputData.getClientInfo().getExperimentId());
			availableSubjectIds.add("TBD");
			
			ListView samplesMetadataView, sampleAssaysView, sampleFactorsView;
			List<Boolean> sampleEditStatus = new ArrayList<Boolean>();
			
			container.setOutputMarkupId(true);
			container.setOutputMarkupPlaceholderTag(true);
			add(container);
			
			DropDownChoice<String> defaultLocationDrop = buildDefaultDrop("defaultLocationDrop", "defaultLocationId", locationsForSamples, "updateForDefaultLocation");
			container.add(defaultLocationDrop);	
			container.add(buildDefaultField("defaultGenusSpecies", "defaultGsId", "updateForDefaultGs"));
			container.add(buildDefaultField("defaultSampleType", "defaultSampleTypeId", "updateForDefaultSampleType"));
			
			container.add(new AjaxGenusSpeciesField("gsLookup"));
			container.add(new AjaxSampleTypeField("stLookup"));
			
			final AutoCompleteTextField experimentField=new AjaxExperimentField("experiment", new PropertyModel(inputData, "clientInfo.experimentId"));
			experimentField.setRequired(true);
			container.add(experimentField);
			
			for (int i = 0; i < Mrc2ExperimentalDesign.MRC2_SUBMISSION_SHEET_NFACTORS; i++)
				container.add(buildFactorNameField("factorLabels." + i, "expDesign.factorLabels." + i));
			
			container.add(newValidTextField("shortCode",new PropertyModel<String>(inputData.getClientInfo(), "shortCode"), null, false));
			container.add(newValidTextField("nihGrantNumber",new PropertyModel<String>(inputData.getClientInfo(), "nihGrantNumber"), null, false));
			//container.add(newValidTextField("serviceRequestId",new PropertyModel<String>(inputData.clientInfo, "serviceRequestId"), null, false));

			//container.add(samplesMetadataView = buildSamplesMetaDataList("samples", false));
			container.add(sampleFactorsView = buildSampleFactorsList("factors", false));
			container.add(sampleAssaysView = buildSampleAssaysList("assays", false));	
			
			container.add(buildSaveLink("saveChanges", this));
			container.add(new AjaxBackButton("backButton", (WebPage) backPage));
			}
		
		
		public TextField<String> buildDefaultField(String id, String property, String tag)
			{
			TextField<String> fld = new TextField<String>(id, new PropertyModel(this, property));
			fld.add(buildStandardFormComponentUpdateBehavior("change",tag));
			return fld;
			}
		
		
		public String getDefaultLocationId() {
			return defaultLocationId;
		}

		
		public void setDefaultSubjectId(String id) {
			defaultSubjectId = id;
			}
		
		public String getDefaultSubjectId() {
			return defaultSubjectId;
		}

		public void setDefaultLocationId(String location) {
			defaultLocationId = location;
			}
		
		
		public String getDefaultSampleTypeId() {
			return defaultSampleTypeId;
		}

		public String getDefaultGsId() {
			return defaultGsId;
		}

		public void setDefaultSampleTypeId(String sampleTypeId) {
			defaultSampleTypeId = sampleTypeId;
		}

		public void setDefaultGsId(String gsId) { defaultGsId = gsId;
		}

		
		private DropDownChoice<String> buildDefaultDrop(String id, String property, List<String> choices, String tag)
			{
			DropDownChoice drp = new DropDownChoice(id,  new PropertyModel(this, property), choices )
				{
				/* @Override
				protected boolean wantOnSelectionChangedNotifications() 
					{
		            return true;
					}*/ // Issue 464
				};
				
			drp.add(buildStandardFormComponentUpdateBehavior("change", tag));
		   /* drp.add(new FormComponentUpdatingBehavior() {
		    	@Override
		        protected void onUpdate() {
		            // do something, page will be rerendered;
		        }
		     
		        protected void onError(RuntimeException ex) {
		            super.onError(ex);
		        }
		        
		   
		        
		    });*/
			
			return drp;
			}		
		
		
		private DropDownChoice<String> buildSubjectIdDrop(String id, String property, List<String> choices, String tag)
			{
			return buildSubjectIdDrop(id, this, property, choices, tag);
			}

		
		private DropDownChoice<String> buildSubjectIdDrop(String id, Object obj, String property, List<String> choices, String tag)
			{
			DropDownChoice drp = new DropDownChoice(id,  new PropertyModel<String>(obj, property), choices );
			drp.add(buildStandardFormComponentUpdateBehavior("change", tag));
			return drp;
			}	
		
		
		private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(String event, final String response)
			{
			return new AjaxFormComponentUpdatingBehavior(event)
			    {
			    @Override
			    protected void onUpdate(AjaxRequestTarget target)
			    	{
			    	switch (response)
			        	{
			        	case "updateForDefaultLocation":
			        		for (int i = 0; i < inputData.samplesMetadata.infoFields.size(); i++)
								inputData.samplesMetadata.infoFields.get(i).setLocationId(defaultLocationId);
			        		break;
			        	
			        	case "updateForDefaultSampleType":
			        		for (int i = 0; i < inputData.samplesMetadata.infoFields.size(); i++)
								inputData.samplesMetadata.infoFields.get(i).setSampleTypeId(defaultSampleTypeId);
			        		break;
			        	
			        	case "updateForDefaultGs":
			        		for (int i = 0; i < inputData.samplesMetadata.infoFields.size(); i++)
								inputData.samplesMetadata.infoFields.get(i).setGenusOrSpeciesId(defaultGsId);
			        		break;
			        	
			        	case "updateForSubjectIdDrop" :
			        		
			        		break;
			        	}
			    	
			    	target.add(container);
			    	}
			    };
			}
		
		
		private TextField<String> buildFactorNameField(String id, String propertyName)
			{
			return new TextField<String>(id, new PropertyModel<String>(inputData, propertyName))
				{
				public boolean isEnabled() { return editMode; }
				};
			}

		// issue 39
		private IndicatingAjaxLink buildSaveLink(String id, Object obj)
			{
			return new IndicatingAjaxLink <Void>(id)
				{
				@Override
				public void onClick(AjaxRequestTarget arg0) 
					{
					for (int i = 0; i < inputData.samplesMetadata.infoFields.size(); i++)
						System.out.println(inputData.samplesMetadata.infoFields.get(i).toTokens().toString());
					
					///sampleService.saveSamplesAndControls(inputData);}
					}
				};
			}

		
		private ListView<Mrc2ExperimentalDesignItem> buildSampleFactorsList(String id, final boolean isEnabled) 
			{
			IModel listModel = new PropertyModel<List<Mrc2ExperimentalDesignItem>>(inputData.expDesign, "infoFields");
			
			ListView <Mrc2ExperimentalDesignItem> lst = new ListView <Mrc2ExperimentalDesignItem>(id, listModel)
				{
				public void populateItem(final ListItem item)
					{
		        	Mrc2ExperimentalDesignItem designDataRow = (Mrc2ExperimentalDesignItem)item.getModelObject();
		        
		            TextField <String>sampleIdText=new TextField<String>("sampleId3", new PropertyModel<String>(designDataRow, "sampleId"));
		            sampleIdText.setEnabled(false);
		            item.add(sampleIdText);
		            
		            TextField <String> userSampleIdText = new TextField<String>("userSampleId", new PropertyModel<String>(designDataRow, "userSampleId"));
		            userSampleIdText.setEnabled(false);
		            item.add(userSampleIdText);
		            
		            //final boolean isEnabled = false; //editMode || data.isNewSample(mapping.getSampleId());
		            
		            for (int i = 0; i < Mrc2ExperimentalDesign.MRC2_SUBMISSION_SHEET_NFACTORS; i++)
		            	item.add(new TextField<String>("factorValues." + i, new PropertyModel<String>(designDataRow, "factorValues." + i))
		        			{
		        			@Override
		        			public boolean isEnabled() { return isEnabled; }
		        			});
		        	}
				};
		
			lst.setOutputMarkupId(true);
			return lst;
			}
	
		
		private ListView<Mrc2ExperimentalDesignItem> buildSampleAssaysList(String id, final boolean isEnabled)
			{
			IModel listModel = new PropertyModel<List<Mrc2ExperimentalDesignItem>>(inputData.expDesign, "infoFields");
			
			ListView <Mrc2ExperimentalDesignItem> lst = new ListView <Mrc2ExperimentalDesignItem>(id, listModel)
				{
				public void populateItem(final ListItem item)
		        	{
		            Mrc2ExperimentalDesignItem designDataRow =(Mrc2ExperimentalDesignItem) item.getModelObject();
		            
		            final TextField <String> sampleIDText=new TextField<String>("sampleID2", new PropertyModel<String>(designDataRow, "sampleId"));
		            sampleIDText.setEnabled(false);
		            item.add(sampleIDText);
		            
		            item.add(new DropDownChoice("assayNames.0", new PropertyModel<String>(designDataRow, "formAssayNames.0"), availableAssays)
		            	{
		            	@Override
		            	public boolean isRequired() { return ((sampleIDText.getConvertedInput()!=null) && (sampleIDText.getInput().length()>0)); }
		            	
		        		@Override
		        		public boolean isEnabled()	{ return true; }
		        		});
		            	
		            // designDataRow.getAssayNames(i)
	            	for (int i = 1; i < Mrc2ExperimentalDesign.MRC2_SUBMISSION_SHEET_NASSAYS; i++)
	            		item.add(new DropDownChoice("assayNames." + i, new PropertyModel<String>(designDataRow, "formAssayNames." +i), availableAssays)
	            			{
	            			@Override
	            			public boolean isEnabled() { return true; } 
	            			});
		        	}
				};
			lst.setOutputMarkupId(true);
			return lst;
			}
			

		private ListView<Mrc2SampleInfoItem> buildSamplesMetaDataList(String id, final boolean isEnabled)
			{
			IModel listModel = new PropertyModel<List<Mrc2SampleInfoItem>>(inputData.samplesMetadata, "infoFields");
			
			ListView<Mrc2SampleInfoItem> lst = new ListView<Mrc2SampleInfoItem>(id, listModel)
				{
				public void populateItem(final ListItem item)
		        	{
					final Mrc2SampleInfoItem sdto = (Mrc2SampleInfoItem) item.getModelObject();
		            
		            final TextField<String> sampleIDText = buildSampleIdField("sampleId", sdto, false);
		            item.add(sampleIDText);
		           
		            item.add(newValidTextField("researcherSampleId",new PropertyModel<String>(sdto, "researcherSampleId"), sampleIDText, isEnabled));
		            item.add(newValidTextField("researcherSubjectId", new PropertyModel<String>(sdto, "researcherSubjectId"), sampleIDText, isEnabled));
		            item.add(newValidTextField("userDefinedSampleType", new PropertyModel<String>(sdto, "userDefinedSampleType"), sampleIDText, isEnabled));
		            item.add(newValidTextField("userDefinedGOS", new PropertyModel<String>(sdto, "userDefinedGOS"), sampleIDText, isEnabled));
	            	item.add(newValidTextField("volume", new PropertyModel<String>(sdto, "volume"), sampleIDText, isEnabled));
		            item.add(newValidTextField("units", new PropertyModel<String>(sdto, "units"), sampleIDText, isEnabled));
		            item.add(newValidTextField("sampleTypeId", new PropertyModel<String>(sdto, "sampleTypeId"), sampleIDText, true));
	            	item.add(newValidTextField("genusOrSpeciesId", new PropertyModel<String>(sdto, "genusOrSpeciesId"), sampleIDText, true));
	            	item.add(newValidTextField("locationId", new PropertyModel<String>(sdto, "locationId"), sampleIDText, true));
	            	item.add(buildSubjectIdDrop("mrc2SubjectId", sdto, "mrc2SubjectId", availableSubjectIds, "updateForSubjectIdDrop" ));
		            }
				};
			lst.setOutputMarkupId(true);
			return lst;
			}
		
		
		private TextField<String> buildSampleIdField(String id, final Mrc2SampleInfoItem sdto, final boolean isEnabled)
			{
			TextField<String> fld = new TextField<String>(id, new PropertyModel<String>(sdto, id))
				{
				@Override
				public boolean isEnabled() { return isEnabled; }
				};
				
			fld.add(new OnChangeAjaxBehavior()
				{
				@Override
				protected void onUpdate(final AjaxRequestTarget target)
					{
					final String valueAsString = ((TextField<String>) getComponent()).getModelObject();
					sdto.setSampleId(valueAsString);
					target.add(container);
					}
				});
			
			fld.add(new AjaxFormComponentUpdatingBehavior("onblur")
				{
				@Override
				protected void onUpdate(final AjaxRequestTarget target)
					{
					final String valueAsString = ((TextField<String>) getComponent()).getModelObject();
					System.out.println("Vale entered was " + valueAsString);
					//ListView itemList = (ListView) form.get("assays");
					sdto.setSampleId(valueAsString);
					target.add(container);
					}
				});
			return fld;
			}
		
		
		
		private TextField newValidTextField(String id, PropertyModel model, final TextField t1, final boolean isEnabled) 
			{
			TextField<String> fld =  new TextField<String>(id, model)
				{
				@Override
				public boolean isRequired() { return true;} //return ((t1.getConvertedInput()!=null)&&(t1.getInput().length()>0)); }
				
				@Override
				public boolean isEnabled() { return isEnabled; }
				};
				
			fld.add(new OnChangeAjaxBehavior()
				{
				@Override
				protected void onUpdate(final AjaxRequestTarget target)
					{
					final String valueAsString = (t1 == null ? "" : ((TextField<String>) getComponent()).getModelObject());
					System.out.println("Value entered was " + valueAsString);
					target.add(container);
					}
				});
		
			fld.add(new AjaxFormComponentUpdatingBehavior("onblur")
				{
				@Override
				protected void onUpdate(final AjaxRequestTarget target)
					{
					final String valueAsString = (t1 == null ? "" : ((TextField<String>) getComponent()).getModelObject());
					System.out.println("Value entered was " + valueAsString);
					//ListView itemList = (ListView) form.get("assays");
					//sdto.setSampleId(valueAsString);
					target.add(container);
					}	
				});
				
			return fld;
			}
		} 
	
	public Mrc2SubmissionSheetData getInputData() 
		{
		return inputData;
		}

	public void setInputData(Mrc2SubmissionSheetData inputData) 
		{
		this.inputData = inputData;
		}

	public String getPageTitle() {
		return pageTitle;
		}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
		}
		
	public String getExpID()
		{
		return "EX0009";
		}
	}
	

////////////////////////////////////////////////////
// RegisterSampleEntryPage.java
// Written by Jan Wigginton October 2015
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.admin.sample_submission.obsolete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.panels.admin.sample_submission.SampleAssayMapping;
import edu.umich.brcf.metabolomics.panels.admin.sample_submission.SampleFactorMapping;
import edu.umich.brcf.metabolomics.panels.admin.sample_submission.SampleRegistrationData;
import edu.umich.brcf.metabolomics.panels.admin.sample_submission.SampleSubmissionLookupPage;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.dto.SampleDTO;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.layers.service.SubjectService;
import edu.umich.brcf.shared.util.widgets.AjaxBackButton;
import edu.umich.brcf.shared.util.widgets.AjaxExperimentField;


public class RegisterSampleEntryPage extends WebPage 
	{
	@SpringBean
	SampleService sampleService;
  
	@SpringBean
	ExperimentService expService;
	
	@SpringBean
	AssayService assayService;
	
	@SpringBean
	SubjectService subjectService;
	
	public static List<String> volumeUnits = Arrays.asList(new String[] { "ul", "ml", "mg", "pl", "ea" });
	public List<String> availableAssays; 
	public List<String> availableSubjectIds;
	private Boolean editMode;
	
	AjaxSubmitLink saveLink;
	SampleRegistrationData inputData; 
	WebMarkupContainer container = new WebMarkupContainer("container");
	
		
	public RegisterSampleEntryPage(Page backPage, String selectedExperiment, int nSamplesToAdd)
		{
		editMode = (nSamplesToAdd == 0);
		Experiment exp = expService.loadExperimentWithInfoForEditing(selectedExperiment);
		
		inputData = new SampleRegistrationData(exp, nSamplesToAdd);
		availableAssays = assayService.allAssayNamesAndIdsShortened(SampleRegistrationData.SHORT_LABEL_LEN);
		availableSubjectIds = subjectService.getSubjectIdsForExperimentId(selectedExperiment);
		
		String pageTitle = (editMode == true ? "Edit Samples" : "Add Samples");
		add(new Label("pageTitle", pageTitle));
		add(new TextField<Integer>("numSamples", new PropertyModel<Integer>(this, "numSamples")));
		
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new RegisterSampleEntryForm("registerSampleForm", backPage));
		}
	

	public final class RegisterSampleEntryForm extends Form 
		{
		ListView samplesMetadataView, sampleAssaysView, sampleFactorsView;
		List<Boolean> sampleEditStatus = new ArrayList<Boolean>();
		
		public RegisterSampleEntryForm(final String id, Page backPage)
			{
			super(id, new CompoundPropertyModel(inputData));
			
			container.setOutputMarkupId(true);
			container.setOutputMarkupPlaceholderTag(true);
			add(container);
			
			final AutoCompleteTextField experimentField=new AjaxExperimentField("experiment", new PropertyModel(inputData, "expID"));
			experimentField.setRequired(true);
			container.add(experimentField);
			
			for (int i = 0; i < SampleRegistrationData.N_FACTORS_TRACKED; i++)
				container.add(buildFactorNameField("factorNames." + i, "factorNames." + i));
			
			container.add(samplesMetadataView = buildSamplesMetaDataList("samples", inputData));
			container.add(sampleFactorsView = buildSampleFactorsList("factors", inputData));
			container.add(sampleAssaysView = buildSampleAssaysList("assays", inputData));	
			
			sampleAssaysView.setOutputMarkupId(true);
			sampleFactorsView.setOutputMarkupId(true);
			samplesMetadataView.setOutputMarkupId(true);
			
			container.add(buildSaveLink("saveChanges", this));
			container.add(new AjaxBackButton("backButton", (WebPage) backPage));
			}
		
		
		private TextField<String> buildFactorNameField(String id, String propertyName)
			{
			return new TextField<String>(id, new PropertyModel<String>(inputData, propertyName))
				{
				@Override
				public boolean isEnabled() { return editMode; }
				};
			}
		
		private AjaxSubmitLink buildSaveLink(String id, Object obj)
			{
			return new AjaxSubmitLink (id)
				{
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form form) 
					{
					ListView itemList = (ListView) form.get("samples");
					
					SampleRegistrationData inputData = (SampleRegistrationData) getForm().getModelObject();
					//for (int i = 0; i < inputData.getSampleData().size(); i++)
					//	System.out.println(inputData.getSampleData().get(i).toString());
					}
				};
			}
					/*
					String temp = inputData.getExpID();
					final String expID=StringParser.parseId(temp);
				//	String groupID = null;//(groupID!=null)? StringParser.parseId(groupID): null;
					List<SampleDTO> samples = inputData.getSampleData();
					if(samples.get(0).getSampleID()!=null && expID!=null)  
						{
						try
							{
							Experiment exp = expService.loadById(expID);
							String shortcode = form.get("shortcode").getDefaultModelObjectAsString();
							String nih_grantNumber = form.get("nih_grantnumber").getDefaultModelObjectAsString();
							if((shortcode!=null) && (shortcode.length()>0))
								try
									{
									Shortcode sc = expService.getExperimentShortcode(shortcode, expID);
									}
								catch(Exception ex)
									{
									Shortcode.instance(shortcode, nih_grantNumber, exp);
									}
							
								ExperimentalGroup group = (groupID==(null)||(groupID.trim().length()==0))? null :expService.loadGroupById(groupID);
								int sampleCount=sampleService.saveSamples(samples, expID, groupID);
								//sampleService.saveSamples(inputData);
								
								if(sampleCount==0)
									ManualSampleEntryPage.this.error("No samples were saved. Please check if Sample ID column has the right values");
							else
								ManualSampleEntryPage.this.info(sampleCount+" sample(s) saved for Experiment " + expID);
							}
						catch(Exception e)
							{
							e.printStackTrace();
							ManualSampleEntryPage.this.error("Sample upload failed: Experiment/Group does not exist.");
							setResponsePage(getPage());
							}}
					else
						{
						if(expID == null)
							ManualSampleEntryPage.this.error("Experiment is a required field.");
						
						if(samples.get(0).getSampleID()==null)
							ManualSampleEntryPage.this.error("Please enter Sample information to create samples");
						}
					
		            target.add(ManualSampleEntryPage.this.get("feedback"));
					}
				
				@Override
				protected void onError( AjaxRequestTarget target, Form form )
					{
				    target.add(ManualSampleEntryPage.this.get("feedback"));
					} */ 
			//	}//; 
		//	}

		
		private ListView<SampleFactorMapping> buildSampleFactorsList(String id, final SampleRegistrationData data) 
			{	
			List<SampleFactorMapping> list = data.getSampleFactorMappings3();
					
			return	new ListView<SampleFactorMapping>(id, list)
				{
				public void populateItem(final ListItem item)
		        	{
		            SampleFactorMapping mapping = (SampleFactorMapping)item.getModelObject();
		        
		          //  TextField <String>sampleIdText=new TextField<String>("sampleID3", new PropertyModel<String>(mapping, "sampleId"));
		          //  item.add(sampleIdText);
		            
		            TextField <String> researcherSampleIdText = new TextField<String>("researcherSampleId", new PropertyModel<String>(mapping, "researcherSampleId"));
		            item.add(researcherSampleIdText);
		            
		            final boolean isEnabled = editMode || data.isNewSample(mapping.getSampleId());
		            
		            for (int i = 0; i < SampleRegistrationData.N_FACTORS_TRACKED; i++)
		            	item.add(new TextField<String>("sampleFactors." + i, new PropertyModel<String>(mapping, "factorValues." + i))
		        			{
		        			@Override
		        			public boolean isEnabled() { return isEnabled; }
		        			});
			        }
				};
			}
	
		
		private ListView<SampleAssayMapping> buildSampleAssaysList(String id, final SampleRegistrationData data)
			{
			List<SampleAssayMapping> list = data.getSampleAssayMappings();
					
			return new ListView<SampleAssayMapping>(id, list)
				{
				public void populateItem(final ListItem item)
		        	{
		            final SampleAssayMapping mapping = (SampleAssayMapping)item.getModelObject();

		            final boolean isEnabled = editMode || data.isNewSample(mapping.getSampleId());

		            final TextField <String>sampleIDText=new TextField<String>("sampleID2", new PropertyModel<String>(mapping, "sampleId"));
		            item.add(sampleIDText);
		            
		            item.add(new DropDownChoice("sampleAssays.0", new PropertyModel<String>(mapping, "sampleAssays.0"), availableAssays)
		            	{
		            	@Override
		            	public boolean isRequired() { return ((sampleIDText.getConvertedInput()!=null) && (sampleIDText.getInput().length()>0)); }
		            	
		        		@Override
		        		public boolean isEnabled()	{ return isEnabled; }
		        		});
		            	
	            	for (int i = 1; i < SampleRegistrationData.N_ASSAYS_TRACKED; i++)
	            		item.add(new DropDownChoice("sampleAssays." + i, new PropertyModel<String>(mapping, "sampleAssays." +i), availableAssays)
	            			{
	            			@Override
	            			public boolean isEnabled() { return isEnabled; } 
	            			});
			        }
				};
			}
			
		private ListView<SampleDTO> buildSamplesMetaDataList(String id, final SampleRegistrationData data)
			{
			return	new ListView<SampleDTO>(id, new PropertyModel <List<SampleDTO>>(data, "sampleData"))
				{
				public void populateItem(final ListItem item)
		        	{
		            final SampleDTO sdto = (SampleDTO)item.getModelObject();
		            final boolean isEnabled = editMode || data.isNewSample(sdto.getSampleID());
		            
		            final TextField<String> sampleIDText = buildSampleIdField("sampleID", sdto, isEnabled);
		            item.add(sampleIDText);
		           
		            item.add(newValidTextField("sampleName",new PropertyModel<String>(sdto, "sampleName"), sampleIDText, isEnabled));
		            item.add(newValidTextField("userDescription",new PropertyModel<String>(sdto, "userDescription"), sampleIDText, isEnabled));
		            item.add(newValidTextField("UserDefSampleType", new PropertyModel<String>(sdto, "UserDefSampleType"), sampleIDText, isEnabled));
		            item.add(newValidTextField("volume", new PropertyModel<String>(sdto, "volume"), sampleIDText, isEnabled));
		            item.add(newValidTextField("sampleTypeId", new PropertyModel<String>(sdto, "sampleTypeId"), sampleIDText, isEnabled));
	            	item.add(newValidTextField("genusOrSpeciesID", new PropertyModel<String>(sdto, "genusOrSpeciesID"), sampleIDText, isEnabled));
	            	item.add(newValidTextField("locID", new PropertyModel<String>(sdto, "locID"), sampleIDText, isEnabled));
	            	
		            boolean hasSampleText = (sampleIDText.getConvertedInput()!= null)&&(sampleIDText.getInput().length() > 0);
		            item.add(buildTableDropdown("volUnits", sdto, volumeUnits, hasSampleText, isEnabled));
		            item.add(buildTableDropdown("subjectId", sdto, availableSubjectIds, hasSampleText, isEnabled));
		            }
				};
			}
		
		
		private TextField<String> buildSampleIdField(String id, final SampleDTO sdto, final boolean isEnabled)
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
					
					sdto.setSampleID(valueAsString);
					target.add(container);
					}
				});
			
			fld.add(new AjaxFormComponentUpdatingBehavior("onblur")
				{
				@Override
				protected void onUpdate(final AjaxRequestTarget target)
					{
					final String valueAsString = ((TextField<String>) getComponent()).getModelObject();
					sdto.setSampleID(valueAsString);
					target.add(container);
					}
				});
			return fld;
			}
		
		
		private DropDownChoice<String> buildTableDropdown(String id, SampleDTO sdto, List<String> optionsArray, 
				final boolean isRequired, final Boolean isEnabled)
			{
			return new DropDownChoice<String>(id, new PropertyModel<String>(sdto, id), optionsArray)
				{
	        	@Override
	        	public boolean isEnabled() { return isEnabled; }
	        	
	        	@Override
	        	public boolean isRequired() { return isRequired; }
				};
			}
		
		
		private TextField newValidTextField(String id, PropertyModel model, final TextField t1, final boolean isEnabled) 
			{
			return new TextField(id, model)
				{
				@Override
				public boolean isRequired() { return ((t1.getConvertedInput()!=null)&&(t1.getInput().length()>0)); }
				
				@Override
				public boolean isEnabled() { return isEnabled; }
				};
			}
		
		
		private AjaxLink buildLinkToModal(final String linkID, final ModalWindow modal1) 
			{
			return new AjaxLink(linkID)
	        	{
	            @Override
	            public void onClick(AjaxRequestTarget target)
	            	{
	            	modal1.setPageCreator(new ModalWindow.PageCreator()
	            		{
	            		public Page createPage()
	            			{
                 		 	return new SampleSubmissionLookupPage(getPage());
	            			}
	            		});
	            	modal1.show(target);
	            	}
	        	};
			}
		}
	
	
	public SampleRegistrationData getInputData() 
		{
		return inputData;
		}

	public void setInputData(SampleRegistrationData inputData) 
		{
		this.inputData = inputData;
		}

	public WebMarkupContainer getContainer() 
		{
		return container;
		}

	public void setContainer(WebMarkupContainer container) 
		{
		this.container = container;
		}
	}


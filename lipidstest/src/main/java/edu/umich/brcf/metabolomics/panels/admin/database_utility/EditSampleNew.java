////////////////////////////////////////////////////
//EditSampleNew.java
//Written by Jan Wigginton, November 2015
////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.admin.database_utility;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.RangeValidator;
import org.apache.wicket.validation.validator.StringValidator;

import edu.umich.brcf.metabolomics.layers.service.TableAccessService;
import edu.umich.brcf.shared.layers.domain.ExperimentSetup;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.domain.Subject;
import edu.umich.brcf.shared.layers.dto.SampleDTO;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.LocationService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.layers.service.SubjectService;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.structures.Pair;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;
import edu.umich.brcf.shared.util.widgets.METWorksPctSizableModal;


public class EditSampleNew extends WebPage 
	{
	@SpringBean
	private SampleService sampleService;

	@SpringBean
	private AliquotService aliquotService;
	
	@SpringBean
	private SubjectService subjectService;
	
	@SpringBean
	TableAccessService tableAccessService;
	
	@SpringBean
	private LocationService locationService;
	
	
	private List<String> availableSubjectIds = new ArrayList<String>();
	private List<Pair> factorInfo; 
	

	public EditSampleNew(Page backPage, IModel sampleModel, final METWorksPctSizableModal modal1) 
		{
		Sample sample = (Sample) sampleModel.getObject();
		
		FeedbackPanel feedback; 
		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);
		
		add(new EditSampleNewForm("editSampleForm", sample.getSampleID(), SampleDTO.instance(sampleService.loadById(sample.getId())),  modal1));
		factorInfo = initializeFactorInfo(sample);
		}

	
	public EditSampleNew(Page backPage, final METWorksPctSizableModal modal1) 
		{
		FeedbackPanel feedback; 
		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);
		
		add(new EditSampleNewForm("editSampleForm", "to be assigned", new SampleDTO(), modal1));
		}

	
	private List<Pair> initializeFactorInfo(Sample sample)
		{
		String sampleId  = sample.getSampleID();
		
		Sample sampleWithFactors = sampleService.loadWithFactorsById(sampleId);//WithFactors(sampleId);
		
		List<ExperimentSetup> setupLst = sampleWithFactors.getFactorLevels(); 
		factorInfo = new ArrayList<Pair>();
		
		for (int i = 0; i < setupLst.size(); i++)
			{
			ExperimentSetup setup = setupLst.get(i);
			String factorName = setup.getLevel().getFactor().getFactorName();
			String factorValue = setup.getLevel().getValue();
			
			if (StringUtils.isNonEmpty(factorName) && StringUtils.isNonEmpty(factorValue))
			factorInfo.add(new Pair(factorName, factorValue));
			}
		
		return factorInfo;
		}
	
	
	
	public final class EditSampleNewForm extends Form 
		{
		boolean subjectIsNew = false;
		Subject subject = null;
		TextField<String> subjectNameField;
		
		private List<String> factorLevels = new ArrayList<String>();
		private List<String> factorNames = new ArrayList<String>();
		
		public EditSampleNewForm(final String id, final String sampleId, SampleDTO sample, final METWorksPctSizableModal modal1) 
			{
			super(id, new CompoundPropertyModel(sample));

			availableSubjectIds = subjectService.getSubjectIdsForExperimentId(sample.getExpID());
			availableSubjectIds.add(0, "Create New");
			
			sample.setSubjectName(grabSubjectName(sample));
			
			add(new Label("id", sampleId));
			add(new Label("expID", sample.getExpID()));
			add(newRequiredTextField("sampleName", 120, "Researcher Sample Name"));
			add(newTextField("userDescription", 4000, "User Description"));
			add(newTextField("userDefSampleType", 100, "User Defined Sample Type"));
			add(newTextField("userDefGOS", 30, "User Defined GOS"));
			
			BigDecimal min = new BigDecimal(0.0000001);
			BigDecimal max = new BigDecimal(99999999999L);
				
			RequiredTextField volFld;
			add(volFld = new RequiredTextField("volume"));
			volFld.setType(BigDecimal.class);
			volFld.add(RangeValidator.<BigDecimal>maximum(max));
					//locID
			add(new DropDownChoice("volUnits",  tableAccessService.getVolumeUnits()));//new PropertyModel(sample, "volUnits"),
			
			TextField currVolFld;
			add(currVolFld = new TextField("currVolume", new PropertyModel(sample, "currVolume"))); //.setType(BigDecimal.class));
			currVolFld.setType(BigDecimal.class);
			currVolFld.add(RangeValidator.<BigDecimal>maximum(max));	
			
			add(subjectNameField = buildSubjectNameField("subjectName", sample));
			add(buildSubjectDropdown("subjectId", sample));
			add(newRequiredTextField("sampleTypeId", 100, "Sample Type Id"));
			
			RequiredTextField gosIdFld;
			add(gosIdFld = new RequiredTextField("genusOrSpeciesID",new PropertyModel<Long>(sample, "genusOrSpeciesID")));
		//	gosIdFld.add(StringValidator.maximumLength(6));
			gosIdFld.setLabel(new Model<String>("Genus or Species ID"));
		
			
			add(buildLocationDrop("locationDrop", sample));
			
			add(buildFactorList("factorValues"));
			add(buildSaveButton("saveButton"));
			add(new AjaxCancelLink("cancelButton", modal1));
			}

		
		private ListView buildFactorList(String id)
			{
			ListView listView = new ListView(id, new PropertyModel(this, "factorInfo"))
				{
				@Override
				protected void populateItem(ListItem item) 
					{
					Pair pair = (Pair) item.getModelObject();
					
                    item.add(new TextField<String>("factorName", new PropertyModel<String>(pair, "id")).setEnabled(false));
									
					item.add(new TextField<String>("factorValue", new PropertyModel<String>(pair, "value")));

					item.add(OddEvenAttributeModifier.create(item));
					}
				};
			
			return listView;
			}
		

		public List<Pair> getFactorInfo()
			{
			return factorInfo;
			}

		
		private String grabSubjectName(SampleDTO sample)
			{
			try { subject = subjectService.loadSubjectById(sample.getSubjectId()); }
			catch (Exception e) {   }
			return (subject == null ? "" : subject.getUserSubjectId());
			}
		
		
		private RequiredTextField buildSubjectNameField(String id, final SampleDTO sample )
			{
			RequiredTextField txt = new  RequiredTextField(id)
				{
				public boolean isEnabled()
					{
					return sample.getSubjectId().equals("Create New");
					}
				};
				
			txt.setOutputMarkupId(true);
			return txt;
			}
		
		
		private DropDownChoice buildSubjectDropdown(String id, SampleDTO sample)
			{
			DropDownChoice<String> drp =  new DropDownChoice<String>(id,  availableSubjectIds);
			drp.add(buildStandardFormComponentUpdateBehavior("change", "updateForSubject", sample));
			return drp;
			}
		
		private DropDownChoice buildLocationDrop(String id, SampleDTO sample)
			{
			LoadableDetachableModel <List<String>> locationOptions = 
			new LoadableDetachableModel<List<String>>()
				{
				@Override
				protected List<String> load()  
					{ 
					return locationService.getLocationsForSamples();
					}
				};
				
			return new DropDownChoice(id, new PropertyModel(sample, "locID"), locationOptions);
			}
		
		
		private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(String event, final String response, final SampleDTO sample)
			{
			return new AjaxFormComponentUpdatingBehavior(event)
			    {
			    @Override
			    protected void onUpdate(AjaxRequestTarget target)
			    	{
			    	switch (response)
			        	{
			        	case "updateForSubject":
			        		target.add(subjectNameField);
			        		sample.setSubjectName(grabSubjectName(sample));
			        	break;
		        	
			        	default : break;
			        	}
			    	}
			    };
			}
		
		
		private TextField newTextField(String id, int maxLength, String fieldName) 
			{
			TextField textField = new TextField(id);
			textField.add(StringValidator.maximumLength(maxLength));
			if (!StringUtils.isEmptyOrNull(fieldName))
				textField.setLabel(new Model<String>(fieldName.trim()));
			return textField;
			}
		
		private RequiredTextField newRequiredTextField(String id, int maxLength, String fieldName) 
			{
			RequiredTextField textField = new RequiredTextField(id);
			textField.add(StringValidator.maximumLength(maxLength));
			if (!StringUtils.isEmptyOrNull(fieldName))
				textField.setLabel(new Model<String>(fieldName.trim()));
			return textField;
			}
		

		public boolean getSubjectIsNew() { return subjectIsNew; }
		public void setSubjectIsNew(boolean subjectIsNew) { this.subjectIsNew = subjectIsNew;  }
	

		private AjaxSubmitLink buildSaveButton(String linkID)
			{
			return new AjaxSubmitLink("saveButton")
				{
				@Override
				protected void onSubmit(AjaxRequestTarget target) // issue 464
					{
					SampleDTO sampleDto = (SampleDTO) getForm().getModelObject();
					
					Sample sample = null;
					try
						{
						
						
						sample = sampleService.updateSingleSample(sampleDto, factorInfo, sampleDto.getSubjectId().equals("Create New"));
						sample.getSampleName();
						EditSampleNew.this.info("Sample " + sample.getSampleID() + " has been updated successfully");
						}
					catch (Exception e)
						{
						String msg = e.getMessage(); 
					//	if (msg.startsWith("Location" || )msg.startsWith("Experiment") || msg.startsWith("Sample") || msg.startsWith("Genus"))
							EditSampleNew.this.error(msg);
						
						target.add(EditSampleNew.this.get("feedback"));
						}
					
					target.add(EditSampleNew.this.get("feedback"));
					}
				
				@Override
				protected void onError(AjaxRequestTarget target)
					{
					target.add(EditSampleNew.this.get("feedback"));
					}
				};
			}
		}
	
	}



/*
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.RangeValidator;
import org.apache.wicket.validation.validator.StringValidator;

import edu.umich.brcf.metabolomics.layers.service.TableAccessService;
import edu.umich.brcf.shared.layers.domain.ExperimentSetup;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.domain.Subject;
import edu.umich.brcf.shared.layers.dto.SampleDTO;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.layers.service.SubjectService;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.structures.Pair;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;
import edu.umich.brcf.shared.util.widgets.AjaxLocationField;
import edu.umich.brcf.shared.util.widgets.METWorksPctSizableModal;


public class EditSampleNew extends WebPage 
	{
	@SpringBean
	private SampleService sampleService;

	@SpringBean
	private AliquotService aliquotService;
	
	@SpringBean
	private SubjectService subjectService;
	
	@SpringBean
	TableAccessService tableAccessService;
	
	private List<String> availableSubjectIds = new ArrayList<String>();
	private List<Pair> factorInfo; 
	private String defaultGsLabel;
	
	RequiredTextField gosIdFld, userDefGOSFld;

	public EditSampleNew(Page backPage, IModel sampleModel, final METWorksPctSizableModal modal1) 
		{
		Sample sample = (Sample) sampleModel.getObject();
		
		FeedbackPanel feedback; 
		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);
		
		add(new EditSampleNewForm("editSampleForm", sample.getSampleID(), SampleDTO.instance(sampleService.loadById(sample.getId())),  modal1));
		factorInfo = initializeFactorInfo(sample);
		}

	
	public EditSampleNew(Page backPage, final METWorksPctSizableModal modal1) 
		{
		FeedbackPanel feedback; 
		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);
		
		add(new EditSampleNewForm("editSampleForm", "to be assigned", new SampleDTO(), modal1));
		}

	
	private List<Pair> initializeFactorInfo(Sample sample)
		{
		String sampleId  = sample.getSampleID();
		
		Sample sampleWithFactors = sampleService.loadWithFactorsById(sampleId);//WithFactors(sampleId);
		
		List<ExperimentSetup> setupLst = sampleWithFactors.getFactorLevels(); 
		factorInfo = new ArrayList<Pair>();
		
		for (int i = 0; i < setupLst.size(); i++)
			{
			ExperimentSetup setup = setupLst.get(i);
			String factorName = setup.getLevel().getFactor().getFactorName();
			String factorValue = setup.getLevel().getValue();
			
			if (StringUtils.isNonEmpty(factorName) && StringUtils.isNonEmpty(factorValue))
			factorInfo.add(new Pair(factorName, factorValue));
			}
		
		return factorInfo;
		}
	
	
	public final class EditSampleNewForm extends Form 
		{
		boolean subjectIsNew = false;
		Subject subject = null;
		TextField<String> subjectNameField;
		
		private List<String> factorLevels = new ArrayList<String>();
		private List<String> factorNames = new ArrayList<String>();
		
		public EditSampleNewForm(final String id, final String sampleId, SampleDTO sample, final METWorksPctSizableModal modal1) 
			{
			super(id, new CompoundPropertyModel(sample));

			availableSubjectIds = subjectService.getSubjectIdsForExperimentId(sample.getExpID());
			availableSubjectIds.add(0, "Create New");
			
			sample.setSubjectName(grabSubjectName(sample));
			
			add(new Label("id", sampleId));
			add(new Label("expID", sample.getExpID()));
			add(newRequiredTextField("sampleName", 120, "Researcher Sample Name"));
			add(newTextField("userDescription", 4000, "User Description"));
			add(newTextField("userDefSampleType", 100, "User Defined Sample Type"));
			add(newTextField("userDefGOS", 30, "User Defined GOS"));
			
			BigDecimal min = new BigDecimal(0.0000001);
			BigDecimal max = new BigDecimal(99999999999L);
				
			RequiredTextField volFld;
			add(volFld = new RequiredTextField("volume"));
			volFld.setType(BigDecimal.class);
			volFld.add(RangeValidator.<BigDecimal>maximum(max));
					
			add(new DropDownChoice("volUnits",  tableAccessService.getVolumeUnits()));//new PropertyModel(sample, "volUnits"),
			
			TextField currVolFld;
			add(currVolFld = new TextField("currVolume", new PropertyModel(sample, "currVolume"))); //.setType(BigDecimal.class));
			currVolFld.setType(BigDecimal.class);
			currVolFld.add(RangeValidator.<BigDecimal>maximum(max));	
			
			add(subjectNameField = buildSubjectNameField("subjectName", sample));
			add(buildSubjectDropdown("subjectId", sample));
			add(newRequiredTextField("sampleTypeId", 100, "Sample Type Id"));
			
		//	DropDownChoice<String> defaultLocationDrop = buildDefaultDrop("defaultLocationDrop", "defaultLocationId", locationsForSamples, "updateForDefaultLocation");
		//	add(defaultLocationDrop);	
		//	add(buildDefaultField("genusOrSpeciesID", "defaultGsId", "updateForDefaultGs", sample));
	//		add(buildDefaultField("defaultSampleType", "defaultSampleTypeId", "updateForDefaultSampleType"));
			
			System.out.println("Building GOS Field");
			sample.getGenusOrSpeciesID();
			sample.getUserDefGOS();
			add(gosIdFld = new RequiredTextField("genusOrSpeciesID", new PropertyModel<Long>(sample, "genusOrSpeciesID")));
			gosIdFld.setLabel(new Model<String>("Genus or Species ID"));
			System.out.println("Done Building GOS Field");
				
			add(userDefGOSFld = new RequiredTextField("userDefGOS", new PropertyModel<String>(sample, "userDefGOS")));
			System.out.println("Done Building GOS Def Field");
			
		
		//	AjaxGenusSpeciesField gosDefFld;
		
		//	add(gosDefFld = new AjaxGenusSpeciesField("defaultGenusSpecies",new PropertyModel<String>(this, "defaultGsLabel")));
		//	gosDefFld.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForDefaultGs", sample));
		//	System.out.println("Done Building GOS Def Field 2");
			//	gosIdFld.add(StringValidator.maximumLength(6));
			
			//add(newRequiredTextField("locID", 6, "Location ID"));
			add(new AjaxLocationField("locID", new PropertyModel<String>(sample, "locID")));
					
			
			add(buildFactorList("factorValues"));
			add(buildSaveButton("saveButton"));
			add(new AjaxCancelLink("cancelButton", modal1));
			}

		
		
		
		public TextField<String> buildDefaultField(String id, String property, String tag, SampleDTO dto)
			{
			TextField<String> fld = new TextField<String>(id, new PropertyModel(this, property));
			fld.add(buildStandardFormComponentUpdateBehavior("change",tag, dto));
			return fld;
			}
		
		/*
		private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(String event, final String response, final SampleDTO sample)
			{
			return new AjaxFormComponentUpdatingBehavior(event)
			    {
			    @Override
			    protected void onUpdate(AjaxRequestTarget target)
			    	{
			    	switch (response)
			        	{
			        //	case "updateForDefaultLocation":
			        //		for (int i = 0; i < inputData.samplesMetadata.infoFields.size(); i++)
					//			inputData.samplesMetadata.infoFields.get(i).setLocationId(defaultLocationId);
			        //		break;
			        	
			        //	case "updateForDefaultSampleType":
			        //		for (int i = 0; i < inputData.samplesMetadata.infoFields.size(); i++)
					//			inputData.samplesMetadata.infoFields.get(i).setSampleTypeId(defaultSampleTypeId);
			        //		break;
			        	
			        	
			        	}
			    	 
			    	target.add(container);
			    	}
			    };
			} 
		
		
		private ListView buildFactorList(String id)
			{
			ListView listView = new ListView(id, new PropertyModel(this, "factorInfo"))
				{
				@Override
				protected void populateItem(ListItem item) 
					{
					Pair pair = (Pair) item.getModelObject();
					
					item.add(new TextField<String>("factorName", new PropertyModel<String>(pair, "id")));
					item.add(new TextField<String>("factorValue", new PropertyModel<String>(pair, "value")));

					item.add(OddEvenAttributeModifier.create(item));
					}
				};
			
			return listView;
			}
		

		public List<Pair> getFactorInfo()
			{
			return factorInfo;
			}

		
		private String grabSubjectName(SampleDTO sample)
			{
			try { subject = subjectService.loadSubjectById(sample.getSubjectId()); }
			catch (Exception e) {   }
			return (subject == null ? "" : subject.getUserSubjectId());
			}
		
		
		private RequiredTextField buildSubjectNameField(String id, final SampleDTO sample )
			{
			RequiredTextField txt = new  RequiredTextField(id)
				{
				public boolean isEnabled()
					{
					return sample.getSubjectId().equals("Create New");
					}
				};
				
			txt.setOutputMarkupId(true);
			return txt;
			}
		
		
		private DropDownChoice buildSubjectDropdown(String id, SampleDTO sample)
			{
			DropDownChoice<String> drp =  new DropDownChoice<String>(id,  availableSubjectIds);
			drp.add(buildStandardFormComponentUpdateBehavior("change", "updateForSubject", sample));
			return drp;
			}
		
		
		private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(String event, final String response, final SampleDTO sample)
			{
			return new AjaxFormComponentUpdatingBehavior(event)
			    {
			    @Override
			    protected void onUpdate(AjaxRequestTarget target)
			    	{
			    	switch (response)
			        	{
			        	case "updateForSubject":
			        		target.add(subjectNameField);
			        		sample.setSubjectName(grabSubjectName(sample));
			        	break;
			        	
			        	case "updateForDefaultGs":
	        				sample.setGenusOrSpeciesID(StringParser.parseId(defaultGsLabel));
	        				sample.setUserDefGOS(StringParser.parseName(defaultGsLabel));
	        				
	        				target.add(gosIdFld);
	        				target.add(userDefGOSFld);
	        			break;
	        	
			        	case "updateForSubjectIdDrop" :
	        		
			        	break;
		        	
			        	default : break;
			        	}
			    	}
			    };
			}
		
		
		private TextField newTextField(String id, int maxLength, String fieldName) 
			{
			TextField textField = new TextField(id);
			textField.add(StringValidator.maximumLength(maxLength));
			if (!StringUtils.isEmptyOrNull(fieldName))
				textField.setLabel(new Model<String>(fieldName.trim()));
			return textField;
			}
		
		private RequiredTextField newRequiredTextField(String id, int maxLength, String fieldName) 
			{
			RequiredTextField textField = new RequiredTextField(id);
			textField.add(StringValidator.maximumLength(maxLength));
			if (!StringUtils.isEmptyOrNull(fieldName))
				textField.setLabel(new Model<String>(fieldName.trim()));
			return textField;
			}
		

		public boolean getSubjectIsNew() { return subjectIsNew; }
		public void setSubjectIsNew(boolean subjectIsNew) { this.subjectIsNew = subjectIsNew;  }
	

		private AjaxSubmitLink buildSaveButton(String linkID)
			{
			return new AjaxSubmitLink("saveButton")
				{
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form)
					{
					SampleDTO sampleDto = (SampleDTO) getForm().getModelObject();
					
					Sample sample = null;
					try
						{
						sample = sampleService.updateSingleSample(sampleDto, factorInfo, sampleDto.getSubjectId().equals("Create New"));
						sample.getSampleName();
						EditSampleNew.this.info("Sample " + sample.getSampleID() + " has been updated successfully");
						}
					catch (Exception e)
						{
						String msg = e.getMessage(); 
					//	if (msg.startsWith("Location" || )msg.startsWith("Experiment") || msg.startsWith("Sample") || msg.startsWith("Genus"))
							EditSampleNew.this.error(msg);
						
						target.add(EditSampleNew.this.get("feedback"));
						}
					
					target.add(EditSampleNew.this.get("feedback"));
					}
				
				@Override
				protected void onError(AjaxRequestTarget target, Form form)
					{
					target.add(EditSampleNew.this.get("feedback"));
					}
				};
			}
		public String getDefaultGsLabel()
			{
			return defaultGsLabel;
			}


		public void setDefaultGsLabel(String gsid)
			{
			defaultGsLabel = gsid;
			}
		}


	public String getDefaultGsLabel()
		{
		return defaultGsLabel;
		}


	public void setDefaultGsLabel(String defaultGsLabel)
		{
		this.defaultGsLabel = defaultGsLabel;
		}
	
	} */


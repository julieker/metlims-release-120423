package edu.umich.brcf.metabolomics.panels.lims.sample;


import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.dto.SampleDTO;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.SampleService;



public class EditSample extends WebPage {

	@SpringBean
	SampleService sampleService;

	@SpringBean
	AliquotService aliquotService;
	

	public EditSample(Page backPage, IModel sampleModel) 
		{
		Sample sample = (Sample) sampleModel.getObject();
		//super.add(new BoxBorder("border").setTransparentResolver(true));
		add(new FeedbackPanel("feedback"));
		add(new EditSampleForm("editSampleForm", sample.getSampleID(), SampleDTO.instance(sampleService.loadById(sample.getId()))));
		}

	public EditSample(Page backPage) 
		{
	//	super.add(new BoxBorder("border").setTransparentResolver(true));
		add(new FeedbackPanel("feedback"));
		add(new EditSampleForm("editSampleForm", "to be assigned", new SampleDTO()));
		}

	public final class EditSampleForm extends Form 
		{
		public EditSampleForm(final String id, final String sampleId, SampleDTO sample) 
			{
			super(id, new CompoundPropertyModel(sample));

			add(new Label("id", sampleId));
			add(newRequiredTextField("sampleName", 120));
			add(new TextField("userDescription").add((StringValidator.maximumLength(1000))));
			add(newRequiredTextField("expID", 7));
			add(new TextField("parentID"));
			add(new TextField("groupID"));
			add(new RequiredTextField("volume", new PropertyModel(sample, "volume")));
			add(new DropDownChoice("volUnits",  aliquotService.getAllVolUnits()));//new PropertyModel(sample, "volUnits"),
			add(newRequiredTextField("sampleTypeId", 100));
			add(new RequiredTextField("genusOrSpeciesID",new PropertyModel(sample, "genusOrSpeciesID")));
			add(newRequiredTextField("locID", 6));
			add(new CheckBox("sampleControlType") 
				{
				public boolean isEnabled()
					{
					return false;
					}
				});
			add(new SaveButton());
			}

		private RequiredTextField newRequiredTextField(String id, int maxLength) 
			{
			RequiredTextField textField = new RequiredTextField(id);
			textField.add(StringValidator.maximumLength(maxLength));
			return textField;
			}
		}

	private final class SaveButton extends Button 
		{
		private static final long serialVersionUID = 1L;

		private SaveButton() {  super("saveChanges");  }

		@Override
		public void onSubmit() 
			{
			SampleDTO sampleDto = (SampleDTO) getForm().getModelObject();
			//Sample sample = sampleService.updateSingleSample(sampleDto);
			//setResponsePage(new SampleDetail(new Model(sample)));
			}
		}
	}

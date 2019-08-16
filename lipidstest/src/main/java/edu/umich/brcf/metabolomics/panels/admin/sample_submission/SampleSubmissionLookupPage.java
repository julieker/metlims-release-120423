package edu.umich.brcf.metabolomics.panels.admin.sample_submission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.validator.StringValidator;

import edu.umich.brcf.metabolomics.layers.domain.GenusSpecies;
import edu.umich.brcf.metabolomics.layers.dto.SampleSubmissionSupportDTO;
import edu.umich.brcf.metabolomics.layers.service.GenusSpeciesService;
import edu.umich.brcf.shared.layers.domain.SampleType;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.SampleTypeService;


public class SampleSubmissionLookupPage extends WebPage 
	{
	@SpringBean
	GenusSpeciesService gsService;
	
	@SpringBean
	SampleTypeService stService;
	
	@SpringBean
	ExperimentService expService;
	
	public SampleSubmissionLookupPage(final Page  window) {
		//super.add(new BoxBorder("border").setTransparentResolver(true));
		add(new FeedbackPanel("feedback"));
		add(new SampleSubmissionLookupForm("sssForm", new SampleSubmissionSupportDTO()));
	}

	public final class SampleSubmissionLookupForm extends Form 
		{
		public SampleSubmissionLookupForm(final String id,  SampleSubmissionSupportDTO sssDto) {
			super(id, new CompoundPropertyModel(sssDto));

			final TextField idText;
			add(idText=new TextField("gsID"));
			
			final AutoCompleteTextField gsfield=newAjaxField("genusName", 60, "Genus");
			add(newHiddenLabel(this,"hiddengenus", gsfield));
			add(gsfield);
			add(new Button("getGenusID"){
				@Override
				public void onSubmit() 
					{
					try
						{
						GenusSpecies gs = gsService.loadByName(gsfield.getInput());
						idText.setModelObject(gs.getGsID()+"");
						}
					catch(Exception e)
		             	{
		            	e.printStackTrace();
		            	SampleSubmissionLookupPage.this.error("GenusSpecies not found, Please try again!");
		             	setResponsePage(getPage());
		             	}
					}
				});
			
			final TextField stidText;
			add(stidText=new TextField("stID"));
			
			final AutoCompleteTextField stfield=newAjaxField("sampleType", 60, "SampleType");
			add(newHiddenLabel(this,"hiddenst", stfield));
			add(stfield);
			
			add(new Button("getSampleTypeID"){
				@Override
				public void onSubmit() {
					try{
					SampleType st = stService.loadByDescription(stfield.getInput());
					stidText.setModelObject(st.getSampleTypeId());
					}
					catch(Exception e)
		             {
		            	 e.printStackTrace();
		            	 SampleSubmissionLookupPage.this.error("Sample Type not found, Please try again!");
		             	setResponsePage(getPage());
		             }
				}
			});
		}
		
		private AutoCompleteTextField newAjaxField(String id, int maxLenght, final String type) {
			final AutoCompleteTextField field = new AutoCompleteTextField(id, new Model("")) {

				@Override
				protected Iterator getChoices(String input) {
					if (Strings.isEmpty(input)) {
						return Collections.EMPTY_LIST.iterator();
					}
					if (type.equals("Genus"))
						return getGenusChoices(input);
					else
						return getSampleTypeChoices(input);
				}
			};
			field.add(StringValidator.maximumLength(maxLenght));
			return field;
		}
		
		private Label newHiddenLabel(SampleSubmissionLookupForm form, String id, AutoCompleteTextField field) {
			final Label label = new Label(id, field.getModel());
			label.setVisible(false);
			label.setOutputMarkupId(true);
			field.add(new AjaxFormSubmitBehavior(form, "change") {
				protected void onSubmit(AjaxRequestTarget target) {
					target.add(label);
				}

				@Override
				protected void onError(AjaxRequestTarget target) {
				}
			});
			return label;
		}
		
		private Iterator getGenusChoices(String input){
			List<String> choices = new ArrayList();
			for (GenusSpecies gs : gsService.limitedGenusSpecies(input)) {
				final String gsName = gs.getGenusName();
				if (gsName.toUpperCase().contains(input.toUpperCase()))
					choices.add(gsName);
			}
			return choices.iterator();
		}
		
		private Iterator getSampleTypeChoices(String input){
			List<String> choices = new ArrayList();
			for (SampleType sType : stService.getMatchingTypes(input)) {
				final String desc = sType.getDescription();
				if (desc.toUpperCase().contains(input.toUpperCase()))
					choices.add(desc);
			}
			return choices.iterator();
		}
	}
}

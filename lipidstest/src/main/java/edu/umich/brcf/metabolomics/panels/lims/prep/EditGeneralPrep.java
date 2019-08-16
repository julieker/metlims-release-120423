package edu.umich.brcf.metabolomics.panels.lims.prep;

import java.math.BigDecimal;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.domain.GeneralPrepSOP;
import edu.umich.brcf.metabolomics.layers.dto.GeneralPrepDTO;
import edu.umich.brcf.shared.layers.service.SamplePrepService;



public abstract class EditGeneralPrep extends WebPage{

	@SpringBean
	SamplePrepService samplePrepService;
	
	public EditGeneralPrep(Page backPage, GeneralPrepDTO gdto){
		add(new FeedbackPanel("feedback"));
		add(new GeneralPrepForm("generalPrepForm", gdto));
	}

	public final class GeneralPrepForm extends Form {
		public GeneralPrepForm(final String id, GeneralPrepDTO gdto){
			super(id, new CompoundPropertyModel(gdto));
			add(new RequiredTextField("sampleVolume"));
			add(new RequiredTextField("crashSolvent"));
			add(new RequiredTextField("recoveryStandardContent"));
			add(new RequiredTextField("crashVolume").setType(BigDecimal.class));
			add(new RequiredTextField("vortex"));
			add(new RequiredTextField("spin"));
			add(new RequiredTextField("nitrogenBlowdownTime"));
			add(new RequiredTextField("lyophilizerTime"));
			add(new RequiredTextField("gcVolume").setType(BigDecimal.class));
			add(new RequiredTextField("lcVolume").setType(BigDecimal.class));
			add(new Button("save"){
				@Override
				public void onSubmit() {
					GeneralPrepDTO prepDto = (GeneralPrepDTO) getForm().getModelObject();
					try{
						GeneralPrepSOP sop=samplePrepService.saveGeneralPrep(prepDto);
						EditGeneralPrep.this.onSave(sop);
						EditGeneralPrep.this.info("Save Successful!");
					}catch (Exception e){
						EditGeneralPrep.this.error("Save unsuccessful! Please re-check values entered!");
					}
				}
			});
		}
	}
	protected abstract void onSave(GeneralPrepSOP sop);
}

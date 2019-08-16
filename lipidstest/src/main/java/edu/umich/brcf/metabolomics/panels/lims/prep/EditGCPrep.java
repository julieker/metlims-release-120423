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

import edu.umich.brcf.metabolomics.layers.domain.GCDerivatizationMethod;
import edu.umich.brcf.metabolomics.layers.dto.GCDerivatizationDTO;
import edu.umich.brcf.shared.layers.service.SamplePrepService;



public abstract class EditGCPrep extends WebPage{
	
	@SpringBean
	SamplePrepService samplePrepService;
	
	public EditGCPrep(Page backPage, GCDerivatizationDTO gdto){
		add(new FeedbackPanel("feedback"));
		add(new GCPrepForm("gcPrepForm", gdto));
	}

	public final class GCPrepForm extends Form {
		public GCPrepForm(final String id, GCDerivatizationDTO gdto){
			super(id, new CompoundPropertyModel(gdto));
			add(new RequiredTextField("reagentComposition"));
			add(new RequiredTextField("incubationConditions"));
			add(new RequiredTextField("derivatizationVolume").setType(BigDecimal.class));
			add(new Button("save"){
				@Override
				public void onSubmit() {
					GCDerivatizationDTO prepDto = (GCDerivatizationDTO) getForm().getModelObject();
					try{
						GCDerivatizationMethod sop=samplePrepService.saveDerivatizationMethod(prepDto);
						EditGCPrep.this.onSave(sop);
						EditGCPrep.this.info("Save Successful!");
					}catch (Exception e){
						EditGCPrep.this.error("Save unsuccessful! Please re-check values entered!");
					}
				}
			});
		}
	}
	protected abstract void onSave(GCDerivatizationMethod sop);
}

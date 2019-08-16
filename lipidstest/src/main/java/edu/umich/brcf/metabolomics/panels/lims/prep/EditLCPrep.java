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

import edu.umich.brcf.metabolomics.layers.domain.LCReconstitutionMethod;
import edu.umich.brcf.metabolomics.layers.dto.LCReconstitutionDTO;
import edu.umich.brcf.shared.layers.service.SamplePrepService;


public abstract class EditLCPrep extends WebPage{

	@SpringBean
	SamplePrepService samplePrepService;
	
	public EditLCPrep(Page backPage, LCReconstitutionDTO ldto){
		add(new FeedbackPanel("feedback"));
		add(new LCPrepForm("lcPrepForm", ldto));
	}

	public final class LCPrepForm extends Form {
		public LCPrepForm(final String id, LCReconstitutionDTO ldto){
			super(id, new CompoundPropertyModel(ldto));
			add(new RequiredTextField("reconSolvent"));
			add(new RequiredTextField("reconVolume").setType(BigDecimal.class));
			add(new Button("save"){
				@Override
				public void onSubmit() {
					try{
						LCReconstitutionDTO prepDto = (LCReconstitutionDTO) getForm().getModelObject();
						LCReconstitutionMethod sop=samplePrepService.saveReconstitutionMethod(prepDto);
						EditLCPrep.this.onSave(sop);
						EditLCPrep.this.info("Save Successful!");
					}catch (Exception e){
						EditLCPrep.this.error("Save unsuccessful! Please re-check values entered!");
					}
				}
			});
		}
	}
	protected abstract void onSave(LCReconstitutionMethod prepDto);
}

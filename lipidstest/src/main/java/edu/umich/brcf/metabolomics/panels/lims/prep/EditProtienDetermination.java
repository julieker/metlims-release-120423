package edu.umich.brcf.metabolomics.panels.lims.prep;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.domain.ProtienDeterminationSOP;
import edu.umich.brcf.metabolomics.layers.dto.ProteinDeterminationDTO;
import edu.umich.brcf.shared.layers.service.SamplePrepService;


public abstract class EditProtienDetermination extends WebPage{

	@SpringBean
	SamplePrepService samplePrepService;
	
	public EditProtienDetermination(Page backPage, ProteinDeterminationDTO pdto){
		add(new FeedbackPanel("feedback"));
		add(new ProtienDeterminationForm("pdForm", pdto));
	}

	public final class ProtienDeterminationForm extends Form {
		public ProtienDeterminationForm(final String id, ProteinDeterminationDTO pdto){
			super(id, new CompoundPropertyModel(pdto));
//			add(new RequiredTextField("sampleVolume"));
			add(new RequiredTextField("bradfordAgent"));
			add(new RequiredTextField("wavelength"));
			add(new RequiredTextField("incubationTime"));
			add(new Button("save"){
				@Override
				public void onSubmit() {
				ProteinDeterminationDTO prepDto = (ProteinDeterminationDTO) getForm().getModelObject();
					try{
						ProtienDeterminationSOP sop=samplePrepService.saveProtienDetermination(prepDto);
						EditProtienDetermination.this.info("Save Successful!");
						EditProtienDetermination.this.onSave(sop);
					}catch (Exception e){
						EditProtienDetermination.this.error("Save unsuccessful! Please re-check values entered!");
					}
				}
			});
		}
	}
	protected abstract void onSave(ProtienDeterminationSOP sop);
}


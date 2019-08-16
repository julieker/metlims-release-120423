package edu.umich.brcf.metabolomics.panels.lims.prep;

import java.math.BigDecimal;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.dto.DilutionBean;
import edu.umich.brcf.shared.layers.service.SamplePrepService;


public abstract class EditDilution extends WebPage
	{
	@SpringBean
	SamplePrepService samplePrepService;
	
	public EditDilution(Page backPage, DilutionBean dil, boolean isSamplePrep)
		{
		add(new FeedbackPanel("feedback"));
		add(new DilutionForm("dilutionForm", dil, isSamplePrep));
		}

	public final class DilutionForm extends Form 
		{
		public DilutionForm(final String id, DilutionBean dil, boolean isSamplePrep)
			{
			super(id, new CompoundPropertyModel(dil));
			add(new RequiredTextField("sampleDiluted").setType(BigDecimal.class));
			if(isSamplePrep)
				add(new Label("units", "µL"));
			else
				add(new Label("units", "mg"));
			add(new RequiredTextField("dilutant"));
			add(new RequiredTextField("dilutantVolume").setType(BigDecimal.class));
			add(new Button("save"){
				@Override
				public void onSubmit() {
					DilutionBean bean = (DilutionBean) getForm().getModelObject();
					try{
						EditDilution.this.onSave(bean);
						EditDilution.this.info("Save Successful!");
					}catch (Exception e){
						EditDilution.this.error("Save unsuccessful! Please re-check values entered!");
					}
				}
			});
		}
	}
	protected abstract void onSave(DilutionBean bean);
}

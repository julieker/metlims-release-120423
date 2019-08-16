package edu.umich.brcf.metabolomics.panels.lims.prep;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.domain.HomogenizationSOP;
import edu.umich.brcf.metabolomics.layers.dto.HomogenizationDTO;
import edu.umich.brcf.shared.layers.service.SamplePrepService;


public abstract class EditHomogenization extends WebPage
	{
	@SpringBean
	SamplePrepService samplePrepService;
	
	public EditHomogenization(Page backPage, HomogenizationDTO hdto)
		{
		add(new FeedbackPanel("feedback"));
		add(new HomogenizationForm("homogenizationForm", hdto));
		}

	public final class HomogenizationForm extends Form 
		{
		public HomogenizationForm(final String id, HomogenizationDTO hdto)
			{
			super(id, new CompoundPropertyModel(hdto));
			add(new RequiredTextField("beadType"));
			add(new RequiredTextField("beadSize"));
			add(new RequiredTextField("beadVolume"));
			add(new RequiredTextField("vortex"));
			add(new RequiredTextField("time"));
			add(new RequiredTextField("temp"));
			
			add(new Button("save")
				{
				@Override
				public void onSubmit() {
					HomogenizationDTO prepDto = (HomogenizationDTO) getForm().getModelObject();
					try{
						HomogenizationSOP sop=samplePrepService.saveHomogenization(prepDto);
						EditHomogenization.this.onSave(sop);
						EditHomogenization.this.info("Save Successful!");
						}
					catch (Exception e){
						EditHomogenization.this.error("Save unsuccessful! Please re-check values entered!");
						}
					}
				});
			}
		}
	
	protected abstract void onSave(HomogenizationSOP sop);
	}


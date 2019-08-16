package edu.umich.brcf.metabolomics.panels.lims.prep;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.dto.PreppedFractionDTO;
import edu.umich.brcf.shared.layers.service.AliquotService;


public abstract class AddSampleToPrepPage extends WebPage
	{
	@SpringBean
	AliquotService aliquotService;
	
	public AddSampleToPrepPage()
		{
		add(new FeedbackPanel("feedback"));
		PreppedFractionDTO dto = new PreppedFractionDTO();
		add(new PreppedSampleForm("preppedSampleForm", dto));//, new PrepPlateBean()
		}

	public final class PreppedSampleForm extends Form 
		{
		public PreppedSampleForm(final String id, PreppedFractionDTO dto)
			{
			super(id, new CompoundPropertyModel(dto));
			add(new RequiredTextField("id", new PropertyModel(dto, "id")));
			add(new RequiredTextField("volume", new PropertyModel(dto, "volume")));
			add(new DropDownChoice("volUnits", aliquotService.getAllVolUnits())
				{
				public boolean isRequired() {  return true; }});
			
			add(new Button("save")
				{
				@Override
				public void onSubmit() 
					{
					PreppedFractionDTO frDto = (PreppedFractionDTO) getForm().getModelObject();
					try
						{
						AddSampleToPrepPage.this.onSave(frDto);
						AddSampleToPrepPage.this.info("Save Successful!");
						}
					catch (Exception e)
						{
						AddSampleToPrepPage.this.error("Save unsuccessful! Please re-check values entered!");
						}
					}
				});
			}
		}
	
	protected abstract void onSave(PreppedFractionDTO frDto);
	}

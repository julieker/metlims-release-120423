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

import edu.umich.brcf.shared.layers.dto.BufferBean;
import edu.umich.brcf.shared.layers.service.SamplePrepService;



public abstract class EditBuffer extends WebPage
	{
	@SpringBean
	SamplePrepService samplePrepService;
	
	public EditBuffer(Page backPage, BufferBean buf)
		{
		add(new FeedbackPanel("feedback"));
		add(new BufferForm("bufferForm", buf));
		}

	
	public final class BufferForm extends Form 
		{
		public BufferForm(final String id, BufferBean buf)
			{
			super(id, new CompoundPropertyModel(buf));
			add(new RequiredTextField("bufferType"));
			add(new RequiredTextField("bufferVolume").setType(BigDecimal.class));
			add(new Button("save")
				{
				@Override
				public void onSubmit() 
					{
					BufferBean bean = (BufferBean) getForm().getModelObject();
					try{
						EditBuffer.this.onSave(bean);
						EditBuffer.this.info("Save Successful!");
						}
					catch (Exception e){
						EditBuffer.this.error("Save unsuccessful! Please re-check values entered!");
						}
					}
				});
			}
		}
	
	protected abstract void onSave(BufferBean bean);
	}

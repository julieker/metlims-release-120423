package edu.umich.brcf.metabolomics.panels.lims.prep;

import java.math.BigDecimal;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.dto.PreppedSampleDTO;
import edu.umich.brcf.shared.layers.service.AliquotService;


public abstract class PreppedFractionDetail extends WebPage{
	
	PreppedFractionDetail pfd;

	@SpringBean
	AliquotService aliquotService;
	
	public void setAliquotService(AliquotService aliquotService) {
		this.aliquotService = aliquotService;
	}

	public PreppedFractionDetail(Page backPage, PreppedSampleDTO prepSample) {
		add(new FeedbackPanel("feedback"));
		add(new PrepFractionForm("prepFractionForm", prepSample));
		pfd=this;
		setOutputMarkupId(true);
	}

	public final class PrepFractionForm extends Form {
		public PrepFractionForm(final String id, final PreppedSampleDTO prepSample){
			super(id, new CompoundPropertyModel(prepSample));
			add(new Label("id"));
			add(new RequiredTextField("volume"));
			add(new DropDownChoice("volUnits", aliquotService.getAllVolUnits()));
			add(new TextField("sampleDiluted").setType(BigDecimal.class));
			add(new TextField("dilutant"));
			add(new TextField("dilutantVolume").setType(BigDecimal.class));
			add(new SaveButton());
		}
	}
	
	private final class SaveButton extends Button {
		private static final long serialVersionUID = 1L;
		private SaveButton() {
			super("save");
		}
		@Override
		public void onSubmit() {
			PreppedSampleDTO prepSample = (PreppedSampleDTO) getForm().getModelObject();
			try{
				PreppedFractionDetail.this.onSave(prepSample);
				PreppedFractionDetail.this.info("Save Successful!");
			}catch (Exception e){
				PreppedFractionDetail.this.error("Save unsuccessful! Please re-check values entered!");
			}
		}
	}
	
	protected abstract void onSave(PreppedSampleDTO prepSample);
}

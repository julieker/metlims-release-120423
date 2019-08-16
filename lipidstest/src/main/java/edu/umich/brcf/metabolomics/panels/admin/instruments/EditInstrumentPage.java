///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 	EditInstrumentPage.java
// 	Written by Jan Wigginton September 2015
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.admin.instruments;


import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.emory.mathcs.backport.java.util.Arrays;
import edu.umich.brcf.metabolomics.layers.domain.Instrument;
import edu.umich.brcf.metabolomics.layers.dto.InstrumentDTO;
import edu.umich.brcf.metabolomics.layers.service.InstrumentService;
import edu.umich.brcf.shared.util.behavior.FocusOnLoadBehavior;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;


public abstract class EditInstrumentPage extends WebPage
	{
	@SpringBean
	InstrumentService instrumentService;

	String titleLabel;
	
	public EditInstrumentPage(Page backPage, IModel<Instrument> model, ModalWindow modal) 
		{
		boolean ifNew = (model == null);
		
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new EditInstrumentForm("editInstrumentForm", ifNew ? "to be assigned" : model.getObject().getInstrumentID(), 
				ifNew ? new InstrumentDTO() : new InstrumentDTO(model.getObject()), modal));
		setTitleLabel(ifNew ? "New Instrument" : "Edit Instrument");
		add(new Label("titleLabel", new PropertyModel(this, "titleLabel")));
		}

	
	public final class EditInstrumentForm extends Form 
		{
		public EditInstrumentForm(final String id, final String org_id, InstrumentDTO dto, final ModalWindow modal) 
			{
			super(id, new CompoundPropertyModel(dto));

			add(new RequiredTextField("name").add(new FocusOnLoadBehavior()));
			add(new RequiredTextField("description"));
			add(new DropDownChoice("type", instrumentService.getListOfAllInstrumentTypes()));
			add(new TextField("room"));
			add(new RequiredTextField("manufacturer"));
			add(new TextField("model"));
			add(new TextField("serialNumber"));
			add(new DropDownChoice("instrumentClass", Arrays.asList(new String [] {"ANALYTICAL", "COMPUTER"})));
			add(new AjaxCancelLink(modal));
			
			add(new IndicatingAjaxButton ("saveChanges", this)
				{
				@Override
				protected void onSubmit(AjaxRequestTarget target) // issue 464
					{
					InstrumentDTO instDto = (InstrumentDTO) getForm().getModelObject();
					try	{
						Instrument instrument = instrumentService.save(instDto);
						EditInstrumentPage.this.info("Instrument '"+ instDto.getName() +"' saved successfully.");
						target.add(EditInstrumentPage.this.get("feedback"));
						EditInstrumentPage.this.onSave(instrument, target);
						}
					catch (Exception e)
						{
						e.printStackTrace();
						EditInstrumentPage.this.error("Save unsuccessful. Please check values.");
						target.add(EditInstrumentPage.this.get("feedback"));
						}
					}
				
				@Override
				protected void onError(AjaxRequestTarget target) 
					{
					target.add(EditInstrumentPage.this.get("feedback"));
					}
				});
			}
		}
	
	void setTitleLabel(String label)
		{
		this.titleLabel = label;
		}

	String getTitleLabel()
		{
		return titleLabel;
		}
	
	protected abstract void onSave(Instrument instrument, AjaxRequestTarget target);
	}
	
	

////////////////////////////////////////////////////
// EditSampleVolume.java
// Written by Jan Wigginton, Nov 19, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.panels.utilitypanels;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.service.TableAccessService;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;



public abstract class EditSampleVolume extends WebPage
	{
	@SpringBean
	TableAccessService tableAccessService;
	
	Double volume;
	String volUnits = null;
	Boolean issueUnitsWarning = true;
	
	public EditSampleVolume(Page backPage, ModalWindow modal)
		{
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		
		add(new EditSampleVolumeForm("editSampleVolumeForm", modal));
		}
	
	
	public final class EditSampleVolumeForm extends Form 
		{
		public EditSampleVolumeForm(final String id, ModalWindow modal)
			{
			super(id);
			
			IModel<Double> model = new PropertyModel<Double>(this, "volume");
			TextField<Double> volumeFld = new TextField<Double>("volume", model);
			volumeFld.setType(Double.class);
			volumeFld.setRequired(true);
			add(volumeFld);
			
			DropDownChoice<String> unitsDrp;
			add(unitsDrp = new DropDownChoice<String>("volUnits",   new PropertyModel<String>(this, "volUnits"), tableAccessService.getVolumeUnits()));
			unitsDrp.setRequired(true); 
			
			add(buildSaveLink("saveLink"));
			add(new AjaxCancelLink("closeButton", modal));
			}
		
		

		public AjaxSubmitLink buildSaveLink(String id)
			{
			return new AjaxSubmitLink (id, this)
				{
				@Override
				protected void onSubmit(AjaxRequestTarget target) 
					{
					try
						{
						EditSampleVolume.this.onSave((Double)getForm().get("volume").getDefaultModelObject(), getForm().get("volUnits").getDefaultModelObjectAsString(),  target, issueUnitsWarning);
						EditSampleVolume.this.info("Volume updated successfully.");
						}
					catch (Exception e)
						{ 
						String msg = e.getMessage() == null ? "" : e.getMessage();
						if (  (issueUnitsWarning && msg.startsWith("Warning")) ||
								(msg.startsWith("Error")))
							{
							EditSampleVolume.this.error(msg);
						    issueUnitsWarning = false;
						   }
						else
						   EditSampleVolume.this.error("Save unsuccessful. Please re-check values entered."); 
						
						}
					
					target.add(EditSampleVolume.this.get("feedback"));
					}
			
				@Override
		         protected void onError(final AjaxRequestTarget target) //issue 464
					{
		            target.add(EditSampleVolume.this.get("feedback"));
					}
				};
			}
		public Double getVolume()
			{
			return volume;
			}

		public void setVolume(Double v)
			{
			volume = v;
			}
		
		public String getVolUnits()
			{
			return volUnits;
			}

		public void setVolUnits(String v)
			{
			volUnits = v;
			}

		}
		
		
	
	public String getVolUnits()
		{
		return volUnits;
		}

	public void setVolUnits(String volUnits)
		{
		this.volUnits = volUnits;
		}

	public Double getVolume()
		{
		return volume;
		}

	public void setVolume(Double volume)
		{
		this.volume = volume;
		}
	
	protected abstract void onSave(Double volume, String volUnits, AjaxRequestTarget target, Boolean issueWarning);
	}
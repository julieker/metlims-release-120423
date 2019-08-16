package edu.umich.brcf.shared.panels.utilitypanels;


import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import edu.umich.brcf.shared.layers.domain.SampleStatus;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;



public abstract class EditSampleStatus extends WebPage
	{
	String status = "In Storage";
	
	public EditSampleStatus(Page backPage, ModalWindow modal)
		{
		add(new EditSampleStatusForm("editSampleStatusForm", modal));
		}
	
	public final class EditSampleStatusForm extends Form 
		{
		public EditSampleStatusForm(final String id, ModalWindow modal)
			{
			super(id);
			DropDownChoice statusDD=new DropDownChoice("status", new PropertyModel(this,"status"),
					SampleStatus.Lims_Sample_statuses, new ChoiceRenderer()
				{
				public Object getDisplayValue(Object object)
	            	{
	                String stringrep;
	                Character temp = (Character) object;
	                switch (temp)
	                	{
	                    case 'S' : stringrep = "In_Storage"; break;
	                    case 'P' : stringrep = "Prepped"; break;	
	                    case 'R' : stringrep = "Processed"; break;
	                    case 'I' : stringrep = "Injected"; break;
	                    case 'C' : stringrep = "Complete"; break;
	                    // Issue 222
	                    case 'T' : stringrep = "Discarded"; break;
	                    case 'B' : stringrep = "Returned"; break;
	                    default : throw new IllegalStateException(temp + " is not mapped!");
	                	}
	                return stringrep;
	            	}
				
				public String getIdValue(Object object, int index)
	            	{
					Character idx = SampleStatus.Lims_Sample_statuses.get(index); 
					return (idx == null ? SampleStatus.Lims_Sample_statuses.get(0).toString() : idx.toString());
	            	}
				});
			
			statusDD.setRequired(true);
			add(statusDD);
			add(new AjaxSubmitLink ("saveChanges", this)
				{
				@Override
				protected void onSubmit(AjaxRequestTarget target) 
					{
					try
						{
						EditSampleStatus.this.onSave((String)getForm().get("status").getDefaultModelObject(), target);
						}
					catch (Exception e)
						{
						EditSampleStatus.this.error("Save unsuccessful. Please re-check values entered.");
						}
					}

				@Override
				protected void onError(AjaxRequestTarget arg0) { }
	
				});
			
			add(new AjaxCancelLink("close", modal));
			}
		
		
		String status;
		public String getStatus()
			{
			return status;
			}
		
		public void setStatus(String status)
			{
			this.status=status;
			}
		}
	
	protected abstract void onSave( String status, AjaxRequestTarget target);
}


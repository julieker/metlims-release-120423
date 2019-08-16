package edu.umich.brcf.shared.util.widgets;


import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.validator.StringValidator;

public class AjaxUpdatingRequiredTextField extends RequiredTextField {
	public AjaxUpdatingRequiredTextField(String id, String event) {
		super(id);
		add(new AjaxFormComponentUpdatingBehavior(event) {
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(getFormComponent());
			}
		});
	}

	public AjaxUpdatingRequiredTextField(String id, IModel model, String event, int length) 
		{
		super(id, model);
		add(StringValidator.maximumLength(length));
		add(new AjaxFormComponentUpdatingBehavior(event) 
			{
			protected void onUpdate(AjaxRequestTarget target) 
				{
				target.add(getFormComponent());
				}
			});
		}
	
	public AjaxUpdatingRequiredTextField(String id, String event, int length) {
		super(id);
		add(StringValidator.maximumLength(length));
		add(new AjaxFormComponentUpdatingBehavior(event) {
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(getFormComponent());
			}
		});
	}

	public AjaxUpdatingRequiredTextField(String id, String event, final IValidator validator) {
		super(id);
		add(new AjaxFormComponentUpdatingBehavior(event) {
			protected void onUpdate(AjaxRequestTarget target) {
				FormComponent fc = getFormComponent().add(validator);
				fc.validate();
				target.add(fc);
			}
		});
	}
}

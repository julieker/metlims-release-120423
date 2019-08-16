package edu.umich.brcf.shared.util.widgets;


import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.validator.StringValidator;




public class AjaxUpdatingTextField extends TextField {

	public AjaxUpdatingTextField(String id, String event) {
		super(id);
		add(new AjaxFormComponentUpdatingBehavior(event) {
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(getFormComponent());
			}
		});
	}

	public AjaxUpdatingTextField(String id, String event, int length) {
		super(id);
		add(StringValidator.maximumLength(length));
		add(new AjaxFormComponentUpdatingBehavior(event) {
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(getFormComponent());
			}
		});
	}

	public AjaxUpdatingTextField(String id, String event, final IValidator validator) {
		super(id);
		add(new AjaxFormComponentUpdatingBehavior(event) {
			protected void onUpdate(AjaxRequestTarget target) {
				FormComponent fc = getFormComponent().add(validator);
				fc.validate();
				target.add(fc);
			}
		});
		}
		
	public AjaxUpdatingTextField(String id, String event, PropertyModel model) 
		{
		super(id, model);
		add(new AjaxFormComponentUpdatingBehavior(event) 
			{
			protected void onUpdate(AjaxRequestTarget target) 
				{
				target.add(getFormComponent());
				}
			});
		}
	}

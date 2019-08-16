package edu.umich.brcf.shared.util.widgets;


import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.validator.StringValidator;


public abstract class AbstractAjaxUpdatingRequiredTextField extends RequiredTextField 
	{
	public AbstractAjaxUpdatingRequiredTextField(String id, String event) 
		{
		super(id);
		add(new AjaxFormComponentUpdatingBehavior(event) 
			{
			protected void onUpdate(AjaxRequestTarget target) 
				{
				target.add(getFormComponent());
				doUpdate(target);
				}
			});
		}

	
	public AbstractAjaxUpdatingRequiredTextField(String id, IModel model, String event, int length) 
		{
		super(id, model);
		add(StringValidator.maximumLength(length));
		add(new AjaxFormComponentUpdatingBehavior(event) 
			{
			protected void onUpdate(AjaxRequestTarget target) 
				{
				target.add(getFormComponent());
				//target.add(getFormComponent());
				doUpdate(target);
				}
			});
		}
	
	
	public AbstractAjaxUpdatingRequiredTextField(String id, IModel model, String event) 
		{
		super(id, model);
		//add(NumberValidator.POSITIVE);
		add(new AjaxFormComponentUpdatingBehavior(event) 
			{
			protected void onUpdate(AjaxRequestTarget target) 
				{
				//target.add(getFormComponent());
				target.add(getFormComponent());
				doUpdate(target);
				}
			});
		}
	
	
	public AbstractAjaxUpdatingRequiredTextField(String id, String event, int length) 
		{
		super(id);
		add(StringValidator.maximumLength(length));
		add(new AjaxFormComponentUpdatingBehavior(event) {
			protected void onUpdate(AjaxRequestTarget target) {
				//target.add(getFormComponent());
				target.add(getFormComponent());
				doUpdate(target);
			}
		});
	}

	public AbstractAjaxUpdatingRequiredTextField(String id, String event, final IValidator validator) {
		super(id);
		add(new AjaxFormComponentUpdatingBehavior(event) {
			protected void onUpdate(AjaxRequestTarget target) {
				FormComponent fc = getFormComponent().add(validator);
				fc.validate();
				//target.add(fc);
				target.add(fc);
				doUpdate(target);
			}
		});
	}
	
	protected abstract void doUpdate(AjaxRequestTarget target);
	}

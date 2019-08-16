////////////////////////////////////////////////////
// AjaxUpdatingDecimalTextField.java
// Written by Jan Wigginton, Dec 5, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.widgets;


import java.math.BigDecimal;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.RangeValidator;



public class AjaxUpdatingDecimalTextField extends TextField<BigDecimal>
	{
	private boolean makeRequired = true;
	
	public AjaxUpdatingDecimalTextField(String id, String event) 
		{
		super(id);
		add(new AjaxFormComponentUpdatingBehavior(event) 
			{
			protected void onUpdate(AjaxRequestTarget target) 
				{
				target.add(getFormComponent());
				}
			});
		setRequired(makeRequired);
		}

	
	public AjaxUpdatingDecimalTextField(String id, String event, IModel<BigDecimal> model) 
		{
		super(id, model);
		add(new AjaxFormComponentUpdatingBehavior(event) 
			{
			protected void onUpdate(AjaxRequestTarget target) 
				{
				target.add(getFormComponent());
				}
			});
		setRequired(makeRequired);
		}
	
	
	public AjaxUpdatingDecimalTextField(String id, String event, IModel<BigDecimal> model, 
		BigDecimal min, BigDecimal max) 
		{
		this(id, event, model);
		add(RangeValidator.<BigDecimal>range(min, max));
		}
	
	
	public boolean isMakeRequired()
		{
		return makeRequired;
		}


	public void setMakeRequired(boolean makeRequired)
		{
		this.makeRequired = makeRequired;
		setRequired(makeRequired);
		}
	}


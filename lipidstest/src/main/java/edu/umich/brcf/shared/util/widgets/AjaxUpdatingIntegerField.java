////////////////////////////////////////////////////
// AjaxUpdatingIntegerField.java
// Written by Jan Wigginton, Dec 5, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.widgets;


import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.RangeValidator;



public class AjaxUpdatingIntegerField extends TextField<Integer>
	{
	private boolean makeRequired = true;
	
	public AjaxUpdatingIntegerField(String id, String event) 
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

	
	public AjaxUpdatingIntegerField(String id, String event, IModel<Integer> model) 
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
	
	
	public AjaxUpdatingIntegerField(String id, String event, IModel<Integer> model, 
		int min, int max) 
		{
		this(id, event, model);
		add(RangeValidator.<Integer>range(min, max));
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



/* 
IModel<Integer> model = new PropertyModel<Integer>(dto, "nSamples");
TextField<Integer> nSamplesField = new TextField<Integer>("nSamples", model);
add(nSamplesField).add(RangeValidator.<Integer>range(0, 9999));
nSamplesField.setRequired(true);
*/

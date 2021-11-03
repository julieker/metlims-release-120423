////////////////////////////////////////////////////
// SearchTypeDropDown.java
// Written by Jan Wigginton, Aug 22, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.widgets;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;



public abstract class SearchTypeDropDown extends DropDownChoice
	{
	List<String> availableSearchTypes = null;
	
	public SearchTypeDropDown(String id, Object object, final String propertyName)
		{
		super(id, new PropertyModel(object, propertyName));
		
		this.setModel(new PropertyModel(object, propertyName));
		availableSearchTypes = getSearchTypes();
		setChoices(new LoadableDetachableModel<List<String>>() 
			{
			@Override
				protected List<String> load() 
				{ 
				if (availableSearchTypes == null)
				availableSearchTypes = getSearchTypes();
				
				return availableSearchTypes;
				}
			});
	
	add(buildUpdateBehavior("change", "updateForVolumeDrop"));			
	}
	
	
	private List <String> getSearchTypes()
		{
		// issue 181
		return Arrays.asList(new String [] {"Experiment Id", "Experiment Name", "Project Id", "Project Name", "Contact Name", "Principal Investigator",  "Organization Name", "Assay Id"});
		}
	
	
	protected AjaxFormComponentUpdatingBehavior buildUpdateBehavior(String event, String eventTag)
		{
		return new AjaxFormComponentUpdatingBehavior(event)
			{
			@Override
			protected void onUpdate(AjaxRequestTarget target)
				{
				doUpdateBehavior(target);
				}	
			};
	}
	
	protected abstract void doUpdateBehavior(AjaxRequestTarget target);
	}




////////////////////////////////////////////
//AjaxSampleTypeField.java
//Written by Jan Wigginton, October 2015
////////////////////////////////////////////

package edu.umich.brcf.shared.panels.utilitypanels;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import edu.umich.brcf.shared.layers.domain.SampleType;
import edu.umich.brcf.shared.layers.service.SampleTypeService;



public class AjaxSampleTypeField extends AutoCompleteTextField
	{
	@SpringBean
	SampleTypeService sampleTypeService;
	
	public AjaxSampleTypeField(String id) 
		{
		super(id, new Model(""));
		}
	
	
	public AjaxSampleTypeField(String id, IModel model) 
		{
		super(id, model);
		}
	

	@Override
	protected Iterator getChoices(String input) 
		{
		if (Strings.isEmpty(input)) 
			return Collections.EMPTY_LIST.iterator();
		else 
			return getSampleTypeChoices(input);
		}
	
	
	private Iterator getSampleTypeChoices(String input)
		{
		List<String> choices = new ArrayList();
		for (SampleType sType : sampleTypeService.getMatchingTypes(input)) 
			{
			final String desc = sType.getDescription();
			final String stId = sType.getSampleTypeId();
			if (desc.toUpperCase().contains(input.toUpperCase()))
				choices.add(desc + " (" + stId + ")");
			}
		return choices.iterator();
		}
	}

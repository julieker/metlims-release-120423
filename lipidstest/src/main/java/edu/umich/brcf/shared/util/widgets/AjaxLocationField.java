////////////////////////////////////////////////////
// AjaxLocationField.java
// Written by Jan Wigginton, Jun 7, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.widgets;

import java.util.Collections;
import java.util.Iterator;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import edu.umich.brcf.shared.layers.service.LocationService;

public class AjaxLocationField extends AutoCompleteTextField
	{
	@SpringBean
    LocationService locationService;
    
	public AjaxLocationField(String id) 
		{
		super(id, new Model(""));
		}
	
	public AjaxLocationField(String id, IModel model) 
		{
		super(id, model);
		}

	@Override
	protected Iterator getChoices(String input) 
		{
		if (Strings.isEmpty(input)) 
			return Collections.EMPTY_LIST.iterator();
		else 
			return locationService.getSampleLocationNamesByUnit("-80 freezer").iterator();
		}
	}
	

////////////////////////////////////////////////////
//AjaxContactField.java
//Written by Jan Wigginton, Feb 10, 2017
////////////////////////////////////////////////////

package edu.umich.brcf.shared.util.widgets;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import edu.umich.brcf.shared.layers.service.ClientService;
import edu.umich.brcf.shared.layers.service.UserService;

//SampleTypeField
public class AjaxContactField extends AutoCompleteTextField
	{
	@SpringBean 
	ClientService clientService;
	
	@SpringBean 
	UserService userService;
	
	public AjaxContactField(String id) 
		{
		super(id, new Model(""));
		}
	
	@Override
	protected Iterator getChoices(String input) 
		{
		if (Strings.isEmpty(input)) 
			return Collections.EMPTY_LIST.iterator();
		
		return getUserChoices(input);
		}
	
	private Iterator getUserChoices(String input)
		{
		List<String> choices = new ArrayList();
		
		try
			{
			List<String> contactChoices  = userService.allUserNamesContactSearch(input);
			
			for (String contact : contactChoices) {
				if (contact.toUpperCase().contains(input.toUpperCase()))
					choices.add(contact);
				}
			}
		catch(IllegalStateException ie){  }
		return choices.iterator();
		}
	}


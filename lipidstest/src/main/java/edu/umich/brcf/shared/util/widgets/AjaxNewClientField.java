////////////////////////////////////////////////////
// AjaxNewClientField.java
// Written by Jan Wigginton, Apr 12, 2017
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


public class AjaxNewClientField extends AutoCompleteTextField
	{
	@SpringBean
	ClientService clientService;
	
	boolean withIds, withLabName;
	
	public AjaxNewClientField(String id) 
		{
		this(id, false);
		}

	
	public AjaxNewClientField(String id, boolean withLabName)
		{
		super(id, new Model(""));
		//this.withIds = withIds;
		this.withLabName = withLabName;
		}
	
	@Override
	protected Iterator getChoices(String input) 
		{
		if (Strings.isEmpty(input)) 
			return Collections.EMPTY_LIST.iterator();
		 
		return (getClientLabChoices(input));
		}

	
	private Iterator getClientLabChoices(String input)
		{
		List<String> choices = new ArrayList();
		try
			{
			List<String> labNames = clientService.allClientLabs(withLabName);
			
			for (String contact : labNames) 
				if (contact.toUpperCase().contains(input.toUpperCase()))
					choices.add(contact);
			}
		catch(IllegalStateException ie){ System.out.println("Input is "+input); }
		
		return choices.iterator();
		}
	
	// TO DO -- FIX 
	/*
	private Iterator getClientChoices(String input)
		{
		List<String> choices = new ArrayList();
		try
			{
			List<String> contactNames = withIds ? clientService.allContactsWithIds() 
					: clientService.allContacts();
			
			for (String contact : contactNames) 
				if (contact.toUpperCase().contains(input.toUpperCase()))
					choices.add(contact);
			}
		catch(IllegalStateException ie){ System.out.println("Input is "+input); }
		
		return choices.iterator(); */
		}
//	}

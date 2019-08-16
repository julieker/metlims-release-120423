//////////////////////////////////////////
//AjaxUserField.java
//Written by Jan Wigginton October 2015
///////////////////////////////////////////


package edu.umich.brcf.shared.util.widgets;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import edu.umich.brcf.shared.layers.service.UserService;

public class AjaxUserField extends AutoCompleteTextField
	{

	@SpringBean
	UserService userService;

	Boolean allOption = false;

	public AjaxUserField(String id)
		{
		this(id, false);
		}

	public AjaxUserField(String id, boolean allOption)
		{
		super(id, new Model(""));
		this.allOption = allOption;
		}

	@Override
	protected Iterator getChoices(String input)
		{
		if (Strings.isEmpty(input))
			return Collections.EMPTY_LIST.iterator();
		else
			return getUserChoices(input);
		}
	

	private Iterator getUserChoices(String input)
		{
		List<String> choices = new ArrayList();
		try
			{
			for (String contact : grabAllPossibleNames())
				{
				if (contact.toUpperCase().contains(input.toUpperCase()))
					choices.add(contact);
				}
			} catch (IllegalStateException ie)
			{
			System.out.println("Input is " + input);
			}
		return choices.iterator();
		}

	private List<String> grabAllPossibleNames()
		{
		List<String> lst = userService.allUserNamesAndIds();

		return lst;
		}
	}

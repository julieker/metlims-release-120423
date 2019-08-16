package edu.umich.brcf.shared.util.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import edu.umich.brcf.shared.layers.service.OrganizationService;

public class AjaxOrganizationField extends AutoCompleteTextField
	{

	@SpringBean
	OrganizationService orgService;

	public AjaxOrganizationField(String id)
		{
		super(id, new Model(""));
		}

	@Override
	protected Iterator getChoices(String input)
		{
		if (Strings.isEmpty(input))
			return Collections.EMPTY_LIST.iterator();
		else
			return getOrganizationChoices(input);
		}

	private Iterator getOrganizationChoices(String input)
		{
		List<String> choices = new ArrayList();
		try
			{
			for (String org : orgService.allOrganizations())
				{
				if (org.toUpperCase().contains(input.toUpperCase()))
					choices.add(org);// + " (" + exp.getExpID()+")"
				}
			} catch (IllegalStateException ie)
			{
			System.out.println("Input is " + input);
			}
		return choices.iterator();
		}
	}

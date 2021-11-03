////////////////////////////////////////////////////
// AjaxAssayField.java
// Written by Julie Keros Oct 15 2021 for issue 187
////////////////////////////////////////////////////

package edu.umich.brcf.shared.util.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.ExperimentService;


public class AjaxAssayField extends AutoCompleteTextField
	{
	//@SpringBean
	//ExperimentService experimentService;
	
	@SpringBean
	AssayService assayService;

	public AjaxAssayField(String id)
		{
		super(id, new Model(""));
		}

	public AjaxAssayField(String id, IModel model)
		{
		super(id, model);
		}

	@Override
	protected Iterator getChoices(String input)
		{
		if (Strings.isEmpty(input))
			return Collections.EMPTY_LIST.iterator();
		
		return getAssayChoices(input);
		}

	private Iterator getAssayChoices(String input)
		{
		List<String> choices = new ArrayList();
		try
			{
			System.out.println("here is input:" + input);
			
	       List<String>  assayNamesSorted =   assayService.allAssayNamesForPlatform(input.toUpperCase()).stream().sorted().collect(Collectors.toList());   ; 
			
			// for (String expl : assayService.allAssayNamesForPlatform(input.toUpperCase()).stream().sorted().collect(Collectors.toList()))
	       for (String expl : assayNamesSorted)
					choices.add(expl);
			} 
		catch (IllegalStateException ie)
			{
			System.out.println("Input is " + input);
			}

		return choices.iterator();
		}
	}

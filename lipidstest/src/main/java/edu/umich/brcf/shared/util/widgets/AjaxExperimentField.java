package edu.umich.brcf.shared.util.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.service.ExperimentService;


public class AjaxExperimentField extends AutoCompleteTextField
	{
	@SpringBean
	ExperimentService expService;

	public AjaxExperimentField(String id)
		{
		super(id, new Model(""));
		}

	public AjaxExperimentField(String id, IModel model)
		{
		super(id, model);
		}

	@Override
	protected Iterator getChoices(String input)
		{
		if (Strings.isEmpty(input))
			return Collections.EMPTY_LIST.iterator();
		
		return getExperimentChoices(input);
		}

	private Iterator getExperimentChoices(String input)
		{
		List<String> choices = new ArrayList();
		try
			{
			for (String expl : expService.getMatchingExperiments(input.toUpperCase()))
					choices.add(expl);

			List<Experiment> list = expService.allExperiments();
			for (Experiment exp : list)
				if (exp.getExpName().toUpperCase().contains(input.toUpperCase())) 
					choices.add(exp.getExpName() + " (" + exp.getExpID() + ")");
			} 
		catch (IllegalStateException ie)
			{
			System.out.println("Input is " + input);
			}

		return choices.iterator();
		}
	}

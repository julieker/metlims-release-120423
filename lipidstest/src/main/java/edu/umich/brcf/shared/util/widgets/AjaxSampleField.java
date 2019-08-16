package edu.umich.brcf.shared.util.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.FormatVerifier;


public class AjaxSampleField extends AutoCompleteTextField
	{
	@SpringBean
	SampleService sampleService;

	public AjaxSampleField(String id)
		{
		super(id, new Model(""));
		}

	@Override
	protected Iterator getChoices(String input)
		{
		if (Strings.isEmpty(input))
			return Collections.EMPTY_LIST.iterator();
		else
			return getSampleChoices(input);
		}

	private Iterator getSampleChoices(String input)
		{
		List<String> choices = new ArrayList();
		try
			{
			if (FormatVerifier.verifyFormat(Sample.idFormat,
					input.toUpperCase()))
				for (String sample : sampleService.getMatchingSamples(input
						.toUpperCase()))
					{
					choices.add(sample);
					}
			} catch (IllegalStateException ie)
			{
			System.out.println("Input is " + input);
			}
		return choices.iterator();
		}
	}

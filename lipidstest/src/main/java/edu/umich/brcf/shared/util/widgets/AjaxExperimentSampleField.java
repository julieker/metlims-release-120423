////////////////////////////////////////////////////
// AjaxExperimentSampleField.java
// Written by Jan Wigginton, Jun 5, 2017
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
import org.apache.wicket.validation.validator.StringValidator;

import edu.umich.brcf.shared.layers.service.SampleService;

public class AjaxExperimentSampleField extends AutoCompleteTextField
	{
	@SpringBean
	 SampleService sampleService;
	
	
	String expId;
	
	
	public AjaxExperimentSampleField(String id) 
		{
		this(id, false);
		}

	public AjaxExperimentSampleField(String id, Boolean useEpiSamples) 
		{
		super(id, new Model(""));
		}
	
	public AjaxExperimentSampleField(String id, String expId, int maxLength) 
		{
		this(id, expId, maxLength, false);
		}

	
	public AjaxExperimentSampleField(String id, String expId, int maxLength, Boolean useEpiSamples) 
		{
		super(id);
		this.expId = expId;
		this.add(StringValidator.maximumLength(maxLength));
		}
	


	@Override
	protected Iterator getChoices(String input) 
		{
		if (Strings.isEmpty(input)) 
			return Collections.EMPTY_LIST.iterator();
		
		return getSampleChoices(input);
		}

	
	private Iterator getSampleChoices(String input)
		{
		List<String> choices = new ArrayList();
		
		List<String> sampleIds = null;
		
		sampleIds = sampleService.sampleIdsForExpId(expId);
		
		try
			{
			for (String sampleId : sampleIds) 
				if (sampleId.toUpperCase().contains(input.toUpperCase()))
					choices.add(sampleId);
			}
		catch(IllegalStateException ie){ System.out.println("Input is "+input); }
		
		return choices.iterator();
		}


	public String getExpId()
		{
		return expId;
		}


	public void setExpId(String expId)
		{
		this.expId = expId;
		}
	}

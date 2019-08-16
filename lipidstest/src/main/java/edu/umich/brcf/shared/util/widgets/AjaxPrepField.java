package edu.umich.brcf.shared.util.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import edu.umich.brcf.shared.layers.domain.PrepPlate;
import edu.umich.brcf.shared.layers.domain.Preparation;
import edu.umich.brcf.shared.layers.service.SamplePrepService;
import edu.umich.brcf.shared.util.FormatVerifier;

public class AjaxPrepField extends AutoCompleteTextField{

	@SpringBean
	SamplePrepService samplePrepService;
	
	public AjaxPrepField(String id) {
		super(id, new Model(""));
	}

	@Override
	protected Iterator getChoices(String input) {
		if (Strings.isEmpty(input)) 
			return Collections.EMPTY_LIST.iterator();
		else 
			return getPrepChoices(input);
	}

	private Iterator getPrepChoices(String input)
		{
		List<String> choices = new ArrayList();
		
		if (input != null)
			{
			try	
				{
				if (FormatVerifier.verifyFormat(Preparation.idFormat,input.toUpperCase()))
				for (String prepId : samplePrepService.getMatchingPreps(input.toUpperCase())) 
					choices.add(prepId);
				
				if(FormatVerifier.verifyFormat(PrepPlate.idFormat,input.toUpperCase()))
					for (String prepId : samplePrepService.getMatchingPlates(input.toUpperCase())) 
						choices.add(prepId);
					
				
				for (Preparation prep : samplePrepService.allSamplePreparations()) 
					{
					final String prepTitle = prep.getTitle();
					if (prepTitle != null && prepTitle.toUpperCase().contains(input.toUpperCase()))
						choices.add(prepTitle + " (" + prep.getPrepID()+")");
					}
				}
			
			catch(IllegalStateException ie){  System.out.println("Input is "+input);	}
			}
		
		return choices.iterator();
		}
	}

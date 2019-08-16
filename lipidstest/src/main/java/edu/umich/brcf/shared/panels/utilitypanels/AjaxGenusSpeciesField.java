/////////////////////////////////////////////
//AjaxGenusSpeciesField.java
//Written by Jan Wigginton, October 2015
/////////////////////////////////////////////

package edu.umich.brcf.shared.panels.utilitypanels;



import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import edu.umich.brcf.metabolomics.layers.domain.GenusSpecies;
import edu.umich.brcf.metabolomics.layers.service.GenusSpeciesService;


public class AjaxGenusSpeciesField extends AutoCompleteTextField
	{
	
	@SpringBean
	GenusSpeciesService genusSpeciesService;
	
	public AjaxGenusSpeciesField(String id) 
		{
		super(id, new Model(""));
		}

	
	public AjaxGenusSpeciesField(String id, IModel model) 
		{
		super(id, model);
		}
	
	
	@Override
	protected Iterator getChoices(String input) 
		{
		if (Strings.isEmpty(input)) 
		return Collections.EMPTY_LIST.iterator();
		else 
		return getGenusChoices(input);
		}
	
	
	private Iterator getGenusChoices(String input)
		{
		List<String> choices = new ArrayList();
		for (GenusSpecies gs : genusSpeciesService.limitedGenusSpecies(input)) 
			{
			final String gsName = gs.getGenusName();
			final String gsId = gs.getGsID().toString();
			if (gsName.toUpperCase().contains(input.toUpperCase()))
			choices.add(gsName + " (" + gsId + ")");
			}
		return choices.iterator();
		}
	
	/*
	private Iterator getExperimentChoices(String input){
	List<String> choices = new ArrayList();
	try{
	if (FormatVerifier.verifyFormat(Experiment.idFormat,input.toUpperCase()))
	for (String expl : genusSpeciesService.getMatchingExperiments(input.toUpperCase())) {
	choices.add(expl);
	}
	
	for (Experiment exp : genusSpeciesService.allExperiments()) {
	final String expName = exp.getExpName();
	if (expName.toUpperCase().contains(input.toUpperCase()))
	choices.add(expName + " (" + exp.getExpID()+")");
	}
	}catch(IllegalStateException ie){
	System.out.println("Input is "+input);
	}
	return choices.iterator();
	}
	*/
	}

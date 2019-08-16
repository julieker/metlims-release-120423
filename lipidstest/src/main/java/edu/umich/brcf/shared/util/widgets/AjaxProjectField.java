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

import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.layers.service.ProjectService;


public class AjaxProjectField extends AutoCompleteTextField 
	{

	@SpringBean
	ProjectService projectService;

	public AjaxProjectField(String id)
		{
		super(id, new Model(""));
		}

	public AjaxProjectField(String id, int maxLength)
		{
		super(id);
		this.add(StringValidator.maximumLength(maxLength));
		
	//	final AutoCompleteSettings opts = new AutoCompleteSettings();
	//	opts.setAdjustInputWidth(false);
	//	opts.setUseSmartPositioning(true);
	//	opts.setUseHideShowCoveredIEFix(true);
	//	AutoCompleteBehavior a = new AutoCompleteBehavior(new StringAutoCompleteRenderer(), opts)
	//		{

		//		@Override
		//		protected Iterator getChoices(String input)
		//			{
		//			return this.getChoices(input);
		//			}
		//	};
		
		
	//	this.add(a);
		}

	@Override
	protected Iterator getChoices(String input)
		{
		if (Strings.isEmpty(input))
			return Collections.EMPTY_LIST.iterator();
		else
			return getProjectChoices(input);
		}

	
	private Iterator getProjectChoices(String input)
		{
		List<String> choices = new ArrayList();
		try
			{
			for (Project project : projectService.allProjects())
				{
				final String projName = project.getProjectName();
				final String projId = " (" + project.getProjectID() + ")";
				if (projName.toUpperCase().contains(input.toUpperCase()))
					choices.add(projName + projId);
				}
			} 
		catch (IllegalStateException ie) { System.out.println("Input is " + input); }
		
		return choices.iterator();
		}
	}

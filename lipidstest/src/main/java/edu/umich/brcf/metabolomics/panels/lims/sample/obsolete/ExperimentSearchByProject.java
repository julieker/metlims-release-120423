package edu.umich.brcf.metabolomics.panels.lims.sample.obsolete;


import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.ProjectService;
import edu.umich.brcf.shared.util.widgets.AjaxProjectField;



public abstract class ExperimentSearchByProject extends WebPage{
	
	String project, input;
	
	@SpringBean 
	private ProjectService projectService;

	public ExperimentSearchByProject()
		{
		}
	
	public ExperimentSearchByProject(Page backPage){
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new SearchByProjectForm("searchByProjectForm"));
	}

	public final class SearchByProjectForm extends Form 
		{
		public SearchByProjectForm(final String id)
			{
			super(id);
			add(new AjaxProjectField("project").add(new AjaxFormSubmitBehavior(this, "change") 
				{
				protected void onSubmit(AjaxRequestTarget target) 
					{
					input = ((AutoCompleteTextField)(this.getComponent())).getInput();
					setProject(input);
					target.add(this.getComponent());
					}
				
				@Override
				protected void onError(AjaxRequestTarget arg0) {}}));
			
			add(new IndicatingAjaxButton("save")
				{
				@Override
				public void onSubmit(AjaxRequestTarget target, Form form) 
					{
					try
						{
						if(projectService.isValidProjectSearch(project))
							ExperimentSearchByProject.this.onSave(project, target);
						else
							{
							doError(target);
							}
						}
					catch (Exception e) 
						{
						doError(target);
						}
					}

				@Override
				protected void onError(AjaxRequestTarget arg0)
					{
					// TODO Auto-generated method stub
					
					}

				});
			}
		}

	
	private void doError(AjaxRequestTarget target)
		{
		String output = (input == null ? "" : input);
		ExperimentSearchByProject.this.error("Can't find project (" + output + "). Please verify that the  search id (or name) is valid.");
		target.add(ExperimentSearchByProject.this.get("feedback"));
		}
	
	
	private void setProject(String input) 
		{
		this.project=input;
		}
	
	
	private String getProject() 
		{
		return project;
		}
	
	protected abstract void onSave(String project, AjaxRequestTarget target);
	}

package edu.umich.brcf.metabolomics.panels.utility;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.panels.lims.preparations.NewNewPreparationsSearchPanel;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.util.widgets.AjaxExperimentField;


public abstract class PrepSearchByExperiment extends WebPage
	{
	String exp, input;
	
	@SpringBean 
	private ExperimentService experimentService;
	
	public PrepSearchByExperiment()
		{
		}

	public PrepSearchByExperiment(Page backPage, NewNewPreparationsSearchPanel parentPanel)
		{
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new SearchByExperimentForm("searchByExperimentForm", parentPanel));
		}

	public final class SearchByExperimentForm extends Form 
		{
		public SearchByExperimentForm(final String id, final NewNewPreparationsSearchPanel parentPanel)
			{
			super(id);
			AjaxExperimentField expField;
			add(new AjaxExperimentField("exp").add(new AjaxFormSubmitBehavior(this, "change") 
				{
				protected void onSubmit(AjaxRequestTarget target) 
					{
					input = ((AutoCompleteTextField)(this.getComponent())).getInput();
					
					setExp(input);
					target.add(this.getComponent());
					}
				
				@Override
				protected void onError(AjaxRequestTarget arg0) { }
				}));
			
			// FocusOnLoad
			add(new IndicatingAjaxButton("save")
				{
				@Override
				protected void onSubmit(AjaxRequestTarget target) // issue 464
					{
					try
						{
						if(experimentService.isValidExperimentSearch(exp))
							PrepSearchByExperiment.this.onSave(exp, target);
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
				});
			}
		}

	private void doError(AjaxRequestTarget target)
		{
		String output = (input == null ? "" : input);
		PrepSearchByExperiment.this.error("Can't find experiment (" + output + "). Please verify that the search id (or name) is valid.");
		target.add(PrepSearchByExperiment.this.get("feedback"));
		}
	
	private void setExp(String input) { this.exp=input; }
	
	private String getExp() { return exp; }
	
	protected abstract void onSave(String exp, AjaxRequestTarget target);
	}

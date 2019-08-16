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
import edu.umich.brcf.shared.layers.service.SamplePrepService;
import edu.umich.brcf.shared.util.widgets.AjaxPrepField;



public abstract class PrepSearchByPrep extends WebPage
	{
	@SpringBean
	SamplePrepService samplePrepService;
	
	String prep, input;

	public PrepSearchByPrep()
		{
		}
	
	public PrepSearchByPrep(Page backPage, final NewNewPreparationsSearchPanel parentPanel)
		{
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new SearchByPrepForm("searchByPrepForm", parentPanel));
		}

	public final class SearchByPrepForm extends Form 
		{
		public SearchByPrepForm(final String id, final NewNewPreparationsSearchPanel parentPanel)
			{
			super(id);
			add(new AjaxPrepField("prep").add(new AjaxFormSubmitBehavior(this, "change") 
				{
				protected void onSubmit(AjaxRequestTarget target) 
					{
					input = ((AutoCompleteTextField)(this.getComponent())).getInput();
					//setPrep(StringParser.parseId(input));
					//parentPanel.setPreparation(StringParser.parseId(input));
					target.add(this.getComponent());
					
					
					PrepSearchByPrep.this.onSave(input, target);
					}
				
				@Override
				protected void onError(AjaxRequestTarget arg0) { }
				}));
			
			add(new IndicatingAjaxButton("save")
				{
				@Override
				public void onSubmit(AjaxRequestTarget target) // issue 464
					{
					try
					{
					if(samplePrepService.isValidPrepSearch(prep))
						PrepSearchByPrep.this.onSave(prep, target);
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
		PrepSearchByPrep.this.error("Can't find prep. ("+ output + "). Please verify that the search id (or name) is valid.");
		target.add(PrepSearchByPrep.this.get("feedback"));
		}
	
	private void setPrep(String input) {
		this.prep=input;
	}
	
	private String getPrep() {
		return prep;
	}
	
	protected abstract void onSave(String prep, AjaxRequestTarget target);
}


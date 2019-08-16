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

import edu.umich.brcf.shared.layers.service.ClientService;
import edu.umich.brcf.shared.util.widgets.AjaxClientField;




public abstract class PrepSearchByDate extends WebPage
	{
	@SpringBean
	ClientService clientService;
	
	String date, input;
	
	public PrepSearchByDate()
		{
		}

	public PrepSearchByDate(Page backPage)
		{
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new SearchByDateForm("searchByDateForm"));
		}

	
	public final class SearchByDateForm extends Form 
		{
		public SearchByDateForm(final String id)
			{
			super(id);
			add(new AjaxClientField("date").add(new AjaxFormSubmitBehavior(this, "change") 
				{
				protected void onSubmit(AjaxRequestTarget target) 
					{
					input = ((AutoCompleteTextField)(this.getComponent())).getInput();
					setDate(input);
					target.add(this.getComponent());
					}
				
				@Override
				protected void onError(AjaxRequestTarget arg0) {
				}}));
			
			add(new IndicatingAjaxButton("save")
				{
				@Override
				public void onSubmit(AjaxRequestTarget target) // issue 464
					{
					try
						{
						PrepSearchByDate.this.onSave(date, target);
						}
					catch (Exception e)
						{
						doError(target);
						}
					}
				});
			}
		}
	
	// Search for compounds (
	private void doError(AjaxRequestTarget target)
		{
		String output = (input == null ? "" : input);
		PrepSearchByDate.this.error("Can't find contact ( " + output + "). Please verify that the search id (or name) is valid.");
		target.add(PrepSearchByDate.this.get("feedback"));
		}
	
	private void setDate(String input) 
		{
		this.date = input;
		}
	
	private String getDate() 
		{
		return date;
		}
	
	protected abstract void onSave(String contact, AjaxRequestTarget target);
	}

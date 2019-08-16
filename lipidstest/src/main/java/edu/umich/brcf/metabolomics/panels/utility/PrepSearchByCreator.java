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
import edu.umich.brcf.shared.util.widgets.AjaxAdminField;





public abstract class PrepSearchByCreator extends WebPage
	{
	@SpringBean
	ClientService clientService;
	
	String creator, input;
	
	public PrepSearchByCreator()
		{
		}

	public PrepSearchByCreator(Page backPage)
		{
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new SearchByCreatorForm("searchByCreatorForm"));
		}

	
	public final class SearchByCreatorForm extends Form 
		{
		public SearchByCreatorForm(final String id)
			{
			super(id);
			add(new AjaxAdminField("creator", true).add(new AjaxFormSubmitBehavior(this, "change") 
				{
				protected void onSubmit(AjaxRequestTarget target) 
					{
					input = ((AutoCompleteTextField)(this.getComponent())).getInput();
					setCreator(input);
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
						PrepSearchByCreator.this.onSave(creator, target);
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
		PrepSearchByCreator.this.error("Can't find contact ( " + output + "). Please verify that the search id (or name) is valid.");
		target.add(PrepSearchByCreator.this.get("feedback"));
		}
	
	private void setCreator(String input) 
		{
		this.creator=input;
		}
	
	private String getCreator() 
		{
		return creator;
		}
	
	protected abstract void onSave(String contact, AjaxRequestTarget target);
	}

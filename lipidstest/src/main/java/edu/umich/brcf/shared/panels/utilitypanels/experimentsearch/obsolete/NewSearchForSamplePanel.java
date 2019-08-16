////////////////////////////////////////////////////
// SearchForClientPanel.java
// Written by Jan Wigginton, Aug 6, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.panels.experimentsearch.obsolete;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.widgets.AjaxSampleField;



public abstract class NewSearchForSamplePanel extends Panel
	{
	@SpringBean
	SampleService sampleService;
	
	private String sample, input, searchType = "Id";
	private Boolean showButton = false;
	
	
	public NewSearchForSamplePanel(String id)
		{
		super(id);
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new NewSearchForSampleForm("searchBySampleForm"));
	}

	public final class NewSearchForSampleForm extends Form 
		{
		public NewSearchForSampleForm(final String id)
			{
			super(id);
			add(new AjaxSampleField("sample").add(new AjaxFormSubmitBehavior(this, "change") 
				{
				protected void onSubmit(AjaxRequestTarget target) 
					{
					input = ((AutoCompleteTextField)(this.getComponent())).getInput();
					setSample(input);
					target.add(this.getComponent());
					}
				
				@Override
				protected void onError(AjaxRequestTarget arg0) {
				}}));
			
			add(new IndicatingAjaxButton("select")
				{
				@Override
				public boolean isVisible() { return showButton; }
				
				@Override
				protected void onSubmit(AjaxRequestTarget target) 
					{
					try
						{
						if(sampleService.isValidSampleSearch(sample))
							NewSearchForSamplePanel.this.onSelect(sample, target);
						else
							doError(target);
						}
					catch (Exception e)
						{
						doError(target);
						}}
				});
			}
		}
	
	
	private void doError(AjaxRequestTarget target)
		{
		String output = (input == null ? "" : input);
		NewSearchForSamplePanel.this.error("Can't find sample (" + output + "). Please verify that the search id (or name) is valid.");
		target.add(NewSearchForSamplePanel.this.get("feedback"));
		}
	
	private void setSample(String input) 
		{
		this.sample=input;
		}
	
	private String getSample() 
		{
		return sample;
		}
	
	public Boolean getShowButton()
		{
		return showButton;
		}

	public void setShowButton(Boolean showButton)
		{
		this.showButton = showButton;
		}

	public String getSearchType()
		{
		return searchType;
		}

	public void setSearchType(String searchType)
		{
		this.searchType = searchType;
		}

	protected abstract void onSelect(String sample, AjaxRequestTarget target);
}

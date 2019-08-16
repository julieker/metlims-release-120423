package edu.umich.brcf.metabolomics.panels.utility;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.util.widgets.AssayDropDown;



public abstract class SearchByAssay extends WebPage
	{
	String assay, input;
	
	@SpringBean 
	private AssayService assayService;
	
	public SearchByAssay()
		{
		}

	public SearchByAssay(Page backPage)
		{
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new SearchByAssayForm("searchByAssayForm"));
		}

	private final class SearchByAssayForm extends Form 
		{
		public SearchByAssayForm(final String id)
			{
			super(id);
			
			AssayDropDown assayDrop = new AssayDropDown("assay", this, "assay", "")
				{
				@Override
				protected void doUpdateBehavior(AjaxRequestTarget target) 
					{
				
					}
				
				String getAssay()
					{
					return assay;
					}
					
				private void setAssay(String s)
					{
					assay = s;
					}
				};
			add(assayDrop);
			
			add(new IndicatingAjaxButton("save")
				{
				@Override
				protected void onSubmit(AjaxRequestTarget target) // issue 464
					{
					final String assayFinal = getAssay();
					try
						{
						SearchByAssay.this.onSave(assayFinal, target);
						}
					catch (Exception e)
						{
						doError(target);
						}
					}
				});
			}
		
		public void setAssay(String input) { assay=input; }
		public String getAssay() { return assay;  } 

		}


	protected void doError(AjaxRequestTarget target)
		{
		String output = (input == null ? "" : input);
		SearchByAssay.this.error("Can't find experiment (" + output + "). Please verify that the search id (or name) is valid.");
		target.add(SearchByAssay.this.get("feedback"));
		}
	
	protected abstract void onSave(String assay, AjaxRequestTarget target);
	}

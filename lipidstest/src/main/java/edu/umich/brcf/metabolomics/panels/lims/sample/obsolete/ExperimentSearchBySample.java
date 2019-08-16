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

import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.widgets.AjaxSampleField;


public abstract class ExperimentSearchBySample extends WebPage
	{
	@SpringBean
	SampleService sampleService;

	String sample, input;

	public ExperimentSearchBySample()
		{
		}

	public ExperimentSearchBySample(Page backPage)
		{
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new SearchBySampleForm("searchBySampleForm"));
		}

	public final class SearchBySampleForm extends Form
		{
		public SearchBySampleForm(final String id)
			{
			super(id);
			add(new AjaxSampleField("sample").add(new AjaxFormSubmitBehavior(
					this, "change")
				{
					protected void onSubmit(AjaxRequestTarget target)
						{
						input = ((AutoCompleteTextField) (this.getComponent()))
								.getInput();
						setSample(input);
						target.add(this.getComponent());
						}

					@Override
					protected void onError(AjaxRequestTarget arg0)
						{
						}
				}));

			add(new IndicatingAjaxButton("save")
				{
					@Override
					protected void onSubmit(AjaxRequestTarget target,
							Form<?> arg1)
						{

						try
							{
							if (sampleService.isValidSampleSearch(sample))
								ExperimentSearchBySample.this.onSave(sample,
										target);
							else
								doError(target);
							} catch (Exception e)
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
		ExperimentSearchBySample.this.error("Can't find sample (" + output
				+ "). Please verify that the search id (or name) is valid.");
		target.add(ExperimentSearchBySample.this.get("feedback"));
		}

	private void setSample(String input)
		{
		this.sample = input;
		}

	private String getSample()
		{
		return sample;
		}

	protected abstract void onSave(String sample, AjaxRequestTarget target);
	}

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

import edu.umich.brcf.shared.layers.service.OrganizationService;
import edu.umich.brcf.shared.util.widgets.AjaxOrganizationField;

public abstract class ExperimentSearchByOrganization extends WebPage
	{

	String organization, input;

	@SpringBean
	OrganizationService organizationService;

	public ExperimentSearchByOrganization()
		{
		}

	public ExperimentSearchByOrganization(Page backPage)
		{
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new SearchByOrganizationForm("searchByOrganizationForm"));
		}

	public final class SearchByOrganizationForm extends Form
		{
		public SearchByOrganizationForm(final String id)
			{
			super(id);

			add(new AjaxOrganizationField("organization")
					.add(new AjaxFormSubmitBehavior(this, "change")
						{
							protected void onSubmit(AjaxRequestTarget target)
								{
								input = ((AutoCompleteTextField) (this
										.getComponent())).getInput();
								setOrganization(input);
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
					protected void onSubmit(AjaxRequestTarget target, Form fom)
						{
						try
							{
							if (organizationService
									.isValidOrganizationSearch(organization))
								ExperimentSearchByOrganization.this.onSave(
										organization, target);
							else
								{
								doError(target);
								}
							} catch (Exception e)
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
		ExperimentSearchByOrganization.this.error("Can't find organization ("
				+ output
				+ "). Please verify that the search id (or name) is valid.");
		target.add(ExperimentSearchByOrganization.this.get("feedback"));
		}

	private void setOrganization(String input)
		{
		this.organization = input;
		}

	private String getOrganization()
		{
		return organization;
		}

	protected abstract void onSave(String organization, AjaxRequestTarget target);
	}

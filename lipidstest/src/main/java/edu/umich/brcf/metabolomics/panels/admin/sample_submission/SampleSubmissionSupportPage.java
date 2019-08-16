package edu.umich.brcf.metabolomics.panels.admin.sample_submission;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;

import edu.umich.brcf.metabolomics.panels.admin.organization.EditOrganization;
import edu.umich.brcf.metabolomics.panels.lims.client.EditClient;
import edu.umich.brcf.metabolomics.panels.lims.newexperiment.EditExperiment;
import edu.umich.brcf.metabolomics.panels.lims.project.EditProject2;
import edu.umich.brcf.shared.layers.domain.Client;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Organization;
import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.widgets.AjaxExperimentField;
import edu.umich.brcf.shared.util.widgets.AjaxNewClientField;
import edu.umich.brcf.shared.util.widgets.AjaxOrganizationField;
import edu.umich.brcf.shared.util.widgets.AjaxProjectField;



public class SampleSubmissionSupportPage extends WebPage
	{
	public SampleSubmissionSupportPage(Page backPage)
		{
		final ModalWindow modal1 = ModalCreator.createModalWindow("modal1",
				550, 400);
		add(modal1);

		add(new AjaxOrganizationField("org"));
		add(new AjaxNewClientField("clnt", true));
		add(new AjaxProjectField("proj"));
		add(new AjaxExperimentField("exp"));

		add(buildLinkToModal("experiment", modal1));
		add(buildLinkToModal("project", modal1));
		add(buildLinkToModal("client", modal1));
		add(buildLinkToModal("organization", modal1));
		add(buildLinkToModal("stSupport", modal1));
		// add(new AjaxCancelLink("cancelButton", modal1));
		}
	

	private AjaxLink buildLinkToModal(final String linkID,
			final ModalWindow modal1)
		{
		return new AjaxLink(linkID)
			{
				@Override
				public void onClick(AjaxRequestTarget target)
					{
					setModalDimensions(linkID, modal1);

					modal1.setPageCreator(new ModalWindow.PageCreator()
						{
						private static final long serialVersionUID = -3923236397960944554L;

						public Page createPage()
							{
							return setPage(linkID, modal1);
							}
						});

					modal1.show(target);
					}
				@Override // issue 464
				public MarkupContainer setDefaultModel(IModel model) 
				    {
					// TODO Auto-generated method stub
					return this;
				    }
			};
		}

	
	private Page setPage(String linkID, final ModalWindow modal1)
		{
		switch (linkID.toUpperCase().charAt(0))
			{
			case 'E':
			return (new EditExperiment(getPage(), modal1)
				{
				@Override
				protected void onSave(Experiment exp, AjaxRequestTarget target1)
					{
					}
				});

			case 'P':
				return new EditProject2(getPage(), modal1)
					{
					@Override
					protected void onSave(Project project, AjaxRequestTarget target1)
						{
						}
					};

			case 'C':
				return (new EditClient(getPage(), null, modal1)
					{
					@Override
					protected void onSave(Client client, AjaxRequestTarget target1)
						{
						if (modal1 != null)
							modal1.close(target1);
						}
					});

			case 'S':
				return new SampleSubmissionLookupPage(getPage());

			default:
				return new EditOrganization(getPage(), true)
					{
					@Override
					protected void onSave(Organization org, AjaxRequestTarget target) {} 
					};
				}
			}

	
	private void setModalDimensions(String linkID, ModalWindow modal1)
		{
		switch (linkID.toUpperCase().charAt(0))
			{
			case 'O':
				modal1.setInitialWidth(700);
				modal1.setInitialHeight(200);
				break;
			case 'C':
				modal1.setInitialWidth(550);
				modal1.setInitialHeight(330);
				break;
			case 'P':
				modal1.setInitialWidth(625);
				modal1.setInitialHeight(500);
				break;
			case 'S':
				modal1.setInitialWidth(1000);
				modal1.setInitialHeight(380);
				break;
			default:
				modal1.setInitialWidth(750);
				modal1.setInitialHeight(800);
			}
		}
	}

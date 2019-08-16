//////////////////////////////////////////
//AddEntityPanel.java
//Written by Jan Wigginton, March 2016
//////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.admin.sample_submission.obsolete;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.panel.Panel;

import edu.umich.brcf.metabolomics.panels.admin.organization.EditOrganization;
import edu.umich.brcf.metabolomics.panels.lims.client.EditClient;
import edu.umich.brcf.metabolomics.panels.lims.newexperiment.EditExperiment;
import edu.umich.brcf.metabolomics.panels.lims.project.EditProject2;
import edu.umich.brcf.shared.layers.domain.Client;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Organization;
import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.widgets.AjaxClientField;
import edu.umich.brcf.shared.util.widgets.AjaxExperimentField;
import edu.umich.brcf.shared.util.widgets.AjaxOrganizationField;
import edu.umich.brcf.shared.util.widgets.AjaxProjectField;


public class AddEntityPanel extends Panel
	{
	public AddEntityPanel(String id)
		{
		super(id);

		final ModalWindow modal1 = ModalCreator.createModalWindow("modal1",
				250, 250);
		add(modal1);
		add(buildLinkToModal("registerOrganization", modal1));
		add(buildLinkToModal("registerClient", modal1));
		add(buildLinkToModal("registerProject", modal1));
		add(buildLinkToModal("registerExperiment", modal1));

		add(new AjaxOrganizationField("orgCheck"));
		add(new AjaxClientField("clientCheck"));
		add(new AjaxProjectField("projCheck"));
		add(new AjaxExperimentField("expCheck"));
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
							// private static final long serialVersionUID =
							// -3923236397960944554L;
							public Page createPage()
								{
								return setPage(linkID, modal1);
								}
						});

					modal1.show(target);
					}
			};
		}
	

	private Page setPage(String linkID, final ModalWindow modal1)
		{
		EditExperiment e;
		switch (linkID)
			{
			case "registerExperiment":
				return (new EditExperiment(getPage(), modal1)
					{
					@Override
					protected void onSave(Experiment exp, AjaxRequestTarget target1) { } 
					});

			case "registerProject":
				return new EditProject2(getPage(), modal1)
					{
					@Override
					protected void onSave(Project project, AjaxRequestTarget target1) { }
					};

			case "registerClient":
				return (new EditClient(getPage(), null, modal1)
					{
					@Override
					protected void onSave(Client client, AjaxRequestTarget target1)
						{
						if (modal1 != null)
							modal1.close(target1);
						}
					});

			default:
				return new EditOrganization(getPage(), true)
					{
					@Override
					protected void onSave(Organization org, AjaxRequestTarget target) { } 
					};
			}
		}
	

	private void setModalDimensions(String linkID, ModalWindow modal1)
		{
		switch (linkID)
			{
			case "registerOrganization":
			modal1.setInitialWidth(700);
			modal1.setInitialHeight(200);
				break;
			case "registerClient":
			modal1.setInitialWidth(550);
			modal1.setInitialHeight(330);
				break;
			case "registerProject":
			modal1.setInitialWidth(625);
			modal1.setInitialHeight(400);
				break;
			case "registerExperiment":
			modal1.setInitialWidth(650);
			modal1.setInitialHeight(650);
				break;
			default:
			modal1.setInitialWidth(750);
			modal1.setInitialHeight(400);
			}
		}
	}

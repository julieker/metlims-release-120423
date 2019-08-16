////////////////////////////////////////////
//DatabaseToolsPanel.java
//Written by Jan Wigginton, November 2015
////////////////////////////////////////////

package edu.umich.brcf.shared.panels.obsolete.database;

import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.panels.admin.database_utility.RegisterAssayPage;
import edu.umich.brcf.metabolomics.panels.admin.database_utility.RegisterFactorPage;
//import edu.umich.brcf.metabolomics.panels.admin.sample_submission.obsolete.AddEntityPanel;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.panels.utilitypanels.CoreExperimentDownloadPanel;
import edu.umich.brcf.shared.panels.utilitypanels.ExperimentSelectorPanel;
import edu.umich.brcf.shared.panels.utilitypanels.ExperimentSelectorWithConfirmPanel;
import edu.umich.brcf.shared.panels.utilitypanels.HubActionSelectorPanel;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.panels.utilitypanels.ScanBarcodesPage;
import edu.umich.brcf.shared.util.widgets.METWorksPctSizableModal;


public class DatabaseToolsPanel extends Panel
	{
	@SpringBean
	private ExperimentService experimentService;

	@SpringBean
	SampleService sampleService;

	
	public DatabaseToolsPanel(String id)
		{
		super(id);

		// add(new FeedbackPanel("feedback"));
		DatabaseToolsForm lde = new DatabaseToolsForm("launchDatabaseToolsForm");
		lde.setMultiPart(true);

		add(lde);
		}

	
	public final class DatabaseToolsForm extends Form
		{
		DatabaseToolsForm(String id)
			{
			super(id);
			final METWorksPctSizableModal modal1 = new METWorksPctSizableModal("modal1", 0.56, .55);
			final ModalWindow modal = ModalCreator.createModalWindow("modal", 1000, 200);
			add(modal);
			add(modal1);

	//		add(new AddEntityPanel("addEntityPanel"));
			add(buildInvalidateSamplesPanel("invalidateSamplesPanel"));
			add(buildAddFactorPanel("addFactorPanel", modal1));
			add(buildAddAssayPanel("addAssayPanel", modal1));
			// / add(buildAlternateUploadSamplePanel("addSamplesPanel", true));
			}

		
		public ExperimentSelectorPanel buildAddAssayPanel(String id, final METWorksPctSizableModal modal1)
			{
			ExperimentSelectorPanel panel = new ExperimentSelectorPanel(id)
				{
				@Override
				public void doSubmit(String selectedExperiment, AjaxRequestTarget target)
					{
					setResponsePage(new RegisterAssayPage("addAssay", selectedExperiment, modal1));
					}

				@Override
				public boolean isEnabled()
					{
					Long level = ((MedWorksSession) Session.get()).getLevel().getId();
					return (level == 98 || level == 99);
					}
				};

			panel.setButtonLabel("Choose Assay...");
			return panel;
			}

		
		public HubActionSelectorPanel buildCheckInSamplesPanel(String id, final ModalWindow modal)
			{
			HubActionSelectorPanel panel = new HubActionSelectorPanel(id)
				{
				@Override
				public void doSubmit(String selectedHub, String selectedAction, AjaxRequestTarget target)
					{
					showBarcodeModal(modal, target, selectedAction);
					}
				};
			return panel;
			}

		
		public void showBarcodeModal(final ModalWindow modal, AjaxRequestTarget target, final String selectedAction)
			{
			modal.setInitialWidth(1000);
			modal.setInitialHeight(550);

			modal.setPageCreator(new ModalWindow.PageCreator()
				{
				public Page createPage()
					{
					return (new ScanBarcodesPage("scanBarcodesPage", selectedAction, " Samples", modal)
						{
						@Override
						public void doSubmit(AjaxRequestTarget target, List<String> scannedIds)
							{
							String msg = "alert('Submit button was clicked');";
							target.appendJavaScript(msg);
							}
						});
					}
				});

			modal.show(target);
			}

		
		public ExperimentSelectorWithConfirmPanel buildInvalidateSamplesPanel(String id)
			{
			ExperimentSelectorWithConfirmPanel panel = new ExperimentSelectorWithConfirmPanel(id)
				{
				@Override
				public void doSubmit(String selectedExperiment)
					{
					System.out.println("Here we would invalidate the samples");
					// experimentService.invalidateSamples(selectedExperiment);
					}
				};

			panel.setButtonLabel("Invalidate Samples");
			return panel;
			}

		
		public CoreExperimentDownloadPanel buildDownloadSheetPanel(String id)
			{
			CoreExperimentDownloadPanel panel = new CoreExperimentDownloadPanel(id, true)
				{
				@Override
				public void doSubmit(String selectedExperiment) {  }
				};

			panel.setButtonLabel("Download");
			return panel;
			}

		
		public ExperimentSelectorPanel buildAddFactorPanel(String id, final METWorksPctSizableModal modal2)
			{
			ExperimentSelectorPanel panel = new ExperimentSelectorPanel(id)
				{
				@Override
				public void doSubmit(final String selectedExperiment, AjaxRequestTarget target)
					{
					modal2.setPageCreator(new ModalWindow.PageCreator()
						{
						public Page createPage()
							{
							return new RegisterFactorPage("addFactor", ((WebPage) getPage()), selectedExperiment, modal2);
							}
						});

					modal2.show(target);
					}

				@Override
				public boolean isEnabled()
					{
					return true;
					// Long level = ((MedWorksSession)
					// Session.get()).getLevel().getId();
					// return (level == 98 || level == 99);
					}
				};

			panel.setButtonLabel("Factor Values...");
			return panel;
			}
		}
	}

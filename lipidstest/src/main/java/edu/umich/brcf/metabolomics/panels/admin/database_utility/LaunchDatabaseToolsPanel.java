///////////////////////////////////////////////////
// LaunchSampleToolsPanel.java
// Written by Jan Wigginton, Jul 2015
////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.admin.database_utility;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.panels.admin.sample_submission.Mrc2SubmissionSheetViewerPage;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.FactorService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.panels.utilitypanels.ExperimentSampleSearchPanel;
import edu.umich.brcf.shared.panels.utilitypanels.ExperimentSelectorPanel;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.datacollectors.ExperimentalDesign;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.widgets.METWorksPctSizableModal;


public class LaunchDatabaseToolsPanel extends Panel
	{
	@SpringBean
	ExperimentService experimentService;
	
	@SpringBean
	SampleService sampleService;
	
	@SpringBean
	UserService userService;

	 @SpringBean
	 FactorService factorService;
	 
	
	public LaunchDatabaseToolsPanel(String id) 
		{
		super(id);
		
		LaunchSampleToolsForm lde = new LaunchSampleToolsForm("launchSampleToolsForm");
		lde.setMultiPart(true);
		add(lde);
		}
	
	public final class LaunchSampleToolsForm extends Form 
		{	
		LaunchSampleToolsForm(String id)
			{
			super(id);

			final ModalWindow modal1= ModalCreator.createModalWindow("modal1", 250, 250);
			add(modal1);
			
			final METWorksPctSizableModal modal2 = new METWorksPctSizableModal("modal2", 0.75, 0.99);
			add(modal2);

			add(buildAddAssayPanel("addAssayPanel", modal2));
			add(buildAddFactorPanel("addFactorPanel", modal2));
			add(buildBatchEditSheetPanel("editSample2", modal2));
			add(buildEditSamplePanel("editSamplePanel", modal2));
			add(buildDeleteSamplePanel("deleteSamplePanel"));
			}		

	
		public ExperimentSelectorPanel buildViewSubmissionSheetPanel(String id)
			{
			ExperimentSelectorPanel panel =  new ExperimentSelectorPanel(id)
				{
				@Override
				public void doSubmit(String selectedExperiment, AjaxRequestTarget target)  { }
				};
			panel.setButtonLabel("View Sheet");	
			return panel;
			}
		
		
		public ExperimentSelectorPanel buildAddAssayPanel(String id, final METWorksPctSizableModal modal2)
			{
			ExperimentSelectorPanel panel =  new ExperimentSelectorPanel(id)
				{
				@Override
				public void doSubmit(final String selectedExperiment, AjaxRequestTarget target) 
					{
					modal2.setPageCreator(new ModalWindow.PageCreator()
				        {
				        public Page createPage()
				        	{
				        	return new RegisterAssayPage("addAssay", selectedExperiment, modal2);
				        	}
				        });
			
					modal2.show(target);
					}
				
				@Override
				public boolean isEnabled()
					{
					String userId = (((MedWorksSession) getSession()).getCurrentUserId());
					return userService.isAdmin(userId);
					}
				};
				
			panel.setButtonLabel("Choose Assay...");	
			return panel;             
			}
		
		
		public ExperimentSelectorPanel builViewSampleFormPanel(String id, final METWorksPctSizableModal modal2)
			{
			ExperimentSelectorPanel panel =  new ExperimentSelectorPanel(id)
				{
				@Override
				public void doSubmit(final String selectedExperiment, AjaxRequestTarget target) 
					{
					modal2.setPageCreator(new ModalWindow.PageCreator()
				        {
				        public Page createPage()
				        	{
				        	return new Mrc2SubmissionSheetViewerPage((WebPage) getPage(), selectedExperiment,6);
				        	}
				        });
			
					modal2.show(target);
					}
				
				@Override
				public boolean isEnabled()
					{
					String userId = (((MedWorksSession) getSession()).getCurrentUserId());
					return userService.isAdmin(userId);
					}
				};
				
			panel.setButtonLabel("Choose Assay...");	
			return panel;             
			}
		
		
		
		public ExperimentSelectorPanel buildAddFactorPanel(String id, final METWorksPctSizableModal modal2)
			{
			modal2.setPageHeightPct(0.98);
			ExperimentSelectorPanel panel = new ExperimentSelectorPanel(id)
				{
				@Override
				public void doSubmit(final String selectedExperiment, AjaxRequestTarget target) 
					{ 
					int max = ExperimentalDesign.SUBMISSION_SHEET_NFACTORS;
					if (factorService.countFactorsForExperiment(selectedExperiment) >= max)
						target.appendJavaScript(StringUtils.makeAlertMessage("Experiment " + selectedExperiment + " already has the maximum number of factors (" + max + ") -- cannot add another"));
					else
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
					}
				
				@Override
				public boolean isEnabled()
					{
					String userId = (((MedWorksSession) getSession()).getCurrentUserId());
					return userService.isAdmin(userId);
					}
				};
				
			panel.setButtonLabel("Factor Values...");
			return panel;
			}
		
		
		public ExperimentSelectorPanel buildBatchEditSheetPanel(final String id, final ModalWindow modal)
			{
			ExperimentSelectorPanel panel = new ExperimentSelectorPanel(id)
				{
				@Override
				public void doSubmit(String selectedExperiment, AjaxRequestTarget target)
					{
					showSampleDetailsPage(id, selectedExperiment, modal, target);
					}
				};
				
			panel.setButtonLabel("View Details...");
			return panel;
			}
		
		
		public void showSampleDetailsPage(String action, final String expId, final ModalWindow modal,  AjaxRequestTarget target)
			{
			modal.setInitialWidth(1300);
			modal.setInitialHeight(850);
			
			// JAK disable batch sample edit
			target.appendJavaScript(StringUtils.makeAlertMessage("Batch Sample Edit not available in this version of METLIMS "));
			/*modal.setPageCreator(new ModalWindow.PageCreator()
				{
				
				
				public Page createPage()
					{
					Mrc2BatchSampleEdit page = new Mrc2BatchSampleEdit("qcPage", expId, modal)
						{
						@Override
						protected void onSave(String selectedExperiment, AjaxRequestTarget target) { }
						};
					
						return null;	
					//return page;
					}
				});
			
			modal.show(target); */
			}
		
		
		
		public ExperimentSampleSearchPanel buildEditSamplePanel(String id, final METWorksPctSizableModal modal2)
			{
			modal2.setPageHeightPct(.98);
			modal2.setPageWidthPct(0.9);
		
			ExperimentSampleSearchPanel panel = new ExperimentSampleSearchPanel(id)
				{
				@Override
				public void doSubmit(final String selectedSample, String selectedExperiment, AjaxRequestTarget target)
					{
					Experiment exp = experimentService.loadExperimentWithInfoForDrcc(selectedExperiment);
					final Sample s = sampleService.loadById(selectedSample);
					
					if (s != null)
						{
						modal2.setPageCreator(new ModalWindow.PageCreator()
					        {
					        public Page createPage()
					        	{
					        	return new EditSampleNew(getPage(), new Model<Sample>(s), modal2);
					        	}
					        });
					
						modal2.show(target);
						}
					else 
						target.appendJavaScript(StringUtils.makeAlertMessage("Cannot retrieve sample with id " + selectedSample));
					}
				
				@Override
				public boolean isEnabled()
					{
					String userId = (((MedWorksSession) getSession()).getCurrentUserId());
					return userService.isAdmin(userId);
					}
				};
		
			panel.setButtonLabel("Edit Sample...");
			return panel;
			}
		
		
		public ExperimentSelectorPanel buildDeleteSamplePanel(String id)
			{
			return new ExperimentSelectorPanel(id)
				{
				@Override
				public void doSubmit(String selectedExperiment, AjaxRequestTarget target) { }
				};
			}
		}  
	}	
		
		
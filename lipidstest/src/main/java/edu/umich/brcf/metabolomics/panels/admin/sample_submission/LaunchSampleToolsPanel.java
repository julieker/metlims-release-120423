///////////////////////////////////////////////////
// LaunchSampleToolsPanel.java
// Written by Jan Wigginton, Jul 2015
////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.admin.sample_submission;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.service.Mrc2SubmissionDataService;
import edu.umich.brcf.metabolomics.panels.admin.messaging.METWorksMailMessage;
import edu.umich.brcf.metabolomics.panels.admin.messaging.METWorksMessageMailer;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.dto.RollbackItemDTO;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.RollbackItemService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.layers.service.SystemConfigService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.panels.utilitypanels.AbstractGenericSampleFormUploadPage;
import edu.umich.brcf.shared.panels.utilitypanels.BarcodeSelectorPage;
import edu.umich.brcf.shared.panels.utilitypanels.ExperimentOrDateSearchPanel;
import edu.umich.brcf.shared.panels.utilitypanels.ExperimentPlusCountPanel;
import edu.umich.brcf.shared.panels.utilitypanels.ExperimentSelectorPanel;
import edu.umich.brcf.shared.panels.utilitypanels.ExperimentSelectorWithPopupConfirm;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.panels.utilitypanels.OptimizedBarcodeSelectorPage;
import edu.umich.brcf.shared.util.METWorksException;
import edu.umich.brcf.shared.util.ModalSizes;
import edu.umich.brcf.shared.util.SampleSheetIOException;
import edu.umich.brcf.shared.util.datacollectors.Mrc2TransitionalSubmissionSheetData;
import edu.umich.brcf.shared.util.interfaces.ISavableSampleData;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.sheetreaders.Mrc2TransitionalSubmissionSheetReader;
import edu.umich.brcf.shared.util.structures.Pair;
import edu.umich.brcf.shared.util.utilpackages.ListUtils;
import edu.umich.brcf.shared.util.widgets.METWorksPctSizableModal;


public class LaunchSampleToolsPanel extends Panel
	{
	@SpringBean
	ExperimentService experimentService;
	
	@SpringBean
	UserService userService;

	@SpringBean 
	SystemConfigService systemConfigService;
	
	@SpringBean
	METWorksMessageMailer mailer;
	 
	@SpringBean 
	RollbackItemService rollbackItemService;
	 
	@SpringBean
	Mrc2SubmissionDataService mrc2SubmissionDataService;
	 
	@SpringBean
	SampleService sampleService;
	
	
	public LaunchSampleToolsPanel(String id) 
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

			add(new NewSampleEntryPanel("submitSamplesPanel"));
			add(buildInvalidateSamplesPanel("invalidateSamplesPanel"));
			add(buildAddUploadSamplesPanel(modal2, "addSamplePanel", true));
			add(buildReportsDateForInventorySelector("sampleInventoryPanel", true, modal2));
			add(buildPrintBarcodesByExperimentPanel("printBarcodesByExperiment", modal1));
			}		

	
		public ExperimentSelectorWithPopupConfirm buildInvalidateSamplesPanel(String id)
			{
			ExperimentSelectorWithPopupConfirm panel = new ExperimentSelectorWithPopupConfirm(id)
				{
				@Override
				public void doSubmit(String selectedExperiment) 
					{
					try
						{
						List<RollbackItemDTO> newItemsToLog = rollbackItemService.gatherInfoForLogging(selectedExperiment);	
						rollbackItemService.createOrUpdateByExpId(selectedExperiment, newItemsToLog);
						}
					catch (Exception e) { System.out.println("Failure during rollback logging"); }
					
					if (experimentService.invalidateSamples(selectedExperiment))
				    	{
					 	List<String> email_contacts = (List<String>) (systemConfigService.getSystemConfigMap()).get("sample_registration_notification_contact");
						String msg = "Sample submission for " + selectedExperiment + " has been rolled back";
						String mailAddress =  "metabolomics@med.umich.edu";
						String mailTitle = "METLIMS Sample Registration Rollback Message";
						
						if (email_contacts != null)
							for (String email_contact : email_contacts) 
								{
								METWorksMailMessage m = new METWorksMailMessage(mailAddress, email_contact, mailTitle,  msg);
								mailer.sendMessage(m);
								}
				    	} 
					}
				};
				 
			panel.setAccountAdminOnly(true);
			panel.setButtonLabel("Invalidate Samples");
			return panel;
			} 
		
		
		private ExperimentOrDateSearchPanel buildReportsDateForInventorySelector(final String id, final boolean isEnabled, ModalWindow modal1)
			{
			ExperimentOrDateSearchPanel panel = new ExperimentOrDateSearchPanel(id)
				{
				@Override
				public void doSubmit(String searchType, String selectedExperiment, Calendar fromDate, Calendar toDate)
					{
					if (StringUtils.isEmptyOrNull(searchType))
						return;

					Boolean searchByRunDate = (searchType == null ? false : searchType.equals("Run Date"));
					try 
					    { 
						return;
						// issue 464 setResponsePage(new ExperimentCompletionReport("searchResults", (WebPage) this.getPage(),fromDate, toDate));
						} 
					catch (Exception e) {  }
					}
				};

			panel.limitToSearchType("Completion Date");
			return panel;
			}
		
			
		private ExperimentPlusCountPanel buildAddUploadSamplesPanel(final ModalWindow modal, final String id, final Boolean isEnabled)
			{
			ExperimentPlusCountPanel panel = new ExperimentPlusCountPanel(id)
				{
				@Override
				public void doSubmit(String selectedExperimentId, Integer selectedCount, AjaxRequestTarget target)
					{
					showUploadModal(modal, selectedExperimentId, selectedCount, target);
					}
				};
				
			panel.setAccountAdminOnly(true);
			panel.setCountLabelString("N Samples To Add");
			panel.setButtonLabel("Select File");
			return panel;
			}
				
	
		 private void showUploadModal(final ModalWindow modal1, final String selectedExperimentId, final Integer selectedCount, AjaxRequestTarget target)
			
		 {
			modal1.setInitialWidth(ModalSizes.SAMPLE_FORM_UPLOAD_PAGE_WIDTH);
			modal1.setInitialHeight(ModalSizes.SAMPLE_FORM_UPLOAD_PAGE_HEIGHT);
			// JAK disable add sample 
			target.appendJavaScript(StringUtils.makeAlertMessage("Add Sample not available in this version of METLIMS "));
		/*	modal1.setPageCreator(new ModalWindow.PageCreator()
	    		{
	            public Page createPage() 
	            { 
	            
	            	
	            
					return null;
	            	//return buildAddingReaderPage(modal1, selectedExperimentId, selectedCount); 
	            
	            }
	    		});
			modal1.show(target); */
			}
			
		 
		 private AbstractGenericSampleFormUploadPage buildAddingReaderPage(final ModalWindow modal, final String selectedExperiment, final Integer selectedCount) 
			{
			return new AbstractGenericSampleFormUploadPage(getPage())
				{
				@Override
				protected ISavableSampleData readData(File newFile, FileUpload upload) throws SampleSheetIOException 
					{
					// JAK remove arguments from Mrc2TransitionalSubmissionSheetReader call to match actual constructor
					Mrc2TransitionalSubmissionSheetReader reader = new Mrc2TransitionalSubmissionSheetReader();
					return (ISavableSampleData) reader.readWorkBook(newFile, upload);
					}
		
				@Override
				protected int saveData(ISavableSampleData data) throws METWorksException
					{
					return mrc2SubmissionDataService.addUploadedSheetData((Mrc2TransitionalSubmissionSheetData) data, selectedCount);
					}
				
				@Override
				protected String getMailAddress() { return "metabolomics@med.umich.edu"; }

				@Override
				protected String getMailTitle() { return "METLIMS Sample Add Registration Message"; }
				};
			}
 		}  
	

	public ExperimentSelectorPanel buildDeleteSamplePanel(String id)
		{
		return new ExperimentSelectorPanel(id)
			{
			@Override
			public void doSubmit(String selectedExperiment, AjaxRequestTarget target)
				{
				}
			};
		}
		
	
	public ExperimentSelectorPanel buildPrintBarcodesByExperimentPanel(String id, final ModalWindow modal)
		{
		ExperimentSelectorPanel panel = new ExperimentSelectorPanel(id)
			{
			@Override
			public void doSubmit(String selectedExperiment, AjaxRequestTarget target)
				{
				List<String> uniqueAliquotLabelsAndIds = sampleService.sampleIdsForExpId(selectedExperiment);
						
				if (uniqueAliquotLabelsAndIds == null || uniqueAliquotLabelsAndIds.size() == 0)
					{
					String msg =  "Nothing to print : Experiment " + selectedExperiment + " has no registered aliquots.";
					target.appendJavaScript("alert('" + msg + "')");
					}
				else
					showBarcodeSelectorPage("print", selectedExperiment, modal, target, uniqueAliquotLabelsAndIds);
				}
			};
			
		panel.setButtonLabel("Select Ids...");
	
		return panel;
		}
	
		
	public void showBarcodeSelectorPage	(String action, final String expId, final ModalWindow modal, AjaxRequestTarget target, 
			final List<String> barcodesForExperiment)
		{
		modal.setInitialWidth(700);
		modal.setInitialHeight(650);
	
		final List<Pair> dummyPairLabels = new ArrayList<Pair>();
		for (String sampleId: barcodesForExperiment)
			dummyPairLabels.add(new Pair(sampleId, ""));
		
		modal.setPageCreator(new ModalWindow.PageCreator()
			{
			public Page createPage()
				{
				OptimizedBarcodeSelectorPage page = new OptimizedBarcodeSelectorPage("barcodeSelector", modal, dummyPairLabels,  expId, 
						"Select sample barcode ids");
				
				page.setHeader1Label("Sample ID");
				page.setHeader2Label("");
				return page;
				}
			});
		
		modal.show(target);
		}
	}
	
	/*
		
	public ExperimentSelectorPanel buildPrintBarcodesByExperimentPanel(String id, final ModalWindow modal)
		{
		ExperimentSelectorPanel panel = new ExperimentSelectorPanel(id)
			{
			@Override
			public void doSubmit(String selectedExperiment, AjaxRequestTarget target)
				{
				Experiment exp = experimentService.loadExperimentWithInfoForDrcc(selectedExperiment);
				if (!ListUtils.isNonEmpty(exp.getSampleList()))
					{
					String msg =  "Nothing to print : Experiment " + selectedExperiment + " has no registered samples.";
					target.appendJavaScript("alert('" + msg + "')");
					}
				else
					showBarcodeSelectorPage(exp, modal, target);
				}
			};
			
		panel.setButtonLabel("Select Ids...");
		panel.setAccountAdminOnly(true);
		return panel;
		}
	
	
	public void showBarcodeSelectorPage(final Experiment exp, final ModalWindow modal, AjaxRequestTarget target )
		{
		modal.setInitialWidth(700);
		modal.setInitialHeight(650);
		
		modal.setPageCreator(new ModalWindow.PageCreator()
			{
			public Page createPage()
				{
				return new OptimizedBarcodeSelectorPage("barcodeSelector", modal, exp.getSampleList(), 
						exp.getExpID(), "Select sample barcode ids");
				}
			});
		modal.show(target);
		}
	}  */



////////////  SCRAP CODE //////////////////////////


/*

public ExperimentSelectorPanel buildAddSamplePanel(String id)
	{
	ExperimentSelectorPanel panel = new ExperimentSelectorPanel(id)
		{
		@Override
		public void doSubmit(String selectedExperiment, AjaxRequestTarget target)
			{
			setResponsePage(new RegisterSampleEntryPage(getPage(), selectedExperiment,  5));
			}	
		};
		
	panel.setAccountAdminOnly(true);	
	panel.setButtonLabel("Add Samples...");
	return panel;
	} */
/*
	private ExperimentSelectorWithAjaxPanel buildUploadSamplePanel( final String id, final Boolean isEnabled, final ModalWindow modal1)
		{
		ExperimentSelectorWithAjaxPanel panel = new ExperimentSelectorWithAjaxPanel(id)
			{
			@Override
			public void doSubmit(final String selectedExperiment, final AjaxRequestTarget target) 
				{
		        modal1.setInitialWidth(650);
		        modal1.setInitialHeight(160);
		            	
		        modal1.setPageCreator(new ModalWindow.PageCreator()
	            	{
	                public Page createPage()
	                    {
	                    return new FileUploadPage(getPage())
	                    	{
							@Override
							public void readFile(File file, FileUpload upload)
								{	
								SampleAddUploader uploader = new SampleAddUploader(selectedExperiment, file, upload);
								try
									{
									uploader.readSubmissionForm();
									}
								catch (METWorksException e)
									{
									String msg = e.getMetworksMessage();
									target.appendJavaScript("alert('" + msg + "');");
									}
								}
	                    	};
	                    }
	            	});
	            modal1.show(target);
				}
			};
			
		panel.setButtonLabel("Add Samples...");
		panel.setAccountAdminOnly(true);
		return panel;
		} */


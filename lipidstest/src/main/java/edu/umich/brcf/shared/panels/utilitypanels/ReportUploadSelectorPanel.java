////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  ReportUploadSelectorPanel.java
//  Written by Jan Wigginton
//  July 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.shared.panels.utilitypanels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.panels.admin.messaging.METWorksMailMessage;
import edu.umich.brcf.metabolomics.panels.admin.messaging.METWorksMessageMailer;
import edu.umich.brcf.metabolomics.panels.admin.messaging.METWorksMessagePanel;
import edu.umich.brcf.shared.layers.dto.DocumentDTO;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.DocumentService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.SampleAssayService;
import edu.umich.brcf.shared.layers.service.SystemConfigService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;
import edu.umich.brcf.shared.util.widgets.METWorksAjaxUpdatingDateTextField;



public abstract class ReportUploadSelectorPanel extends Panel
	{
	@SpringBean
	private ExperimentService experimentService;
	
	@SpringBean
	private AssayService assayService;
	
	@SpringBean
	private DocumentService documentService;
	
	@SpringBean
	private SystemConfigService systemConfigService;
	
	@SpringBean
	private SampleAssayService sampleAssayService;
	
	@SpringBean
	private UserService userService;
	
	@SpringBean
	METWorksMessageMailer mailer;
	
	
	private FileUploadField fileUploadField;
	private ArrayList<FileUpload> filesUploaded;
	
	
	public ReportUploadSelectorPanel(String id) 
		{
		super(id);
		
		ReportUploadSelectorForm lde = new ReportUploadSelectorForm("launchDrccForm");
		lde.add(new UploadProgressBar("progress", lde));
		lde.setMultiPart(true);	
		add(lde);
		}

	
	public class ReportUploadSelectorForm extends Form 
		{	
		DropDownChoice<String> 	platformDrop, experimentDrop, assayDrop;
		IndicatingAjaxButton 	searchButton;
		
		List<String> availablePlatforms = Arrays.asList(new String [] {"Agilent", "ABSciex"});		
		List <String> absciexExperiments = experimentService.allExpIdsForAbsciex();  
		List <String> agilentExperiments = experimentService.allExpIdsForAgilent(); 
		
		String selectedExperiment = null, selectedAssay = null, selectedPlatform = "Agilent", selectedDocType = "Client Report";
		private Date editDate = new Date();
		
		Boolean reportOnly; 
		
		METWorksAjaxUpdatingDateTextField editDateFld;
		
		List <String> availableAssays = new ArrayList<String>();	
		FeedbackPanel feedback;
		
		public ReportUploadSelectorForm(String id)
			{
			super(id);
			
			add(feedback = new FeedbackPanel("feedback"));
			this.setOutputMarkupId(true);
			setMultiPart(true);
			
			add(platformDrop = buildPlatformDropdown("platformDropdown", "selectedPlatform"));
			add(experimentDrop = buildExperimentDropdown("experimentDropdown", "selectedExperiment"));
			add(assayDrop = buildAssayDropdown("assayDropdown", "selectedAssay"));

		  	add(fileUploadField = new FileUploadField("fileContents", new Model(filesUploaded)));
			add(searchButton = buildSearchButton());
			}		
		

		private IndicatingAjaxButton buildSearchButton()
			{
			return new IndicatingAjaxButton("uploadButton")
				{
				public boolean isEnabled()
					{
					return (drpIsSelected(getSelectedExperiment())  && drpIsSelected(getSelectedPlatform())
							&& drpIsSelected(getSelectedExperiment()));
					}
	
				
				@Override
				protected void onSubmit(AjaxRequestTarget target) 
					{
					try
						{
						doSubmit(selectedAssay, selectedExperiment, target);
						setOutputMarkupId(true);
						String assayId = StringParser.parseId(selectedAssay);
						target.add(ReportUploadSelectorForm.this.get("feedback"));
						}
					catch (Exception e)  {  }
					}

				@Override // issue 464
				protected void onError(AjaxRequestTarget target)  { target.add(ReportUploadSelectorForm.this.get("feedback")); }
				};
			}
		
		
		private boolean drpIsSelected(String drpValue)
			{
			return (!StringUtils.isNullOrEmpty(drpValue) && !"Choose One".equals( drpValue.equals("Choose One")));
			}
		
			
		private DropDownChoice buildExperimentDropdown(final String id, String propertyName)
			{
			DropDownChoice selectedExperimentDrop =  new DropDownChoice(id,  new PropertyModel(this, propertyName), 
				new LoadableDetachableModel<List<String>>() 
					{
		        	@Override
		        	protected List<String> load() 
		        		{ 
		        		return "absciex".equals(getSelectedPlatform())  ? absciexExperiments : agilentExperiments;  
		        		}
		        		
					})
				{
				@Override
				public boolean isEnabled()
					{
					return drpIsSelected(getSelectedPlatform());
					}
				};
			
			selectedExperimentDrop.setEnabled(false);
			selectedExperimentDrop.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForExperimentDrop"));			
			
			return selectedExperimentDrop;
			}
		
		
		private DropDownChoice buildPlatformDropdown(final String id, String propertyName)
			{
			DropDownChoice platformDrop =  new DropDownChoice(id,  new PropertyModel(this, propertyName), 
				availablePlatforms);
				
			platformDrop.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForPlatformDrop"));			
			
			return platformDrop;
			}
		
		
		private DropDownChoice buildAssayDropdown(final String id, String propertyName)
			{
			DropDownChoice drp = new DropDownChoice(id,  new PropertyModel(this, propertyName), 
					new LoadableDetachableModel<List<String>>() 
					{
	            	@Override
	            	protected List<String> load() 
	            		{ 
	            		if (availableAssays != null)
	            			return availableAssays;
	            		
	            		return new ArrayList<String>();
	            		}
					})
				{
				@Override
				public boolean isEnabled()
					{
					return (drpIsSelected(getSelectedPlatform()) && drpIsSelected(getSelectedExperiment()));
					}
				};
			
			drp.setEnabled(false);
			drp.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForAssayDrop"));			
			
			return drp;
			}
		
	
		private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(final String event,  final String response)
			{
			return new AjaxFormComponentUpdatingBehavior(event)
		        {
		        @Override
		        protected void onUpdate(AjaxRequestTarget target)
		        	{
		        	switch (response)
			        	{
			        	case "updateForExperimentDrop" :
			        	
			        		availableAssays = assayService.allAssayNamesForPlatformAndExpId(selectedPlatform, getSelectedExperiment());
			            	setSelectedAssay((availableAssays.size() == 1 ? availableAssays.get(0) : null));
				        
			            default:	
			            	
			            	target.add(searchButton);
				        	target.add(experimentDrop);
				        	target.add(assayDrop);
				        	break;
				        }
		        	}
		        };
			}
				

		public Date getEditDate()
			{
			return editDate;
			}
		
		
		public void setEditDate(Date dt)
			{
			editDate = dt;
			}
	
		public String getSelectedPlatform() 
			{
			return selectedPlatform == null ? "" : selectedPlatform.toLowerCase();
			}

		public void setSelectedPlatform(String selectedPlatform) 
			{
			this.selectedPlatform = selectedPlatform;
			}

		public String getSelectedExperiment() 
			{
			return selectedExperiment;
			}

		public void setSelectedExperiment(String selectedExperiment) 
			{
			this.selectedExperiment = selectedExperiment;
			}

		public String getSelectedAssay() 
			{
			return selectedAssay;
			}

		public void setSelectedAssay(String selectedAssay) 
			{
			this.selectedAssay = selectedAssay;
			}
		
	
		protected void doSubmit(String selectedAssay, String selectedExperiment, AjaxRequestTarget target)
			{
			boolean docIsReport = true; //selectedDocType.equals("Client Report");
			boolean docIsNull = true;
			try
				{
				final FileUpload upload = fileUploadField.getFileUpload();	
				
				docIsNull = (upload == null);
				if (!docIsNull)
	            	{
					DocumentDTO docDto = new DocumentDTO();
		            docDto.setFileContents(upload.getBytes());
		            docDto.setAccosiated(selectedExperiment);
		            docDto.setFileName(upload.getClientFileName());    
		            docDto.setFileType(upload.getContentType());
		            
		            String assayId = StringParser.parseId(selectedAssay);
		            docDto.setAssociatedAssay(assayId);
		            
		            if (true)
		            	uploadClientReport(upload, docDto, assayId, selectedExperiment);
		            else
		            	uploadDocument(upload, docDto);
		            
		            
		            target.appendJavaScript("alert('Document  uploaded successfully');");
	            	}
				}
			catch (Exception e) 
				{
				String msg = "Error while uploading file";
				
				if (docIsNull)
					msg = "The file couldn't be opened -- please verify that you have read permissions"
							+ " for it and that it isn't open in another program.";
					
				ReportUploadSelectorPanel.this.info(msg);
				target.appendJavaScript("alert('" + msg + "');");
				}
			}
		
	
		private void uploadDocument(FileUpload upload, DocumentDTO docDto)
			{
	        documentService.saveDocument(docDto);
	        ReportUploadSelectorForm.this.info("Document uploaded successfully.");
			}
		
		
		private void uploadClientReport(FileUpload upload, DocumentDTO docDto, String assayId, String selectedExperiment)
			{
			documentService.saveClientReport(docDto);
			
			final String assayName = assayId == null ? "Unknown" : assayService.getNameForAssayId(assayId);
			final String userName = userService.getFullNameByUserId(((MedWorksSession) getSession()).getCurrentUserId());
			
			String msg = mailer.getReportUploadMessage(userName, assayName, selectedExperiment);
			List<String> email_contacts = (List<String>) (systemConfigService.getSystemConfigMap()).get("client_report_notification_contact");
     		
			for (String email_contact : email_contacts)
	     		{
	     		mailer.sendMessage(new METWorksMailMessage("metabolomics@med.umich.edu", email_contact, "METLIMS Client Report Upload Message", msg));
	     		} 
     	
			// JAK fix 155 and 159
			sampleAssayService.updateStatusForExpAndAssayIdEfficiently(selectedExperiment, assayId, "C");

	    	//ReportUploadSelectorForm.this.info("Client report uploaded successfully.");
	    	//ReportUploadSelectorPanel.this.info("Client report uploaded successfully.");			
			}
		
	
		public void setSelectedDocType(String s)
			{
			selectedDocType = s;
			}
		
		public String getSelectedDocType()
			{
			return selectedDocType;
			}
		
		private boolean docIsReport()
			{
			if (reportOnly)
				return true;
			return selectedDocType.equals("Client Report");
			}
		}
		
	public abstract WebPage getResponsePage(String id, WebPage backPage, String selectedExperiment, String assayId);
	}
		
			
			

//EditDocument.java : 
//Written by Jan Wigginton January 2015

package edu.umich.brcf.shared.panels.utilitypanels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.markup.html.form.DropDownChoice;

import edu.umich.brcf.metabolomics.panels.admin.messaging.METWorksMailMessage;
import edu.umich.brcf.metabolomics.panels.admin.messaging.METWorksMessageMailer;
import edu.umich.brcf.shared.layers.dto.DocumentDTO;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.DocumentService;
import edu.umich.brcf.shared.layers.service.SampleAssayService;
import edu.umich.brcf.shared.layers.service.SystemConfigService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.DocumentOptions;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;



public class EditDocumentPage extends WebPage 
	{
	@SpringBean
	DocumentService documentService;
	
	@SpringBean
	AssayService assayService;
	
	@SpringBean
	SampleAssayService  sampleAssayService;
	
	@SpringBean
	SystemConfigService systemConfigService;
	
	@SpringBean
	UserService userService;
	
	@SpringBean
	METWorksMessageMailer mailer;
	
	Page backPage; 
	FeedbackPanel feedback;
	boolean reportOnly, documentOnly, protocolOnly;
	
	
	public EditDocumentPage(Page backPage, String associated, ModalWindow modal1, boolean reportOnly, boolean documentOnly) 
		{
		ArrayList<String> singleStrAsArr = new ArrayList<String>();
		singleStrAsArr.add(associated);
		initialize(backPage, singleStrAsArr, modal1, reportOnly, documentOnly, "");
		}
	
	public EditDocumentPage(Page backPage, String associated, ModalWindow modal1, boolean reportOnly, boolean documentOnly, boolean protocolOnly) 
		{
		ArrayList<String> singleStrAsArr = new ArrayList<String>();
		singleStrAsArr.add(associated);
		initialize(backPage, singleStrAsArr, modal1, reportOnly, documentOnly, "", protocolOnly);
		}
	
	
	public EditDocumentPage(Page backPage, String selectedExperiment, String selectedAssay, ModalWindow modal1, boolean reportOnly, boolean documentOnly) 
		{
		ArrayList<String> singleStrAsArr = new ArrayList<String>();
		singleStrAsArr.add(selectedExperiment);
		initialize(backPage, singleStrAsArr, modal1, reportOnly, documentOnly, selectedAssay);
		}
	
	public EditDocumentPage(Page backPage, String selectedExperiment, String selectedAssay, ModalWindow modal1, boolean reportOnly, boolean documentOnly, 
		boolean protocolOnly) 
		{
		ArrayList<String> singleStrAsArr = new ArrayList<String>();
		singleStrAsArr.add(selectedExperiment);
		initialize(backPage, singleStrAsArr, modal1, reportOnly, documentOnly, selectedAssay, protocolOnly);
		}
	
	
	public EditDocumentPage(Page backPage, ArrayList<String> assocArray, ModalWindow modal1, boolean reportOnly, boolean documentOnly) 
		{	
		initialize(backPage,assocArray, modal1, reportOnly, documentOnly, "");
		}
	
	
	public void initialize(Page backPage, ArrayList<String> assocArray, final ModalWindow modal1, boolean reportOnly, 
			boolean documentOnly, String selectedAssay)
			{
			initialize(backPage,assocArray, modal1, reportOnly, documentOnly, "", false);
			}

			
			
	public void initialize(Page backPage, ArrayList<String> assocArray, final ModalWindow modal1, boolean reportOnly, 
			boolean documentOnly, String selectedAssay, boolean protocolOnly) 
		{
		this.backPage = backPage;
		this.documentOnly = documentOnly;
		this.reportOnly = reportOnly;
		this.protocolOnly = protocolOnly;
		
		modal1.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
					
		EditDocumentForm edf = new EditDocumentForm("editDocumentForm",assocArray, modal1, selectedAssay); 
		edf.add(new UploadProgressBar("progress", edf));
		add(edf);
		}
	
	
	public final class EditDocumentForm extends Form 
		{
		private FileUploadField fileUploadField;
		private ArrayList<FileUpload> filesUploaded;
		
		private String expId = "", expLabel = "";
		private String selectedDocType, selectedAssay, selectedAssociation;
	        	
		private AjaxLink  cancelLink;
	    private DropDownChoice<String> docTypeDrop, assayTypeDrop, associateWithDrop;
	    private AjaxSubmitLink submitButton;
	    
	    
		public EditDocumentForm(final String id, final ArrayList <String> assocArr, final ModalWindow modal1, String selectedAssay) 
			{
			super(id);
			
			add(feedback = new FeedbackPanel("feedback"));
			feedback.setOutputMarkupId(true);
			this.setOutputMarkupId(true);
			setMultiPart(true);
			
			add(docTypeDrop = buildDropdownDocType("docType"));
			setSelectedDocType(documentOnly ? "Other" : (protocolOnly ? "Protocol" : "Client Report"));
			
			add(associateWithDrop = buildDropdownAssociateWith("associateWith", assocArr));
			
		 	add(assayTypeDrop = buildDropdownAssayType("assayType"));
		 	if (StringUtils.isNonEmpty(selectedAssay))
		 		setSelectedAssay(selectedAssay);
         
		 	add(fileUploadField = new FileUploadField("fileContents", new Model(filesUploaded)));
		 	//fileUploadField.add(buildStandardFormComponentUpdateBehavior("change", "updateForDocSelection"));
			 	
		 	add(new AjaxCancelLink("cancelButton", modal1));
		 
		 	add(submitButton = new AjaxSubmitLink("submitButton", this)
	            {
	            @Override
	        	public boolean isEnabled()
	        		{
	            	// Issue 251
	            	return ( !StringUtils.isEmptyOrNull(getSelectedAssay()) || getSelectedDocType().equals("Other"));
	        		}
				
	        	@Override
				protected void onSubmit(AjaxRequestTarget target) { doSubmit(target);}

				@Override // issue 464
				protected void onError(AjaxRequestTarget arg0) {   }
	            });
         
		 	submitButton.setOutputMarkupId(true);
			}


		private DropDownChoice buildDropdownDocType(String id)
			{
			DropDownChoice drp = new DropDownChoice("docType",  new PropertyModel(this, "selectedDocType"),
			  DocumentOptions.DOC_TYPES)
				{
				@Override
				public boolean isEnabled()
					{
					return !reportOnly && !documentOnly && !protocolOnly;
					}
				};
			
			if (reportOnly)
				setSelectedDocType("Client Report");
			
			if (protocolOnly)
				setSelectedDocType("Protocol");
			
			
			drp.add(buildStandardFormComponentUpdateBehavior("change", "updateForDocTypeDrop"));
			
			return drp;
			}
		
		
		private DropDownChoice<String> buildDropdownAssociateWith(String id, ArrayList <String> assocArr)
			{
			final ArrayList <String> labelledAssoc = buildLabelledAssociations(assocArr);
			final boolean haveChoices = labelledAssoc.size() > 1;
			
			DropDownChoice<String> drp = new  DropDownChoice<String>("associateWith",  new PropertyModel<String>(this, "selectedAssociation"), 
					labelledAssoc)
				{
				public boolean isEnabled()
					{
					if (selectedDocType.equals("Client Report") || reportOnly || protocolOnly) 
						return false;

					return (haveChoices);
					}
				};
				
			// always associate client report with experiment; if not  report, and there's only one choice, pick first; otherwise force user to select an association
			String defaultChoice = reportOnly || protocolOnly  ? expLabel  : (labelledAssoc.size() > 1 ? "" : labelledAssoc.get(0));
			setSelectedAssociation(defaultChoice);
			
			drp.add(buildStandardFormComponentUpdateBehavior("change", "updateForAssociationDrop"));
			
			return drp;
			}
		
		
		private DropDownChoice buildDropdownAssayType(String id)
			{
			DropDownChoice drp = new DropDownChoice("assayType",  new PropertyModel(this, "selectedAssay"),  new LoadableDetachableModel<List<String>>() 
					{
	             	@Override
	             	protected List<String> load() 
	             		{ 
	             		List <String> expAssays = assayService.allAssayNamesForExpId(expId,false); // JAK issue 163 do not skip ShoGun
	             		if (!(expAssays == null) && expAssays.size() > 0)
	             			return expAssays;
	             		
	             		return assayService.allAssayNames();
	             		}
             		})
				{
				public boolean isEnabled()
					{
					return selectedDocType.equals("Client Report") || selectedDocType.equals("Protocol");
					}
				};	
			
			List <String> expAssays = assayService.allAssayNamesForExpId(expId);
	     	if (expAssays.size() == 1)
	     		setSelectedAssay(expAssays.get(0));
	     		
			drp.add(buildStandardFormComponentUpdateBehavior("change", "updateForAssayDrop"));
			
			return drp;
			}
	
		
		private ArrayList<String> buildLabelledAssociations(ArrayList <String> arr)
			{
			ArrayList <String> labelled = new ArrayList <String>();
			for (int i =0; i < arr.size(); i++)
				{
				String assoc = arr.get(i);
				String c12 = assoc.substring(0, 2);
				char c1 = assoc.charAt(0);
				
				String typeAssoc;
				switch (c1)
					{
					case 'E' :   typeAssoc = "Experiment"; expId = assoc; break;
					case 'P' :   typeAssoc = "Project"; break;
					case 'S' :   typeAssoc = "Sample";  break;
					case 'C' :   typeAssoc = "Client";  break;
					default : typeAssoc = "Other"; 
					}
				String label = typeAssoc + "(" + assoc + ")";
				if (c1 == 'E') expLabel = label;
				labelled.add(label);
				}
	
			return labelled;
			} 
			
		
		private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(final String event, 
				final String response)
			{
			return new AjaxFormComponentUpdatingBehavior(event)
		        {
		        @Override
		        protected void onUpdate(AjaxRequestTarget target)
		        	{
		        	switch (response)
			        	{
			        	case "updateForAssociationDrop" : break;
			        	case "updateForAssayDrop":   break;
			        	case "updateForDocSelection" : break;
			        	case "updateForDocTypeDrop" : 
			        		setSelectedAssay("");
			        		if (selectedDocType.equals("Client Report") || selectedDocType.equals("Protocol"))
			        			setSelectedAssociation(expLabel); 
			        		break;
			        	}
		        	
		        	updateDropDowns(target);
	    			}
		        
		        void updateDropDowns(AjaxRequestTarget target)
				   {
				   target.add(docTypeDrop);
				   target.add(assayTypeDrop);
				   target.add(associateWithDrop);  
				   target.add(submitButton);
				   }
				};
			}
		

		@Override
		protected void onSubmit() {   }
		
		protected void doSubmit(AjaxRequestTarget target)
			{
			boolean docIsReport = selectedDocType.equals("Client Report");
			boolean docIsProtocol = selectedDocType.equals("Protocol");
			
			boolean docIsNull = true;
			try
				{
				final FileUpload upload = fileUploadField.getFileUpload();	
				
				docIsNull = (upload == null);
				if (!docIsNull)
	            	{
	            	DocumentDTO docDto = new DocumentDTO();
		            docDto.setFileContents(upload.getBytes());
		            docDto.setAccosiated(getSelectedId());
		            docDto.setFileName(upload.getClientFileName());    
		            docDto.setFileType(upload.getContentType());
		            
		            String assayId = StringParser.parseId(selectedAssay);
		            docDto.setAssociatedAssay(assayId);
		            
		            if (docIsProtocol)
		            	uploadProtocol(upload, docDto, assayId);
		            
		            else if (docIsReport())
		            	uploadClientReport(upload, docDto, assayId);
		            else
		            	uploadDocument(upload, docDto);
		            }
				}
			catch (Exception e) 
				{
				if (docIsNull)
					EditDocumentPage.this.info("The file couldn't be opened -- please verify that you have read permission " + System.getProperty("line.separator")
							+ " for it and that it isn't open in another program.");
				else
					EditDocumentPage.this.info("Error while uploading file");
				}
			target.add(feedback);
			}
		
		
		private void uploadDocument(FileUpload upload, DocumentDTO docDto)
			{
	         documentService.saveDocument(docDto);
	         EditDocumentPage.this.info("Document uploaded successfully.");
			}
		
		
		private void uploadClientReport(FileUpload upload, DocumentDTO docDto, String assayId)
			{
			documentService.saveClientReport(docDto);
			EditDocumentPage.this.info("Client report uploaded successfully.");
		
			final String assayName = assayId == null ? "Unknown" : assayService.getNameForAssayId(assayId);
			final String userName = userService.getFullNameByUserId(((MedWorksSession) getSession()).getCurrentUserId());
			
			String msg = mailer.getReportUploadMessage(userName, assayName, expId);
			List<String> email_contacts = (List<String>) (systemConfigService.getSystemConfigMap()).get("client_report_notification_contact");
     		
			for (String email_contact : email_contacts)
	     		{
	     		mailer.sendMessage(new METWorksMailMessage("metabolomics@med.umich.edu", email_contact, "METLIMS Client Report Upload Message", msg));
	     		} 
     	
     		sampleAssayService.updateStatusForExpAndAssayId(expId, assayId, "Complete");
			}
		
		private void uploadProtocol(FileUpload upload, DocumentDTO docDto, String assayId)
			{
			documentService.saveProtocolReport(docDto);
			EditDocumentPage.this.info("Protocol uploaded successfully.");
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
		 
	
		
		public void setSelectedAssay(String s)
			{
			selectedAssay = s;
			}
	
		public String getSelectedAssay()
			{
			return selectedAssay;
			}
		
		public void setSelectedAssociation(String a)
			{
			selectedAssociation = a;
			}
	
		public String getSelectedAssociation()
			{
			return selectedAssociation;
			}
		
		private String getSelectedId()
			{
			return StringParser.parseId(selectedAssociation);
			}
		}
	}
	
///////  SCRAP CODE //////////////



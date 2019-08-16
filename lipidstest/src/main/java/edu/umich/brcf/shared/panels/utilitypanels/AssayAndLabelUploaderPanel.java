////////////////////////////////////////////////////
// AssayAndFileSelectorPanel.java
// Written by Jan Wigginton, Dec 3, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.panels.utilitypanels;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
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
import edu.umich.brcf.shared.layers.dto.DocumentDTO;
import edu.umich.brcf.shared.layers.dto.StandardProtocolDTO;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.DocumentService;
import edu.umich.brcf.shared.layers.service.SampleAssayService;
import edu.umich.brcf.shared.layers.service.StandardProtocolService;
import edu.umich.brcf.shared.layers.service.SystemConfigService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;



public abstract class AssayAndLabelUploaderPanel extends Panel
	{
	@SpringBean
	AssayService assayService;
	
	@SpringBean
	 StandardProtocolService standardProtocolService;
	
	@SpringBean 
	 DocumentService documentService;
	
	@SpringBean
	 SystemConfigService systemConfigService;
	
	@SpringBean
	SampleAssayService sampleAssayService;
	
	@SpringBean
	UserService userService;
	
	@SpringBean
	METWorksMessageMailer mailer;
	
	
	private FileUploadField fileUploadField;
	private ArrayList<FileUpload> filesUploaded;
	
	
	public AssayAndLabelUploaderPanel(String id) 
		{
		super(id);
		
		AssayAndLabelUploaderForm lde = new AssayAndLabelUploaderForm("launchDrccForm");
		lde.add(new UploadProgressBar("progress", lde));
		lde.setMultiPart(true);	
		add(lde);
		}

	
	public class AssayAndLabelUploaderForm extends Form 
		{	
		DropDownChoice<String> 	assayDrop;
		IndicatingAjaxButton 	searchButton;
		TextField<String>  		assayLabelField; 
		List<String> documentTypes = Arrays.asList(new String [] { "STANDARD PROTOCOL"});
		
		String  selectedAssay = null,  selectedLabel = "",  selectedDocType = "STANDARD PROTOCOL";
		String sampleType = "";
		
		List <String> availableAssays = new ArrayList<String>();	
		FeedbackPanel feedback;
		
		public AssayAndLabelUploaderForm(String id)
			{
			super(id);
			
			availableAssays = assayService.allAssayNamesAndIds();
			add(feedback = new FeedbackPanel("feedback"));
			feedback.setOutputMarkupId(true);
			setMultiPart(true);
			
			add(assayDrop = buildAssayDropdown("assayDropdown", "selectedAssay"));
			add(buildDocTypeDrop("docTypeDropdown", "selectedDocType"));
			
			//add(new TextField("sampleType", new PropertyModel(this, "sampleType")));
			add(assayLabelField = new TextField<String>("labelField", new PropertyModel(this, "selectedLabel")));
			assayLabelField.add(buildStandardFormComponentUpdateBehavior("change", "updateForAssayDrop"));
			
			add(fileUploadField = new FileUploadField("fileContents", new Model(filesUploaded)));
			fileUploadField.add(buildStandardFormComponentUpdateBehavior("change", "updateForAssayDrop"));
			
			
			add(searchButton = buildSearchButton());
			}		
		

		private DropDownChoice<String> buildDocTypeDrop(String id, String property)
			{
			DropDownChoice<String> docDrop = new DropDownChoice<String>(id, new PropertyModel<String>(this, property),
					documentTypes);
			
			return docDrop;
			}
		
		
		public String getSampleType()
			{
			return sampleType;
			}


		public void setSampleType(String sampleType)
			{
			this.sampleType = sampleType;
			}


		private IndicatingAjaxButton buildSearchButton()
			{
			return new IndicatingAjaxButton("uploadButton")
				{
				public boolean isEnabled()
					{
					return true;
					
				  //  return (fileUploadField.getFileUpload() != null && drpIsSelected(getSelectedAssay())  && drpIsSelected(getSelectedLabel()));
					}
	
				
				@Override
				protected void onSubmit(AjaxRequestTarget target) // issue 464
					{
					try
						{
						doSubmit(selectedAssay, selectedLabel, target);
						setOutputMarkupId(true);
						String assayId = StringParser.parseId(selectedAssay);
						target.add(AssayAndLabelUploaderForm.this.get("feedback"));
						}
					catch (Exception e)  {  }
					}

				@Override
				protected void onError(AjaxRequestTarget target)  { target.add(AssayAndLabelUploaderForm.this.get("feedback")); }
				};
			}
		
		
		private boolean drpIsSelected(String drpValue)
			{
			return (!StringUtils.isNullOrEmpty(drpValue) && !"Choose One".equals(drpValue));
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
					});
			
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
			             default:	
			            	
			            	target.add(searchButton);
				        	
				        	target.add(assayDrop);
				        	break;
				        }
		        	}
		        };
			}
				
		public String getSelectedLabel()
			{
			return selectedLabel;
			}


		public void setSelectedLabel(String selectedLabel)
			{
			this.selectedLabel = selectedLabel;
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
			boolean docIsNull = true;
			FileUpload upload = null;
			try
				{
				try
				    {
				     upload = fileUploadField.getFileUpload();
				    }
				catch (Exception e)
				    {
					e.printStackTrace();
				    }
				
				// issue 235
				if (!(upload == null))
	            	{
					String msg = "Document uploaded successfully";
					if (upload.getClientFileName().indexOf(".docx") == -1 && upload.getClientFileName().indexOf(".pdf") == -1)
						msg = "Please upload only .pdf or .docx files";
					// Issue 242
					else if (StringUtils.isEmptyOrNull(getSelectedLabel()))
					    msg = "Please choose a sample type";
					// Issue 242
					else if (StringUtils.isEmptyOrNull(selectedAssay))
						msg = "Please choose an assay";
					else
	            	    processFile(upload, selectedAssay, selectedExperiment);	
	            	AssayAndLabelUploaderForm.this.error(msg);
	            	target.add(AssayAndLabelUploaderForm.this.get("feedback"));
					target.appendJavaScript("alert('" + msg + "');");
	            	}
				}
			catch (Exception e) 
				{
				String msg = "Error while uploading file";
				
				if (docIsNull)
					msg = "The file couldn't be opened -- please verify that you have read permissions"
							+ " for it and that it isn't open in another program.";
					
				AssayAndLabelUploaderForm.this.info(msg);
				target.appendJavaScript("alert('" + msg + "');");
				}
			}
		
	
			
	
		public void setSelectedDocType(String s)
			{
			selectedDocType = s;
			}
		
		public String getSelectedDocType()
			{
			return selectedDocType;
			}
		}
		
	
	public abstract void processFile(FileUpload upload, String selectedAssay, String selectedLabel);

	}
		
			
			

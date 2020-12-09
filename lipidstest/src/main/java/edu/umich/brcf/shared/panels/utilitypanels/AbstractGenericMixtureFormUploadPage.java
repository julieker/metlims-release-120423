///////////////////////////////////////////
//AbstractGenericMixtureFormUploadPage.java
//Written by Jan Wigginton January 2016
///////////////////////////////////////////

package edu.umich.brcf.shared.panels.utilitypanels;

// BLENDED ////////////
import java.io.File;
import java.util.List;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.file.Folder;
import edu.umich.brcf.metabolomics.panels.admin.messaging.METWorksMailMessage;
import edu.umich.brcf.metabolomics.panels.admin.messaging.METWorksMessageMailer;
import edu.umich.brcf.shared.layers.dto.DocumentDTO;
import edu.umich.brcf.shared.layers.service.DocumentService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.SystemConfigService;
import edu.umich.brcf.shared.util.METWorksException;
import edu.umich.brcf.shared.util.MixtureSheetIOException;
import edu.umich.brcf.shared.util.SampleSheetIOException;
import edu.umich.brcf.shared.util.interfaces.ISavableMixtureData;

public abstract class AbstractGenericMixtureFormUploadPage extends WebPage
	{
	@SpringBean
	DocumentService docService;

	@SpringBean
	ExperimentService experimentService;
	
	 @SpringBean
	 METWorksMessageMailer mailer;
	 
	 @SpringBean 
	 SystemConfigService systemConfigService;
	
	FeedbackPanel feedback;

	public AbstractGenericMixtureFormUploadPage(Page backPage)
		{
		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);		
		final AbstractGenericMixtureFormUploadForm ajaxSimpleUploadForm = new AbstractGenericMixtureFormUploadForm("createSampleForm");
		ajaxSimpleUploadForm.add(new UploadProgressBar("progress",ajaxSimpleUploadForm));
		add(ajaxSimpleUploadForm);
		}
	
	private class AbstractGenericMixtureFormUploadForm extends Form
		{
		private FileUploadField fileUploadField;

		public AbstractGenericMixtureFormUploadForm(String name)
			{
			super(name);
			setMultiPart(true);
			add(fileUploadField = new FileUploadField("fileInput"));
			add(buildSaveButton("submitButton"));
			}	
		IndicatingAjaxButton buildSaveButton(String id)
			{
		   return new IndicatingAjaxButton(id)
			    {
				@Override
				protected void onSubmit(AjaxRequestTarget target)// issue 464
					{
					final FileUpload upload = fileUploadField.getFileUpload();
					if (upload == null)
						{
						AbstractGenericMixtureFormUploadPage.this.error("Please specify an upload file");
						target.add(AbstractGenericMixtureFormUploadPage.this.get("feedback"));
						return;
						}	
					String cType = upload.getContentType().toLowerCase();
		
					if (!cType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
							&& !cType.startsWith("application/vnd.ms-excel"))
						{
						AbstractGenericMixtureFormUploadPage.this.error("File upload failed: "+ upload.getClientFileName()+ " is an invalid file format. ");
						target.add(AbstractGenericMixtureFormUploadPage.this.get("feedback"));
						return;
						}	
					File newFile = new File(getUploadFolder(), upload.getClientFileName());	
					try
						{
						newFile.createNewFile();
						upload.writeTo(newFile);
						ISavableMixtureData data = readData(newFile, upload);	
						if (data.getSampleCount() > 0)
							{
							int nSaved = saveData(data);
							AbstractGenericMixtureFormUploadPage.this.error("Saved " + 1 + " mixture from file: "+ upload.getClientFileName());
							target.add(AbstractGenericMixtureFormUploadPage.this.get("feedback"));						
							List<String> email_contacts = (List<String>) (systemConfigService.getSystemConfigMap()).get("mixture_registration_notify_contact");
							String msg = nSaved + " Mixture has been registered";
							for (String email_contact : email_contacts) 
								{
								METWorksMailMessage m = new METWorksMailMessage(getMailAddress(), email_contact, getMailTitle(),  msg);
								mailer.sendMessage(m);
								}
							}
						}	            
					catch (MixtureSheetIOException m)
						{
						// JAK issue 158
						m.printStackTrace();
						String msg = "Mixture sheet error in " + m.getSheetName() ;
						AbstractGenericMixtureFormUploadPage.this.error(msg + " " +  m.getMessage());
						target.add(AbstractGenericMixtureFormUploadPage.this.get("feedback"));
						}
					catch (METWorksException e)
						{
						String msg = "Sample save error : ";
						AbstractGenericMixtureFormUploadPage.this.error(msg + " " +  e.getMessage());
						target.add(AbstractGenericMixtureFormUploadPage.this.get("feedback"));
						}
					catch (Exception e)
						{
						System.out.println("here is stack trace...");
						e.printStackTrace();
						
						AbstractGenericMixtureFormUploadPage.this.error("Unable to upload file " + e.getMessage());
						target.add(AbstractGenericMixtureFormUploadPage.this.get("feedback"));
						} 
					finally 
					    { 
						Files.remove(newFile); 
						}
					}			
		        public void onError(AjaxRequestTarget target, Form form) 
			        { 
			    	target.add(AbstractGenericMixtureFormUploadPage.this.get("feedback"));
			    	} 
			    };
			}
		
		private void writeDocumentToDatabase(FileUpload upload, String expId, AjaxRequestTarget target)
			{
			Boolean docIsNull = (upload == null);
			if (!docIsNull)
				{
				DocumentDTO docDto = new DocumentDTO();
				docDto.setFileContents(upload.getBytes());
				docDto.setAccosiated(expId);
				docDto.setFileName(upload.getClientFileName());
				docDto.setFileType(upload.getContentType());
				docDto.setAssociatedAssay("");
				docService.saveDocument(docDto);
				AbstractGenericMixtureFormUploadPage.this.info("Document uploaded successfully.");
				target.add(AbstractGenericMixtureFormUploadPage.this.get("feedback"));
				}
			}
	
		private Folder getUploadFolder()
			{
			Folder uploadFolder = new Folder(System.getProperty("java.io.tmpdir"), "sample-uploads");
			uploadFolder.mkdirs();
			return (uploadFolder);
			}
		}	
	protected abstract String getMailTitle();
	protected abstract String getMailAddress();
	protected abstract ISavableMixtureData readData(File file, FileUpload upload) throws MixtureSheetIOException, METWorksException; 
	protected abstract int saveData(ISavableMixtureData data) throws METWorksException;
	}



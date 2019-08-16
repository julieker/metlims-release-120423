///////////////////////////////////////////
//AbstractGenericSampleFormUploadPage.java
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
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.dto.DocumentDTO;
import edu.umich.brcf.shared.layers.service.DocumentService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.SystemConfigService;
import edu.umich.brcf.shared.util.METWorksException;
import edu.umich.brcf.shared.util.SampleSheetIOException;
import edu.umich.brcf.shared.util.interfaces.ISavableSampleData;



public abstract class AbstractGenericSampleFormUploadPage extends WebPage
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

	public AbstractGenericSampleFormUploadPage(Page backPage)
		{
		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);
		
		final AbstractGenericSampleFormUploadForm ajaxSimpleUploadForm = new AbstractGenericSampleFormUploadForm("createSampleForm");
		ajaxSimpleUploadForm.add(new UploadProgressBar("progress",ajaxSimpleUploadForm));
		add(ajaxSimpleUploadForm);
		}

	
	private class AbstractGenericSampleFormUploadForm extends Form
		{
		private FileUploadField fileUploadField;

		public AbstractGenericSampleFormUploadForm(String name)
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
					AbstractGenericSampleFormUploadPage.this.error("Please specify an upload file");
					target.add(AbstractGenericSampleFormUploadPage.this.get("feedback"));
					return;
					}
	
				String cType = upload.getContentType().toLowerCase();
	
				if (!cType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
						&& !cType.startsWith("application/vnd.ms-excel"))
					{
					AbstractGenericSampleFormUploadPage.this.error("File upload failed: "+ upload.getClientFileName()+ " is an invalid file format. ");
					target.add(AbstractGenericSampleFormUploadPage.this.get("feedback"));
					return;
					}
	
				File newFile = new File(getUploadFolder(), upload.getClientFileName());
	
				try
					{
					checkFileExists(newFile);
					newFile.createNewFile();
					upload.writeTo(newFile);
	
					ISavableSampleData data = readData(newFile, upload);
	
					if (data.getSampleCount() > 0)
						{
						int nSaved = saveData(data);
						// writeDocumentToDatabase(upload, data.getExpId());
				
						AbstractGenericSampleFormUploadPage.this.error("Saved " + nSaved + " samples from file: "+ upload.getClientFileName());
						target.add(AbstractGenericSampleFormUploadPage.this.get("feedback"));
						
						List<String> email_contacts = (List<String>) (systemConfigService.getSystemConfigMap()).get("sample_registration_notification_contact");
						Experiment exp = experimentService.loadById(data.getExpId());		
						String msg = nSaved + " samples have been registered for experiment - '"+ exp.getExpName()+" ("+exp.getExpID()+")'";
						
						for (String email_contact : email_contacts) 
							{
							METWorksMailMessage m = new METWorksMailMessage(getMailAddress(), email_contact, getMailTitle(),  msg);
							mailer.sendMessage(m);
							}
						}
					}
	
				catch (SampleSheetIOException e)
					{
					// JAK issue 158
					e.printStackTrace();
					String msg = "Sample sheet error in " + e.getSheetName() + " at line " + e.getLine();
					AbstractGenericSampleFormUploadPage.this.error(msg + " " +  e.getMessage());
					target.add(AbstractGenericSampleFormUploadPage.this.get("feedback"));
					}
				catch (METWorksException e)
					{
					String msg = "Sample save error : ";
					AbstractGenericSampleFormUploadPage.this.error(msg + " " +  e.getMessage());
					target.add(AbstractGenericSampleFormUploadPage.this.get("feedback"));
					}
				catch (Exception e)
					{
					AbstractGenericSampleFormUploadPage.this.error("Unable to upload file " + e.getMessage());
					target.add(AbstractGenericSampleFormUploadPage.this.get("feedback"));
					} 
				finally { Files.remove(newFile); }
				}
			
				public void onError(AjaxRequestTarget target, Form form) { target.add(AbstractGenericSampleFormUploadPage.this.get("feedback")); } 
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
				AbstractGenericSampleFormUploadPage.this.info("Document uploaded successfully.");
				target.add(AbstractGenericSampleFormUploadPage.this.get("feedback"));
				}
			}

		
		private void checkFileExists(File newFile)
			{
			// if (newFile.exists() && !Files.remove(newFile))
			// throw new IllegalStateException("Unable to overwrite " +
			// newFile.getAbsolutePath());
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
	protected abstract ISavableSampleData readData(File file, FileUpload upload) throws SampleSheetIOException, METWorksException; 
	protected abstract int saveData(ISavableSampleData data) throws METWorksException;
	}


// Experiment exp = expService.loadById(expId);
// List<String> email_contacts = (List<String>) (systemConfigService.getSystemConfigMap()).get("sample_registration_notification_contact");
// for (String email_contact : email_contacts)
// 		mailer.sendMessage(new METWorksMailMessage("metabolomics@med.umich.edu", email_contact,"METLIMS Sample Registration Message", handler.getSamplesRead() + 
// " samples have been registered for experiment - '"+exp.getExpName()+" ("+exp.getExpID()+")'"));


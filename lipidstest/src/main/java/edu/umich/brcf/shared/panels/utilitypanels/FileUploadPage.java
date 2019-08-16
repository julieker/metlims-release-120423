
package edu.umich.brcf.shared.panels.utilitypanels;


import java.io.File;
import org.apache.wicket.Page;

import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.file.Folder;

import edu.umich.brcf.metabolomics.panels.admin.messaging.METWorksMessageMailer;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.ControlService;
import edu.umich.brcf.shared.layers.service.ControlTypeService;
import edu.umich.brcf.shared.layers.service.DocumentService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.layers.service.SystemConfigService;



public abstract class FileUploadPage extends WebPage 
	{
	@SpringBean
	SampleService sampleService;
	
	@SpringBean
	ControlService controlService;
	
	@SpringBean
	ControlTypeService controlTypeService;
	
	@SpringBean
	ExperimentService expService;
	
	@SpringBean
	DocumentService docService;
	
	@SpringBean
	SystemConfigService systemConfigService;
	
	@SpringBean 
	AssayService assayService;
	
	@SpringBean
	METWorksMessageMailer mailer;
	
	String title;
	
	public FileUploadPage(Page backPage)
		{
		add(new FeedbackPanel("feedback"));
		
		title = "Upload New Sample Info";
	    add(new Label("title", new PropertyModel<String>(this, "title")));
	       
		final FileUploadForm ajaxSimpleUploadForm = new FileUploadForm("createSampleForm");
     ajaxSimpleUploadForm.add(new UploadProgressBar("progress", ajaxSimpleUploadForm));
     add(ajaxSimpleUploadForm);
		}

	
	private class FileUploadForm extends Form
 		{
		private int rowCount = 0, sheetNum = 0;
		private FileUploadField fileUploadField;
		
 
     
     public FileUploadForm(String name)
     	{
         super(name);
         setMultiPart(true);
         add(fileUploadField = new FileUploadField("fileInput"));
        // add(buildSubmitButton("submitButton"));
     	}

     /*
     AjaxSubmitButton buildSubmitButton(String id)
     	{
     	return new AjaxSubmitButton(id)
     		{
     		@Override
     		public boolean isEnabled()
     			{
     			return (fileUploadField.getFileUpload() != null);
     			}

				@Override
				protected void onSubmit(AjaxRequestTarget arg0, Form<?> arg1) {
					// TODO Auto-generated method stub
					
				}
     		};
     	}
     */
     @Override
     protected void onSubmit()
     	{
         final FileUpload upload = fileUploadField.getFileUpload();
         
         if (upload == null)
         	{
         	FileUploadForm.this.error("Please specify an upload file");
         	return;
         	}
         
     	String cType = upload.getContentType().toLowerCase();
     if(!cType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") && 
     			!cType.startsWith("application/vnd.ms-excel"))
     	 	{
     		FileUploadForm.this.error("File upload failed: "+upload.getClientFileName() + " is an invalid File Format! ");
     		return;		
     	 	}
     	
     	File newFile = new File(getUploadFolder(), upload.getClientFileName());
         checkFileExists(newFile);
    
         try
         	{
         	System.out.println("Creating new file");
             newFile.createNewFile();
             upload.writeTo(newFile);
         	}
         catch (Exception e)
         	{
             throw new IllegalStateException("Unable to write file");
         	}
         try
         	{  readFile(newFile, upload);  }
         catch (Exception e) {     }
     	}
			
			
		private void checkFileExists(File newFile)
			{
			System.out.println("Checking that file exists");
		    // if (newFile.exists() && !Files.remove(newFile))
			//    throw new IllegalStateException("Unable to overwrite " + newFile.getAbsolutePath());
			}
			

		private Folder getUploadFolder()
			{
			Folder uploadFolder = new Folder(System.getProperty("java.io.tmpdir"), "sample-uploads");
			uploadFolder.mkdirs();
			return (uploadFolder);
			}

		
		}

	public String getTitle() 
		{
		return title;
		}

	public void setTitle(String title) 
		{
		this.title = title;
		}
	public abstract void readFile(File file, FileUpload upload);
	}


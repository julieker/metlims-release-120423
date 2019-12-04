// SampleInfoUploaderPage.java
// Written by Jan Wigginton September 2015

package edu.umich.brcf.metabolomics.panels.lipidshome.browse;

import java.util.ArrayList;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.service.Ms2DataSetService;
import edu.umich.brcf.metabolomics.layers.service.Ms2SampleMapService;
import edu.umich.brcf.shared.layers.service.SampleService;


public class SampleInfoUploaderPage extends WebPage 
	{
	Page backPage; 
	
	//private String UPLOAD_FOLDER = "./";
	
	@SpringBean 
	SampleService sampleService;
	
	
	@SpringBean
	Ms2DataSetService ms2DataSetService;
	
	@SpringBean
	Ms2SampleMapService ms2SampleMapService;
	
	
	public SampleInfoUploaderPage(Page backPage, SampleInfoUploader uploader, ModalWindow modal1) 
		{	
		this.backPage = backPage;
		modal1.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
		
		setOutputMarkupId(true);
		//add(new FeedbackPanel("feedback"));
		SampleInfoUploaderForm rlf = new  SampleInfoUploaderForm("randomizationForm", uploader, modal1);
		rlf.add(new UploadProgressBar("progress", rlf));
//		{
//			@Override
//			public boolean isEnabled()
//			{
//				return uploader != null && uploader.fileUploaded != null;
//			}
//		}
		 
		add(rlf);
		}
	

	//FeedbackPanel
	public final class SampleInfoUploaderForm extends Form 
		{
		//private static final long serialVersionUID = -6032695592374473021L;

		
		private FileUploadField fileUploadField;
		private ArrayList<FileUpload> filesUploaded;
		private SampleInfoUploader infoUploader;
				
	    private DropDownChoice<String> uploadTypeDrop; 
	    private String uploadType;
	    ModalWindow modal;
		
	    
	    public SampleInfoUploaderForm(String id, SampleInfoUploader uploader, final ModalWindow modal1) 
			{
			super(id);
			
			add(new FeedbackPanel("feedback"));
			this.setOutputMarkupId(true);
			setMultiPart(true);
			
			modal = modal1;
			infoUploader = uploader;
			uploadType = uploader.getUploadType().equals(SampleInfoUploader.UploadType.UPLOAD_TYPE_RANDOMIZATION) ? "Randomization" : "Sample Map";
			
			this.setOutputMarkupId(true);
			
			setMultiPart(true);	
			
			SampleInfoUploaderPage.this.info("Document uploaded successfully.");
		 	add(uploadTypeDrop = buildDropdownUploadType("randomizationType"));
		 	// issue 39
		    add(new AjaxLink <Void>("cancelButton")
            	{
				public void onClick(AjaxRequestTarget target)
					{
					if (modal1 != null)
						modal1.close(target);
					}	
				}); 
            add(fileUploadField = new FileUploadField("fileContents", new Model(filesUploaded)));
			
            add(new AjaxSubmitLink("submitButton", this)
	            {
	        	public boolean isEnabled()
	        		{
	        		return true;
	        		}

				
		        	@Override
				protected void onSubmit(AjaxRequestTarget arg0) 
	        		{
		        	}
	            });

			}
          
		private DropDownChoice<String> buildDropdownUploadType(String id)
			{
			return new DropDownChoice<String>(id,  new PropertyModel<String>(this, "uploadType"), 
				infoUploader.getPossibleUploadTypes())
				{
				public boolean isEnabled()
					{
					return false;
					}
				};		
			}		

		
		@Override
		public void onSubmit() 
			{
			filesUploaded = (ArrayList<FileUpload>) fileUploadField.getFileUploads();
			FileUpload  fileUpload = filesUploaded.size() > 0 ? filesUploaded.get(0) : null;
			
			//System.out.println("File uploaded" + (fileUploaded == null ? " is null. " : " is not null. "));
			if (fileUpload != null) 
				{
				try
					{
					//System.out.println("Attempting to parse file");
					///infoUploader.parseUploadAndSave(fileUploaded);
					SampleInfoUploaderPage.this.info("Document uploaded successfully.");
					error("Document uploaded");
					//SampleInfoUploaderForm.this.error("Please select the sample(s) to annotate");
					}
				catch (Exception e)
					{
					SampleInfoUploaderForm.this.error("Please select the sample(s) to annotate");
					SampleInfoUploaderPage.this.error("Please select the sample(s) to annotate");

            		//target.add(pmp.getParent().getParent().getParent());
            		//return;
					//System.out.println(e.getMetworksMessage());
					}
				SampleInfoUploaderPage.this.info("Document uploaded successfully.");
				}
			}
        	
		
		public String getUploadType()
			{
			return uploadType;
			}
		
		
		public void setUploadType(String uploadType)
			{
			this.uploadType = uploadType;
			}
    	}
	}


//////////////////////////////

//if (er != null && er.hasAllSamplesForExp())
//	{
//	System.out.println("Experiment was " + er.getExpId());
	//info("Finished loading randomization for experiment" + er.getExpId());
//	}
//else
//	info("Didn't load randomization values for samples in experiment " + expId);







/*package edu.umich.metworks.web.utils.panels;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.metworks.lims.service.SampleService;


public abstract class SampleInfoUploaderPage extends WebPage 
	{
	AjaxLink  cancelLink;
	Page backPage; 
	
	private FileUploadField fileUploadField;
	private String UPLOAD_FOLDER = "./";
	
	//Map<String, Integer> randomMap = SampleInfoUploader.uploadRandomization(fileUploaded);
		
	@SpringBean 
	SampleService sampleService;

	List <String> possibleUploadTypes = Arrays.asList(new String [] {"Randomization", "Sample Name Map"});
	public enum UploadType
		{ 
		UPLOAD_TYPE_RANDOMIZATION, 
		UPLOAD_TYPE_SAMPLE_MAP
		}
	
	
	public SampleInfoUploaderPage(Page backPage, String uploadType, final String eid, ModalWindow modal1) 
		{	
		this.backPage = backPage;
		modal1.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
		
		SampleInfoUploaderForm rlf = new  SampleInfoUploaderForm("randomizationForm", uploadType, eid, modal1);
		rlf.add(new UploadProgressBar("progress", rlf));
		add(rlf);
		}
	
	
	public FileUploadField getFileUploadField() 
		{
		return fileUploadField;
		}


	public void setFileUploadField(FileUploadField fileUploadField) 	
		{
		this.fileUploadField = fileUploadField;
		}


	public final class SampleInfoUploaderForm extends Form 
		{
		private static final long serialVersionUID = -6032695592374473021L;

		private FileUploadField fileUploadField;
		private FileUpload fileUploaded;
		String expId;
				
	    DropDownChoice<String> uploadTypeDrop; 
	    String  selectedUploadType; 
	    ModalWindow modal;
		
		public SampleInfoUploaderForm(String id, String uploadType, String eid, final ModalWindow modal1) 
			{
			super(id);
			
			modal = modal1;
			expId = eid;
			
			add(new FeedbackPanel("feedback"));
			this.setOutputMarkupId(true);
			
			setMultiPart(true);	
			
		 	add(uploadTypeDrop = buildDropdownUploadType("randomizationType"));
		 	setSelectedUploadType("Randomization");
            
		 	add(fileUploadField = new FileUploadField("fileContents", new Model<FileUpload>(fileUploaded))
	        	{
		 		public boolean isEnabled()
					{
					return fileUploadField != null;
					}
		 		
	        	protected boolean wantOnSelectionChangedNotifications() 
					{
	                return true;
					}
	        	});
			 	
		 	
            add(new AjaxLink("cancelButton")
            	{
				public void onClick(AjaxRequestTarget target)
					{
					if (modal1 != null)
						modal1.close(target);
					}					
				});
            
            
            add(new Button("submitButton")
	            {
	        	public boolean isVisible()
	        		{
	        		return true;
	        		}
	        	
	        	public boolean isEnabled()
	        		{
	        		return fileUploadField != null; //(fileUploadField.getFileUpload().getClientFileName() != null); // getSelected().equals("Client Report");
	        		}
	            });
 	     	}
   
		
		private DropDownChoice<String> buildDropdownUploadType(String id)
			{
			return new DropDownChoice<String>(id,  new PropertyModel(this, "selectedAssay"), 
				new LoadableDetachableModel<List<String>>() 
					{
                	@Override
                	protected List<String> load() 
                		{ 
                		return Arrays.asList(new String [] {"Sample Name Map", "Randomization"});
                		}
					})
				{
				
				//private static final long serialVersionUID = 8374367750179219455L;

				public boolean isEnabled()
					{
					return false;
					}
				
				protected boolean wantOnSelectionChangedNotifications() 
					{
		            return true;
					}
				};		
			}		

		
		@Override
		protected void onSubmit()
			{
			fileUploaded = fileUploadField.getFileUpload();
			
			System.out.println("File uploaded" + (fileUploaded == null ? " is null. " : " is not null. "));
			if (fileUploaded != null) 
				{	
				Map <String, Object> map = doUpload();
				afterUpload(map);
				
				//for (String key : randomMap.keySet())
				//	System.out.println("Value " + key + " Index " + randomMap.get(key));
				}
			}
		
		
		private String uploadFile(FileUpload uploadedFile)
			{
			File newFile = new File(UPLOAD_FOLDER + uploadedFile.getClientFileName());
			 
			if (newFile.exists()) 
				newFile.delete();
			
			try 
				{
				newFile.createNewFile();
				uploadedFile.writeTo(newFile);
				} 
			catch (Exception e) 
				{
				info("Error while uploading file " + uploadedFile.getClientFileName() + " " + e.getMessage());
				return null;
				}
			
			return newFile.getName();
			}

		public void setSelectedUploadType(String s)
			{
			selectedUploadType = s;
			}
	
		public String getSelectedUploadType()
			{
			return selectedUploadType;
			}
    	}
	
	public abstract Map<String, Object> doUpload();
	public abstract void afterUpload(Map<String, Object> sampleInfoMap);
	}


//////////////////////////////

//if (er != null && er.hasAllSamplesForExp())
//	{
//	System.out.println("Experiment was " + er.getExpId());
	//info("Finished loading randomization for experiment" + er.getExpId());
//	}
//else
//	info("Didn't load randomization values for samples in experiment " + expId);
*/

//
/*

add(new IndicatingAjaxLink("submitButton")
    {
	public boolean isVisible()
		{
		return true;
		}
	
	public boolean isEnabled()
		{
		return fileUploadField.getFileUpload().getClientFileName() != null; // getSelected().equals("Client Report");
		}

	@Override
	public void onClick(AjaxRequestTarget target) 
		{
		final FileUpload fileUploaded = fileUploadField.getFileUpload();
		
		System.out.println("File uploaded" + (fileUploaded == null ? " is null. " : " is not null. "));
		if (fileUploaded != null || fileUploaded == null) 
			{
			try
				{
				System.out.println("Attempting file parse");
				infoUploader.parseUploadAndSave(fileUploaded);
				}
			catch (METWorksException e)
				{
				String msg = e.getMetworksMessage();
				target.appendJavaScript("alert('" + msg + "');");
				}
			}
		}
    });
}

*/
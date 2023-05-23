///////////////////////////////////////////
//AbstractGenericSampleFormUploadPage.java
//Written by Jan Wigginton January 2016
///////////////////////////////////////////

package edu.umich.brcf.shared.panels.utilitypanels;

// BLENDED ////////////
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;

import edu.umich.brcf.metabolomics.panels.admin.messaging.METWorksMessageMailer;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.dto.DocumentDTO;
import edu.umich.brcf.shared.layers.dto.ProcessTrackingDetailsDTO;
import edu.umich.brcf.shared.layers.service.DocumentService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.MetLIMSTrackingAutomatedMessageService;
import edu.umich.brcf.shared.layers.service.ProcessTrackingService;
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
	ProcessTrackingService processTrackingService;
		
	 @SpringBean
	 METWorksMessageMailer mailer;
	 
	 @Autowired
	 @SpringBean
	 MetLIMSTrackingAutomatedMessageService  metLIMSTrackingAutomatedMessageService;
	 
	 @SpringBean 
	 SystemConfigService systemConfigService;
	 
	@Autowired
	@SpringBean
	private JavaMailSender mailSender = null;
	
	FeedbackPanel feedback;
	Calendar dDateStarted;
	String theWorkFlowIDStr;
	String theWorkFlowIDLabel;

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
		String mailTitle = "METLIMS TRACKING task list for Experiment ";
        String mailAddress =  "metabolomics@med.umich.edu";
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
					newFile.createNewFile();
					upload.writeTo(newFile);
	
					ISavableSampleData data = readData(newFile, upload);
	
					if (data.getSampleCount() > 0)
						{
						int nSaved = saveData(data);
						AbstractGenericSampleFormUploadPage.this.error("Saved " + nSaved + " samples from file: "+ upload.getClientFileName());
						target.add(AbstractGenericSampleFormUploadPage.this.get("feedback"));
						List<String> email_contacts = (List<String>) (systemConfigService.getSystemConfigMap()).get("sample_registration_notification_contact");
						Experiment exp = experimentService.loadById(data.getExpId());
						String msg = "";
						List <ProcessTrackingDetailsDTO> processTrackingDTOList =createAssignWorkFlowDetail(exp);
						if (StringUtils.isNullOrEmpty(theWorkFlowIDStr) || (processTrackingDTOList == null))
							{
							msg = nSaved + " samples have been registered for experiment - '"+ exp.getExpName()+" ("+exp.getExpID()+")'" 
						    		  + " Experiment: " + exp.getExpID() + " has not been assigned to any workflow";
						    
							}
							// issue 210
						else
							{
							msg = nSaved + " samples have been registered for experiment - '"+ exp.getExpName()+" ("+exp.getExpID()+")'" 
						    + " <br> <br> Experiment: " + exp.getExpID() + " has been assigned to workflow(s):" + theWorkFlowIDLabel;						
							}
						if (!StringUtils.isNullOrEmpty(theWorkFlowIDStr))
							try
								{
								processTrackingService.saveDefaultDTOs(processTrackingDTOList);
								}
							catch (Exception e)
								{
								System.out.println("Exception okay");
								e.printStackTrace();
								}
					
						List<Object[]> nList = new ArrayList<Object[]> ();
						nList = processTrackingService.loadTasksAssignedForExp(exp.getExpID());	
						metLIMSTrackingAutomatedMessageService.sendAssignedTasksExpReport(nList, msg, false);
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
					e.getStackTrace();
					AbstractGenericSampleFormUploadPage.this.error("Unable to upload file " + e.getMessage());
					target.add(AbstractGenericSampleFormUploadPage.this.get("feedback"));
					} 
				finally { Files.remove(newFile); }
				}
			
				public void onError(AjaxRequestTarget target, Form form) { target.add(AbstractGenericSampleFormUploadPage.this.get("feedback")); } 
			   };
			}

		// issue 210
		private List <ProcessTrackingDetailsDTO> createAssignWorkFlowDetail(Experiment exp)
			{
			List <ProcessTrackingDetailsDTO> theDtoList = new ArrayList <ProcessTrackingDetailsDTO> ();
			int totalDaysExpectedToSpan = 0; 
			theWorkFlowIDStr = "";
			theWorkFlowIDLabel = "";
			// issue 210
			String numSamplesStr = experimentService.grabNumSamples(exp.getExpID());
			List <Object []> assayObjects = experimentService.grabAssayType(exp.getExpID());
			String wfTypeStr = "";
			try 
			    {
			   	wfTypeStr = experimentService.grabWfType(exp.getExpID());
			    }
			catch (Exception e)
				{
				e.printStackTrace();
				}
			if (StringUtils.isNullOrEmpty(wfTypeStr))
				return null;
			String lWorkFlow = "";
			for (Object [] iAssay : assayObjects)
				{
				lWorkFlow = experimentService.grabtheWorkFlow(numSamplesStr.toLowerCase().replace(" ", ""), iAssay[0].toString().toLowerCase().replace(" ", ""), wfTypeStr.toLowerCase().replace(" ", ""));
				if (!theWorkFlowIDStr.contains(lWorkFlow) )
					theWorkFlowIDStr = theWorkFlowIDStr + " " +  lWorkFlow;
				}
		    int i = 0, idx = 0;
		    theWorkFlowIDLabel = theWorkFlowIDStr;
		    
			if (!StringUtils.isNullOrEmpty(theWorkFlowIDStr))
				{
				for (Object [] iAssay : assayObjects)
					{
				   
				    totalDaysExpectedToSpan = 0;
					wfTypeStr = experimentService.grabWfType(exp.getExpID());
					try
						{
						theWorkFlowIDStr = experimentService.grabtheWorkFlow(numSamplesStr.toLowerCase().replace(" ", ""), iAssay[0].toString().toLowerCase().replace(" ", ""), wfTypeStr.toLowerCase().replace(" ", ""));
						}
					catch (Exception e)
						{
						e.printStackTrace();
						}
					List<Object[]> nList = processTrackingService.loadAllDefaultTasksAssigned(theWorkFlowIDStr);
				    idx=0;
				    for (Object [] tobj: nList)
						{
						ProcessTrackingDetailsDTO lilProcessTrackingDetailsDTO = new ProcessTrackingDetailsDTO();
						lilProcessTrackingDetailsDTO.setTaskDesc(tobj[1].toString());
						lilProcessTrackingDetailsDTO.setDateAssigned(tobj[2].toString());
						lilProcessTrackingDetailsDTO.setAssignedTo(tobj[6].toString());						
						lilProcessTrackingDetailsDTO.setExpID(exp.getExpID());
						lilProcessTrackingDetailsDTO.setAssayID(iAssay[1].toString());
						lilProcessTrackingDetailsDTO.setDaysExpected(tobj[8].toString());
						dDateStarted = Calendar.getInstance();
						dDateStarted.add(Calendar.DAY_OF_MONTH, totalDaysExpectedToSpan);
						SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
						String datestr = sdf.format(dDateStarted.getTime());
						if (tobj[7] == null )
							lilProcessTrackingDetailsDTO.setDetailOrder(idx);
						else
							lilProcessTrackingDetailsDTO.setDetailOrder(Integer.parseInt(tobj[7].toString()));
						lilProcessTrackingDetailsDTO.setDateStarted(datestr);	
						lilProcessTrackingDetailsDTO.setWfID(theWorkFlowIDStr);
						lilProcessTrackingDetailsDTO.setAssayID(iAssay[1].toString());
						lilProcessTrackingDetailsDTO.setComments(" ");
						// issue 273
						if (idx == 0)
							lilProcessTrackingDetailsDTO.setStatus("In progress");
						else
							lilProcessTrackingDetailsDTO.setStatus("In queue");
						totalDaysExpectedToSpan = totalDaysExpectedToSpan + Integer.parseInt(tobj[8].toString());
						theDtoList.add(lilProcessTrackingDetailsDTO);	
						idx++;
						}
				    i++;
					}
				 }
			   return theDtoList;
			
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

		private Folder getUploadFolder()
			{
			Folder uploadFolder = new Folder(System.getProperty("java.io.tmpdir"), "sample-uploads");
			uploadFolder.mkdirs();
			return (uploadFolder);
			}
		}

	
	
	 public  String buildTaskString (List<Object[]> sList )
	     {
	     String sampleStringHeader = "";
	     
	    sampleStringHeader = sampleStringHeader + " <html> <head> <style>";
	     sampleStringHeader = sampleStringHeader + "table { font-family: arial, sans-serif;border-collapse: collapse; width: 100%;}";
	     sampleStringHeader = sampleStringHeader + "td, th { border: 1px solid #dddddd; text-align: left; padding: 8px;}";
	     sampleStringHeader = sampleStringHeader + "table tr:nth-child(even) {  background-color: #f2f2f2; }"; 
	     sampleStringHeader = sampleStringHeader + " </style> </head> <body>";
	     sampleStringHeader = sampleStringHeader + "<h2> List of Assigned Task for Experiment  </h2>";
	     sampleStringHeader = sampleStringHeader + "<table> <tr style = \"background-color: #f2f2f2\">   <th>User Name </th>  <th>Task Description</th> <th> Date Started </th> <th> Date On Hold </th> <th>Status</th> <th> Exp ID </th> <th> Assay ID </th> <th>WorkFlow Desc</th> </tr><tr>";
	     for (Object [] obj : sList)
		     sampleStringHeader = sampleStringHeader + " <tr><td>" + obj[0].toString() + "</td><td>" + obj[1] + "</td><td> " +  (obj[2] == null ? " " : obj[2].toString()) + "</td><td> " + (obj[3] == null   ? " " : obj[3].toString()) + "</td><td> " + obj[4] + "</td><td> " +  obj[5] +  "</td><td> " +   obj[6] +  "</td><td> " + obj[8] + "</td></tr>" ;
	     sampleStringHeader = sampleStringHeader + "</table></body></html>";
	     return sampleStringHeader;
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


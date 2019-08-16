package edu.umich.brcf.metabolomics.panels.admin.sample_submission.obsolete;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//has missing sample information
//import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.file.Folder;

import edu.umich.brcf.metabolomics.layers.service.GenusSpeciesService;
import edu.umich.brcf.metabolomics.panels.admin.messaging.METWorksMailMessage;
import edu.umich.brcf.metabolomics.panels.admin.messaging.METWorksMessageMailer;
import edu.umich.brcf.shared.layers.domain.ClientDocument;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.dto.SampleDTO;
import edu.umich.brcf.shared.layers.dto.ShortcodeDTO;
import edu.umich.brcf.shared.layers.service.ControlService;
import edu.umich.brcf.shared.layers.service.ControlTypeService;
import edu.umich.brcf.shared.layers.service.DocumentService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.layers.service.SystemConfigService;
import edu.umich.brcf.shared.util.widgets.MyFileLink;




public class SampleFormUploadPage extends WebPage 
	{
	@SpringBean
	SampleService sampleService;
    
	@SpringBean
	ExperimentService expService;
	
	@SpringBean
	DocumentService docService;
	
	@SpringBean
	SystemConfigService systemConfigService;
	
	@SpringBean
	METWorksMessageMailer mailer;
	
	@SpringBean
	ControlService controlService;
	
	@SpringBean
	ControlTypeService controlTypeService;
	
	@SpringBean
	GenusSpeciesService genusSpeciesService;
	
	FeedbackPanel feedback;
	
	public SampleFormUploadPage(Page backPage)
		{
		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);
			
		final CreateSampleForm ajaxSimpleUploadForm = new CreateSampleForm("createSampleForm");
        ajaxSimpleUploadForm.add(new UploadProgressBar("progress", ajaxSimpleUploadForm));
        add(ajaxSimpleUploadForm);
		}

	private class CreateSampleForm extends Form
    	{
        private FileUploadField fileUploadField;
        IndicatingAjaxButton submitButton ;
        public CreateSampleForm(String name)
        	{
        	super(name);
            setMultiPart(true);
            add(fileUploadField = new FileUploadField("fileInput"));
            add(submitButton = buildSubmitButton("submitButton"));
        	}
        
        
      private IndicatingAjaxButton buildSubmitButton(String id)
      	{
        IndicatingAjaxButton btn = new IndicatingAjaxButton(id)
        	{
        	@Override
            protected void onError(final AjaxRequestTarget target, final Form form) 
        		{
                target.add(feedback);
            	target.add(SampleFormUploadPage.this.get("feedback"));
    			}
        	

			@Override
			protected void onSubmit(AjaxRequestTarget target)
				{
				doSubmit(fileUploadField);
				target.add(feedback);
				target.add(SampleFormUploadPage.this.get("feedback"));
				}
        	};
        	
        return btn;	
     	}
	
	
	private void doSubmit(FileUploadField fileUploadField)
		{
        final FileUpload upload = fileUploadField.getFileUpload();
        if (upload != null)
        	{
            if((upload.getContentType().equalsIgnoreCase("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))||((upload.getContentType().equalsIgnoreCase("application/vnd.ms-excel"))))
            	{
                File newFile = new File(getUploadFolder(), upload.getClientFileName());
                
                // checkFileExists(newFile);
                try
                	{
                	newFile.createNewFile();
                    upload.writeTo(newFile);
                	}
                catch (Exception e)
                	{
                    throw new IllegalStateException("Unable to write file");
                	}
                int rowCount=0;
                //int sheetNum=1;
                String sheetName = "";
                
                try
                	{
                	Workbook workbook;
                	if(upload.getContentType().equalsIgnoreCase("application/vnd.ms-excel")){
                		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(newFile));
                		workbook = new HSSFWorkbook(fs);
                	}
                	else{
                		OPCPackage pkg = OPCPackage.open(newFile);
                		workbook = new XSSFWorkbook(pkg);
                	}
                    Sheet sheet = workbook.getSheet("Client Data"); //workbook.getSheetAt(0);
                   // ++sheetNum;
                    Row row=sheet.getRow(11);
                    rowCount=12;
                    String expID = row.getCell((short) 1).toString().trim();
                    boolean expExists=true;
                    Experiment exp;
                    try{
                    exp = expService.loadById(expID);
                    }
                    catch(Exception ex)
                    	{
                    	SampleFormUploadPage.this.error("File upload failed: Experiment "+expID+" does not exist!");
                    	expExists=false;
                    	return;
                    	}
                    
                    sheetName = "Client Data";
	                if (expExists)
	                    {
	                    ShortcodeDTO scDto = new ShortcodeDTO();
	                    row=sheet.getRow(14); rowCount=15;
	                    String code = row.getCell((short) 1).toString().trim();
	                    scDto.setCode(code);
	                    
	                    row=sheet.getRow(15); rowCount=16;
	                    String grantNo = row.getCell((short) 1).toString().trim();
	                    scDto.setNIH_GrantNumber(grantNo);
	                    
	                    scDto.setExp(exp);
	                    if((scDto.getCode()!=null) && (scDto.getCode().length()>0)){
	                    //	System.out.println("Saving shortcode");
	                    	expService.saveShortcode(scDto);
		                	}
	                    row=sheet.getRow(16); rowCount=17;
	                    
	                    String serviceRequest = row.getCell((short) 1).toString().trim();
	                    if((serviceRequest!=null) && (serviceRequest.length()>0)){
	                    //	System.out.println("Updating service request");
	                    	expService.updateServiceRequestForExperiment(exp, serviceRequest);
		                	}
		                
//	                    sheet = workbook.getSheetAt(1);
	                    
	                    sheet = workbook.getSheet("Samples Metadata");
	                    sheetName = "Samples Metadata";
	                   // ++sheetNum;
	                    rowCount=0;
	                    Iterator<Row> rows = sheet.rowIterator ();
	                    String strCel = null;
	              
	                    SampleDTO sdto;
	                    List<SampleDTO> samples=new ArrayList<SampleDTO>();
	                    while (rows.hasNext())
	                    	{
		                    row = rows.next();
		                    ++rowCount;
		               //     System.out.println("Reading row " + rowCount);
		                    if (rowCount>3)
		                    	{
		                    	sdto=new SampleDTO();
		                    	sdto.setExpID(expID);
		                    	if (row.getCell((short) 0)==null)
		                    		break;
		                    	
		                    	String sid = row.getCell((short)0).toString().trim();
		                    	
		                    	if(sid==null|| sid.trim().length()==0)
		                    		break;
		                    	try{
		                    		Sample thisSample = sampleService.loadSampleAlongWithExpById(sid);
		                    		if(thisSample!=null)
		                    			{
		                    		//	System.out.println("Sample id exists '" + sid + "'");
		                    			
		                    			SampleFormUploadPage.this.error("Unable to upload file, error in sheet "+sheetName +" at line: "+rowCount);
				                    	break;
			                    		}
		                    		}
		                    	catch(Exception ee){   }
		                    	
		                    	sdto.setSampleID(row.getCell((short)0).toString().trim());
		                    	sdto.setSampleName(row.getCell((short)1).toString().trim());
		                    	sdto.setSubjectId(row.getCell((short)2).toString().trim());
		                    	
		                    	Cell cell = row.getCell((short)3);
		                    	String userDefSampleType = (cell == null ? "" :  cell.toString().trim());
		                    	if (userDefSampleType.contains("--Please select") || userDefSampleType.startsWith("---"))
		                    		sdto.setUserDefSampleType("");
		                    	else
		                    		sdto.setUserDefSampleType(userDefSampleType);
		                    	
		                    	
		                    	cell = row.getCell((short)4);
		                    	String userDefGOS = (cell == null ? "" :  cell.toString().trim());
		                    	if (userDefGOS.contains("--Please select") || userDefGOS.startsWith("---"))
			                    	sdto.setUserDefGOS("");
		                    	else
		                    		sdto.setUserDefGOS(userDefGOS);
		                    	
		                    	
		                    	//System.out.println(row.getCell((short)5).toString());
		                    	BigDecimal vol= new BigDecimal(row.getCell((short)5).toString());
		                    	//System.out.println("Volume is " + vol);
		                    	sdto.setVolume(vol);
		                    	sdto.setVolUnits(row.getCell((short)6).toString().trim());
		                    	sdto.setSampleTypeId(row.getCell((short)7).toString().trim());
		                    	
		                    	strCel= row.getCell((short)8).toString();
		                    	if(strCel.indexOf(".")>=0)
		                    			sdto.setGenusOrSpeciesID(Long.valueOf(strCel.substring(0, strCel.indexOf("."))));
		                    	else
		                    		sdto.setGenusOrSpeciesID(Long.valueOf(strCel));

		                    	sdto.setLocID(row.getCell((short)9).toString().trim());
			                    
		                    //	System.out.println("Saved sample dto " + sdto.getSampleID());
		                    	samples.add(sdto);
		                    	}
	                    	}
	                    
	                    //sheet = workbook.getSheetAt(2);
	                    sheet = workbook.getSheet("Experimental Design");
	                    sheetName = "Experimental Design";
	                   // ++sheetNum;
	                    rowCount = 0;
	                    int fCellCount=0, aCellCount=0, cellCount=0;
	                    
	                    rows = sheet.rowIterator();
	                    Map<String, List<String>> factor_map = new HashMap<String, List<String>>();
	                    Map<String, List<String>> assay_map = new HashMap<String, List<String>>();
	                   // Map<String, String> experiment_assays = new HashMap<String, String>();
	                    List<String> factors = new ArrayList<String>();
	                    
	                    for(SampleDTO s : samples)
	                    	{
	                    	assay_map.put(s.getSampleID(), new ArrayList<String>());
	                    	}
	                    
	                    // MChearProjectInfoPanel
                    	while (rows.hasNext())
	                    	{
                    		row = rows.next();
                    		rowCount++;
		                    if ((factors.size()==0)&&(rowCount==10))
		                    	{
	                    		while(fCellCount>=0)
	                    			{
	                    			String cellStr = row.getCell((short)cellCount++).toString().trim();
	                    			
	                    			if(cellStr.startsWith("Factor"))
			                    		fCellCount++;
			                    	else if(cellStr.startsWith("Assay"))
			                    		aCellCount++;
			                    	else if(cellStr.equals(null)||cellStr==null ||cellStr.isEmpty())
				                    		break;
			                    	}
		                    	}
	                    	else if (factors.size()==0  && rowCount==11)
		                    	{
		                    	int fCount=1;
		                    	while (fCount < fCellCount+1)
		                    		{
		                    		String factorName = row.getCell((short)fCount++).toString().trim();
		                    		if(!factorName.equals(null)&&!factorName.isEmpty() &&!"<enter factor name>".equals(factorName) 
		                    			&& !"<Enter Name>".equals(factorName))
		                    				factors.add(factorName);
		                    		}
		                    	if (factors.size()>0)
		                    		for (int f=0; f<factors.size(); f++)
		                    			{
		                    			if (factor_map.get(factors.get(f)) != null)
		                    				{
		                    				SampleFormUploadPage.this.error("Unable to upload file, repeated factor name" + factors.get(f));
		                    				break;
		                    				}
		                    				
		                    			factor_map.put(factors.get(f), new ArrayList<String>());
		                    			}
		                    	}
		                    else if ((rowCount>11)&&(rowCount<samples.size()+12))
		                    	{
		                    	if(factors.size()>0)	
		                    		{
			                    	for(int cCount=1; cCount<factors.size()+1;cCount++)
			                    		{
			                    		String cellStr = row.getCell((short)cCount).toString().trim();
			                    		if((!(cellStr==null))&&(!cellStr.trim().isEmpty()))
				                    		factor_map.get(factors.get(cCount-1)).add(cellStr);
					                    else	
				                    		break;
			                    		}
		                    		}
		                    	//NewSampleForm
		                    	for(int cCount=fCellCount+1; cCount<fCellCount+aCellCount+1;cCount++)
		                    		{
		                    		Cell cell = row.getCell((short)cCount);
		                    		
		                    		if(!(cell==null))
		                    			{
		                    			String cellStr = cell.toString().trim();
			                    		if((!(cellStr==null))&&(!cellStr.isEmpty()))
			                    			{
				                    		String sid = samples.get(rowCount-12).getSampleID();
				                    		assay_map.get(samples.get(rowCount-12).getSampleID()).add(cellStr);
				                        //	experiment_assays.put(cellStr, sid);
			                    			}
			                    		else 
			                    			break;
		                    			}
		                    		else 
		                    			break;
		                    		}
		                    	}
		                    else if(rowCount>=samples.size()+12)
		                    	break;
		                    
		                }
                    	
                    	System.out.println("Samples number " + samples.size());
                    	boolean factorCheckPass = true, assayCheckPass=true;
	                    for (int f=0; f<factors.size(); f++)
	                    	{
	                    	if (samples.size()!=factor_map.get(factors.get(f)).size())
	                    		{
	                    		SampleFormUploadPage.this.error("Please check column "+(f+1)+" in sheet "+sheetName + " to make sure factor information has been provided for all samples being submitted" );
	                    		factorCheckPass=false;
	                    		System.out.println("Factor check passs didn't");
	                    		break;
	                    		}
	                    	}
	                    for(int s=0;s<samples.size();s++)
	                    	{
	                    	if(assay_map.get(samples.get(s).getSampleID()).isEmpty())
	                    		{
	                    		SampleFormUploadPage.this.error("Error in row "+(s+12)+" of sheet "+ sheetName +"! At least 1 assay should be selected for all samples being submitted" );
	                    		System.out.println("Assay check passs didn't");
	                    		
	                    		assayCheckPass=false;
                    			break;
	                    		}
	                    	}
	                    
	                    
	                    int sampleCount = 0;
	                    if(factorCheckPass && assayCheckPass)
	                    	{
	                    	sampleCount=sampleService.saveSamplesNew(expID, samples, factor_map, assay_map);//sampleService.saveSamples(samples, null,null);
	                    	}
	                    if(sampleCount==0)
	                    	SampleFormUploadPage.this.info("0 samples saved. Sample IDs cannot be duplicated, Please check values in the uploaded file - " + upload.getClientFileName());
	   				 	else
	   				 		{
	   				 		SampleFormUploadPage.this.info("Saved "+sampleCount+" samples from File: " + upload.getClientFileName());
		   				 	
	   				 		List<String> email_contacts = (List<String>) (systemConfigService.getSystemConfigMap()).get("sample_registration_notification_contact");
		   			       
	   				 		if (email_contacts != null)
	   				 			for (String email_contact : email_contacts){
	   				 				mailer.sendMessage(new METWorksMailMessage("metabolomics@med.umich.edu", email_contact,
		   								"METLIMS Sample Registration Message", sampleCount+" samples have been registered for experiment - '"+exp.getExpName()+" ("+exp.getExpID()+")'"));
		   			            }
	   				 		}
                    }
                }
                catch (Exception e)
                	{
                	e.printStackTrace();
                	SampleFormUploadPage.this.error("Unable to upload file, error in sheet "+ sheetName +" at line: "+rowCount);
                	}
                finally { Files.remove(newFile); }
            	}
        	else
        		{
        		SampleFormUploadPage.this.error("File upload failed: "+upload.getClientFileName() + " is an invalid File Format! ");
        		}
        	}
		}

	
	private Link buildFileLink(String linkId, String filename){//final ClientDocument doc) {
		ClientDocument doc=docService.loadClientDocByName(filename);
		Link link = new MyFileLink(linkId, new Model(doc)) ;
		link.add(new Label(filename, doc.getFileName()));
		return link;
		}
	
	
	private void checkFileExists(File newFile)
    	{
        if (newFile.exists())
        	{
            if (!Files.remove(newFile))
	            {
	            throw new IllegalStateException("Unable to overwrite " + newFile.getAbsolutePath());
	            }
        	}
    	}

    private Folder getUploadFolder()
    	{
    	Folder uploadFolder = new Folder(System.getProperty("java.io.tmpdir"), "sample-uploads");
        // Ensure folder exists
        uploadFolder.mkdirs();
        return (uploadFolder);
    	}
    }
}

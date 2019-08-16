///////////////////////////////////////////////
//Mrc2OldSubmissionSheetReader.java 
//Written by Jan Wigginton October 20, 2016
///////////////////////////////////////////////

package edu.umich.brcf.shared.util.sheetreaders;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.file.Files;

import edu.umich.brcf.metabolomics.layers.service.GenusSpeciesService;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.FactorLevel;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.dto.SampleDTO;
import edu.umich.brcf.shared.layers.dto.ShortcodeDTO;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.DocumentService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.FactorService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.layers.service.SampleTypeService;
import edu.umich.brcf.shared.layers.service.SubjectService;
import edu.umich.brcf.shared.layers.service.SystemConfigService;
import edu.umich.brcf.shared.util.METWorksException;
import edu.umich.brcf.shared.util.SampleSheetIOException;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.datacollectors.Mrc2SubmissionSheetData;
import edu.umich.brcf.shared.util.interfaces.ISampleWorkbookReader;
import edu.umich.brcf.shared.util.io.SpreadSheetReader;
import edu.umich.brcf.shared.util.utilpackages.NumberUtils;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;

// NOTE : This reader does not fully fill in values for the ClientInfo object, which are assumed to 
// be already in the database or saved in shortcode routines here.  
// TO CONSIDER : Should we move the shortcode saves to the sample service save? 


public class Mrc2OldSubmissionSheetReader extends SpreadSheetReader implements Serializable, ISampleWorkbookReader
	{
	@SpringBean
	SampleService sampleService;
	
	@SpringBean
	SampleTypeService sampleTypeService;
	
	@SpringBean
	ExperimentService expService;
	
	@SpringBean
	DocumentService docService;
	
	@SpringBean 
	FactorService factorService;
	
	@SpringBean
	SystemConfigService systemConfigService;
	
	@SpringBean 
	AssayService assayService;
	
	@SpringBean
	SubjectService subjectService;
	
	@SpringBean 
	GenusSpeciesService genusSpeciesService;
	
	private String sheetName;
	private int rowCount = 0, sheetNum = 0;

	private Mrc2SubmissionSheetData data = null; 
	
	private List<String> sampleList; 
	private List<String> sampleColNames = Arrays.asList( new String [] {"Sample Id", "Researcher Sample Id", "Researcher Subject Id", "Sample Type", "Genus or Species",
            "Volume", "Units", "Sample Type Id", "Genus or Species Id", "Location Id"});

	
	public Mrc2OldSubmissionSheetReader() 
		{
		Injector.get().inject(this);
		}
	
	
	public Mrc2SubmissionSheetData readWorkBook(File newFile, FileUpload upload) throws SampleSheetIOException
		{
		String expID;
		sampleList = new ArrayList<String>();
		data = new Mrc2SubmissionSheetData(); 
		
		try
			{
			Workbook workbook = createWorkBook(newFile, upload);
			expID = readClientInfo(workbook);
			if (expID != null)
				{
				data.getClientInfo().setExperimentId(expID);
				List <SampleDTO> samples = readSampleInfo(workbook, expID);
				readExperimentDesign(workbook, samples, expID, upload);    
				}
			return data;
			}
		
		catch (METWorksException | SampleSheetIOException e)
			{
			System.out.println("Error was " + e.getMessage());
			throw new SampleSheetIOException(e.getMessage(), rowCount,sheetName);
			}
		catch (Exception e)
			{
			e.printStackTrace();
			throw new SampleSheetIOException(e.getMessage(), rowCount , sheetName);
		    }
		finally { Files.remove(newFile); }
		}
		
 
	 private String readClientInfo(Workbook workbook) throws SampleSheetIOException
		{
		sheetName = "Client Data";
		Sheet sheet = workbook.getSheet(sheetName); 
		sheetNum = 2;
		rowCount=12;
		    
		Row row=sheet.getRow(11);
	    String expId = row.getCell((short) 1).toString().trim();
	 
		if (StringUtils.isNullOrEmpty(expId))
			throw new SampleSheetIOException("Experiment ID is missing", rowCount, sheetName);
		
		 Experiment exp;
		 try { exp = expService.loadSimplestById(expId); }
		 catch(Exception ex)
		 	{
		 	String msg = "File upload failed: Experiment id + (" + expId + ") in client info sheet does not correspond to any known experiments";
			System.out.println(msg);
			throw new SampleSheetIOException(msg, rowCount, sheetName);
		    }
		     
		 readInitialClientInfo(sheet, row, exp);
		 
		 return expId;
		 }
	 
 
	 private void readInitialClientInfo(Sheet sheet, Row row, Experiment exp) throws SampleSheetIOException
	 	 {
	 	 ShortcodeDTO scDto = new ShortcodeDTO();
	 	 
	 	 int rowCt = 14;
	 	 try 
    		{ 
    		row=sheet.getRow(rowCt++); 
    		//	String code = row.getCell(1).toString().trim();
    		Cell cell = row.getCell(1);   		
    		cell.setCellType(Cell.CELL_TYPE_STRING);  		   
  	    	String code = cell.toString().trim();  	    	 
    		scDto.setCode(code);
	     
		    row=sheet.getRow(rowCt++); 
		    //String grantStr = row.getCell((short) 1).toString().trim();
		    
		    cell = row.getCell(1); 
		    cell.setCellType(Cell.CELL_TYPE_STRING);  	
		    String grantStr = cell.toString().trim();
			
		    if (StringUtils.isNullOrEmpty(grantStr) || "NO NIH GRANT".equals(grantStr.toUpperCase()))
		    	scDto.setNIH_GrantNumber("No NIH Grant");
		    else
		    	{ 
		    	String [] grants = parseGrantNumbers(grantStr);
		    
		    	if (grants != null)
			    	{
			    	if (grants.length > 0)
			    		{
			    		scDto.setNIH_GrantNumber(grants[0]);
			    		data.getClientInfo().setNihGrantNumber(grants[0]);
			    		}
			    	
			    	if (grants.length > 1)
			    		{
			    		data.getClientInfo().setNihGrantNumber2(grants[1]);
			    		scDto.setNIH_GrantNumber_2(grants[1]);
			    		}
	
			    	if (grants.length == 3)
			    		{
			    		data.getClientInfo().setNihGrantNumber2(grants[2]);
			    		
			    		scDto.setNIH_GrantNumber_3(grants[2]);
			    		}
			    	
			    	if (grants.length > 3)
			    		{
			    		StringBuilder sb = new StringBuilder();
			    		
			    		for (int j = 2; j < grants.length; j++)
			    			sb.append(grants[j] + ", ");
			    		
			    		String remainingGrants = sb.toString();
			    		data.getClientInfo().setNihGrantNumber2(remainingGrants);
			    		scDto.setNIH_GrantNumber_3(remainingGrants);
			    		}
			    	}
		    	}
		   	 
		    scDto.setExp(exp);
		    if((scDto.getCode()!=null) && (scDto.getCode().length()>0))
			    expService.saveShortcode(scDto); 
		    else
		    	throw new SampleSheetIOException("Shortcode cannot be blank.  If no shortcode exists, please indicate this by filling the shortcode field with NA", 15, "Client Data");
    		}
    	
    	catch (RuntimeException | SampleSheetIOException e) { throw new SampleSheetIOException(e.getMessage(), 15, "Client Data"); }
    	catch (Exception e) { throw new SampleSheetIOException("Error while saving shortcode. please make sure shortcode has no more than 20 characters", 15, "Client Data"); }
	     	
	     String serviceRequest = "";
	     try
	    	 {	     
	    	 row=sheet.getRow(rowCt); 
	    	 serviceRequest = row.getCell((short) 1).toString().trim();
	    	 if(serviceRequest!=null && serviceRequest.length()>0)
	    		 expService.updateServiceRequestForExperiment(exp, serviceRequest);
	     	}
	     catch (Exception e) { throw new SampleSheetIOException("Error while saving service request " + serviceRequest, 16, "Client Data");}
	 	 }
	     
	 
     String [] parseGrantNumbers(String grantStr)
	 	{
	 	if (StringUtils.isNullOrEmpty(grantStr)) 
	 		return null;
	 	
		String [] grants = StringUtils.splitAndTrim(grantStr, ";");
	 	if (grants.length > 1)
	 		return grants;
	 	
	 	return StringUtils.splitAndTrim(grantStr, ",");
	 	}

	
	 private List <SampleDTO> readSampleInfo(Workbook workbook, String expID) throws SampleSheetIOException 
		{
		sheetName = "Samples Metadata";
	    Sheet sheet = workbook.getSheet(sheetName); 
	    sheetNum = 3;
	    rowCount=0;
	    
	    Iterator<Row> rows = sheet.rowIterator ();
	    String strCel = null;
	    SampleDTO sdto;
	    List<SampleDTO> samples=new ArrayList<SampleDTO>();
	    
	    while (rows.hasNext())
	    	{
	        Row row = rows.next();
	        
	        if (++rowCount < 4)
	        	continue;
	        
	        if (this.isSampleRowBlank(row))
	        	break;

	      	String sid = row.getCell((short)0).toString().trim();
	      	
	       	if(sid != null && sid.length() != 9)
	      		throw new SampleSheetIOException("Unable to upload file,  sample id " + sid + " is not a valid (9 character) metabolomics sample id", rowCount, sheetName);
	  	
	      	doMissingInfoCheck(row, sid);
	      	doAlreadyExistsCheck(sid);
	      	doDuplicateSampleCheck(sid);
	      	doCheckForSampleLengthViolations(row, sid);
	      	
	      	try { samples.add(readInSampleRow(row, expID, sid)); }
	      	catch (Exception e)  { throw new SampleSheetIOException(e.getMessage(), rowCount, sheetName); }
	      	}

	    return samples;
		}

	 
	 // Need to check for both blank and "please select or describe" in the dropdown fields
	 private boolean isSampleRowBlank(Row row)
		 {
		 for (int i = 0; i < 10; i++)
			 {
			 String val = row.getCell(i).toString();
			 
			 if ((i == 3 || i == 4) && val.contains("Please select or describe---"))
				 continue;
			 
			 if (!StringUtils.isNullOrEmpty(val))
				 return false;
			 }
		 
		 return true;
		 }
	 
	 
	 private void doMissingInfoCheck(Row row, String sid) throws SampleSheetIOException
		 {
		for (int i = 0; i < 10; i++)
			 {
			 String val = row.getCell(i).toString();
			 
			 // column 3 or 4 can be unselected (for now) -- keep check in place in case this changes
			 if (i == 3 || i == 4) 
				 continue;
			 
			 boolean isOrdinaryMissing = StringUtils.isNullOrEmpty(val);
			 boolean isNonStandardMissing = (i == 3 || i == 4) && !isOrdinaryMissing && val.startsWith("---");
			 
			 if (isOrdinaryMissing || isNonStandardMissing)
				 throw new SampleSheetIOException("Unable to load sample " + sid  + ". Value for column " + sampleColNames.get(i) + " is missing", rowCount, sheetName);
			 }
		 }
	 
    
	private void doDuplicateSampleCheck(String sid) throws SampleSheetIOException
		{
	   	if (sampleList.indexOf(sid) > -1) 
      		{
      		System.out.println("Sample ID" + sid + " already exists.");
      		throw new SampleSheetIOException("Unable to upload file, sample: "  + sid + " already exists in the spreadsheet "
      				, rowCount, sheetName);
      		}
      	
      	sampleList.add(sid)	;
		}
	
	
	private void doAlreadyExistsCheck(String sid) throws SampleSheetIOException
		{
		Sample thisSample = null;
      	try	{ thisSample = sampleService.loadSampleAlongWithExpById(sid);  }
      	catch (Exception ee) { }
      	
      	if(thisSample != null)
      		throw new SampleSheetIOException("Unable to upload file,  sample " + thisSample.getSampleID() + " already exists in the database.", rowCount, sheetName);
  		}
	 	    
	  
	  private void  doCheckForSampleLengthViolations(Row row, String sid) throws SampleSheetIOException
		  {
		  List <Integer> fLengths = Arrays.asList(new Integer[] { 9, 120, 100, 100, 50, 100, 26, 7, 8, 6}); 
		  // 9, 120, 100, 100, 30, 11.3, 26, 7, 6, 
	//	  private List<String> sampleColNames = Arrays.asList( new String [] {"Sample Id", "Researcher Sample Id", "Researcher Subject Id", "Sample Type", "Genus or Species",
	//	            "Volume", "Units", "Sample Type Id", "Genus or Species Id", "Location Id"});

		  // TO DO Need to adjust some column widths
	      int index = 0;
	      for (String theName : sampleColNames)
	    	  {
	    	  String val = row.getCell(index).toString();
	    	  Cell cell = row.getCell(index);
	    	  cell.setCellType(Cell.CELL_TYPE_STRING);
	    	  val = cell.toString();
	    	 
	    	 
	    	   if ("Volume".equals(theName))
	    	     {
	    	     if (val != null && val.startsWith("-"))
	    	    	 {
	    	    	 String msg = "Cannot load sample " + sid + ". Volume must be a positive number.";
	    	    	 throw new SampleSheetIOException(msg, rowCount, sheetName);
	    	    	 }
	    	   
	    	     if (!NumberUtils.verifyDecimalRange(val, 22, 9))
	    	    	{
	    	    	try
	    	    		{
	    	    		//NumberUtils.exceptionCheckDecimalRange(val, 8, 3);
	    	    		String msg = "Cannot load sample " + sid + ". " + " volume " + val + " is an illegal value";
	    	    		throw new SampleSheetIOException(msg, rowCount, sheetName);
	    	    		}
	    	    	catch (Exception e)
	    	    		{
	    	    		String msg = "Cannot load sample " + sid + ". " + e.getMessage();
	    	    		throw new SampleSheetIOException(msg, rowCount, sheetName);
	    	    		}
	    	    	}
	    	     
	    		 continue;
	    	     }
	    	 
	    	 if (val.length() > fLengths.get(index) && index !=5 )
	    		  {
	    		  String msg = "Cannot load sample " + sid + ". Column '" + theName + "'with value " +  val + " cannot be longer than " + fLengths.get(index) + " characters";
	    		  throw new SampleSheetIOException(msg, rowCount, sheetName);
	    		  }
	    	  index ++;
	    	  }
	      }
	
	  //mailer
	  

	private SampleDTO readInSampleRow(Row row, String expID, String sid) throws SampleSheetIOException
		{
		SampleDTO sdto=new SampleDTO();

		try
			{
			sdto.setExpID(expID);
			
			String sidrow = row.getCell((short)0).toString().trim();
	      	sdto.setSampleID(sidrow);
	      	
	      	String sName = row.getCell((short)1).toString().trim();
	      	sdto.setSampleName(sName);
	      	
	      	String suid = row.getCell((short)2).toString().trim();
	      	sdto.setSubjectId(suid);
	      	
	      	Cell cell = row.getCell((short)3);
	      	String userDefSampleType = (cell == null ? "" :  cell.toString().trim());
	      	if (userDefSampleType.contains("--Please select") || userDefSampleType.startsWith("---"))
	      		sdto.setUserDefSampleType("");
	      	else
	      		sdto.setUserDefSampleType(userDefSampleType);
	    
	      	
	      	cell = row.getCell((short)4);
	      	cell.setCellType(Cell.CELL_TYPE_STRING);
	    	String userDefGOS = (cell == null ? "" :  cell.toString().trim());
	      	
	      	if (userDefGOS.contains("--Please select") || userDefGOS.startsWith("---"))
	          	sdto.setUserDefGOS("");
	      	else if (userDefGOS.contains("Please specify if known"))
	      		sdto.setUserDefGOS(StringParser.parseName(userDefGOS));
	      	else		
	      		sdto.setUserDefGOS(userDefGOS);
	      	
	      	cell = row.getCell((short) 5);
	      	cell.setCellType(Cell.CELL_TYPE_STRING);
	      	BigDecimal vol= new BigDecimal(cell == null ? "" :  cell.toString().trim());
	      	sdto.setVolume(vol);
	      	sdto.setVolUnits(row.getCell((short)6).toString().trim());
	      	sdto.setSampleTypeId(row.getCell((short)7).toString().trim());
	      	
	      	cell = row.getCell((short) 8);
	      	cell.setCellType(Cell.CELL_TYPE_STRING);
	      	String strCel= cell.toString();
	      	if(strCel.indexOf(".")>=0)
	      		sdto.setGenusOrSpeciesID(Long.valueOf(strCel.substring(0, strCel.indexOf("."))));
	      	else
	      		sdto.setGenusOrSpeciesID(Long.valueOf(strCel));
	
	      	sdto.setLocID(row.getCell((short)9).toString().trim());
	      	}	
		catch (Exception e)
			{
			e.printStackTrace();
			String sampleId = (sdto == null  ? "" : sdto.getSampleID());
			
			String msg = "Error while reading sample information"; 
			if (!StringUtils.isNullOrEmpty(sampleId))
				msg +=  (" for sample id " + sampleId);
			
			System.out.println(msg);
			throw new SampleSheetIOException(msg, rowCount, sheetName);
			}
		
		return sdto;
		}
			
	
	private void readExperimentDesign(Workbook workbook, List <SampleDTO> samples, String expId, FileUpload upload) throws SampleSheetIOException
		{
		sheetName = "Experimental Design";
		sheetNum = 4;
        rowCount = 0;
        
        Sheet sheet = workbook.getSheet(sheetName); 
	    int fCellCount=0, aCellCount=0, cellCount=0;
         
        Iterator<Row> rows = sheet.rowIterator();
        Map<String, List<String>> factor_map = new HashMap<String, List<String>>();
        Map<String, List<String>> caseLessMap = new HashMap<String, List<String>>();
        
        Map<String, List<String>> assay_map = new HashMap<String, List<String>>();
        Map<String, String> experiment_assays = new HashMap<String, String>();
        List<String> factorNames = new ArrayList<String>();
         
        for(SampleDTO s : samples)
         	assay_map.put(s.getSampleID(), new ArrayList<String>());
         
     	while (rows.hasNext())
     		{
     		Row row = rows.next();
     		
     		if(++rowCount >= samples.size()+12)
              	break;
     		
     		if (factorNames.size()==0 && rowCount==10)
            	{
         		while(fCellCount >= 0)
         			{
         			String cellStr = row.getCell((short)cellCount++).toString().trim();
         			if(cellStr.equals(null)||cellStr==null ||cellStr.isEmpty())
         				break;
         			
         			if(cellStr.startsWith("Factor"))
                 		fCellCount++;
                 	
         			if(cellStr.startsWith("Assay"))
                 		aCellCount++;
                 	}
             	}
         	
            else if (factorNames.size() == 0  && rowCount == 11)
             	{
             	int fCount=1;
             	while (fCount < fCellCount+1)
             		{
             		String factorName = row.getCell((short)fCount++).toString().trim();
             		
             		if (StringUtils.isNullOrEmpty(factorName))
             			{
             		    String msgf = "Can not have blank factor names";
                        throw new SampleSheetIOException(msgf, rowCount, sheetName);
             			}
             		
             		if( !"<enter factor name>".equals(factorName)  && !"<Enter Name>".equals(factorName))
             			factorNames.add(factorName);
             		}
             	
             	if (factorNames.size()>0)
             		for (int f=0; f<factorNames.size(); f++)
             			{
             			String factorName = factorNames.get(f);
             			if (factor_map.get(factorName) != null 
             					|| caseLessMap.get(factorName.toLowerCase()) != null)
             				throw new SampleSheetIOException("Unable to upload file : repeated factor name" +  factorNames.get(f), rowCount, sheetName);
             				
             			factor_map.put(factorName, new ArrayList<String>());
             			caseLessMap.put(factorName.toLowerCase(), new ArrayList<String>());
             			}
             	}
            
             else if (rowCount > 11 &&  rowCount<samples.size()+12)
             	{
             	if(factorNames.size()>0)	
             	 	for(int cCount=1; cCount<factorNames.size()+1;cCount++)
                 		{
                 		String cellStr = row.getCell((short)cCount).toString().trim();
                 		String fName = factorNames.get(cCount-1);
                 		
                 		if (StringUtils.isNullOrEmpty(cellStr))
                 	      	throw new SampleSheetIOException("Missing value for factor " + fName, rowCount, sheetName);
                        
             			if (cellStr.length() > FactorLevel.FACTOR_VALUE_FIELD_LEN) 
             				{
             				String msg = "Value for factor name " + fName + " ( " + cellStr + ") can't be longer than " + FactorLevel.FACTOR_VALUE_FIELD_LEN + " characters"; 
             				throw new SampleSheetIOException(msg, rowCount, sheetName);
             				}
                    	factor_map.get(fName).add(cellStr);
                  		}
             	
             	String sid = samples.get(rowCount-12).getSampleID();
             	for (int cCount = fCellCount+1; cCount <= fCellCount + aCellCount; cCount++)
             		{
             		Cell cell = row.getCell((short)cCount);
             		if (cell == null)  break;
             		
         			String cellStr = cell.toString().trim();
         			if (StringUtils.isNullOrEmpty(cellStr))  break;
             		
            		if (assay_map.get(sid).contains(cellStr))
            			throw new SampleSheetIOException("Duplicate assay (" + cellStr + ") for sample id " + sid, rowCount, sheetName);
            	
            		assay_map.get(sid).add(cellStr);
                	experiment_assays.put(cellStr, sid);
             		}
             	}
     		  }
         
     		String msg = doRequirementChecks(samples, expId, factor_map, factorNames, assay_map);
     		if (!msg.startsWith("OK")) 
     			throw new SampleSheetIOException(msg, rowCount, sheetName);
     		
     		try
     			{
     			if (experiment_assays != null)
     				data = new Mrc2SubmissionSheetData(expId, factor_map, assay_map, samples, factorNames, upload, experiment_assays);
     			}
     		catch (Exception e) { } 
     		}
 	
		
		private String doRequirementChecks(List<SampleDTO> samples, String expID, Map<String,  List<String>> factor_map, List<String> factors, 
			Map<String, List<String>> assay_map)
			{
			String msg  = "OK";
			
			for (int f = 0; f < factors.size(); f++)
		    	if (samples.size()!=factor_map.get(factors.get(f)).size())
		    		return "Please check column " + (f+1) + " in sheet "+sheetNum + " to make sure factor information has been provided for all samples being submitted";
		    	
		    for(int s = 0; s < samples.size();s++)
		    	if(assay_map.get(samples.get(s).getSampleID()).isEmpty())
		    		return  "Error in row "+(s+12)+" of sheet "+ sheetName +". At least 1 assay should be selected for all samples being submitted for " + samples.get(s).getSampleID();
		    
		    return msg;
			}
		}



////////////// SCRAP CODE ////////////////

/*	
private void printValues(String expId, Map<String, List<String>> factor_map, Map<String, List<String>> assay_map, 
		List <SampleDTO> samples, List<String> factors, FileUpload upload, Map<String, String> experiment_assays)
	{
	System.out.println("Number of factors is " + factors.size() + " and number of experiment assays is " + experiment_assays.size());

	for  (int i = 0; i < factors.size(); i++)
		System.out.println("Factor " + i + " is " + factors.get(i));

	for (String key : experiment_assays.keySet())
		{
		System.out.println("Experiment assay for " + key + " is " + experiment_assays.get(key));
		}
	
	for (String key: factor_map.keySet())
		{
		List<String> values = factor_map.get(key);
		System.out.println("Values for key " + key + " are : ");
		for (int j = 0; j < values.size(); j++)	
			System.out.println("Value " + j + " is " + values.get(j));
		}
	
	for (String key: assay_map.keySet())
		{
		List<String> values = assay_map.get(key);
		System.out.println("Values for key " + key + " are : ");
		for (int j = 0; j < values.size(); j++)	
			System.out.println("Value " + j + " is " + values.get(j));
		}
	} */
	

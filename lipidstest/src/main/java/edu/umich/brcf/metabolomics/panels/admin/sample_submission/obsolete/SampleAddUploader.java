////////////////////////////////////////////////////
// SampleAddUploader.java
// Written by Jan Wigginton, Jul 11, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.admin.sample_submission.obsolete;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.file.Files;

import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.dto.SampleDTO;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.FactorService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.layers.service.SubjectService;
import edu.umich.brcf.shared.util.METWorksException;
import edu.umich.brcf.shared.util.io.StringUtils;


public class SampleAddUploader 
	{
	@SpringBean
	ExperimentService expService;
	
	@SpringBean
	SampleService sampleService;
	
	@SpringBean 
	FactorService factorService;
	
	@SpringBean
	SubjectService subjectService;
	
	String selectedExperiment;
	FileUpload upload;
	File newFile;
	
	int sheetNum, rowCount;
	
	public SampleAddUploader(String eid, File file, FileUpload upload)
		{
		Injector.get().inject(this);
		
		selectedExperiment = eid;
		newFile = file;
		this.upload = upload;
		}
	
	public void readSamples()
		{
		}
	
	 
    private Workbook createWorkBook(File newFile,FileUpload upload) throws FileNotFoundException, IOException, InvalidFormatException
		{
		if(upload.getContentType().equalsIgnoreCase("application/vnd.ms-excel"))
			{
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(newFile));
			return new HSSFWorkbook(fs);
			}
		OPCPackage pkg = OPCPackage.open(newFile);
		return new XSSFWorkbook(pkg);
		}
            
   
    public void readSubmissionForm() throws METWorksException
		{
		try
    		{
			Workbook workbook = createWorkBook(newFile, upload);
			//selectedExperiment = readClientInfo(workbook);
			if (selectedExperiment != null)
				{
				List <SampleDTO> samples = readSampleInfo(workbook, selectedExperiment);
				readExperimentDesign(workbook, samples, selectedExperiment, upload);    
				}
			}
		catch (METWorksException e)
			{
			throw new METWorksException(e.getMetworksMessage());
			}
		catch (Exception e)
			{
			e.printStackTrace();
			String msg ="Unable to upload file, error in sheet "+sheetNum +" at line: "+rowCount;
		//	System.out.println(msg);
			throw new METWorksException(msg);
		    }
		finally { Files.remove(newFile); }
		}
	
    
		
    private String readClientInfo(Workbook workbook) throws METWorksException 
		{
    	sheetNum = 1;
    	rowCount = 0;
    	Sheet sheet = workbook.getSheetAt(0);
		Row row = sheet.getRow(11 + 2);
		
		Experiment exp;
		String expID = "";
		try 
			{
			expID = row.getCell((short) 1).toString().trim();
			exp = expService.loadById(expID);  
			}
		catch (NullPointerException e)
			{  	
			String msg = "File upload failed: Unable to read experiment id in client info sheet";
			System.out.println(msg);
			throw new METWorksException(msg);
			}
		catch(Exception ex)
			{
			String msg = "File upload failed: Experiment "+expID+" does not exist!";
			System.out.println(msg);
			throw new METWorksException(msg);
			}
	
		return expID;
		}
		
		
    /*
	private List <SampleDTO> readSampleInfo(Workbook workbook, String expID) throws METWorksException 
		{
		Sheet sheet = workbook.getSheetAt(1);
	    Iterator<Row> rows = sheet.rowIterator();
	    sheetNum = 2; rowCount = 0; 
		
	    List<SampleDTO> samples=new ArrayList<SampleDTO>();
	    
	    int idx = 0;
	    while (rows.hasNext())
	    	{
	        Row row = rows.next();
	        ++rowCount;
	        if (rowCount>3)
	        	{
	        	String sid = row.getCell((short)0).toString().trim();
	        	  
	        	System.out.println("Sid is " + sid);
	        	if( row.getCell((short) 0)==null || sid==null || sid.trim().length()==0)
	        		break;

	        	try	
	        		{
	        		System.out.println("Sample id is " + sid);
	        		Sample thisSample = sampleService.loadSampleAlongWithExpById(sid);
	        	
	        		if(thisSample!=null)
	            		{
	        			String msg = "Unable to upload file, error in sheet "+sheetNum +" at line: "+rowCount +  ". Sample already exists in the database.";
	        			System.out.println("The sample name is " + thisSample.getSampleID()  + " " + thisSample.getSampleName());	
	        			System.out.println(msg);
	        			throw new METWorksException(msg);
	            		}
	        		samples.add(readInSampleRow(row, expID, sid, idx + 1));
	        		}
	        	catch (METWorksException e)
	        		{
	        		throw new METWorksException(e.getMetworksMessage());
	        		}
	        	catch(Exception ee)
	        		{  
	        		ee.printStackTrace(); 
	        		throw new METWorksException("Error while reading sample sheet -- please check for missing or incorrect values."); 
	        		}
	        	
	        	idx++;
	        	}
	    	}
	    return samples;
		} */	
	    
	private List <SampleDTO> readSampleInfo(Workbook workbook, String expID) throws METWorksException 
	{
	Sheet sheet = workbook.getSheetAt(1);
    Iterator<Row> rows = sheet.rowIterator();
    sheetNum = 2; rowCount = 0; 
	
    List<SampleDTO> samples=new ArrayList<SampleDTO>();
    
    int idx = 0;
    while (rows.hasNext())
    	{
        Row row = rows.next();
        ++rowCount;
        if (rowCount>3)
        	{
        	String sid = row.getCell((short)0).toString().trim();
        	  
        	System.out.println("Sid is " + sid);
        	if( row.getCell((short) 0)==null || sid==null || sid.trim().length()==0)
        		break;

        	try	
        		{
        		System.out.println("Sample id is " + sid);
        		Sample thisSample = sampleService.loadSampleAlongWithExpById(sid);
        	
        		if(thisSample!=null)
            		{
        			String msg = "Unable to upload file, error in sheet "+sheetNum +" at line: "+rowCount +  ". Sample already exists in the database.";
        			System.out.println("The sample name is " + thisSample.getSampleID()  + " " + thisSample.getSampleName());	
        			System.out.println(msg);
        			throw new METWorksException(msg);
            		}
        		samples.add(readInSampleRow(row, expID, sid, idx + 1));
        		}
        	catch (METWorksException e)
        		{
        		throw new METWorksException(e.getMetworksMessage());
        		}
        	catch(Exception ee)
        		{  
        		ee.printStackTrace(); 
        		throw new METWorksException("Error while reading sample sheet -- please check for missing or incorrect values."); 
        		}
        	
        	idx++;
        	}
    	}
    return samples;
	}	
    
private SampleDTO readInSampleRow(Row row, String expID, String sid, Integer idx) throws METWorksException
	{
	SampleDTO sdto=new SampleDTO();
	try
		{
		sdto.setExpID(expID);
		sdto.setSampleID(row.getCell((short)0).toString().trim());
		System.out.println("Id is " + sdto.getSampleID());
		  
		String cellStr = row.getCell((short)1).toString().trim();
		boolean cellEmpty =  (StringUtils.isEmpty(cellStr));
		sdto.setSampleName(cellEmpty ? idx.toString() : cellStr);
	    
	    System.out.println("Sample Name is " + sdto.getSampleName());
	    
	    sdto.setSubjectId(row.getCell((short)2).toString().trim());
	    System.out.println("Subject id is " + sdto.getSubjectId());
	    
	    sdto.setUserDefSampleType(row.getCell((short)3).toString().trim());
	    System.out.println("User defined sample type is " + sdto.getUserDefSampleType());
	    
	    
	    sdto.setUserDefGOS(row.getCell((short)4).toString().trim());
	    System.out.println("User defined GOS " + sdto.getUserDefGOS());
	    
	    String volString = row.getCell((short) 5).toString().replace('~', ' ').trim();
	    System.out.println("Volume is " + volString);
	    sdto.setVolume(new BigDecimal(volString));
		
	    sdto.setVolUnits(row.getCell((short)6).toString().trim());
	    System.out.println("Vol units is " + sdto.getVolUnits());
	    
		sdto.setSampleTypeId(row.getCell((short)7).toString().trim());
		System.out.println("Sample type id is " + sdto.getSampleTypeId());
	    
		String strCel= row.getCell((short)8).toString();
		System.out.println("Strcel is " + strCel);
		
		if(strCel.indexOf(".")>=0)
			sdto.setGenusOrSpeciesID(Long.valueOf(strCel.substring(0, strCel.indexOf("."))));
		else
			sdto.setGenusOrSpeciesID(Long.valueOf(strCel));
	
		sdto.setLocID(row.getCell((short)9).toString().trim());
		System.out.println("Location id is " + sdto.getLocID());
	    
		return sdto;
		}
	catch (Exception e)
		{
		e.printStackTrace();
		String sampleId = (sdto == null  ? "" : sdto.getSampleID());
		String msg = "Error while reading sample information"; 
		if (!StringUtils.isEmpty(sampleId))
			msg +=  (" for sample id " + sampleId);
		System.out.println(msg);
		throw new METWorksException(msg);
		}
	}



private void readExperimentDesign(Workbook workbook, List <SampleDTO> samples, String expId, FileUpload upload) throws METWorksException
	{
	Sheet sheet = workbook.getSheetAt(2);
	Row row;
	
	sheetNum = 3;
	rowCount = 0;
    
    System.out.println("Reading experiment design.  Number of sample dtos" + samples.size());
    Map<String, List<String>> factor_map = new HashMap<String, List<String>>();
    Map<String, List<String>> assay_map = new HashMap<String, List<String>>();
    Map<String, String> experiment_assays = new HashMap<String, String>();
    List<String> factors = new ArrayList<String>();
    
    int fCellCount=0, aCellCount=0, cellCount = 1;
    for(SampleDTO s : samples)
    	{
    	assay_map.put(s.getSampleID(), new ArrayList<String>());
    	System.out.println("SAMPLE DTO " + s.getExpID() + " " + s.getSampleID() + " " + s.getSampleName());
    	}

    try
    {
    Iterator<Row> rows = sheet.rowIterator();
	while (rows.hasNext())
    	{
        row = rows.next();
        ++rowCount;
    
        // Unable to upload
        if ((factors.size()==0)&&(rowCount==10))
        	{
        	System.out.println("Reading row 10");
    		while(fCellCount>=0)
    			{
    			String cellStr = row.getCell((short)cellCount++).toString().trim();
    			
    			//System.out.println("Counting factors and assays " + cellStr == null ? "null" : cellStr);
    			if (cellStr == null || cellStr.equals(null) ||cellStr.isEmpty())
    				break;

    			if(cellStr.startsWith("Factor"))
    				fCellCount++;
    			
    			else if(cellStr.startsWith("Assay"))
    				aCellCount++;
    			}
        	}
    	else if (factors.size()==0 && rowCount==11)
        	{
        	System.out.println("Reading row factor names");

        	int fCount=2;
        	while (fCount < fCellCount+2)
        		{
	        	String factorName = row.getCell((short)fCount++).toString().trim();
	        	if(factorName.equals(null) || factorName.trim().isEmpty())
	        		break;
	        	if (!factorName.equals("<Enter Name>"))
	        		{
	        		factors.add(factorName);
	        		//System.out.println("Adding Factor *" + factorName + "*");
	        		}
	        	}
        	
        	System.out.println("There are " + factors.size() + " factors");
        	if (factors.size()>0)
        		for (int f = 0; f < factors.size(); f++)
        			factor_map.put(factors.get(f), new ArrayList<String>());
        	}

    	else if (rowCount > 11 && (rowCount < samples.size() + 12))
        	{
        	System.out.println("Reading row " + rowCount);

        	if(factors.size() > 0)
        		{
            	for(int cCount=2; cCount<factors.size()+2;cCount++)
            		{
            		String cellStr = row.getCell((short)cCount).toString().trim();
            		
                	if (cellStr == null || cellStr.isEmpty())
                		continue;
                	
            		System.out.println("Factor value is "+cellStr + " for factor " + factors.get(cCount - 2));
                	factor_map.get(factors.get(cCount-2)).add(cellStr);
                	}
        		}
        	
        	for(int cCount = fCellCount+2; cCount < fCellCount+aCellCount+2; cCount++)
        		{
        		Cell cell = row.getCell((short)cCount);
        		if (cell == null)  break;
        		
        		String cellStr = cell.toString().trim();
        		
        		if (cellStr == null || cellStr.trim().isEmpty())  break;
            	
        		String sid = samples.get(rowCount-12).getSampleID();
                assay_map.get(sid).add(cellStr);
                System.out.println("Adding to assay map. Id is " + sid + " Assay " + cellStr);
                experiment_assays.put(cellStr, sid);
        		}
        	}
        
        else if(rowCount >= samples.size()+12)
        	break;
    	}

	//List <ControlDTO> controls  = createControlDtos(expId, assay_map);
		addSamplesAndControls(expId, factor_map, assay_map, samples, factors, upload, experiment_assays);
    	}
    catch (METWorksException e) 
    	{ 
    	throw new METWorksException(e.getMetworksMessage()); 
    	}
    catch (Exception e) 
    	{ 
    	throw new METWorksException(e.getMessage()); 
    	}
    }
	
	
	private void addSamplesAndControls(String expId, Map<String, List<String>> factor_map, Map<String, List<String>> assay_map, 
			List <SampleDTO> samples, List<String> factors, FileUpload upload, Map<String, String> experiment_assays) throws METWorksException 
		{
		printValues(expId, factor_map, assay_map, samples, factors, upload, experiment_assays);
		int sampleCount = 0;
	    String msg = doRequirementChecks(samples, expId, factor_map, factors, assay_map);
	    
	    if (msg.startsWith("OK"))
	    	{
	    	//sampleCount =  sampleService.saveAdditionalSamples(expId, samples, factor_map, assay_map, experiment_assays);
	    	System.out.println("All error checks were passed");
	    	msg = ("Saved "+sampleCount+" samples from File: " + upload.getClientFileName());
	    	}
	    System.out.println(msg);
			//System.out.println("No samples were saved. Sample IDs cannot be duplicated, Please check values in the uploaded file - " + upload.getClientFileName());
		throw new METWorksException(msg);
		}
			
		
	private void printValues(String expId, Map<String, List<String>> factor_map, Map<String, List<String>> assay_map, 
			List <SampleDTO> samples, List<String> factors, FileUpload upload, Map<String, String> experiment_assays)
			{
		
		System.out.println("Number of factors is " + factors.size() + " and number of experiment assays is " + experiment_assays.size());

		for  (int i = 0; i < factors.size(); i++)
			{
			System.out.println("Factor " + i + " is " + factors.get(i));
			}

		Set <String>aKeys = experiment_assays.keySet();
		for (String key : aKeys)
			{
			System.out.println("Experiment assay for " + key + " is " + experiment_assays.get(key));
			}
		
		Set<String> keys = factor_map.keySet();
		for (String key: keys)
			{
			List<String> values = factor_map.get(key);
			System.out.println("Values for key " + key + " are : ");
			for (int j = 0; j < values.size(); j++)	
				System.out.println("Value " + j + " is " + values.get(j));
			}
		
		Set<String> keys2 = assay_map.keySet();
		for (String key: keys2)
			{
			List<String> values = assay_map.get(key);
			System.out.println("Values for key " + key + " are : ");
			for (int j = 0; j < values.size(); j++)	
				System.out.println("Value " + j + " is " + values.get(j));
			}
		
		
	}
	
	public String doRequirementChecks(List<SampleDTO> samples, String expID, Map<String, 
			List<String>> factor_map, List<String> factors, Map<String, List<String>> assay_map)
		{
		String msg  = "OK";
		
		for (int f = 0; f < factors.size(); f++)
	    	if (samples.size()!=factor_map.get(factors.get(f)).size())
	    		{
	    		msg = "Please check column " + (f+1) + " in sheet "+sheetNum
	    				+ " to make sure factor information has been provided for all samples being submitted";
	    		System.out.println(msg);
	    		return msg;
	    		}
		
	    for(int s = 0; s < samples.size();s++)
	    	{
	    	if(assay_map.get(samples.get(s).getSampleID()).isEmpty())
	    		{
	    		msg = "Error in row "+(s+12)+" of sheet "+sheetNum+". At least 1 assay should be selected for all samples being submitted for" + samples.get(s).getSampleID();
	    		System.out.println(msg);
	    		return msg;
	    		}
	    	}
		
		if (!factorService.areFactorsForExpId(expID, factor_map.keySet()))
			return "Some factors you are trying to add are not recorded for experiment " + expID + 
					"." + System.getProperty("line.separator") + "Any factors listed for new samples must already be added (in the submission sheet) "
					+ " or using the Add Factor panel";
					
		
		 if (!factorService.allFactorsPresent(expID, factor_map.keySet()))
			 return "All experimental factors must be accounted for when adding samples.";
			
		// if (!verifySubjectIds(expID, samples))
		//	 return "Subject Ids should be left blank or assigned to an id already listed for this experiment.";

		return msg; 
		}
	
	/*
	
	private Boolean verifySubjectIds(String expId, List<SampleDTO> dtos)
		{
		List <String> subjectIds = new ArrayList<String>();
		
		for (SampleDTO dto : dtos)
			if (StringUtils.isNonEmpty(dto.getSubjectId()))
				subjectIds.add(dto.getSubjectId());
		
		return subjectService.areSubjectsForExpId(expId, subjectIds);
		} */
	}

////////////////////////////////////SCRAP CODE/////////////////////

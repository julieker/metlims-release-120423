////////////////////////////////////////////////////
// Mrc2TransitionalSamplesMetadataReader.java
// Written by Jan Wigginton, Jun 15, 2017
// updated by Julie Keros jan 11 2021
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.sheetreaders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.dto.SampleDTO;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.FieldLengths;
import edu.umich.brcf.shared.util.SampleSheetIOException;
import edu.umich.brcf.shared.util.datacollectors.Mrc2SamplesMetadata; // JAK change 1
import edu.umich.brcf.shared.util.utilpackages.NumberUtils;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;

public class Mrc2TransitionalSamplesMetadataReader
	{
	@SpringBean
	SampleService sampleService;
	public static final int mrc2SampleIdColumnNo = 0;
	public static final String mrc2SampleIdColumnHeader = "Sample ID";
	private int rowIdx = -1,  rowCount;
	private final String sheetName = "Samples Metadata";
	// Used for add mode (next stage)
	private List<String> sampleReadOrder;
	private Map<String, String> sampleIdMappings, reverseSampleIdMappings;
	private Map<String, String> idsInSheet, idsInDatabase;
	Map<String, Integer> colLengthMap;
	List <Integer> fLengths;
	List <String> sampleColNames = Arrays.asList( new String [] {"Sample ID", "Researcher_Sample_ID", "Researcher_Subject_ID", "Sample Type", "GenusOrSpecies",
          "Volume", "Units", "Sample Type ID", "GenusOrSpecies ID", "Loc ID"});
	List<String> dropDownCols =  Arrays.asList(new String [] { "Sample Type", "GenusOrSpecies"});
	
	public Mrc2TransitionalSamplesMetadataReader() 
		{
		Injector.get().inject(this);
		} 

	public Mrc2SamplesMetadata read(Sheet sheet, String expId) throws SampleSheetIOException
		{ // JAK change 2		
		colLengthMap = initializeColLengthMap();
		idsInDatabase = sampleService.sampleIdMapForExpId(expId);
		idsInSheet = new HashMap<String, String>();		
		Mrc2SamplesMetadata samplesMetadata = null; 
		try 
			{
			List <SampleDTO> samples = readSampleInfo(sheet, expId);
			sampleReadOrder = new ArrayList<String>();
			sampleIdMappings = new HashMap<String, String>();
			reverseSampleIdMappings = new HashMap<String, String>();			
			for (int i = 0; i < samples.size(); i++)
				{
				sampleReadOrder.add(samples.get(i).getSampleID());
				sampleIdMappings.put(samples.get(i).getSampleID(), samples.get(i).getResearcherSampleId());
				reverseSampleIdMappings.put(samples.get(i).getResearcherSampleId(), samples.get(i).getSampleID());
				}
			samplesMetadata = new Mrc2SamplesMetadata(samples);
			}
		  catch (SampleSheetIOException e) { throw e; }
		  catch (Exception e)
			{ 
			e.printStackTrace(); 
			throw new SampleSheetIOException("Unspecified error while reading sample sheet", rowIdx, sheetName);
			}
		return samplesMetadata;
		}
	
	// JAK change 3
	private Map<String, Integer> initializeColLengthMap()
		{
		fLengths = Arrays.asList(new Integer[] { 
			  FieldLengths.MRC2_SAMPLE_ID_LENGTH, 
			  FieldLengths.MRC2_RESEARCHER_SAMPLE_ID_LENGTH, 
			  FieldLengths.MRC2_RESEARCHER_SUBJECT_ID_LENGTH,
			  FieldLengths.MRC2_SAMPLE_TYPE_LENGTH, 
			  FieldLengths.MRC2_GENUS_OR_SPECIES_LENGTH,
			  FieldLengths.MRC2_VOLUME_LENGTH, 
			  FieldLengths.MRC2_VOLUME_UNITS_LENGTH, 
			  FieldLengths.MRC2_SAMPLE_TYPE_ID_LENGTH,
			  FieldLengths.MRC2_GENUS_OR_SPECIES_ID_LENGTH,
			  FieldLengths.MRC2_LOCATION_ID_LENGTH}); 	
		int i = 0; 
		 Map<String, Integer> map = new HashMap<String, Integer>();
		 for (String name : sampleColNames)
			map.put(name, fLengths.get(i++));	
		return map;
		}
	  
	 private List <SampleDTO> readSampleInfo(Sheet sheet,  String expID) throws SampleSheetIOException 
		{
		rowCount=0;		
	    int firstSampleRow = 3, lastSampleRow = sheet.getLastRowNum()+1;
	    List<SampleDTO> samples=new ArrayList<SampleDTO>();	  
	    for (int j = firstSampleRow; j < lastSampleRow; j++)
	    	{
	        List<String> tokens = readLine(j, sheet);	        
	        if (isSampleRowBlank(tokens))
	              break;	
	      	String sid = tokens.get(Mrc2TransitionalSamplesMetadataReader.mrc2SampleIdColumnNo);
	       	if(sid != null && sid.length() != 9)
	      		throw new SampleSheetIOException("Unable to upload file,  sample id " + sid + " is not a valid (9 character) metabolomics sample id", rowCount, sheetName);	  	
	       	screenForDatabaseDuplicates(sid);
	       	screenForSheetDuplicates(sid);
	      	screenForMissingInfo(tokens, sid);
	      	screenForSampleLengthViolations(tokens, sid);
	      	SampleDTO dto = readInSampleRowTokens(tokens, expID, sid);
	      	samples.add(dto);
	      	}
	    return samples;
		}
	 
	 private List<String> readLine(int rowIdx, Sheet sheet)
	 	{
	 	Row row = sheet.getRow(rowIdx);
	 	FormulaEvaluator evaluator;
	 	String raw;
        // issue 109
	 	List<String> tokens = new ArrayList<String>();
        for (int i = 0; i < sampleColNames.size(); i++)
        	{
        	Cell cell = row.getCell(i);
        	evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();		
    		evaluator.evaluateInCell(cell);
    		DataFormatter objDefaultFormat = new DataFormatter();			
    		raw = objDefaultFormat.formatCellValue(cell);
	        tokens.add(raw == null ? "" : raw.trim());
        	}
        return tokens;
	 	}
	  
	 // Need to check for both blank and "please select or describe" in the dropdown fields
	 private boolean isSampleRowBlank(List<String> tokens)
		 {
		 for (int i = 0; i < sampleColNames.size(); i++)
			 {
			 String val = tokens.get(i);
			 String headerName = sampleColNames.get(i).trim();			 
			 if (dropDownCols.contains(headerName) && val.contains("Please select or describe---"))
				 continue;		 
			 if (!StringUtils.isNullOrEmpty(val))
				 return false;
			 }		 
		 return true;
		 }
	  
	 private void screenForMissingInfo(List<String> tokens, String sid) throws SampleSheetIOException
		 {
		 for (int i = 0; i < sampleColNames.size(); i++)
			 {
			 String val = tokens.get(i);
			 String headerName = sampleColNames.get(i).trim();				
			 // column species && st can be unselected (for now) -- keep check in place in case this changes
			 if (dropDownCols.contains(headerName) && val.contains("Please select or describe---"))
				 continue;			
			 boolean isOrdinaryMissing = StringUtils.isNullOrEmpty(val);
			 boolean isNonStandardMissing = (dropDownCols.contains(headerName) && !isOrdinaryMissing && val.startsWith("---"));			 
			 if (isOrdinaryMissing || isNonStandardMissing)
				 throw new SampleSheetIOException("Unable to load sample " + sid  + ". Value for column " + sampleColNames.get(i) + " is missing", rowCount, sheetName);
			 }
		 }
	 
	private void screenForSheetDuplicates(String sid) throws SampleSheetIOException
		{
	   	if (idsInSheet.containsKey(sid))
	   		{
	   		System.out.println("Sample ID" + sid + " already exists.");
	   		throw new SampleSheetIOException("Unable to upload file, sample: "  + sid + " is duplicated ", rowCount, sheetName);
	   		}
	   	
	   	idsInSheet.put(sid,  null);
		}
	
	private void screenForDatabaseDuplicates(String sid) throws SampleSheetIOException
		{
		if (idsInDatabase.containsKey(sid))
	   		throw new SampleSheetIOException("Unable to upload file,  sample " + sid + " already exists in the database.", rowCount, sheetName);
		}
	 	      
	  private void  screenForSampleLengthViolations(List<String> tokens, String sid) throws SampleSheetIOException
		  {
		  for (int i = 0; i < sampleColNames.size(); i++) 
	    	  {
	    	  String val = tokens.get(i);
	    	  String headerName = sampleColNames.get(i);	    	 
	    	  if ("Volume".equals(headerName))
	    		  {    		  
	    		  if (val.startsWith("-"))
	    			  {
	    			  String msg = "Cannot load sample " + sid + ". Volume must be a positive number.";
	    			  throw new SampleSheetIOException(msg, rowCount, sheetName);
	    			  }	    	   
	    		  if (!NumberUtils.verifyDecimalRange(val, 13, 9))
	    			  {
	    			  String msg = "Cannot load sample " + sid + ". " + " volume " + val + " is an illegal value";
	    			  throw new SampleSheetIOException(msg, rowCount, sheetName);
	    			  }	    	     
	    		 continue;
	    	     }    	 
	    	 if (val.length() > fLengths.get(i) && i != 5 )
	    		  {
	    		  String msg = "Cannot load sample " + sid + ". Column '" + headerName + "'with value " +  val + " cannot be longer than " + fLengths.get(i) + " characters";
	    		  throw new SampleSheetIOException(msg, rowCount, sheetName);
	    		  }
	    	  }
	      }
	    
	  private SampleDTO readInSampleRowTokens(List<String> tokens, String expID, String sid) throws SampleSheetIOException
		  {
		  SampleDTO sdto=new SampleDTO();
		  sdto.setExpID(expID);			
		  try
		      {
			  for(int i = 0; i < tokens.size(); i++)
				  setValueForHeaderTag(tokens.get(i), sampleColNames.get(i), sdto);
			  }
		  catch (Exception e)
			  {
			  String msg = "Error while reading sample information " + (StringUtils.isEmptyOrNull(sid) ? "" : "for sample id " + sid); 
			  e.printStackTrace();
			  throw new SampleSheetIOException(msg, rowCount, sheetName);
			  }
		  return sdto;
		  }
	  
	  private void setValueForHeaderTag(String value, String tag, SampleDTO dto)
		  {
		  if (dropDownCols.contains(tag) && (value.contains("Please select or describe---") || value.startsWith("---")))
				 return;		
		  tag = tag.toLowerCase();
		  tag = StringUtils.cleanAndTrim(tag);
		  switch (tag)
		  	{
	        case "sampleid" :  dto.setSampleID(value); break;
            case "researchersampleid" : dto.setResearcherSampleId(value); dto.setSampleName(value); break;
            case "researchersubjectid" : dto.setResearcherSubjectId(value); dto.setSubjectId(value); break;
            case "sampletype" : dto.setUserDefSampleType(value);break;
            case "genusorspecies" : dto.setUserDefGOS(value);  break;
            case "volume" : dto.setVolume(new BigDecimal(value)); break;
            case "units" : dto.setVolUnits(value); break;
            case "sampletypeid" : dto.setSampleTypeId(value); break;
            case "genusorspeciesid" : 
            	if(value.indexOf(".")>=0)
		      		dto.setGenusOrSpeciesID(Long.valueOf(value.substring(0, value.indexOf("."))));
		      	else
		      		dto.setGenusOrSpeciesID(Long.valueOf(value));
	            break;
	            
            case "locid" : dto.setLocID(value); break;
            default : break;
		  	}
		} 
	
	public List<String> getSampleReadOrder()
		{
		return sampleReadOrder;
		}

	public void setSampleReadOrder(List<String> sampleReadOrder)
		{
		this.sampleReadOrder = sampleReadOrder;
		}
	
	public Map<String, String> getSampleIdMapping()
		{
		return sampleIdMappings;
		}
	
	public Map<String, String> getReverseSampleIdMapping()
		{
		return sampleIdMappings;
		}
	
	public Boolean idsMapOneToOne()
		{
		return sampleIdMappings.keySet().size() == reverseSampleIdMappings.keySet().size();
		}
	}

// JAK change 4


////////////////////////// Scrap Code and spreadsheet reader changes //////////////////

/*

LINE 24 change 1 change Mrc2TransitionalSamplesMetadata to Mrc2SamplesMetadata
LINE 61 change 2 change read method from 
---->
public Mrc2TransitionalSamplesMetadata read(Sheet sheet, String expId) throws SampleSheetIOException
		{
		colLengthMap = initializeColLengthMap();
		idsInDatabase = sampleService.sampleIdMapForExpId(expId);
		idsInSheet = new HashMap<String, String>();
		
		if (!addMode && (idsInDatabase != null &&  idsInDatabase.keySet().size() >  0))	
			throw new SampleSheetIOException("Unable to upload file :  " + idsInDatabase.size() + " samples have already been registered for experiment " + expId + ". To add samples to an experiment please use the add uploader at the bottom of the database tools page"
					, rowIdx, sheetName);
	
		Mrc2TransitionalSamplesMetadata samplesMetadata = new Mrc2TransitionalSamplesMetadata() ; 
		try 
			{
			List <SampleDTO> samples = readSampleInfo(sheet, expId);
			initializeMappings(samples);
			samplesMetadata.setInfoFields(samples);
			}
		  catch (SampleSheetIOException e) { throw e; }
		  catch (Exception e)
			{ 
			e.printStackTrace(); 
			throw new SampleSheetIOException("Unspecified error while reading sample sheet", rowIdx, sheetName);
			}
		
		return samplesMetadata;
		}
-->
to 
  ---->
  
       		public Mrc2SamplesMetadata read(Sheet sheet, String expId) throws SampleSheetIOException
		{
		colLengthMap = initializeColLengthMap();
		idsInDatabase = sampleService.sampleIdMapForExpId(expId);
		idsInSheet = new HashMap<String, String>();
		
		Mrc2SamplesMetadata samplesMetadata = null; 
		try 
			{
			List <SampleDTO> samples = readSampleInfo(sheet, expId);
			sampleReadOrder = new ArrayList<String>();
			sampleIdMappings = new HashMap<String, String>();
			reverseSampleIdMappings = new HashMap<String, String>();
		
			
			for (int i = 0; i < samples.size(); i++)
				{
				sampleReadOrder.add(samples.get(i).getSampleID());
				sampleIdMappings.put(samples.get(i).getSampleID(), samples.get(i).getResearcherSampleId());
				reverseSampleIdMappings.put(samples.get(i).getResearcherSampleId(), samples.get(i).getSampleID());
				}
		//	System.out.println("Here is samples get id:" + samples.get(522));
			samplesMetadata = new Mrc2SamplesMetadata(samples);
			}
		  catch (SampleSheetIOException e) { throw e; }
		  catch (Exception e)
			{ 
			e.printStackTrace(); 
			throw new SampleSheetIOException("Unspecified error while reading sample sheet", rowIdx, sheetName);
			}
		
		return samplesMetadata;
		}
	
LINE 95 change 3 remove
private void initializeMappings(List<SampleDTO> samples)
		{
		sampleReadOrder = new ArrayList<String>();
		sampleIdMappings = new HashMap<String, String>();
		reverseSampleIdMappings = new HashMap<String, String>();
		
		for (int i = 0; i < samples.size(); i++)
			{
			sampleReadOrder.add(samples.get(i).getSampleID());
			sampleIdMappings.put(samples.get(i).getSampleID(), samples.get(i).getResearcherSampleId());
			reverseSampleIdMappings.put(samples.get(i).getResearcherSampleId(), samples.get(i).getSampleID());
			}
		}


LINE 331 change 4 remove
	public Boolean getAddMode()
		{
		return addMode;
		}


	public String getExpectedExperimentId()
		{
		return expectedExperimentId;
		}


	public void setAddMode(Boolean addMode)
		{
		this.addMode = addMode;
		}


	public void setExpectedExperimentId(String expectedExperimentId)
		{
		this.expectedExperimentId = expectedExperimentId;
		}
		

/////////////////////////   SCRAP CODE ////////////////////////////////////////////////////


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
*/

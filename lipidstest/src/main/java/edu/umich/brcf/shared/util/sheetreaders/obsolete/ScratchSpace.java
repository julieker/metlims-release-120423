////////////////////////////////////////////////////
// ScratchSpace.java
// Written by Jan Wigginton, Jun 20, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.sheetreaders.obsolete;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;



//import edu.umich.brcf.epigenetics.panels.hub.registration.sampletools.datacollectors.EpiSampleInfoItem;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.dto.SampleDTO;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.FieldLengths;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.SampleSheetIOException;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.datacollectors.Mrc2SamplesMetadata;
import edu.umich.brcf.shared.util.datacollectors.Mrc2TransitionalExperimentDesignItem;
import edu.umich.brcf.shared.util.io.SpreadSheetReader;
import edu.umich.brcf.shared.util.utilpackages.ListUtils;
import edu.umich.brcf.shared.util.utilpackages.NumberUtils;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;

public class ScratchSpace extends SpreadSheetReader
{
@SpringBean
SampleService sampleService;

public static final int mrc2SampleIdColumnNo = 0;
public static final String mrc2SampleIdColumnHeader = "Sample ID";

private int rowIdx = -1,  rowCount;
private final String sheetName = "Samples Metadata";

// Used for add mode (next stage)
private Boolean addMode = false;
private String supplementalExperimentId = null, expectedExperimentId = null;

List<String> sampleList;

private List<String> sampleReadOrder, loadedSampleIds;
private Map<String, String> sampleIdMappings, reverseSampleIdMappings;

Map<String, Integer> colLengthMap;
List <Integer> fLengths;
List <String> sampleColNames = Arrays.asList( new String [] {"Sample Id", "Researcher Sample Id", "Researcher Subject Id", "Sample Type", "Genus or Species",
      "Volume", "Units", "Sample Type Id", "Genus or Species Id", "Location Id"});
Map<String, String> dropDownCols;

public ScratchSpace() 
	{
	Injector.get().inject(this);
	} 


public Mrc2SamplesMetadata read(Sheet sheet, String expId) throws SampleSheetIOException
	{
	colLengthMap = initializeColLengthMap();
	loadedSampleIds = new ArrayList<String>();
	
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

/*
 * 
 * 	List<String> sampleIdsInDatabase = epiSampleService.sampleIdsForExpId(sheetExpId);
			if (!addMode && ListUtils.isNonEmpty(sampleIdsInDatabase))	
				throw new SampleSheetIOException("Unable to upload file :  " + sampleIdsInDatabase.size() + " samples have already been registered for experiment " + sheetExpId + ". To add samples to an experiment please use the add uploader at the bottom of the database tools page"
						, rowIdx, sheetName);
				
			Map<String, String> nonRequiredColsWithValues = new HashMap<String, String>();
			
			int speciesCol = headerNames.indexOf("Species");
			Map<String, String> mixPermissableCols = new HashMap<String, String>();
			for (int i = speciesCol + 1; i < headerNames.size(); i++)
				mixPermissableCols.put(headerNames.get(i), null);
			
			rowCt = 8;
			int end = sheet.getPhysicalNumberOfRows(), start = rowCt;
			Row headers = sheet.getRow(7); 
			Boolean firstRowBlank = false;*/


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
	List<String> sampleIdsInDatabase = sampleService.sampleIdsForExpId(expID);
	if (!addMode && ListUtils.isNonEmpty(sampleIdsInDatabase))	
		throw new SampleSheetIOException("Unable to upload file :  " + sampleIdsInDatabase.size() + " samples have already been registered for experiment " + expID + ". To add samples to an experiment please use the add uploader at the bottom of the database tools page"
				, rowIdx, sheetName);
	
	Map<String, String> dropDownCols = new HashMap<String, String>();
	dropDownCols.put("Sample Type", null);
	dropDownCols.put("Genus or Species", null);
	
	int startRowNumSheet = findStartingRowNum(sheet);	
	//int endRowNum = sheet.getPhysicalNumberOfRows();
	int endRowNum = sheet.getLastRowNum();
	
	rowCount=0;
    Iterator<Row> rows = sheet.rowIterator ();
    String strCel = null;
    SampleDTO sdto;
    
    List<SampleDTO> samples=new ArrayList<SampleDTO>();
    Row headers = sheet.getRow(startRowNumSheet - 1);
    
    for (rowCount = startRowNumSheet; rowCount < endRowNum; rowCount++)
  //  while (rows.hasNext())
    	{
    //    Row row = rows.next();
        Row row = sheet.getRow(rowCount); 
     //   if (++rowCount < 4)
     //   	continue;
        
        if (this.isSampleRowBlank(row))
        	break;

      	//String sid = row.getCell((short)0).toString().trim();
	    Cell cell  = row.getCell(ScratchSpace.mrc2SampleIdColumnNo);
		String sid = cell.getCellType() == 1 ? cell.getStringCellValue() : cell.toString();

      
		if (!FormatVerifier.verifyFormat(Sample.idFormat, sid))
      		throw new SampleSheetIOException("Unable to upload file,  sample id " + sid + " is not a valid (9 character) metabolomics sample id", rowCount, sheetName);
  	
      //	doMissingInfoCheck(row, sid, dropDownCols);
      	screenForDatabaseDuplicates(sid, sampleIdsInDatabase);
      	screenForSheetDuplicates(sid);
      	
      //	doCheckForSampleLengthViolations(row, sid);
      	
      	
      	
      //	try { samples.add(readInSampleRow(sheet, rowIdx, headers, expID, sid)); }
     // 	catch (Exception e)  { throw new SampleSheetIOException(e.getMessage(), rowCount, sheetName); }
      	}

    return samples;
	}
 
 /*
SampleDTO readInSampleRow(Sheet sheet, int rowIdx, Row headers, String expID, String sid)
	{
	SampleDTO dto = new SampleDTO();
	
	for (Cell header : headers) 
		{
		if (isCellEmpty(header)) break;
		String value = getDataAt(rowIdx, header.getColumnIndex(), sheet, false);
	//	String headerName = screenForMissingness(header, value,sid, rowIdx, mixPermissableCols,nonRequiredColsWithValues);
	
		
	//	String value = getDataAt(rowIdx, header.getColumnIndex(), sheet, false);
		String headerName = header.getStringCellValue().trim();
		
//		if ("Researcher Subject ID".equals(headerName))
//			if (StringUtils.isEmptyOrNull(value))
//				value = itemsRead.toString();
				
		if (this.dropDownCols.containsKey(headerName) && value.startsWith("---"))
			throw new SampleSheetIOException("Unable to load sample " + sid  + ". Value for column " + headerName + " is missing", rowIdx, sheetName);
		
		if (value.length() > this.colLengthMap.get(headerName))
			throw new SampleSheetIOException("Unable to load sample " + sid  + ". Value for column " + headerName + " should not exceed " + colLengthMap.get(headerName) + " characters", rowIdx, sheetName);
		
	//	setValueForHeaderTag(headerName, value, info, sid, rowIdx);
		}

	}
 */
 
 /*
  * 	    EpiSampleInfoItem info = new EpiSampleInfoItem();
				for (Cell header : headers) 
					{
					if (isCellEmpty(header)) break;
					String value = getDataAt(rowIdx, header.getColumnIndex(), sheet, false);
					String headerName = screenForMissingness(header, value,sid, rowIdx, mixPermissableCols,nonRequiredColsWithValues);
					setValueForHeaderTag(headerName, value, info, sid, rowIdx);
					}
			    infoList.add(info);
				}
			
			return infoList;
			}
		catch (Exception e) { throw new SampleSheetIOException(e.getMessage(), rowIdx, sheetName); }
		} 
	
	
	private String screenForMissingness(Cell header, String value, String sid, int rowIdx, Map<String, String> mixableCols, Map<String, String> nonRequiredColsWithValues) throws SampleSheetIOException
		{
		String headerName = header.getStringCellValue().trim();
		
		if (header.getColumnIndex() != this.EPI_SAMPLE_ID_COL_IDX) 
			if (header.getColumnIndex() < this.EPI_END_MANDATORIES_COL_IDX ||  nonRequiredColsWithValues.containsKey(headerName))     
				if (mixableCols.get(headerName.toLowerCase()) ==  null)
					if (StringUtils.isEmptyOrNull(value))
						throw new SampleSheetIOException("Unable to load sample " + sid  + ". Value for column " + headerName + " is missing", rowIdx, sheetName);
			
		if (header.getColumnIndex() >= this.EPI_END_MANDATORIES_COL_IDX && !StringUtils.isEmptyOrNull(value))
			nonRequiredColsWithValues.put(headerName, null);
	
		if (this.dropDownColNames.contains(headerName) && value.startsWith("---"))
			throw new SampleSheetIOException("Unable to load sample " + sid  + ". Value for column " + headerName + " is missing", rowIdx, sheetName);
		
		if (value.length() > this.colLengthMap.get(headerName))
			throw new SampleSheetIOException("Unable to load sample " + sid  + ". Value for column " + headerName + " should not exceed " + colLengthMap.get(headerName) + " characters", rowIdx, sheetName);
		
	    return headerName;
		}
	*/
 
 
 
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
 
 
 private int findStartingRowNum(Sheet sheet)
		{
		int startRowNum = sheet.getFirstRowNum();
		int endRowNum = sheet.getLastRowNum()+1;
	
		while (startRowNum < endRowNum) 
			{
			Row row = sheet.getRow(startRowNum);
			if (row != null)
				{
				Cell cell = row.getCell(ScratchSpace.mrc2SampleIdColumnNo);
				if (cell != null && cell.getCellType() == 1)
					{
					String cellValue = cell.getStringCellValue();
					if (cellValue.startsWith(ScratchSpace.mrc2SampleIdColumnHeader))
						break;
					}
				}
			startRowNum++;
			}
		return ++startRowNum;
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
 

private void screenForDatabaseDuplicates(String sid, List<String> sampleIdsInDatabase) throws SampleSheetIOException
	{
	if (sampleIdsInDatabase.indexOf(sid) != -1)
	    throw new SampleSheetIOException("Unable to upload file : sample  with id " + sid  + " already exists in the database. To correct a "
	    	+ "sheet upload please invalidate samples before re-uploading the sheet.", -1, sheetName);
	    
  }
 	    
private void screenForSheetDuplicates(String sid) throws SampleSheetIOException
	{
	if (loadedSampleIds.indexOf(sid) > -1) 
		throw new SampleSheetIOException("Duplicate sample : Unable to upload file, sample: "  + sid + " is duplicated in the spreadsheet "
				, -1, sheetName);
	
	loadedSampleIds.add(sid)	;
	}



  private void  doCheckForSampleLengthViolations(Row row, String sid) throws SampleSheetIOException
	  {
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

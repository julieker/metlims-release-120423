////////////////////////////////////////////////////
// Mrc2AddingSamplesMetadataReader.java
// Written by Jan Wigginton, Jun 6, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.sheetreaders.obsolete;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.dto.SampleDTO;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.SampleSheetIOException;
import edu.umich.brcf.shared.util.datacollectors.Mrc2SamplesMetadata;
import edu.umich.brcf.shared.util.io.SpreadSheetReader;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;



public class Mrc2AddingSamplesMetadataReader extends SpreadSheetReader implements Serializable
	{
	@SpringBean
	SampleService sampleService;

	public static final int mrc2SampleIdColumnNo = 0;
	public static final String mrc2SampleIdColumnHeader = "Sample ID";
	
	private Boolean addMode = false;
	private final String sheetName = "Samples Metadata";
	private int rowIdx = -1,  headerRowIdx;
	
	List<String> sampleList;
	
	Map<String, Integer> colLengthMap;
	List <String> sampleColNames = Arrays.asList( new String [] {"Sample Id", "Researcher Sample Id", "Researcher Subject Id", "Sample Type", "Genus or Species",
          "Volume", "Units", "Sample Type Id", "Genus or Species Id", "Location Id"});

	private String supplementalExperimentId = null, expectedExperimentId = null;
	
	public Mrc2AddingSamplesMetadataReader() 
		{
		Injector.get().inject(this);
		} 
	
	
	public Mrc2SamplesMetadata read(Sheet sheet) throws SampleSheetIOException
		{
		colLengthMap = initializeColLengthMap();
		Mrc2SamplesMetadata smData = new Mrc2SamplesMetadata();
		
		//try { } smData.setInfoFields(readSheetLines(sheet)); }
		//catch (SampleSheetIOException e) { throw e; }
		//catch (Exception e)
		//	{ 
		//	e.printStackTrace(); 
		//	throw new SampleSheetIOException("Unspecified error while reading sample sheet", rowIdx, sheetName);
		//	}
		
		return smData;
		}
	
	
	private Map<String, Integer> initializeColLengthMap()
		{
		List<Integer> colLengths = Arrays.asList(new Integer []  {9, 120, 100, 100, 50, 100, 26, 7, 8,  6});
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		int i = 0; 
		for (String name : sampleColNames)
			map.put(name, colLengths.get(i++));
	
		return map;
		}
	
	
	public List <SampleDTO> readSheetLines(Sheet sheet, String expId ) throws SampleSheetIOException 
		{
	    int end = sheet.getPhysicalNumberOfRows();
		headerRowIdx = this.findHeaderRowNum(sheet);
	    Row headers = sheet.getRow(headerRowIdx);
	    		
	    sampleList = new ArrayList<String>();
	    List<SampleDTO> samples=new ArrayList<SampleDTO>();
		
	    for (rowIdx = headerRowIdx + 1; rowIdx < end; rowIdx++) 
	    	{
	        Row row = sheet.getRow(rowIdx); 
	    
	        if (this.isSampleRowBlank(row))
	        	break;

	        String sid = getDataAt(rowIdx, Mrc2AddingSamplesMetadataReader.mrc2SampleIdColumnNo, sheet, false);
	        
	       	if(sid != null && sid.length() != 9)
	      		throw new SampleSheetIOException("Unable to upload file,  sample id " + sid + " is not a valid (9 character) metabolomics sample id", rowIdx, sheetName);
	  	
	      	doMissingInfoCheck(row, sid);
	      	doAlreadyExistsCheck(sid);
	      	doDuplicateSampleCheck(sid);
	      	
	      	Integer itemsRead = 1;
			for (Cell header : headers) 
				{
				if (isCellEmpty(header)) break;
		
				String value = getDataAt(rowIdx, header.getColumnIndex(), sheet, false);
				String headerName = header.getStringCellValue();
				headerName= headerName.trim();
				
				if ("Subject ID".equals(headerName))
					if (StringUtils.isEmptyOrNull(value))
						value = itemsRead.toString();
						
				if (value.length() > this.colLengthMap.get(headerName))
					throw new SampleSheetIOException("Unable to load sample " + sid  + ". Value for column " + headerName + " should not exceed " + colLengthMap.get(headerName) + " characters", rowIdx, sheetName);
				
				//setValueForHeaderTag(headerName, value, dto, sid, rowIdx);
				
				samples.add(readInSampleRow(row, expId, sid));
				itemsRead++;
				}
		  	}

	    return samples;
		}
	
	
	private SampleDTO readInSampleRow(Row row, String expID, String sid) throws SampleSheetIOException
		{
		SampleDTO sdto = new SampleDTO();

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
	      	String userDefGOS = (cell == null ? "" :  cell.toString().trim());
	      	if (userDefGOS.contains("--Please select") || userDefGOS.startsWith("---"))
	          	sdto.setUserDefGOS("");
	      	else
	      		sdto.setUserDefGOS(userDefGOS);
	      	
	      	BigDecimal vol= new BigDecimal(row.getCell((short)5).toString());
	      	sdto.setVolume(vol);
	      	sdto.setVolUnits(row.getCell((short)6).toString().trim());
	      	sdto.setSampleTypeId(row.getCell((short)7).toString().trim());
	      	
	      	String strCel= row.getCell((short)8).toString();
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
			if (!StringUtils.isEmptyOrNull(sampleId))
				msg +=  (" for sample id " + sampleId);
			System.out.println(msg);
			throw new SampleSheetIOException(msg, rowIdx, sheetName);
			}
		
		return sdto;
		}
	
	
	private int findHeaderRowNum(Sheet sheet)
		{
		int startRowNum = sheet.getFirstRowNum();
		int endRowNum = sheet.getLastRowNum()+1;
	
		while (startRowNum < endRowNum) 
			{
			Row row = sheet.getRow(startRowNum);
			if (row != null)
				{
				Cell cell = row.getCell(Mrc2AddingSamplesMetadataReader.mrc2SampleIdColumnNo);
				if (cell != null && cell.getCellType() == 1)
					{
					String cellValue = cell.getStringCellValue();
					if (cellValue.equals(Mrc2AddingSamplesMetadataReader.mrc2SampleIdColumnHeader))
						{
						headerRowIdx = startRowNum;
						break;
						}
					}
				}
			
			startRowNum++;
			}
		
		return ++startRowNum;
		}
	 

	 private boolean isSampleRowBlank(Row row)
		 {
		 for (int i = 0; i < this.sampleColNames.size(); i++)
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
		for (int i = 0; i < sampleColNames.size(); i++)
			 {
			 String val = row.getCell(i).toString();
			 
			 // column 3 or 4 can be unselected (for now) -- keep check in place in case this changes
			 if (i == 3 || i == 4) 
				 continue;
			 
			 boolean isOrdinaryMissing = StringUtils.isNullOrEmpty(val);
			 boolean isNonStandardMissing = (i == 3 || i == 4) && !isOrdinaryMissing && val.startsWith("---");
			 
			 if (isOrdinaryMissing || isNonStandardMissing)
				 {
				 String msg = "Unable to load sample " + sid  + ". Value for column " + sampleColNames.get(i) + " is missing";
				 throw new SampleSheetIOException(msg, rowIdx, sheetName);
				 }
			 }
		 }
	 

	private void doDuplicateSampleCheck(String sid) throws SampleSheetIOException
		{
	   	if (sampleList.indexOf(sid) > -1) 
	 		throw new SampleSheetIOException("Unable to upload file, sample: "  + sid + " already exists in the spreadsheet "
	 				, rowIdx, sheetName);
	 	
	 	sampleList.add(sid)	;
		}
	
	
	private void doAlreadyExistsCheck(String sid) throws SampleSheetIOException
		{
		Sample thisSample = null;
		try	{ thisSample = sampleService.loadSampleAlongWithExpById(sid);  }
		catch (Exception ee) { }
 	
		if(thisSample != null)
			{
			String msg = "Unable to upload file,  sample " + thisSample.getSampleID() + " already exists in the database.";
			throw new SampleSheetIOException(msg, rowIdx, sheetName);
			}
		}
	
	
	public Boolean getAddMode()
		{
		return addMode;
		}

	public void setAddMode(Boolean addMode)
		{
		this.addMode = addMode;
		}
			
	public String getExpectedExperimentId()
		{
		return expectedExperimentId;
		}

	public void setExpectedExperimentId(String expectedExperimentId)
		{
		this.expectedExperimentId = expectedExperimentId;
		}


	public String getSupplementalExperimentId()
		{
		return supplementalExperimentId;
		}


	public void setSupplementalExperimentId(String supplementalExperimentId)
		{
		this.supplementalExperimentId = supplementalExperimentId;
		}
	}




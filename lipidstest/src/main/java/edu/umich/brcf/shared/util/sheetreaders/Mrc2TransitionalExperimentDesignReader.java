////////////////////////////////////////////////////
// Mrc2TransitionalExperimentDesignReader.java
// Created by Jan Wigginton from existing submission sheet reader. Jun 15, 2017
////////////////////////////////////////////////////

package edu.umich.brcf.shared.util.sheetreaders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.wicket.injection.Injector;
import edu.umich.brcf.shared.util.FieldLengths;
import edu.umich.brcf.shared.util.SampleSheetIOException;
import edu.umich.brcf.shared.util.datacollectors.Mrc2TransitionalExperimentDesign;
import edu.umich.brcf.shared.util.datacollectors.Mrc2TransitionalExperimentDesignItem;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;



public class Mrc2TransitionalExperimentDesignReader
	{
	public static final int researcherSampleIdColumnNum = 0;
	public static final int firstFactorColumnNum = 1;
   	public static final String researcherSampleIdColumnHeader = "Researcher Sample ID";

//	public static final String researcherSampleIdColumnHeader = "Researcher _Sample_ID";	
   	//issue 261
   	public static final int nPossibleAssays = 5, nPossibleFactors = 5; 

	private int startRowNumSheet;
	private int standardAssayCol;
	private FormulaEvaluator evaluator;
	private final String sheetName = "Experimental Design";
	
	//vars for add mode
	private Boolean addMode = false;
	private String supplementalExperimentId = null, expectedExperimentId = null;
	
	public Mrc2TransitionalExperimentDesignReader() 
		{
		Injector.get().inject(this);
		} 
	
	
	public Mrc2TransitionalExperimentDesign read(Sheet sheet, String expId, List<String> sampleReadOrder) throws SampleSheetIOException
		{
		evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

		
		startRowNumSheet = findStartingRowNum(sheet);
		//Issue 261
		standardAssayCol = findStandardAssayCol(startRowNumSheet, sheet);
		if (standardAssayCol <= 0) // issue 261
			standardAssayCol= Mrc2TransitionalExperimentDesignReader.nPossibleFactors  + Mrc2TransitionalExperimentDesignReader.nPossibleAssays + 1;		
		Mrc2TransitionalExperimentDesign design = new Mrc2TransitionalExperimentDesign();
		design.setFactorLabels(readFactorLabels(sheet, startRowNumSheet));

		design.setInfoFields(readExperimentalDesignItems(sheet, design.getFactorLabels(), startRowNumSheet + 1, sampleReadOrder));
	    
		return design;
		}
	
	// issue 261
    private int findStandardAssayCol (int startRowNumSheet, Sheet sheet)
        {
    	int i = 0;
    	Row row = sheet.getRow(startRowNumSheet-1);
    	int lastCol = row.getLastCellNum();
    	while (i < lastCol)
    	   {
    		if (row.getCell(i).getStringCellValue().startsWith("Standard Assays"))
    		    break;
    		i++;
    	   }
    	return i;
    	
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
				Cell cell = row.getCell(Mrc2TransitionalExperimentDesignReader.researcherSampleIdColumnNum);
				if (cell != null && cell.getCellType() == 1)
					{
					String cellValue = cell.getStringCellValue();
					if (cellValue.startsWith(Mrc2TransitionalExperimentDesignReader.researcherSampleIdColumnHeader))
						break;
					}
				}
			startRowNum++;
			}
		
		return ++startRowNum;
		}


	private List<String> readFactorLabels(Sheet factorSheet, int startRowNum) throws SampleSheetIOException
		{
	
		List<String> factorLabels = new ArrayList<String>();
		Row factorLabelRow = factorSheet.getRow(startRowNum);
		Map<String, String> caseLessMap = new HashMap<String, String>();
		
		for (Cell cell : factorLabelRow) 
			{
			if (cell.getColumnIndex() < firstFactorColumnNum) continue;
			
			String factorLabel = cell.getStringCellValue();
		   
			if (doesExistBlankFactorLabel(factorLabelRow))
				throw new SampleSheetIOException("Cannot have blank factor names", startRowNum, sheetName);
			
			if (caseLessMap.get(factorLabel.toLowerCase()) != null)
				throw new SampleSheetIOException("Unable to upload file : repeated factor name (" + factorLabel + "). "
						+ "Note : Factor labels are case insensitive (e.g. F1 and f1 will be flagged as repeated factor names)", startRowNum, sheetName);
			
			if (factorLabel.length() > FieldLengths.MRC2_FACTOR_NAME_LENGTH)
				throw new SampleSheetIOException("Unable to upload file : factor name (" + factorLabel + "). is too long.  Factor labels must have no more than " 
					+ FieldLengths.MRC2_FACTOR_NAME_LENGTH + " characters", startRowNum, sheetName);
			
			if (!factorLabel.isEmpty() && !"<Enter Name>".equals(factorLabel))
				{
				factorLabels.add(factorLabel);
				caseLessMap.put(factorLabel.toLowerCase(), "");
				}
			}
		
		return factorLabels;
		}
		

	private boolean isDesignRowBlank(Row row, int cols)
		 {
		String val = null;
		 for (int i = Mrc2TransitionalExperimentDesignReader.researcherSampleIdColumnNum + 1; i < cols; i++)
			 {
			 
			 // JAK fix bug 167 
			   if (row == null)
				   continue;

			   if ((row.getCell(i) == null) && (row != null))
			   {
				  
	                  continue;
			   }
			 if (i == Mrc2TransitionalExperimentDesignReader.researcherSampleIdColumnNum)
				 val = evaluator.evaluate(row.getCell(i)).getStringValue();
			 else
				 val = row.getCell(i).toString();
			
			 
			 if (!StringUtils.isEmptyOrNull(val))
			 {
				
				 return false;
			 }
			 }
		
		 return true;
		 }
	
	
	public boolean doesExistBlankFactorLabel(Row factorLabelRow)
		{
		int idx = -1;
		String factorLabelsNoBlanks = "";
		String factorLabelswBlanks = "";
		
		for (Cell cell: factorLabelRow)
			{
			if (idx++ <= this.firstFactorColumnNum)
				continue;
			
			if (cell.getCellType() != Cell.CELL_TYPE_BLANK)
				{
				factorLabelsNoBlanks = factorLabelsNoBlanks + cell.getStringCellValue();
				factorLabelswBlanks  = factorLabelswBlanks  + cell.getStringCellValue();
				}
			else
				factorLabelswBlanks  = factorLabelswBlanks  + " ";
			}
		
		if (StringUtils.isEmptyOrNull(factorLabelsNoBlanks))
			return false;
		
		if (factorLabelswBlanks.indexOf("  ") == 0 )
			return true;
		
		if (factorLabelswBlanks.trim().length() == factorLabelsNoBlanks.trim().length())
		      return false;
	
		return true;
		}


	private List<Mrc2TransitionalExperimentDesignItem> readExperimentalDesignItems(Sheet designSheet, 
			List<String> factorLabels, int rowNumSheet, List<String> sampleReadOrder) throws SampleSheetIOException
		{
		List<Mrc2TransitionalExperimentDesignItem> designItems = new ArrayList<Mrc2TransitionalExperimentDesignItem>();
		int endRowNum = designSheet.getLastRowNum();
		
		int samplesRead = 0,  samplesToRead = endRowNum - startRowNumSheet + 1;
		
	//	if (samplesToRead != sampleReadOrder.size())
	//		throw new SampleSheetIOException("Different # of samples on the experiment design and samples metadata sheets" + samplesToRead + " vs " + sampleReadOrder.size(), rowNumSheet, sheetName);
		
		//int lastCol = Mrc2TransitionalExperimentDesignReader.nPossibleFactors + Mrc2TransitionalExperimentDesignReader.researcherSampleIdColumnNum 
			//	+ Mrc2TransitionalExperimentDesignReader.nPossibleAssays;
		int lastCol = standardAssayCol -1; // issue 261 correction
		while (samplesRead < samplesToRead) 
			{
			Row row = designSheet.getRow(rowNumSheet);
			if (this.isDesignRowBlank(row, lastCol))
				break;
				
			Mrc2TransitionalExperimentDesignItem itm = readExperimentalDesignItem(row, factorLabels, rowNumSheet, (samplesRead == 0));
	
			itm.setSampleId(sampleReadOrder.get(samplesRead));
	
			designItems.add(itm);
	
			samplesRead++;
			++rowNumSheet;
			} 
		return designItems;
		}
	
	
	private Mrc2TransitionalExperimentDesignItem readExperimentalDesignItem(Row row, List<String> factorLabels, int rowNumSheet, Boolean isFirstRow) throws SampleSheetIOException
		{
		Mrc2TransitionalExperimentDesignItem designItem = new Mrc2TransitionalExperimentDesignItem();
		
		CellValue sampleIdd = evaluator.evaluate(row.getCell(Mrc2TransitionalExperimentDesignReader.researcherSampleIdColumnNum));
		String sampleId = sampleIdd.getStringValue();
	
		designItem.setSampleLabel(sampleId.trim()); // JAK fix bug 191
		
		
		int factorsRead = 0;
		for (Cell cell : row)
			{
			if (cell.getColumnIndex() == Mrc2TransitionalExperimentDesignReader.researcherSampleIdColumnNum) continue;				
			// JAK fix bug 175
			//if (cell.getColumnIndex() >= Mrc2TransitionalExperimentDesignReader.nPossibleAssays + Mrc2TransitionalExperimentDesignReader.nPossibleFactors 
				//+ Mrc2TransitionalExperimentDesignReader.researcherSampleIdColumnNum + 1) break;			
			if (cell.getColumnIndex() >= standardAssayCol)
			    break; //issue 261 correction
			
			String cellValue = cell.getCellType() == 1 ? cell.getStringCellValue() : cell.toString();
			if (cellValue.startsWith("----"))
				throw new SampleSheetIOException("Unable to upload file : selected assay name " + cellValue + " isn't an assay", rowNumSheet,  sheetName );
			cellValue = cellValue.trim(); // JAK bug 178		
			Boolean blankValue = (StringUtils.isNullOrEmpty(cellValue));
			
			if (cell.getColumnIndex() <  Mrc2TransitionalExperimentDesignReader.nPossibleFactors + 1)
				{
				if (factorsRead >= factorLabels.size())
					continue;
				
				String factorName = factorLabels.get(factorsRead);
				if (blankValue) 
					throw new SampleSheetIOException("Unable to upload file : value for " + factorName + " shouldn't be blank", rowNumSheet, sheetName);
				
				designItem.setValueForFactor(factorLabels.get(factorsRead++), cellValue);
				}			
		//	else if (cell.getColumnIndex() < Mrc2TransitionalExperimentDesignReader.nPossibleFactors  + Mrc2TransitionalExperimentDesignReader.nPossibleAssays + 1)
			else if (cell.getColumnIndex() < standardAssayCol) // issue 261
				{
				
				if (blankValue) continue;
				designItem.addAssayForSample(cellValue);
				}
			}
		

		return designItem;
		}
	}





////////////////////////////////////////////////////////////SCRAP CODE///////////////////////////////


/*

	
	private Mrc2TransitionalExperimentDesign readExperimentDesign(Sheet sheet, Map<String, String> samples, String expId) throws SampleSheetIOException
		{
	    rowCount = 0;
	    
	  //  Sheet sheet = workbook.getSheet(sheetName); 
	    int fCellCount=0, aCellCount=0, cellCount=0;
	     
	    Iterator<Row> rows = sheet.rowIterator();
	    Map<String, List<String>> factor_map = new HashMap<String, List<String>>();
	    Map<String, List<String>> caseLessMap = new HashMap<String, List<String>>();
	    
	    Map<String, List<String>> assay_map = new HashMap<String, List<String>>();
	    Map<String, String> experiment_assays = new HashMap<String, String>();
	    List<String> factorNames = new ArrayList<String>();
	    
	    /// map is sample id mapped to factor name to value map
	    Map<String, Map<String, String>> overallFactorMap = new HashMap<String, Map<String, String>>();
	     
	    for(String sampleId : samples.keySet())
	     	assay_map.put(sampleId, new ArrayList<String>());
	     	
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
	         	Map<String, String> factorValuesForSampleMap = new HashMap<String, String>();
	         	
	         	if(factorNames.size()>0)	
	         	 	for(int cCount=1; cCount<factorNames.size()+1;cCount++)
	             		{
	             		String value = row.getCell((short)cCount).toString().trim();
	             		String factorName = factorNames.get(cCount-1);
	             		
	             		if (StringUtils.isNullOrEmpty(value))
	             	      	throw new SampleSheetIOException("Missing value for factor " + factorName, rowCount, sheetName);
	                    
	         			if (value.length() > FactorLevel.FACTOR_VALUE_FIELD_LEN) 
	         				{
	         				String msg = "Value for factor name " + factorName + " ( " + value + ") can't be longer than " + FactorLevel.FACTOR_VALUE_FIELD_LEN + " characters"; 
	         				throw new SampleSheetIOException(msg, rowCount, sheetName);
	         				}
	         			factorValuesForSampleMap.put(factorName, value);
	              		}
	         	
	         	
	         	String sid = samples.get(rowCount-12);
	         	overallFactorMap.put(sid, factorValuesForSampleMap);
	         	
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
 		
 		return new Mrc2TransitionalExperimentDesign(samples, overallFactorMap, factorNames, assay_map);
     	}
	
	
	private String doRequirementChecks(Map<String, String> samples, String expID, Map<String,  List<String>> factor_map, List<String> factors, 
		Map<String, List<String>> assay_map)
		{
		String msg  = "OK";
		
		for (int f = 0; f < factors.size(); f++)
	    	if (samples.size()!=factor_map.get(factors.get(f)).size())
	    		return "Please check column " + (f+1) + " in sheet "+sheetNum + " to make sure factor information has been provided for all samples being submitted";
	    	
	     for (String sampleId : samples.keySet())
	    	if(assay_map.get(sampleId).isEmpty())
	    		return  "Error at sample "+ sampleId+" in sheet "+ sheetName +". At least 1 assay should be selected for all samples.";
	    
	    return msg;
		} */
	

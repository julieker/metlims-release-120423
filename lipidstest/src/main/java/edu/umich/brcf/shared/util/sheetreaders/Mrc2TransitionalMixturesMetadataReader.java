////////////////////////////////////////////////////
// Mrc2TransitionalMixturesMetadataReader.java
// Written by Julie Keros, Dec 1, 2020
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.sheetreaders;

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
import edu.umich.brcf.shared.layers.dto.MixtureDTO;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.util.MixtureSheetIOException;
import edu.umich.brcf.shared.util.datacollectors.Mrc2MixtureMetadata;
import edu.umich.brcf.shared.util.utilpackages.NumberUtils;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;

public class Mrc2TransitionalMixturesMetadataReader
	{
	@SpringBean
	AliquotService aliquotService;
	
	public int mrc2AliquotColumnNumber =1;
	private int rowIdx = -1,  rowCount;
	private final String sheetName = "Mixture";

	// Used for add mode (next stage)
	private Map<String, String>  aliquotsIdsInSheet, aliquotsIDsInDatabase;
	Map<String, Integer> colLengthMap;
	List <Integer> fLengths;

	// issue 94
	List <String> mixtureColNames = Arrays.asList( new String [] {"Primary Name", "Aliquot", "volume_aliquot (uL)", "conc (FROM LIMS) uM", "volume solvent  to add", "desired final volume", "New mixture name" , "Information", "compounds", "Concentration_aliquot", "Units", "# aliquots"});
	List <String> headersInSpreadsheet = new ArrayList <String> ();
	private int aliquotCol = 1;
	private int volAliquotCol = 2;
	private int concentrateAliquotCol = 9;
	private int concFromLimsCol = 3;
	private int volumeSolventToAdd = 4;
	private int desiredFinalVolume = 5;
	private int maxNumberColumns = 100;
	private int desiredVolumeCol = 5;
	private int headerRowIdx = 0;
	
	List<String> dropDownCols =  Arrays.asList(new String [] { "Sample Type", "GenusOrSpecies"});
	
	public Mrc2TransitionalMixturesMetadataReader() 
		{
		Injector.get().inject(this);
		} 
	
	public Mrc2MixtureMetadata read(Sheet sheet) throws MixtureSheetIOException
		{ // JAK change 2
		aliquotsIDsInDatabase = aliquotService.allAliquotIdsForMap();
		aliquotsIdsInSheet = new HashMap<String, String>();
		Mrc2MixtureMetadata mixturesMetadata = null; 
		try 
			{
			String missingHeaderName =  getMissingHeaderName (headerRowIdx, sheet);
			if (!StringUtils.isEmptyOrNull(missingHeaderName))
			    throw new MixtureSheetIOException ("The header:" + missingHeaderName + " is missing", 0, sheetName);	
			
			List <MixtureDTO> mixtures = readMixtureInfo(sheet);
			mixturesMetadata = new Mrc2MixtureMetadata(mixtures);
			}
		 catch (MixtureSheetIOException m) { throw m; }
		 catch (Exception e)
			{ 
			e.printStackTrace(); 
			throw new MixtureSheetIOException("Unspecified error while reading sample sheet", rowIdx, sheetName);
			}		
		return mixturesMetadata; 
		}
	
	 //// Read main mixture info just 1st row
	 // issue 94
	private List <MixtureDTO> readMixtureInfo(Sheet sheet) throws MixtureSheetIOException 
		{
		rowCount=0;
		MixtureDTO dto = null;		
	    int firstMixtureRow = 1, lastMixtureRow = sheet.getLastRowNum()+1;
	    List<MixtureDTO> mixtures=new ArrayList<MixtureDTO>();
	  // Issue 94 read in main information
	    List <String> listOfAliquots = new ArrayList<String> ();
	    List <String> listOfVolumeAliquots = new ArrayList<String> ();
	    List <String> listOfConcentrationAliquots = new ArrayList<String> ();	    
	    for (int j = firstMixtureRow; j <= lastMixtureRow; j++)
	    	{
	    	if (sheet.getRow(j) == null)
	    		break;
	        List<String> tokens = readLine(j, sheet);
	        if (isMixtureRowBlank(tokens))
	            break;
	        screenForNonExistentAliquots(tokens.get(aliquotCol), j);
	        screenForMissingInfo(tokens, j);
	        screenForAliquotSheetDuplicates(tokens.get(aliquotCol),j);
	        screenForInvalidNumber(tokens, j);
	       	if (j == 1)
	      	    dto = readInMixtureRowTokens(tokens);
	        listOfAliquots.add(tokens.get(aliquotCol));        
	        listOfVolumeAliquots.add(tokens.get(volAliquotCol));
	        listOfConcentrationAliquots.add(tokens.get(concentrateAliquotCol));
	      	}
	    dto.setAliquotList(listOfAliquots);
	    dto.setAliquotVolumeList(listOfVolumeAliquots);
	    dto.setAliquotConcentrationList(listOfConcentrationAliquots);
	    mixtures.add(dto);
	    return mixtures;
		}
	
	private List<String> readLine (int rowIdx, Sheet sheet) throws MixtureSheetIOException  
	 	{
		try 
			{
			return readLine (rowIdx, sheet, false);
			}
		catch (Exception e)
		    {
			e.printStackTrace();
			MixtureSheetIOException mixtureSheetIOException = new MixtureSheetIOException(e.getMessage() + " on line:" + rowIdx, rowIdx, sheetName);	
			throw new MixtureSheetIOException(e.getMessage() + " on line:" + rowIdx, rowCount, sheetName);	
		 	}
	 	}
	
	private void screenForNonExistentAliquots(String aid, int rowCount) throws MixtureSheetIOException
		{
		if (!aliquotsIDsInDatabase.containsKey(aid))
	   		throw new MixtureSheetIOException("Unable to upload file,  aliquot " + aid + " does not exist in the database in row:" + rowCount, rowCount, sheetName);
		}
	 
	private String getMissingHeaderName (int headerRowIdx, Sheet sheet)
		{
		Row row = sheet.getRow(headerRowIdx);
		Cell cell = null;
		String raw = null;
		for (int i = 0; i < maxNumberColumns; i++  )
			{
			cell = row.getCell(i);
			if (cell == null) 
				break;
            cell.setCellType(Cell.CELL_TYPE_STRING);
        	raw = (cell == null ? "" : cell.toString());
			headersInSpreadsheet.add(raw.trim().toLowerCase());
			}
		for (String header : mixtureColNames )
			{
			if (headersInSpreadsheet.contains(header.trim().toLowerCase()))
				continue;
			else
				return header;
			}
		return null;
		}
	
	private List<String> readLine(int rowIdx, Sheet sheet, boolean aliquotOnly)
	 	{
	 	Row row = sheet.getRow(rowIdx); 	
	 	List<String> tokens = new ArrayList<String>();
	 	String raw = null;
        Cell cell = null;      
	 	for (int i = 0; i < mixtureColNames.size(); i++)
        	{
	 		if (row == null)
	 			break;
        	cell = row.getCell(i);
        	if (i==desiredVolumeCol)
	        	{
        		raw = cell == null ? " " :  String.valueOf(cell.getNumericCellValue());
        		tokens.add(raw == null ? "" : raw.trim());
        		continue;
	        	} 
        	if (i==concentrateAliquotCol)
	        	{
	    		raw = cell == null ? " " :  String.valueOf(cell.getNumericCellValue());
	    		tokens.add(raw == null ? "" : raw.trim());
	    		continue;
	        	} 
        	if (cell != null) 
                cell.setCellType(Cell.CELL_TYPE_STRING);
        	raw = (cell == null ? "" : cell.toString());	
        	tokens.add(raw == null ? "" : raw.trim());
        	}
        return tokens;
	 	}
	 
	private boolean isMixtureRowBlank(List<String> tokens)
		 {
		 for (int i = 0; i < mixtureColNames.size(); i++)
			 {
			 String val = tokens.get(i);
			 if (!StringUtils.isNullOrEmpty(val))
				 return false;
			 }		 
		 return true;
		 }
	 
	private void screenForAliquotSheetDuplicates(String aid, int rowNum) throws MixtureSheetIOException
		{
	   	if (aliquotsIdsInSheet.containsKey(aid))
	   		{
	   		throw new MixtureSheetIOException("Unable to upload file, aliquot: "  + aid + " is duplicated in row: " + rowNum, rowNum, sheetName);
	   		}	   	
	   	aliquotsIdsInSheet.put(aid,  null);
		}
		
	// issue 94
	private void screenForInvalidNumber(List<String> tokens, int rowCount) throws MixtureSheetIOException
	    {
		for (int i = 0; i < mixtureColNames.size(); i++)
			{
			String val = tokens.get(i);
	    	String headerName = mixtureColNames.get(i);	
			if (("volume_aliquot (uL)".equals(headerName)) || i==volAliquotCol)	
				{
				if (!NumberUtils.verifyDecimalRange(val, 8, 7))
					{
					 String msg = "Cannot load volume: "  + val + " is an illegal value";
	    			 throw new MixtureSheetIOException(msg, rowCount, sheetName);	
					}
				}
			if (("Concentration_aliquot".equals(headerName)) || i==concentrateAliquotCol)	
				{
				if (!NumberUtils.verifyDecimalRange(val, 8, 7))
					{
					 String msg = "Cannot load concentration: "  + val + " is an illegal value";
	    			 throw new MixtureSheetIOException(msg, rowCount, sheetName);	
					}
				}
			
			if (("conc (FROM LIMS) uM".equals(headerName)) || i==concFromLimsCol)	
				{
				if (!NumberUtils.verifyDecimalRange(val, 8, 7))
					{
					 String msg = "Cannot load concentration from lims: "  + val + " is an illegal value";
	    			 throw new MixtureSheetIOException(msg, rowCount, sheetName);	
					}
				}
			
			if (rowCount == 1 && (("volume solvent  to add".equals(headerName)) || i==volumeSolventToAdd))	
				{
				if (!NumberUtils.verifyDecimalRange(val, 8, 7))
					{
					 String msg = "Cannot load volume solvent to add: "  + val + " is an illegal value";
	    			 throw new MixtureSheetIOException(msg, rowCount, sheetName);	
					}
				}
			
			if (rowCount == 1 && (("desired final volume".equals(headerName)) || i==desiredFinalVolume))	
				{
				if (!NumberUtils.verifyDecimalRange(val, 8, 7))
					{
					 String msg = "Cannot load desired final volume: "  + val + " is an illegal value";
	    			 throw new MixtureSheetIOException(msg, rowCount, sheetName);	
					}
				}
			}
		}
	  
	// issue 94
	private MixtureDTO readInMixtureRowTokens(List<String> tokens) throws MixtureSheetIOException
		{
		MixtureDTO mdto=new MixtureDTO();
	    String mixtureStr = null;
		try
			{
			for(int i = 0; i < tokens.size(); i++)
			    {
				setValuesForMixtureHeaderTag(tokens.get(i), mixtureColNames.get(i), mdto);
			    }
			}
		catch (Exception e)
	        {
			String msg = "Error while reading mixture information "; 
			e.printStackTrace();
			throw new MixtureSheetIOException(msg, rowCount, sheetName);
			}
		return mdto;
		}
	
	private void setValuesForMixtureHeaderTag(String value, String tag, MixtureDTO dto)
		{	
		tag = tag.toLowerCase();
		tag = StringUtils.cleanAndTrim(tag);
		switch (tag)
		  	{
	        case "volumesolventtoadd" : dto.setVolumeSolventToAdd(value); 
	        						    break;
	        case "desiredfinalvolume" : dto.setDesiredFinalVolume(value);
	        						    break;
	        default : break;	        
		  	}
		 } 
	
	// issue 94
	 private void screenForMissingInfo(List<String> tokens, int rowNum) throws MixtureSheetIOException
		 {
		 for (int i = 0; i < mixtureColNames.size(); i++)
			 {
			 String val = tokens.get(i);
			 boolean isOrdinaryMissing = StringUtils.isNullOrEmpty(val);
			 String headerName = mixtureColNames.get(i).trim();				
			 // column species && st can be unselected (for now) -- keep check in place in case this changes
			 if (i != 1 && i != 2 && i!= 3 && i != 9  && i != 4 && i != 5)
				 continue;
			 
			 if (rowNum == 1 && (i == 1 || i == 2 || i == 3 || i == 9  ||  i == 4 || i == 5 ))
			 	{
				if (isOrdinaryMissing)
				    throw new MixtureSheetIOException("Unable to load mixture " + ". Value for column " + mixtureColNames.get(i) + " is missing on row: " + rowNum, rowCount, sheetName);
			 	}
			 if (rowNum != 1 && (i == 1 || i == 2 || i== 3 ||  i == 9 ))
			 	{
				if (isOrdinaryMissing)
				    throw new MixtureSheetIOException("Unable to load mixture " + ". Value for column " + mixtureColNames.get(i) + " is missing on row: " + rowNum, rowCount, sheetName);
			 	}
			 }
		 }
	}






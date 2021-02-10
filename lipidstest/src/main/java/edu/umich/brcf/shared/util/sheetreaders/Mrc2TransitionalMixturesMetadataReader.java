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
import edu.umich.brcf.shared.layers.service.MixtureService;
import edu.umich.brcf.shared.util.FieldLengths;
import edu.umich.brcf.shared.util.MixtureSheetIOException;
import edu.umich.brcf.shared.util.datacollectors.Mrc2MixtureMetadata;
import edu.umich.brcf.shared.util.utilpackages.NumberUtils;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;

public class Mrc2TransitionalMixturesMetadataReader
	{
	@SpringBean
	AliquotService aliquotService;
	
	@SpringBean
	MixtureService mixtureService; // issue 110
	
	public int mrc2AliquotColumnNumber =1;
	private int rowIdx = -1,  rowCount;
	private final String sheetName = "Mixture";

	// Used for add mode (next stage)
	private Map<String, String>  idsInSheet, mixtureNamesAlreadyInDatabase,  aliquotsIDsInDatabase, complexMixturesInDatabase, mixtureIDsInDatabase, mixtureIdsInSheet;
	Map<String, Integer> colLengthMap;
	List <Integer> fLengths;

	// issue 94
	List <String> mixtureColNames = Arrays.asList( new String [] {"Mixture Name", "Primary Name", "Aliquot", "volume_aliquot (uL)", "conc (FROM LIMS) uM", "volume solvent  to add", "desired final volume",  "Concentration_aliquot", "Units", "# aliquots"});
	List <String> headersInSpreadsheet = new ArrayList <String> ();
	private int aliquotCol = 2;
	private int volAliquotCol = 3;
	private int concentrateAliquotCol = 7;
	private int concFromLimsCol = 4;
	private int volumeSolventToAdd = 5;
	private int desiredFinalVolume = 6;
	private int maxNumberColumns = 100;
	private int desiredVolumeCol = 6;
	private int mixtureNameCol = 0;
	private int headerRowIdx = 0;
	List <String> mixtureColNamesForLength = Arrays.asList( new String [] {"Mixture Name"}); // issue 118
	
	public Mrc2TransitionalMixturesMetadataReader() 
		{
		Injector.get().inject(this);
		} 
	
	public Mrc2MixtureMetadata read(Sheet sheet) throws MixtureSheetIOException
		{ 
		// issue 110
		colLengthMap = initializeColLengthMap();// issue 118
		aliquotsIDsInDatabase = aliquotService.allAliquotIdsForMap();
		mixtureIDsInDatabase = mixtureService.allMixtureIdsForMap();
		complexMixturesInDatabase = mixtureService.allComplexMixtureIdsForMap();
		mixtureNamesAlreadyInDatabase = mixtureService.allMixtureIdsNamesMap();
		idsInSheet = new HashMap<String, String>();
		mixtureIdsInSheet = new HashMap<String, String>();
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
	
	// issue 118
	private Map<String, Integer> initializeColLengthMap()
		{
		fLengths = Arrays.asList(new Integer[] { 
			  FieldLengths.MRC2_MIXTURE_NAME}); 	
		int i = 0; 
		 Map<String, Integer> map = new HashMap<String, Integer>();
		 for (String name : mixtureColNamesForLength)
			map.put(name, fLengths.get(i++));	
		return map;
		}
	
	// issue 118
	  private void  screenForMixtureLengthViolations(List<String> tokens) throws  MixtureSheetIOException
	  {
	  for (int i = 0; i < mixtureColNamesForLength.size(); i++) 
    	  {
    	  String val = tokens.get(i);
    	  String headerName = mixtureColNamesForLength.get(i);	    	     	 
    	  if (val.length() > fLengths.get(i) && i == 0 )
    		  throw new MixtureSheetIOException("Unable to upload file,  mixture name: " + val + " is longer than the allowed value of:" + FieldLengths.MRC2_MIXTURE_NAME, rowCount, sheetName);
    	  }
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
	   // issue 110
	    List <String> listOfMixtures = new ArrayList<String> ();
	    List <String> listOfVolumeMixtures = new ArrayList<String> ();
	    List <String> listOfConcentrationMixtures = new ArrayList<String> ();
	    for (int j = firstMixtureRow; j <= lastMixtureRow; j++)
	    	{
	    	if (sheet.getRow(j) == null)
	    		break;
	        List<String> tokens = readLine(j, sheet);
	        if (tokens.size() == 0 && j == 0)
	            throw new MixtureSheetIOException("Unable to load mixture.  This is a blank spreadsheet without any rows", rowCount, sheetName);        	
	        if (tokens.size() == 0 )
	        	break;
	        if (isMixtureRowBlank(tokens))
	            break;
	        // issue 110
	        screenForMixtureLengthViolations(tokens);// issue 118
	        screenForNonExistentAliquotsMixtures(tokens.get(aliquotCol), j);
	        if (tokens.get(aliquotCol).startsWith("M"))
	        	screenForComplexMixtures(tokens.get(aliquotCol), j);
	        screenForMissingInfo(tokens, j);
	        screenForMixtureNameInDatabase(tokens.get(mixtureNameCol),j);
	        screenForSheetDuplicates(tokens.get(aliquotCol),j);
	        screenForInvalidNumber(tokens, j);
	       	if (j == 1)
	      	    dto = readInMixtureRowTokens(tokens);
	       	// issue 110
	       	if (!tokens.get(aliquotCol).startsWith("M"))
	       		{
	       		listOfAliquots.add(tokens.get(aliquotCol));        
	       		listOfVolumeAliquots.add(tokens.get(volAliquotCol));
	       		listOfConcentrationAliquots.add(tokens.get(concentrateAliquotCol));
	       		}
	       	else
	       		{
	       		listOfMixtures.add(tokens.get(aliquotCol));        
	       		listOfVolumeMixtures.add(tokens.get(volAliquotCol));
	       		listOfConcentrationMixtures.add(tokens.get(concentrateAliquotCol));
	       		}
	      	}	    
	    dto.setAliquotList(listOfAliquots);
	    dto.setAliquotVolumeList(listOfVolumeAliquots);
	    dto.setAliquotConcentrationList(listOfConcentrationAliquots);
	    // issue 110 	    
	    dto.setMixtureList(listOfMixtures);
	    dto.setMixtureVolumeList(listOfVolumeMixtures);
	    dto.setMixtureConcentrationList(listOfConcentrationMixtures);	    
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
	
	private void screenForNonExistentAliquotsMixtures(String aOrMid, int rowCount) throws MixtureSheetIOException
		{
		if (!aliquotsIDsInDatabase.containsKey(aOrMid) && !aOrMid.startsWith("M"))
	   		throw new MixtureSheetIOException("Unable to upload file,  aliquot " + aOrMid + " does not exist in the database in row:" + rowCount, rowCount, sheetName);
		if (!mixtureIDsInDatabase.containsKey(aOrMid) && aOrMid.startsWith("M"))
	   		throw new MixtureSheetIOException("Unable to upload file,  mixture " + aOrMid + " does not exist in the database in row:" + rowCount, rowCount, sheetName);
		}
	
	// Issue 118
	private void screenForMixtureNameInDatabase(String mName, int rowCount) throws MixtureSheetIOException
		{
		if (mixtureNamesAlreadyInDatabase.containsKey(mName))
	   		throw new MixtureSheetIOException("Unable to upload file,  mixture name " + mName + " already exists in row:" + rowCount, rowCount, sheetName);
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
        	if (i == 1 && StringUtils.isEmptyOrNull((cell == null ? "" : cell.toString())))
        		break;
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
		 if (tokens.size()<= 1)
			 return true;
		 for (int i = 0; i < mixtureColNames.size(); i++) // issue 118
			 {
			 String val = tokens.get(i);
			 if (!StringUtils.isNullOrEmpty(val) &&  i>0) 
				 return false;
			 }	
		 return true;
		 }
	 
	// issue 110
	private void screenForSheetDuplicates(String id, int rowNum) throws MixtureSheetIOException
		{
	   	if (idsInSheet.containsKey(id))
	   		{
	   		throw new MixtureSheetIOException("Unable to upload file, " + (id.startsWith("M") ? "mixture:" : "aliquot" ) + id + " is duplicated in row: " + rowNum, rowNum, sheetName);
	   		}	   	
	   	idsInSheet.put(id,  null);
		}
	
	private void screenForComplexMixtures(String mid, int rowCount) throws MixtureSheetIOException
		{
		if (complexMixturesInDatabase.containsKey(mid))
	   		throw new MixtureSheetIOException("Unable to upload file,  mixture " + mid + " contains other mixtures... in row:" + rowCount, rowCount, sheetName);
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
		    // issue 118
		  	case "mixturename" 		  : dto.setMixtureName(value);
			    						break;
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
		 //issue 118 
		 for (int i = 0; i < mixtureColNames.size(); i++)
			 {
			 String val = tokens.get(i);
			 boolean isOrdinaryMissing = StringUtils.isNullOrEmpty(val);
			 String headerName = mixtureColNames.get(i).trim();				
			 // column species && st can be unselected (for now) -- keep check in place in case this changes
			 if (i!= 0 && i!= 2 && i != 3 && i != 4 && i!= 5   && i != 6 && i != 7)
				 continue;
			 
			 if (rowNum == 1 && (i == 0 || i == 2 || i == 3 || i == 4 || i == 5  ||  i == 6 || i == 7 ))
			 	{
				if (isOrdinaryMissing)
				    throw new MixtureSheetIOException("Unable to load mixture " + ". Value for column " + mixtureColNames.get(i) + " is missing on row: " + rowNum, rowCount, sheetName);
			 	}
			 if (rowNum != 1 && ( i == 2 || i == 3 || i==4||  i == 7 ))
			 	{
				if (isOrdinaryMissing)
				    throw new MixtureSheetIOException("Unable to load mixture " + ". Value for column " + mixtureColNames.get(i) + " is missing on row: " + rowNum, rowCount, sheetName);
			 	}
			 }
		 }
	}






////////////////////////////////////////////////////
// Mrc2AddingExperimentDesignReader.java
// Written by Jan Wigginton, Jun 7, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.sheetreaders.obsolete;

import java.io.Serializable;
import java.util.ArrayList;
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

import edu.umich.brcf.shared.layers.domain.FactorLevel;
import edu.umich.brcf.shared.layers.dto.SampleDTO;
import edu.umich.brcf.shared.util.SampleSheetIOException;
import edu.umich.brcf.shared.util.io.SpreadSheetReader;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;



public class Mrc2AddingExperimentDesignReader extends SpreadSheetReader implements Serializable
	{
	Boolean addMode = false;
	String sheetName = "Experimental Design", targetExpId;
	int sheetNum = 4, rowCount = 0;
	
	public Mrc2AddingExperimentDesignReader() 
		{
		Injector.get().inject(this);
		}

	public void readExperimentDesign(Workbook workbook, List <SampleDTO> samples, String expId, FileUpload upload) throws SampleSheetIOException
		{
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
     		
     	///	try
     	//		{
     	//		if (experiment_assays != null)
     	//			data = new Mrc2SubmissionSheetData(expId, factor_map, assay_map, samples, factorNames, upload, experiment_assays);
     	//		}
     	//	catch (Exception e) { } 
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

		public Boolean getAddMode()
			{
			return addMode;
			}

		public void setAddMode(Boolean addMode)
			{
			this.addMode = addMode;
			}

		public String getTargetExpId()
			{
			return targetExpId;
			}

		public void setTargetExpId(String targetExpId)
			{
			this.targetExpId = targetExpId;
			}
		}



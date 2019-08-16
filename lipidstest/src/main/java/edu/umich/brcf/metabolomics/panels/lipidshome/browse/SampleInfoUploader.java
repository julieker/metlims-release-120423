
// SampleInfoUploader.java
// Written by Jan Wigginton, August 2015

package edu.umich.brcf.metabolomics.panels.lipidshome.browse;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
import org.apache.wicket.util.file.Folder;

import edu.umich.brcf.metabolomics.layers.service.Ms2SampleMapService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.METWorksException;

  
public class SampleInfoUploader implements Serializable
	{
	@SpringBean
	SampleService sampleService;
	
	@SpringBean
	Ms2SampleMapService ms2SampleMapService;
	
	private static File newFile;
	private static Map <String, String> sampleMap = new HashMap<String, String>();
	private static Map<String, Integer> sampleRandomization = new HashMap<String, Integer>();
	
	
	public static enum UploadType
		{ 
		UPLOAD_TYPE_RANDOMIZATION, 
		UPLOAD_TYPE_SAMPLE_MAP
		}
	
	private static List<String> possibleUploadTypes =  Arrays.asList(new String [] {"Randomization", "Sample Map"});
	private UploadType uploadType;
	String associatedId, expId;

	
	public SampleInfoUploader()
		{
		Injector.get().inject(this);  
		}
	
	
	public void parseUploadAndSave(FileUpload upload) throws METWorksException
		{
		if (uploadType.equals(UploadType.UPLOAD_TYPE_RANDOMIZATION))
			uploadAndSaveRandomization(upload);
		else
			uploadAndSaveSampleMap(upload);
		}
	
	
	public Map<String, String> uploadAndSaveSampleMap(FileUpload upload)
		{
		if (upload == null)
			return null;
		
		File newFile = uploadToServer(upload, "sample-map");
	
		sampleMap = parseSampleMapFile(upload, newFile);
		//if (!verifySampleIds(sampleMap.keySet()))
		//	return null;
		
		return sampleMap;
		}
	
	
	public void uploadAndSaveRandomization(FileUpload upload) throws METWorksException
		{
		if (upload == null)
			throw new METWorksException("Upload was null");
		
		File newFile = uploadToServer(upload, "sample-random");
		
		
		sampleRandomization = parseRandomizationFile(upload, newFile);
		boolean haveSampleIdsOnly = verifySampleIds(sampleRandomization.keySet(), true);
		boolean haveAllIds = verifySampleIds(sampleRandomization.keySet(), false);
		if (!haveSampleIdsOnly && !haveAllIds)
			sampleRandomization = buildDummyRandomization(expId, "A004");
		
		try
			{
			ms2SampleMapService.updateRandomizationFromSampleInfoUpload(this);
			}	
		catch (Exception e)
			{
			throw new METWorksException("Error while saving randomization to database");
			}
		}
	
	
	private Map <String, Integer> buildDummyRandomization(String expId,String assayId)
		{
		//expId = "EX00421";
		//this.associatedId = "MD000017";
		
		List <String> sampleIds = sampleService.sampleIdsForExpIdAndAssayId(expId, "A004");
		
		Map<String, Integer> dummy = new HashMap<String, Integer>();
		
		for (int i = 0; i < sampleIds.size(); i++)
			dummy.put(sampleIds.get(i), i);
		
		return dummy;
		}
	
	private boolean verifySampleIds(Set<String> uploadedIds, boolean samplesOnly)
		{
		List<String> targetIds;
		
		if (samplesOnly)
			targetIds = sampleService.sampleIdsForExpIdAndAssayId(expId, "A004");
		else
			targetIds = ms2SampleMapService.loadSampleTagsForDataSetId(associatedId);
		
		System.out.println("Verifying the sample ids we need to upload for this experiment");
		return (targetIds.containsAll(uploadedIds) && uploadedIds.containsAll(targetIds));
		}
	
	//for (int i = 0; i < sampleIds.size(); i++)
	//	crossCheckMap.put(sampleIds.get(i), null);
	
	//if (map.size() != sampleIds.size())
	//	return false;
	
	//map.ke
	//for (String key : map.keySet())
	//	{
	//	if (!crossCheckMap.containsKey(key))
	//		return false;
		
	//	crossCheckMap.put(key, key);
	//}

	//return crossCheckMap.containsValue(null);
	
	
	private File uploadToServer(FileUpload upload, String tempFolderTag)
		{
     	if((upload.getContentType().equalsIgnoreCase("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))||((upload.getContentType().equalsIgnoreCase("application/vnd.ms-excel"))))
        	{
     		newFile = new File(getUploadFolder(tempFolderTag), upload.getClientFileName());
            
            try
            	{
                newFile.createNewFile();
                upload.writeTo(newFile);
            	}
            
            catch (Exception e)
            	{
                throw new IllegalStateException("Unable to write file");
            	}
        	}
    	
     	return newFile;
		}
         
	
	private Workbook createWorkbook(FileUpload upload, File newFile)
		{
		Workbook workbook = null;
		System.out.println("Creating workbook.");

		try
			{
	        if(upload.getContentType().equalsIgnoreCase("application/vnd.ms-excel"))
	     		{
	        	POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(newFile));
	        	workbook = new HSSFWorkbook(fs);
	     		}
	        else	
				{
	        	OPCPackage pkg = OPCPackage.open(newFile);
	        	workbook = new XSSFWorkbook(pkg);
				}

	        System.out.println("Workbook " + (workbook == null ? " is null." : " is not null"));
			}
		catch (Exception e)
			{
			}
        
		return workbook;
		}
	

	private Map<String, String> parseSampleMapFile(FileUpload upload, File newFile)
		{
		Map<String, String> map = new HashMap<String, String>();
	    int rowCount = 0;
	    
	    Workbook workbook = createWorkbook(upload, newFile);
	    Sheet sheet = workbook.getSheetAt(1);
	    Row row;
	    
	    Iterator<Row> rows = sheet.rowIterator ();
	    while (rows.hasNext())
	    	{
	    	row = rows.next();
	    	if (rowCount++ == 0)
	    		continue;
	    	
	    	if (row.getCell((short) 0) == null)
	    		break;
	    	
	    	String sid = row.getCell((short)0).toString().trim();
	    	
	    	if( (sid==null) || (sid.trim().length() == 0))
	     		break;
	    	
	    	if (row.getCell((short) 1) == null)
	    		break;
	
	    	String sLabel = row.getCell((short) 1).toString().trim();
	    	
	    	if (sLabel == null || sLabel.trim().length() == 0)
	    		break;
	    	
	    	 map.put(sid, sLabel);
	    	}
	     
	    return map;
		}

	
	
	private Map<String, Integer> parseRandomizationFile(FileUpload upload, File newFile)
		{
		Map<String, Integer> map = new HashMap<String, Integer>();
		int rowCount = 0;
		
		Workbook workbook = createWorkbook(upload, newFile);
		if (workbook == null)
			return null;
		
		Sheet sheet = workbook.getSheetAt(0);
		Row row;
		
		Iterator<Row> rows = sheet.rowIterator ();
		while (rows.hasNext())
			{
			row = rows.next();
			if (rowCount == 0)
				{
				rowCount++;
				continue;
				}
			
			rowCount++;
			
			Integer random = -1;
			String sid = "";
			try
				{
				
				Iterator <Cell> cellIterator = row.cellIterator();
		         while ( cellIterator.hasNext()) 
		         {
		            Cell cell = cellIterator.next();
		            switch (cell.getCellType()) 
		            {
		               case Cell.CELL_TYPE_NUMERIC:
		               System.out.print( 
		               cell.getNumericCellValue() + " \t\t " );
		               Double randomDbl = cell.getNumericCellValue();
		               random = randomDbl.intValue();
		               break;
		               
		               case Cell.CELL_TYPE_STRING:
		               System.out.print(
		               sid = cell.getStringCellValue() + " \t\t " );
		               break;
		            }
		         }
				}
			catch (Exception e)
				{   
				}
			
			
			if (random == null || random.equals(-1))
				break;
			
			//String sid = "cat";
			map.put(sid, random);
			}
		 
		return map;
		}
	
	
	public void checkUpload()
		{
		Map map = sampleMap != null && sampleMap.size() > 0 ? sampleMap : sampleRandomization;
		
		for (Object key : map.keySet())
			System.out.println(key.toString() + "\t" + map.get(key));
		}
	
	
	private Folder getUploadFolder(String tag)
		{
		Folder uploadFolder = new Folder(System.getProperty("java.io.tmpdir"), tag + "-uploads");
	    uploadFolder.mkdirs();
	    return (uploadFolder);
		}
	
	public void setUploadType(UploadType uploadType)
		{
		this.uploadType = uploadType;
		}
	
	public UploadType getUploadType()
		{
		return this.uploadType;
		}
			
	
	public List<String> getPossibleUploadTypes()
		{
		return this.possibleUploadTypes;
		}
	
	public Map<String, Integer> getSampleRandomization()
		{
		return sampleRandomization;
		}

	public String getAssociatedId() {
		return associatedId;
	}

	public void setAssociatedId(String associatedId) {
		this.associatedId = associatedId;
	}

	public String getExpId() 
		{
		return expId;
		}


	public void setExpId(String expId) 
		{
		this.expId = expId;
		}
	
	
	} 
////////////////////////////////////////////////////
// Mrc2TransitionalSubmissionSheetReader.java
// Written by Jan Wigginton, Jun 14, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.sheetreaders;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import edu.umich.brcf.shared.util.SampleSheetIOException; // JAK change 1
import edu.umich.brcf.shared.util.datacollectors.Mrc2TransitionalExperimentDesignItem;
import edu.umich.brcf.shared.util.datacollectors.Mrc2TransitionalSubmissionSheetData;
import edu.umich.brcf.shared.util.interfaces.ISampleWorkbookReader;
import edu.umich.brcf.shared.util.io.SpreadSheetReader; // JAK change 2



// NOTE : This reader is a temporary measure to safely transition from the stable but badly designed old submission sheet readers
// We will be slowly moving each sheet read out into its own class and transfer the shortcode save to the submission data service
// as it should have been already...  Once the plain reader is stable, we will be incorporating the add uplaod code.

public class Mrc2TransitionalSubmissionSheetReader extends SpreadSheetReader implements Serializable, ISampleWorkbookReader
	{
	private String sheetName; // JAK change 3
	private Boolean addMode = false;
	private Mrc2TransitionalSubmissionSheetData data = null; // JAK change 4
	
	
	public Mrc2TransitionalSubmissionSheetReader() 
		{
		Injector.get().inject(this);
		}
	
	
	public Mrc2TransitionalSubmissionSheetData readWorkBook(File newFile, FileUpload upload) throws SampleSheetIOException
		{
		data = new Mrc2TransitionalSubmissionSheetData(); 
		
		try
			{
			Workbook workbook = createWorkBook(newFile, upload); // JAK change 5
			Mrc2ClientInfoReader ciReader = new Mrc2ClientInfoReader();
			Mrc2TransitionalSamplesMetadataReader sampleReader = new Mrc2TransitionalSamplesMetadataReader();
			Mrc2TransitionalExperimentDesignReader designReader = new Mrc2TransitionalExperimentDesignReader();
			
			List<String> sheets = Arrays.asList(new String [] { "Client Data", "Samples Metadata", "Experimental Design"});
			ciReader.setReport(false);
			
			// Error while initializing experiment design
			for (String sheetname : sheets) 
				{
				Sheet sheet = workbook.getSheet(sheetname);
				sheetName = sheetname; // JAK change 6
				
				switch(sheetname)
					{
					case "Client Data" : // JAK change 7
						if (!addMode)
							data.setClientInfo(ciReader.read(sheet)); 
						else if (sheet != null)
							throw new SampleSheetIOException("To append samples to an experiment, please use the suppplental sample submission sheet.", -1, "Client Data");
						break;
					
					// sampleReader builds mapping of mrc2id to researcher_id for subsequent matching...
					case "Samples Metadata" : 
						data.setSamplesMetadata(sampleReader.read(sheet, data.getExpId())); break; 
					
					 	
					// expDesignReader maps researcherIds (read in) to sample ids (from read order)
					case "Experimental Design" :   
						data.setExpDesign(designReader.read(sheet, data.getExpId(), sampleReader.getSampleReadOrder())); break;
				    }	
				}
			
			// (Paranoid) double check that mappings read in from sample sheet correspond to matches created in the exp design read
			verifyIdMappings(data, sampleReader.getSampleIdMapping(), sampleReader.getReverseSampleIdMapping());
			}
		catch (SampleSheetIOException e) { throw e; }
		catch (Exception e) { 
			e.printStackTrace();
			throw new SampleSheetIOException("Error while reading submission sheet data" , -1 , sheetName);  } 
		// JAK change 8
		return data;
		}
	
	
	//TO DO :  Deal with redundant researcher ids with backwards check.  For now this is illegal...
	public void verifyIdMappings(Mrc2TransitionalSubmissionSheetData data, Map<String, String> idToRsIdMapping, Map<String, String> rsIdToIdMapping) throws SampleSheetIOException
		{
		
		for (Mrc2TransitionalExperimentDesignItem item : data.getExpDesign().getInfoFields())
			{
			String sampleId = item.getSampleId();
			String researcherId = item.getSampleLabel();
			if (!idToRsIdMapping.get(sampleId).equals(researcherId)) // JAK change 9
				throw new SampleSheetIOException("Error while reading submission data : experiment design items didn't map correctly. " , -1 , "Submission Sheet Reader");			
			}		
		}
	}
		
	///////////////////   Scrap code spreadsheet reader code changes :
/*
 * LINE 17 change 1 removed
import org.apache.wicket.spring.injection.annot.SpringBean;
import edu.umich.brcf.shared.layers.service.FactorService;

  LINE 21 change 2 removed
  import edu.umich.brcf.shared.util.utilpackages.StringUtils;
  
  LINE 31 change 3 removed
  	@SpringBean
	FactorService factorService;
	
  LINE 33 change 4 removed
  private Integer expectedCount;
	private Integer expectedCount;
	private String expectedExperimentId = null, sheetName;
	
  LINE 49, 50, 51 change 5 removed

			ciReader.setAddMode(addMode);
			ciReader.setExpectedExpId(expectedExperimentId);
			
		
			sampleReader.setAddMode(addMode);
			sampleReader.setExpectedExperimentId(expectedExperimentId);
			
			
			designReader.setAddMode(addMode);
			designReader.setExpectedExperimentId(expectedExperimentId);
			
LINE 61 change 6 removed

            if (sheet == null)
					throw new SampleSheetIOException("Workbook tab with this name not found.  Please verify that you are uploading an "
							+ " Epigenomics Sample Submission Form", -1, sheetName);

LINE 64 change 7 ADDED
                   case "Client Data" : 
					--->	if (!addMode)
							data.setClientInfo(ciReader.read(sheet)); 
					-->	else if (sheet != null)
							throw new SampleSheetIOException("To append samples to an experiment, please use the suppplental sample submission sheet.", -1, "Client Data");
						break;
										
LINE 89 change 8 removed

			
		if (addMode && !sampleCountMatchesExpected())
			throw new SampleSheetIOException("Number of samples read in " + data.getSampleCount() + " does not match expected number of samples.", 1, "Samples Metadata");
	
		if (addMode)
			{
			if (!factorService.allFactorsPresent(data.getExpId(), data.getExpDesign().getFactorLabels()))
				{
				List<String> expectedFactors = factorService.getFactorNamesForExpId(data.getExpId());
				
				String message = "Error while uploading supplemental submission sheet -- factor names on Factors sheet " 
					+ StringUtils.buildDatabaseListFromList(data.getExpDesign().getFactorLabels()) + " do not match existing factor names "
					+ StringUtils.buildDatabaseListFromList(expectedFactors) + " for experiment " + data.getExpId();

				throw new SampleSheetIOException(message, 5, "Factors");
				} 
			} 
			
LINE 103 change 9 change

			if (!idToRsIdMapping.get(sampleId).equals(researcherId))
				{
				throw new SampleSheetIOException("Error while reading submission data : experiment design items didn't map correctly. " , -1 , "Submission Sheet Reader");
				}
			else
				{
				}
			}
			
		to
			if (!idToRsIdMapping.get(sampleId).equals(researcherId))
				{
				
				throw new SampleSheetIOException("Error while reading submission data : experiment design items didn't map correctly. " , -1 , "Submission Sheet Reader");
				}
				
	
	
 * 
 * 
 * 
 * 
 * 
 * 
 * 
*/

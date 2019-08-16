////////////////////////////////////////////////////
// Mrc2AddingSubmissionSheetReader.java
// Written by Jan Wigginton, Jun 4, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.sheetreaders.obsolete;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.file.Files;

import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.FactorService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.METWorksException;
import edu.umich.brcf.shared.util.SampleSheetIOException;
import edu.umich.brcf.shared.util.datacollectors.Mrc2SubmissionSheetData;
import edu.umich.brcf.shared.util.interfaces.ISampleWorkbookReader;
import edu.umich.brcf.shared.util.io.SpreadSheetReader;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;



public class Mrc2AddingSubmissionSheetReader extends SpreadSheetReader implements Serializable, ISampleWorkbookReader
	{
	@SpringBean 
	ExperimentService experimentService;
	
	@SpringBean
	FactorService factorService;
	
	Mrc2SubmissionSheetData data;
	
	private String sheetName;
	private int rowNum = -1;
	
	private Boolean addMode = false;
	private Integer nExpectedSamples;
	private String expectedExperimentId = null;
	
	
	public Mrc2AddingSubmissionSheetReader() 
		{
		Injector.get().inject(this);
		}
	
	
	public Mrc2SubmissionSheetData readWorkBook(File newFile, FileUpload upload) throws SampleSheetIOException
		{
		data = new Mrc2SubmissionSheetData(); 
		
		try
			{
			Workbook workbook = createWorkBook(newFile, upload);
		
			Mrc2AddingClientInfoReader ciReader = new Mrc2AddingClientInfoReader();
			Mrc2AddingSamplesMetadataReader sampleReader = new Mrc2AddingSamplesMetadataReader();
			Mrc2AddingExperimentDesignReader expDesignReader = new Mrc2AddingExperimentDesignReader();
			expDesignReader.setAddMode(addMode);
			sampleReader.setAddMode(addMode);
			expDesignReader.setTargetExpId(expectedExperimentId);
			
			List<String> sheets = Arrays.asList(new String [] { "Client Data", "Samples Metadata", "Factors", "Assays"});
			ciReader.setReport(false);
			
			if (addMode) 
				{
				sampleReader.setExpectedExperimentId(expectedExperimentId);
				Experiment exp = null;
				try
					{
					exp = experimentService.loadById(expectedExperimentId);
					exp.getExpDescription();
					}
				catch (Exception e) {  } 
		
				if (exp != null &&  (exp.getSampleList() == null || exp.getSampleList().size() == 0))
					throw new SampleSheetIOException("Cannot add samples to " + expectedExperimentId + ". No samples have been (previously) registered. "
					   + "Please use the sample upload under the Register Samples section to create the first batch of samples for an experiment" , -1,  "Samples Metadata");
				}
		
			
			for (String sheetname : sheets) 
				{
				Sheet sheet = workbook.getSheet(sheetname);
				sheetName = sheetname;
				if (!addMode && sheet == null)
					throw new SampleSheetIOException("Workbook tab with this name not found.  Please verify that you are uploading the "
							+ " MCHEAR Sample Submission Form (and not a supplemental submission form)", -1, sheetName);

				if (addMode && "ClientInfo".equals(sheetname))
					continue;
				
				switch(sheetname)
					{
					case "Client Data" : 
					if (!MedWorksSession.get().getTestMode())
						{	
						if (!addMode)
							data.setClientInfo(ciReader.read(sheet)); 
						else if (sheet != null)
							throw new SampleSheetIOException("To append samples to an experiment, please use the suppplental sample submission sheet.", -1, "Client Data");
						}
						break;
					case "Samples Metadata" : 
						data.setSamplesMetadata(sampleReader.read(sheet));   //readSampleInfo(workbook, data.getExpId())); 
						if (addMode)
							data.getClientInfo().setExperimentId(sampleReader.getSupplementalExperimentId());
						break; 
					case "Factors" : 
						//if (!MedWorksSession.get().getTestMode())
						//	data.setExpDesign(expDesignReader.readExperimentDesign(workbook, data.getSamplesMetadata().grabDTOList(), expId, upload); break;
							
					}
				}
			
			
		//	if (addMode && expectedExperimentId != null && !expectedExperimentId.equals(sampleReader.getSupplementalExperimentId()))
		//		throw new SampleSheetIOException("Experiment number expected (" + expectedExperimentId + ") does not match the experiment on the submission sheet (" + data.getExpId() + ")", 
		//				1, "Samples Metadata");
				
			if (!sampleCountMatchesExpected())
				throw new SampleSheetIOException("Number of samples read in " + data.getSampleCount() + " does not match expected number of samples.", 1, "Samples Metadata");
		
			if (addMode)
				{
				if (!factorService.allFactorsPresent(data.getExpId(), data.getExpDesign().getFactorLabels()))
					{
					List<String> expectedFactors = factorService.getFactorNamesForExpId(data.getExpId());
					
					String message = "Error while uploading supplemental submission sheet -- factor names on Factors sheet " 
						+ StringUtils.buildDatabaseListFromList(data.getExpDesign().getFactorLabels()) + " do not match existing factor names "
						+ StringUtils.buildDatabaseListFromList(expectedFactors) + " for experiment " + data.getExpId() + ". Note : factor names are case sensitive and must match exactly.";

					throw new SampleSheetIOException(message, 5, "Factors");
					}
				} 
			
			return data;
			}
		catch (SampleSheetIOException e) {  throw e; }
		catch (METWorksException e) { throw new SampleSheetIOException(e.getMessage(), rowNum, sheetName); }
		catch (Exception e)
			{
			e.printStackTrace();
			throw new SampleSheetIOException(e.getMessage(), rowNum , sheetName);
		    }
		finally { Files.remove(newFile); }
		}
	
	
	private Boolean sampleCountMatchesExpected() throws SampleSheetIOException
		{
		Integer nRead = data.getSampleCount();
		
		/*
		if (!addMode)
			{
			Experiment exp =  null;
			try  { exp = experimentService.loadBasicsById(data.getExpId()); }
			catch (Exception e) { throw new SampleSheetIOException("Missing or invalid experiment id " , 15, "Client Info Sheet"); }
		
			if (exp.getnReportedSamples() == null)
				return true;

			return (exp.getnReportedSamples().equals(nRead));
			}
		*/
		return nRead.equals(nExpectedSamples); 
		}


	public Boolean getAddMode()
		{
		return addMode;
		}


	public void setAddMode(Boolean addMode)
		{
		this.addMode = addMode;
		}


	public Integer getnExpectedSamples()
		{
		return nExpectedSamples;
		}


	public void setnExpectedSamples(Integer nExpectedSamples)
		{
		this.nExpectedSamples = nExpectedSamples;
		}

	public String getExpectedExperimentId()
		{
		return expectedExperimentId;
		}

	public void setExpectedExperimentId(String id)
		{
		this.expectedExperimentId = id;
		}
	}

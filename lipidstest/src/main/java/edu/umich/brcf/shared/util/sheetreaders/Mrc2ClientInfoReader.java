////////////////////////////////////////////////////
// Mrc2ClientInfoReader.java
// Written by Jan Wigginton, Jun 4, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.sheetreaders;


import org.apache.poi.ss.usermodel.Sheet;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.util.SampleSheetIOException;
import edu.umich.brcf.shared.util.datacollectors.ClientDataInfo;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;


public class Mrc2ClientInfoReader extends NewClientInfoReader
	{
	@SpringBean
	ExperimentService experimentService;
	// JAK change 1
	public Mrc2ClientInfoReader() 
		{ 
		Injector.get().inject(this);
		}
	
	public ClientDataInfo read(Sheet sheet) throws SampleSheetIOException
		{
		setReport(false);
		ClientDataInfo clientInfo = ((ClientDataInfo) super.read(sheet));
		
		String expId = clientInfo.getExperimentId();
		
		
		if (StringUtils.isNullOrEmpty(expId))
			throw new SampleSheetIOException("Experiment ID is missing", -1, "Client Data");
		// JAK change 2
		
		Experiment exp; 
		try
			{
			exp = experimentService.loadSimplestById(expId);
			exp.getExpName();
			}
		catch(Exception ex)
		 	{
		 	String msg = "File upload failed: Experiment id + (" + expId + ") in client info sheet does not correspond to any known experiments";
			System.out.println(msg);
			throw new SampleSheetIOException(msg, -1, "Client Data");
		    }
			
		
		try // JAK change 3
			{
			Integer nSamples = experimentService.countSamplesAsInt(expId);
			if (nSamples > 0) 
				{
				String msg = "Experiment already has samples.  Please use the Add Samples button at the bottom of the Sample Tools Panel page to append (additional) samples to an existing experiment.";
				throw new SampleSheetIOException(msg,  getExpIdRow(), "Client Data");
				}
			}
		catch (SampleSheetIOException e) { throw e;  }
		catch (Exception e) { }
		
		if (StringUtils.isNullOrEmpty(clientInfo.getNihGrantNumber()))
				clientInfo.setNihGrantNumber("NO NIH GRANT");
		
		if (StringUtils.isNullOrEmpty(clientInfo.getShortCode()))
			throw new SampleSheetIOException("Shortcode cannot be blank.  If no shortcode exists, please indicate this by filling the shortcode field with NA", 15, "Client Data");
		
				
		return clientInfo;
		}
		// JAK change 4
	}

////////////////////   Scrap code and spreadsheet reader changes 
/*
 * 
 * 
 * LINE 23 change 1 remove
 * 	private Boolean addMode = false;
	private String expectedExpId;
	
	LINE  39 change 2 remove
	if (!expId.equals(expectedExpId))
			throw new SampleSheetIOException("Experiment id expected (" + expectedExpId + ") does not match the experiment on the submission sheet (" + clientInfo.getExperimentId() + ")",  1, "Client Data");
	
	Line 55 change 3  
	change
	try
			{
			Integer nSamples = experimentService.countSamplesAsInt(expId);
			if (!addMode && nSamples > 0) 
				{
				String msg = "Experiment already has samples.  Please use the Add Samples button at the bottom of the Sample Tools Panel page to append (additional) samples to an existing experiment.";
				throw new SampleSheetIOException(msg,  getExpIdRow(), "Client Data");
				}
			
			if (addMode && nSamples < 1)
				{
				String msg = "No samples have been registered for experiment " + expectedExpId + ". Please register new samples via the link on the Register Samples panel." ;
				throw new SampleSheetIOException(msg,  getExpIdRow(), "Client Data");
				}
			}
	to 
	
			try
			{
			Integer nSamples = experimentService.countSamplesAsInt(expId);
			if (nSamples > 0) 
				{
				String msg = "Experiment already has samples.  Please use the Add Samples button at the bottom of the Sample Tools Panel page to append (additional) samples to an existing experiment.";
				throw new SampleSheetIOException(msg,  getExpIdRow(), "Client Data");
				}
			}
			
LINE 76 change 4
remove 
	public Boolean getAddMode()
		{
		return addMode;
		}

	public String getExpectedExpId()
		{
		return expectedExpId;
		}

	public void setAddMode(Boolean addMode)
		{
		this.addMode = addMode;
		}

	public void setExpectedExpId(String expectedExpId)
		{
		this.expectedExpId = expectedExpId;
		}					
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */

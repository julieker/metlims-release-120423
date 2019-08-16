// GeneratedWorklistDTO.java
// Written by Jan Wigginton June 2015
package edu.umich.brcf.metabolomics.layers.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.wicket.Session;

import edu.umich.brcf.metabolomics.panels.workflow.worklist_builder.WorklistSimple;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;


public class GeneratedWorklistDTO implements Serializable
	{
	public static GeneratedWorklistDTO instance(WorklistSimple worklistSimple)
		{
		String platformId = worklistSimple.getSelectedPlatformId();
		String expId = worklistSimple.getDefaultExperimentId();
		String assayId = worklistSimple.getDefaultAssayId();
		String instrumentId = StringParser.parseName(worklistSimple
				.getSelectedInstrument());
		String injectionMode = worklistSimple.getSelectedMode();
		String randomizationType = ""; // worklistSimple.getRandomizationType();
		String randomizationFileName = ""; // worklistSimple.getRandomizationFile();

		String generatedBy = ((MedWorksSession) Session.get())
				.getCurrentUserId();
		Calendar dateGenerated = DateUtils.calendarFromDateStr(
				worklistSimple.getRunDate(), "MM/dd/yyyy");
		String fileName = worklistSimple.getWorklistName();

		return new GeneratedWorklistDTO(null, platformId, expId, assayId,
				instrumentId, injectionMode, randomizationType,
				randomizationFileName, generatedBy, dateGenerated, fileName);
		}

	public static GeneratedWorklistDTO instance(String worklistId,
			String platformId, String expId, String assayId,
			String instrumentId, String injectionMode,
			String randomizationType, String randomizationFileName,
			String generatedBy, Calendar dateGenerated, String fileName)
		{
		return new GeneratedWorklistDTO(worklistId, platformId, expId, assayId,
				instrumentId, injectionMode, randomizationType,
				randomizationFileName, generatedBy, dateGenerated, fileName);
		}

	private String worklistId;
	private String platformId;
	private String expId;
	private String assayId;
	private String instrumentId;
	private String injectionMode;
	private String randomizationType;
	private String randomizationFileName;
	private String generatedBy;
	private String dateGenerated;
	private String fileName;

	public GeneratedWorklistDTO()
		{
		}

	public GeneratedWorklistDTO(String worklistId, String platformId,
			String expId, String assayId, String instrumentId,
			String injectionMode, String randomizationType,
			String randomizationFileName, String generatedBy,
			Calendar dateGenerated, String fileName)
		{
		this.worklistId = worklistId;
		this.platformId = platformId;
		this.expId = expId;
		this.assayId = assayId;
		this.instrumentId = instrumentId;
		this.injectionMode = injectionMode;
		this.randomizationType = randomizationType;
		this.randomizationFileName = randomizationFileName;
		this.generatedBy = generatedBy;
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		this.dateGenerated = sdf.format(dateGenerated.getTime());
		this.fileName = fileName;
		}

	// ////// Getters/Setters /////////////////////////////////////////

	public String getWorklistId()
		{
		return worklistId;
		}

	public void setWorklistId(String worklistId)
		{
		this.worklistId = worklistId;
		}

	public String getPlatformId()
		{
		return platformId;
		}

	public void setPlatformId(String platformId)
		{
		this.platformId = platformId;
		}

	public String getExpId()
		{
		return expId;
		}

	public void setExpId(String expId)
		{
		this.expId = expId;
		}

	public String getAssayId()
		{
		return assayId;
		}

	public void setAssayId(String assayId)
		{
		this.assayId = assayId;
		}

	public String getInstrumentId()
		{
		return instrumentId;
		}

	public void setInstrumentId(String instrumentId)
		{
		this.instrumentId = instrumentId;
		}

	public String getInjectionMode()
		{
		return injectionMode;
		}

	public void setInjectionMode(String injectionMode)
		{
		this.injectionMode = injectionMode;
		}

	public String getRandomizationType()
		{
		return randomizationType;
		}

	public void setRandomizationType(String randomizationType)
		{
		this.randomizationType = randomizationType;
		}

	public String getRandomizationFileName()
		{
		return randomizationFileName;
		}

	public void setRandomizationFileName(String randomizationFileName)
		{
		this.randomizationFileName = randomizationFileName;
		}

	public String getGeneratedBy()
		{
		return generatedBy;
		}

	public void setGeneratedBy(String generatedBy)
		{
		this.generatedBy = generatedBy;
		}

	public String getDateGenerated()
		{
		return dateGenerated;
		}

	public void setDateGenerated(String dateGenerated)
		{
		this.dateGenerated = dateGenerated;
		}

	public String getFileName()
		{
		return fileName;
		}

	public void setFileName(String fileName)
		{
		this.fileName = fileName;
		}

	}

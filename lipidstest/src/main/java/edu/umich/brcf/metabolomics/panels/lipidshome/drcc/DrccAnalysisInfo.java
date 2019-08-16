//  DrccAnalysisInfo.java 
//  Written by Jan Wigginton	//
//  July 2015


package edu.umich.brcf.metabolomics.panels.lipidshome.drcc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.umich.brcf.shared.util.utilpackages.DateUtils;


public class DrccAnalysisInfo implements Serializable
	{
	ArrayList <DrccInfoField> infoFields = new ArrayList<DrccInfoField>();
	
	public static final int IDX_DRCC_ANALYSIS_DATE_FIELD = 17;
	public static final int IDX_DRCC_RAW_DIRECTORY_FIELD = 13;
	public static final int IDX_DRCC_PROCESS_DIRECTORY_FIELD = 14;

	Date analysisDate;
	String selectedMode;
	DrccAnalysisInfo()
		{
		}
	
	DrccAnalysisInfo(String expId, String selectedMode, Date analysisDate)
		{
		this.analysisDate = analysisDate;
		this.selectedMode = selectedMode;
		
		infoFields.add(new DrccInfoField("Ion Mode", "ionMode", "NEGATIVE", "POSITIVE"));
		infoFields.add(new DrccInfoField("Laboratory Name", "laboratoryName", "Michigan Regional Comprehensive Metabolomics Research Core", 2));
		infoFields.add(new DrccInfoField("Operator Name", "operatorName", "TM Ranjendiren", 2)) ;
		infoFields.add(new DrccInfoField( "Analysis Type*", "analysisType", "MS", 2));
		infoFields.add(new DrccInfoField("Instrument Name*", "instrumentName", "AB Sciex Triple TOF 5600", 2));
		infoFields.add(new DrccInfoField("Instrument Manufacturer*", "instrumentManufacturer", "ABSciex", 2));
		infoFields.add(new DrccInfoField("Software Version", "softwareVersion", "ABSciex Analyst", 2));
		infoFields.add(new DrccInfoField("Detector Type", "detectorType", "", 2));
		infoFields.add(new DrccInfoField("Data Acquisition File Name", "dataAcquisitionFileName", "", 2));
		infoFields.add(new DrccInfoField("Data Processing Method File Name", "dataProcessingMethodFileName","", 2));
		infoFields.add(new DrccInfoField("Analysis Protocol Id", "analysisProtocolId", "A004 (Shotgun lipidomics)", 2));
		infoFields.add(new DrccInfoField("Acquisition Id", "acquisitionId","", 2));
		infoFields.add(new DrccInfoField("Instrument Parameters File", "instrumentParametersFile", "", 2));
		infoFields.add(new DrccInfoField("Raw Data File Directory",  "rawDataDirectory", "", 2));
		infoFields.add(new DrccInfoField("Process Data File Directory", "processedDataDirectory", "", 2));
		infoFields.add(new DrccInfoField("Randomization Order", "randomizationOrder", "", 2));
		infoFields.add(new DrccInfoField("Data Format", "dataFormat", ".wiff, .txt", 2));
		infoFields.add(new DrccInfoField("Acquisition Date", "acquisitionDateAsString", DateUtils.dateAsString(analysisDate), 2));
		infoFields.add(new DrccInfoField("Acquisition Time", "acquisitionTimeAsString", "", 2));
		infoFields.add(new DrccInfoField("Analysis Comments", "analysisComments", "", 2));
		
		
		updateDirectoryInfo(expId, 0, "Neg");
		updateDirectoryInfo(expId, 1, "Pos");
		
		}
	
	private void updateDirectoryInfo(String expId, int idx, String mode)
		{
		String directory = buildDirectoryName(expId, idx, mode);
		setRawDirectories(directory, idx);
		setProcessedDirectories(directory, idx);
		}


	private String  buildDirectoryName(String expId, int idx, String mode)
		{
		String year = "" + (analysisDate.getYear() + 1900);
		
		int monthInt = analysisDate.getMonth() + 1;
		String month = (monthInt < 10 ? "0" : "") + monthInt;
		
		int dayInt = analysisDate.getDate();
		String day = (dayInt < 10 ? "0" : "") + dayInt;
		
		
		String dateString = year + month + day;
		return (year + "\\" + dateString  + "_" + expId + "\\" + mode);
		}
	
	public List <DrccInfoField> getInfoFields()
		{
		return infoFields;
		}

	
	public List <DrccInfoField> getInfoValue()
		{
		return infoFields;
		}
	
	public String getInfoValue(int i)
		{
		return infoFields.get(i).getFieldValues(0);
		}

	public void setInfoValue(int i, String value)
		{
		infoFields.get(i).setFieldValues(0, value);
		}
	

	public String getAnalysisDate() 
		{
		return infoFields.get(IDX_DRCC_ANALYSIS_DATE_FIELD).getFieldValues(0);
		}

	
	public void setAnalysisDate(String date) 
		{
		infoFields.get(IDX_DRCC_ANALYSIS_DATE_FIELD).setFieldValues(0, date);
		}
	
	
	public void setAnalysisDates(String date, int j) 
		{
		infoFields.get(IDX_DRCC_ANALYSIS_DATE_FIELD).setFieldValues(j, date);
		}

	
	public String getAnalysisDates(int j)
		{
		return infoFields.get(IDX_DRCC_ANALYSIS_DATE_FIELD).getFieldValues(j);
		}
	
	
	public String getRawDirectories(int j)
		{
		return infoFields.get(IDX_DRCC_RAW_DIRECTORY_FIELD).getFieldValues(j);
		}
	//IN0024
	
	public String getProcessedDirectories(int j)
		{
		return infoFields.get(IDX_DRCC_PROCESS_DIRECTORY_FIELD).getFieldValues(j);
		}
	
	
	public void setRawDirectories(String directory, int j)
		{
		infoFields.get(IDX_DRCC_RAW_DIRECTORY_FIELD).setFieldValues(j, directory);
		}
	
	public void setProcessedDirectories(String directory, int j)
		{
		infoFields.get(IDX_DRCC_PROCESS_DIRECTORY_FIELD).setFieldValues(j, directory);
		}
	
	
	public void setViewNegativeMode(boolean printNegative)
		{
		for (int i = 0; i < infoFields.size(); i++)
			infoFields.get(i).setColPrinted(0, printNegative);
		}
	
	public void setViewPositiveMode(boolean printPositive)
		{
		for (int i = 0; i < infoFields.size(); i++)
			infoFields.get(i).setColPrinted(1, printPositive);
		}
	}
	
	
	










// DrccReport.java
// Written by Jan Wigginton July 2015

package edu.umich.brcf.metabolomics.panels.lipidshome.drcc;

import java.io.OutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.umich.brcf.shared.util.interfaces.IWriteableSpreadsheet;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;




public class DrccReport implements Serializable,  IWriteableSpreadsheet
	{

	String selectedExperiment, selectedMode;
	Date analysisDate;
	
	DrccProjectInfo projectInfo; 
	DrccStudyInfo studyInfo; 
	DrccStudyDesignInfoSet studyDesignInfoSet; 
	DrccSubjectInfoSet subjectInfoSet;
	DrccCollectionInfo collectionInfo; 
	DrccSamplePrepInfo samplePrepInfo; 
	DrccAnalysisInfo analysisInfo; 
	DrccMsInfo msInfo; 
	
	public DrccReport(String selectedExperiment, String selectedMode, Date analysisDate)
		{
		this.selectedExperiment = selectedExperiment;
		this.selectedMode = selectedMode;
		this.analysisDate = analysisDate;
		
		projectInfo = new DrccProjectInfo(selectedExperiment);
		studyInfo = new DrccStudyInfo(selectedExperiment);
		studyDesignInfoSet = new DrccStudyDesignInfoSet(selectedExperiment);
		subjectInfoSet = new DrccSubjectInfoSet(selectedExperiment);
		collectionInfo = new DrccCollectionInfo(selectedExperiment);
		samplePrepInfo = new DrccSamplePrepInfo(selectedExperiment);
		analysisInfo = new DrccAnalysisInfo(selectedExperiment, selectedMode, analysisDate);
		msInfo = new DrccMsInfo(selectedExperiment, selectedMode, analysisDate);
		}
	
	
	public void generateExcelReport(OutputStream output)
		{
		System.out.println("Generating out excel");
		Workbook workBook = new XSSFWorkbook();
		
		createSheet("Project", workBook, projectInfo.infoFields);
		createSheet("Study", workBook, studyInfo.infoFields);
		createStudyDesignInfoSheet("Study Design", workBook, studyDesignInfoSet.infoFields);
		createSubjectInfoSheet("Subjects", workBook, subjectInfoSet.infoFields);
		createSheet("Collection", workBook, collectionInfo.infoFields);
		createSheet("Sample Prep", workBook, samplePrepInfo.infoFields);
		createSheet("Analysis", workBook, analysisInfo.infoFields);
		createSheet("MS", workBook, msInfo.infoFields);
	
	    
	   // FileOutputStream out;
		try {
			//out = new FileOutputStream(fileName);
			workBook.write(output);
	        output.close();
			} 
		catch (Exception e) 
			{
			e.printStackTrace();
			}
		}
	
	public String getReportFileName()
		{
		String tag = "_" + selectedExperiment + "_" + selectedMode;
		try
			{
			String dtP = DateUtils.dateAsString(analysisDate, "MM/dd/yyyy");
			tag += ("_" + DateUtils.grabYYYYmmddString(dtP, "MM/dd/yyyy"));
			}
		catch (ParseException e){ }
		
		String fileName = "DRCCReport" + tag + (haveWorkbook() ? ".xlsx" : ".xls");
    	return fileName;
		}
	// MRC2 Subject
	private Boolean haveWorkbook()
		{
		return true;
		}
	
	public Sheet createSheet(String title, Workbook wb, List <DrccInfoField> list)
		{
        Sheet sheet = createEmptySheet(title, wb);
     
        if (list != null)
        	writeValuesToSheet(list, sheet);
        
        return sheet;
        }
	
	
	public Sheet createSubjectInfoSheet(String title, Workbook wb, List <DrccSubjectInfoItem> list)
		{
	    Sheet sheet = createEmptySheet(title, wb);
	    writeSubjectInfoValues(list, sheet);
	    return sheet;
	    }
	
	public Sheet createStudyDesignInfoSheet(String title, Workbook wb, List <DrccStudyDesignInfoItem> list)
		{
		List<String> labels = this.studyDesignInfoSet.getFactorLabels();
			 
	    Sheet sheet = createEmptySheet(title, wb);
	    Row row = sheet.createRow(0);
	    Cell cell = row.createCell(0);
	    cell.setCellValue("Researcher Subject Id");
	    cell = row.createCell(1);
	    cell.setCellValue("Researcher Sample Id");
	    
	    for (int i =0; i < labels.size(); i++)
	    	{
	    	cell = row.createCell(i + 2);
	    	cell.setCellValue(labels.get(i));
	    	}
        
	    writeStudyDesignInfoValues(list, sheet);
	    return sheet;
	    }

	//has been written to 
	public Sheet createEmptySheet(String title, Workbook wb)
		{
		Sheet sheet = wb.createSheet(title);
	    sheet.setPrintGridlines(false);
	    sheet.setDisplayGridlines(true);
	
	    PrintSetup printSetup = sheet.getPrintSetup();
	    printSetup.setLandscape(true);
	    sheet.setFitToPage(true);
	    sheet.setHorizontallyCenter(true);
	
	    sheet.setColumnWidth(0, 50*256);
	    sheet.setColumnWidth(1, 50*256);
	    sheet.setColumnWidth(2, 50*256);
	    sheet.setColumnWidth(3, 50*256);
	    sheet.setColumnWidth(4, 50*256);
	    sheet.setColumnWidth(5, 50*256);
	    sheet.setColumnWidth(6, 50*256);
	
	    Row titleRow = sheet.createRow(0);
	    for (int i = 1; i <= 7; i++) 
	        titleRow.createCell(i); //.setCellStyle(styles.get("title"));
	        
	    return sheet;
	    }
	
	
	public void writeValuesToSheet(List <DrccInfoField> list, Sheet sheet)
		{
		for (int i = 0; i < list.size(); i++)
			{
			DrccInfoField field = list.get(i);
			Row row = sheet.createRow(i + 1);
	        Cell cell = row.createCell(0);
	        cell.setCellValue(field.getFieldLabel());
	        
	        for (int j = 0; j < field.getFieldValues().size();j++)
		        {
		        cell = row.createCell(j + 1);
		        if (field.colsToPrint.get(j).equals(true))
		        		cell.setCellValue(field.getFieldValues(j));
		        }
			}
		}
	
	
	public void writeSubjectInfoValues(List <DrccSubjectInfoItem> list, Sheet sheet)
		{
		Row headerrow = sheet.createRow(0);
		List <String> headers = Arrays.asList(new String []{"Subject ID", "Subject Type", "Subject Species", "Taxonomy ID"});
		
		for (int i = 0; i < headers.size(); i++)
			{
			Cell cell = headerrow.createCell(i);
			cell.setCellValue(headers.get(i));
			}
		
        for (int i = 0;i < list.size(); i++)
        	{
        	DrccSubjectInfoItem item = list.get(i);
        	List <String> tokens = item.toTokens();
        	
        	Row row = sheet.createRow(i + 1);
	        Cell cell = row.createCell(0);
	        
	        
	        for (int j = 0; j < tokens.size();j++)
		        {
		        cell = row.createCell(j);
		        cell.setCellValue(tokens.get(j));
		        }
        	}
		}
	
	public void writeStudyDesignInfoValues(List <DrccStudyDesignInfoItem> list, Sheet sheet)
		{
	    for (int i = 0;i < list.size(); i++)
	    	{
	    	DrccStudyDesignInfoItem item = list.get(i);
	    	List <String> tokens = item.toTokens();
	    	Row row = sheet.createRow(i + 1);
	        Cell cell = row.createCell(0);
	        
	        for (int j = 0; j < tokens.size();j++)
		        {
		        cell = row.createCell(j);
		        cell.setCellValue(tokens.get(j));
		        }
	    	}
		}
	}
	
	
	

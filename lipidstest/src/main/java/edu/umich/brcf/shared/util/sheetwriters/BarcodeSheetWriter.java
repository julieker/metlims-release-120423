////////////////////////////////////////////////////
// BarcodeSheetWriter.java
// Written by Jan Wigginton, Jun 22, 2016
////////////////////////////////////////////////////

package edu.umich.brcf.shared.util.sheetwriters;

import java.io.OutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.wicket.injection.Injector;

import edu.umich.brcf.shared.util.interfaces.IWriteableSpreadsheet;
import edu.umich.brcf.shared.util.io.PoiUtils;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;




public class BarcodeSheetWriter extends SubmissionSheetWriter implements Serializable, IWriteableSpreadsheet 
{
private String selectedExperiment; 
private List<String> barcodesToPrint;
	
public BarcodeSheetWriter(String selectedExperiment, List<String> barcodesToPrint)
	{
	this();
	this.barcodesToPrint = barcodesToPrint;
	}


public BarcodeSheetWriter()
	{
	super();
	Injector.get().inject(this);
	}


@Override
public void generateExcelReport(OutputStream output)
	{
	Workbook workBook = new XSSFWorkbook();		
	createIdSheet("Id Sheet", workBook, barcodesToPrint);
	
	try 
		{
		workBook.write(output);
		output.close();
		} 
	catch (Exception e)  {  e.printStackTrace(); }
	}


@Override
public String getReportFileName()
	{
	String tag = "";
	if (!StringUtils.isEmptyOrNull(selectedExperiment))
		tag += "_" + selectedExperiment;
	
	try
		{
		String dtP = DateUtils.dateAsString(new Date(), "MM/dd/yyyy");
		tag += ("_" + DateUtils.grabYYYYmmddString(dtP, "MM/dd/yyyy"));
		}
	catch (ParseException e){ }
	
	return ("BarcodeIds" + tag + (haveWorkbook() ? ".xlsx" : ".xlsx"));
	}


private Boolean haveWorkbook()
	{
	return true;
	}


public Sheet createIdSheet(String title, Workbook workBook, List<String> barcodesToPrint)
	{
	Sheet sheet = createEmptySheet(title,workBook, 3, null, 1);
	
	sheet.setColumnWidth(0, 5 * 256);
	sheet.setColumnWidth(1, 30 * 256);
	sheet.setColumnWidth(2, 40* 256);
	
	PoiUtils.createBlankRow(1,  sheet);
	
	int rowCt = 2; 
	
	PoiUtils.createBlankRow(rowCt++,  sheet);
	PoiUtils.createBlankRow(rowCt++,  sheet);
	
	List <String> headers = Arrays.asList(new String []{ "Barcode ID"}); 			
	
	sheet.createRow(rowCt);
	for (int i = 0; i < headers.size(); i++)
		PoiUtils.createRowEntry(rowCt, i + 1, sheet, headers.get(i), grabStyleBlue(workBook));
	
	rowCt++;
	for (String item : barcodesToPrint)
		{
		Row row = sheet.createRow(rowCt);
		XSSFCellStyle style = grabStyleWhite(workBook, true);
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setIndention((short) 2);
		Cell cell = PoiUtils.createRowEntry(rowCt, 1, sheet, item, style);
		rowCt++;
		}
	
	return sheet;
	}


public List<String> getBarcodesToPrint()
	{
	return barcodesToPrint;
	}

public void setBarcodesToPrint(List<String> barcodesToPrint)
	{
	this.barcodesToPrint = barcodesToPrint;
	}
}





/*

public class BarcodeSheetWriter extends SubmissionSheetWriter implements Serializable, IWriteableSpreadsheet 
	{
	private String selectedExperiment; 
	private List<String> barcodesToPrint;
	
	public BarcodeSheetWriter(String selectedExperiment, List<String> barcodesToPrint)
		{
		this();
		this.barcodesToPrint = barcodesToPrint;
		}
	
	public BarcodeSheetWriter()
		{
		super();
		Injector.get().inject(this);
		}
	
	
	@Override
	public void generateExcelReport(OutputStream output)
		{
		Workbook workBook = new XSSFWorkbook();		
		createIdSheet("Id Sheet", workBook, barcodesToPrint);
		
		try 
			{
			workBook.write(output);
			output.close();
			} 
		catch (Exception e)  {  e.printStackTrace(); }
		}
	
	
	@Override
	public String getReportFileName()
		{
		String tag = "";
		if (!StringUtils.isEmptyOrNull(selectedExperiment))
			tag += "_" + selectedExperiment;
		
		try
			{
			String dtP = DateUtils.dateAsString(new Date(), "MM/dd/yyyy");
			tag += ("_" + DateUtils.grabYYYYmmddString(dtP, "MM/dd/yyyy"));
			}
		catch (ParseException e){ }
		
		return ("BarcodeIds" + tag + (haveWorkbook() ? ".xlsx" : ".xlsx"));
		}
	
	
	private Boolean haveWorkbook()
		{
		return true;
		}
	
	
	public Sheet createIdSheet(String title, Workbook workBook, List<String> barcodesToPrint)
		{
		Sheet sheet = createEmptySheet(title,workBook, 3, null, 1);
		
		sheet.setColumnWidth(0, 5 * 256);
		sheet.setColumnWidth(1, 30 * 256);
		sheet.setColumnWidth(2, 40* 256);
		
		PoiUtils.createBlankRow(1,  sheet);
		
		int rowCt = 2; 
		PoiUtils.createBlankRow(rowCt++,  sheet);
		PoiUtils.createBlankRow(rowCt++,  sheet);
		
		List <String> headers = Arrays.asList(new String []{ "Barcode ID"}); 			
		
		sheet.createRow(rowCt);
		for (int i = 0; i < headers.size(); i++)
			PoiUtils.createRowEntry(rowCt, i + 1, sheet, headers.get(i), grabStyleBlue(workBook));
	
		rowCt++;
		for (String item : barcodesToPrint)
			{
			Row row = sheet.createRow(rowCt);
			XSSFCellStyle style = grabStyleWhite(workBook, true);
			style.setAlignment(HorizontalAlignment.LEFT);
			style.setIndention((short) 2);
			Cell cell = PoiUtils.createRowEntry(rowCt, 1, sheet, item, style);
			rowCt++;
			}
		
		return sheet;
		}
	}

*/


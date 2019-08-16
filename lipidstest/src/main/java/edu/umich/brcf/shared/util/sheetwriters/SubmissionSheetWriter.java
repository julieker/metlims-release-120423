/////////////////////////////////////////////
// SubmissionSheetWriter.java
// Written by Jan Wigginton, January 2016
/////////////////////////////////////////////
package edu.umich.brcf.shared.util.sheetwriters;

import java.io.Serializable;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import edu.umich.brcf.shared.util.io.SpreadSheetWriter;


public class SubmissionSheetWriter extends SpreadSheetWriter implements Serializable
	{
	public SubmissionSheetWriter() {}
	
	public Sheet createEmptySheet(String title, Workbook workBook, int page, List <String> headers, int firstDataCol)
		{
		Sheet sheet = workBook.createSheet(title);
		sheet.setPrintGridlines(false);
	    sheet.setDisplayGridlines(false);
	    sheet.getPrintSetup().setLandscape(true);
	    sheet.setFitToPage(true);
	    sheet.setHorizontallyCenter(true);
	    sheet.setZoom(75, 100);
	    sheet.protectSheet("jan");
	    
	    setColumnWidths(sheet, page, headers, firstDataCol);
	
	    return sheet;
	    }
	
	
	public void createInstructionsHeader(Sheet sheet, Workbook workBook, String instructions, List<String> headers, int firstDataCol)
		{
		createInstructionsHeader(sheet, workBook, instructions, headers, firstDataCol, 0);
		}
	

	public void setColumnWidths(Sheet sheet, int page, List <String> headers, int firstDataCol)
		{
		sheet.setColumnWidth(0, 50*256);
	    sheet.setColumnWidth(1, (page == 1 ? 2 * 256 : 5 * 256));
	    int lastCol = headers == null ? 13 + firstDataCol : headers.size() + firstDataCol;
	    for (int i = firstDataCol; i < lastCol; i++)
	    	sheet.setColumnWidth(i,  page == 1 ? 50 * 256 : 30 * 256);
	    	
	    if (page == 3)
	    	for (int i = firstDataCol; i < lastCol; i++)
	    		sheet.setColumnWidth(i, i < 6 ? 50 * 256 : 70 * 256);
	    else 
	    	for (int i = 0; i < 100; i++)
	    		sheet.setColumnWidth(i, 40 * 256);
		}
		
	
	public void createInstructionsHeader(Sheet sheet, Workbook workBook, String instructions, List<String> headers, int firstDataCol, int firstDataRow)
		{
		Row rowTop = sheet.createRow(firstDataRow);
		
		Cell cellInfo = rowTop.createCell(0);
		
		int top = headers == null ? firstDataCol + 13 : firstDataCol + headers.size();
		for (int i = firstDataCol; i <top; i++)
			rowTop.createCell(i);
		
		
		top = headers == null ? firstDataCol + 6 : firstDataCol + headers.size();
		for (int i = firstDataCol + 6; i <top; i++)
			rowTop.createCell(i);
		
		cellInfo.setCellStyle(grabStyleInstructions(workBook, false, true));
		cellInfo.setCellValue(instructions);
		sheet.addMergedRegion(new CellRangeAddress(0,0,0, firstDataCol + 5));
		
		cellInfo = rowTop.createCell(firstDataCol + 6);
		cellInfo.setCellStyle(grabStyleInstructions(workBook, false, true));
		cellInfo.setCellValue("Test instructions");
		
		sheet.addMergedRegion(new CellRangeAddress(0,0,firstDataCol + 6, firstDataCol + 10));
		}
	}

////////////////////////////////////////////
//SpreadSheetReader.java
//Written by Jan Wigginton November 2015
/////////////////////////////////////////////

package edu.umich.brcf.shared.util.io;


import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.util.file.Folder;
import edu.umich.brcf.shared.util.METWorksException;


public class SpreadSheetReader implements Serializable 
		{
		public SpreadSheetReader()
			{
			}
	
		protected Workbook createClientSideWorkBook(File newFile, boolean isExcel)
			{
			try
				{
				if(isExcel)
					{
					POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(newFile));
					return new HSSFWorkbook(fs);
					}
				
				OPCPackage pkg = OPCPackage.open(newFile);
				return new XSSFWorkbook(pkg);
				}
			catch (Exception e)
				{
				String msg = "Error while opening spreadsheet";
				System.out.println(msg);
				}
				
			return null;
			}
	
	
	// TO DO : Handle individual exceptions 
	protected Workbook createWorkBook(File newFile, FileUpload upload) throws METWorksException// throws FileNotFoundException, IOException, IllegalFormatException
		{
		try
			{
			if(upload.getContentType().equalsIgnoreCase("application/vnd.ms-excel"))
			{
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(newFile));
			return new HSSFWorkbook(fs);
			}
			OPCPackage pkg = OPCPackage.open(newFile);
			return new XSSFWorkbook(pkg);
			}
		catch (Exception e)
			{
			String msg = "Error while opening spreadsheet";
			e.printStackTrace(); // JAK issue 158
			throw new METWorksException(msg); //e.getMessage());
			}
		}
	
	
	protected File uploadFile(FileUpload uploadedFile) throws METWorksException
		{
		File newFile = new File(uploadedFile.getClientFileName());
		if (newFile.exists())  {   newFile.delete(); }
		
		try 
			{
			newFile.createNewFile();
			uploadedFile.writeTo(newFile);
			} 
		catch (Exception e) 
			{
			throw new METWorksException("Error while uploading file " + uploadedFile.getClientFileName());
			}	
		
		return newFile;
		}
	
	
	protected void checkFileExists(File newFile)
		{
		// if (newFile.exists() && !Files.remove(newFile))
		//    throw new IllegalStateException("Unable to overwrite " + newFile.getAbsolutePath());
		}
	
	
	protected Folder getUploadFolder()
		{
		Folder uploadFolder = new Folder(System.getProperty("java.io.tmpdir"), "sample-uploads");
		uploadFolder.mkdirs();
		return (uploadFolder);
		}
	
	
	protected String getDataAt(int row, int col,  Sheet sheet, boolean report)
		{
		Row rowLine = sheet.getRow(row);
		Cell cell = rowLine.getCell((short) col);
		if (report)
		System.out.println("Cell is " + (cell == null ? "null" : "not null"));
		
		String cellStr = (cell == null ? "" : cell.toString().trim());
		if (report)
		System.out.println(cellStr + " for col" + col + " and row " + row);
		
		return cellStr;
		}
	
	
	
	protected Boolean isRowEmpty(Row row) 
		{
	    for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) 
	    	{
	        Cell cell = row.getCell(c);
	        if (!isCellEmpty(cell))
	        	return false;
	    	}
	    
	    return true;
		}
	
	
	
	protected Boolean isCellEmpty(Cell cell)
		{
	    if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK)
	        return false;
		
		return true;
		}
	}

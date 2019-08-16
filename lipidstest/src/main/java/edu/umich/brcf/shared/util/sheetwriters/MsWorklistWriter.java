////////////////////////////////////////////////////
// MsWorklistWriter.java
// Written by Jan Wigginton, Jul 27, 2016
////////////////////////////////////////////////////

package edu.umich.brcf.shared.util.sheetwriters;

import java.awt.Color;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.wicket.Session;
import org.apache.wicket.injection.Injector;

import edu.umich.brcf.metabolomics.panels.workflow.worklist_builder.WorklistItemSimple;
import edu.umich.brcf.metabolomics.panels.workflow.worklist_builder.WorklistSimple;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.interfaces.IWriteableSpreadsheet;
import edu.umich.brcf.shared.util.io.PoiUtils;
import edu.umich.brcf.shared.util.io.StringUtils;

import edu.umich.brcf.shared.util.io.SpreadSheetWriter;



public class MsWorklistWriter extends SpreadSheetWriter implements Serializable, IWriteableSpreadsheet
	{
	private String selectedExperiment; 
	protected WorklistSimple worklist;
	// issue 450
    private final int CONTROLNAMECOL = 2;
	private final int CONTROLPOSCOL = 3;
	private final int IDDADATAFILECOL = 7;
	
	// issue 450
	public MsWorklistWriter( WorklistSimple worklist)
		{
		this();
		this.worklist = worklist;
		}
	
	public MsWorklistWriter()
		{
		super();
		Injector.get().inject(this);
		}


	@Override
	public void generateExcelReport(OutputStream output)
		{
		Workbook workBook = new XSSFWorkbook();	
		// issue 450
		if (worklist.getSelectedMode().equals("Positive + Negative"))
		    {	
			createWorklistSheet("Worklist Builder Sheet - Pos", workBook, worklist, worklist.isPlatformChosenAs("agilent"), "Positive");
			createWorklistSheet("Worklist Builder Sheet - Neg", workBook, worklist, worklist.isPlatformChosenAs("agilent"), "Negative");
		    }
		else
		    createWorklistSheet("Worklist Builder Sheet", workBook, worklist, worklist.isPlatformChosenAs("agilent"), worklist.getSelectedMode());
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
		return worklist.getWorklistName() + ".xlsx";
		}
		
	
	private Boolean haveWorkbook()
		{
		return true;
		}
	
		
	public Sheet createEmptySheet(String title, Workbook workBook, int page, List <String> headers, int firstDataCol)
		{
		Sheet sheet = workBook.createSheet(title);
	    sheet.getPrintSetup().setLandscape(true);
	    sheet.setFitToPage(true);
	    sheet.setHorizontallyCenter(true);
	    sheet.setZoom(75, 100);
	        
	    for (int i = 0; i < headers.size(); i++) 
	    	{
	    	if (i == 1)
	    		sheet.setColumnWidth(i + 1, 50 * 256);
	    	else
	    		sheet.setColumnWidth(i + 1, i > 5 ?  90 * 256 : 30 * 256);
	    	}
	    
	    return sheet;
	    }
	
	// issue 450
	public Sheet createWorklistSheet(String title, Workbook workBook, WorklistSimple worklist, boolean isPlatformAgilent, String strMode)
		{
		// issue 450
		XSSFCellStyle styleHorizontalAndWhite = grabStyleWhite(workBook, true);
        styleHorizontalAndWhite.setAlignment(HorizontalAlignment.LEFT);
        styleHorizontalAndWhite.setIndention((short) 2);
		List<WorklistItemSimple> items = worklist.getItems();
		List<String> headers = worklist.getColTitles();
		int rowCt = 0;
	
		// issue 450
		//Sheet sheet = createEmptySheet("Worklist Builder Sheet",  workBook, 1, worklist.getColTitles(), 1);
		Sheet sheet = createEmptySheet(title,  workBook, 1, worklist.getColTitles(), 1);
		PoiUtils.createBlankRow(rowCt,  sheet);
		for (int i = 0; i < headers.size(); i++)
			PoiUtils.createRowEntry(rowCt, i + 1, sheet, headers.get(i), grabStyleBlue(workBook));		
		rowCt++;
		for (WorklistItemSimple item : items)
			{
			String itemStr = item.toCharDelimited(",");	
			String [] tokens = StringUtils.splitAndTrim(itemStr, ",", true);
			
			PoiUtils.createBlankRow(rowCt,  sheet);
			for (int i = 0; i < tokens.length; i++)
				{				
				// issue 410
				if (! ( i== 6 && tokens[i].length() >= 2))
				     PoiUtils.createRowEntry(rowCt, i + 1, sheet, i==0 && isPlatformAgilent ? "" : tokens[i], styleHorizontalAndWhite);
				// issue 450
				else
					 PoiUtils.createRowEntry(rowCt, i+ 1, sheet, tokens[i].substring(0,tokens[i].length() -2 ) +   (strMode.equals("Positive") ? "-P" : "-N"), styleHorizontalAndWhite);					
			    }
			rowCt++;
			}
		// issue 432
		printOutIDDA  (workBook, sheet, rowCt, strMode) ; 
		return sheet;
		}
	
	// issue 432
	public String grabPlatePosition(String lastPoolName)
		{
		for (WorklistItemSimple item : worklist.getItems())	
			{
			if (item.getSampleName().equals(lastPoolName))
				return item.getSamplePosition();
			}
		return null;
		}
	
	// issue 432
	public void printOutIDDA(Workbook workBook, Sheet sheet, int rowCt, String strMode)
		{
		Font font = workBook.createFont();
	    XSSFCellStyle styleColorBlue = grabStyleWhite(workBook, true);
        styleColorBlue.setAlignment(HorizontalAlignment.LEFT);
		styleColorBlue.setIndention((short) 2);
        styleColorBlue.setFillForegroundColor((IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex()));
        styleColorBlue.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        styleColorBlue.setFont(font);
            
        XSSFCellStyle styleStandard = grabStyleWhite(workBook, true);
        styleStandard.setAlignment(HorizontalAlignment.LEFT);
        styleStandard.setIndention((short) 2);
            
        XSSFCellStyle styleItalic = grabStyleWhite(workBook, true);
        styleItalic.setAlignment(HorizontalAlignment.LEFT);
        styleItalic.setIndention((short) 2);
        styleItalic.setFont(font);

        PoiUtils.createBlankRow(rowCt++,  sheet);
        PoiUtils.createBlankRow(rowCt++,  sheet);
            
        int startPoint = worklist.getMasterPoolsBefore() + worklist.getLastPoolBlockNumber() + 1;
        String iddaPlatePos = grabPlatePosition("CS00000MP-" + startPoint) ;
        String iddaStr = "";
        String fileStr = worklist.grabOutputFileNameIDDA();
        // issue 456
        Integer numToPad = startPoint + worklist.getMasterPoolsAfter() -1;
        numToPad = numToPad.toString().length();
		for (int i = startPoint; i < startPoint + worklist.getMasterPoolsAfter();i++)
			{
			iddaStr = 	"CS00000MP-" + String.format("%0" + numToPad +"d",i); // issue 456
			for (int j = 0; j <= ((MedWorksSession) Session.get()).getNCE10Reps();j++)
			    {
				if (((MedWorksSession) Session.get()).getNCE10Reps() == 0)
					continue;
				PoiUtils.createBlankRow(rowCt,  sheet);
				if (j==0) // issue 453
					fileStr = worklist.grabOutputFileNameIDDA() + "-"  + iddaStr + "-" + (strMode.contains("Positive") ? "P" : "N") + ".d"  ;
				else
					fileStr = worklist.grabOutputFileNameIDDA() + "-" +iddaStr +  "-IDDA_ce10" + "_" + j + "-" + (strMode.contains("Positive") ? "P" : "N") +   ".d";
			    PoiUtils.createRowEntry(rowCt, CONTROLNAMECOL, sheet, iddaStr, j == 0 ? styleItalic : styleStandard );
                PoiUtils.createRowEntry(rowCt,CONTROLPOSCOL, sheet, iddaPlatePos, j == 0 ? styleItalic : styleStandard);
                PoiUtils.createRowEntry(rowCt,IDDADATAFILECOL, sheet, fileStr, j == 0 ? styleColorBlue : styleStandard); //use constant
			    rowCt ++;
			    }
			for (int j = 0; j <= ((MedWorksSession) Session.get()).getNCE20Reps();j++)
			    {
				if (((MedWorksSession) Session.get()).getNCE20Reps() == 0)
					continue;
				PoiUtils.createBlankRow(rowCt,  sheet);
				if (j==0) // issue 453
					fileStr = worklist.grabOutputFileNameIDDA() + "-"  + iddaStr + "-" + (strMode.contains("Positive") ? "P" : "N") + ".d"  ;
				else
			        fileStr = worklist.grabOutputFileNameIDDA() + "-" +iddaStr +  "-IDDA_ce20" + "_" + j + "-" + (strMode.contains("Positive") ? "P" : "N") +   ".d";
				PoiUtils.createRowEntry(rowCt, CONTROLNAMECOL, sheet, iddaStr, j == 0 ? styleItalic : styleStandard );
	            PoiUtils.createRowEntry(rowCt, CONTROLPOSCOL, sheet, iddaPlatePos, j == 0 ? styleItalic : styleStandard);
	            PoiUtils.createRowEntry(rowCt, IDDADATAFILECOL, sheet, fileStr, j == 0 ? styleColorBlue : styleStandard); //use constant
			    rowCt ++;
			    }
			for (int j = 0; j <= ((MedWorksSession) Session.get()).getNCE40Reps();j++)
			    { 
				if (((MedWorksSession) Session.get()).getNCE40Reps() == 0)
					continue;
				PoiUtils.createBlankRow(rowCt,  sheet);
			    if (j==0) // issue 453
					fileStr = worklist.grabOutputFileNameIDDA() + "-"  + iddaStr + "-" + (strMode.contains("Positive") ? "P" : "N") + ".d"  ;
				else
					fileStr = worklist.grabOutputFileNameIDDA() + "-" +iddaStr +  "-IDDA_ce40" + "_" + j + "-" + (strMode.contains("Positive") ? "P" : "N") +   ".d";
			    PoiUtils.createRowEntry(rowCt, CONTROLNAMECOL, sheet, iddaStr, j == 0 ? styleItalic : styleStandard );
	            PoiUtils.createRowEntry(rowCt, CONTROLPOSCOL, sheet, iddaPlatePos, j == 0 ? styleItalic : styleStandard);
	            PoiUtils.createRowEntry(rowCt, IDDADATAFILECOL, sheet, fileStr, j == 0 ? styleColorBlue : styleStandard); //use constant
			    rowCt ++;
			    }			
		    }
	    }
	}



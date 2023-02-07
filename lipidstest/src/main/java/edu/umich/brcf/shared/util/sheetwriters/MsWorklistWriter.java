////////////////////////////////////////////////////
// MsWorklistWriter.java
// Written by Jan Wigginton, Jul 27, 2016
////////////////////////////////////////////////////

package edu.umich.brcf.shared.util.sheetwriters;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.service.InstrumentService;
import edu.umich.brcf.metabolomics.panels.workflow.worklist_builder.WorklistBuilderPanel;
import edu.umich.brcf.metabolomics.panels.workflow.worklist_builder.WorklistItemSimple;
import edu.umich.brcf.metabolomics.panels.workflow.worklist_builder.WorklistSimple;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.interfaces.IWriteableSpreadsheetReturnStream;
import edu.umich.brcf.shared.util.io.PoiUtils;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;
import edu.umich.brcf.shared.util.io.SpreadSheetWriter;



public class MsWorklistWriter extends SpreadSheetWriter implements Serializable, IWriteableSpreadsheetReturnStream
	{
	
	// issue 287	
	@SpringBean
	private InstrumentService instrumentService;
	
	private String selectedExperiment; 
	protected WorklistSimple worklist;
	public OutputStream joutput = null;
	// issue 450
	
	// issue 179
    private final int CONTROLNAMECOL = 0;
	private final int CONTROLPOSCOL = 1;// issue 166
	private final int IDDADATAFILECOL = 3; // issue 166
	List <String> workListData = new ArrayList <String> ();
    public OutputStream outputReportStream;
    
    // issue 247
    
    
	WorklistBuilderPanel wpWriter;
	// issue 450
	public MsWorklistWriter( WorklistSimple worklist, WorklistBuilderPanel wp)
		{
		this();
		this.worklist = worklist;
		wpWriter = wp;
		}
	
	public MsWorklistWriter()
		{
		super();
		Injector.get().inject(this);
		}


	@Override
	public List<String> generateExcelReport(OutputStream output)
		{
		Workbook workBook = new XSSFWorkbook();	
		// issue 450
		if (worklist.getSelectedMode().equals("Positive + Negative"))
		    {	
			createWorklistSheet("Worklist Builder Sheet - Pos", workBook, worklist, worklist.isPlatformChosenAs("agilent"), "Positive");
			createWorklistSheet("Worklist Builder Sheet - Neg", workBook, worklist, worklist.isPlatformChosenAs("agilent"), "Negative");
		    }
		// issue 247
		else if (worklist.getSelectedMode().equals("Positive + Negative + CC"))
		    {	
			createWorklistSheet("Worklist Builder Sheet - Pos", workBook, worklist, worklist.isPlatformChosenAs("agilent"), "Positive");
			createWorklistSheet("Worklist Builder Sheet - Neg", workBook, worklist, worklist.isPlatformChosenAs("agilent"), "Negative");
			createWorklistSheet("Worklist Builder Sheet - Neg CC", workBook, worklist, worklist.isPlatformChosenAs("agilent"), "Negative");
		    }
		else
			createWorklistSheet("Worklist Builder Sheet", workBook, worklist, worklist.isPlatformChosenAs("agilent"), worklist.getSelectedMode());  		
		try 
			{
	
			if (worklist.getSelectedMode().contains("Positive + Negative")  )
				workBook.write(output);
			output.close();
			if (!worklist.getSelectedMode().contains("Positive + Negative"))
				return worklist.getIddaStrList();
			else
				return null;
			} 
		catch (Exception e)  {  e.printStackTrace();  return null;}
		
		}
			
	// issue 209
	@Override
	public String getReportFileName()
		{
		return   worklist.getWorklistName() +  (worklist.getSelectedMode().contains("CC") ? "-CC" : "") + (worklist.getSelectedMode().contains("Positive + Negative") ? ".xlsx" : ".txt" )    ;
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
	
	// issue 25
	private String fileNamePredicateWithIdda(String vOutputFileName, String iddaString)
		{
		String fileName = "";
		// issue 32
		if (worklist.getIsCustomDirectoryStructure())
		    return (StringUtils.isEmptyOrNull(worklist.getCustomDirectoryStructureName()) ? " " : worklist.getCustomDirectoryStructureName()) +   "\\IDDA\\" + iddaString; 	// issue 32			
		if (vOutputFileName.indexOf("\\") < 0 )
		    return "IDDA\\" + iddaString; // issue 32 issue 48
		String [] fileNameArray = StringUtils.splitAndTrim(vOutputFileName, "\\\\");
		if (fileNameArray.length >= 7)
			{
			fileName = fileNameArray[0] + "\\"  +  fileNameArray[1] + "\\"  + fileNameArray[2] + "\\" +  fileNameArray[3] + "\\" + fileNameArray[4] + "\\" + fileNameArray[5] +  "\\IDDA\\" + iddaString; 
			return fileName;
			}
	    return "";
		}
		
	// issue 450
	public Sheet createWorklistSheet(String title, Workbook workBook, WorklistSimple worklist, boolean isPlatformAgilent, String strMode)
		{
		// issue 128
		// issue 207
		///// put back worklist.getIddaStrList().clear();
		worklist.getIddaStrList().clear();
		try
		    {
		// issue 450
			XSSFCellStyle styleHorizontalAndWhite = grabStyleWhite(workBook, true);
	        styleHorizontalAndWhite.setAlignment(HorizontalAlignment.LEFT);
	        styleHorizontalAndWhite.setIndention((short) 2);
			List<WorklistItemSimple> items = worklist.getItems();
			List<String> headers = worklist.getColTitles();
			int rowCt = 0;
		    String outputFileNameBase = "";
		    boolean initializedOutputFileNameBase = false;
			// issue 450
			//Sheet sheet = createEmptySheet("Worklist Builder Sheet",  workBook, 1, worklist.getColTitles(), 1);
			Sheet sheet = createEmptySheet(title,  workBook, 1, worklist.getColTitles(), 1);
			PoiUtils.createBlankRow(rowCt,  sheet);
			for (int i = 0; i < headers.size(); i++)
				// issue 179
				{
				PoiUtils.createRowEntry(rowCt, i, sheet, headers.get(i), grabStyleBlue(workBook));		
				///////
				if (i < headers.size() -1)
					worklist.getIddaStrList().add(headers.get(i) + "\t");
				else
					worklist.getIddaStrList().add(headers.get(i));
				}
			worklist.getIddaStrList().add("\r\n");
			rowCt++;
			for (WorklistItemSimple item : items)
				{
				// issue 25
				if (!initializedOutputFileNameBase)
					{				
				    outputFileNameBase = item.getOutputFileName();
				    initializedOutputFileNameBase = true;
					}	
				worklist.setWorksheetTitle(title);
				worklist.setInstrumentName(worklist.getSelectedInstrument() == null ? "Missing field : please select an instrument." : worklist.getSelectedInstrument().substring(0,worklist.getSelectedInstrument().indexOf("(") ).trim());
				String itemStr = item.toCharDelimited((worklist.getSelectedMode().contains("Positive + Negative") ? "," : "\t" ));	
				worklist.getIddaStrList().add(itemStr + "\r\n"); // issue 207
				String [] tokens = StringUtils.splitAndTrim(itemStr, (worklist.getSelectedMode().contains("Positive + Negative") ? "," : "\t" ), true);
				PoiUtils.createBlankRow(rowCt,  sheet);
				sheet.setColumnWidth(CONTROLNAMECOL, 28*256); // issue 179
				sheet.setColumnWidth(IDDADATAFILECOL, 96*256);
				for (int i = 0; i < tokens.length; i++)
					{	
						// issue 410
					// issue 179
					if (! ( i== IDDADATAFILECOL  && tokens[i].length() >= 2)) // issue 166
						PoiUtils.createRowEntry(rowCt, i , sheet,  tokens[i], styleHorizontalAndWhite);
						// issue 450
					else
						PoiUtils.createRowEntry(rowCt, i, sheet, tokens[i].substring(0,tokens[i].length() -2 ) +   (strMode.equals("Positive") ? "-P" : "-N"), styleHorizontalAndWhite);
				    }
				rowCt++;
				}
			// issue 432
			// issue 25		
			// issue 209
			// issue 212
		       if (worklist.getControlGroupsList().size() > 1 && worklist.isPlatformChosenAs("agilent") && (worklist.getNCE10Reps()> 0 || worklist.getNCE20Reps()> 0 ||  worklist.getNCE40Reps()> 0   )  && !worklist.getIs96Well() )
		    	   printOutIDDA  (workBook, sheet, rowCt, strMode, outputFileNameBase) ; 
			   return sheet;
		    }
	    catch (Exception e)
		    {
			e.printStackTrace();
			return null;
		    }		
		}
	
	// issue 432
	public String grabPlatePosition()
		{
		for (WorklistItemSimple item : worklist.getItems())	
			{
			// issue 128
			if (worklist.getPoolTypeA() == null)
		        return null;
			// issue 27
			if (item.getSampleName().contains(worklist.getPoolTypeA()))
				return item.getSamplePosition();
			}
		return null;
		}
	
	// issue 432
// issue 56
	// issue 229
	// issue 247
	public String createWorkListCCString(String ccStringToReplace)
		{
		Calendar cal = Calendar.getInstance();
		Date date = DateUtils.dateFromDateStr(worklist.getRunDate(), "MM/dd/yy");
		/////////////////////////////////////
		Date date2 = DateUtils.dateFromDateStr(worklist.getRunDate(), "MM/dd/yy");
		String monthAsStr = "";
		String yearStr = "";
		String monthAsStr2 = "";
		String yearStr2 = "";
		try 
		    {
		    if (worklist.getWorksheetTitle() != null && worklist.getWorksheetTitle().equals("Worklist Builder Sheet - Neg CC"))
		        {
			    cal.setTime(date2);
			    cal.add(Calendar.DAY_OF_MONTH, 4);
			    date2 = cal.getTime();				
			    monthAsStr = DateUtils.grabMonthString(DateUtils.dateAsFullString(date));
			    yearStr =  DateUtils.grabYearString(DateUtils.dateAsFullString(date));
			    monthAsStr2 = DateUtils.grabMonthString(DateUtils.dateAsFullString(date2));
				yearStr2 = DateUtils.grabYearString(DateUtils.dateAsFullString(date2));
			    ccStringToReplace = ccStringToReplace.replace(DateUtils.grabYYYYmmddString(  DateUtils.dateAsFullString(date)  ), DateUtils.grabYYYYmmddString(  DateUtils.dateAsFullString(date2)  ))
						.replace(monthAsStr, monthAsStr2)
						.replace(yearStr, yearStr2)
						.replace(worklist.getDefaultAssayId(), "A049")
						.replace(worklist.getInstrumentName(), "IN0030");
		        }
		    return ccStringToReplace;   
		    }
		
		catch (Exception e)
		  	{
			e.printStackTrace();
			return ccStringToReplace;
		  	}
		  }
	
	
	public void printOutIDDA(Workbook workBook, Sheet sheet, int rowCt, String strMode, String outputFileBase)
		{
		printOutIDDA(workBook, sheet, rowCt,  strMode, outputFileBase, true);
		}
	
	// issue 247
	public void printOutIDDA(Workbook workBook, Sheet sheet, int rowCt, String strMode, String outputFileBase, boolean includeSpreadsheet)
		{	
		
		///////////////
		
		/////////////////
		
		Font font = null;
		//worklist.getIddaStrList().clear();
		XSSFCellStyle styleColorBlue = null;
		XSSFCellStyle styleStandard = null;
		XSSFCellStyle styleItalic = null;
	    if (includeSpreadsheet)
		    {
	    	font = workBook.createFont();
		    styleColorBlue = grabStyleWhite(workBook, true);
	        styleColorBlue.setAlignment(HorizontalAlignment.LEFT);
			styleColorBlue.setIndention((short) 2);
	        styleColorBlue.setFillForegroundColor((IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex()));
	        styleColorBlue.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
	        styleColorBlue.setFont(font);
		    }
          
        if (includeSpreadsheet)
        	{
        	styleStandard = grabStyleWhite(workBook, true);
	        styleStandard.setAlignment(HorizontalAlignment.LEFT);
	        styleStandard.setIndention((short) 2);
        	}
           
        if (includeSpreadsheet)
    		{
        	styleItalic = grabStyleWhite(workBook, true);
	        styleItalic.setAlignment(HorizontalAlignment.LEFT);
	        styleItalic.setIndention((short) 2);
	        styleItalic.setFont(font);
    		}
        if (includeSpreadsheet)
			{
	        PoiUtils.createBlankRow(rowCt++,  sheet);
	        PoiUtils.createBlankRow(rowCt++,  sheet);
			}    
        if ((worklist.getNCE10Reps()> 0 || worklist.getNCE20Reps()> 0 ||  worklist.getNCE40Reps()> 0   )  && !worklist.getIs96Well())
	        {
	        worklist.getIddaStrList().add("\r\n"); // issue 207
	        worklist.getIddaStrList().add("\r\n");
	        }
        int startPoint = worklist.getStartingPoint() + 1;// issue 29
        String iddaPlatePos = grabPlatePosition() ;// issue 27
        // issue 128
        if (iddaPlatePos == null)
    	    return;
        String fileStr = worklist.grabOutputFileNameIDDA();
        /// issue 25
		String iddaStr = 	worklist.getPoolTypeA() + "-" + String.format("%0" + worklist.getAmountToPad() +"d",startPoint); // issue 456 // issue 13
		// issue 29
		if (startPoint == 1)
		    return;
		for (int j = 0; j <= worklist.getNCE10Reps();j++)
		    {
			if (worklist.getNCE10Reps() == 0)
				continue;
			if (includeSpreadsheet)
				PoiUtils.createBlankRow(rowCt,  sheet);			
			if (j==0) // issue 453
				fileStr = worklist.grabOutputFileNameIDDA() + "-"  + iddaStr + "-" + (strMode.contains("Positive") ? "P" : "N") + ".d"  ;
			else
				fileStr = worklist.grabOutputFileNameIDDA() + "-" +iddaStr +  "-IDDA_ce10" + "_" + j + "-" + (strMode.contains("Positive") ? "P" : "N") +   ".d";
			if (worklist.getWorksheetTitle() != null)
			    fileStr = (worklist.getWorksheetTitle().contains("CC") ? createWorkListCCString( fileStr) :  fileStr  );
			if (includeSpreadsheet)
	    		{
				PoiUtils.createRowEntry(rowCt, CONTROLNAMECOL, sheet, iddaStr, j == 0 ? styleItalic : styleStandard );
			    PoiUtils.createRowEntry(rowCt,CONTROLPOSCOL, sheet, iddaPlatePos, j == 0 ? styleItalic : styleStandard);
			    PoiUtils.createRowEntry(rowCt,IDDADATAFILECOL, sheet, fileNamePredicateWithIdda(outputFileBase,fileStr), j == 0 ? styleColorBlue : styleStandard); //use constant
	    		}
			    //issue 209
			 // issue 247
		  ////  worklist.getIddaStrList().add(iddaStr + (worklist.getSelectedMode().contains("Positive + Negative") ? "," : "\t" ) + iddaPlatePos + "\t\t" +fileNamePredicateWithIdda(outputFileBase,fileStr)  + "\t\t\t\t\r\n" );
			 worklist.getIddaStrList().add(iddaStr + "\t" + iddaPlatePos + "\t\t" +fileNamePredicateWithIdda(outputFileBase,fileStr)  + "\t\t\t\t\r\n" );
			 rowCt ++;
		    }
		// issue 38
		if (worklist.getNCE10Reps() > 0)
			startPoint++;
        iddaStr = 	worklist.getPoolTypeA() + "-" + String.format("%0" + worklist.getAmountToPad() +"d",startPoint);
		for (int j = 0; j <= worklist.getNCE20Reps();j++)
		    {
			if (worklist.getNCE20Reps() == 0)
				continue;
			if (includeSpreadsheet)
				PoiUtils.createBlankRow(rowCt,  sheet);	
			if (j==0) // issue 453
				fileStr = worklist.grabOutputFileNameIDDA() + "-"  + iddaStr + "-" + (strMode.contains("Positive") ? "P" : "N") + ".d"  ;
			else
		        fileStr = worklist.grabOutputFileNameIDDA() + "-" +iddaStr +  "-IDDA_ce20" + "_" + j + "-" + (strMode.contains("Positive") ? "P" : "N") +   ".d";
			if (worklist.getWorksheetTitle() != null)
			    fileStr = (worklist.getWorksheetTitle().contains("CC") ? createWorkListCCString( fileStr) :  fileStr  );
			if (includeSpreadsheet)
	    		{
				
				PoiUtils.createRowEntry(rowCt, CONTROLNAMECOL, sheet, iddaStr, j == 0 ? styleItalic : styleStandard );
	            PoiUtils.createRowEntry(rowCt, CONTROLPOSCOL, sheet, iddaPlatePos, j == 0 ? styleItalic : styleStandard);
	            PoiUtils.createRowEntry(rowCt, IDDADATAFILECOL, sheet, fileNamePredicateWithIdda(outputFileBase,fileStr), j == 0 ? styleColorBlue : styleStandard); //use constant
	    		}
	            // issue 209
            worklist.getIddaStrList().add(iddaStr + "\t" + iddaPlatePos + "\t\t" +fileNamePredicateWithIdda(outputFileBase,fileStr)  + "\t\t\t\t\r\n");
            rowCt ++;
		    }
		// issue 38
		if (worklist.getNCE20Reps() > 0)
			startPoint++;
        iddaStr = 	worklist.getPoolTypeA() + "-" + String.format("%0" + worklist.getAmountToPad() +"d",startPoint);
		for (int j = 0; j <= worklist.getNCE40Reps();j++)
		    { 
			if (worklist.getNCE40Reps() == 0)
				continue;
			if (includeSpreadsheet)
				PoiUtils.createBlankRow(rowCt,  sheet);	
		    if (j==0) // issue 453
				fileStr = worklist.grabOutputFileNameIDDA() + "-"  + iddaStr + "-" + (strMode.contains("Positive") ? "P" : "N") + ".d"  ;
			else
				fileStr = worklist.grabOutputFileNameIDDA() + "-" +iddaStr +  "-IDDA_ce40" + "_" + j + "-" + (strMode.contains("Positive") ? "P" : "N") +   ".d";
		    if (worklist.getWorksheetTitle() != null)
		        fileStr = (worklist.getWorksheetTitle().contains("CC")  ? createWorkListCCString( fileStr) :  fileStr  );
		    if (includeSpreadsheet)
	    		{
			    PoiUtils.createRowEntry(rowCt, CONTROLNAMECOL, sheet, iddaStr, j == 0 ? styleItalic : styleStandard );
	            PoiUtils.createRowEntry(rowCt, CONTROLPOSCOL, sheet, iddaPlatePos, j == 0 ? styleItalic : styleStandard);	 
	            // issue 247
	            PoiUtils.createRowEntry(rowCt, IDDADATAFILECOL, sheet, (worklist.getWorksheetTitle().contains("CC") ? createWorkListCCString( fileNamePredicateWithIdda(outputFileBase,fileStr)) :  fileNamePredicateWithIdda(outputFileBase,fileStr)  ), j == 0 ? styleColorBlue : styleStandard); //use constant
	    		}
		    	worklist.getIddaStrList().add(iddaStr + "\t"  + iddaPlatePos + "\t\t" +fileNamePredicateWithIdda(outputFileBase,fileStr) + "\t\t\t\t\r\n");
		    rowCt ++;
		    }
		worklist.setWorksheetTitle("");
		}
	}



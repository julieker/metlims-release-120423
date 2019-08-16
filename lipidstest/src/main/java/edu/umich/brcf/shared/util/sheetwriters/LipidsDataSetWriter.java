////////////////////////////////////////////////////
// LipidsDataSetWriter.java
// Written by Jan Wigginton, Aug 2, 2016
////////////////////////////////////////////////////
////////////////////////////////////////////////////
// MsWorklistWriter.java
// Written by Jan Wigginton, Jul 27, 2016
////////////////////////////////////////////////////

package edu.umich.brcf.shared.util.sheetwriters;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.wicket.injection.Injector;

import edu.umich.brcf.metabolomics.layers.domain.Ms2PeakSet;
import edu.umich.brcf.metabolomics.panels.lipidshome.browse.Ms2DataSetHandler;

import edu.umich.brcf.shared.util.interfaces.IWriteableSpreadsheet;
import edu.umich.brcf.shared.util.io.PoiUtils;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.io.SpreadSheetWriter;



public class LipidsDataSetWriter extends SpreadSheetWriter implements Serializable, IWriteableSpreadsheet
	{
	private String selectedExperiment; 
	protected Ms2DataSetHandler dataHandler;
	
	public LipidsDataSetWriter(Ms2DataSetHandler handler)
		{
		this();
		this.dataHandler = handler;
	//	this.selectedExperiment = selectedExperiment;
		}
	
	public LipidsDataSetWriter()
		{
		super();
		Injector.get().inject(this);
		}


	@Override
	public void generateExcelReport(OutputStream output)
		{
		Workbook workBook = new XSSFWorkbook();		
		createWorklistSheet("Worklist", workBook, dataHandler);
		
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
			String fileName =  "peak_areas" + dataHandler.getDataSet().getExpId();
			
			if (dataHandler.getDataSet().getIonMode() != null && !dataHandler.getDataSet().getIonMode().trim().equals(""))
				fileName += "_" + dataHandler.getDataSet().getIonMode();
			
			if (dataHandler.getDataSet().getDataNotation() != null && !dataHandler.getDataSet().getDataNotation().trim().equals(""))
				fileName += "_" + dataHandler.getDataSet().getDataNotation();
			
			return fileName;
			}
			
		
		
		private Boolean haveWorkbook()
			{
			return true;
			}
		
		
		public Sheet createEmptySheet(String title, Workbook workBook, int page, Ms2DataSetHandler dataHandler, List<String> headers)
			{
			Sheet sheet = workBook.createSheet(title);
		    sheet.getPrintSetup().setLandscape(true);
		    sheet.setFitToPage(true);
		    sheet.setHorizontallyCenter(true);
		    sheet.setZoom(75, 100);
		        
		    for (int i = 0; i < headers.size(); i++) 
		    	{
		    	if (i == 1)
		    		sheet.setColumnWidth(i, 50 * 256);
		    	else
		    		sheet.setColumnWidth(i, i > 7 ?  200 * 256 : 30 * 256);
		    	}
		    
		    return sheet;
		    }
		
		
		public Sheet createWorklistSheet(String title, Workbook workBook, Ms2DataSetHandler handler)
			{
			
			List<String> headers = Arrays.asList(new String [] {"Peak Set", "Compound Name", "Start Mass", "End Mass",
					"Expected Rt", "Lipid Class", "Known Status"});
					
			List <String> colTitles = new ArrayList<String>();
			for (int i = 0; i < headers.size();i++)
				colTitles.add(headers.get(i));
			
			for (int i = 0; i < dataHandler.tableColumnLabels.size(); i++) 
				{
				String val = dataHandler.tableColumnLabels.get(i);
				colTitles.add(val == null ? "-" : val);
				}
			
			//List<WorklistItemSimple> items = worklist.getItems();
			//List<String> headers = worklist.getColTitles();
			
			List<Ms2PeakSet> peakSets = dataHandler.getDataSet().getPeakSets();
			int rowCt = 0;
			Sheet sheet = createEmptySheet("Worklist Builder Sheet",  workBook, 1, dataHandler, colTitles);
			
			PoiUtils.createBlankRow(rowCt,  sheet);
			
			for (int i = 0; i < colTitles.size(); i++)
				PoiUtils.createRowEntry(rowCt, i + 1, sheet, colTitles.get(i), grabStyleBlue(workBook));
			
			rowCt++;
			
			for (Ms2PeakSet item : peakSets)
				{
				String itemStr = item.toCharDelimited(",");	
				String [] tokens = StringUtils.splitAndTrim(itemStr, ",");
				
				PoiUtils.createBlankRow(rowCt,  sheet);
				
				for (int i = 0; i < tokens.length; i++)
					{
					XSSFCellStyle style = grabStyleWhite(workBook, true);
					style.setAlignment(HorizontalAlignment.LEFT);
					style.setIndention((short) 2);
					Cell cell = PoiUtils.createRowEntry(rowCt, i + 1, sheet, tokens[i], style);
					}
				rowCt++;
				}

			return sheet;
			}
		}



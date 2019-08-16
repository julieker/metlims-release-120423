//////////////////////////////////////////////
// ServiceRequestSheetWriter.java			
// Written by Jan Wigginton, November 2015	
//////////////////////////////////////////////

package edu.umich.brcf.shared.util.sheetwriters;

import java.io.OutputStream;
import java.io.Serializable;

//import edu.umich.brcf.mchear.panels.sampletools.datacollectors.ServiceRequestHolder;
import edu.umich.brcf.shared.util.interfaces.IWriteableSpreadsheet;
import edu.umich.brcf.shared.util.io.SpreadSheetWriter;



public class ServiceRequestSheetWriter extends SpreadSheetWriter implements
		Serializable, IWriteableSpreadsheet
	{

	@Override
	public String getReportFileName()
		{
		// TODO Auto-generated method stub
		return null;
		}

	@Override
	public void generateExcelReport(OutputStream output)
		{
		// TODO Auto-generated method stub

		}
	/*
	 * ServiceRequestHolder serviceRequest; int rowCt; String requestId = "";
	 * int printVersion = 1;
	 * 
	 * public ServiceRequestSheetWriter(ServiceRequestHolder svcr) { super();
	 * this.serviceRequest = svcr; }
	 * 
	 * 
	 * @Override public void generateExcelReport(OutputStream output) {
	 * getRequestId(); Workbook workBook = new XSSFWorkbook(); Sheet sheet =
	 * createEmptySheet("Empty", workBook, 1);
	 * 
	 * writeInfoHeader(workBook, sheet); Map<String, String> valueMap =
	 * ObjectHandler.createObjectMap(serviceRequest);
	 * writeValuesToSheet(valueMap, workBook, sheet);
	 * 
	 * writeAssaysHeader(sheet, workBook); for (int i = 0; i <
	 * serviceRequest.getAssayRequests().size(); i++) writeAssayValuesToSheet(i
	 * + rowCt, serviceRequest.getAssayRequests().get(i), workBook, sheet);
	 * 
	 * writeWorkbook(output, workBook);
	 * 
	 * printVersion++; }
	 * 
	 * @Override public String getReportFileName() { return
	 * "MCHEAR_SERVICE_REQUEST_" + getRequestId() + ".xlsx"; }
	 * 
	 * 
	 * public void writeInfoHeader(Workbook workBook, Sheet sheet) { rowCt = 3;
	 * Row row = sheet.createRow(rowCt); createCellWithValue(1, row,
	 * "A. Contact Info", MyCellStyle.LIGHTBLUE_SUBTITLE, workBook); }
	 * 
	 * 
	 * public void writeAssayValuesToSheet(int ct,
	 * edu.umich.brcf.mchear.panels.sampletools
	 * .datacollectors.AssayRequestHolder assayRequestHolder, Workbook workBook,
	 * Sheet sheet) { Row row = sheet.createRow(ct);
	 * 
	 * createCellWithValue(1, row, assayRequestHolder.getSelectedArea(), null,
	 * workBook); createCellWithValue(2, row,
	 * assayRequestHolder.getSelectedCore(), null, workBook);
	 * createCellWithValue(3, row,assayRequestHolder.getSelectedAssay(), null,
	 * workBook); createCellWithValue(4,
	 * row,assayRequestHolder.getSelectedSampleType(), null, workBook);
	 * createCellWithValue(5, row,assayRequestHolder.getnSamples(), null,
	 * workBook); // createCellWithValue(6, row,assayRequestHolder.getCost(),
	 * null, workBook); }
	 * 
	 * 
	 * public void writeAssaysHeader(Sheet sheet, Workbook workBook) {
	 * List<String> headerNames = Arrays.asList(new String [] { "Area",
	 * "Service", "Assay", "Sample Type", "# Samples", "Cost"});
	 * 
	 * rowCt = 22; Row row = sheet.createRow(rowCt++); createCellWithValue(1,
	 * row, "B. Assays Requested", MyCellStyle.LIGHTBLUE_SUBTITLE, workBook);
	 * 
	 * rowCt = 24; row = sheet.createRow(rowCt); for (int i = 0; i <
	 * headerNames.size(); i++) createCellWithValue(i + 1, row,
	 * headerNames.get(i), i >= 0 && i <= 6 ? MyCellStyle.BLUE_TABLEHEADER_SMALL
	 * : MyCellStyle.WHITE, workBook); rowCt++; }
	 * 
	 * 
	 * public void writeValuesToSheet(Map<String, String> valueMap, Workbook
	 * workBook, Sheet sheet) { rowCt = 5; List<String> orderedFields =
	 * Arrays.asList(new String [] {"Project Name", "Pi Last Name" ,
	 * "Pi First Name", "Pi Address", "Pi E Mail", "Pi Phone", "Organization",
	 * "Organization Address", "Department", "Lab", "Contact Last Name",
	 * "Contact First Name", "Contact Address", "Contact E Mail",
	 * "Contact Phone", "Total Samples"});
	 * 
	 * for (String key : orderedFields) { Row row = sheet.createRow(rowCt);
	 * createCellWithValue(1, row, key, null, workBook); createCellWithValue(2,
	 * row, valueMap.get(key), null, workBook); rowCt++; }
	 * 
	 * sheet.protectSheet("jan"); }
	 * 
	 * 
	 * public Sheet createEmptySheet(String title, Workbook workBook, int page)
	 * { Sheet sheet = workBook.createSheet(title);
	 * sheet.setPrintGridlines(false); sheet.setDisplayGridlines(false);
	 * sheet.setFitToPage(true);
	 * 
	 * PrintSetup printSetup = sheet.getPrintSetup();
	 * printSetup.setLandscape(true); sheet.setFitToPage(true);
	 * sheet.setHorizontallyCenter(true);
	 * 
	 * sheet.setColumnWidth(0, 400); for (int i = 1; i < 12; i++)
	 * sheet.setColumnWidth(i, i < 4 ? 48 * 256 : 15 * 256);
	 * 
	 * Row row = sheet.createRow(1); Cell cell = row.createCell(1);
	 * sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 7));
	 * cell.setCellStyle(this.grabStylePageTitle(workBook, true, true));
	 * cell.setCellValue("MCHEAR Service Request Form");
	 * 
	 * row = sheet.createRow(2); cell = row.createCell(1);
	 * 
	 * sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 7));
	 * cell.setCellStyle(this.grabStylePageTitle(workBook, true, false));
	 * //requestId = "MC" + StringUtils.grabRandomDigitString(7);
	 * 
	 * cell.setCellValue("( Request Id : " + getRequestId() + " )");
	 * 
	 * return sheet; }
	 * 
	 * 
	 * public String getRequestId() { if ("".equals(requestId)) requestId = "MC"
	 * + StringUtils.grabRandomDigitString(7) + "." + printVersion; return
	 * requestId; }
	 * 
	 * public void setRequestId(String requestId) { this.requestId = requestId;
	 * }
	 */
	}

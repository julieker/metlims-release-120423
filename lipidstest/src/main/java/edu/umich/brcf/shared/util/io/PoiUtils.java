////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//PoiUtils.java
//Written by Jan Wigginton, November 2015
//////////////////////////////////////////////////////////////

package edu.umich.brcf.shared.util.io;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;

public class PoiUtils
	{
	// public static String getString(int row, int col) {
	// String value = null;
	// Cell cell = getCell(row, col);
	// if (cell == null)
	// return null;
	//
	// getString(cell);
	// }

	public static String readDropDownCol(Row row, short col)
		{
		String strCel = row.getCell(col).toString().trim();
		if (strCel != null
				&& strCel.toUpperCase().contains("SELECT OR ENTER TEXT"))
			strCel = "";

		if (strCel != null && strCel.toUpperCase().contains("CHOOSE ONE"))
			strCel = "";
		return strCel;
		}

	public static Row createBlankRow(int row, Sheet sheet)
		{
		Row rowBlank = sheet.createRow(row);

		Cell cellBlank = rowBlank.createCell(0);
		cellBlank.setCellValue("");
		return rowBlank;
		}

	public static Cell createRowStart(int row, Sheet sheet, String cellValue,
			CellStyle cellStyle)
		{
		return createRowEntry(row, 0, sheet, cellValue, cellStyle);
		}

	public static Cell updateRowEntry(int row, int col, Sheet sheet,
			String cellValue, CellStyle cellStyle)
		{
		return createRowEntry(row, col, sheet, cellValue, cellStyle);
		}

	public static Cell createRowEntry(int row, int col, Sheet sheet,
			String cellValue, CellStyle cellStyle)
		{
		Row rowBlank = sheet.getRow(row);
		if (rowBlank == null)
			sheet.createRow(row);

		Cell cellBlank = rowBlank.createCell(col);
		cellBlank.setCellValue(cellValue);
		if (cellStyle != null)
			cellBlank.setCellStyle(cellStyle);
		return cellBlank;
		}

	public static Cell createRowEntryWithFormula(int row, int col, Sheet sheet,
			String formula, CellStyle cellStyle)
		{
		Cell cell = createRowEntry(row, col, sheet, "", cellStyle);
		cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
		cell.setCellFormula(formula);
		return cell;
		}

	public static String getString(Cell cell)
		{
		String value = "";
		int type = cell.getCellType();

		if (type == Cell.CELL_TYPE_FORMULA)
			type = cell.getCachedFormulaResultType();

		switch (type)
			{
			case Cell.CELL_TYPE_BLANK:
			value = "";
				break;
			case Cell.CELL_TYPE_BOOLEAN:
			value = cell.getBooleanCellValue() ? "true" : "false";
				break;
			case Cell.CELL_TYPE_STRING:
			value = cell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_NUMERIC:
			value = "" + cell.getNumericCellValue();
			cell.setCellValue("" + value);
			value = cell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_ERROR:

			default:
				break;
			}

		return value;
		}
	}

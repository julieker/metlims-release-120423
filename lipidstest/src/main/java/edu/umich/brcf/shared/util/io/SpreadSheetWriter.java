//////////////////////////////////////////////
//SpreadSheetWriter.java			
//Written by Jan Wigginton, November 2015	
//////////////////////////////////////////////

package edu.umich.brcf.shared.util.io;

//import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder.BorderSide;

// ColorUtils
public class SpreadSheetWriter implements Serializable
	{
	public enum MyCellStyle
		{
		RED_INSTRUCTIONS, BLUE_TITLE, BLUE_TITLE_SMALL, BLUE_TABLEHEADER_SMALL, BLUE_TABLEHEADER_LARGE, YELLOW, WHITE_BORDERED, WHITE, LIGHTBLUE_SUBTITLE
		}

	public SpreadSheetWriter()
		{
		// //workBook = new XSSFWorkbook();
		}

	private XSSFColor grabColor(int red, int green, int blue)
		{
		byte[] rgb = new byte[3];
		rgb[0] = (byte) red; // red
		rgb[1] = (byte) green; // green
		rgb[2] = (byte) blue; // blue
		return new XSSFColor(rgb); //
		}

	protected Workbook createWorkBook(File newFile, boolean isExcel)
		{
		try
			{
			if (isExcel)
				{
				POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(
						newFile));
				return new HSSFWorkbook(fs);
				}

			OPCPackage pkg = OPCPackage.open(newFile);

			return new XSSFWorkbook(pkg);
			} catch (Exception e)
			{
			String msg = "Error while opening spreadsheet";
			System.out.println(msg);
			// throw new METWorksException(msg); //e.getMessage());
			}

		return null;
		}

	public void createCellWithValue(int col, Row row, String val,
			MyCellStyle style, Workbook workBook)
		{
		Cell cell = row.createCell(col);
		if (style != null)
			cell.setCellStyle(grabStyle(style, workBook));
		cell.setCellValue(val);
		}

	public XSSFDataValidation createDateTypeValidation(XSSFSheet sheet,
			CellRangeAddressList addressList, String startDate, String endDate,
			String format)
		{
		DVConstraint dvConstraint = DVConstraint
				.createDateConstraint(
						org.apache.poi.ss.usermodel.DataValidationConstraint.OperatorType.BETWEEN,
						startDate, endDate, format);

		XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);

		XSSFDataValidation validation = (XSSFDataValidation) dvHelper
				.createValidation(dvConstraint, addressList);

		validation.setEmptyCellAllowed(true);
		validation.setErrorStyle(DataValidation.ErrorStyle.INFO);
		validation.createErrorBox("Error", "Invalid Date.");
		validation.setShowErrorBox(true);

		sheet.addValidationData(validation);
		return validation;
		}

	public XSSFDataValidation createIntBeyondOrEqualToValidation(
			XSSFSheet sheet, Integer rangeLimit, Boolean greaterThan,
			CellRangeAddressList addressList, String valueLabel)
		{
		DVConstraint dvConstraint = null;
		if (greaterThan)
			dvConstraint = DVConstraint.createNumericConstraint(
					DVConstraint.ValidationType.INTEGER,
					DVConstraint.OperatorType.GREATER_OR_EQUAL,
					rangeLimit.toString(),
					new Integer(Integer.MAX_VALUE).toString());
		else
			dvConstraint = DVConstraint.createNumericConstraint(
					DVConstraint.ValidationType.INTEGER,
					DVConstraint.OperatorType.LESS_OR_EQUAL,
					rangeLimit.toString(),
					new Integer(Integer.MIN_VALUE).toString());

		XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
		XSSFDataValidation validation = (XSSFDataValidation) dvHelper
				.createValidation(dvConstraint, addressList);

		validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
		validation.createErrorBox("Insufficient " + valueLabel.toLowerCase(),
				valueLabel
						+ " must be "
						+ (greaterThan ? "greater than or equal to "
								: " less than or equal to ") + rangeLimit);

		sheet.addValidationData(validation);
		// return so error message can be further customized
		return validation;
		}

	public XSSFDataValidation createDecimalBeyondOrEqualToValidation(
			XSSFSheet sheet, Integer rangeLimit, Boolean greaterThan,
			CellRangeAddressList addressList, String valueLabel)
		{
		DVConstraint dvConstraint = null;
		if (greaterThan)
			dvConstraint = DVConstraint.createNumericConstraint(
					DVConstraint.ValidationType.DECIMAL,
					DVConstraint.OperatorType.GREATER_OR_EQUAL,
					rangeLimit.toString(),
					new Double(Integer.MAX_VALUE).toString());
		else
			dvConstraint = DVConstraint.createNumericConstraint(
					DVConstraint.ValidationType.DECIMAL,
					DVConstraint.OperatorType.LESS_OR_EQUAL,
					rangeLimit.toString(),
					new Double(Double.MIN_VALUE).toString());

		XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
		XSSFDataValidation validation = (XSSFDataValidation) dvHelper
				.createValidation(dvConstraint, addressList);

		validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
		validation.createErrorBox("Insufficient " + valueLabel.toLowerCase(),
				valueLabel
						+ " must be "
						+ (greaterThan ? "greater than or equal to "
								: " less than or equal to ") + rangeLimit);

		sheet.addValidationData(validation);
		// return so error message can be further customized
		return validation;
		}

	public XSSFDataValidation createIntBeyondValidation(XSSFSheet sheet,
			Integer rangeLimit, Boolean greaterThan,
			CellRangeAddressList addressList, String valueLabel)
		{
		DVConstraint dvConstraint = null;
		if (greaterThan)
			dvConstraint = DVConstraint.createNumericConstraint(
					DVConstraint.ValidationType.INTEGER,
					DVConstraint.OperatorType.GREATER_THAN,
					rangeLimit.toString(),
					new Integer(Integer.MAX_VALUE).toString());
		else
			dvConstraint = DVConstraint.createNumericConstraint(
					DVConstraint.ValidationType.INTEGER,
					DVConstraint.OperatorType.LESS_THAN, new Integer(
							Integer.MIN_VALUE).toString(), rangeLimit
							.toString());

		XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
		XSSFDataValidation validation = (XSSFDataValidation) dvHelper
				.createValidation(dvConstraint, addressList);

		validation.setErrorStyle(DataValidation.ErrorStyle.WARNING);
		validation.createErrorBox("Insufficient " + valueLabel.toLowerCase(),
				valueLabel
						+ " must be "
						+ (greaterThan ? "greater than or equal to "
								: " less than or equal to ") + rangeLimit);

		sheet.addValidationData(validation);
		return validation;
		}

	public XSSFDataValidation createIntRangeValidation(XSSFSheet sheet,
			Integer rangeBottom, Integer rangeTop,
			CellRangeAddressList addressList, String valueLabel)
		{
		DVConstraint dvConstraint = DVConstraint.createNumericConstraint(
				DVConstraint.ValidationType.INTEGER,
				DVConstraint.OperatorType.BETWEEN, rangeBottom.toString(),
				rangeTop.toString());

		XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
		XSSFDataValidation validation = (XSSFDataValidation) dvHelper
				.createValidation(dvConstraint, addressList);

		validation.setErrorStyle(DataValidation.ErrorStyle.WARNING);
		validation.createErrorBox(
				StringUtils.capitalize(valueLabel) + " error", valueLabel
						+ " must be between " + rangeBottom + " and "
						+ rangeTop);

		sheet.addValidationData(validation);
		return validation;
		}

	public XSSFDataValidation createDecimalRangeValidation(XSSFSheet sheet,
			Double rangeBottom, Double rangeTop,
			CellRangeAddressList addressList, String valueLabel)
		{
		DVConstraint dvConstraint = DVConstraint.createNumericConstraint(
				DVConstraint.ValidationType.DECIMAL,
				DVConstraint.OperatorType.BETWEEN, rangeBottom.toString(),
				rangeTop.toString());

		XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
		XSSFDataValidation validation = (XSSFDataValidation) dvHelper
				.createValidation(dvConstraint, addressList);

		validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
		validation.createErrorBox("Insufficient " + valueLabel.toLowerCase(),
				valueLabel + " must be between " + rangeBottom + " and "
						+ rangeTop);

		sheet.addValidationData(validation);
		return validation;
		}

	public void createDataValidation(XSSFSheet sheet, String[] values,
			int firstRow, int lastRow, int firstCol, int lastCol)
		{
		XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
		XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper
				.createExplicitListConstraint(values);

		CellRangeAddressList addressList = new CellRangeAddressList(firstRow,
				lastRow, firstCol, lastCol);
		XSSFDataValidation validation = (XSSFDataValidation) dvHelper
				.createValidation(dvConstraint, addressList);
		validation.setShowErrorBox(true);
		sheet.addValidationData(validation);
		}

	private char buildHiddenList(Workbook workbook, XSSFSheet sheet,
			List<String> list, int colNum)
		{
		Sheet hidden = workbook.getSheet("hidden");

		for (int i = 0; i < list.size(); i++)
			{
			Row row = hidden.getRow(i);
			if (row == null)
				row = hidden.createRow(i);
			Cell cell = row.createCell(colNum);
			cell.setCellValue(list.get(i));
			}

		return (char) (colNum + (int) 'A');
		}

	public void createDataValidationFromRange(Workbook workbook,
			XSSFSheet sheet, CellRangeAddressList addressList,
			List<String> list, int colNum)
		{
		char colName = buildHiddenList(workbook, sheet, list, colNum);

		Name namedCell = workbook.createName();
		namedCell.setNameName("hidden" + colNum);
		namedCell.setRefersToFormula("'hidden'!$" + colName + "$1:$" + colName
				+ "$" + list.size());

		XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
		XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper
				.createFormulaListConstraint("hidden" + colNum);

		XSSFDataValidation validation = (XSSFDataValidation) dvHelper
				.createValidation(dvConstraint, addressList);

		validation.setEmptyCellAllowed(true);
		validation.setErrorStyle(DataValidation.ErrorStyle.INFO);
		// validation.createErrorBox("Error", "InValid Date.");
		validation.setShowErrorBox(false);

		sheet.addValidationData(validation);
		}

	public void writeWorkbook(OutputStream output, Workbook workBook)
		{
		try
			{
			workBook.write(output);
			output.close();
			} catch (Exception e)
			{
			e.printStackTrace();
			}
		}

	XSSFCellStyle grabStyle(MyCellStyle style, Workbook workBook)
		{
		switch (style)
			{
			case RED_INSTRUCTIONS:
			return this.grabStyleInstructions(workBook, false);

			case BLUE_TITLE:
			return this.grabStylePageTitle(workBook, false, true);

			case BLUE_TITLE_SMALL:
			return this.grabStylePageTitle(workBook, false, false);

			case LIGHTBLUE_SUBTITLE:
			return this.grabStyleSubTitle(workBook, false);

			case BLUE_TABLEHEADER_SMALL:
			return this.grabStyleBlue(workBook, false);

			case BLUE_TABLEHEADER_LARGE:
			return this.grabStyleBlue(workBook, true);

			case YELLOW:
			return this.grabStyleYellow(workBook);

			case WHITE:
			return this.grabStyleWhite(workBook, false);

			case WHITE_BORDERED:
			return this.grabStyleWhite(workBook, true);

			default:
			return this.grabStyleWhite(workBook, false);
			}
		}

	public XSSFCellStyle grabStyleSubTitle(Workbook workBook, boolean larger)
		{
		Font fontHeader = workBook.createFont();
		fontHeader.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);
		fontHeader.setFontHeightInPoints((short) (larger ? 18 : 16));
		fontHeader.setItalic(true);
		fontHeader.setColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());

		XSSFCellStyle styleTitle = (XSSFCellStyle) workBook.createCellStyle();
		styleTitle.setAlignment(HorizontalAlignment.LEFT);
		styleTitle.setFont(fontHeader);
		// if (bottomAlign)
		// styleTitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
		styleTitle.setLocked(true);
		return styleTitle;
		}

	public XSSFCellStyle grabStyleYellow(Workbook workBook)
		{
		XSSFCellStyle styleYellow = (XSSFCellStyle) workBook.createCellStyle();
		styleYellow.setLocked(false);
		styleYellow.setAlignment(HorizontalAlignment.LEFT);

		styleYellow.setFillBackgroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE
				.getIndex());

		XSSFColor colorBlack = grabColor(0, 0, 0); // new
													// XSSFColor(Color.BLACK);
		styleYellow.setBorderColor(BorderSide.BOTTOM, colorBlack);
		styleYellow.setBorderColor(BorderSide.TOP, colorBlack);
		styleYellow.setBorderColor(BorderSide.LEFT, colorBlack);
		styleYellow.setBorderColor(BorderSide.RIGHT, colorBlack);

		styleYellow.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		styleYellow.setBorderTop(XSSFCellStyle.BORDER_THIN);
		styleYellow.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		styleYellow.setBorderRight(XSSFCellStyle.BORDER_THIN);

		styleYellow.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleYellow.setFillForegroundColor(IndexedColors.LEMON_CHIFFON
				.getIndex());
		styleYellow.setWrapText(true);
		styleYellow.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);

		return styleYellow;
		}

	public XSSFCellStyle grabStyleWhite(Workbook workBook, boolean bordered)
		{
		return grabStyleWhite(workBook, bordered, false);
		}

	public XSSFCellStyle grabStyleWhite(Workbook workBook, boolean bordered, boolean locked)
		{
		XSSFCellStyle styleWhite = (XSSFCellStyle) workBook.createCellStyle();
		styleWhite.setAlignment(HorizontalAlignment.RIGHT);
		if (!bordered)
			styleWhite.setIndention((short) 5);
		Font fontLabel = workBook.createFont();
		fontLabel.setBoldweight((short) 4);
		styleWhite.setFont(fontLabel);

		if (bordered)
			{
			fontLabel.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
			XSSFColor colorGrey = grabColor(200, 200, 200); // colorGrey;
			styleWhite.setBorderColor(BorderSide.BOTTOM, colorGrey);
			styleWhite.setBorderColor(BorderSide.TOP, colorGrey);
			styleWhite.setBorderColor(BorderSide.LEFT, colorGrey);
			styleWhite.setBorderColor(BorderSide.RIGHT, colorGrey);

			styleWhite.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			styleWhite.setBorderTop(XSSFCellStyle.BORDER_THIN);
			styleWhite.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			styleWhite.setBorderRight(XSSFCellStyle.BORDER_THIN);

			styleWhite.setAlignment(HorizontalAlignment.CENTER);
			}

		styleWhite.setLocked(locked);
		return styleWhite;
		}

	public XSSFCellStyle grabStyleLocked(Workbook workBook, boolean bordered)
		{
		XSSFCellStyle styleWhite = (XSSFCellStyle) workBook.createCellStyle();
		styleWhite.setAlignment(HorizontalAlignment.RIGHT);
		if (!bordered)
			styleWhite.setIndention((short) 5);

		Font fontLabel = workBook.createFont();
		fontLabel.setBoldweight((short) 4);
		styleWhite.setFont(fontLabel);

		if (bordered)
			{
			fontLabel.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
			XSSFColor color = grabColor(200, 200, 200);
			styleWhite.setBorderColor(BorderSide.BOTTOM, color);
			styleWhite.setBorderColor(BorderSide.TOP, color);
			styleWhite.setBorderColor(BorderSide.LEFT, color);
			styleWhite.setBorderColor(BorderSide.RIGHT, color);

			styleWhite.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			styleWhite.setBorderTop(XSSFCellStyle.BORDER_THIN);
			styleWhite.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			styleWhite.setBorderRight(XSSFCellStyle.BORDER_THIN);

			styleWhite.setAlignment(HorizontalAlignment.CENTER);
			}

		styleWhite.setLocked(true);
		return styleWhite;
		}

	public XSSFCellStyle grabStyleBlue(Workbook workBook)
		{
		return grabStyleBlue(workBook, false);
		}

	public XSSFCellStyle grabStyleBlue(Workbook workBook, boolean larger)
		{
		Font fontHeaderWhite = workBook.createFont();
		fontHeaderWhite.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		fontHeaderWhite.setFontHeightInPoints(larger ? (short) 16 : (short) 12);
		fontHeaderWhite.setColor(IndexedColors.WHITE.getIndex());

		XSSFCellStyle styleBlueHeader = (XSSFCellStyle) workBook
				.createCellStyle();
		styleBlueHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleBlueHeader.setFillForegroundColor(IndexedColors.DARK_BLUE
				.getIndex());
		XSSFColor color = grabColor(255, 255, 255); // ; //new
													// XSSFColor(Color.WHITE);

		styleBlueHeader.setBorderColor(BorderSide.BOTTOM, color);
		styleBlueHeader.setBorderColor(BorderSide.TOP, color);
		styleBlueHeader.setBorderColor(BorderSide.LEFT, color);
		styleBlueHeader.setBorderColor(BorderSide.RIGHT, color);
		styleBlueHeader.setAlignment(HorizontalAlignment.CENTER);

		styleBlueHeader.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		styleBlueHeader.setBorderTop(XSSFCellStyle.BORDER_THIN);
		styleBlueHeader.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		styleBlueHeader.setBorderRight(XSSFCellStyle.BORDER_THIN);

		styleBlueHeader.setFont(fontHeaderWhite);
		styleBlueHeader.setLocked(true);

		return styleBlueHeader;
		}

	public XSSFCellStyle grabStyleInstructions(Workbook workBook,
			boolean bottomAlign)
		{
		return grabStyleInstructions(workBook, bottomAlign, false);
		}

	public XSSFCellStyle grabStyleInstructions(Workbook workBook,
			boolean bottomAlign, boolean emphasize)
		{
		Font fontHeader = workBook.createFont();
		fontHeader.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);
		fontHeader.setFontHeightInPoints(emphasize ? (short) 14 : (short) 12);
		fontHeader.setItalic(true);
		if (emphasize)
			fontHeader.setColor(IndexedColors.LIGHT_BLUE.getIndex());

		XSSFCellStyle styleInstructions = (XSSFCellStyle) workBook
				.createCellStyle();
		styleInstructions.setFont(fontHeader);
		if (bottomAlign)
			styleInstructions.setVerticalAlignment(VerticalAlignment.BOTTOM);

		styleInstructions.setLocked(true);
		return styleInstructions;
		}

	public XSSFCellStyle grabStylePageTitle(Workbook workBook,
			boolean bottomAlign, boolean larger)
		{
		Font fontHeader = workBook.createFont();
		fontHeader.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);
		fontHeader.setFontHeightInPoints((short) (larger ? 16 : 12));
		fontHeader.setItalic(true);
		fontHeader.setColor(IndexedColors.DARK_BLUE.getIndex());

		XSSFCellStyle styleTitle = (XSSFCellStyle) workBook.createCellStyle();
		styleTitle.setAlignment(HorizontalAlignment.CENTER);
		styleTitle.setFont(fontHeader);
		if (bottomAlign)
			styleTitle.setVerticalAlignment(VerticalAlignment.BOTTOM);

		styleTitle.setLocked(true);
		return styleTitle;
		}
	}

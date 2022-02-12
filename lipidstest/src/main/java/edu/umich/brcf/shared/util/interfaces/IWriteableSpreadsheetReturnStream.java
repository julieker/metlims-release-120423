//////////////////////////////////////////
//IWriteableSpreadsheet.java
//Written by Julie Keros feb 7 2022
//////////////////////////////////////////
// issue 
package edu.umich.brcf.shared.util.interfaces;
// issue 207
import java.io.OutputStream;
import java.util.List;

public interface IWriteableSpreadsheetReturnStream
	{
	public String getReportFileName();

	public List <String> generateExcelReport(OutputStream output);
	
	}

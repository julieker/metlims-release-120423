//////////////////////////////////////////
//IWriteableSpreadsheet.java
//Written by Julie Keros feb 7 2022
//////////////////////////////////////////
// issue 
package edu.umich.brcf.shared.util.interfaces;
// issue 207
import java.io.OutputStream;
import java.util.List;
import java.util.ArrayList;

public interface IWriteableSpreadsheetReturnStream
	{
	public String getReportFileName();
	public void setReportFileName(String str);   
	// issue 313
	public List <String> generateExcelReport(OutputStream output);
	public List <String> generateExcelReport(OutputStream output, boolean doJustTxtFile);
	public List <String> getIddaList ();
	
	}

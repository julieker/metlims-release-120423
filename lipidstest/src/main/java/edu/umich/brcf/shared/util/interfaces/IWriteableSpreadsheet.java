//////////////////////////////////////////
//IWriteableSpreadsheet.java
//Written by Jan Wigginton September 2015
//////////////////////////////////////////

package edu.umich.brcf.shared.util.interfaces;

import java.io.OutputStream;

public interface IWriteableSpreadsheet
	{
	public String getReportFileName();

	public void generateExcelReport(OutputStream output);
	}

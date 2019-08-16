////////////////////////////////////////////////////
// IWriteableTextData.java
// Written by Jan Wigginton, Aug 2, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.interfaces;

import java.io.OutputStream;


public interface IWriteableTextData
	{
	public String getReportFileName();

	public void generateTextReport(OutputStream output);
	
	public Character getDelimiter();
	}



/////////////////////////////////////////
//SampleSheetIOException.java
//Written by Jan Wigginton, October 2015
/////////////////////////////////////////

package edu.umich.brcf.shared.util;

public class SampleSheetIOException extends Exception 
	{
	private int line, sheet;
	private String sheetName;
	
	public SampleSheetIOException(String msg, Integer line, int sheet)
		{
		super(msg);
		this.line = line;
		this.sheet = sheet;
		this.sheetName = "";
		}
	
	
	public SampleSheetIOException(String msg, Integer line, String sheetName)
		{
		super(msg);
		
		this.line = line;
		this.sheetName = sheetName;
		}
	
	public int getLine()
		{
		return line;
		}
	
	public void setLine(int line)
		{
		this.line = line;
		}
	
	public int getSheet()
		{
		return sheet;
		}
	
	public void setSheet(int sheet)
		{
		this.sheet = sheet;
		}
	
	public String getSheetName()
		{
		return sheetName;
		}
	
	public void setSheetName(String sheetName)
		{
		this.sheetName = sheetName;
		}
	}


/*

public class SampleSheetIOException extends Exception 
	{
	private int line, sheet;
	private String sheetName;
	
	public SampleSheetIOException(String msg, int line, int sheet)
		{
		super(msg);
		this.line = line;
		this.sheet = sheet;
		this.sheetName = "";
		}
	
	public SampleSheetIOException(String msg, int line, String sheetName)
		{
		super(msg);
		
		this.line = line;
		this.sheetName = sheetName;
		}

	public int getLine()
		{
		return line;
		}

	public void setLine(int line)
		{
		this.line = line;
		}

	public int getSheet()
		{
		return sheet;
		}

	public void setSheet(int sheet)
		{
		this.sheet = sheet;
		}

	public String getSheetName()
		{
		return sheetName;
		}

	public void setSheetName(String sheetName)
		{
		this.sheetName = sheetName;
		}
	
	
	}
*/
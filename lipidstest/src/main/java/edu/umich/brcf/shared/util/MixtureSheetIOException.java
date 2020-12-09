/////////////////////////////////////////
//MixtureSheetIOException.java
//Written by Julie Keros, December 2020
/////////////////////////////////////////

package edu.umich.brcf.shared.util;

public class MixtureSheetIOException extends Exception 
	{
	private int line, sheet;
	private String sheetName;
	
	public MixtureSheetIOException(String msg, Integer line, int sheet)
		{
		super(msg);
		this.line = line;
		this.sheet = sheet;
		this.sheetName = "";
		}
		
	public MixtureSheetIOException(String msg, Integer line, String sheetName)
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



////////////////////////////////////////////////////
// NumberUtils.java
// Written by Jan Wigginton, Dec 7, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.utilpackages;

import java.math.BigDecimal;

import edu.umich.brcf.shared.util.METWorksException;


public class NumberUtils
	{
	/*public static Boolean exceptionCheckDecimalRange(String value, int wholePart, int decimalPart) throws METWorksException
		{
		return exceptionCheckDecimalRange(value, wholePart, decimalPart, false);
		}
	
	
	public static Boolean exceptionCheckDecimalRange(String value, int wholePart, int decimalPart, Boolean rejectNull) throws METWorksException
		{
		if (rejectNull && value == null) 
			throw new METWorksException("Value is null " + value);
		
		if (wholePart < 0 || decimalPart < 0 || (wholePart == 0 && decimalPart == 0)) 
			throw new METWorksException("Invalid range parameter. Cannot check for a number in range with whole part " + wholePart + " and decimal part " +decimalPart);
		
		Double dblValue = null;
		try {
			Double.parseDouble(value);
			}
		catch (Exception e)
			{
			throw new METWorksException("Value " + value + " isn't a number");
			}
		


		Double whole = Math.floor(Math.abs(dblValue)); 
		if (whole > Math.pow(10, wholePart))
			throw new METWorksException("Value " + value + " cannot be greater than 1e " + wholePart);  // + " and 1e-" + decimalPart); 
		
		
		Double remainder = Math.abs(dblValue) - whole; 
		if (whole < 1e-300 && remainder < Math.pow(10, decimalPart))
			throw new METWorksException("Value cannot have more than 3 decimal places");
				
		
		return true;
		} */
	
	
	public static Boolean verifyDecimalRange(String value, int wholePart, int decimalPart) 
		{
		return verifyDecimalRange(value, wholePart, decimalPart, false);
		}
	
	/*
	public static Boolean verifyDecimalRange(String value, int wholePart, int decimalPart, boolean rejectNull) 
		{
		if (rejectNull && value == null) 
			return false;
		
		if (wholePart < 0 || decimalPart < 0  || (wholePart == 0 && decimalPart == 0)) 
			return false;
		
		Double dblValue = null;
		try {
			dblValue = Double.parseDouble(value);
			}
		catch (Exception e)
			{
			return false;
			}
		
		
		
		Double whole = Math.floor(Math.abs(dblValue)); 
		if (whole > Math.pow(10, wholePart))
			return false;
		
		Double remainder = Math.abs(dblValue) - whole; 
		if (whole < 1e-300 && remainder < Math.pow(10, decimalPart))
			return false;
		
		return true;
		}   */
	
	

	public static Boolean verifyDecimalRange(String value, int wholePart, int decimalPart, boolean rejectNull) 
		{
		if (rejectNull && value == null) 
			return false;
		
		if (wholePart < 0 || decimalPart < 0  || (wholePart == 0 && decimalPart == 0)) 
			return false;
		
		Double dblValue = null;
		try {
            dblValue = Double.parseDouble(value);
			}
		catch (Exception e) { return false; }

		
		Double dblValuePos = Math.abs(dblValue);
		if(dblValuePos >= Math.pow(10, wholePart))
			return false;
	
		return true;
		}
	}
	

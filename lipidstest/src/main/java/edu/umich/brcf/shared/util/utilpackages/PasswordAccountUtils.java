////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//PasswordAccountUtils.java
//Written by Jan Wigginton, June 2016
//////////////////////////////////////////////////////////////


package edu.umich.brcf.shared.util.utilpackages;

import edu.umich.brcf.shared.layers.domain.User;


public class PasswordAccountUtils 
	{
	public static String createUserName(String fn, String ln)
		{
		if (fn == null || fn.length() < 2) 
			return "";
		
		if (ln == null || ln.length() < 2) 
			return "";
		
		Integer randomInteger  = (int) Math.round(Math.random() * 100);
		String randomIntStr = randomInteger.toString();
		String compFn = StringUtils.removeSpaces(fn);
		String compLn = StringUtils.removeSpaces(ln);
		
		String appended = compFn.substring(0, 2) + compLn + randomIntStr;
		return (appended.length() < User.USERNAME_LEN ? appended : appended.substring(0, 15));
		}	
	
	
	public static String createUserPassword(int digits)
		{
		
		// JAK fix issue 201
		Boolean hasUpperCase = false, hasDigit = false, hasLowerCase = false;
		StringBuilder sb= new StringBuilder();
		for (int i = 0;i < Math.min(User.USERPW_LEN - 1, digits); i++)
			{
			Boolean grabDigit  =  ((int) Math.round(Math.random() * 2)) < 1;
			if (grabDigit)
				{
				sb.append(StringUtils.grabRandomDigitString(1));
				hasDigit = true;
				}
			else
				{
				String rawLetter = StringUtils.grabRandomLetterString(1);
				if (!hasLowerCase)
					{
					sb.append(rawLetter.toLowerCase());
					hasLowerCase = true;
					}
				else
					{
					sb.append(hasUpperCase ? rawLetter : rawLetter.toUpperCase());
					hasUpperCase = true;
					}
				}
			}
		
		if (hasDigit)
			sb.append(StringUtils.grabRandomLetterString(1));
		else
			sb.append(StringUtils.grabRandomDigitString(1));
				
		return sb.toString();
		}
	}


/*

public class PasswordAccountUtils 
	{
	public static String createUserName(String fn, String ln)
		{
		if (fn.length() < 2) 
			return "";
		
		Integer randomInteger  = (int) Math.round(Math.random() * 100);
		String randomIntStr = randomInteger.toString();
		String compFn = StringUtils.removeSpaces(fn);
		String compLn = StringUtils.removeSpaces(ln);
		
		String appended = compFn.substring(0, 2) + compLn + randomIntStr;
		return (appended.length() < User.USERNAME_LEN ? appended : appended.substring(0, 15));
		}	
	
	public static String createUserPassword(int digits)
		{
		StringBuilder sb= new StringBuilder();
		for (int i = 0;i < Math.min(User.USERPW_LEN, digits); i++)
			{
			Boolean isLetter  =  ((int) Math.round(Math.random() * 2)) < 1;
			if (isLetter)
				sb.append(StringUtils.grabRandomDigitString(1));
			else
				sb.append(StringUtils.grabRandomLetterString(1));
			}
				
		System.out.println("New string is " + sb.toString());
		return sb.toString();
		}
	}
*/
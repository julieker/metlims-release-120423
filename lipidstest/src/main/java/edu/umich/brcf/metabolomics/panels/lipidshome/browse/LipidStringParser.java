///////////////////////////////////////
//LipidStringParser.java
//Written by Jan Wigginton May 2015
///////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.lipidshome.browse;

import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.util.FormatVerifier;


public class LipidStringParser
	{
	public static String getLipidBlastAdductFromLongName(String fullName)
		{
		String [] tokens = fullName.split(";");
		String adduct = tokens.length > 1 ? tokens[1].trim() : "";
		return adduct;
		}


	public static String getLipidBlastCodeNameFromLongName(String fullName)
		{
		String [] tokens = fullName.split(";");
		String codeName = tokens.length > 0 ? tokens[0].trim() : "";
		return codeName;
		}

	
	public static String parseCarbonsFromLipidName(String fullName) 
		{
		if (fullName == null || fullName.trim().equals(""))
			return "";
		
		String searchName = fullName.replace('(', ' ').replace(')', ' ');
		String carbonBondString = getCarbonDoubleBondString(searchName);
		String [] tokens = carbonBondString.split(":");
		
		return tokens.length > 1 ? tokens[0] : "";
		}
	
	
	public static String getSampleLabelFromWorklistLabel(String worklistLabel)
		{
		String sampleId;
		String [] tokens; 
		String blankString = worklistLabel;
		// Ugly, but Lipid parser is static and this requires a spring bean
		if (worklistLabel == null || worklistLabel.trim().equals(""))
			return "-";
	
		tokens = worklistLabel.split("_");
		if (tokens.length < 2)
			return blankString;
	
		sampleId = tokens[0];
		if (sampleId != null)
			System.out.println("Sample id is " + sampleId);
		
		if (sampleId == null || sampleId.trim().equals(""))
			return blankString;
		
		if (!FormatVerifier.verifyFormat(Sample.idFormat,sampleId.toUpperCase()))
			return blankString;
		
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < tokens.length; i++)
			{
			if (i > 1)
				sb.append("_");
			sb.append(tokens[i]);
			}
	
		return sb.toString();
		}

	
	public static String getSampleIdFromWorklistLabel(String worklistLabel)
		{
		String sampleId;
		String [] tokens; 
		String blankString = "   -   ";

		if (worklistLabel == null || worklistLabel.trim().equals(""))
			return "-";
	
		tokens = worklistLabel.split("_");
		if (tokens.length < 1)
			return blankString;
	
		sampleId = tokens[0];
		
		if (sampleId == null || sampleId.trim().equals(""))
			return blankString;
		
		if (!FormatVerifier.verifyFormat(Sample.idFormat,sampleId.toUpperCase()))
			return blankString;
		
		return sampleId;
		}


	public static String parseDoubleBondsFromLipidName(String fullName)
		{
		if (fullName == null || fullName.trim().equals(""))
			return "";
		
		String searchName = fullName.replace('(', ' ').replace(')', ' ');
		String carbonBondString = getCarbonDoubleBondString(searchName);
		String [] tokens = carbonBondString.split(":");
		
		return tokens.length > 1 ? tokens[1] : "";
		}
	
	
	public static String parseSampleIdFromWorklistLabel(String worklistLabel)
		{
		if (worklistLabel == null || worklistLabel.trim().equals(""))
			return "";
		
		String tokens [] = worklistLabel.split("_");
		
		return "";
		} 
	
	
	public static String getCarbonDoubleBondString(String fullName)
		{
		String firstToken, secondToken;
		String [] tokens = fullName.split(";");
		
		firstToken = tokens.length > 0 ? tokens[0].trim() : "";
		
		if (firstToken.trim().equals(""))
			return "";
		
		tokens = firstToken.split("\\s");
		
		secondToken = tokens.length > 1 ? tokens[1].trim() : "";
		
		if (secondToken.trim().equals(""))
			return "";
		
		return secondToken;
		}
	}
	
	
	

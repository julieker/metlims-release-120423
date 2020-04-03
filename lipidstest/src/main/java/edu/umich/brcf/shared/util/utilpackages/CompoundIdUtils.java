package edu.umich.brcf.shared.util.utilpackages;
//Created by Julie Keros Mar 11, 2020

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.h2.util.StringUtils;

public class CompoundIdUtils
	{
	// issue 27 
	// issue 31
	public static List<String> grabSmilesFromCompoundId(String compoundId, String idIndicator)
	    {
	    StringBuilder sb = new StringBuilder();
	    List<String> smilesAndmultipleTagList = new ArrayList <String> ();
	    smilesAndmultipleTagList.add("");
	    smilesAndmultipleTagList.add("");
	    URL url = null;
	    String urlString = "";
	    BufferedReader br = null;
		if (StringUtils.isNullOrEmpty(compoundId))
			return smilesAndmultipleTagList;
		// issue 31 2020		
		if (idIndicator.toLowerCase().equals("inchikey"))
			urlString = "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/inchikey/" + compoundId + "/xml";
		else 
			urlString = "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/"  + compoundId + "/xml";
		try 
	        {
	        url = new URL(urlString);
	        } 
	    catch (MalformedURLException e2) 
	        {
		// TODO Auto-generated catch block
		    e2.printStackTrace();
		    return smilesAndmultipleTagList;
	        }
	    try 
	        {
	 	   br = new BufferedReader(new InputStreamReader(url.openStream())) ;
	        String line;
	        while ((line = br.readLine()) != null) 
	            {
	            sb.append(line);
	            sb.append(System.lineSeparator());
	            }
	        br.close();
	        } 
	    catch (SocketTimeoutException sT)
	        {
	 	    sT.printStackTrace();
	 	    return smilesAndmultipleTagList;
	        }
	    catch (IOException e1) 
	       {
		// TODO Auto-generated catch block
		   e1.printStackTrace();
		   try 
		       {
		       if (br!=null)
			       br.close();
			   } 
		   catch (IOException e) 
		       {
				// TODO Auto-generated catch block
			   e.printStackTrace();
			   return smilesAndmultipleTagList;
			   }
		   return smilesAndmultipleTagList;		    
	       }  
	     // Now parse the text....
	    if (sb.toString().indexOf("SMILES") < 0)
	        return smilesAndmultipleTagList;	
	    String cSmiles = sb.toString().substring(sb.toString().indexOf("SMILES"));	    
	    int numSmileTags =  ( sb.toString().length() - sb.toString().replace("SMILES", "").length())/"SMILES".length();
	    smilesAndmultipleTagList.clear();
	    smilesAndmultipleTagList.add(cSmiles.toString().substring(cSmiles.toString().lastIndexOf("<PC-InfoData_value_sval>")+"<PC-InfoData_value_sval>".length(),cSmiles.toString().lastIndexOf("</PC-InfoData_value_sval>")));
	    smilesAndmultipleTagList.add(numSmileTags > 2 ? "* There are multiple Smiles": "");
	    return  smilesAndmultipleTagList;     	    
	    }
	}

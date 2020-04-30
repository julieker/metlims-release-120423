package edu.umich.brcf.shared.util.utilpackages;
//Created by Julie Keros Mar 11, 2020

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
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
	    List<String> smilesInchiKeyAndmultipleTagList = new ArrayList <String> ();
	    smilesInchiKeyAndmultipleTagList.add("");
	    smilesInchiKeyAndmultipleTagList.add("");
	    smilesInchiKeyAndmultipleTagList.add(""); // issue 36
	    URL url = null;
	    String urlString = "";
	    BufferedReader br = null;
		if (StringUtils.isNullOrEmpty(compoundId))
		    return smilesInchiKeyAndmultipleTagList;
		// issue 31 2020
		try 
	        {
			// issue 47
			compoundId = idIndicator.toLowerCase().equals("smiles") ? compoundId.replace("/", ".").replace("&", "%26"): compoundId;
			urlString = "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/" + (idIndicator.toLowerCase().equals("cas") ? "name" : idIndicator.toLowerCase()) + "/" +  compoundId + "/xml";
	        url = new URL(urlString);
	        } 
	    catch (MalformedURLException e2) 
	        {
		// TODO Auto-generated catch block
		    e2.printStackTrace();
		    return smilesInchiKeyAndmultipleTagList;
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
	 	    return smilesInchiKeyAndmultipleTagList;
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
			   return smilesInchiKeyAndmultipleTagList;
			   }
		   return smilesInchiKeyAndmultipleTagList;		    
	       }  
	     // Now parse the text....	    
	    return createsmilesInchiKeyAndmultipleTagList(sb);     	    
	    }
	
	// issue 36
	private static List<String> createsmilesInchiKeyAndmultipleTagList (StringBuilder sb)
		{
		List<String> smilesInchiKeyAndmultipleTagList = new ArrayList <String> ();
	    smilesInchiKeyAndmultipleTagList.add("");
	    smilesInchiKeyAndmultipleTagList.add("");
	    smilesInchiKeyAndmultipleTagList.add("");
		if (sb.toString().indexOf("SMILES") < 0)
		    return smilesInchiKeyAndmultipleTagList;	
	    String cSmiles = sb.toString().substring(sb.toString().indexOf("SMILES"));	    
	    int numSmileTags =  ( sb.toString().length() - sb.toString().replace("SMILES", "").length())/"SMILES".length();
	    smilesInchiKeyAndmultipleTagList.clear();
	    smilesInchiKeyAndmultipleTagList.add(cSmiles.toString().substring(cSmiles.toString().lastIndexOf("<PC-InfoData_value_sval>")+"<PC-InfoData_value_sval>".length(),cSmiles.toString().lastIndexOf("</PC-InfoData_value_sval>")));
	    smilesInchiKeyAndmultipleTagList.add(numSmileTags > 2 ? "* There are multiple Smiles": "");
	    // issue 33
		if (numSmileTags <= 2)
	    	{  
			// issue 45
			if (sb.toString().indexOf("InChIKey") < 0)
			    {
				smilesInchiKeyAndmultipleTagList.add("");
				return smilesInchiKeyAndmultipleTagList;
			    }
	    	String cInchiKey = sb.toString().substring(sb.toString().indexOf("InChIKey"));			
	    	smilesInchiKeyAndmultipleTagList.add(cInchiKey.toString().substring(cInchiKey.toString().indexOf("<PC-InfoData_value_sval>")+"<PC-InfoData_value_sval>".length(),cInchiKey.toString().indexOf("</PC-InfoData_value_sval>")));
	    	}
		else 
		    smilesInchiKeyAndmultipleTagList.add("");
		return smilesInchiKeyAndmultipleTagList;
		}	
	}

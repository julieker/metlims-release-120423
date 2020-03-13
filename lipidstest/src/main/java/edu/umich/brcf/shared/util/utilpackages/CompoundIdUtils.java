package edu.umich.brcf.shared.util.utilpackages;
//Created by Julie Keros Mar 11, 2020

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import org.h2.util.StringUtils;

public class CompoundIdUtils
	{
	// issue 27 
	public static String grabSmilesFromInchiKey(String inchiKey)
	    {
	    StringBuilder sb = new StringBuilder();
	    URL url = null;
	    BufferedReader br = null;
		if (StringUtils.isNullOrEmpty(inchiKey))
		    return null;	   
		try 
	        {
	        url = new URL("https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/inchikey/" + inchiKey + "/xml");
	        } 
	    catch (MalformedURLException e2) 
	        {
		// TODO Auto-generated catch block
		    e2.printStackTrace();
		    return null;
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
			   }
		   return null;		    
	       }  
	     // Now parse the text....
	    if (sb.toString().indexOf("SMILES") < 0)
	        return null;	
	    String cSmiles = sb.toString().substring(sb.toString().indexOf("SMILES"));
	    return cSmiles.toString().substring(cSmiles.toString().lastIndexOf("<PC-InfoData_value_sval>")+"<PC-InfoData_value_sval>".length(),cSmiles.toString().lastIndexOf("</PC-InfoData_value_sval>"));     
	    }
	}

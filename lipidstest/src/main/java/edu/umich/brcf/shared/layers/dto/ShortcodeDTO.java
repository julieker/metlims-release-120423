package edu.umich.brcf.shared.layers.dto;

import java.io.Serializable;

import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.util.datacollectors.ClientDataInfo;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;



public class ShortcodeDTO implements Serializable
	{
	private String code;
	private String NIH_GrantNumber, NIH_GrantNumber_2, NIH_GrantNumber_3;
	private Experiment exp;

	private ShortcodeDTO(String code, String NIH_GrantNumber, Experiment exp)
		{
		this(code, NIH_GrantNumber, exp, "", "");
		}

	private ShortcodeDTO(String code, String NIH_GrantNumber, Experiment exp,
			String NIH_GrantNumber_2, String NIH_GrantNumber_3)
		{
		this.code = code;
		this.NIH_GrantNumber = NIH_GrantNumber;
		this.exp = exp;
		this.NIH_GrantNumber_2 = NIH_GrantNumber_2;
		this.NIH_GrantNumber_3 = NIH_GrantNumber_3;
		}

	public static ShortcodeDTO instance(String code, String NIH_GrantNumber,
			Experiment exp)
		{
		return new ShortcodeDTO(code, NIH_GrantNumber, exp, null, null);
		}

	public static ShortcodeDTO instance(String code, String NIH_GrantNumber,
			Experiment exp, String NIH_GrantNumber_2, String NIH_GrantNumber_3)
		{
		return new ShortcodeDTO(code, NIH_GrantNumber, exp, NIH_GrantNumber_2,
				NIH_GrantNumber_3);
		}

	public ShortcodeDTO()
		{
		}

	public String getCode()
		{
		return code;
		}

	public void setCode(String code)
		{
		this.code = code;
		}

	public String getNIH_GrantNumber()
		{
		return NIH_GrantNumber;
		}

	public void setNIH_GrantNumber(String grantNumber)
		{
		NIH_GrantNumber = grantNumber;
		}

	public String getNIH_GrantNumber_2()
		{
		return NIH_GrantNumber_2;
		}

	public void setNIH_GrantNumber_2(String grantNumber2)
		{
		NIH_GrantNumber_2 = grantNumber2;
		}

	public String getNIH_GrantNumber_3()
		{
		return NIH_GrantNumber_3;
		}

	public void setNIH_GrantNumber_3(String grantNumber3)
		{
		NIH_GrantNumber_3 = grantNumber3;
		}

	public Experiment getExp()
		{
		return exp;
		}

	public void setExp(Experiment exp)
		{
		this.exp = exp;
		}
	
	
	public void update(ClientDataInfo info)
		{
		updateForGrantString(info.getNihGrantNumber());
		}
	
	
	public void updateForGrantString(String grantStr)
		{
		if (StringUtils.isNullOrEmpty(grantStr) || "NO NIH GRANT".equals(grantStr.toUpperCase()))
	    	 setNIH_GrantNumber("No NIH Grant");
	    else
	    	{ 
	    	String [] grants = parseGrantNumbers(grantStr);
	    
	    	if (grants != null)
		    	{
		    	if (grants.length > 0) setNIH_GrantNumber(grants[0]);
		    	if (grants.length > 1) setNIH_GrantNumber_2(grants[1]);
		    	if (grants.length == 3) setNIH_GrantNumber_3(grants[2]);
		    	
		    	if (grants.length > 3)
		    		{
		    		StringBuilder sb = new StringBuilder();
		    		for (int j = 2; j < grants.length; j++)
		    			sb.append(grants[j] + ", ");
		    		
		    		setNIH_GrantNumber_3(sb.toString());
		    		}
			    }
	    	}
		}
	
	
	String [] parseGrantNumbers(String grantStr)
	 	{
	 	if (StringUtils.isNullOrEmpty(grantStr)) 
	 		return null;
	 	
		String [] grants = StringUtils.splitAndTrim(grantStr, ";");
	 	if (grants.length > 1) return grants;
	 	
	 	return StringUtils.splitAndTrim(grantStr, ",");
	 	}
	}

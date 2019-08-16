/////////////////////////////////////////

package edu.umich.brcf.shared.util;


public class SecurityOptions
	{
	public static boolean isTrustedId(String userId)
		{
		return !"U00037".equals(userId) && !"U00077".equals(userId);
		}
	
	public static boolean isTrustedName(String userName)
		{
		return !"araskind".equals(userName) && !"AUTO".equals(userName);
		}
	}

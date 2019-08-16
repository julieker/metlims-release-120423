////////////////////////////////////////////////////
// BCryptEncrypter.java
// Written by Jan Wigginton, Sep 9, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util;

//import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.util.Assert;



public class BCryptEncrypter
	{
	private static Logger log = Logger.getLogger(BCryptEncrypter.class);
	private static int reps = 13;
	
	public static String encrypt(String text_pwd)
		{
		//Base64 base = new Base64();
		String hashed_password = null;
		
		Assert.notNull(text_pwd);
		try
			{
			String salt = BCrypt.gensalt(reps);
			hashed_password =  BCrypt.hashpw(text_pwd, salt);
			} 
		catch (Exception e)
			{
			log.error("====================>  received UnsupportedEncodingException: " + e.getMessage());
			hashed_password = null;
			}
		
		return hashed_password;
		}
	}

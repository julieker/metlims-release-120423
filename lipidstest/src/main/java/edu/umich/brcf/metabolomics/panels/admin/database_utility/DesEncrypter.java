package edu.umich.brcf.metabolomics.panels.admin.database_utility;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.spec.KeySpec;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


// createNativeQuery

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
//import javax.xml.bind.DatatypeConverter;


import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.util.Assert;

//import sun.misc.BASE64Decoder;

public class DesEncrypter implements Serializable
	{
	private static Logger log = Logger.getLogger(DesEncrypter.class);
	Cipher ecipher;
	Cipher dcipher;

	byte[] salt = { (byte) 0x09, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
			(byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03 };

	int iterationCount = 19;

	public DesEncrypter(String passPhrase)
		{
		Assert.notNull(passPhrase);
		try
			{
			//BCrypt.gensalt();
			KeySpec keySpec = new DESKeySpec(passPhrase.getBytes("UTF8"));
			SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(
					keySpec);
			ecipher = Cipher.getInstance("DES");
			dcipher = Cipher.getInstance("DES");

			ecipher.init(Cipher.ENCRYPT_MODE, key);
			dcipher.init(Cipher.DECRYPT_MODE, key);
			} catch (UnsupportedEncodingException e)
			{
			log.error("====================>  received UnsupportedEncodingException: "
					+ e.getMessage());
			} catch (NoSuchPaddingException e)
			{
			log.error("====================>  received NoSuchPaddingException: "
					+ e.getMessage());
			} catch (NoSuchAlgorithmException e)
			{
			log.error("====================>  received NoSuchAlgorithmException: "
					+ e.getMessage());
			} catch (InvalidKeyException e)
			{
			log.error("====================>  received InvalidKeyException: "
					+ e.getMessage());
			} catch (Exception e)
			{
			log.error("====================>  received some exception: "
					+ e.getMessage());
			}
		}

	public String encrypt(String str)
		{
		Base64 base = new Base64();

		Assert.notNull(str);
		try
			{
			byte[] utf8 = str.getBytes("UTF8");
			byte[] enc = ecipher.doFinal(utf8);
			// return new sun.misc.BASE64Encoder().encode(enc);
			return base.encodeAsString(enc);
			// return DatatypeConverter.printBase64Binary(enc); // String
			// decoded = new
			// String(DatatypeConverter.parseBase64Binary(encoded));
			} catch (Exception e)
			{
			log.error("====================>  received UnsupportedEncodingException: "
					+ e.getMessage());
			}
		return null;
		}

	public String decrypt(String str)
		{
		Assert.notNull(str);
		try
			{
			// byte[]dec = new BASE64Decoder().decodeBuffer(str);
			Base64 base = new Base64();
			byte[] dec = base.decodeBase64(str);
			byte[] utf8 = dcipher.doFinal(dec);
			return new String(utf8, "UTF8");
			} catch (BadPaddingException e)
			{
			} catch (IllegalBlockSizeException e)
			{
			} catch (UnsupportedEncodingException e)
			{
			} catch (IOException e)
			{
			}
		return null;
		}

	// This method returns all available services types
	public static String[] getServiceTypes()
		{
		Set result = new HashSet();

		// All all providers
		Provider[] providers = Security.getProviders();
		for (int i = 0; i < providers.length; i++)
			{
			// Get services provided by each provider
			Set keys = providers[i].keySet();
			for (Iterator it = keys.iterator(); it.hasNext();)
				{
				String key = (String) it.next();
				key = key.split(" ")[0];

				if (key.startsWith("Alg.Alias."))
					{
					// Strip the alias
					key = key.substring(10);
					}
				int ix = key.indexOf('.');
				result.add(key.substring(0, ix));
				}
			}
		return (String[]) result.toArray(new String[result.size()]);
		}

	// This method returns the available implementations for a service type
	public static String[] getCryptoImpls(String serviceType)
		{
		Set result = new HashSet();

		// All all providers
		Provider[] providers = Security.getProviders();
		for (int i = 0; i < providers.length; i++)
			{
			// Get services provided by each provider
			Set keys = providers[i].keySet();
			for (Iterator it = keys.iterator(); it.hasNext();)
				{
				String key = (String) it.next();
				key = key.split(" ")[0];

				if (key.startsWith(serviceType + "."))
					{
					result.add(key.substring(serviceType.length() + 1));
					} else if (key.startsWith("Alg.Alias." + serviceType + "."))
					{
					// This is an alias
					result.add(key.substring(serviceType.length() + 11));
					}
				}
			}
		return (String[]) result.toArray(new String[result.size()]);
		}
	}

package edu.umich.brcf.shared.util;


public class METWorksException extends Exception 
	{
	private String metworksMessageTag = "MetLIMS error : ";
	private String metworksMessage; 
	
	public METWorksException(Exception e)
		{
		this(e.getMessage());
		}

	public METWorksException(String msg)
		{
		this(msg, false);
		}
	
	public METWorksException(String msg, boolean showSystemMessage) 
		{
		super(msg);
		setMetworksMessage(metworksMessageTag + msg + (showSystemMessage ? System.getProperty("line.separator") + getMessage() : ""));
		}

	public String getMetworksMessage()
		{
		return metworksMessage;
		}
	
	public void setMetworksMessage(String msg)
		{
		metworksMessage = msg;
		}
	}

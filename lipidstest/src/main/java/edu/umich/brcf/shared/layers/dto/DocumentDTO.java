package edu.umich.brcf.shared.layers.dto;

import java.io.Serializable;


public class DocumentDTO implements Serializable
	{
	private byte[] fileContents;
	private String accosiated;
	private String discriminator;
	private String fileName;
	private String fileType;
	private String associatedAssay;

	public byte[] getFileContents()
		{
		return fileContents;
		}

	public void setFileContents(byte[] fileContents)
		{
		this.fileContents = fileContents;
		}

	public String getAccosiated()
		{
		return accosiated;
		}

	public void setAccosiated(String accosiated)
		{
		this.accosiated = accosiated;
		}

	public String getDiscriminator()
		{
		return discriminator;
		}

	public void setDiscriminator(String discriminator)
		{
		this.discriminator = discriminator;
		}

	public String getFileName()
		{
		return fileName;
		}

	public void setFileName(String fileName)
		{
		this.fileName = fileName;
		}

	public String getFileType()
		{
		return fileType;
		}

	public void setFileType(String fileType)
		{
		this.fileType = fileType;
		}

	public String getAssociatedAssay()
		{
		return associatedAssay;
		}

	public void setAssociatedAssay(String associatedAssay)
		{
		this.associatedAssay = associatedAssay;
		}
	}

//Written by Jan Wigginton June 2015
//METWorksDataDownload.java

package edu.umich.brcf.shared.util.io;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.model.PropertyModel;

import edu.umich.brcf.shared.util.interfaces.IWriteConvertable;


public class MetWorksDataDownload implements Serializable
	{
	/*
	WebFileDownloadResource resource = null;
	ResourceLink resourceLink;
	private String linkId = "downloadData";
	private String fileName = "file.tsv"; 
	private List<String> colTitles = null;
	String mimeType = "text/tsv";
	
	public MetWorksDataDownload()
		{
		}
	
	public MetWorksDataDownload(String linkId, List <? extends IWriteConvertable> infoList, String fileName)
		{
		this(linkId, infoList, fileName, null);
		}

	public MetWorksDataDownload(List <? extends IWriteConvertable> infoList, String fileName)
		{
		this("downloadData", infoList, fileName, null);
		}
	
	public MetWorksDataDownload(List<? extends IWriteConvertable> infoList, String fileName, List<String> colTitles)
		{
		this("downloadData", infoList, fileName, colTitles);
		}
	
	public MetWorksDataDownload(String linkId, List<? extends IWriteConvertable> infoList, String fileName, List<String> colTitles)
		{
		this.colTitles = colTitles;
		this.fileName = fileName;
		this.linkId = linkId;

		resource = new WebFileDownloadResource(infoList);
		resource.setCacheable(false);	
	
		resource.setColTitlesListModel(new PropertyModel <List<String>>(this, "colTitles"));
		resource.setOutfileName(new PropertyModel<String> (this, "fileName"));
		resource.setMimeType("text/tsv");
		resourceLink = new ResourceLink(linkId, resource);
		}
	public MetWorksDataDownload(String linkId, PropertyModel <List<? extends IWriteConvertable>> infoList, String fileName, List<String> colTitles)
		{
		this.colTitles = colTitles;
		this.fileName = fileName;
		this.linkId = linkId;

		resource = new WebFileDownloadResource(infoList);
		resource.setCacheable(false);	
	
		resource.setColTitlesListModel(new PropertyModel <List<String>>(this, "colTitles"));
		resource.setOutfileName(new PropertyModel<String> (this, "fileName"));
		resource.setMimeType("text/tsv");
		resourceLink = new ResourceLink(linkId, resource);
	
		}

	
	public ResourceLink getResourceLink()
		{
		return resourceLink;
		}
	
	public String getFileName()
		{
		return fileName;
		}
	
	public void setFileName(String fileName)
		{
		this.fileName = fileName;
		}
	
	public List<String> getColTitles()
		{
		return colTitles;
		}
	
	public void setColTitles(List <String> colTitles)
		{
		this.colTitles = colTitles;
		}
	
	public void setLinkId(String linkId)
		{
		this.linkId = linkId;
		}
	
	public void setMimeType(String mimeType)
		{
		resource.setMimeType(mimeType);
		}

	public WebFileDownloadResource getResource() {
		return resource;
	}

	public void setResource(WebFileDownloadResource resource) {
		this.resource = resource;
	}
 */
	
	}
	
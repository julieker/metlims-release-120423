////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//WebFileDownloadResource.java
//Written by Jan Wigginton
//February 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


package edu.umich.brcf.shared.util.io;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.resource.ZipResourceStream;

import edu.umich.brcf.shared.util.interfaces.IWriteConvertable;



public class WebFileDownloadResource extends ByteArrayResource implements Serializable
{

	public WebFileDownloadResource(String contentType, byte[] array)
		{
		super(contentType, array);
		// TODO Auto-generated constructor stub
		}
/*
private static final long serialVersionUID = -7627204374967270692L;

List <? extends IWriteConvertable> valuesToWrite;
PropertyModel<List <? extends IWriteConvertable>> valuesToWriteModel = null;

WriteConvertableList colTitles;

private String mimeType = "text/csv";
private String lineSeparator = System.getProperty("line.separator");
private String outfileName = "cats.csv";
private Boolean returnZipped = false;

PropertyModel <String> outnameSource;
PropertyModel <List<String>> colTitlesListModel;


public WebFileDownloadResource() 
{
super();
colTitles = null;
valuesToWrite = null; //new ArrayList<String>();
}

public  WebFileDownloadResource(PropertyModel<List <? extends IWriteConvertable>> model) 
{
this.valuesToWriteModel = model;
}


public WebFileDownloadResource(PropertyModel<List <? extends IWriteConvertable>> vtw, List<? extends Object> ct) 
{
colTitles = new WriteConvertableList(ct);
valuesToWriteModel = vtw;
}

public  WebFileDownloadResource(List <? extends IWriteConvertable> valuesToWrite) 
{
this.valuesToWrite = valuesToWrite;
}


public WebFileDownloadResource(List <? extends IWriteConvertable> vtw, List<? extends Object> ct) 
{
colTitles = new WriteConvertableList(ct);
valuesToWrite = vtw;
}

@Override
public IResourceStream getResourceStream() 
{
if (returnZipped)
return getZippedResourceStream();

StringBuilder builder = new StringBuilder();
builder.append(writeColHeaders());

if (valuesToWriteModel != null)
valuesToWrite = valuesToWriteModel.getObject();

//for (IWriteConvertable item : valuesToWriteModel.getObject())
//	{
for (int i = 0; i < valuesToWrite.size(); i++)
{
IWriteConvertable item = valuesToWrite.get(i);
String line = writeConverted(item);
System.out.println(line);
if (line != null)
builder.append(line +  lineSeparator);
}

return new StringResourceStream(builder.toString(), mimeType);
}


public IResourceStream getZippedResourceStream()
{
List<String> lines = new ArrayList<String>();

lines.add("test");
if (valuesToWriteModel != null)
valuesToWrite = valuesToWriteModel.getObject();

for (IWriteConvertable item : valuesToWrite)
{
String line = writeConverted(item);
if (line != null)
lines.add(line +  lineSeparator);
}

File file = FileUtils.writeToFile(lines, outfileName);
org.apache.wicket.util.file.File valuesFile = new org.apache.wicket.util.file.File(file);

return new ZipResourceStream(valuesFile);
}	


private String writeColHeaders()
{
StringBuilder builder  = new StringBuilder();

colTitles = getColTitles();

if (colTitles != null)
{
switch (mimeType)
{
case "text/csv" : 		builder.append(colTitles.toCharDelimited(", ")); break; 
case "text/tsv" : 		builder.append(colTitles.toCharDelimited("\t")); break; 
case "application/xls": builder.append(colTitles.toExcelRow()); break; 
default : 				builder.append(colTitles.toCharDelimited("\t")); break; 
}
}		
builder.append(System.getProperty("line.separator"));
return builder.toString();
}


protected void setHeaders(WebResponse response)
{
// TO DO : REVIEW setAttachmentHeader call;
super.setHeaders(response);
response.setAttachmentHeader(getOutfileName());
}


public String writeConverted(IWriteConvertable item)
{
switch (mimeType)
{
case "text/csv" : 			return item.toCharDelimited(", "); 
case "text/tsv" : 			return item.toCharDelimited("\t");
case "application/xls" : 	return item.toExcelRow();
default : 					return item.toString();
}
}


public void setMimeType(String type)
{
switch (type)
{
case  		"text/csv" :
case  		"text/tsv" :
case      	"application/xls" : mimeType = type; break;
default : 	mimeType = "text/tsv";
}
}	


public void setOutfileName(String name)
{
this.outfileName = name;
}


public void setOutfileName(PropertyModel <String> model)
{
outnameSource = model;
}


public String getOutfileName()
{
return outnameSource != null ?  outnameSource.getObject().toString() : outfileName;
}


public void setValuesToWriteModel(PropertyModel <List <? extends IWriteConvertable>> model)
{
this.valuesToWriteModel = model;
}


public void setColTitlesListModel(PropertyModel <List<String>> model)
{
colTitlesListModel = model;
}

public List<String> getColTitlesListModel()
{
return (colTitlesListModel != null ?  (List<String>) colTitlesListModel.getObject() :  null);
}

public WriteConvertableList getColTitles()
{
if (getColTitlesListModel() != null)
return new WriteConvertableList(getColTitlesListModel());

return colTitles;
}

public Boolean getReturnZipped()
{
return this.returnZipped;
}

public void setReturnZipped(Boolean zipped)
{
this.returnZipped = zipped;
}  */
}





/*
package edu.umich.brcf.shared.util.io;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.resource.ZipResourceStream;

import edu.umich.brcf.shared.util.interfaces.IWriteConvertable;
import org.apache.wicket.request.resource.ByteArrayResource;




public class WebFileDownloadResource extends ByteArrayResource implements Serializable
{
private static final long serialVersionUID = -7627204374967270692L;

List <? extends IWriteConvertable> valuesToWrite;
PropertyModel<List <? extends IWriteConvertable>> valuesToWriteModel = null;

WriteConvertableList colTitles;

private String mimeType = "text/csv";
private String lineSeparator = System.getProperty("line.separator");
private String outfileName = "cats.csv";
private Boolean returnZipped = false;

PropertyModel <String> outnameSource;
PropertyModel <List<String>> colTitlesListModel;


public WebFileDownloadResource() 
{
colTitles = null;
valuesToWrite = null; //new ArrayList<String>();
}

public  WebFileDownloadResource(PropertyModel<List <? extends IWriteConvertable>> model) 
{
this.valuesToWriteModel = model;
}


public WebFileDownloadResource(PropertyModel<List <? extends IWriteConvertable>> vtw, List<? extends Object> ct) 
{
colTitles = new WriteConvertableList(ct);
valuesToWriteModel = vtw;
}

public  WebFileDownloadResource(List <? extends IWriteConvertable> valuesToWrite) 
{
this.valuesToWrite = valuesToWrite;
}


public WebFileDownloadResource(List <? extends IWriteConvertable> vtw, List<? extends Object> ct) 
{
colTitles = new WriteConvertableList(ct);
valuesToWrite = vtw;
}

@Override
public IResourceStream getResourceStream() 
{
if (returnZipped)
return getZippedResourceStream();

StringBuilder builder = new StringBuilder();
builder.append(writeColHeaders());

if (valuesToWriteModel != null)
valuesToWrite = valuesToWriteModel.getObject();

//for (IWriteConvertable item : valuesToWriteModel.getObject())
//	{
for (int i = 0; i < valuesToWrite.size(); i++)
{
IWriteConvertable item = valuesToWrite.get(i);
String line = writeConverted(item);
System.out.println(line);
if (line != null)
builder.append(line +  lineSeparator);
}

return new StringResourceStream(builder.toString(), mimeType);
}


public IResourceStream getZippedResourceStream()
{
List<String> lines = new ArrayList<String>();

lines.add("test");
if (valuesToWriteModel != null)
valuesToWrite = valuesToWriteModel.getObject();

for (IWriteConvertable item : valuesToWrite)
{
String line = writeConverted(item);
if (line != null)
lines.add(line +  lineSeparator);
}

File file = FileUtils.writeToFile(lines, outfileName);
org.apache.wicket.util.file.File valuesFile = new org.apache.wicket.util.file.File(file);

return new ZipResourceStream(valuesFile);
}	


private String writeColHeaders()
{
StringBuilder builder  = new StringBuilder();

colTitles = getColTitles();

if (colTitles != null)
{
switch (mimeType)
{
case "text/csv" : 		builder.append(colTitles.toCharDelimited(", ")); break; 
case "text/tsv" : 		builder.append(colTitles.toCharDelimited("\t")); break; 
case "application/xls": builder.append(colTitles.toExcelRow()); break; 
default : 				builder.append(colTitles.toCharDelimited("\t")); break; 
}
}		
builder.append(System.getProperty("line.separator"));
return builder.toString();
}


protected void setHeaders(WebResponse response)
{
// TO DO : REVIEW setAttachmentHeader call;
super.setHeaders(response);
response.setAttachmentHeader(getOutfileName());
}


public String writeConverted(IWriteConvertable item)
{
switch (mimeType)
{
case "text/csv" : 			return item.toCharDelimited(", "); 
case "text/tsv" : 			return item.toCharDelimited("\t");
case "application/xls" : 	return item.toExcelRow();
default : 					return item.toString();
}
}


public void setMimeType(String type)
{
switch (type)
{
case  		"text/csv" :
case  		"text/tsv" :
case      	"application/xls" : mimeType = type; break;
default : 	mimeType = "text/tsv";
}
}	


public void setOutfileName(String name)
{
this.outfileName = name;
}


public void setOutfileName(PropertyModel <String> model)
{
outnameSource = model;
}


public String getOutfileName()
{
return outnameSource != null ?  outnameSource.getObject().toString() : outfileName;
}


public void setValuesToWriteModel(PropertyModel <List <? extends IWriteConvertable>> model)
{
this.valuesToWriteModel = model;
}


public void setColTitlesListModel(PropertyModel <List<String>> model)
{
colTitlesListModel = model;
}

public List<String> getColTitlesListModel()
{
return (colTitlesListModel != null ?  (List<String>) colTitlesListModel.getObject() :  null);
}

public WriteConvertableList getColTitles()
{
if (getColTitlesListModel() != null)
return new WriteConvertableList(getColTitlesListModel());

return colTitles;
}

public Boolean getReturnZipped()
{
return this.returnZipped;
}

public void setReturnZipped(Boolean zipped)
{
this.returnZipped = zipped;
}
}

*/



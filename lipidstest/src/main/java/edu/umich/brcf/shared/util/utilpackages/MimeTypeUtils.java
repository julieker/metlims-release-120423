package edu.umich.brcf.shared.util.utilpackages;

///////////////////////////////////////
//MimeTypeUtils.java
//Written by Jan Wigginton August 2015
///////////////////////////////////////

public class MimeTypeUtils
	{
	public static String getExtensionForMimeType(String mimeType)
		{
		if (mimeType == null)
			return "";

		String lc = mimeType.toLowerCase().trim();

		if (lc.equals("application/xml"))
			return "cef";
		if (lc.equals("application/msword"))
			return "doc";

		if (lc.equals("application/powerpoint"))
			return "ppt";
		if (lc.equals("application/powerpoint"))
			return "pptx";
		if (lc.equals("application/excel"))
			return "xls";
		if (lc.equals("application/zip"))
			return "zip";
		if (lc.equals("application/pdf"))
			return "pdf";
		if (lc.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
			return "doc";
		if (lc.equals("text/xml"))
			return "xml";
		if (lc.equals("application/vnd.ms-excel"))
			return "xlsx";
		if (lc.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
			return "xlsx";
		if (lc.equals("application/octet-stream"))
			return "zip";

		return "";
		}

	public static String getMimeTypeForExtension(String extension)
		{
		if (extension == null)
			return "";

		String uc = extension.toUpperCase().trim();

		if (uc.equals("CEF"))
			return "application/xml";
		if (uc.equals("DOC"))
			return "application/msword";
		if (uc.equals("DOCX"))
			return "application/msword";
		if (uc.equals("PPT"))
			return "application/powerpoint";
		if (uc.equals("PPTX"))
			return "application/powerpoint";
		if (uc.equals("XLS"))
			return "application/excel";
		if (uc.equals("XLSX"))
			return "application/excel";
		if (uc.equals("ZIP"))
			return "application/zip";
		if (uc.equals("PDF"))
			return "application/pdf";
		if (uc.equals("DOC"))
			return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
		if (uc.equals("XML"))
			return "text/xml";
		if (uc.equals("XLSX"))
			return "application/vnd.ms-excel";
		if (uc.equals("XLSX"))
			return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		if (uc.equals("PPTX"))
			return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
		if (uc.equals("ZIP"))
			return "application/octet-stream";

		return "";
		}
	}

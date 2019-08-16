//////////////////////////////////////////
//ISampleWorkbookReader.java
//Written by Jan Wigginton February 2016
//////////////////////////////////////////


package edu.umich.brcf.shared.util.interfaces;

import java.io.File;

import org.apache.wicket.markup.html.form.upload.FileUpload;

import edu.umich.brcf.shared.util.SampleSheetIOException;


public interface ISampleWorkbookReader
	{
	public ISavableSampleData readWorkBook(File newFile, FileUpload upload) throws SampleSheetIOException;
	}

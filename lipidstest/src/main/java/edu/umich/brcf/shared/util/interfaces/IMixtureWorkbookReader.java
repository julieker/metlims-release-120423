//////////////////////////////////////////
//ISampleWorkbookReader.java
//Written by Jan Wigginton February 2016
//////////////////////////////////////////


package edu.umich.brcf.shared.util.interfaces;

import java.io.File;

import org.apache.wicket.markup.html.form.upload.FileUpload;

import edu.umich.brcf.shared.util.MixtureSheetIOException;
import edu.umich.brcf.shared.util.SampleSheetIOException;


public interface IMixtureWorkbookReader
	{
	public ISavableMixtureData readWorkBook(File newFile, FileUpload upload) throws MixtureSheetIOException;
	}

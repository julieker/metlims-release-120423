//////////////////////////////////////////
//IMixtureWorkbookReader.java
//Written by Julie Keros Dec 2020
//////////////////////////////////////////


package edu.umich.brcf.shared.util.interfaces;

import java.io.File;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import edu.umich.brcf.shared.util.MixtureSheetIOException;

public interface IMixtureWorkbookReader
	{
	public ISavableMixtureData readWorkBook(File newFile, FileUpload upload) throws MixtureSheetIOException;
	}

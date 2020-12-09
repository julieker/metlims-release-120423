////////////////////////////////////////////////////
// Mrc2TransitionalMixtureSheetReader.java
// Written by Julie Keros, Oct 2020 issue 94
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.sheetreaders;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import edu.umich.brcf.shared.util.MixtureSheetIOException;
import edu.umich.brcf.shared.util.datacollectors.Mrc2MixtureMetadata;
import edu.umich.brcf.shared.util.datacollectors.Mrc2TransitionalMixtureSheetData;
import edu.umich.brcf.shared.util.interfaces.IMixtureWorkbookReader;
import edu.umich.brcf.shared.util.io.SpreadSheetReader; 



// NOTE : This reader is a temporary measure to safely transition from the stable but badly designed old submission sheet readers
// We will be slowly moving each sheet read out into its own class and transfer the shortcode save to the submission data service
// as it should have been already...  Once the plain reader is stable, we will be incorporating the add uplaod code.

public class Mrc2TransitionalMixtureSheetReader extends SpreadSheetReader implements Serializable, IMixtureWorkbookReader
	{
	private String sheetName; // JAK change 3
	private Mrc2TransitionalMixtureSheetData data = null; // JAK change 4
	private Mrc2MixtureMetadata data2 = null; 
	
	public Mrc2TransitionalMixtureSheetReader() 
		{
		Injector.get().inject(this);
		}
		
	public Mrc2TransitionalMixtureSheetData readWorkBook(File newFile, FileUpload upload) throws MixtureSheetIOException
		{
		data = new Mrc2TransitionalMixtureSheetData(); 
		data2 = new Mrc2MixtureMetadata();		
		try
			{
			Workbook workbook = createWorkBook(newFile, upload); // JAK change 5
			Mrc2TransitionalMixturesMetadataReader mixtureReader = new Mrc2TransitionalMixturesMetadataReader();
			List<String> sheets = Arrays.asList(new String [] { "Mixture"});
			for (String sheetname : sheets) 
				{
				Sheet sheet = workbook.getSheet(sheetname);
				sheetName = sheetname; // JAK change 6
				data2 = mixtureReader.read(sheet);
				data.setMixtureMetadata(data2);
				}
			}
		catch (Exception e) { 
			e.printStackTrace();
			throw new MixtureSheetIOException(e.getMessage() , -1 , sheetName);  }  
		return data;
		}
	}
		

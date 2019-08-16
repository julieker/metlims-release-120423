package edu.umich.brcf.metabolomics.panels.lims.prep;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.file.Folder;

import edu.umich.brcf.metabolomics.layers.dto.AbsorbanceDTO;
import edu.umich.brcf.shared.layers.service.SamplePrepService;


public class EditAbsorbancePage extends WebPage
	{
	@SpringBean
	SamplePrepService samplePrepService;
	

	private FileUploadField fileUploadField;
	
	public EditAbsorbancePage(String preparation, int cols, int rows)
		{
		add(new FeedbackPanel("feedback"));
		add(new AbsorbanceForm("absorbanceForm", preparation, cols, rows));
		}

	public final class AbsorbanceForm extends Form 
		{
		public AbsorbanceForm(final String id, final String preparation, final int cols, final int rows){
			super(id);
			add(fileUploadField=new FileUploadField("fileInput"));
	        add(new Label("fileUploadText", "Upload Protein Readings"));
	        add(new Label("fileText", "File"));
	        add(new Button("upload")
	        	{
	        	@Override
	        	public void onSubmit()
	        		{
//	        		uploadFile(preparation, cols, rows);
	        		}
	        	});
			}
		}
	
	
	public void uploadFile(String preparation, int cols, int rows)
		{
		BigDecimal[][] protienReadings=new BigDecimal[rows][cols];
		AbsorbanceDTO[] pReads = new AbsorbanceDTO[96];
        final FileUpload upload = fileUploadField.getFileUpload();
        if (upload != null)
        {
            if(upload.getContentType().equalsIgnoreCase("application/vnd.ms-excel"))
            {
                // Create a new file
                File newFile = new File(getUploadFolder(), upload.getClientFileName());
                checkFileExists(newFile);
                try
                {
                    // Save to new file
                    newFile.createNewFile();
                    upload.writeTo(newFile);
                }catch (Exception e)
                {
                    throw new IllegalStateException("Unable to write file");
                }
                int rowCount=0,cellCount=0;
                try
                {
                	HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(newFile));
                    HSSFSheet sheet = workbook.getSheetAt(0);
                    Row row;
                    Cell cell;
                    Iterator<Row> sheet_rows = sheet.rowIterator ();
                    while(sheet_rows.hasNext())
                    {
                    	++rowCount;
                    	cellCount=0;
                    	row=sheet_rows.next();//sheet.getRow(rowCount++);
                    	if ((rowCount>1)&&(rowCount<98))
	                    {
                			if((row.getCell((short) 1)==null)||(row.getCell((short) 1).toString()==null)||(row.getCell((short) 1).toString().trim().length()==0))
                				break;
                			pReads[rowCount-2] = new AbsorbanceDTO();
       					 	Iterator<Cell> cells=row.cellIterator();
                    		 while(cells.hasNext()){
                    			 ++cellCount;
                    			 cell=cells.next();
                				 if((cellCount>0)&&(cellCount<5)){
                    				 if((cell==null)||(cell.toString()==null)||(cell.toString().trim().length()==0)){
                    					 EditAbsorbancePage.this.error("Unable to upload file, error at row : "+rowCount+ ", column: "+cellCount);
                    				 	break;
                    				 }
                    				 else
                    				 {
                    					  switch (cellCount)
                    		                {
                    		                    case 1:
                    		                    	pReads[rowCount-2].setWellIndex(cell.toString().trim());
                    		                        break;
                    		                    case 2:
                    		                    	pReads[rowCount-2].setAbsorbance1(new BigDecimal(cell.toString().trim()));
                    		                        break;
                    		                    case 3:
                    		                    	pReads[rowCount-2].setAbsorbance2(new BigDecimal(cell.toString().trim()));
                    		                        break;
                    		                    case 4:
                    		                    	pReads[rowCount-2].setConcentration(new BigDecimal(cell.toString().trim()));
                    		                        break;
                    		                }
                    				 }
//                    				 protienReadings[rowCount-2][cellCount-2]= new BigDecimal(cell.toString());
//                    				 System.out.println("["+(rowCount-2)+"]["+(cellCount-2)+"] "+protienReadings[rowCount-2][cellCount-2]);
                    			 }
                				 else
                					 break;
                    		 }
                    	}
                    }
//                    PrepPlate prepPlate=samplePrepService.loadPlateByID(plate);
                    for (int s=0;s<pReads.length;s++)
                    	System.out.println(pReads[s]);
                    String errorAt=samplePrepService.assignProtienReadings(preparation,pReads);
                    if(errorAt.equals("none"))
                    	EditAbsorbancePage.this.info("Save Successful!");
                    else if (errorAt.equals("0"))
                    	EditAbsorbancePage.this.error("0 readings were uploaded! Please make sure that Protein Readings are in sheet 1 of the document being uploaded!");
                    else
                    	EditAbsorbancePage.this.error("Unable to upload file, error assigning value to sample in location: "+errorAt);
	            }
                catch (Exception e)
                {
                	e.printStackTrace();
                	EditAbsorbancePage.this.error("Unable to upload file, error in sheet 1 at line: "+rowCount+", cell:"+cellCount);
                }
            }
        }
    }
	
	private void checkFileExists(File newFile)
    {
        if (newFile.exists())
        {
            // Try to delete the file
            if (!Files.remove(newFile))
            {
                throw new IllegalStateException("Unable to overwrite " + newFile.getAbsolutePath());
            }
        }
    }

    private Folder getUploadFolder()
    {
    	Folder uploadFolder = new Folder(System.getProperty("java.io.tmpdir"), "sample-uploads");
        // Ensure folder exists
        uploadFolder.mkdirs();
        return (uploadFolder);
    }
}

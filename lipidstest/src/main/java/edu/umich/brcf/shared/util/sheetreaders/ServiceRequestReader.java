/////////////////////////////////////////////
//ServiceRequestReader.java
//Written by Jan Wigginton December 2015
/////////////////////////////////////////////

package edu.umich.brcf.shared.util.sheetreaders;


import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.util.file.Files;

import edu.umich.brcf.shared.util.io.SpreadSheetReader;

/*
import edu.umich.brcf.mchear.layers.dto.AssayRequestHolder;
import edu.umich.brcf.mchear.layers.dto.ServiceRequestDTO;
import edu.umich.brcf.mchear.panels.sampletools.datacollectors.ServiceRequestPreview;
import edu.umich.brcf.shared.util.METWorksException;
import edu.umich.brcf.shared.util.io.SpreadSheetReader;

*/

public class ServiceRequestReader extends SpreadSheetReader
	{
	public ServiceRequestReader() {}
	
	/*
	
	public ServiceRequestPreview previewSheet(FileUpload upload) throws METWorksException
		{
		ServiceRequestPreview preview = null; //new ServiceRequestPreview(); 
		File newFile = null;
		try
			{
			newFile = uploadFile(upload);
			Workbook workbook = createWorkBook(newFile, upload);
			preview = readPreviewData(workbook);
			}
		catch (METWorksException e)
			{
			throw e;
			}
		catch (Exception e)
			{
			e.printStackTrace();
			throw new METWorksException("Unable to upload file -- error while previewing sheet");
			}
		finally { Files.remove(newFile); }
		
		return preview;
		}
	
	
	private ServiceRequestPreview readPreviewData(Workbook workbook) throws METWorksException
		{
		boolean report = true; 
		ServiceRequestPreview previewData = new ServiceRequestPreview();
		Sheet sheet = workbook.getSheetAt(0);
		
		previewData.setProjName(readProjectName(sheet, report));
		
		previewData.setPiName(readPiFirstName(sheet, report) + ", " + readPiLastName(sheet, report));
		previewData.getDto().setPiLastName(readPiLastName(sheet, report));
		previewData.getDto().setPiFirstName(readPiLastName(sheet, report));
		previewData.getDto().setPiAddress(readPiAddress(sheet, report));
		previewData.getDto().setPiEMail(readPiEmail(sheet, report));
		previewData.getDto().setPiPhone(readPiPhone(sheet, report));
		
		previewData.setClientName(previewData.getPiName());
		
		previewData.setOrgName(readOrganization(sheet, report));
		previewData.getDto().setOrgAddress(readOrgAddress(sheet, report));
		previewData.getDto().setDepartment(readDepartment(sheet, report));
		previewData.getDto().setLab(readLab(sheet, report));
		
		previewData.setContactName(readContactFirstName(sheet, report) + ", " + readContactLastName(sheet, report));
		previewData.getDto().setContactLastName(readContactLastName(sheet, report));
		previewData.getDto().setContactFirstName(readContactFirstName(sheet, report));
		previewData.getDto().setContactAddress(readContactAddress( sheet, report));
		previewData.getDto().setContactEMail(readContactEmail(sheet, report));
		previewData.getDto().setContactPhone(readContactPhone(sheet, report));
		
		previewData.getDto().setTotalSamples(readTotalSamples(sheet, report));
		
		return previewData;
		}
	
	
	private String readProjectName(Sheet sheet, boolean report)
		{
		return getDataAt(5,2, sheet, report);
		}
	
	private String readTotalSamples(Sheet sheet, boolean report)
		{
		return getDataAt(20,2, sheet, report);
		}
	
	private String readContactFirstName(Sheet sheet, boolean report)
		{
		return getDataAt(15,2, sheet, report);
		}
	
	private String readContactLastName(Sheet sheet, boolean report)
		{
		return getDataAt(16,2, sheet, report);
		}
	
	private String readContactAddress(Sheet sheet, boolean report)
		{
		return getDataAt(17,2, sheet, report);
		}
	
	private String readContactPhone(Sheet sheet, boolean report)
		{
		return getDataAt(19,2, sheet, report);
		}
	
	private String readContactEmail(Sheet sheet, boolean report)
		{
		return getDataAt(18,2, sheet, report);
		}
	
	
	private String readPiFirstName(Sheet sheet, boolean report)
		{
		return getDataAt(6,2, sheet, report);
		}
	
	private String readPiLastName(Sheet sheet, boolean report)
		{
		return getDataAt(7,2, sheet, report);
		}
	
	private String readPiAddress(Sheet sheet, boolean report)
		{
		return getDataAt(8,2, sheet, report);
		}
	
	private String readPiPhone(Sheet sheet, boolean report)
		{
		return getDataAt(10,2, sheet, report);
		}
	
	private String readPiEmail(Sheet sheet, boolean report)
		{
		return getDataAt(9,2, sheet, report);
		}
	
	private String readOrganization(Sheet sheet, boolean report)
		{
		return getDataAt(11,2, sheet, report);
		}
	
	private String readOrgAddress(Sheet sheet, boolean report)
		{
		return getDataAt(12,2, sheet, report);
		}
	
	private String readDepartment(Sheet sheet, boolean report)
		{
		return getDataAt(13,2, sheet, report);
		}
	
	private String readLab(Sheet sheet, boolean report)
		{
		return getDataAt(14,2, sheet, report);
		}
	
	
	
	private void readServiceRequest(Workbook workbook)
		{
		ServiceRequestDTO requestDTO = new ServiceRequestDTO();
		Sheet sheet = workbook.getSheetAt(0);
		Boolean report = false;
		
		requestDTO.setPiFirstName(readPiFirstName(sheet, report ));
		requestDTO.setPiLastName(readPiFirstName(sheet, report ));
		requestDTO.setPiAddress(readPiAddress(sheet, report ));
		requestDTO.setPiEMail(readPiEmail(sheet, report ));
		requestDTO.setPiPhone(readPiPhone(sheet, report ));
		
		requestDTO.setContactFirstName(readPiFirstName(sheet, report ));
		requestDTO.setContactLastName(readPiFirstName(sheet, report ));
		requestDTO.setContactAddress(readPiAddress(sheet, report ));
		requestDTO.setContactEMail(readPiEmail(sheet, report ));
		requestDTO.setContactPhone(readPiPhone(sheet, report ));
		
		requestDTO.setOrganization(readOrganization(sheet, report ));
		requestDTO.setOrgAddress(readOrgAddress(sheet, report ));
		requestDTO.setDepartment(readDepartment(sheet, report ));
		requestDTO.setLab(readLab(sheet, report ));
		
		requestDTO.setTotalSamples(this.readTotalSamples(sheet, report));
		requestDTO.setProjectName(readProjectName(sheet, report ));
		requestDTO.setStatusId("R");
		//	requestDTO.setServiceRequestId();
		
		List<AssayRequestHolder> dtos = readAssayDTOs(sheet, report);
		}
	
	
	private List<AssayRequestHolder> readAssayDTOs(Sheet sheet, Boolean report)
		{
		List<AssayRequestHolder> dtos = new ArrayList<AssayRequestHolder>();
		Iterator<Row> rows = sheet.rowIterator();
		
		int idx = 0;
		while (rows.hasNext())
			{
			Row row = rows.next();
			
			String area = row.getCell((short)2).toString().trim();
			String core = row.getCell((short)3).toString().trim();
			String assay = row.getCell((short) 4).toString().trim();
			String sampleType = row.getCell((short)5).toString().trim();
			String nSamples = row.getCell((short)6).toString().trim();
			
			dtos.add(new AssayRequestHolder(area, core, assay, sampleType, "", nSamples));
			}
			
		return dtos;
		} */
	}
	

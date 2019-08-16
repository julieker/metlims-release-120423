////////////////////////////////////////////////////
// Mrc2AddingClientInfoReader.java
// Written by Jan Wigginton, Jun 6, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.sheetreaders.obsolete;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.util.SampleSheetIOException;
import edu.umich.brcf.shared.util.datacollectors.ClientDataInfo;
import edu.umich.brcf.shared.util.sheetreaders.ClientInfoReader;


public class Mrc2AddingClientInfoReader extends ClientInfoReader
	{
	@SpringBean
	ExperimentService experimentService;
	
	String sheetName = "Client Data";
	int sheetNum = 2, rowCount = 12;
	
	
	public Mrc2AddingClientInfoReader() 
		{ 
		Injector.get().inject(this);
		}
	
	
	public ClientDataInfo read(Sheet sheet) throws SampleSheetIOException
		{
		this.setOrgRow(2);
	    this.setOrgAddressRow(-1);
		this.setLabRow(3);
		this.setDeptRow(4);
		this.setContactNameRow(5);
		this.setContactEmailRow(6);
		this.setContactPhoneRow(7);
		this.setPiNameRow(8);
		this.setPiEmailRow(-1);
		this.setPiPhoneRow(-1);
		this.setDateRow(9);
		this.setProjNameRow(12);
		this.setProjDescriptionRow(13);
		
		this.setExpNameRow(10);
		this.setExpIdRow(11);
		this.setExpDescriptionRow(-1);
		
		this.setServiceRequestIdRow(16);
		this.setNihGrantNumberRow(15);
		this.setShortCodeRow(14);
		
		setReport(false);
		ClientDataInfo clientInfo = ((ClientDataInfo) super.read(sheet));
		
		String expId = clientInfo.getExperimentId();
		try
			{
			Integer nSamples = Integer.parseInt(experimentService.countSamples(expId));
			if (nSamples > 0) 
				{
				String msg = "Experiment already has samples.  Please use the Add Samples button at the bottom of the Sample Tools Panel page to append (additional) samples to an existing experiment.";
				throw new SampleSheetIOException(msg,  getExpIdRow(), "Client Data");
				}
			}
		catch (SampleSheetIOException e)
			{
			throw e;
			}
		catch (Exception e) { }
			
		return clientInfo;
		}
		
	}

	
	
	/*
	
	private ClientDataInfo readClientInfo(Workbook workbook) throws SampleSheetIOException
			{
			Sheet sheet = workbook.getSheet(sheetName); 
			    
			Row row=sheet.getRow(11);
		    String expId = row.getCell((short) 1).toString().trim();
		 
			if (StringUtils.isNullOrEmpty(expId))
				throw new SampleSheetIOException("Experiment ID is missing", rowCount, sheetName);
			
			 Experiment exp;
			 try { exp = experimentService.loadSimplestById(expId); }
			 catch(Exception ex)
			 	{
			 	String msg = "File upload failed: Experiment id + (" + expId + ") in client info sheet does not correspond to any known experiments";
				System.out.println(msg);
				throw new SampleSheetIOException(msg, rowCount, sheetName);
			    }
			     
			 readInitialClientInfo(sheet, row, exp);
			 
			 return expId;
			 }
		 
	 
		 private void readInitialClientInfo(Sheet sheet, Row row, Experiment exp) throws SampleSheetIOException
		 	 {
		 	 ShortcodeDTO scDto = new ShortcodeDTO();
		 	 
		 	 int rowCt = 14;
		 	 try 
	    		{ 
	    		row=sheet.getRow(rowCt++); 
	    		//	String code = row.getCell(1).toString().trim();
	    		Cell cell = row.getCell(1);   		
	    		cell.setCellType(Cell.CELL_TYPE_STRING);  		   
	  	    	String code = cell.toString().trim();  	    	 
	    		scDto.setCode(code);
		     
			    row=sheet.getRow(rowCt++); 
			    //String grantStr = row.getCell((short) 1).toString().trim();
			    
			    cell = row.getCell(1); 
			    cell.setCellType(Cell.CELL_TYPE_STRING);  	
			    String grantStr = cell.toString().trim();
				
			    if (StringUtils.isNullOrEmpty(grantStr) || "NO NIH GRANT".equals(grantStr.toUpperCase()))
			    	scDto.setNIH_GrantNumber("No NIH Grant");
			    else
			    	{ 
			    	String [] grants = parseGrantNumbers(grantStr);
			    
			    	if (grants != null)
				    	{
				    	if (grants.length > 0)
				    		{
				    		scDto.setNIH_GrantNumber(grants[0]);
				    		data.clientInfo.setNihGrantNumber(grants[0]);
				    		}
				    	
				    	if (grants.length > 1)
				    		{
				    		data.clientInfo.setNihGrantNumber2(grants[1]);
				    		scDto.setNIH_GrantNumber_2(grants[1]);
				    		}
		
				    	if (grants.length == 3)
				    		{
				    		data.clientInfo.setNihGrantNumber2(grants[2]);
				    		
				    		scDto.setNIH_GrantNumber_3(grants[2]);
				    		}
				    	
				    	if (grants.length > 3)
				    		{
				    		StringBuilder sb = new StringBuilder();
				    		
				    		for (int j = 2; j < grants.length; j++)
				    			sb.append(grants[j] + ", ");
				    		
				    		String remainingGrants = sb.toString();
				    		data.clientInfo.setNihGrantNumber2(remainingGrants);
				    		scDto.setNIH_GrantNumber_3(remainingGrants);
				    		}
				    	}
			    	}
			   	 
			    scDto.setExp(exp);
			    if((scDto.getCode()!=null) && (scDto.getCode().length()>0))
			    	experimentService.saveShortcode(scDto); 
			    else
			    	throw new SampleSheetIOException("Shortcode cannot be blank.  If no shortcode exists, please indicate this by filling the shortcode field with NA", 15, "Client Data");
	    		}
	    	
	    	catch (RuntimeException | SampleSheetIOException e) { throw new SampleSheetIOException(e.getMessage(), 15, "Client Data"); }
	    	catch (Exception e) { throw new SampleSheetIOException("Error while saving shortcode. please make sure shortcode has no more than 20 characters", 15, "Client Data"); }
		     	
		     String serviceRequest = "";
		     try
		    	 {	     
		    	 row=sheet.getRow(rowCt); 
		    	 serviceRequest = row.getCell((short) 1).toString().trim();
		    	 if(serviceRequest!=null && serviceRequest.length()>0)
		    		 experimentService.updateServiceRequestForExperiment(exp, serviceRequest);
		     	}
		     catch (Exception e) { throw new SampleSheetIOException("Error while saving service request " + serviceRequest, 16, "Client Data");}
		 	 }
		     
		 
	     String [] parseGrantNumbers(String grantStr)
		 	{
		 	if (StringUtils.isNullOrEmpty(grantStr)) 
		 		return null;
		 	
			String [] grants = StringUtils.splitAndTrim(grantStr, ";");
		 	if (grants.length > 1)
		 		return grants;
		 	
		 	return StringUtils.splitAndTrim(grantStr, ",");
		 	}
    */



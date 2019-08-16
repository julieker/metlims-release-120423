////////////////////////////////////////////////////
// StandardProtocolService.java
// Written by Jan Wigginton, Dec 3, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.layers.service;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.transaction.Transactional;

import edu.umich.brcf.shared.layers.dao.StandardProtocolDAO;
import edu.umich.brcf.shared.layers.domain.Assay;
import edu.umich.brcf.shared.layers.domain.ClientReport;
import edu.umich.brcf.shared.layers.domain.StandardProtocol;
import edu.umich.brcf.shared.layers.dto.ClientReportDTO;
import edu.umich.brcf.shared.layers.dto.StandardProtocolDTO;
import edu.umich.brcf.shared.util.FormatVerifier;



@Transactional
public class StandardProtocolService
	{
	StandardProtocolDAO standardProtocolDao;

	public List<StandardProtocol> loadForUploadDateRange(Calendar fromDate, Calendar toDate)
		{
		return standardProtocolDao.loadForUploadDateRange(fromDate, toDate);
		}
	
	public List<StandardProtocolDTO> loadInfoForUploadDateRange(Calendar fromDate, Calendar toDate)
		{
		return standardProtocolDao.loadInfoForRunDateRange(fromDate, toDate);
		}
	
	
	public StandardProtocol loadById(String id)
		{
		return standardProtocolDao.loadById(id);
		}
	
	
	public StandardProtocol loadLatestByAssayIdAndSampleType(String id, String sampleType)
		{
		return standardProtocolDao.loadLatestByAssayIdAndSampleType(id, sampleType);
		}
	
	// JAK issue 197
	public StandardProtocol loadLatestByAssayIdAndSampleTypeEFficiently(String id, String sampleType)
	    {
	    return standardProtocolDao.loadLatestByAssayIdAndSampleTypeEfficiently(id, sampleType);
	    }

	public StandardProtocol loadByProtocolId(String id)
		{
		return standardProtocolDao.loadByProtocolId(id);
		}
	
	public StandardProtocol loadSafeByProtocolId(String id)
		{
		return standardProtocolDao.loadySafeByProtocolId(id);
		}
	 	
	public List<StandardProtocol> loadAllBasics()
		{
		return standardProtocolDao.loadAllBasics();
		}
	
	
	public List<String>  getSampleTypesForStandardProtocolsByAssayId(String assayId)
		{
		if (!FormatVerifier.verifyFormat(Assay.fullIdFormat, assayId))
			return new ArrayList<String>();
		
		return standardProtocolDao.getSampleTypesForStandardProtocolsByAssayId(assayId);
		}
	
	
	public StandardProtocolDAO getStandardProtocolDao() 
		{
		return standardProtocolDao;
		}

	public void setStandardProtocolDao(StandardProtocolDAO standardProtocolDao)
		{
		this.standardProtocolDao = standardProtocolDao;
		}
		
	// Issue 192
    public static void writeToOutputStream( StandardProtocol protocolDoc , String wDir)
	    {
		// JAK issue 170 write file out to output stream into working directory
	    byte[] contents = protocolDoc.getContents();		
		FileOutputStream fileOuputStream = null;
	    try 
	       {		        	 
	       String fileName = protocolDoc.getFileName();
	       fileOuputStream = new FileOutputStream( wDir + fileName);
	       fileOuputStream.write(contents);	          
	       fileOuputStream.close();	       
	       } 
	    catch (IOException e) 
	       {
	       e.printStackTrace();	            
	       }
	    catch (Exception e) 
	       {
	       e.printStackTrace();	            
	       }		
		}
	}

///////////////////////////////////////////
// Writtten by Anu Janga
// Revisited by Jan Wigginton September 2016
///////////////////////////////////////////

package edu.umich.brcf.shared.layers.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
//import org.apache.commons.io.FileUtils;
import java.util.Map;

import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;


import org.apache.wicket.Session;
import org.apache.wicket.spring.injection.annot.SpringBean;

//// put back import org.docx4j.Docx4J;
//import org.docx4j.convert.out.FOSettings;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.metabolomics.panels.lipidshome.browse.Ms2DataSetReader;
import edu.umich.brcf.metabolomics.panels.lipidshome.browse.Ms2DataSetReader.FileType;
import edu.umich.brcf.shared.layers.dao.AssayDAO;
import edu.umich.brcf.shared.layers.dao.ClientDAO;
import edu.umich.brcf.shared.layers.dao.DocumentDAO;
import edu.umich.brcf.shared.layers.dao.ExperimentDAO;
import edu.umich.brcf.shared.layers.dao.ProjectDAO;
import edu.umich.brcf.shared.layers.dao.SampleDAO;
import edu.umich.brcf.shared.layers.domain.Assay;
import edu.umich.brcf.shared.layers.domain.ClientDocument;
import edu.umich.brcf.shared.layers.domain.ClientReport;
import edu.umich.brcf.shared.layers.domain.Document;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.ExperimentDocument;
import edu.umich.brcf.shared.layers.domain.ProjectDocument;
import edu.umich.brcf.shared.layers.domain.ProtocolReport;
import edu.umich.brcf.shared.layers.domain.StandardProtocol;
import edu.umich.brcf.shared.layers.dto.DocumentDTO;
import edu.umich.brcf.shared.layers.dto.StandardProtocolDTO;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.utilpackages.MimeTypeUtils;
import edu.umich.brcf.shared.util.utilpackages.FileUtils;


@Transactional
public class DocumentService 
	{
	DocumentDAO documentDao;
	SampleDAO sampleDao;
	ExperimentDAO experimentDao;
	ProjectDAO projectDao;
	ClientDAO clientDao;
	AssayDAO assayDao;
	
	// Issue 192
	@SpringBean
	StandardProtocolService standardProtocolService;
	
	public Document saveDocument(DocumentDTO dto){
		Assert.notNull(dto);
		Document doc = null;  
		char type= dto.getAccosiated().charAt(0);
		switch (type)
	         {
	         case 'E' :
	        	 doc = ExperimentDocument.instance(null,experimentDao.loadById(dto.getAccosiated()), dto.getFileContents(), dto.getFileName(),dto.getFileType());
	             break;
	         case 'P' :
	        	 doc = ProjectDocument.instance(null,projectDao.loadById(dto.getAccosiated()), dto.getFileContents(), dto.getFileName(),dto.getFileType());
	             break;
	         case 'C' :
	        	 if (dto.getAccosiated().startsWith("CL"))
	        		 doc = ClientDocument.instance(null,clientDao.loadById(dto.getAccosiated()), dto.getFileContents(), dto.getFileName(),dto.getFileType());
	        	 break;
	         default :
	        	throw new IllegalStateException(type + " is not mapped!");
	     	  }
		
		documentDao.createDocument(doc);
		
		return doc;
		}
	
	// Issue 245
	public List<ExperimentDocument> getAllDocsForExperimentList(Experiment exp)
	    {
	    return documentDao.getAllDocsForExperimentList(exp);	    
	    }
	
	// issue 441
	public Map<String, ExperimentDocument> loadSmallDocByByIdForExpIdNotDeleted(String expId) 
		{
		return documentDao.loadSmallDocByIdForExpIdNotDeleted(expId);
		}
	
	public Map <String, String> loadIdNameMapByExpIdNotDeleted(String expId) 
		{
		return documentDao.loadIdNameMapByExpIdNotDeleted(expId);
		}
	
	public void deleteDocument(String docId)
		{
		documentDao.deleteDocument(docId);
		}
	
	public Document loadById(String documentId)
		{
		return documentDao.loadById(documentId);
		}
	
	public ClientDocument loadClientDocByName(String name)
		{
		return documentDao.loadClientDocByName(name);
		}
	
	// Issue 192
	public byte [] convertToPDFPoi(StandardProtocol standardProtocol) 
        {	
        String docPath = System.getProperty("catalina.base") + "/work/" + standardProtocol.getFileName();
        String pdfPath = System.getProperty("catalina.base") + "/work/" + standardProtocol.getFileName().replace(".docx",  ".pdf");
	    try 
            {	
		    File file = new File(docPath);			  
		    standardProtocolService.writeToOutputStream(  standardProtocol, System.getProperty("catalina.base") + "/work/" );		    
            InputStream doc = new FileInputStream(new File(docPath));	            
            XWPFDocument document = new XWPFDocument(doc);
            PdfOptions options = PdfOptions.create(); 
            File pdfFile = new File(pdfPath);
            File docFile = new File(docPath);
            OutputStream out = new FileOutputStream(new File(pdfPath));
            PdfConverter.getInstance().convert(document, out, options);	
            byte [] bytesFrompdf = FileUtils.getBytes(pdfPath);
            if (new File(docPath).isFile())
        	    docFile.delete();
            if (new File(pdfPath).isFile())
        	    pdfFile.delete();	         
            return bytesFrompdf;	            
            } 
        catch (Exception e) 
            {
            e.printStackTrace();
            return null;
            }
        }
	 	
	// Issue 192
	public StandardProtocol saveStandardProtocol(StandardProtocolDTO dto)
        {
		Assert.notNull(dto);
		// issue 192
		if (dto.getFileName().indexOf(".docx") > -1 &&  dto.getFileType().indexOf("document") >-1)
		    {
			StandardProtocol protocol = StandardProtocol.instance(assayDao.loadAssayByID(dto.getAssayId()), dto.getSampleType(), 
					((MedWorksSession) Session.get()).getCurrentUserId(), dto.getStartDate(), dto.getRetiredDate(),  
				     dto.getFileContents(), dto.getFileName(),  dto.getFileType() );	
			documentDao.createStandardProtocol(protocol);			
			try 
			    {
				byte[] convertedBytes = convertToPDFPoi(protocol);
				dto = writePDFConversionToBlobBytes(convertedBytes, dto);
			    } 
			catch (Exception e) 
			    {
				// TODO Auto-generated catch block
				e.printStackTrace();
			    }	
			dto.setFileName(dto.getFileName().replace(".docx", ".pdf"));
			dto.setFileType("application/pdf");	
			Calendar calAdd = Calendar.getInstance();	
			Date date = new Date();
			calAdd.set(Calendar.SECOND, date.getSeconds() + 1);
			//calAdd.add(Calendar.SECOND, 1);
			StandardProtocol protocolPdf = StandardProtocol.instance(assayDao.loadAssayByID(dto.getAssayId()), dto.getSampleType(), 
					((MedWorksSession) Session.get()).getCurrentUserId(), calAdd, dto.getRetiredDate(),  
				    dto.getFileContents(), dto.getFileName(),  dto.getFileType() );
			documentDao.createStandardProtocol(protocolPdf);
			return protocolPdf;
		    }
		else
		    {
		    StandardProtocol protocol = StandardProtocol.instance(assayDao.loadAssayByID(dto.getAssayId()), dto.getSampleType(), 
			        ((MedWorksSession) Session.get()).getCurrentUserId(), dto.getStartDate(), dto.getRetiredDate(),  
		            dto.getFileContents(), dto.getFileName(),  dto.getFileType() );	
		    documentDao.createStandardProtocol(protocol);	
		    return protocol;
		    }		
		}
	 
	// Issue 192
    public StandardProtocolDTO writePDFConversionToBlobBytes( byte[] bytesOfDocument, StandardProtocolDTO dto)
        {
	    dto.setFileContents(bytesOfDocument);
	    return dto;
	    } 
	
	public ClientReport saveClientReport(DocumentDTO dto)
		{
		Assert.notNull(dto);
		
		ClientReport cr = ClientReport.instance(experimentDao.loadById(dto.getAccosiated()), 
		((MedWorksSession) Session.get()).getCurrentUserId(), dto.getFileContents(),  dto.getFileName(), dto.getFileType(), dto.getAssociatedAssay());		
		documentDao.createClientReport(cr);
		return cr;
		}
		
	public ProtocolReport saveProtocolReport(DocumentDTO dto)
		{
		Assert.notNull(dto);		
		ProtocolReport cr = ProtocolReport.instance(experimentDao.loadById(dto.getAccosiated()), 
		((MedWorksSession) Session.get()).getCurrentUserId(), dto.getFileContents(),  dto.getFileName(), dto.getFileType(), dto.getAssociatedAssay());		
		documentDao.createProtocolReport(cr);
		return cr;
		}
	
	public String getNiceDocumentName(ExperimentDocument doc)
		{
		String name = doc.getFileName();
		String fileType = doc.getFileType();
		return getNiceName(name, fileType);	
		}
	
	public String getNiceReportName(ClientReport rep)
		{
		String name = rep.getFileName();
		String fileType = rep.getFileType();
		return getNiceName(name, fileType);
		}
		
	public String getNiceReportName(ProtocolReport rep)
		{
		String name = rep.getFileName();
		String fileType = rep.getFileType();
		return getNiceName(name, fileType);
		}
	
	
	private String getNiceName(String name, String fileType)
		{
		String [] tokens = name.split("\\.");
		if (tokens.length > 1) return name;		
		String extension = MimeTypeUtils.getExtensionForMimeType(fileType);
		if (StringUtils.isNonEmpty(extension))
			name += "." + extension;		
		return name;
		}
	
	public String getPrettyReportName(ClientReport rep)
		{
		StringBuilder builder = new StringBuilder();
		String assayId = rep.getAssayId();
		Assay assay = assayId == null ?  null : assayDao.loadAssayByID(assayId);
		String assayName = assay == null ? "" : assay.getAssayName();
		
		if (!(rep.getExp() == null))
			builder.append(rep.getExp().getExpID() + "_");
		
		if (!assayName.equals(""))
			builder.append(assayName + "_");
		
		String dateCreatedStr = rep.getDateCreatedStr();
		if (StringUtils.isNonEmpty(dateCreatedStr))
			builder.append(dateCreatedStr);
		
		String extension = MimeTypeUtils.getExtensionForMimeType(rep.getFileType());
		if (StringUtils.isNonEmpty(extension))
			builder.append("." + extension);
		
		String prettyName = builder.toString();
		if (prettyName.length() < 12)
			return rep.getFileName();
		
		return prettyName;
		}
	
	
	public void setDocumentDao(DocumentDAO documentDao) 
		{
		this.documentDao = documentDao;
		}
	
	public void setSampleDao(SampleDAO sampleDao) 
		{
		this.sampleDao = sampleDao;
		}

	public void setExperimentDao(ExperimentDAO experimentDao) 
		{
		this.experimentDao = experimentDao;
		}

	public void setProjectDao(ProjectDAO projectDao) 
		{
		this.projectDao = projectDao;
		}

	public void setClientDao(ClientDAO clientDao) 
		{
		this.clientDao = clientDao;
		}

	public DocumentDAO getDocumentDao()
		{
		return this.documentDao;
		}
	
	public AssayDAO getAssayDao()
		{
		return assayDao;
		}

	public void setAssayDao(AssayDAO assayDao) 
		{
		this.assayDao = assayDao;
		}
	}

package edu.umich.brcf.shared.layers.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import edu.umich.brcf.metabolomics.layers.domain.GeneratedWorklistItem;
import edu.umich.brcf.shared.layers.domain.ClientDocument;
import edu.umich.brcf.shared.layers.domain.ClientReport;
import edu.umich.brcf.shared.layers.domain.Document;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.ExperimentDocument;
import edu.umich.brcf.shared.layers.domain.ProtocolReport;
import edu.umich.brcf.shared.layers.domain.StandardProtocol;
import edu.umich.brcf.shared.layers.domain.User;
import edu.umich.brcf.shared.layers.dto.UserDTO;
import edu.umich.brcf.shared.util.FieldLengths;
import edu.umich.brcf.shared.util.utilpackages.FileUtils;



@Repository
public class DocumentDAO extends BaseDAO{
	
	public void createDocument(Document doc)
		{
		getEntityManager().persist(doc);
		}
	
	// Issue 245
	public List<ExperimentDocument> getAllDocsForExperimentList(Experiment exp)
        {
        List<ExperimentDocument> documentList = getEntityManager().createQuery("from ExperimentDocument d where (d.deletedFlag is null or d.deletedFlag = false) and  associated = ?1  order by d.fileName").setParameter(1, exp).getResultList();
        return documentList;
        }
	
	public Document loadById(String documentId)
		{
		Document doc = getEntityManager().find(Document.class, documentId);
		return doc;
		}
	
	
	// issue 441
	public Map<String, ExperimentDocument> loadSmallDocByIdForExpIdNotDeleted(String expId) 
	    {
		String queryStr =  "from ExperimentDocument d where d.deletedFlag is null and d.associated.expID = ?1 and dbms_lob.getlength(contents) < " + FieldLengths.SMALL_DOCUMENT_LIMIT;
	    List<ExperimentDocument> docs =  getEntityManager().createQuery(queryStr).setParameter(1, expId).getResultList();	    
	    Map<String, ExperimentDocument> smallDocMap = new HashMap<String, ExperimentDocument>();
	    if (docs != null)
	    	for (ExperimentDocument doc : docs)
	    		smallDocMap.put(doc.getDocumentId(), doc);	    	
	    return smallDocMap;		
	    }
			
	// issue 441
	public Map <String, String> loadIdNameMapByExpIdNotDeleted(String expId) 
	    {
		Query query = getEntityManager().createNativeQuery("select cast(document_id as VARCHAR2(10)), "
				+ "cast(file_name as VARCHAR2(150)), cast(file_type as VARCHAR2(80)), dbms_lob.getlength(contents) from document where discriminator = 'EXPERIMENT' and deleted is null and  associated = ?1");	    
	    List<Object[]> resultList  = query.setParameter(1, expId).getResultList();		
	    Map<String, String> idNameMap = new HashMap<String, String>();		
	    if (resultList != null)
		    for (Object [] obj : resultList)
				{		
				String doc_id = "", filename = "", filetype;
				Double size;				
				if (obj.length < 4)
					continue;							
				doc_id = (String) obj[0];				
				filename = (String) obj[1];
				filetype = (String) obj[2];
				BigDecimal sz = (BigDecimal) obj[3];
				String inM = " ( " + sz + " bytes )"; 				
				try {
					String tag = "M";
					Double inMDbl = sz.doubleValue()/ (1024.0 * 1024.0);
					if (inMDbl < 1.0)
						{
						inMDbl *= 1024.0;
						tag = "K";
						}
					inM = "    ( Size : " + String.format("%.1f", inMDbl) + tag +" )";
					}
				catch (Exception e)  { System.out.println("Error while parsing size"); }				
				idNameMap.put(doc_id, FileUtils.getNiceName(filename, filetype, inM));
				}		
	    return idNameMap;
	    }
	
	
	
	
	public void deleteDocument(String docId)
		{
		Document doc = getEntityManager().find(Document.class, docId);
		doc.setDeleted();
		}

	public ClientDocument loadClientDocByName(String name) 
		{
		List<ClientDocument> lst =  getEntityManager().createQuery("from ClientDocument d where trim(d.fileName) = :name")
				.setParameter("name", name.trim()).getResultList();
		
		ClientDocument doc;
		try { doc = (ClientDocument)DataAccessUtils.requiredSingleResult(lst); }
		catch(Exception e){ doc=null; }
		
		return doc;
		}

	public void createClientReport(ClientReport cr) 
		{
		getEntityManager().persist(cr);
		}
	
	public void createStandardProtocol(StandardProtocol protocol) 
		{
		getEntityManager().persist(protocol);
		}
	
	
	public void createProtocolReport(ProtocolReport pr) 
		{
		getEntityManager().persist(pr);
		}
	}

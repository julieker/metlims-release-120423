package edu.umich.brcf.shared.layers.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import edu.umich.brcf.shared.layers.domain.BarcodePrinters;



@Repository
public class BarcodePrintingDAO extends BaseDAO{

	public List<BarcodePrinters> allPrinters() 
		{
		List<BarcodePrinters> list = getEntityManager().createQuery("from BarcodePrinters b order by b.printerId").getResultList();
		return list;
		}
	
	
	public List<String> getPrinterNames() 
		{
		Query query = getEntityManager().createQuery("select b.printerName from BarcodePrinters b order by 1");
		List<String> pNames = query.getResultList();
		return pNames;
		}
	
	
	public BarcodePrinters loadPrinterByName(String printerName)
		{
		List<BarcodePrinters> printerList = getEntityManager().createQuery("from BarcodePrinters b where b.printerName = :printerName")
			.setParameter("printerName", printerName).getResultList();
		
		BarcodePrinters printer = (BarcodePrinters) DataAccessUtils.requiredSingleResult(printerList);
		return printer;
		}
	}

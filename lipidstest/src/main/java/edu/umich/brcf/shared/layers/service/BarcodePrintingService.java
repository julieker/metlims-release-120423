package edu.umich.brcf.shared.layers.service;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import edu.umich.brcf.shared.layers.dao.BarcodePrintingDAO;
import edu.umich.brcf.shared.layers.dao.SystemConfigDAO;
import edu.umich.brcf.shared.layers.domain.BarcodePrinters;
import edu.umich.brcf.shared.util.utilpackages.BarcodePrinterUtil;



@Transactional
public class BarcodePrintingService 
	{
	SystemConfigDAO systemConfigDao;
	BarcodePrintingDAO barcodePrintingDao;

	public SystemConfigDAO getSystemConfigDao() 
		{
		return systemConfigDao;
		}

	public void setSystemConfigDao(SystemConfigDAO systemConfigDao) 
		{
		this.systemConfigDao = systemConfigDao;
		}

	public List<BarcodePrinters> getPrinterList() 
		{
		return systemConfigDao.getBarcodePrinterList();
		}
	
	public List<BarcodePrinters> getBarcodePrinters() 
		{
		return barcodePrintingDao.allPrinters();
		}
	
	public List<String> getPrinterNamesWithDownloader() 
		{
		List<String> allPrinters =  barcodePrintingDao.getPrinterNames();
		allPrinters.add("Spreadsheet Writer");
		return allPrinters;
		}
	
	// Issue 205
	public List<String> getPrinterNames() 
		{
		return getPrinterNames(false);
		}
	
	// Issue 205
	public List<String> getPrinterNames(boolean withNone) 
	    {       
        if (withNone)
            {
        	List<String> barcodeListWithNone = barcodePrintingDao.getPrinterNames();
            barcodeListWithNone.add("None");
        	return barcodeListWithNone;      	
            }
		else 
			return barcodePrintingDao.getPrinterNames();		
	    }
	

	public BarcodePrinters loadPrinterByName(String printerName)
		{
		return barcodePrintingDao.loadPrinterByName(printerName);
		}
	
	public void printOnePlateBarcode(String barcode) throws Exception 
		{
		BarcodePrinterUtil  printer = systemConfigDao.getPlateBarcodePrinter();
		Socket printerSocket = new Socket(printer.getAddress(), 9100);
		DataOutputStream dataToPrinter = new DataOutputStream(printerSocket.getOutputStream());
		String format = String.format(printer.getPrintString() + printer.getEndString(), 1, barcode);
		dataToPrinter.writeBytes(format);
		printerSocket.close();
		}

	public void setBarcodePrintingDao(BarcodePrintingDAO barcodePrintingDao) 
		{
		this.barcodePrintingDao = barcodePrintingDao;
		}
	}

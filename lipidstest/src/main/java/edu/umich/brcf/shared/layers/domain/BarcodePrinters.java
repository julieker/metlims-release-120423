package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "BARCODEPRINTERS")
public class BarcodePrinters implements Serializable
	{
	public static BarcodePrinters instance(String id) 
		{
		return new BarcodePrinters(id, null, null, null);
		}	
	
	@Id()
	@Column(name = "PRINTER_ID", unique = true, nullable = false, length = 4, columnDefinition = "CHAR(4)")
	private String printerId;

	@Basic()
	@Column(name = "PRINTER_NAME", length = 50, columnDefinition = "VARCHAR2(50)")
	private String printerName;

	@Basic()
	@Column(name = "PRINTER_ADDRESS", length = 16, columnDefinition = "VARCHAR2(16)")
	private String printerAddress;

	@Basic()
	@Column(name = "PRINTER_LOCATION", length = 25, columnDefinition = "VARCHAR2(25)")
	private String printerLocation;
	

	public BarcodePrinters() { }

	
	private BarcodePrinters(String printerId, String printerName, String printerAddress, String printerLocation) 
		{
		this.printerId = printerId;
		this.printerName = printerName;
		this.printerAddress = printerAddress;
		this.printerLocation = printerLocation;
		}

	
	public String getPrinterId() 
		{
		return printerId;
		}

	public void setPrinterId(String printerId) 
		{
		this.printerId = printerId;
		}

	public String getPrinterName() 
		{
		return printerName;
		}

	public void setPrinterName(String printerName) 
		{
		this.printerName = printerName;
		}

	public String getPrinterAddress() 
		{
		return printerAddress;
		}
	
	public String toString()
		{
		return printerName;
		}

	public void setPrinterAddress(String printerAddress) 
		{
		this.printerAddress = printerAddress;
		}

	public String getPrinterLocation() 
		{
		return printerLocation;
		}

	public void setPrinterLocation(String printerLocation) 
		{
		this.printerLocation = printerLocation;
		}
	}

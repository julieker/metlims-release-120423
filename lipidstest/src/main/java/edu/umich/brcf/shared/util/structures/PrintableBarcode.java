package edu.umich.brcf.shared.util.structures;


import java.io.DataOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.spring.injection.annot.SpringBean;
import edu.umich.brcf.shared.layers.service.BarcodePrintingService;
import edu.umich.brcf.shared.util.structures.ValueLabelBean;

public class PrintableBarcode implements Serializable
	{
	@SpringBean
	BarcodePrintingService barcodePrintingService;
	
	private String printerName;
	private List<ValueLabelBean> barcodesList;
	private String printerIP;
	
	public  PrintableBarcode(BarcodePrintingService barcodePrintingService, String printerName, List<ValueLabelBean> barcodesList){
		this.printerName = printerName;
		this.barcodesList = barcodesList;
		setPrinterIP(barcodePrintingService.loadPrinterByName(printerName).getPrinterAddress());
		}

	public String getPrinterName() {
		return printerName;
	}
	public void setPrinterName(String printerName) {
		this.printerName = printerName;
	} 

	public List<ValueLabelBean> getBarcodesList() {
		return barcodesList;
	}

	public void setBarcodesList(List<ValueLabelBean> barcodesList) {
		this.barcodesList = barcodesList;
	}
	
	public String getPrinterIP() {
		return printerIP;
	}

	public void setPrinterIP(String printerIP) {
		this.printerIP = printerIP;
	}

	// issue 84
	public void printBarcodes(String id, boolean isAliquot)
		{
		barcodesList = new ArrayList<ValueLabelBean>();
		barcodesList.add(new ValueLabelBean(id, null));
		String errMsg=print(isAliquot);
		if (errMsg.length()>0)
			throw new RuntimeException(errMsg);
		}	
	// issue 84
	public String print ()
		{
		return print(false);
		}
		
	public String print(boolean isAliquotPrinting )
	    {
		String errMsg="";
		try	
			{
			if (printerName!=null)
			    {
				Socket printerSocket=new Socket(getPrinterIP(),9100);//bundle.getString(printerName),9100);
				DataOutputStream outToPrinter  = new DataOutputStream(printerSocket.getOutputStream() );
				String addString1, addString2;
				  String s;
				   if (barcodesList.size()>0)
				       {
					   for (ValueLabelBean barcodeBean : barcodesList) 
						   {
						// JAK adjust for smaller labels
						// JAK issue 157 print correctly on both Zebra printers						
						// issue 84
						// issue 86
						   if (!isAliquotPrinting && barcodeBean.getValue().length() <= 9 )
						       {
                               s = "${^XA^LT5^LS-120^FO0,12^BY,5^BXN,4,200^FD" + barcodeBean.getValue().trim()  + "^FS" ;                                
                               s = s + "^FO0,70^A0N,38,25^FD" +  barcodeBean.getValue().trim()  +  "^XZ}$";  
						       }
						   else 
						   	   {
							   s = "${^XA^LT5^LS-120^FO0,12^BY,1^BXN,2,200^FD" + barcodeBean.getValue().trim()  + "^FS" ;                                
							   s = s + "^FO0,70^A0N,27,14^FD" +  barcodeBean.getValue().trim()  +  "^XZ}$";  
						   	   } 
						addString2=((barcodeBean.getLabel()==null)||(barcodeBean.getLabel().trim().length()==0))? "":"TEXT 0(0,0,1,2) 270 45 "+barcodeBean.getLabel()+'\n';
						addString1=((barcodeBean.getLabel()==null)||(barcodeBean.getLabel().trim().length()==0))? "TEXT 0(0,0,1,2) 175 90 "+barcodeBean.getValue().trim()+'\n':"TEXT 0(0,0,1,2) 270 5 "+barcodeBean.getValue().trim()+'\n';
					    if ((barcodeBean.getValue()!=null)&&(barcodeBean.getValue().trim().length()>0))
					    	{					    	
					  	    if ( printerName.toLowerCase().indexOf("zebra") > -1 )
					  	        outToPrinter .writeBytes(s);
					  	    else 
					  	    	outToPrinter .writeBytes("! 0 100 140 1" + '\n'+"BARCODE DATAMATRIX (,F,,,2,~) 175 10 "+'\n'+ "~"+barcodeBean.getValue().trim()+"~" +'\n'+ addString1+ addString2+"END" +'\n');					  	       
					    	}
						}
					}
			    printerSocket.close();
				}
			}
		catch(Exception e)
			{
			e.printStackTrace();
			errMsg="An error occured while trying to print barcodes, please check the printer connection and labels!";
			}		
		return errMsg;
		}

	public void setBarcodePrintingService(BarcodePrintingService barcodePrintingService) {
		this.barcodePrintingService = barcodePrintingService;
	    }
    }



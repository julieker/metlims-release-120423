package edu.umich.brcf.shared.panels.utilitypanels.discard;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.BarcodePrintingService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.structures.PrintableBarcode;
import edu.umich.brcf.shared.util.structures.ValueLabelBean;


public class PrintBarcodesPage extends WebPage 
	{
	@SpringBean
	BarcodePrintingService barcodePrintingService;
	
	@SpringBean
	SampleService sampleService;
	
	String printerName;
	
	
	public String getPrinterName() 
		{
		return printerName;
		}

	public void setPrinterName(String printerName) 
		{
		this.printerName = printerName;
		}

	public PrintBarcodesPage(Page page) 
		{
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new PrintBarcodesForm("printBarcodesForm"));
		}

	public final class PrintBarcodesForm extends Form {
		public PrintBarcodesForm(final String id){
			super(id);
			add(new TextField("noBarcodes", new Model("")));
			add( new DropDownChoice("printerName", new PropertyModel(PrintBarcodesPage.this, "printerName"),  barcodePrintingService.getPrinterNames() ).setRequired(true));
			
			add(new AjaxSubmitLink ("print", this)
				{
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form form) 
					{
					String noBarcodes = form.get("noBarcodes").getDefaultModelObjectAsString();
					String printerName= form.get("printerName").getDefaultModelObjectAsString();
//					BarcodePrinters bp = barcodePrintingService.loadPrinterByName(getPrinterName());
					int num = (noBarcodes.equals(null)||(noBarcodes.trim().length()==0))? 0: Integer.parseInt(noBarcodes);
					
					if (num>0)
					 	{
						List<ValueLabelBean> barcodesList = new ArrayList<ValueLabelBean>();
						for(int i=1;i<=num;i++)
							barcodesList.add(new ValueLabelBean(sampleService.getNextSampleID(), null));
						
						String errMsg=new PrintableBarcode(barcodePrintingService, printerName, barcodesList).print();//"BarcodeLabelPrinter1"
						
						if (errMsg.length()>0)
							PrintBarcodesPage.this.error(errMsg);
						 else
							PrintBarcodesPage.this.info(num + " barcode(s) printed on " + printerName);
					 	}
					else
						PrintBarcodesPage.this.error("Please enter the number of barcodes to be printed.");
					
					target.add(PrintBarcodesPage.this.get("feedback"));
					}

				@Override
				protected void onError(AjaxRequestTarget arg0) { }
				});
			}
		}
	}

////////////////////////////////////////////////////
// PrintBarcodesOldPage.java
// Written by Jan Wigginton, Jun 19, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.panels.utilitypanels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.RangeValidator;

import edu.umich.brcf.shared.layers.service.BarcodePrintingService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.structures.PrintableBarcode;
import edu.umich.brcf.shared.util.structures.ValueLabelBean;


public class PrintBarcodesOldPage extends WebPage 
	{
	@SpringBean
	BarcodePrintingService barcodePrintingService;
	int maxInt = 7000;
	
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

	public PrintBarcodesOldPage(Page page) 
		{
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new PrintBarcodesForm("printBarcodesForm"));
		}

	public final class PrintBarcodesForm extends Form {
		
		//Issue 211
		private String toSampleId, fromSampleId, noBarcodes;
		public PrintBarcodesForm(final String id){
			super(id);
			
			// Issue 205
			final RequiredTextField noBarCodesTxt;
			add(noBarCodesTxt = new RequiredTextField("noBarcodes", new PropertyModel<String>(this, "noBarcodes")));
			noBarCodesTxt.setType(Integer.class).setLabel(new Model("Number of Barcodes")).add(new RangeValidator<>(1, maxInt));
	
			// Issue 205
			fromSampleId = sampleService.getNextSampleID(false, 1);
			final TextField fromSampleTxt = new TextField("fromSample", new PropertyModel<String>(this, "fromSampleId") );
			final TextField toSampleTxt =   new TextField("toSample", new PropertyModel<String>(this, "toSampleId") );
			fromSampleTxt.setOutputMarkupId(true);
			toSampleTxt.setOutputMarkupId(true);
			add(fromSampleTxt.setEnabled(false) );	
			add (toSampleTxt.setEnabled(false));
			
			// Issue 205
			add( new DropDownChoice("printerName", new PropertyModel(PrintBarcodesOldPage.this, "printerName"),  barcodePrintingService.getPrinterNames()).setRequired(true).setLabel(new Model("Printer")));		   			
			noBarCodesTxt.add(new AjaxFormComponentUpdatingBehavior("change")
        	    {
			    @Override
                protected void onUpdate(AjaxRequestTarget target)
			        {
			    	// Issue 211		    		
			    	toSampleId = sampleService.getNextSampleID(false, Integer.parseInt(noBarcodes));
					target.add(toSampleTxt);		    	
            	    }
			    @Override
				protected void onError(AjaxRequestTarget target, RuntimeException e)
				    {
					target.add(PrintBarcodesOldPage.this.get("feedback"));
				    }
        	    });
			
			// Issue 205			
			final AjaxSubmitLink printBtn = new AjaxSubmitLink ("print", this)
				{
				@Override
				protected void onSubmit(AjaxRequestTarget target) // issue 464
					{					
					// Issue 211		  	
				     List<ValueLabelBean> barcodesList = new ArrayList<ValueLabelBean>();
					for(int i=1;i<=Integer.parseInt(noBarcodes);i++)
						barcodesList.add(new ValueLabelBean(sampleService.getNextSampleID(false, i), null));						
					String errMsg=new PrintableBarcode(barcodePrintingService, printerName, barcodesList).print();//"BarcodeLabelPrinter1"
					// Issue 213
					if (errMsg.length()>0)
					    PrintBarcodesOldPage.this.error(errMsg);
					else
					    {
						sampleService.getNextSampleID(true, Integer.parseInt(noBarcodes));
						PrintBarcodesOldPage.this.info(noBarcodes + " barcode(s) :" +  fromSampleId  + " to "  + toSampleId  + "  printed on " + printerName);
						fromSampleId = sampleService.getNextSampleID(false,1);
						toSampleId = sampleService.getNextSampleID(false,Integer.parseInt(noBarcodes));				
						target.add(fromSampleTxt);
						target.add(toSampleTxt);							
						}
					target.add(PrintBarcodesOldPage.this.get("feedback"));
					}

				 @Override
				 protected void onError(AjaxRequestTarget arg0)
				    {
					// Issue 212
					arg0.add(PrintBarcodesOldPage.this.get("feedback"));
					}
					@Override // issue 464
					public MarkupContainer setDefaultModel(IModel model) 
					    {
						// TODO Auto-generated method stub
						return this;
					    }
				};
			add(printBtn);		   
			}
		    public String getToSampleId() { return toSampleId; }
	        public void setToSampleId(String toSampleId) { this.toSampleId = toSampleId; }
	        public String getFromSampleId() { return fromSampleId; }
	        public void setFromSampleId(String fromSampleId) { this.fromSampleId = fromSampleId; }
	        public String getNoBarcodes() { return noBarcodes; }
	        public void setNoBarcodes(String noBarcodes) { this.noBarcodes = noBarcodes; }
		}
	}

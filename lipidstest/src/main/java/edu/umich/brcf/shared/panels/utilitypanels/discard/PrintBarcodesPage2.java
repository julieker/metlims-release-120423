package edu.umich.brcf.shared.panels.utilitypanels.discard;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.BarcodePrintingService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.sheetwriters.BarcodeSheetWriter;
import edu.umich.brcf.shared.util.structures.PrintableBarcode;
import edu.umich.brcf.shared.util.structures.ValueLabelBean;
import edu.umich.brcf.shared.util.utilpackages.ListUtils;
import edu.umich.brcf.shared.util.widgets.AjaxBackButton;
import edu.umich.brcf.shared.util.widgets.ExcelDownloadLink;




public class PrintBarcodesPage2 extends WebPage 
	{
	@SpringBean
	BarcodePrintingService barcodePrintingService;
	
	@SpringBean
	SampleService sampleService;
	
	Page backPage;
	
	public PrintBarcodesPage2(Page page) 
		{
		backPage = page;
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new PrintBarcodesForm("printBarcodesForm", null));
		}
	
	
	public PrintBarcodesPage2(Page page, List<String> barcodeIds) 
		{
		backPage = page;
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new PrintBarcodesForm("printBarcodesForm", barcodeIds));
		}
	

	public final class PrintBarcodesForm extends Form 
		{
		List<String> idsToPrint;
		String idListString;
		
		String printerName = "Zebra Printer";
		Integer nCopies = 1;
		AjaxSubmitLink submitLink = null;
		ExcelDownloadLink downloadLink;
		
		List<Integer> countOptions = Arrays.asList(new Integer [] { 1, 2, 3, 4, 5} );
		
		
		public PrintBarcodesForm(final String id, final List<String> ids)
			{
			super(id);
			idsToPrint = ids;
			
			add(new Label("count", new Model<String>("# Copies :"))
				{
				public boolean isVisible() {return (idsToPrint != null); }
				});
			
			add(new Label("toprint", "Ids to Print :")
				{
				public boolean isVisible() { return (!(idsToPrint == null)); }
				});
			
			add(new DropDownChoice<Integer>("noBarcodes", new PropertyModel<Integer>(this, "nCopies"), countOptions));
			
			TextArea<String> samplesBox = new TextArea<String>("samples", new PropertyModel<String>(this, "idListString"))
				{
				@Override
				public boolean isVisible() { return (!(idsToPrint == null)); }
				};
			add(samplesBox);
			samplesBox.setOutputMarkupId(true);
			
			add(new AjaxBackButton("backButton", (WebPage) backPage));
			
			
			add(submitLink = new AjaxSubmitLink ("print", this)
				{
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form form) 
					{
					String noBarcodes = form.get("noBarcodes").getDefaultModelObjectAsString();
					String printerName= form.get("printerName").getDefaultModelObjectAsString();
					
					int num;
					if (idsToPrint == null) 
						num = (noBarcodes.equals(null)||(noBarcodes.trim().length()==0))? 0: Integer.parseInt(noBarcodes);
					else
						num = idsToPrint.size();
					
					if (num>0)
					 	{
						List<ValueLabelBean> barcodesList = new ArrayList<ValueLabelBean>();
					
						for (int j = 0; j < nCopies; j++)
							for(int i=0; i < num; i++)
								barcodesList.add(new ValueLabelBean(idsToPrint.get(i), null));
						
						String errMsg=new PrintableBarcode(barcodePrintingService, printerName, barcodesList).print();//"BarcodeLabelPrinter1"
						
						if (errMsg.length()>0)
							PrintBarcodesPage2.this.error(errMsg);
						 else
							PrintBarcodesPage2.this.info(num + " barcode(s) printed on " + printerName);
					 	}
					else
						PrintBarcodesPage2.this.error("Please enter the number of barcodes to be printed.");
					
					target.add(PrintBarcodesPage2.this.get("feedback"));
					}
				
				@Override
				public boolean isVisible() {  return true; } //!StringUtils.isEmptyOrNull(printerName) && !"Spreadsheet Writer".equals(printerName);  }
				});
			
			submitLink.setOutputMarkupId(true);
			
			DropDownChoice<String> printerDrp = null;
			add(printerDrp = new DropDownChoice<String>("printerName", new PropertyModel(this, "printerName"), 
					barcodePrintingService.getPrinterNames()));
			
			printerDrp.setRequired(true);
			printerDrp.setOutputMarkupId(true);
			
			add(downloadLink = buildDownloadSheetButton("downloadButton", idsToPrint));
			downloadLink.setOutputMarkupId(true);
			//printerDrp.add(buildStandardFormComponentUpdateBehavior("change", "drop", downloadLink));
			}

		
		private ExcelDownloadLink buildDownloadSheetButton(String id,  List<String> barcodesToPrint)
			{
			BarcodeSheetWriter writer = new BarcodeSheetWriter("", barcodesToPrint);
			
			return new ExcelDownloadLink(id, writer)
				{
				@Override
				public boolean isVisible()  { return true; } 
				};
			}
		
		
		public Integer getnCopies()
			{
			return nCopies;
			}


		public void setnCopies(Integer nCopies)
			{
			this.nCopies = nCopies;
			}


		public String getIdListString()
			{
			idListString = idsToPrint == null ? "" : ListUtils.bulletPrint(idsToPrint, "");
			return idListString;
			}

		
		public void setIdListString(String idListString)
			{
			this.idListString = idListString;
			}
		
		public String getPrinterName() 
			{
			return printerName;
			}

		
		public void setPrinterName(String printerName) 
			{
			this.printerName = printerName;
			}
		
	
		/*
		private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(final String event, final 
				String tag, final Component object)
				{
				return new AjaxFormComponentUpdatingBehavior(event)
					{
					@Override
					protected void onUpdate(AjaxRequestTarget target) 
						{ 
						target.add(downloadLink);  target.add(submitLink);} 
					};
				} */
		}
	}

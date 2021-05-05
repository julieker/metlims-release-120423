////////////////////////////////////////////////////
//PrintBarcodesPage.java
//Written by Jan Wigginton, January 2017
////////////////////////////////////////////////////

package edu.umich.brcf.shared.panels.utilitypanels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import edu.umich.brcf.shared.layers.service.BarcodePrintingService;
import edu.umich.brcf.shared.util.interfaces.IWriteableSpreadsheet;
import edu.umich.brcf.shared.util.sheetwriters.BarcodeSheetWriter;
import edu.umich.brcf.shared.util.structures.PrintableBarcode;
import edu.umich.brcf.shared.util.structures.ValueLabelBean;
import edu.umich.brcf.shared.util.utilpackages.ListUtils;
import edu.umich.brcf.shared.util.widgets.ExcelDownloadLink;

public class PrintBarcodesPage extends WebPage 
	{
	@SpringBean
	BarcodePrintingService barcodePrintingService;

	Page backPage;
	ModalWindow modal;
	String selectedExperiment = null;
	
	public PrintBarcodesPage(Page page) 
		{
		// issue 120
		backPage = page;
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new PrintBarcodesForm("printBarcodesForm", null, null));
		}
		
	public PrintBarcodesPage(Page page, List<String> barcodeIds) 
		{
		this(page, barcodeIds, (String) null);
		}
	
	public PrintBarcodesPage(Page page, List<String> barcodeIds, String selectedExperiment) 
		{		
		this(page, barcodeIds, (ModalWindow) null, selectedExperiment);
		}
	
	public PrintBarcodesPage(Page page, List<String> barcodeIds, ModalWindow m, String selectedExperiment) 
		{
		backPage = page;
		modal = m;
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new PrintBarcodesForm("printBarcodesForm", barcodeIds, selectedExperiment));		
		}
	
	public final class PrintBarcodesForm extends Form 
		{
		private List<String> idsToPrint;
		private List<String> allIds; 
		private String idListString;
	
		private Boolean useAliquotTags = false;
		private String printerName = "Zebra Printer";
		private Integer nCopies = 1, firstAliquotExtension = 1;
		
		AjaxSubmitLink submitLink = null;
		ExcelDownloadLink downloadLink;
		final WebMarkupContainer container;
		
		List<Integer> countOptions = Arrays.asList(new Integer [] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15} );
		
		public PrintBarcodesForm(final String id, final List<String> ids, String s)
			{
			super(id);			
			idsToPrint = ids;	
			container = new WebMarkupContainer("container");
			container.setOutputMarkupId(true);
			selectedExperiment = s;
			DropDownChoice<String> printerDrp = null;
			add(printerDrp = new DropDownChoice<String>("printerName", new PropertyModel(this, "printerName"), 
					barcodePrintingService.getPrinterNames()));			
			printerDrp.setRequired(true);
			printerDrp.setOutputMarkupId(true);
			add(new Label("count", new Model<String>("# Copies :"))
				{
				public boolean isVisible() {return (idsToPrint != null); }
				public boolean isEnabled() { return false; }
				});
			
			add(buildCountDropdown("noBarcodes", "nCopies"));
			add(buildCountDropdown("startAtAliquot", "firstAliquotExtension"));
			
			add(new Label("aliquotTagLabel", new Model<String>("Tag as aliquot(s) - starting at id :"))
				{
				public boolean isVisible() { return (idsToPrint != null); }
				public boolean isEnabled() { return false; }
				});
					
			add(new AjaxCheckBox("tagAsAliquots", new PropertyModel<Boolean>(this, "useAliquotTags"))
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target) 
					{ 
					target.add(container);
					}
				
				@Override
				public boolean isVisible() { return (idsToPrint != null);  }
				
				@Override
				public boolean isEnabled() { return false; }
				}); 
			
	         add(new Label("toprint", "Ids to print : ")
				{
				public boolean isVisible() { return (!(idsToPrint == null)); }
				});// issue 120
				
				MultiLineLabel samplesBox = new MultiLineLabel ("samples",new PropertyModel<String>(this, "idListString"))
	            {
				@Override
				public boolean isVisible() { return (!(idsToPrint == null)); }
				};
			samplesBox.setEscapeModelStrings(false);	
			container.add(samplesBox);
			samplesBox.setOutputMarkupId(true);
				 // issue 120			
			add(container);		
			// Issue 464			
			if (modal != null) 
			    {
				 AjaxButton cancelButton = 
					 new AjaxButton("backButton")
			             {
			        	 // Issue 231
						 @Override		 
						 public void onSubmit(AjaxRequestTarget target) 
						    {
							 try
							     {				     
								 modal.close(target);
							     }
							 catch (Exception e)
							     {
							     e.printStackTrace();	
							     }							
							 };			        		 
			             };
					add (cancelButton);	
				    cancelButton.setDefaultFormProcessing(false);
			     }
			else
		     	{
				AjaxButton cancelButton = 
				    new AjaxButton("backButton")
					    {
						@Override
						public void onSubmit (AjaxRequestTarget target)
						    {
							try
							    {	 						 
								setResponsePage(backPage);
							    }
							catch (Exception e)
							    {
							     e.printStackTrace();	
							    }							
							};						
						};
			         add (cancelButton);
			         cancelButton.setDefaultFormProcessing(false);	
		        }
	
			add(downloadLink = buildDownloadSheetButton("downloadButton", idsToPrint));	// issue 120		
			add(submitLink = new AjaxSubmitLink ("print", this)
				{
				@Override
				protected void onSubmit(AjaxRequestTarget target) // issue 464
					{
					String noBarcodes = getForm().get("noBarcodes").getDefaultModelObjectAsString();
					String printerName= getForm().get("printerName").getDefaultModelObjectAsString();					
					int num;
					if (idsToPrint == null) 
						num = (noBarcodes.equals(null)||(noBarcodes.trim().length()==0))? 0: Integer.parseInt(noBarcodes);
					else
						num = idsToPrint.size();				
					if (num > 0)
					 	{
						List<ValueLabelBean> barcodesList = new ArrayList<ValueLabelBean>();					
						List<String> allIds = new ArrayList<String>();  						
						allIds = getAllIds();								
						for (int j = 0; j < allIds.size(); j++)
							// issue 120
							barcodesList.add(new ValueLabelBean(allIds.get(j).replace("<br>",  "\\&"), null));
						String errMsg=new PrintableBarcode(barcodePrintingService, printerName, barcodesList).print();//"BarcodeLabelPrinter1"						
						if (errMsg.length()>0)
							PrintBarcodesPage.this.error(errMsg);
						 else
							PrintBarcodesPage.this.info(num + " barcode(s) printed on " + printerName);
					 	}
					else
						PrintBarcodesPage.this.error("No barcodes have been selected for printing.");					
					target.add(PrintBarcodesPage.this.get("feedback"));
					}
				
				@Override
				public boolean isVisible() {  return true; } //!StringUtils.isEmptyOrNull(printerName) && !"Spreadsheet Writer".equals(printerName);  }
				    });			
			submitLink.setOutputMarkupId(true);			
			}

		private List<String> getAllIds()
			{
			allIds = new ArrayList<String>();		
			if (idsToPrint == null)
				return allIds;
				for (int j = 0; j < nCopies; j++)
					for (int i=0; i < idsToPrint.size(); i++)
						allIds.add(idsToPrint.get(i));
				((BarcodeSheetWriter) downloadLink.getReport()).setBarcodesToPrint(allIds);
			return allIds;
			}
			// jak put back final
		private ExcelDownloadLink buildDownloadSheetButton(String id, final List<String> barcodesToPrint)
			{
			// issue 120
			final IWriteableSpreadsheet writer;
			final BarcodeSheetWriter bw = new BarcodeSheetWriter("", barcodesToPrint);
		   // writer = new BarcodeSheetWriter("", barcodesToPrint);
			writer = bw;
			ExcelDownloadLink lnk = new ExcelDownloadLink(id, writer)
				{
				@Override
				public boolean isVisible()  { return true; } 
				
				@Override
				public void onClick()
					{
				    bw.setBarcodesToPrint(replaceHtmlBR(barcodesToPrint));
					doClick(writer);					
					}			
				};				
			lnk.setOutputMarkupId(true);
			return lnk;
			}
				
		private DropDownChoice<Integer> buildCountDropdown(String id, String property)
			{
			DropDownChoice<Integer> drp = new DropDownChoice<Integer>(id, new PropertyModel<Integer>(this, property), countOptions);			
			drp.add(new AjaxFormComponentUpdatingBehavior("change")
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target)  {  target.add(container); }
				});			
			drp.setEnabled(!("startAtAliquot".equals(id)));
			return drp;
			}
			
		public Integer getnCopies()
			{
			return nCopies;
			}

		public void setnCopies(Integer nCopies)
			{
			this.nCopies = nCopies;
			}
		
		public List<String> getIdsToPrint()
			{
			return idsToPrint;
			}

		public Boolean getUseAliquotTags()
			{
			return useAliquotTags;
			}

		public void setIdsToPrint(List<String> idsToPrint)
			{
			this.idsToPrint = idsToPrint;
			}

		public void setUseAliquotTags(Boolean useAliquotTags)
			{
			this.useAliquotTags = useAliquotTags;
			}

		public String getIdListString()
			{
			List<String> allIdList = this.getAllIds();			
			idListString = idsToPrint == null ? "" : ListUtils.bulletPrint(allIdList, "");
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
		// issue 120
		public List<String> replaceHtmlBR(List<String> htmlList)
			{
			List<String> result = new ArrayList<>();
			for (String s : htmlList) 
			    result.add(s.replaceAll("<br>", " "));		    
			return result;
			}
		}
	}
	
////////////////////////////////////////////////////
// ReserveBarcodesPage.java
// Written by Jan Wigginton, Jun 19, 2017
////////////////////////////////////////////////////

// issue 355
package edu.umich.brcf.shared.panels.utilitypanels;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.RangeValidator;

import edu.umich.brcf.shared.layers.service.BarcodePrintingService;
import edu.umich.brcf.shared.layers.service.SampleService;

// issue 355


public class ReserveBarcodesPage extends WebPage 
	{
	@SpringBean
	BarcodePrintingService barcodePrintingService;
	
	@SpringBean
	SampleService sampleService;
	int maxInt = 7000;
	
	public ReserveBarcodesPage(Page page) 
		{
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new ReserveBarcodesForm("reserveBarcodesForm"));
		}

	public final class ReserveBarcodesForm extends Form {
		private String toSampleId, fromSampleId, noBarcodes;
		public ReserveBarcodesForm(final String id){
			super(id);
			
			// Issue 205
			final RequiredTextField noBarCodesTxt;
			add(noBarCodesTxt = new RequiredTextField("noBarcodes", new PropertyModel<String>(this, "noBarcodes")));
			noBarCodesTxt.setType(Integer.class).setLabel(new Model("Number of Barcodes")).add(new RangeValidator<>(1, maxInt));
		
			// Issue 205
			fromSampleId = sampleService.getNextSampleID(false, 1);
			final TextField fromSampleTxt= new TextField("fromSample", new PropertyModel<String>(this, "fromSampleId") );
			final TextField toSampleTxt=   new TextField("toSample", new PropertyModel<String>(this, "toSampleId") );
			fromSampleTxt.setOutputMarkupId(true);
			toSampleTxt.setOutputMarkupId(true);
			add(fromSampleTxt.setEnabled(false) );	
			add (toSampleTxt.setEnabled(false));
			
			// Issue 205		   
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
					target.add(ReserveBarcodesPage.this.get("feedback"));
				    }
        	    });
			
			// issue 205
			final AjaxSubmitLink reserveBtn = new AjaxSubmitLink ("reserve", this)
			    {
			    @Override
			    protected void onSubmit(AjaxRequestTarget target) // issue 464
				    {			
					sampleService.getNextSampleID(true, Integer.parseInt(noBarcodes));
					ReserveBarcodesPage.this.info(noBarcodes + " barcode(s) :" + fromSampleId + " to " + toSampleId + " have been reserved" );
					fromSampleId = sampleService.getNextSampleID(false,1);
					toSampleId   = sampleService.getNextSampleID(false,Integer.parseInt(noBarcodes));
				    target.add(fromSampleTxt);
					target.add(toSampleTxt);
					target.add(ReserveBarcodesPage.this.get("feedback"));
				    }
			    @Override
				protected void onError(AjaxRequestTarget target)
					{
					target.add(ReserveBarcodesPage.this.get("feedback"));
					}
			    };		 
			    add(reserveBtn);
			}
	        public String getToSampleId() { return toSampleId; }
	        public void setToSampleId(String toSampleId) { this.toSampleId = toSampleId; }
	        public String getFromSampleId() { return fromSampleId; }
	        public void setFromSampleId(String fromSampleId) { this.fromSampleId = fromSampleId; }
	        public String getNoBarcodes() { return noBarcodes; }
	        public void setNoBarcodes(String noBarcodes) { this.noBarcodes = noBarcodes; }
		}	    
	}

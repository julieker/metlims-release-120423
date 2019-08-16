package edu.umich.brcf.metabolomics.panels.lims.prep;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.service.InstrumentService;
import edu.umich.brcf.shared.layers.service.SamplePrepService;



public abstract class CreateNewPlatePage extends WebPage
	{
	@SpringBean
	SamplePrepService samplePrepService;
	
	@SpringBean
	InstrumentService instrumentService;
	
	final List<String> instrumentName = new ArrayList<String>();
	
	public CreateNewPlatePage(Page backPage)
		{
		add(new FeedbackPanel("feedback"));
		add(new CNPForm("cnpForm"));
		}
	
	public class CNPForm extends Form 
		{
		public CNPForm(final String id)
			{
			super(id);
			
			List<String> availableInstruments = instrumentService.getListOfAnalyticalForAgilent();
			
			ListMultipleChoice instrumentDD = new ListMultipleChoice("instrumentName", new Model((Serializable) instrumentName), 
					availableInstruments).setMaxRows(4);
			add(instrumentDD.setRequired(true));
			add(new Button("save")
				{
				@Override
				public void onSubmit() 
					{
					try
						{
						CreateNewPlatePage.this.onSave(instrumentName);
						CreateNewPlatePage.this.info("Save Successful!");
						}
					catch (Exception e){
						CreateNewPlatePage.this.error("Save unsuccessful! Please re-check values entered!");
						}
					}
				});
			}
		}
	
	protected abstract void onSave( List<String> instrumentName);
	}

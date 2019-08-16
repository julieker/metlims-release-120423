package edu.umich.brcf.metabolomics.panels.lims.prep;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import edu.umich.brcf.shared.layers.domain.Preparation;
import edu.umich.brcf.shared.layers.dto.PreppedFractionDTO;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.metabolomics.layers.service.FractionationService;
import edu.umich.brcf.shared.layers.service.SampleService;


public class CreateFractionsPrepPlate extends WebPage
	{
	@SpringBean
	AliquotService aliquotService;
	
	@SpringBean
	SampleService sampleService;
	
	@SpringBean
	FractionationService fractionationService;
	
	final int cols=10, rows = 10;
	
	public static List<String> INSTRUMENT_NAMES = Arrays.asList(new String[] { "GC", "LC1", "LC2"});
	
	
	public CreateFractionsPrepPlate(Page backPage, int num, FractionPrepPanel fp)
		{
		add(new FeedbackPanel("feedback"));
		List<PreppedFractionDTO> inputSamples = new ArrayList<PreppedFractionDTO>();
		for (int l=0; l<num; l++)
			inputSamples.add(new PreppedFractionDTO());
		add(new PrepPlateForm("preparationPlateForm", inputSamples, fp));//, new PrepPlateBean()
		}

	
	public final class PrepPlateForm extends Form 
		{
		int index=0;
		public PrepPlateForm(final String id, final List<PreppedFractionDTO> inputSamples, final FractionPrepPanel fp)
			{
			super(id);//, new CompoundPropertyModel(prepPlateBean)
			final AutoCompleteTextField title = new AutoCompleteTextField("title", new Model(""))
				{
				@Override
				protected Iterator getChoices(String input) {
					if (Strings.isEmpty(input)) 
						{
						return Collections.EMPTY_LIST.iterator();
						}
					return getFractionPrepChoices(input);
					}
				};
			add(title);
			
			final List<String> instrumentName = new ArrayList<String>();
			ListMultipleChoice instrumentDD = new ListMultipleChoice("instrumentName", new Model((Serializable) instrumentName), INSTRUMENT_NAMES).setMaxRows(3);
//			instrumentDD.setRequired(true);
			add(instrumentDD);
			
			add(new ListView("samples", inputSamples)
				{
				public void populateItem(final ListItem item)
					{
	                final PreppedFractionDTO dto = (PreppedFractionDTO)item.getModelObject();
	                if(index<inputSamples.size())
		            	   ++index;
		               else
		            	   index=1;
	                item.add(new Label("index",new Model(index+" ")));
	                final TextField sampleIDText=new TextField("fractionID", new PropertyModel(dto, "id"));
		            item.add(sampleIDText);
		            item.add(newValidTextField("volume", new PropertyModel(dto, "volume"), sampleIDText));
					item.add(new DropDownChoice("volUnits", new PropertyModel(dto, "volUnits"), aliquotService.getAllVolUnits()
					){public boolean isRequired() {
					       return ((sampleIDText.getConvertedInput()!=null)&&(sampleIDText.getInput().length()>0));
				    }});
		        }
			});
			
			add(new Button("save")
				{
				@Override
				public void onSubmit() 
					{
					List<PreppedFractionDTO> inputSamples=((ListView) getForm().get("samples")).getList();
					String titleStr=title.getValue().trim();
//					String volUnits=((DropDownChoice)getForm().get("volUnits")).getModelObjectAsString();
					
					if (titleStr==(null)||titleStr.isEmpty()||instrumentName.size()==0)
						CreateFractionsPrepPlate.this.error("Prep Title and Instrument Name are required fields!!");
					else{
						String retStr=fractionationService.onPrepSave(titleStr,instrumentName,inputSamples);
						String[] messages = retStr.split("_");
						if (retStr.startsWith("Save"))
							{
							CreateFractionsPrepPlate.this.info(messages[0]);
							fp.setPreparation(messages[1]);
							}
		        		 else
		        			{
		        			for (int k=0;k<messages.length;k++)
		        				CreateFractionsPrepPlate.this.error(messages[k]);
		        			 fp.setPreparation(null);
		        			}
						}
					}
				});
			}
		}
	
	private TextField newValidTextField(String id, PropertyModel model, final TextField t1) {
		TextField textField = new TextField(id, model){public boolean isRequired() {
		       return ((t1.getConvertedInput()!=null)&&(t1.getInput().length()>0));
	    }};
		return textField;
	}
	
	private Iterator getFractionPrepChoices(String input){
		List<String> choices = new ArrayList<String>();
		for (Preparation prep : fractionationService.allFractionPreparations()) {
			final String prepTitle = prep.getTitle();
			if (prepTitle.toUpperCase().contains(input.toUpperCase()))
				choices.add(prepTitle + " (" + prep.getPrepID()+")");
		}
		return choices.iterator();
	}
}

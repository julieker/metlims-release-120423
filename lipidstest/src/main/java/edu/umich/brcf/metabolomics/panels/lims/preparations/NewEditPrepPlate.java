package edu.umich.brcf.metabolomics.panels.lims.preparations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.emory.mathcs.backport.java.util.Arrays;
import edu.emory.mathcs.backport.java.util.Collections;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.SamplePrepService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.io.StringUtils;


public class NewEditPrepPlate extends WebPage
	{
	@SpringBean
	AliquotService aliquotService;
	
	@SpringBean
	SampleService sampleService;
	
	@SpringBean
	SamplePrepService samplePrepService;
	
	@SpringBean
	ExperimentService experimentService;
	
	int cols=0, rows = 0, cells = 0;
	String[][] thisValues;
	String prepTitle;
	Loop boxLoop; 
	public static List<String> INSTRUMENT_NAMES = Arrays.asList(new String[] { "GC", "LC1", "LC2", "LC3", "LC4", "LC5", "LC6"});
	
	
	
	public NewEditPrepPlate(Page backPage, String title, NewNewPreparationsSearchPanel pp, String plateFrmt)
		{
		if(plateFrmt.equals("96 Well")){ cols=12; rows=8; cells=96; }
		else{  cols=9; rows=6; cells=54; }
		thisValues= new String[rows][cols];
		
		add(new FeedbackPanel("feedback"));
		add(new NewPrepPlateForm("preparationPlateForm", title, pp));
		}

	public final class NewPrepPlateForm extends Form 
		{
		int index=0;
		WebMarkupContainer container;
		
		public NewPrepPlateForm(final String id, final String title, final NewNewPreparationsSearchPanel pp)
			{
			super(id);
			setPrepTitle(title);
			
			System.out.println("Experiments were " + getExperiments().toString());
			add(new Label("prepTitle", title));
			add(new TextField("volume",new Model("")));
			add(new DropDownChoice("volUnits",  new Model(""),aliquotService.getAllVolUnits()));
			
			add(buildClearButton("clearButton"));
			add(buildPopulateButton("populateButton"));
			
			container  = new WebMarkupContainer("container");
			container.add(boxLoop = new Loop("cols", cols) 
				{
				protected void populateItem(LoopItem item) 
					{
					final int col = item.getIndex();
					item.add(buildCol(rows, col));
					}
				});
			boxLoop.setOutputMarkupId(true);
			container.setOutputMarkupId(true);
			add(container);
			
			final List<String> instrumentNames = new ArrayList<String>();
			ListMultipleChoice instrumentDD = new ListMultipleChoice("instrumentName", new Model((Serializable) instrumentNames), INSTRUMENT_NAMES).setMaxRows(4);
			add(instrumentDD);
			//verifyFormat
			add(buildSaveButton("save", title, pp, instrumentNames));
			}
		
		
		private Button buildSaveButton(String id, final String title, final NewNewPreparationsSearchPanel pp, final List <String> instrumentNames) 
			{
			return new Button("save")
				{
				@Override
				public void onSubmit() 
					{
					index=0;
					String volume=getForm().get("volume").getDefaultModelObjectAsString();
					String volUnits=((DropDownChoice)getForm().get("volUnits")).getDefaultModelObjectAsString();
					
					if (volume.equals(null)||volume.isEmpty()||volUnits.equals(null)||volUnits.isEmpty()||instrumentNames.size()==0)
						{
						NewEditPrepPlate.this.error("Volume, Units and Instrument Name are required fields!!");
						return;
						}
					
					String retStr = samplePrepService.validateAndSave(title,instrumentNames, thisValues, volume, volUnits, 
							cols, rows);
					String[] messages = retStr.split("_");
					
					if (retStr.startsWith("Save"))
						{
						NewEditPrepPlate.this.info(messages[0]);
						pp.setPreparation(messages[1]);
						}
	        		 else
	        		 	{
	        			for (int k=0;k<messages.length;k++)
	        				 NewEditPrepPlate.this.error(messages[k]);
	        			pp.setPreparation(null);
	        		 	}
					}
				};
			}
		
		
		private Loop buildCol(int rows, final int col)
			{
			return new Loop("rows", rows)
				{
				protected void populateItem(LoopItem item) 
					{
					if(index<cells)
		        	   ++index;
					else
		        	   index=1;
		           final int row=item.getIndex();
		           
		           IModel model=new IModel() 
		           		{  
		        	   	public Object getObject()          {  return thisValues[row][col];  }
						public void setObject(Object arg0) { thisValues[row][col]=(String) arg0; }
						public void detach() { }
		           		};
		           		
		            item.add(new TextField("cell", model));
		            String value = index < 10 ? "0" + index + " " : index + " ";
		            item.add(new Label("index",new Model(value)));
					}
				};
			}
		
		private IndicatingAjaxLink buildPopulateButton(String id)
			{
			// issue 39
			return new IndicatingAjaxLink <Void> (id)
				{
				@Override
				public void onClick(AjaxRequestTarget target) 
					{
					int row = 0;
					List <String> sids = getExperimentSampleIds();
					
					for (int i =0; i < sids.size(); i++)
						{
						if (i >= cells || row>= rows) break;
						int col = i % cols;
						thisValues[row][col] = sids.get(i);
						if (col == cols - 1) row++;
						}
					
					if (sids.size() > rows * cols)
						target.appendJavaScript("alert('There were too many samples to place');");
				
					target.add(container);
					}
				};
			}
		
		private IndicatingAjaxLink buildClearButton(String id)
			{
			// issue 39
			return new IndicatingAjaxLink <Void>(id)
				{
				@Override
				public void onClick(AjaxRequestTarget target) 
					{
					for (int i = 0; i < rows; i++)
						for (int j = 0;  j < cols; j++)
							thisValues[i][j] = "";
					
					target.add(container);
				//	setSelectedExperiment("");
				//	setSelectedAssay("");
				//	target.add(selectedExperimentDrop);
				//	target.add(selectedAssayDrop);
					}
				};
			}
		}
		private List<String> getExperiments()
			{
			List<String>lst = new ArrayList<String>();
			
			if (prepTitle == null)
				return lst;
			
			String [] tokens = StringUtils.splitAndTrim(prepTitle, "_");
			
			for (int i =0; i < tokens.length - 1; i++)
				if (tokens[i].startsWith("EX"))
					lst.add(tokens[i]);
			
			return lst;
			}
		

		private List<String> getAssays()
			{
			List<String>lst = new ArrayList<String>();
			
			String [] tokens = StringUtils.splitAndTrim(prepTitle, "_");
			
			for (int i =0; i < tokens.length - 1; i++)
				if (tokens[i].startsWith("A0"))
					lst.add(tokens[i]);
			
			return lst;
			}
	public String getPrepTitle() {
		return prepTitle;
	}

	public void setPrepTitle(String prepTitle) {
		this.prepTitle = prepTitle;
	}
	
	public List<String> getExperimentSampleIds()
		{
		List<String> expIds = getExperiments();
		List<String> assayIds = getAssays();
		List<String> sids = new ArrayList<String>();
	
		for (int i = 0; i < expIds.size(); i++)
			{
			String expId = expIds.get(i);
			String aid = assayIds.get(i);
			
			List <String> esids = new ArrayList<String>();
			if (FormatVerifier.verifyFormat(Experiment.idFormat,expId.toUpperCase()))
				esids = sampleService.sampleIdsForExpIdAndAssayId(expId, aid);
			
			sids.addAll(esids);
			}
		Collections.sort(sids);
		return sids;
		}
	}


////////////////////  SCRAP CODE//////////////
//add(new Loop("rows", rows) {//, prepPlateBean.getRows()
//protected void populateItem(LoopItem item) {
// final int row=item.getIteration();
// item.add(new Loop("cols", cols) {//, prepPlateBean.getCols()
//	 protected void populateItem(LoopItem item) {
//		 if(index<96)
//        	   ++index;
//           else
//        	   index=1;
//           final int col=item.getIteration();
//           IModel model=new IModel() {
//				public Object getObject() {
//					return thisValues[row][col];
//				}
//
//				public void setObject(Object arg0) {
//					thisValues[row][col]=(String) arg0;
//				}
//
//				public void detach() {
//				}
//           };
//           item.add(new TextField("cell", model).setVisible(index<5));
//           item.add(new Label("index",new Model(index+" ")));
//        }
//	});
//}
//});

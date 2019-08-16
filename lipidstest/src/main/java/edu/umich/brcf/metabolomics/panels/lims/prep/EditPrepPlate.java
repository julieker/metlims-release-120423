package edu.umich.brcf.metabolomics.panels.lims.prep;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
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
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.SamplePrepService;
import edu.umich.brcf.shared.layers.service.SampleService;


public class EditPrepPlate extends WebPage{

	@SpringBean
	AliquotService aliquotService;
	
	@SpringBean
	SampleService sampleService;
	
	@SpringBean
	SamplePrepService samplePrepService;
	
	int cols=0;//PrepMethodsPanel.cols;
	int rows=0;//PrepMethodsPanel.rows;
	int cells=0;
	String[][] thisValues;
//	private Cell[][] values;

	public static List<String> INSTRUMENT_NAMES = Arrays.asList(new String[] { "GC", "LC1", "LC2", "LC3", "LC4", "LC5", "LC6"});
	
	public EditPrepPlate(Page backPage, String title, PreparationSearchPanel pp, String plateFrmt){
		if(plateFrmt.equals("96 Well")){
			cols=12; rows=8; cells=96;
		}
		else{
			cols=9; rows=6; cells=54;
		}
		thisValues= new String[rows][cols];
		add(new FeedbackPanel("feedback"));
		add(new PrepPlateForm("preparationPlateForm", title));//, new PrepPlateBean()
	}

	public final class PrepPlateForm extends Form {
		int index=0;
		public PrepPlateForm(final String id, final String title){
			super(id);//, new CompoundPropertyModel(prepPlateBean)
			add(new TextField("volume",new Model("")));
			add(new DropDownChoice("volUnits",  new Model(""),aliquotService.getAllVolUnits()));//.setRequired(true));
//			add(new Loop("rows", rows) {//, prepPlateBean.getRows()
//				protected void populateItem(LoopItem item) {
//			     final int row=item.getIteration();
//			     item.add(new Loop("cols", cols) {//, prepPlateBean.getCols()
//			    	 protected void populateItem(LoopItem item) {
//			    		 if(index<96)
//			            	   ++index;
//			               else
//			            	   index=1;
//			               final int col=item.getIteration();
//			               IModel model=new IModel() {
//								public Object getObject() {
//									return thisValues[row][col];
//								}
//
//								public void setObject(Object arg0) {
//									thisValues[row][col]=(String) arg0;
//								}
//
//								public void detach() {
//								}
//			               };
//			               item.add(new TextField("cell", model).setVisible(index<5));
//			               item.add(new Label("index",new Model(index+" ")));
//			            }
//					});
//			   }
//			});
			
			add(new Loop("cols", cols) 
				{
				protected void populateItem(LoopItem item) 
					{
					final int col=item.getIndex();
					item.add(new Loop("rows", rows) {//, prepPlateBean.getCols()
			    	 protected void populateItem(LoopItem item) {
			    		  if(index<cells)
			            	   ++index;
			               else
			            	   index=1;
			    		  
			               final int row=item.getIndex();
			               IModel model=new IModel() {
								public Object getObject() {
									return thisValues[row][col];
								}

								public void setObject(Object arg0) {
									thisValues[row][col]=(String) arg0;
								}

								public void detach() {
								}
			               };
			               item.add(new TextField("cell", model));
			               item.add(new Label("index",new Model(index+" ")));
			            }
					});
			   }
			});
			// OnComponentTag
			
			final List<String> instrumentName = new ArrayList<String>();
			ListMultipleChoice instrumentDD = new ListMultipleChoice("instrumentName", new Model((Serializable) instrumentName), INSTRUMENT_NAMES).setMaxRows(3);
//			instrumentDD.setRequired(true);
			add(instrumentDD);
			add(new Button("save"){
				@Override
				public void onSubmit() {
					index=0;
					String volume=getForm().get("volume").getDefaultModelObjectAsString();
					String volUnits=((DropDownChoice)getForm().get("volUnits")).getDefaultModelObjectAsString();
					if (volume.equals(null)||volume.isEmpty()||volUnits.equals(null)||volUnits.isEmpty()||instrumentName.size()==0)
						EditPrepPlate.this.error("Volume, Units and Instrument Name are required fields!!");
					else{
						String retStr=samplePrepService.validateAndSave(title,instrumentName, thisValues, volume, volUnits, cols, rows);
						String[] messages = retStr.split("_");
						if (retStr.startsWith("Save")){
							EditPrepPlate.this.info(messages[0]);
							//pp.setPreparation(messages[1]);
		        		 }
		        		 else{
		        			 for (int k=0;k<messages.length;k++)
		        				 EditPrepPlate.this.error(messages[k]);
		        			// pp.setPreparation(null);
		        		 }
					}
				}
			});
		}
	}
}
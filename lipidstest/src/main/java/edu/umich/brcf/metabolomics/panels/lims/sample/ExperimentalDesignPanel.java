package edu.umich.brcf.metabolomics.panels.lims.sample;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.emory.mathcs.backport.java.util.Collections;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Factor;
import edu.umich.brcf.shared.layers.service.ExperimentService;


public class ExperimentalDesignPanel extends Panel{
	
	String[][] designMatrix;
	String[] matrixColumnHeaders;
	int cols;
	int rows;
	Experiment experiment;
	
	@SpringBean 
	private ExperimentService experimentService;
	
	public ExperimentalDesignPanel(String id, Experiment experiment) {
		super(id);
		setOutputMarkupId(true);
		setExperiment(experiment);
		
		add(new Label("head", new Model(getExperimentName())));
		add(new Loop("rows", rows) 
			{
			protected void populateItem(LoopItem item) 
				{
			     final int row=item.getIndex();
			     item.add(new Loop("cols", cols) 
			     	{
			    	 protected void populateItem(LoopItem item) {
			               final int col=item.getIndex();
			               IModel model=new IModel(){
								public Object getObject() {
									return designMatrix[row][col];
								}

								public void setObject(Object arg0) {
									designMatrix[row][col]=(String) arg0;
								}

								public void detach() {
								}
			               };
			               item.add(new Label("data", model));
			    	 }
			     });
				}
		 });
		 add(new Loop("colHeaders", cols) {
				protected void populateItem(LoopItem item) {
					final int col=item.getIndex();
					 IModel model=new IModel() {
							public Object getObject() {
								return matrixColumnHeaders[col];
							}

							public void setObject(Object arg0) {
								matrixColumnHeaders[col]=(String) arg0;
							}

							public void detach() {
							}
		               };
		               item.add(new Label("colHeader", model));
				}});
	}

	private String getExperimentName() {
		if (getExperiment()!=null)
			return (getExperiment().getExpName()+" ("+experiment.getExpID()+")");
		else
			return "";
	}

	public Experiment getExperiment() {
		return experiment;
		}

	public void setExperiment(Experiment experiment) 
		{
		this.experiment=experiment;
		
		if (experiment!=null && !experiment.equals(null))
			{
			setDesignMatrix(experimentService.getDesignMatrix(experiment));
			if(getDesignMatrix().length>0)
				{
				List<Factor> factors = experiment.getFactors();
				List<String> fNames = new ArrayList<String>();
				for(Factor factor:factors)
					fNames.add(factor.getFactorName());
				
				if(fNames.size()>0)
					{
					Collections.sort(fNames);
					matrixColumnHeaders=new String[fNames.size()+1];
					matrixColumnHeaders[0]="Sample ID";
					for (int i=1; i<=fNames.size();i++)
						{
						//System.out.println("In set Experiemnt col header " + (i -1) + " is " + fNames.get(i-1));
						matrixColumnHeaders[i] = fNames.get(i-1);
						}
					}
				}
			else 
				{
				matrixColumnHeaders = new String[1];
				matrixColumnHeaders[0] = "UNAVAILABLE";
				}
			}		
		}

	
	public String[][] getDesignMatrix() {
		return designMatrix;
	}

	public void setDesignMatrix(String[][] designMatrix) {
		this.designMatrix = designMatrix;
	}
}

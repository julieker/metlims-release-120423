// DrccPanel.java
// Written by Jan Wigginton July 2015


package edu.umich.brcf.metabolomics.panels.lipidshome.drcc;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import edu.umich.brcf.shared.util.InfoLine;
import edu.umich.brcf.shared.util.interfaces.IWriteConvertable;
import edu.umich.brcf.shared.util.widgets.AjaxBackButton;



public abstract class DrccPanel extends Panel implements Serializable 
	{
	String expId;

	List<String> propertyList = new ArrayList<String>(); 
	List<IWriteConvertable> infoLines = new ArrayList<IWriteConvertable>();
//	List<String> absciexInstruments = Arrays.asList(new String [] {"IN0024 (LIPIDS)", "IN0027 (LIPIDS2)"});
	String panelTitle = "";

	public DrccPanel(String id, String selectedExperiment, WebPage backPage) 
		{
		super(id);
	
		expId = selectedExperiment; 
		
		panelTitle = getPanelTitle();
		add(new Label("panelTitle", new PropertyModel<String>(this, panelTitle)));
		Field [] fields = getInfoObjectFields();
		for (int i = 0; i < fields.length; i++)
			propertyList.add(fields[i].getName());
		
		addPropertyFields(fields);
		
		String outputFileName = getOutputFileName(this.expId);
		
		//MetWorksDataDownload resource = new MetWorksDataDownload("downloadData", infoLines, outputFileName +".tsv", null);
		//add(resource.getResourceLink());
		
		add(new AjaxBackButton("backButton", backPage));
		}
	
	
	private void addPropertyFields(Field [] fields)
		{
		PropertyModel<String> model = null; 
		
		for(int i = 0; i < propertyList.size(); i++)
			{
			String fieldLabel = "";
			try {  fieldLabel = ((DrccInfoField) fields[i].get(getDataSource())).getFieldLabel();  }
			catch (Exception e) {}
			
			addPropertyField(fieldLabel, propertyList.get(i), model, i);
			}
		}

	protected void addPropertyField(String fieldLabelTitle, String propertyName, PropertyModel <String> model, int i)
		{	
		//System.out.println("Property name is " + propertyName);
		model = new PropertyModel<String>(getDataSource(), propertyName);
		//System.out.println("Model is " + (model == null ? " null" : " not null"));
		infoLines.add(new InfoLine(fieldLabelTitle, model));
		addPropertyFieldComponent(propertyName, model);
		}
	
	
	protected String getPanelTitle()
		{
		return panelTitle;
		}
	
	
	
	protected abstract Object getDataSource();
	protected abstract void addPropertyFieldComponent(String propertyName, PropertyModel<String> model);
	protected abstract String getOutputFileName(String tag);
	protected abstract Field [] getInfoObjectFields();
//	protected abstract String getPanelTitle();
	}
	
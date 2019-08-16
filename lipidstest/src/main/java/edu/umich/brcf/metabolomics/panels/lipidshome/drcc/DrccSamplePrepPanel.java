// DrccSamplePrepPanel.java
// Written by Jan Wigginton 06/01/15

package edu.umich.brcf.metabolomics.panels.lipidshome.drcc;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import edu.umich.brcf.shared.util.interfaces.IWriteConvertable;




public class DrccSamplePrepPanel extends Panel
	{
	DrccSamplePrepInfo samplePrepInfo; // = new DrccSamplePrepInfo();

	
	String expId;

	List<String> propertyList = new ArrayList<String>(); 
	List<IWriteConvertable> infoLines = new ArrayList<IWriteConvertable>();
	List<String> absciexInstruments = Arrays.asList(new String [] {"IN0024 (LIPIDS)", "IN0027 (LIPIDS2)"});
	

	public DrccSamplePrepPanel(String id, String selectedExperiment, WebPage backPage, DrccSamplePrepInfo samplePrepInfo) 
		{
		super(id);
	
		expId = selectedExperiment; 
		this.samplePrepInfo = samplePrepInfo;
		
		for (int i = 0; i< samplePrepInfo.getInfoFields().size(); i++)
			add(buildTextField(i)); 
		
		//String fullName = "DrccAnalysisInfo_" + this.expId;
		//METWorksDataDownload resource = new METWorksDataDownload("downloadData", 
		//		new PropertyModel(samplePrepInfo, "infoFields"), fullName +".tsv", null);
		//
		//add(resource.getResourceLink());
		//add(new AjaxBackButton("backButton", backPage));
		}
	
	
	private TextField buildTextField(int i)
		{
		TextField fld = new TextField(samplePrepInfo.getInfoFields().get(i).getFieldTag(), 
				new PropertyModel <String>(samplePrepInfo.infoFields.get(i), "fieldValues.0"));
		
		fld.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForTextField", fld,  i));
		
		return fld;
		}

	private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(final String event, final 
			String response, final TextField field, final int i)
		{
		return new AjaxFormComponentUpdatingBehavior(event)
	        {
	        @Override
	        protected void onUpdate(AjaxRequestTarget target)
	        	{
	        	target.add(field);
	        	samplePrepInfo.infoFields.get(i).setFieldValues(0, field.getDefaultModelObjectAsString());
	        	}
	        };
		}
	}

	//public DrccSamplePrepPanel(String id, String selectedExperiment, WebPage backPage) 
	//	{
	//	super(id, selectedExperiment, backPage);
	//	}

//	@Override
//	protected String getOutputFileName(String tag) 
//		{
//		return "DrccSamplePrepInfo_" + tag;
//		}

//	@Override
//	protected Field[] getInfoObjectFields() 
//		{
//		return DrccSamplePrepInfo.class.getFields();
//		}	

//	@Override
//	protected Object getDataSource() 
//		{
//		return (samplePrepInfo == null ? new DrccSamplePrepInfo() : samplePrepInfo);	
//		}


//	@Override
//	protected void addPropertyFieldComponent(String propertyName, PropertyModel<String> model) 
//		{
//		if (!propertyName.equals("instrumentName"))
//			add(new TextField(propertyName, model));
		//else
		//	add(buildInstrumentDropdown(propertyName, model));
//		}
	
	//@Override
//	protected String getPanelTitle()
//		{
//		return "Sample Prep";
//		}
//	}


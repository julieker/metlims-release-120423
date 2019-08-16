// DrccStudyInfoPanel.java
// Written by Jan Wigginton 06/01/15

package edu.umich.brcf.metabolomics.panels.lipidshome.drcc;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;




public class DrccStudyInfoPanel extends Panel 
	{
	private static final long serialVersionUID = -65758071513554142L;
	
	String expId; 
	
	DrccStudyInfo studyInfo;
			
	public DrccStudyInfoPanel(String id, String selectedExperiment, WebPage backPage, DrccStudyInfo studyInfo)
		{
		super(id);
		

		expId = ((selectedExperiment != null && !selectedExperiment.trim().equals("")) ? selectedExperiment : "EX00417");
		if (expId.charAt(0) != 'E' || expId.charAt(1) != 'X')
			expId = "EX00417";
		
		this.studyInfo = studyInfo;
		//studyInfo.initializeFromExpId(expId);
		for (int i = 0; i< studyInfo.getInfoFields().size(); i++)
			{
			DrccInfoField valueSource = studyInfo.infoFields.get(i);
			String fieldTag = valueSource.getFieldTag();
			
			if (fieldTag.equals("studyDescription"))
				add(buildTextArea(i));
			else if (fieldTag.equals("studyComments"))
				add(buildTextArea(i));
			else
				add(buildTextField(i)); 
			}
	
		//String fullName = "DrccStudyInfo_" + this.expId;
		//METWorksDataDownload resource = new METWorksDataDownload("downloadLink", new PropertyModel(studyInfo, "infoFields"), fullName +".tsv", null);
		//add(resource.getResourceLink());
	
		//add(new AjaxBackButton("backButton", backPage));
		}
	
	
	private TextArea buildTextArea(int i)
		{
		DrccInfoField valueSource = studyInfo.infoFields.get(i);
		TextArea fld = new TextArea(valueSource.getFieldTag(),  new PropertyModel <String>(valueSource, "fieldValues.0"));
		
		fld.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForTextField", fld,  i));
		
		return fld;
		}
	
	
	private TextField buildTextField(int i)
		{
		DrccInfoField valueSource = studyInfo.infoFields.get(i);
		TextField fld = new TextField(valueSource.getFieldTag(),  new PropertyModel <String>(valueSource, "fieldValues.0"));
		
		fld.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForTextField", fld,  i));
		
		return fld;
		}

	
	private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(final String event, final 
		String response, final Component field, final int i)
		{
		return new AjaxFormComponentUpdatingBehavior(event)
	        {
	        @Override
	        protected void onUpdate(AjaxRequestTarget target)
	        	{
	        	target.add(field);
	        	DrccInfoField valueSource = studyInfo.infoFields.get(i);
	    		
	        	valueSource.setFieldValues(0, field.getDefaultModelObjectAsString());
	        	}
	        };
		}	
	
	
	public String getOutputFileName()
		{
		return "DrccStudyInfo_" + expId + ".tsv";
		}
	}
	

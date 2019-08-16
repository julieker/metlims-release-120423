// DrccCollectionPanel.java
// Written by Jan Wigginton July 2015

package edu.umich.brcf.metabolomics.panels.lipidshome.drcc;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;



public class DrccCollectionPanel extends Panel
	{
	DrccCollectionInfo collectionInfo; // = new DrccCollectionInfo();
	String expId;
	List <String> possibleSampleTypes = Arrays.asList(new String [] {"Blood", "Urine", "Saliva", "Tissue", "Cell", "Other"});
	

	public DrccCollectionPanel(String id, String selectedExperiment, WebPage backPage, DrccCollectionInfo collectionInfo) 
		{
		super(id);
	
		expId = selectedExperiment; 
		this.collectionInfo = collectionInfo;
		
		for (int i = 0; i< collectionInfo.getInfoFields().size(); i++)
			{
			String fieldTag = collectionInfo.getInfoFields().get(i).getFieldTag();
			if ("sampleType".equals(fieldTag))
				add(this.getSampleTypeDropdown("sampleType", new PropertyModel<String>(collectionInfo.getInfoFields().get(i), "fieldValues.0" )));
			else
				add(buildTextField(i)); 
			}
		
		//String fullName = "DrccCollectionInfo_" + this.expId;
		//MetWorksDataDownload resource = new MetWorksDataDownload("downloadData", 
		//		new PropertyModel(collectionInfo, "infoFields"), fullName +".tsv", null);
		
		//add(resource.getResourceLink());
		//add(new AjaxBackButton("backButton", backPage));
		}
	
	
	private TextField buildTextField(int i)
		{
		TextField fld = new TextField(collectionInfo.getInfoFields().get(i).getFieldTag(), 
				new PropertyModel <String>(collectionInfo.infoFields.get(i), "fieldValues.0"));
		
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
	        	collectionInfo.infoFields.get(i).setFieldValues(0, field.getDefaultModelObjectAsString());
	        	}
	        };
		}
	
	
	public DropDownChoice<String> getSampleTypeDropdown(String propertyName, PropertyModel <String>model)
		{
		DropDownChoice drp = new DropDownChoice<String>(propertyName, model, possibleSampleTypes)
			{
			/*  issue 464
			@Override
	     	protected boolean wantOnSelectionChangedNotifications() 
				{
	            return true;
				}*/ 
			};					
					
		return drp;
		}
	}


	
	

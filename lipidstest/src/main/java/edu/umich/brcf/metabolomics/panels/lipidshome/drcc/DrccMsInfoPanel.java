// DrccMsInfoPanel.java
// Written by Jan Wigginton 06/01/15

package edu.umich.brcf.metabolomics.panels.lipidshome.drcc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import edu.umich.brcf.shared.util.utilpackages.DateUtils;
import edu.umich.brcf.shared.util.widgets.METWorksAjaxUpdatingDateTextField;





public class DrccMsInfoPanel extends Panel
	{
	DrccMsInfo msInfo; // = new DrccMsInfo();
	String selectedMode, expId, outputFileName;

	private Date analysisDate = new Date();
	private METWorksAjaxUpdatingDateTextField dateSelectionField;
	
	List <String> availableInstrumentTypes = Arrays.asList(new String [] {"Triple TOF", "GC X GC-TOF",  "GC-ITQ", "GC-TOF", "Ion Trap", "LTQ-FT",
			"ORBITRAP", "QTOF", "Single Quadrupole", "Triple Quadrupole", "Other"});
	List <String> availableModes = Arrays.asList(new String [] {"Positive", "Negative", "Both"});
	List <String> availableIonizationTypes = Arrays.asList(new String [] {"APCI", "API", "EI", "ESI", "HESI", "MALDI"});
	
	
	public DrccMsInfoPanel(String id, String selectedExperiment, WebPage backPage, DrccMsInfo msInfo) 
		{
		super(id);
	
		expId = selectedExperiment; 
		this.msInfo = msInfo;
		this.selectedMode = msInfo.selectedMode;
		this.analysisDate = msInfo.analysisDate;
		
		add(this.buildPropertyDropdown("selectedMode", new PropertyModel<String> (this, "selectedMode"), availableModes, 2));
		add(dateSelectionField = this.grabDateTextField("analysisDate", "analysisDate"));
		
		add(buildDisappearingTextField("positiveHeader", "POSITIVE", "Negative"));
		add(buildDisappearingTextField("negativeHeader", "NEGATIVE", "Positive"));
			
		
		for (int i = 0; i< msInfo.getInfoFields().size(); i++)
			{
			String fieldTag = msInfo.getInfoFields().get(i).getFieldTag();
			
			for (int j = 0; j < 2; j++)
				{
				if ("ionMode".equals(fieldTag))
					add(buildPropertyDropdown("ionMode." + j , 
							new PropertyModel<String>(msInfo.getInfoFields().get(i), "fieldValues." + j),
							this.availableModes, j));
				
				else if ("msInstrumentType".equals(fieldTag))
					add(buildPropertyDropdown("msInstrumentType." + j , 
							new PropertyModel<String>(msInfo.getInfoFields().get(i), "fieldValues." + j),
							this.availableInstrumentTypes, j));
				
				else if ("msIonizationType".equals(fieldTag))
					add(buildPropertyDropdown("msIonizationType." + j , 
							new PropertyModel<String>(msInfo.getInfoFields().get(i), "fieldValues." + j),
							this.availableIonizationTypes, j));

				else
					add(buildTextFields(i, j)); 
				}
			}
		
	//	String fullName = getOutputFileName();
	//	METWorksDataDownload resource = new METWorksDataDownload("downloadData", new PropertyModel(msInfo, "infoFields"), 
	//			getOutputFileName(), null);
	//.getResource().setOutfileName(new PropertyModel<String>(this, "outputFileName"));
	//	add(resource.getResourceLink());
		
	//	add(new AjaxBackButton("backButton", backPage));
		}
	

	// field builders
	
	private TextField buildDisappearingTextField(String id, String value,  final String hideValue)
		{
		return new TextField(id, new Model(value))
			{
			@Override
			public boolean isVisible()
				{
				msInfo.setViewNegativeMode(!("Positive".equals(selectedMode)));
	        	msInfo.setViewPositiveMode(!("Negative".equals(selectedMode)));
	        	
				return !getSelectedMode().equals(hideValue);
				}
			};
		}
		
	
	private TextField buildTextFields(int i, final int j)
		{
		TextField fld = new TextField(msInfo.getInfoFields().get(i).getFieldTag() + "." + j, 
				new PropertyModel <String>(msInfo.infoFields.get(i), "fieldValues." + j))
			{
			@Override
			public boolean isVisible()
				{
				return getVisibilityForElement(j);
				}
			};
		
		fld.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForTextField", fld,  i, j));
	
		return fld;
		}
	
	
	
	private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(final String event, final 
			String response, final Component field, final int i, final int j )
		{
		return new AjaxFormComponentUpdatingBehavior(event)
	        {
	        @Override
	        protected void onUpdate(AjaxRequestTarget target)
	        	{
	        	target.add(field);
	        	if (i >= 0 && j >= 0)
	        		{
	        		DrccInfoField valueSource = msInfo.infoFields.get(i);
	        		valueSource.setFieldValues(j, field.getDefaultModelObjectAsString());
	        		}
	        	}
	        };
		}
	
	
	private METWorksAjaxUpdatingDateTextField grabDateTextField(String id, String property)
		{
		return new METWorksAjaxUpdatingDateTextField(id, new PropertyModel<Date>(this, property), "change")
			{
			@Override
			protected void onUpdate(AjaxRequestTarget target) 
				{				
				//String newDate = dateSelectionField.grabValueAsString();
				//setAnalysisDate(dateSelectionField.grabValueAsDate());
				//target.add(dateForAnalysisInfoField_1);
				}
			};
		}

	
	private DropDownChoice buildPropertyDropdown(String id, PropertyModel<String> model, List<String>choices, final int j)
		{
		DropDownChoice drp = new DropDownChoice(id, model, choices)
		 	{
			/* issue 464 @Override
	    	protected boolean wantOnSelectionChangedNotifications() 
				{
	            return true;
				}*/
			
			@Override
			public boolean isVisible()
				{
				return getVisibilityForElement(j);
				}
			};					
		
	//	drp.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForDrop", drp, -1, -1));
		return drp;
		}
	
	
	// Getters/setters/utilities
	private boolean getVisibilityForElement(final int j)
		{
		return ((  j == 0  && !("Positive".equals(getSelectedMode())) )
				|| (j == 1 && !("Negative".equals(getSelectedMode())) 
				|| (j == 2)));
		}
	
	
	public String getOutputFileName()
		{
		String dateStr = analysisDate == null ? "" : DateUtils.dateAsString(analysisDate);
		return ("DrccMsInfo_" + this.expId + "_" + dateStr + "_" + this.getSelectedMode() + ".tsv");
		}

	public List<String> getAvailableInstrumentTypes() 
		{
		return availableInstrumentTypes;
		}

	
	public void setAvailableInstrumentTypes(List<String> availableInstrumentTypes) 
		{
		this.availableInstrumentTypes = availableInstrumentTypes;
		}

	public List<String> getAvailableModes() 
		{
		return availableModes;
		}

	public void setAvailableModes(List<String> availableModes) 	
		{
		this.availableModes = availableModes;
		}

	public List<String> getAvailableIonizationTypes() 
		{
		return availableIonizationTypes;
		}

	public void setAvailableIonizationTypes(List<String> availableIonizationTypes) 
		{
		this.availableIonizationTypes = availableIonizationTypes;
		}

	public String getSelectedMode() 
		{
		return selectedMode;
		}

	public void setSelectedMode(String selectedMode) 
		{
		this.selectedMode = selectedMode;
		}

	public Date getAnalysisDate()
		{
		return this.analysisDate;
		}

	public void setAnalysisDate(Date date)
		{
		this.analysisDate = date;
		}
	}



/////////////////////////SCRAP///////////////////////




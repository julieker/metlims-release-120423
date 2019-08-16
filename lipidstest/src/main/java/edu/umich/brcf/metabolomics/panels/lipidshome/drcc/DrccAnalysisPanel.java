// DrccAnalysisPanel.java
// Written by Jan Wigginton 06/15/15

package edu.umich.brcf.metabolomics.panels.lipidshome.drcc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import edu.umich.brcf.shared.util.utilpackages.DateUtils;
import edu.umich.brcf.shared.util.widgets.METWorksAjaxUpdatingDateTextField;



public class DrccAnalysisPanel extends Panel 
	{
	private String expId;
	private Date analysisDate;


	private TextField dateForAnalysisInfoField_0, dateForAnalysisInfoField_1;
	private TextField rawDataDirectory_0, rawDataDirectory_1;
	private TextField processedDataDirectory_0, processedDataDirectory_1;
	private METWorksAjaxUpdatingDateTextField dateSelectionField;
	
	String selectedMode = "Both";
	
	DrccAnalysisInfo analysisInfo; // = new DrccAnalysisInfo();
	List <String> analysisTypes = Arrays.asList(new String [] {"MS", "NMR"});
	List <String> availableModes = Arrays.asList(new String [] {"Positive", "Negative", "Both"});
	
	List<String> instrumentNames = Arrays.asList(new String [] {"AB Sciex Triple TOF 5600", 
																"Agilent 1200 LC/Agilent 6530 QTOF",
																"Agilent 6220 ToF MS",
																"Agilent 6530 QTOF",
																"Agilent 6550 QTOF", 
																"Agilent 7890A GC/Agilent MSD 5975C MS",
																"Bruker Avance III",
																"Leco GC-TOF",
																"Leco Pegasus 4D GC X GC-TOF",
																"Leco Pegasus III GC-TOF",
																"Leco Pegasus IV",
																"Leco Pegasus IV GC X GC-TOF",
																"Thermo LTQ-FT",
																"Thermo Scientific ITQ",
																"Thermo Scientific Q-Exactive Orbitrap",
																"Thermo Scientific TSQ Ultra Quantum",
																"Thermo-Finnigan LTQ MS",
																"Thermo-Finnigan Trace DSQ MS",
																"Waters Synapt-G2"});

	List<TextField> fieldsArray;
	
	public DrccAnalysisPanel(String id, String selectedExperiment, WebPage backPage, DrccAnalysisInfo analysisInfo) 
		{
		super(id);
	
		this.analysisInfo = analysisInfo;
		expId = selectedExperiment; 
		this.selectedMode = analysisInfo.selectedMode;
		this.analysisDate = analysisInfo.analysisDate;
		
		
		add(this.buildPropertyDropdown("selectedMode", new PropertyModel<String> (analysisInfo, "selectedMode"), availableModes, 2));
		add(dateSelectionField = this.grabDateTextField("analysisDate", "analysisDate"));
		
		fieldsArray = new ArrayList<TextField>();
		
	
		for (int i = 0; i< analysisInfo.getInfoFields().size(); i++)
			{
			for (int j  = 0; j < 2; j++)
				{
				String fieldTag = analysisInfo.getInfoFields().get(i).getFieldTag();
				if ("instrumentName".equals(fieldTag))
					{
					if (j == 0)
						add(this.buildPropertyDropdown("instrumentName." + j, new PropertyModel<String>(analysisInfo.getInfoFields().get(i), "fieldValues." + j ), instrumentNames, j));
					else 
						add(this.buildPropertyDropdown("instrumentName." + j, new PropertyModel<String>(analysisInfo.getInfoFields().get(i), "fieldValues." + j ), instrumentNames, j));
					}		
				else if ("analysisType".equals(fieldTag))
					add(this.buildPropertyDropdown("analysisType." + j, new PropertyModel<String>(analysisInfo.getInfoFields().get(i), "fieldValues." + j) , analysisTypes, j));
				else if ("acquisitionDateAsString".equals(fieldTag))
					{
					
					if (j == 0)
						add(dateForAnalysisInfoField_0 = buildDateTextFields(i, j));
					else if (j == 1)
						add(dateForAnalysisInfoField_1 = buildDateTextFields(i, j));
					}
				else if ("rawDataDirectory".equals(fieldTag))
					{
					if (j == 0)
						{
						add(this.rawDataDirectory_0 = buildTextFields(i, j));
						fieldsArray.add(rawDataDirectory_0);
						}
					else if (j == 1)
						{
						add(this.rawDataDirectory_1 = buildTextFields(i, j));
						fieldsArray.add(rawDataDirectory_1);
						}
					}
				else if ("processedDataDirectory".equals(fieldTag))
					{
					if (j == 0)
						{
						add(this.processedDataDirectory_0 = buildTextFields(i, j));
						fieldsArray.add(processedDataDirectory_0);
						}
					else if (j == 1)
						{
						add(this.processedDataDirectory_1 = buildTextFields(i, j));
						fieldsArray.add(processedDataDirectory_1);
						}	
					}
				else
					{
					TextField fld;
					
					add(fld = buildTextFields(i, j));
					fieldsArray.add(fld);
					}
				}
			}
		
		//String newDate = dateSelectionField.grabValueAsString();
		
	//	analysisInfo.setAnalysisDates(newDate, 0);
	//	analysisInfo.setAnalysisDates(newDate, 1);
	//	setAnalysisDate(dateSelectionField.grabValueAsDate());
		
	//	updateDirectoryInfo(dateSelectionField.grabValueAsDate(), 0, "Neg");
	//	updateDirectoryInfo(dateSelectionField.grabValueAsDate(), 1, "Pos");
		
		//String fullName = getOutputFileName();
		//METWorksDataDownload resource = new METWorksDataDownload("downloadData", new PropertyModel(analysisInfo, "infoFields"), fullName +".tsv", null);
		//resource.getResource().setOutfileName(new PropertyModel<String>(this, "outputFileName"));
		
		//add(resource.getResourceLink());
		
		//add(new AjaxBackButton("backButton", backPage));
		//containerDefault.setOutputMarkupId(true);
		//add(containerDefault);

		}

	
	public String getOutputFileName()
		{
		String dateStr = analysisDate == null ? "" : DateUtils.dateAsString(analysisDate);
		return "DrccAnalysisInfo_" + this.expId + "_" + dateStr + "_" + this.getSelectedMode() + ".tsv";
		}
	
	private TextField buildDateTextFields(int i, final int j)
		{
		TextField fld = new TextField(analysisInfo.getInfoFields().get(i).getFieldTag() + "." + j, 
				new PropertyModel <String>(analysisInfo, "analysisDates." + j))
			{
			@Override
			public boolean isVisible()
				{
				return ((  j == 0  && !("Positive".equals(getSelectedMode())) )
					|| (j == 1 && !("Negative".equals(getSelectedMode())) )
					|| (j == 2));
				}
		 	};
	
		fld.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForTextField", fld,  i, j));
			
		return fld;
		}
	
	private TextField buildTextFields(int i, final int j)
		{
		TextField fld = new TextField(analysisInfo.getInfoFields().get(i).getFieldTag() + "." + j, 
				new PropertyModel <String>(analysisInfo.infoFields.get(i), "fieldValues." + j))
			{
			@Override
			public boolean isVisible()
				{
				return ((  j == 0  && !("Positive".equals(getSelectedMode())) )
						|| (j == 1 && !("Negative".equals(getSelectedMode())) ));
							
				}
			};
		
		fld.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForTextField", fld,  i, j));
		
		return fld;
		}
	
	
	private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(final String event, final 
			String response, final Component field, final int i, final int j)
		{
		return new AjaxFormComponentUpdatingBehavior(event)
	        {
	        @Override
	        protected void onUpdate(AjaxRequestTarget target)
	        	{
	        	if (i >= 0)
	        		{
	        		target.add(field);
	        		analysisInfo.infoFields.get(i).setFieldValues(j, field.getDefaultModelObjectAsString());
	        		}
	        	
	        	analysisInfo.setViewNegativeMode(!("Positive".equals(selectedMode)));
	        	analysisInfo.setViewPositiveMode(!("Negative".equals(selectedMode)));
	        	target.add(rawDataDirectory_0);
	        	target.add(rawDataDirectory_1);
	        	//target.add(containerDefault);
	        	for (int i = 0; i < fieldsArray.size(); i++)
	        		target.add(fieldsArray.get(i));
	        	}
	        };
		}

	private AjaxFormComponentUpdatingBehavior buildModeChangeUpdateBehavior(final String event, final 
			String response)
		{
		return new AjaxFormComponentUpdatingBehavior(event)
	        {
	        @Override
	        protected void onUpdate(AjaxRequestTarget target)
	        	{
	        	analysisInfo.setViewNegativeMode(!("Positive".equals(selectedMode)));
	        	analysisInfo.setViewPositiveMode(!("Negative".equals(selectedMode)));
	        	}
	        };
		}
	
	private DropDownChoice buildPropertyDropdown(String id, PropertyModel<String> model, List<String>choices, final int j)
		{
		DropDownChoice drp = new DropDownChoice(id, model, choices)
		 	{ // issue 464
		//	@Override
		//	protected boolean wantOnSelectionChangedNotifications() 
			//	{
	        //    return true;
			//	}
			
			@Override
			public boolean isVisible()
				{
				analysisInfo.setViewNegativeMode(!("Positive".equals(selectedMode)));
	        	analysisInfo.setViewPositiveMode(!("Negative".equals(selectedMode)));
	        	
				return ((  j == 0  && !("Positive".equals(getSelectedMode())) )
					|| (j == 1 && !("Negative".equals(getSelectedMode())) )
					|| (j == 2));
				}
			@Override // issue 464
			public MarkupContainer setDefaultModel(IModel model) 
			    {
				// TODO Auto-generated method stub
				return this;
			    }
		 	};
		//if (id.equals("selectedMode"))
		//	drp.add(this.buildModeChangeUpdateBehavior("change", "updateForMode"));

		//drp.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForDrop", null, -1, j));
		return drp;
		}
	
	
/*	private DropDownChoice buildModeDropdown(String id, PropertyModel<String> model, List<String>choices, int i, int j)
		{
		DropDownChoice drp = new DropDownChoice(id, model, choices)
		 	{
			@Override
			protected boolean wantOnSelectionChangedNotifications() 
				{
	            return true;
				}
			};					
					
		drp.add(this.buildModeChangeUpdateBehavior("change", "updateForMode"));
		return drp;
		}
	*/
	private METWorksAjaxUpdatingDateTextField grabDateTextField(String id, String property)
		{
		return new METWorksAjaxUpdatingDateTextField(id, new PropertyModel<Date>(this, property), "change")
			{
			@Override
			protected void onUpdate(AjaxRequestTarget target) 
				{				
				//String newDate = dateSelectionField.grabValueAsString();
				
			//	analysisInfo.setAnalysisDates(newDate, 0);
			//	analysisInfo.setAnalysisDates(newDate, 1);
			//	setAnalysisDate(dateSelectionField.grabValueAsDate());
				
			//	updateDirectoryInfo(dateSelectionField.grabValueAsDate(), 0, "Neg");
			//	updateDirectoryInfo(dateSelectionField.grabValueAsDate(), 1, "Pos");
					
				target.add(processedDataDirectory_0);
				target.add(processedDataDirectory_1);

				target.add(rawDataDirectory_0);
				target.add(rawDataDirectory_1);

				target.add(dateForAnalysisInfoField_0);
				target.add(dateForAnalysisInfoField_1);
				}
			};
		}

	

	
	public Date getAnalysisDate()
		{
		return this.analysisDate;
		}
	
	
	public void setAnalysisDate(Date date)
		{
		this.analysisDate = date;
		}
	
	public String getSelectedMode()
		{
		return selectedMode;
		}
	
	public void setSelectedMode(String mode)
		{
		selectedMode = mode;
		}
	
	}







/*
package edu.umich.metworks.web.panels.analysis.drcc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import edu.emory.mathcs.backport.java.util.Arrays;
import edu.umich.metworks.lims.interfaces.IWriteConvertable;
import edu.umich.metworks.web.utils.InfoLine;
import edu.umich.metworks.web.utils.METWorksDataDownload;
import edu.umich.metworks.web.utils.widget.AjaxBackButton;

public class DrccAnalysisPanel extends Panel 
	{
	String expId;

	DrccAnalysisInfo analysisInfo = new DrccAnalysisInfo();
	List<String> propertyList = new ArrayList<String>(); 
	List<IWriteConvertable> infoLines = new ArrayList<IWriteConvertable>();
	List<String> absciexInstruments = Arrays.asList(new String [] {"IN0024 (LIPIDS)", "IN0027 (LIPIDS2)"});
	

	public DrccAnalysisPanel(String id, String selectedExperiment, WebPage backPage) 
		{
		super(id);
	
		expId = selectedExperiment; 
		
		Field [] fields = DrccAnalysisInfo.class.getFields();
		for (int i = 0; i < fields.length; i++)
			propertyList.add(fields[i].getName());
		addPropertyFields(fields);
		
		String fullName = "DrccAnalysisInfo_" + this.expId;
		METWorksDataDownload resource = new METWorksDataDownload("downloadData", infoLines, fullName +".tsv", null);
		add(resource.getResourceLink());
		
		add(new AjaxBackButton("backButton", backPage));
		}

	
	private void addPropertyFields(Field [] fields)
		{
		PropertyModel<String> model = null; 
		
		for(int i = 0; i < propertyList.size(); i++)
			{
			String fieldLabel = "";
			try {  fieldLabel = ((DrccInfoField) fields[i].get(analysisInfo)).getFieldLabel();  }
			catch (Exception e) {}
			
			addPropertyField(fieldLabel, propertyList.get(i), model);
			}
		}

	private void addPropertyField(String fieldLabelTitle, String propertyName, PropertyModel <String> model)
		{
		model = new PropertyModel<String>(analysisInfo, propertyName);
		
		infoLines.add(new InfoLine(fieldLabelTitle, model));
		if (!propertyName.equals("instrumentName"))
			add(new TextField(propertyName, model));
		else
			add(buildInstrumentDropdown(propertyName, model));
		}
	

	// Instrument
	private DropDownChoicee buildInstrumentDropdown(String id, PropertyModel<String> model)
		{
		DropDownChoice drp = new DropDownChoice(id, model, absciexInstruments)
			{
			@Override
			protected boolean wantOnSelectionChangedNotifications() 
				{
	            return true;
				}
			};					
					
		//drp.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForInstrument"));
		return drp;
		}
	}
	
*/
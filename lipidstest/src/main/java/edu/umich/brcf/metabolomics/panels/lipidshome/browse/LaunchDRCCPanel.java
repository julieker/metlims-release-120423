///////////////////////////////////////
// LaunchDRCCPanel.java
// Written by Jan Wigginton June 2015
///////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.lipidshome.browse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.emory.mathcs.backport.java.util.Arrays;
import edu.umich.brcf.metabolomics.layers.service.GeneratedWorklistService;
import edu.umich.brcf.metabolomics.panels.lipidshome.drcc.DrccMainPage;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.model.ExperimentListModel;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;
import edu.umich.brcf.shared.util.widgets.METWorksAjaxUpdatingDateTextField;


public class LaunchDRCCPanel extends Panel
	{
	@SpringBean
	private ExperimentService experimentService;
	
	@SpringBean
	private GeneratedWorklistService worklistService;
	
	WebMarkupContainer container;
	DropDownChoice<String> editExperimentDrop, runDateDrop;
	IndicatingAjaxButton editButton;
	
	Date analysisDate = null;
	String analysisDateStr, analysisDateFormat = "MM/dd/yyyy";
	List <String> availableModes = Arrays.asList(new String [] {"Positive", "Negative", "Both"});
	String selectedExperiment = null, selectedMode = null;
	
	List<String> expRunDates = new ArrayList<String>();
	
	public LaunchDRCCPanel(String id) 
		{
		super(id);
		
		LaunchDRCCForm lde = new LaunchDRCCForm("launchDrccForm");
		lde.setMultiPart(true);
		add(lde);
		}
	
	
	public final class LaunchDRCCForm extends Form 
		{	
		METWorksAjaxUpdatingDateTextField dateSelectionField;
		
		LaunchDRCCForm(String id)
			{
			super(id);
		
			container = new WebMarkupContainer("container");
				
			add(buildModeDrop("modeDrop", new PropertyModel<String>(this, "selectedMode"), availableModes));
			add(dateSelectionField = this.grabDateTextField("analysisDate", "analysisDate"));
			//add(runDateDrop = buildRunDateDrop("runDateDrop", "analysisDateStr"));
			add(editExperimentDrop = buildEditExperimentDrop("editExperimentDrop", "selectedExperiment"));
			add(editButton = buildEditButton());
			
			container.setOutputMarkupId(true);
			add(container);
			}		
		
		
		private DropDownChoice<String> buildRunDateDrop(String id, String property)
			{
			runDateDrop =  new DropDownChoice(id,  new PropertyModel(this, property), new LoadableDetachableModel<List<String>>() 
				{
            	@Override
            	protected List<String> load() 
            		{ 
            		if (expRunDates == null)
            			return worklistService.loadRunDatesByExpIdAndAssayId(selectedExperiment, "A004");
            		
            		return expRunDates;
            		}
				})
				{
				@Override
				public boolean isEnabled() { return (getSelectedExperiment() != null);  }
				};
			
			runDateDrop.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForRunDateDrop"));			
			runDateDrop.setOutputMarkupId(true);
			return runDateDrop;
			}
		
		
		private IndicatingAjaxButton buildEditButton()
			{
			return new IndicatingAjaxButton("editButton")
				{
				public boolean isEnabled() { return (getSelectedExperiment() != null && analysisDate != null);  }
	
				@Override
				protected void onSubmit(AjaxRequestTarget arg0) // issue 464
					{
					try
						{
						setOutputMarkupId(true);
						setResponsePage(new DrccMainPage("drccMainPanel", (WebPage) getPage(), selectedExperiment, selectedMode, analysisDate));
						}
					catch (Exception e)  {   }
					}
				};
			}
		
		
		
		private METWorksAjaxUpdatingDateTextField grabDateTextField(String id, String property)
			{
			METWorksAjaxUpdatingDateTextField dateFld =  new METWorksAjaxUpdatingDateTextField(id, new PropertyModel<Date>(this, property), "change")
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target) 
					{				
					setAnalysisDate(dateSelectionField.grabValueAsDate());
					target.add(editButton);
					}
				};
				
			dateFld.add(buildStandardFormComponentUpdateBehavior("change", "updateForDateChange"));
			dateFld.setOutputMarkupId(true);
			return dateFld;
			}
		
		
		private DropDownChoice buildModeDrop(String id, PropertyModel<String> model, List<String>choices)
			{
			DropDownChoice drp = new DropDownChoice(id, model, choices);
			drp.add(buildStandardFormComponentUpdateBehavior("change", "updateForModeChange"));
			return drp;
			}

		
		private DropDownChoice buildEditExperimentDrop(final String id, String propertyName)
			{
			editExperimentDrop =  new DropDownChoice(id,  new PropertyModel(this, propertyName), 
				new ExperimentListModel("absciex", experimentService, false));
				
			editExperimentDrop.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForEditExperimentDrop"));			
			
			return editExperimentDrop;
			}
		
	
		private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(final String event,  final String response)
			{
			return new AjaxFormComponentUpdatingBehavior(event)
		        {
		        @Override
		        protected void onUpdate(AjaxRequestTarget target)
		        	{
		        	switch (response)
			        	{
			        	case "updateForEditExperimentDrop" :
			        		expRunDates = worklistService.loadRunDatesByExpIdAndAssayId(selectedExperiment, "A004");
				        	dateSelectionField.setFieldVisible(true); //expRunDates != null && expRunDates.size() == 0);
				        	container.setVisible(expRunDates.size() == 0);
			        		target.add(editButton);
				        	//target.add(runDateDrop);
				        	target.add(dateSelectionField);
				        	target.add(container);
				        	break;
				        	
			        	case "updateForRunDateDrop" :
			        		setAnalysisDateStr(analysisDateStr);
			        		target.add(dateSelectionField);
			        		break;
			        		
			        
			        	case "updateForModeChange" :
			        	case "updateForDatedChange" :
			        		setAnalysisDate(dateSelectionField.grabValueAsDate());
			        		target.add(editButton);
			        		break;
				        }
		        	target.add(container);
		        	}
		        };
			}

		public Date getAnalysisDate() {	 return analysisDate;  }
		public void setAnalysisDate(Date dt) 
			{ 
			analysisDate = dt; 
			analysisDateStr = DateUtils.dateAsString(analysisDate, analysisDateFormat);
			}
		
		public String getSelectedExperiment() { return selectedExperiment; }
		public void setSelectedExperiment(String ee) { selectedExperiment = ee; }
		
		public String getSelectedMode() { return selectedMode; }
		public void setSelectedMode(String mode) { selectedMode = mode; }
		
		public String getAnalysisDateStr() { return analysisDateStr; }
		public void setAnalysisDateStr(String dtStr) 
			{ 
			analysisDateStr = dtStr;
			analysisDate = DateUtils.dateFromDateStr(analysisDateStr, analysisDateFormat);
			}
		}
	}
		
		
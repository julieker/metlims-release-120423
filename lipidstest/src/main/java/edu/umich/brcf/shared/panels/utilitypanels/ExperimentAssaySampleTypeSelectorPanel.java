////////////////////////////////////////////////////
// ExperimentAssaySampleTypeSelectorPanel.java
// Written by Jan Wigginton, Jun 4, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.panels.utilitypanels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.mysql.jdbc.StringUtils;

import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.StandardProtocolService;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.widgets.METWorksAjaxUpdatingDateTextField;



public abstract class ExperimentAssaySampleTypeSelectorPanel extends Panel
	{
	@SpringBean
	private ExperimentService experimentService;
	
	@SpringBean
	private AssayService assayService;
	
	@SpringBean 
	StandardProtocolService standardProtocolService;
	
	
	public ExperimentAssaySampleTypeSelectorPanel(String id) 
		{
		super(id);
		
		ExperimentAssaySampleTypeSelectorForm lde = new ExperimentAssaySampleTypeSelectorForm("launchDrccForm");
		lde.setMultiPart(true);	
		add(lde);
		}

	
	public class ExperimentAssaySampleTypeSelectorForm extends Form 
		{	
		DropDownChoice<String> 	platformDrop, experimentDrop, assayDrop, sampleTypeDrop;
		IndicatingAjaxButton 	searchButton;
		
		List<String> availablePlatforms = Arrays.asList(new String [] {"Agilent", "ABSciex"});		
		List <String> absciexExperiments = experimentService.allExpIdsForAbsciex();
		List <String> agilentExperiments = experimentService.allExpIdsForAgilent(); 
		List<String> availableSampleTypes = new ArrayList<String>();
		List <String> availableAssays = new ArrayList<String>();			
		
		String selectedExperiment = null, selectedAssay = null, selectedPlatform = "Agilent", selectedSampleType = null;
		private Date editDate = new Date();
		
		METWorksAjaxUpdatingDateTextField editDateFld;
	
		
		public ExperimentAssaySampleTypeSelectorForm(String id)
			{
			super(id);
			
			add(platformDrop = buildPlatformDropdown("platformDropdown", "selectedPlatform"));
			add(experimentDrop = buildExperimentDropdown("experimentDropdown", "selectedExperiment"));
			add(assayDrop = buildAssayDropdown("assayDropdown", "selectedAssay"));
			add(sampleTypeDrop = buildSampleTypeDropdown("sampleTypeDropdown", "selectedSampleType"));
			add(searchButton = buildSearchButton());
			}		
		
		
		private IndicatingAjaxButton buildSearchButton()
			{
			return new IndicatingAjaxButton("searchButton")
				{
				public boolean isEnabled()
					{
					return (drpIsSelected(getSelectedExperiment()) && drpIsSelected(getSelectedPlatform()) 
					    && drpIsSelected(getSelectedAssay()) && drpIsSelected(getSelectedSampleType())   );
					}
	
				@Override
				protected void onSubmit(AjaxRequestTarget arg0) // issue 464
					{
					try
						{
						setOutputMarkupId(true);
						String assayId = StringParser.parseId(selectedAssay);
						WebPage responsePage = getResponsePage("worklistLookUp", (WebPage) getPage(), selectedExperiment, assayId, selectedSampleType);
						setResponsePage(responsePage);  
						}
					catch (Exception e)  { arg0.appendJavaScript("alert('Experiment " + selectedExperiment + " has missing sample information and cannot be accessed at this time.');"); }
					}
				};
			}
				
		
		private boolean drpIsSelected(String drpValue)
			{
			return (!StringUtils.isNullOrEmpty(drpValue) && !drpValue.equals("Choose One"));
			}
					
		
		private DropDownChoice buildSampleTypeDropdown(final String id, String propertyName)
			{
			DropDownChoice selectedSampleTypeDrop =  new DropDownChoice(id,  new PropertyModel(this, propertyName), 
				new LoadableDetachableModel<List<String>>() 
				{
	        	@Override
	        	protected List<String> load()  {  return availableSampleTypes; }
	        	})
					{
					@Override
					public boolean isEnabled() { return true; } //drpIsSelected(getSelectedPlatform()); }
					};
			
			selectedSampleTypeDrop.setOutputMarkupId(true);
			selectedSampleTypeDrop.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForSampleTypeDrop"));			
			
			return selectedSampleTypeDrop;
			}
		
		
		private DropDownChoice buildExperimentDropdown(final String id, String propertyName)
			{
			DropDownChoice selectedExperimentDrop =  new DropDownChoice(id,  new PropertyModel(this, propertyName), 
				new LoadableDetachableModel<List<String>>() 
					{
		        	@Override
		        	protected List<String> load()  {  return getSelectedPlatform().equals("absciex") ? absciexExperiments : agilentExperiments;  }
		        	})
				{
				@Override
				public boolean isEnabled() { return true; }
				};
			
			selectedExperimentDrop.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForExperimentDrop"));			
			return selectedExperimentDrop;
			}
		
		
		private DropDownChoice buildPlatformDropdown(final String id, String propertyName)
			{
			DropDownChoice platformDrop =  new DropDownChoice(id,  new PropertyModel<String>(this, propertyName), 
				availablePlatforms);
				
			platformDrop.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForPlatformDrop"));			
			
			return platformDrop;
			}
		
		
		private DropDownChoice buildAssayDropdown(final String id, String propertyName)
			{
			DropDownChoice drp = new DropDownChoice(id,  new PropertyModel(this, propertyName),  new LoadableDetachableModel<List<String>>() 
					{
	            	@Override
	            	protected List<String> load() 
	            		{ 
	            		if (availableAssays != null)
	            			return availableAssays;
	            		
	            		return new ArrayList<String>();
	            		}
					})
				{
				@Override
				public boolean isEnabled()
					{
					return (drpIsSelected(getSelectedPlatform()) && drpIsSelected(getSelectedExperiment()));
					}
				};
			
			drp.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForAssayDrop"));			
			
			return drp;
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
			        	case "updateForExperimentDrop" :
			        		try
				        		{
				        		if (StringUtils.isNullOrEmpty(selectedExperiment))
				        			setSelectedPlatform(null);				        	
				        		availableAssays = assayService.allAssayNamesForPlatformAndExpId(selectedPlatform, selectedExperiment);
				        		setSelectedAssay((availableAssays != null && availableAssays.size() == 1 ? availableAssays.get(0) : null));
				        		if (selectedAssay != null)
				        			availableSampleTypes = standardProtocolService.getSampleTypesForStandardProtocolsByAssayId(StringParser.parseId(selectedAssay));
				        		else
				        			availableSampleTypes = new ArrayList<String>();
				        	    if (availableSampleTypes != null && availableSampleTypes.size() >= 1) // issue 233
				        		    setSelectedSampleType(availableSampleTypes.get(0));
				        	    else if (availableSampleTypes.size() == 0)
				        	        setSelectedSampleType(""); // 233				        		
				        		}
				        	catch (Exception e) { target.appendJavaScript("alert('Experiment " + selectedExperiment + " has missing sample information and cannot be accessed at this time');"); }
			        		
			        		break;
			     
			        	case  "updateForPlatformDrop" :
			        		setSelectedExperiment(null);
			        		setSelectedAssay(null);
			        		setSelectedSampleType(null);
			        		break;
			        		
			        	case "updateForAssayDrop" :
			        	    String assayId = StringParser.parseId(selectedAssay);
			        		availableSampleTypes = standardProtocolService.getSampleTypesForStandardProtocolsByAssayId(assayId);
			        		setSelectedSampleType(null);
			        		break;
			        	
			        	case "updateForSampleTypeDrop" :
			        		
				        default:	
			            	break;
				        }
		        	
		        	update(target);
		        	}
		        };
			}
		
		
		private void update(AjaxRequestTarget target)
			{
			target.add(searchButton);
        	target.add(experimentDrop);
        	target.add(assayDrop);
        	target.add(sampleTypeDrop);
			}
			
		
		public Date getEditDate()
			{
			return editDate;
			}
		
		public void setEditDate(Date dt)
			{
			editDate = dt;
			}
	
		public String getSelectedPlatform() 
			{
			return selectedPlatform == null ? "" : selectedPlatform.toLowerCase();
			}

		public void setSelectedPlatform(String selectedPlatform) 
			{
			this.selectedPlatform = selectedPlatform;
			}

		public String getSelectedExperiment() 
			{
			return selectedExperiment;
			}

		public void setSelectedExperiment(String selectedExperiment) 
			{
			this.selectedExperiment = selectedExperiment;
			}

		public String getSelectedAssay() 
			{
			return selectedAssay;
			}

		public void setSelectedAssay(String selectedAssay) 
			{
			this.selectedAssay = selectedAssay;
			}


		public String getSelectedSampleType()
			{
			return selectedSampleType;
			}


		public void setSelectedSampleType(String selectedSampleType)
			{
			this.selectedSampleType = selectedSampleType;
			}
		}
	
	public abstract WebPage getResponsePage(String id, WebPage backPage, String selectedExperiment, String assayId, String sampleType);
	}	
		
			
			

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  ExperimentAssaySelectorPanel.java
//  Written by Jan Wigginton
//  July 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.widgets.METWorksAjaxUpdatingDateTextField;



public abstract class ExperimentAssaySelectorPanel extends Panel
	{
	@SpringBean
	private ExperimentService experimentService;
	
	@SpringBean
	private AssayService assayService;
	
	
	public ExperimentAssaySelectorPanel(String id) 
		{
		super(id);
		
		ExperimentAssaySelectorForm lde = new ExperimentAssaySelectorForm("launchDrccForm");
		lde.setMultiPart(true);	
		add(lde);
		}

	
	public class ExperimentAssaySelectorForm extends Form 
		{	
		DropDownChoice<String> 	platformDrop, experimentDrop, assayDrop;
		IndicatingAjaxButton 	searchButton;
		
		List<String> availablePlatforms = Arrays.asList(new String [] {"Agilent", "ABSciex"});		
		List <String> absciexExperiments = experimentService.allExpIdsForAbsciex();
		List <String> agilentExperiments = experimentService.allExpIdsForAgilent(); 
	
		String selectedExperiment = null, selectedAssay = null, selectedPlatform = "Agilent";
		private Date editDate = new Date();
		
		METWorksAjaxUpdatingDateTextField editDateFld;
		
		List <String> availableAssays = new ArrayList<String>();			
		
		public ExperimentAssaySelectorForm(String id)
			{
			super(id);
			
			//selectedPlatform = null;
			add(platformDrop = buildPlatformDropdown("platformDropdown", "selectedPlatform"));
			add(experimentDrop = buildExperimentDropdown("experimentDropdown", "selectedExperiment"));
			add(assayDrop = buildAssayDropdown("assayDropdown", "selectedAssay"));

			add(searchButton = buildSearchButton());
			}		
		

		private IndicatingAjaxButton buildSearchButton()
			{
			return new IndicatingAjaxButton("searchButton")
				{
				public boolean isEnabled()
					{
					return (drpIsSelected(getSelectedExperiment()) && drpIsSelected(getSelectedPlatform()) && drpIsSelected(getSelectedAssay()));
					}
	
				@Override
				protected void onSubmit(AjaxRequestTarget arg0) // issue 464
					{
					try
						{
						setOutputMarkupId(true);
						String assayId = StringParser.parseId(selectedAssay);
						WebPage responsePage = getResponsePage("worklistLookUp", (WebPage) getPage(), selectedExperiment, assayId);
						setResponsePage(responsePage);  
						}
					catch (Exception e) 
						{
						arg0.appendJavaScript("alert('Experiment " + selectedExperiment + " has missing sample information and cannot be accessed at this time.');");
						}
					setSelectedExperiment("");
					setSelectedAssay("");
					setSelectedPlatform("");
					}
				};
			}
		
			private boolean drpIsSelected(String drpValue)
				{
				return (drpValue != null && !drpValue.trim().equals("") && !drpValue.equals("Choose One"));
				}
			
			
			private DropDownChoice buildExperimentDropdown(final String id, String propertyName)
				{
				DropDownChoice selectedExperimentDrop =  new DropDownChoice(id,  new PropertyModel(this, propertyName), 
					new LoadableDetachableModel<List<String>>() 
						{
			        	@Override
			        	protected List<String> load() 
			        		{ 
			        		return getSelectedPlatform().equals("absciex") ? absciexExperiments : agilentExperiments;  
			        		}
			        		
						})
					{
					@Override
					public boolean isEnabled() { return true; } //drpIsSelected(getSelectedPlatform()); }
					};
				
				selectedExperimentDrop.setEnabled(false);
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
				
				drp.setEnabled(false);
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
					        			{
					        			setSelectedPlatform(null);
					        			target.add(platformDrop);
					        			}
					        	
					        		availableAssays = assayService.allAssayNamesForPlatformAndExpId(selectedPlatform, selectedExperiment);
					        		setSelectedAssay((availableAssays.size() == 1 ? availableAssays.get(0) : null));
					        		}
					        	catch (Exception e)
					        		{
					        		target.appendJavaScript("alert('Experiment " + selectedExperiment + " has missing sample information and cannot be accessed at this time');");
					        		}
				        		
				        		update(target);
				        		break;
				     
				        	case  "updateForPlatformDrop" :
				        		setSelectedExperiment(null);
				        		setSelectedAssay(null);
				        		
					        default:	
				            	update(target);
					        	break;
					        }
			        	}
			        };
				}
			
			
			private void update(AjaxRequestTarget target)
				{
				target.add(searchButton);
	        	target.add(experimentDrop);
	        	target.add(assayDrop);
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
			}
		
		public abstract WebPage getResponsePage(String id, WebPage backPage, String selectedExperiment, String assayId);
		}	
		
			
			

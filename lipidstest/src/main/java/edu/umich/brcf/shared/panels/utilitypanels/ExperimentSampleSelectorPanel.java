///////////////////////////////////////
//ExperimentAssaySelectorPanel.java
//Written by Jan Wigginton September 2015
///////////////////////////////////////

package edu.umich.brcf.shared.panels.utilitypanels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.model.ExperimentListModel;


public abstract class ExperimentSampleSelectorPanel extends Panel
	{
	@SpringBean
	private ExperimentService experimentService;
	
	@SpringBean 
	private SampleService sampleService;
	
	DropDownChoice<String> selectedSampleDrop, selectedExperimentDrop;
	protected String selectedSample= null;
	
	protected String selectedExperiment = null;
	IndicatingAjaxButton editButton;
	String buttonLabel = "Edit Data";
	
	List<String> availableSamples = new ArrayList<String>();
	
	public ExperimentSampleSelectorPanel(String id) 
		{
		super(id);
		
		add(new FeedbackPanel("feedback"));
		
		ExperimentSampleSelectorForm lde = new ExperimentSampleSelectorForm("sampleSelectorForm");
		lde.setMultiPart(true);
		add(lde);
		}
		
		
	public class ExperimentSampleSelectorForm extends Form 
		{	
		ExperimentSampleSelectorForm(String id)
			{
			super(id);
			
			add(selectedExperimentDrop = buildExperimentDropdown("selectedExperimentDrop", "selectedExperiment"));
			add(selectedSampleDrop = buildSampleDropdown("selectedSampleDrop", "selectedSample"));
			add(editButton = buildEditButton());
			}
		
			
		private DropDownChoice buildExperimentDropdown(final String id, String propertyName)
			{
			selectedExperimentDrop =  new DropDownChoice(id,  new PropertyModel(this, "selectedExperiment"), 
					new ExperimentListModel("both", experimentService, false));
			
			selectedExperimentDrop.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForExperimentDrop"));			
			
			return selectedExperimentDrop;
			}
		
		private DropDownChoice buildSampleDropdown(final String id, String propertyName)
			{
			DropDownChoice drp = new DropDownChoice(id,  new PropertyModel(this, propertyName), 
				new LoadableDetachableModel<List<String>>() 
					{
					@Override
					protected List<String> load() 
						{ 
						if (availableSamples != null)
							return availableSamples;
						
						return new ArrayList<String>();
						}
					})
			{
			public boolean isEnabled() { return isExperimentSelected(); }
			};
		
			drp.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForAssayDrop"));			
		
		return drp;
		}
	
		
	private IndicatingAjaxButton buildEditButton()
		{
		return new IndicatingAjaxButton("editButton")
			{
			@Override
			public boolean isEnabled()
				{
				return isExperimentSelected() && isAssaySelected();
				}
				
			@Override
			protected void onComponentTag(ComponentTag tag)
				{
				super.onComponentTag(tag);
				String label = getButtonLabel();
				tag.put("value", label);
				}
			
			@Override
			protected void onSubmit(AjaxRequestTarget target) // issue 464
				{
				try
					{
					setOutputMarkupId(true);
					doSubmit(selectedSample, selectedExperiment, target);
					}
				catch (Exception e) 
					{
					}
					}
				};
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
						case "updateForAssayDrop" :
							target.add(editButton);
							break;
						
						case "updateForExperimentDrop" :
							availableSamples = sampleService.sampleIdsForExpId(selectedExperiment);
							target.add(selectedSampleDrop);
							target.add(editButton);
							break;	
						}	
					}
				};
			}
			
		
		public String getSelectedSample()
			{
			return selectedSample;
			}
			
		
		public void setSelectedSample(String ee)
			{
			selectedSample = ee;
			}
			
		
		public String getSelectedExperiment()
			{
			return selectedExperiment;
			}
			
		
		public void setSelectedExperiment(String ee)
			{
			selectedExperiment = ee;
			}
		}
			
		public String getButtonLabel()
			{
			return buttonLabel;
			}
			
		public void setButtonLabel(String label)
			{
			buttonLabel = label;
			}
			
		public boolean isExperimentSelected()
			{
			return (selectedExperiment != null && !("".equals(selectedExperiment.trim())));
			}
			
		public boolean isAssaySelected()
			{
			return (selectedSample != null && !("".equals(selectedSample.trim())));
			}
			
		public abstract void doSubmit(String selectedSample, String selectedExperiment, AjaxRequestTarget target);
		}

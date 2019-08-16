////////////////////////////////////////////////////
// ExperimentSampleSearchPanel.java
// Written by Jan Wigginton, Mar 28, 2017
////////////////////////////////////////////////////
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
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.model.ExperimentListModel;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;
import edu.umich.brcf.shared.util.widgets.AjaxExperimentSampleField;
import edu.umich.brcf.shared.util.widgets.AjaxSampleField;



public abstract class ExperimentSampleSearchPanel extends AccessLimitedPanel
	{
	@SpringBean
	private ExperimentService experimentService;
	
	@SpringBean 
	SampleService mchearSampleService;
	
	protected String selectedSample = "", buttonLabel = "Edit Data", selectedExperiment = null;
	private List<String> availableSamples = new ArrayList<String>();
	
	
	public ExperimentSampleSearchPanel(String id) 
		{
		this(id, false);
		}
	
	
	public ExperimentSampleSearchPanel(String id, Boolean useEpiSamples) 
		{
		super(id);
		
		add(new FeedbackPanel("feedback"));
		
		ExperimentSampleSelectorForm lde = new ExperimentSampleSelectorForm("sampleSelectorForm");
		lde.setMultiPart(true);
		add(lde);
		}
	
	
	public class ExperimentSampleSelectorForm extends Form 
		{	
		private DropDownChoice<String>  selectedExperimentDrop;
		private AjaxExperimentSampleField sampleSearchField;
		private IndicatingAjaxButton editButton;
		
		public ExperimentSampleSelectorForm(String id)
			{
			super(id);
			add(selectedExperimentDrop = buildExperimentDropdown("selectedExperimentDrop", "selectedExperiment"));
			
			add(sampleSearchField = buildSampleSearchField("sampleSearchBox"));
			
			add(editButton = buildEditButton());
			}
		
	
		
		AjaxExperimentSampleField buildSampleSearchField(String id)
			{
			AjaxExperimentSampleField fld = new AjaxExperimentSampleField(id)
				{
				@Override
				public boolean isEnabled() { return isExperimentSelected(); } 
				};
				
			fld.setModel(new PropertyModel<String>(this, "selectedSample"));
			fld.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForAssayDrop"));		
			return fld;
			}
		
		
		private DropDownChoice<String> buildExperimentDropdown(final String id, String propertyName)
			{
			selectedExperimentDrop =  new DropDownChoice<String>(id,  new PropertyModel<String>(this, "selectedExperiment"),  
					new ExperimentListModel("both", experimentService, false, false));
			selectedExperimentDrop.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForExperimentDrop"));			
			return selectedExperimentDrop;
			}
		
		
		private DropDownChoice<String> buildSampleDropdown(final String id, String propertyName)
			{
			DropDownChoice<String> drp = new DropDownChoice<String>(id,  new PropertyModel<String>(this, propertyName), 
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
				@Override
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
				public boolean isEnabled() {  return isExperimentSelected() && isAssaySelected(); } 
		
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
						String selectedId = StringParser.parseName(selectedSample);
						doSubmit(selectedId, getSelectedExperimentId(), target);
						}
					catch (Exception e)  {  }
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
							//availableSamples = mchearSampleService.bothSampleIdsForExpId(getSelectedExperimentId());
							sampleSearchField.setExpId(StringParser.parseName(getSelectedExperiment()));
							
							target.add(sampleSearchField);
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
		
		public String getSelectedExperimentId()
			{
			return StringParser.parseName(selectedExperiment);
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
		return !StringUtils.isNullOrEmpty(selectedExperiment);
		}
	
	public boolean isAssaySelected()
		{
		return !StringUtils.isNullOrEmpty(selectedSample); //
		}
	
	public abstract void doSubmit(String selectedSample, String selectedExperiment, AjaxRequestTarget target);
	}

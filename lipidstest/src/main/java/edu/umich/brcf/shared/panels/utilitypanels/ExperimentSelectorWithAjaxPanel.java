////////////////////////////////////////////////////
// ExperimentSelectorWithAjaxPanel.java
// Written by Jan Wigginton, Jul 11, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.panels.utilitypanels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.util.model.ExperimentListModel;


public abstract class ExperimentSelectorWithAjaxPanel extends AccessLimitedPanel
	{
	@SpringBean
	private ExperimentService experimentService;
	
	IndicatingAjaxButton editButton;
	String buttonLabel = "Edit Data", selectedExperiment = "";
	
	
	public ExperimentSelectorWithAjaxPanel(String id) 
		{
		super(id);
		
		add(new FeedbackPanel("feedback"));
		
		ExperimentSelectorWithAjaxForm lde = new ExperimentSelectorWithAjaxForm("experimentSelectorForm");
		lde.setMultiPart(true);
		add(lde);
		}
	
		
	public class ExperimentSelectorWithAjaxForm extends Form 
		{	
		private DropDownChoice<String> editExperimentDrop;
		
		ExperimentSelectorWithAjaxForm(String id)
			{
			super(id);
			String buttonLabel ="Edit Data";
			
			add(editExperimentDrop = buildEditExperimentDropdown("editExperimentDropdown", "selectedExperiment"));
			add(editButton = buildEditButton());
			editButton.setOutputMarkupId(true);
			}
	
	
		private IndicatingAjaxButton buildEditButton()
			{
			return new IndicatingAjaxButton("editButton")
				{
				@Override
				public boolean isEnabled()
					{
					return true; //selectedExperiment != null && selectedExperiment.startsWith("EX"); 
					}
				
				@Override
				protected void onComponentTag(ComponentTag tag)
					{
					super.onComponentTag(tag);
					String label = getButtonLabel();
					tag.put("value", label);
					}
					
				@Override
				protected void onSubmit(AjaxRequestTarget target) 
					{
					try
						{
						setOutputMarkupId(true);
						doSubmit(selectedExperiment, target);
						}
					catch (Exception e)  { }
					}
				};
			}
			
		
		private DropDownChoice buildEditExperimentDropdown(final String id, String propertyName)
			{
			editExperimentDrop =  new DropDownChoice(id,  new PropertyModel<String>(this, propertyName), 
			new ExperimentListModel("both", experimentService, false));
			
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
							
							target.add(editButton);
							break;
						}
					}
				};
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
		
	public abstract void doSubmit(String selectedExperiment, AjaxRequestTarget target );
	}
		

////////////////////////////////////////////////////
// ExperimentPlusSampleCountPanel.java
// Written by Jan Wigginton, Apr 4, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.panels.utilitypanels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.RangeValidator;

import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.model.ExperimentListModel;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;



public abstract class ExperimentPlusCountPanel extends AccessLimitedPanel
	{
	@SpringBean
	private ExperimentService experimentService;
	
	@SpringBean
	private UserService userService;
	
	private DropDownChoice<String> editExperimentDrop;
	private String selectedExperiment = null, countLabelString = "N Items", buttonLabel = "Edit Data";
	private IndicatingAjaxButton editButton;
	private FeedbackPanel feedback;
	private Integer maxCount = 9999, selectedCount = null;
	
	
	public ExperimentPlusCountPanel(String id) 
		{
		super(id);
		
		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);
		
		ExperimentSelectorForm lde = new ExperimentSelectorForm("experimentSelectorForm");
		lde.setMultiPart(true);
		add(lde);
		}


	public class ExperimentSelectorForm extends Form 
		{	
		public ExperimentSelectorForm(String id)
		{
		super(id);
		
		
		add(new Label("countLabel", new PropertyModel<String>(this, "countLabelString")));
		add(buildCountField("selectedCount").setLabel(new PropertyModel<String>(this, "countLabelString")));
		add(editExperimentDrop = buildEditExperimentDropdown("editExperimentDropdown", "selectedExperiment"));
		add(editButton = buildEditButton());
		editButton.setOutputMarkupId(true);
		}
	
	
	private RequiredTextField buildCountField(String id)
		{
		IModel<Integer> model2 = new PropertyModel<Integer>(this, "selectedCount");
		RequiredTextField<Integer> fld = new RequiredTextField<Integer>(id, model2);
		fld.add(RangeValidator.<Integer>range(0, maxCount));
		fld.setRequired(true); 
	
		return fld;
		}
	
	// Protocol
	private IndicatingAjaxButton buildEditButton()
		{
		return new IndicatingAjaxButton("editButton")
			{
			@Override
			public boolean isEnabled()
				{
				return !StringUtils.isNullOrEmpty(selectedExperiment);
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
					String selectedExperimentId = getSelectedExperimentId();
					doSubmit(selectedExperimentId, selectedCount, target);
					}
				catch (Exception e)  {  }
					}
			
			
				@Override
				protected void onError(AjaxRequestTarget target)
					{
					target.add(feedback);
					}
				};
			}
	
	
	private DropDownChoice buildEditExperimentDropdown(final String id, String propertyName)
		{
		editExperimentDrop =  new DropDownChoice(id,  new PropertyModel<String>(this, propertyName), 
		new ExperimentListModel("both", experimentService, false, true));
		
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
	
	public String getSelectedExperimentId()
		{
		return StringParser.parseName(selectedExperiment);
		}
	
	public Integer getSelectedCount()
		{
		return selectedCount;
		}
	
	public void setSelectedCount(Integer ct)
		{
		selectedCount = ct;
		}
	
	public String getCountLabelString()
		{
		return countLabelString;
		}

	public void setCountLabelString(String str)
		{
		countLabelString = str;
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
	
	
	public Integer getMaxCount()
		{
		return maxCount;
		}

	public void setMaxCount(Integer maxCount)
		{
		this.maxCount = maxCount;
		}

	public String getCountLabelString()
		{
		return countLabelString;
		}

	public void setCountLabelString(String countLabelString)
		{
		this.countLabelString = countLabelString;
		}

	public abstract void doSubmit(String selectedExperimentId, Integer selectedCount,AjaxRequestTarget target);
	}
	

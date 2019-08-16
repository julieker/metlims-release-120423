////////////////////////////////////////////////////////////////////
//ExperimentSelectorWithConfirm.java
//Written by Jan Wigginton September 2015
////////////////////////////////////////////////////////////////////

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

public abstract class ExperimentSelectorWithConfirmPanel extends Panel
	{
	@SpringBean
	private ExperimentService experimentService;

	DropDownChoice<String> editExperimentDrop;
	String selectedExperiment = null;
	IndicatingAjaxButton editButton, startEditButton;
	String buttonLabel = "Edit Data";
	boolean editMode = false;

	public ExperimentSelectorWithConfirmPanel(String id)
		{
		super(id);

		add(new FeedbackPanel("feedback"));

		ExperimentSelectorWithConfirmForm lde = new ExperimentSelectorWithConfirmForm(
				"experimentSelectorForm");
		lde.setMultiPart(true);
		add(lde);
		}

	public class ExperimentSelectorWithConfirmForm extends Form
		{
		ExperimentSelectorWithConfirmForm(String id)
			{
			super(id);
			String buttonLabel = "Edit Data";

			add(editExperimentDrop = buildEditExperimentDropdown("editExperimentDropdown", "selectedExperiment"));
			add(editButton = buildEditButton("editButton"));
			}

		private IndicatingAjaxButton buildEditButton(String id)
			{
			return new IndicatingAjaxButton(id)
				{
					@Override
					public boolean isEnabled()
						{
						return selectedExperiment != null
								&& selectedExperiment.startsWith("EX");
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
							if (editMode)
								{
								doSubmit(selectedExperiment);
								setButtonLabel("Invalidate Samples");
								setSelectedExperiment(null);
								target.add(editExperimentDrop);
								} else
								{
								target.appendJavaScript("alert('If you would like to irreversibly remove all samples and metadata from the database for this experiment, please select Confirm');");
								setButtonLabel("Confirm");
								}

							target.add(this);
							editMode ^= true;
							} 
						catch (Exception e) { }
						}

					@Override
					protected void onError(AjaxRequestTarget arg0)
						{
						}
				};
			}

		
		private DropDownChoice buildEditExperimentDropdown(final String id,
				String propertyName)
			{
			editExperimentDrop = new DropDownChoice(id, new PropertyModel(this, propertyName), new ExperimentListModel("both",
					experimentService, false));

			editExperimentDrop.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForEditExperimentDrop"));

			return editExperimentDrop;
			}

		
		private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(
				final String event, final String response)
			{
			return new AjaxFormComponentUpdatingBehavior(event)
				{
					@Override
					protected void onUpdate(AjaxRequestTarget target)
						{
						switch (response)
							{
							case "updateForEditExperimentDrop":

							setEditMode(false);
							setButtonLabel("Invalidate Samples");
							target.add(editButton);
								break;
							}
						}
				};
			}

		public void setEditMode(boolean mode)
			{
			editMode = mode;
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

	// public abstract void setButtonLabel(String label);
	public abstract void doSubmit(String selectedExperiment);
	}

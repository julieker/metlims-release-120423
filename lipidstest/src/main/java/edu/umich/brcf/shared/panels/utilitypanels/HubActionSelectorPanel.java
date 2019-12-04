///////////////////////////////////////
//HubActionSelectorPanel.java
//Written by Jan Wigginton November 2015
///////////////////////////////////////

package edu.umich.brcf.shared.panels.utilitypanels;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

//import edu.umich.brcf.mchear.layers.service.MChearSubmissionDataService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.util.interfaces.IWriteableSpreadsheet;

// CoreExperiment

public abstract class HubActionSelectorPanel extends Panel
	{
	@SpringBean
	private ExperimentService experimentService;

	private String selectedHub = "", selectedAction = "";
	private Boolean hubEnabled = true, actionEnabled = true;

	// AjaxLink actionButton;
	private String buttonLabel = "Edit Data";
	IWriteableSpreadsheet report;
	List<String> hubs = Arrays.asList(new String[] { "BRIR", "Epigenomics",
			"Metabolomics", "EHS" });

	public HubActionSelectorPanel(String id)
		{
		this(id, false);
		}

	public HubActionSelectorPanel(String id, Boolean withExcelDownload)
		{
		super(id);

		add(new FeedbackPanel("feedback"));
		HubActionSelectorForm lde = new HubActionSelectorForm(
				"hubActionSelectorForm", withExcelDownload);
		lde.setMultiPart(true);
		add(lde);
		}

	public class HubActionSelectorForm extends Form
		{
		DropDownChoice<String> editHubDrop, actionSelectorDrop;
		AjaxLink actionButton;

		HubActionSelectorForm(String id, boolean withExcelDownload)
			{
			super(id);

			add(editHubDrop = buildEditHubDropDown("editHubDropDown",
					"selectedHub"));
			add(actionSelectorDrop = buildActionSelectorDropdown(
					"actionSelectorDrop", "selectedAction"));
			add(actionButton = buildActionButton());
			}

		private DropDownChoice<String> buildActionSelectorDropdown(
				final String id, String propertyName)
			{
			List<String> actionChoices = Arrays.asList(new String[] {
					"Check In", "Register New", "Something Else" });

			DropDownChoice<String> actionSelectorDrop = new DropDownChoice<String>(
					id, new PropertyModel(this, propertyName), actionChoices)
				{
					@Override
					public boolean isEnabled()
						{
						return actionEnabled;
						}
				};

			actionSelectorDrop.setOutputMarkupId(true);
			actionSelectorDrop.add(this
					.buildStandardFormComponentUpdateBehavior("change",
							"updateForActionSelectorDrop"));

			return actionSelectorDrop;
			}

		private AjaxLink buildActionButton()
			{
			// issue 39
			AjaxLink link = new AjaxLink <Void>("actionButton")
				{
					@Override
					public void onClick(AjaxRequestTarget target)
						{
						// setOutputMarkupId(true);
						doSubmit(selectedHub, selectedAction, target);
						}

					@Override
					protected void onComponentTag(ComponentTag tag)
						{
						super.onComponentTag(tag);
						String label = getButtonLabel();
						tag.put("value", label);
						}
				};

			link.setOutputMarkupId(true);
			return link;
			}

		private DropDownChoice<String> buildEditHubDropDown(final String id,
				String propertyName)
			{
			editHubDrop = new DropDownChoice<String>(id,
					new PropertyModel<String>(this, propertyName),
					new PropertyModel<List<String>>(this, "hubs"))
				{
					@Override
					public boolean isEnabled()
						{
						return hubEnabled;
						}
				};

			editHubDrop.setOutputMarkupId(true);
			editHubDrop.add(this.buildStandardFormComponentUpdateBehavior(
					"change", "updateForEditHubDrop"));

			return editHubDrop;
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
							case "updateForEditHubDrop":
							target.add(actionButton);
								break;

							case "updateForActionSelectorDrop":
							setButtonLabel(selectedAction);
							target.add(actionButton);
								// target.add(downloadButton);
								break;
							}
						}
				};
			}

		public List<String> getHubs()
			{
			return hubs;
			}

		public String getSelectedHub()
			{
			return selectedHub;
			}

		public void setSelectedHub(String ee)
			{
			selectedHub = ee;
			}

		public String getSelectedAction()
			{
			return selectedAction;
			}

		public void setSelectedAction(String ee)
			{
			selectedAction = ee;
			}
		}

	public String getSelectedHub()
		{
		return selectedHub;
		}

	public void setSelectedHub(String ee)
		{
		selectedHub = ee;
		}

	public String getSelectedAction()
		{
		return selectedAction;
		}

	public void setSelectedAction(String ee)
		{
		selectedAction = ee;
		}

	public String getButtonLabel()
		{
		return buttonLabel;
		}

	public void setButtonLabel(String label)
		{
		buttonLabel = label;
		}

	public Boolean getHubEnabled()
		{
		return hubEnabled;
		}

	public void setHubEnabled(Boolean hubEnabled)
		{
		this.hubEnabled = hubEnabled;
		}

	public Boolean getActionEnabled()
		{
		return actionEnabled;
		}

	public void setActionEnabled(Boolean actionEnabled)
		{
		this.actionEnabled = actionEnabled;
		}

	public List<String> getHubs()
		{
		return hubs;
		}

	public void setHubs(List<String> lst)
		{
		this.hubs = lst;
		}

	public abstract void doSubmit(String selectedHub, String selectedAction,
			AjaxRequestTarget target);
	}

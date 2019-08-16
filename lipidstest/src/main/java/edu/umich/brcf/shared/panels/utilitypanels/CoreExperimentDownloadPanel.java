///////////////////////////////////////
// CoreExperimentDownloadPanel.java
// Written by Jan Wigginton October 2015
///////////////////////////////////////

package edu.umich.brcf.shared.panels.utilitypanels;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.util.widgets.ExcelDownloadLink;
import edu.umich.brcf.shared.util.interfaces.IWriteableSpreadsheet;
import edu.umich.brcf.shared.util.model.ExperimentListModel;



public abstract class CoreExperimentDownloadPanel extends Panel
	{
	@SpringBean
	private ExperimentService experimentService;

	private DropDownChoice<String> editHubDrop, editExperimentDrop;
	private String selectedHub = "", selectedExperiment = "";
	private ExcelDownloadLink downloadButton;
	private String buttonLabel = "Edit Data";
	private IWriteableSpreadsheet report;
	private List<String> hubs = ((List<String>) Arrays.asList(new String[] { "BRIR", "Epigenetics", "Metabolomics", "EHS" }));

	
	public CoreExperimentDownloadPanel(String id)
		{
		this(id, false);
		}

	
	public CoreExperimentDownloadPanel(String id, Boolean withExcelDownload)
		{
		super(id);

		add(new FeedbackPanel("feedback"));
		CoreExperimentSelectorForm lde = new CoreExperimentSelectorForm( "hubSelectorForm");
		lde.setMultiPart(true);
		add(lde);
		}

	
	public CoreExperimentDownloadPanel(String id, List<String> hubs)
		{
		super(id);
		this.hubs = hubs;
		add(new FeedbackPanel("feedback"));
		CoreExperimentSelectorForm lde = new CoreExperimentSelectorForm("hubSelectorForm");
		lde.setMultiPart(true);
		add(lde);
		}

	
	public class CoreExperimentSelectorForm extends Form
		{
		CoreExperimentSelectorForm(String id)
			{
			super(id);

			add(editHubDrop = buildEditHubDropDown("editHubDropDown", "selectedHub"));
			add(editExperimentDrop = buildEditExperimentDropdown("editExperimentDropdown", "selectedExperiment"));
			add(downloadButton = buildDownloadButton());
			}

		
		private DropDownChoice<String> buildEditExperimentDropdown(final String id, String propertyName)
			{
			editExperimentDrop = new DropDownChoice<String>(id, new PropertyModel(this, propertyName), new ExperimentListModel("both", experimentService, false));
			editExperimentDrop.setOutputMarkupId(true);
			editExperimentDrop.add(this.buildStandardFormComponentUpdateBehavior("change","updateForEditExperimentDrop"));

			return editExperimentDrop;
			}

		
		private ExcelDownloadLink buildDownloadButton()
			{
			ExcelDownloadLink link = new ExcelDownloadLink("editButton", null)
				{
				@Override
				public boolean isEnabled()
					{
					return true; // selectedHub != null && // !selectedHub.trim().isEmpty()  && selectedExperiment != null &&  !selectedExperiment.trim().isEmpty();
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

		
		public List<String> getHubs()
			{
			return hubs;
			}

		
		private DropDownChoice<String> buildEditHubDropDown(final String id, String propertyName)
			{
			editHubDrop = new DropDownChoice<String>(id, new PropertyModel<String>(this, propertyName), new PropertyModel<List<String>>(this, "hubs"));
			editHubDrop.setOutputMarkupId(true);
			editHubDrop.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForEditHubDrop"));

			return editHubDrop;
			}

		
		private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(final String event, final String response)
			{
			return new AjaxFormComponentUpdatingBehavior(event)
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target)
					{
					switch (response)
						{
						case "updateForEditHubDrop": target.add(downloadButton); break;

						case "updateForEditExperimentDrop":
							// report = new Mrc2SubmissionSheetWriter(selectedExperiment,
							// getSelectedHub());
							// downloadButton.setReport(report);
							// target.add(downloadButton);
							break;
						}
					}
				};
			}
		

		public String getSelectedHub()
			{
			return selectedHub;
			}

		public void setSelectedHub(String ee)
			{
			selectedHub = ee;
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

	public List<String> getHubs()
		{
		return this.hubs;
		}

	public void setHubs(List<String> lst)
		{
		this.hubs = lst;
		}

	public String getButtonLabel()
		{
		return buttonLabel;
		}

	public void setButtonLabel(String label)
		{
		buttonLabel = label;
		}

	public abstract void doSubmit(String selectedHub);
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//ExperimentRunInfoUploadPanel.java
//Written by Jan Wigginton August 2015
///////////////////////////////////////////////

package edu.umich.brcf.shared.panels.utilitypanels;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;



///import edu.umich.brcf.metabolomics.panels.lipidshome.browse.ExperimentRunInfo;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.datacollectors.ExperimentRunInfo;
import edu.umich.brcf.shared.util.model.ExperimentListModel;
import edu.umich.brcf.shared.util.widgets.METWorksAjaxUpdatingDateTextField;



public abstract class ExperimentRunInfoUploadPanel extends Panel
	{
	@SpringBean
	private ExperimentService experimentService;

	List<String> absciexExperiments = experimentService.expIdsByInceptionDate();
	List<String> possModes = Arrays.asList(new String[] { "Positive",
			"Negative" });
	List<String> possNotations = Arrays.asList(new String[] { "Combined",
			"Normalized", "Raw", "Other" });

	private FileUploadField fileUploadField;
	protected ArrayList<FileUpload> filesUploaded;
	private String UPLOAD_FOLDER = "./";

	DropDownChoice<String> uploadedExperimentDrop, experimentModeDrop,
			dataNotationDrop;
	METWorksAjaxUpdatingDateTextField runDateFld;

	private boolean showModeOption = true, showNotationOption = true,
			showDateOption = true;
	IndicatingAjaxButton submitButton;

	ExperimentRunInfo expRunInfo = new ExperimentRunInfo();

	public ExperimentRunInfoUploadPanel(String id)
		{
		super(id);
		// add(new FeedbackPanel("feedback"));
		ExperimentRunInfoUploadForm form;
		add(form = new ExperimentRunInfoUploadForm("expRunInfoUploadForm"));
		form.setMultiPart(true);
		}

	class ExperimentRunInfoUploadForm extends Form
		{
		public ExperimentRunInfoUploadForm(String id)
			{
			super(id);

			expRunInfo.setRunDate(new Date());

			add(experimentModeDrop = buildDisappearingDropDown(
					"experimentModeDropdown", new PropertyModel<String>(
							expRunInfo, "expMode"), possModes, "mode"));
			add(dataNotationDrop = buildDisappearingDropDown(
					"dataNotationDropdown", new PropertyModel<String>(
							expRunInfo, "dataNotation"), possNotations,
					"notation"));
			add(buildDisappearingLabel("dateLabel", "Date : ", "date"));
			add(buildDisappearingLabel("notationLabel", "Data Type : ",
					"notation"));
			add(buildDisappearingLabel("modeLabel", "Mode : ", "mode"));

			add(runDateFld = grabDateTextField("uploadedRunDateTxt",
					"uploadedRunDate"));
			runDateFld.setOutputMarkupId(true);

			add(fileUploadField = buildFileUploadField("fileContents",
					filesUploaded));

			add(uploadedExperimentDrop = new DropDownChoice(
					"experimentDropdown",
					new PropertyModel(expRunInfo, "expId"),
					new ExperimentListModel("absciex", experimentService, false)));
			uploadedExperimentDrop.add(this
					.buildStandardFormComponentUpdateBehavior("change",
							"updateForExperimentDrop"));

			add(submitButton = buildSubmitButton());
			}

		// SWITCHOVER FROM PRODUCTION TO STAGING
		private boolean userHasUploadPermission()
			{
			String userId = ((MedWorksSession) getSession()).getCurrentUserId();
			return true;// userId.equals("U00358");
			}

		// AliquotBarcodesPage
		private IndicatingAjaxButton buildSubmitButton()
			{
			return new IndicatingAjaxButton("submitButton")
				{
					public boolean isEnabled()
						{
						return userHasUploadPermission()
								&& (expRunInfo.getExpId() != null && !expRunInfo
										.getExpId().equals(""));
						}

					@Override // issue 464
					protected void onSubmit(AjaxRequestTarget target
							)
						{
						try
							{
							final FileUpload uploadedFile = fileUploadField
									.getFileUpload();

							if (uploadedFile != null)
								{
								File newFile = uploadFile(uploadedFile);
								expRunInfo.setUploadedFileName(newFile
										.getName());
								doSubmit(target, uploadedFile, newFile,
										expRunInfo);
								}
							} catch (Exception e)
							{
							throw e;
							// throw new
							// METWorksException("Error while uploading file");
							// -- please be sure that your file isnt open in
							// another program and that it
							// has been copied to your computer from any
							// networked drives");
							}
						}

					@Override
					protected void onError(AjaxRequestTarget arg0)
						{
						}
				};
			}

		private DropDownChoice<String> buildDisappearingDropDown(String id,
				IModel model, List<String> choices, final String tag)
			{
			DropDownChoice<String> drp = new DropDownChoice(id, model, choices)
				{
					@Override
					public boolean isVisible()
						{
						return "mode".equals(tag) ? showModeOption
								: showNotationOption;
						}

					@Override
					protected void onComponentTag(final ComponentTag tag)
						{
						super.onComponentTag(tag);

						boolean show = "mode".equals(tag) ? showModeOption
								: showNotationOption;
						tag.put("padding-top", show ? "7px" : "3px");
						}
				};

			drp.setOutputMarkupId(true);
			return drp;
			}

		private Label buildDisappearingLabel(String id, String txt,
				final String tag)
			{
			Label lbl = new Label(id, txt)
				{
					@Override
					public boolean isVisible()
						{
						return "mode".equals(tag) ? showModeOption
								: ("notation".equals(tag) ? showNotationOption
										: showDateOption);
						}

					@Override
					protected void onComponentTag(final ComponentTag tag)
						{
						super.onComponentTag(tag);
						boolean showHere = "mode".equals(tag) ? showModeOption
								: ("notation".equals(tag) ? showNotationOption
										: showDateOption);
						tag.put("padding-top", showHere ? "7px" : "3px");
						}
				};

			lbl.setOutputMarkupId(true);
			return lbl;
			}

		private FileUploadField buildFileUploadField(String id,
				ArrayList<FileUpload> fileUp)
			{
			FileUploadField fld = new FileUploadField(id,
					new Model(fileUp))
				{
					private static final long serialVersionUID = 1L;

					public boolean isEnabled()
						{
						return (userHasUploadPermission() && expRunInfo
								.getExpId() != null);
						}
				};

			fld.setOutputMarkupId(true);
			return fld;
			}

		private File uploadFile(FileUpload uploadedFile)
			{
			File newFile = new File(UPLOAD_FOLDER
					+ uploadedFile.getClientFileName());
			if (newFile.exists())
				{
				newFile.delete();
				}

			try
				{
				newFile.createNewFile();
				uploadedFile.writeTo(newFile);
				info("saved file: " + uploadedFile.getClientFileName());
				} catch (Exception e)
				{
				System.out.println("Error while uploading file "
						+ uploadedFile.getClientFileName());
				}

			return newFile;
			}

		private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(
				final String event, final String response)
			{
			return new AjaxFormComponentUpdatingBehavior(event)
				{
					@Override
					protected void onUpdate(AjaxRequestTarget target)
						{
						target.add(submitButton);
						target.add(fileUploadField);
						}
				};
			}

		private METWorksAjaxUpdatingDateTextField grabDateTextField(String id,
				String property)
			{
			return new METWorksAjaxUpdatingDateTextField(id, new PropertyModel(
					expRunInfo, "runDate"), "change")
				{
					@Override
					protected void onUpdate(AjaxRequestTarget target)
						{
						}

					@Override
					public boolean isVisible()
						{
						return showDateOption;
						}
				};
			}
		}

	public boolean isShowModeOption()
		{
		return showModeOption;
		}

	public boolean isShowNotationOption()
		{
		return showNotationOption;
		}

	public boolean isShowDateOption()
		{
		return showDateOption;
		}

	public void setShowModeOption(boolean showModeOption)
		{
		this.showModeOption = showModeOption;
		}

	public void setShowNotationOption(boolean showNotationOption)
		{
		this.showNotationOption = showNotationOption;
		}

	public void setShowDateOption(boolean showDateOption)
		{
		this.showDateOption = showDateOption;
		}

	public abstract void doSubmit(AjaxRequestTarget target,
			FileUpload fileUpload, File newFile, ExperimentRunInfo expRunInfo);
	}

// ///////////////// SCRAP CODE////////////////

/*
 * 
 * 
 * private DropDownChoice buildExperimentDropdown(final String id, String
 * propertyName) { uploadedExperimentDrop =
 * 
 * uploadedExperimentDrop.add(this.buildStandardFormComponentUpdateBehavior(
 * "change", "updateForExperimentDrop"));
 * 
 * return uploadedExperimentDrop; }
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * private DropDownChoice buildModeDropdown(final String id, String property) {
 * return new DropDownChoice(id, new PropertyModel<String>(expRunInfo,
 * "expMode"), possModes); }
 * 
 * 
 * 
 * private DropDownChoice buildDataNotationDropdown(final String id, String
 * property) { return new DropDownChoice(id, new
 * PropertyModel<String>(expRunInfo, "dataNotation"), possNotations); }
 */
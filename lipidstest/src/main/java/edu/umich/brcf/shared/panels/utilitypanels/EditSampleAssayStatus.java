package edu.umich.brcf.shared.panels.utilitypanels;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import edu.umich.brcf.shared.layers.domain.SampleAssayStatus;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;


public abstract class EditSampleAssayStatus extends WebPage
	{

	// @SpringBean
	// SamplePrepService samplePrepService;

	String status;

	public EditSampleAssayStatus(Page backPage, ModalWindow modal)
		{
		//add(new FeedbackPanel("feedback"));
		add(new EditSampleAssayStatusForm("editSampleStatusForm", modal));
		status = "In Storage";
		}

	public final class EditSampleAssayStatusForm extends Form
		{
		public EditSampleAssayStatusForm(final String id, ModalWindow modal)
			{
			super(id);
			DropDownChoice statusDD = new DropDownChoice("status",
					new PropertyModel(this, "status"),
					SampleAssayStatus.Lims_Sample_Assay_statuses,
					new ChoiceRenderer() // issue 464
						{
							public Object getDisplayValue(Object object)
								{
								String stringrep;
								Character temp = (Character) object;
								switch (temp)
									{
									case 'Q':
									stringrep = "Queued";
										break;
									case 'P':
									stringrep = "Prepped";
										break;
									case 'R':
									stringrep = "Samples Run";
										break;
									case 'D':
									stringrep = "Data Curation";
										break;
									case 'C':
									stringrep = "Complete";
										break;
									// case 'S' : stringrep =
									// "Samples Received"; break;
									case 'X':
									stringrep = "Samples Excluded";
										break;
									default:
									throw new IllegalStateException(temp
											+ " is not mapped!");
									}
								return stringrep;
								}

							public String getIdValue(Object object, int index)
								{
								Character idx = SampleAssayStatus.Lims_Sample_Assay_statuses
										.get(index);
								return (idx == null ? SampleAssayStatus.Lims_Sample_Assay_statuses
										.get(0).toString() : idx.toString());
								}
						});

			statusDD.setRequired(true);
			add(statusDD);
			add(new AjaxSubmitLink("saveChanges", this)
				{
					@Override
					protected void onSubmit(AjaxRequestTarget target
							)
						{
						try
							{ // issue 464
							EditSampleAssayStatus.this.onSave((String) getForm()
									.get("status").getDefaultModelObject(),
									target);
							} catch (Exception e)
							{
							EditSampleAssayStatus.this
									.error("Save unsuccessful. Please re-check values entered.");
							}
						}

					@Override
					protected void onError(AjaxRequestTarget target
							)
						{
						// TODO Auto-generated method stub

						}
				});
			add(new AjaxCancelLink("close", modal));
//close
			}

		String status;

		public String getStatus()
			{
			return status;
			}

		public void setStatus(String status)
			{
			this.status = status;
			}
		}

	protected abstract void onSave(String status, AjaxRequestTarget target);
	}

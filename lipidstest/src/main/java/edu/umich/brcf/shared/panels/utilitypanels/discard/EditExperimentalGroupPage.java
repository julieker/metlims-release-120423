package edu.umich.brcf.shared.panels.utilitypanels.discard;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.ExperimentalGroup;
import edu.umich.brcf.shared.layers.dto.ExperimentalGroupDTO;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.widgets.AjaxExperimentField;

public abstract class EditExperimentalGroupPage extends WebPage
	{

	@SpringBean
	ExperimentService expService;
	AjaxSubmitLink saveLink;
	ExperimentalGroupDTO egDto;

	public ExperimentalGroupDTO getEgDto()
		{
		return egDto;
		}

	public void setEgDto(ExperimentalGroupDTO egDto)
		{
		this.egDto = egDto;
		}

	public EditExperimentalGroupPage(Page backPage)
		{
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		setEgDto(new ExperimentalGroupDTO());
		add(new EditGroupForm("editGroupForm"));
		}

	public final class EditGroupForm extends Form
		{

		public EditGroupForm(String id)
			{
			super(id);
			setOutputMarkupId(true);
			setModel(new CompoundPropertyModel(egDto));
			add(new RequiredTextField("group_name"));
			add(new RequiredTextField("group_description"));
			AutoCompleteTextField experimentField = new AjaxExperimentField(
					"expID");
			// experimentField.setOutputMarkupId(true);
			experimentField.add(new AjaxFormSubmitBehavior(this, "change")
				{
					protected void onSubmit(AjaxRequestTarget target)
						{
						String input = ((AutoCompleteTextField) (this
								.getComponent())).getInput();
						getEgDto().setExpID(input);
						target.add(this.getComponent());
						}

					@Override
					protected void onError(AjaxRequestTarget arg0)
						{
						}
				});
			// // final AutoCompleteTextField
			// experimentField=newAjaxField("expID", 80);
			// // add(newHiddenLabel("hiddenexperiment", experimentField));
			add(experimentField);// .setRequired(true)
			add(saveLink = new AjaxSubmitLink("saveChanges", this)
				{
					@Override
					protected void onSubmit(AjaxRequestTarget target, Form form)
						{
						ExperimentalGroupDTO myBean = getEgDto();
						myBean.setExpID(StringParser.parseId(myBean.getExpID()));
						ExperimentalGroup group = null; // =
														// expService.saveGroup(myBean);
						EditExperimentalGroupPage.this
								.info("Experimental Group "
										+ group.getGroupID()
										+ " saved Successfully!");
						target.add(EditExperimentalGroupPage.this
								.get("feedback"));
						EditExperimentalGroupPage.this.onSave(group, target);
						}

					@Override
					protected void onError(AjaxRequestTarget arg0)
						{
						// TODO Auto-generated method stub

						}
				});
			}
		}

	protected abstract void onSave(ExperimentalGroup group,
			AjaxRequestTarget target);
	}

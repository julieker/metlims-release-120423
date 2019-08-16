package edu.umich.brcf.metabolomics.panels.lims.newexperiment;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Priority;
import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.layers.dto.ExperimentDTO;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.ProjectService;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;
import edu.umich.brcf.shared.util.widgets.AjaxProjectField;


public abstract class EditExperiment extends WebPage 
	{
	@SpringBean
	ExperimentService experimentService;

	@SpringBean
	ProjectService projectService;

	ExperimentDTO expDto;

	AjaxSubmitLink  saveLink;
	FeedbackPanel feedbackPanel;
	int serviceRequestLength = 20;

	public ExperimentDTO getExpDto() { return expDto; }
	public void setExpDto(ExperimentDTO expDto)  { this.expDto = expDto; }

	
	public EditExperiment(Page backPage, IModel experimentModel, ModalWindow modal1) 
		{
		Experiment exp = (Experiment) experimentModel.getObject();
		add(new Label("pageTitle", "Edit Experiment"));
	
		add(feedbackPanel = new FeedbackPanel("feedback"));
		feedbackPanel.setOutputMarkupId(true);
		
		setExpDto(ExperimentDTO.instance(exp));
		add(new EditExperimentForm("editExperimentForm", false, modal1));
		}

	
	public EditExperiment(Page backPage, ModalWindow modal)
		{
		add(new Label("pageTitle", "Add Experiment"));
		
		add(feedbackPanel = new FeedbackPanel("feedback"));
		feedbackPanel.setOutputMarkupId(true);
		
		setExpDto(new ExperimentDTO());
		expDto.setExpID("to be assigned");
		
		add(new EditExperimentForm("editExperimentForm" , true, modal));
		}

	
	public final class EditExperimentForm extends Form 
		{
		public EditExperimentForm(final String id, boolean ifNew, ModalWindow modal1) 
			{
			super(id);
			setOutputMarkupId(true);
			setModel(new CompoundPropertyModel(expDto));
		
			add(new Label("expID"));
			add(newRequiredTextField("expName", 120));
			add(newRequiredTextArea("expDescription", 4000)); //issue 449

			AutoCompleteTextField projectField=new AjaxProjectField("projID", 100);
			add(newHiddenLabel(this, "hiddenproject", projectField));
			add(projectField);
			// Issue 206
			AjaxCheckBox box = new AjaxCheckBox("isChear", new PropertyModel(expDto, "isChear"))
			    {
			    @Override
			    public void onUpdate(AjaxRequestTarget target)
				    {
				
				    }
			    };
			add (box);
			
			final DropDownChoice priorityDD=new DropDownChoice("priority", Priority.PRIORITIES);
			priorityDD.setRequired(true);
			expDto.setPriority(Priority.MEDIUM);
			add(priorityDD);		
			// Issue 442
			TextField txtServiceRequest = new TextField("serviceRequest");
			// issue 442
			txtServiceRequest.add(StringValidator.maximumLength(serviceRequestLength));
			add(txtServiceRequest);			
			add(new TextArea("notes")
				{
				public boolean isRequired() 
					{
				    return (priorityDD.getConvertedInput()!=null && (((String) priorityDD.getConvertedInput()).trim().equals("LOW")));
					}
				});
				
			add(new AjaxCancelLink("cancelButton", modal1));
			
			
			add(saveLink = new AjaxSubmitLink ("saveChanges", this)
				{
				@Override
				protected void onSubmit(AjaxRequestTarget target) // issue 464
					{
					ExperimentDTO expDto = (ExperimentDTO) getForm().getModelObject();
					try{
						if(!FormatVerifier.verifyFormat(Project.fullIdFormat, expDto.getProjID()))
							expDto.setProjID(StringParser.parseId(expDto.getProjID()));
						
						Experiment experiment = experimentService.save(expDto);
						EditExperiment.this.info("Experiment "+experiment.getExpID()+" saved successfully.");
						target.add(EditExperiment.this.get("feedback"));
						}
				
					catch (Exception e)
						{
						e.printStackTrace();
						String msg = e.getMessage();
						if (msg.contains("Experiment") ||  msg.startsWith("Project") || msg.startsWith("Duplicate"))
							EditExperiment.this.error(msg);
						else 
						     EditExperiment.this.error("Save unsuccessful. Please re-check values entered.");
					
						target.add(EditExperiment.this.get("feedback"));
						}
					}
				
					@Override // issue 464
			         protected void onError(final AjaxRequestTarget target) 
						{
			            target.add(feedbackPanel);
						}
				});
			}

		
		private RequiredTextField newRequiredTextField(String id, int maxLength) 
			{
			RequiredTextField textField = new RequiredTextField(id);
			textField.add(StringValidator.maximumLength(maxLength));
			return textField;
			}
		
		
		private TextArea newRequiredTextArea(String id, int maxLength) 
			{
			TextArea textArea = new TextArea(id);
			textArea.add(StringValidator.maximumLength(maxLength));
			textArea.setRequired(true);
			return textArea;
			}
		
		
		private Label newHiddenLabel(EditExperimentForm form, String id, AutoCompleteTextField field) 
			{
			final Label label = new Label(id, field.getModel());
			label.setVisible(false);
			label.setOutputMarkupId(true);
			
			field.add(new AjaxFormSubmitBehavior(form, "change") 
				{
				@Override
				protected void onSubmit(AjaxRequestTarget target) { target.add(label); }

				@Override
				protected void onError(AjaxRequestTarget target) {  }
				});
			return label;
			}
		}

	
	protected abstract void onSave(Experiment experiment, AjaxRequestTarget target);
	}

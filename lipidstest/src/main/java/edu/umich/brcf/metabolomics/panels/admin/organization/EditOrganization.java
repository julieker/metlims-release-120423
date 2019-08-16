package edu.umich.brcf.metabolomics.panels.admin.organization;


import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import edu.umich.brcf.shared.layers.domain.Organization;
import edu.umich.brcf.shared.layers.dto.OrganizationDTO;
import edu.umich.brcf.shared.layers.service.OrganizationService;
import edu.umich.brcf.shared.util.behavior.FocusOnLoadBehavior;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;


public abstract class EditOrganization extends WebPage
	{
	@SpringBean
	private OrganizationService organizationService;

	private String titleLabel;
	boolean openForEdits;

	
	public EditOrganization(Page backPage, boolean ifNew)
		{
		this(backPage, new OrganizationDTO(), ifNew);
		}

	
	public EditOrganization(Page backPage, OrganizationDTO dto, boolean ifNew)
		{
		this(backPage, dto, ifNew, null);
		}
	
	
	public EditOrganization(Page backPage, OrganizationDTO dto, boolean ifNew, ModalWindow modal)
		{
		this(backPage, dto, ifNew, modal, true);
		}
	
	public EditOrganization(Page backPage, OrganizationDTO dto, boolean ifNew, ModalWindow modal, 
			boolean openForEdits)
		{
		this.openForEdits = openForEdits;
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new EditOrganizationForm("editOrganizationForm", "to be assigned", dto, ifNew, modal));
	
		setTitleLabel(ifNew ? "New Organization" : "Edit Organization");
		add(new Label("titleLabel", new PropertyModel<String>(this, "titleLabel")));
		}
	

	public final class EditOrganizationForm extends Form
		{
		public EditOrganizationForm(final String id, String org_id, OrganizationDTO org, final boolean newOrg, ModalWindow modal)
			{
			super(id, new CompoundPropertyModel(org));

			RequiredTextField orgNameFld = new RequiredTextField<String>("orgName")
				{
				@Override
				public boolean isEnabled() { return openForEdits; }
				};
			orgNameFld.add(new FocusOnLoadBehavior());	
			orgNameFld.add(StringValidator.maximumLength(120));
			orgNameFld.setLabel(new Model<String>("Organization Name"));
			//validator
			add(orgNameFld);
					
					
			RequiredTextField orgAddressFld = new RequiredTextField<String>("orgAddress")
				{
				@Override
				public boolean isEnabled() { return openForEdits; }
				};
			orgAddressFld.add(StringValidator.maximumLength(100));	
			orgAddressFld.setLabel(new Model<String>("Organization Address"));
			add(orgAddressFld);
		

			add(new AjaxCancelLink("closeButton", modal));
			
			add(new IndicatingAjaxButton("saveChanges", this)
				{
				@Override
				public boolean isEnabled() { return openForEdits; }
			
				@Override
				protected void onSubmit(AjaxRequestTarget target) // issue 464
					{
					OrganizationDTO orgDto = (OrganizationDTO) getForm().getModelObject();
					try
						{
						Organization org = null;
						if (newOrg)
							org = organizationService.save(orgDto);
						else 
							org =  organizationService.update(orgDto);
						
						EditOrganization.this.info("Organization '" + orgDto.getOrgName() + "' saved successfully.") ;
						target.add(EditOrganization.this.get("feedback"));
						EditOrganization.this.onSave(org, target);
						} 
					catch (Exception e)
						{
						String msg = "Save unsuccessful. Please make sure that organization name is less than 120 characters and address is less than 100 characters."; 
						if (e.getMessage() != null && e.getMessage().startsWith("Duplicate"))
							msg = e.getMessage();
							
						e.printStackTrace();
						EditOrganization.this.error(msg); 
						target.add(EditOrganization.this .get("feedback"));
						}
					}

					@Override
					protected void onError(AjaxRequestTarget target) { target.add(EditOrganization.this .get("feedback"));}
				});
			}
		}


	void setTitleLabel(String label)
		{
		this.titleLabel = label;
		}

	String getTitleLabel()
		{
		return titleLabel;
		}

	protected abstract void onSave(Organization org, AjaxRequestTarget target);
	}

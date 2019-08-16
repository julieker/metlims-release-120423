// Revisited : October 2016 (JW)

package edu.umich.brcf.metabolomics.panels.lims.compounds;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.metabolomics.layers.domain.CompoundName;
import edu.umich.brcf.metabolomics.layers.service.CompoundNameService;
import edu.umich.brcf.metabolomics.layers.service.CompoundService;
import edu.umich.brcf.shared.layers.dto.CompoundDTO;
import org.apache.wicket.markup.html.form.ChoiceRenderer;


public class EditCompound extends WebPage
	{
	@SpringBean
	CompoundService cmpService;
	
	@SpringBean
	CompoundNameService cnameService;
	String impCompound;
	
	
	public EditCompound(Page backPage, final CompoundDetailPanel container, final ModalWindow window) 
		{
		CompoundDTO cmpDto = new CompoundDTO();
		add(new FeedbackPanel("feedback"));
		add(new Label("titleLabel", "Add Compound"));
		add(new EditCompoundForm("editCompoundForm", "to be assigned", cmpDto, backPage, container, window));
		}
	
	
	public EditCompound(Page backPage, IModel cmpModel, CompoundDetailPanel container, final ModalWindow window) 
		{
		Compound cmp = (Compound) cmpModel.getObject();
		add(new FeedbackPanel("feedback"));
		add(new Label("titleLabel", "Edit Compound"));
		add(new EditCompoundForm("editCompoundForm", cmp.getCid(), CompoundDTO.instance(cmp), backPage, container, window));
		}
	
	
	public final class EditCompoundForm extends Form 
		{
		public EditCompoundForm(final String id, final String cid, final CompoundDTO cmpDto, final Page backPage, final CompoundDetailPanel container, final ModalWindow window) 
			{
			super(id, new CompoundPropertyModel(cmpDto));

			add(new Label("cid", cid));
			add(newRequiredTextField("chem_abs_number", 30));
			add(newRequiredTextField("smiles", 500));
			
			// issue 464
			DropDownChoice humanRelDD=new DropDownChoice("human_rel", Compound.Human_Rel_Types, new ChoiceRenderer()
				{
				public Object getDisplayValue(Object object)
					{
	                String stringrep=null;
	                String temp = (String) object;
	                Character value = temp.charAt(0);
	                switch (value)
	                	{
	                    case '1' :  stringrep = "Human"; break;
	                    case '2' : stringrep = "Inorganic"; break;
	                    case '3' : stringrep = "Generic"; break;
	                    case '4' : stringrep = "Standard"; break;
	                    case '9' : stringrep = "Xenobiotic"; break;
	                    case '0' : stringrep = "Unknown"; break;
	                    default :
	                        throw new IllegalStateException(value + " is not mapped!");
	                	}
	                return stringrep;
					}
				
				public String getIdValue(Object object, int index)
					{
					//System.out.println("Index is " + index); 
					return (Compound.Human_Rel_Types.get(index));
					}

				})
			;
			humanRelDD.setRequired(true);
			add(humanRelDD);
			
			TextField parentCid;
			add(parentCid = new TextField("parentCid")
				{
				@Override
				public boolean isRequired() {	return false; }
				});
			parentCid.add(StringValidator.maximumLength(6));
			
			
			TextField nameFld;
			add(nameFld = new TextField("name")
				{
				@Override
				public boolean isRequired() { return ( cid!=null && cid.equals("to be assigned")); }
				}) ;
			nameFld.add(StringValidator.maximumLength(500));
			
			
			add(new DropDownChoice("type", CompoundName.TYPES)
				{
				@Override
				public boolean isRequired() { return (cid!=null && cid.equals("to be assigned")); }
				});
			
			TextField htmlFld;
			add(htmlFld =new TextField("html"));
			htmlFld.add(StringValidator.maximumLength(500));
			
			add( new AjaxLink("close")
				{
				public void onClick(AjaxRequestTarget target)
					{ 
					if (container!=null && impCompound!=null)
						{
						container.setCmpId(impCompound);
					//	target.add(container);
						}
					window.close(target);
					}
				@Override // issue 464
				public MarkupContainer setDefaultModel(IModel model) 
				    {
					// TODO Auto-generated method stub
					return this;
				    }
				});
			
			add(new Button("saveChanges")
				{
				public void onSubmit() 
					{
					CompoundDTO cmpDto = (CompoundDTO) getForm().getModelObject();
					boolean err = false;
					
					if(cmpDto.getCid()!=null)
						if (cmpDto.getName()!=null && cmpDto.getName().length()>0)
							{
							for (CompoundName cmpName : cnameService.loadByCid(cmpDto.getCid())) 
								{
								if ((cmpName.getNameType().equals("pri"))&&(cmpDto.getType().equals("pri")))
									{
									EditCompound.this.error("Compound cannot have more than one primary name.");
									err=true;
									}
								if (cmpName.getName().equalsIgnoreCase(cmpDto.getName()))
									{
									EditCompound.this.error("Compound cannot have duplicate names.");
									err=true;
									}
								}
							}
					
					if (!err)
						{
						try {
							Compound cmp = cmpService.save(cmpDto);
							EditCompound.this.info("Compound/Name detail saved.");
							if (container!=null)
								{
								container.setCmpId(cmp.getCid());
								impCompound=cmp.getCid();
								}
							}
						catch(Exception e){  EditCompound.this.error("Save unsuccessful. Please make sure that smiles is valid."); }
						}
					
					setResponsePage(getPage());
					}
				
				public void onError(AjaxRequestTarget target, Form form)
					{
					target.add(EditCompound.this.get("feedback")); 
					}
				});
			
		
			}

		
		private RequiredTextField newRequiredTextField(String id,  int maxLength) 
			{
			RequiredTextField textField = new RequiredTextField(id);
			textField.add(StringValidator.maximumLength(maxLength));
			return textField;
			}
		}
	}

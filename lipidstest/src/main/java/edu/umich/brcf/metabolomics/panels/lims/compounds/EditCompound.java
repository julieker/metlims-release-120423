// Revisited : October 2016 (JW)

////////////////////////////////////////////////////
// EditCompound.java

// Updated by Julie Keros Mar 4, 2020
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.lims.compounds;

import java.util.Arrays;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import org.h2.util.StringUtils;

import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.metabolomics.layers.domain.CompoundName;
import edu.umich.brcf.metabolomics.layers.service.CompoundNameService;
import edu.umich.brcf.metabolomics.layers.service.CompoundService;
import edu.umich.brcf.shared.layers.dto.CompoundDTO;
import edu.umich.brcf.shared.util.utilpackages.CompoundIdUtils;

import org.apache.wicket.markup.html.form.ChoiceRenderer;

public class EditCompound extends WebPage
	{
	@SpringBean
	CompoundService cmpService;
	
	@SpringBean
	CompoundNameService cnameService;
	String impCompound;
	TextField smilesFld;
	TextField inchiKeyFld;
	//TextField casFld;
	EditCompound editCompound = this;// issue 27 2020
	public EditCompound(Page backPage, final CompoundDetailPanel container, final ModalWindow window) 
		{
		CompoundDTO cmpDto = new CompoundDTO();
		add(new FeedbackPanel("feedback"));
		add(new Label("titleLabel", "Add Compound"));
		add(new EditCompoundForm("editCompoundForm", "to be assigned", cmpDto, backPage, container, window,editCompound ));
		}
		
	public EditCompound(Page backPage, IModel cmpModel, CompoundDetailPanel container, final ModalWindow window) 
		{
		Compound cmp = (Compound) cmpModel.getObject();
		add(new FeedbackPanel("feedback"));
		add(new Label("titleLabel", "Edit Compound"));
		add(new EditCompoundForm("editCompoundForm", cmp.getCid(), CompoundDTO.instance(cmp), backPage, container, window, editCompound));
		}
	
	public final class EditCompoundForm extends Form 
		{
		public EditCompoundForm(final String id, final String cid, final CompoundDTO cmpDto, final Page backPage, final CompoundDetailPanel container, final ModalWindow window, EditCompound editCompound) // issue 27 2020
			{
			super(id, new CompoundPropertyModel(cmpDto));
			// issue 31 2020
			if (!StringUtils.isNullOrEmpty(cmpDto.getInchiKey()) ||  !StringUtils.isNullOrEmpty(cmpDto.getSmiles())  ||   !StringUtils.isNullOrEmpty(cmpDto.getChem_abs_number()))
			    cmpDto.setCompoundIdentifier(!StringUtils.isNullOrEmpty(cmpDto.getSmiles()) ? "Smiles" : (!StringUtils.isNullOrEmpty(cmpDto.getInchiKey()) ? "InchiKey" : "CAS")); 	
			add(new Label("cid", cid));
			add(newRequiredTextField("chem_abs_number", 30));
			// issue 8
			add(smilesFld =new TextField("smiles")
			    {
				@Override
				public boolean isEnabled()
					{ 
					if (StringUtils.isNullOrEmpty(cmpDto.getCompoundIdentifier()))
						return false;
					return cmpDto.getCompoundIdentifier().equals("Smiles");
					}	
				}				
		        );
			// issue 27 2020
			smilesFld.add(StringValidator.maximumLength(500));
			smilesFld.setOutputMarkupId(true);
			// issue 27 2020
			add(inchiKeyFld =new TextField("inchiKey")
				{
				public boolean isEnabled()
					{ 
					if (StringUtils.isNullOrEmpty(cmpDto.getCompoundIdentifier()))
						return false;
					return cmpDto.getCompoundIdentifier().equals("InchiKey");
					}	
				}				
			    );
			inchiKeyFld.add(StringValidator.maximumLength(500));
			inchiKeyFld.setOutputMarkupId(true);
			DropDownChoice compoundIdentifierDD = new DropDownChoice("compoundIdentifier",  Arrays.asList(new String[] { "InchiKey", "Smiles", "CAS"}));
			compoundIdentifierDD.add(buildStandardFormComponentUpdateBehavior("change", "updateForcompoundIdentifier", cmpDto, container, editCompound )); // issue 27 2020
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
					return (Compound.Human_Rel_Types.get(index));
					}
				});
			humanRelDD.setRequired(true);
			add(humanRelDD);
			add (compoundIdentifierDD); // issue 27 2020
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
			// issue 39
			add( new AjaxLink<Void>("close")
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
				});
			
			add(new Button("saveChanges")
				{
				public void onSubmit() 
					{
					String smilesStr = "";
					String smilesOrSmilesFromCompoundIdStr = "";
					CompoundDTO cmpDto = (CompoundDTO) getForm().getModelObject();
					boolean err = false;
					
					// issue 27 2020
					// issue 31 2020
					if (cmpDto.getCompoundIdentifier().equals("InchiKey"))
						cmpDto.setSmiles(null);
					else if (cmpDto.getCompoundIdentifier().equals("CAS"))
						{
						cmpDto.setSmiles(null);
						cmpDto.setInchiKey(null);
						}
					else if (cmpDto.getCompoundIdentifier().equals("Smiles"))
						cmpDto.setInchiKey(null);
					// issue 27 2020 
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
						try 
						    {	
							// issue 27 2020
							// issue 31 2020
							smilesOrSmilesFromCompoundIdStr = "";
							if (StringUtils.isNullOrEmpty(cmpDto.getSmiles()) && cmpDto.getCompoundIdentifier().equals("Smiles") )
								cmpDto.setCompoundIdentifier("CAS");
							if (cmpDto.getCompoundIdentifier().equals("InchiKey") && !StringUtils.isNullOrEmpty(cmpDto.getInchiKey()))
							    smilesOrSmilesFromCompoundIdStr = CompoundIdUtils.grabSmilesFromCompoundId(cmpDto.getInchiKey(), "inchiKey").get(0);
							else if (cmpDto.getCompoundIdentifier().equals("Smiles") && !StringUtils.isNullOrEmpty(cmpDto.getSmiles()))
								smilesOrSmilesFromCompoundIdStr = cmpDto.getSmiles();
							else
								smilesOrSmilesFromCompoundIdStr = CompoundIdUtils.grabSmilesFromCompoundId(cmpDto.getChem_abs_number(), "cas").get(0);							
							// issue 31 2020
							Compound cmp = cmpService.save(cmpDto, smilesOrSmilesFromCompoundIdStr);										
							if (cmpDto.getCompoundIdentifier().equals("InchiKey") && StringUtils.isNullOrEmpty(smilesOrSmilesFromCompoundIdStr) && !StringUtils.isNullOrEmpty(cmp.getInchiKey()) )
								EditCompound.this.info("The InchiKey:" + cmp.getInchiKey() + " could not be found but the Compound/Name detail is saved")  ;  	
							else if (cmpDto.getCompoundIdentifier().equals("CAS") && StringUtils.isNullOrEmpty(smilesOrSmilesFromCompoundIdStr) && !StringUtils.isNullOrEmpty(cmp.getChem_abs_number()) )
								EditCompound.this.info("The CAS:" + cmp.getChem_abs_number() + " could not be found but the Compound/Name detail is saved")  ;	
							else
							    EditCompound.this.info("Compound/Name detail saved.");
							if (container!=null)
								{
								container.setCmpId(cmp.getCid());
								impCompound=cmp.getCid();
								}
							}
						catch(Exception e){ e.printStackTrace(); EditCompound.this.error("Save unsuccessful. Please make sure that smiles is valid."); }
						}					
					setResponsePage(getPage());
					}
				
				public void onError(AjaxRequestTarget target, Form form)
					{
					target.add(EditCompound.this.get("feedback")); 
					}
				});					
			}
	
		// Issue 27 2020
		// issue 31 2020
		private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(String event, final String response, final CompoundDTO cmpDTO, CompoundDetailPanel container , final EditCompound editCompound)
			{
			return new AjaxFormComponentUpdatingBehavior(event)
			    {
			    @Override
			    protected void onUpdate(AjaxRequestTarget target)
			    	{
			    	switch (response)
			        	{
			        	case "updateForcompoundIdentifier":
			                target.add(editCompound.smilesFld);
			                target.add(editCompound.inchiKeyFld);
			        	break;		        	
			        	default : break;
			        	}
			    	}
			    };
			}
		
		private RequiredTextField newRequiredTextField(String id,  int maxLength) 
			{
			RequiredTextField textField = new RequiredTextField(id);
			textField.add(StringValidator.maximumLength(maxLength));
			return textField;
			}
		}
	}

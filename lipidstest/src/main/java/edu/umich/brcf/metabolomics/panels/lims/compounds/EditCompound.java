// Revisited : October 2016 (JW)
// Updated by Julie Keros May 11, 2020

////////////////////////////////////////////////////
// EditCompound.java

// Updated by Julie Keros Mar 4, 2020
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.lims.compounds;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
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
import edu.umich.brcf.shared.util.utilpackages.NumberUtils;


public class EditCompound extends WebPage
	{
	@SpringBean
	CompoundService cmpService;
	
	@SpringBean
	CompoundNameService cnameService;
	String impCompound;
	TextField smilesFld;
	TextField inchiKeyFld;
	TextField htmlFld;
	TextField nameFld;
	TextField molecularWeightFld;
	TextField additionalSolubilityFld ; // issue 62
	//TextField casFld;
	EditCompound editCompound = this;// issue 27 2020
	public EditCompound(Page backPage, final CompoundDetailPanel container, final ModalWindow window) 
		{
		CompoundDTO cmpDto = new CompoundDTO();
		add(new FeedbackPanel("feedback").setEscapeModelStrings(false)); // issue 79
		add(new Label("titleLabel", "Add Compound"));
		add(new EditCompoundForm("editCompoundForm", "to be assigned", cmpDto, backPage, container, window,editCompound ));
		}
		
	public EditCompound(Page backPage, IModel cmpModel, CompoundDetailPanel container, final ModalWindow window) 
		{
		Compound cmp = (Compound) cmpModel.getObject();
		add(new FeedbackPanel("feedback").setEscapeModelStrings(false));
		add(new Label("titleLabel", "Edit Compound"));
		add(new EditCompoundForm("editCompoundForm", cmp.getCid(), CompoundDTO.instance(cmp), backPage, container, window, editCompound));
		}
	
	public final class EditCompoundForm extends Form 
		{
		String cidAssigned = "";		
		public EditCompoundForm(final String id, final String cid, final CompoundDTO cmpDto, final Page backPage, final CompoundDetailPanel container, final ModalWindow window, final EditCompound editCompound) // issue 27 2020
			{
			super(id, new CompoundPropertyModel(cmpDto));
			// issue 31 2020
			if (!StringUtils.isNullOrEmpty(cmpDto.getInchiKey()) ||  !StringUtils.isNullOrEmpty(cmpDto.getSmiles())  ||   !StringUtils.isNullOrEmpty(cmpDto.getChem_abs_number()))
			    cmpDto.setCompoundIdentifier(!StringUtils.isNullOrEmpty(cmpDto.getSmiles()) ? "Smiles" : (!StringUtils.isNullOrEmpty(cmpDto.getInchiKey()) ? "InchiKey" : "CAS")); 	
			add(new Label("cid", cid));
			add(newRequiredTextField("chem_abs_number", 30));
			// issue 79 			
			add(new Label("molecularweightLabel", "Molecular Weight:")
				{
				public boolean isVisible()
					{
					return ( !(cid.equals("to be assigned") && StringUtils.isNullOrEmpty(cidAssigned)))	;
					}
				});					
			add (molecularWeightFld = new TextField ("molecular_weight")
				{
				@Override
				public boolean isVisible()
					{
					return ( !(cid.equals("to be assigned") && StringUtils.isNullOrEmpty(cidAssigned)))	;
					}
				});
			 // issue 79 edit molecular weight
			molecularWeightFld.setType(BigDecimal.class);  // issue 79
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
				});
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
			// issue 58
			add (compoundIdentifierDD); // issue 27 2020
			TextField parentCid;
			add(parentCid = new TextField("parentCid")
				{
				@Override
				public boolean isRequired() {	return false; }
				});
			parentCid.add(StringValidator.maximumLength(6));
			// issue 57
			add(nameFld = new TextField("name")
				{
				@Override
				public boolean isRequired() 
				    { 
					return ( cid!=null && cid.equals("to be assigned")); 
				    }
				@Override
				public boolean isEnabled() 
				    { 
					return ( StringUtils.isNullOrEmpty(cidAssigned)); 
				    }				
				}
			    ) ;
			nameFld.add(StringValidator.maximumLength(500));				
			add(new DropDownChoice("type", CompoundName.TYPES)
				{
				@Override
				public boolean isRequired() 
					{ 
					return (cid!=null && cid.equals("to be assigned")); 
					}
				public boolean isEnabled() 
			    	{ 
					return ( StringUtils.isNullOrEmpty(cidAssigned)); 
			    	}	
				}
			    );			
			add(htmlFld =new TextField("html")
			    {
				@Override
				// issue 57
				public boolean isEnabled() 
				    { 
					return false; 
				    }	
				}
			    );
			htmlFld.add(StringValidator.maximumLength(500));			
			// issue 62
			add(additionalSolubilityFld =new TextField("additionalSolubility"));
			additionalSolubilityFld.add(StringValidator.maximumLength(500));
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
			// issue 57
			add(new Button("saveChanges")
				{
				public void onSubmit() 
					{
					// issue 57
					editCompound.htmlFld.setDefaultModelObject(editCompound.nameFld.getDefaultModelObjectAsString());				
					String smilesStr = "";
					String smilesOrSmilesFromCompoundIdStr = "";
					CompoundDTO cmpDto = (CompoundDTO) getForm().getModelObject();
					boolean err = false;
					// issue 41
					if(cmpDto.getCid()!=null )
						if (cmpDto.getName()!=null && cmpDto.getName().length()>0)
							{
							for (CompoundName cmpName : cnameService.loadByCid(cmpDto.getCid())) 
								{
								if ((cmpName.getNameType().equals("pri"))&&(cmpDto.getType().equals("pri"))  )
									{
									if (StringUtils.isNullOrEmpty(cidAssigned))
									    EditCompound.this.error("Compound cannot have more than one primary name.");
									err=true;
									}
								if (cmpName.getName().equalsIgnoreCase(cmpDto.getName()) )
									{
									if (StringUtils.isNullOrEmpty(cidAssigned))
									    EditCompound.this.error("Compound cannot have duplicate names.");
									err=true;
									}
								}
							}
					if (!err || (!StringUtils.isNullOrEmpty(cidAssigned)))
						{
						try 
						    {	
							// issue 27 2020
							// issue 31 2020
							smilesOrSmilesFromCompoundIdStr = "";
							if (StringUtils.isNullOrEmpty(cmpDto.getSmiles()) && cmpDto.getCompoundIdentifier().equals("Smiles") )
								cmpDto.setCompoundIdentifier("CAS");
							if (cmpDto.getCompoundIdentifier().equals("InchiKey") && !StringUtils.isNullOrEmpty(cmpDto.getInchiKey()))
								{
								List <String> smilesInchiKeyList = CompoundIdUtils.grabSmilesFromCompoundId(cmpDto.getInchiKey(), "inchiKey");
								smilesOrSmilesFromCompoundIdStr = smilesInchiKeyList.get(0);			
							    // issue 36
								if (StringUtils.isNullOrEmpty(smilesInchiKeyList.get(1)) && StringUtils.isNullOrEmpty(cmpDto.getSmiles()))
									cmpDto.setSmiles(smilesOrSmilesFromCompoundIdStr);
								}
							else if (cmpDto.getCompoundIdentifier().equals("Smiles") && !StringUtils.isNullOrEmpty(cmpDto.getSmiles()))
								{
								// issue 36
								List <String> smilesInchiKeyList = CompoundIdUtils.grabSmilesFromCompoundId(cmpDto.getSmiles(), "smiles");
								smilesOrSmilesFromCompoundIdStr = cmpDto.getSmiles();
								if (StringUtils.isNullOrEmpty(cmpDto.getInchiKey()))
								    cmpDto.setInchiKey(smilesInchiKeyList.get(2));
								}
							else
								// issue 33
								// issue 36
							    {
								List <String> smilesInchiKeyList = CompoundIdUtils.grabSmilesFromCompoundId(cmpDto.getChem_abs_number(), "cas");
								smilesOrSmilesFromCompoundIdStr = smilesInchiKeyList.get(0);
								// issue 45 weird case where CAS is invalid and there is a smiles string
								if (StringUtils.isNullOrEmpty(smilesOrSmilesFromCompoundIdStr))
									if (!StringUtils.isNullOrEmpty(cmpDto.getSmiles()))
										smilesOrSmilesFromCompoundIdStr = cmpDto.getSmiles();
								//// signal that the cas is invalid
								if (StringUtils.isNullOrEmpty(smilesInchiKeyList.get(1)) )
								    {		
									if (StringUtils.isNullOrEmpty(cmpDto.getSmiles()))
								        cmpDto.setSmiles(smilesOrSmilesFromCompoundIdStr);
									if (StringUtils.isNullOrEmpty(cmpDto.getInchiKey()))
									    cmpDto.setInchiKey(smilesInchiKeyList.get(2));
									}
							    }
							// issue 43
							if (cmpDto.getCompoundIdentifier().equals("InchiKey") && StringUtils.isNullOrEmpty(smilesOrSmilesFromCompoundIdStr) && !StringUtils.isNullOrEmpty(cmpDto.getInchiKey()) )
							    {	
								EditCompound.this.info("The InchiKey:" + cmpDto.getInchiKey() + " could not be found.  The Compound/Name detail is not saved.  Please choose another inchiKey.")  ; 
								setResponsePage(getPage());
								return;
							    }
							// issue 79
							if (!StringUtils.isNullOrEmpty(cmpDto.getMolecular_weight()))
								cmpDto.setMolecular_weight(cmpDto.getMolecular_weight().replace(",",  ""));
							if (!StringUtils.isNullOrEmpty(cmpDto.getMolecular_weight()) && !NumberUtils.verifyDecimalRange(cmpDto.getMolecular_weight(), 5, 5))
								{
								EditCompound.this.info("Please make sure that the molecular weight has no more than 5 digits for the whole number and 5 digits for the decimal place");
								setResponsePage(getPage());
								return;
								}
							// issue 31 2020
							// issue 41 2020
							Compound cmp = cmpService.save(cmpDto, smilesOrSmilesFromCompoundIdStr, cidAssigned, err);										
							if (cmp.getCid() != null && (cmpDto.getCid()== null || cmpDto.getCid().equals("to be assigned")))
							    {
								cidAssigned = cmp.getCid();
								cmpDto.setCid(cidAssigned);
								cmpDto.setMolecular_weight(Double.toString(cmp.getMass(smilesOrSmilesFromCompoundIdStr)));					
							    }
							// issue 21
							if (cmpDto.getCompoundIdentifier().equals("CAS") && StringUtils.isNullOrEmpty(smilesOrSmilesFromCompoundIdStr) && !StringUtils.isNullOrEmpty(cmpDto.getChem_abs_number()) )
								EditCompound.this.info(  "The CAS:" + cmpDto.getChem_abs_number() + " could not be found.  The Compound/Name detail is saved for compound:" + cmp.getCid() + "." )  ;
							else
							    EditCompound.this.info("<span style=\"color:blue;\">" + "Compound/Name detail saved for compound:" + cmp.getCid() + "." +  "</span>");
							if (container!=null)
								{
								container.setCmpId(cmp.getCid());
								impCompound=cmp.getCid();
								}
							String calcMolWeight = Double.toString(cmp.getMass(smilesOrSmilesFromCompoundIdStr));
							if (StringUtils.isNullOrEmpty(cmpDto.getMolecular_weight()))
							    cmpDto.setMolecular_weight(calcMolWeight);
							if (!StringUtils.isNullOrEmpty(cmpDto.getMolecular_weight()) && !calcMolWeight.toString().equals(cmpDto.getMolecular_weight()))
								EditCompound.this.info("The molecular weight of " + cmpDto.getMolecular_weight() + " is different from the calculated molecular weight of: " + calcMolWeight.toString() + ".  Please double check the molecular weight.");			
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

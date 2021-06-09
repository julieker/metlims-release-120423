// Revisited : October 2016 (JW)
// Updated by Julie Keros May 11, 2020

////////////////////////////////////////////////////
// EditCompoundDialog.java issue 113

// Created by Julie Keros Dec 23, 2020
// issue 113
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.lims.compounds;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import org.h2.util.StringUtils;

import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.metabolomics.layers.domain.CompoundName;
import edu.umich.brcf.metabolomics.layers.service.CompoundNameService;
import edu.umich.brcf.metabolomics.layers.service.CompoundService;
import edu.umich.brcf.shared.layers.dto.CompoundDTO;
import edu.umich.brcf.shared.util.FieldLengths;
import edu.umich.brcf.shared.util.utilpackages.CompoundIdUtils;
// issue 113
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.kendo.ui.form.button.Button;

public abstract class EditCompoundDialog extends AbstractFormDialog
	{
	@SpringBean
	CompoundService cmpService;
	@SpringBean
	CompoundNameService cnameService;
	String custCompound = "";
	public Form<?> form;
	int chemAbsNumberLength = 30;
	String impCompound;
	String childCompound;
	TextField compoundIdCustomFld;
	TextField smilesFld;
	TextField inchiKeyFld;
	TextField htmlFld;
	TextField nameFld;
	TextField molecularWeightFld;
	TextField additionalSolubilityFld ; // issue 62
	TextField chemAbsNumberFld;
	CompoundDetailPanel mainContainer;
	CompoundDTO mainDto;
	ModalWindow mainWindow;
	String cidAssigned = "";
	public DialogButton submitButton = new DialogButton("submit", "Done");
	//TextField casFld;
	EditCompoundDialog editCompoundDialog = this;// issue 27 2020

	public EditCompoundDialog(String id, String title, final CompoundDetailPanel container, final ModalWindow window, CompoundDTO cmpDto) 
		{	
		super(id, title,  true);
	    cmpDto = new CompoundDTO();
		add(new FeedbackPanel("feedback").setEscapeModelStrings(false)); // issue 79
		this.form = new EditCompoundFormDialog("editCompoundForm", "to be assigned", cmpDto,  container, window,editCompoundDialog );		    
		this.add(this.form);
		}
		
	public  class EditCompoundFormDialog extends Form 
		{			
		public EditCompoundFormDialog(final String id, final String cid, final CompoundDTO cmpDto,  final CompoundDetailPanel container, final ModalWindow window, final EditCompoundDialog editCompoundDialog) // issue 27 2020
			{
			super(id, new CompoundPropertyModel(cmpDto));
			mainContainer = container;
			mainWindow = window;
			if (!StringUtils.isNullOrEmpty(cmpDto.getInchiKey()) ||  !StringUtils.isNullOrEmpty(cmpDto.getSmiles())  ||   !StringUtils.isNullOrEmpty(cmpDto.getChem_abs_number()))
			    cmpDto.setCompoundIdentifier(!StringUtils.isNullOrEmpty(cmpDto.getSmiles()) ? "Smiles" : (!StringUtils.isNullOrEmpty(cmpDto.getInchiKey()) ? "InchiKey" : "CAS")); 	
			//add(new Label("cid", cid));
			chemAbsNumberFld = new TextField("chem_abs_number");
			chemAbsNumberFld.add(StringValidator.maximumLength(chemAbsNumberLength));
			add(chemAbsNumberFld);	
			add(new Label("compoundIdCustomLabel", "Compound Id:")
				{
				});	
			
			compoundIdCustomFld = new TextField ("customCid");
			add (compoundIdCustomFld);
			compoundIdCustomFld.add(StringValidator.maximumLength(FieldLengths.COMPOUND_ID_LENGTH));
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
			molecularWeightFld.setType(BigDecimal.class);  // issue 79
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
			compoundIdentifierDD.add(buildStandardFormComponentUpdateBehavior("change", "updateForcompoundIdentifier", cmpDto, container, editCompoundDialog )); 
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
				}) ;
			nameFld.add(StringValidator.maximumLength(500));				
			add(new DropDownChoice("type", CompoundName.TYPES)
				{
				});			
			add(htmlFld =new TextField("html")
			    {
				@Override
				public boolean isEnabled() 
				    { 
					return false; 
				    }	
				});
			htmlFld.add(StringValidator.maximumLength(500));			
			add(additionalSolubilityFld =new TextField("additionalSolubility"));
			additionalSolubilityFld.add(StringValidator.maximumLength(500));
			// issue 39
			} // end of form constructor
	
		//////////// issue 113		
		// Issue 27 2020
		// issue 31 2020
		private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(String event, final String response, final CompoundDTO cmpDTO, CompoundDetailPanel container , final EditCompoundDialog editCompoundDialog)
			{
			return new AjaxFormComponentUpdatingBehavior(event)
			    {
			    @Override
			    protected void onUpdate(AjaxRequestTarget target)
			    	{
			    	switch (response)
			        	{
			        	case "updateForcompoundIdentifier":
			                target.add(editCompoundDialog.smilesFld);
			                target.add(editCompoundDialog.inchiKeyFld);
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
		} // end form class

	@Override
	public Form<?> getForm() 
		{
		return this.form;
		}
	@Override
	public DialogButton getSubmitButton() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onError(AjaxRequestTarget target, DialogButton button) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target, DialogButton button) {
		// TODO Auto-generated method stub
		
	}
	
	///// issue  113
	public void reinitialize ()
		{	
		editCompoundDialog.htmlFld.setDefaultModelObject("");	
		editCompoundDialog.nameFld.setDefaultModelObject("");
		editCompoundDialog.smilesFld.setDefaultModelObject("");
		editCompoundDialog.inchiKeyFld.setDefaultModelObject("");
		editCompoundDialog.chemAbsNumberFld.setDefaultModelObject("");
		editCompoundDialog.molecularWeightFld.setDefaultModelObject("");
		editCompoundDialog.additionalSolubilityFld.setDefaultModelObject("");
		editCompoundDialog.compoundIdCustomFld.setDefaultModelObject("");
		}
	
	///// issue 113
	public String doEditChecks (CompoundDTO cmpDto)
		{
		if (StringUtils.isNullOrEmpty(cmpDto.getName()))
		    return "1";
		else if (StringUtils.isNullOrEmpty(cmpDto.getChem_abs_number()))
			return "2";
		else if (StringUtils.isNullOrEmpty(cmpDto.getType()))
			return "3";
		else 
			return "0";
		}
	///// issue 113	
	public String saveCompound (CompoundDTO cmpDto)
		{
		// issue 57
		editCompoundDialog.htmlFld.setDefaultModelObject(editCompoundDialog.nameFld.getDefaultModelObjectAsString());				
		String smilesStr = "";
		String smilesOrSmilesFromCompoundIdStr = "";
		custCompound = StringUtils.isNullOrEmpty(editCompoundDialog.compoundIdCustomFld.getDefaultModelObjectAsString()) ? null : editCompoundDialog.compoundIdCustomFld.getDefaultModelObjectAsString() ;
		if (cmpService.doesCompoundIdAlreadyExist(custCompound))
			return "The compound:" + custCompound + " already exists.  Please choose another.";
		cmpDto = (CompoundDTO) getForm().getModelObject();
		if (StringUtils.isNullOrEmpty(cmpDto.getCompoundIdentifier()))
			return "Please choose InchiKey, Smiles, or CAS from the drop down list";
		String editChk = doEditChecks(cmpDto);
		if (! editChk.equals("0") )
			return "Please specify a value for the " + (editChk.equals("1") ? "name" : (editChk.equals("2") ? "CAS number" : "type"    )) ;
		cmpDto.setCid(null);
	    boolean err = false;
		// issue 41
		if (!err || (!StringUtils.isNullOrEmpty(cidAssigned)))
			{
			try 
			    {	
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
					return "The InchiKey:" + cmpDto.getInchiKey() + " could not be found.  The Compound/Name detail is not saved.  Please choose another inchiKey.";
				    }
				// issue 79
				// issue 31 2020
				// issue 41 2020
				// issue 144
				//editCompoundDialog.nameFld.getDefaultModelObjectAsString()			
				Compound cmp = cmpService.save(cmpDto, smilesOrSmilesFromCompoundIdStr, cidAssigned, err, custCompound);										
				if (cmp.getCid() != null && (cmpDto.getCid()== null || cmpDto.getCid().equals("to be assigned")))
					cmpDto.setMolecular_weight(Double.toString(cmp.getMass(smilesOrSmilesFromCompoundIdStr)));					
				childCompound = cmp.getCid();
				if (mainContainer!=null)
					{
					mainContainer.setCmpId(cmp.getCid());
					impCompound=cmp.getCid();
					childCompound = cmp.getCid();
					}
				if (cmpDto.getCompoundIdentifier().equals("CAS") && StringUtils.isNullOrEmpty(smilesOrSmilesFromCompoundIdStr) && !StringUtils.isNullOrEmpty(cmpDto.getChem_abs_number()) )					
					return "The CAS:" + cmpDto.getChem_abs_number() + " could not be found.  The Compound/Name detail is saved for compound:" + cmp.getCid() + "." ;
				String calcMolWeight = Double.toString(cmp.getMass(smilesOrSmilesFromCompoundIdStr));
				if (StringUtils.isNullOrEmpty(cmpDto.getMolecular_weight()))
				    cmpDto.setMolecular_weight(calcMolWeight);
				if (!StringUtils.isNullOrEmpty(cmpDto.getMolecular_weight()) && !calcMolWeight.toString().equals(cmpDto.getMolecular_weight()))
					EditCompoundDialog.this.info("The molecular weight of " + cmpDto.getMolecular_weight() + " is different from the calculated molecular weight of: " + calcMolWeight.toString() + ".  Please double check the molecular weight.");			
				}
			catch(Exception e)
			    { 
				e.printStackTrace(); 
				EditCompoundDialog.this.error("Save unsuccessful. Please make sure that smiles is valid."); 
				}
			}		
	   // put this back setResponsePage(getPage());
	    return "0";
	    }	
	}

////////////////////////////////////////////////////
// EditAliquot.java
// 
// Created by Julie Keros June 1st, 2020
////////////////////////////////////////////////////

// issue 61
package edu.umich.brcf.metabolomics.panels.lims.compounds;

/*****************
 * Created by Julie Keros
 * Aug 20 2020
 * For Aliquot processing
 ********************/
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import org.h2.util.StringUtils;
import edu.umich.brcf.metabolomics.layers.service.CompoundService;
import edu.umich.brcf.metabolomics.layers.service.InventoryService;
import edu.umich.brcf.shared.layers.domain.Aliquot;
import edu.umich.brcf.shared.layers.domain.Inventory;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.LocationService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.utilpackages.NumberUtils;
import edu.umich.brcf.shared.util.widgets.METWorksAjaxUpdatingDateTextField;
import edu.umich.brcf.shared.layers.dto.AliquotDTO;
public class EditAliquot extends WebPage
	{
	@SpringBean
	AliquotService aliquotService;	
	@SpringBean
	AssayService assayService;	
	@SpringBean
	LocationService locationService;
	@SpringBean
	InventoryService inventoryService;
	@SpringBean
	UserService userService;
	@SpringBean
	CompoundService compoundService;
	// issue 79
	FeedbackPanel aFeedback;
	String location ;
	TextField aliquotIdTxt;
	TextField ivolTxt;
	TextField iconTxt;
	TextField dconTxt;	
	RequiredTextField weightedAmountTxt;
	RequiredTextField molecularWeightTxt;
	TextField dConcTxt;	
	TextField dvol;
	Label weightedAmountLabel;	
	Label   ivolLabel;
	Label   dvolLabel;
	Label ivolUnits;
	Label dvolUnits;
	Label iconLabel;
	Label iconUnits;
	Label dconLabel;
	Label dconUnits;
	Label dConcLabel;
	Label molecularWeightLabel;
	TextField numAliquotsTxt;
	TextField solventTxt;
	TextField otherSolvent;
	Label initialVolumeLabel;
	Label volUnitsLabel;
	Label numAliquotsLabel;
	Label otherSolventLabel;
	Label solventLabel;
	Label calcSectionLabel;
	Label dilutionFormulaLabel;
	Label dVoluLabel;
	Label isDryLabel;
	DropDownChoice<String> aliquotNeatOrDilutionDD;
	DropDownChoice<String> aliquotNeatOrDilutionUnitsDD;
	DropDownChoice<String> selectedParentInventroyDrop;	
	DropDownChoice<String> aliquotUnitDD;
	DropDownChoice<String> locationsDD;
	DropDownChoice<String> solventDD;
	DropDownChoice<String> dConcentrationUnitsDD;
	DropDownChoice<String> weightedAmountUnitsDD;
	AjaxCheckBox dryCheckBox;
	EditAliquot editAliquot = this;// issue 61 2020
	AliquotDTO aliquotDto = new AliquotDTO();
	Button calculateNeatButton;
	Button calculateDilutionButton;
	Button saveChangesButton;
	WebMarkupContainer dilutionContainer;
	int maxSolventLength = 94; // issue 79 allow for OTHER:	
	boolean calculateOnly = true;
	ListMultipleChoice<String> selectedAssays;
	boolean isAssayListUpdated = false;
	EditAliquotForm editAliquotForm;
	
	public AliquotDTO getAliquotDto() { return aliquotDto; }
	public void setAliquotDto(AliquotDTO aliquotDto)  { this.aliquotDto = aliquotDto; }
		
	public EditAliquot(Page backPage, final InventoryDetailPanel detailPanel, final ModalWindow window) 
		{		
		aFeedback = new FeedbackPanel("feedback");
		aFeedback.setEscapeModelStrings(false);		
		add(aFeedback);		
		add(new Label("titleLabel", "Add Aliquot"));
		add(editAliquotForm = new EditAliquotForm("editAliquotForm", "to be assigned", aliquotDto, backPage, detailPanel, window,editAliquot, null ));
		}
		
	public EditAliquot(Page backPage, IModel cmpModel, InventoryDetailPanel detailPanel, final ModalWindow window) 
		{
		this (backPage,cmpModel,detailPanel,window, false);
		}
	
	public EditAliquot(Page backPage, IModel cmpModel, InventoryDetailPanel detailPanel, final ModalWindow window, boolean isViewOnly) 
		{
		Aliquot alq = (Aliquot) cmpModel.getObject();	
		aFeedback = new FeedbackPanel("feedback");
		aFeedback.setEscapeModelStrings(false);		
		add(aFeedback);	
		add(new Label("titleLabel", isViewOnly || !(userService.isAliquotAdmin(((MedWorksSession) Session.get()).getCurrentUserId()) || alq.getCreatedBy().equals(((MedWorksSession) Session.get()).getCurrentUserId())) ? "View Aliquot" : "Edit Aliquot"));
		setAliquotDto(AliquotDTO.instance(alq));
		// issue 196
		add(editAliquotForm = new EditAliquotForm("editAliquotForm", alq.getAliquotId(), aliquotDto, backPage, detailPanel, window, editAliquot, alq, isViewOnly));
		}
	
	public final class EditAliquotForm extends Form 
		{
		DropDownChoice selectedParentInventoryDD;
		String aliquotIdAssigned = "";	
		String unit= "";
		boolean isNoInventory = false; // issue 196
		AjaxCheckBox isNoInventoryCheckBox; // issue 196
		public EditAliquotForm(final String id, final String aliquotId, final AliquotDTO aliquotDto, final Page backPage, final InventoryDetailPanel detailPanel, final ModalWindow window, final EditAliquot editAliquot, final Aliquot alq) // issue 27 2020
			{
			this (id, aliquotId, aliquotDto,backPage, detailPanel, window, editAliquot, alq, false);
			}
		
		public EditAliquotForm(final String id, final String aliquotId, final AliquotDTO aliquotDto, final Page backPage, final InventoryDetailPanel detailPanel, final ModalWindow window, final EditAliquot editAliquot, final Aliquot alq, final boolean isViewOnly) // issue 27 2020
			{
			super(id, new CompoundPropertyModel(aliquotDto));
			
			
			dilutionContainer = new WebMarkupContainer("dilutionContainer")
				{
				public boolean isVisible()
					{
					if (aliquotDto.getNeatOrDilution() == null)
						return false;
					if (aliquotDto == null)
						return false;
					return (aliquotDto.getNeatOrDilutionText().equals("Dilution") );
					}
			     };
			add(dilutionContainer);
			dilutionContainer.setOutputMarkupId(true);
			dilutionContainer.setOutputMarkupPlaceholderTag(true);			
			numAliquotsLabel = new Label("numAliquotsLabel", "Number of Aliquots:")
				{
				@Override
					public boolean isVisible()
					{
					if (alq == null && aliquotDto.getReplicate() != null && aliquotDto.getReplicate() > 1  && !calculateOnly)
						return false;
					return alq == null && StringUtils.isNullOrEmpty(aliquotIdAssigned);	
					}
				};
			add(numAliquotsLabel);
			numAliquotsLabel.setOutputMarkupId(true);
			numAliquotsLabel.setOutputMarkupPlaceholderTag(true) ;							
			numAliquotsTxt = new TextField("replicate")
				{
				@Override
				public boolean isVisible()
					{
					if (alq == null && aliquotDto.getReplicate() != null && aliquotDto.getReplicate() > 1  && !calculateOnly)
						return false;
					return alq == null && StringUtils.isNullOrEmpty(aliquotIdAssigned) ;		
					}
				};			
			numAliquotsTxt.setType(Integer.class);				
			add (numAliquotsTxt);	
			numAliquotsTxt.setOutputMarkupId(true);
			numAliquotsTxt.setOutputMarkupPlaceholderTag(true) ;					
			// issue 61 2020
			add(new Label("aliquotId", aliquotId));			
			add(new Label("cid", detailPanel.getCompound().getCid()));
			add(new Label("userName", alq == null ? userService.getFullNameByUserId(((MedWorksSession) getSession()).getCurrentUserId()) : userService.getFullNameByUserId(alq.getCreatedBy()) ));
			TextArea textAreaNotes = new TextArea("notes");
			textAreaNotes.add(StringValidator.maximumLength(4000));
			add(textAreaNotes);
		    // issue 61	
			aliquotNeatOrDilutionDD = new DropDownChoice("neatOrDilutionText",  Arrays.asList(new String[] {"Choose One", "Neat", "Dilution" }))
				{
			    @Override
	            protected CharSequence getDefaultChoice(String selectedValue) 
			    	{
	                return "";
			    	} 				
				};					
			aliquotNeatOrDilutionDD.add(buildStandardFormComponentUpdateBehavior("change", "updateNeatOrDilution", aliquotDto, detailPanel, editAliquot )); // issue 27 2020	
			aliquotNeatOrDilutionDD.setOutputMarkupId(true);	
			aliquotNeatOrDilutionDD.setRequired(true);
			add (aliquotNeatOrDilutionDD);	
			aliquotNeatOrDilutionUnitsDD = new DropDownChoice("neatOrDilutionUnits", Arrays.asList(new String[] {"ug", "mg", "g" }) )
				{
				@Override
				public boolean isVisible()
					{
					if (aliquotDto.getNeatOrDilution() == null)
						return false;
					if (aliquotDto == null)
						return false;
					if (aliquotDto.getIsDry() == null)
						return false;
					return aliquotDto.getNeatOrDilutionText().equals("Dilution");	
					}
				};
			aliquotNeatOrDilutionUnitsDD.setOutputMarkupId(true);	
			aliquotNeatOrDilutionUnitsDD.setOutputMarkupPlaceholderTag(true) ;
			aliquotNeatOrDilutionUnitsDD.add(buildStandardFormComponentUpdateBehavior("change", "updateNeatOrDilutionUnits", aliquotDto, detailPanel, editAliquot )); // issue 27 2020
			aliquotNeatOrDilutionUnitsDD.setRequired(true);
			add (aliquotNeatOrDilutionUnitsDD);			
			METWorksAjaxUpdatingDateTextField dateFld =  new METWorksAjaxUpdatingDateTextField("createDate", new PropertyModel<String>(aliquotDto, "createDate"), "createDate")
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target)  
			        { 
			        }
				};		
			dateFld.setDefaultStringFormat(Aliquot.ALIQUOT_DATE_FORMAT);
			add(dateFld);	
			// issue 79
			solventDD = new DropDownChoice("solventText",  Arrays.asList(new String[] {"WATER", "ETHANOL", 
					                       "METHANOL", "CHLOROFORM", "ISOPROPANOL", "ACETONITRILE", "ETHYL ACETEATE", "OTHER"}))
					{
					@Override
					public boolean isVisible()
						{
						if (aliquotDto.getNeatOrDilution() == null)
							return false;
						if (aliquotDto == null)
							return false;
						if (aliquotDto.getIsDry() == null)
							return false;
						return ( aliquotDto.getNeatOrDilutionText().equals("Dilution") || (aliquotDto.getNeatOrDilutionText().equals("Neat") && !aliquotDto.getIsDry())    );
						} 
					};
			solventDD.add(buildStandardFormComponentUpdateBehavior("change", "updateSolvent", aliquotDto, detailPanel, editAliquot )); // issue 27 2020	
			solventDD.setOutputMarkupId(true);
			solventDD.setOutputMarkupPlaceholderTag(true) ;	
			add (solventDD);			
			otherSolvent = new TextField("otherSolvent")
				{
				@Override
				public boolean isVisible()
					{
					if ( aliquotDto.getSolventText() ==null)
						return false;	
					return aliquotDto.getSolventText().equals("OTHER");		
					}
				};	
		    add (otherSolvent);
			otherSolvent.setOutputMarkupId(true);
			otherSolvent.setOutputMarkupPlaceholderTag(true) ;	
			// issue 79 set up max length
			otherSolvent.add(StringValidator.maximumLength(maxSolventLength));
			otherSolventLabel = new Label("otherSolventLabel", "Other Solvent")
				{
				@Override
					public boolean isVisible()
					{
					if ( aliquotDto.getSolventText() ==null)
						return false;	
					return aliquotDto.getSolventText().equals("OTHER");		
					}
				};			
			add (otherSolventLabel);
			otherSolventLabel.setOutputMarkupId(true);
			otherSolventLabel.setOutputMarkupPlaceholderTag(true) ;	
			// Issue 79
			solventLabel = new Label("solventLabel", "Solvent:")
				{
				@Override
					public boolean isVisible()
						{
						if (aliquotDto.getNeatOrDilution() == null)
							return false;
						if (aliquotDto == null)
							return false;
						if (aliquotDto.getIsDry() == null)
							return false;
						return ( aliquotDto.getNeatOrDilutionText().equals("Dilution") || (aliquotDto.getNeatOrDilutionText().equals("Neat") && !aliquotDto.getIsDry())    );
						} 
				};			
			add (solventLabel);
			solventLabel.setOutputMarkupId(true);
			solventLabel.setOutputMarkupPlaceholderTag(true) ;			
			selectedParentInventoryDD = new DropDownChoice("parentId", getListOfInvIds(detailPanel.getCompound().getInventory()))
				{	
					public boolean isEnabled()
						{
						return !getIsNoInventory();
						}
								
				}
			     ;			
			add (selectedParentInventoryDD); 
			selectedParentInventoryDD.setOutputMarkupId(true);
			// issue 100
			selectedAssays = new ListMultipleChoice ("assayIds",assayService.allAssayNames() )	
			    {
				// issue 196
				public boolean isEnabled()
					{
					return !getIsNoInventory();
					}				
				@Override
				protected void onComponentTag(ComponentTag tag)
					{
					super.onComponentTag(tag);
					tag.put("onfocus", "this.size = 10;");
					tag.put("onblur", "this.size = 1;");
					tag.put("size", "1");
					}				
				};	
			add (selectedAssays);
			selectedAssays.setOutputMarkupId(true);
			selectedAssays.setOutputMarkupPlaceholderTag(true) ;
			selectedAssays.add(buildStandardFormComponentUpdateBehavior("change", "updateAssays", aliquotDto, detailPanel, editAliquot )); // issue 27 2020
			weightedAmountLabel  = new Label("weightedAmountLabel", "Weighted Amt")	
			    {
				@Override
					public boolean isVisible()
					{
					if (aliquotDto.getNeatOrDilution() == null)
						return false;
					if (aliquotDto == null)
						return false;	
					return (aliquotDto.getNeatOrDilutionText().equals("Neat"));
					}
				};						
			add (weightedAmountLabel);
			weightedAmountLabel.setOutputMarkupId(true);
			weightedAmountLabel.setOutputMarkupPlaceholderTag(true) ;			
			weightedAmountUnitsDD = new DropDownChoice("weightedAmountUnits", Arrays.asList(new String[] {"ug", "mg", "g" }) )	
			    {
				@Override
					public boolean isVisible()
					{
					if (aliquotDto.getNeatOrDilution() == null)
						return false;
					if (aliquotDto == null)
						return false;
					if (aliquotDto.getIsDry() == null)
						return false;
					return (aliquotDto.getNeatOrDilutionText().equals("Neat")  && aliquotDto.getNeatOrDilutionUnits() != null);
					}
				};						
			add (weightedAmountUnitsDD);
			weightedAmountUnitsDD.setOutputMarkupId(true);
			weightedAmountUnitsDD.setOutputMarkupPlaceholderTag(true) ;	
			weightedAmountUnitsDD.add(buildStandardFormComponentUpdateBehavior("change", "updateWeightedAmount", aliquotDto, detailPanel, editAliquot )); // issue 27 2020			
			weightedAmountTxt = new RequiredTextField("weightedAmount")
				{
				@Override
				public boolean isVisible()
					{
					if (aliquotDto.getNeatOrDilution() == null)
						return false;
					if (aliquotDto == null)
						return false;
					return (aliquotDto.getNeatOrDilutionText().equals("Neat") );
					}
				};
			weightedAmountTxt.setType(BigDecimal.class);
		    add (weightedAmountTxt);
		    weightedAmountTxt.setOutputMarkupId(true);
		    weightedAmountTxt.setOutputMarkupPlaceholderTag(true) ;	    
		    molecularWeightTxt = new RequiredTextField("molecularWeight")
				{
				@Override
				public boolean isVisible()
					{
					if (aliquotDto.getNeatOrDilution() == null)
						return false;
					if (aliquotDto == null)
						return false;
					if (aliquotDto.getIsDry() == null)
						return false;
					return (aliquotDto.getNeatOrDilutionText().equals("Neat") && !aliquotDto.getIsDry());
					}
				};
			molecularWeightTxt.setType(BigDecimal.class);
		    add (molecularWeightTxt);
		    molecularWeightTxt.setOutputMarkupId(true);
		    molecularWeightTxt.setOutputMarkupPlaceholderTag(true) ;
		    if (alq == null)
		        aliquotDto.setMolecularWeight(detailPanel.getCompound().getMolecular_weight().toString());  
		    dConcLabel  = new Label("dConcLabel", "Desired Concentration")	
			    {
				@Override
					public boolean isVisible()
					{
					if (aliquotDto.getNeatOrDilution() == null)
						return false;
					if (aliquotDto == null)
						return false;
					if (aliquotDto.getIsDry() == null)
						return false;
					return (aliquotDto.getNeatOrDilutionText().equals("Neat") && !aliquotDto.getIsDry());	
					}
				};						
		    add (dConcLabel);
		    dConcLabel.setOutputMarkupId(true);
		    dConcLabel.setOutputMarkupPlaceholderTag(true) ;		    
		    dConcentrationUnitsDD = new DropDownChoice("dConcentrationUnits", Arrays.asList(new String[] {"uM", "mM", "M" }) )
				{
				@Override
				public boolean isVisible()
					{
					if (aliquotDto.getNeatOrDilution() == null)
						return false;
					if (aliquotDto == null)
						return false;
					if (aliquotDto.getIsDry() == null)
						return false;
					return (aliquotDto.getNeatOrDilutionText().equals("Neat") && !aliquotDto.getIsDry() && aliquotDto.getNeatOrDilutionUnits() != null);	
					}
				};
			dConcentrationUnitsDD.setOutputMarkupId(true);	
			dConcentrationUnitsDD.setOutputMarkupPlaceholderTag(true) ;
			dConcentrationUnitsDD.add(buildStandardFormComponentUpdateBehavior("change", "updateDConcentration", aliquotDto, detailPanel, editAliquot )); // issue 27 2020
			add (dConcentrationUnitsDD);  	       
		    molecularWeightLabel  = new Label("molecularWeightLabel", "Molecular Wt.")	
			    {
				@Override
				public boolean isVisible()
					{
					if (aliquotDto.getNeatOrDilution() == null)
						return false;
					if (aliquotDto == null)
						return false;
					if (aliquotDto.getIsDry() == null)
						return false;
					return (aliquotDto.getNeatOrDilutionText().equals("Neat") && !aliquotDto.getIsDry());	
					}
				};						
		    add (molecularWeightLabel);
		    molecularWeightLabel.setOutputMarkupId(true);
		    molecularWeightLabel.setOutputMarkupPlaceholderTag(true) ;		    
		    dConcTxt = new TextField("dConc")
				{
				@Override
				public boolean isVisible()
					{
					if (aliquotDto.getNeatOrDilution() == null)
						return false;
					if (aliquotDto == null)
						return false;
					if (aliquotDto.getIsDry() == null)
						return false;
					return (aliquotDto.getNeatOrDilutionText().equals("Neat") && !aliquotDto.getIsDry());
					}
				};
			dConcTxt.setType(BigDecimal.class);
		    add (dConcTxt); 
		    dConcTxt.setOutputMarkupId(true);
		    dConcTxt.setOutputMarkupPlaceholderTag(true) ;
		    dVoluLabel  = new Label("dVoluLabel", "")
				{
				@Override
				public boolean isVisible()
					{
					if (aliquotDto.getNeatOrDilution() == null)
						return false;
					if (aliquotDto == null)
						return false;
					if (aliquotDto.getIsDry() == null)
						return false;
					return (aliquotDto.getNeatOrDilutionText().equals("Neat") && !aliquotDto.getIsDry());	
					}
				};						
			add (dVoluLabel);
			dVoluLabel.setOutputMarkupId(true);
			dVoluLabel.setOutputMarkupPlaceholderTag(true) ;		    			
			dvol  = new TextField("dvol");
			dvol.setType(BigDecimal.class);
			dilutionContainer.add (dvol);
			dvol.setOutputMarkupId(true);
			dvol.setOutputMarkupPlaceholderTag(true) ;	
			dilutionFormulaLabel  = new Label("dilutionFormulaLabel", "=")	;						
			dilutionContainer.add (dilutionFormulaLabel);
			dilutionFormulaLabel.setOutputMarkupId(true);
			dilutionFormulaLabel.setOutputMarkupPlaceholderTag(true) ;	
			ivolLabel  = new Label("ivolLabel", "V1");						
			dilutionContainer.add (ivolLabel);
			ivolLabel.setOutputMarkupId(true);
			ivolLabel.setOutputMarkupPlaceholderTag(true) ;				
			dvolLabel  = new Label("dvolLabel", "V2");						
			dilutionContainer.add (dvolLabel);
			dvolLabel.setOutputMarkupId(true);
			dvolLabel.setOutputMarkupPlaceholderTag(true) ;	
			ivolUnits  = new Label  ("ivolUnits","mL"  );						
			dilutionContainer.add (ivolUnits);
			ivolUnits.setOutputMarkupId(true);
			ivolUnits.setOutputMarkupPlaceholderTag(true) ;	
			dvolUnits  = new Label("dvolUnits",  "mL")  ;					
			dilutionContainer.add (dvolUnits);
			dvolUnits.setOutputMarkupId(true);
			dvolUnits.setOutputMarkupPlaceholderTag(true) ;			
			iconLabel  = new Label("iconLabel", "C1");						
			dilutionContainer.add (iconLabel);
			iconLabel.setOutputMarkupId(true);
			iconLabel.setOutputMarkupPlaceholderTag(true) ;				
			iconUnits  = new Label("iconUnits", ( aliquotDto.getNeatOrDilutionUnits() == null ? "" : editAliquot.aliquotNeatOrDilutionUnitsDD.getModelObject().toString() ) );						
			dilutionContainer.add (iconUnits);
			iconUnits.setOutputMarkupId(true);
			iconUnits.setOutputMarkupPlaceholderTag(true) ;			
			dconLabel  = new Label("dconLabel", "C2")	;					
			dilutionContainer.add (dconLabel);
			dconLabel.setOutputMarkupId(true);
			dconLabel.setOutputMarkupPlaceholderTag(true) ;				
			dconUnits  = new Label("dconUnits", ( aliquotDto.getNeatOrDilutionUnits() == null ? "" : editAliquot.aliquotNeatOrDilutionUnitsDD.getModelObject().toString() ) );							
			dilutionContainer.add (dconUnits);
			dconUnits.setOutputMarkupId(true);
			dconUnits.setOutputMarkupPlaceholderTag(true) ;			
		    ivolTxt = new TextField("ivol");
		    ivolTxt.setType(BigDecimal.class);
		    dilutionContainer.add (ivolTxt);
		    ivolTxt.setOutputMarkupId(true);
		    ivolTxt.setOutputMarkupPlaceholderTag(true) ;
		    iconTxt = new TextField("icon");
		    iconTxt.setType(BigDecimal.class);
		    dilutionContainer.add(iconTxt);
		    iconTxt.setOutputMarkupId(true);
		    iconTxt.setOutputMarkupPlaceholderTag(true) ;
			iconTxt.setType(BigDecimal.class);
			dconTxt = new TextField("dcon");
		    dconTxt.setType(BigDecimal.class);
		    dilutionContainer.add(dconTxt);
		    dconTxt.setOutputMarkupId(true);
		    dconTxt.setOutputMarkupPlaceholderTag(true) ;
			dconTxt.setType(BigDecimal.class);			
			List<String> locationChoices = new ArrayList<String>();	
			locationsDD= new DropDownChoice("location",    locationChoices)
			    {
				public boolean isEnabled()
					{
					return !getIsNoInventory();
					}
				}
				;			
			locationsDD.setOutputMarkupId(true);
			add(locationsDD);
			if (alq != null)
				setValuesForEdit(alq);
			setUnit("-80 freezer");
			locationsDD.setChoices(unit != null ? ((ArrayList <String>) locationService.getSampleLocationNamesByUnit(unit)) :  new ArrayList <String> ());
					
			add( new AjaxLink<Void>("close")
				{
				public void onClick(AjaxRequestTarget target)
					{ 
					window.close(target);
					}
				});			
			calculateNeatButton = new Button("calculateNeat")
		    	{
				@Override
				public boolean isVisible()
					{
					if (aliquotDto.getNeatOrDilution() == null)
						return false;
					if (aliquotDto == null)
						return false;
					return (aliquotDto.getNeatOrDilutionText().equals("Neat") && !aliquotDto.getIsDry());
					}
				public void onSubmit() 
				    {
					boolean errorP = true;
					calculateOnly = true;
					if (!aliquotDto.getIsDry())						
						errorP = calculateNeatDesiredVolume();
					if (errorP)
					    {
						EditAliquot.this.error("There was an error in the calculation");
						return;
					    }
				    }
		    	};
			add (calculateNeatButton);
			calculateNeatButton.setOutputMarkupId(true);
			calculateNeatButton.setOutputMarkupPlaceholderTag(true) ;
			calculateDilutionButton = new Button  ("calculateDilution")
		    	{
				@Override
				public void onSubmit()
				    {
					boolean errorP = true;
					calculateOnly = true;
					errorP  = processUnknownForDilution();
					if (errorP)
					    {
						EditAliquot.this.error("There was an error in the calculation");
						return;
					    }
				    }
		    	};		    	
			dilutionContainer.add (calculateDilutionButton);
			calculateDilutionButton.setOutputMarkupId(true);
			calculateDilutionButton.setOutputMarkupPlaceholderTag(true) ;
			saveChangesButton = new Button("saveChanges")
				{
				@Override
				public boolean isEnabled()
					{
					if (isViewOnly)
						return false;
					// issue 79
					if (userService.isAliquotAdmin(((MedWorksSession) Session.get()).getCurrentUserId()))
						return true;
					return alq== null ? true : alq.getCreatedBy().equals(((MedWorksSession) Session.get()).getCurrentUserId()) ;
					}
				public void onSubmit() 
					{	
					calculateOnly=false;
					AliquotDTO aliquotDto = (AliquotDTO) getForm().getModelObject();															
					if (!doEditChecks())
						return;
					// issue 79 solvent processing
					if (aliquotDto.getIsDry())
						aliquotDto.setSolvent("");
					else
						if (!StringUtils.isNullOrEmpty(aliquotDto.getOtherSolvent()))		
						    aliquotDto.setSolvent("OTHER:" + aliquotDto.getOtherSolvent());
					if (aliquotDto.getNeatOrDilutionText().equals("Dilution"))
						{		
						boolean errorProcessing = processUnknownForDilution();
						aliquotDto.setDConc("0");
						aliquotDto.setWeightedAmount("0");						
						if (errorProcessing) return;
						}
					else 
						{
						boolean errorProcessing = true;
						aliquotDto.setIvol("0");
						aliquotDto.setIcon("0");
						aliquotDto.setDcon("0");
						aliquotDto.setDvol("0");
						if (!aliquotDto.getIsDry())						
							errorProcessing = calculateNeatDesiredVolume();
						else
							{
							errorProcessing = false;
							aliquotDto.setDConc	("0");
							}
						if (errorProcessing) return;
						}
					// issue 79
					if (alq == null)
						aliquotDto.setCreatedBy(((MedWorksSession) Session.get()).getCurrentUserId());
						if (aliquotDto.getNeatOrDilutionText().equals("Dilution"))
						aliquotDto.setIsDry(false);	
					try 
					    {	
						aliquotDto.setCid(detailPanel.getCompound().getCid());							
						aliquotDto.setLocationObj(isNoInventory ? null  : locationService.loadById(aliquotDto.getLocation()));
						if (!getIsNoInventory())
							aliquotDto.setInventoryObj(inventoryService.loadById(aliquotDto.getParentId()));
						else 
							aliquotDto.setInventoryObj(null);
						aliquotDto.setCompoundObj(compoundService.loadCompoundById(aliquotDto.getCid()));
						if (aliquotDto.getReplicate() == null || aliquotDto.getReplicate() <= 0)
							aliquotDto.setReplicate(1);	
						List <Aliquot> aliquotList =  aliquotService.save(aliquotDto, aliquotIdAssigned, isAssayListUpdated, isNoInventory);
						/// issue 100 
						if  (!StringUtils.isNullOrEmpty(aliquotIdAssigned))
							{
							String msg = "<span style=\"color:blue;\">" +   "Aliquot/Name detail saved for Aliquot:" + aliquotIdAssigned +  "." + "</span>";	;
							EditAliquot.this.info(msg);
							return;
							}
						if (aliquotList.size() == 1)
							{
							Aliquot alq = aliquotList.get(0);
							if (alq.getAliquotId() != null && (aliquotDto.getAliquotId()== null || aliquotDto.getAliquotId().equals("to be assigned")))
							    {						
								aliquotIdAssigned = alq.getAliquotId();
								aliquotDto.setAliquotId(aliquotIdAssigned);
							    }	
							String msg = "<span style=\"color:blue;\">" +   "Aliquot/Name detail saved for Aliquot:" + alq.getAliquotId()+  "." + "</span>";	;
							EditAliquot.this.info(msg);
							}
						else
							{
							Aliquot alq = aliquotList.get(aliquotList.size()-1);
							if (alq.getAliquotId() != null && (aliquotDto.getAliquotId()== null || aliquotDto.getAliquotId().equals("to be assigned")))
							    {						
								aliquotIdAssigned = alq.getAliquotId();
								aliquotDto.setAliquotId(aliquotIdAssigned);
							    }	
							String saveMsg = "Aliquot/Name detail saved for multiple Aliquots: ";
							for (Aliquot vAlq : aliquotList)
								{
								saveMsg = saveMsg + vAlq.getAliquotId() + "->" + vAlq.getAliquotLabel() + ",";
								}
							saveMsg = saveMsg.substring(0, saveMsg.length()-1);
							saveMsg = "<span style=\"color:blue;\">" + saveMsg +  "</span>";
							EditAliquot.this.info(saveMsg);
						    }
						} 
					catch(Exception e)
						{ 
						e.printStackTrace(); 
						EditAliquot.this.error("Save unsuccessful. Please make sure that smiles is valid."); 
						}						
					setResponsePage(getPage());
					}				
				public void onError(AjaxRequestTarget target, Form form)
					{
					target.add(EditAliquot.this.get("feedback")); 
					}
				};	
			saveChangesButton.add(( new AttributeModifier("onclick", buildHTMLC1C2String())));	
			add(saveChangesButton);
			add(this.buildIsDryChkBox(alq == null ? true : false));
			dryCheckBox.add(buildStandardFormComponentUpdateBehavior("change", "updateDry", aliquotDto, detailPanel, editAliquot )); // issue 27 2020
			dryCheckBox.setOutputMarkupId(true);
			dryCheckBox.setOutputMarkupPlaceholderTag(true) ;	
			
			// issue 196
			add(isNoInventoryCheckBox = this.buildIsInventoryChkBox("isNoInventory"));
			isNoInventoryCheckBox.add(buildStandardFormComponentUpdateBehavior("change", "updateIsNoInventory", aliquotDto, detailPanel, editAliquot )); // issue 27 2020
			isNoInventoryCheckBox.setOutputMarkupId(true);
			isNoInventoryCheckBox.setOutputMarkupPlaceholderTag(true) ;
			
			isDryLabel = new Label("isDryLabel", "Dry?")
				{
				@Override
					public boolean isVisible()
					{
					if (aliquotDto.getNeatOrDilutionText() == null)
						return false;
					return aliquotDto.getNeatOrDilutionText().equals("Neat");	
					}
				};
			add(isDryLabel);
			isDryLabel.setOutputMarkupId(true);
			isDryLabel.setOutputMarkupPlaceholderTag(true) ;			
			}
	
		protected AjaxCheckBox buildIsDryChkBox(boolean setIsDry )
		    {
		    dryCheckBox = new AjaxCheckBox("isDry", new PropertyModel(aliquotDto, "isDry"))
			    {
		    	@Override
			    public boolean isVisible()
				    {
		    		if (aliquotDto.getNeatOrDilutionText() == null)
						return false;
					return aliquotDto.getNeatOrDilutionText().equals("Neat");
				    }
		    	@Override
			    public boolean isEnabled()
				    {
		    		if (aliquotDto.getNeatOrDilutionText() == null)
						return false;
					return aliquotDto.getNeatOrDilutionText().equals("Neat");	
				    }
			    @Override
			    public void onUpdate(AjaxRequestTarget target)
				    {
				    }
			    };
			if (setIsDry)
				aliquotDto.setIsDry(true);
		    return dryCheckBox;
		    }
		
		//issue 196
		protected AjaxCheckBox buildIsInventoryChkBox(String id )
	    {
	    isNoInventoryCheckBox = new AjaxCheckBox(id, new PropertyModel(this, id))
		    {
	    	@Override
		    public boolean isVisible()
			    {
	    		return true;
			    }
	    	@Override
		    public boolean isEnabled()
			    {
	    		return true;
			    }
		    @Override
		    public void onUpdate(AjaxRequestTarget target)
			    {
			    }
		    };
	    return isNoInventoryCheckBox;
	    }
		
			
		// issue 61 2020
		private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(String event, final String response, final AliquotDTO aliquotDto, InventoryDetailPanel detailPanel , final EditAliquot editAliquot)
			{
			 return new AjaxFormComponentUpdatingBehavior(event)
			    {
			    @Override
			    protected void onUpdate(AjaxRequestTarget target)
			    	{
			    	switch (response)
			        	{
			        	case "updateIsNoInventory" :
			    	    //	target.add(editAliquot.editAliquotForm.isNoInventoryCheckBox);
			        	    target.add(editAliquot.locationsDD);
			        	    target.add(editAliquot.selectedAssays);
			    	    	target.add(editAliquot.editAliquotForm.selectedParentInventoryDD);
			    	    	
			    	    	break;
			    	    case "updateAssays" :
			    	    	isAssayListUpdated = true;
			    	    	break;
			        	case "updateNeatOrDilution" :
			        		aliquotDto.setNeatOrDilution (aliquotDto.getNeatOrDilutionText().equals("Neat") ? '1' : '0');
			        		if (aliquotNeatOrDilutionDD.getChoices().contains("Choose One"))
			        		    {
			        			aliquotNeatOrDilutionDD.setChoices(Arrays.asList(new String[] {"Neat", "Dilution" })); 
			        		    target.add(aliquotNeatOrDilutionDD);
			        		    }
			        		if (aliquotDto.getNeatOrDilutionText().equals("Neat"))
			        		    {
			        			aliquotNeatOrDilutionUnitsDD.setChoices(Arrays.asList(new String[] {"ug", "mg", "g" }));
			        			aliquotDto.setNeatOrDilutionUnits("ug");
			        		    }
			 		        else
			 		            {
			 		        	aliquotNeatOrDilutionUnitsDD.setChoices(Arrays.asList(new String[] {"uM", "mM", "M" })); 
			 		        	aliquotDto.setNeatOrDilutionUnits("uM");
			 		        	aliquotDto.setIsDry(false); // issue 79 solvent processing
			 		            }
			        		if (editAliquot.aliquotNeatOrDilutionUnitsDD.getModelObject() != null)
			        			{
				        		editAliquot.iconUnits.setDefaultModelObject(editAliquot.aliquotNeatOrDilutionUnitsDD.getModelObject().toString());
				        		editAliquot.dconUnits.setDefaultModelObject(editAliquot.aliquotNeatOrDilutionUnitsDD.getModelObject().toString());
			        			}
			        		target.add(editAliquot.aliquotNeatOrDilutionUnitsDD);
			        		target.add(editAliquot.dilutionContainer);
			        		target.add(editAliquot.ivolTxt);
			        		target.add(editAliquot.iconTxt);
			        		target.add(editAliquot.dconTxt);
			        		target.add(editAliquot.weightedAmountLabel);
			        		target.add(editAliquot.dConcLabel);
			        		target.add(editAliquot.molecularWeightLabel);
			        		target.add(editAliquot.weightedAmountTxt);
			        		target.add(editAliquot.dConcTxt);
			        		target.add(editAliquot.molecularWeightTxt);
			        		target.add(editAliquot.dryCheckBox);
			        		target.add(editAliquot.isDryLabel);
			        		target.add(editAliquot.ivolLabel);
			        		target.add(editAliquot.dilutionFormulaLabel);
			        		target.add(editAliquot.dvolLabel);
			        		target.add(editAliquot.ivolUnits);
			        		target.add(editAliquot.dvolUnits);
			        		target.add(editAliquot.iconUnits);			        		
			        		target.add(editAliquot.iconLabel);
			        		target.add(editAliquot.dconLabel);
			        		target.add(editAliquot.dconUnits);
			        		target.add(editAliquot.dvol);
			        		target.add(editAliquot.dVoluLabel);
			        	    target.add(editAliquot.weightedAmountUnitsDD);
			        	    target.add(editAliquot.dConcentrationUnitsDD);
			        		target.add(editAliquot.calculateNeatButton);
			        		target.add(editAliquot.calculateDilutionButton);
			        		target.add(editAliquot.solventDD);
			        		target.add(editAliquot.solventLabel);
			        		break;
			        	case "updateNeatOrDilutionUnits" :
			        		editAliquot.iconUnits.setDefaultModelObject(editAliquot.aliquotNeatOrDilutionUnitsDD.getModelObject().toString());
			        		editAliquot.dconUnits.setDefaultModelObject(editAliquot.aliquotNeatOrDilutionUnitsDD.getModelObject().toString());
			        		target.add(editAliquot.ivolUnits);
			        		target.add(editAliquot.dvolUnits);
			        		target.add(editAliquot.iconUnits);
			        		target.add(editAliquot.dconUnits);
			        		
			        		break;
			        	case "updateSolvent":
			        		if (aliquotDto.getSolventText() != null && aliquotDto.getSolventText().equals("OTHER") )
			        		    aliquotDto.setSolvent("OTHER:" + aliquotDto.getOtherSolvent());
			        		else
			        			{
			        			aliquotDto.setSolvent (aliquotDto.getSolventText());
			        			aliquotDto.setOtherSolvent("");
			        			}
			        		target.add(editAliquot.otherSolvent);
			        		target.add(editAliquot.otherSolventLabel);
			        		break;
			        	case "updateDry":
			        		target.add(editAliquot.weightedAmountLabel);
			        		target.add(editAliquot.weightedAmountTxt);
			        		target.add(editAliquot.dConcTxt);
			        		target.add(editAliquot.molecularWeightTxt);
			        		target.add(editAliquot.dryCheckBox);
			        		target.add(editAliquot.isDryLabel);
			        		target.add(editAliquot.dConcLabel);
			        		target.add(editAliquot.molecularWeightLabel);
			        		target.add(editAliquot.dConcentrationUnitsDD);
			        		target.add(editAliquot.weightedAmountUnitsDD);
			        		target.add(editAliquot.dVoluLabel);
			        		target.add(editAliquot.calculateNeatButton);
			        		target.add(editAliquot.solventDD);
			        		target.add(editAliquot.solventLabel);
			        		break;
			        	case "updateDConcentration":
			        		target.add(editAliquot.dConcentrationUnitsDD);
			        		break;
			        	case "updateWeightedAmount":
			        		target.add(editAliquot.weightedAmountUnitsDD);
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
		
		// issue 61 2020
		private List<String> getListOfInvIds (List <Inventory> lstInv)
			{
			List<String> listOfInvIds = new ArrayList <String> ();
			for (Inventory inv : lstInv)
				listOfInvIds.add(inv.getInventoryId())	;
			return listOfInvIds;
			}
		
		private boolean processUnknownForDilution ()
			{
		    Double calcValue;
		    Double ivolPart = aliquotDto.getIvol() == null ? null : new BigDecimal(aliquotDto.getIvol().replace(",", "")).doubleValue();
		    Double iconPart = aliquotDto.getIcon() == null ? null : new BigDecimal(aliquotDto.getIcon().replace(",", "")).doubleValue();
		    Double dconPart = aliquotDto.getDcon() == null ? null : new BigDecimal(aliquotDto.getDcon().replace(",", "")).doubleValue();
		    Double dvolPart = aliquotDto.getDvol() == null ? null : new BigDecimal(aliquotDto.getDvol().replace(",", "")).doubleValue();	
		    int iNumUnknowns = 0;
		    boolean errorProcessing = true;
		    if (aliquotDto.getIvol() == null)
				iNumUnknowns++;
			if (aliquotDto.getIcon() == null)
		        {
				iNumUnknowns++;
				if (iNumUnknowns > 1)
				    {
				    EditAliquot.this.error("You can only have one unknown:  C1, V1, C2 or V2" +   "."); 
				    return errorProcessing;
				    }
		        }
			if (aliquotDto.getDcon() == null)
			    {
				iNumUnknowns++;
				if (iNumUnknowns > 1)
				    {
			        EditAliquot.this.error("You can only have one unknown:  C1, V1, C2 or V2" +   "."); 
			        return errorProcessing;
				    }
			    }
			if (aliquotDto.getDvol() == null)
			    {
				iNumUnknowns++;
				if (iNumUnknowns > 1)
				    {	
			        EditAliquot.this.error("You can only have one unknown:  C1, V1, C2 or V2" +   "."); 
			        return errorProcessing;
				    }
			    }			
			if ((aliquotDto.getIvol() != null && aliquotDto.getIvol().trim().equals("0")) || (aliquotDto.getDvol() != null && aliquotDto.getDvol().trim().equals("0")) ||(aliquotDto.getDcon() != null && aliquotDto.getDcon().trim().equals("0")) || (aliquotDto.getIcon() != null && aliquotDto.getIcon().trim().equals("0")))
				{
				EditAliquot.this.error("Please put in non-zero values"); 
			    return errorProcessing;
				}			
			
			if (aliquotDto.getIvol() == null)
			    {
				calcValue = (dvolPart* dconPart)/iconPart;
				BigDecimal bd = new BigDecimal(calcValue);
				aliquotDto.setIvol (bd.toString());
			    }
			else if (aliquotDto.getIcon() == null)
			    {
				calcValue= (dvolPart* dconPart)/ivolPart;
				aliquotDto.setIcon(calcValue.toString());
			    }
			else if (aliquotDto.getDcon() == null)
			    {
				calcValue= (ivolPart* iconPart)/dvolPart;
				aliquotDto.setDcon(calcValue.toString());
			    }
			else
			  	{
				calcValue = (ivolPart* iconPart)/dconPart;
				aliquotDto.setDvol(calcValue.toString());
			    }
			
			if (!NumberUtils.verifyDecimalRange(aliquotDto.getIvol(), 8, 7))
				{
				EditAliquot.this.error("Please make sure that the initial volume has no more than 8 digits for the whole number and 7 digits for the decimal place");
				return errorProcessing;
				}
			if (!NumberUtils.verifyDecimalRange(aliquotDto.getDvol(), 8, 7))
				{
				EditAliquot.this.error("Please make sure that the desired volume has no more than 8 digits for the whole number and 7 digits for the decimal place");
				return errorProcessing;
				}
			if (!NumberUtils.verifyDecimalRange(aliquotDto.getDcon(), 8, 7))
				{
				EditAliquot.this.error("Please make sure that the desired concentration has no more than 8 digits for the whole number and 7 digits for the decimal place");
				return errorProcessing;
				}
			if (!NumberUtils.verifyDecimalRange(aliquotDto.getIcon(), 8, 7))
				{
				EditAliquot.this.error("Please make sure that the initial concentration has no more than 8 digits for the whole number and 7 digits for the decimal place");
				return errorProcessing;
				}
			return !errorProcessing;
			}
		
		public boolean calculateNeatDesiredVolume()
			{
			boolean errorProcessing = true;
			if ((aliquotDto.getDConc() != null && aliquotDto.getDConc().trim().equals("0")) || (aliquotDto.getWeightedAmount() != null && aliquotDto.getWeightedAmount().trim().equals("0")) ||(aliquotDto.getMolecularWeight() != null && aliquotDto.getMolecularWeight().trim().equals("0")) )
				{
				EditAliquot.this.error("Please put in non-zero values"); 
			    return errorProcessing;
				}
			if (StringUtils.isNullOrEmpty(aliquotDto.getWeightedAmountUnits()))
				{
				EditAliquot.this.error("Please choose value for Weighted Amount Units"); 
			    return errorProcessing;	
				}
			if (StringUtils.isNullOrEmpty(aliquotDto.getDConcentrationUnits()))
				{
				EditAliquot.this.error("Please choose value for Desired Concentration Units"); 
			    return errorProcessing;	
				}	
			if (StringUtils.isNullOrEmpty(aliquotDto.getDConc()))
			    {
				EditAliquot.this.error("Please choose value for Desired Concentration"); 
		        return errorProcessing;
			    }
			if (!NumberUtils.verifyDecimalRange(aliquotDto.getDConc(), 8, 7))
				{
				EditAliquot.this.error("Please make sure that the desired concentration has no more than 8 digits for the whole number and 7 digits for the decimal place");
				return errorProcessing;
				}
			if (!NumberUtils.verifyDecimalRange(aliquotDto.getWeightedAmount(), 8, 7))
				{
				EditAliquot.this.error("Please make sure that the weighted amount has no more than 8 digits for the whole number and 7 digits for the decimal place");
				return errorProcessing;
				}
			if (!NumberUtils.verifyDecimalRange(aliquotDto.getMolecularWeight(), 5, 5))
				{
				EditAliquot.this.error("Please make sure that the molecular weight has no more than 5 digits for the whole number and 5 digits for the decimal place");
				return errorProcessing;
				}
			Double dConcPart = new BigDecimal(aliquotDto.getDConc().replace(",", "")).doubleValue();
			Double weightedAmountPart = new BigDecimal(aliquotDto.getWeightedAmount().replace(",", "")).doubleValue();							
			Double molecularWeightPart = new BigDecimal(aliquotDto.getMolecularWeight().replace(",", "")).doubleValue();
			Double conversionDesiredConcentration = (aliquotDto.getDConcentrationUnits().equals("uM") ? .000001 : (aliquotDto.getDConcentrationUnits().equals("mM") ? .001 : 1)   );
			Double conversionWeightedAmount = (aliquotDto.getWeightedAmountUnits().equals("ug") ? .000001 : (aliquotDto.getWeightedAmountUnits().equals("mg") ? .001 : 1)   );
			Double desiredVolume =  (weightedAmountPart*conversionWeightedAmount)/(molecularWeightPart*dConcPart*conversionDesiredConcentration);					
		    BigDecimal bd = new BigDecimal(Double.toString(desiredVolume ));
		    bd = bd.setScale(6, RoundingMode.HALF_UP);
		    editAliquot.dVoluLabel.setDefaultModelObject("Desired Volume=" + bd.toString() + " L");
			return !errorProcessing;
			}
		
		public String getUnit()
			{
			return unit;
			}

		public void setUnit(String u)
			{
			unit = u;
			}
		
		// issue 100
		public List <String> getValuesForAssayAliquot (Aliquot alq)
			{
			return aliquotService.retrieveAssayNames(alq.getAliquotId());
			}
			
		public void setValuesForEdit (Aliquot alq)
			{
			if  (!StringUtils.isNullOrEmpty(alq.getSolvent()) && alq.getSolvent().contains("OTHER:"))
				{
			    aliquotDto.setSolventText("OTHER");
			    aliquotDto.setOtherSolvent(alq.getSolvent().replace("OTHER:", ""));
				}
			else
			    aliquotDto.setSolventText(alq.getSolvent());
			aliquotDto.setIvol(alq.getIvol().toString());
			aliquotDto.setAssayIds(getValuesForAssayAliquot(alq)); // issue 100
			aliquotDto.setDcon(alq.getDcon().toString());
			aliquotDto.setIcon(alq.getIcon().toString());
			aliquotDto.setIsDry(alq.getDry() ==  null ? false : (alq.getDry().equals('1') ? true : false));
			aliquotDto.setWeightedAmount(alq.getWeightedAmount().toString());
			aliquotDto.setDConc(alq.getDconc().toString());		
			// issue 79
			//aliquotDto.setUserName( userService.getFullNameByUserId(((MedWorksSession) getSession()).getCurrentUserId()));
			String createDateStr = alq.getCreateDateString();
		    aliquotDto.setCreateDate(createDateStr);
		    aliquotDto.setNeatOrDilution(alq.getNeat());
		    aliquotDto.setNeatOrDilutionText(alq.getNeat().equals('1') ? "Neat" : "Dilution");    	   
	        // issue 196
		    if (alq.getInventory() == null)
		    	aliquotDto.setParentId("");
		    else
	        	aliquotDto.setParentId(alq.getInventory().getInventoryId());
	        setIsNoInventory(alq.getInventory() == null ? true : false);
		    if (aliquotDto.getNeatOrDilutionText().equals("Neat"))
	        	aliquotNeatOrDilutionUnitsDD.setChoices(Arrays.asList(new String[] {"ug", "mg", "g" }));
	        else
	        	aliquotNeatOrDilutionUnitsDD.setChoices(Arrays.asList(new String[] {"uM", "mM", "M" }));
			}
		
		private String buildHTMLC1C2String()
			{
			String htmlStr = "";
			htmlStr = htmlStr + "var dcon = " + "Number(document.getElementById(" +  "\"" + "dcon" + "\"" + ")" + ".value) ;";
			htmlStr = htmlStr + "var icon = " + "Number(document.getElementById(" +  "\"" + "icon" + "\"" + ")" + ".value) ;";
			htmlStr = htmlStr + "var ivol = " + "Number(document.getElementById(" +  "\"" + "ivol" + "\"" + ")" + ".value) ;";
			htmlStr = htmlStr + "var dvol = " + "Number(document.getElementById(" +  "\"" + "dvol" + "\"" + ")" + ".value) ;";	
			htmlStr = htmlStr + "if (isNaN(dcon) || isNaN(icon) || isNaN(dvol) || isNaN(ivol))";
			htmlStr = htmlStr + " {return true;}";
			htmlStr =  htmlStr + "if (" + "document.getElementById(" +  "\"" + "dcon" + "\"" + ")" + ".value.length ==0)";
			htmlStr = htmlStr + "{" ;
			htmlStr = htmlStr + " dcon = (Number(icon) * Number(ivol))/Number(dvol);";
			htmlStr = htmlStr + " if (dvol > 0 && ivol > 0 && icon> 0 ) ";
			htmlStr = htmlStr + " { document.getElementById(" + "\"" + "dcon" + "\"" + ").value= Number(dcon); } ";
			htmlStr = htmlStr + "}" ;
			htmlStr =  htmlStr + "if (" + "document.getElementById(" +  "\"" + "icon" + "\"" + ")" + ".value.length ==0)";			
			htmlStr = htmlStr + "{" ;
			htmlStr = htmlStr + " icon = (Number(dcon) * Number(dvol))/Number(ivol);";
			htmlStr = htmlStr + "if (ivol > 0 && dvol > 0 && dcon > 0 )";
			htmlStr = htmlStr + "{document.getElementById(" + "\"" + "icon" + "\"" + ").value= Number(icon);}";
			htmlStr = htmlStr + "}" ;
			htmlStr =  htmlStr + "if (" + "Number(document.getElementById(" +  "\"" + "dcon" + "\"" + ")" + ".value) >" + "Number(document.getElementById(" +  "\"" + "icon" + "\"" + ")" + ".value) && dcon > 0 && icon > 0 && isFinite(dcon) && isFinite(icon))";
			htmlStr = htmlStr + "{" ;
			String msg = "Warning c2 is greater than c1.  Do not use this unless you are concentrating the standard.  Are you sure you want to continue and save?";	
			htmlStr = htmlStr + "return confirm('" + msg + "'); ";
			htmlStr = htmlStr + "}  else { return true; }";
			return htmlStr;
			}	
		private boolean doEditChecks()
			{
			if (aliquotDto.getIsDry() && aliquotDto.getNeatOrDilutionText().equals("Neat"))
				if (!StringUtils.isNullOrEmpty(aliquotDto.getWeightedAmount()) && aliquotDto.getWeightedAmount().trim().equals("0"))
					{
					EditAliquot.this.error("Please enter non-zero value for weighted amount ");
					return false;	
					}
			if (StringUtils.isNullOrEmpty(aliquotDto.getCreateDate()))
				{
				EditAliquot.this.error("Please enter value for Create Date ");
				return false;
				}
			if (StringUtils.isNullOrEmpty(aliquotDto.getParentId()) && !getIsNoInventory())
				{
				EditAliquot.this.error("Please enter value for Parent Inventory Id ");
				return false;
				}
			if (StringUtils.isNullOrEmpty(aliquotDto.getLocation()) && !getIsNoInventory())
				{
				EditAliquot.this.error("Please enter value for Location ");
				return false;	
				}
			if ( editAliquot.solventDD.isVisible()  && StringUtils.isNullOrEmpty(aliquotDto.getSolventText())  )
				{
				EditAliquot.this.error("Please enter value for Solvent ");
				return false;
				}
			if (StringUtils.isNullOrEmpty(aliquotDto.getDConc()) && aliquotDto.getNeatOrDilutionText().equals("Neat") && !(aliquotDto.getIsDry()))
				{
				EditAliquot.this.error("Please enter value for Desired Concentration ");
				return false;
				}
			if (StringUtils.isNullOrEmpty(aliquotDto.getNeatOrDilutionText()))
				{
				EditAliquot.this.error("Please choose Neat or Dilution");
				return false;
				}
			if (aliquotDto.getNeatOrDilutionText().equals("Choose One"))
				{
				EditAliquot.this.error("Please choose Neat or Dilution");
				return false;
				}
			if (aliquotDto.getNeatOrDilutionText().equals("Neat") && StringUtils.isNullOrEmpty(aliquotDto.getWeightedAmount()))
				{
				EditAliquot.this.error("Please put in a weighted amount");
				return false;
				}
			if (aliquotDto.getNeatOrDilutionText().equals("Neat") && StringUtils.isNullOrEmpty(aliquotDto.getWeightedAmountUnits()))
				{
				EditAliquot.this.error("Please choose a unit for the weighted amount");
				return false;
				}
			if (aliquotDto.getNeatOrDilutionText().equals("Neat") && StringUtils.isNullOrEmpty(aliquotDto.getDConc()) && !aliquotDto.getIsDry())
				{
				EditAliquot.this.error("Please put in a desired concentration");
				return false;
				}	
			if (aliquotDto.getNeatOrDilutionText().equals("Neat") && StringUtils.isNullOrEmpty(aliquotDto.getDConcentrationUnits()) && !aliquotDto.getIsDry() )
				{
				EditAliquot.this.error("Please choose a unit for the desired concentration");
				return false;
				}
			if (StringUtils.isNullOrEmpty(aliquotDto.getNeatOrDilutionUnits()))
				{
				EditAliquot.this.info ("Please choose Units");
				return false;
				}
			if (aliquotDto.getNeatOrDilutionText()!= null && aliquotDto.getNeatOrDilutionText().equals("Neat") && !NumberUtils.verifyDecimalRange(aliquotDto.getWeightedAmount(), 8, 7)) 
			    {  
				EditAliquot.this.info ("Please make sure that the weighted amount has no more than 8 digits for the whole number and 7 digits for the decimal place");
				return false;
			    }
			if (aliquotDto.getNeatOrDilutionText()!= null && aliquotDto.getNeatOrDilutionText().equals("Neat") && !aliquotDto.getIsDry() && !NumberUtils.verifyDecimalRange(aliquotDto.getDConc(), 8, 7)) 
			    {  
				EditAliquot.this.info ("Please make sure that the desired concentration has no more than 8 digits for the whole number and 7 digits for the decimal place");
				return false;
			    }
			return true;
		    }
		
		//issue 196
		public Boolean getIsNoInventory() 
		    {
			return isNoInventory;
		    }

		//issue 196
		public void setIsNoInventory(Boolean isNoInventory )
		    {
			this.isNoInventory = isNoInventory;
		    } 
		
		}
	       
	}

package edu.umich.brcf.metabolomics.panels.lims.mixtures;
/***********************
 * Created By:  Julie Keros 
 * Date:  Feb 15 2021
 * Add mixtures through metlims issue 123
 ********************/

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.validation.validator.StringValidator;
import org.h2.util.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.transaction.annotation.Transactional;

import edu.umich.brcf.metabolomics.layers.service.CompoundNameService;
import edu.umich.brcf.metabolomics.layers.service.Mrc2MixtureDataService;
import edu.umich.brcf.shared.layers.domain.Aliquot;
import edu.umich.brcf.shared.layers.domain.Mixture;
import edu.umich.brcf.shared.layers.dto.MixtureDTO;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.MixtureService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.util.FieldLengths;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.utilpackages.NumberUtils;

public class MixturesAdd extends WebPage
	{
	// issue 123
	@SpringBean
	CompoundNameService compoundNameService;	
	// issue 61
	@SpringBean 
	AliquotService aliquotService;
	@SpringBean
	Mrc2MixtureDataService mixtureDataService;
	@SpringBean
	MixtureService mixtureService;
	@SpringBean 
	AssayService assayService;
	@SpringBean
	UserService userService;
	FeedbackPanel aFeedback;
	String assayId = "";
	AliquotInfo aliquotInfo;
	MixtureInfo mixtureInfo;
	MixAliquotInfo mixAliquotInfo;
	List <MixAliquotInfo> mAliquotList ;
	String aliquotId = "";
	String volumeTxt = "";
	String concentrationTxt = "";
	List <String> aliquotDryList = new ArrayList <String> ();
	WebMarkupContainer dryContainer, wetContainer, dryContainerMix, wetContainerMix , tdcomponent, tdcomponentwet, tdComponentAliquotDry, tdComponentAliquotWet ;	// issue 196
	Map<String, List<String>> aliquotMap = new HashMap<String, List<String>>();	
	//Map<String, List<String>> aliquotMapDry = new HashMap<String, List<String>>();	issue 196
	List <String> taliquots = new ArrayList <String> ();
	List <String> allAliquots = new ArrayList <String> ();
	Map<String, List<String>> mixtureAliquotMap = new HashMap<String, List<String>>();
	Map<String, List<MixAliquotInfo>> mixtureAliquotInfoMap = new HashMap<String, List<MixAliquotInfo>>();
	List <String> aliquotList;
	List <AliquotInfo> aliquotInfoList = new ArrayList<AliquotInfo> ();
	List <AliquotInfo> tAliquotInfoList = new ArrayList<AliquotInfo> ();
	List <MixtureInfo> mixtureInfoList = new ArrayList<MixtureInfo> (); 
	List <MixAliquotInfo> mixAliquotInfoList = new ArrayList<MixAliquotInfo> (); 
	List <MixAliquotInfo> gMixAliquotInfoList = new ArrayList<MixAliquotInfo> (); 
	List <String> volumeList, volumeUnitList;
	List <Character> dryRetiredList = new ArrayList<Character> ();
	List <String> concentrationList = new ArrayList<String> ();
	List <String> volumeMixtureList = new ArrayList<String> ();
	List <String> volumeMixtureUnitList = new ArrayList<String> ();
	List <String> concentrationFinalList =new ArrayList<String> ();
	List <String> concentrationMixtureList =new ArrayList<String> ();
	ListView listViewAssay; // issue 94
	ListView<AliquotInfo> listViewAliquots; // issue 94
	ListView<MixtureInfo> listViewMixtures; // issue 123
	ListView<MixAliquotInfo> listViewAliquotsOfMixtures; // issue 123
	DropDownChoice<String> finalVolumeUnitsDD;
	DropDownChoice<String> volumeAliquotUnitsDD;
	DropDownChoice<String> volumeMixtureUnitsDD;
	String concentrationToolTip  = "";
	
	//MixturesAdd MixturesAdd = this;
	Label volumeAliquotLabel;
	Label concentrationAliquotUnitsLabel;
	Label mixtureNameLabel;
	Label aliquotNameLabel;	
	Label solventLabel;
	Label finalVolumeLabel;
	Label aNameLabel;
	Label mixLabel;
	Label aliquotIdForMixtureLabel;
	TextField molecularWeightMixText;
	TextField mixtureNameText;
	TextField solventText;
	TextField finalVolumeText;
	TextField volumeAliquotText;
	TextField concentrationAliquotText;
	TextField concentrationAliquotFinalText;
	TextField volumeMixtureText;
	TextField concentrationMixtureText; 
	TextField concentrationMixtureAliquotText;
	TextField concentrationMixtureAliquotFinalText;
	TextField concentrationUnitMixtureAliquotText;
	TextField concentrationAliquotUnitsAliquotText;
	TextField molecularWeightText;	
	TextField weightedAmountText,weightedAmountMixText ,weightedAmountUnitsText, weightedAmountMixUnitText;
	Label molecularWeightLabel,molecularWeightLabelMix ;
	Label weightedAmountLabel,weightedAmountLabelMix;
	// itemList	
	MixtureDTO mixtureDto = new MixtureDTO();
	MixturesAdd MixturesAdd = this;
	boolean isBuildVisible = false;
	AjaxButton calculateButton;
	ListMultipleChoice aliquotsChosen;
	String mId;
    ListMultipleChoice mixturesSelected;
    ListMultipleChoice aliquotNoAssaysSelected;
    ListMultipleChoice aliquotNoAssaysSelectedDry;
	Label mixtureDDLabel;
	Label aliquotNoAssayLabel;
	Label aliquotNoAssayLabelDry;
	Label concentrationAliquotLabel;
	Label aliquotIdLabel;
	WebMarkupContainer mixtureBuildContainer;
	List <AliquotInfo> existingAliquotInfoList;
	List <AliquotInfo> newAliquotInfoList;
	List <String> mIdList = new ArrayList <String> ();
	Map <String, String> toolTipsMap = new HashMap <String, String> ();
	Map <String, String> toolTipsMapMixture = new HashMap <String, String> ();
	MixturesAdd mixturesAdd = this;
	Boolean doClear = false;
	MixturesAddForm mixturesAddForm ;
	String gAliquotId = null;
	String gMixId = null;
	Map<String, List<String>> aliquotMapBeforeDelete = new HashMap<String, List<String>>(); 
	Map <String, String> nameMapForAliquot = new HashMap <String, String> ();
	Boolean buildMixtureButtonPressed  = false;
	Boolean deleteAliquotPressed = false;
	boolean inEditMixture = false;
	String mixtureButtonLabel = "Create Mixture";
	// issue 123
	private Map<String, String> mixtureNamesAlreadyInDatabase;
	Map <String, List<String>> aliquotMapOfEditAliquots = new HashMap <String, List<String>> ();
	List <String> aliquotForEditList = new ArrayList <String> ();
	List <String> mixtureForEditList = new ArrayList <String> ();
	String volaliquotUnits;
	String volumeAliquotUnits; // issue 196
	IModel <List<Mixture>> assayModel = new LoadableDetachableModel() 
		{
		protected Object load() { return getAssayList(); }
		}	;
		
	IModel <List<AliquotInfo>> aliquotMixtureModel = new LoadableDetachableModel() 
		{
		protected Object load() { return mixturesAddForm.getAliquotInfoList(); }
		}	;
				
	IModel <List<MixtureInfo>> mixtureInfoModel = new LoadableDetachableModel() 
        {
		protected Object load() { return getMixtureInfoList(); }
		}	;   
	
	public MixturesAdd(String id,   ModalWindow modal1)
		{
	    this (id, null, modal1, false);
		}
		
	// issue 138
	public MixturesAdd(String id, Mixture mixture,  ModalWindow modal1, boolean editingMixture) 
		{	
	    toolTipsMap = buildToolTipForAliquotsMap ();
	    toolTipsMapMixture = buildToolTipForMixturesMap (mixture);
		mixtureNamesAlreadyInDatabase = mixtureService.allMixtureIdsNamesMap(mixture);
		inEditMixture = editingMixture;
		if (editingMixture )
		    {
			aliquotDryList =  aliquotService.loadAliquotListDryKeepDryForEdit() ;
			aliquotMap = buildAliquotMapAndAliquotInfoForEdit(mixture);
			isBuildVisible = true;
			mixtureButtonLabel = "Save Mixture";
			mixtureDto.setDesiredFinalVolume(mixture.getDesiredFinalVol().toString());
		    mixtureDto.setMixtureName(mixture.getMixtureName());
		    mixtureDto.setVolumeSolventToAdd(mixture.getVolSolvent().toString());
		    mixtureDto.setFinalVolumeUnits(mixture.getDesiredFinalVolUnits()); // issue 196
		    for (String lilA : mixtureDto.getAliquotNoAssayMultipleChoiceList())
		    	aliquotForEditList.add(lilA);
		    // issue 196
		    for (String lilB : mixtureDto.getAliquotNoAssayMultipleChoiceListDry())
		    	aliquotForEditList.add(lilB);
		    buildSecondaryMixtureAliquotForEdit(mixture);
		    if ( mixtureDto.getMixtureList() != null)
		    	{
		    	for (String lilM : mixtureDto.getMixtureList())
		    		mixtureForEditList.add(lilM);
		    	}
		    }
		add(new MixturesAddForm("MixturesAddForm", modal1, editingMixture, mixture));			
		}
	
	// issue 75
	public final class MixturesAddForm extends Form 
		{		
		List <String> inventoryList;
		List <String> assayList;
		String theblankinput;
		String volaliquotUnits;
		
		public MixturesAddForm (String id,  final ModalWindow modal1)
			{
			this(id,   modal1,  false, null);
			}
	
		public MixturesAddForm (String id,  final ModalWindow modal1, boolean editMixture, Mixture mixtureToEdit)
		    {		
			super(id, new CompoundPropertyModel(mixtureDto));	
			
			if (mixtureToEdit != null )
				mixtureDto.setMixtureId(mixtureToEdit.getMixtureId());
			mixturesAddForm = this;
			MixturesAdd.setOutputMarkupId(true);
			MixturesAdd.setOutputMarkupPlaceholderTag(true);
			mixtureBuildContainer = new WebMarkupContainer("mixtureBuildContainer");
			aFeedback = new FeedbackPanel("feedback");
			aFeedback.setEscapeModelStrings(false);		
			mixtureBuildContainer.add(aFeedback);
			add(mixtureBuildContainer);
			add(confirmBehavior);
			add(confirmBehaviorMixture);
			mixtureBuildContainer.setOutputMarkupId(true);
			mixtureBuildContainer.setOutputMarkupPlaceholderTag(true);		
			mixtureNameLabel = new Label("mixtureNameLabel", "Mixture Name:")
				{
				};
			
			mixLabel = new Label("mixLabel", editMixture? "Edit Mixture:" + mixtureToEdit.getMixtureId() : "Create Mixture" )
				{
				};
			solventLabel = new Label("solventLabel", "Solvent to add:")
				{
				};	
				
			finalVolumeLabel = new Label("finalVolumeLabel", "Final Volume:")
				{
				};	
						
			mixtureNameText = new TextField("mixtureName")
				{
				};
			mixtureNameText.add(StringValidator.maximumLength(FieldLengths.MRC2_MIXTURE_NAME));
				
			solventText = new TextField("volumeSolventToAdd")
				{
				public boolean isEnabled()
					{ 
					return false;
					}
				};	
			solventText.setOutputMarkupId(true);
			finalVolumeText = new TextField("desiredFinalVolume")
				{
				};	
				
			finalVolumeUnitsDD = new DropDownChoice("finalVolumeUnits", Arrays.asList(new String[] {"uL", "mL", "L" }) )
				{
				
				};	
			
			finalVolumeUnitsDD.setOutputMarkupId(true);	
			finalVolumeUnitsDD.setOutputMarkupPlaceholderTag(true) ;
		//	finalVolumeUnitsDD.add(buildStandardFormComponentUpdateBehavior("change", "updateNeatOrDilutionUnits", )); // issue 27 2020
			
			// issue 196
			finalVolumeUnitsDD.add(buildStandardFormComponentUpdateBehavior("change", "updateFinalVolumeUnits", mixtureDto,  MixturesAdd , "NoAssay"));
			finalVolumeUnitsDD.setRequired(true); 
			mixtureBuildContainer.add(finalVolumeUnitsDD);
			if (!editMixture && StringUtils.isNullOrEmpty(mixtureDto.getFinalVolumeUnits()))	
				mixtureDto.setFinalVolumeUnits("L");	
			mixtureBuildContainer.add(mixtureNameLabel)	;
			mixtureBuildContainer.add(solventLabel)	;
			mixtureBuildContainer.add(finalVolumeLabel);
			mixtureBuildContainer.add(mixtureNameText)	;
			mixtureBuildContainer.add(solventText)	;
			mixtureBuildContainer.add(finalVolumeText)	;		
			mixtureBuildContainer.add(mixLabel);
			aliquotNoAssaysSelected = new ListMultipleChoice ("aliquotNoAssayMultipleChoiceList",aliquotService.loadAliquotListNoAssay() )	
			    {
			     @Override
				 protected void onComponentTag(ComponentTag tag)
				    {
				    super.onComponentTag(tag);
				     tag.put("onfocus", "this.size = 20;");
				     tag.put("onmouseover", "this.size = 20;");
				     tag.put("onmouseleave", "this.size = 1;");
				    }
			     
			 public void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag)
					{
					List<?> choices = getChoices();
					final AppendingStringBuffer buffer = new AppendingStringBuffer((choices.size() * 50) + 16);
					final String selectedValue = getValue();
					buffer.append(getDefaultChoice(selectedValue));
					for (int index = 0; index < choices.size(); index++)
						{
						Object choice = choices.get(index);
						if (aliquotForEditList.contains(choice.toString()))
							buffer.append("<option title=" + "\"" + toolTipsMap.get(choice.toString().trim()) + "\"" +  " value=\"" + index + "\"" + "selected>" + choice.toString() + "</option>");
						else
							buffer.append("<option title=" + "\"" + toolTipsMap.get(choice.toString().trim()) + "\"" +  " value=\"" + index + "\"" + ">" + choice.toString() + "</option>");	
						}
					buffer.append('\n');
					replaceComponentTagBody(markupStream, openTag, buffer);
					}
				};	
					
				add (aliquotNoAssaysSelected);	
				aliquotNoAssaysSelected.add(buildStandardFormComponentUpdateBehavior("change", "updateAliquotNoAssay", mixtureDto,  MixturesAdd , "NoAssay"));
				aliquotDryList =  aliquotService.loadAliquotListDryKeepDryForEdit() ;
				aliquotNoAssaysSelectedDry = new ListMultipleChoice ("aliquotNoAssayMultipleChoiceListDry",aliquotService.loadAliquotListDry(mixtureToEdit == null ? null :mixtureToEdit.getMixtureId()) )	
				    {
				     @Override
					 protected void onComponentTag(ComponentTag tag)
					    {
					    super.onComponentTag(tag);
					     tag.put("onfocus", "this.size = 20;");
					     tag.put("onmouseover", "this.size = 20;");
					     tag.put("onmouseleave", "this.size = 1;");
					    }
					
				     public void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag)
						{
						List<?> choices = getChoices();
						final AppendingStringBuffer buffer = new AppendingStringBuffer((choices.size() * 50) + 16);
						final String selectedValue = getValue();
						buffer.append(getDefaultChoice(selectedValue));
						for (int index = 0; index < choices.size(); index++)
							{
							Object choice = choices.get(index);
							if (aliquotForEditList.contains(choice.toString()))
								buffer.append("<option title=" + "\"" + toolTipsMap.get(choice.toString().trim()) + "\"" +  " value=\"" + index + "\"" + "selected>" + choice.toString() + "</option>");
							else
								buffer.append("<option title=" + "\"" + toolTipsMap.get(choice.toString().trim()) + "\"" +  " value=\"" + index + "\"" + ">" + choice.toString() + "</option>");	
							}
						buffer.append('\n');
						replaceComponentTagBody(markupStream, openTag, buffer);
						}
					};	
					
				add (aliquotNoAssaysSelectedDry);	
				aliquotNoAssaysSelectedDry.add(buildStandardFormComponentUpdateBehavior("change", "updateAliquotNoAssayDry", mixtureDto,  MixturesAdd , "NoAssay"));
	
				//////////////
				// issue 123			
				mixturesSelected = new ListMultipleChoice ("mixtureList",mixtureService.getNonComplexMixtureIds(mixtureToEdit) )	
				    {
				    @Override
					protected void onComponentTag(ComponentTag tag)
					    {
					    super.onComponentTag(tag);
					     tag.put("onfocus", "this.size = 20;");
					     tag.put("onmouseover", "this.size = 20;");
					     tag.put("onmouseleave", "this.size = 1;");
					    }
			     
			    public void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag)
					{
					List<?> choices = getChoices();
					final AppendingStringBuffer buffer = new AppendingStringBuffer((choices.size() * 50) + 16);
					final String selectedValue = getValue();
					buffer.append(getDefaultChoice(selectedValue));
					for (int index = 0; index < choices.size(); index++)
						{
						Object choice = choices.get(index);
						if (mixtureForEditList.contains(choice.toString()))
							buffer.append("<option title=" + "\"" + toolTipsMapMixture.get(choice.toString().trim()) + "\"" +  " value=\"" + index + "\"" + "selected>" + choice.toString() + "</option>");
						else
							buffer.append("<option title=" + "\"" + toolTipsMapMixture.get(choice.toString().trim()) + "\"" +  " value=\"" + index + "\"" + ">" + choice.toString() + "</option>");
						//buffer.append("<option title=" + "\"" + toolTipsMapMixture.get(choice.toString().trim()) + "\"" +  " value=\"" + index + "\"" + ">" + choice.toString() + "</option>");	
						}
					buffer.append('\n');
					replaceComponentTagBody(markupStream, openTag, buffer);
					}
				};					
			add (mixturesSelected);			
			mixturesSelected.add(buildStandardFormComponentUpdateBehavior("change", "updateMixture", mixtureDto,  MixturesAdd , assayId));	
			mixtureDDLabel = new Label("mixturesChoiceLabel", "Choose Mixture(s) if desired.");
			add(mixtureDDLabel);						
			aliquotNoAssayLabel = new Label("chooseAliquotNoAssayLabel", "Choose Non-Dry Aliquot(s) from global list OR ");
			add(aliquotNoAssayLabel);		
			
			// issue 196
			aliquotNoAssayLabelDry = new Label("chooseAliquotNoAssayLabelDry", "Choose Dry Aliquot(s) from global list OR ");
			add(aliquotNoAssayLabelDry);	
			
			
			// issue 123					
			// issue 94			
			add(listViewAssay = new ListView("assayList", assayModel) 
				{
				public void populateItem(final ListItem listItem) 
					{
			        assayId = (String) listItem.getModelObject();		
				    listItem.add(new Label("AssayId", new Model(shortVersionOfAssay(assayId))).add(AttributeModifier.append("title", assayId)));
					aliquotsChosen=new  ListMultipleChoice("aliquotList", aliquotService.loadAliquotList(StringParser.parseId(assayId)))
				    	{
						@Override
						protected void onComponentTag(ComponentTag tag)
							{
							super.onComponentTag(tag);
							tag.put("onfocus", "this.size = 10;");
							tag.put("onmouseover", "this.size = 10;");
						    tag.put("onmouseleave", "this.size = 1;");						    
							}	
						
						@Override
						public void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag)
							{
							List<?> choices = getChoices();
							final AppendingStringBuffer buffer = new AppendingStringBuffer((choices.size() * 50) + 16);
							final String selectedValue = getValue();
							buffer.append(getDefaultChoice(selectedValue));
							for (int index = 0; index < choices.size(); index++)
								{
								Object choice = choices.get(index);
								buffer.append("<option title=" + "\"" + toolTipsMap.get(choice.toString().trim()) + "\"" +  " value=\"" + index + "\"" + ">" + choice.toString() + "</option>");	
								}
							buffer.append('\n');
							replaceComponentTagBody(markupStream, openTag, buffer);
							}			
						};	
					aliquotsChosen.setOutputMarkupId(true);
					aliquotsChosen.setEscapeModelStrings(false);
					aliquotsChosen.setOutputMarkupPlaceholderTag(true);
					aliquotsChosen.add(buildStandardFormComponentUpdateBehavior("change", "updateAliquot", mixtureDto,  MixturesAdd , assayId));
					List<String> aliquotChoiceList = new ArrayList <String> ();
					int i = 0;
					listItem.add(aliquotsChosen);
					}
				});	
		    mixtureBuildContainer.add(listViewAliquots =  new ListView<AliquotInfo>("aliquotInfoList", new PropertyModel(this, "aliquotInfoList"))	
			    {				
				private AjaxLink buildDeleteAliquotButton(String id, final String aliquotId)
				    {
					AjaxLink link = new AjaxLink <Void>(id)
					    {
					    @Override
					    public void onClick(final AjaxRequestTarget target)
						    {
					    	gAliquotId = aliquotId;
					    	target.appendJavaScript("if (confirm('Are you sure that you would like to remove this Aliquot from the mixture?  Clicking the build mixture button on the left will bring this aliquot back." 
			                           + "?')) { " +  confirmBehavior.getCallbackScript() + " }"  );	
				    		}				 		    
					    };			    
				    return link;	
				    }
					
				@Override
				public boolean isVisible()
					{
					return isBuildVisible;
					}
				public void populateItem(final ListItem listItem) 
					{
					aliquotInfo = (AliquotInfo) listItem.getModelObject();	
					listItem.add(aliquotIdLabel = new Label("aliquotId", new Model(aliquotInfo.aliquotId)));
					wetContainer = new WebMarkupContainer("wetContainer")
						{
						};
					dryContainer = new WebMarkupContainer("dryContainer")
						{
						};	
						
					tdComponentAliquotDry = new WebMarkupContainer("tdComponentAliquotDry")
						{
						};	
					tdComponentAliquotWet = new WebMarkupContainer("tdComponentAliquotWet")
						{
						};	
					listItem.add(tdComponentAliquotDry);
					listItem.add(tdComponentAliquotWet);
									
					tdComponentAliquotWet.add(wetContainer);
					tdComponentAliquotDry.add(dryContainer);
				    
					
					volumeAliquotUnitsDD = new DropDownChoice("volumeAliquotUnits",new PropertyModel<String>(aliquotInfo, "volumeAliquotUnits"),Arrays.asList(new String[] {"uL", "mL", "L" }) )
						{
					    
						};	
						
					volumeAliquotUnitsDD.setOutputMarkupId(true);	
					volumeAliquotUnitsDD.setOutputMarkupPlaceholderTag(true) ;
					// issue 196
					volumeAliquotUnitsDD.add(buildStandardFormComponentUpdateBehavior("change", "updateVolumeAliquotUnits", mixtureDto,  MixturesAdd , "NoAssay"));
					if (!editMixture && StringUtils.isNullOrEmpty(aliquotInfo.getVolumeAliquotUnits()))
						aliquotInfo.setVolumeAliquotUnits("L");	
					wetContainer.add(volumeAliquotUnitsDD);
			    	String cid = aliquotService.getCompoundIdFromAliquot(aliquotInfo.aliquotId);
			    	String aName = compoundNameService.getCompoundName(cid);
			    	aliquotIdLabel.add(AttributeAppender.replace("title", aName));
			    	wetContainer.add(AttributeAppender.replace("style", "display:none"));
			    	
			    	wetContainer.add( volumeAliquotLabel = new Label("volumeAliquotLabel", "Volume Added:")
						{
						}	
						);	
					wetContainer.add(concentrationAliquotLabel = new Label("concentrationAliquotLabel", "Aliquot Concentration:")
						{
						}							
						);
					listItem.add(new Label("concentrationAliquotFinalLabel", "Concentration (Final):"));
					listItem.add(buildDeleteAliquotButton("deleteAliquotButton", aliquotInfo.getAliquotId()).setOutputMarkupId(true));
					wetContainer.add(concentrationAliquotUnitsLabel = new Label("concentrationAliquotUnitsLabel", new Model("Units")));	
					// issue 196
					dryContainer.add(molecularWeightLabel = (molecularWeightLabel = new Label("molecularWeightLabel", "Molecular Weight:")));
					molecularWeightText =  new TextField("molecularWeight", new PropertyModel<String>(aliquotInfo, "molecularWeightTxt"))
						{
						public boolean isEnabled()
							{ 
							return false;
							}
						};					
					dryContainer.add(molecularWeightText);
					
					// issue 196
					dryContainer.add(weightedAmountLabel = (weightedAmountLabel = new Label("weightedAmountLabel", "Weighed Amount:")));
					weightedAmountText =  new TextField("weightedAmount", new PropertyModel<String>(aliquotInfo, "weightedAmountTxt"))
						{
						public boolean isEnabled()
							{ 
							return false;
							}
						};					
					dryContainer.add(weightedAmountText);
					
					// issue 196
					weightedAmountUnitsText =  new TextField("weightedAmountUnits", new PropertyModel<String>(aliquotInfo, "weightedAmountUnitsTxt"))
						{
						public boolean isEnabled()
							{ 
							return false;
							}
						};
					
					
					dryContainer.add(weightedAmountUnitsText);
					concentrationAliquotUnitsAliquotText =  new TextField("concentrationAliquotUnitsAliquot", new PropertyModel<String>(aliquotInfo, "concentrationUnitsTxt"))
						{
						public boolean isEnabled()
							{ 
							return false;
							}
						};
					
					concentrationAliquotText = new TextField("concentrationAliquot", new PropertyModel<String>(aliquotInfo, "concentrationTxt"))
						{
						public boolean isEnabled()
							{ 
							return false;
							}
						};

					volumeAliquotText = new TextField <String >("volumeAliquot", new PropertyModel<String>(aliquotInfo, "volumeTxt"))
						{
						
					
						};	
						
					concentrationAliquotFinalText = new TextField("concentrationAliquotFinal", new PropertyModel<String>(aliquotInfo, "concentrationTxtFinal"))
						{
						public boolean isEnabled()
							{ 
							return false;
							}
						};			
					wetContainer.add(concentrationAliquotText);					
					listItem.add(concentrationAliquotFinalText);
					concentrationToolTip = buildFinalConcentrationToolTip(concentrationAliquotFinalText.getDefaultModelObjectAsString(), aliquotInfo.getConcentrationTxtFinal())	;	
					concentrationAliquotFinalText.add(AttributeAppender.replace("title",concentrationToolTip));		
					concentrationAliquotFinalText.setOutputMarkupId(true);
					wetContainer.add(volumeAliquotText);
					wetContainer.add(concentrationAliquotUnitsAliquotText);
					// issue 196:
					if (aliquotDryList.contains(aliquotInfo.aliquotId))
				     	{
						wetContainer.add(AttributeAppender.replace("style", "display:none"));
						dryContainer.add(AttributeAppender.replace("style", "display:block"));
						tdComponentAliquotWet.add(AttributeAppender.replace("style", "display:none"));
						tdComponentAliquotDry.add(AttributeAppender.replace("style", "display:block"));						
				     	}
					else 
						{
						wetContainer.add(AttributeAppender.replace("style", "display:block;text-align : left"));
						dryContainer.add(AttributeAppender.replace("style", "display:none"));
						tdComponentAliquotWet.add(AttributeAppender.replace("style", "display:block"));
						tdComponentAliquotDry.add(AttributeAppender.replace("style", "display:none"));
						}
					}
				});	
			
			// Issue 123 
			mixtureBuildContainer.add(listViewMixtures = new ListView<MixtureInfo>("mixtureInfoList", mixtureInfoModel) 
				{
				@Override
				public boolean isVisible()
					{
					return isBuildVisible;
					}
				public void populateItem(final ListItem listItem) 
					{
					mixtureInfo = (MixtureInfo) listItem.getModelObject();	
					mixtureInfo.setAliquotDryList(aliquotDryList);
					listItem.add(new Label("mixtureId", new Model(mixtureInfo.mixtureId)));
					listItem.add(new Label("volumeMixtureLabel", "Volume Added:"));
						  
					volumeMixtureText = new TextField <String >("volumeMixture", new PropertyModel<String>(mixtureInfo, "mixtureVolumeTxt"))
						{
						};
					listItem.add(volumeMixtureText);
					
					volumeMixtureUnitsDD = new DropDownChoice("volumeMixtureUnits",new PropertyModel<String>(mixtureInfo, "volumeMixtureUnits"),Arrays.asList(new String[] {"uL", "mL", "L" }) )
						{
						};
					
					volumeMixtureUnitsDD.setOutputMarkupId(true);	
					volumeMixtureUnitsDD.setOutputMarkupPlaceholderTag(true) ;
					// issue 196					
					volumeMixtureUnitsDD.add(buildStandardFormComponentUpdateBehavior("change", "updateVolumeMixtureUnits", mixtureDto,  MixturesAdd , "NoAssay"));	
					listItem.add(volumeMixtureUnitsDD);
					
					listItem.add(buildDeleteMixtureButton("deleteMixtureButton", mixtureInfo.mixtureId).setOutputMarkupId(true));
					mId = mixtureInfo.getMixtureId();
					mIdList.add(mId);
					mixtureInfo.setListObject(mixtureService.aliquotsForMixtureId(mId));
					
					listItem.add(listViewAliquotsOfMixtures =  new ListView<MixAliquotInfo>("mixtureInfoAliquotList", new PropertyModel(mixtureInfo, "mAliquotList"))
					    {
						public void populateItem(final ListItem listItema) 
							{
							mixAliquotInfo = (MixAliquotInfo) listItema.getModelObject();
							mixtureAliquotMap.put(mixAliquotInfo.getMixtureId(), mixtureInfo.getListAliquots());
							String aliquotIdStr = mixAliquotInfo.getAliquotId();							
							// issue 196
							wetContainerMix = new WebMarkupContainer("wetContainerMix")
								{
								};
							dryContainerMix = new WebMarkupContainer("dryContainerMix")
								{
								};
						    tdcomponent = new WebMarkupContainer("tdcomponent")
								{
								};
							tdcomponentwet = new WebMarkupContainer("tdcomponentwet")
								{
								};
							tdcomponentwet.add(wetContainerMix);
							tdcomponent.add(dryContainerMix);
							listItema.add(tdcomponent);
							listItema.add(tdcomponentwet);
							//issue 196		
							if (aliquotDryList.contains(mixAliquotInfo.aliquotId))
								{
								wetContainerMix.add(AttributeAppender.replace("style", "display:none"));
								dryContainerMix.add(AttributeAppender.replace("style", "display:block"));
								tdcomponent .add(AttributeAppender.replace("style", "display:block"));
								tdcomponentwet.add(AttributeAppender.replace("style", "display:none"));
								}
							else 
								{
								wetContainerMix.add(AttributeAppender.replace("style", "display:block;text-align : left"));
								dryContainerMix.add(AttributeAppender.replace("style", "display:none"));
								tdcomponent .add(AttributeAppender.replace("style", "display:none"));
								tdcomponentwet.add(AttributeAppender.replace("style", "display:block"));
								}
								
							dryContainerMix.add(molecularWeightLabelMix = new Label("molecularWeightLabelMix", new Model("Molecular Weight:")));
							
							
							molecularWeightMixText = new TextField("molecularWeightMix", new PropertyModel<String>(mixAliquotInfo, "molecularWeightMix"))
								{
								@Override
								public boolean isEnabled()
									{ 
									return false;
									}
								};
							//////////////////////////////////
							// issue 196
								
							dryContainerMix.add(molecularWeightMixText);
							dryContainerMix.add(weightedAmountLabelMix = new Label("weightedAmountLabelMix", new Model("Weighed Amount:")));
							weightedAmountMixText = new TextField("weightedAmountMix", new PropertyModel<String>(mixAliquotInfo, "weightedAmountMix"))
								{
								@Override
								public boolean isEnabled()
									{ 
									return false;
									}
								};
							dryContainerMix.add(weightedAmountMixText);
							
							// issue 196
							weightedAmountMixUnitText = new TextField("weightedAmountMixUnit", new PropertyModel<String>(mixAliquotInfo, "weightedAmountMixUnit"))
								{
								@Override
								public boolean isEnabled()
									{ 
									return false;
									}
								};
						dryContainerMix.add(weightedAmountMixUnitText);
							
							
							listItema.add(aliquotIdForMixtureLabel =  new Label("aliquotIdForMixture", new Model(aliquotIdStr)));							
							
							
							wetContainerMix.add(new Label("concentrationAliquotForMixtureLabel", new Model("Aliquot Concentration:")));				
							listItema.add(new Label("concentrationAliquotForMixtureFinalLabel", new Model("(Final)")));
							wetContainerMix.add(new Label("concentrationAliquotForMixtureUnitsLabel", new Model("Units")));
							String cid = aliquotService.getCompoundIdFromAliquot(aliquotIdStr);				             					    	
					    	String aname = compoundNameService.getCompoundName(cid);
					    	aliquotIdForMixtureLabel.add(AttributeAppender.replace("title", aname));
					    	
					    	concentrationUnitMixtureAliquotText = new TextField("concentrationUnitsAliquot", new PropertyModel<String>(mixAliquotInfo, "mixAliquotConUnits"))
								{
								@Override
								public boolean isEnabled()
									{ 
									return false;
									}
								};
							wetContainerMix.add (concentrationUnitMixtureAliquotText);
					    	concentrationMixtureAliquotFinalText = new TextField("concentrationAliquotForFinalMixture", new PropertyModel<String>(mixAliquotInfo, "mixAliquotConcentrationFinal"))
								{
								@Override
								public boolean isEnabled()
									{ 
									return false;
									}
								};
							concentrationToolTip = buildFinalConcentrationToolTip(concentrationMixtureAliquotFinalText.getDefaultModelObjectAsString(), mixAliquotInfo.getMixAliquotConcentrationFinal())	;	
							concentrationMixtureAliquotFinalText.add(AttributeAppender.replace("title",concentrationToolTip));	
							//wetContainerMix.add (concentrationMixtureAliquotFinalText);
							listItema.add (concentrationMixtureAliquotFinalText);
							concentrationMixtureAliquotText = new TextField("concentrationAliquotForMixture", new PropertyModel<String>(mixAliquotInfo, "mixAliquotConcentration"))
								{
								@Override
								public boolean isEnabled()
									{ 
									return false;
									}
								};
							wetContainerMix.add (concentrationMixtureAliquotText);
							//}
							///////////////
							}
						});
					}
				});	
			
			listViewAliquots.setOutputMarkupId(true);
			listViewAliquots.setOutputMarkupPlaceholderTag(true);				
			add(new Label("titleLabel", "Build MixturesAdd for compound:" + " " + " Note start by choosing the assay then choose the aliquots"));
			add(new Label("mixtureLabel", "Choose Mixture(s).   Note the choose mixtures option is disabled <br> for secondary mixtures.").setEscapeModelStrings(false));
			add(new Label("chooseAliquotLabel", "Choose Non-Dry Aliquot(s) based on Assay"));
			add(buildClearButton("clear"));
			add(new AjaxButton  ("buildMixture") 
			    { 		
				@Override
				public void onSubmit(AjaxRequestTarget target)
					{
					buildMixtureButtonPressed = true;
					mixtureDto.getAliquotNoAssayMultipleChoiceList().addAll(aliquotForEditList);
					if (aliquotMap.size() == 0  && (mixtureDto.getMixtureList() != null &&  mixtureDto.getMixtureList().size() == 0) )
					    {
						target.appendJavaScript(edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage("Please choose either an aliquot or mixture."));
						return;
						}
				    isBuildVisible = true;
				    doClear = false;
				    aliquotMap = getDupFreeAliquotMap(aliquotMap);
				    target.add(mixtureBuildContainer);
					}
				});
			calculateButton = new  AjaxButton ("calculateSolvent")					
		    	{
				@Override
				public boolean isEnabled()
					{
					return (aliquotInfoList.size() > 0 || mixtureInfoList.size() > 0  ) ;	
					}
				@Override
				public void onSubmit(AjaxRequestTarget target)
				    {	
					resetMixtureList();
					target.add(mixtureBuildContainer);
					volumeList = new ArrayList <String> ();	
					volumeUnitList = new ArrayList <String> ();
					Double sumOfVolumeAliquot = 0.0 ;
					Double calcSolventToAdd = 0.0;
					Double sumOfVolumeMixture = 0.0;
					String errCode = getErrorCheckCode (mixtureDto.getMixtureName(), "mixture name", "string");
					if (StringUtils.isNullOrEmpty(errCode))	
					   errCode = getErrorCheckCode (mixtureDto.getDesiredFinalVolume(), "desired final volume", "number");
					if (!StringUtils.isNullOrEmpty(errCode))	
				        {
					    MixturesAdd.this.error(errCode);
					    return;
					    }
					if (setAliquotConcentrationInfo(target,  null ) == -1)
						return;
					sumOfVolumeAliquot = sumOfVolumeAliquot = getSumVolumeList(volumeList, volumeUnitList, mixtureDto.getFinalVolumeUnits() );
					sumOfVolumeMixture = calculateSecondaryMixtureAliquotInfo ( target, volumeMixtureUnitList , mixtureDto.getFinalVolumeUnits());			
					calcSolventToAdd = Double.parseDouble(mixtureDto.getDesiredFinalVolume()) - sumOfVolumeAliquot - (sumOfVolumeMixture == null ? 0 : sumOfVolumeMixture);
					mixtureDto.setVolumeSolventToAdd(calcSolventToAdd.toString());		
					if (calcSolventToAdd < 0 )
						MixturesAdd.this.error("The Solvent To Add can not be less than 0.  Please use different volumes. " );
					
				    }
				@Override
				public void onError (AjaxRequestTarget target)
					{
					onError(target);
					}
		
		    	};	
		    
		    mixtureBuildContainer.add (calculateButton);
			mixtureBuildContainer.add(new AjaxButton   ("createMixture", new Model (mixtureButtonLabel)) 
				{
				@Override
				public boolean isEnabled()
					{
					return (aliquotInfoList.size() > 0 || mixtureInfoList.size() > 0  ) ;	
					}
				@Override
				@Transactional(rollbackFor = Exception.class)
				public void onSubmit(AjaxRequestTarget target)
					{
					resetMixtureList();
					target.add(mixtureBuildContainer);
					Double sumOfVolumeMixture = 0.0;
					Double sumOfVolumeAliquot = 0.0 ;
					Double calcSolventToAdd = 0.0;
					String errCode = getErrorCheckCode (mixtureDto.getMixtureName(), "mixture name", "string");
					if (StringUtils.isNullOrEmpty(errCode))	
					   errCode = getErrorCheckCode (mixtureDto.getDesiredFinalVolume(), "desired final volume", "number");
					if (!StringUtils.isNullOrEmpty(errCode))	
				       {
				       target.appendJavaScript(edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage(errCode));
					   return;
					   }
					List <String> aliquotIdList = new ArrayList <String> ();
					volumeList = new ArrayList <String> ();
					
					dryRetiredList = new ArrayList <Character> ();
					volumeUnitList = new ArrayList <String> (); // issue 196
					concentrationList = new ArrayList <String> ();
					volumeMixtureList = new ArrayList <String> ();
					volumeMixtureUnitList = new ArrayList <String> ();
					concentrationFinalList = new ArrayList <String> (); 
					concentrationMixtureList = new ArrayList <String> ();
					if (setAliquotConcentrationInfo(target,  aliquotIdList) == -1)
					    return;     
					sumOfVolumeMixture = 0.0;
					sumOfVolumeMixture = calculateSecondaryMixtureAliquotInfo ( target ,volumeMixtureUnitList , mixtureDto.getFinalVolumeUnits());
					if (sumOfVolumeMixture == null)
					    return;
					mixtureDto.setMixtureName(mixtureNameText.getDefaultModelObjectAsString());														
					mixtureDto.setAliquotList(aliquotIdList);
					mixtureDto.setAliquotVolumeList(volumeList);
					// issue 199
					mixtureDto.setDryRetiredList(dryRetiredList);
					mixtureDto.setAliquotVolumeUnitList(volumeUnitList);
					mixtureDto.setAliquotConcentrationList(concentrationFinalList);
					mixtureDto.setMixtureConcentrationList(concentrationMixtureList);
					mixtureDto.setMixtureVolumeList(volumeMixtureList);
					mixtureDto.setMixtureVolumeUnitList(volumeMixtureUnitList);
					mixtureDto.setDesiredFinalVolume(finalVolumeText.getDefaultModelObjectAsString());
				    mixtureDto.setAliquotVolumeUnitList(volumeUnitList);
					// issue 123 recalculate volume solvent to add.....
					Double solvToAddDec ;
					solvToAddDec = new BigDecimal(mixtureDto.getDesiredFinalVolume()).doubleValue();
					sumOfVolumeAliquot = getSumVolumeList(volumeList, volumeUnitList, mixtureDto.getFinalVolumeUnits() );
					calcSolventToAdd = Double.parseDouble(mixtureDto.getDesiredFinalVolume()) - sumOfVolumeAliquot - (sumOfVolumeMixture == null ? 0 : sumOfVolumeMixture);
					mixtureDto.setVolumeSolventToAdd(calcSolventToAdd.toString());
					if (calcSolventToAdd < 0 )
						{
						target.appendJavaScript(edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage("The Solvent To Add can not be less than 0.  Please use different volumes."));
						return;
						}				
					Iterator it = mixtureAliquotMap.entrySet().iterator(); 
					mixtureDto.setMixtureAliquotMap(mixtureAliquotMap);
					mixtureDto.setMixtureAliquotInfoMap(mixtureAliquotInfoMap);
					Session session = null;
			        Transaction tx = null;  
			        SessionFactory sessionFactory = null;
					try
					    {
						boolean calledFromMetlimsInterface = true;
						if (editMixture)
							{					
							mixtureService.updateMixtureAndChildrenAliquotInfo( mixtureToEdit, mixtureDto, aliquotInfoList);
							target.appendJavaScript(edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage("Changes to mixture: " + mixtureToEdit.getMixtureId()  + " have been saved."));
							modal1.close(target);
							}
						else
							{
							Mixture mixture = mixtureDataService.createNewMixture(mixtureDto, calledFromMetlimsInterface);
							target.appendJavaScript(edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage("Mixture: " + mixture.getMixtureId()  + " has been created."));						
							modal1.close(target);
							}
					    }
					catch (Exception e)
						{
						e.printStackTrace();
						target.appendJavaScript(edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage("There was an error saving the mixture"));
						}				
					}
				});
			
			add( new AjaxLink<Void>("close")
				{
				public void onClick(AjaxRequestTarget target)
					{ 
					modal1.close(target);
					}
				});	
            } //// end form 
			
		public void setInventoryList(List<String> inventoryList)
			{
			this.inventoryList= inventoryList;
			}
		
		public void setAliquotList(List<String> aList)
			{
		    aliquotList= aList;
			}

		private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(String event, final String response, final MixtureDTO mixtureDto , final MixturesAdd MixturesAdd, final String assayId)
			{
			return new AjaxFormComponentUpdatingBehavior(event)
		        {
			    @Override
			    protected void onUpdate(AjaxRequestTarget target)
			    	{
			    	List<String> newAliquotList = new ArrayList<String>();
			    	switch (response)
			        	{
			    	    case "updateFinalVolumeUnits" :
			    	    	break;
			    	    case "updateVolumeMixtureUnits" :
			    	    	//target.add(mixturesAddForm);
			    	    	
			    	        break;
			    	    case "updateVolumeAliquotUnits" :
			    	    	//aliquotInfo.setVolumeAliquotUnits(mixtureDto.getVolumeAliquotUnits());
			    	    	break;
				    	case "updateAliquot": 
				    		newAliquotList.addAll(mixtureDto.getAliquotList());
				    		aliquotMap.put(assayId, newAliquotList);
				    		if (aliquotMapBeforeDelete.size()> 0)
				    			{
				    			aliquotMapBeforeDelete.put(assayId, newAliquotList);
				    			aliquotMapBeforeDelete = getDupFreeAliquotMap(aliquotMapBeforeDelete);
				    			}
				    	    break;
				    	case "updateAliquotNoAssay": 
				    		newAliquotList.addAll(mixtureDto.getAliquotNoAssayMultipleChoiceList());				    		
				    		aliquotMap.put(assayId , newAliquotList);		
				    		if (aliquotMapBeforeDelete.size()> 0)
				    			{
				    			aliquotMapBeforeDelete.put(assayId , newAliquotList);
					    	    aliquotMapBeforeDelete = getDupFreeAliquotMap(aliquotMapBeforeDelete);
				    			}
				    		break;
				    	case "updateAliquotNoAssayDry": 
				    		newAliquotList.addAll(mixtureDto.getAliquotNoAssayMultipleChoiceListDry());					    		
				    		aliquotMap.put("NoAssayDry" , newAliquotList);
				    		if (aliquotMapBeforeDelete.size()> 0)
				    			{
				    			aliquotMapBeforeDelete.put("NoAssayDry" , newAliquotList);
					    	    aliquotMapBeforeDelete = getDupFreeAliquotMap(aliquotMapBeforeDelete);
				    			}
				    		break;	
				    	case "updateMixture":				    		
				    	    break;
				    	}
			    	}
			    };
			}
		
		public Map <String, List<String>> getDupFreeAliquotMap (Map<String, List<String>> aliquotMap)
			{
			Iterator it = aliquotMap.entrySet().iterator();
			Map<String, List<String>> dupFreeMap = new HashMap<String, List<String>>();
		    List<String> existsSoFar = new ArrayList <String> ();
    		while (it.hasNext()) 
    			{    			
    		    Map.Entry pairs = (Map.Entry)it.next(); 
    		    List <String> dupFreeAliquotList = new ArrayList ();
    		    for (String aliquotStr: (List <String>) pairs.getValue())
    		    	{
    		    	if (! existsSoFar.contains (aliquotStr))
    		    		{
    		    		existsSoFar.add(aliquotStr);
    		    		dupFreeAliquotList.add(aliquotStr);
    		    		}  		    	
    		    	}
    		    dupFreeMap.put(pairs.getKey().toString(), dupFreeAliquotList);
    			}
    		return dupFreeMap;
			}
		
		public String getErrorCheckCode (String valueToCheck, String fieldName, String fieldType)
			{	
			// issue 123
			
			if (StringUtils.isNullOrEmpty(valueToCheck) && fieldName.equals("aliquot volume")  && !aliquotDryList.contains(aliquotInfo.aliquotId))				
			    return "Error.  The " +  fieldName +  " can not be blank.";
			else 
				{
			    if (StringUtils.isNullOrEmpty(valueToCheck)	)		
			        return "Error.  The " +  fieldName +  " can not be blank.";
				}
			
			if (fieldName.equals("mixture name"))
				{
			    if ( mixtureService.isMixtureNameInDatabase(valueToCheck, mixtureNamesAlreadyInDatabase))
					{
					return "Error.  The mixture name:" + valueToCheck + " already exists.";
					}	
				}
			if (  (fieldType.equals("number") && fieldName.equals("aliquot volume") && !aliquotDryList.contains(aliquotInfo.aliquotId))  ||  (fieldType.equals("number") && fieldName.equals("mixture volume") ))
				{
				if (!NumberUtils.verifyDecimalRange(valueToCheck, 8, 7))
					return "Error.  The "   + fieldName + ": " + valueToCheck  + " is not a valid 8 digit 7 decimal place number. Please try again.";
				if (Double.parseDouble(valueToCheck) <= 0  )
					return "Error.  The "   + fieldName + ": " + valueToCheck  + " needs to be greater than 0. Please try again.";
				}
			return null;
			}
		
		public void setAliquotInfoList(List<AliquotInfo> aliquotInfoList)
			{
			aliquotInfoList = this.getAliquotInfoList();
			}
		
		public List<AliquotInfo> getAliquotInfoList()
			{	
			if (aliquotInfoList.size() == 0)
			    {
				for (Map.Entry<String,List<String>> entry : aliquotMap.entrySet()) 
					{
			        for (String aliquotStr : entry.getValue())
						{	
						AliquotInfo ainfo = new AliquotInfo("","");
						ainfo.aliquotId = aliquotStr;
						ainfo.volumeTxt = "";
						Aliquot ali = aliquotService.loadById(aliquotStr);	
						ainfo.concentrationTxt = ali.getNeat().equals('1') ? ali.getDconc().toString() : ali.getDcon().toString();
						ainfo.concentrationUnitsTxt = ali.getNeat().equals('1') ? ali.getDConcentrationUnits() :ali.getNeatSolVolUnits(); // issue 123
						//ainfo.concentrationTxt = ali.getNeat().equals('1') ? ali.getDconc().toString() : ali.getDcon().toString();
						//ainfo.concentrationUnitsTxt = ali.getNeat().equals('1') ? ali.getDConcentrationUnits() :ali.getNeatSolVolUnits(); // issue 123
						if (aliquotDryList.contains(aliquotStr))
							{
							ainfo.molecularWeightTxt = ali.getMolecularWeight().toString();
							ainfo.weightedAmountTxt = ali.getWeightedAmount().toString();
							ainfo.weightedAmountUnitsTxt = ali.getWeightedAmountUnits().toString();
							}
						else 
							{	
							ainfo.volumeAliquotUnits = "";
							}
						aliquotInfoList.add(ainfo);
						}
					} 			 
			     }
			else
				{
				updateAliquotList();
				}
			return aliquotInfoList;
			}
			
		public List<AliquotInfo> updateAliquotList()
			{			
		    existingAliquotInfoList = new ArrayList<AliquotInfo> ();
		    newAliquotInfoList = new ArrayList<AliquotInfo> ();
		    if (aliquotMapBeforeDelete.size() > 0 && buildMixtureButtonPressed && deleteAliquotPressed)	
		    	copyChoicesBeforeDelete();	
		    for (Map.Entry<String,List<String>> entry : aliquotMap.entrySet()) 
			    {
				for (String aliquotStr : entry.getValue())
				    {	
					if (entry.getValue() != null)
						{
						AliquotInfo ainfo = new AliquotInfo("","");
						ainfo = returnExistingAliquotInfo(aliquotStr);
						if (ainfo.aliquotId == null)
					    	{
							ainfo.aliquotId = aliquotStr;
						    newAliquotInfoList.add(ainfo);
					    	}
						else 
							existingAliquotInfoList.add(ainfo);
						}
					}
				}
			aliquotInfoList.clear();
			aliquotInfoList.addAll(existingAliquotInfoList);
			if ( aliquotInfoList.size() > 0)
				aliquotInfoList.addAll(aliquotInfoList.size(), newAliquotInfoList);
			return aliquotInfoList;
			}
		
		public AliquotInfo returnExistingAliquotInfo (String aliquotId)
			{
			for (AliquotInfo sAliquotInfo : aliquotInfoList)
				{
				if (sAliquotInfo.getAliquotId().trim().equals(aliquotId.trim()))
				    return sAliquotInfo;
				}
			AliquotInfo newAliquotInfo = new AliquotInfo("","");
			newAliquotInfo.aliquotId = null;
			newAliquotInfo.volumeTxt = "";
			newAliquotInfo.volumeAliquotUnits = "";
			Aliquot ali = aliquotService.loadById(aliquotId);
			newAliquotInfo.concentrationTxt = ali.getNeat().equals('1') ? ali.getDconc().toString() : ali.getDcon().toString();
			newAliquotInfo.concentrationUnitsTxt = ali.getNeat().equals('1') ? ali.getDConcentrationUnits() :ali.getNeatSolVolUnits(); // issue 123
			if (aliquotDryList.contains(aliquotId))
				{
				newAliquotInfo.molecularWeightTxt = ali.getMolecularWeight().toString();
				newAliquotInfo.weightedAmountTxt = ali.getWeightedAmount().toString();
				newAliquotInfo.weightedAmountUnitsTxt = ali.getWeightedAmountUnits().toString();
				}
			return newAliquotInfo;
			}
		
		// issue 196
		public int setAliquotConcentrationInfo(AjaxRequestTarget target, List <String> aliquotIdList )
			{
			String errCode = "";
			Double vol;
			Double desiredVolConverted = 1.0;
			Double desiredAliquotVolConverted = 1.0;
			Double desiredWeighedAmountConverted = 1.0;
			Double desiredConcentrationConverted = 1.0;
			
			for (AliquotInfo singleAliquotInfo : getAliquotInfoList()) 
				{
				if (!aliquotDryList.contains(singleAliquotInfo.aliquotId))
					{
					errCode = getErrorCheckCode (singleAliquotInfo.getVolumeTxt(), "aliquot volume", "number");				    
					if (StringUtils.isNullOrEmpty(errCode))						        
						errCode = getErrorCheckCode (singleAliquotInfo.getConcentrationTxt(), "aliquot concentration", "number");										
					if (!StringUtils.isNullOrEmpty(errCode))	
						{
						target.appendJavaScript(edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage(errCode));
						return -1;
						}
					}
				desiredVolConverted = Double.parseDouble(mixtureDto.getDesiredFinalVolume() ) * calculateConversion (mixtureDto.getFinalVolumeUnits());
				if (aliquotDryList.contains(singleAliquotInfo.aliquotId))
					{
					desiredWeighedAmountConverted = Double.parseDouble(singleAliquotInfo.getWeightedAmountTxt()) * calculateConversion (singleAliquotInfo.getWeightedAmountUnitsTxt());
					dryRetiredList.add('1'); // issue 199
					}
				else
					{					
					desiredAliquotVolConverted = Double.parseDouble(singleAliquotInfo.getVolumeTxt()) * calculateConversion (singleAliquotInfo.getVolumeAliquotUnits());
				    desiredConcentrationConverted = Double.parseDouble(singleAliquotInfo.getConcentrationTxt()) * calculateConversion(singleAliquotInfo.concentrationUnitsTxt);
				    dryRetiredList.add('0'); // issue 199
					}
		
				volumeList.add(StringUtils.isNullOrEmpty(singleAliquotInfo.getVolumeTxt()) ? "0" : singleAliquotInfo.getVolumeTxt());
				volumeUnitList.add(StringUtils.isNullOrEmpty(singleAliquotInfo.getVolumeAliquotUnits()) ? " " : singleAliquotInfo.getVolumeAliquotUnits());
				concentrationList.add(singleAliquotInfo.getConcentrationTxt());
				if (aliquotDryList.contains(singleAliquotInfo.aliquotId))
					vol = ((desiredWeighedAmountConverted )/( Double.parseDouble(singleAliquotInfo.getMolecularWeightTxt()) * desiredVolConverted  )   );
				else
					vol =  desiredAliquotVolConverted  * desiredConcentrationConverted/desiredVolConverted;	
				singleAliquotInfo.setConcentrationTxtFinal(vol.toString());
				concentrationFinalList.add(singleAliquotInfo.getConcentrationTxtFinal());
				if (aliquotIdList != null)
					aliquotIdList.add(singleAliquotInfo.getAliquotId());
				}
			return 0;
			}
		
		// issue 196
		public Double calculateConversion(String unitsToConvert)
			{
			Double conversion = 0.0;
			switch (unitsToConvert)
				{
	    	    case "uL" :
	    	    	conversion =  .000001;
	    	    	break;
	    	    case "mL" :
	    	    	conversion =  .001;
	    	    	break;
	    	    case "L" :
	    	    	conversion =  1.0;	
	    	    	break;
	    	    case "ug" :
	    	    	conversion =  .000001;
	    	    	break;
	    	    case "mg" :
	    	    	conversion =  .001;
	    	    	break;
	    	    case "g" :
	    	    	conversion =  1.0;	
	    	    	break;
	    	    case "uM" :
	    	    	conversion =  .000001;
	    	    	break;
	    	    case "mM" :
	    	    	conversion =  .001;
	    	    	break;
	    	    case "M" :
	    	    	conversion =  1.0;	
	    	    	break;
	    	    default:
	    	    	conversion = 1.0;
				}			
			return conversion;
			
			}
		
		public Double calculateSecondaryMixtureAliquotInfo (AjaxRequestTarget target, List<String> volumeMixUnits, String volumeFinalUnit  )
			{
			String errCode = "";
			Double conFinal ;
			Double desiredVolConverted;
			Double desiredMixVolConverted;
			Double desiredWeighedAmountConverted;
			Double desiredMixConConverted;
			Double sumOfVolumeMixture = 0.0;	
			Double conversion = 1.0;
			Double conversionMixtureAliquot = 1.0;
			int i = 0;
			
			
			aliquotDryList =  aliquotService.loadAliquotListDryKeepDryForEdit() ;
			desiredVolConverted = Double.parseDouble(mixtureDto.getDesiredFinalVolume() ) * calculateConversion (mixtureDto.getFinalVolumeUnits());
			errCode = getErrorCheckCode (mixtureDto.getMixtureName(), "mixture name", "string");
			if (!StringUtils.isNullOrEmpty(errCode))	
	        	{
				target.appendJavaScript(edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage(errCode));
				return null;
	        	}
			
			errCode = getErrorCheckCode (mixtureDto.getDesiredFinalVolume(), "mixture desired volume", "number");
			if (!StringUtils.isNullOrEmpty(errCode))	
	        	{
				target.appendJavaScript(edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage(errCode));
				return null;
	        	}
			 if (volumeFinalUnit .equals("L") )
			    	conversion = 1.0;
			    else if (volumeFinalUnit .equals("mL") )
			    	conversion = 1000.0;
			    else
			    	conversion = 1000000.0;
			for (MixtureInfo singleMixtureInfo : (ArrayList<MixtureInfo>) mixtureInfoModel.getObject()) 
				{
				// issue 123
				List <String> listOfAliquots = new ArrayList <String>();
				List <MixAliquotInfo> listOfMixAliquots = new ArrayList <MixAliquotInfo> ();
				errCode = getErrorCheckCode (singleMixtureInfo.getMixtureVolumeTxt(), "mixture volume", "number");
				if (!StringUtils.isNullOrEmpty(errCode))	
			        {
					target.appendJavaScript(edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage(errCode));
					return null;
					}
				errCode = getErrorCheckCode (singleMixtureInfo.getVolumeMixtureUnits(), "mixture volume units", "string");
				if (!StringUtils.isNullOrEmpty(errCode))	
			        {
					target.appendJavaScript(edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage(errCode));
					return null;
					}
				
				// issue 123
			    for (MixAliquotInfo singleMixAliquot: singleMixtureInfo.getMAliquotList())
			        {
			    	listOfAliquots.add(singleMixAliquot.getAliquotId());
			    	listOfMixAliquots.add(singleMixAliquot);
			    	if (!singleMixtureInfo.getMixtureId().equals(singleMixAliquot.getMixtureId()))
					    continue;
			    	// issue 196
		    	    if ((aliquotDryList.contains(singleMixAliquot.getAliquotId())))
		    	    	{
		    	    	desiredWeighedAmountConverted = Double.parseDouble(singleMixAliquot.getWeightedAmountMix()) * calculateConversion (singleMixAliquot.getWeightedAmountMixUnit());
		    	    	conFinal = ((desiredWeighedAmountConverted)/( Double.parseDouble(singleMixAliquot.getMolecularWeightMix()) * desiredVolConverted   ));
		    	    	}
		    	    else	
		    	    	{
		    	    	desiredMixVolConverted = Double.parseDouble(singleMixtureInfo.getMixtureVolumeTxt() ) * calculateConversion (singleMixtureInfo.volumeMixtureUnits);
		    	    	desiredMixConConverted = Double.parseDouble(singleMixAliquot.getMixAliquotConcentration()) * calculateConversion (singleMixAliquot.getMixAliquotConUnits());   	
		    	   	    conFinal = (desiredMixVolConverted  * desiredMixConConverted)/desiredVolConverted;
		    	    	}
			    	   	singleMixAliquot.setMixAliquotConcentrationFinal(conFinal.toString());		
			        }
			    mixtureAliquotMap.put(singleMixtureInfo.getMixtureId(), listOfAliquots);
				mixtureAliquotInfoMap.put(singleMixtureInfo.getMixtureId(), listOfMixAliquots);
			    errCode = getErrorCheckCode (singleMixtureInfo.getMixtureVolumeTxt(), "mixture volume", "number");				
			    if (!StringUtils.isNullOrEmpty(errCode))	
					{
					target.appendJavaScript(edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage(errCode));
					return null;
					}					    
			    singleMixtureInfo.setMixtureConcentrationTxt("0");
				if (sumOfVolumeMixture == null)
					sumOfVolumeMixture = 0.0;
				
			
				if (!StringUtils.isNullOrEmpty(errCode))	
					{
					target.appendJavaScript(edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage(errCode));
					return null;
					}
				volumeMixtureList.add(singleMixtureInfo.getMixtureVolumeTxt());
				volumeMixtureUnitList.add(singleMixtureInfo.getVolumeMixtureUnits()); // issue 196
				concentrationMixtureList.add(singleMixtureInfo.getMixtureConcentrationTxt());
				// issue 196
				///////////////////////

				if (singleMixtureInfo.getVolumeMixtureUnits().equals("L"))
					conversionMixtureAliquot = 1.0;
					else if (singleMixtureInfo.getVolumeMixtureUnits().equals("mL"))
					    conversionMixtureAliquot = 1000.0;
					else
						conversionMixtureAliquot = 1000000.0; 
				sumOfVolumeMixture += Double.parseDouble(singleMixtureInfo.getMixtureVolumeTxt()) *(conversion/conversionMixtureAliquot);
				i++;
						
				}	
			return sumOfVolumeMixture;
			}
				
		public String getTheblankinput ()
			{
			return theblankinput;
			}
		public void setTheblankinput (String theblankinput)
			{
			this.theblankinput = theblankinput;
			}
		
		} //////////////////////////////////////////end of form
	
	public String getAssayId()
	   {
	   return this.assayId;
	   }
	
	public void setAssayId(String assayId)
	   {
	   this.assayId = assayId;
	   }
	
	public String getAliquotId()
	   {
	   return this.aliquotId;
	   }
	
	public void setAliquotId(String aliquotId)
	   {
	   this.aliquotId = aliquotId;
	   }
	
	// issue 123
	public List<String> getAssayList()
		{
		List<String> iList = assayService.loadByAssayWithAliquots();
		return iList;
		}
	
	// issue 123 
	public List<MixtureInfo> getMixtureInfoList()
		{
		if (mixtureDto.getMixtureList() == null)
			return new ArrayList <MixtureInfo> ();
		if (doClear)
			return new ArrayList <MixtureInfo> ();
		if (mixtureInfoList.size() == 0  && mixtureDto.getMixtureList() != null)
			{
			for (String mixtureIdStr : mixtureDto.getMixtureList() ) 
				{	
				MixtureInfo minfo = new MixtureInfo();
				minfo.mixtureId = mixtureIdStr;
				minfo.mixtureVolumeTxt = "";
				minfo.mixtureConcentrationTxt = "";
				mixtureInfoList.add(minfo);
				mixtureAliquotMap.put(mixtureIdStr, null);
				}
			}
		else 
			{
			updateMixInfoListWithDeletes();
			for (String mixtureIdStr : mixtureDto.getMixtureList() ) 
				{
				if (!mixtureAliquotMap.containsKey(mixtureIdStr) && mixtureAliquotMap.size() > 0)
					{
					MixtureInfo minfo = new MixtureInfo();
					minfo.mixtureId = mixtureIdStr;
					mixtureInfoList.add(minfo);
					mixtureAliquotMap.put(mixtureIdStr, null);
					}
				}
			// issue 196
			if (mixtureInfoList.size() == 0  && mixtureDto.getMixtureList() != null && mixtureAliquotMap.size() == 0)
				{
				for (String mixtureIdStr : mixtureDto.getMixtureList() ) 
					{	
					MixtureInfo minfo = new MixtureInfo();
					minfo.mixtureId = mixtureIdStr;
					minfo.mixtureVolumeTxt = "";
					minfo.mixtureConcentrationTxt = "";
					mixtureInfoList.add(minfo);
					mixtureAliquotMap.put(mixtureIdStr, null);
					}
				}
			// issue 196
			}
		return mixtureInfoList;
		}
	
	
	
	public String getVolumeTxt()
	   {
	   return this.volumeTxt;
	   }
	
	public void setVolumeTxt(String volumeTxt)
	   {
	   this.volumeTxt = volumeTxt;
	   }
	
	// issue 196
	public String  getVolumeAliquotUnits()
		{
		return this.volumeAliquotUnits; // issue 196
		}
	public void  setVolumeAliquotUnits(String volumeAliquotUnits)
		{
		this.volumeAliquotUnits = volumeAliquotUnits; // issue 196
		}
	
	public String getConcentrationTxt()
	   {
	   return this.concentrationTxt;
	   }
	
	public void setConcentrationTxt(String concentrationTxt)
	   {
	   this.concentrationTxt = concentrationTxt;
	   }
	
	public double getSumVolumeList (List<String> volumeList, List<String> volumeUnitList, String desiredFinalVolumeUnit )
		{
		double sumVolume = 0;
	    Double conversion ;
	    Double conversionVolumeAliquot;
	    
	    if (desiredFinalVolumeUnit.equals("L") )
	    	conversion = 1.0;
	    else if (desiredFinalVolumeUnit.equals("mL") )
	    	conversion = 1000.0;
	    else
	    	conversion = 1000000.0;
	    int i=0;
		for (String volStr : volumeList)
			{
			if (volumeUnitList.get(i).equals("L"))
				conversionVolumeAliquot = 1.0;
			else if (volumeUnitList.get(i).equals("mL"))
				conversionVolumeAliquot = 1000.0;
			else
				conversionVolumeAliquot = 1000000.0;  
			sumVolume = sumVolume + Double.parseDouble(volStr == null ? "0" : volStr) * (conversion/conversionVolumeAliquot);
			i++;
			}
		
	    return sumVolume;
		}
	
	private Button buildClearButton (String id)
		{
		 final Button btn  =  new Button (id)
		    {
		    @Override
		    public void onSubmit()
			    {
		    	// issue 123
		    	clearSelections();
			    }				 		    
		    };			    
	    String confirmMsg = "Are you sure that you would like to clear all of your selections?";
	    btn.add(new AttributeModifier("onclick", "return confirm('" + confirmMsg + "');" ));	
	    return btn;	
		}
	 
	private  AjaxLink<Void> buildDeleteMixtureButton(String id, final String mixId)
	    {
	    final AjaxLink lnk  = new AjaxLink <Void> (id)
		    {
		    @Override
		    public void onClick(AjaxRequestTarget target)
			    {
		    	// issue 123
		    	gMixId = mixId;
		    	target.appendJavaScript("if (confirm('Are you sure that you would like to remove this Secondary Mixture from the mixture?  Clicking the build mixture button on the left will bring this Secondary Mixture back." 
                + "?')) { " +  confirmBehaviorMixture.getCallbackScript() + " }"  );	
			    }				 		    
		    };			    		    
	    return lnk;	
	    }
	
	private void deleteAliquotMap (String aliquotId)
		{	
		allAliquots = new ArrayList <String> ();
		Iterator it = aliquotMap.entrySet().iterator(); 		
		try 
		    {
			while (it.hasNext()) 
				{ 
			    Map.Entry pairs = (Map.Entry)it.next(); 
			    taliquots = (List <String>) pairs.getValue();
			    if (taliquots.contains(aliquotId))
			    	{
			    	taliquots.remove(aliquotId);
			    	aliquotMap.put(pairs.getKey().toString(), taliquots);
			    	}	
			    allAliquots.addAll(taliquots);			    
			    }			
		    if (inEditMixture )
		    	{
		    	aliquotMap = new HashMap<String, List<String>>();
		    	aliquotMap.put("NoAssay", allAliquots);
		    	}
			}
		catch (Exception e)
			{
			e.printStackTrace();	
			}
		}

	private void deleteMixInfoMixture (String mId)
		{
		int i = 0;
		for (MixtureInfo singleMixtureInfo : mixtureInfoList)
			{
			if (singleMixtureInfo.getMixtureId().equals(mId))
				{
				mixtureInfoList.remove(i);
				return;
				};
			i++;			
			}
		}
	
	/// issue 123
	private void deleteAndRebuildAliquotInfoMixture (String aId)
		{
		int i = 0;
		for (AliquotInfo singleAliquotInfo : aliquotInfoList)
			{
			if (!singleAliquotInfo.getAliquotId().equals(aId))
				tAliquotInfoList.add(singleAliquotInfo);
			}	
		aliquotInfoList.clear();
		aliquotInfoList.addAll(tAliquotInfoList);
		}
	
	// issue 196
	public Map <String, String> buildNameMapForAliquot ()
		{
		List <String> allAliquotIds = aliquotService.allAliquotIds();
		Map <String, String> lnameMapForAliquot = new HashMap <String, String> ();
		for (String aliquotStr : allAliquotIds)
			{
			String cid = aliquotService.getCompoundIdFromAliquot(aliquotStr);
			String aname = compoundNameService.getCompoundName(cid);
			lnameMapForAliquot.put(aliquotStr,aname  );
			}	
		return lnameMapForAliquot;
		}
	
	 public Map <String, String> buildToolTipForAliquotsMap ()
		{
		List <Object []> aliquotObjects = aliquotService.tooltipsListForMap();
		for (Object[] result : aliquotObjects)
	    	{
			String toolTipStr = "Aliquot Id:" + result[0].toString() + 
				"\n" + "Aliquot Name:" + result[1].toString() + "\n" + 
				"Date Created:" + result[2].toString() + "\n" + 
				"Created By: " + result[3].toString() + "\n" + 
				"Concentration: " + result[5]  + " " + result[4] +  "\n" ;
			toolTipsMap.put(result[0].toString(), toolTipStr);
	    	}
		return toolTipsMap;
		}
	
    
    // issue 138
    
    public Map <String, List<String>> buildAliquotMapAndAliquotInfoForEdit (Mixture mixture)
    	{
    	aliquotMapOfEditAliquots = new HashMap <String, List<String>> ();
    	List <String> aliquotListForMap = new ArrayList <String> ();
    	List <String> aliquotListForMapDry = new ArrayList <String> ();
    	List <Object []> aliquotObjects = mixtureService.aliquotsForMixtureId(mixture.getMixtureId());
    	for (Object[] result : aliquotObjects)
    	    {
			AliquotInfo aliquotInfo = new AliquotInfo ("","");
			aliquotInfo.setAliquotId((String) result[2]);
			aliquotInfo.setConcentrationTxt(result[3].toString()); 
			aliquotInfo.setConcentrationUnitsTxt(result[4] == null ? " " : result[4].toString());
			aliquotInfo.setConcentrationTxtFinal(result[5].toString());
			aliquotInfo.setVolumeTxt(result[6].toString());	
			aliquotInfo.setWeightedAmountTxt(result[7].toString());	
			aliquotInfo.setMolecularWeightTxt(result[8].toString());
			aliquotInfo.setVolumeAliquotUnits(result[9] == null ? " " :result[9].toString()); // issue 196
			aliquotInfo.setWeightedAmountUnitsTxt(result[10] == null ? " " :result[10].toString()); // issue 196
		    if (aliquotDryList.contains(aliquotInfo.aliquotId))
		        aliquotListForMapDry.add( (String) result[2]);
		    else
		    	aliquotListForMap.add((String) result[2]);
			aliquotInfoList.add(aliquotInfo);
    	    }
    	//aliquotListForMap.addAll(aliquotListForMapDry);
    	aliquotMapOfEditAliquots.put("NoAssay", aliquotListForMap);
    	aliquotMapOfEditAliquots.put("NoAssayDry", aliquotListForMapDry);
		mixtureDto.setAliquotNoAssayMultipleChoiceList(aliquotListForMap);
		mixtureDto.setAliquotNoAssayMultipleChoiceListDry(aliquotListForMapDry);
		return aliquotMapOfEditAliquots;
    	}
    
 // issue 138   
    public void buildSecondaryMixtureAliquotForEdit (Mixture mixture)
    	{    	
    	List <Object []> secondaryMixObjects = mixtureService.secondaryMixturesForMixture(mixture.getMixtureId());
    	List <Object []> aliquotSecondaryMixObjects = new ArrayList <Object []> ();
        //mixtureService.aliquotsForSecondaryMixtures(mixture.getMixtureId());
    	mixtureAliquotMap = new HashMap<String, List<String>>();
    	for (Object[] result : secondaryMixObjects)
    		{
    		MixtureInfo mixInfo = new MixtureInfo();
    		mixInfo.setMixtureId (result[0].toString());
    		mixInfo.setMixtureVolumeTxt(result[1].toString());
    		mixInfo.setVolumeMixtureUnits(result[2].toString());
    		if (mixtureDto.getMixtureList() == null)
    			mixtureDto.setMixtureList (new ArrayList <String> ());    				
    		mixtureDto.getMixtureList().add(mixInfo.getMixtureId());
    		if (mixtureInfoList == null)
    			mixtureInfoList = new ArrayList <MixtureInfo> ();
    		mixtureInfoList.add(mixInfo);
    		aliquotSecondaryMixObjects = mixtureService.aliquotsForSecondaryMixtures(mixInfo.getMixtureId(), mixture.getMixtureId());
    		for (Object[] resultAliquot : aliquotSecondaryMixObjects)
    			{
    			MixAliquotInfo mixAliquotInfo = new MixAliquotInfo ();
    			mixAliquotInfo.setAliquotId((String) resultAliquot[2]);
    			mixAliquotInfo.setMixtureId(mixInfo.getMixtureId());
    			mixAliquotInfo.setMixAliquotConcentration(resultAliquot[4].toString());
    			mixAliquotInfo.setMixAliquotConcentrationFinal(resultAliquot[3].toString());
    			mixAliquotInfo.setMixAliquotConUnits((String) resultAliquot[5]);
    			
    			// issue 196
    			if (aliquotDryList.contains(mixAliquotInfo.getAliquotId()))
    				{
    				mixAliquotInfo.setWeightedAmountMix(resultAliquot[8].toString());
    				mixAliquotInfo.setMolecularWeightMix(resultAliquot[7].toString());
    				mixAliquotInfo.setWeightedAmountMixUnit(resultAliquot[9].toString());  				
    				}
    			mixInfo.getListMixAliquotInfo().add(mixAliquotInfo);
    			}
    		}    	 
    	}
        
 // issue 123
    
    public Map <String, String> buildToolTipForMixturesMap (Mixture eMixture)
		{
		Map <String, String> toolTipsMap = new HashMap <String, String> ();
		List <Object []> mixtureObjects = mixtureService.tooltipsListForMixtureMap();
		for (Object[] result : mixtureObjects)
	    	{	
			String toolTipStr = "Mixture Id:" + result[0].toString() + 
					"\n" + "Mixture Name:" + result[1].toString() + "\n" + 
					 "Date Created:" + result[2].toString() + "\n" + 
					"Created By: " + result[3].toString() + "\n" + 
			          "\n" ;
			toolTipsMap.put(result[0].toString(), toolTipStr);			
	    	}	 
		return toolTipsMap;
		}
    
    final AbstractDefaultAjaxBehavior confirmBehavior = new AbstractDefaultAjaxBehavior() 
        { 
        @Override 
		protected void respond(AjaxRequestTarget target) 
            {         	           	   
		    try 
		        { 	
		    	deleteAliquotPressed = true;
		    	buildMixtureButtonPressed = false;	
		    	if (aliquotMapBeforeDelete.size() == 0)
			    	{
		        	List <String> aliquotTestList = new ArrayList <String> ();		
		        	List <String> aliquotTestListDry = new ArrayList <String> ();	
		        	// issue 196
		        	if ( aliquotMap.get("NoAssay") != null)
		        		{
		        		for (String aStr : aliquotMap.get("NoAssay"))
		        		    aliquotTestList.add(aStr);	
		        		}
		        	if ( aliquotMap.get("NoAssayDry") != null)
		        		{
		        		for (String aStr : aliquotMap.get("NoAssayDry"))
		        			aliquotTestListDry.add(aStr);	
		        		}
		        	aliquotMapBeforeDelete.put("NoAssay", aliquotTestList);
		        	aliquotMapBeforeDelete.put("NoAssayDry", aliquotTestListDry);	
			    	}  		  
		    	deleteAndRebuildAliquotInfoMixture(gAliquotId);   
		    	deleteAliquotMap(gAliquotId);    			    	
		    	target.add(mixtureBuildContainer);
		        } 
		    catch (Exception e) 
		        { 	
		    	e.printStackTrace();
		        } 
		     } 
		 };
				 
	final AbstractDefaultAjaxBehavior confirmBehaviorMixture = new AbstractDefaultAjaxBehavior() 
        { 
        @Override 
		protected void respond(AjaxRequestTarget target) 
            {           
		    try 
		        { 
		    	deleteMixInfoMixture(gMixId);
		    	mixtureDto.getMixtureList().remove(gMixId);
		    	mixtureAliquotMap.remove(gMixId);
		    	target.add(mixtureBuildContainer);
		        } 
		    catch (Exception e) 
		        { 				        
		        } 
		     } 
		 };
		 
	public void clearSelections ()
		{
		aliquotInfoList.clear();
    	mixtureInfoList.clear();
    	mixtureDto.setMixtureList(null);
    	mixtureAliquotMap.clear();
    	aliquotMap.clear();
    	aliquotMapBeforeDelete = new HashMap<String, List<String>>();
    	buildMixtureButtonPressed = false;
    	deleteAliquotPressed = false;
    	doClear = true;
		}
	
	public void copyChoicesBeforeDelete()
		{
		aliquotMap = new HashMap<String, List<String>> ();
		
	        for (Map.Entry<String, List<String>> entry : aliquotMapBeforeDelete.entrySet()) 
	            {
	        	List <String> aliquotTestList = new ArrayList <String> ();	
	        	for (String aStr : entry.getValue())
	        		aliquotTestList.add(aStr);		        			        		
	        	aliquotMap.put(entry.getKey(), aliquotTestList);
	    	    }
	    aliquotMapBeforeDelete = new HashMap<String, List<String>>();
	    buildMixtureButtonPressed = false;
	    deleteAliquotPressed = false;
		}
	
	public String shortVersionOfAssay (String assayId)
		{
		String shortVersionAssayStr= StringParser.parseName(assayId);
		shortVersionAssayStr= shortVersionAssayStr.length() < 32 ? shortVersionAssayStr : shortVersionAssayStr.substring(0,32);
		shortVersionAssayStr =shortVersionAssayStr + " " +  "(" +  StringParser.parseId(assayId) + ")";
		return shortVersionAssayStr;
		}
	
	public void updateMixInfoListWithDeletes ()
		{
		List <String> mixToDelete = new ArrayList <String> ();
		for (MixtureInfo lMinfo : mixtureInfoList)
			{
			if (! mixtureDto.getMixtureList().contains(lMinfo.getMixtureId()))
				mixToDelete.add(lMinfo.getMixtureId());
			}
		if (mixToDelete.size() > 0)
			{
			for (String lMix : mixToDelete)
				{
				deleteMixInfoMixture(lMix);
				mixtureAliquotMap.remove(lMix);
				}
			}
		}
	
	public void resetMixtureList()
		{
		List <String> tMixtureList= new ArrayList <String> ();
		for (MixtureInfo lMixtureInfo : mixtureInfoList)
			tMixtureList.add(lMixtureInfo.getMixtureId());
		mixtureDto.setMixtureList(tMixtureList);
		}
	
	// issue 196
	public String buildFinalConcentrationToolTip(String field, String concentrationFinal)
		{
		if ( field.trim().length() <=0   )
			return  " ";
	else	
	    return concentrationFinal + " M" + "\n" + 
	    (Double.parseDouble(concentrationFinal) * 1000) + " mM" +  "\n" + 
		(Double.parseDouble(concentrationFinal) * 1000000) + " uM" +  "\n";
		
		}
	
	}


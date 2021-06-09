package edu.umich.brcf.metabolomics.panels.lims.mixtures;
/***********************
 * Created By:  Julie Keros 
 * Date:  Feb 15 2021
 * Add mixtures through metlims issue 123
 ********************/

import java.math.BigDecimal;
import java.util.ArrayList;
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
	Map<String, List<String>> aliquotMap = new HashMap<String, List<String>>();	
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
	List <String> volumeList;
	List <String> concentrationList = new ArrayList<String> ();
	List <String> volumeMixtureList = new ArrayList<String> ();
	List <String> concentrationFinalList =new ArrayList<String> ();
	List <String> concentrationMixtureList =new ArrayList<String> ();
	ListView listViewAssay; // issue 94
	ListView<AliquotInfo> listViewAliquots; // issue 94
	ListView<MixtureInfo> listViewMixtures; // issue 123
	ListView<MixAliquotInfo> listViewAliquotsOfMixtures; // issue 123
	DropDownChoice<String> MixturesAddDD;
	//MixturesAdd MixturesAdd = this;
	Label mixtureNameLabel;
	Label aliquotNameLabel;	
	Label solventLabel;
	Label finalVolumeLabel;
	Label aNameLabel;
	Label mixLabel;
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
	// itemList	
	MixtureDTO mixtureDto = new MixtureDTO();
	MixturesAdd MixturesAdd = this;
	boolean isBuildVisible = false;
	AjaxButton calculateButton;
	ListMultipleChoice aliquotsChosen;
	String mId;
    ListMultipleChoice mixturesSelected;
    ListMultipleChoice aliquotNoAssaysSelected;
	Label mixtureDDLabel;
	Label aliquotNoAssayLabel;
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
	Boolean buildMixtureButtonPressed  = false;
	Boolean deleteAliquotPressed = false;
	boolean inEditMixture = false;
	String mixtureButtonLabel = "Create Mixture";
	// issue 123
	private Map<String, String> mixtureNamesAlreadyInDatabase;
	Map <String, List<String>> aliquotMapOfEditAliquots = new HashMap <String, List<String>> ();
	List <String> aliquotForEditList = new ArrayList <String> ();
	List <String> mixtureForEditList = new ArrayList <String> ();
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
		toolTipsMap = buildToolTipForWetAliquotsMap ();
		toolTipsMapMixture = buildToolTipForMixturesMap (mixture);
		mixtureNamesAlreadyInDatabase = mixtureService.allMixtureIdsNamesMap(mixture);
		inEditMixture = editingMixture;
		if (editingMixture )
		    {
			aliquotMap = buildAliquotMapAndAliquotInfoForEdit(mixture);
			isBuildVisible = true;
			mixtureButtonLabel = "Save Mixture";
			mixtureDto.setDesiredFinalVolume(mixture.getDesiredFinalVol().toString());
		    mixtureDto.setMixtureName(mixture.getMixtureName());
		    mixtureDto.setVolumeSolventToAdd(mixture.getVolSolvent().toString());
		    for (String lilA : mixtureDto.getAliquotNoAssayMultipleChoiceList())
		    	aliquotForEditList.add(lilA);
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
					listItem.add(new Label("aliquotId", new Model(aliquotInfo.aliquotId)));
			    	String cid = aliquotService.getCompoundIdFromAliquot(aliquotInfo.aliquotId);
			    	String aName = compoundNameService.getCompoundName(cid);
			    	aNameLabel = new Label("aliquotName", new Model(aName.substring(0, (aName.length() >=20 ? 20 : aName.length() ) )));
			    	aNameLabel.setEscapeModelStrings(false);
			    	aNameLabel.add(AttributeModifier.append("title", aName));
			    	listItem.add(aNameLabel);
					listItem.add(new Label("volumeAliquotLabel", "Volume:"));
					listItem.add(new Label("concentrationAliquotLabel", "Concentration (Lims):"));
					listItem.add(new Label("concentrationAliquotFinalLabel", "Concentration (Final):"));
					listItem.add(buildDeleteAliquotButton("deleteAliquotButton", aliquotInfo.getAliquotId()).setOutputMarkupId(true));
					listItem.add(new Label("concentrationAliquotUnitsLabel", new Model("Concentration Units")));
					
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
					listItem.add(concentrationAliquotText);					
					listItem.add(concentrationAliquotFinalText);
					concentrationAliquotFinalText.setOutputMarkupId(true);
					listItem.add(volumeAliquotText);
					listItem.add(concentrationAliquotUnitsAliquotText);
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
					listItem.add(new Label("mixtureId", new Model(mixtureInfo.mixtureId)));
					listItem.add(new Label("volumeMixtureLabel", "Volume:"));
					volumeMixtureText = new TextField <String >("volumeMixture", new PropertyModel<String>(mixtureInfo, "mixtureVolumeTxt"))
						{
						};
					listItem.add(volumeMixtureText);
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
							//////////////////////////////////
							listItema.add(new Label("aliquotIdForMixture", new Model(aliquotIdStr)));							
							listItema.add(new Label("concentrationAliquotForMixtureLabel", new Model("Concentration (Lims):")));
							listItema.add(new Label("concentrationAliquotForMixtureFinalLabel", new Model("Concentration (Final)")));
							listItema.add(new Label("concentrationAliquotForMixtureUnitsLabel", new Model("Units")));
							String cid = aliquotService.getCompoundIdFromAliquot(aliquotIdStr);
					    	listItema.add(new Label("aliquotNameForMixture", new Model(compoundNameService.getCompoundName(cid))).setEscapeModelStrings(false));					             					    	
					    	concentrationUnitMixtureAliquotText = new TextField("concentrationUnitsAliquot", new PropertyModel<String>(mixAliquotInfo, "mixAliquotConUnits"))
								{
								@Override
								public boolean isEnabled()
									{ 
									return false;
									}
								};
							listItema.add (concentrationUnitMixtureAliquotText);
					    	concentrationMixtureAliquotFinalText = new TextField("concentrationAliquotForFinalMixture", new PropertyModel<String>(mixAliquotInfo, "mixAliquotConcentrationFinal"))
								{
								@Override
								public boolean isEnabled()
									{ 
									return false;
									}
								};
							listItema.add (concentrationMixtureAliquotFinalText);
							concentrationMixtureAliquotText = new TextField("concentrationAliquotForMixture", new PropertyModel<String>(mixAliquotInfo, "mixAliquotConcentration"))
								{
								@Override
								public boolean isEnabled()
									{ 
									return false;
									}
								};
							listItema.add (concentrationMixtureAliquotText);
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
					if (aliquotMap.size() == 0  && (mixtureDto.getMixtureList() != null &&  mixtureDto.getMixtureList().size() == 0))
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
					sumOfVolumeAliquot = getSumVolumeList(volumeList);
					sumOfVolumeMixture = calculateSecondaryMixtureAliquotInfo ( target );			
					calcSolventToAdd = Double.parseDouble(mixtureDto.getDesiredFinalVolume()) - sumOfVolumeAliquot - (sumOfVolumeMixture == null ? 0 : sumOfVolumeMixture);
					mixtureDto.setVolumeSolventToAdd(calcSolventToAdd.toString());		
					if (calcSolventToAdd < 0 )
						MixturesAdd.this.error("The Solvent To Add can not be less than 0.  Please use different volumes. " );
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
					concentrationList = new ArrayList <String> ();
					volumeMixtureList = new ArrayList <String> ();
					concentrationFinalList = new ArrayList <String> (); 
					concentrationMixtureList = new ArrayList <String> ();
					if (setAliquotConcentrationInfo(target,  aliquotIdList) == -1)
					    return;     
					sumOfVolumeMixture = 0.0;
					sumOfVolumeMixture = calculateSecondaryMixtureAliquotInfo ( target );
					if (sumOfVolumeMixture == null)
					    return;
					mixtureDto.setMixtureName(mixtureNameText.getDefaultModelObjectAsString());														
					mixtureDto.setAliquotList(aliquotIdList);
					mixtureDto.setAliquotVolumeList(volumeList);
					mixtureDto.setAliquotConcentrationList(concentrationFinalList);
					mixtureDto.setMixtureConcentrationList(concentrationMixtureList);
					mixtureDto.setMixtureVolumeList(volumeMixtureList);
					mixtureDto.setDesiredFinalVolume(finalVolumeText.getDefaultModelObjectAsString());
					// issue 123 recalculate volume solvent to add.....
					Double solvToAddDec ;
					solvToAddDec = new BigDecimal(mixtureDto.getDesiredFinalVolume()).doubleValue();
					sumOfVolumeAliquot = getSumVolumeList(volumeList);
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
			
			if (StringUtils.isNullOrEmpty(valueToCheck))				
			    return "Error.  The " +  fieldName +  " can not be blank.";
			if (fieldName.equals("mixture name"))
				{
			    if ( mixtureService.isMixtureNameInDatabase(valueToCheck, mixtureNamesAlreadyInDatabase))
					{
					return "Error.  The mixture name:" + valueToCheck + " already exists.";
					}	
				}
			if (fieldType.equals("number"))
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
						aliquotInfoList.add(ainfo);
						}
					} 			
			     }
			else 
				updateAliquotList();
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
			Aliquot ali = aliquotService.loadById(aliquotId);
			newAliquotInfo.concentrationTxt = ali.getNeat().equals('1') ? ali.getDconc().toString() : ali.getDcon().toString();
			newAliquotInfo.concentrationUnitsTxt = ali.getNeat().equals('1') ? ali.getDConcentrationUnits() :ali.getNeatSolVolUnits(); // issue 123
			return newAliquotInfo;
			}
		
		public int setAliquotConcentrationInfo(AjaxRequestTarget target, List <String> aliquotIdList )
			{
			String errCode = "";
			for (AliquotInfo singleAliquotInfo : getAliquotInfoList()) 
				{
				errCode = getErrorCheckCode (singleAliquotInfo.getVolumeTxt(), "aliquot volume", "number");
				if (StringUtils.isNullOrEmpty(errCode))						        
				    errCode = getErrorCheckCode (singleAliquotInfo.getConcentrationTxt(), "aliquot concentration", "number");						
				if (!StringUtils.isNullOrEmpty(errCode))	
					{
					target.appendJavaScript(edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage(errCode));
					return -1;
					}
				volumeList.add(singleAliquotInfo.getVolumeTxt());
				concentrationList.add(singleAliquotInfo.getConcentrationTxt());
				Double vol =  (Double.parseDouble(singleAliquotInfo.getVolumeTxt())  * Double.parseDouble(singleAliquotInfo.getConcentrationTxt()))/Double.parseDouble(mixtureDto.getDesiredFinalVolume());
				singleAliquotInfo.setConcentrationTxtFinal(vol.toString());
				concentrationFinalList.add(singleAliquotInfo.getConcentrationTxtFinal());
				if (aliquotIdList != null)
					aliquotIdList.add(singleAliquotInfo.getAliquotId());
				}
			return 0;
			}
		
		public Double calculateSecondaryMixtureAliquotInfo (AjaxRequestTarget target)
			{
			String errCode = "";
			Double sumOfVolumeMixture = 0.0;			
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
				// issue 123
			    for (MixAliquotInfo singleMixAliquot: singleMixtureInfo.getMAliquotList())
			        {
			    	listOfAliquots.add(singleMixAliquot.getAliquotId());
			    	listOfMixAliquots.add(singleMixAliquot);
			    	if (!singleMixtureInfo.getMixtureId().equals(singleMixAliquot.getMixtureId()))
					    continue;
					    Double conFinal =  (Double.parseDouble(singleMixtureInfo.getMixtureVolumeTxt())  * Double.parseDouble(singleMixAliquot.getMixAliquotConcentration()))/Double.parseDouble(mixtureDto.getDesiredFinalVolume());
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
				
				sumOfVolumeMixture += Double.parseDouble(singleMixtureInfo.getMixtureVolumeTxt());					
				if (!StringUtils.isNullOrEmpty(errCode))	
					{
					target.appendJavaScript(edu.umich.brcf.shared.util.io.StringUtils.makeAlertMessage(errCode));
					return null;
					}
				volumeMixtureList.add(singleMixtureInfo.getMixtureVolumeTxt());
				concentrationMixtureList.add(singleMixtureInfo.getMixtureConcentrationTxt());
				}	
			return sumOfVolumeMixture;
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
	
	public String getConcentrationTxt()
	   {
	   return this.concentrationTxt;
	   }
	
	public void setConcentrationTxt(String concentrationTxt)
	   {
	   this.concentrationTxt = concentrationTxt;
	   }
	
	public double getSumVolumeList (List<String> volumeList)
		{
		double sumVolume = 0;
		for (String volStr : volumeList)
			{
			sumVolume = sumVolume + Double.parseDouble(volStr);
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
	
	// issue 123
    public Map <String, String> buildToolTipForWetAliquotsMap ()
		{
    	Map <String, String> toolTipsMap = new HashMap <String, String> ();
    	List <String> allDryAliquotsList = aliquotService.loadAliquotListNoAssay();
    	for (String aliquotStr : allDryAliquotsList)
	    	{	
    		Aliquot aliquot = aliquotService.loadById(aliquotStr);
			String cId = aliquot.getCompound().getCid();
			String aName = compoundNameService.getCompoundName(cId);
			String dateStr = aliquot.getCreateDateString();
			String createdBy = userService.getFullNameByUserId(aliquot.getCreatedBy());
			//String unitStr = aliquot.getNeatSolVolUnits();
			String unitStr = aliquot.getNeat().equals('1') ? aliquot.getDConcentrationUnits() :aliquot.getNeatSolVolUnits(); // issue 123
			//String desiredConStr = aliquot.getDcon().toString();
			String desiredConStr = aliquot.getNeat().equals('1') ? aliquot.getDconc().toString() : aliquot.getDcon().toString();
			String toolTipStr = "Aliquot Id:" + aliquot.getAliquotId() + 
					"\n" + "Aliquot Name:" + aName + "\n" + 
					 "Date Created:" + dateStr + "\n" + 
					"Created By: " + createdBy + "\n" + 
					 "Concentration: " + desiredConStr  + " " + unitStr +  "\n" ;
			toolTipsMap.put(aliquotStr, toolTipStr);			
	    	}	 
		return toolTipsMap;
		}		
    
    // issue 138
    
    public Map <String, List<String>> buildAliquotMapAndAliquotInfoForEdit (Mixture mixture)
    	{
    	aliquotMapOfEditAliquots = new HashMap <String, List<String>> ();
    	List <String> aliquotListForMap = new ArrayList <String> ();
    	List <Object []> aliquotObjects = mixtureService.aliquotsForMixtureId(mixture.getMixtureId());
    	for (Object[] result : aliquotObjects)
    	    {
			aliquotListForMap.add((String) result[2]);
			AliquotInfo aliquotInfo = new AliquotInfo ("","");
			aliquotInfo.setAliquotId((String) result[2]);
			aliquotInfo.setConcentrationTxt(result[3].toString()); 
			aliquotInfo.setConcentrationUnitsTxt(result[4].toString());
			aliquotInfo.setConcentrationTxtFinal(result[5].toString());
			aliquotInfo.setVolumeTxt(result[6].toString());		   	    
    	    aliquotInfoList.add(aliquotInfo);
    	    }
    	aliquotMapOfEditAliquots.put("NoAssay", aliquotListForMap);
		mixtureDto.setAliquotNoAssayMultipleChoiceList(aliquotListForMap);
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
    			mixInfo.getListMixAliquotInfo().add(mixAliquotInfo);
    			}
    		}    	 
    	}
        
 // issue 123
    public Map <String, String> buildToolTipForMixturesMap (Mixture eMixture)
		{
    	Map <String, String> toolTipsMap = new HashMap <String, String> ();
    	List <String> nonComplexMixtureList = mixtureService.getNonComplexMixtureIds(eMixture);
    	for (String mixtureStr : nonComplexMixtureList)
	    	{	
    		Mixture mixture = mixtureService.loadById(mixtureStr);
			String nameStr = mixture.getMixtureName();
			String dateStr = mixture.getCreateDateString();			
			String createdBy = mixture.getCreatedBy().getFullName();
			String toolTipStr = "Mixture Id:" + mixtureStr + 
					"\n" + "Mixture Name:" + nameStr + "\n" + 
					 "Date Created:" + dateStr + "\n" + 
					"Created By: " + createdBy + "\n" + 
			          "\n" ;
			toolTipsMap.put(mixtureStr, toolTipStr);			
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
			        for (Map.Entry<String, List<String>> entry : aliquotMap.entrySet()) 
			        	{
			        	List <String> aliquotTestList = new ArrayList <String> ();			        	
			        	for (String aStr : entry.getValue())	 
			        		aliquotTestList.add(aStr);		        		
			        	 aliquotMapBeforeDelete.put(entry.getKey(), aliquotTestList);		        	 
			        	}	
			    	}  		     
		    	deleteAndRebuildAliquotInfoMixture(gAliquotId);   
		    	deleteAliquotMap(gAliquotId);    			    	
		    	target.add(mixtureBuildContainer);
		        } 
		    catch (Exception e) 
		        { 				        
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
	
	}


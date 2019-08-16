package edu.umich.brcf.metabolomics.panels.lims.prep;


import java.math.BigDecimal;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.domain.GCPlate;
import edu.umich.brcf.metabolomics.layers.domain.LCPlate;
import edu.umich.brcf.metabolomics.panels.lims.sample.SampleDetailPanel;
import edu.umich.brcf.shared.layers.domain.BiologicalSample;
import edu.umich.brcf.shared.layers.domain.PrepPlate;
import edu.umich.brcf.shared.layers.domain.PreppedSample;
import edu.umich.brcf.shared.layers.service.SamplePrepService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.structures.ValueLabelBean;





public class PreparationDetailPanel  extends Panel{

	String preparation=null;
	List<PreppedSample> sampleList;
	List<PrepPlate> plateList;
	ModalWindow modal1;
	@SpringBean
	SamplePrepService samplePrepService;
	
	@SpringBean
	SampleService sampleService;
	
	//@SpringBean
	//InjectionsService injectionsService;
	
	List<String> PDSamples = samplePrepService.getAllowedDuplicates();
	
	public void setSamplePrepService(SamplePrepService samplePrepService) {
		this.samplePrepService = samplePrepService;
	}
	
	public void setSampleService(SampleService sampleService) {
		this.sampleService = sampleService;
	}

	//public void setInjectionsService(InjectionsService injectionsService) {
	//	this.injectionsService = injectionsService;
//	}

	public PreparationDetailPanel(String id, String preparation) {
		super(id);
		setPreparation(preparation);
		add(getNewModal());

		
		add(new ListView("sampleList", new PropertyModel(this, "sampleList"))
			{
			public void populateItem(final ListItem listItem)
				{
				final PreppedSample sample= (PreppedSample)listItem.getModelObject();
				
				listItem.add(new Label("index", new Model(sample.getWell().getIndex())));
				
				AjaxLink sampleLink;
				listItem.add(sampleLink=buildPanelLinkToModal("sidLink", modal1, sample.getSample().getSampleID()));
				sampleLink.add(new Label("sid",new Model(sample.getSample().getSampleID())));
				sampleLink.setEnabled(isBlankSample(sample.getSample()));
				
				listItem.add(new Label("name", new Model(sample.getSample().getSampleName())));
				listItem.add(new Label("well", new Model(sample.getWell().getLocation())));
				listItem.add(new Label("volume", new Model(sample.getVolume()+" "+sample.getVolUnits())));

				String hsop = sample.getHomogenization()!=null ? sample.getHomogenization().getId():"";
	
				WebPage hsopPage = sample.getHomogenization()!=null ? new HomogenizationDetail(new Model(sample.getHomogenization())) : null;
				listItem.add(buildLinkToModal("homogenizationLink", modal1, hsopPage).add(new Label("homogenization", new Model(hsop))));
				
				//listItem.add(new Label("diluted", new Model(getVolumewithUnits(sample.getSampleDiluted()))));
				
				//String psop=(sample.getProtienDetermination()!=null)?sample.getProtienDetermination().getId():"";
				//WebPage psopPage=(sample.getProtienDetermination()!=null)?new ProteinDeterminationDetail(
				//		new Model(sample.getProtienDetermination())):null;
				//listItem.add(buildLinkToModal("protienDeterLink", modal1, psopPage).add(new Label("protienDeter", new Model(psop))));
				
				String sop=(sample.getGeneralPrepSOP()!=null)?sample.getGeneralPrepSOP().getPrepID():"";
				WebPage sopPage=(sample.getGeneralPrepSOP()!=null)?new GeneralPrepDetail(
						new Model(sample.getGeneralPrepSOP())):null;
				listItem.add(buildLinkToModal("generalPrepLink", modal1, sopPage).add(new Label("generalPrep", new Model(sop))));
				
			//	listItem.add(new Label("protienReading", new Model(sample.getProtienReading())));
				
			//	listItem.add(new Label("volTransferred", new Model(getVolumewithUnits(sample.getVolumeTransferred()))));
				
				listItem.add(OddEvenAttributeModifier.create(listItem));
				}

		});
		add(new ListView("plateList", new PropertyModel(this, "plateList"))
		{
			public void populateItem(final ListItem listItem)
			{
				final PrepPlate plate= (PrepPlate)listItem.getModelObject();
				listItem.add(new Label("plateID", new Model(plate.getPlateID())));
				listItem.add(new Label("instrument", new Model(plate.getInstrument().getName())));
				String sop;
				Model sopModel;
				WebPage modelPage=null;
				if(plate instanceof GCPlate){
					sop= (((GCPlate)plate).getDerivatizationMethod()!=null)?((GCPlate)plate).getDerivatizationMethod().getDerivatizationID():"";
					modelPage=(((GCPlate)plate).getDerivatizationMethod()!=null)?
							new GCPrepDetail(new Model(((GCPlate)plate).getDerivatizationMethod())):null;
				}
				else{
					sop= (((LCPlate)plate).getReconstitutionMethod()!=null)?((LCPlate)plate).getReconstitutionMethod().getReconstitutionID():"";
					modelPage=(((LCPlate)plate).getReconstitutionMethod()!=null)?
							new LCPrepDetail(new Model(((LCPlate)plate).getReconstitutionMethod())):null;
				}
				listItem.add(buildLinkToModal("platePrepLink", modal1, modelPage).add(new Label("sop", new Model(sop))));
//				listItem.add(new Button("run"){
//					@Override
//					public void onSubmit() {
//						injectionsService.runWorklist(((METWorksSession) getSession()).getCurrentUserName(),
//								samplePrepService.loadPreparationByID(getPreparation()), plate, true);
//					} Search By Sample
//				});//.setEnabled(false)
//				listItem.add(new Label("sop", new Model(sop)));
//				listItem.add(new Button("hom"));
//				listItem.add(new Button("protDet"));
//				listItem.add(new Button("crash"));
//				listItem.add(new Button("report"));
//				WebPage runPage=new RunPlatePage(plate, true);
				WebPage runPage=new SampleRunPage();
				listItem.add(buildLinkToModal("run", modal1, runPage));
				listItem.add(OddEvenAttributeModifier.create(listItem));
			}
		});
		add(new ListView("sampleObservationsList", new PropertyModel(this, "sampleObservationsList"))
		{
			public void populateItem(final ListItem listItem)
			{
				final ValueLabelBean lvb= (ValueLabelBean)listItem.getModelObject();
				listItem.add(new Label("sampleNotes", new Model(lvb.getValue())));
				listItem.add(new Label("samplesAssociated", new Model(lvb.getLabel())));
				listItem.add(OddEvenAttributeModifier.create(listItem));
			}
		});
		add(new ListView("plateObservationsList", new PropertyModel(this, "plateObservationsList"))
		{
			public void populateItem(final ListItem listItem)
			{
				final ValueLabelBean lvb= (ValueLabelBean)listItem.getModelObject();
				listItem.add(new Label("plateNotes", new Model(lvb.getValue())));
				listItem.add(new Label("platesAssociated", new Model(lvb.getLabel())));
				listItem.add(OddEvenAttributeModifier.create(listItem));
			}
		});
	}

	public void setPreparation(String preparation)
		{
		this.preparation = preparation;
		if((preparation!=null)&&(preparation.length()>0))
			{
			setSampleList(samplePrepService.loadPreppedSamples(preparation));
    		setPlateList(samplePrepService.loadPlatesByPreparation(preparation));
    		}
		}
	
	
	public void setPlateList(List<PrepPlate> plateList) {
		this.plateList=plateList;
	}

	public void setSampleList(List<PreppedSample> sampleList) {
		this.sampleList=sampleList;
	}
	
	public List<PrepPlate> getPlateList() {
		return plateList;
	}

	public List<PreppedSample> getSampleList() {
		return sampleList;
	}

	public String getPreparation(){
		return preparation;
	}
	
	public List<ValueLabelBean> getSampleObservationsList(){
		return samplePrepService.loadSampleObservationByPrepID(getPreparation());
	}
	
	public List<ValueLabelBean> getPlateObservationsList(){
		return samplePrepService.loadPlateObservationByPrepID(getPreparation());
	}
	
	private ModalWindow getNewModal(){
		modal1= new ModalWindow("modal1");
		modal1.setInitialWidth(620);
        modal1.setInitialHeight(300);
        return modal1;
	}
	
	private boolean isBlankSample(BiologicalSample sample) {
		if((sample instanceof BiologicalSample) && (sample.getSampleID()!=null)&&(!PDSamples.contains(sample.getSampleID())))
			return true;
		else
			return false;
	}

	private AjaxLink buildPanelLinkToModal(String linkID, final ModalWindow modal1, final String sid)
		{
		return (new AjaxLink(linkID)
			{
			@Override
			public void onClick(AjaxRequestTarget target)
				{
				modal1.setInitialWidth(700);
		        modal1.setInitialHeight(500);
				
		        modal1.setContent(new SampleDetailPanel(modal1.getContentId(), new CompoundPropertyModel(sampleService.loadById(sid))){
					@Override
					protected void onEdit(IModel sampleModel, AjaxRequestTarget target) 
						{
						}
				});
			
				target.add(modal1);
			modal1.show(target);
			}
			@Override // issue 464
			public MarkupContainer setDefaultModel(IModel model) 
			    {
				// TODO Auto-generated method stub
				return this;
			    }
		});
	}
	
	private AjaxLink buildLinkToModal(String linkID, final ModalWindow modal1, final WebPage page) {
		return new AjaxLink(linkID)
        {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
            	if ((page instanceof GCPrepDetail)||(page instanceof LCPrepDetail)){
            		modal1.setInitialWidth(620);
        			modal1.setInitialHeight(150);
            	}
            	else if (page instanceof SampleRunPage){
            		modal1.setInitialWidth(1200);
            		modal1.setInitialHeight(650);
            	}
            	else{
            		modal1.setInitialWidth(620);
        			modal1.setInitialHeight(300);
            	}
        		modal1.setPageCreator(new ModalWindow.PageCreator(){
                     public Page createPage(){
                         return (page);
                     }
                 });
            	 modal1.show(target);
            }
        	@Override // issue 464
			public MarkupContainer setDefaultModel(IModel model) 
			    {
				// TODO Auto-generated method stub
				return this;
			    }
        };
	}
	
	private String getVolumewithUnits(BigDecimal volume) {
		if (volume!=null)
			return volume+" µL";
		return null;
	}
}

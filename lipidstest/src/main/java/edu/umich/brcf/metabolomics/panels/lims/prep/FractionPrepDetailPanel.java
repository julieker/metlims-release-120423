package edu.umich.brcf.metabolomics.panels.lims.prep;

import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.PrepPlate;
import edu.umich.brcf.shared.layers.domain.PreppedFraction;
import edu.umich.brcf.metabolomics.layers.service.FractionationService;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;



public class FractionPrepDetailPanel extends Panel{

	String preparation=null;
	List<PreppedFraction> fractionsList;
	List<PrepPlate> plateList;
	ModalWindow modal1;
	@SpringBean
	FractionationService fractionationService;
	
	//@SpringBean
	//InjectionsService injectionsService;
	
	public void setFractionationService(FractionationService fractionationService) {
		this.fractionationService = fractionationService;
	}

	//public void setInjectionsService(InjectionsService injectionsService) {
	//	this.injectionsService = injectionsService;
	//}

	public FractionPrepDetailPanel(String id, String preparation) {
		super(id);
		setPreparation(preparation);
		add(getNewModal());
		setOutputMarkupId(true);
		add(new ListView("fractionsList", new PropertyModel(this, "fractionsList"))
		{
			public void populateItem(final ListItem listItem)
			{
				final PreppedFraction fraction= (PreppedFraction)listItem.getModelObject();
				listItem.add(new Label("index", new Model(fraction.getWell().getIndex())));
				listItem.add(new Label("name", new Model(fraction.getFraction().getSampleName())));
				listItem.add(new Label("id", new Model(fraction.getFraction().getSampleID())));
				listItem.add(new Label("well", new Model(fraction.getWell().getLocation())));
				listItem.add(new Label("volume", new Model(fraction.getVolume()+" "+fraction.getVolUnits())));
//				listItem.add(new Label("vialTare", new Model(fraction.getFraction().getVialTare())));
//				listItem.add(new Label("mass", new Model(fraction.getFraction().getMass())));
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
//				listItem.add(new Button("run"){
//					@Override
//					public void onSubmit() {
////						injectionsService.runWorklist(((METWorksSession) getSession()).getCurrentUserName(),
////								fractionationService.loadById(getPreparation()), plate, false);
//					}
//				});//.setEnabled(false)
//				listItem.add(new Label("sop", new Model(sop)));
				WebPage runPage=new RunPlatePage(plate, false);
				listItem.add(buildLinkToModal("run", modal1, runPage));
				listItem.add(OddEvenAttributeModifier.create(listItem));
			}
		});
	}

	public void setPreparation(String preparation){
		this.preparation=preparation;
		if((preparation!=null)&&(preparation.length()>0)){
			setFractionsList(fractionationService.loadPreppedFractions(preparation));
    		setPlateList(fractionationService.loadPlatesByPreparation(preparation));}
	}
	
	public void setPlateList(List<PrepPlate> plateList) {
		this.plateList=plateList;
	}

	public void setFractionsList(List<PreppedFraction> fractionsList) {
		this.fractionsList=fractionsList;
	}
	
	public List<PrepPlate> getPlateList() {
		return plateList;
	}

	public List<PreppedFraction> getFractionsList() {
		return fractionsList;
	}

	public String getPreparation(){
		return preparation;
	}
	
	private ModalWindow getNewModal(){
		modal1= new ModalWindow("modal1");
		modal1.setInitialWidth(620);
        modal1.setInitialHeight(550);
        return modal1;
	}
	
	private AjaxLink buildLinkToModal(String linkID, final ModalWindow modal1, final WebPage page) {
		// issue 39
		return new AjaxLink <Void>(linkID)
        {
            @Override
            public void onClick(AjaxRequestTarget target)
                {
            	modal1.setPageCreator(new ModalWindow.PageCreator()
            	    {
                    public Page createPage()
                       {
                        return (page);
                       }
                    });
           	    modal1.show(target);
                }
        };
	}
}

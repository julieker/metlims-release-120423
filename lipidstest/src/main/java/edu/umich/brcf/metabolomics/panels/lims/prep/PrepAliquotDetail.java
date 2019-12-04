package edu.umich.brcf.metabolomics.panels.lims.prep;

import java.math.BigDecimal;

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
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.dto.PreppedSampleDTO;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.SamplePrepService;


public abstract class PrepAliquotDetail extends WebPage{

	PrepAliquotDetail pad;
	ModalWindow modal1;
//	AjaxLink homogenizationLink,eraseHomogenization;
	
	@SpringBean
	AliquotService aliquotService;
	
	@SpringBean
	SamplePrepService samplePrepService;
	
	public void setAliquotService(AliquotService aliquotService) {
		this.aliquotService = aliquotService;
	}

	public PrepAliquotDetail(Page backPage, PreppedSampleDTO prepSample) {
		add(new FeedbackPanel("feedback"));
		add(new PrepAliquotForm("prepAliquotForm", prepSample));
		pad=this;
		setOutputMarkupId(true);
	}

	public final class PrepAliquotForm extends Form {
		public PrepAliquotForm(final String id, final PreppedSampleDTO prepSample){
			super(id, new CompoundPropertyModel(prepSample));
//			add(getNewModal());
			add(new Label("id"));
			add(new Label("sampleid"));
			add(new Label("well"));
			add(new RequiredTextField("volume"));
			add(new DropDownChoice("volUnits", aliquotService.getAllVolUnits()));
//			WebPage hsopPage=(prepSample.getHomogenizationID().length()>0)?new HomogenizationDetail(
//					new Model(samplePrepService.loadHomogenizationByID(prepSample.getHomogenizationID()))):null;
//			add(homogenizationLink=buildLinkToModal("homogenizationLink", modal1, hsopPage));
//			homogenizationLink.add(new Label("homogenizationID", new PropertyModel(prepSample,"homogenizationID")));
//			homogenizationLink.setOutputMarkupId(true);
//			add(eraseHomogenization=new AjaxLink("erazeHom")
//	        {
//	            @Override
//	            public void onClick(AjaxRequestTarget target)
//	            {
//	            	samplePrepService.eraseHomogenization(prepSample);
//	            	prepSample.setHomogenizationID("");
//	            	setVisible(false);
//	            	target.add(homogenizationLink);
//	            	target.add(this);
//				}
//	        });
//			eraseHomogenization.setVisible(prepSample.getHomogenizationID()!=null && prepSample.getHomogenizationID().length()>0);
//			add(new RequiredTextField("volUnits"));
			add(new TextField("bufferType"));
			add(new TextField("bufferVolume").setType(BigDecimal.class));
			add(new TextField("sampleDiluted").setType(BigDecimal.class));
			add(new TextField("dilutant"));
			add(new TextField("dilutantVolume").setType(BigDecimal.class));
			add(new TextField("volumeTransferred").setType(BigDecimal.class));
			add(new SaveButton());
		}
	}
	
	private final class SaveButton extends Button {
		private static final long serialVersionUID = 1L;
		private SaveButton() {
			super("save");
		}
		@Override
		public void onSubmit() {
			PreppedSampleDTO prepSample = (PreppedSampleDTO) getForm().getModelObject();
			try{
				PrepAliquotDetail.this.onSave(prepSample);
				PrepAliquotDetail.this.info("Save Successful!");
			}catch (Exception e){
				PrepAliquotDetail.this.error("Save unsuccessful! Please re-check values entered!");
			}
		}
	}
	
	private ModalWindow getNewModal(){
		modal1= new ModalWindow("modal1");
		modal1.setInitialWidth(620);
        modal1.setInitialHeight(300);
        return modal1;
	}
	
	private AjaxLink buildLinkToModal(String linkID, final ModalWindow modal1, final WebPage page) {
		// issue 39
		return new AjaxLink <Void>(linkID)
        {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
            	if ((page instanceof GCPrepDetail)||(page instanceof LCPrepDetail))
        			modal1.setInitialHeight(150);
        		else
        			modal1.setInitialHeight(300);
        		modal1.setPageCreator(new ModalWindow.PageCreator(){
                     public Page createPage(){
                         return (page);
                     }
                 });
            	 modal1.show(target);
            }
        };
        
	}
	
	protected abstract void onSave(PreppedSampleDTO prepSample);
	
}

package edu.umich.brcf.metabolomics.panels.lims.prep;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.PrepPlate;
import edu.umich.brcf.shared.layers.service.SamplePrepService;




public class RunPlatePage extends WebPage{
	
	@SpringBean
	SamplePrepService samplePrepService;
	
	//@SpringBean
	//InjectionsService injectionsService;
	
	public static List<String> modeChoices = Arrays.asList(new String[] { "P", "N", "P & N", "Z" });
	
	public RunPlatePage(PrepPlate plate, boolean isSamplePrep){
		add(new FeedbackPanel("feedback"));
		add(new RunPlateForm("runPlateForm", plate, isSamplePrep));
	}
	
	public final class RunPlateForm extends Form {
		public RunPlateForm(final String id,final PrepPlate plate, final boolean isSamplePrep){
			super(id);//, new CompoundPropertyModel(gdto));
			/*
			add(new RunnableSampleSelectionPanel("selectionPanel", plate, isSamplePrep));
//			final RadioChoice modeChoice=new RadioChoice("runMode", modeChoices);
//			modeChoice.setSuffix(" ");
//			add(modeChoice);
//			modeChoice.setOutputMarkupId(true);
//			add(new DropDownChoice("mode",  new Model(injectionsService.getDefaultMode(plate)), 
//					injectionsService.getModeList(plate)).setRequired(true));
			add(new DropDownChoice("tray",  new Model(injectionsService.getDefaultTray(plate)), 
					injectionsService.getTrayList(plate)).setRequired(true));
			add(new CheckBox("randomize", new Model(true)));
			
			add(new Button("run"){
				@Override
				public void onSubmit() {
					String tray=getForm().get("tray").getDefaultModelObjectAsString();
					boolean randomFlag=getForm().get("randomize").getDefaultModelObjectAsString().equals("true")?true:false;
					List<RunnableSampleDTO> samples = ((RunnableSampleSelectionPanel)getForm().get("selectionPanel")).getItems();
					if(isSamplePrep){
//					injectionsService.runWorklist2(((METWorksSession) getSession()).getCurrentUserName(),
//							samples, plate, tray, randomFlag);
					}
					else{}
//						injectionsService.makeFractionInjections(((METWorksSession) getSession()).getCurrentUserName(),
//							samples, plate, tray, randomFlag);
				}
			});
			
			add(new MyPopupLink("createInjections", 600, 400) { //, new Model(new DocumentDTO()), null)
				public void onClick() {
					String tray=this.getParent().get("tray").getDefaultModelObjectAsString();
					boolean randomFlag=this.getParent().get("randomize").getDefaultModelObjectAsString().equals("true")?true:false;
					List<RunnableSampleDTO> samples = ((RunnableSampleSelectionPanel)(this.getParent().get("selectionPanel"))).getItems();
					if(isSamplePrep){
//						List<InjectionInfo> injList = injectionsService.getInjectionList(((METWorksSession) getSession()).getCurrentUserName(),
//							samples, plate, tray, randomFlag);
//						setResponsePage(new InjectionListPage(injList));
					}
					else
						{}
				}
			}); */
		}
	}
}

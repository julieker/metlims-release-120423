package edu.umich.brcf.metabolomics.panels.lims.prep;


import org.apache.wicket.markup.html.WebPage;




public class SampleRunPage extends WebPage
	{
	/*
	@SpringBean
	SystemConfigService systemConfigService;
	
	@SpringBean
	METWorksMessageMailer mailer;
	
	public static List<String> modeChoices = Arrays.asList(new String[] { "P", "N", "Z" });
	//@SpringBean
	//InjectionsService injectionsService;
	
	DropDownChoice methodsDD;
	DropDownChoice assaysDD;
	String runMode;
	InjectionListPanel injListPanel;
	List<InjectionInfoDTO> injList=new ArrayList<InjectionInfoDTO>();
	AjaxSubmitLink runLink;
	CheckBox randomCheck;
	Label assayLbl;
	
	public SampleRunPage(PrepPlate plate){
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		setRunMode(injectionsService.getDefaultRunMode(plate));
		add(new RunPlateForm("runPlateForm", plate));
	}
	
	public final class RunPlateForm extends Form {
		public RunPlateForm(final String id,final PrepPlate plate){
			super(id);
			setOutputMarkupId(true);
			add(new Label("plate", new Model(plate.getPlateID()+"_"+plate.getInstrument().getName())));
			add(new SelectableItemsPanel("selectionPanel", plate.getSamplePrep().getPrepID()));
			final RadioChoice modeChoice=new RadioChoice("runMode", new PropertyModel(SampleRunPage.this, "runMode"), modeChoices);
//			{
//				protected void onDisabled(ComponentTag tag){
//					sample.setRunMode(null);
//				}
//			};
			modeChoice.add(new AjaxFormChoiceComponentUpdatingBehavior() {
	            private static final long serialVersionUID = 1L;

	            @Override
	            protected void onUpdate(AjaxRequestTarget target) {
	            	methodsDD.setChoices(injectionsService.getSampleRunMethodsForInstrumentTypeAndMode(plate.getInstrument().getType(), getRunMode()));
	            	methodsDD.setModelObject(injectionsService.getDefaultSampleRunMethodForInstrumentTypeAndMode(plate.getInstrument().getType(), getRunMode()));
	            	target.add(methodsDD);
		            target.add(this.getComponent().getParent());
	            }
	        });
			modeChoice.setSuffix(" ");
			modeChoice.setEnabled(plate.getInstrument().getType().equals("LC"));
			add(modeChoice);
			add(methodsDD=new DropDownChoice("methodsDD",  new Model(injectionsService.getDefaultSampleRunMethodForInstrumentTypeAndMode(plate.getInstrument().getType(), getRunMode())), 
					injectionsService.getSampleRunMethodsForInstrumentTypeAndMode(plate.getInstrument().getType(), getRunMode())));
			methodsDD.setRequired(true);
			methodsDD.setOutputMarkupId(true);
			add(new DropDownChoice("tray",  new Model(injectionsService.getDefaultTray(plate)), 
					injectionsService.getTrayList(plate)).setRequired(true));
			add(assaysDD=new DropDownChoice("assaysDD",  new Model(injectionsService.getDefaultAssay()), 
					injectionsService.getAllAssays()));
			assaysDD.setRequired(true);
			assaysDD.setOutputMarkupId(true);
			add(assayLbl=new Label("assayLbl", "Assay: "));
			add(randomCheck=new CheckBox("randomize", new Model(false)));
			randomCheck.add(new Label("randomLbl", "Randomize"));
			add(injListPanel=new InjectionListPanel("injListPanel", getInjList()));
			injListPanel.setOutputMarkupId(true);
			add(new AjaxSubmitLink ("addToList", this){
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form form) {
					String rMode = form.get("runMode").getDefaultModelObjectAsString();
					String runMethod = form.get("methodsDD").getDefaultModelObjectAsString();
					String tray=form.get("tray").getDefaultModelObjectAsString();
//					boolean randomFlag=form.get("randomize").getDefaultModelObjectAsString().equals("true")?true:false;
					List<SelectableObject> samples = ((SelectableItemsPanel)form.get("selectionPanel")).getItems();
//					injectionsService.runWorklist(((METWorksSession) getSession()).getCurrentUserName(),
//							samples, plate, rMode, runMethod, tray, randomFlag);
//					List<InjectionInfo> injList = (List<InjectionInfo>)((ListView)(injListPanel.get("samples"))).getList();
//					injList.addAll(injectionsService.getInjectionList(plate, samples, rMode, runMethod, tray, randomFlag));
					injList.addAll(injectionsService.getInjectionList(samples, rMode, runMethod, tray));
					((ListView)(injListPanel.get("samples"))).setList(injList);
					updatePage();
					((SelectableItemsPanel)(this.getParent().get("selectionPanel"))).clearAllSelection();
					target.add(this.getParent());
				}
			});
			add(runLink=new AjaxSubmitLink ("run", this){
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form form) {
					int index=0;
					for(InjectionInfoDTO inj: injList)
						inj.setIndex(++index);
					String assay = form.get("assaysDD").getDefaultModelObjectAsString();
					boolean randomFlag=form.get("randomize").getDefaultModelObjectAsString().equals("true")?true:false;
					injectionsService.runWorklist( plate,injList, randomFlag, assay);
					SampleRunPage.this.info(getInjList().size()+" injections have been sent to the injection queue!");
					setInjList(new ArrayList<InjectionInfoDTO>());
					List<String> email_contacts = (List<String>) (systemConfigService.getSystemConfigMap()).get("sample_run_notification_contact");
		            for (String email_contact : email_contacts){
		            	mailer.sendMessage(new METWorksMailMessage("metabolomics@med.umich.edu", email_contact,
							"METLIMS Sample Run Message", "Sample Run started for plate - '"+plate.getPlateID()+"' of Sample Prep '"+plate.getSamplePrep().getTitle()+" ("+plate.getSamplePrep().getPrepID()+")'"));
		            }
					updatePage();
					target.add(this.getParent());
					target.add(this.getParent().getParent().get("feedback"));
				}
			});
			updatePage();
		}
	}
	
	public void updatePage(){
		injListPanel.setVisible(!getInjList().isEmpty());
		assaysDD.setVisible(!getInjList().isEmpty());
		assayLbl.setVisible(!getInjList().isEmpty());
		randomCheck.setVisible(!getInjList().isEmpty());
		runLink.setVisible(!getInjList().isEmpty());
	}

	public String getRunMode() {
		return runMode;
	}

	public void setRunMode(String runMode) {
		this.runMode = runMode;
	}

	public List<InjectionInfoDTO> getInjList() {
		return injList;
	}

	public void setInjList(List<InjectionInfoDTO> injList) {
		this.injList = injList;
	} */
}

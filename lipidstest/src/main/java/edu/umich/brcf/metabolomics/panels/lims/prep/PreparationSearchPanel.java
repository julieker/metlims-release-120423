package edu.umich.brcf.metabolomics.panels.lims.prep;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import edu.umich.brcf.shared.layers.domain.Preparation;
import edu.umich.brcf.shared.layers.service.SamplePrepService;



public class PreparationSearchPanel extends Panel 
	{
	@SpringBean
	SamplePrepService samplePrepService;
	
	TabbedPanel tabbedPanel; 
	PreparationSearchPanel pp;
	PrepDetailHeaderPanel pdh;

	String preparation=null;

	public void setSamplePrepService(SamplePrepService samplePrepService) 
		{
		this.samplePrepService = samplePrepService;
		}
	
	public PreparationSearchPanel(String id) 
		{
		super(id);
		pp=this;
		setOutputMarkupId(true);
        add(new FeedbackPanel("feedback"));
		add(new PreparationForm("preparationForm"));
		}
	
	
	public final class PreparationForm extends Form 
		{
		public PreparationForm(final String id) 
			{
			super(id);
			
			final List tabs=new ArrayList();
			
			tabs.add(new AbstractTab(new Model("Prep Details")) 
				{
		 		public Panel getPanel(String panelId)
		 		   	{
		 			return (new PreparationDetailPanel(panelId, getPreparation()));
		 		   	}
		 		 });
			 
			tabs.add(new AbstractTab(new Model("Edit Prep Methods")) 
				{
	 		   	public Panel getPanel(String panelId)
	 		   		{
	 		   		return (new PrepMethodsPanel(panelId, getPreparation()));
	 		   		}
				});
		   
		    add(tabbedPanel=new TabbedPanel("tabs", tabs));
	        tabbedPanel.setSelectedTab(0);
			tabbedPanel.setOutputMarkupId(true);

			
			add(pdh=new PrepDetailHeaderPanel("pdh", new CompoundPropertyModel(null)));
			pdh.setOutputMarkupId(true);
			final AutoCompleteTextField title = new AutoCompleteTextField("title", new Model("")){
				@Override
				protected Iterator getChoices(String input) {
					if (Strings.isEmpty(input)) {
						return Collections.EMPTY_LIST.iterator();
					}
					return getSamplePrepChoices(input);
				}
			};
			add(title);//new TextField("title", new Model(""))
			final ModalWindow modal1= new ModalWindow("modal1");
			modal1.setInitialWidth(900);
		    modal1.setInitialHeight(550);
		    modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
		    {
		        public void onClose(AjaxRequestTarget target)
		        {
		        	updatePanels(preparation);
		        	target.add(pp);
		        	target.add(pdh);
        			target.add(tabbedPanel);
		        	tabbedPanel.setSelectedTab(tabbedPanel.getSelectedTab());
		        }
		    });
		    add(modal1);
		    AjaxLink createPlate;
		    add(createPlate=buildLinkToModal("createPlate",title, modal1, pp));
		    title.add(new AjaxFormSubmitBehavior(this, "change") {
				protected void onSubmit(AjaxRequestTarget target) {
					target.add(modal1);
				}
				@Override
				protected void onError(AjaxRequestTarget target) {
				}
			});
		    updatePanels(null); 
		}

//		private PrepDetailHeaderPanel getHeaderPanel() {
//			if
//			new PrepDetailHeaderPanel("pdh", new CompoundPropertyModel(samplePrepService.loadPreparationByID(getPreparation())))
//			return null;
//		}
	}
	
	public void setPreparation(String preparation){
		this.preparation=preparation;
	}
	
	public String getPreparation(){
		return preparation;
	}
	
	public void updatePanels(String preparation){
		tabbedPanel.setVisible((preparation!=null));
		if(preparation!=null)pdh.setDefaultModel(new CompoundPropertyModel(samplePrepService.loadPreparationByID(getPreparation())));
		pdh.setVisible((preparation!=null));
	}
	private Iterator getSamplePrepChoices(String input){
		List<String> choices = new ArrayList();
		for (Preparation prep : samplePrepService.allSamplePreparations()) {
			final String prepTitle = prep.getTitle();
			if (prepTitle.toUpperCase().contains(input.toUpperCase()))
				choices.add(prepTitle + " (" + prep.getPrepID()+")");
		}
		return choices.iterator();
	}
	
	private AjaxLink buildLinkToModal(String linkID, final TextField title, final ModalWindow modal1, final PreparationSearchPanel pp) 
		{
		AjaxLink link=new AjaxLink(linkID)
        	{
            @Override
            public void onClick(AjaxRequestTarget target)
            	{
            	final String titleTxt=title.getValue().trim();
            	if ((titleTxt!=null)&&(titleTxt.length()>0))
            		{
            		String temp=parseValue(titleTxt);
            		if(verifyFormat("(SP)\\d{5}",temp))
            			{
            			setPreparation(temp);
            			updatePanels(temp);
            			target.add(pdh);
            			target.add(tabbedPanel);
            			tabbedPanel.setSelectedTab(tabbedPanel.getSelectedTab());
            			}
            		else{
            			modal1.setPageCreator(new ModalWindow.PageCreator()
            				{
            				public Page createPage()
            					{
            					return (new DataEntryTypePage(getPage())
   	                        		{
            						@Override
            						protected void onSave(String choice1, String choice2) {
									if (choice1.equals("Manual"))
									{
									
											setResponsePage(new EditPrepPlate(getPage(), titleTxt, pp, choice2));
												//new EditPrepPlate(getPage(), titleTxt, pp));
									}
									else
										setResponsePage(new PrepSheetUpload(getPage(), titleTxt, pp, choice2));
								}
   	                         });
   	                     }
   	                 });
//            		modal1.setPageCreator(new ModalWindow.PageCreator(){
//	                     public Page createPage(){
//	                         return (new EditPrepPlate(getPage(), titleTxt, pp));
//	                     }
//	                 });
	            	 modal1.show(target);
            		}
            	}
            	else{
            		setPreparation(null);
            		updatePanels(null);
            		getParent().error("Please enter a Prep Title! ");
            	}
            	target.add(pp);
            }
        	@Override // issue 464
			public MarkupContainer setDefaultModel(IModel model) 
			    {
				// TODO Auto-generated method stub
				return this;
			    }
        };
		return link;
	}
	
	private String parseValue(String temp){
		if (temp.indexOf("(")>0)
			temp = temp.substring(temp.lastIndexOf("(") + 1, temp.lastIndexOf(")"));
		return temp;
	}
	
	private boolean verifyFormat(String format, String input) {
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
	}
}



///////////////SCRAP CODE ////////////
//			String creator = ((METWorksSession) Session.get()).getCurrentUserFirstName()+" "+((METWorksSession) Session.get()).getCurrentUserLastName();
//SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
//String prepDate = sdf.format(Calendar.getInstance().getTime());
//add(new Label("creator", creator));
//add(new Label("prepDate", prepDate));

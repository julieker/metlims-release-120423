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
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import edu.umich.brcf.shared.layers.domain.Preparation;
import edu.umich.brcf.metabolomics.layers.service.FractionationService;



public class FractionPrepPanel extends Panel{
	
	String preparation=null;
	FractionPrepPanel fPanel;
//	FractionPrepDetailPanel fpdPanel;
	AjaxLink createLink=null;
	AjaxTabbedPanel tabbedPanel; 
	@SpringBean
	FractionationService fractionationService;
	
	public FractionPrepPanel(String id) {
		super(id);
		fPanel=this;
		setOutputMarkupId(true);
		add(new FeedbackPanel("feedback"));
		add(new FractionPrepForm("preparationForm"));
	}

	public final class FractionPrepForm extends Form {
		public FractionPrepForm(final String id) {
			super(id);
			setOutputMarkupId(true);
			final List<ITab> tabs=new ArrayList<ITab>();
			 tabs.add(new AbstractTab(new Model("Prep Details")) {
		 		   public Panel getPanel(String panelId)
		 		   {
		 			 return (new FractionPrepDetailPanel(panelId, getPreparation()));
		 		   }
		 		 });
			 tabs.add(new AbstractTab(new Model("Edit Prep")) {
	 		   public Panel getPanel(String panelId)
	 		   {
	 			 return (new PrepMethodsPanel(panelId, getPreparation()));
	 		   }
	 		 });
		   
		    add(tabbedPanel=new AjaxTabbedPanel("tabs", tabs));
	        tabbedPanel.setSelectedTab(0);
			tabbedPanel.setOutputMarkupId(true);
//			fpdPanel=new FractionPrepDetailPanel("fractionPrepDetailPanel", getPreparation());
			final ModalWindow modal1= new ModalWindow("modal1");
		    modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
		    {
		        public void onClose(AjaxRequestTarget target)
		        {
		        	updatePanels(preparation);
		        	target.add(fPanel);
		        	target.add(tabbedPanel);
        			tabbedPanel.setSelectedTab(tabbedPanel.getSelectedTab());
		        }
		    });
		    add(modal1);
		    final TextField noSamples;
		    add(noSamples=new TextField("numberOfSamples", new Model("")));
		    noSamples.setType(Integer.class);
		    noSamples.add(new OnChangeAjaxBehavior () {
				protected void onUpdate(AjaxRequestTarget target) {
					target.add(createLink);
				}
			});
		    add(createLink=new AjaxLink("create"){
				@Override
				public void onClick(AjaxRequestTarget target){
					updatePanels(null);
					String noSamplesStrr=noSamples.getDefaultModelObjectAsString();
					final int num = (noSamplesStrr==null||(noSamplesStrr.trim().length()==0))? 0: Integer.parseInt(noSamplesStrr);
					if ((num>0)&&(num<=98))
					 {
						modal1.setInitialWidth(500);
					    modal1.setInitialHeight(500);
						modal1.setPageCreator(new ModalWindow.PageCreator(){
		                     public Page createPage(){
		                         return (new CreateFractionsPrepPlate(getPage(), num, fPanel));
		                     }
		                 });
						 target.add(modal1);
						 modal1.show(target);
			         }
					else{
						FractionPrepPanel.this.error("Please enter a number between 0 and 98! ");
						target.add(fPanel);}
				}
				@Override // issue 464
				public MarkupContainer setDefaultModel(IModel model) 
				    {
					// TODO Auto-generated method stub
					return this;
				    }
			});
	         final AutoCompleteTextField title = new AutoCompleteTextField("title", new Model("")){
					@Override
					protected Iterator getChoices(String input) {
						if (Strings.isEmpty(input)) {
							return Collections.EMPTY_LIST.iterator();
						}
						return getFractionPrepChoices(input);
					}
				};
			 add(title);
			 final AjaxLink getLink;
			 add(getLink=buildLink("getPrep", title, null));
			 final AjaxLink uploadLink;
			 add(uploadLink=buildLink("uploadLink", title, modal1));
			 title.add(new AjaxFormSubmitBehavior(this, "change") {
					protected void onSubmit(AjaxRequestTarget target) {
//						target.add(getLink);
//						target.add(uploadLink);
						target.add(modal1);
//						target.add(getLink.getParent());
					}
					@Override
					protected void onError(AjaxRequestTarget target) {
					}
				});
//	         add(fpdPanel);
	         updatePanels(null);
		}
	}
	
	public void updatePanels(String preparation){
		tabbedPanel.setVisible((preparation!=null));
	}
	
	private Iterator getFractionPrepChoices(String input){
		List<String> choices = new ArrayList<String>();
		for (Preparation prep : fractionationService.allFractionPreparations()) {
			final String prepTitle = prep.getTitle();
			if (prepTitle.toUpperCase().contains(input.toUpperCase()))
				choices.add(prepTitle + " (" + prep.getPrepID()+")");
		}
		return choices.iterator();
	}
	
	private AjaxLink buildLink(final String linkID, final TextField title, final ModalWindow modal1) {
		AjaxLink link=new AjaxLink(linkID)
        {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
            	final String titleTxt=title.getValue().trim();
            	if ((titleTxt!=null)&&(titleTxt.length()>0)){
            		if(linkID.equals("getPrep")){
	            		String temp=parseValue(titleTxt);
	            		if(verifyFormat("(SP)\\d{5}",temp)){
	            			setPreparation(temp);
	            			updatePanels(temp);
	            			target.add(tabbedPanel);
	            			tabbedPanel.setSelectedTab(tabbedPanel.getSelectedTab());
	            		}
	            		else{
	            			FractionPrepPanel.this.error("Incorrect title....... Please try again! ");
	            		}
            		}
            		else{
            			setPreparation(null);
            			updatePanels(null);
            			String temp=parseValue(titleTxt);
	            		if(verifyFormat("(SP)\\d{5}",temp)){
	            			FractionPrepPanel.this.error("Prep title already exists....... Please try again! ");
	            		}
	            		else {
	            			String id=fractionationService.getFractionPrepIdByName(titleTxt.trim());
	            			if(!(id==null) && !id.isEmpty()){
	            				FractionPrepPanel.this.error("Prep title already exists....... Please try again! ");
	            			}
	            			else{
		            			modal1.setInitialWidth(650);
		            		    modal1.setInitialHeight(200);
		            			modal1.setPageCreator(new ModalWindow.PageCreator(){
				                     public Page createPage(){
				                         return (new PrepSheetUpload(getPage(), titleTxt, fPanel));
				                     }
				                 });
								 target.add(modal1);
								 modal1.show(target);
		            		}
	            		}
	            	}
            	}
            	else{
            		setPreparation(null);
            		updatePanels(null);
            		FractionPrepPanel.this.error("Please enter a Prep Title! ");
            	}
            	target.add(fPanel);
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
	
	public void setPreparation(String preparation){
		this.preparation=preparation;
	}
	
	public String getPreparation(){
		return preparation;
	}
}

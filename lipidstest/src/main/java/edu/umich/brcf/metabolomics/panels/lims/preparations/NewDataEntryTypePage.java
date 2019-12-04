////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  NewDataEntryTypePage.java
//  Written by Jan Wigginton
//  October 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.lims.preparations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.model.ExperimentListModel;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;



public abstract class NewDataEntryTypePage extends WebPage
	{
	@SpringBean 
	private ExperimentService experimentService;
	
	@SpringBean
	private AssayService assayService;
	
	
	DropDownChoice selectedAssayDrop, selectedExperimentDrop;
	List<String> availableAssays = new ArrayList<String>();
	
	String selectedExperiment  =  null, selectedAssay  = null;
	String prepTitle = "", titleTag = "";
	
	DETForm detForm;
	AjaxSubmitLink saveLink;
	RadioChoice radioChoice1, radioChoice2;
	TextField prepTitleFld, tagFld;

	
	public NewDataEntryTypePage(Page backPage, final ModalWindow modal1)
		{
		add(new FeedbackPanel("feedback"));
		add(new DETForm("detForm", modal1));
		}
	

	public final class DETForm extends Form 
		{
		String choice1, choice2;
		Map<String, String> selectionPairs = new HashMap<String, String>();

		public DETForm(final String id, final ModalWindow modal1)
			{
			super(id);
			
			detForm=this;
			
			setOutputMarkupId(true);
			
			add(selectedExperimentDrop = buildExperimentDropdown("selectedExperimentDrop", "selectedExperiment"));
			add(selectedAssayDrop = buildAssayDropdown("selectedAssayDrop", "selectedAssay"));
			add(prepTitleFld = buildPrepTitleField("prepTitleFld", "prepTitle"));
			add(tagFld = buildTitleTagField("titleTag", "titleTag"));
			
			add(buildAddExpButton("addExp"));
			add(buildClearAllButton("clearAll"));
			
			final List<String> choices = Arrays.asList(new String[] { "File Upload", "Manual"});
			setChoice1(choices.get(1));
			add(radioChoice1=new RadioChoice("choice1",new PropertyModel(this,"choice1"), choices));
			radioChoice1.setRequired(true);

			final List<String> choices1 = Arrays.asList(new String[] { "96 Well", "54 Well"});
			setChoice2(choices1.get(1));
			add(radioChoice2=new RadioChoice("choice2",new PropertyModel(this,"choice2"), choices1));
			radioChoice2.setRequired(true);
			 
			add(saveLink=new AjaxSubmitLink("save")
	        	{
	            @Override
	            public void onSubmit(AjaxRequestTarget target) // issue 464
	            	{
	            	modal1.close(target);
	            	System.out.println("Prep title is " + prepTitle);
	            	NewDataEntryTypePage.this.onSave(getChoice1(), getChoice2(), prepTitle);
	            	}
	            @Override
				protected void onError( AjaxRequestTarget target ){
				    target.add( NewDataEntryTypePage.this.get("feedback") );
					} 
	        	});
		 
			radioChoice1.add(new AjaxFormChoiceComponentUpdatingBehavior() 
			 	{
		        private static final long serialVersionUID = 1L;

		        @Override
		        protected void onUpdate(AjaxRequestTarget target) {
		            System.out.println("Data entry choice = "+ getDefaultModelObjectAsString());
		            target.add(saveLink);
		            }
		        });
			 
			radioChoice2.add(new AjaxFormChoiceComponentUpdatingBehavior() 
				{
	            private static final long serialVersionUID = 1L;

	            @Override
	            protected void onUpdate(AjaxRequestTarget target) {
	            	System.out.println("plate format choice = "+ getDefaultModelObjectAsString());
	            	target.add(saveLink);
	            	}
			 	});
			}
		
		private IndicatingAjaxLink buildAddExpButton(String id)
			{
			return new IndicatingAjaxLink <Void>(id)
				{
				@Override
				public void onClick(AjaxRequestTarget target) 
					{
					setSelectedExperiment("");
					setSelectedAssay("");
					target.add(selectedExperimentDrop);
					target.add(selectedAssayDrop);
					}
				};
			}
		// issue 464
		private IndicatingAjaxLink buildClearAllButton(String id)
			{
			// isuse 39
			return new IndicatingAjaxLink <Void>(id)
				{
				@Override
				public void onClick(AjaxRequestTarget target) 
					{
					setSelectedExperiment("");
					setSelectedAssay("");
					setPrepTitle("");
					setTitleTag("");
					selectionPairs.clear();
					target.add(selectedExperimentDrop);
					target.add(selectedAssayDrop);
					target.add(prepTitleFld);
					target.add(tagFld);
					}
				};
			}
		
		
		private DropDownChoice buildExperimentDropdown(final String id, String propertyName)
			{
			selectedExperimentDrop =  new DropDownChoice(id,  new PropertyModel(this, "selectedExperiment"), 
				new ExperimentListModel("both", experimentService, false));
				
			selectedExperimentDrop.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForExperimentDrop"));			
			
			return selectedExperimentDrop;
			}

		
		private TextField<String> buildTitleTagField(String id, String property)
			{
			TextField <String> fld = new TextField <String>(id, new PropertyModel<String>(this, property))
				{
				@Override
				public boolean isEnabled()
					{
					return StringUtils.isNonEmpty(prepTitle);
					}
				};
				
			fld.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForTag"));
			fld.add(this.buildStandardFormComponentUpdateBehavior("onblur", "updateForTag"));
			
			fld.setOutputMarkupId(true);
			return fld;			
			}
		
		
		private TextField<String> buildPrepTitleField(String id, String property)
			{
			TextField <String> fld = new TextField <String>(id, new PropertyModel<String>(this, property))
				{
				@Override
				public boolean isEnabled()
					{
					return false;
					}
				
				@Override
				protected void onInvalid()
					{
					}
				};
			fld.setOutputMarkupId(true);
			return fld;			
			}	
		
		private DropDownChoice buildAssayDropdown(final String id, String propertyName)
			{
			DropDownChoice drp = new DropDownChoice(id,  new PropertyModel(this, propertyName), 
					new LoadableDetachableModel<List<String>>() 
					{
			    	@Override
			    	protected List<String> load() 
			    		{ 
			    		if (availableAssays != null)
			    			return availableAssays;
			    		
			    		return new ArrayList<String>();
			    		}
					})
				{
				public boolean isEnabled()
					{
					return selectedExperiment != null && !"".equals(selectedExperiment.trim());
					}
				
				protected boolean wantOnSelectionChangedNotifications() 
					{
			        return true;
					}
				};
		
			drp.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForAssayDrop"));			
		
			return drp;
			}
		
	private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(final String event,  final String response)
		{
		return new AjaxFormComponentUpdatingBehavior(event)
	    	{
	        @Override
	        protected void onUpdate(AjaxRequestTarget target)
	        	{
	        	switch (response)
		        	{
		        	case "updateForAssayDrop" :
		        		
		        		if (selectedExperiment != null && !"".equals(selectedExperiment.trim()))
	            			{
		        			setPrepTitle(buildTitle());
		        			target.add(prepTitleFld);
		        			target.add(tagFld);
	            			}
						break;
			        	
		        	case "updateForExperimentDrop" :
	
		        		availableAssays = assayService.allAssayNamesForExpId(selectedExperiment, false);
		        		if (availableAssays.size() == 1)
		        			selectedAssay = availableAssays.get(0);
		        		
		            	target.add(selectedAssayDrop);
		            	if (StringUtils.isNonEmpty(selectedAssay))
		            		{
		            		setPrepTitle(buildTitle());
		            		target.add(prepTitleFld);
		            		target.add(tagFld);
		            		}
			        	break;	
			        	
		        	case "updateForTag" :
		        		
		        		setPrepTitle(buildTitle());
		        		target.add(prepTitleFld);
		        		System.out.println("Updating for tag" + titleTag);
		        		break;
		        	}
	        	}
	    	};
		}

	private String buildTitle()
		{
		System.out.println("Building title. Tag is " + titleTag);
		
		String date = DateUtils.dateAsString(new Date(), "MM/dd/yyyy");

		if (!StringUtils.isNonEmpty(selectedExperiment) || !StringUtils.isNonEmpty(selectedAssay))
			return prepTitle;
		
		String assayId = StringParser.parseId(selectedAssay);
		
		if (this.selectionPairs.containsKey(selectedExperiment))
			if (selectionPairs.get(selectedExperiment).equals(assayId))
				{
				if (!prepTitle.endsWith(titleTag))
					prepTitle += "_" + titleTag;
				
				return prepTitle;
				}
		
		selectionPairs.put(selectedExperiment, assayId);
		
		prepTitle = "";
		for (String key : selectionPairs.keySet())
			{
			if (StringUtils.isNonEmpty(prepTitle))
				prepTitle += "_";
				
			prepTitle += key + "_" + selectionPairs.get(key);
			}
		prepTitle += ("_" + date);
		
		if (StringUtils.isNonEmpty(titleTag))
			prepTitle += ("_" + titleTag);
		
		System.out.println("Returning prep title " + prepTitle);
		return prepTitle;
		}


	public String getChoice1()
		{
		return choice1;
		}
		
		public void setChoice1(String choice1){
			this.choice1=choice1;
		}
		
		public String getChoice2(){
			return choice2;
		}
		
		public void setChoice2(String choice2){
			this.choice2=choice2;
		}
		
		public String getSelectedExperiment() {
			return selectedExperiment;
		}


		public void setSelectedExperiment(String se) {
			selectedExperiment = se;
		}


		public String getSelectedAssay() {
			return selectedAssay;
		}


		public void setSelectedAssay(String sa) {
			selectedAssay =sa;
		}

		
		public String getPrepTitle() {
			return prepTitle;
		}

		public void setPrepTitle(String pt) {
			prepTitle = pt;
		}

		public String getTitleTag() {
			return titleTag;
		}

		public void setTitleTag(String tt) {
			titleTag = tt;
		}
	}

	
	

	protected abstract void onSave(String choice, String string, String prepTitle);

}

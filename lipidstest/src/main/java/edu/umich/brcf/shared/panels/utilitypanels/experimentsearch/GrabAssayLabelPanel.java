////////////////////////////////////////////////////
// GrabAssayLabelPanel.java

// Written by Julie Keros Oct 15 2021 for issue 187
////////////////////////////////////////////////////

package edu.umich.brcf.shared.panels.utilitypanels.experimentsearch;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Aliquot;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;
import edu.umich.brcf.shared.util.widgets.AjaxAssayField;
import edu.umich.brcf.shared.util.widgets.METWorksAjaxUpdatingDateTextField;



public abstract class GrabAssayLabelPanel extends Panel
	{
	@SpringBean 
	private AssayService assayService;
	
	private String assay, input, searchType = "Id";
	private FeedbackPanel feedback;
	public GrabAssayLabelForm grabAssayLabelForm;
	AjaxCheckBox useDateCheckBox;
	public GrabAssayLabelPanel(String id)
		{
		super(id);
		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);	
		grabAssayLabelForm = new GrabAssayLabelForm("grabAssayLabelForm");
		add(grabAssayLabelForm);
		}
		
	public final class GrabAssayLabelForm extends Form 
		{
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		String createDate = DateUtils.dateStrFromCalendar("MM/dd/yyyy", cal);
		METWorksAjaxUpdatingDateTextField dateFld, dateFldTo;
		boolean useDate = false;
		
		// issue 187
		public boolean getUseDate()
			{
			return useDate;
			}
	// issue 187
		public void setUseDate(boolean useDate)
			{
			this.useDate = useDate;
			}
				
	// issue 187
		public String getCreateDate()
			{
			return createDate;
			}
	// issue 187
		public void setCreateDate(String createDate)
			{
			this.createDate = createDate;
			}	
		String createDateTo = DateUtils.dateStrFromCalendar("MM/dd/yyyy", cal);
		// issue 187
		public String getCreateDateTo()
			{
			return createDateTo;
			}
		// issue 187
		public void setCreateDateTo(String createDateTo)
			{
			this.createDateTo = createDateTo;
			}
		public GrabAssayLabelForm(final String id)
			{
			super(id);		
			cal.setTime(date);
			
			AjaxAssayField assayNameField;
			add(assayNameField = new AjaxAssayField("assay")
				{
				@Override
				public boolean isVisible()  { return "Name".equals(getSearchType()); }
				});
			assayNameField.setOutputMarkupId(true);
			assayNameField.add(buildFormSubmitBehavior(false, assayNameField));
			
			DropDownChoice<String> assayIdDrop;
			add(assayIdDrop = (DropDownChoice<String>) buildDropdown("assayDrop").setOutputMarkupId(true));
			assayIdDrop.add(buildFormSubmitBehavior(true, assayNameField));	
			// issue 187
			
			dateFld =  new METWorksAjaxUpdatingDateTextField("createDateTxt", new PropertyModel<String>(this, "createDate"), "change")
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target)  
			        { 
			        }
				@Override
				public boolean isEnabled()  
			        { 
					return getUseDate();
			        }
				
				};		
			dateFld.setDefaultStringFormat(Aliquot.ALIQUOT_DATE_FORMAT);
			add(dateFld);
			dateFldTo =  new METWorksAjaxUpdatingDateTextField("createDateToTxt", new PropertyModel<String>(this, "createDateTo"), "change")
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target)  
			        { 
			        }
				@Override
				public boolean isEnabled()  
			        { 
					return getUseDate();
			        }
				};		
			dateFldTo.setDefaultStringFormat(Aliquot.ALIQUOT_DATE_FORMAT);
			add(dateFldTo);
			
			add(this.builduseDateChkBox("useDate"));	
			useDateCheckBox.add(buildStandardFormComponentUpdateBehavior("change", "updateUseDate" )); 
			}
				
		private DropDownChoice buildDropdown(String id)
			{
			LoadableDetachableModel <List<String>> assayListModel = new LoadableDetachableModel<List<String>>() 
				{
				@Override
				protected List<String> load() { return assayService.allAssayNamesAndIdsMatching(true);  }
				};
			return new DropDownChoice<String>(id, new PropertyModel<String>(this, "assay"), assayListModel)
				{
				@Override
				public boolean isVisible()  { return "Id".equals(getSearchType()); }
				};
			}
				
		private AjaxFormSubmitBehavior buildFormSubmitBehavior(final boolean isDrop, final AjaxAssayField nameField)
			{
			return new AjaxFormSubmitBehavior(this, "change") 
				{
				protected void onSubmit(AjaxRequestTarget target) 
					{
					input = isDrop ? assay : ((AutoCompleteTextField)(nameField)).getInput();
					
					try
						{
						String assayId = assayService.getAssayIdForSearchString(StringParser.parseId(input), "with name");
						target.add(feedback);
						GrabAssayLabelPanel.this.onSelect(input, grabAssayLabelForm.getCreateDate(), grabAssayLabelForm.getCreateDateTo(),  target);
						}	
					catch (Exception e) 
						{ 
						e.printStackTrace();
						GrabAssayLabelPanel.this.error(e.getMessage());
						target.add(feedback);
						}
					}
				};
			}
	    //////////////////////////////
	
		protected AjaxCheckBox builduseDateChkBox(String id )
		    {
		    useDateCheckBox = new AjaxCheckBox(id, new PropertyModel(this, "useDate"))
			    {
		    	@Override
			    public boolean isVisible()
				    {
		    		return true;
				    }
		    	@Override
			    public boolean isEnabled()
				    {
		    		return true;
				    }
			    @Override
			    public void onUpdate(AjaxRequestTarget target)
				    {
				    }
			    };
		    return useDateCheckBox;
		    }
				
		private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(String event, final String response)
	        {
	         return new AjaxFormComponentUpdatingBehavior(event)
	            {
	            @Override 
	            protected void onUpdate(AjaxRequestTarget target)
	                {
	                switch (response)
	                        {
	                    case "updateUseDate" :
	                    	target.add(dateFld);
	                    	target.add(dateFldTo);
	                    	target.add(grabAssayLabelForm);
	                        break;
	                    default : break;
	                        }
	                }
	            };
	        }
			
		public String getAssay() { return assay; }
		public void setAssay(String e) { assay = e; }
		}
	
	public String getSearchType()
		{
		return searchType;
		}
	
	public void setSearchType(String searchType)
		{
		this.searchType = searchType;
		}
		
	public String getAssay() { return assay; }
	public void setAssay(String a) { assay = a; }
	
	protected abstract void onSelect(String assay, String createDate, String createDateTo, AjaxRequestTarget target);
	}
	


package edu.umich.brcf.metabolomics.panels.lims.prep;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;


public abstract class DataEntryTypePage extends WebPage
	{
	DETForm detForm;
	AjaxSubmitLink saveLink;
	RadioChoice radioChoice1, radioChoice2;
	
	public DataEntryTypePage(Page backPage)
		{
		add(new FeedbackPanel("feedback"));
		add(new DETForm("detForm"));
		}
	

	public final class DETForm extends Form 
		{
		String choice1, choice2;
		
		public DETForm(final String id)
			{
			super(id);
			detForm=this;
			setOutputMarkupId(true);
			final List<String> choices = Arrays.asList(new String[] { "File Upload", "Manual"});
			 
			add(radioChoice1=new RadioChoice("choice1",new PropertyModel(this,"choice1"), choices));
			setChoice1(choices.get(1));
			radioChoice1.setRequired(true);
			 
			final List<String> choices1 = Arrays.asList(new String[] { "96 Well", "54 Well"});
			add(radioChoice2 = new RadioChoice("choice2",new PropertyModel(this,"choice2"), choices1));
			setChoice2(choices1.get(1));
			radioChoice2.setRequired(true);
			 
			add(saveLink=new AjaxSubmitLink("save")
		        {
		            @Override
		            public void onSubmit(AjaxRequestTarget target) // issue 464
		            	{
		            	DataEntryTypePage.this.onSave(getChoice1(), getChoice2());
		            	}
		            @Override
					protected void onError( AjaxRequestTarget target )
		            	{
					    target.add( DataEntryTypePage.this.get("feedback") );
		            	} 
		        });
			 
			 radioChoice1.add(new AjaxFormChoiceComponentUpdatingBehavior() 
				 {
		         private static final long serialVersionUID = 1L;

		            @Override
		            protected void onUpdate(AjaxRequestTarget target) 
		            	{
		            	System.out.println("Data entry choice = "+ getDefaultModelObjectAsString());
		            	target.add(saveLink);
		            	}
				 });
			 
			 radioChoice2.add(new AjaxFormChoiceComponentUpdatingBehavior() 
				 {
				 private static final long serialVersionUID = 1L;

				 @Override
				 protected void onUpdate(AjaxRequestTarget target) 
					 {
					 System.out.println("plate format choice = "+ getDefaultModelObjectAsString());
					 target.add(saveLink);
					 }
				 });
			}
		
		public String getChoice1()
			{
			return choice1;
			}
		
		public void setChoice1(String choice1)
			{
			this.choice1=choice1;
			}
		
		public String getChoice2()
			{
			return choice2;
			}
		
		public void setChoice2(String choice2)
			{
			this.choice2=choice2;
			}
		}

	
	protected abstract void onSave(String choice, String string);
	}

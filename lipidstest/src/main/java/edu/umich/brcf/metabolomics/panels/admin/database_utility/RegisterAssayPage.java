package edu.umich.brcf.metabolomics.panels.admin.database_utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.SampleAssayService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.structures.SelectableObject;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.datacollectors.AssaySelectionSet;
//import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;
import edu.umich.brcf.shared.util.widgets.AssayDropDown;
import edu.umich.brcf.shared.util.widgets.METWorksPctSizableModal;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;


public class RegisterAssayPage extends WebPage
	{
	@SpringBean
	SampleService sampleService;
	
	@SpringBean
	ExperimentService experimentService;
	
	@SpringBean
	SampleAssayService sampleAssayService;
	
	@SpringBean
	AssayService assayService;
	
	AssaySelectionSet assaySelection;
	List<String> availableAssays = assayService.allAssayNamesAndIds();
	

	public RegisterAssayPage(String id, String eid, final METWorksPctSizableModal modal2)
		{
		super();		
		FeedbackPanel feedback; 
		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);
		add(new RegisterAssayForm("assayAddForm", eid, modal2)); 
		}
	
	
	public class RegisterAssayForm extends Form
		{
		private WebMarkupContainer container;
		private AjaxSubmitLink saveButton;
		
		public RegisterAssayForm(String id, final String eid, final METWorksPctSizableModal modal2)
			{
			super(id); 
			assaySelection = new AssaySelectionSet(eid.trim());			
			container = new WebMarkupContainer("container");
			container.setOutputMarkupId(true);			
			container.add(new TextField("expId", new PropertyModel<String>(assaySelection, "expId"))
				{
				@Override 
				public boolean isEnabled() { return false; }
				});			
			container.add(this.buildAssayDropdown("assayDrop"));
			container.add(buildSamplesListView("sampleSelectionView")); 
			container.add(new AjaxCancelLink("backButton",  modal2));
			container.add(buildSelectAllCheck("selectAll"));			
			container.add(saveButton = new AjaxSubmitLink("saveButton")
				{
				@Override
				public boolean isEnabled()
					{
					//return assaySelection != null && !StringUtils.isNullOrEmpty(assaySelection.getAssayId()) && assaySelection.countNSelected() > 0 && StringUtils.isNonEmpty(assaySelection.getAssayLabel());
					return assaySelection != null && !StringUtils.isNullOrEmpty(assaySelection.getAssayId())  && StringUtils.isNonEmpty(assaySelection.getAssayLabel());
					}
				
				@Override
				protected void onSubmit(AjaxRequestTarget target)  // issue 464
					{
					String msg = "Assay has already been selected for this experiment";
					List <String> samplesTooMany = new ArrayList <String> ();
					if (assaySelection.countNSelected() > 0)						
					     samplesTooMany = assayService.samplesTooManyAssaysForSample(StringUtils.buildDatabaseTupleListFromList(assaySelection.getSelectedAssayIds()),assaySelection.getAssayId());	
					Collections.sort(samplesTooMany);					
					// Issue 249 
					try 
					    {
					    if (samplesTooMany.size() > 0)					    
						    throw new RuntimeException("Unable to save selection.  The following samples have more than 5 assays:" + samplesTooMany.toString());									  
					    sampleService.saveSampleAssay(assaySelection);
					// issue 249
						msg = "Assay " + assaySelection.getAssayLabel() + " has been added to experiment " + eid + " and is now associated with " + assaySelection.getSelectedAssayIds().size() + " sample(s) ";           
					    }									
					catch (RuntimeException r)
				        {
						msg = r.getMessage();
						}
					catch (Exception e) 
					    { 
						msg = "Error while saving assay -- please report the problem to wiggie@umich.edu"; 
					    }
					if (!StringUtils.isEmptyOrNull(msg))
						RegisterAssayPage.this.error(msg);					
					target.add(RegisterAssayPage.this.get("feedback"));					
					}
				
				@Override
				protected void onError(AjaxRequestTarget target) {  } 		// issue 464		
				// Issue 249
				@Override
				protected void updateAjaxAttributes( AjaxRequestAttributes attributes )
				    {
				    super.updateAjaxAttributes( attributes );
				    if (assaySelection.countNSelected() == 0)
				        {
				        AjaxCallListener ajaxCallListener = new AjaxCallListener();
				        String text = "This will delete all the samples from this assay. Are you sure ?";
				        ajaxCallListener.onPrecondition( "return confirm('" + text + "');" );
				        attributes.getAjaxCallListeners().add( ajaxCallListener );
				        }
				    }				
				});			
			saveButton.setOutputMarkupId(true);
			add(container);
			}
	

		ListView<SelectableObject> buildSamplesListView(String id)
			{
			return new ListView(id, new PropertyModel<List<SelectableObject>>(assaySelection, "sampleSelection"))
				{
				@Override
				protected void populateItem(ListItem item) 
					{
					SelectableObject entry = (SelectableObject) item.getModelObject();
					Sample s = (Sample) entry.getSelectionObject();					
					item.add(buildSampleField("sampleID", s, "sampleID"));
					item.add(new AjaxCheckBox("selectSample", new PropertyModel<Boolean>(entry, "selected"))
						{
						@Override
						protected void onUpdate(AjaxRequestTarget target) { target.add(saveButton);  }
						});
					
					item.add(OddEvenAttributeModifier.create(item));
					}
				};
			}
		
		
		public AjaxCheckBox buildSelectAllCheck(String id)
			{
			AjaxCheckBox check = new AjaxCheckBox("selectAll", new PropertyModel<Boolean>(assaySelection, "allSelected"))
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target) 
					{
					assaySelection.updateSelectionForAll(assaySelection.getAllSelected());
					target.add(container);
					} 
				};			
			check.setOutputMarkupId(true);			
			return check;
			}
		
		
		public TextField buildSampleField(String id, Sample sample, String property)
			{
			return new TextField(id, new PropertyModel<String>(sample, property))
				{
				@Override
				public boolean isEnabled() {  return false; }
				};
			}
		

		// Issue 249
		public DropDownChoice<String> buildAssayDropdown(String id)
		    {
		    return new AssayDropDown(id, assaySelection, "assayLabel", "")
			    {
			    @Override
			    protected void doUpdateBehavior(AjaxRequestTarget target) 
			        { 
				    target.add(saveButton); 
				    assaySelection.updateSelectionForAssay(true);
				    target.add(container);
			        }
			    };
		    }
		}    
	}
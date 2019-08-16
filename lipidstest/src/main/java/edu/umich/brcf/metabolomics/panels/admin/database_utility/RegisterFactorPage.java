// RegisterFactorPage.java
// Written by Jan Wigginton, October 2015

package edu.umich.brcf.metabolomics.panels.admin.database_utility;

import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.FactorService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.datacollectors.ExperimentalDesign;
import edu.umich.brcf.shared.util.datacollectors.FactorValueSet;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.structures.Pair;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;
import edu.umich.brcf.shared.util.widgets.AjaxUpdatingTextField;
import edu.umich.brcf.shared.util.widgets.METWorksPctSizableModal;


public class RegisterFactorPage extends WebPage
	{
	@SpringBean
	SampleService sampleService;

	@SpringBean
	ExperimentService experimentService;
	
	@SpringBean
	FactorService factorService;

	FactorValueSet factorInfo;

	
	public RegisterFactorPage(String id, WebPage backPage, String eid, final METWorksPctSizableModal modal2)
		{
		super();
		
		FeedbackPanel feedback; 
		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);
		
		add(new RegisterFactorAddForm("factorAddForm", backPage, eid, modal2));
		}
	

	public class RegisterFactorAddForm extends Form
		{
		IndicatingAjaxLink saveButton;
		Boolean disableSubmit = false;

		public RegisterFactorAddForm(String id, WebPage backPage, String eid, final METWorksPctSizableModal modal2)
			{
			super(id);
			factorInfo = new FactorValueSet(eid);
			
			disableSubmit = (factorService.countFactorsForExperiment(factorInfo.getExpId()) >= ExperimentalDesign.SUBMISSION_SHEET_NFACTORS);
			
			modal2.setPageHeightPct(1.0);
			add(buildExperimentIdField("expId", "expId"));

			add(buildFactorNameField("factorName", "change", "factorName"));
			add(buildFactorListView("factorView"));

			add(new AjaxCancelLink("cancelButton", modal2));
			
			add(saveButton = new IndicatingAjaxLink("saveButton")
				{
				@Override
				public boolean isEnabled()
					{
					return factorInfo != null && factorInfo.getFactorName() != null
							&& !factorInfo.getFactorName().trim().startsWith("New Factor Name")
							&& (factorInfo.valuesInitialized())
							&& !disableSubmit;
					}

				@Override // issue 464
				public MarkupContainer setDefaultModel(IModel model) 
				    {
					// TODO Auto-generated method stub
					return this;
				    }

				@Override
				public void onClick(AjaxRequestTarget target)
					{	
					String msg = "";
					if (!factorInfo.valuesInitialized())
						msg = "All factor values must be initialized. ";

					String name = factorInfo.getFactorName();
					if (StringUtils.isEmpty(name) || "New Factor Name Here".equals(name.trim()))
						msg += "Factor name must be initialized.";

					if (!(factorInfo.nameIsNewToExperiment()))
						msg += "Factor name already exists for this experiment";

					if (!StringUtils.isEmptyOrNull(msg))
						RegisterFactorPage.this.error(msg);
					
					else
						try
						{
						sampleService.saveSampleFactor(factorInfo);
						disableSubmit = (factorService.countFactorsForExperiment(factorInfo.getExpId()) >= ExperimentalDesign.SUBMISSION_SHEET_NFACTORS);
						target.add(saveButton);
					
						RegisterFactorPage.this.info("Factor values successfully saved");
						if (factorService.countFactorsForExperiment(factorInfo.getExpId()) >= ExperimentalDesign.SUBMISSION_SHEET_NFACTORS)
							// JAK correct typo of fators
							RegisterFactorPage.this.info("This experiment now has the maximum number of factors.  You will not be able to add any more factors");
				
						}
					catch (Exception e)
						{
						msg = e.getMessage();
						RegisterFactorPage.this.error(msg);
						}
					
					target.add(RegisterFactorPage.this.get("feedback"));
					}
				});
			}

		
		private TextField<String> buildExperimentIdField(String id, String property)
			{
			return new TextField<String>("expId", (IModel<String>) ((IModel) new PropertyModel<String>(factorInfo, "expId")))
				{
				@Override
				public boolean isEnabled() { return false; }
				};
			}
		

		TextField<String> buildFactorNameField(String id, String event, String property)
			{
			TextField<String> fld = new AjaxUpdatingTextField(id, "change", new PropertyModel(factorInfo, property));

			fld.add(new AjaxFormComponentUpdatingBehavior("change")
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target) { target.add(saveButton); }
				});
			
			fld.add(StringValidator.maximumLength(120));
			fld.setRequired(true);

			return fld;
			}

		// onComponentTag
		ListView<Pair> buildFactorListView(String id)
			{
			return new ListView(id, new PropertyModel<List<Pair>>(factorInfo, "idsAndValues"))
				{
				@Override
				protected void populateItem(ListItem item)
					{
					Pair entry = (Pair) item.getModelObject();

					item.add(buildSampleField("sampleID", entry, "id"));
					item.add(buildValueField("sampleValue", entry, "value"));
					item.add(OddEvenAttributeModifier.create(item));
					}
				};
			}

		
		public TextField<String> buildSampleField(String id, Pair entry, String property)
			{
			TextField<String> fld = new TextField<String>(id, new PropertyModel<String>(entry, property))
				{
				@Override
				public boolean isEnabled() {  return false; }
				};
				
			return fld;
			}

		
		public TextField<String> buildValueField(String id, final Pair entry, String property)
			{
			TextField<String> fld = new TextField<String>(id, new PropertyModel<String>(entry, property))
				{
				@Override
				protected void onComponentTag(ComponentTag tag)
					{
					super.onComponentTag(tag);
					
					int idxForPair = factorInfo.getIndexForSampleId(entry.getId());
					idxForPair++;
					
					String label = "tabindex=" + idxForPair;
					tag.put("tabIndex", label);
					}
				};
				
			fld.add(new AjaxFormComponentUpdatingBehavior("change")
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target) { target.add(saveButton); }
				});
			
			
			fld.add(StringValidator.maximumLength(40));
			return fld;
			}
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//ExperimentOrDateSearchPanel.java
//Written by Jan Wigginton August 2015
///////////////////////////////////////////////
package edu.umich.brcf.shared.panels.utilitypanels;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;
import edu.umich.brcf.shared.util.model.ExperimentListModel;
import edu.umich.brcf.shared.util.widgets.METWorksAjaxUpdatingDateTextField;


public abstract class ExperimentOrDateSearchPanel extends Panel
	{
	@SpringBean
	ExperimentService experimentService;

	List<String> searchTypeOptions = Arrays.asList(new String[] { "Experiment", "Run Date", "Upload Date", "Completion Date"});
	DropDownChoice<String> searchTypeDrop;
	String searchType = searchTypeOptions.get(0);

	public ExperimentOrDateSearchPanel(String id)
		{
		super(id);

		ExperimentOrDateSearchForm form;
		add(form = new ExperimentOrDateSearchForm("expOrDateSearchForm"));
		form.setMultiPart(true);
		}
	

	class ExperimentOrDateSearchForm extends Form
		{
		WebMarkupContainer wmc, wmc2;

		private IndicatingAjaxButton searchButton;
		private DropDownChoice<String> experimentDrop;
		private METWorksAjaxUpdatingDateTextField fromDateFld, toDateFld;

		private String searchExperiment = "All";
		private Date fromDate = new Date(), toDate = new Date();

		public ExperimentOrDateSearchForm(String id)
			{
			super(id);

			add(wmc = grabMarkupContainer("wmc"));
			add(wmc2 = grabMarkupContainer("wmc2"));

			add(searchTypeDrop = buildSearchTypeDropdown("searchTypeDropdown", "searchType"));
			wmc.add(experimentDrop = buildExperimentDropdown("experimentDropdown", "searchExperiment"));
			wmc2.add(fromDateFld = grabDateTextField("fromDateTxt", "fromDate"));
			wmc2.add(toDateFld = grabDateTextField("toDateTxt", "toDate"));

			add(searchButton = buildSearchButton());
			}

		
		private IndicatingAjaxButton buildSearchButton()
			{
			return new IndicatingAjaxButton("searchButton")
				{
				public boolean isEnabled()
					{
					return true; // ((getSearchType().equals("Experiment")// && getSearchExperiment() != null) // || (!getSearchType().trim().equals("")));
					}

				@Override // issue 464
				protected void onSubmit(AjaxRequestTarget arg0)
					{
					doSubmit(getSearchType(), getSearchExperiment(), DateUtils.dateAsCalendar(fromDate), DateUtils.dateAsCalendar(toDate));
					}

				@Override
				protected void onError(AjaxRequestTarget arg0) {  }
				};
			}

		
		private DropDownChoice buildExperimentDropdown(String id, String propertyName)
			{
			experimentDrop = new DropDownChoice(id, new PropertyModel(this, propertyName), new ExperimentListModel("absciex", experimentService, true))
				{
				@Override
				public boolean isVisible()
					{
					return searchType.equals("Experiment");
					}
				};

			experimentDrop.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForSearchExperimentDrop"));
			experimentDrop.add(this.buildStandardFormComponentUpdateBehavior("onload", "updateForSearchExperimentDrop"));

			return experimentDrop;
			}

		
		private DropDownChoice buildSearchTypeDropdown(String id, String propertyName)
			{
			searchTypeDrop = new DropDownChoice(id, new PropertyModel(this, propertyName), searchTypeOptions);
			searchTypeDrop.add(this.buildStandardFormComponentUpdateBehavior( "onload", "updateForSearchTypeDrop"));
			searchTypeDrop.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForSearchTypeDrop"));

			return searchTypeDrop;
			}

		
		private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(final String event, final String response)
			{
			return new AjaxFormComponentUpdatingBehavior(event)
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target)
					{
					switch (response)
						{
						case "updateForSearchExperimentDrop": target.add(searchButton); break;

						case "updateForSearchTypeDrop":

							target.add(experimentDrop);
							target.add(fromDateFld);
							target.add(toDateFld);
							break;
						}
					
					target.add(wmc);
					target.add(wmc2);
					}
				};
			}
		

		private METWorksAjaxUpdatingDateTextField grabDateTextField(String id, String property)
			{
			return new METWorksAjaxUpdatingDateTextField(id, new PropertyModel(this, property), "change")
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target) { }
				};
			}

		
		private WebMarkupContainer grabMarkupContainer(final String id)
			{
			WebMarkupContainer w = new WebMarkupContainer(id)
				{
				@Override
				public boolean isVisible()
					{
					if (id.equals("wmc"))
						return (searchType != null && searchType.equals("Experiment"));

					return (searchType != null && (searchType.equals("Run Date") || searchType.equals("Upload Date") || searchType.equals("Completion Date")));
					}
				};

			w.setOutputMarkupPlaceholderTag(true);
			return w;
			}

		
		public String getSearchExperiment()
			{
			return searchExperiment;
			}

		public void setSearchExperiment(String exp)
			{
			searchExperiment = exp;
			}

		public Date getFromDate()
			{
			return fromDate;
			}

		public void setFromDate(Date searchFromDate)
			{
			this.fromDate = searchFromDate;
			}

		public Date getToDate()
			{
			return toDate;
			}

		public void setToDate(Date searchToDate)
			{
			this.toDate = searchToDate;
			}

		public String getSearchType()
			{
			return searchType;
			}

		public void setSearchType(String st)
			{
			searchType = st;
			}
		}

	public void limitToSearchType(String type)
		{
		searchTypeDrop.setEnabled(false);
		setSearchType(type);
		}

	public void setSearchType(String st)
		{
		searchType = st;
		}

	public abstract void doSubmit(String searchType, String selectedExperiment, Calendar fromDate, Calendar toDate);
	}
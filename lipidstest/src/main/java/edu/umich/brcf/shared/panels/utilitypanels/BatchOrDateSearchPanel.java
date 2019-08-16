///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//BatchOrDateSearchPanel.java
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

import edu.umich.brcf.shared.util.utilpackages.DateUtils;
import edu.umich.brcf.shared.util.widgets.METWorksAjaxUpdatingDateTextField;



public abstract class BatchOrDateSearchPanel extends Panel
	{
	List<String> searchStatusOptions = Arrays.asList(new String[] { "All", "Pending", "Complete" });
	List<String> dateRangeOptions = Arrays.asList(new String[] { "All", "Range" });

	DropDownChoice<String> dateRangeTypeDrop, searchStatusDrop;
	String dateRangeType = dateRangeOptions.get(0), searchStatus = searchStatusOptions.get(0);

	
	public BatchOrDateSearchPanel(String id)
		{
		super(id);
		BatchOrDateSearchForm form;
		add(form = new BatchOrDateSearchForm("batchOrDateSearchForm"));
		form.setMultiPart(true);
		}

	
	class BatchOrDateSearchForm extends Form
		{
		WebMarkupContainer wmc, wmc2;

		private IndicatingAjaxButton searchButton;
		private DropDownChoice<String> batchDrop;
		private METWorksAjaxUpdatingDateTextField fromDateFld, toDateFld;

		private Date fromDate = new Date(), toDate = new Date();

		
		public BatchOrDateSearchForm(String id)
			{
			super(id);

			add(wmc = grabMarkupContainer("wmc"));
			add(wmc2 = grabMarkupContainer("wmc2"));

			add(searchStatusDrop = buildSearchStatusDropdown("searchStatusDrop", "searchStatus"));
			wmc.add(dateRangeTypeDrop = buildDateRangeTypeDropdown("dateRangeTypeDrop", "dateRangeType"));
			wmc2.add(fromDateFld = grabDateTextField("fromDateTxt", "fromDate"));
			wmc2.add(toDateFld = grabDateTextField("toDateTxt", "toDate"));

			add(searchButton = buildSearchButton());
			}
		

		private IndicatingAjaxButton buildSearchButton()
			{
			return new IndicatingAjaxButton("searchButton")
				{
				@Override
				public boolean isEnabled()
					{
					return true; // ((getSearchType().equals("Experiment") && getSearchExperiment() != null) || (!getSearchType().trim().equals("")));
					}

				@Override
				protected void onSubmit(AjaxRequestTarget arg0) // issue 464
					{
					doSubmit(getSearchStatus(), DateUtils.dateAsCalendar(fromDate), DateUtils.dateAsCalendar(toDate));
					}

				@Override
				protected void onError(AjaxRequestTarget arg0) { } // issue 464
				};
			}
		

		private DropDownChoice buildSearchStatusDropdown(String id,String propertyName)
			{
			searchStatusDrop = new DropDownChoice(id, new PropertyModel(this, propertyName), searchStatusOptions);
			searchStatusDrop.add(this.buildStandardFormComponentUpdateBehavior("onload", "updateForSearchStatusDrop"));
			searchStatusDrop.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForSearchStatusDrop"));

			return searchStatusDrop;
			}

		
		private DropDownChoice buildDateRangeTypeDropdown(String id, String propertyName)
			{
			dateRangeTypeDrop = new DropDownChoice(id, new PropertyModel(this,propertyName), dateRangeOptions);
			dateRangeTypeDrop.add(this.buildStandardFormComponentUpdateBehavior("onload","updateForDateRangeTypeDrop"));
			dateRangeTypeDrop.add(this.buildStandardFormComponentUpdateBehavior("change","updateForDateRangeTypeDrop"));

			return dateRangeTypeDrop;
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
						case "updateForSearchBatchDrop":
							target.add(searchButton);
							break;

						case "updateForDateRangeTypeDrop":
							target.add(dateRangeTypeDrop);
							target.add(fromDateFld);
							target.add(toDateFld);
							break;

						case "updateForSearchStatusDrop":
							target.add(searchStatusDrop);
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
						return true; // ("Status".equals(searchType));

					return ("Range".equals(dateRangeType));
					}
				};

			w.setOutputMarkupPlaceholderTag(true);
			return w;
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

		public void limitToSearchType(String type)
			{
			dateRangeTypeDrop.setEnabled(false);
			setDateRangeType(type);
			}

		public String getDateRangeType()
			{
			return dateRangeType;
			}

		public void setDateRangeType(String ss)
			{
			dateRangeType = ss;
			}

		public void setSearchStatus(String ss)
			{
			searchStatus = ss;
			}

		public String getSearchStatus()
			{
			return searchStatus;
			}
		}

	
	public abstract void doSubmit(String searchStatus, Calendar fromDate, Calendar toDate);
	}
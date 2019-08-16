// DataSetListPanel.java
// Written by Jan Wigginton 04/29/15

// Upload Client

package edu.umich.brcf.metabolomics.panels.lipidshome.browse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.domain.Ms2DataSet;
import edu.umich.brcf.metabolomics.layers.service.Ms2DataSetService;
import edu.umich.brcf.metabolomics.panels.lipidshome.browse.SampleInfoUploader.UploadType;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;
import edu.umich.brcf.shared.util.widgets.AjaxBackButton;



public class DataSetListPanel extends Panel
	{
	@SpringBean
	Ms2DataSetService ms2DataSetService;
	
	
	private static final long serialVersionUID = -5410690939806185714L;
	

	public DataSetListPanel(String id, WebPage backPage, Calendar fromDateCalendar, Calendar toDateCalendar, Boolean ifEdit, Boolean useRunDate)
		{
		super(id);
		
		add(new FeedbackPanel("feedback"));
		
		DataSetForm luf = new DataSetForm("dataSetForm", backPage, null, fromDateCalendar, toDateCalendar, ifEdit, useRunDate);
		luf.setMultiPart(true);
		//luf.add(new UploadProgressBar("progress", luf));
		add(luf);
		}
	
	public DataSetListPanel(String id, WebPage backPage, String expId, Boolean ifEdit, Boolean useRunDate)
		{
		super(id);
		
		add(new FeedbackPanel("feedback"));
		
		DataSetForm luf = new DataSetForm( "dataSetForm", backPage, expId, null, null, ifEdit, useRunDate);
		luf.setMultiPart(true);
		//luf.add(new UploadProgressBar("progress", luf));
		add(luf);
		}

	
	
	public final class DataSetForm extends Form 
		{
		private WebMarkupContainer container; 
		protected PageableListView dataSetView;
		List <Ms2DataSet> dataSets = new ArrayList <Ms2DataSet>();
		
		String searchTitle, expId;
		Boolean ifEdit;
		ModalWindow modal1;
		
		
		public DataSetForm(String id,  WebPage backPage, String expId, Calendar fromDateCalendar, Calendar toDateCalendar, Boolean ifEdit, Boolean useRunDate)
			{
			super(id);
			
			modal1 = ModalCreator.createModalWindow("modal1", 500, 300);
			add(modal1);

			this.ifEdit = ifEdit;
			this.expId = expId;
			
			if (expId != null)
				searchTitle =  expId.equals("All") ? "All Uploaded Data Sets" :  "Data Sets Uploaded for Experiment " + expId;
			else if (useRunDate)
				searchTitle = "Data Sets for Samples Run : " + DateUtils.dateStrFromCalendar("MM/dd/yy", fromDateCalendar) 
				+ "-" + DateUtils.dateStrFromCalendar("MM/dd/yy", toDateCalendar);
			else
				searchTitle = "Data Sets Uploaded : " + DateUtils.dateStrFromCalendar("MM/dd/yy", fromDateCalendar) 
				+ "-" + DateUtils.dateStrFromCalendar("MM/dd/yy", toDateCalendar);
			
			dataSets = grabDataSets(expId, fromDateCalendar, toDateCalendar, useRunDate);
			
			add(new Label("searchResultTitle", new PropertyModel <String>(this, "searchTitle")));
			add(new AjaxBackButton("backButton", backPage));
	
			add(dataSetView = buildListView("dataSetListView"));
			dataSetView.setOutputMarkupId(true);
			}
		
		
		List <Ms2DataSet> grabDataSets(String expId, Calendar fromDateCalendar, Calendar toDateCalendar, Boolean useRunDate)
			{
			List <Ms2DataSet> lst = new ArrayList<Ms2DataSet> ();
			
			//System.out.println("Grabbing data sets for" + DateUtils.dateStrFromCalendar("MM/dd/yy", fromDateCalendar) + " to " + DateUtils.dateStrFromCalendar("MM/dd/yy", toDateCalendar));
			if (fromDateCalendar != null)
				return ( useRunDate ? ms2DataSetService.loadForRunDateRange(fromDateCalendar, toDateCalendar)
								 :	ms2DataSetService.loadForUploadDateRange(fromDateCalendar, toDateCalendar));
			
			if (expId == null || expId.equals("") || expId.equals("All"))
				return ms2DataSetService.loadAll();
			
			return  ms2DataSetService.loadByExpId(expId);
			}
		
		
		public PageableListView buildListView(String id)
			{
			return new PageableListView(id, new PropertyModel(this, "dataSets"), 600)
				{	
				public void populateItem(ListItem listItem) 
					{
					final Ms2DataSet item =  (Ms2DataSet) listItem.getModelObject();
					
					listItem.add(new Label("dataSetId", new PropertyModel(item, "dataSetId")));
					listItem.add(new Label("expId", new PropertyModel(item, "expId")));
					listItem.add(new Label("numCompounds", new PropertyModel <Integer>(item, "numCompounds")));
					listItem.add(new Label("ionMode", new PropertyModel<String>(item, "ionMode")));
					listItem.add(new Label("dataNotation", new PropertyModel<String>(item, "dataNotation")));
					listItem.add(new Label("runDate", new PropertyModel<Calendar>(item, "runDateAsStr")));
					listItem.add(new Label("uploadDate", new PropertyModel<Calendar>(item, "uploadDateAsStr")));
					listItem.add(new Label("uploadedBy", new PropertyModel<String>(item, "uploadedBy")));
					listItem.add(buildLinkToData("dataSetLink", item.getDataSetId()));
					listItem.add(buildLinkToRandomizationModal("randomizationLoaderLink", modal1, item.getDataSetId(), item.getExpId()));
					listItem.add(OddEvenAttributeModifier.create(listItem));
					}
				};
			}
				
		
		private IndicatingAjaxButton buildLinkToData(String id, final String dataSetId)
			{
			return new IndicatingAjaxButton(id)
				{
				@Override
				protected void onComponentTag(ComponentTag tag)
		    		{
		    		super.onComponentTag(tag);
		    		String displayTitle = ifEdit ? "Edit Data..." : "View Data...";
		    		tag.put("value", displayTitle);
		    		}
	
				@Override
				protected void onSubmit(AjaxRequestTarget arg0) // issue 464
					{
					if (!ifEdit)
						setResponsePage(new Ms2DataSetPage("dataResults", (WebPage) this.getPage(), dataSetId));
					else
						setResponsePage(new EditSampleMapPage("editSampleMap", (WebPage) this.getPage(), dataSetId));
					}
				};
			}
		
		
		private IndicatingAjaxButton buildBackButton(String id, final WebPage backPage)
			{
			return new IndicatingAjaxButton(id)
				{
				@Override
				protected void onSubmit(AjaxRequestTarget arg0) // issue 464
					{
					setResponsePage(backPage); 
					}
				};
			}
	
		private AjaxLink buildLinkToRandomizationModal(final String linkID, final ModalWindow modal1, final String dataSetId,  final String expId) 
			{	
			final String failMessage = "alert('WARNING :  Your randomization did not load.  ')";	  	            	    	
			
			modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
		     	{
				public void onClose(AjaxRequestTarget target)
		        	{
		        	//	target.appendJavaScript(failMessage);
		        	}
		        });
		
			modal1.setCloseButtonCallback(new ModalWindow.CloseButtonCallback()
		    	{
		        public boolean onCloseButtonClicked(AjaxRequestTarget target)
		        	{
		        	//		target.appendJavaScript(failMessage);
		        	return true;
		        	}
		    	});
			
			AjaxLink link =  new AjaxLink(linkID)
		    	{
				public boolean isEnabled()
					{
					return true; //item.getRandomizationType().equals("Custom");
					}
				
		        @Override
		        public void onClick(final AjaxRequestTarget target)
		        	{
		        	modal1.setInitialWidth(750);
				    modal1.setInitialHeight(175);
			    	
				    final SampleInfoUploader uploader = new SampleInfoUploader();
		            uploader.setUploadType(UploadType.UPLOAD_TYPE_RANDOMIZATION);
		            uploader.setExpId(expId);
		            uploader.setAssociatedId(dataSetId);
		            
		        	modal1.setPageCreator(new ModalWindow.PageCreator()
		        		{
		                public Page createPage()
		                	{
		                	return new SampleInfoUploaderPage(getPage(), uploader, modal1);
		                	}
		        		});
		        	
		        	modal1.show(target);
		        	}
		    	@Override // issue 464
				public MarkupContainer setDefaultModel(IModel model) 
				    {
					// TODO Auto-generated method stub
					return this;
				    }
		    	};
		    	
		    link.setOutputMarkupId(true);
		    return link;
			}
		
		public List<Ms2DataSet> getDataSets()
			{
			return dataSets;
			}
	
		public void setDataSets(List<Ms2DataSet> dataSets)
			{
			this.dataSets = dataSets;
			}
			
		public WebMarkupContainer getContainer()
			{
			return container;
			}
	
		public void setDataSetView(PageableListView view)
			{
			dataSetView = view;
			}
		
		
		public PageableListView getDataSetView()
			{
			return dataSetView;
			}
		
		public String getSearchTitle()
			{
			return searchTitle;
			}
		
		
		}
	}	








	




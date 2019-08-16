
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//LipidsMainPanel.java
//Written by Jan Wigginton 02/09/15,  Upgraded 05/09/15
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.lipidshome.browse;

import org.apache.wicket.markup.html.WebPage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.panels.utilitypanels.ExperimentOrDateSearchPanel;
import edu.umich.brcf.shared.panels.utilitypanels.ExperimentRunInfoUploadPanel;
import edu.umich.brcf.shared.panels.utilitypanels.ReportUploadSelectorPanel;
import edu.umich.brcf.shared.util.METWorksException;
import edu.umich.brcf.shared.util.datacollectors.ExperimentRunInfo;





public class LipidsMainPanel extends Panel 
	{
	@SpringBean
	private ExperimentService experimentService;

	List <String> possColIndices = Arrays.asList(new String [] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"});
	final List <String> colIndices = Arrays.asList(new String [] {"1", "2", "3", "4", "5", "6", "7"});
	
	public LipidsMainPanel(String id) 
		{
		super(id);
		
		add(new FeedbackPanel("feedback"));
		
		final List <CheckBox> checkBoxes = new ArrayList<CheckBox>();

		add(buildExperimentOrDateSearchPanel("experimentDateSelectorPanel"));
		add(new LaunchDRCCPanel("launchDRCCPanel"));
		add(buildLaunchDataEditPanel("launchLabelEditPanel"));
		
		add(buildExperimentOrDateSearchPanel("experimentRandomizationUploadPanel"));
		
		
		checkBoxes.clear();
		for (int i = 0; i < 7; i++)
			{
			CheckBox chk = new CheckBox("checkbox" + i, Model.of(Boolean.TRUE));
			chk.setEnabled(false);
			checkBoxes.add(chk);
			add(chk);
			add(buildColNoDropdown("colIdx" + i, "colIndices." + i));
			}
		
		ExperimentRunInfoUploadPanel uploadPanel;
		ExperimentRunInfo info;
		add(uploadPanel = new ExperimentRunInfoUploadPanel("experimentInfoUploadPanel")
			{
			public void doSubmit(AjaxRequestTarget target, FileUpload uploadedFile, File newFile, ExperimentRunInfo runInfo)
				{
				ArrayList <Integer> colIndicesToPass = new ArrayList<Integer>();
				for (int i = 0; i < checkBoxes.size(); i++)
					colIndicesToPass.add(checkBoxes.get(i).getModelObject() ? Integer.parseInt(colIndices.get(i)) - 1: -1);
			
				try
					{
					setResponsePage(new Ms2DataSetPage("lipidResults", (WebPage) this.getPage(), runInfo.getExpId(),  runInfo.getUploadedFileName(),
						runInfo.getRunDateAsCalendar(), runInfo.getExpMode(), runInfo.getDataNotation(), colIndicesToPass));
					}
				catch (METWorksException e)
					{
					//String msg = e.getMetworksMessage();
					//target.appendJavaScript("alert('" + msg + "');");
					}
				}

			});
		}
			
	
		private DropDownChoice buildColNoDropdown(final String id, String property)
			{
			return new DropDownChoice(id,  new PropertyModel(this, property),  possColIndices)
				{
				public boolean isEnabled()
					{
					return false;
					}
				};
			}
		
		private ExperimentOrDateSearchPanel buildExperimentOrDateSearchPanel(final String id)
			{
			return new ExperimentOrDateSearchPanel(id)
				{
				@Override 
				public void doSubmit(String searchType, String selectedExperiment, Calendar fromDate, Calendar toDate)
					{
					if (searchType == null || searchType.trim().equals(""))
						return;
					
					Boolean searchByRunDate = searchType == null ? false : searchType.equals("Run Date");
					
					try
						{
						if (searchType.equals("Experiment"))
							setResponsePage(new DataSetListPage("searchResults", (WebPage) this.getPage(), selectedExperiment, false));
						else
							setResponsePage(new DataSetListPage("searchResults", (WebPage) this.getPage(), fromDate, toDate, false, searchByRunDate)); 
						}
					catch (Exception e) 
						{
						}
					}
				
				@Override
				public boolean isEnabled()
					{
					return true; //(id.equals("experimentDateSelectorPanel"));
					}
				};
			}
		
		
		private ReportUploadSelectorPanel buildLaunchDataEditPanel(String id)
			{
			return new ReportUploadSelectorPanel(id)
				{
				@Override
				public WebPage getResponsePage(String id, WebPage backPage, String selectedExperiment, 
						String assayId)
					{
					return null;
					}
				
				@Override
				public boolean isEnabled()
					{
					return false;
					}
				};
			}
		
		public String getColIndices(int i)
			{
			return colIndices.get(i);
			}
		
		public List<String>getColIndices()
			{
			return colIndices;
			}
		}
		
			
		





/*
package edu.umich.metworks.web.panels.analysis.lipids;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.security.extensions.markup.html.tabs.SecureTabbedPanel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.metworks.lims.service.ExperimentService;
import edu.umich.metworks.web.METWorksAjaxUpdatingDateTextField;
import edu.umich.metworks.web.METWorksSession;
import edu.umich.metworks.web.model.ExperimentListModel;
import edu.umich.metworks.web.utils.panels.ExperimentOrDateSearchPanel;



public class LipidsMainPanel extends Panel implements IAjaxIndicatorAware
	{
	@SpringBean
	private ExperimentService experimentService;

	List<String> searchTypeOptions = Arrays.asList(new String [] { "Experiment", "Run Date", "Upload Date" });
	List <String> possColIndices = Arrays.asList(new String [] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"});
	List <String> absciexExperiments = experimentService.allExpIdsForAbsciex();
	List <String> possModes = Arrays.asList(new String [] {"Positive", "Negative"});
	List <String> possNotations = Arrays.asList(new String [] {"Combined", "Normalized", "Raw", "Other"});
	
	
	public LipidsMainPanel(String id, SecureTabbedPanel parent) 
		{
		super(id);
		
		add(new FeedbackPanel("feedback"));
		
		LipidUploadForm luf = new LipidUploadForm("lipidUploadForm", parent);
		luf.setMultiPart(true);
		
		luf.add(new ExperimentOrDateSearchPanel("experimentDateSelectorPanel"));
		luf.add(new LaunchDataEditPanel("launchDataEditPanel"));
		luf.add(new LaunchDRCCPanel("launchDRCCPanel"));
		//luf.add(new UploadProgressBar("progress", luf));
		add(luf);
		}

	
	public final class LipidUploadForm extends Form 
		{	
		private FileUploadField fileUploadField;
		private FileUpload fileUploaded;
		private String UPLOAD_FOLDER = "./";
	
		DropDownChoice<String> uploadedExperimentDrop, experimentModeDrop,  dataNotationDrop;
		String uploadedExperiment = "", experimentMode = "", dataNotation = "";
		METWorksAjaxUpdatingDateTextField runDateFld;
		Date uploadedRunDate = new Date();
		
		IndicatingAjaxButton submitButton; // searchButton;
		
		ArrayList <CheckBox> checkBoxes = new ArrayList<CheckBox>();
		ArrayList <Boolean> checkValues = new ArrayList <Boolean> ();
		ArrayList <String> colIndices = new ArrayList<String>();
		

		LipidUploadForm(String id, SecureTabbedPanel parent)
			{
			super(id);
			
			checkBoxes.clear();
			for (int i = 0; i < 7; i++)
				{
				String label = "checkbox" + i;
				CheckBox chk = new CheckBox(label, Model.of(Boolean.TRUE));
				chk.setEnabled(false);
				checkBoxes.add(chk);
				add(chk);
				add(buildColNoDropdown("colIdx" + i, "colIndices." + i));
				}
			
			List<String> values = Arrays.asList(new String [] {"1", "2", "3", "4", "5", "6", "7"});
			for (int i = 0; i < values.size(); i++)
				colIndices.add(values.get(i));
						
			add(uploadedExperimentDrop = buildExperimentDropdown("experimentDropdown", "uploadedExperiment"));
			add(experimentModeDrop = buildModeDropdown("experimentModeDropdown", "experimentMode"));
			add(dataNotationDrop = buildDataNotationDropdown("dataNotationDropdown", "dataNotation"));
			add(runDateFld = grabDateTextField("uploadedRunDateTxt", "uploadedRunDate"));
			add(fileUploadField = buildFileUploadField("fileContents", fileUploaded));
			
			add(submitButton = buildSubmitButton());
			}
	
		
		private DropDownChoice buildModeDropdown(final String id, String property)
			{
			return new DropDownChoice(id, new PropertyModel<String>(this, property), possModes);
			}
		
		private DropDownChoice buildDataNotationDropdown(final String id, String property)
			{
			return new DropDownChoice(id, new PropertyModel<String>(this, property), possNotations);
			}
		
		private METWorksAjaxUpdatingDateTextField grabDateTextField(String id, String property)
			{
			return new METWorksAjaxUpdatingDateTextField(id, new PropertyModel(this, property), "change")
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target) 
					{
					}
				};
			}
		
	
		// SWITCHOVER FROM PRODUCTION TO STAGING
		private boolean userHasUploadPermission()
			{
			String userId = ((METWorksSession) getSession()).getCurrentUserId();
		     
			return userId.equals("U00358");
			}
		
		
		private IndicatingAjaxButton buildSubmitButton()
			{
			return new IndicatingAjaxButton("submitButton")
				{
				public boolean isEnabled()
					{
					return userHasUploadPermission() && (getUploadedExperiment() != null && !uploadedExperiment.trim().equals("")); //(fileUploadField.getFileUpload().getClientFileName() != null); // getSelected().equals("Client Report");
					}
	
				@Override
				protected void onSubmit(AjaxRequestTarget arg0, Form<?> arg1) 
					{
					try
						{
						final FileUpload uploadedFile = fileUploadField.getFileUpload();
					
						if (uploadedFile != null) 
							{
							String uploadedFileName = uploadFile(uploadedFile);
							
							ArrayList <Integer> colIndicesToPass = new ArrayList<Integer>();
							for (int i = 0; i < checkBoxes.size(); i++)
								colIndicesToPass.add(checkBoxes.get(i).getModelObject() ? Integer.parseInt(colIndices.get(i)) - 1: -1);
								
					
						//	searchType = "";
							setOutputMarkupId(true);
							setResponsePage(new Ms2DataSetPage("lipidResults", (WebPage) this.getPage(), uploadedExperiment, uploadedFileName,
									runDateFld.grabValueAsCalendar(), experimentMode, dataNotation, colIndicesToPass));  
							}
						}
					catch (Exception e) 
						{
						} 
					
				//	String msg = "alert('File upload requires database write permissions which have not been granted to your MetLIMS account');";
					//arg0.appendJavascript(msg);
					} 
				};
			}
			
		private FileUploadField buildFileUploadField(String id, FileUpload fileUp)
			{
			FileUploadField fld = new FileUploadField(id, new Model<FileUpload>(fileUp))
				{
				private static final long serialVersionUID = 1L;
			
				public boolean isEnabled()
					{
					return (userHasUploadPermission() && getUploadedExperiment() != null );
					}
				};
				
			fld.setOutputMarkupId(true);
			return fld;
			}

		private DropDownChoice buildExperimentDropdown(final String id, String propertyName)
			{
			uploadedExperimentDrop =  new DropDownChoice(id,  new PropertyModel(this, propertyName), 
				new ExperimentListModel("absciex", experimentService, false));
				
			uploadedExperimentDrop.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForExperimentDrop"));			
			
			return uploadedExperimentDrop;
			}
	
		
		protected void onSubmit()
			{
			System.out.println("Form onSubmit is called");
			}
		
		@Override 
		protected void onError()
			{
			System.out.println("Form onError is called");
			}
	
		
		private String uploadFile(FileUpload uploadedFile)
			{
			File newFile = new File(UPLOAD_FOLDER + uploadedFile.getClientFileName());
			
			if (newFile.exists()) 
				{
				newFile.delete();
				}
			
			try 
				{
				newFile.createNewFile();
				uploadedFile.writeTo(newFile);
				
				info("saved file: " + uploadedFile.getClientFileName());
				} 
			catch (Exception e) 
				{
				System.out.println("Error while uploading file " + uploadedFile.getClientFileName());
				//throw new IllegalStateException("Error");
				}	
			
			return newFile.getName();
			}
			
			
		private DropDownChoice buildColNoDropdown(final String id, String property)
			{
			return new DropDownChoice(id,  new PropertyModel(this, property),  possColIndices)
				{
				public boolean isEnabled()
					{
					return false;
					}
	   
				};
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
			        	case "updateForExperimentDrop" :
			        	
				        	target.add(submitButton);
				        	target.add(fileUploadField);
				        	break;
				        }
		        	}
		        };
			}
		
		public String getUploadedExperiment()
			{
			return uploadedExperiment;
			}
	
		public void setUploadedExperiment(String se)
			{
			uploadedExperiment = se;
			}
		
		public String getExperimentMode()
			{
			return this.experimentMode;
			}
		
		public void setExperimentMode(String mode)
			{
			this.experimentMode = mode;
			}
		
		
		public Date getUploadedRunDate()
			{
			return uploadedRunDate;
			}


		public void setUploadedRunDate(Date selectedRunDate)
			{
			this.uploadedRunDate = selectedRunDate;
			}


		public String getColIndices(int i)
			{
			return colIndices.get(i);
			}
		
		
		public String getDataNotation() 
			{
			return dataNotation;
			}


		public void setDataNotation(String dataNotation) 
			{
			this.dataNotation = dataNotation;
			}
		
		}
			
		@Override
		public String getAjaxIndicatorMarkupId() 
			{
		    return "indicator";
			}
		}


/*
package edu.umich.metworks.web.panels.analysis.lipids;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.security.extensions.markup.html.tabs.SecureTabbedPanel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.metworks.lims.service.ExperimentService;
import edu.umich.metworks.web.METWorksAjaxUpdatingDateTextField;
import edu.umich.metworks.web.METWorksSession;
import edu.umich.metworks.web.model.ExperimentListModel;
import edu.umich.metworks.web.utils.panels.ExperimentOrDateSearchPanel;



public class LipidsMainPanel extends Panel implements IAjaxIndicatorAware
	{
	@SpringBean
	private ExperimentService experimentService;

	List<String> searchTypeOptions = Arrays.asList(new String [] { "Experiment", "Run Date", "Upload Date" });
	List <String> possColIndices = Arrays.asList(new String [] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"});
	List <String> absciexExperiments = experimentService.allExpIdsForAbsciex();
	List <String> possModes = Arrays.asList(new String [] {"Positive", "Negative"});
	List <String> possNotations = Arrays.asList(new String [] {"Combined", "Normalized", "Raw", "Other"});
	
	
	public LipidsMainPanel(String id, SecureTabbedPanel parent) 
		{
		super(id);
		
		add(new FeedbackPanel("feedback"));
		
		LipidUploadForm luf = new LipidUploadForm("lipidUploadForm", parent);
		luf.setMultiPart(true);
		
		luf.add(new ExperimentOrDateSearchPanel("experimentDateSelectorPanel"));
		luf.add(new LaunchDataEditPanel("launchDataEditPanel"));
		luf.add(new LaunchDRCCPanel("launchDRCCPanel"));
		//luf.add(new UploadProgressBar("progress", luf));
		add(luf);
		}

	
	public final class LipidUploadForm extends Form 
		{	
		private FileUploadField fileUploadField;
		private FileUpload fileUploaded;
		private String UPLOAD_FOLDER = "./";
	
		DropDownChoice<String> uploadedExperimentDrop, experimentModeDrop, searchExperimentDrop, searchTypeDrop, dataNotationDrop;
		String uploadedExperiment = "", experimentMode = "", searchExperiment = "All", searchType = "Experiment", dataNotation = "";
		METWorksAjaxUpdatingDateTextField runDateFld;
		Date uploadedRunDate = new Date();
		
		IndicatingAjaxButton submitButton, searchButton;
		
	//	private Date searchFromDate = new Date(), searchToDate = new Date();
	//	METWorksAjaxUpdatingDateTextField searchFromDateFld, searchToDateFld;
		
		WebMarkupContainer wmc, wmc2;
				
		ArrayList <CheckBox> checkBoxes = new ArrayList<CheckBox>();
		ArrayList <Boolean> checkValues = new ArrayList <Boolean> ();
		ArrayList <String> colIndices = new ArrayList<String>();
		

		LipidUploadForm(String id, SecureTabbedPanel parent)
			{
			super(id);
			
			add(wmc = grabMarkupContainer("wmc"));
			//add(wmc2 = grabMarkupContainer("wmc2"));
			
			add(new AjaxCheckBox("showTitles", new PropertyModel<Boolean>(wmc, "visible"))
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target)
					{
					target.add(wmc);
					}	
				});
			
			checkBoxes.clear();
			for (int i = 0; i < 7; i++)
				{
				String label = "checkbox" + i;
				CheckBox chk = new CheckBox(label, Model.of(Boolean.TRUE));
				chk.setEnabled(false);
				checkBoxes.add(chk);
				add(chk);
				add(buildColNoDropdown("colIdx" + i, "colIndices." + i));
				}
			
			List<String> values = Arrays.asList(new String [] {"1", "2", "3", "4", "5", "6", "7"});
			for (int i = 0; i < values.size(); i++)
				colIndices.add(values.get(i));
						
			add(uploadedExperimentDrop = buildExperimentDropdown("experimentDropdown", "uploadedExperiment"));
			add(experimentModeDrop = buildModeDropdown("experimentModeDropdown", "experimentMode"));
			add(dataNotationDrop = buildDataNotationDropdown("dataNotationDropdown", "dataNotation"));
			add(runDateFld = grabDateTextField("uploadedRunDateTxt", "uploadedRunDate"));
			add(fileUploadField = buildFileUploadField("fileContents", fileUploaded));
			
			add(submitButton = buildSubmitButton());
		
			// search panel
			//add(searchTypeDrop = buildSearchTypeDropdown("searchTypeDropdown", "searchType"));
			//wmc.add(searchExperimentDrop = buildSearchExperimentDropdown("searchExperimentDropdown", "searchExperiment"));
		//	wmc2.add(searchFromDateFld = grabDateTextField("searchFromDateTxt", "searchFromDate"));
			//wmc2.add(searchToDateFld = grabDateTextField("searchToDateTxt", "searchToDate"));
		
			//add(searchButton = buildSearchButton(parent));
			}
	
		
		private DropDownChoice buildModeDropdown(final String id, String property)
			{
			return new DropDownChoice(id, new PropertyModel<String>(this, property), possModes);
			}
		
		private DropDownChoice buildDataNotationDropdown(final String id, String property)
			{
			return new DropDownChoice(id, new PropertyModel<String>(this, property), possNotations);
			}
		
		private METWorksAjaxUpdatingDateTextField grabDateTextField(String id, String property)
			{
			return new METWorksAjaxUpdatingDateTextField(id, new PropertyModel(this, property), "change")
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target) 
					{
					}
				};
			}

		private WebMarkupContainer grabMarkupContainer(final String id)
			{
			WebMarkupContainer w =  new WebMarkupContainer(id)
				{
				@Override
				public boolean isVisible()
					{
					if (id.equals("wmc"))
						return (searchType != null && searchType.equals("Experiment"));
					
					return (searchType != null && (searchType.equals("Run Date")  || searchType.equals("Upload Date"))); 
					}
				};
			
			w.setOutputMarkupPlaceholderTag(true);
			
			return w;
			}
		
		private IndicatingAjaxButton buildSearchButton(final SecureTabbedPanel parent)
			{
			return new IndicatingAjaxButton("searchButton")
				{
				public boolean isEnabled()
					{
					return true; //((getSearchType().equals("Experiment") && getSearchExperiment() != null)
					       // || (!getSearchType().trim().equals("")));
					}
	
				@Override
				protected void onSubmit(AjaxRequestTarget arg0, Form<?> arg1) 
					{
					Boolean searchRunDate = getSearchType() == null ? false : getSearchType().equals("Run Date");
					
					try
						{
						setOutputMarkupId(true);
						if (getSearchType().equals("Experiment"))
							setResponsePage(new DataSetListPage("searchResults", (WebPage) this.getPage(), getSearchExperiment(), false));
						else
							setResponsePage(new DataSetListPage("searchResults", (WebPage) this.getPage(), searchFromDateFld.grabValueAsCalendar(), 
							  searchToDateFld.grabValueAsCalendar(), false,  searchRunDate));
						}
					catch (Exception e) 
						{
						}
					}
				};
			}
	
		// SWITCHOVER FROM PRODUCTION TO STAGING
		private boolean userHasUploadPermission()
			{
			String userId = ((METWorksSession) getSession()).getCurrentUserId();
		     
			//return (userId.equals("U00353") || userId.equals("U00351") );
			// production db 
			return userId.equals("U00358");
			}
		
		
		private IndicatingAjaxButton buildSubmitButton()
			{
			return new IndicatingAjaxButton("submitButton")
				{
				public boolean isEnabled()
					{
					return userHasUploadPermission() && (getUploadedExperiment() != null && !uploadedExperiment.trim().equals("")); //(fileUploadField.getFileUpload().getClientFileName() != null); // getSelected().equals("Client Report");
					}
	
				@Override
				protected void onSubmit(AjaxRequestTarget arg0, Form<?> arg1) 
					{
				
					try
						{
						final FileUpload uploadedFile = fileUploadField.getFileUpload();
					
						if (uploadedFile != null) 
							{
							String uploadedFileName = uploadFile(uploadedFile);
							
							ArrayList <Integer> colIndicesToPass = new ArrayList<Integer>();
							for (int i = 0; i < checkBoxes.size(); i++)
								colIndicesToPass.add(checkBoxes.get(i).getModelObject() ? Integer.parseInt(colIndices.get(i)) - 1: -1);
								
					
							searchType = "";
							setOutputMarkupId(true);
							setResponsePage(new Ms2DataSetPage("lipidResults", (WebPage) this.getPage(), uploadedExperiment, uploadedFileName,
									runDateFld.grabValueAsCalendar(), experimentMode, dataNotation, colIndicesToPass));  
							}
						}
					catch (Exception e) 
						{
						} 
					
				//	String msg = "alert('File upload requires database write permissions which have not been granted to your MetLIMS account');";
					//arg0.appendJavascript(msg);
					} 
				};
			}
			
		private FileUploadField buildFileUploadField(String id, FileUpload fileUp)
			{
			FileUploadField fld = new FileUploadField(id, new Model<FileUpload>(fileUp))
				{
				private static final long serialVersionUID = 1L;
			
				public boolean isEnabled()
					{
					return (userHasUploadPermission() && getUploadedExperiment() != null );
					}
				};
				
			fld.setOutputMarkupId(true);
			return fld;
			}

		private DropDownChoice buildSearchExperimentDropdown(String id, String propertyName)
			{
			searchExperimentDrop =  new DropDownChoice(id,  new PropertyModel(this, propertyName), 
				new ExperimentListModel("absciex", experimentService, true))
				{
				@Override
				public boolean isVisible()
					{
					return searchType.equals("Experiment");
					}
				};
				
			searchExperimentDrop.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForSearchExperimentDrop"));			
			searchExperimentDrop.add(this.buildStandardFormComponentUpdateBehavior("onload", "updateForSearchExperimentDrop"));			
			
			return searchExperimentDrop;
			}
	
		private DropDownChoice buildSearchTypeDropdown(String id, String propertyName)
			{
			searchTypeDrop =  new DropDownChoice(id,  new PropertyModel(this, propertyName), searchTypeOptions);
			searchTypeDrop.add(this.buildStandardFormComponentUpdateBehavior("onload", "updateForSearchTypeDrop"));			
			searchTypeDrop.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForSearchTypeDrop"));			
			
			return searchTypeDrop;
			}
		
		private DropDownChoice buildExperimentDropdown(final String id, String propertyName)
			{
			uploadedExperimentDrop =  new DropDownChoice(id,  new PropertyModel(this, propertyName), 
				new ExperimentListModel("absciex", experimentService, false));
				
			uploadedExperimentDrop.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForExperimentDrop"));			
			
			return uploadedExperimentDrop;
			}
	
		
		protected void onSubmit()
			{
			System.out.println("Form onSubmit is called");
			}
		
		@Override 
		protected void onError()
			{
			System.out.println("Form onError is called");
			}
	
		
		private String uploadFile(FileUpload uploadedFile)
			{
			File newFile = new File(UPLOAD_FOLDER + uploadedFile.getClientFileName());
			
			if (newFile.exists()) 
				{
				newFile.delete();
				}
			
			try 
				{
				newFile.createNewFile();
				uploadedFile.writeTo(newFile);
				
				info("saved file: " + uploadedFile.getClientFileName());
				} 
			catch (Exception e) 
				{
				System.out.println("Error while uploading file " + uploadedFile.getClientFileName());
				//throw new IllegalStateException("Error");
				}	
			
			return newFile.getName();
			}
			
			
		private DropDownChoice buildColNoDropdown(final String id, String property)
			{
			return new DropDownChoice(id,  new PropertyModel(this, property),  possColIndices)
				{
				public boolean isEnabled()
					{
					return false;
					}
	   
				};
			}
		
		/*
		private DateTextField buildDateTextField(String id, String property)
			{
			DateTextField dateTextField = new DateTextField(id, new PropertyModel<Date>(
		            this, property ), new StyleDateConverter("S-", true))
			{
				@Override
				public boolean isVisible()
				{
				return !searchType.equals("Experiment");	
				}
			};
			
		    DatePicker datePicker = new DatePicker();
		    datePicker.setShowOnFieldClick(true);
		    dateTextField.add(datePicker);
		    
		    return dateTextField;    
			}
		*//*
		private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(final String event,  final String response)
			{
			return new AjaxFormComponentUpdatingBehavior(event)
		        {
		        @Override
		        protected void onUpdate(AjaxRequestTarget target)
		        	{
		        	switch (response)
			        	{
			        	case "updateForExperimentDrop" :
			        	
				        	//System.out.println("Selected experiment updated to " + uploadedExperiment);
				        	target.add(submitButton);
				        	target.add(fileUploadField);
				        	break;
					   
			        	case "updateForSearchExperimentDrop" :
			        	
				        	//System.out.println("Searched experiment updated to " + searchExperiment);
				        	target.add(searchButton);
				        	break;
				        	
				        	
			        	case "updateForSearchTypeDrop" :
			        	
				        	//System.out.println("Searched experiment updated to " + searchExperiment);
				        	target.add(searchExperimentDrop);
				        	target.add(searchFromDateFld);
				        	target.add(wmc);
				        	target.add(wmc2);
				        	target.add(searchToDateFld);
			        	
			        	break;	
				        }
		        	}
		        };
			}
		
		public String getUploadedExperiment()
			{
			return uploadedExperiment;
			}
	
		public void setUploadedExperiment(String se)
			{
			uploadedExperiment = se;
			}
		
		public String getExperimentMode()
			{
			return this.experimentMode;
			}
		
		public void setExperimentMode(String mode)
			{
			this.experimentMode = mode;
			}
		
		public Date getUploadedRunDate()
			{
			return uploadedRunDate;
			}

		public void setUploadedRunDate(Date selectedRunDate)
			{
			this.uploadedRunDate = selectedRunDate;
			}
		
		
		public String getSearchType()
			{
			return searchType;
			}

		public void setSearchType(String searchType)
			{
			this.searchType = searchType;
			}

		public Date getSearchFromDate()
			{
			return searchFromDate;
			}


		public void setSearchFromDate(Date searchFromDate)
			{
			this.searchFromDate = searchFromDate;
			}


		public Date getSearchToDate()
			{
			return searchToDate;
			}

		public void setSearchToDate(Date searchToDate)
			{
			this.searchToDate = searchToDate;
			}

		public String getColIndices(int i)
			{
			return colIndices.get(i);
			}
		
		
		public String getSearchExperiment()
			{
			return searchExperiment;
			}

		public void setSearchExperiment(String sExperiment)
			{
			searchExperiment = sExperiment;
			}


		public String getDataNotation() 
			{
			return dataNotation;
			}


		public void setDataNotation(String dataNotation) 
			{
			this.dataNotation = dataNotation;
			}
		
		}
			
		@Override
		public String getAjaxIndicatorMarkupId() 
			{
		    return "indicator";
			}
		
	
	@Override
	protected void configureResponse(WebResponse response) 
		{
	    super.configureResponse(response);
	    response.setHeader("Cache-Control","no-cache, max-age=0, must-revalidate, no-store");
		}
	}
*/
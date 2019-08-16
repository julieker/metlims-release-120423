package edu.umich.brcf.metabolomics.panels.lipidshome.browse;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//Ms2DataSetPanel.java
//Written by Jan Wigginton 02/10/15,  Rewritten 05/01/15
//
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.domain.Ms2PeakSet;
import edu.umich.brcf.metabolomics.layers.service.Ms2PeakSetService;
import edu.umich.brcf.metabolomics.panels.lipidshome.browse.Ms2DataSetHandler;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.panels.utilitypanels.ClickablePropertyColumn;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.METWorksException;
import edu.umich.brcf.shared.util.interfaces.IWriteableSpreadsheet;
import edu.umich.brcf.shared.util.interfaces.IWriteableTextData;
import edu.umich.brcf.shared.util.sheetwriters.LipidsDataSetWriter;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;
import edu.umich.brcf.shared.util.widgets.AjaxBackButton;
import edu.umich.brcf.shared.util.widgets.AjaxExcelDownloadLink;
import edu.umich.brcf.shared.util.widgets.TextDownloadLink;



public class Ms2DataSetPanel extends Panel 
	{
	@SpringBean
	Ms2PeakSetService ms2PeakSetService;
	
	private Ms2DataSetHandler dataHandler;
	private Boolean dataAlreadyLoaded;
	
	ModalWindow modal;
	//private Set<Ms2PeakSet> selected = new HashSet<Ms2PeakSet>();
	
	public Ms2DataSetPanel(String id) throws METWorksException
		{
		this(id, null, "", "", null,"", "", null);
		}
	
	public Ms2DataSetPanel(String id, WebPage backPage, String dataSetId)
		{
		super(id);
		Injector.get().inject(this);
		dataAlreadyLoaded = true;
		dataHandler = new Ms2DataSetHandler(dataSetId);
		displayResults("N/A", backPage);
		}
	
	public Ms2DataSetPanel(String id, WebPage backPage, String expId, String inputFileName, Calendar runDate, String ionMode,
	 String dataNotation, ArrayList<Integer> colIndices) throws METWorksException
		{
		super(id);
		Injector.get().inject(this);
	
		try
			{
			dataAlreadyLoaded = false;
			dataHandler = new Ms2DataSetHandler(expId, inputFileName, runDate,ionMode, dataNotation, colIndices, ((MedWorksSession) getSession()).getCurrentUserId());
			displayResults(inputFileName, backPage);
			}
		catch (Exception e) 
			{
			throw new METWorksException(e.getMessage());
			}
		}
	
	
	private void displayResults(String inputFileName, WebPage backPage)
		{
		modal = ModalCreator.createScalingModalWindow("modal1", 1.0, 0.7, (MedWorksSession) getSession());		
		add(modal);
		
		Label fileLabel, fileLabelPrefix; 
		add(fileLabelPrefix = new Label("fileLabelPrefix", "Data File"));
		add(fileLabel = new Label("fileLabel", inputFileName));
		fileLabel.setVisible(!dataAlreadyLoaded);
		fileLabelPrefix.setVisible(!dataAlreadyLoaded);
		
		add(new Label("experimentLabel", "Peak Areas by Compound for " + dataHandler.getDataSet().getExpId()));
		add(new Label("ionMode", dataHandler.getDataSet().getIonMode()));
		add(new Label("dataNotation", dataHandler.getDataSet().getDataNotation()));
		add(new Label("dateLabel",  DateUtils.dateStrFromCalendar("MM/dd/yy" , dataHandler.getDataSet().getRunDate())));
		String uploadDateStr = dataAlreadyLoaded ? dataHandler.getDataSet().getUploadDateAsStr() : new Date().toString();
		add(new Label("uploadLabel", uploadDateStr));
		
		DefaultDataTable dataTable = buildDataTable("table",  dataHandler.tableColumnLabels, dataHandler.tableColumnLabels);
		add(dataTable);	
		
		IWriteableTextData writer = new Ms2DataSetWriter(dataHandler);
		TextDownloadLink fileDownloadButton = new TextDownloadLink("downloadData", writer);
		add(fileDownloadButton);
		fileDownloadButton.setOutputMarkupId(true);

		add(new AjaxBackButton("backButton", backPage));
		
		IndicatingAjaxLink persistButton;
		add(persistButton = buildPersistButton("persistData"));
		persistButton.setOutputMarkupId(true);
		
		//IndicatingAjaxLink reportButton;
		//add(reportButton = buildReportButton("reportButton"));
		//reportButton.setOutputMarkupId(true);
		}	
	
	
protected AjaxExcelDownloadLink buildExcelDownloadLink(String linkId, final Ms2DataSetHandler handler)
		{
		//LipidsDataSetWriter
		IWriteableSpreadsheet writer = new LipidsDataSetWriter(handler);
		
		AjaxExcelDownloadLink link = new AjaxExcelDownloadLink(linkId, writer)
			{
			@Override
			public boolean isVisible()
				{
				return true; //!worklist.getItems().isEmpty();
				}

			@Override
			public boolean validate(AjaxRequestTarget target, IWriteableSpreadsheet report)
				{
				System.out.println("Validating");
				return true;
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

protected TextDownloadLink buildTextDownloadLink(String linkId, final Ms2DataSetHandler handler)
	{
	
	IWriteableTextData writer = new Ms2DataSetWriter(handler);
	
	TextDownloadLink link = new TextDownloadLink(linkId, writer);

	
	return link;
	}


	public DefaultDataTable <Ms2PeakSet, String>  buildDataTable(String id, List<String> colNames, List<String> varNames)
		{	
		SortableMs2ResultDataProvider cmpdProvider = new SortableMs2ResultDataProvider(dataHandler.getDataSet().getPeakSets());
		List<IColumn<?, ?>> columns = new ArrayList<IColumn<?, ?>>();

		ClickablePropertyColumn<Ms2PeakSet, ?> clickablePeakSet;
		if (dataAlreadyLoaded)
			columns.add(clickablePeakSet = buildClickablePeakSetCol());
		
		ClickablePropertyColumn<Ms2PeakSet, ?> clickableLipid;
		columns.add(clickableLipid = buildClickableLipidCol());
		
		if (dataHandler.hasStartMass)
			columns.add(getPropertyColumn("Start_Mass", "startMass", this.dataHandler.hasStartMass ? "startMass" : ""));
		
		if (dataHandler.hasEndMass)
			columns.add(getPropertyColumn("End_Mass", "endMass", dataHandler.hasEndMass ? "endMass" : ""));
		
		if (dataHandler.hasRt)
			columns.add(getPropertyColumn("Expected_RT", "expectedRt", dataHandler.hasRt ?  "expectedRt" : ""));
		
		columns.add(getPropertyColumn("Lipid_Class", "lipidClass", "lipidClass"));
		columns.add(getPropertyColumn("Known_Status", "knownStatus", "knownStatus"));
		
		for (int j = 0; j < colNames.size();  j++)
			{
			String propertyExpression =  "samplePeaks." + j + ".peakArea";
			columns.add(getPropertyColumn(dataHandler.tableColumnLabels.get(j) + "", propertyExpression, propertyExpression));
			}
		
		DefaultDataTable table = new DefaultDataTable(id, columns, cmpdProvider, 8000);
		
		//table.addTopToolbar(new NavigationToolbar(table));
		return table;
		}
	
	
	
	public ClickablePropertyColumn <Ms2PeakSet, ?> buildClickableLipidCol()
			{
			return new ClickablePropertyColumn<Ms2PeakSet, String> (Model.of("Lipid/Compound_Name"), "lipidName", "lipidName")
			{
			@Override
			protected void onClick(final IModel<Ms2PeakSet> clicked, AjaxRequestTarget target)
				{
				setModalDimensions(modal, 0.9, 0.8);
				
				modal.setPageCreator(new ModalWindow.PageCreator()
					{
				     public Page createPage()
	                 	{
	                	return ((Page) (new LipidBlastMatchesPage(getPage(), modal, clicked)));	
	                 	}
					});
				
				modal.show(target);
				}
			
			@Override
			public String getCssClass() { return "borderColumn2"; }
			
			@Override
			public Component getHeader(String componentId) 
				{
				Component header=super.getHeader(componentId);
				header.add(new AttributeModifier("style","color : blue; width:600px; text-align : middle")); 
				return header;
				}
			};
		}
	
	
	public void setModalDimensions(ModalWindow modal, double pctWidth, double pctHeight)
		{
		int pageHeight = 800; //((MedWorksSession) getSession()).getClientProperties().getBrowserHeight();
		modal.setInitialHeight(((int) Math.round(pageHeight * pctHeight)));
		int pageWidth = 1000; //((MedWorksSession) getSession()).getClientProperties().getBrowserWidth();
		modal.setInitialWidth(((int) Math.round(pageWidth * pctWidth)));
		}
	
	
	public ClickablePropertyColumn <Ms2PeakSet, ?> buildClickablePeakSetCol()
		{
		return new ClickablePropertyColumn<Ms2PeakSet, String> (Model.of("Peak_Set"), "peakSetId", "peakSetId")
			{
			@Override
			protected void onClick(final IModel<Ms2PeakSet> clicked, AjaxRequestTarget target)
				{
				setModalDimensions(modal, 0.5, 0.8);
				
				modal.setPageCreator(new ModalWindow.PageCreator()
					{
					public Page createPage()
						{
						return new PeakSetPage("id", clicked.getObject().getPeakSetId(), modal, dataHandler.sampleHashMap);	
						}
						});
					modal.show(target);
					}
	
			@Override
			public String getCssClass() { return "borderColumn2"; }
			
			@Override
			public Component getHeader(String componentId) 
				{
				Component header=super.getHeader(componentId);
				header.add(new AttributeModifier("style","color : blue; width:300px; text-align : middle")); 
				return header;
				}
			};
		}
	
	
	private <T, S> PropertyColumn<?, ?> getPropertyColumn(final String label, final String  propertyExpression, String sortVar)
		{
		if (sortVar.equals(""))
			return new PropertyColumn(new Model<String>(label), propertyExpression)
			{
			@Override
			public String getCssClass()
				{
				if (propertyExpression.startsWith("Lipid"))
					return "borderColumn2";
				
				if (this.toString().startsWith("K"))
					return "borderColumn2";
				
				return "borderColumn";
				}
		
			@Override
			public Component getHeader(String componentId) 
				{
				Component header=super.getHeader(componentId);
				header.add(new AttributeModifier("style","color : blue; width:600px; text-align : middle")); 
				return header;
				}
			};
	
	return new PropertyColumn(new Model<String>(label), propertyExpression, sortVar)
		{
		@Override
		public String getCssClass() { return label.startsWith("Lipid") ?  "borderColumn2" :  "borderColumn";   }
		
		@Override
		public Component getHeader(String componentId) 
			{
			Component header=super.getHeader(componentId);
			header.add(new AttributeModifier("style","width: 600px; font-weight ; 700;  background : white; text-align : center; padding : 0px;")); 
			return header;
			}
		};	
		}

	
	private IndicatingAjaxLink buildPersistButton(String id)
		{
		return new IndicatingAjaxLink(id)
			{
			public boolean isVisible() { return !dataAlreadyLoaded; }
			
			@Override
			public void onClick(AjaxRequestTarget target) 
				{
				String alertString = "alert('Data for " + dataHandler.getDataSet().getExpId() + " has been written to the database');";
				target.appendJavaScript(alertString);
				dataHandler.persistToDatabase();
				}
			@Override // issue 464
			public MarkupContainer setDefaultModel(IModel model) 
			    {
				// TODO Auto-generated method stub
				return this;
			    }
			};
		}


	private IndicatingAjaxLink buildReportButton(String id)
		{
		return new IndicatingAjaxLink(id)
			{
			@Override
			public boolean isVisible() { return dataAlreadyLoaded; }
			
			@Override
			public void onClick(AjaxRequestTarget target) 
				{
				String alertString = "alert('This will go to a data report preview page');";
				target.appendJavaScript(alertString);
				
				List selectedIds =  new ArrayList <String>();
				setResponsePage(new DataReportPage("reportPage", (WebPage) this.getPage(), "dataSetId", selectedIds));
				}
			@Override // issue 464
			public MarkupContainer setDefaultModel(IModel model) 
			    {
				// TODO Auto-generated method stub
				return this;
			    }
			};
		}


	public Ms2DataSetHandler getDataHandler()
		{
		return dataHandler;
		}
	}










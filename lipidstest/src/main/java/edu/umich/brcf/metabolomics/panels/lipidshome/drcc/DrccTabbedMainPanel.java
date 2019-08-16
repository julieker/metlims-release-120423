////////////////////////////////////
// DrccTabbedMainPanel.java
// Written by Jan Wigginton 06/01/15
/////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.lipidshome.drcc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import edu.umich.brcf.shared.util.widgets.AjaxBackButton;
import edu.umich.brcf.shared.util.widgets.ExcelDownloadLink;


public class DrccTabbedMainPanel extends Panel
	{
	List <AbstractTab>tabs = new ArrayList<AbstractTab>();
	int tabOrder=0;
	
	String selectedExperiment, selectedMode;
	DrccReport report; 
	Date analysisDate;
	
	public DrccTabbedMainPanel(String id, String se, WebPage backPage, String selectedMode, Date analysisDate)
		{
		super(id);

		this.selectedExperiment = se;
		this.selectedMode = selectedMode;
		this.analysisDate = analysisDate;
		
		setDefaultModel(new Model("tabpanel1"));
		// issue 464
		add(new TabbedPanel("drccTabs", makeTabs(backPage))
				.add(new AttributeModifier("class",  DrccTabbedMainPanel.this.getDefaultModel())));

		report = new DrccReport(selectedExperiment, selectedMode, analysisDate);
		add(new ExcelDownloadLink("reportButton", report));
		
		add(new AjaxBackButton("backButton", backPage));
		}
	
	
	private List makeTabs(final WebPage backPage) 
		{
		List tabs = new ArrayList();
	
		tabs.add(new AbstractTab(new Model("Project Info")) 
			{
			public Panel getPanel(String panelId) 
				{
				return new DrccProjectInfoPanel(panelId, selectedExperiment, backPage, report.projectInfo);
				}
			});
			
		tabs.add(new AbstractTab(new Model("Study Info")) 
			{
			public Panel getPanel(String panelId) 
				{
				return new DrccStudyInfoPanel(panelId, selectedExperiment, backPage, report.studyInfo);
				}
			});
		
		tabs.add(new AbstractTab(new Model("Study Design")) 
			{
			public Panel getPanel(String panelId) 
				{
				return new DrccStudyDesignPanel(panelId, selectedExperiment, backPage, report.studyDesignInfoSet);
				}
			});
			
		
		tabs.add(new AbstractTab(new Model("Subject Info")) 
			{
			public Panel getPanel(String panelId) 
				{
				return new DrccSubjectInfoPanel(panelId, selectedExperiment, backPage, report.subjectInfoSet);
				}
			});
		
		
		tabs.add(new AbstractTab(new Model("Collection")) 
			{
			public Panel getPanel(String panelId) 
				{
				return new DrccCollectionPanel(panelId, selectedExperiment, backPage, report.collectionInfo);
				}
			});
	
	
		tabs.add(new AbstractTab(new Model("Sample Prep")) 
			{
			public Panel getPanel(String panelId) 
				{
				return new DrccSamplePrepPanel(panelId, selectedExperiment, backPage, report.samplePrepInfo);
				}
			});

		tabs.add(new AbstractTab(new Model("Analysis")) 
			{
			public Panel getPanel(String panelId) 
				{
				return new DrccAnalysisPanel(panelId, selectedExperiment, backPage, report.analysisInfo);
				}
			});
		
		tabs.add(new AbstractTab(new Model("MS Info")) 
			{
			public Panel getPanel(String panelId) 
				{
				return new DrccMsInfoPanel(panelId, selectedExperiment, backPage, report.msInfo);
				}
			});
	
		return tabs;
		}
	}
	
	
	
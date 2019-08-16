// DrccStudyDesignPanel.java
// Written by Jan Wigginton June 2015

package edu.umich.brcf.metabolomics.panels.lipidshome.drcc;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;

import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;


public class DrccStudyDesignPanel extends Panel 
	{
	private String expId;
	
	DrccStudyDesignInfoSet studyDesignInfoSet;
	
	public DrccStudyDesignPanel(String id, String selectedExperiment,  WebPage backPage,
			DrccStudyDesignInfoSet infoSet) 
		{
		super(id);
		
		expId = selectedExperiment;
		
		studyDesignInfoSet = infoSet;
	
		String tabTitle = studyDesignInfoSet.getMode().equals("drcc") ? "C.  Study Design" : "Study Design for " + selectedExperiment;
		add(new Label("tabTitle", tabTitle));
		String header = studyDesignInfoSet.getMode().equals("drcc") ? "Metabolomics Core - DRCC Data Reporting Tool" : "";
		
		add(new Label("header", header)
			{
			public boolean isVisible()
				{
				return studyDesignInfoSet.getMode().equals("drcc");
				}
			});
		
		add(new Label("factor1Label", new PropertyModel<String>(studyDesignInfoSet, "factorLabels.0")));
		add(new Label("factor2Label", new PropertyModel<String>(studyDesignInfoSet, "factorLabels.1")));
		add(new Label("factor3Label", new PropertyModel<String>(studyDesignInfoSet, "factorLabels.2")));
		add(new Label("factor4Label", new PropertyModel<String>(studyDesignInfoSet, "factorLabels.3")));
		add(new Label("factor5Label", new PropertyModel<String>(studyDesignInfoSet, "factorLabels.4")));
	
		Label lbl2 = new Label("userDefinedSampleTypeLabel", "Sample Type")
		{
		public boolean isVisible()
			{
			return !studyDesignInfoSet.getMode().equals("drcc");
			}
		};
		
		lbl2.setOutputMarkupId(true);
		add(lbl2);

		Label lbl3 = new Label("userSampleIdLabel", "Researcher Sample Id")
		{
		public boolean isVisible()
			{
			return !studyDesignInfoSet.getMode().equals("drcc");
			}
		};
		
		lbl3.setOutputMarkupId(true);
		add(lbl3);

		
		add(new ListView("subjectInfo", new PropertyModel<List<DrccStudyDesignInfoItem>>(studyDesignInfoSet, "infoFields"))
			{
			@Override
			protected void populateItem(ListItem listItem) 
				{
				final DrccStudyDesignInfoItem info = (DrccStudyDesignInfoItem) listItem.getModelObject();
				
				listItem.add(new Label("subjectName", new PropertyModel<String>(info, "subjectName")));
				
				// Tweak for Tanu
				listItem.add(new Label("sampleId",  new PropertyModel<String>(info, "sampleId"))
					{
					@Override 
					public boolean isVisible()
						{
						return false; //info.mode.equals("drcc");
						}
					});
				
				listItem.add(new Label("researcherSampleName", new PropertyModel<String>(info, "researcherSampleName")));
				
				for (int i = 0; i < studyDesignInfoSet.nFactorsTracked; i++)
					listItem.add(new Label("factor" + i, new PropertyModel<String>(info, "factorValues." + i)));
				
				Label lbl = new Label("userDefinedSampleType", new PropertyModel<String>(info, "userDefinedSampleType"))
					{
					public boolean isVisible()
						{
						return !info.mode.equals("drcc");
						}
					};
					
					Label lbl2 = new Label("userSampleId", new PropertyModel<String>(info, "userSampleId"))
					{
					public boolean isVisible()
						{
						return !info.mode.equals("drcc");
						}
					};	
					
				lbl.setOutputMarkupId(true);
				lbl2.setOutputMarkupId(true);
				
				listItem.add(lbl);
				listItem.add(lbl2);
				
				listItem.add(OddEvenAttributeModifier.create(listItem));
				}	
			});
		}
	
	public void setExperiment(Experiment experiment)
		{
		this.expId = experiment == null ? "EX00417" : experiment.getExpID(); 
		
		this.studyDesignInfoSet = new DrccStudyDesignInfoSet(expId);
		}
	}



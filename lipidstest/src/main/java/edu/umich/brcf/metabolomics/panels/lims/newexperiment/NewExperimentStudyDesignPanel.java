//ExperimentStudyDesignPanel.java
//Written by Jan Wigginton June 2015

package edu.umich.brcf.metabolomics.panels.lims.newexperiment;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import edu.umich.brcf.metabolomics.panels.lims.newexperiment.datacollectors.StudyDesignInfoItem;
import edu.umich.brcf.metabolomics.panels.lims.newexperiment.datacollectors.StudyDesignInfoSet;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.datacollectors.ExperimentalDesign;


public class NewExperimentStudyDesignPanel extends Panel 
	{
	private String expId;
	private StudyDesignInfoSet studyDesignInfoSet;
	private Experiment experiment;
	
	public NewExperimentStudyDesignPanel(String id, Experiment experiment,  WebPage backPage,
			NewExperimentPanel parent) 
		{
		super(id);
	
		this.experiment = experiment;
		expId = (experiment == null ? "" : experiment.getExpID());
		
		if (parent.getStudyDesignSet() == null)
			parent.setStudyDesignSet(new StudyDesignInfoSet(expId, "drcc", true));
		
		this.studyDesignInfoSet = parent.getStudyDesignSet();
		// issue 443
		List<String> factors = this.studyDesignInfoSet.getFactorLabels();
		int nF = factors  == null ? 0 : factors.size();
		final int nFactors = Math.min(nF, ExperimentalDesign.SUBMISSION_SHEET_NFACTORS);
		final int defaultWidth = 90/(nFactors + 3);
		int otherWidth = 10 + (90 - (nFactors + 3) * defaultWidth);
		// Issue 443
		add(buildSampleColumn("internalSampleId", "Sample ID", otherWidth));
		add(buildSampleColumn("sampleName", "Sample Name", defaultWidth));
		add(buildSampleColumn("subjectId", "Subject ID", defaultWidth));
		
		add(new Label("expId", getExperimentName()));
		for (int i = 0; i < ExperimentalDesign.SUBMISSION_SHEET_NFACTORS; i++)
			add(buildFactorColumn(i, studyDesignInfoSet, (int) (i < nFactors ? defaultWidth : 0), i < nFactors));
		add(buildSampleColumn("sampleType", "Sample Type", defaultWidth));
		add(new ListView("subjectInfo", new PropertyModel<List<StudyDesignInfoItem>>(studyDesignInfoSet, "infoFields"))
			{
			@Override
			protected void populateItem(ListItem listItem) 
				{
				final StudyDesignInfoItem info = (StudyDesignInfoItem) listItem.getModelObject();				
				listItem.add(new Label("subjectName", new PropertyModel<String>(info, "subjectName")));				
				// issue 443
				Label researcherSampleLabel = new Label("researcherSampleId", new PropertyModel<String>(info, "shortSampleName"));
			    researcherSampleLabel.add(AttributeModifier.append("title", info.getSampleName()));
				//listItem.add(new Label("researcherSampleId", new PropertyModel<String>(info, "shortSampleName")));
				listItem.add(researcherSampleLabel);
		
				listItem.add(new Label("sampleId", new PropertyModel<String>(info, "sampleId")));						
				for (int i = 0; i < nFactors; i++)
					listItem.add(new Label("factor" + i, new PropertyModel<String>(info, "factorValues." + i)));
				
				for (int i = nFactors; i < ExperimentalDesign.SUBMISSION_SHEET_NFACTORS; i++)
					listItem.add(new Label("factor" + i, " ").setVisible(false));
					
				listItem.add(new Label("userDefinedSampleType", new PropertyModel<String>(info, "userDefinedSampleType")));
				
				
				listItem.add(OddEvenAttributeModifier.create(listItem));
				}	
			});
		}
	
	// Issue 443
	public Label buildFactorColumn(int i, final StudyDesignInfoSet info, final Integer width, Boolean visibility)
		{
		Label lbl = new Label("factorLabels." + i, new PropertyModel<String>(info, "factorLabels." + i))
			{
			@Override
			protected void onComponentTag(ComponentTag tag)
	    		{
	    		super.onComponentTag(tag);
	    		tag.put("style", "width : " + width + "%; word-wrap:break-word");
	    		}
			};
			
		lbl.setVisible(visibility);
		lbl.setOutputMarkupId(true);
		return lbl;
		}
	
	// issue 443
	public Label buildSampleColumn(String name,  String title, final Integer width)
		{
		Label lbl = new Label(name, new Model<String>(title))
			{
			@Override
			protected void onComponentTag(ComponentTag tag)
	    		{
	    		super.onComponentTag(tag);
	    		tag.put("style", "width : " + width + "%");
	    		}
			};
			
		return lbl;
		}
	
	public Experiment getExperiment()
		{
		return experiment;
		}
	
	
	private String getExperimentName() 
		{
		if (getExperiment()!=null)
			return (getExperiment().getExpName()+" ("+experiment.getExpID()+")");
		
		return "";
		}
	
	
	public void setExperiment(Experiment experiment)
		{
		this.expId = experiment == null ? "EX00417" : experiment.getExpID(); 
		this.studyDesignInfoSet = new StudyDesignInfoSet(expId);
		}
	}



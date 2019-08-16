////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  WorklistAbsciexPanel.java
//  Written by Jan Wigginton
//  February 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import org.apache.wicket.markup.html.list.ListItem;

public class WorklistABSciexPanel extends BaseWorklistPanel
	{
	public WorklistABSciexPanel()
		{
		this("");
		}
	
	public WorklistABSciexPanel(String id)
		{
		this(id, null);
		}

	public WorklistABSciexPanel(String id, WorklistSimple worklist)
		{
		super(id, worklist);
		}

	@Override
	protected void initListItem(ListItem listItem, WorklistItemSimple item)
		{
		item.getSampleWorklistLabel();
		listItem.add(buildRedoCheckbox("selected", listItem, getContainer()));
		listItem.add(WorklistFieldBuilder.buildIntegerWorklistField("randomIdx", item, "randomIdx")); 
		listItem.add(WorklistFieldBuilder.buildStringWorklistField("rackCode",item, "rackCode"));
		listItem.add(WorklistFieldBuilder.buildStringWorklistField("rackPosition", item, "rackPosition"));
		listItem.add(WorklistFieldBuilder.buildStringWorklistField("plateCode", item, "plateCode"));
		listItem.add(WorklistFieldBuilder.buildStringWorklistField("platePosition", item, "platePosition"));
		listItem.add(WorklistFieldBuilder.buildStringWorklistField("vialPosition", item, "vialPosition"));
		listItem.add(WorklistFieldBuilder.buildStringWorklistField("methodFileName", item, "methodFileName"));
		listItem.add(WorklistFieldBuilder.buildStringWorklistField("outputFileDir", item, "sampleWorklistLabel"));
		listItem.add(WorklistFieldBuilder.buildStringWorklistField("outputFileName", item, "outputFileName"));
		listItem.add(WorklistFieldBuilder.buildDoubleWorklistField("injectionVolume", item, "injectionVolume"));
		listItem.add(buildCommentsButton("notes", item));
		}
	}

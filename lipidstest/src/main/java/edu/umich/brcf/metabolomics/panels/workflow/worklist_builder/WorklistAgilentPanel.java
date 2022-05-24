////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  WorklistAgilentPanel.java
//  Written by Jan Wigginton
//  February 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;


public class WorklistAgilentPanel extends BaseWorklistPanel
	{
	TextField <String> nameTextField;

	public WorklistAgilentPanel()
		{
		this("id");
		}
	
	public WorklistAgilentPanel(String id) 
		{
		this(id, null);
		}
	
	
	public WorklistAgilentPanel(String id, WorklistSimple w) 
		{
		super(id, w);
		}

	@Override
	// issue 229
	protected void initListItem(ListItem listItem, WorklistItemSimple item)
		{
		listItem.add(buildRedoCheckbox("selected", listItem, getContainer()));
		//issue 410
		item.setOutputFileNameWithDir(item.getGroup().getParent().getIsCustomDirectoryStructure() ?  item.grabDataFileWithCustomDirectory() : item.getOutputFileNameWithDir());
		listItem.add(WorklistFieldBuilder.buildStringWorklistField("sampleName", item, "sampleName"));
		listItem.add(WorklistFieldBuilder.buildStringWorklistField("samplePosition", item, "samplePosition"));
		listItem.add(WorklistFieldBuilder.buildDoubleWorklistFieldAgilent("injectionVolume", item, "injectionVolume"));
		listItem.add(WorklistFieldBuilder.buildStringWorklistField("methodFileName", item, "methodFileName"));
		listItem.add(WorklistFieldBuilder.buildStringWorklistField("overrideMethod", item, "overrideMethod"));
		// issue 229 
		listItem.add(WorklistFieldBuilder.buildStringWorklistField("outputFileName", item, "outputFileNameWithDir"));
		listItem.add(WorklistFieldBuilder.buildStringWorklistField("commentDisplay", item, "comments"));
		listItem.add(buildCommentsButton("notes", item));
		}
	}

	
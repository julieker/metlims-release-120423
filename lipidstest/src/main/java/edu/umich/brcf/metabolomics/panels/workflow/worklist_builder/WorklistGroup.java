////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  WorklistGroup.java
//  Written by Jan Wigginton
//  July 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.io.Serializable;



public class WorklistGroup implements Serializable
	{
	protected String experimentId = null;
	protected WorklistSimple parent;

	public WorklistGroup()
		{
		parent = null;
		}

	public WorklistGroup(WorklistSimple p, String eid)
		{
		experimentId = eid;
		parent = p;
		}

	public String getExperimentId()
		{
		return experimentId;
		}

	public void setExperimentId(String eid)
		{
		experimentId = eid;
		}

	public WorklistSimple getParent()
		{
		return parent;
		}

	public void setParent(WorklistSimple w)
		{
		parent = w;
		}
	}

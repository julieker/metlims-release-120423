/////////////////////////////////////
// ExperimentListModel.java 
// Written by Jan Wigginton, July 2015
//////////////////////////////////////

package edu.umich.brcf.shared.util.model;

import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.ExperimentService;

public class ExperimentListModel extends LoadableDetachableModel<List<String>>
	{
	@SpringBean
	ExperimentService experimentService;

	private Boolean allOption = false;
	private Boolean addBlank = false;
	private String platform;

	
	public ExperimentListModel(String platform, ExperimentService experimentService,
			Boolean allOption)
		{
		this(platform, experimentService, allOption, false);
		}
	
	// REVIEW : Get rid of this (+ calls)
	public ExperimentListModel(String platform, ExperimentService experimentService,
			Boolean allOption, Boolean blankOption)
		{
		super();
		this.platform = platform;
		this.experimentService = experimentService;
		this.allOption = allOption;
		this.addBlank = blankOption;
		}

	@Override
	protected List<String> load()
		{
		List<String> lst = experimentService.expIdsByInceptionDate();
		if (allOption)
			lst.add(0, "All");
		if (addBlank) 
			lst.add(0, "");
		return lst;
		}

	public Boolean getAllOption()
		{
		return allOption;
		}

	public void setAllOption(Boolean allOption)
		{
		this.allOption = allOption;
		}

	public Boolean getAddBlank()
		{
		return addBlank;
		}

	public void setAddBlank(Boolean addBlank)
		{
		this.addBlank = addBlank;
		}
	}

package edu.umich.brcf.shared.layers.dto;

import java.io.Serializable;


public class ExperimentalGroupDTO implements Serializable
	{
	private String expID;
	private String group_name;
	private String group_description;

	public ExperimentalGroupDTO()
		{
		}

	public String getExpID()
		{
		return expID;
		}

	public void setExpID(String expID)
		{
		this.expID = expID;
		}

	public String getGroup_name()
		{
		return group_name;
		}

	public void setGroup_name(String group_name)
		{
		this.group_name = group_name;
		}

	public String getGroup_description()
		{
		return group_description;
		}

	public void setGroup_description(String group_description)
		{
		this.group_description = group_description;
		}
	}

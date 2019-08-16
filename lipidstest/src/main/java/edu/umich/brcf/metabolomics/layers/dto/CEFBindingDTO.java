package edu.umich.brcf.metabolomics.layers.dto;


public class CEFBindingDTO 
	{
	CEFCompoundBindingDTO[] compoundList;

	public CEFCompoundBindingDTO[] getCompoundList() 
		{
		return compoundList;
		}

	public void setCompoundList(CEFCompoundBindingDTO[] compoundList) 
		{
		this.compoundList = compoundList;
		}
	}

// Mrc2ExperimentalDesignItem.java
// Written by Jan Wigginton, September 2015

// This object corresponds to one line on the sample submission sheet's experimental design tab

package edu.umich.brcf.shared.util.datacollectors;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.util.interfaces.IWriteConvertable;
import edu.umich.brcf.shared.util.io.StringUtils;





public class Mrc2ExperimentalDesignItem implements IWriteConvertable, Serializable
	{
	@SpringBean
	AssayService assayService;
	
	private String sampleId;
	private String userSampleId;
	private List <String> factorValues;
	private List<String> assayNames, displayAssayNames, namesOnly;
	
	
	public Mrc2ExperimentalDesignItem()
		{
		Injector.get().inject(this);
		factorValues = new ArrayList<String>();
		assayNames = new ArrayList<String>();
		}
	
	public Mrc2ExperimentalDesignItem(String sampleId, String userSampleId, 
			List <String> f, List<String> names)
		{
		Injector.get().inject(this);
		
		this.sampleId = sampleId;
		this.userSampleId = userSampleId;
		setFactorValues(f);
		setAssayNames(names);
		// TO DO Deal with misspellings...
		displayAssayNames = assayService.pullShortNamesAndIdsFor(names, Mrc2ExperimentalDesign.SHORT_LABEL_LEN);
		setAssayNames(displayAssayNames);
		}
	
	
	public String getSampleId() 
		{
		return sampleId;
		}
	
	
	public void setSampleId(String sampleId) 
		{
		this.sampleId = sampleId;
		}
	
	public String getFactorValues(int i)
		{
		try { return factorValues.get(i); }
		catch (IndexOutOfBoundsException e) { return ""; }
		}
	
	public String getAssayNames(int i)
		{
		try { return assayNames.get(i); }
		catch (IndexOutOfBoundsException e) { return ""; }
		}
	
	public String getFormAssayNames(int i)
		{
		if (assayNames.size() > i && !StringUtils.isEmptyOrNull(assayNames.get(i)))
			{
			return assayNames.get(i);
			}
		
		return null;
		}

	public void setFactorValues(int i, String val)
		{
		try {  factorValues.set(i, val);  }	
		catch (IndexOutOfBoundsException e) { }
		}
	
	public List <String> getFactorValues()
		{
		return factorValues;
		}
	
	public void setFactorValues(List <String> factorValues)
		{
		this.factorValues = new ArrayList<String>(); 
		
		for (int i = 0; i < factorValues.size(); i++)
			this.factorValues.add(factorValues.get(i));
		}
	
	public List<String> getAssayNames()
		{
		return assayNames;
		}
	
	public void setDisplayAssayNames(List<String> lst)
		{
		displayAssayNames = assayService.pullShortNamesAndIdsFor(lst, Mrc2ExperimentalDesign.SHORT_LABEL_LEN);
		setAssayNames(displayAssayNames);
		}
	
	public void setAssayNames(List<String> lst)
		{
		setAssayNames(lst, false);
		}
	
	public void setAssayNames(List<String> lst, boolean noBlank)
		{
		this.assayNames = new ArrayList<String>();
		for (int i =0; i < Mrc2ExperimentalDesign.MRC2_SUBMISSION_SHEET_NASSAYS; i++)
			{
			if (!noBlank)
				assayNames.add(i < lst.size() ? lst.get(i) : "");
			else
				if (i < lst.size())
					assayNames.add(lst.get(i));
			}
		}

	public String getUserSampleId() 
		{
		return userSampleId;
		}

	public void setUserSampleId(String userSampleId) 
		{
		this.userSampleId = userSampleId;
		}

	public String toString()
		{
		List<String> tokens =  toTokens();
		List<String> retTokens = new ArrayList<String>();
		
		for (String str : tokens)
			if (str != null && !str.trim().isEmpty())
				retTokens.add(str);
		
		return retTokens.toString();
		}
	
	@Override
	public String toCharDelimited(String separator)
		{
		StringBuilder sb = new StringBuilder();
	
		String token = userSampleId == null ? "" : this.userSampleId;
		sb.append(token + separator);

		token = sampleId == null ? "" : this.sampleId;
		sb.append(token + separator);
		
		for (int i = 0; i < factorValues.size(); i++)
			{
			String fv = factorValues.get(i) == null ? "" : factorValues.get(i).replace(separator, " ");
			sb.append(fv + separator);
			}
	
		String name; 
		for (int j = 0; j < Mrc2ExperimentalDesign.MRC2_SUBMISSION_SHEET_NASSAYS; j++)
			{
			name = "";
			if (j < assayNames.size())
				name = assayNames.get(j) == null ? "" : assayNames.get(j).replace(separator, " ");
			
			sb.append(name + separator);
			}
		
		return sb.toString();
		}


	public List<String> toTokens()
		{
		List <String> tokens = new ArrayList<String>();
		
		tokens.add(sampleId == null ? "" : sampleId.replace(",", " "));
		tokens.add(userSampleId == null ? "" : userSampleId.replace(",", " "));
				
		for (int i = 0; i < Mrc2ExperimentalDesign.MRC2_SUBMISSION_SHEET_NFACTORS; i++)
			{
			String fv = (factorValues == null || i >= factorValues.size()) ? "" : factorValues.get(i).replace(",", " ");
			tokens.add(fv);
			}
		
		if (assayNames != null)
			for (int i = 0; i < Mrc2ExperimentalDesign.MRC2_SUBMISSION_SHEET_NASSAYS; i++)
				tokens.add(i >= assayNames.size() ? "" : assayNames.get(i));
		
		return tokens;
		}
	
	public String getValueForHeader(String label)
		{
		if (label == null) return "";
		
		int nfactors = factorValues.size();
		int nassays = assayNames.size();
		
		switch (StringUtils.removeSpaces(label).toLowerCase())
			{
			case "factor1" : return nfactors > 0 ? factorValues.get(0)  : "";
			case "factor2" : return nfactors > 1 ? factorValues.get(1)  : "";
			case "factor3" : return nfactors > 2 ? factorValues.get(2)  : "";
			case "factor4" : return nfactors > 3 ? factorValues.get(3)  : "";
			case "factor5" : return nfactors > 4 ? factorValues.get(4)  : "";
			
			case "assay1" : return nassays > 0 ? this.assayNames.get(0) : "";
			case "assay2" : return nassays > 1 ? this.assayNames.get(1) : "";
			case "assay3" : return nassays > 2 ? this.assayNames.get(2) : "";
			case "assay4" : return nassays > 3 ? this.assayNames.get(3) : "";
			case "assay5" : return nassays > 4 ? this.assayNames.get(4) : "";
			case "assay6" : return nassays > 5 ? this.assayNames.get(5) : "";
			default : return "";
			}
		}
	
	
	public List<String> getValuesForHeaders(List<String> headers)
		{
		List <String> tokens = new ArrayList<String>();
		for (int j = 0; j < headers.size(); j++)
			tokens.add(getValueForHeader(headers.get(j)));
	
		return tokens;
		}
	

	@Override
	public String toExcelRow() 
		{
		// TODO Auto-generated method stub
		return null;
		}
	}

/*
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.metworks.lims.interfaces.IWriteConvertable;
import edu.umich.metworks.lims.service.AssayService;
import edu.umich.metworks.util.StringUtils;

public class Mrc2ExperimentalDesignItem implements IWriteConvertable, Serializable
	{
	@SpringBean
	AssayService assayService;
	
	private String sampleId;
	private String userSampleId;
	private List <String> factorValues;
	private List<String> assayNames, displayAssayNames, namesOnly;
	
	
	public Mrc2ExperimentalDesignItem()
		{
		Injector.get().inject(this);
		factorValues = new ArrayList<String>();
		assayNames = new ArrayList<String>();
		}
	
	public Mrc2ExperimentalDesignItem(String sampleId, String userSampleId, 
			List <String> f, List<String> names)
		{
		Injector.get().inject(this);
		
		this.sampleId = sampleId;
		this.userSampleId = userSampleId;
		setFactorValues(f);
		setAssayNames(names);
		// TO DO Deal with misspellings...
		displayAssayNames = assayService.pullShortNamesAndIdsFor(names, Mrc2ExperimentalDesign.SHORT_LABEL_LEN);
		setAssayNames(displayAssayNames);
		}
	
	
	public String getSampleId() 
		{
		return sampleId;
		}
	
	
	public void setSampleId(String sampleId) 
		{
		this.sampleId = sampleId;
		}
	
	public String getFactorValues(int i)
		{
		try { return factorValues.get(i); }
		catch (IndexOutOfBoundsException e) { return ""; }
		}
	
	public String getAssayNames(int i)
		{
		try { return assayNames.get(i); }
		catch (IndexOutOfBoundsException e) { return ""; }
		}

	public void setFactorValues(int i, String val)
		{
		try {  factorValues.set(i, val);  }	
		catch (IndexOutOfBoundsException e) { }
		}
	
	public List <String> getFactorValues()
		{
		return factorValues;
		}
	
	public void setFactorValues(List <String> factorValues)
		{
		this.factorValues = new ArrayList<String>(); 
		
		for (int i = 0; i < factorValues.size(); i++)
			this.factorValues.add(factorValues.get(i));
		}
	
	public List<String> getAssayNames()
		{
		return assayNames;
		}
	
	public void setDisplayAssayNames(List<String> lst)
		{
		displayAssayNames = assayService.pullShortNamesAndIdsFor(lst, Mrc2ExperimentalDesign.SHORT_LABEL_LEN);
		setAssayNames(displayAssayNames);
		}
	
	public void setAssayNames(List<String> lst)
		{
		this.assayNames = new ArrayList<String>();
		for (int i =0; i < Mrc2ExperimentalDesign.MRC2_SUBMISSION_SHEET_NASSAYS; i++)
			assayNames.add(i < lst.size() ? lst.get(i) : "");
		}

	public String getUserSampleId() 
		{
		return userSampleId;
		}

	public void setUserSampleId(String userSampleId) 
		{
		this.userSampleId = userSampleId;
		}

	@Override
	public String toCharDelimited(String separator)
		{
		StringBuilder sb = new StringBuilder();
	
		String token = userSampleId == null ? "" : this.userSampleId;
		sb.append(token + separator);

		token = sampleId == null ? "" : this.sampleId;
		sb.append(token + separator);
		
		for (int i = 0; i < factorValues.size(); i++)
			{
			String fv = factorValues.get(i) == null ? "" : factorValues.get(i).replace(separator, " ");
			sb.append(fv + separator);
			}
	
		String name; 
		for (int j = 0; j < Mrc2ExperimentalDesign.MRC2_SUBMISSION_SHEET_NASSAYS; j++)
			{
			name = "";
			if (j < assayNames.size())
				name = assayNames.get(j) == null ? "" : assayNames.get(j).replace(separator, " ");
			
			sb.append(name + separator);
			}
		
		return sb.toString();
		}


	public List<String> toTokens()
		{
		List <String> tokens = new ArrayList<String>();
		
		tokens.add(sampleId == null ? "" : sampleId.replace(",", " "));
		tokens.add(userSampleId == null ? "" : userSampleId.replace(",", " "));
				
		for (int i = 0; i < Mrc2ExperimentalDesign.MRC2_SUBMISSION_SHEET_NFACTORS; i++)
			{
			String fv = (factorValues == null || i >= factorValues.size()) ? "" : factorValues.get(i).replace(",", " ");
			tokens.add(fv);
			}
		
		if (assayNames != null)
			for (int i = 0; i < Mrc2ExperimentalDesign.MRC2_SUBMISSION_SHEET_NASSAYS; i++)
				tokens.add(i >= assayNames.size() ? "" : assayNames.get(i));
		
		return tokens;
		}
	
	public String getValueForHeader(String label)
		{
		if (label == null) return "";
		
		int nfactors = factorValues.size();
		int nassays = assayNames.size();
		
		switch (StringUtils.removeSpaces(label).toLowerCase())
			{
			case "factor1" : return nfactors > 0 ? factorValues.get(0)  : "";
			case "factor2" : return nfactors > 1 ? factorValues.get(1)  : "";
			case "factor3" : return nfactors > 2 ? factorValues.get(2)  : "";
			case "factor4" : return nfactors > 3 ? factorValues.get(3)  : "";
			case "factor5" : return nfactors > 4 ? factorValues.get(4)  : "";
			
			case "assay1" : return nassays > 0 ? this.assayNames.get(0) : "";
			case "assay2" : return nassays > 1 ? this.assayNames.get(1) : "";
			case "assay3" : return nassays > 2 ? this.assayNames.get(2) : "";
			case "assay4" : return nassays > 3 ? this.assayNames.get(3) : "";
			case "assay5" : return nassays > 4 ? this.assayNames.get(4) : "";
			case "assay6" : return nassays > 5 ? this.assayNames.get(5) : "";
			default : return "";
			}
		}
	
	public List<String> getValuesForHeaders(List<String> headers)
		{
		List <String> tokens = new ArrayList<String>();
    	for (int j = 0; j < headers.size(); j++)
    		tokens.add(getValueForHeader(headers.get(j)));
    	
    	return tokens;
		}
	

	@Override
	public String toExcelRow() 
		{
		// TODO Auto-generated method stub
		return null;
		}
	}  */
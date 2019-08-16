package edu.umich.brcf.shared.util.datacollectors;

//ExperimentalDesignItem.java
//Written by Jan Wigginton, September 2015

//This object corresponds to one line on the sample submission sheet's experimental design tab


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.util.interfaces.IWriteConvertable;


public class ExperimentalDesignItem implements IWriteConvertable, Serializable
	{
	@SpringBean
	AssayService assayService;
	
	private String sampleId;
	private String userSampleId;
	private List <String> factorValues;
	private List<String> assayNames, displayAssayNames;
	
	
	public ExperimentalDesignItem()
		{
		Injector.get().inject(this);
		assayNames = new ArrayList<String>();
		factorValues = new ArrayList<String>();
		displayAssayNames = new ArrayList<String>();
		}
	
	
	public ExperimentalDesignItem(String sampleId, String userSampleId, 
			List <String> f, List<String> names)
		{
		this();
		
		this.sampleId = sampleId;
		this.userSampleId = userSampleId;
		setFactorValues(f);
		setAssayNames(names);
		// TO DO Deal with misspellings...
		displayAssayNames = assayService.pullShortNamesAndIdsFor(names, ExperimentalDesign.SHORT_LABEL_LEN);
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
		displayAssayNames = assayService.pullShortNamesAndIdsFor(lst, ExperimentalDesign .SHORT_LABEL_LEN);
		setAssayNames(displayAssayNames);
		}
	
	public void setAssayNames(List<String> lst)
		{
		this.assayNames = new ArrayList<String>();
		for (int i =0; i < ExperimentalDesign.SUBMISSION_SHEET_NASSAYS; i++)
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
		for (int j = 0; j < ExperimentalDesign.SUBMISSION_SHEET_NASSAYS; j++)
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
				
		for (int i = 0; i < ExperimentalDesign.SUBMISSION_SHEET_NASSAYS; i++)
			{
			String fv = i >= factorValues.size() ? "" : factorValues.get(i).replace(",", " ");
			tokens.add(fv);
			}
		
		
		for (int i = 0; i < ExperimentalDesign.SUBMISSION_SHEET_NASSAYS; i++)
			tokens.add(i >= assayNames.size() ? "" : assayNames.get(i));
		
		return tokens;
		}
	

	@Override
	public String toExcelRow() 
		{
		// TODO Auto-generated method stub
		return null;
		}
	}

//ExperimentalDesignItem.java
//Written by Jan Wigginton, September 2015

//This object corresponds to one line on the sample submission sheet's experimental design tab

/*

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.util.interfaces.IWriteConvertable;



public class ExperimentalDesignItem implements IWriteConvertable, Serializable
	{
	@SpringBean
	AssayService assayService;
	
	private String sampleId;
	private String userSampleId;
	private List <String> factorValues;
	private List<String> assayNames, displayAssayNames;
	
	
	public ExperimentalDesignItem()
		{
		Injector.get().inject(this);
		assayNames = new ArrayList<String>();
		factorValues = new ArrayList<String>();
		displayAssayNames = new ArrayList<String>();
		}
	
	
	public ExperimentalDesignItem(String sampleId, String userSampleId, 
			List <String> f, List<String> names)
		{
		this();
		
		this.sampleId = sampleId;
		this.userSampleId = userSampleId;
		setFactorValues(f);
		setAssayNames(names);
		// TO DO Deal with misspellings...
		displayAssayNames = assayService.pullShortNamesAndIdsFor(names, ExperimentalDesign.SHORT_LABEL_LEN);
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
		displayAssayNames = assayService.pullShortNamesAndIdsFor(lst, ExperimentalDesign .SHORT_LABEL_LEN);
		setAssayNames(displayAssayNames);
		}
	
	public void setAssayNames(List<String> lst)
		{
		this.assayNames = new ArrayList<String>();
		for (int i =0; i < ExperimentalDesign.SUBMISSION_SHEET_NASSAYS; i++)
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
		for (int j = 0; j < ExperimentalDesign.SUBMISSION_SHEET_NASSAYS; j++)
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
				
		for (int i = 0; i < ExperimentalDesign.SUBMISSION_SHEET_NASSAYS; i++)
			{
			String fv = i >= factorValues.size() ? "" : factorValues.get(i).replace(",", " ");
			tokens.add(fv);
			}
		
		
		for (int i = 0; i < ExperimentalDesign.SUBMISSION_SHEET_NASSAYS; i++)
			tokens.add(i >= assayNames.size() ? "" : assayNames.get(i));
		
		return tokens;
		}
	

	@Override
	public String toExcelRow() 
		{
		// TODO Auto-generated method stub
		return null;
		}
	} */
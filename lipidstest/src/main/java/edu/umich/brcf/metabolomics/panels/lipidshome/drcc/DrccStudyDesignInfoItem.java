// DrccStudyDesignInfoItem.java
// Written by Jan Wigginton July 2015

package edu.umich.brcf.metabolomics.panels.lipidshome.drcc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.umich.brcf.shared.util.interfaces.IWriteConvertable;



public class DrccStudyDesignInfoItem implements IWriteConvertable, Serializable
	{
	private String sampleId;
	private String sampleName;
	private String subjectName;
	private String  researcherSampleName;
	private List <String> factorValues;
	private int nFactors;
	private String userDefinedSampleType;
	private String userSampleId;
	String mode = "drcc";
	
	public DrccStudyDesignInfoItem()
		{
		}
	
	public DrccStudyDesignInfoItem(String sampleId, String subjectId, String researcherSampleName, List <String> f)
		{
		this.sampleId = sampleId;
		this.subjectName = subjectId;
		this.researcherSampleName = researcherSampleName;
		this.factorValues  = new ArrayList <String>();
		for (int i = 0; i < f.size(); i++)
			this.factorValues.add(f.get(i));
		
		this.nFactors = factorValues.size();
		for (int i =  nFactors; i < 5; i++)
			this.factorValues.add("");
		
		}
	
	
	public String getSampleId() 
		{
		return sampleId;
		}
	
	
	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}
	
	
	public String getSampleName() {
		return sampleName;
	}
	
	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}
	
	public String getSubjectName() {
		return subjectName;
	}
	
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	
	public String getResearcherSampleName() {
		return researcherSampleName;
	}
	
	public void setResearcherSampleName(String researcherSampleName) {
		this.researcherSampleName = researcherSampleName;
	}
	
	public String getFactorValues(int i)
		{
		return (i < nFactors ? factorValues.get(i) : "");
		}

	public void setFactorValues(int i, String val)
		{
		if (i < nFactors)
			factorValues.set(i, val);
		}
	
	public List <String> getFactorValues()
		{
		return factorValues;
		}
	
	public void setFactorValues(List <String> factorValues)
		{
		this.factorValues = factorValues;
		nFactors = factorValues.size();
		
		for (int i = 0; i < nFactors; i++)
			factorValues.add("");
		}
	
	public String getUserDefinedSampleType()
		{
		return this.userDefinedSampleType;
		}
	
	public void setUserDefinedSampleType(String type)
		{
		this.userDefinedSampleType = type;
		}
	
	
	
	public String getUserSampleId() {
		return userSampleId;
	}

	public void setUserSampleId(String userSampleId) {
		this.userSampleId = userSampleId;
	}

	@Override
	public String toCharDelimited(String separator)
		{
		StringBuilder sb = new StringBuilder();
	
		String sName = subjectName == null ? "" : subjectName.replace(",", " ");
		sb.append(sName + separator);

		String rSName = researcherSampleName == null ? "" : researcherSampleName.replace(",", " ");
		sb.append(rSName + separator);
		
		for (int i = 0; i < nFactors; i++)
			{
			String fv = factorValues.get(i) == null ? "" : factorValues.get(i).replace(",", " ");
			sb.append(fv + separator);
			}
		
		if (!mode.equals("drcc"))
			{
			
			String sampleType = userDefinedSampleType == null ? "" : this.userDefinedSampleType;
			sb.append(sampleType + separator);
			
			String sampleId = userSampleId == null ? "" : this.userSampleId;
			sb.append(this.userSampleId + separator);
			}	
		return sb.toString();
		}


	public List<String> toTokens()
		{
		List <String> tokens = new ArrayList<String>();
		
		String sName = subjectName == null ? "" : subjectName.replace(",", " ");
		tokens.add(sName);

		String rSName = researcherSampleName == null ? "" : researcherSampleName.replace(",", " ");
		tokens.add(rSName);
		
		for (int i = 0; i < nFactors; i++)
			{
			String fv = factorValues.get(i) == null ? "" : factorValues.get(i).replace(",", " ");
			tokens.add(fv);
			}
		
		if (!mode.equals("drcc"))
			{
			tokens.add(userDefinedSampleType == null ? "" : userDefinedSampleType.replace(",", " "));
			tokens.add(userSampleId == null ? "" : userSampleId.replace(",", " "));
			}
		return tokens;
		}
	

	@Override
	public String toExcelRow() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setMode(String mode)
		{
		this.mode = mode;
		}
	}
// DrccSubjectInfoItem.java
// Written by Jan Wigginton 06/01/15

package edu.umich.brcf.metabolomics.panels.lipidshome.drcc;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import edu.umich.brcf.shared.util.interfaces.IWriteConvertable;

public class DrccSubjectInfoItem implements IWriteConvertable, Serializable
	{
	String subjectId;
	String subjectType;
	String subjectSpecies, subjectSpeciesId;
	String taxonomyId;
	
	public DrccSubjectInfoItem(String subjectId, String subjectType, String subjectSpeciesId, String subjectSpecies, String taxId)
		{
		this.subjectId = subjectId;
		this.subjectType = subjectType;
		this.subjectSpeciesId = subjectSpeciesId;
		this.subjectSpecies = subjectSpecies;
		this.taxonomyId = taxId;
		}
	
	public int compare(DrccSubjectInfoItem o1, DrccSubjectInfoItem o2) {
		return o1.getSubjectId().compareTo(o2.getSubjectId());
	}
	public String getSubjectId() {
		return subjectId;
	}
	
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	
	public String getSubjectType() {
		return subjectType;
	}
	
	
	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}
	
	public String getSubjectSpecies() {
		return subjectSpecies;
	}
	
	public void setSubjectSpecies(String subjectSpecies) {
		this.subjectSpecies = subjectSpecies;
	}
	
	
	public String getSubjectSpeciesId() {
		return subjectSpeciesId;
	}
	
	public void setSubjectSpeciesId(String subjectSpeciesId) {
		this.subjectSpeciesId = subjectSpeciesId;
	}
	
	public String getTaxonomyId() 
		{
		return taxonomyId;
		}
	
	public void setTaxonomyId(String taxonomyId) 
		{
		this.taxonomyId = taxonomyId;
		}
	

	@Override
	public String toCharDelimited(String separator)
		{
		StringBuilder sb = new StringBuilder();
		
		String sid = subjectId == null ? "" : subjectId.replace(",", " ");
		sb.append(sid + separator);
		
		String sType = subjectType == null ? "" : subjectType.replace(",", " ");
		sb.append(sType + separator);
		
		String sSpecies = subjectSpecies == null ? "" : subjectSpecies.replace(",", " ");
		sb.append(sSpecies.replace(",", " ") + separator);
		
		String tid = taxonomyId == null ? "" : taxonomyId.replace(",", " ");
		sb.append(tid + separator);
		
		return sb.toString();
		}
	
	public List<String> toTokens()
		{
		List <String> tokens = new ArrayList<String>();
		
		String sid = subjectId == null ? "" : subjectId.replace(",", " ");
		tokens.add(sid);
		
		String sType = subjectType == null ? "" : subjectType.replace(",", " ");
		tokens.add(sType);
		
		String sSpecies = subjectSpecies == null ? "" : subjectSpecies.replace(",", " ");
		tokens.add(sSpecies);
		
		String tid = taxonomyId == null ? "" : taxonomyId.replace(",", " ");
		tokens.add(tid);
		
		return tokens;
		}
	
	@Override
	public String toExcelRow() {
		// TODO Auto-generated method stub
		return null;
	}


}
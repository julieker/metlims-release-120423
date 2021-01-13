//////////////////////////////////////////////
// Mrc2SampleInfoItem.java
// Written by Jan Wigginton, September 2015
//////////////////////////////////////////////

package edu.umich.brcf.shared.util.datacollectors;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.umich.brcf.shared.layers.dto.SampleDTO;
import edu.umich.brcf.shared.util.interfaces.IWriteConvertable;


public class Mrc2SampleInfoItem implements Serializable, IWriteConvertable
	{
	String sampleId = "";
	String researcherSampleId  = "";
	String researcherSubjectId  = "", mrc2SubjectId = "TBD";
	String sampleTypeId  = "", userDefinedSampleType  = "";
	String genusOrSpeciesId  = "", userDefinedGOS  = "";
	String volume  = "", units  = "";
	String locationId  = "";


	public Mrc2SampleInfoItem(String sampleId, String researcherSampleId, String researcherSubjectId,
		String userDefinedSampleType, String userDefinedGOS, String volume, String units, String sampleTypeId, 
		String genusOrSpeciesId, String locationId, String mrc2SubjectId)
		{
		this.sampleId = sampleId;
		this.researcherSampleId = researcherSampleId;
		this.researcherSubjectId = researcherSubjectId;
		//this.sampleType = sampleType;
		this.sampleTypeId = sampleTypeId;
		//this.genusOrSpecies = genusOrSpecies;
		this.genusOrSpeciesId = genusOrSpeciesId;
		this.volume = volume;
		this.units = units;
		this.locationId = locationId;
		this.userDefinedGOS = userDefinedGOS;
		this.userDefinedSampleType = userDefinedSampleType;
		boolean noMrc2SubjectId = (mrc2SubjectId == null || "".equals(mrc2SubjectId.trim()));
		this.mrc2SubjectId = noMrc2SubjectId ?  "TBD" : mrc2SubjectId;
		}
	
	public String getValueForHeader(String header, Boolean blankMode)
		{
		if (blankMode)
			return "";
		
		switch(header)
			{
			case "Sample ID" : return this.getSampleId(); 
			case "Researcher Sample Id" : return this.getResearcherSampleId();
			case "Researcher Subject Id" : return this.getResearcherSubjectId();
			case "Sample Type" : return this.getUserDefinedSampleType();
			case "Genus or Species" : return this.getUserDefinedGOS(); 
			case "Volume" : return this.getVolume();
			case "Units" : return this.getUnits();
			case "Sample Type ID" : return this.getSampleTypeId();
			case "GenusOrSpecies ID" : return this.getGenusOrSpeciesId();
			case "Loc ID" : return this.getLocationId();
			default : return "";
			}
		}
	
	//9606
	public SampleDTO toIncompleteSampleDTO()
		{
		DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
		((DecimalFormat) format).setParseBigDecimal(true);
	
		BigDecimal volumeAsBigDecimal = null;
		try
			{
			volumeAsBigDecimal = (BigDecimal) format.parse(this.volume);
			}
		catch (ParseException e)
			{
			}
		
		Calendar creationDate = Calendar.getInstance();
		
		Long genusSpeciesIdAsLong = null;
		try
			{
			genusSpeciesIdAsLong = Long.parseLong(genusOrSpeciesId);
			}
		catch (NumberFormatException e)
			{
			}
		
		// Issue 244
		SampleDTO dto = new SampleDTO(sampleId, researcherSampleId, "", "", genusSpeciesIdAsLong,
			locationId, userDefinedSampleType, volumeAsBigDecimal, units, 'Q',
			null, Calendar.getInstance(), this.getSampleTypeId(),  
			"", "", this.getUserDefinedGOS()); 
		dto.setSubjectName(researcherSubjectId);
		dto.setSubjectId(researcherSubjectId);
		return dto;
		}
	
	public String getSampleId() 
		{
		return sampleId;
		}

	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}



	public String getResearcherSampleId() {
		return researcherSampleId;
	}



	public void setResearcherSampleId(String researcherSampleId) {
		this.researcherSampleId = researcherSampleId;
	}



	public String getResearcherSubjectId() {
		return researcherSubjectId;
	}



	public void setResearcherSubjectId(String researcherSubjectId) {
		this.researcherSubjectId = researcherSubjectId;
	}

	public String getSampleTypeId() {
		return sampleTypeId;
	}



	public void setSampleTypeId(String sampleTypeId) {
		this.sampleTypeId = sampleTypeId;
	}



	public String getGenusOrSpeciesId() {
		return genusOrSpeciesId;
	}



	public void setGenusOrSpeciesId(String genusOrSpeciesId) {
		this.genusOrSpeciesId = genusOrSpeciesId;
	}



	public String getVolume() {
		return volume;
	}



	public void setVolume(String volume) {
		this.volume = volume;
	}



	public String getUnits() {
		return units;
	}



	public void setUnits(String units) {
		this.units = units;
	}



	public String getLocationId() {
		return locationId;
	}



	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	
	public String getUserDefinedSampleType() {
		return userDefinedSampleType;
	}

	public String getUserDefinedGOS() {
		return userDefinedGOS;
	}

	public void setUserDefinedSampleType(String userDefinedSampleType) {
		this.userDefinedSampleType = userDefinedSampleType;
	}

	public void setUserDefinedGOS(String userDefinedGOS) {
		this.userDefinedGOS = userDefinedGOS;
	}

	
	
	public String getMrc2SubjectId() {
		return mrc2SubjectId;
	}

	public void setMrc2SubjectId(String mrc2SubjectId) {
		this.mrc2SubjectId = mrc2SubjectId;
	}

	
	public String toString()
		{
		return toTokens2().toString();
		//return toCharDelimited(",");
		}
	
	@Override
	public String toCharDelimited(String separator)
		{
		Class myObjectClass = Mrc2SampleInfoItem.class;
		Field [] fields  = myObjectClass.getFields();
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < fields.length; i++)
			{
			Field field = fields[i];
			if (i > 0) 
				sb.append(separator);
			
			try 
				{
				Object value = field.get(this);
				sb.append(value == null ? "" : value.toString());
				}
			catch (Exception e) {}
			}

		// NOTE : DO NOT EXAPND THIS WITHOUT CHECKING USAGE IN DRCC AND EXPERIMENT TABS
		return sb.toString();
		}
		
	
	public List<String> toTokens()
		{
		List <String> lst = new ArrayList<String>();
		lst.add(this.sampleId);
		lst.add(this.researcherSampleId);
		lst.add(this.researcherSubjectId);
		//lst.add(this.sampleType);
		//lst.add(this.genusOrSpecies);
		lst.add(this.volume);
		lst.add(this.units);
		lst.add(this.sampleTypeId);
		lst.add(this.genusOrSpeciesId);
		lst.add(this.locationId);
		// NOTE :DO NOT EXPAND THIS WITHOUT CHECKING USAGE IN DRCC AND EXPERIMENT TABS
		
		return lst;
		}
	
	public List<String> toTokens2()
		{
		List <String> lst = new ArrayList<String>();
		lst.add(this.sampleId);
		lst.add(this.researcherSampleId);
		lst.add(this.researcherSubjectId);
		lst.add(this.userDefinedSampleType);
		lst.add(this.userDefinedGOS);
		lst.add(this.volume);
		lst.add(this.units);
		lst.add(this.sampleTypeId);
		lst.add(this.genusOrSpeciesId);
		lst.add(this.locationId);
		
		return lst;
		}
	
	
	/*
	public List<String> toTokens()
		{
		List <String> tokens = new ArrayList<String>();
		Class myObjectClass = Mrc2SampleInfoItem.class;
		Field [] fields  = myObjectClass.getFields();
		
		for (int i = 0; i < fields.length; i++)
			{
			try
				{
				Object value = fields[i].get(this);
				tokens.add(value.toString());
				}
			catch (Exception e) {}
			}

		return tokens;
		}*/

	@Override
	public String toExcelRow() {
		// TODO Auto-generated method stub
		return null;
	}




}

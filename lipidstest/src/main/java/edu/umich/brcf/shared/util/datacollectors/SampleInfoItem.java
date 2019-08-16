//////////////////////////////////////////////

package edu.umich.brcf.shared.util.datacollectors;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import edu.umich.brcf.shared.layers.dto.SampleDTO;
import edu.umich.brcf.shared.util.interfaces.IWriteConvertable;
import edu.umich.brcf.shared.util.io.StringUtils;

public abstract class SampleInfoItem implements Serializable, IWriteConvertable
	{
	protected String sampleId = "";
	protected String researcherSampleId = "";
	protected String researcherSubjectId = "", subjectId = "TBD";
	protected String sampleTypeId = "", userDefinedSampleType = "";
	protected String genusOrSpeciesId = "", userDefinedGOS = "";
	protected String volume = "", units = "";
	protected String locationId = "";

	public SampleInfoItem()
		{
		}

	public SampleInfoItem(String sampleId, String researcherSampleId,
			String researcherSubjectId, String userDefinedSampleType,
			String userDefinedGOS, String volume, String units,
			String sampleTypeId, String genusOrSpeciesId, String locationId,
			String suid)
		{
		this.sampleId = sampleId;
		this.researcherSampleId = researcherSampleId;
		this.researcherSubjectId = researcherSubjectId;
		// this.sampleType = sampleType;
		this.sampleTypeId = sampleTypeId;
		// this.genusOrSpecies = genusOrSpecies;
		this.genusOrSpeciesId = genusOrSpeciesId;
		this.volume = volume;
		this.units = units;
		this.locationId = locationId;
		this.userDefinedGOS = userDefinedGOS;
		this.userDefinedSampleType = userDefinedSampleType;
		boolean noSubjectId = (suid == null || "".equals(suid.trim()));
		this.subjectId = noSubjectId ? "TBD" : suid;
		}

	public String getValueForHeader(String header, Boolean blankMode)
		{
		if (blankMode)
			return "";

		String headerToCheck = StringUtils.removeSpaces(header.toLowerCase());
	
		switch (headerToCheck)
			{
			case "sampleid":
			return this.getSampleId();
			case "researchersampleid":
			return this.getResearcherSampleId();
			case "researchersubjectid":
			return this.getResearcherSubjectId();
			case "sampletype":
			return this.getUserDefinedSampleType();
			case "genusorspecies":
			return this.getUserDefinedGOS();
			case "volume":
			return this.getVolume();
			case "units":
			return this.getUnits();
			case "sampletypeid":
			return this.getSampleTypeId();
			case "genusorspeciesid":
			return this.getGenusOrSpeciesId();
			case "locid":
			return this.getLocationId();
			default:
			return "";
			}
		}

	public abstract SampleDTO toSampleDTO();

	/*
	 * public SampleDTO toIncompleteSampleDTO() { DecimalFormat format =
	 * (DecimalFormat) DecimalFormat.getInstance(); ((DecimalFormat)
	 * format).setParseBigDecimal(true);
	 * 
	 * BigDecimal volumeAsBigDecimal = null; try { volumeAsBigDecimal =
	 * (BigDecimal) format.parse(this.volume); } catch (ParseException e) { }
	 * 
	 * Calendar creationDate = Calendar.getInstance();
	 * 
	 * Long genusSpeciesIdAsLong = null; try { genusSpeciesIdAsLong =
	 * Long.parseLong(genusOrSpeciesId); } catch (NumberFormatException e) { }
	 * 
	 * return new SampleDTO(sampleId, researcherSampleId, "", "",
	 * genusSpeciesIdAsLong, locationId, userDefinedSampleType,
	 * volumeAsBigDecimal, units, 'Q', null, Calendar.getInstance(),
	 * this.getSampleTypeId(), "", ""); }
	 */

	public String getSampleId()
		{
		return sampleId;
		}

	public void setSampleId(String sampleId)
		{
		this.sampleId = sampleId;
		}

	public String getResearcherSampleId()
		{
		return researcherSampleId;
		}

	public void setResearcherSampleId(String researcherSampleId)
		{
		this.researcherSampleId = researcherSampleId;
		}

	public String getResearcherSubjectId()
		{
		return researcherSubjectId;
		}

	public void setResearcherSubjectId(String researcherSubjectId)
		{
		this.researcherSubjectId = researcherSubjectId;
		}

	// public String getSampleType() {
	// return sampleType;
	// }

	// public void setSampleType(String sampleType) {
	// this.sampleType = sampleType;
	// }

	public String getSampleTypeId()
		{
		return sampleTypeId;
		}

	public void setSampleTypeId(String sampleTypeId)
		{
		this.sampleTypeId = sampleTypeId;
		}

	// public String getGenusOrSpecies() {
	// return genusOrSpecies;
	// }

	// public void setGenusOrSpecies(String genusOrSpecies) {
	// this.genusOrSpecies = genusOrSpecies;
	// }

	public String getGenusOrSpeciesId()
		{
		return genusOrSpeciesId;
		}

	public void setGenusOrSpeciesId(String genusOrSpeciesId)
		{
		this.genusOrSpeciesId = genusOrSpeciesId;
		}

	public String getVolume()
		{
		return volume;
		}

	public void setVolume(String volume)
		{
		this.volume = volume;
		}

	public String getUnits()
		{
		return units;
		}

	public void setUnits(String units)
		{
		this.units = units;
		}

	public String getLocationId()
		{
		return locationId;
		}

	public void setLocationId(String locationId)
		{
		this.locationId = locationId;
		}

	public String getUserDefinedSampleType()
		{
		return userDefinedSampleType;
		}

	public String getUserDefinedGOS()
		{
		return userDefinedGOS;
		}

	public void setUserDefinedSampleType(String userDefinedSampleType)
		{
		this.userDefinedSampleType = userDefinedSampleType;
		}

	public void setUserDefinedGOS(String userDefinedGOS)
		{
		this.userDefinedGOS = userDefinedGOS;
		}

	public String getSubjectId()
		{
		return subjectId;
		}

	public void setSubjectId(String suid)
		{
		this.subjectId = suid;
		}

	@Override
	public String toCharDelimited(String separator)
		{
		Class myObjectClass = SampleInfoItem.class;
		Field[] fields = myObjectClass.getFields();
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
				} catch (Exception e)
				{
				}
			}

		return sb.toString();
		}

	public List<String> toTokens()
		{
		List<String> lst = new ArrayList<String>();
		lst.add(this.sampleId);
		lst.add(this.researcherSampleId);
		lst.add(this.researcherSubjectId);
		// lst.add(this.sampleType);
		// lst.add(this.genusOrSpecies);
		lst.add(this.volume);
		lst.add(this.units);
		lst.add(this.sampleTypeId);
		lst.add(this.genusOrSpeciesId);
		lst.add(this.locationId);

		return lst;
		}

	@Override
	public String toExcelRow()
		{
		// TODO Auto-generated method stub
		return null;
		}

	}

/*
 * package edu.umich.metworks.web.datastructure;
 * 
 * import java.io.Serializable; import java.lang.reflect.Field; import
 * java.math.BigDecimal; import java.text.DecimalFormat; import
 * java.text.ParseException; import java.util.ArrayList; import
 * java.util.Calendar; import java.util.List;
 * 
 * import edu.umich.metworks.lims.dto.SampleDTO; import
 * edu.umich.metworks.lims.interfaces.IWriteConvertable;
 * 
 * 
 * 
 * public abstract class SampleInfoItem implements Serializable,
 * IWriteConvertable { protected String sampleId = ""; protected String
 * researcherSampleId = ""; protected String researcherSubjectId = "", subjectId
 * = "TBD"; protected String sampleTypeId = "", userDefinedSampleType = "";
 * protected String genusOrSpeciesId = "", userDefinedGOS = ""; protected String
 * volume = "", units = ""; protected String locationId = "";
 * 
 * public SampleInfoItem() { }
 * 
 * public SampleInfoItem(String sampleId, String researcherSampleId, String
 * researcherSubjectId, String userDefinedSampleType, String userDefinedGOS,
 * String volume, String units, String sampleTypeId, String genusOrSpeciesId,
 * String locationId, String suid) { this.sampleId = sampleId;
 * this.researcherSampleId = researcherSampleId; this.researcherSubjectId =
 * researcherSubjectId; //this.sampleType = sampleType; this.sampleTypeId =
 * sampleTypeId; //this.genusOrSpecies = genusOrSpecies; this.genusOrSpeciesId =
 * genusOrSpeciesId; this.volume = volume; this.units = units; this.locationId =
 * locationId; this.userDefinedGOS = userDefinedGOS; this.userDefinedSampleType
 * = userDefinedSampleType; boolean noSubjectId = (suid == null ||
 * "".equals(suid.trim())); this.subjectId = noSubjectId ? "TBD" : suid; }
 * 
 * public String getValueForHeader(String header, Boolean blankMode) { if
 * (blankMode) return "";
 * 
 * switch(header) { case "Sample ID" : return this.getSampleId(); case
 * "Researcher Sample Id" : return this.getResearcherSampleId(); case
 * "Researcher Subject Id" : return this.getResearcherSubjectId(); case
 * "Sample Type" : return this.getUserDefinedSampleType(); case
 * "Genus or Species" : return this.getUserDefinedGOS(); case "Volume" : return
 * this.getVolume(); case "Units" : return this.getUnits(); case
 * "Sample Type ID" : return this.getSampleTypeId(); case "GenusOrSpecies ID" :
 * return this.getGenusOrSpeciesId(); case "Loc ID" : return
 * this.getLocationId(); default : return ""; } }
 * 
 * 
 * 
 * 
 * public String getSampleId() { return sampleId; }
 * 
 * public void setSampleId(String sampleId) { this.sampleId = sampleId; }
 * 
 * 
 * 
 * public String getResearcherSampleId() { return researcherSampleId; }
 * 
 * 
 * 
 * public void setResearcherSampleId(String researcherSampleId) {
 * this.researcherSampleId = researcherSampleId; }
 * 
 * 
 * 
 * public String getResearcherSubjectId() { return researcherSubjectId; }
 * 
 * 
 * 
 * public void setResearcherSubjectId(String researcherSubjectId) {
 * this.researcherSubjectId = researcherSubjectId; }
 * 
 * 
 * 
 * //public String getSampleType() { // return sampleType; //}
 * 
 * 
 * 
 * //public void setSampleType(String sampleType) { // this.sampleType =
 * sampleType; //}
 * 
 * 
 * 
 * public String getSampleTypeId() { return sampleTypeId; }
 * 
 * 
 * 
 * public void setSampleTypeId(String sampleTypeId) { this.sampleTypeId =
 * sampleTypeId; }
 * 
 * 
 * 
 * //public String getGenusOrSpecies() { // return genusOrSpecies; //}
 * 
 * 
 * 
 * //public void setGenusOrSpecies(String genusOrSpecies) { //
 * this.genusOrSpecies = genusOrSpecies; //}
 * 
 * 
 * 
 * public String getGenusOrSpeciesId() { return genusOrSpeciesId; }
 * 
 * 
 * 
 * public void setGenusOrSpeciesId(String genusOrSpeciesId) {
 * this.genusOrSpeciesId = genusOrSpeciesId; }
 * 
 * 
 * 
 * public String getVolume() { return volume; }
 * 
 * 
 * 
 * public void setVolume(String volume) { this.volume = volume; }
 * 
 * 
 * 
 * public String getUnits() { return units; }
 * 
 * 
 * 
 * public void setUnits(String units) { this.units = units; }
 * 
 * 
 * 
 * public String getLocationId() { return locationId; }
 * 
 * 
 * 
 * public void setLocationId(String locationId) { this.locationId = locationId;
 * }
 * 
 * 
 * public String getUserDefinedSampleType() { return userDefinedSampleType; }
 * 
 * public String getUserDefinedGOS() { return userDefinedGOS; }
 * 
 * public void setUserDefinedSampleType(String userDefinedSampleType) {
 * this.userDefinedSampleType = userDefinedSampleType; }
 * 
 * public void setUserDefinedGOS(String userDefinedGOS) { this.userDefinedGOS =
 * userDefinedGOS; }
 * 
 * 
 * 
 * public String getSubjectId() { return subjectId; }
 * 
 * public void setSubjectId(String suid) { this.subjectId = suid; }
 * 
 * @Override public String toCharDelimited(String separator) { Class
 * myObjectClass = SampleInfoItem.class; Field [] fields =
 * myObjectClass.getFields(); StringBuilder sb = new StringBuilder();
 * 
 * for (int i = 0; i < fields.length; i++) { Field field = fields[i]; if (i > 0)
 * sb.append(separator);
 * 
 * try { Object value = field.get(this); sb.append(value == null ? "" :
 * value.toString()); } catch (Exception e) {} }
 * 
 * return sb.toString(); } } }
 */

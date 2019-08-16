////////////////////////////////////////////////////
// ICheckinSampleItem.java
// Written by Jan Wigginton, Jun 5, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.interfaces;

import java.util.Calendar;

public interface ICheckinSampleItem
	{
	public String getSampleId();
	public String getSubjectId();
	public Calendar getCheckinDate();
	public String getResearcherSampleId();
	public String getResearcherSubjectId();
	
	public void setSampleId(String val);
	public void setSubjectId(String val);
	public void setCheckinDate(Calendar cal);
	public void setResearcherSampleId(String val);
	public void setResearcherSubjectId(String val);
	}

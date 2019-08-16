////////////////////////////////////////////////////
// PrepData.java
// Written by Jan Wigginton, Dec 6, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.umich.brcf.shared.util.utilpackages.DateUtils;

public class PrepData implements Serializable
	{
	private List<String> sampleIds;
	private List<String> expIds;
	private List<String> assayIds;
	private String instrumentId;
	private BigDecimal volume;
	private String volUnits;
	private int nRows, nCols;
	private String prepTitle;


	
	public PrepData()
		{
		sampleIds = new ArrayList<String>();
		}
	
	public WorklistSimple grabAsWorklist()
		{
		WorklistSimple w = new WorklistSimple();
		
		w.setWorklistName(getPrepTitle());
		w.setSelectedPlatform("agilent");
		w.setSelectedInstrument(this.getInstrumentId());
		w.setRunDate(DateUtils.dateStrFromCalendar("MM/dd/yyyy", Calendar.getInstance()));
		w.setDefaultInjectionVol(getVolume().toString());
		
		return w;
		}

	
	
	public List<String> getSampleIds()
		{
		return sampleIds;
		}

	public List<String> getExpIds()
		{
		return expIds;
		}

	public List<String> getAssayIds()
		{
		return assayIds;
		}

	public String getInstrumentId()
		{
		return instrumentId;
		}

	public BigDecimal getVolume()
		{
		return volume;
		}

	public String getVolUnits()
		{
		return volUnits;
		}

	public int getnRows()
		{
		return nRows;
		}

	public int getnCols()
		{
		return nCols;
		}

	public String getPrepTitle()
		{
		return prepTitle;
		}

	public void setSampleIds(List<String> sampleIds)
		{
		this.sampleIds = sampleIds;
		}

	public void setExpIds(List<String> expIds)
		{
		this.expIds = expIds;
		}

	public void setAssayIds(List<String> assayIds)
		{
		this.assayIds = assayIds;
		}

	public void setInstrumentId(String instrumentId)
		{
		this.instrumentId = instrumentId;
		}

	public void setVolume(BigDecimal volume)
		{
		this.volume = volume;
		}

	public void setVolUnits(String volUnits)
		{
		this.volUnits = volUnits;
		}

	public void setnRows(int nRows)
		{
		this.nRows = nRows;
		}

	public void setnCols(int nCols)
		{
		this.nCols = nCols;
		}

	public void setPrepTitle(String prepTitle)
		{
		this.prepTitle = prepTitle;
		}
	
	
	
	}


/*


String worklistName = "values";
private String selectedPlatform  = null, selectedInstrument = null, selectedMode = "Positive";
private String runDate = "";
private String defaultInjectionVol = "5.0";
private String defaultMethodFileName = "";
private String defaultExperimentId = "", defaultAssayId = "";
private String maxItems = "50"; 
private Boolean allSelected = false;
private Boolean openForUpdates = true;
private boolean plateWarningGiven = false, plateWarningGivenTwice = false;

private ArrayList <String> sampleNamesArray = new ArrayList<String>();
private List <WorklistItemSimple> items = new ArrayList <WorklistItemSimple>();
private List <WorklistSampleGroup> sampleGroupsList = new ArrayList<WorklistSampleGroup>();
private List <WorklistControlGroup> controlGroupsList = new ArrayList<WorklistControlGroup>();
private List<String> controlIds = new ArrayList <String>();

PlateListHandler plateListHandler; 
int nPlates;	
int rowsPerPlate = 6, colsPerPlate = 9;  */
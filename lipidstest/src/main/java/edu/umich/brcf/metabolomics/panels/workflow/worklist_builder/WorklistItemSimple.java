////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  WorklistItemSimple.java
//  Written by Jan Wigginton
//  February 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import edu.umich.brcf.shared.util.interfaces.ICommentObject;
import edu.umich.brcf.shared.util.interfaces.IWriteConvertable;
import edu.umich.brcf.shared.util.structures.SelectableObject;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;



public class WorklistItemSimple extends SelectableObject implements Serializable, IWriteConvertable, ICommentObject
	{
	private boolean representsUserDefinedControl = false;
	private String nameForUserControlGroup = "";
	private String shortNameForUserControlGroup = "";// issue 346
	
	private static final long serialVersionUID = 5062930960041066301L;

	private WorklistSimple parent;
	private WorklistGroup group;

	private String workListName;
	private String methodFileName, outputFileName, outputFileDir;
	private String overrideMethod;
    private String relatedSample;
	private String randomIdx;
	private String sampleName = "", sampleType, samplePosition,
			 shortSampleName = ""; // issue 268
	private String sampleTypeTagForFilename = "";
    public String sampleWorklistLabel; // issue 411
	private String injectionVolume;

	private String rackCode, plateCode;
	private String rackPosition, platePosition, vialPosition;

	private String experimentId;
	private String comments = "";

	private Boolean representsControl = false;

	private Boolean isDeleted, isHandEdited;

	int belongsToPlate;

	public WorklistItemSimple()
		{
		this("", null, null);
		}

	public WorklistItemSimple(String sn, WorklistSimple w, WorklistGroup g)
		{
		this(sn, "", false, w, g);
		}

	public WorklistItemSimple(String sn, String swl, WorklistSimple w,
			WorklistGroup g)
		{
		this(sn, swl, false, w, g);
		}

	public WorklistItemSimple(String sn, String swl, Boolean rc,
			WorklistSimple w, WorklistGroup g)
		{
		parent = w;
		group = g;
		setSampleName(sn);
		workListName = "Worklist";
		randomIdx = "";
		isDeleted = isHandEdited = false;
		representsControl = rc;
		sampleTypeTagForFilename = "";
		resetOptions();
		}

	// Issue 347 
	WorklistItemSimple makeCopy()
	    {
	    WorklistItemSimple newItem = new WorklistItemSimple();
	    newItem.comments = comments;
	    newItem.experimentId = experimentId;
	    newItem.group = group;
	    newItem.isDeleted = isDeleted;
	    newItem.isHandEdited = isHandEdited;
	    newItem.methodFileName = methodFileName;
	    newItem.outputFileDir = outputFileDir;
	    newItem.outputFileName = outputFileName;
	    newItem.overrideMethod = overrideMethod;
	    newItem.parent = parent;
	    newItem.plateCode = plateCode;
	    newItem.platePosition = platePosition;
	    newItem.rackPosition = rackPosition;
	    newItem.rackCode = rackCode;
	    newItem.randomIdx = randomIdx;
	    newItem.representsControl = representsControl;
	    newItem.representsUserDefinedControl = representsUserDefinedControl;
	    newItem.nameForUserControlGroup = nameForUserControlGroup;	
	    // item 346
	    newItem.shortNameForUserControlGroup = nameForUserControlGroup.length() > 9 ?nameForUserControlGroup.substring(0,9) : nameForUserControlGroup;
	    newItem.sampleName = sampleName;
	    //Issue 268
	    newItem.shortSampleName = sampleName.length() > 9 ? sampleName.substring(0, 9) : sampleName;	
	    newItem.samplePosition = samplePosition;
	    newItem.sampleType = sampleType;
	    newItem.sampleTypeTagForFilename = sampleTypeTagForFilename;
	    newItem.sampleWorklistLabel = sampleWorklistLabel;
	    newItem.vialPosition = vialPosition;
	    newItem.workListName = workListName;
	    return newItem;
	    }



	public void setSampleTypeTagForFilename(String tag)
		{
		this.sampleTypeTagForFilename = tag;
		}

	public String getSampleTypeTagForFilename()
		{
		return sampleTypeTagForFilename;
		}
	
	// issue 426
	public void setRelatedSample(String relatedSample)
		{
		this.relatedSample = relatedSample;	
		}
	
	// issue 426
	public String getRelatedSample()
	    {
		return relatedSample;
		}
	

	public void resetOptions()
		{
		overrideMethod = "";
		methodFileName = parent == null ? "" : parent.getDefaultMethodFileName();
		outputFileName = "";
		outputFileDir = "";

		experimentId = "";
		injectionVolume = parent == null ? "11" : parent.getDefaultInjectionVol();
		sampleType = "Unknown";
		samplePosition = "";
		rackCode = "";
		rackPosition = "";
		plateCode = "";
		platePosition = "";
		vialPosition = "";
		}

	public String getExperimentId()
		{
		return experimentId;
		}

	public void setExperimentId(String id)
		{
		experimentId = id;
		}

	public void setWorkListName(String wln)
		{
		workListName = wln;
		}

	public String getWorklistName()
		{
		return workListName;
		}

	public String getOutputFileName()
		{
		return outputFileName;
		}

	public String getOutputFileDir()
		{
		return outputFileDir;
		}

	public String getOverrideMethod()
		{
		return overrideMethod;
		}

	public void setMethodFileName(String fn)
		{
		methodFileName = fn;
		}

	public String getMethodFileName()
		{
		return methodFileName;
		}

	public void setOutputFileName(String fn)
		{
		outputFileName = fn;
		}

	public void setOutputFileDir(String fn)
		{
		outputFileDir = fn;
		}

	public void setOverrideMethod(String om)
		{
		overrideMethod = om;
		}

	public String getSampleName()
		{
		return sampleName;
		}
	
	// issue 268
	public String getShortSampleName()
	    {
	    return shortSampleName;
	    }
	
	public void setShortSampleName(String shortSample)
        {
        this.shortSampleName = shortSample;
        }

	public String getSampleWorklistLabel()
		{
		return sampleWorklistLabel;
		}

	public String getSampleType()
		{
		return sampleType;
		}

	public String getSamplePosition()
		{
		return samplePosition;
		}

	public void updateSampleName()
		{
		setSampleName(this.getSampleOrControlId());
		}
	
	public void setSampleName(String sid)
		{
		sampleName = sid;

		String dts = "";
		if (parent == null)
			return;
		
		try
			{
			
			Date date = DateUtils.dateFromDateStr(parent.getRunDate(), "mm/dd/yy");
			String fullString = DateUtils.dateAsFullString(date);
			dts = DateUtils.grabYYYYmmddString(fullString);
			//System.out.println("In sample name rebuild" + dts);
			//dts = DateUtils.grabYYYYmmddString(parent.getRunDate());
			String sampleDescription = sid; // (representsControl ? sid :
											// sampleService.sampleDescriptionForSampleId(sid));
			setSampleWorklistLabel(parent.getSelectedPlatform().equals("absciex") ? (dts + "_" + sampleDescription) : sampleDescription);
			} 
		catch (ParseException e)
			{
			setSampleWorklistLabel("Error in default date field");
			}
		}

	public String getSampleOrControlId()
		{
		return "";
		
		/*if (StringUtils.isEmptyOrNull(getSampleWorklistLabel())) return "";
		
		String [] ids = StringUtils.splitAndTrim(getSampleWorklistLabel(), "_");
		if (ids.length > 1)
			return ids[1];
		
		return ""; */
		}

	
	public void setSampleWorklistLabel(String sn)
		{
		sampleWorklistLabel = sn + (representsControl ? "(" + ")" : "");
		}

	public void setSampleType(String st)
		{
		sampleType = st;
		}

	public void setSamplePosition(String sp)
		{
		samplePosition = sp;
		}

	public String getRandomIdx()
		{
		return randomIdx;
		}

	public void setRandomIdx(Integer i)
		{
		randomIdx = "" + i;
		}

	public void setRandomIdx(String idx)
		{
		randomIdx = idx;
		}

	public String getInjectionVolume()

		{
		return injectionVolume;
		}

	public void setInjectionVolume(String v)
		{
		try
			{
			// Ersatz validator...
			
			// Issue 464
			if (!StringUtils.isNullOrEmpty(v))
				Double.parseDouble(v);
			injectionVolume = v;
			} 
		catch (NumberFormatException e) { }
		}
	

	public String getRackCode()
		{
		return rackCode;
		}

	public String getPlateCode()
		{
		return plateCode;
		}

	public String getRackPosition()
		{
		return rackPosition;
		}

	public String getPlatePosition()
		{
		return platePosition;
		}

	public String getVialPosition()
		{
		return vialPosition;
		}

	public void setRackCode(String code)
		{
		rackCode = code;
		}

	public void setPlateCode(String code)
		{
		plateCode = code;
		}

	public void setRackPosition(String pos)
		{
		rackPosition = pos;
		}

	public void setPlatePosition(String pos)
		{
		platePosition = pos;
		}

	public void setVialPosition(String pos)
		{
		vialPosition = pos;
		}

	public Boolean getRepresentsControl()
		{
		return representsControl;
		}

	public void setRepresentsControl(Boolean b)
		{
		representsControl = b;
		}

	public Boolean getIsDeleted()
		{
		return isDeleted;
		}

	public void setIsDeleted(Boolean idel)
		{
		isDeleted = idel;
		}

	public Boolean getIsHandEdited()
		{
		return isHandEdited;
		}

	public void setIsHandEdited(Boolean ihe)
		{
		isHandEdited = ihe;
		}

	public String getComments()
		{
		return comments;
		}

	public void setComments(String comments)
		{
		this.comments = comments;
		}

	@Override
	public String toCharDelimited(String delimiter)
		{
		if (delimiter.contains(","))
			{
			if (getIsDeleted())
				return null;

			return parent.getSelectedPlatform().equals("absciex") ? writeInAbsciexFormat(',') : writeInAgilentFormat(',');
			}
		
		return parent.getSelectedPlatform().equals("absciex") ? writeInAbsciexFormat('\t') : writeInAgilentFormat('\t');
		}

	@Override
	public String toExcelRow()
		{
		return null;
		}

	public String writeInAbsciexFormat(char separator)
		{
		StringBuilder sb = new StringBuilder();

		sb.append(this.getRandomIdx().toString() + separator);
		sb.append(this.getSampleWorklistLabel() + separator);

		sb.append(this.getRackCode() + separator);
		sb.append(this.getRackPosition() + separator);
		sb.append(this.getPlateCode() + separator);
		sb.append(this.getPlatePosition() + separator);
		sb.append(this.getVialPosition() + separator);

		sb.append(this.getMethodFileName() + separator);
		sb.append(this.getOutputFileName() + separator);
		sb.append(this.getInjectionVolume());

		return sb.toString();
		}

	public String writeInAgilentFormat(char separator)
		{
		StringBuilder sb = new StringBuilder();

		sb.append(this.getRandomIdx().toString() + separator);
		sb.append(this.getSampleName() + separator);
		sb.append(this.getSamplePosition() + separator);
		sb.append(this.getInjectionVolume() + separator);
		sb.append(this.getMethodFileName() + separator);
		sb.append(this.getOverrideMethod() + separator);
		sb.append(this.getOutputFileName());

		return sb.toString();
		}

	public WorklistGroup getGroup()
		{
		return group;
		}
	
	// issue 347
	public Boolean getRepresentsUserDefinedControl() 
	    {
	    return representsUserDefinedControl;
	    }

    public void setRepresentsUserDefinedControl(Boolean representsUserDefinedControl) 
	    {
	    this.representsUserDefinedControl = representsUserDefinedControl;
	    }
    
    public String getNameForUserControlGroup() 
		{
		return nameForUserControlGroup;
		}
    
    // issue 346
    public String getShortNameForUserControlGroup() 
	    {
	return shortNameForUserControlGroup;
	    }

    // issue 346
	public void setShortNameForUserControlGroup(String shortNameForUserControlGroup) 
	    {
	    this.shortNameForUserControlGroup = shortNameForUserControlGroup;
	    }
	
    
	public void setNameForUserControlGroup(String nameForUserControlGroup) 
		{
		this.nameForUserControlGroup = nameForUserControlGroup;
		}
	}

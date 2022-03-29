////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  WorklistItemSimple.java
//  Written by Jan Wigginton
//  February 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.umich.brcf.shared.util.interfaces.ICommentObject;
import edu.umich.brcf.shared.util.interfaces.IWriteConvertable;
import edu.umich.brcf.shared.util.structures.SelectableObject;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;


public class WorklistItemSimple extends SelectableObject implements Serializable, IWriteConvertable, ICommentObject
	{
	// issue 166
	private boolean representsUserDefinedControl = false;
	private String nameForUserControlGroup = "";
	private String shortNameForUserControlGroup = "";// issue 346
	private String researcherName ; // issue 166
	private String sampleIndex ; // issue 166
	private static final long serialVersionUID = 5062930960041066301L;
	private WorklistSimple parent;
	private WorklistGroup group;
	private String workListName;
	private String methodFileName, outputFileName, outputFileDir;
	private String overrideMethod;
    private String relatedSample;
	private String randomIdx;
	private String sampleName = "", sampleType, samplePosition,
			 shortSampleName = "", mpQcmpName = ""; // issue 268 issue 17
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
	private int direction;
	private Integer customLoadOrder;
	
	// issue 29
	public int getDirection ()
		{
		return this.direction;	
		}
		
	//issue 29
	public void setDirection (int vDirection)
		{
	    this.direction = vDirection;	
		}
	
	// issue 212
	public Integer getCustomLoadOrder ()
		{
		return this.customLoadOrder;	
		}
		
	//issue 212
	public void setCustomLoadOrder (Integer vcustomLoadOrder)
		{
	    this.customLoadOrder = vcustomLoadOrder	;
		}

	// issue 17
	public String getMpQcmpName ()
		{
		return this.mpQcmpName;	
		}
	
	//issue 17
	public void setMpQcmpName (String vMpQcmpName)
		{
	    this.mpQcmpName = vMpQcmpName;	
		}
	
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
	
	// issue 166
	public String getResearcherName()
	    {
		return researcherName;
		}
	
	// issue 166
	public void setResearcherName(String researcherName)
		{
		this.researcherName = researcherName;	
		}
	
	// issue 166
	public String getSampleIndex()
	    {
		return sampleIndex;
		}
	
	// issue 166
	public void setSampleIndex(String sampleIndex)
		{
		this.sampleIndex = sampleIndex;	
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
			// issue 179
			if (parent.getSelectedPlatform().equals("absciex"))
			// Issue 464
				{
				if (!StringUtils.isNullOrEmpty(v))
					Double.parseDouble(v);
				}
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

	// issue 32
	public String grabDataFileWithCustomDirectory()
		{
		if (this.getOutputFileName().indexOf("\\") < 0 )
	        return (StringUtils.isEmptyOrNull(parent.getCustomDirectoryStructureName()) ? " " : parent.getCustomDirectoryStructureName()) + "\\" + this.getOutputFileName();
		else
			{
			String [] fileNameArray = StringUtils.splitAndTrim(this.getOutputFileName(), "\\\\");	
			String lastPartDataFile = fileNameArray[fileNameArray.length -1];
			return (StringUtils.isEmptyOrNull(parent.getCustomDirectoryStructureName()) ? " " : parent.getCustomDirectoryStructureName()) + "\\" + lastPartDataFile;
			}			
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

	// issue 25
	// issue 166
	// issue 212
	public String calcPosIndicator (WorklistItemSimple wI, WorklistSimple ws)
		{
		List <String> sampleNameListNoControls = new ArrayList <String> ();
		int idxSamplePos = 0;
		
		if (wI.getRepresentsControl())
			return "";
		else
			{
			//for (String lSampleName : this.getGroup().getParent().getSampleNamesArray())
			for (String lSampleName : ws.getSampleNamesArray())
				{
				if (lSampleName.startsWith("S0"))
					sampleNameListNoControls.add(lSampleName);
				}
			//return String.valueOf(sampleNameListNoControls.indexOf(this.sampleName) + 1 + Integer.valueOf(wI.getGroup().getParent().getStartSequence()) -1 );
			return String.valueOf(sampleNameListNoControls.indexOf(this.sampleName) + 1 + Integer.valueOf(ws.getStartSequence()) -1 );
			}
		}

	// issue 166
	public String calcCommentContent (String theSampleId)
		{
		// issue 201
		if (!this.getRepresentsControl())
			{
		//	System.out.println("this is the researcher name:" + this.getResearcherName());
			return this.getResearcherName();
			}
		else
			{
			WorklistControlGroup wg = this.getGroup().getParent().getControlGroupsList().get(0);					
			String controlCode = this.getSampleName().indexOf("-") == -1 ? this.getSampleName() : this.getSampleName().substring(0,this.getSampleName().lastIndexOf("-"));
			for (WorklistControlGroup wwg : this.getGroup().getParent().getControlGroupsList())
				{
				// issue 179
				if (wwg.getControlType() == null)
					{
					continue;
					}
				// issue 201
				if (wwg.getControlType().contains(controlCode))
					{ 
					String preControltype = controlCode.equals("CS00000MP-Pre") ? "Master Pool   (CS00000MP)" : wwg.getControlType();
					switch (controlCode)
						{
						case "CS00000MP-Pre": preControltype = "Master Pool   (CS00000MP)";
						     break;
						case "R00CHRUR1-Pre": preControltype = "Chear - urine  (R00CHRUR1)";
						     break;
						case "R00CHRPL1-Pre": preControltype = "Chear - plasma (R00CHRPL1)"; 
						     break;
						case "CS000BPM1-Pre": preControltype = "Batch Pool.M1 (CS000BPM1)";
					     	break;
						case "CS000BPM2-Pre": preControltype = "Batch Pool.M2 (CS000BPM2)";
				     		break;
						case "CS000BPM3-Pre": preControltype = "Batch Pool.M3 (CS000BPM3)";
				     		break;
						case "CS000BPM4-Pre": preControltype = "Batch Pool.M4 (CS000BPM4)";
				     		break;
						case "CS000BPM5-Pre": preControltype = "Batch Pool.M5 (CS000BPM5)";
				     		break;	
						case "CS00000SB-Pre": preControltype = "Solvent Blank (CS00000SB)";
			     			break;	
						case "CS000BPM1": preControltype = "Batch Pool.M1 (CS000BPM1)";
				     		break;
						case "CS000BPM2": preControltype = "Batch Pool.M2 (CS000BPM2)";
			     			break;
						case "CS000BPM3": preControltype = "Batch Pool.M3 (CS000BPM3)";
			     			break;
						case "CS000BPM4": preControltype = "Batch Pool.M4 (CS000BPM4)";
			     			break;
						case "CS000BPM5": preControltype = "Batch Pool.M5 (CS000BPM5)";
							break;	
						case "CS00000MP": preControltype = "Master Pool   (CS00000MP)";
				     		break;
						case "R00CHRUR1": preControltype = "Chear - urine  (R00CHRUR1)";
					     	break;
					    case "R00CHRPL1": preControltype = "Chear - plasma (R00CHRPL1)"; 
					     	break;   
					     	// issue 207
					    case "CS00000SB": preControltype = "Solvent Blank (CS00000SB)";
					    	break;
						default: preControltype =   wwg.getControlType();
						     break;
						}
					//return wwg.getControlType().substring(0, wwg.getControlType().lastIndexOf("(")).replace(",", "");
					return preControltype.substring(0, preControltype.lastIndexOf("(")).replace(",", "");	
					}
				}
			return wg.getControlType().replace(",", "").replace("-Pre", "");		
			}
		}
	public String writeInAgilentFormat(char separator)
	// issue 179
		{
		int dashIndex;
		int stdIndex;
		String stdIntString;
		StringBuilder sb = new StringBuilder();       
		// issue 179 sb.append(this.getRandomIdx().toString() + separator);
		sb.append(this.getSampleName() + separator);
		sb.append(this.getSamplePosition() + separator);
		// issue 181
		sb.append((StringUtils.isNullOrEmpty(this.getMethodFileName()) ? " " : this.getMethodFileName())  + separator); 
		sb.append((parent.getIsCustomDirectoryStructure() ? this.grabDataFileWithCustomDirectory() : this.getOutputFileName()  )+ separator);  
		sb.append(((this.getSampleName().contains("CS000STD") || this.getSampleName().contains("CS00STD")) ? "Calibration" : "Sample") + separator);
		
		if ((this.getSampleName().contains("CS000STD") || this.getSampleName().contains("CS00STD")) && this.getSampleName().contains("-"))
			{
			dashIndex = this.getSampleName().lastIndexOf("-");
			stdIndex  =  this.getSampleName().indexOf("STD") + 3;
			stdIntString = this.getSampleName().substring(stdIndex, dashIndex);
			}
		else 
			stdIntString = "";
		sb.append(stdIntString + separator);
		sb.append(this.getInjectionVolume() + separator); 
		// issue 179
	    Integer countOfSamples = this.getGroup().getParent().countOfSamplesForItems(this.getGroup().getParent().getItems());
		Integer endingIndex = Integer.parseInt(this.getGroup().getParent().getStartSequence()) + countOfSamples-1;
		String endingIndexStr = endingIndex.toString();
		String theIndex = this.getRepresentsControl() ? "" : String.format("%1$" + endingIndexStr.length() + "s" , calcPosIndicator(this, this.getGroup().getParent())).replace(' ', '0');	
		//sb.append(theIndex +  (this.getRepresentsControl() ? "" : "_") + calcCommentContent(this.getSampleName()) +  separator);// issue 166		
		sb.append(theIndex +  (this.getRepresentsControl() ? "" : "_") + calcCommentContent(this.getSampleName())  );// issue 166
		// issue 25
		// issue 32		
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
	
	// issue 215
		public String calcCommentToolTip (WorklistSimple ws, WorklistItemSimple wi)
			{
			Integer countOfSamples = ws.countOfSamplesForItems(ws.getItems());
			Integer endingIndex = Integer.parseInt(ws.getStartSequence()) + countOfSamples-1;
			String endingIndexStr = endingIndex.toString();
			String theIndex = wi.getRepresentsControl() ? "" : String.format("%1$" + endingIndexStr.length() + "s" , calcPosIndicator(this, ws)).replace(' ', '0');	
			String theComment = theIndex +  (wi.getRepresentsControl() ? "" : "_") + calcCommentContent(wi.getSampleName())  ;//			
			return theComment;
			}
	
	}

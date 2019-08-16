////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  WorklistControlGroup.java
//  Written by Jan Wigginton
//  February 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.umich.brcf.shared.util.METWorksException;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.io.StringUtils;


public class WorklistControlGroup extends WorklistGroup implements Serializable
	{
	private static final long serialVersionUID = 2863817067468270754L;
	private String 	controlType = null, quantity, direction, relatedSample = null;	
	private Integer intQuantity;
	private boolean standardNotAddedControl = false ;
	
	
	public WorklistControlGroup()
		{
		this((WorklistSimple) null);
		}
	
	
	WorklistControlGroup(WorklistSimple w)
		{
		this("", null, "1", "Before", null, w);
		}
	
	
	//new WorklistControlGroup(null, label, "1", "Before", null, worklist);
	WorklistControlGroup(String eid, String type, String q, String dir, String rs, WorklistSimple w)
		{
		super(w, eid);
		
		controlType = type;
		quantity = q;
		intQuantity = Integer.parseInt(quantity);
		
		direction = dir;
		relatedSample = rs;
		}

	private WorklistItemSimple newControlItem(int i)
		{
		String name = this.getIdForControl(getControlType(), i);
		String label = this.getLabelForControl(getControlType(), i);
		WorklistItemSimple w = new WorklistItemSimple(name, label, true, parent, this);	
		w.setSampleType(getControlType());
		return w;
		}

	private String getLabelForControl(String fullname, int i)
		{
		return StringParser.parseId(fullname) + "." + (i+1) ;
		}

	// issue 394
	private String getControlTypeAbbreviation(String ct)
	    {
	    switch (ct)
	        {
	        case "Test Pool" :     return "TP";
	        case "Standard Mix" :  return "SM";
	        case "Pooled Plasma" : return "PP";
	        case "Blank" :         return "BL";
	        case "Process Blank" : return "PB";
	        case "Solvent Blank" : return "SB";
	        case "RC Plasma" : 
	        case "Matrix Blank" :  return "MB";
	        case "Neat Blank" :    return "NB";
	        case "Master Pool" : 
	        case "Pool.0" : return "P0";
	        case "Batch Pool.M1" : 
	        case "Pool.1" : return "P1";
	        case "Pool.1b" : return "P1b"; // issue 302
	        case "Batch Pool.M2" : 
	        case "Pool.2" : return "P2";
	        case "Batch Pool.M3" : 
	        case "Pool.3" : return "P3";
	        case "Batch Pool.M4" : 
	        case "Pool.4" : return "P4";
	        case "Batch Pool.M5" : 
	        case "Pool.5" : return "P5";
	        case "Other Pool.0" : 
	        case "Pool.6" : return "P6";
	        case "Other Pool.1" : 
	        case "Pool.7" : return "P7";
	        case "Other Pool.2" : 
	        case "Pool.8" : return "P8";
	        case "Other Pool.3" : 
	        case "Pool.9" : return "P9";
	        case "Standard.0" : 
	        case "Standard 0" :  return "S00";
	        case "Standard.1" : 
	        case "Standard 1" :  return "S01";
	        case "Standard.2" : 
	        case "Standard 2" :  return "S02";
	        case "Standard.3" : 
	        case "Standard 3" :  return "S03";
	        case "Standard.4" :
	        case "Standard 4" :  return "S04";
	        case "Standard.5" :
	        case "Standard 5" :  return "S05";
	        case "Standard.6" :
	        case "Standard 6" :  return "S06";
	        case "Standard.7" :
	        case "Standard 7" :  return "S07";
	        case "Standard.8" :
	        case "Standard 8" :  return "S08";
	        case "Standard.9" :
	        case "Standard 9" :  return "S09";
	        case "Standard.10" :
	        case "Standard 10" : return "S10";
	        case "Standard.11" :
	        case "Standard 11" : return "S11";
	        case "Standard.12" :
	        case "Standard 12" : return "S12";
	        case "Reference 1 - urine" :  return "R91";
	        case "Reference 2 - urine" :  return "R92";
	        case "Reference 1 - plasma" :  return "R98";
	        case "Reference 2 - plasma" :  return "R99";
	        default : return "UNK";
	        }
	    }
	
	
	private String getIdForControl(String fullname, int i)
		{
		return StringParser.parseId(fullname) + "-" + (i + 1); 
		}
	
	
	private int calculateInsertPoint()
		{
		int val = getParent().getItems().size() > 0 ? -1 : -2;
		
		if (relatedSample == null && "Before".equals(getDirection()))
			return 0;
		
		if (relatedSample == null && "After".equals(getDirection()))
			return getParent().getItems().size();
		// issue 456
        String relatedSampleTrimmed = relatedSample;
		if (relatedSample.contains("-"))
			{
			int indexOfDash = relatedSample.lastIndexOf("-");
			relatedSampleTrimmed = relatedSample.substring(0,  indexOfDash) + "-" + ( Integer.parseInt(relatedSample.substring(indexOfDash + 1)));
			}
		for (int i =0; i < getParent().getItems().size(); i++)
			if (relatedSampleTrimmed.equals(getParent().getItem(i).getSampleName()))
				return this.direction.equals("After") ? i + 1 : i;
		return val;
		}
	
		
	public int getInsertPointSafe() throws METWorksException
		{
		int val = calculateInsertPoint();
		
		if (val == -1 || val == -2)
			{
			throw new METWorksException("Can't calculate insertion point for related sample " + relatedSample 
					+ " in worklist " + parent.toString() +  " Value: " + val);
			}
		
		return val;
		}
	
	
	// issue 393
    public boolean getStandardNotAddedControl ()
        {
    	return this.standardNotAddedControl;
        }
    
    // issue 393
    public void setStandardNotAddedControl (boolean standardNotAddedControl)
        {
	    this.standardNotAddedControl = standardNotAddedControl;
        }
    
	
	public List <WorklistItemSimple> getWorklistItemsForGroup(int startIdx, int controlGroupIdx)
		{
		List <WorklistItemSimple> list = new ArrayList <WorklistItemSimple>();
		
		if (StringUtils.isEmptyOrNull(this.getControlType()))
			return list;
		
		for (int i = 0; i < this.getIntQuantity(); i++)
			{
			WorklistItemSimple item = newControlItem(i + startIdx);

			item.setSampleTypeTagForFilename(this.getControlTypeAbbreviation(StringParser.parseName(getControlType())));
			String outname = parent.grabOutputFileName(item.getSampleName(), item);
			item.setOutputFileName(outname);

			item.setInjectionVolume(parent.getDefaultInjectionVol());
			list.add(item);
			}
		
		return list;
		}
	
	
	private String calculateRackPosition(int controlGroupIdx)
		{
		int nSamples = getParent().countSamples();
		int nPlateRows = 3, nPlateCols = 3;
		int slot = controlGroupIdx + nSamples;	
		
		String plateRow = "", plateCol = "";
	
		Integer intPlateRow =  (int) Math.floor( slot / nPlateCols);
		Integer intPlateCol = (int) ((slot + 1)-  intPlateRow * nPlateCols);
		
		switch (intPlateRow)
			{
			case 0 :  plateRow = "A"; break;
			case 1 :  plateRow = "B"; break;
			case 2 :  plateRow = "C"; break;
			case 3 :  plateRow = "D"; break;
			case 4 :  plateRow = "E"; break;
			case 5 :  plateRow = "F"; break;
			default : plateRow = "ERROR";
			}
		
		return (plateRow + intPlateCol.toString());
		}
	
	
	public String getControlType()
		{
		return controlType;  //(controlType != null ? controlType.trim() : "");
		}
	
	public void setControlType(String ct)
		{
		controlType = ct;
		}

	
	public String getQuantity()
		{
		return (quantity != null ? quantity.trim() : "");
		}

	public void setQuantity(String q)
		{
		quantity = q; 
		intQuantity = Integer.parseInt(quantity);
		}
	
	public Integer getIntQuantity()
		{
		return intQuantity;
		}
	
	
	public String getDirection()
		{
		return (direction != null ? direction.trim() : "");
		}
	
	public void setDirection(String dir)
		{
		direction = dir;
		}
	
	public String getRelatedSample()
		{
		return (relatedSample != null ? relatedSample.trim() : "");
		}
	
	public void setRelatedSample(String rs)
		{
		relatedSample = rs;
		}
	}

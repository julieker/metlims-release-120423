// AssaySelectionSet.java
// Written by Jan Wigginton, October 2015

package edu.umich.brcf.shared.util.datacollectors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.structures.SelectableObject;
import edu.umich.brcf.shared.util.StringParser;



public class AssaySelectionSet implements Serializable
	{
	@SpringBean
	SampleService sampleService;
	
	@SpringBean
	AssayService assayService;
	
	
	
	private String expId, assayLabel; // selectedAssay;
	boolean allSelected = true;
	private List<SelectableObject> sampleSelection = new ArrayList<SelectableObject>();
	
	public AssaySelectionSet(String eid)
		{
		expId = eid;
		Injector.get().inject(this);
	
		assayLabel = "";
		//allSelected = true;
		
		List<String> sampleIds = sampleService.sampleIdsForExpId(expId);
		for (int i = 0;i < sampleIds.size(); i++)
			{
			Sample s;
			try {  s = sampleService.loadById(sampleIds.get(i)); }
			catch (Exception e) { continue; } 
			
			SelectableObject obj;
			if (s != null)
				{
				obj = new SelectableObject(s);
				obj.setSelected(true);
				sampleSelection.add(obj);
				}
			}
		 this.updateSelectionForAssay(true);		    
		}
	
	public String getSelectedAssayId()
		{
		return StringParser.parseId(assayLabel);
		}
	
	public List<String> getSelectedAssayIds()
		{
		List<String> lst = new ArrayList<String>();
		
		for (int i = 0; i < this.getSampleSelection().size(); i++)
			if (getSampleSelection().get(i).isSelected())
				lst.add(((Sample) getSampleSelection().get(i).getSelectionObject()).getSampleID());
		
		return lst;
		}
	
	// JAK issue 249
	public void updateSelectionForAssay(boolean setAll)
		{
		//JAK issue 249
		String selectedId = StringParser.parseId(this.getAssayLabel());	
		updateSelectionForAll(false);
		List<String> lst = new ArrayList<String>();
		lst = assayService.samplesAssociatedWithAssay(expId, selectedId);
		for (int i = 0; i < this.sampleSelection.size(); i++)
		    {
			if (lst.indexOf( ((Sample) sampleSelection.get(i).getSelectionObject()).getSampleId()) >= 0)
			   sampleSelection.get(i).setSelected(true);
		    }
		}

	public void updateSelectionForAll(boolean setAll)
		{
		//this.setAllSelected(allSelected);
		for (int i = 0; i < this.sampleSelection.size(); i++)
			sampleSelection.get(i).setSelected(setAll);
		}
	

	
	public String getSampleId(int i)
		{
		try {
			return ((Sample)sampleSelection.get(i).getSelectionObject()).getSampleID();
			}
		catch (IndexOutOfBoundsException e)  { return ""; }
		}

	public boolean getSampleSelected(int i)
		{
		try { return sampleSelection.get(i).isSelected();  }
		catch (IndexOutOfBoundsException e) { return false; }
		}


	public String getExpId() 
		{
		return expId;
		}

	public void setExpId(String expId) 
		{
		this.expId = expId;
		}
	
	public List<SelectableObject> getSampleSelection()
		{
		return sampleSelection;
		}
	
	public void setSampleSelection(List<SelectableObject> selection)
		{
		sampleSelection = selection;
		}
	
	public String getAssayLabel()
		{
		return assayLabel;
		}
	
	public void setAssayLabel(String label)
		{
		assayLabel = label;
		}

	public String getAssayId() 
		{
		return StringParser.parseId(assayLabel);
		}

	public String getAssayName() 
		{
		return StringParser.parseName(assayLabel);
		}
	
	public boolean getAllSelected()
		{
		return allSelected;
		}

	public void setAllSelected(boolean setAll)
		{
		allSelected = setAll;
		}	
	
	public Boolean assayIsNew()
		{
		List<String> lst = assayService.allAssayIdsForExpId(expId, false);
		
		String selectedId = StringParser.parseId(assayLabel);
		
		for (int i =0; i < lst.size(); i++)
			{
			if (selectedId != null && lst.get(i) != null && selectedId.trim().equals(lst.get(i).trim()))
				return false;
			}
		return true;
		}
	
	public int countNSelected()
		{
		int ct = 0;
		for (int i = 0; i < sampleSelection.size(); i++)
			if (sampleSelection.get(i).isSelected())
				ct++;
				
		return ct;		
		}
	}
// ControlDTO.java
// Written by Jan Wigginton
// March 2015

package edu.umich.brcf.shared.layers.dto;

import java.io.Serializable;

import edu.umich.brcf.shared.layers.domain.Control;



public class ControlDTO implements Serializable 
	{
	public static ControlDTO instance(Control control) 
		{
		return new ControlDTO(control.getControlId(), control.getExp().getExpID(), 
			control.getAssay().getAssayId(), control.getControlTypeId());
		}
	
	
	public static ControlDTO instance(String controlId, String expId, String assayId, String controlTypeId) 
		{
		return new ControlDTO(controlId, expId, assayId, controlTypeId); 
		}

	
	private String controlId;
	private String expId;
	private String assayId;
	private String controlTypeId;


	private ControlDTO(String controlId, String expId, String assayId, String controlTypeId)
		{
		this.controlId = controlId;
		this.assayId = assayId;
		this.expId = expId;
		this.controlTypeId = controlTypeId;
		}

	
	public ControlDTO() {  } 
	
	
	public String getControlId() 
		{
		return controlId;
		}


	public String getExpId() 
		{
		return expId;
		}

	
	public String getAssayId() 
		{
		return assayId;
		}


	public String getControlTypeId()
		{
		return controlTypeId;
		}

	
	public void setExpId(String eid)
		{
		expId = eid;
		}
	
	
	public void setAssayId(String aid)
		{
		assayId = aid;
		}
	
	
	public void setControlId(String cid)
		{
		controlId = cid;
		}

	
	public void setControlTypeId(String ctid)
		{
		controlTypeId = ctid;
		}
	}


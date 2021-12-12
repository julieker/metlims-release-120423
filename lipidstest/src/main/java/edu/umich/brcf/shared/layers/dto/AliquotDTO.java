
////////////////////////////////////////////////////
// AliquotDTO.java

// Created by by Julie Keros May 28, 2020
////////////////////////////////////////////////////
// Updated by Julie Keros May 11, 2020

package edu.umich.brcf.shared.layers.dto;

// issue 61 2020
import java.io.Serializable;


import java.util.List;


import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.shared.layers.domain.Aliquot;
import edu.umich.brcf.shared.layers.domain.Inventory;
import edu.umich.brcf.shared.layers.domain.Location;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;



// issue 61 2020
// issue 31 2020
public class AliquotDTO implements Serializable
	{
	public static AliquotDTO instance(String aliquotId, 
								       String location,
								       Character status,String labId,
								       String parentId,
								       String sampleId,
								       Integer replicate,Character dry, String cid, String solvent, String aliquotLabel, String notes, String createdBy, Character neat, String neatSolVolUnits, 
								       String ivol, String icon, String dcon, String dConc , String weightedAmount, String dvol, String dConcentrationUnits, String weightedAmountUnits, String molecularWeight) 
	    {
	    return new AliquotDTO(aliquotId, location,  parentId,replicate,dry,cid, solvent, aliquotLabel, notes, createdBy, neat, neatSolVolUnits, ivol, icon, dcon, weightedAmount, dConc, dvol, dConcentrationUnits, weightedAmountUnits, molecularWeight);
		}
	
    // issue 196
	public static AliquotDTO instance(Aliquot alq)
		{
		// issue 196
		return new AliquotDTO(alq.getAliquotId(), (alq.getLocation()== null ? "" :alq.getLocation().getLocationId()),  (alq.getInventory() == null ? null :alq.getInventory().getInventoryId()),alq.getReplicate(), alq.getDry(), alq.getCompound().getId(), alq.getSolvent(), alq.getAliquotLabel(),alq.getNotes(), alq.getCreatedBy(), alq.getNeat(),alq.getNeatSolVolUnits(), alq.getIvol().toString(), alq.getIcon().toString(), alq.getDcon().toString() , alq.getWeightedAmount().toString(), alq.getDconc().toString(), alq.getDvol() == null ? null :alq.getDvol().toString(), alq.getDConcentrationUnits(), alq.getWeightedAmountUnits(), alq.getMolecularWeight().toString());
		}	
	// issue 61 2020
	private String aliquotId;
	private String volUnits;
	private Location locationObj;
	private String parentId;
	private Integer replicate;
	private Character dry;
	private String cid;
	private List <String> parentIdList;
	private String location;
	private String solvent;
	private String solventText;
	private String otherSolvent;
	private String createDate;
	private String aliquotLabel;
	private Inventory inventoryObj;
	private Compound compoundObj;
	private String notes;
	private String createdBy ;
	private String userName;
	private Character NeatOrDilution;
	private String NeatOrDilutionText;
	private String NeatOrDilutionUnits ;
	private String ivol;
	private String dcon;
	private String dvol;
	private String icon;
	private String weightedAmount;
	private String molecularWeight;
	private String dConc;
	private Boolean isDry;
	private String dConcentrationUnits;
	private String weightedAmountUnits;
	private String deleteReason ;
	private String otherReason ;// issue 79
	private List<String> assayIds;
	private String assayId;
	
	// issue 61 2020
	// issue 31 2020
	// issue 61 2020
	private AliquotDTO(String aliquotId, 
			           String location,
			           String parentId,
			           Integer replicate,Character dry, String cid, String solvent, String aliquotLabel, String notes, String createdBy, Character neat, String neatSolVolUnits, String ivol, String dcon, String icon, String weightedAmount, String dconc, String dvol, String dConcentrationUnits, String weightedAmountUnits, String molecularWeight)
		{
		this.aliquotId = aliquotId;
		this.volUnits = volUnits;
		this.location = location;
		this.parentId = parentId;
		this.replicate = replicate;
		this.dry = dry;
		this.cid = cid;
		this.solvent = solvent;
		this.aliquotLabel = aliquotLabel;
		this.notes = notes;
		this.createdBy = createdBy;
		this.NeatOrDilution = neat;
		this.NeatOrDilutionUnits = neatSolVolUnits;
		this.ivol = ivol;
		this.dcon = dcon;
		this.icon = icon;	
		this.dConc = dConc;
		this.weightedAmount = weightedAmount;
		this.dvol = dvol;
		this.dConcentrationUnits = dConcentrationUnits;
		this.weightedAmountUnits = weightedAmountUnits;
		this.molecularWeight = molecularWeight;
		} 
	public AliquotDTO() { }	
		
	public String getAliquotId() 
		{
		return aliquotId;
		}
	
	public void setAliquotId(String aliquotId) 
		{
		this.aliquotId = aliquotId;
		}
	
	// issue 61 2020
	public String getVolUnits() 
	    {
		return volUnits;
	    }
	// issue 61 2020
	public void setVolUnits(String volUnits) 
	    {
		this.volUnits = volUnits;
	    }	
	public Compound getCompoundObj() 
	    {
		return compoundObj;
	    }
	public void setCompoundObj(Compound compound) 
	    {
		this.compoundObj = compound;
	    }	
	public Location getLocationObj() 
	    {
		return locationObj;
	    }
	public void setLocationObj(Location location) 
	    {
		this.locationObj = location;
	    }
	
	public Inventory getInventoryObj() 
	    {
		return inventoryObj;
	    }
	
	public void setInventoryObj(Inventory inventory) 
	    {
		this.inventoryObj = inventory;
	    }
	 
	public String getParentId() 
		{
		return parentId;
		}

	public void setParentId(String parentId) 
		{
		this.parentId = parentId;
		}
	
	public List<String> getParentIdList() 
		{
		return parentIdList;
		}

	public void setParentId(List <String> parentIdList) 
		{
		this.parentIdList = parentIdList;
		}
			
	// issue 61 2020
	public Integer getReplicate() 
	    {
		return replicate;
	    }
	// issue 61
	public void setReplicate(Integer replicate )
	    {
		this.replicate = replicate;
	    }
	
	public Character getNeatOrDilution() 
	    {
		return NeatOrDilution;
	    }
	
//issue 61
	public void setNeatOrDilution(Character NeatOrDilution )
	    {
		this.NeatOrDilution = NeatOrDilution;
	    }
	
	public String getNeatOrDilutionText() 
	    {
		return NeatOrDilutionText;
	    }
//issue 61
	public void setNeatOrDilutionText(String NeatOrDilutionText )
	    {
		this.NeatOrDilutionText = NeatOrDilutionText;
	    }
		
	// issue 61 2020
	public String getCid() 
	    {
		return cid;
	    }
	
	// issue 61 2020
	public void setCid(String cid) 
	    {
		this.cid = cid;
	    }	
	
	public String getLocation() 
	    {
		return location;
	    }
	
	public void setLocation(String location) 
	    {
		this.location = location;
	    }
	
	// issue 61 2020
    public String getSolvent() 
	    {
		return solvent;
	    }
		
		// issue 61 2020
	public void setSolvent(String solvent) 
	    {
		this.solvent = solvent;
	    }
	
	// issue 61 2020
    public String getSolventText() 
	    {
		return solventText;
	    }
		
		// issue 61 2020
	public void setSolventText(String solventText) 
	    {
		this.solventText = solventText;
	    }
	
	// issue 61 2020
    public String getOtherSolvent() 
	    {
		return otherSolvent;
	    }
		
		// issue 61 2020
	public void setOtherSolvent(String otherSolvent) 
	    {
		this.otherSolvent = otherSolvent;
	    }
	
	// issue 61 2020
    public String getCreateDate() 
	    {
		return createDate;
	    }
		
		// issue 61 2020
	public void setCreateDate(String createDate) 
	    {
		this.createDate = createDate;
	    }
	
	// issue 61 2020
    public String getAliquotLabel() 
	    {
		return aliquotLabel;
	    }
		
		// issue 61 2020
	public void setAliquotLabel(String aliquotLabel) 
	    {
		this.aliquotLabel = aliquotLabel;
	    }
	
	// issue 61 2020
    public String getCreatedBy() 
	    {
		return createdBy;
	    }
		
		// issue 61 2020
	public void setCreatedBy(String createdBy) 
	    {
		this.createdBy = createdBy;
	    }
	
	// issue 61 2020
    public String getNotes() 
	    {
		return notes;
	    }
		
		// issue 61 2020
	public void setNotes(String notes) 
	    {
		this.notes = notes;
	    }
	
	// issue 61 2020
    public String getUserName() 
	    {
		return userName;
	    }
		
		// issue 61 2020
	public void setUserName(String userName) 
	    {
		this.userName = userName;
	    }
	
	// issue 61 2020
    public String getNeatOrDilutionUnits() 
	    {
		return NeatOrDilutionUnits;
	    }
		
		// issue 61 2020
	public void setNeatOrDilutionUnits(String NeatOrDilutionUnits) 
	    {
		this.NeatOrDilutionUnits = NeatOrDilutionUnits;
	    }
	
	// issue 61 2020
    public String getDConcentrationUnits() 
	    {
		return dConcentrationUnits;
	    }
		
		// issue 61 2020
	public void setDConcentrationUnits(String dConcentrationUnits) 
	    {
		this.dConcentrationUnits = dConcentrationUnits;
	    }
	
	// issue 61 2020
    public String getWeightedAmountUnits() 
	    {
		return weightedAmountUnits;
	    }
		
		// issue 61 2020
	public void setWeightedAmountUnits(String weightedAmountUnits) 
	    {
		this.weightedAmountUnits = weightedAmountUnits;
	    }
	
	// issue 61 2020
    public String getIcon() 
	    {
    	return StringUtils.isEmptyOrNull(icon) ? icon :icon.replace(",", "");
	    }
		
		// issue 61 2020
	public void setIcon(String icon) 
	    {
		this.icon = icon;
	    }
	
	// issue 61 2020
    public String getDcon() 
	    {
    	return StringUtils.isEmptyOrNull(dcon) ? dcon :dcon.replace(",", "");
	    }
		
		// issue 61 2020
	public void setDcon(String dcon) 
	    {
		this.dcon = dcon;
	    }
	// issue 61 2020
    public String getIvol() 
	    {
    	return StringUtils.isEmptyOrNull(ivol) ? ivol :ivol.replace(",", "");
	    }
		
		// issue 61 2020
	public void setIvol(String ivol) 
	    {
		this.ivol = ivol;
	    }
	
	// issue 61 2020
    public String getDvol() 
	    {
		return StringUtils.isEmptyOrNull(dvol) ? dvol :dvol.replace(",", "");
	    }
		
		// issue 61 2020
	public void setDvol(String dvol) 
	    {
		this.dvol = dvol;
	    }
	
    public String getWeightedAmount() 
	    {
		return StringUtils.isEmptyOrNull(weightedAmount) ? weightedAmount :weightedAmount.replace(",", "");
	    }
	
	// issue 61 2020
    public void setWeightedAmount(String weightedAmount) 
	    {
		this.weightedAmount= weightedAmount;
	    }
    
    public String getMolecularWeight() 
	    {
    	return StringUtils.isEmptyOrNull(molecularWeight) ? molecularWeight :molecularWeight.replace(",", "");
	    }

// issue 61 2020
    public void setMolecularWeight(String molecularWeight) 
	    {
		this.molecularWeight= molecularWeight;
	    }
	
    public String getDConc() 
	    {
		return StringUtils.isEmptyOrNull(dConc) ? dConc :dConc.replace(",", "");
	    }

//issue 61 2020
	public void setDConc(String dConc) 
	    {
		this.dConc= dConc;
	    }
	
	public Boolean getIsDry() 
	    {
		return isDry;
	    }
	
    //issue 61
	public void setIsDry(Boolean isDry )
	    {
		this.isDry = isDry;
	    }
		
	   // issue 79 2020
    public String getDeleteReason() 
	    {
		return deleteReason;
	    }
		
	// issue 79 2020
	public void setDeleteReason(String deleteReason) 
	    {
		this.deleteReason = deleteReason;
	    }
	   // issue 79 2020
	public String getOtherReason() 
	    {
		return otherReason;
	    }
		
	// issue 79 2020
	public void setOtherReason(String otherReason) 
	    {
		this.otherReason = otherReason;
	    }
	
	
	// issue 100 list of assay ids for an assay in EditAliquot
	public List<String> getAssayIds() 
		{
		return assayIds;
		}

	public void setAssayIds(List <String> assayIds) 
		{
		this.assayIds = assayIds;
		}
	
	// Issue 100 assay id chosen for search in AssayAliquotDetailPanel
	public String getAssayId() 
		{
		return assayId;
		}

	public void setAssayId(String assayId) 
		{
		this.assayId = assayId;
		}
	
	
	}

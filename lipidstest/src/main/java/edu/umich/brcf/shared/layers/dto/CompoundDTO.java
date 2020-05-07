
////////////////////////////////////////////////////
// CompoundDTO.java

// Updated by Julie Keros Mar 4, 2020
////////////////////////////////////////////////////

package edu.umich.brcf.shared.layers.dto;

import java.io.Serializable;
import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.metabolomics.layers.domain.CompoundName;

// issue 27 2020
// issue 31 2020
public class CompoundDTO implements Serializable
	{
	public static CompoundDTO instance(String cid, String chem_abs_number, 
	String smiles, String parentCid, String name, String type, String html, String inchiKey) 
		{
		return new CompoundDTO(cid, chem_abs_number, smiles, parentCid, name, type, html, inchiKey);
		}
	
	// issue 27 2020
	public static CompoundDTO instance(Compound cmpd)
		{
		return new CompoundDTO(cmpd.getCid(), cmpd.getChem_abs_number(), 
		cmpd.getSmiles(),  cmpd.getParent() != null ? cmpd.getParent().getCid() : "", "", "", "",cmpd.getInchiKey());
		}
	
	// issue 27 2020
	public static CompoundDTO instance(Compound cmpd, CompoundName cn)
		{
		return new CompoundDTO(cmpd.getCid(), cmpd.getChem_abs_number(), 
		cmpd.getSmiles(),  cmpd.getParent().getCid(), cn.getName(), cn.getNameType(), cn.getHtml(), cmpd.getInchiKey()); // issue 31 2020
		}
	
	private String cid;
	private String chem_abs_number;
	//private BigDecimal molecular_weight;
	//private String molecular_formula;
	private String smiles;
	//private BigDecimal logP;
	//private BigDecimal nominalMass;
	private String parentCid;
	private String name;
	private String type;
	private String html;
	private String inchiKey;// issue 27 2020
	private String compoundIdentifier="Smiles";// issue 27 2020
	
	// issue 27 2020
	// issue 31 2020
	private CompoundDTO(String cid, String chem_abs_number, String smiles, String parentCid, String name, String type, String html, String inchiKey)
		{
		this.cid = cid;
		this.chem_abs_number = chem_abs_number;
		this.smiles = smiles;
		this.parentCid = parentCid;
		this.name = name;
		this.type = type;
		this.html = html;
		this.inchiKey = inchiKey;
		}
 
	public CompoundDTO() { }

	
	public String getCid() 
		{
		return cid;
		}
	
	public void setCid(String cid) 
		{
		this.cid = cid;
		}
	
	public String getChem_abs_number() 
		{
		return chem_abs_number;
		}
	
	public void setChem_abs_number(String chem_abs_number) 
	    {
		this.chem_abs_number = chem_abs_number;
	    }
	public String getSmiles() 
	    {
		return smiles;
	    }
	public void setSmiles(String smiles) 
	    {
		this.smiles = smiles;
	    }
	// issue 27 2020
	public String getInchiKey() 
	    {
		return inchiKey;
	    }
	// issue 27 2020
	public void setInchiKey(String inchiKey) 
	    {
		this.inchiKey = inchiKey;
	    }
	// issue 27 2020
	public String getCompoundIdentifier() 
	    {
		return compoundIdentifier;
	    }
	// issue 27 2020
	public void setCompoundIdentifier(String compoundIdentifier) 
	    {
		this.compoundIdentifier = compoundIdentifier;
	    }
// issue 58 get rid of human_rel
	public String getParentCid() 
	    {
		return parentCid;
	    }
	public void setParentCid(String parentCid) 
	    {
		this.parentCid = parentCid;
	    }

	public String getName() 
	    {
		return name;
	    }

	public void setName(String name) 
	    {
		this.name = name;
	    }

	public String getType() 
		{
		return type;
		}

	public void setType(String type) 
		{
		this.type = type;
		}

	public String getHtml() 
		{
		return html;
		}

	public void setHtml(String html) 
		{
		this.html = html;
		}	
	}

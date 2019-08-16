
package edu.umich.brcf.shared.layers.dto;

import java.io.Serializable;

import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.metabolomics.layers.domain.CompoundName;

//import chemaxon.formats.MolImporter;
//import chemaxon.marvin.calculations.ElementalAnalyserPlugin;
//import chemaxon.marvin.calculations.logPPlugin;
//import chemaxon.marvin.plugin.PluginException;
//import chemaxon.struc.Molecule;



public class CompoundDTO implements Serializable
	{
	public static CompoundDTO instance(String cid, String chem_abs_number, 
	String smiles, char human_rel, String parentCid, String name, String type, String html) 
		{
		return new CompoundDTO(cid, chem_abs_number, smiles, human_rel, parentCid, name, type, html);
		}
	
	
	public static CompoundDTO instance(Compound cmpd)
		{
		return new CompoundDTO(cmpd.getCid(), cmpd.getChem_abs_number(), 
		 cmpd.getSmiles(), cmpd.getHumanRel(), cmpd.getParent() != null ? cmpd.getParent().getCid() : "", "", "", "");
		}
	
	public static CompoundDTO instance(Compound cmpd, CompoundName cn)
		{
		return new CompoundDTO(cmpd.getCid(), cmpd.getChem_abs_number(), 
		 cmpd.getSmiles(), cmpd.getHumanRel(), cmpd.getParent().getCid(), cn.getName(), cn.getNameType(), cn.getHtml());
		}
	
	private String cid;
	private String chem_abs_number;
	//private BigDecimal molecular_weight;
	//private String molecular_formula;
	private String smiles;
	//private BigDecimal logP;
	private String human_rel;
	//private BigDecimal nominalMass;
	private String parentCid;
	private String name;
	private String type;
	private String html;
	
	
	private CompoundDTO(String cid, String chem_abs_number, String smiles, char human_rel, String parentCid, String name, String type, String html)
		{
		this.cid = cid;
		this.chem_abs_number = chem_abs_number;
		this.smiles = smiles;
		this.human_rel = human_rel+"";
		this.parentCid = parentCid;
		this.name = name;
		this.type = type;
		this.html = html;
//		if ((smiles!=null)&& (smiles.trim().length()>0))
//			{
//			this.molecular_formula = getFormula(smiles);
//			this.logP = new BigDecimal(getLogp(smiles));
//			this.molecular_weight = new BigDecimal(getMass(smiles));
//			this.nominalMass = new BigDecimal(getExactMass(smiles));
//			}
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
	
	public void setChem_abs_number(String chem_abs_number) {
		this.chem_abs_number = chem_abs_number;
	}
	public String getSmiles() {
		return smiles;
	}
	public void setSmiles(String smiles) {
		this.smiles = smiles;
	}
	public String getHuman_rel() {
		return human_rel;
	}
	public void setHuman_rel(String human_rel) {
		this.human_rel = human_rel;
	}
	public String getParentCid() {
		return parentCid;
	}
	public void setParentCid(String parentCid) {
		this.parentCid = parentCid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

//	public BigDecimal getMolecular_weight() {
//		return molecular_weight;
//	}
//
//	public void setMolecular_weight(BigDecimal molecular_weight) {
//		this.molecular_weight = molecular_weight;
//	}
//
//	public String getMolecular_formula() {
//		return molecular_formula;
//	}
//
//	public void setMolecular_formula(String molecular_formula) {
//		this.molecular_formula = molecular_formula;
//	}
//
//	public BigDecimal getLogP() {
//		return logP;
//	}
//
//	public void setLogP(BigDecimal logP) {
//		this.logP = logP;
//	}
//
//	public BigDecimal getNominalMass() {
//		return nominalMass;
//	}
//
//	public void setNominalMass(BigDecimal nominalMass) {
//		this.nominalMass = nominalMass;
//	}
	
	
}

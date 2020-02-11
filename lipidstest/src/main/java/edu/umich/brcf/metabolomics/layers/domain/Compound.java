package edu.umich.brcf.metabolomics.layers.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.wicket.util.io.IClusterable;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.util.Assert;

import chemaxon.formats.MolImporter;
import chemaxon.marvin.calculations.ElementalAnalyserPlugin;
import chemaxon.marvin.calculations.logPPlugin;
import chemaxon.struc.Molecule;
import edu.umich.brcf.shared.layers.domain.CompoundDocument;
import edu.umich.brcf.shared.layers.domain.Inventory;
import edu.umich.brcf.shared.layers.domain.Solvent;
import edu.umich.brcf.shared.layers.dto.CompoundDTO;


@Entity()
@Table(name = "COMPOUND")
public class Compound implements IClusterable {
	
	public static List<String> Human_Rel_Types = Arrays.asList(new String[] { "1", "2", "3", "4", "9", "0"});
	
	public static Compound instance(String cid, String absNumber, String smiles,
			char rel, Compound parent) {
		return new Compound(cid,absNumber, smiles, rel, parent);
	}


	public static Compound instance(String cid) 
		{
		return new Compound(cid, null, null, 'H', null);
		}

	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
			@Parameter(name = "idClass", value = "Compound"), @Parameter(name = "width", value = "6") })
	@Column(name = "CID", unique = true, nullable = false, length = 6, columnDefinition = "CHAR(6)")
	private String cid;

	@Basic()
	@Column(name = "CHEM_ABS_NUMBER", nullable = true, length = 30)
	private String chem_abs_number;

	// Integer
	@Basic()
	@Column(name = "HUMAN_REL", nullable = true)
	private Character humanRel;

	// Boolean
	@Basic()
	@Column(name = "MOLECULAR_WEIGHT", nullable = true)
	private BigDecimal molecular_weight;

	@Basic()
	@Column(name = "MOLECULAR_FORMULA", nullable = true, length = 100)
	private String molecular_formula;

	@Basic()
	@Column(name = "SMILES", nullable = true, length = 500)
	private String smiles;

	@OneToMany(mappedBy = "compound", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	List<CompoundName> names;

	@OneToMany(mappedBy = "compound", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	List<Inventory> inventory;

	@Basic()
	@Column(name = "LOGP", nullable = true, precision = 4, scale = 2, columnDefinition = "NUMBER(4,2)")
	private BigDecimal logP;

	@Basic()
	@Column(name = "FORMULA_WEIGHT", nullable = true, precision = 10, scale = 5, columnDefinition = "NUMBER(10,5)")
	private BigDecimal formulaWeight;

	@Basic()
	@Column(name = "NOMINAL_MASS", nullable = true, precision = 10, scale = 5, columnDefinition = "NUMBER(10,5)")
	private BigDecimal nominalMass;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_CID", referencedColumnName = "CID", nullable = true)
	Compound parent;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SOLVENTID", referencedColumnName = "ID", nullable = true)
	Solvent solvent;
	
	@OneToMany(mappedBy = "associated", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	List<CompoundDocument> docList;

	public Compound() {  }

	private Compound(String cid, String absNumber, String smiles, char rel, Compound parent) 
		{
		this.cid = cid;
		this.chem_abs_number = absNumber;
		this.smiles = smiles;
		if ((smiles!=null)&& (smiles.trim().length()>0))
			{
			this.molecular_formula = getFormula(smiles);
			this.logP = new BigDecimal(getLogp(smiles));
			this.molecular_weight = new BigDecimal(getMass(smiles));
			this.nominalMass = new BigDecimal(getExactMass(smiles));
			}
		this.humanRel = rel;
		this.names = new ArrayList<CompoundName>();
		this.inventory = new ArrayList<Inventory>();
		this.parent = parent;
		}


	public void clear() 
		{
		this.chem_abs_number = "";
		this.cid = "";
		this.molecular_formula = "";
		this.molecular_weight = new BigDecimal(0.0);
		this.smiles = "";
		this.humanRel = '\0';
		this.names.clear();
		}

	
	public void update(CompoundDTO dto, Compound parent) 
		{
		this.chem_abs_number = dto.getChem_abs_number();
		this.smiles = dto.getSmiles();
		this.humanRel = new Character(dto.getHuman_rel().charAt(0));
		this.parent = parent;
		if ((smiles!=null)&& (smiles.trim().length()>0))
			{
			this.molecular_formula = getFormula(smiles);
			this.logP = new BigDecimal(getLogp(smiles));
			this.molecular_weight = new BigDecimal(getMass(smiles));
			this.nominalMass = new BigDecimal(getExactMass(smiles));
			}
		}
	
	
	public void updateParent(Compound parent) 
		{
		this.parent = parent;
		}
	
	
	public void updateSolvent(Solvent solvent) 
		{
		this.solvent = solvent;
		}

	
	public void addName(CompoundName name) 
		{
		Assert.notNull(name);
		this.names.add(name);
		}

	
	public void addInventory(Inventory inv) {
		Assert.notNull(inv);
		this.inventory.add(inv);
	}

	public String getPrimaryName() {
		for (CompoundName cn : names)
			if (cn.getNameType().equals(CompoundName.PRIMARY_NAME_TYPE))
				return cn.getName();
		return null;
	}

	public List<String> getNameSynonyms() {
		List<String> lst = new ArrayList<String>();
		for (CompoundName cn : names)
			if (cn.getNameType().equals(CompoundName.SYNONYM_NAME_TYPE))
				lst.add(cn.getName());
		return lst;
	}

	public boolean nameContains(String str) {
		if (str == null)
			return false;

		for (CompoundName cn : names) {
			if (cn.getName().contains(str))
				return true;
		}
		return false;
	}

	public boolean contains(String str) {
		if (str == null)
			return false;

		if (molecular_formula.contains(str) || cid.contains(str) || chem_abs_number.contains(str) || nameContains(str))
			return true;
		else
			return false;
	}

//	public void update(CompoundDTO dto, List<CompoundName> names) {
//		this.update(dto);
//		this.names = names;
//	}

	public List<CompoundName> getNames() {
		return names;
	}

	public void setNames(List<CompoundName> names) {
		this.names = names;
	}

	public String getName() {
		return getPrimaryName();
	}

	public String getParentName() {
		return "";
	}

	/**
	 * @return the cid
	 */
	public String getCid() {
		return cid;
	}

	public String getId() {
		return getCid();
	}

	/**
	 * @return the molecular_formula
	 */
	public String getMolecular_formula() 
	    {
		return molecular_formula;
	    }
	// issue 8
	public void setMolecular_formula(String vMolecular_formula) 
		{
		this.molecular_formula = vMolecular_formula;
		}

	/**
	 * @return the molecular_weight
	 */
	public BigDecimal getMolecular_weight() 
	    {
		return molecular_weight;
	    }
	// issue 8 
	public void setMolecular_weight(BigDecimal vMolecular_weight) 
		{
		this.molecular_weight = vMolecular_weight;
		}
	
	
	
	public double getMolecularWeightAsDouble() {
		return (molecular_weight == null ? Double.NaN : molecular_weight.doubleValue());
	}

	/**
	 * @return the chem_abs_number
	 */
	public String getChem_abs_number() {
		return chem_abs_number;
	}

	/**
	 * @return the smiles
	 */
	public String getSmiles() {
		return smiles;
	}

	/**
	 * @return the human_rel
	 */
	public char getHumanRel() {
		return (humanRel==null)? ' ':humanRel;
	}

	public List<Inventory> getInventory() {
		List<Inventory> lst = new ArrayList<Inventory>();
		for (Inventory inv : inventory)
			if (inv.getActive()!=('I'))
				lst.add(inv);
		return lst;
	}

	public BigDecimal getLogP() {
		return logP;
	}
	
	public double getLogpAsDouble() {
		return (logP == null ? Double.NaN : logP.doubleValue());
	}

	public BigDecimal getFormulaWeight() {
		return formulaWeight;
	}

	public BigDecimal getNominalMass() {
		return nominalMass;
	}
	
	public double getNominalMassAsDouble() {
		return (nominalMass == null ? Double.NaN : nominalMass.doubleValue());
	}

	public Compound getParent() {
		return parent;
	}
	
	public Solvent getSolvent() {
		return solvent;
	}
	
	public List<CompoundDocument> getDocList() {
		return docList;
	}


	public double getLogp(String smiles){
		
		
		 logPPlugin lplugin = new logPPlugin();
		  double logp=0;
		/*  try{
		  lplugin.setUserTypes("logPTrue,logPMicro,increments");
		  Molecule target = MolImporter.importMol(smiles);
		  lplugin.setMolecule(target);
		//  lplugin.run(); 
		  logp = lplugin.getlogPTrue();
		  }catch(IOException ioe){
				ioe.printStackTrace();
		  }catch(PluginException pe){
				pe.printStackTrace();
		  }*/
		  return logp;
	  }
	  
	  public double getMass(String smiles){
	  	
	  	
		  double mass=0;
		  try{	
		  ElementalAnalyserPlugin  elemanal  = new ElementalAnalyserPlugin (); 
		  Molecule target = MolImporter.importMol(smiles);
		  elemanal .setMolecule(target);
		  elemanal.run();
		  mass = elemanal.getMass();
		  }catch(Exception e){
				
		  }
		  return mass;
	  }
	  
	  public double getExactMass(String smiles){
	  
	 
		  double exactMass=0;
		/*  try{	
		  ElementalAnalyserPlugin  elemanal  = new ElementalAnalyserPlugin (); 
		  Molecule target = MolImporter.importMol(smiles);
		  elemanal .setMolecule(target);
		  elemanal.run();
		  exactMass = elemanal.getExactMass();
		  }catch(Exception e){
				
		  }*/
		  
		  return exactMass; 
	  }
	  
	  public String getFormula(String smiles)
		  {
		
	 
		  String formula="";
		  try{	
		  ElementalAnalyserPlugin  elemanal  = new ElementalAnalyserPlugin (); 
		  Molecule target = MolImporter.importMol(smiles);
		  elemanal .setMolecule(target);
		  elemanal.run();
		  formula = elemanal.getFormula();
		  }catch(Exception e){
				
		  }
		  return formula; 
	  } 
	  
	  public String getPka()
		  {
		  return "";
		  /*
			String pka="{";
			try{
				pKaPlugin pkaplugin = new pKaPlugin();
				Molecule target = MolImporter.importMol(smiles);
				pkaplugin.setMolecule(target);
				pkaplugin.run();
				double thisPka=0;
				int count = target.getAtomCount(); 
				for (int i=0; i < count; ++i) {
					thisPka=pkaplugin.getpKa(i);
					if (!((thisPka+"").equals("NaN")))
					{
						thisPka=Math.floor(thisPka * 100)/100;
						pka=pka+thisPka+", ";
					}
				}
				if (pka.indexOf(",")>0)
					pka=pka.substring(0, pka.lastIndexOf(","))+"}";
				else
					pka="";
			  }catch(Exception e){
				  pka="";
			  }
			  return pka; */
		  }
}

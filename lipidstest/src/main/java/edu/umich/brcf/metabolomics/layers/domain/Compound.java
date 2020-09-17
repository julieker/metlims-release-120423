// Updated by Julie Keros May 11, 2020
package edu.umich.brcf.metabolomics.layers.domain;

import java.io.IOException;
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
import chemaxon.marvin.plugin.PluginException;
import chemaxon.struc.Molecule;
import edu.umich.brcf.shared.layers.domain.CompoundDocument;
import edu.umich.brcf.shared.layers.domain.Inventory;
import edu.umich.brcf.shared.layers.domain.Solvent;
import edu.umich.brcf.shared.layers.dto.CompoundDTO;
import edu.umich.brcf.shared.util.io.StringUtils;

@Entity()
@Table(name = "COMPOUND")
public class Compound implements IClusterable 
    {	
	// issue 58 get rid of human_rel
	// issue 27 2020
	// issue 58 get rid of human_rel
	// issue 62 add additional solubility
	public static Compound instance(String cid, String absNumber, String smiles,
			 Compound parent, String inchiKey,  String smilesOrsmilesOrSmilesFromCompoundIdString, String additionalSolubility, BigDecimal molecularWeight) 
	    {
		// issue 58
		return new Compound(cid,absNumber, smiles,  parent, inchiKey, smilesOrsmilesOrSmilesFromCompoundIdString,additionalSolubility,  molecularWeight );
	    }

	public static Compound instance(String cid) 
		{
		return new Compound(cid, null, null, null, null, null, null, null);
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
    // issue 58
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
	
	// issue 27
	@Basic()
	@Column(name = "INCHIKEY", nullable = true, length = 500)
	private String inchiKey;
	
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
	
	// issue 62
	@Basic()
	@Column(name = "ADDITIONAL_SOLUBILITY", nullable = true, length = 500)
	private String additionalSolubility;

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

	// issue 27 2020
	// issue 58
	private Compound(String cid, String absNumber, String smiles,  Compound parent, String inchiKey,  String smilesOrSmilesFromCompoundIdStr, String additionalSolubility, BigDecimal molecularWeight) 
		{
		this.cid = cid;
		this.chem_abs_number = absNumber;
		this.smiles = smiles;
		this.inchiKey = inchiKey;
		if ((smilesOrSmilesFromCompoundIdStr!=null)&& (smilesOrSmilesFromCompoundIdStr.trim().length()>0))
			{
			this.molecular_formula = getFormula(smilesOrSmilesFromCompoundIdStr);
			this.logP = new BigDecimal(getLogp(smilesOrSmilesFromCompoundIdStr));
			this.molecular_weight = (molecularWeight == null) ? new BigDecimal(getMass(smilesOrSmilesFromCompoundIdStr)) : molecular_weight;
			this.nominalMass = new BigDecimal(getExactMass(smilesOrSmilesFromCompoundIdStr));
			}
		// issue 58
		this.names = new ArrayList<CompoundName>();
		this.inventory = new ArrayList<Inventory>();
		this.parent = parent;
		this.additionalSolubility = additionalSolubility;
		}

	public void clear() 
		{
		this.chem_abs_number = "";
		this.cid = "";
		this.molecular_formula = "";
		this.molecular_weight = new BigDecimal(0.0);
		this.smiles = "";
		this.inchiKey = ""; // issue 27 2020
		this.names.clear();
		this.additionalSolubility = ""; // issue 62
		}

	// issue 27 2020
	public void update(CompoundDTO dto, Compound parent, String smilesOrSmilesFromCompoundIdStr) 
		{
		int intZero = 0;
		this.chem_abs_number = dto.getChem_abs_number();
		this.smiles = dto.getSmiles();
		this.inchiKey = dto.getInchiKey();
		// issue 58
		this.parent = parent;
		this.additionalSolubility = dto.getAdditionalSolubility(); // issue 62
		if ((smilesOrSmilesFromCompoundIdStr!=null)&& (smilesOrSmilesFromCompoundIdStr.trim().length()>0))
			{
			this.molecular_formula = getFormula(smilesOrSmilesFromCompoundIdStr);
	        // issue 45			
			this.logP = this.logP = new BigDecimal(getLogp(smilesOrSmilesFromCompoundIdStr));
			this.molecular_weight = (StringUtils.isEmptyOrNull(dto.getMolecular_weight()) ? new BigDecimal(getMass(smilesOrSmilesFromCompoundIdStr)) : new BigDecimal(dto.getMolecular_weight()));
			this.nominalMass = new BigDecimal(getExactMass(smilesOrSmilesFromCompoundIdStr));			
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
	
	public void addInventory(Inventory inv) 
	    {
		Assert.notNull(inv);
		this.inventory.add(inv);
	    }

	public String getPrimaryName() 
	    {
		for (CompoundName cn : names)
			if (cn.getNameType().equals(CompoundName.PRIMARY_NAME_TYPE))
				return cn.getName();
		return null;
	    }

	public List<String> getNameSynonyms() 
	    {
		List<String> lst = new ArrayList<String>();
		for (CompoundName cn : names)
			if (cn.getNameType().equals(CompoundName.SYNONYM_NAME_TYPE))
				lst.add(cn.getName());
		return lst;
	    }

	public boolean nameContains(String str) 
	    {
		if (str == null)
			return false;
		for (CompoundName cn : names) 
		    {
			if (cn.getName().contains(str))
				return true;
		    }
		return false;
	    }

	public boolean contains(String str) 
	    {
		if (str == null)
			return false;

		if (molecular_formula.contains(str) || cid.contains(str) || chem_abs_number.contains(str) || nameContains(str))
			return true;
		else
			return false;
	    }

	public List<CompoundName> getNames() 
	    {
		return names;
	    }

	public void setNames(List<CompoundName> names) 
	    {
		this.names = names;
	    }

	public String getName() 
	    {
		return getPrimaryName();
	    }

	public String getParentName() 
	    {		
		return "";
	    }

	/**
	 * @return the cid
	 */
	public String getCid() 
	    {
		return cid;
	    }

	public String getId() 
	    {
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
		
	public double getMolecularWeightAsDouble() 
	    {
		return (molecular_weight == null ? Double.NaN : molecular_weight.doubleValue());
	    }

	/**
	 * @return the chem_abs_number
	 */
	public String getChem_abs_number() 
	    {
		return chem_abs_number;
	    }

	/**
	 * @return the smiles
	 */
	public String getSmiles() {
		return smiles;
	}
	
	// issue 27 2020
	public String getInchiKey() 
	    {
		return inchiKey;
	    }
	
	// issue 27 2020
	public void setInchiKey (String vInchiKey)
		{
		this.inchiKey = vInchiKey;
		}
	
    // issue 58
	public List<Inventory> getInventory() 
	    {
		List<Inventory> lst = new ArrayList<Inventory>();
		for (Inventory inv : inventory)
			if (inv.getActive()!=('I'))
				lst.add(inv);
		return lst;
	    }

	public BigDecimal getLogP() 
	    {
		return logP;
	    }
	
	public double getLogpAsDouble() 
	    {
		return (logP == null ? Double.NaN : logP.doubleValue());
	    }

	public BigDecimal getFormulaWeight() 
	    {
		return formulaWeight;
	    }

	public BigDecimal getNominalMass() 
	    {
		return nominalMass;
	    }
	
	public double getNominalMassAsDouble() 
	    {
		return (nominalMass == null ? Double.NaN : nominalMass.doubleValue());
	    }

	public Compound getParent() 
	    {
		return parent;
	    }
	
	public Solvent getSolvent() 
	    {
		return solvent;
	    }
	
	public List<CompoundDocument> getDocList() 
	    {
		return docList;
	    }

	// issue 45
	public void setLogP(BigDecimal vLogP)
		{
		this.logP = vLogP;
		}
	
	// issue 45 
	public double getLogp(String smiles)
	    {		   	
		logPPlugin lplugin = new logPPlugin();
		double logp=0;
		try
		    {
	   	    lplugin.setUserTypes("logPTrue,logPMicro,increments");
	   	    Molecule target = MolImporter.importMol(smiles);
	   	    lplugin.setMolecule(target);
	   	    lplugin.run(); 
	   	    logp = lplugin.getlogPTrue();
		   	}
		catch(IOException ioe)
	        {
		    ioe.printStackTrace();
	        }
		catch(PluginException pe)
		    {
		    pe.printStackTrace();
		    }
		return logp;
		}
	  
    public double getMass(String smiles)
        {	  	
	    double mass=0;
	    try
	        {	
			ElementalAnalyserPlugin  elemanal  = new ElementalAnalyserPlugin (); 
			Molecule target = MolImporter.importMol(smiles);
			elemanal .setMolecule(target);
			elemanal.run();
			mass = elemanal.getMass();
	        }
	    catch(Exception e)
	        {				
	        }
	    return mass;
        }
    
    // issue 45
	public void setNominalMass(BigDecimal vMass) 
		{
		this.nominalMass = vMass;
		}
	  
    // issue 45
    public double getExactMass(String smiles)
        {       
        double exactMass=0;
     	try
     	    {	
     	    ElementalAnalyserPlugin  elemanal  = new ElementalAnalyserPlugin (); 
     	    Molecule target = MolImporter.importMol(smiles);
     	    elemanal .setMolecule(target);
     	    elemanal.run();
     	    exactMass = elemanal.getExactMass();
     	    }
     	catch(Exception e)
     	    {  
     		e.printStackTrace();
     	    }    	  
     	return exactMass; 
        }
	  
    public String getFormula(String smiles)
	    {	 
		String formula="";
		try
		    {	
			ElementalAnalyserPlugin  elemanal  = new ElementalAnalyserPlugin (); 
			Molecule target = MolImporter.importMol(smiles);
			elemanal .setMolecule(target);
			elemanal.run();
			formula = elemanal.getIsotopeFormula(); // issue 27 2020
	        }
	    catch(Exception e)
		    {					
		    }
	    return formula; 
        } 
	  
	public String getPka()
	    {
	    return "";
	    }
	
	// issue 62
	public void setAdditionalSolubility (String vAdditionalSolubility)
		{
		this.additionalSolubility = vAdditionalSolubility;
		}
	
	// issue 62
	public String getAdditionalSolubility() 
	    {
		return additionalSolubility;
	    }
	
    }

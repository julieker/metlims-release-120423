package edu.umich.brcf.metabolomics.layers.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.wicket.util.io.IClusterable;


// Force recompile
@Entity()
@Table(name = "OUR_GENUSSPECIES")
public class GenusSpecies implements IClusterable
	{
	public static GenusSpecies instance(Long gsID, Long ncbiID, String genusName)
		{
		return new GenusSpecies(gsID, ncbiID, genusName);
		}

	public static GenusSpecies instance(Long id)
		{
		return new GenusSpecies(id, null, null);
		}

	@Id()
	@Column(name = "GENUS_SPECIES_ID", unique = true, nullable = false, columnDefinition = "NUMBER(6)")
	private Long gsID;

	@Basic()
	@Column(name = "NCBI_ID", nullable = false, columnDefinition = "NUMBER(6)")
	private Long ncbiID;

	@Basic()
	@Column(name = "GENUS_NAME", nullable = false, columnDefinition = "VARCHAR2(100)")
	private String genusName;

	public GenusSpecies()
		{
		}

	private GenusSpecies(Long gsID, Long ncbiID, String genusName)
		{
		this.gsID = gsID;
		this.ncbiID = ncbiID;
		this.genusName = genusName;
		}

	public Long getGsID()
		{
		return gsID;
		}

	public Long getNcbiID()
		{
		return ncbiID;
		}

	public String getGenusName()
		{
		return genusName;
		}
	}

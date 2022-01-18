/******************
 * Created by Julie Keros
 * Date: Nov 11 2020 
 * To store MIXTUREs associated with aliquots
 * 
 * 
 */
// issue 100
package edu.umich.brcf.shared.layers.domain;
/************************************
 * Created by Julie Keros issue 100 to associate MIXTUREs with aliquots
 */

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import edu.umich.brcf.metabolomics.panels.lims.mixtures.AliquotInfo;

@Entity()
@org.hibernate.annotations.Entity(mutable = false)
@Table(name = "MIXTURE_ALIQUOT", uniqueConstraints = @UniqueConstraint(columnNames = {
		"MIXTURE_ID", "ALIQUOT_ID" }))
public class MixtureAliquot implements Serializable
	{
	public static MixtureAliquot instance(Mixture mixture, Aliquot aliquot, String volumeAliquotStr, String concentrateAliquotStr, String volumeAliquotUnits, Character dryRetired)
		{
		return new MixtureAliquot(mixture, aliquot, volumeAliquotStr, concentrateAliquotStr, volumeAliquotUnits, dryRetired);
		}
	@EmbeddedId
	protected MixtureAliquotPK id;
	// Issue 79
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ALIQUOT_ID", insertable = false, updatable = false)
	@org.hibernate.annotations.ForeignKey(name = "MIXTURE_ALIQUOT_FK1")
	private Aliquot aliquot;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MIXTURE_ID", insertable = false, updatable = false)
	@org.hibernate.annotations.ForeignKey(name = "MIXTURE_ALIQUOT_FK2")
	private Mixture mixture;
	
	@Basic()
	@Column(name = "VOLUME_ALIQUOT", columnDefinition = "NUMBER(15,7)")
	private BigDecimal volumeAliquot;
	
	@Basic()
	@Column(name = "CONCENTRATION_ALIQUOT", columnDefinition = "NUMBER(15,7)")
	private BigDecimal concentrationAliquot;
	
	@Basic()
	@Column(name = "VOLUME_ALIQUOT_UNITS", columnDefinition = "VARCHAR2(10)")
	private String volumeAliquotUnits;
	
	// issue 199
	@Basic()
	@Column(name = "DRY_ALIQUOT_RETIRED", columnDefinition = "CHAR(1)")
	private Character dryAliquotRetired;
	
	
	
	public MixtureAliquot()
		{
		}
	// issue 199
	private MixtureAliquot(Mixture mixture, Aliquot aliquot, String volumeAliquotStr, String concentrateAliquotStr, String volumeAliquotUnits, Character dryRetired)
		{
		this.aliquot = aliquot;
		this.mixture = mixture;
		this.id = MixtureAliquotPK.instance(mixture, aliquot);	
		this.volumeAliquot = new BigDecimal(org.h2.util.StringUtils.isNullOrEmpty(volumeAliquotStr) ? "0" :  volumeAliquotStr) ;// issue 196
		
		this.dryAliquotRetired = dryRetired; // issue 199
		this.concentrationAliquot = new BigDecimal(concentrateAliquotStr);
		this.volumeAliquotUnits = volumeAliquotUnits; // issue 196
		}
	
	// issue 199
	public Character getDryAliquotRetired()
		{
		return this.dryAliquotRetired;
		}

	public void setDryAliquotRetired(Character dryAliquotRetired)
		{
		this.dryAliquotRetired = dryAliquotRetired;
		}
	// issue 199
	public void updateToRetired(String dryAliquotRetired)
		{
		this.dryAliquotRetired = '1';
		}
	public MixtureAliquotPK getId()
		{
		return id;
		}
	public Mixture getMixture()
		{
		return mixture;
		}
	public Aliquot getAliquot()
		{
		return aliquot;
		}	
	public BigDecimal getVolumeAliquot()
		{
		return volumeAliquot;
		}
	public BigDecimal getConcentrationAliquot()
		{
		return concentrationAliquot;
		}
	// issue 196
	public String getVolumeAliquotUnits()
		{
		return volumeAliquotUnits;
		}
	
	}

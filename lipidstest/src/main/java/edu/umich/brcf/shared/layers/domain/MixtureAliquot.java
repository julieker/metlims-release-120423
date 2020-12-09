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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity()
@org.hibernate.annotations.Entity(mutable = false)
@Table(name = "MIXTURE_ALIQUOT", uniqueConstraints = @UniqueConstraint(columnNames = {
		"MIXTURE_ID", "ALIQUOT_ID" }))
public class MixtureAliquot implements Serializable
	{
	public static MixtureAliquot instance(Mixture mixture, Aliquot aliquot, String volumeAliquotStr, String concentrateAliquotStr)
		{
		return new MixtureAliquot(mixture, aliquot, volumeAliquotStr, concentrateAliquotStr);
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
	
	public MixtureAliquot()
		{
		}
	private MixtureAliquot(Mixture mixture, Aliquot aliquot, String volumeAliquotStr, String concentrateAliquotStr)
		{
		this.aliquot = aliquot;
		this.mixture = mixture;
		this.id = MixtureAliquotPK.instance(mixture, aliquot);	
		this.volumeAliquot = new BigDecimal(volumeAliquotStr);
		this.concentrationAliquot = new BigDecimal(concentrateAliquotStr);
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
	
	}

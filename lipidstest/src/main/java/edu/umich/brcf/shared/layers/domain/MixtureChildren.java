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
@Table(name = "MIXTURE_CHILDREN", uniqueConstraints = @UniqueConstraint(columnNames = {
		"MIXTURE_ID", "PARENT_MIXTURE_ID" }))
public class MixtureChildren implements Serializable
	{
	public static MixtureChildren instance(Mixture mixture, Mixture parentMixture, String volumeMixtureStr, String concentrateAliquotStr)
		{
		return new MixtureChildren(mixture, parentMixture, volumeMixtureStr, concentrateAliquotStr);
		}
	@EmbeddedId
	protected MixtureChildrenPK id;
	// Issue 79
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MIXTURE_ID", insertable = false, updatable = false)
	@org.hibernate.annotations.ForeignKey(name = "MIXTURE_CHILDREN_FK1")
	private Mixture mixture;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_MIXTURE_ID", insertable = false, updatable = false)
	@org.hibernate.annotations.ForeignKey(name = "MIXTURE_CHILDREN_FK2")
	private Mixture parentMixture;
	
	@Basic()
	@Column(name = "VOLUME_MIXTURE", columnDefinition = "NUMBER(15,7)")
	private BigDecimal volumeMixture;
	
	@Basic()
	@Column(name = "CONCENTRATION_MIXTURE", columnDefinition = "NUMBER(15,7)")
	private BigDecimal concentrationMixture;
	
	public MixtureChildren()
		{
		}
	private MixtureChildren(Mixture mixture, Mixture parentMixture, String volumeMixtureStr, String concentrateAliquotStr)
		{
		this.mixture = mixture;
		this.parentMixture = parentMixture;
		this.id = MixtureChildrenPK.instance(mixture, parentMixture);	
		this.volumeMixture = new BigDecimal(volumeMixtureStr);
		this.concentrationMixture = new BigDecimal(concentrateAliquotStr);
		}
	public MixtureChildrenPK getId()
		{
		return id;
		}
	public Mixture getMixture()
		{
		return mixture;
		}
	public Mixture getParentMixture()
		{
		return parentMixture;
		}	
	public BigDecimal getVolumeMixture()
		{
		return volumeMixture;
		}
	public BigDecimal getConcentrationMixture()
		{
		return concentrationMixture;
		}
	
	}

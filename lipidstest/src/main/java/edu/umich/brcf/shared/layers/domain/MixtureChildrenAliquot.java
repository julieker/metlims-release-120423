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
@Table(name = "MIXTURE_CHILDREN_ALIQUOT", uniqueConstraints = @UniqueConstraint(columnNames = {
		"MIXTURE_ID", "PARENT_MIXTURE_ID", "ALIQUOT_ID" }))
public class MixtureChildrenAliquot implements Serializable
	{
	public static MixtureChildrenAliquot instance(Mixture mixture, Mixture parentMixture, Aliquot aliquot ,  String concentrateFinalStr)
		{
		return new MixtureChildrenAliquot(mixture, parentMixture, aliquot, concentrateFinalStr);
		}
	@EmbeddedId
	protected MixtureChildrenAliquotPK id;
	// Issue 79
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MIXTURE_ID", insertable = false, updatable = false)
	@org.hibernate.annotations.ForeignKey(name = "MIXTURE_CHILDREN_ALIQUOT_FK1")
	private Mixture mixture;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_MIXTURE_ID", insertable = false, updatable = false)
	@org.hibernate.annotations.ForeignKey(name = "MIXTURE_CHILDREN_ALIQUOT_FK2")
	private Mixture parentMixture;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ALIQUOT_ID", insertable = false, updatable = false)
	@org.hibernate.annotations.ForeignKey(name = "MIXTURE_CHILDREN_ALIQUOT_FK3")
	private Aliquot aliquot;
	
	@Basic()
	@Column(name = "CONCENTRATION_FINAL", columnDefinition = "NUMBER(15,7)")
	private BigDecimal concentrationFinal;
	
	public MixtureChildrenAliquot()
		{
		}
	private MixtureChildrenAliquot(Mixture mixture, Mixture parentMixture, Aliquot aliquot, String concentrationFinalStr)
		{
		this.mixture = mixture;
		this.parentMixture = parentMixture;
		this.id = MixtureChildrenAliquotPK.instance(mixture, parentMixture, aliquot);	
		this.concentrationFinal = new BigDecimal(concentrationFinalStr);
		}
	public MixtureChildrenAliquotPK getId()
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
   
	public Aliquot getAliquot()
		{
		return aliquot;
		}
	
	public BigDecimal getConcentrationFinal()
		{
		return concentrationFinal;
		}
	
	}

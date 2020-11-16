/******************
 * Created by Julie Keros
 * Date: Nov 11 2020 
 * To store assays associated with aliquots
 * 
 * 
 */
// issue 100
package edu.umich.brcf.shared.layers.domain;
/************************************
 * Created by Julie Keros issue 100 to associate assays with aliquots
 */

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity()
@org.hibernate.annotations.Entity(mutable = false)
@Table(name = "ASSAY_ALIQUOT", uniqueConstraints = @UniqueConstraint(columnNames = {
		"ASSAY_ID", "ALIQUOT_ID" }))
public class AssayAliquot implements Serializable
	{
	public static AssayAliquot instance(Assay assay, Aliquot aliquot)
		{
		return new AssayAliquot(assay, aliquot);
		}
	@EmbeddedId
	protected AssayAliquotPK id;
	// Issue 79
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ALIQUOT_ID", insertable = false, updatable = false)
	@org.hibernate.annotations.ForeignKey(name = "assay_ALIQUOT_FK1")
	private Aliquot aliquot;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ASSAY_ID", insertable = false, updatable = false)
	@org.hibernate.annotations.ForeignKey(name = "assay_ALIQUOT_FK2")
	private Assay assay;
	public AssayAliquot()
		{
		}
	private AssayAliquot(Assay assay, Aliquot aliquot)
		{
		this.aliquot = aliquot;
		this.assay = assay;
		this.id = AssayAliquotPK.instance(assay, aliquot);	
		}
	public AssayAliquotPK getId()
		{
		return id;
		}
	public Assay getAssay()
		{
		return assay;
		}
	public Aliquot getAliquot()
		{
		return aliquot;
		}	
	}

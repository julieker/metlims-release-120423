package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity()
@Table(name = "CONTROLS")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Control implements Serializable
	{
	// public static String
	// idFormat="(C)\\d{1}|(S)\\d{2}|(S)\\d{3}|(S)\\d{4}|(S)\\d{5}|(S)\\d{6}|(S)\\d{7}|(S)\\d{8}|\\d{3}|\\d{4}|\\d{5}";

	public static Control instance(String controlId, Experiment exp,
			Assay assay, String controlTypeId)
		{
		return new Control(controlId, exp, assay, controlTypeId);
		}

	@Id()
	// @GeneratedValue(generator = "IdGeneratorDAO")
	// @GenericGenerator(name = "IdGeneratorDAO", strategy =
	// "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO",
	// parameters = {
	// @Parameter(name = "idClass", value = "Control"),
	// @Parameter(name = "width", value = "9") })
	@Column(name = "CONTROL_ID", unique = true, nullable = false, length = 9, columnDefinition = "CHAR(9)")
	private String controlId;

	@Basic()
	@Column(name = "CONTROL_TYPE_ID", nullable = false, columnDefinition = "CHAR(6)")
	private String controlTypeId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EXP_ID", referencedColumnName = "EXP_ID", nullable = false)
	private Experiment exp;

	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "PARENT_ID", referencedColumnName = "SAMPLE_ID",
	// nullable = true, columnDefinition = "CHAR(9)")
	// private Assay assay;

	// @OneToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "ASSAY_ID", referencedColumnName = "ASSAY_ID",
	// nullable = false, columnDefinition = "CHAR(4)")

	@Basic()
	@Column(name = "ASSAY_ID", nullable = false, columnDefinition = "CHAR(4)")
	private Assay assay;

	private Control(String controlId, Experiment exp, Assay assay,
			String controlTypeId)
		{
		this.controlId = controlId;
		this.exp = exp;
		this.assay = assay;
		this.controlTypeId = controlTypeId;
		}

	public Control()
		{
		}

	public void setControlId(String controlId)
		{
		this.controlId = controlId;
		}

	public void setControlTypeId(String controlTypeId)
		{
		this.controlTypeId = controlTypeId;
		}

	public void setExp(Experiment exp)
		{
		this.exp = exp;
		}

	public void setAssay(Assay assay)
		{
		this.assay = assay;
		}

	public String getControlId()
		{
		return controlId;
		}

	public Experiment getExp()
		{
		return exp;
		}

	public Assay getAssay()
		{
		return assay;
		}

	public String getControlTypeId()
		{
		return controlTypeId;
		}
	}

// public static String getIdFormat() {
// return idFormat;
// }

// public static void setIdFormat(String idFormat) {
// Control.idFormat = idFormat;
// }


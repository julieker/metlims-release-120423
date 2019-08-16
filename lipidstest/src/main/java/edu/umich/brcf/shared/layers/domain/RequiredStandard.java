package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import edu.umich.brcf.metabolomics.layers.domain.Compound;


@Entity
@Table(name = "QAQC_REQUIRED_STANDARDS")
public class RequiredStandard implements Serializable 
	{
	public static String STANDARD_TYPE_PERFORMANCE = "PERF_STD";
	public static String STANDARD_TYPE_INJECTION = "INJ_STD";
	public static String STANDARD_TYPE_RECOVERY = "RECOV_STD";
	public static String STANDARD_TYPE_DERIVITIZATION = "DERIV_STD";
	public static Double MAX_ZSCORE = 2.0;

	@EmbeddedId
	private RequiredStandardPK id;

	@Basic
	@Column(name = "AREA_AVERAGE_VALUE", precision = 22, scale = 4)
	private Double areaAverage;

	@Basic
	@Column(name = "AREA_STANDARD_DEVIATION", precision = 22, scale = 5)
	private Double areaStandardDeviation;

	@Transient
	private Boolean selected;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPOUND_ID", referencedColumnName = "CID", nullable = false)
	private Compound compound;

	public String getInstrumentType() {
		return id.getInstrumentType();
	}

	public String getStandardType() {
		return id.getStandardType();
	}

	public String getName() {
		return id.getName();
	}

	public RequiredStandardPK getId() {
		return id;
	}

	public Double getAreaAverage() {
		return areaAverage;
	}

	public Double getAreaStandardDeviation() {
		return areaStandardDeviation;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	public Compound getCompound() {
		return compound;
	}


}

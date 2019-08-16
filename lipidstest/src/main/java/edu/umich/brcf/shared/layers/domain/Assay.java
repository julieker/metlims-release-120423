
package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity()
@Table(name = "ASSAYS")
public class Assay implements Serializable
	{
	public static String fullIdFormat = "(A)\\d{3}";

	public static Assay instance(String assayId, String assayName, BigDecimal price, String platformId, String alternateName)
		{
		return new Assay(assayId, assayName, price, platformId, alternateName);
		}

	@Id()
	@Column(name = "ASSAY_ID", nullable = false, unique = true, length = 4, columnDefinition = "CHAR(4)")
	private String assayId;

	@Basic()
	@Column(name = "ASSAY_NAME", nullable = true, length = 150, columnDefinition = "VARCHAR2(150)")
	private String assayName;

	@Basic()
	@Column(name = "PRICE_PER_SAMPLE", nullable = true, columnDefinition = "NUMBER")
	private BigDecimal price;

	@Basic()
	@Column(name = "PLATFORM_ID", nullable = false, columnDefinition = "CHAR(5)")
	String platformId;

	@Basic()
	@Column(name = "ALTERNATE_NAME", nullable = true, columnDefinition = "VARCHAR2(200)")
	private String alternateName;

	
	private Assay(String assayId, String assayName, BigDecimal price, String platformId, String alternateName)
		{
		this.assayId = assayId;
		this.assayName = assayName;
		this.price = price;
		this.platformId = platformId;
		this.alternateName = alternateName;
		}

	public Assay()   {    }

	
	public String getAssayId()
		{
		return assayId;
		}

	public void setAssayId(String assayId)
		{
		this.assayId = assayId;
		}

	public String getAssayName()
		{
		return assayName;
		}

	public void setAssayName(String assayName)
		{
		this.assayName = assayName;
		}

	public BigDecimal getPrice()
		{
		return price;
		}

	public void setPrice(BigDecimal price)
		{
		this.price = price;
		}

	public void setPlatformId(String platformId)
		{
		this.platformId = platformId;
		}

	public String getPlatformId()
		{
		return platformId;
		}

	public String getAlternateName()
		{
		return alternateName;
		}

	public void setAlternateName(String alternateName)
		{
		this.alternateName = alternateName;
		}

	public BigDecimal getMinVolume()
		{
		return new BigDecimal(0.0);
		}

	public String getCoreId()
		{
		return "";
		}
	}

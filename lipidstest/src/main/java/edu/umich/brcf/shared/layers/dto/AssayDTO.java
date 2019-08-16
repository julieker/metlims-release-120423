package edu.umich.brcf.shared.layers.dto;

import java.math.BigDecimal;


public class AssayDTO
	{
	public static AssayDTO instance(String assayId, String assayName, String coreId, BigDecimal minVolume, BigDecimal price)
		{
		return new AssayDTO(assayId, assayName, coreId, minVolume, price);
		}
	
	private String assayId = "", assayName = "", coreId = ""; 
	private BigDecimal minVolume, price;
	
	public AssayDTO() {}
	
	
	public AssayDTO(String assayId, String assayName, String coreId, BigDecimal minVolume, BigDecimal price)
		{
		this.assayId = assayId;
		this.assayName = assayName;
		this.coreId = coreId;
		this.minVolume = minVolume;
		this.price = price;
		}
	}

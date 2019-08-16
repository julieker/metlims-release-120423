package edu.umich.brcf.shared.layers.domain;


import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.wicket.util.io.IClusterable;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.shared.layers.dto.InventoryDTO;




@Entity()
@Table(name = "INVENTORY")
public class Inventory implements IClusterable 
	{
	public static List<String> STATUS_TYPES = Arrays.asList(new String[] { "A", "S", "I", "O" });
	public static List<String> SUPPLIERS = Arrays.asList(new String[] { "Sigma Aldrich", "Sigma", "Aldrich", "FLUKA", "Fisher Scientific", "Riedel-de Haen", "Supelco" });
	
	public static Inventory instance(Character active, String contSize, String catNum, Compound compound,
	Location loc, String supplier, BigDecimal purity) 
		{
		return new Inventory(null, active, contSize, catNum, compound, Calendar.getInstance(), loc, supplier, purity);
		}

	public static Inventory instance(String inventoryCode) 
		{
		return new Inventory(inventoryCode, new Character('\0'), null, null, null, null, null, null,  new BigDecimal(0.0));
		}

	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
			@Parameter(name = "idClass", value = "Inventory"), @Parameter(name = "width", value = "7") })
	@Column(name = "INVENTORYID", nullable = false, unique = true, length = 7, columnDefinition = "CHAR(7)")
	private String inventoryId;

	@Basic()
	@Column(name = "ACTIVE", nullable = false, columnDefinition = "CHAR(1)")
	private Character active;

	@Basic()
	@Column(name = "BOTSIZE", nullable = false, length = 50)
	private String containerSize;
	
//	@Basic()
//	@Column(name = "BOTSIZE_UNIT", nullable = false, columnDefinition = "VARCHAR2(26)")
//	private String unit;

	@Basic()
	@Column(name = "CATNUM", nullable = false, length = 50)
	private String catalogueNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CID", referencedColumnName = "CID", nullable = false)
	private Compound compound;

	@Basic()
	@Column(name = "INVDATE")
	private Calendar inventoryDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LOCID", referencedColumnName = "LOCATIONID", nullable = false)
	private Location location;

	@Basic()
	@Column(name = "SUPPLIER", nullable = false, length = 50)
	private String supplier;

	@Basic()
	@Column(name = "PURITY", precision = 5, columnDefinition = "NUMBER(5,2)")
	private BigDecimal purity;

	
	
	public BigDecimal getPurity() 
		{
		return purity;
		}

	public Inventory() {   }

	private Inventory(String code, Character active, String contSize, String catalogNumber, Compound compound,
			Calendar inventoryDate, Location location, String supplier, BigDecimal purity) 
		{
		this.inventoryId = code;
		this.active = active;
		this.containerSize = contSize;
		this.catalogueNumber = catalogNumber;
		this.compound = compound;
		this.inventoryDate = inventoryDate;
		this.location = location;
		this.supplier = supplier;
		this.purity = purity;
		}

	
	public void update(InventoryDTO dto, Compound cmpd, Location loc) 
		{
		this.inventoryId = dto.getInventoryId();
		this.active = dto.getActive().charAt(0);
		this.containerSize = dto.getBotSize();
		this.catalogueNumber = dto.getCatNum();
		this.compound = cmpd;
		
		Date date ; 
	    DateFormat formatter = new SimpleDateFormat(Project.PROJECT_DATE_FORMAT);
	    try 
	    	{
	    	date = (Date)formatter.parse(dto.getInvDate());
			Calendar cal=Calendar.getInstance();
			cal.setTime(date);
			this.inventoryDate = cal;
	    	}
	    catch (ParseException e) { e.printStackTrace(); }
		this.location = loc;
		this.supplier = dto.getSupplier();
		this.purity = dto.getPurity();
		}
	
	public char getActive() 
		{
		return active;
		}

	public String getContainerSize() 
		{
		return containerSize;
		}

	public String getCatalogueNumber() 
		{
		return catalogueNumber;
		}

	public Compound getCompound() 
		{
		return compound;
		}

	public Calendar getInventoryDate() 
		{
		return inventoryDate;
		}
	
	public String getInventoryDateStr() 
		{
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		return (inventoryDate == null ? "" : sdf.format(inventoryDate.getTime()));
		}

	
	public Location getLocation() 
		{
		return location;
		}

	public String getSupplier() 
		{
		return supplier;
		}

	public String getInventoryId() 
		{
		return inventoryId;
		}
	}

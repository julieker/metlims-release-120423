package edu.umich.brcf.shared.layers.domain;


import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Entity;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.shared.layers.dto.AliquotDTO;
import edu.umich.brcf.shared.util.utilpackages.CalendarUtils;

import javax.persistence.Table;

@Entity()
@Table(name = "ALIQUOT")

// issue 61
public class Aliquot implements Serializable 
	{
	public static String fullIdFormat = "(AL)\\d{8}";
	public static String ALIQUOT_DATE_FORMAT = "MM/dd/yy";

	public static Aliquot instance(Integer replicate, Location location, Inventory inventory, 
		Character dry, Compound compound, String solvent, Calendar createDate, String aliquotLabel, String notes, String createdBy, Character neat, String neatSolVolUnits, BigDecimal ivol, BigDecimal dcon, BigDecimal icon, BigDecimal weightedAmount, BigDecimal dconc, BigDecimal dvol, String dConcentrationUnits, String weightedAmountUnits, BigDecimal molecularWeight) 
		{
		return new Aliquot(null,  location,  
		 inventory,  replicate, dry, compound, solvent, createDate, aliquotLabel, notes, createdBy, neat, neatSolVolUnits, ivol, dcon, icon, weightedAmount, dconc, dvol, dConcentrationUnits, weightedAmountUnits, molecularWeight);
		}

	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
	@Parameter(name = "idClass", value = "Aliquot"), @Parameter(name = "width", value = "10") })
	@Column(name = "ALIQUOT_ID", nullable = false, unique = true, length = 10, columnDefinition = "CHAR(10)")
	private String aliquotId;
	
	// issue 61 2020
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CID", referencedColumnName = "CID", nullable = false, columnDefinition = "CHAR(6)")
	private Compound compound; 
	
	@Basic()
	@Column(name = "DELETED", length = 1)
	private Boolean deletedFlag;
	
	@Basic()
	@Column(name = "CREATED_BY", nullable = false, columnDefinition = "CHAR(6)")
	private String createdBy;
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LOCATION_ID", nullable = false, columnDefinition = "CHAR(6)")
	private Location location;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INVENTORYID", nullable = false, columnDefinition = "CHAR(7)")
	private Inventory inventory;
	
	@Basic()
	@Column(name = "REPLICATE", columnDefinition = "NUMBER")
	private Integer replicate;
	
	@Basic()
	@Column(name = "DRY", columnDefinition = "CHAR(1)")
	private Character dry;
	
	@Basic()
	@Column(name = "SOLVENT", columnDefinition = "VARCHAR2(100)")
	private String solvent;
	
	@Basic()
	@Column(name = "CREATE_DATE", nullable = true)
	private Calendar createDate;
	
	@Basic()
	@Column(name = "ALIQUOT_LABEL", columnDefinition = "VARCHAR2(100)")
	private String aliquotLabel;
	
	@Basic()
	@Column(name = "NOTES", columnDefinition = "VARCHAR2(2000)")
	private String notes;
	
	@Basic()
	@Column(name = "NEAT", columnDefinition = "CHAR(1)")
	private Character neat;
	
	@Basic()
	@Column(name = "NEAT_SOL_VOL_UNITS", columnDefinition = "VARCHAR2(26)")
	private String neatSolVolUnits;
	
	@Basic()
	@Column(name = "INITIAL_VOLUME", columnDefinition = "NUMBER(15,7)")
	private BigDecimal ivol;
	
	@Basic()
	@Column(name = "INITIAL_CONCENTRATION", columnDefinition = "NUMBER(15,7)")
	private BigDecimal icon;
	
	@Basic()
	@Column(name = "DESIRED_CONCENTRATION", columnDefinition = "NUMBER(15,7)")
	private BigDecimal dcon;
	
	@Basic()
	@Column(name = "WEIGHTED_AMOUNT", columnDefinition = "NUMBER(15,7)")
	private BigDecimal weightedAmount;
	
	@Basic()
	@Column(name = "DESIRED_CONCENTRATION_NEAT", columnDefinition = "NUMBER(15,7)")
	private BigDecimal dconc;

	@Basic()
	@Column(name = "DESIRED_VOLUME", columnDefinition = "NUMBER(15,7)")
	private BigDecimal dvol;
	
	@Basic()
	@Column(name = " DESIRED_CONCENTRATION_UNITS", columnDefinition = "VARCHAR2(26)")
	private String dConcentrationUnits;
	
	@Basic()
	@Column(name = "WEIGHTED_AMOUNT_UNITS", columnDefinition = "VARCHAR2(26)")
	private String weightedAmountUnits;
	
	@Basic()
	@Column(name = "MOLECULAR_WEIGHT", columnDefinition = "NUMBER(10,5)")
	private BigDecimal molecularWeight;
	
	@Basic()
	@Column(name = "DELETE_REASON", columnDefinition = "VARCHAR2(100)")
	private String deleteReason;
	
	public Aliquot() {  }
	
	private Aliquot(String aliquotId, 
			  Location location,  Inventory inventory, 
			  Integer replicate, Character dry , Compound compound , String solvent, Calendar createDate, String aliquotLabel, String notes, String createdBy , Character neat, String neatSolVolUnits, BigDecimal ivol,BigDecimal dcon, BigDecimal icon, BigDecimal weightedAmount, BigDecimal dconc, BigDecimal dvol, String dConcentrationUnits, String weightedAmountUnits , BigDecimal molecularWeight )
			{
			this.aliquotId = aliquotId;
			this.location = location;
			this.inventory = inventory;
			this.replicate = replicate;
		    this.dry = dry;
			this.compound = compound;
			this.solvent = solvent;
			this.createDate = createDate;
			this.aliquotLabel = aliquotLabel;
			this.notes = notes;
			this.createdBy = createdBy;
            this.neat = neat;
            this.neatSolVolUnits = neatSolVolUnits;
            this.ivol = ivol;
            this.dcon = dcon;
            this.icon = icon;
            this.dconc = dconc;
            this.weightedAmount = weightedAmount;
            this.dvol = dvol;
            this.dConcentrationUnits = dConcentrationUnits;
            this.weightedAmountUnits = weightedAmountUnits;
            this .molecularWeight = molecularWeight;
			}	
	
	// issue 61
	public void update (AliquotDTO dto)
		{
		this.aliquotId = dto.getAliquotId();
		this.compound = dto.getCompoundObj();
		this.location = dto.getLocationObj();
		this.inventory = dto.getInventoryObj();
		this.replicate = dto.getReplicate();
		this.dry = dto.getIsDry() ? '1' : '0';
		this.solvent = dto.getSolvent();
		this.createDate = CalendarUtils.calendarFromString(dto.getCreateDate(),Aliquot.ALIQUOT_DATE_FORMAT);
		this.aliquotLabel = dto.getAliquotLabel();
		this.notes = dto.getNotes();
		this.createdBy = dto.getCreatedBy();
		this.neat = dto.getNeatOrDilution();
		this.neatSolVolUnits = dto.getNeatOrDilutionUnits();
		this.ivol = new BigDecimal(dto.getIvol());
		this.icon = new BigDecimal(dto.getIcon());
		this.dcon = new BigDecimal(dto.getDcon());
		this.weightedAmount = new BigDecimal(dto.getWeightedAmount());
		this.dconc = new BigDecimal(dto.getDConc());
		this.dvol = dto.getDvol() == null ? null : new BigDecimal(dto.getDvol());
		this.dConcentrationUnits = dto.getDConcentrationUnits();
		this.weightedAmountUnits = dto.getWeightedAmountUnits();
		this.molecularWeight = dto.getMolecularWeight()== null ? null : new BigDecimal(dto.getMolecularWeight());
		}
	
	public String getAliquotId()
		{
		return aliquotId;
		}
	
	public void setAliquotId(String aliquotId)
		{
		this.aliquotId = aliquotId;
		}
	
	public Location getLocation()
		{
		return location;
		}
	
	public void setLocation(Location location)
		{
		this.location = location;
		}
	
	public Inventory getInventory()
		{
		return inventory;
		}
	
	public void setInventory(Inventory inventory)
		{
		this.inventory = inventory;
		}
	
	public Compound getCompound()
		{
		return compound;
		}

	public void setCompound(Compound compound)
		{
		this.compound = compound;
		}
			
	public Integer getReplicate()
		{
		return replicate;
		}

	public void setReplicate(Integer replicate)
		{
		this.replicate = replicate;
		}
	
	// issue 61 2020
	public Character getDry()
		{
		return dry;
		}

	public void setDry(Character vdry)
		{
		this.dry = vdry;
		}
	
	// issue 61 2020
	public Character getNeat()
		{
		return neat;
		}

	public void setNeat(Character neat)
		{
		this.neat = neat;
		}
	
	// issue 61
	public void setDeleted()
		{
		this.deletedFlag = true;
		}
	
	// issue 61 2020
	public String getSolvent()
		{
		return solvent;
		}

	public void setSolvent(String solvent)
		{
		this.solvent = solvent;
		}
	
    // issue 61 2020
	public Calendar getCreateDate()
		{
		return createDate;
		}

	public void setCreateDate(Calendar createDate)
		{
		this.createDate = createDate;
		}
	
	public String getCreateDateString()
		{
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		return (createDate == null) ? "" : sdf.format(createDate.getTime());
		}
	
	// issue 61 2020
	public String getAliquotLabel()
		{
		return aliquotLabel;
		}

	public void setAliquotLabel(String aliquotLabel)
		{
		this.aliquotLabel = aliquotLabel;
		}
	
	// issue 61 2020
	public String getNotes()
		{
		return notes;
		}

	public void setNotes(String notes)
		{
		this.notes = notes;
		}
	
	// issue 61 2020
	public String getCreatedBy()
		{
		return createdBy;
		}

	public void setCreatedBy(String createdBy)
		{
		this.createdBy = createdBy;
		}
	
	public void setNeatSolVolUnits(String neatSolVolUnits)
		{
		this.neatSolVolUnits = neatSolVolUnits;
		}

	public String getNeatSolVolUnits()
		{
		return neatSolVolUnits;
		}
	
	public BigDecimal getIvol()
		{
		return ivol;
		}
	
	public void setIvol(BigDecimal ivol)
		{
		this.ivol = ivol;
		}
	
	public BigDecimal getDvol()
		{
		return dvol;
		}

	public void setDvol(BigDecimal dvol)
		{
		this.dvol = dvol;
		}
	
	public BigDecimal getDcon()
		{
		return dcon;
		}

	public void setDcon(BigDecimal dcon)
		{
		this.dcon = dcon;
		}
	
	public BigDecimal getIcon()
		{
		return icon;
		}

	public void setIcon(BigDecimal icon)
		{
		this.icon = icon;
		}
	
	public BigDecimal getWeightedAmount()
		{
		return weightedAmount;
		}

	public void setWeightedAmount(BigDecimal weightedAmount)
		{
		this.weightedAmount = weightedAmount;
		}
	
	public BigDecimal getMolecularWeight()
		{
		return molecularWeight;
		}

	public void setMolecularWeight(BigDecimal weightedAmount)
		{
		this.weightedAmount = weightedAmount;
		}
	
	public BigDecimal getDconc()
		{
		return dconc;
		}

	public void setDconc(BigDecimal dconc)
		{
		this.dconc = dconc;
		}
	
	public void setDConcentrationUnits(String dConcentrationUnits)
		{
		this.dConcentrationUnits = dConcentrationUnits;
		}

	public String getDConcentrationUnits()
		{
		return dConcentrationUnits;
		}
	
	// issue 61 2020
    public String getWeightedAmountUnits() 
	    {
		return weightedAmountUnits;
	    }
		
		// issue 61 2020
	public void setWeightedAmountUnits(String weightedAmountUnits) 
	    {
		this.weightedAmountUnits = weightedAmountUnits;
	    }
	
	// issue 79 2020
    public String getDeleteReason() 
	    {
		return deleteReason;
	    }
		
		// issue 79 2020
	public void setDeleteReason(String deleteReason)
	    {
		this.deleteReason = deleteReason;
	    }
	
	}
	
	

package edu.umich.brcf.shared.layers.domain;

//PLATEITEM
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity()
@Table(name = "SAMPLE_PREP")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DISCRIMINATOR", discriminatorType = DiscriminatorType.STRING)
public abstract class Preparation implements Serializable
	{

	public static String idFormat = "(SP)\\d{1}|(SP)\\d{2}|(SP)\\d{3}|(SP)\\d{4}|(SP)\\d{5}|\\d{3}|\\d{4}|\\d{5}";

	public final static HashMap<String, String> INSTRUMENTS = new HashMap<String, String>();
	static
		{
		INSTRUMENTS.put("LC1", "IN0001");
		INSTRUMENTS.put("GC", "IN0002");
		INSTRUMENTS.put("LC2", "IN0016");
		INSTRUMENTS.put("LC3", "IN0020");
		INSTRUMENTS.put("LC4", "IN0021");
		INSTRUMENTS.put("LC5", "IN0022");
		INSTRUMENTS.put("LC6", "IN0023");
		}
	// public static Preparation instance(String title,User creator){
	// return new Preparation(null, title, Calendar.getInstance(), creator);
	// }

	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
			@Parameter(name = "idClass", value = "Preparation"), @Parameter(name = "width", value = "7") })
	@Column(name = "SAMPLE_PREP_ID", unique = true, nullable = false, length = 7, columnDefinition = "CHAR(7)")
	private String prepID;

	@Basic()
	@Column(name = "TITLE", nullable = false, columnDefinition = "VARCHAR2(100)")
	private String title;

	@Basic()
	@Column(name = "PREP_DATE", nullable = false, columnDefinition = "DATE")
	private Calendar prepDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CREATOR", referencedColumnName = "RESEARCHER_ID", nullable = true, columnDefinition = "CHAR(6)")
	private User creator;

	@OneToMany(mappedBy = "samplePrep", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	protected List<PreppedItem> items;

	
	protected Preparation(String prepID, String title, User creator)
		{
		this.prepID = prepID;
		this.title = title;
		this.prepDate = Calendar.getInstance();
		this.creator = creator;
		this.items = new ArrayList<PreppedItem>();
		}

	public String getNodeObjectName()
		{
		return prepID + " - " + title;
		}

	public Preparation() { }

	
	public String getPrepID()
		{
		return prepID;
		}

	public void setPrepID(String prepID)
		{
		this.prepID = prepID;
		}

	public String getTitle()
		{
		return title;
		}

	public void setTitle(String title)
		{
		this.title = title;
		}

	public Calendar getPrepDate()
		{
		return prepDate;
		}

	public void setPrepDate(Calendar prepDate)
		{
		this.prepDate = prepDate;
		}

	public String getPrepDateStr()
		{
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		return sdf.format(getPrepDate().getTime());
		}

	public User getCreator()
		{
		return creator;
		}

	public void setCreator(User creator)
		{
		this.creator = creator;
		}

	public String getCreatorName()
		{
		return creator.getFullName();
		}

	public List<PreppedItem> getItems()
		{
		return items;
		}

	// public List<PrepPlate> getPlateList() {
	// return plateList;
	// }

	public void addPreppedItem(PreppedItem item)
		{
		items.add(item);
		}

	public List<PrepPlate> getPlateList()
		{
		// TODO Auto-generated method stub
		return null;
		}

	// public void addPlate(PrepPlate plate) {
	// plateList.add(plate);
	// }
	}

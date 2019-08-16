package edu.umich.brcf.metabolomics.layers.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import edu.umich.brcf.metabolomics.layers.dto.InstrumentDTO;
import edu.umich.brcf.shared.layers.domain.User;



@Entity()
@Table(name = "INSTRUMENT")
public class Instrument implements Serializable
	{
	public static String LC = "LC";
	public static String GC = "GC";
	public static String CE = "CE";

	
	public static Instrument instance(String instrumentID, String name, String description)
		{
		return new Instrument(instrumentID, name, description);
		}
	
	public static Instrument instance(String instrumentID, String name, String description, String type)
		{
		return new Instrument(instrumentID, name, description, type);
		}

	public static Instrument instance(String id, String name, String description, String type, String room, String manufacturer,
			String model, String serialNumber, String instClass)
		{
		return new Instrument(id, name, description, type, room, manufacturer, model, serialNumber, instClass); 
		}
	
	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
			@Parameter(name = "idClass", value = "Instrument"),
			@Parameter(name = "width", value = "6") })
	@Column(name = "INSTRUMENT_ID", unique = true, nullable = false, length = 6, columnDefinition = "CHAR(6)")
	private String instrumentID;

	@Basic()
	@Column(name = "NAME", nullable = false, columnDefinition = "VARCHAR2(100)")
	private String name;

	@Basic()
	@Column(name = "DESCRIPTION", nullable = true, columnDefinition = "VARCHAR2(100)")
	private String description;

	@Basic()
	@Column(name = "TYPE", nullable = true, columnDefinition = "VARCHAR2(50)")
	private String type;

	@Basic()
	@Column(name = "ROOM", nullable = true, columnDefinition = "VARCHAR2(10)")
	private String room;

	@Basic()
	@Column(name = "MANUFACTURER", nullable = true, columnDefinition = "VARCHAR2(25)")
	private String manufacturer;

	@Basic()
	@Column(name = "MODEL", nullable = true, columnDefinition = "VARCHAR2(50)")
	private String model;

	@Basic()
	@Column(name = "SERIAL_NUMBER", nullable = true, columnDefinition = "VARCHAR2(25)")
	private String serialNumber;

	@Basic()
	@Column(name = "CLASS", nullable = true, columnDefinition = "VARCHAR2(25)")
	private String instrumentClass;

	@OneToMany(mappedBy = "instrument", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<InstrumentRegistry> registry;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "INSTRUMENT_NOTIFICATION_MAP", joinColumns = @JoinColumn(name = "INSTRUMENT_ID"), inverseJoinColumns = @JoinColumn(name = "USER_ID"))
	private Set<User> userNotifySet;

	public Instrument()
		{
		}

	protected Instrument(String id)
		{
		this.instrumentID = id;
		}

	private Instrument(String instrumentID, String name, String description)
		{
		this.instrumentID = instrumentID;
		this.name = name;
		this.description = description;
		this.userNotifySet = new HashSet<User>();
		}

	private Instrument(String instrumentID, String name, String description,
			String type)
		{
		this.instrumentID = instrumentID;
		this.name = name;
		this.description = description;
		this.userNotifySet = new HashSet<User>();
		this.type = type;
		}
	

	private Instrument(String id, String name, String description, String type, String room, String manufacturer, 
		String model, String serialNumber, String instClass)
		{
		this.instrumentID = id;
		this.name = name;
		this.description = description;
		this.type = type;
		this.room = room;
		this.manufacturer = manufacturer;
		this.model = model;
		this.serialNumber = serialNumber;
		this.instrumentClass = instClass;
		}
	
	
	public void update(InstrumentDTO dto)
		{
		this.instrumentID = dto.getInstrumentID();
		this.name = dto.getName();
		this.description = dto.getDescription();
		this.type = dto.getType();
		this.room = dto.getRoom();
		this.manufacturer = dto.getManufacturer();
		this.model = dto.getModel();
		this.serialNumber = dto.getSerialNumber();
		this.instrumentClass = dto.getInstrumentClass();
		}

	
	public String getInstrumentID()
		{
		return instrumentID;
		}

	public String getName()
		{
		return name;
		}

	public String getDescription()
		{
		return description;
		}

	public String getType()
		{
		return type;
		}

	public InstrumentClass getInstrumentClass()
		{
		return InstrumentClass.getEnumValue(instrumentClass);
		}


	public String getRoom()
		{
		return room;
		}

	public String getManufacturer()
		{
		return manufacturer;
		}

	public String getModel()
		{
		return model;
		}

	public InstrumentRegistry getRegistryEntry()
		{
		if (registry == null || registry.size() < 1)
			return null;
		
		return registry.get(0);
		}

	public String getSerialNumber()
		{
		return serialNumber;
		}

	public Set<User> getUserNotifySet()
		{
		return userNotifySet;
		}

	public void addUserNotifyAssociation(User user)
		{
		userNotifySet.add(user);
		}
	}

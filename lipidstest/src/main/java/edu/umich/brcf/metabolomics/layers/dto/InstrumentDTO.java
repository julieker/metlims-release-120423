package edu.umich.brcf.metabolomics.layers.dto;

import java.io.Serializable;

import edu.umich.brcf.metabolomics.layers.domain.Instrument;

public class InstrumentDTO implements Serializable
	{
	public static InstrumentDTO instance(String id, String name,
			String description, String type, String room, String manufacturer,
			String model, String serialNumber, String instClass)
		{
		return new InstrumentDTO(null, name, description, type, room,
				manufacturer, model, serialNumber, instClass);
		}

	private String instrumentID;
	private String name;
	private String description;
	private String type;
	private String room;
	private String manufacturer;
	private String model;
	private String serialNumber;
	private String instrumentClass;

	// Instrument i;

	private InstrumentDTO(String id, String name, String description,
			String type, String room, String manufacturer, String model,
			String serialNumber, String instClass)
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

	public InstrumentDTO(Instrument instrument)
		{
		this.instrumentID = instrument.getInstrumentID();
		this.name = instrument.getName();
		this.description = instrument.getDescription();
		this.type = instrument.getType();
		this.room = instrument.getRoom();
		this.manufacturer = instrument.getManufacturer();
		this.model = instrument.getModel();
		this.serialNumber = instrument.getSerialNumber();
		this.instrumentClass = ""; // instrument.getClass() == null ? "" :
									// InstrumentClass.ANALYTICAL ;
									// //instrument.getInstrumentClass().name();
		}

	public InstrumentDTO()
		{
		}

	public String getInstrumentID()
		{
		return instrumentID;
		}

	public void setInstrumentID(String instrumentID)
		{
		this.instrumentID = instrumentID;
		}

	public String getName()
		{
		return name;
		}

	public void setName(String name)
		{
		this.name = name;
		}

	public String getDescription()
		{
		return description;
		}

	public void setDescription(String description)
		{
		this.description = description;
		}

	public String getType()
		{
		return type;
		}

	public void setType(String type)
		{
		this.type = type;
		}

	public String getRoom()
		{
		return room;
		}

	public void setRoom(String room)
		{
		this.room = room;
		}

	public String getManufacturer()
		{
		return manufacturer;
		}

	public void setManufacturer(String manufacturer)
		{
		this.manufacturer = manufacturer;
		}

	public String getModel()
		{
		return model;
		}

	public void setModel(String model)
		{
		this.model = model;
		}

	public String getSerialNumber()
		{
		return serialNumber;
		}

	public void setSerialNumber(String serialNumber)
		{
		this.serialNumber = serialNumber;
		}

	public String getInstrumentClass()
		{
		return instrumentClass;
		}

	public void setInstrumentClass(String instrumentClass)
		{
		this.instrumentClass = instrumentClass;
		}

	}

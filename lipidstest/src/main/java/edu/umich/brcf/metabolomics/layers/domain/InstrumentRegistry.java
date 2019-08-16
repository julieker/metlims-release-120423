package edu.umich.brcf.metabolomics.layers.domain;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity()
@Table(name = "INSTRUMENT_REGISTRY")
public class InstrumentRegistry implements Serializable {
	public static InstrumentRegistry instance(Instrument instrument, String ipAddress, String queueName,
			String hostName, String dataFolder) {
		return new InstrumentRegistry(instrument, ipAddress, queueName, hostName, dataFolder);
	}

	@Embeddable
	public static class InstrumentRegistryPK implements Serializable {
		public static InstrumentRegistryPK instance(Instrument instrument) {
			return new InstrumentRegistryPK(instrument);
		}

		public static InstrumentRegistryPK instance(String id) {
			return new InstrumentRegistryPK(id);
		}

		@Column(name = "INSTRUMENT_ID")
		private String itemId;

		private InstrumentRegistryPK(Instrument instrument) {
			this.itemId = instrument.getInstrumentID();
		}

		private InstrumentRegistryPK(String id) {
			this.itemId = id;
		}

		public InstrumentRegistryPK() {

		}

		public String getItemId() {
			return itemId;
		}

		public boolean equals(Object o) {
			if (o != null && o instanceof InstrumentRegistryPK) {
				InstrumentRegistryPK that = (InstrumentRegistryPK) o;
				return this.itemId.equals(that.itemId);
			}

			return false;
		}

		public int hashCode() {
			return itemId.hashCode();
		}
	}

	@EmbeddedId
	protected InstrumentRegistryPK id;// = new WellMapPK();

	@Basic()
	@Column(name = "FIRST_REGISTERED", nullable = false, columnDefinition = "TIMESTAMP")
	private Calendar firstRegistered;

	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "CURRENT_STATE", referencedColumnName =
	// "INSTRUMENT_REGISTRY_STATUS", nullable = false)
	// private InstrumentRegistryStatus currentStatus;
	@Basic()
	@Column(name = "CURRENT_STATE", nullable = false, length = 16, columnDefinition = "VARCHAR2(16)")
	private String currentStatus;

	@Basic()
	@Column(name = "LAST_STATE_CHANGE", nullable = false, columnDefinition = "TIMESTAMP")
	private Calendar lastStateChange;

	@Basic()
	@Column(name = "LAST_POLL_RESPONSE", nullable = false, columnDefinition = "TIMESTAMP")
	private Calendar lastPollResponse;

	@Basic()
	@Column(name = "IP_ADDRESS", nullable = false, length = 16, columnDefinition = "VARCHAR2(16)")
	private String ipAddress;

	@Basic()
	@Column(name = "QUEUE_NAME", nullable = false, length = 64, columnDefinition = "VARCHAR2(64)")
	private String queueName;

	@Basic()
	@Column(name = "HOST_NAME", nullable = false, length = 32, columnDefinition = "VARCHAR2(32)")
	private String hostName;

	@Basic()
	@Column(name = "DATA_FOLDER", nullable = false, length = 512, columnDefinition = "VARCHAR2(512)")
	private String dataFolder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INSTRUMENT_ID", referencedColumnName = "INSTRUMENT_ID", nullable = false, insertable = false, updatable = false)
	private Instrument instrument;
	
	@Basic()
	@Column(name = "SAMPLE_RUN_STATUS", length = 1, nullable = true)
	Boolean isSampleSequenceRunning;

	private InstrumentRegistry(Instrument instrument, String ipAddress, String queueName, String hostName,
			String dataFolder) {
		this.id = new InstrumentRegistryPK(instrument);
		this.instrument = instrument;
		this.ipAddress = ipAddress;
		this.queueName = queueName;
		this.hostName = hostName;
		this.dataFolder = dataFolder;
		Calendar cal = Calendar.getInstance();
		this.firstRegistered = cal;
		this.lastPollResponse = cal;
		this.isSampleSequenceRunning=Boolean.valueOf("0");
		//online();
	}

	public InstrumentRegistry() {

	}

	public InstrumentRegistryPK getId() {
		return id;
	}

	/*
	public void online() {
		this.currentStatus = InstrumentRegistryStatus.ONLINE;
		// this.currentStatus =
		// InstrumentRegistryStatus.instance(InstrumentRegistryStatus.ONLINE);
		this.lastStateChange = Calendar.getInstance();
	}

	public void error() {
		this.currentStatus = InstrumentRegistryStatus.ERROR;
		// this.currentStatus =
		// InstrumentRegistryStatus.instance(InstrumentRegistryStatus.ERROR);
		this.lastStateChange = Calendar.getInstance();
	}

	public void offline() {
		this.currentStatus = InstrumentRegistryStatus.OFFLINE;
		// this.currentStatus =
		// InstrumentRegistryStatus.instance(InstrumentRegistryStatus.OFFLINE);
		this.lastStateChange = Calendar.getInstance();
	}
 */
	public void updateLastPollResponse() {
		this.lastPollResponse = Calendar.getInstance();
	}

	public Instrument getInstrument() {
		return instrument;
	}

	public Calendar getLastStateChange() {
		return lastStateChange;
	}

	public Calendar getFirstRegistered() {
		return firstRegistered;
	}

	public Calendar getLastPollResponse() {
		return lastPollResponse;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getQueueName() {
		return queueName;
	}

	public String getHostName() {
		return hostName;
	}

	// public InstrumentRegistryStatus getCurrentStatus() {
	public String getCurrentStatus() {
		return currentStatus;
	}

	public String getDataFolder() {
		return dataFolder;
	}
}

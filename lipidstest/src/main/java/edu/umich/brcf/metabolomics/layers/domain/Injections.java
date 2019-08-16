package edu.umich.brcf.metabolomics.layers.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import edu.umich.brcf.shared.layers.domain.PrepPlate;
import edu.umich.brcf.shared.layers.domain.PreppedItem;
import edu.umich.brcf.shared.layers.domain.ResultsQaQc;
import edu.umich.brcf.shared.util.METWorksStatusEnum;
import edu.umich.brcf.shared.util.utilpackages.CalendarUtils;


@Entity()
@Table(name = "INJECTIONS")
public class Injections implements Serializable {

	public static String STATUS_GOOD = "GOOD";
	public static String STATUS_ERROR = "ERROR";

	public static Injections instance(PrepPlate plate, PreppedItem preppedItem, String fileName, String sequence,
			String mode, long injVol, long sequenceOrder, String sequenceName, String aqMethodName) 
		{
	//	long hack = (injVol == null ? 0 : injVol);
		return new Injections(preppedItem, plate, fileName, sequence, mode, injVol, sequenceOrder, sequenceName, aqMethodName);
	}

	@Id()
	@SequenceGenerator(name = "idGenerator", sequenceName = "INJECTION_SEQ")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idGenerator")
	@Column(name = "INJECTION_ID", unique = true, nullable = false, length = 10)
	protected Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PREP_ITEM_ID", referencedColumnName = "PREP_ITEM_ID", nullable = false)
	private PreppedItem preppedItem;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PLATE_ID", referencedColumnName = "PLATE_ID", nullable = false)
	private PrepPlate plate;

	@Column(name = "FILE_NAME", length = 64, nullable = false)
	private String dataFileName;

	@Column(name = "SEQUENCE", length = 5, nullable = false)
	private String sequence;

	@Column(name = "STATUS", length = 10, nullable = true)
	private String status;

	@Column(name = "DATE_INJECTED", nullable = true)
	private Calendar injectionTime;

	@Column(name = "INJECTION_MODE", nullable = true)
	private String mode;
	
	@Column(name = "SEQUENCE_NAME", nullable = true)
	private String sequenceName;
	
	@Column(name = "SEQUENCE_ORDER", nullable = true)
	private long sequenceOrder;
	
	@Column(name = "INJECTION_VOLUME", nullable = true)
	private long injVol;
	
	@Column(name = "AQMETHOD_NAME", nullable = true)
	private String aqMethodName;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "injection", fetch = FetchType.LAZY)
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<ResultsQaQc> resultsQaQc;

	public Injections() {
	}

	private Injections(PreppedItem preppedItem, PrepPlate plate, String fileName, String sequence, String mode, long injVol, long sequenceOrder, String sequenceName, String aqMethodName) {
		this.preppedItem = preppedItem;
		this.plate = plate;
		this.dataFileName = fileName;
		this.sequence = sequence;
		this.mode = mode;
		this.injVol=injVol;
		this.sequenceOrder=sequenceOrder;
		this.sequenceName=sequenceName;
		this.aqMethodName=aqMethodName;
		this.resultsQaQc = new ArrayList<ResultsQaQc>();
	}

	public boolean equals(Object o) {
		if (o != null && o instanceof Injections) {
			Injections that = (Injections) o;
			return this.id.equals(that.id);
		}
		return false;
	}

	public void addResultsQaQcItem(ResultsQaQc qaItem) {
		this.resultsQaQc.add(qaItem);
	}

	public void statusGood() {
		this.status = STATUS_GOOD;
	}

	public void statusError() {
		this.status = STATUS_ERROR;
	}

	public METWorksStatusEnum getErrorStatus() {
		if (status != null) {
			if (status.equals(STATUS_GOOD))
				return METWorksStatusEnum.GOOD;
			else
				// (status.equals(STATUS_ERROR))
				return METWorksStatusEnum.ERROR;
		} else
			return null;
	}

	public Long getId() {
		return id;
	}

	public String toString() {
		return "Injection==> ID:" + id + "  Plate:" + plate.getPlateID() + "  PreppedItem:" + preppedItem.getItemID()
				+ "  File:" + dataFileName + "  Seq:" + sequence;
	}

	public PreppedItem getPreppedItem() {
		return preppedItem;
	}

	public PrepPlate getPlate() {
		return plate;
	}

	public String getDataFileName() {
		return dataFileName;
	}

	public String getSequence() {
		return sequence;
	}

	public String getStatus() {
		return status;
	}

	public List<ResultsQaQc> getResultsQaQc() {
		return resultsQaQc;
	}

	public void setInjectionTimeNow() {
		injectionTime = Calendar.getInstance();
	}

	public Calendar getInjectionTime() {
		return injectionTime;
	}

	public String getMode() {
		return mode;
	}

	public String getNodeObjectName() {
		return dataFileName + "-" + sequence + "-" + mode + "   "
				+ ((injectionTime != null) ? CalendarUtils.dateTimeDisplayStringFrom(injectionTime.getTime()) : "");
	}

	public String getSequenceName() {
		return sequenceName;
	}

	public long getSequenceOrder() {
		return sequenceOrder;
	}

	public long getInjVol() {
		return injVol;
	}

	public String getAqMethodName() {
		return aqMethodName;
	}
}

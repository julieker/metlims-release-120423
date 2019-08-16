package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import edu.umich.brcf.metabolomics.layers.domain.Injections;
import edu.umich.brcf.metabolomics.layers.dto.AnalyzerProTargetComponentDTO;
import edu.umich.brcf.metabolomics.layers.dto.CEFCompoundBindingDTO;
import edu.umich.brcf.shared.util.METWorksStatusEnum;


@Entity()
@Table(name = "RESULTS_QAQC")
public class ResultsQaQc implements Serializable 
	{
	public static ResultsQaQc instance(Injections injection, CEFCompoundBindingDTO dto) 
		{
		ResultsQaQc qaqc = new ResultsQaQc();
		qaqc.position = dto.getRetentionTime();
		qaqc.injection = injection;
		qaqc.targetComponentDate = Calendar.getInstance();
		qaqc.name =  ""; 
		qaqc.area = dto.getAbundance()!=null?dto.getAbundance().longValue():null;
		qaqc.height = dto.getHeight()!=null?dto.getHeight().longValue():null;
		qaqc.status = "Found";
		return qaqc;
		}


	public static ResultsQaQc instance(Injections injection, AnalyzerProTargetComponentDTO dto) 
		{
		return new ResultsQaQc(injection, dto);
		}


	@Id()
	@SequenceGenerator(name = "idGenerator", sequenceName = "RESULTS_QAQC_SEQ")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idGenerator")
	@Column(name = "RESULTS_QAQC_ID", unique = true, nullable = false, precision = 22, columnDefinition = "NUMBER(22,0)")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INJECTION_ID", referencedColumnName = "INJECTION_ID", nullable = false)
	private Injections injection;

	@Basic()
	@Column(name = "TARGET_COMPONENT_DATE", nullable = false)
	private Calendar targetComponentDate;

	@Basic()
	@Column(name = "NAME", length = 128, nullable = false)
	private String name;

	@Basic
	@Column(name = "EXPECT_POSITION", precision = 20, scale = 5)
	private Double expectPosition;

	@Basic
	@Column(name = "FORWARD_THRESHOLD", precision = 22)
	private Long forwardThreshold;

	@Basic
	@Column(name = "REVERSE_THRESHOLD", precision = 22)
	private Long reverseThreshold;

	@Basic
	@Column(name = "AREA", precision = 22)
	private Long area;

	@Basic
	@Column(name = "HEIGHT", precision = 22)
	private Long height;

	@Basic
	@Column(name = "POSITION", precision = 22, scale = 5)
	private Double position;

	@Basic
	@Column(name = "DELTA", precision = 22, scale = 5)
	private Double delta;

	@Basic
	@Column(name = "FORWARD", precision = 22)
	private Long forward;

	@Basic
	@Column(name = "REVERSE", precision = 22)
	private Long reverse;

	@Basic
	@Column(name = "STATUS", length = 10, nullable = false)
	private String status;

	@Basic
	@Column(name = "CONFIDENCE", precision = 22, scale = 3)
	private Double confidence;

	@Basic
	@Column(name = "COMPONENT_ID", precision = 22)
	private Long componentId;

	@Basic
	@Column(name = "ION_RATIO_STATUS", length = 15)
	private String ionRatioStatus;

	@Transient
	private RequiredStandard standards;

	public ResultsQaQc() {  } 

	
	private ResultsQaQc(Injections injection, AnalyzerProTargetComponentDTO dto) 
		{
		this.injection = injection;
		this.targetComponentDate = Calendar.getInstance();
		this.name = dto.getName();
		this.area = checkLongValue(dto.getArea());
		this.componentId = checkLongValue(dto.getComponentIndex());
		this.confidence = checkDoubleValue(dto.getConfidence());
		this.delta = checkDoubleValue(dto.getDelta());
		this.expectPosition = checkDoubleValue(dto.getExpectPosition());
		this.forward = checkLongValue(dto.getForward());
		this.forwardThreshold = checkLongValue(dto.getForwardThreshold());
		this.height = checkLongValue(dto.getHeight());
		this.ionRatioStatus = dto.getIonRationStatus();
		this.position = checkDoubleValue(dto.getPosition());
		this.reverse = checkLongValue(dto.getReverse());
		this.reverseThreshold = checkLongValue(dto.getReverseThreshold());
		this.status = dto.getStatus();
		}

	private Long checkLongValue(String string) 
		{
		if (!string.equals("") && !string.equals("-"))
			return Long.parseLong(string);
		
		return null;
		}

	private Double checkDoubleValue(String string) 
		{
		if (!string.equals("") || !string.equals("-"))
			return Double.parseDouble(string);
		
		return null;
		}

	
	public Long getId() 
		{
		return id;
		}

	public Double getExpectPosition() {
		return expectPosition;
	}

	public Long getForwardThreshold() {
		return forwardThreshold;
	}

	public Long getReverseThreshold() {
		return reverseThreshold;
	}

	public Long getArea() {
		return area;
	}

	public void updateArea(Long newArea) {
		area = newArea;
	}

	public Long getHeight() {
		return height;
	}

	public Double getPosition() {
		return position;
	}

	public Double getDelta() {
		return delta;
	}

	public Long getForward() {
		return forward;
	}

	public Long getReverse() {
		return reverse;
	}

	public String getStatus() {
		return status;
	}

	public Double getConfidence() {
		return confidence;
	}

	public Long getComponentId() {
		return componentId;
	}

	public String getIonRatioStatus() {
		return ionRatioStatus;
		}
	

	public METWorksStatusEnum getErrorStatus() 
		{
		if (status.equals("Found") && (area - standards.getAreaAverage()) / standards.getAreaStandardDeviation() <= RequiredStandard.MAX_ZSCORE)
			return METWorksStatusEnum.GOOD;
		
		return METWorksStatusEnum.ERROR;
		}

	public void setRequiredStandardValue(RequiredStandard standard) {
		this.standards = standard;
	}

	public String getNodeObjectName() {
		return getName();
	}

	public Injections getInjection() {
		return injection;
	}

	public Calendar getTargetComponentDate() {
		return targetComponentDate;
	}

	public String getName() {
		return name;
	}  
}

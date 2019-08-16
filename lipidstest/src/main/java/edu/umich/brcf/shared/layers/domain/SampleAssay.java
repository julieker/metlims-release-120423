package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity()
@org.hibernate.annotations.Entity(mutable = false)
@Table(name = "SAMPLE_ASSAYS", uniqueConstraints = @UniqueConstraint(columnNames = {
		"SAMPLE_ID", "ASSAY_ID" }))
public class SampleAssay implements Serializable
	{
	public static SampleAssay instance(Sample sample, Assay assay,
			SampleAssayStatus status)
		{
		return new SampleAssay(sample, assay, status);
		}

	@Embeddable
	public static class SampleAssayPK implements Serializable
		{
		public static SampleAssayPK instance(Sample sample, Assay assay)
			{
			return new SampleAssayPK(sample, assay);
			}

		@Column(name = "SAMPLE_ID")
		private String sampleId;

		@Column(name = "ASSAY_ID")
		private String assayId;

		private SampleAssayPK(Sample sample, Assay assay)
			{
			this.sampleId = sample.getSampleID();
			this.assayId = assay.getAssayId();
			}

		public SampleAssayPK()
			{

			}

		public boolean equals(Object o)
			{
			if (o != null && o instanceof SampleAssayPK)
				{
				SampleAssayPK that = (SampleAssayPK) o;
				return this.sampleId.equals(that.sampleId)
						&& this.assayId.equals(that.assayId);
				} else
				return false;
			}

		public int hashCode()
			{
			return sampleId.hashCode() + assayId.hashCode();
			}
		}

	@EmbeddedId
	protected SampleAssayPK id;

	// Issue 250
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SAMPLE_ID", insertable = false, updatable = false)
	@org.hibernate.annotations.ForeignKey(name = "SAMPLE_ASSAYS_FK1")
	private Sample sample;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ASSAY_ID", insertable = false, updatable = false)
	@org.hibernate.annotations.ForeignKey(name = "SAMPLE_ASSAYS_FK2")
	private Assay assay;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "STATUS", referencedColumnName = "ID", nullable = false)
	private SampleAssayStatus status;

	public SampleAssay()
		{
		}

	private SampleAssay(Sample sample, Assay assay, SampleAssayStatus status)
		{
		this.sample = sample;
		this.assay = assay;
		this.id = SampleAssayPK.instance(sample, assay);
		this.status = status;
		}

	public SampleAssayPK getId()
		{
		return id;
		}

	public Sample getSample()
		{
		return sample;
		}

	public Assay getAssay()
		{
		return assay;
		}

	public SampleAssayStatus getStatus()
		{
		return status;
		}

	public void setQueuedStatus()
		{
		status = SampleAssayStatus.instance(SampleAssayStatus.STATUS_QUEUED);
		}

	public void setPreppedStatus()
		{
		status = SampleAssayStatus.instance(SampleAssayStatus.STATUS_PREPPED);
		}

	public void setStatusSamplesRun()
		{
		status = SampleAssayStatus
				.instance(SampleAssayStatus.STATUS_SAMPLES_RUN);
		}

	public void setStatusDataCuration()
		{
		status = SampleAssayStatus
				.instance(SampleAssayStatus.STATUS_DATA_CURATION);
		}

	public void setCompletedStatus()
		{
		status = SampleAssayStatus.instance(SampleAssayStatus.STATUS_COMPLETE);
		}

	public void setPrepStatus()
		{
		status = SampleAssayStatus.instance(SampleAssayStatus.STATUS_PREPPED);
		}

	// public void setInjectedStatus() {
	// status =
	// SampleAssayStatus.instance(SampleAssayStatus.STATUS_SAMPLES_RUN);
	// }

	public void setProcessedStatus()
		{
		status = SampleAssayStatus.instance(SampleAssayStatus.STATUS_COMPLETE);
		}

	// public void setSamplesReceivedStatus()
	// {
	// status = SampleAssayStatus.instance(SampleAssayStatus.STATUS_RECEIVED);
	// }

	public void setStatusExcluded()
		{
		status = SampleAssayStatus.instance(SampleAssayStatus.STATUS_EXCLUDED);
		}
	}

package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import edu.emory.mathcs.backport.java.util.Arrays;

@Entity()
@Table(name = "SAMPLE_ASSAY_STATUS")
public class SampleAssayStatus implements Serializable
	{
	// public static Character STATUS_RECEIVED = 'S';
	public static Character STATUS_QUEUED = 'Q';
	public static Character STATUS_PREPPED = 'P';
	public static Character STATUS_SAMPLES_RUN = 'R';
	public static Character STATUS_DATA_CURATION = 'D';
	public static Character STATUS_COMPLETE = 'C';
	public static Character STATUS_EXCLUDED = 'X';
	

	public static List<Character> Lims_Sample_statuses = Arrays
			.asList(new Character[] { 'S', 'Q', 'P', 'R', 'D', 'C', 'X' });
	public static List<Character> Lims_Sample_Assay_statuses = Arrays
			.asList(new Character[] { 'Q', 'P', 'R', 'D', 'C', 'X' });

	public static SampleAssayStatus instance(Character id)
		{
		return new SampleAssayStatus(id);
		}

	@Id()
	@Column(name = "ID", unique = true, nullable = false, columnDefinition = "CHAR(1)")
	Character id;

	@Basic()
	@Column(name = "STATUS_VALUE", nullable = false, columnDefinition = "VARCHAR2(32)")
	String statusValue;

	public SampleAssayStatus()
		{
		}

	private SampleAssayStatus(Character id)
		{
		this.id = id;
		}

	public Character getId()
		{
		return id;
		}

	public String getStatusValue()
		{
		return statusValue;
		}

	}

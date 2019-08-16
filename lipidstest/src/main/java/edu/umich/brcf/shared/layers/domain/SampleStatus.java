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
@Table(name = "SAMPLE_STATUS")
public class SampleStatus implements Serializable
	{
	public static Character STATUS_IN_STORAGE = 'S';
	public static Character STATUS_COMPLETE = 'C';
	public static Character STATUS_IN_PREP = 'P';
	public static Character STATUS_IN_ANALYSIS = 'A';
	public static Character STATUS_INJECTED = 'I';
	public static Character STATUS_PROCESSED = 'R';
	public static Character STATUS_DATA_CURATION = 'D';
	public static Character STATUS_DISCARDED = 'T';
	public static Character STATUS_RETURNED = 'B'; // Issue 222

	public static List<Character> Lims_Sample_statuses = Arrays
			.asList(new Character[] { 'S', 'P', 'I', 'R', 'C', 'B', 'T' }); // Issue 222

	public static SampleStatus instance(Character id)
		{
		return new SampleStatus(id);
		}

	// EditSampleStatus
	@Id()
	@Column(name = "ID", unique = true, nullable = false, columnDefinition = "CHAR(1)")
	Character id;

	@Basic()
	@Column(name = "STATUS_VALUE", nullable = false, columnDefinition = "VARCHAR2(32)")
	String statusValue;

	public SampleStatus()
		{

		}

	private SampleStatus(Character id)
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

package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;


@Entity()
@Table(name = "EXPERIMENTAL_GROUP")
public class ExperimentalGroup implements Serializable
	{

	public static ExperimentalGroup instance(String group_name, Experiment exp, String group_description)
		{
		return new ExperimentalGroup(null, group_name, exp, group_description);
		}

	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
			@Parameter(name = "idClass", value = "ExperimentalGroup"), @Parameter(name = "width", value = "7") })
	@Column(name = "GROUP_ID", unique = true, nullable = false, length = 7, columnDefinition = "CHAR(7)")
	private String groupID;

	@Basic()
	@Column(name = "GROUP_NAME", nullable = false, columnDefinition = "VARCHAR2(100)")
	private String group_name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EXP_ID", referencedColumnName = "EXP_ID", nullable = false)
	private Experiment exp;

	@Basic()
	@Column(name = "GROUP_DESCRIPTION", nullable = true, columnDefinition = "VARCHAR2(1000)")
	private String group_description;

	
	private ExperimentalGroup(String groupID, String group_name, Experiment exp, String group_description)
		{
		this.groupID = groupID;
		this.group_name = group_name;
		this.exp = exp;
		this.group_description = group_description;
		}

	public ExperimentalGroup() {   } 

	
	public String getGroupID()
		{
		return groupID;
		}

	public String getGroup_name()
		{
		return group_name;
		}

	public Experiment getExp()
		{
		return exp;
		}

	public String getGroup_description()
		{
		return group_description;
		}
	}

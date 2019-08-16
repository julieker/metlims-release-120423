package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity()
@Table(name = "SUBJECT")
public class Subject implements Serializable
	{
	public static Subject instance(String subjectId, Long taxId, String userSubjectId)
		{
		return new Subject(subjectId, taxId, userSubjectId);
		}

	@Id()
	// @GeneratedValue(generator = "IdGeneratorDAO")
	// @GenericGenerator(name = "IdGeneratorDAO", strategy =
	// "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
	// @Parameter(name = "idClass", value = "Subject"), @Parameter(name =
	// "width", value = "9") })
	@Column(name = "SUBJECT_ID", unique = true, nullable = false, length = 9, columnDefinition = "CHAR(9)")
	private String subjectId;

	@Basic()
	@Column(name = "SUBJECT_TAX_ID", nullable = false)
	private Long taxId;

	@Basic()
	@Column(name = "USER_SUBJECT_ID", nullable = true)
	private String userSubjectId;

	
	private Subject(String subjectId, Long taxId, String userSubjectId)
		{
		this.subjectId = subjectId;
		this.taxId = taxId;
		this.userSubjectId = userSubjectId;
		}

	public Subject()  {   } 

	
	public String getSubjectId()
		{
		return subjectId;
		}

	public Long getTaxId()
		{
		return taxId;
		}

	public String getUserSubjectId()
		{
		return userSubjectId;
		}
	}

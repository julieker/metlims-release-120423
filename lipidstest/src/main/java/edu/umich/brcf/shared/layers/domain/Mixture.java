package edu.umich.brcf.shared.layers.domain;
/***************************
 *Created by Julie Keros issue 94
 * 
 * 
 ********************/

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Entity;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import javax.persistence.Table;

@Entity()
@Table(name = "MIXTURE")

// issue 61
public class Mixture implements Serializable 
	{
	public static String MIXTURE_DATE_FORMAT = "MM/dd/yy";

	public static Mixture instance( Calendar createDate,  User createdBy, BigDecimal volSolvent, BigDecimal desiredFinalVol  ) 
		{
		return new Mixture(null, createDate, createdBy,  volSolvent,  desiredFinalVol);
		}

	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
	@Parameter(name = "idClass", value = "Mixture"), @Parameter(name = "width", value = "10") })
	@Column(name = "MIXTURE_ID", nullable = false, unique = true, length = 10, columnDefinition = "CHAR(10)")
	private String mixtureId;
	
	@Basic()
	@Column(name = "CREATE_DATE", nullable = true)
	private Calendar createDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CREATED_BY", nullable = true, columnDefinition = "CHAR(6)")
	private User createdBy;
	
	@Basic()
	@Column(name = " VOLUME_SOLVENT_TO_ADD", columnDefinition = "NUMBER(15,7)")
	private BigDecimal volSolvent;
	
	@Basic()
	@Column(name = "DESIRED_FINAL_VOLUME", columnDefinition = "NUMBER(15,7)")
	private BigDecimal desiredFinalVol;
	
	public Mixture() {  }
	
	private Mixture(String mixtureId ,  Calendar createDate, User createdBy, BigDecimal volSolvent, BigDecimal desiredFinalVol )
		{
		this.mixtureId = mixtureId;
		this.createDate = createDate;
		this.createdBy = createdBy;
		this.volSolvent = volSolvent;
		this.desiredFinalVol = desiredFinalVol;
		}

	public String getMixtureId()
		{
		return mixtureId;
		}
	
	public void setMixtureId(String mixtureId)
		{
		this.mixtureId = mixtureId;
		}
	
	public Calendar getCreateDate()
		{
		return createDate;
		}

	public void setCreateDate(Calendar createDate)
		{
		this.createDate = createDate;
		}
	
	public User getCreatedBy()
		{
		return createdBy;
		}

	public void setCreatedBy(User createdBy)
		{
		this.createdBy = createdBy;
		}
	public BigDecimal getVolSolvent()
		{
		return volSolvent;
		}

	public void setVolSolvent(BigDecimal volSolvent)
		{
		this.volSolvent = volSolvent;
		}
	
	public BigDecimal getDesiredFinalVol()
		{
		return desiredFinalVol;
		}

	public void setDesiredFinalVol(BigDecimal desiredFinalVol)
		{
		this.volSolvent = desiredFinalVol;
		}
	
	public String getCreateDateString()
		{
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		return (createDate == null) ? "" : sdf.format(createDate.getTime());
		}
	}
	
	

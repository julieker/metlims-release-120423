package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import edu.umich.brcf.shared.layers.dto.ShortcodeDTO;


@Entity()
@org.hibernate.annotations.Entity(mutable = true)
@Table(name = "SHORTCODES", uniqueConstraints = @UniqueConstraint(columnNames = { "SHORTCODE", "EXP_ID" }))
public class Shortcode implements Serializable
	{
	public static Shortcode instance(String code, String NIH_GrantNumber, Experiment exp) 
		{
		return new Shortcode(code, NIH_GrantNumber, exp);
		}

	public static Shortcode instance(String code, String NIH_GrantNumber,
			Experiment exp, String NIH_GrantNumber_2, String NIH_GrantNumber_3) 
		{
		return new Shortcode(code, NIH_GrantNumber, exp, NIH_GrantNumber_2, NIH_GrantNumber_3);
		}
	
	@Embeddable
	public static class ShortcodePK implements Serializable 
		{
		public static ShortcodePK instance(String code, Experiment exp) 
			{
			return new ShortcodePK(code, exp);
			}

		@Column(name = "SHORTCODE", length = 20)
		private String code;

		@Column(name = "EXP_ID")
		private String expId;
		
		private ShortcodePK(String code, Experiment exp) 
			{
			this.code = code;
			this.expId = exp.getExpID();
			}

		public ShortcodePK() { }

		public boolean equals(Object o) 
			{
			if (o != null && o instanceof ShortcodePK) 
				{
				ShortcodePK that = (ShortcodePK) o;
				return this.code.equals(that.code) && this.expId.equals(that.expId);
				}
			return false;
			}

		public int hashCode() { return code.hashCode() + expId.hashCode(); }
		}

	@EmbeddedId
	protected ShortcodePK id;
	
	@Column(name = "SHORTCODE", insertable = false, updatable = false)
	private String code;
	
	@Basic()
	@Column(name = "NIH_GRANT_NUMBER", nullable = true, length = 100)
	private String NIH_GrantNumber;
	
	@Basic()
	@Column(name = "NIH_GRANT_NUMBER_2", nullable = true, length = 100)
	private String NIH_GrantNumber_2;
	
	@Basic()
	@Column(name = "NIH_GRANT_NUMBER_3", nullable = true, length = 100)
	private String NIH_GrantNumber_3;
	
	//issue 250
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EXP_ID", insertable = false, updatable = false)
	@org.hibernate.annotations.ForeignKey(name = "SHORTCODES_FK1")
	
	private Experiment exp;

	
	public Shortcode() { }

	
	private Shortcode(String code, String NIH_GrantNumber, Experiment exp) 
		{
		this(code, NIH_GrantNumber, exp, null, null);
		}
	
	
	private Shortcode(String code, String NIH_GrantNumber, Experiment exp,String NIH_GrantNumber_2, String NIH_GrantNumber_3)
		{
		this.code=code;
		this.NIH_GrantNumber=NIH_GrantNumber;
		this.exp=exp;
		this.id = ShortcodePK.instance(code, exp);
		this.NIH_GrantNumber_2 = NIH_GrantNumber_2;
		this.NIH_GrantNumber_3 = NIH_GrantNumber_3;
		}
	
	
	public void updateGrantNums(ShortcodeDTO dto)
		{
		this.NIH_GrantNumber = dto.getNIH_GrantNumber();
		this.NIH_GrantNumber_2 = dto.getNIH_GrantNumber_2();
		this.NIH_GrantNumber_3 = dto.getNIH_GrantNumber_3();
		}

	
	public ShortcodePK getId() 
		{
		return id;
		}

	public String getCode() 
		{
		return code;
		}

	public String getNIH_GrantNumber() 
		{
		return NIH_GrantNumber;
		}

	public Experiment getExp() 
		{
		return exp;
		}
	
	public String getNIH_GrantNumber_2() 
		{
		return NIH_GrantNumber_2;
		}

	public String getNIH_GrantNumber_3() 
		{
		return NIH_GrantNumber_3;
		}

	public void setNIH_GrantNumber(String nIH_GrantNumber)
		{
		NIH_GrantNumber = nIH_GrantNumber;
		}

	public void setNIH_GrantNumber_2(String nIH_GrantNumber_2)
		{
		NIH_GrantNumber_2 = nIH_GrantNumber_2;
		}

	public void setNIH_GrantNumber_3(String nIH_GrantNumber_3)
		{
		NIH_GrantNumber_3 = nIH_GrantNumber_3;
		}
	
	
	}

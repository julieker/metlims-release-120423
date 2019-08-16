
package edu.umich.brcf.metabolomics.layers.domain;


import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.wicket.util.io.IClusterable;

import edu.emory.mathcs.backport.java.util.Arrays;
import edu.umich.brcf.shared.layers.dto.CompoundNameDTO;


@Entity()
@Table(name = "NAMES", uniqueConstraints = @UniqueConstraint(columnNames = { "CID", "NAME" }))
//  
public class CompoundName implements IClusterable 
	{
	public static String PRIMARY_NAME_TYPE = "pri";
	public static String SYNONYM_NAME_TYPE = "sym";
	public static List<String> TYPES = Arrays.asList(new String[] { PRIMARY_NAME_TYPE, SYNONYM_NAME_TYPE, "sys", "acr", "err" });

	public static CompoundName instance(String html, Compound compound, String name, String nameType) {
		return new CompoundName(html, compound, name, nameType);
	}

	@Id()
	@Column(name = "NAME", nullable = false, length = 500)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CID", referencedColumnName = "CID", nullable = false)
	private Compound compound;

	@Basic()
	@Column(name = "HTML", nullable = true, length = 500)
	private String html;

	@Basic()
	@Column(name = "TYPE", nullable = true, length = 3)
	private String nameType;

	public CompoundName() {
	}

	private CompoundName(String html, Compound compound, String name, String nameType) {
		this.html = html;
		this.compound = compound;
		this.name = name;
		this.nameType = nameType;
	}

	public void update(CompoundNameDTO dto, Compound cmpd) {
		this.html = dto.getHtml();
		this.compound = cmpd;
		this.name = dto.getNewName();
		this.nameType = dto.getType();
	}
	
	public String getHtml() {
		return html;
	}

	public Compound getCompound() {
		return compound;
	}

	public String getName() {
		return name;
	}

	public String getNameType() {
		return nameType;
	}
}

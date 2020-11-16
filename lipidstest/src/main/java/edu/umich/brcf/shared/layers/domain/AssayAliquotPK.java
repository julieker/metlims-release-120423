/******************
 * Created by Julie Keros
 * Date: Nov 11 2020 
 * To store assays associated with aliquots
 * 
 * 
 */
// issue 100


package edu.umich.brcf.shared.layers.domain;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

	@Embeddable
	public  class AssayAliquotPK implements Serializable
		{
		public static AssayAliquotPK instance(Assay assay, Aliquot aliquot)
			{
			return new AssayAliquotPK(assay, aliquot);
			}

		@Column(name = "ASSAY_ID")
		private String assayId;

		@Column(name = "ALIQUOT_ID")
		private String aliquotId;

		private AssayAliquotPK(Assay assay, Aliquot aliquot)
			{
			this.assayId= assay.getAssayId();
			this.aliquotId = aliquot.getAliquotId();
			}

		public AssayAliquotPK()
			{

			}

		public boolean equals(Object o)
			{
			if (o != null && o instanceof AssayAliquotPK)
				{
				AssayAliquotPK that = (AssayAliquotPK) o;
				return this.assayId.equals(that.assayId)
						&& this.aliquotId.equals(that.aliquotId);
				} else
				return false;
			}

		public int hashCode()
			{
			return assayId.hashCode() + aliquotId.hashCode();
			}
		}






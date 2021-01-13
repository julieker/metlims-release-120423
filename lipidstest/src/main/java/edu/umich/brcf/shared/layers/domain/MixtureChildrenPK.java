/******************
 * Created by Julie Keros
 * Date: Nov 30 2020 
 * To store mixtures and aliquots
 * 
 * 
 */
// issue 100


package edu.umich.brcf.shared.layers.domain;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

	@Embeddable
	public  class MixtureChildrenPK implements Serializable
		{
		public static MixtureChildrenPK instance(Mixture mixture, Mixture parentMixture)
			{
			return new MixtureChildrenPK(mixture, parentMixture);
			}

		@Column(name = "MIXTURE_ID")
		private String mixtureid;

		@Column(name = "PARENT_MIXTURE_ID")
		private String parentMixtureid;

		private MixtureChildrenPK(Mixture mixture, Mixture parentMixture)
			{
			this.mixtureid= mixture.getMixtureId();
			this.parentMixtureid = parentMixture.getMixtureId();
			}

		public MixtureChildrenPK()
			{

			}

		public boolean equals(Object o)
			{
			if (o != null && o instanceof MixtureChildrenPK)
				{
				MixtureChildrenPK that = (MixtureChildrenPK) o;
				return this.mixtureid.equals(that.mixtureid)
						&& this.parentMixtureid.equals(that.parentMixtureid);
				} else
				return false;
			}

		public int hashCode()
			{
			return mixtureid.hashCode() + parentMixtureid.hashCode();
			}
		}






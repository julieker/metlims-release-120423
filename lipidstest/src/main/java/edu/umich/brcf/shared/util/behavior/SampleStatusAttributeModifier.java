package edu.umich.brcf.shared.util.behavior;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.model.AbstractReadOnlyModel;

public class SampleStatusAttributeModifier
	{
	public static AttributeModifier create(final Character status)
		{
		return SampleStatusAttributeModifier(status);
		}

	// public static AttributeModifier create(final String status) {
	// return SampleStatusAttributeModifier(status));
	// }

	private static AttributeModifier SampleStatusAttributeModifier(
			final Character status)
		{ // issue 464
		return new AttributeModifier("class",  new AbstractReadOnlyModel()
			{
				public Object getObject()
					{
					String modfr = "";
					switch (status)
						{
						case 'S':
						modfr = "storage";
							break;
						case 'P':
						modfr = "prepped";
							break;
						case 'I':
						modfr = "injected";
							break;
						case 'R':
						modfr = "processed";
							break;
						case 'C':
						modfr = "complete";
							break;
						default:
						modfr = "unknown";
							break;
						// throw new IllegalStateException(status +
						// " is not mapped!");
						}
					return modfr;
					}
			});
		}

	}

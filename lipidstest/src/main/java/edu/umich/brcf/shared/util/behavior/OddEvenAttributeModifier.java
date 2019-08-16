package edu.umich.brcf.shared.util.behavior;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;

public class OddEvenAttributeModifier {
	public static AttributeModifier create(final Item item) {
		return oddEvenAttributeModifier(item.getIndex());
	}

	public static AttributeModifier create(final ListItem item) {
		return oddEvenAttributeModifier(item.getIndex());
	}
// issue 464
	private static AttributeModifier oddEvenAttributeModifier(final int index) {
		return new AttributeModifier("class",  new AbstractReadOnlyModel() {
			public Object getObject() {
				return (index % 2 == 1) ? "even" : "odd";
			}
		});
	}
}


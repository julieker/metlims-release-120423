////////////////////////////////////////////////////
// OptGroup.java
// Written by Jan Wigginton, Dec 4, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.widgets;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOption;
import org.apache.wicket.markup.ComponentTag;


public class OptGroup extends SelectOption 
{ 
        String label; 
        
        public OptGroup(String id, String label) 
        { 
                super(id); 
                this.label = label; 
        } 
        
        protected void onComponentTag(final ComponentTag tag) 
        { 
                checkComponentTag(tag, "optgroup"); 
                Select select = (Select)findParent(Select.class); 
                if (select == null) 
                { 
                        throw new WicketRuntimeException( 
                                        "OptGroup component [" + 
                                                        getPath() + 
                                                        "] cannot find its parent Select. All OptGroup components must be a child of or below in the hierarchy of a Select component."); 
                } 

                
                tag.put("label", label); 
        } 
}


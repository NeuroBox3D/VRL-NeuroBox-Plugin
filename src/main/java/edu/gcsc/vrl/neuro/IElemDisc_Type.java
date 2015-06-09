package edu.gcsc.vrl.neuro;

import edu.gcsc.vrl.ug.api.I_IElemDisc;
import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.visual.VBoxLayout;

/**
 *
 * @author mbreit
 */
@TypeInfo(type=I_IElemDisc.class, input = true, output = true, style="default")
public class IElemDisc_Type extends TypeRepresentationBase
{
    private static final long serialVersionUID = 1L;
    
    public IElemDisc_Type()
    {
        // Defines the value name. This will set the caption of the name label.
        setValueName("IElemDisc");
        
        // Sets the layout.
        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.Y_AXIS);
        setLayout(layout);

        // Adds components to the type representation.
        this.add(nameLabel);
    }
}

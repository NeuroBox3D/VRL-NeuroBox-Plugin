package edu.gcsc.vrl.neurobox.types;

import edu.gcsc.vrl.ug.api.I_ICableMembraneTransport;
import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.visual.VBoxLayout;

/**
 *
 * @author mbreit
 * @date   2015-06-12
 */
@TypeInfo(type=I_ICableMembraneTransport.class, input = true, output = true, style="default")
public class IMembraneTransport_Type extends TypeRepresentationBase
{
    private static final long serialVersionUID = 1L;
    
    public IMembraneTransport_Type()
    {
        // Defines the value name. This will set the caption of the name label.
        setValueName("IMembraneTransport");
        
        // Sets the layout.
        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.Y_AXIS);
        setLayout(layout);

        // Adds components to the type representation.
        this.add(nameLabel);
    }
}

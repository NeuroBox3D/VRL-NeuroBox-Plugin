package edu.gcsc.vrl.neurobox.types;

import edu.gcsc.vrl.ug.api.I_ICableMembraneTransport;
import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.types.ArrayBaseType;

/**
 *
 * @author mbreit
 */
@TypeInfo(type=I_ICableMembraneTransport[].class, input = true, output = true, style="array")
public class IMembraneTransport_ArrayType extends ArrayBaseType
{
    private static final long serialVersionUID = 1L;
    
    public IMembraneTransport_ArrayType()
    {
        setValueName("IChannel Array");
    }
}

package edu.gcsc.vrl.neurobox.types;

import edu.gcsc.vrl.ug.api.I_IChannel;
import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.types.ArrayBaseType;

/**
 *
 * @author mbreit
 */
@TypeInfo(type=I_IChannel[].class, input = true, output = true, style="array")
public class IChannel_ArrayType extends ArrayBaseType
{
    private static final long serialVersionUID = 1L;
    
    public IChannel_ArrayType()
    {
        setValueName("IChannel Array");
    }
}

package edu.gcsc.vrl.neurobox.types;

import edu.gcsc.vrl.ug.api.I_IElemDisc;
import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.types.ArrayBaseType;

/**
 *
 * @author mbreit
 */
@TypeInfo(type=I_IElemDisc[].class, input = true, output = true, style="array")
public class IElemDisc_ArrayType extends ArrayBaseType
{
    private static final long serialVersionUID = 1L;
    
    public IElemDisc_ArrayType()
    {
        setValueName("IElemDisc Array");
    }
}

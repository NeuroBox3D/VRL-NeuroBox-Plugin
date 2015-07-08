/* 
 * FileSilentType.java
 */

package edu.gcsc.vrl.neurobox.types;

import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import java.io.File;


/**
 * TypeRepresentation for <code>java.io.File</code>.
 * @author mbreit
 */
@TypeInfo(type=File.class, input = true, output = true, style="silent")
public class FileSilentType extends TypeRepresentationBase
{
    public FileSilentType() {}
}


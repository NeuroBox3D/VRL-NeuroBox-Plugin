/**
 * Resource path provider for NeuroBox plugin.
 * 
 * This class holds the static data member "path2plugin" which contains the path
 * to the user's NeuroBox plugin in their VRL folder (which is written to this
 * variable by the plugin configurator).
 * 
 * It can be used, e.g. to load geometries for template projects contained in a
 * plugin.
 * 
 * @date 2015-06-24
 * @author mbreit
**/

package edu.gcsc.vrl.neurobox.control;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.system.VMessage;
import java.io.File;
import java.io.IOException;

import java.io.Serializable;


@ComponentInfo(name="ResourcePathProvider", category="Neuro")
public class ResourcePathProvider implements Serializable
{
    private static final long serialVersionUID = 1L;
    public static File path2plugin;
    
    @MethodInfo(name="get resource file", valueName="file")//, callOptions="autoinvoke")
    public File getResourceFile
    (
        @ParamInfo(name="file name") String fName
    )
    {
        File f = new File(path2plugin, fName);
        
        if (!f.exists())
        {
            VMessage.exception("File not found",
                "The file '"+path2plugin.getAbsolutePath()+File.separator+fName+"' can not be found.");
            
            return null;
        }
        
        return f;
    }
    
    @MethodInfo(name="create resource file", valueName="file")//, callOptions="autoinvoke")
    public File createResourceFile
    (
        @ParamInfo(name="file name") String fName
    )
    {
        File f = new File(path2plugin, fName);
        
        try
        {
            f.createNewFile();
        }
        catch (IOException ex)
        {
            VMessage.exception("File creation error",
            "I/O error while trying to create file '"
            +path2plugin.getAbsolutePath()+fName+"': "+ex.getMessage());
        }
        catch (SecurityException ex)
        {
            VMessage.exception("File creation error",
            "No access granted to file '"
            +path2plugin.getAbsolutePath()+fName+"' : "+ex.getMessage());
        }
        
        return f;
    }
}

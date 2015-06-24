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
import eu.mihosoft.vrl.system.VMessage;
import java.io.File;

import java.io.Serializable;


@ComponentInfo(name="ResourcePathProvider", category="Neuro")
public class ResourcePathProvider implements Serializable
{
    private static final long serialVersionUID = 1L;
    public static File path2plugin;
    
    @MethodInfo(name="get network geometry file", valueName="file", callOptions="autoinvoke")
    public File networkGeometry()
    {
        File f = new File(path2plugin, "testNetwork.ugx");
        
        if (!f.exists())
        {
            VMessage.exception("File not found",
                "The file "+path2plugin.getAbsolutePath()+"/testNetwork.ugx"+" can not be found.");
            
            return null;
        }
        
        return f;
    }
}

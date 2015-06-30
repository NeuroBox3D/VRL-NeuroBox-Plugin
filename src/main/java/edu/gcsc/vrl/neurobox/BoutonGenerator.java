/**
 * BoutonGenerator.java
 * 
 * @date 2014-06-18
 * @author mstepnie
**/

package edu.gcsc.vrl.neurobox;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.system.VMessage;
import java.io.File;
import java.io.Serializable;


@ComponentInfo(name="BoutonGenerator", category="Neuro")
public class BoutonGenerator implements java.io.Serializable{
    private static final long serialVersionUID = 1L;

    @MethodInfo(name="BuildBouton", valueName="file")
    public File BuildBouton(@ParamInfo(name="File name", style="save-dialog", options="tag=\"TheFile\"") java.io.File file,
                            @ParamInfo(name="Create extracellular space", options="value=true") boolean bExtSpace,
                            @ParamInfo(name="bouton radius [µm]", options="value=1.0") double radius,
                            @ParamInfo(name="num. refinements", options="value=3") int numRefinements,
                            @ParamInfo(name="num. release sites", options="value=12") int numReleaseSites,
                            @ParamInfo(name="T-bar Height [µm]" , options="value=0.07") double TbarHeight,
                            @ParamInfo(name="T-bar Leg-radius [µm]", options="value=0.02") double TbarLegRadius,
                            @ParamInfo(name="T-bar Top-radius [µm]", options="value=0.15") double TbarTopRadius,
                            @ParamInfo(name="T-bar Top-height [µm]", options="value=0.015") double TbarTopHeight) {
                              
        
        String fileName = file.getAbsoluteFile().getAbsolutePath();
       
        edu.gcsc.vrl.ug.api.F_BuildBouton.invoke( bExtSpace,
                                                  radius, 
                                                  numRefinements, 
                                                  numReleaseSites, 
                                                  TbarHeight, 
                                                  TbarLegRadius, 
                                                  TbarTopRadius, 
                                                  TbarTopHeight,
                                                  fileName);
        
        if (!file.exists())
        {
            VMessage.exception("BoutonGenerator failed",
                "The geometry supposedly created by the bouton generator can not be found.");
            
            return null;
        }
        
        return file;
    }
    
}

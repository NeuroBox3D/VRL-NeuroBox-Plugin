package edu.gcsc.vrl.neurobox.membrane_transport;

import edu.gcsc.vrl.ug.api.ChannelHH;
import edu.gcsc.vrl.ug.api.I_ChannelHH;
import edu.gcsc.vrl.userdata.UserDataTuple;
import edu.gcsc.vrl.userdata.UserDependentSubsetModel;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.Serializable;

/**
 *
 * @author mbreit
 * @date 22-06-2015
 */

@ComponentInfo(name="Hodgkin-Huxley", category="Neuro")
public class Hodgkin_Huxley implements Serializable
{
    private static final long serialVersionUID = 1L;

    private transient I_ChannelHH hhChannel = null;
    
    /**
     *
     * @param hhData
     * @return Hodgkin-Huxley channel object
     */
    @MethodInfo(name="create HH channel", valueName="HH channel", initializer=true, hide=false, interactive=false)
    public I_ChannelHH create
    (
        @ParamInfo(name="", style="default", options="ugx_globalTag=\"gridFile\"; fct_tag=\"fctDef\"; type=\"S1:membrane potential\"")
        UserDataTuple hhData
    )
    {
        String[] selFct = ((UserDependentSubsetModel.FSDataType) hhData.getData(0)).getSelFct();
        if (selFct.length != 1) throw new RuntimeException("Hodgkin-Huxley channel mechanism needs exactly one function, but has "+selFct.length+".");
        
        String[] selSs = ((UserDependentSubsetModel.FSDataType) hhData.getData(0)).getSelSs();
        if (selSs.length == 0) throw new RuntimeException("No subset definition in Hodgkin-Huxley channel definition!");
        
        // construct HH object
        hhChannel = new ChannelHH(selFct, selSs);
        
        return hhChannel;
    }
}

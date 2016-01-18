package edu.gcsc.vrl.neurobox.membrane_transport.cable;

import edu.gcsc.vrl.ug.api.ChannelHH;
import edu.gcsc.vrl.ug.api.I_ChannelHH;
import edu.gcsc.vrl.ug.api.I_ConstUserNumber;
import edu.gcsc.vrl.userdata.UserDataTuple;
import edu.gcsc.vrl.userdata.UserDependentSubsetModel;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.Serializable;

/**
 * Hodgkin-Huxley mechanism
 * This class is a VRL visualization for the Hodgkin-Huxley transport
 * implementation usable in a 1D cable equation.
 * 
 * @author mbreit
 * @date 22-06-2015
 */

@ComponentInfo
(
    name="Hodgkin-Huxley",
    category="Neuro/cable",
    description="Hodgkin-Huxley transport mechanism usable in a 1D cable equation"
)
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
    
    @MethodInfo(name="set conductances global", interactive = false)
    public void set_conductances_global
    (
        @ParamInfo(name=" K cond [S/m^2]", style="default", options="value=360.0") double gK,
        @ParamInfo(name="Na cond [S/m^2]", style="default", options="value=1200.0") double gNa
    )
    {
        check_channel_exists();
        check_value(gK);
        check_value(gNa);
        
        hhChannel.set_conductances(gK, gNa);
    }
    
    @MethodInfo(name="set conductances", interactive = false)
    public void set_conductances
    (
        @ParamInfo(name=" ", style="array", options="ugx_globalTag=\"gridFile\"; type=\"s|n|n:subset,gK [S/m^2],gNa [S/m^2]\"") UserDataTuple[] condData
    )
    {
        check_channel_exists();
        
        for (int i = 0; i < condData.length; ++i)
        {
            String selSs = condData[i].getSubset(0);
            if (! (condData[i].getNumberData(1) instanceof I_ConstUserNumber))
                eu.mihosoft.vrl.system.VMessage.exception("Invalid specification: ",
                    "Hodgkin-Huxley channel conductance cannot be given as code.");
            
            if (! (condData[i].getNumberData(2) instanceof I_ConstUserNumber))
                eu.mihosoft.vrl.system.VMessage.exception("Invalid specification: ",
                    "Hodgkin-Huxley channel conductance cannot be given as code.");
            
            double gK = ((I_ConstUserNumber) condData[i].getNumberData(1)).const__get();
            double gNa = ((I_ConstUserNumber) condData[i].getNumberData(2)).const__get();
            
            check_value(gK);
            check_value(gNa);

            hhChannel.set_conductances(gK, gNa, selSs);
        }
    }
    
    @MethodInfo(name="log hGate", interactive = false)
    public void set_log_hGate
    (
        @ParamInfo(name="log h-gate", style="default", options="value=true") boolean bLog
    )
    {
        check_channel_exists();
        
        hhChannel.set_log_hGate(bLog);
    }
    
    @MethodInfo(name="log mGate", interactive = false)
    public void set_log_mGate
    (
        @ParamInfo(name="log m-gate", style="default", options="value=true") boolean bLog
    )
    {
        check_channel_exists();
        
        hhChannel.set_log_mGate(bLog);
    }
    
    @MethodInfo(name="log nGate", interactive = false)
    public void set_log_nGate
    (
        @ParamInfo(name="log n-gate", style="default", options="value=true") boolean bLog
    )
    {
        check_channel_exists();
        
        hhChannel.set_log_nGate(bLog);
    }
    
    @MethodInfo(noGUI=true)
    private void check_channel_exists()
    {
        if (hhChannel == null)
        {
            eu.mihosoft.vrl.system.VMessage.exception("Usage before initialization: ",
                "No method can be called on Hodgkin-Huxley channel object "
                + "before it has been initialized using the 'create()' method.");
        }
    }
    
    
    @MethodInfo(noGUI=true)
    private void check_value(double val)
    {
        if (val < 0.0)
        {
            eu.mihosoft.vrl.system.VMessage.exception("Invalid value: ",
                "The value "+val+" is not admissable in the Hodgkin-Huxley "
                + "channel object. Values must not be negative.");
        }
    }
}

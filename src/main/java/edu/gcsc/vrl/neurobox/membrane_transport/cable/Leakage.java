package edu.gcsc.vrl.neurobox.membrane_transport.cable;

import edu.gcsc.vrl.ug.api.ChannelLeak;
import edu.gcsc.vrl.ug.api.I_ChannelLeak;
import edu.gcsc.vrl.userdata.UserDataTuple;
import edu.gcsc.vrl.userdata.UserDependentSubsetModel;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.Serializable;

/**
 * Leakage mechanism
 * This is a VRL visualization for any leakage term in a 1D cable equation.
 * 
 * @author mbreit
 * @date 30-06-2015
 */

@ComponentInfo(name="Leakage", category="Neuro/cable", description="leakage mechanism in a 1D cable equation")
public class Leakage implements Serializable
{
    private static final long serialVersionUID = 1L;

    private transient I_ChannelLeak leak = null;
    
    /**
     *
     * @param leakData
     * @return leakage mechanism object
     */
    @MethodInfo(name="create leak", valueName="leak", initializer=true, hide=false, interactive=false)
    public I_ChannelLeak create
    (
        @ParamInfo(name="", style="default", options="ugx_globalTag=\"gridFile\"; fct_tag=\"fctDef\"; type=\"S1:membrane potential\"")
        UserDataTuple leakData
    )
    {
        String[] selFct = ((UserDependentSubsetModel.FSDataType) leakData.getData(0)).getSelFct();
        if (selFct.length != 1) throw new RuntimeException("Leakage mechanism needs exactly one function, but has "+selFct.length+".");
        
        String[] selSs = ((UserDependentSubsetModel.FSDataType) leakData.getData(0)).getSelSs();
        if (selSs.length == 0) throw new RuntimeException("No subset definition in leakage mechanism definition!");
        
        // construct leakage object
        leak = new ChannelLeak(selFct, selSs);
        
        return leak;
    }
    
    @MethodInfo(name="set reversal potential", interactive = false)
    public void set_reversal_potential
    (
        @ParamInfo(name="leak rev pot [mV]", style="default", options="value=-65.0") double revPotLeak
    )
    {
        check_leak_exists();
        
        leak.set_rev_pot(revPotLeak);
    }
    
    
    @MethodInfo(name="set conductance", interactive = false)
    public void set_conductance
    (
        @ParamInfo(name="conductance [10^6 S/m^2]", style="default", options="value=3.0E-6") double cond
    )
    {
        check_leak_exists();
        check_value(cond);
        
        leak.set_cond(cond);
    }
    
    
     @MethodInfo(noGUI=true)
    private void check_leak_exists()
    {
        if (leak == null)
        {
            eu.mihosoft.vrl.system.VMessage.exception("Usage before initialization: ",
                "No method can be called on leakage object before it has been initialized"
                + "using the 'create()' method.");
        }
    }
    
    
    @MethodInfo(noGUI=true)
    private void check_value(double val)
    {
        if (val < 0.0)
        {
            eu.mihosoft.vrl.system.VMessage.exception("Invalid value: ",
                "The value "+val+" is not admissable in the leakage object. Values must not be negative.");
        }
    }
}

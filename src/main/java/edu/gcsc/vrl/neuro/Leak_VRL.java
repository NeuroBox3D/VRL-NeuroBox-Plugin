package edu.gcsc.vrl.neuro;

import edu.gcsc.vrl.ug.api.I_CplUserNumber;
import edu.gcsc.vrl.ug.api.I_MembraneTransportFV1;
import edu.gcsc.vrl.ug.api.I_Leak;
import edu.gcsc.vrl.ug.api.MembraneTransportFV1;
import edu.gcsc.vrl.ug.api.Leak;
import edu.gcsc.vrl.userdata.UserDataTuple;
import edu.gcsc.vrl.userdata.UserDependentSubsetModel;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.Serializable;

/**
 *
 * @author mbreit
 * @date 24-10-2014
 */

@ComponentInfo(name="Leak", category="Neuro")
public class Leak_VRL implements Serializable
{
    private static final long serialVersionUID = 1L;

    private transient I_Leak leak = null;
    private transient I_CplUserNumber leakDensityFct = null;
    private transient String[] leakSelSs = null;
    
    /**
     *
     * @param leakData
     */
    @MethodInfo(name="create Leak",hide=false)
    public void createLeak
    (
        @ParamInfo(name="", style="default", options="ugx_globalTag=\"gridFile\"; fct_tag=\"fctDef\"; type=\"S2|n:source, target, density\"")
        UserDataTuple leakData
    )
    {
        String[] leakSelFcts = ((UserDependentSubsetModel.FSDataType) leakData.getData(0)).getSelFct();
        if (leakSelFcts.length != 2) throw new RuntimeException("Leak pump mechanism needs exactly two functions, but has "+leakSelFcts.length+".");
        
        leakSelSs = ((UserDependentSubsetModel.FSDataType) leakData.getData(0)).getSelSs();
        if (leakSelSs.length == 0) throw new RuntimeException("No subset definition in Leak pump definition!");
        
        leakDensityFct = (I_CplUserNumber) leakData.getNumberData(1);
        
        // construct Leak object
        leak = new Leak(leakSelFcts);
    }
    
    @MethodInfo(name="create elemDisc", valueName="Leak ElemDisc", hide=false)
    public I_MembraneTransportFV1 createElemDisc()
    {
        // check that createLeak has been called (<=> Leak != null)
        check_leak();
        
        // construct MembraneTransporter object
        I_MembraneTransportFV1 leakDisc = new MembraneTransportFV1(leakSelSs, leak);
        leakDisc.set_density_function(leakDensityFct);
        
        return leakDisc;
    }
    
    @MethodInfo(name="set constant value", interactive = false)
    public void set_const_value
    (
        @ParamInfo(name="unknown", style="indexSelection", options="value=[\"source\", \"target\"]") Integer ind,
        @ParamInfo(name="value", style="default") double val
    )
    {
        check_leak();
        check_value(val);
        
        leak.set_constant(ind, val);
    }
    
    public void scale_input
    (
        @ParamInfo(name="input", style="indexSelection", options="value=[\"source\", \"target\"]") Integer ind,
        @ParamInfo(name="value", style="default") double scale
    )
    {
        check_leak();
        check_value(scale);
        
        leak.set_scale_input(ind, scale);
    }
    
    public void scale_flux
    (
        @ParamInfo(name="unknown", style="indexSelection", options="value=[\"leakage\"]") Integer ind,
        @ParamInfo(name="value", style="default") double scale
    )
    {
        check_leak();
        check_value(scale);
        
        leak.set_scale_flux(ind, scale);
    }
    
    @MethodInfo(noGUI=true)
    private void check_value(double val)
    {
        if (val < 0.0)
        {
            eu.mihosoft.vrl.system.VMessage.exception("Invalid value: ",
                "The value "+val+" is not admissable in Leak object. Values must not be negative.");
        }
    }
    
    
    @MethodInfo(noGUI=true)
    private void check_leak()
    {
        if (leak == null)
        {
            eu.mihosoft.vrl.system.VMessage.exception("Usage before initialization: ",
                "No method can be called on Leak object before it has been initialized"
                + "using the 'createLeak()' method.");
        }
    }
}

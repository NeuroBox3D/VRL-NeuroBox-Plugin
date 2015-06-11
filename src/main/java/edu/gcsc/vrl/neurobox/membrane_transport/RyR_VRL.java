package edu.gcsc.vrl.neurobox.membrane_transport;

import edu.gcsc.vrl.ug.api.I_CplUserNumber;
import edu.gcsc.vrl.ug.api.I_MembraneTransportFV1;
import edu.gcsc.vrl.ug.api.I_RyR;
import edu.gcsc.vrl.ug.api.MembraneTransportFV1;
import edu.gcsc.vrl.ug.api.RyR;
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

@ComponentInfo(name="RyR", category="Neuro")
public class RyR_VRL implements Serializable
{
    private static final long serialVersionUID = 1L;

    private transient I_RyR ryr = null;
    private transient I_CplUserNumber ryrDensityFct = null;
    private transient String[] ryrSelSs = null;
    
    /**
     *
     * @param ryrData
     */
    @MethodInfo(name="create RyR",hide=false)
    public void createRyR
    (
        @ParamInfo(name="", style="default", options="ugx_globalTag=\"gridFile\"; fct_tag=\"fctDef\"; type=\"S2|n:cytosolic calcium, endoplasmic calcium, density\"")
        UserDataTuple ryrData
    )
    {
        String[] ryrSelFcts = ((UserDependentSubsetModel.FSDataType) ryrData.getData(0)).getSelFct();
        if (ryrSelFcts.length != 2) throw new RuntimeException("RyR pump mechanism needs exactly two functions, but has "+ryrSelFcts.length+".");
        
        ryrSelSs = ((UserDependentSubsetModel.FSDataType) ryrData.getData(0)).getSelSs();
        if (ryrSelSs.length == 0) throw new RuntimeException("No subset definition in RyR pump definition!");
        
        ryrDensityFct = (I_CplUserNumber) ryrData.getNumberData(1);
        
        // construct RyR object
        ryr = new RyR(ryrSelFcts);
    }
    
    @MethodInfo(name="create elemDisc", valueName="RyR ElemDisc", hide=false)
    public I_MembraneTransportFV1 createElemDisc()
    {
        // check that createRyR has been called (<=> RyR != null)
        check_ryr();
        
        // construct MembraneTransporter object
        I_MembraneTransportFV1 ryrDisc = new MembraneTransportFV1(ryrSelSs, ryr);
        ryrDisc.set_density_function(ryrDensityFct);
        
        return ryrDisc;
    }
    
    @MethodInfo(name="set constant value", interactive = false)
    public void set_const_value
    (
        @ParamInfo(name="unknown", style="indexSelection", options="value=[\"ca_cyt\", \"ca_er\"]") Integer ind,
        @ParamInfo(name="value", style="default") double val
    )
    {
        check_ryr();
        check_value(val);
        
        ryr.set_constant(ind, val);
    }
    
    public void scale_input
    (
        @ParamInfo(name="input", style="indexSelection", options="value=[\"ca_cyt\", \"ca_er\"]") Integer ind,
        @ParamInfo(name="value", style="default") double scale
    )
    {
        check_ryr();
        check_value(scale);
        
        ryr.set_scale_input(ind, scale);
    }
    
    public void scale_flux
    (
        @ParamInfo(name="unknown", style="indexSelection", options="value=[\"ca\"]") Integer ind,
        @ParamInfo(name="value", style="default") double scale
    )
    {
        check_ryr();
        check_value(scale);
        
        ryr.set_scale_flux(ind, scale);
    }
    
    @MethodInfo(noGUI=true)
    private void check_value(double val)
    {
        if (val < 0.0)
        {
            eu.mihosoft.vrl.system.VMessage.exception("Invalid value: ",
                "The value "+val+" is not admissable in RyR object. Values must not be negative.");
        }
    }
    
    
    @MethodInfo(noGUI=true)
    private void check_ryr()
    {
        if (ryr == null)
        {
            eu.mihosoft.vrl.system.VMessage.exception("Usage before initialization: ",
                "No method can be called on RyR object before it has been initialized"
                + "using the 'createRyR()' method.");
        }
    }
}

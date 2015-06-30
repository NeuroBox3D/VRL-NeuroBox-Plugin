package edu.gcsc.vrl.neurobox.membrane_transport;

import edu.gcsc.vrl.ug.api.I_CplUserNumber;
import edu.gcsc.vrl.ug.api.I_MembraneTransportFV1;
import edu.gcsc.vrl.ug.api.I_MCU;
import edu.gcsc.vrl.ug.api.MembraneTransportFV1;
import edu.gcsc.vrl.userdata.UserDataTuple;
import edu.gcsc.vrl.userdata.UserDependentSubsetModel;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.Serializable;

/**
 *
 * @author mstepnie
 * @date 30-06-2015
 */

@ComponentInfo(name="MCU", category="Neuro")
public class MCU_VRL implements Serializable
{
    private static final long serialVersionUID = 1L;

    private transient I_MCU mcu = null;
    private transient I_CplUserNumber mcuDensityFct = null;
    private transient String[] mcuSelSs = null;
    
    /**
     *
     * @param mcuData
     */
    @MethodInfo(name="create MCU",hide=false)
    public void createMCU
    (
        @ParamInfo(name="", style="default", options="ugx_globalTag=\"gridFile\"; fct_tag=\"fctDef\"; type=\"S2|n:cytosolic calcium, extracellular calcium, density\"")
        UserDataTuple mcuData
    )
    {
        String[] mcuSelFcts = ((UserDependentSubsetModel.FSDataType) mcuData.getData(0)).getSelFct();
        if (mcuSelFcts.length != 2) throw new RuntimeException("MCU channel mechanism needs exactly two functions, but has "+mcuSelFcts.length+".");
        
        mcuSelSs = ((UserDependentSubsetModel.FSDataType) mcuData.getData(0)).getSelSs();
        if (mcuSelSs.length == 0) throw new RuntimeException("No subset definition in MCU pump definition!");
        
        mcuDensityFct = (I_CplUserNumber) mcuData.getNumberData(1);
        
        // construct MCU object
        mcu = new edu.gcsc.vrl.ug.api.MCU(mcuSelFcts);
    }
    
    @MethodInfo(name="create elemDisc", valueName="MCU ElemDisc", hide=false)
    public I_MembraneTransportFV1 createElemDisc()
    {
        // check that createMCU has been called (<=> MCU != null)
        check_mcu();
        
        // construct MembraneTransporter object
        I_MembraneTransportFV1 mcuDisc = new MembraneTransportFV1(mcuSelSs, mcu);
        mcuDisc.set_density_function(mcuDensityFct);
        
        return mcuDisc;
    }
    
    @MethodInfo(name="set constant value", interactive = false)
    public void set_const_value
    (
        @ParamInfo(name="unknown", style="indexSelection", options="value=[\"ca_cyt\", \"ca_ext\"]") Integer ind,
        @ParamInfo(name="value", style="default") double val
    )
    {
        check_mcu();
        check_value(val);
        
        mcu.set_constant(ind, val);
    }
    
    public void scale_input
    (
        @ParamInfo(name="input", style="indexSelection", options="value=[\"ca_cyt\", \"ca_ext\"]") Integer ind,
        @ParamInfo(name="value", style="default") double scale
    )
    {
        check_mcu();
        check_value(scale);
        
        mcu.set_scale_input(ind, scale);
    }
    
    public void scale_flux
    (
        @ParamInfo(name="unknown", style="indexSelection", options="value=[\"ca\"]") Integer ind,
        @ParamInfo(name="value", style="default") double scale
    )
    {
        check_mcu();
        check_value(scale);
        
        mcu.set_scale_flux(ind, scale);
    }
    
    @MethodInfo(noGUI=true)
    private void check_value(double val)
    {
        if (val < 0.0)
        {
            eu.mihosoft.vrl.system.VMessage.exception("Invalid value: ",
                "The value "+val+" is not admissable in MCU object. Values must not be negative.");
        }
    }
    
    
    @MethodInfo(noGUI=true)
    private void check_mcu()
    {
        if (mcu == null)
        {
            eu.mihosoft.vrl.system.VMessage.exception("Usage before initialization: ",
                "No method can be called on MCU object before it has been initialized"
                + "using the 'createMCU()' method.");
        }
    }
}

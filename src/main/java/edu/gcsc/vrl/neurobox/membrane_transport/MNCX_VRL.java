package edu.gcsc.vrl.neurobox.membrane_transport;

import edu.gcsc.vrl.ug.api.I_CplUserNumber;
import edu.gcsc.vrl.ug.api.I_MembraneTransportFV1;
import edu.gcsc.vrl.ug.api.I_MNCX;
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

@ComponentInfo(name="MNCX", category="Neuro")
public class MNCX_VRL implements Serializable
{
    private static final long serialVersionUID = 1L;

    private transient I_MNCX mncx = null;
    private transient I_CplUserNumber mncxDensityFct = null;
    private transient String[] mncxSelSs = null;
    
    /**
     *
     * @param mncxData
     */
    @MethodInfo(name="create MNCX",hide=false)
    public void createMNCX
    (
        @ParamInfo(name="", style="default", options="ugx_globalTag=\"gridFile\"; fct_tag=\"fctDef\"; type=\"S2|n:cytosolic calcium, extracellular calcium, density\"")
        UserDataTuple mncxData
    )
    {
        String[] mncxSelFcts = ((UserDependentSubsetModel.FSDataType) mncxData.getData(0)).getSelFct();
        if (mncxSelFcts.length != 2) throw new RuntimeException("MNCX channel mechanism needs exactly two functions, but has "+mncxSelFcts.length+".");
        
        mncxSelSs = ((UserDependentSubsetModel.FSDataType) mncxData.getData(0)).getSelSs();
        if (mncxSelSs.length == 0) throw new RuntimeException("No subset definition in MNCX pump definition!");
        
        mncxDensityFct = (I_CplUserNumber) mncxData.getNumberData(1);
        
        // construct MNCX object
        mncx = new edu.gcsc.vrl.ug.api.MNCX(mncxSelFcts);
    }
    
    @MethodInfo(name="create elemDisc", valueName="MNCX ElemDisc", hide=false)
    public I_MembraneTransportFV1 createElemDisc()
    {
        // check that createMNCX has been called (<=> MNCX != null)
        check_mncx();
        
        // construct MembraneTransporter object
        I_MembraneTransportFV1 mncxDisc = new MembraneTransportFV1(mncxSelSs, mncx);
        mncxDisc.set_density_function(mncxDensityFct);
        
        return mncxDisc;
    }
    
    @MethodInfo(name="set constant value", interactive = false)
    public void set_const_value
    (
        @ParamInfo(name="unknown", style="indexSelection", options="value=[\"ca_cyt\", \"ca_ext\"]") Integer ind,
        @ParamInfo(name="value", style="default") double val
    )
    {
        check_mncx();
        check_value(val);
        
        mncx.set_constant(ind, val);
    }
    
    public void scale_input
    (
        @ParamInfo(name="input", style="indexSelection", options="value=[\"ca_cyt\", \"ca_ext\"]") Integer ind,
        @ParamInfo(name="value", style="default") double scale
    )
    {
        check_mncx();
        check_value(scale);
        
        mncx.set_scale_input(ind, scale);
    }
    
    public void scale_flux
    (
        @ParamInfo(name="unknown", style="indexSelection", options="value=[\"ca\"]") Integer ind,
        @ParamInfo(name="value", style="default") double scale
    )
    {
        check_mncx();
        check_value(scale);
        
        mncx.set_scale_flux(ind, scale);
    }
    
    @MethodInfo(noGUI=true)
    private void check_value(double val)
    {
        if (val < 0.0)
        {
            eu.mihosoft.vrl.system.VMessage.exception("Invalid value: ",
                "The value "+val+" is not admissable in MNCX object. Values must not be negative.");
        }
    }
    
    
    @MethodInfo(noGUI=true)
    private void check_mncx()
    {
        if (mncx == null)
        {
            eu.mihosoft.vrl.system.VMessage.exception("Usage before initialization: ",
                "No method can be called on MNCX object before it has been initialized"
                + "using the 'createMNCX()' method.");
        }
    }
}

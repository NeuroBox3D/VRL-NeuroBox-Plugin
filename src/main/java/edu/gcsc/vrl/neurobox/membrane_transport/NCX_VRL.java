package edu.gcsc.vrl.neurobox.membrane_transport;

import edu.gcsc.vrl.ug.api.I_CplUserNumber;
import edu.gcsc.vrl.ug.api.I_MembraneTransportFV1;
import edu.gcsc.vrl.ug.api.I_NCX;
import edu.gcsc.vrl.ug.api.MembraneTransportFV1;
import edu.gcsc.vrl.ug.api.NCX;
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

@ComponentInfo(name="NCX", category="Neuro")
public class NCX_VRL implements Serializable
{
    private static final long serialVersionUID = 1L;

    private transient I_NCX ncx = null;
    private transient I_CplUserNumber ncxDensityFct = null;
    private transient String[] ncxSelSs = null;
    
    /**
     *
     * @param ncxData
     */
    @MethodInfo(name="create NCX",hide=false, initializer=true)
    public void createNCX
    (
        @ParamInfo(name="", style="default", options="ugx_globalTag=\"gridFile\"; fct_tag=\"fctDef\"; type=\"S2|n:cytosolic calcium, extracellular calcium, density\"")
        UserDataTuple ncxData
    )
    {
        String[] ncxSelFcts = ((UserDependentSubsetModel.FSDataType) ncxData.getData(0)).getSelFct();
        if (ncxSelFcts.length != 2) throw new RuntimeException("NCX pump mechanism needs exactly two functions, but has "+ncxSelFcts.length+".");
        
        ncxSelSs = ((UserDependentSubsetModel.FSDataType) ncxData.getData(0)).getSelSs();
        if (ncxSelSs.length == 0) throw new RuntimeException("No subset definition in NCX pump definition!");
        
        ncxDensityFct = (I_CplUserNumber) ncxData.getNumberData(1);
        
        // construct NCX object
        ncx = new NCX(ncxSelFcts);
    }
    
    @MethodInfo(name="create elemDisc", valueName="NCX ElemDisc", hide=false)
    public I_MembraneTransportFV1 createElemDisc()
    {
        // check that createNCX has been called (<=> NCX != null)
        check_ncx();
        
        // construct MembraneTransporter object
        I_MembraneTransportFV1 ncxDisc = new MembraneTransportFV1(ncxSelSs, ncx);
        ncxDisc.set_density_function(ncxDensityFct);
        
        return ncxDisc;
    }
    
    @MethodInfo(name="set constant value", interactive = false)
    public void set_const_value
    (
        @ParamInfo(name="unknown", style="indexSelection", options="value=[\"ca_cyt\", \"ca_ext\"]") Integer ind,
        @ParamInfo(name="value", style="default") double val
    )
    {
        check_ncx();
        check_value(val);
        
        ncx.set_constant(ind, val);
    }
    
    public void scale_input
    (
        @ParamInfo(name="input", style="indexSelection", options="value=[\"ca_cyt\", \"ca_ext\"]") Integer ind,
        @ParamInfo(name="value", style="default") double scale
    )
    {
        check_ncx();
        check_value(scale);
        
        ncx.set_scale_input(ind, scale);
    }
    
    public void scale_flux
    (
        @ParamInfo(name="unknown", style="indexSelection", options="value=[\"ca\"]") Integer ind,
        @ParamInfo(name="value", style="default") double scale
    )
    {
        check_ncx();
        check_value(scale);
        
        ncx.set_scale_flux(ind, scale);
    }
    
    @MethodInfo(noGUI=true)
    private void check_value(double val)
    {
        if (val < 0.0)
        {
            eu.mihosoft.vrl.system.VMessage.exception("Invalid value: ",
                "The value "+val+" is not admissable in NCX object. Values must not be negative.");
        }
    }
    
    
    @MethodInfo(noGUI=true)
    private void check_ncx()
    {
        if (ncx == null)
        {
            eu.mihosoft.vrl.system.VMessage.exception("Usage before initialization: ",
                "No method can be called on NCX object before it has been initialized"
                + "using the 'createNCX()' method.");
        }
    }
}

package edu.gcsc.vrl.neurobox.membrane_transport;

import edu.gcsc.vrl.ug.api.I_CplUserNumber;
import edu.gcsc.vrl.ug.api.I_MembraneTransportFV1;
import edu.gcsc.vrl.ug.api.I_SERCA;
import edu.gcsc.vrl.ug.api.MembraneTransportFV1;
import edu.gcsc.vrl.ug.api.SERCA;
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

@ComponentInfo(name="SERCA", category="Neuro")
public class SERCA_VRL implements Serializable
{
    private static final long serialVersionUID = 1L;

    private transient I_SERCA serca = null;
    private transient I_CplUserNumber sercaDensityFct = null;
    private transient String[] sercaSelSs = null;
    
    /**
     *
     * @param sercaData
     */
    @MethodInfo(name="create SERCA",hide=false)
    public void createSERCA
    (
        @ParamInfo(name="", style="default", options="ugx_globalTag=\"gridFile\"; fct_tag=\"fctDef\"; type=\"S2|n:cytosolic calcium, endoplasmic calcium, density\"")
        UserDataTuple sercaData
    )
    {
        String[] sercaSelFcts = ((UserDependentSubsetModel.FSDataType) sercaData.getData(0)).getSelFct();
        if (sercaSelFcts.length != 2) throw new RuntimeException("SERCA pump mechanism needs exactly two functions, but has "+sercaSelFcts.length+".");
        
        sercaSelSs = ((UserDependentSubsetModel.FSDataType) sercaData.getData(0)).getSelSs();
        if (sercaSelSs.length == 0) throw new RuntimeException("No subset definition in SERCA pump definition!");
        
        sercaDensityFct = (I_CplUserNumber) sercaData.getNumberData(1);
        
        // construct SERCA object
        serca = new SERCA(sercaSelFcts);
    }
    
    @MethodInfo(name="create elemDisc", valueName="SERCA ElemDisc", hide=false)
    public I_MembraneTransportFV1 createElemDisc()
    {
        // check that createSERCA has been called (<=> SERCA != null)
        check_serca();
        
        // construct MembraneTransporter object
        I_MembraneTransportFV1 sercaDisc = new MembraneTransportFV1(sercaSelSs, serca);
        sercaDisc.set_density_function(sercaDensityFct);
        
        return sercaDisc;
    }
    
    @MethodInfo(name="set constant value", interactive = false)
    public void set_const_value
    (
        @ParamInfo(name="unknown", style="indexSelection", options="value=[\"ca_cyt\", \"ca_er\"]") Integer ind,
        @ParamInfo(name="value", style="default") double val
    )
    {
        check_serca();
        check_value(val);
        
        serca.set_constant(ind, val);
    }
    
    public void scale_input
    (
        @ParamInfo(name="input", style="indexSelection", options="value=[\"ca_cyt\", \"ca_er\"]") Integer ind,
        @ParamInfo(name="value", style="default") double scale
    )
    {
        check_serca();
        check_value(scale);
        
        serca.set_scale_input(ind, scale);
    }
    
    public void scale_flux
    (
        @ParamInfo(name="unknown", style="indexSelection", options="value=[\"ca\"]") Integer ind,
        @ParamInfo(name="value", style="default") double scale
    )
    {
        check_serca();
        check_value(scale);
        
        serca.set_scale_flux(ind, scale);
    }
    
    @MethodInfo(noGUI=true)
    private void check_value(double val)
    {
        if (val < 0.0)
        {
            eu.mihosoft.vrl.system.VMessage.exception("Invalid value: ",
                "The value "+val+" is not admissable in SERCA object. Values must not be negative.");
        }
    }
    
    
    @MethodInfo(noGUI=true)
    private void check_serca()
    {
        if (serca == null)
        {
            eu.mihosoft.vrl.system.VMessage.exception("Usage before initialization: ",
                "No method can be called on SERCA object before it has been initialized"
                + "using the 'createSERCA()' method.");
        }
    }
}

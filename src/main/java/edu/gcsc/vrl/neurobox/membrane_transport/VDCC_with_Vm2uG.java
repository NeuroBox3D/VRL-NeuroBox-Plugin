package edu.gcsc.vrl.neurobox.membrane_transport;

import edu.gcsc.vrl.ug.api.I_ApproximationSpace;
import edu.gcsc.vrl.ug.api.I_CplUserNumber;
import edu.gcsc.vrl.ug.api.I_MembraneTransportFV1;
import edu.gcsc.vrl.ug.api.I_VDCC_BG_VM2UG;
import edu.gcsc.vrl.ug.api.MembraneTransportFV1;
import edu.gcsc.vrl.ug.api.VDCC_BG_VM2UG;
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

@ComponentInfo(name="VDCC_Vm2uG", category="Neuro")
public class VDCC_with_Vm2uG implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private transient I_VDCC_BG_VM2UG vdcc = null;
    private transient I_CplUserNumber vdccDensityFct = null;
    private transient String[] vdccSelSs = null;

    /**
     *
     * @param approxSpace
     * @param vdccData
     * @param vdccChannelType
     * @param vdccFile
     * @param vdccFileTimeFormatString
     * @param vdccFileExtension
     * @param vdccFileInterval
     * @param vdccFileOffset
     */
    @MethodInfo(name="create VDCC",hide=false)
    public void createVDCC
    (
        // approx space
        @ParamInfo(name="Approximation Space", style="default")
        I_ApproximationSpace approxSpace,
        
        @ParamInfo(name="", style="default", options="ugx_globalTag=\"gridFile\"; fct_tag=\"fctDef\"; type=\"S1|n:cytosolic calcium, density\"")
        UserDataTuple vdccData,
        
        @ParamInfo(name="channel type", style="selection", options="value=[\"L\",\"N\",\"T\"]")
        String vdccChannelType,
        
        @ParamInfo(name="voltage files", style="load-dialog", options="")
        String vdccFile,
        
        @ParamInfo(name="timestep format", style="default", options="value=\"%.3f\"")
        String vdccFileTimeFormatString,
        
        @ParamInfo(name="file extension", style="default", options="value=\".dat\"")
        String vdccFileExtension,
        
        @ParamInfo(name="file time interval", style="default", options="value=0.001")
        double vdccFileInterval,
        
        @ParamInfo(name="first file at time", style="default", options="value=0.0")
        double vdccFileOffset
    )
    {
        String[] vdccSelFcts = ((UserDependentSubsetModel.FSDataType) vdccData.getData(0)).getSelFct();
        vdccSelSs = ((UserDependentSubsetModel.FSDataType) vdccData.getData(0)).getSelSs();
        vdccDensityFct = (I_CplUserNumber) vdccData.getNumberData(1);
        
        // create VDCC object
        vdcc = new VDCC_BG_VM2UG(vdccSelFcts , vdccSelSs, approxSpace, vdccFile,
                vdccFileTimeFormatString, vdccFileExtension, false);
        
        if ("L".equals(vdccChannelType)) vdcc.set_channel_type_L();
        else if ("N".equals(vdccChannelType)) vdcc.set_channel_type_N();
        else if ("T".equals(vdccChannelType)) vdcc.set_channel_type_T();
        
        vdcc.set_file_times(vdccFileInterval, vdccFileOffset);
        vdcc.init(0.0D);
    }
    
    @MethodInfo(name="create elemDisc", valueName="VDCC ElemDisc", hide=false)
    public I_MembraneTransportFV1 createElemDisc()
    {
        // check that createVDCC has been called (<=> VDCC != null)
        check_vdcc();
        
        // construct MembraneTransporter object
        I_MembraneTransportFV1 vdccDisc = new MembraneTransportFV1(vdccSelSs, vdcc);
        vdccDisc.set_density_function(vdccDensityFct);
        
        return vdccDisc;
    }
    
    @MethodInfo(name="set constant value", interactive = false)
    public void set_const_value
    (
        @ParamInfo(name="unknown", style="indexSelection", options="value=[\"ca_cyt\", \"ca_ext\"]") Integer ind,
        @ParamInfo(name="value", style="default") double val
    )
    {
        check_vdcc();
        check_value(val);
        
        vdcc.set_constant(ind, val);
    }
    
    public void scale_input
    (
        @ParamInfo(name="input", style="indexSelection", options="value=[\"ca_cyt\", \"ca_ext\"]") Integer ind,
        @ParamInfo(name="value", style="default") double scale
    )
    {
        check_vdcc();
        check_value(scale);
        
        vdcc.set_scale_input(ind, scale);
    }
    
    public void scale_flux
    (
        @ParamInfo(name="unknown", style="indexSelection", options="value=[\"ca\"]") Integer ind,
        @ParamInfo(name="value", style="default") double scale
    )
    {
        check_vdcc();
        check_value(scale);
        
        vdcc.set_scale_flux(ind, scale);
    }
    
    @MethodInfo(noGUI=true)
    private void check_value(double val)
    {
        if (val < 0.0)
        {
            eu.mihosoft.vrl.system.VMessage.exception("Invalid value: ",
                "The value "+val+" is not admissable in VDCC object. Values must not be negative.");
        }
    }
    
    
    @MethodInfo(noGUI=true)
    private void check_vdcc()
    {
        if (vdcc == null)
        {
            eu.mihosoft.vrl.system.VMessage.exception("Usage before initialization: ",
                "No method can be called on VDCC object before it has been initialized"
                + "using the 'createVDCC()' method.");
        }
    }
}

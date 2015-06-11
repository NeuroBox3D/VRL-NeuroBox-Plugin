package edu.gcsc.vrl.neurobox.membrane_transport;

import edu.gcsc.vrl.ug.api.I_CplUserNumber;
import edu.gcsc.vrl.ug.api.I_MembraneTransportFV1;
import edu.gcsc.vrl.ug.api.I_IP3R;
import edu.gcsc.vrl.ug.api.MembraneTransportFV1;
import edu.gcsc.vrl.ug.api.IP3R;
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

@ComponentInfo(name="IP3R", category="Neuro")
public class IP3R_VRL implements Serializable
{
    private static final long serialVersionUID = 1L;

    private transient I_IP3R ip3r = null;
    private transient I_CplUserNumber ip3rDensityFct = null;
    private transient String[] ip3rSelSs = null;
    
    /**
     *
     * @param ip3rData
     */
    @MethodInfo(name="create IP3R",hide=false)
    public void createIP3R
    (
        @ParamInfo(name="", style="default", options="ugx_globalTag=\"gridFile\"; fct_tag=\"fctDef\"; type=\"S3|n:cytosolic calcium, endoplasmic calcium, ip3, density\"")
        UserDataTuple ip3rData
    )
    {
        String[] ip3rSelFcts = ((UserDependentSubsetModel.FSDataType) ip3rData.getData(0)).getSelFct();
        if (ip3rSelFcts.length != 3) throw new RuntimeException("IP3R pump mechanism needs exactly three functions, but has "+ip3rSelFcts.length+".");
        
        ip3rSelSs = ((UserDependentSubsetModel.FSDataType) ip3rData.getData(0)).getSelSs();
        if (ip3rSelSs.length == 0) throw new RuntimeException("No subset definition in IP3R pump definition!");
        
        ip3rDensityFct = (I_CplUserNumber) ip3rData.getNumberData(1);
        
        // construct IP3R object
        ip3r = new IP3R(ip3rSelFcts);
    }
    
    @MethodInfo(name="create elemDisc", valueName="IP3R ElemDisc", hide=false)
    public I_MembraneTransportFV1 createElemDisc()
    {
        // check that createIP3R has been called (<=> IP3R != null)
        check_ip3r();
        
        // construct MembraneTransporter object
        I_MembraneTransportFV1 ip3rDisc = new MembraneTransportFV1(ip3rSelSs, ip3r);
        ip3rDisc.set_density_function(ip3rDensityFct);
        
        return ip3rDisc;
    }
    
    @MethodInfo(name="set constant value", interactive = false)
    public void set_const_value
    (
        @ParamInfo(name="unknown", style="indexSelection", options="value=[\"ca_cyt\", \"ca_er\", \"ip3\"]") Integer ind,
        @ParamInfo(name="value", style="default") double val
    )
    {
        check_ip3r();
        check_value(val);
        
        ip3r.set_constant(ind, val);
    }
    
    public void scale_input
    (
        @ParamInfo(name="input", style="indexSelection", options="value=[\"ca_cyt\", \"ca_er\", \"ip3\"]") Integer ind,
        @ParamInfo(name="value", style="default") double scale
    )
    {
        check_ip3r();
        check_value(scale);
        
        ip3r.set_scale_input(ind, scale);
    }
    
    public void scale_flux
    (
        @ParamInfo(name="unknown", style="indexSelection", options="value=[\"ca\"]") Integer ind,
        @ParamInfo(name="value", style="default") double scale
    )
    {
        check_ip3r();
        check_value(scale);
        
        ip3r.set_scale_flux(ind, scale);
    }
    
    @MethodInfo(noGUI=true)
    private void check_value(double val)
    {
        if (val < 0.0)
        {
            eu.mihosoft.vrl.system.VMessage.exception("Invalid value: ",
                "The value "+val+" is not admissable in IP3R object. Values must not be negative.");
        }
    }
    
    
    @MethodInfo(noGUI=true)
    private void check_ip3r()
    {
        if (ip3r == null)
        {
            eu.mihosoft.vrl.system.VMessage.exception("Usage before initialization: ",
                "No method can be called on IP3R object before it has been initialized"
                + "using the 'createIP3R()' method.");
        }
    }
}

package edu.gcsc.vrl.neuro;

import edu.gcsc.vrl.ug.api.I_CplUserNumber;
import edu.gcsc.vrl.ug.api.I_MembraneTransportFV1;
import edu.gcsc.vrl.ug.api.I_PMCA;
import edu.gcsc.vrl.ug.api.MembraneTransportFV1;
import edu.gcsc.vrl.ug.api.PMCA;
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

@ComponentInfo(name="PMCA", category="Neuro")
public class PMCA_VRL implements Serializable
{
    private static final long serialVersionUID = 1L;

    private transient I_PMCA pmca = null;
    private transient I_CplUserNumber pmcaDensityFct = null;
    private transient String[] pmcaSelSs = null;
    
    /**
     *
     * @param pmcaData
     */
    @MethodInfo(name="create PMCA",hide=false)
    public void createPMCA
    (
        @ParamInfo(name="", style="default", options="ugx_globalTag=\"gridFile\"; fct_tag=\"fctDef\"; type=\"S2|n:cytosolic calcium, extracellular calcium, density\"")
        UserDataTuple pmcaData
    )
    {
        String[] pmcaSelFcts = ((UserDependentSubsetModel.FSDataType) pmcaData.getData(0)).getSelFct();
        if (pmcaSelFcts.length != 2) throw new RuntimeException("PMCA pump mechanism needs exactly two functions, but has "+pmcaSelFcts.length+".");
        
        pmcaSelSs = ((UserDependentSubsetModel.FSDataType) pmcaData.getData(0)).getSelSs();
        if (pmcaSelSs.length == 0) throw new RuntimeException("No subset definition in PMCA pump definition!");
        
        pmcaDensityFct = (I_CplUserNumber) pmcaData.getNumberData(1);
        
        // construct PMCA object
        pmca = new PMCA(pmcaSelFcts);
    }
    
    @MethodInfo(name="create elemDisc", valueName="PMCA ElemDisc", hide=false)
    public I_MembraneTransportFV1 createElemDisc()
    {
        // check that createPMCA has been called (<=> PMCA != null)
        check_pmca();
        
        // construct MembraneTransporter object
        I_MembraneTransportFV1 pmcaDisc = new MembraneTransportFV1(pmcaSelSs, pmca);
        pmcaDisc.set_density_function(pmcaDensityFct);
        
        return pmcaDisc;
    }
    
    @MethodInfo(name="set constant value", interactive = false)
    public void set_const_value
    (
        @ParamInfo(name="unknown", style="indexSelection", options="value=[\"ca_cyt\", \"ca_ext\"]") Integer ind,
        @ParamInfo(name="value", style="default") double val
    )
    {
        check_pmca();
        check_value(val);
        
        pmca.set_constant(ind, val);
    }
    
    public void scale_input
    (
        @ParamInfo(name="input", style="indexSelection", options="value=[\"ca_cyt\", \"ca_ext\"]") Integer ind,
        @ParamInfo(name="value", style="default") double scale
    )
    {
        check_pmca();
        check_value(scale);
        
        pmca.set_scale_input(ind, scale);
    }
    
    public void scale_flux
    (
        @ParamInfo(name="unknown", style="indexSelection", options="value=[\"ca\"]") Integer ind,
        @ParamInfo(name="value", style="default") double scale
    )
    {
        check_pmca();
        check_value(scale);
        
        pmca.set_scale_flux(ind, scale);
    }
    
    @MethodInfo(noGUI=true)
    private void check_value(double val)
    {
        if (val < 0.0)
        {
            eu.mihosoft.vrl.system.VMessage.exception("Invalid value: ",
                "The value "+val+" is not admissable in PMCA object. Values must not be negative.");
        }
    }
    
    
    @MethodInfo(noGUI=true)
    private void check_pmca()
    {
        if (pmca == null)
        {
            eu.mihosoft.vrl.system.VMessage.exception("Usage before initialization: ",
                "No method can be called on PMCA object before it has been initialized"
                + "using the 'createPMCA()' method.");
        }
    }
}

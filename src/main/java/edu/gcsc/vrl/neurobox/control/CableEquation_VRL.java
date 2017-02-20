/**
 * A model setup GUI element for cableEquation problems in the
 * VRL-NeuroBox plugin.
 * 
 * @date 2015-05-12
 * @author mbreit
**/

package edu.gcsc.vrl.neurobox.control;

import edu.gcsc.vrl.ug.api.*;
import edu.gcsc.vrl.userdata.FunctionDefinition;
import edu.gcsc.vrl.userdata.UserDataTuple;
import edu.gcsc.vrl.userdata.UserDependentSubsetModel;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ObjectInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamGroupInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@ComponentInfo(name="Cable Equation", category="Neuro/cable")
@ObjectInfo(instances = 1)
public class CableEquation_VRL implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private transient I_CableEquation cableDisc = null;
    
    /**
     *
     * @param approxSpace
     * @param functionDefinition
     * @param vmDiscSubsetData
     * @param memTransporters
     * @param synHandler
     * @param startValue
     * @return
     */
    @MethodInfo(valueStyle="multi-out", hide = false, interactive=false , initializer=true)
    @OutputInfo
    (
        style="multi-out",
        elemNames = {"Cable Disc", "Domain Disc", "Initial Solution"},
        elemTypes = {I_CableEquation.class, I_DomainDiscretization.class, UserDataTuple[].class}
    )
    public Object[] createCableEquation
    (
        /// Approx space
        @ParamInfo(name="Approximation Space", style="default")
        I_ApproximationSpace approxSpace,
        
        /// function definition
        @ParamInfo(name="Function Definitions", style="default")
        FunctionDefinition[] functionDefinition,
        
        /// Problem definition ///
        @ParamGroupInfo(group="Problem definition|true")
        @ParamInfo(name="", style="default", options="ugx_globalTag=\"gridFile\"; fct_tag=\"fctDef\"; type=\"S4:V, [K], [Na], [Ca]\"")
        UserDataTuple vmDiscSubsetData,
        
        @ParamGroupInfo(group="Problem definition|true; Membrane transport|false")
        @ParamInfo(name=" ", style="array", options="minArraySize=0; elemName=\"transport mechanism\"")
        I_ICableMembraneTransport[] memTransporters,
               
        @ParamGroupInfo(group="Problem definition|true; Membrane transport|false")
        @ParamInfo(name="synapse handler", style="default", options="", nullIsValid=true)
        I_SynapseHandler synHandler,
        
        @ParamGroupInfo(group="Problem definition|true; Initial values|false")
        @ParamInfo(name=" ", style="array", options="ugx_globalTag=\"gridFile\";"
            + "fct_tag=\"fctDef\"; minArraySize=1; type=\"S1|n:function & subset, start value\"")
        UserDataTuple[] startValue
    )
    {
        //////////////////////////
        // discretization setup //
        //////////////////////////
        
        I_DomainDiscretization domainDisc = new DomainDiscretization(approxSpace);
        
        // get selected function and selected subsets
        String[] selFcts = ((UserDependentSubsetModel.FSDataType) vmDiscSubsetData.getData(0)).getSelFct();
        if (selFcts.length != 4) throw new RuntimeException("Cable equation discretization needs exactly "
                + "four functions, but has "+selFcts.length+".");
        
        // check whether ions are given or not
        boolean with_ions = true;
        if ("".equals(selFcts[1]) || "".equals(selFcts[2]) || "".equals(selFcts[3]))
            with_ions = false;

        String[] selSs = ((UserDependentSubsetModel.FSDataType) vmDiscSubsetData.getData(0)).getSelSs();
        if (selSs.length == 0) throw new RuntimeException("No subset definition in IP3R pump definition!");
        String ssString = "";
        for (String s: selSs) ssString = ssString + ", " + s;
        ssString = ssString.substring(2);
        
        // construct VMDisc object
        cableDisc = new CableEquation(ssString, with_ions);

        // add channels
        for (I_ICableMembraneTransport ch : memTransporters)
            cableDisc.add(ch);
                
        // synapse handler
        if (synHandler != null)
        {
            synHandler.set_ce_object(cableDisc);
            cableDisc.set_synapse_handler(synHandler);
        }
        
        
        // add to domain disc
        domainDisc.add(cableDisc);
        
        
        /// start value
        
        // check that every function has been initialized on each of its subsets
        for (FunctionDefinition fd: functionDefinition)
        {
            // construct list of all subset lists in initial solution definition
            // for the given function
            List<List<String>> dssll = new ArrayList<List<String>>();
            for (UserDataTuple udt: startValue)
            {
                if (((UserDependentSubsetModel.FSDataType) udt.getData(0)).getSelFct().length != 1)
                {
                    throw new RuntimeException("Start value definition needs exactly one function at a time, but has "
                        + ((UserDependentSubsetModel.FSDataType) udt.getData(0)).getSelFct().length+".");
                }
                String fct = ((UserDependentSubsetModel.FSDataType) udt.getData(0)).getSelFct()[0];
                
                if (fct.equals(fd.getFctData().fctName))
                    dssll.add(fd.getFctData().subsetList);
            }
            
            // check that each item on the definition subset list
            // is somewhere in the initial solution subset list list
            List<String> dssl = fd.getFctData().subsetList;
            for (String dss: dssl)
            {
                boolean found = false;
                outerSearchLoop:
                for (List<String> issl: dssll)
                {
                    for (String iss: issl)
                        if (iss.equals(dss))
                        {
                            found = true;
                            break outerSearchLoop;
                        }
                }
                if (!found)
                {
                    throw new RuntimeException("Initialization value for function '"
                            +fd.getFctData().fctName+"' on subset '"+dss+"' is missing!");
                }
            }
        }
        
        return new Object[]{cableDisc, domainDisc, startValue};
    }
    
    @MethodInfo(name="set material constants", interactive = false)
    public void set_material_constants
    (
        @ParamInfo(name="specific resistance [Ohm*m]", style="default", options="value=1.5") double specRes,
        @ParamInfo(name="specific capacity [F/m^2]", style="default", options="value=1.0e-2") double specCap
    )
    {
        check_elemDisc_exists();
        
        check_value(specRes);
        check_value(specCap);
        
        cableDisc.set_spec_res(specRes);
        cableDisc.set_spec_cap(specCap);
    }
    
    @MethodInfo(name="set diffusion constants", interactive = false)
    public void set_diffusion_constants
    (
        @ParamInfo(name="K [m^2/s]", style="default", options="value=1.0e-9") double diffK,
        @ParamInfo(name="Na [m^2/s]", style="default", options="value=1.0e-9") double diffNa,
        @ParamInfo(name="Ca [m^2/s]", style="default", options="value=2.2e-10") double diffCa
    )
    {
        check_elemDisc_exists();
        
        check_value(diffK);
        check_value(diffNa);
        check_value(diffCa);
        
        Double[] diffs = new Double[]{diffK, diffNa, diffCa};
        
        cableDisc.set_diff_coeffs(diffs);
    }
    
    @MethodInfo(name="set reversal potentials", interactive = false)
    public void set_reversal_potentials
    (
        @ParamInfo(name="K [V]", style="default", options="value=-0.09") double revPotK,
        @ParamInfo(name="Na [V]", style="default", options="value=0.06") double revPotNa,
        @ParamInfo(name="Ca [V]", style="default", options="value=0.14") double revPotCa
    )
    {
        check_elemDisc_exists();
                
        cableDisc.set_rev_pot_k(revPotK);
        cableDisc.set_rev_pot_na(revPotNa);
        cableDisc.set_rev_pot_ca(revPotCa);
    }
    
    @MethodInfo(name="set outer concentrations", interactive = false)
    public void set_outer_concentrations
    (
        @ParamInfo(name="K [mM]", style="default", options="value=4.0") double concK,
        @ParamInfo(name="Na [mM]", style="default", options="value=150.0") double concNa,
        @ParamInfo(name="Ca [mM]", style="default", options="value=1.5") double concCa
    )
    {
        check_elemDisc_exists();
        
        check_value(concK);
        check_value(concNa);
        check_value(concCa);
        
        cableDisc.set_k_out(concK);
        cableDisc.set_na_out(concNa);
        cableDisc.set_ca_out(concCa);
    }
    
    @MethodInfo(name="set diameter", interactive = false)
    public void set_constant_diameter
    (
        @ParamInfo(name="diameter [m]", style="default", options="value=1e-6") double diam
    )
    {
        check_elemDisc_exists();
        
        check_value(diam);
        
        cableDisc.set_diameter(diam);
    }
    
    @MethodInfo(name="set temperature", interactive = false)
    public void set_temperature_celsius
    (
        @ParamInfo(name="temperature [°C]", style="default", options="value=37.0") double temp
    )
    {
        check_elemDisc_exists();
        
        cableDisc.set_temperature_celsius(temp);
    }
    
    @MethodInfo(name="set temperature", interactive = false)
    public void set_temperature
    (
        @ParamInfo(name="temperature [K]", style="default", options="value=310.0") double temp
    )
    {
        check_elemDisc_exists();
        check_value(temp);
        
        ///vmDisc.set_temperature(temp);
    }
    
    @MethodInfo(noGUI=true)
    private void check_elemDisc_exists()
    {
        if (cableDisc == null)
        {
            eu.mihosoft.vrl.system.VMessage.exception("Usage before initialization: ",
                "No method can be called on cableEquation object before it has been initialized"
                + "using the 'createCableEquation()' method.");
        }
    }
    
    
    @MethodInfo(noGUI=true)
    private void check_value(double val)
    {
        if (val < 0.0)
        {
            eu.mihosoft.vrl.system.VMessage.exception("Invalid value: ",
                "The value "+val+" is not admissable in the cableEquation object. Values must not be negative.");
        }
    }
    
}

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


@ComponentInfo(name="Cable Equation", category="Neuro")
@ObjectInfo(instances = 1)
public class CableEquation implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private transient I_VMDisc vmDisc = null;
    
    /**
     *
     * @param approxSpace
     * @param functionDefinition
     * @param vmDiscSubsetData
     * @param memTransporters
     * @param presynSubsetData
     * @param biexpSubsetData
     * @param synHandler
     * @param startValue
     * @return
     */
    @MethodInfo(valueStyle="multi-out", hide = false, interactive=false , initializer=true)
    @OutputInfo
    (
        style="multi-out",
        elemNames = {"Domain Disc", "Initial Solution"},
        elemTypes = {I_DomainDiscretization.class, UserDataTuple[].class}
    )
    public Object[] createCableEquation
    (
        /// Approx space
        @ParamInfo(name="Approximation Space", style="default")
        I_ApproximationSpace approxSpace,
        
        /// function definition
        @ParamInfo(name="Function Definition", style="default")
        edu.gcsc.vrl.userdata.FunctionDefinition[] functionDefinition,
        
        /// Problem definition ///
        @ParamGroupInfo(group="Problem definition|true")
        @ParamInfo(name="", style="default", options="ugx_globalTag=\"gridFile\"; fct_tag=\"fctDef\"; type=\"S4:V, [K], [Na], [Ca]\"")
        UserDataTuple vmDiscSubsetData,
        
        @ParamGroupInfo(group="Problem definition|true")
        @ParamInfo(name="presynapse subset", style="default", options="ugx_globalTag=\"gridFile\"; type=\"s:presynaptic subset\"")
        UserDataTuple presynSubsetData,
        
        @ParamGroupInfo(group="Problem definition|true")
        @ParamInfo(name="(interneuronal) synapse subset", style="default", options="ugx_globalTag=\"gridFile\"; type=\"s:bi-exp. synapse subset\"")
        UserDataTuple biexpSubsetData,
        
        @ParamGroupInfo(group="Problem definition|true; Membrane transport|false")
        @ParamInfo(name="transport mechanisms", style="array", options="minArraySize=0")
        I_IChannel[] memTransporters,
               
        @ParamGroupInfo(group="Problem definition|true; Membrane transport|false")
        @ParamInfo(name="synapse handler", style="default", options="")
        I_NETISynapseHandler synHandler,
        
        @ParamGroupInfo(group="Problem definition|true; Start Value|false")
        @ParamInfo(name="Start Value", style="array", options="ugx_globalTag=\"gridFile\";"
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

        String[] selSs = ((UserDependentSubsetModel.FSDataType) vmDiscSubsetData.getData(0)).getSelSs();
        if (selSs.length == 0) throw new RuntimeException("No subset definition in IP3R pump definition!");
        String ssString = "";
        for (String s: selSs) ssString = ssString + ", " + s;
        ssString = ssString.substring(2);
        
        // construct VMDisc object
        vmDisc = new VMDisc(ssString);

        // add channels
        for (I_IChannel ch : memTransporters)
            vmDisc.add_channel(ch);
        
        // get synaptic subset info
        String presynSs = presynSubsetData.getSubset(0);
        String biExpSs = biexpSubsetData.getSubset(0);
        
        // synapse handler
        synHandler.set_vmdisc(vmDisc);
        synHandler.set_presyn_subset(presynSs);
        vmDisc.set_synapse_handler(synHandler);
        
        // Dirichlet bnds on inter-neuronal synapse subset
        I_DirichletBoundary diri = new DirichletBoundary();
        for (int i = 0; i < selFcts.length; ++i)
            diri.add(0.0, selFcts[i], biExpSs);
        
        
        // add to domain disc
        domainDisc.add(vmDisc);
        domainDisc.add(diri);
        
        
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
        
        return new Object[]{domainDisc, startValue};
    }
    
    @MethodInfo(name="set material constants", interactive = false)
    public void set_material_constants
    (
        @ParamInfo(name="specific resistance [uOhm*m]", style="default", options="value=1e6") double specRes,
        @ParamInfo(name="specific capacity [kF/m^2]", style="default", options="value=1e-5") double specCap
    )
    {
        check_elemDisc_exists();
        
        check_value(specRes);
        check_value(specCap);
        
        vmDisc.set_spec_res(specRes);
        vmDisc.set_spec_cap(specCap);
    }
    
    @MethodInfo(name="set diffusion constants", interactive = false)
    public void set_diffusion_constants
    (
        @ParamInfo(name="K [m^2/ms]", style="default", options="value=1e-12") double diffK,
        @ParamInfo(name="Na [m^2/ms]", style="default", options="value=1e-12") double diffNa,
        @ParamInfo(name="Ca [m^2/ms]", style="default", options="value=2.2e-13") double diffCa
    )
    {
        check_elemDisc_exists();
        
        check_value(diffK);
        check_value(diffNa);
        check_value(diffCa);
        
        Double[] diffs = new Double[]{diffK, diffNa, diffCa};
        
        vmDisc.set_diff_coeffs(diffs);
    }
    
    @MethodInfo(name="set reversal potentials", interactive = false)
    public void set_reversal_potentials
    (
        @ParamInfo(name="K [mV]", style="default", options="value=50.0") double revPotK,
        @ParamInfo(name="Na [mV]", style="default", options="value=-77.0") double revPotNa,
        @ParamInfo(name="Ca [mV]", style="default", options="value=138.0") double revPotCa,
        @ParamInfo(name="Leak [mV]", style="default", options="value=-54.4") double revPotLeak
    )
    {
        check_elemDisc_exists();
                
        vmDisc.set_ek(revPotK);
        vmDisc.set_ena(revPotNa);
        vmDisc.set_eca(revPotCa);
        vmDisc.set_eleak(revPotLeak);
    }
    
    /* not yet avail on ug side
    @MethodInfo(name="set outer concentrations", interactive = false)
    public void set_outer_concentrations
    (
        @ParamInfo(name="K [mM]", style="default", options="value=2.5") double concK,
        @ParamInfo(name="Na [mM]", style="default", options="value=140.0") double concNa,
        @ParamInfo(name="Ca [mM]", style="default", options="value=1.5") double concCa
    )
    {
        check_elemDisc_exists();
        
        check_value(concK);
        check_value(concNa);
        check_value(concCa);
        
        vmDisc.set_(concK);
        vmDisc.set_(concNa);
        vmDisc.set_(concCa);
    }
    */
    
    @MethodInfo(name="set diameter", interactive = false)
    public void set_constant_diameter
    (
        @ParamInfo(name="diameter [m]", style="default", options="value=1e-6") double diam
    )
    {
        check_elemDisc_exists();
        
        check_value(diam);
        
        vmDisc.set_diameter(diam);
    }
    
    @MethodInfo(name="set temperature", interactive = false)
    public void set_temperature
    (
        @ParamInfo(name="temperature [deg C]", style="default", options="value=37.0") double temp
    )
    {
        check_elemDisc_exists();
        
        // for the Kelvin case
        //check_value(temp);
        
        vmDisc.set_celsius(temp);
    }
    
    
    @MethodInfo(noGUI=true)
    private void check_elemDisc_exists()
    {
        if (vmDisc == null)
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

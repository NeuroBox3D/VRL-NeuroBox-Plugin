/**
 * VRL-CalciumDynamics plugin
 * 
 * @date 2013-12-09
 * @author mbreit
**/

package edu.gcsc.vrl.neuro;

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


@ComponentInfo(name="CalciumDynamics", category="Neuro")
@ObjectInfo(instances = 1)
public class CalciumDynamics implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     *
     * @param approxSpace
     * @param functionDefinition
     * @param diffusionDiscs
     * @param bufferDiscs
     * @param ERMDiscs
     * @param PMDiscs
     * @param FBDiscs
     * @param startValue
     * @return
     */
    @MethodInfo(valueStyle="multi-out", interactive = false)
    @OutputInfo
    (
        style="multi-out",
        elemNames = {"Domain Disc", "Approximation Space", "Initial Solution"},
        elemTypes = {I_DomainDiscretization.class, I_ApproximationSpace.class, UserDataTuple[].class}
    )
    public Object[] invoke
    (
        /// Approx space
        @ParamInfo(name="Approximation Space", style="default")
        I_ApproximationSpace approxSpace,
        
        /// function definition
        @ParamInfo(name="Function Definition", style="default")
        edu.gcsc.vrl.userdata.FunctionDefinition[] functionDefinition,
                
        /// Problem definition ///
        @ParamGroupInfo(group="Problem definition|true; Diffusion|false")
        @ParamInfo(name="Diffusion elem discs", style="array", options="minArraySize=0")
        I_IElemDisc[] diffusionDiscs,
        
        @ParamGroupInfo(group="Problem definition|true; Buffering|false")
        @ParamInfo(name="Buffer elem discs", style="array", options="minArraySize=0")
        I_IElemDisc[] bufferDiscs,
        
        @ParamGroupInfo(group="Problem definition|true; ER Membrane|false")
        @ParamInfo(name="ERM elem discs", style="array", options="minArraySize=0")
        I_IElemDisc[] ERMDiscs,
        
        @ParamGroupInfo(group="Problem definition|true; Plasma Membrane|false")
        @ParamInfo(name="PM elem discs", style="array", options="minArraySize=0")
        I_IElemDisc[] PMDiscs,
        
        @ParamGroupInfo(group="Problem definition|true; Boundaries|false")
        @ParamInfo(name="Flux boundary elem discs", style="array", options="minArraySize=0")
        I_IElemDisc[] FBDiscs,
        
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
        
        // diffusion processes
        for (I_IElemDisc diffDisc : diffusionDiscs)
            domainDisc.add(diffDisc);
        
        // buffering processes
        for (I_IElemDisc buffDisc : bufferDiscs)
            domainDisc.add(buffDisc);
        
        /// ER membrane transport mechanisms
        for (I_IElemDisc elemDisc : ERMDiscs)
            domainDisc.add(elemDisc);
        
        /// PM transport mechanisms
        for (I_IElemDisc elemDisc : PMDiscs)
            domainDisc.add(elemDisc);
        
        // Neumann boundaries
        for (I_IElemDisc elemDisc : FBDiscs)
            domainDisc.add(elemDisc);
        
        
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
        return new Object[]{domainDisc, approxSpace, startValue};
    }
    
    
}

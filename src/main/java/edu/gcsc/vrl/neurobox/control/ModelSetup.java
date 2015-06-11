/**
 * Gathering class for IElemDiscs.
 * 
 * This is a general purpose implementation of a class that can combine an 
 * arbitrary number of IElemDiscs in one domainDisc.
 * It depends on the DomainAndFunctionDefinition class and will only work if an
 * instance of this class is already on the canvas and a geometry chosen as well
 * as functions defined.
 * 
 * The class can also serve as a template for more involved model setups.
 * 
 * @date 2014-10-27
 * @author mbreit
**/

package edu.gcsc.vrl.neurobox.control;

import edu.gcsc.vrl.ug.api.*;
import edu.gcsc.vrl.userdata.FunctionDefinition;
import edu.gcsc.vrl.userdata.UserDataTuple;
import edu.gcsc.vrl.userdata.UserDependentSubsetModel;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamGroupInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@ComponentInfo(name="Model Setup", category="Neuro")
public class ModelSetup implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     *
     * @param approxSpace
     * @param functionDefinition
     * @param elemDiscs
     * @param startValue
     * @return
     */
    @MethodInfo(valueStyle="multi-out", interactive = false)
    @OutputInfo
    (
        style="multi-out",
        elemNames = {"Domain Disc", "Initial Solution"},
        elemTypes = {I_DomainDiscretization.class, UserDataTuple[].class}
    )
    public Object[] setupModel
    (
        // approx space
        @ParamInfo(name="Approximation Space", style="default")
        I_ApproximationSpace approxSpace,
        
        // function definition
        @ParamInfo(name="Function Definition", style="default")
        edu.gcsc.vrl.userdata.FunctionDefinition[] functionDefinition,
                
        // collect IElemDisc objects by data connections
        @ParamGroupInfo(group="Problem definition|true")
        @ParamInfo(name="Connect your elem discs.", style="array", options="minArraySize=1")
        I_IElemDisc[] elemDiscs,
        
        @ParamGroupInfo(group="Initial values|true")
        @ParamInfo(name="Provide initial values for all unknowns.", style="array", options="ugx_globalTag=\"gridFile\";"
            + "fct_tag=\"fctDef\"; minArraySize=1; type=\"S1|n:function & subset, start value\"")
        UserDataTuple[] startValue
    )
    {
        //////////////////////////
        // discretization setup //
        //////////////////////////
        
        // create a new domain disc
        I_DomainDiscretization domainDisc = new DomainDiscretization(approxSpace);
        
        // add all connected elem discs to it
        for (I_IElemDisc disc : elemDiscs)
            domainDisc.add(disc);
        
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
}

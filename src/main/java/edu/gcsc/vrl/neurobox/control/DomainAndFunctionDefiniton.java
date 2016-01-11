package edu.gcsc.vrl.neurobox.control;

import edu.gcsc.vrl.ug.api.AlgebraType;
import edu.gcsc.vrl.ug.api.ApproximationSpace;
import edu.gcsc.vrl.ug.api.Domain;
import edu.gcsc.vrl.ug.api.F_GlobalDomainRefiner;
import edu.gcsc.vrl.ug.api.F_Order_cuthillmckee;
import edu.gcsc.vrl.ug.api.F_InitUG;
import edu.gcsc.vrl.ug.api.F_LoadDomain;
import edu.gcsc.vrl.ug.api.I_ApproximationSpace;
import edu.gcsc.vrl.ug.api.I_Domain;
import edu.gcsc.vrl.ug.api.I_IRefiner;
import edu.gcsc.vrl.ug.api.UGXFileInfo;
import edu.gcsc.vrl.userdata.FunctionDefinition;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ObjectInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamGroupInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.Serializable;

/**
 * @author mbreit
 */

@ComponentInfo(name="DomainAndFunctionDefinition", category="Neuro")
@ObjectInfo(instances = 1)
public class DomainAndFunctionDefiniton implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    @MethodInfo(valueStyle="multi-out", interactive = false)
    @OutputInfo
    (
        style="multi-out",
        elemNames = {"Approximation Space", "Function Definitions"},
        elemTypes = {I_ApproximationSpace.class, FunctionDefinition[].class}
    )
    public Object[] defineDomainAndFunction
    (
        @ParamGroupInfo(group="Domain")
        @ParamInfo(name="Grid", style="ugx-load-dialog", options="ugx_tag=\"gridFile\"")
        java.io.File gridFile,
        
        @ParamGroupInfo(group="Domain")
        @ParamInfo(name="refinements", style="default", options="value=0")
        Integer num_refs,
        
        @ParamGroupInfo(group="Functions")
        @ParamInfo(name="functions", style="array", options="ugx_tag=\"gridFile\"; fct_tag=\"fctDef\"; minArraySize=1;")
        FunctionDefinition[] functionDefinition
    )
    {
        // get selected geometry and its dim
        String gridFileName = gridFile.getAbsolutePath();
        UGXFileInfo ugxFI = new UGXFileInfo();

        //  parse ugx file for world dimension
        if (ugxFI.parse_file(gridFileName) == false)
            throw new RuntimeException("Unable to parse ugx-File: " + gridFileName);
        
        if (ugxFI.const__num_grids() != 1)
            throw new RuntimeException("UGX file must contain exactly one grid.");
        
        int dim = ugxFI.const__grid_world_dimension(0);

        // Init UG for dimension and algebra
        F_InitUG.invoke(dim, new AlgebraType("CPU", 1));
        
        // create, load, refine and distribute domain
        System.out.println("Create and refine domain");
        I_Domain dom = createAndRefineDomain(gridFileName, num_refs);
        
        /*
        //System.out.println("Saving domain grid and hierarchy.");
        //F_SaveDomain.invoke(dom, "refined_grid_p" + F_GetProcessRank.invoke() + ".ugx");
        //F_SaveGridHierarchyTransformed.invoke(dom.grid(), "refined_grid_hierarchy_p" + F_GetProcessRank.invoke() + ".ugx", 20.0);
        System.out.println("Saving parallel grid layout");
        F_SaveParallelGridLayout.invoke(dom.grid(), "parallel_grid_layout_p" + F_GetProcessRank.invoke() + ".ugx", 20.0);
        */
        
        // create approximation space
        System.out.println("Create approximation space");
        I_ApproximationSpace approxSpace = new ApproximationSpace(dom);
        
        boolean allFctsEverywhere = true;
        
        // defining approximation space according to function definition
        for (edu.gcsc.vrl.userdata.FunctionDefinition fd: functionDefinition)
        {
            String subsets = "";
            if (fd.getFctData().subsetList.isEmpty())
                throw new RuntimeException("No subset definition for function '"+fd.getFctData().fctName+"'!");
            
            for (String s: fd.getFctData().subsetList) subsets = subsets + ", " + s;
            subsets = subsets.substring(2);
            
            // check whether all subsets are chosen;
            // if so, add without subset specification
            if (ugxFI.const__num_subset_handlers(0) < 1)
                throw new RuntimeException("UGX file must contain at least one subset handler.");
        
            if (fd.getFctData().subsetList.size() == ugxFI.const__num_subsets(0, 0))
                approxSpace.add_fct(fd.getFctData().fctName, "Lagrange", 1);
            else
            {
                approxSpace.add_fct(fd.getFctData().fctName, "Lagrange", 1, subsets);
                allFctsEverywhere = false;
            }
        }

        approxSpace.init_levels();
        approxSpace.init_surfaces();
        approxSpace.init_top_surface();
        approxSpace.const__print_layout_statistic();
        approxSpace.const__print_statistic();
        
        if (allFctsEverywhere) F_Order_cuthillmckee.invoke(approxSpace);
        
        return new Object[]{approxSpace, functionDefinition};
    }
    
    
    // The following functions are mere java equivalents of lua script functions
    // defined in ug_util.lua and domain_distribution_util.lua.
    
    private I_Domain createAndRefineDomain(String gridName, int numRefs)
    {
        // create Instance of a Domain
	I_Domain dom = new Domain();
	
	// load domain
        System.out.print("Loading domain " + gridName + " ... ");
        F_LoadDomain.invoke(dom, gridName);
	System.out.print("done.\n");
        
	// Create a refiner instance. This is a factory method
	// which automatically creates a parallel refiner if required.
	I_IRefiner refiner = null;
	if (numRefs > 0)
        {
            System.out.print("Refining(" + numRefs + "): ");
            refiner = F_GlobalDomainRefiner.invoke(dom);
        }
        
	// perform refinements
	if (refiner != null) 
            for (int i = 0; i < numRefs; ++i)
                refiner.refine();
	
	// return the created domain
	return dom;
    }
    
}
        


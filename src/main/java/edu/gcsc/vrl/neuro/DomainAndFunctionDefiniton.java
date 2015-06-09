package edu.gcsc.vrl.neuro;

import edu.gcsc.vrl.ug.api.AlgebraType;
import edu.gcsc.vrl.ug.api.ApproximationSpace;
import edu.gcsc.vrl.ug.api.Domain;
import edu.gcsc.vrl.ug.api.F_GlobalDomainRefiner;
import edu.gcsc.vrl.ug.api.F_InitUG;
import edu.gcsc.vrl.ug.api.F_LoadDomain;
import edu.gcsc.vrl.ug.api.I_ApproximationSpace;
import edu.gcsc.vrl.ug.api.I_Domain;
import edu.gcsc.vrl.ug.api.I_IRefiner;
import edu.gcsc.vrl.ug.api.I_InterSubsetPartitionWeighting;
import edu.gcsc.vrl.ug.api.I_MGSubsetHandler;
import edu.gcsc.vrl.ug.api.I_PartitionWeighting;
import edu.gcsc.vrl.ug.api.InterSubsetPartitionWeighting;
import edu.gcsc.vrl.ug.api.UGXFileInfo;
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
        elemTypes = {I_ApproximationSpace.class, edu.gcsc.vrl.userdata.FunctionDefinition[].class}
    )
    public Object[] defineDomainAndFunction
    (
        @ParamGroupInfo(group="Domain")
        @ParamInfo(name="Grid", style="ugx-load-dialog", options="ugx_tag=\"gridFile\"")
        java.io.File gridFile,
        
        /// SIMULATION PARAMS ///
        // Refiner params
        @ParamGroupInfo(group="Simulation params|false;Refiner")
        @ParamInfo(name="refinements", style="default", options="value=0")
        Integer num_refs,

        @ParamGroupInfo(group="Simulation params|false;Refiner")
        @ParamInfo(name="pre-refinements", style="default", options="value=0")
        Integer num_prerefs,
        
        /// Funtions and subsets
        @ParamGroupInfo(group="Functions|true")
        @ParamInfo(name="functions", style="array", options="ugx_tag=\"gridFile\"; fct_tag=\"fctDef\"; minArraySize=1;")
        edu.gcsc.vrl.userdata.FunctionDefinition[] functionDefinition
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
        System.out.println("Create, refine and distribute domain");
        String[] neededSubsets = {};
        String distributionMethod = "metisReweigh";
        I_InterSubsetPartitionWeighting weightingFct = new InterSubsetPartitionWeighting();
        weightingFct.set_default_weights(1,1);
        weightingFct.set_inter_subset_weight(0, 1, 1000);
        I_Domain dom = createAndDistributeDomain(gridFileName, num_refs, num_prerefs, neededSubsets, distributionMethod, true, -1, -1, weightingFct);
        
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
        
        // defining approximation space according to function definition
        for (edu.gcsc.vrl.userdata.FunctionDefinition fd: functionDefinition)
        {
            String subsets = "";
            if (fd.getFctData().subsetList.isEmpty())
                throw new RuntimeException("No subset definition for function '"+fd.getFctData().fctName+"'!");
            
            for (String s: fd.getFctData().subsetList) subsets = subsets + ", " + s;
            subsets = subsets.substring(2);
            approxSpace.add_fct(fd.getFctData().fctName, "Lagrange", 1, subsets);
        }

        approxSpace.init_levels();
        approxSpace.const__print_layout_statistic();
        approxSpace.const__print_statistic();
        
        return new Object[]{approxSpace, functionDefinition};
    }
    
    
    // The following functions are mere java equivalents of lua script functions
    // defined in ug_util.lua and domain_distribution_util.lua.
    
    private I_Domain createAndDistributeDomain(String gridName, int numRefs, int numPreRefs, String[] neededSubsets, String distributionMethod, boolean verticalInterfaces, int numTargetProcs, int distributionLevel, I_PartitionWeighting wFct)
    {
        // create Instance of a Domain
	I_Domain dom = new Domain();
	
	// load domain
        System.out.print("Loading domain " + gridName + " ... ");
        F_LoadDomain.invoke(dom, gridName);
	System.out.print("done.\n");
        
	// create Refiner
	if (numPreRefs > numRefs)
            throw new RuntimeException("numPreRefs must be smaller than numRefs.");
	
	if (numPreRefs > numRefs)  numPreRefs = numRefs;
	
	// Create a refiner instance. This is a factory method
	// which automatically creates a parallel refiner if required.
	I_IRefiner refiner = null;
	if (numRefs > 0)
        {
            System.out.print("Refining(" + numRefs + "): ");
            refiner = F_GlobalDomainRefiner.invoke(dom);
	
            // performing pre-refines
            for (int i=0; i<numPreRefs; i++)
            {
                refiner.refine();
                System.out.print(i + " ");
            }
            
            System.out.print("done.\n");
        }
	
        /*
        // distribute the domain to all involved processes
	if (!distributeDomain(dom, distributionMethod, verticalInterfaces, numTargetProcs, distributionLevel, wFct))
            throw new RuntimeException("Error while Distributing Grid.");
	*/
        
	// perform post-refine
	for (int i=numPreRefs; i<numRefs; i++) refiner.refine();
	
	// loop all subsets and search for them
        // in the SubsetHandler of the domain
	if (neededSubsets != null)
        {
            if (!checkSubsets(dom, neededSubsets))
                throw new RuntimeException("Something wrong with required subsets. Aborting.");
        }
	
	// return the created domain
	return dom;
    }
    
    /*
    private boolean distributeDomain(I_Domain dom, String partitioningMethod, boolean verticalInterfaces, int numTargetProcs, int distributionLevel, I_PartitionWeighting wFct)
    {
        if (F_NumProcs.invoke() == 1) return true;
	
	I_PartitionMap partitionMap = new PartitionMap();
        
	if (partitioningMethod == null) partitioningMethod = "bisection";
	
	if (numTargetProcs <= 0) numTargetProcs = F_NumProcs.invoke();
        
	if (distributionLevel < 0)
        {
		distributionLevel = dom.grid().const__num_levels() - 1;
		if (distributionLevel < 0) distributionLevel = 0;
        }
	
	if (dom.const__domain_info().const__num_elements_on_level(distributionLevel) < numTargetProcs)
        {
            System.out.println("\nWARNING in DistributeDomain:");
            System.out.println("    There are less elements on distributionLevel than there are target processes.");
            System.out.println("    If ug hangs during parallel execution, consider increasing numPreRefs to avoid this!");
            System.out.println("    num elements on level " + distributionLevel + ": "
                + dom.const__domain_info().const__num_elements_on_level(distributionLevel));
            System.out.println("    num target processes: " + numTargetProcs);
            System.out.println("");
        }
	
	if ("bisection".equals(partitioningMethod))
        {
            if (distributionLevel < dom.grid().const__num_levels() - 1)
            {
                System.out.println("WARNING in util.DistributeDomain: 'bisection' can currently "
                    + "only be performed on the top level. Sorry...");
            }
            partitionMapBisection(dom, partitionMap, numTargetProcs);
        }
        else if ("metis".equals(partitioningMethod))
        {
            partitionMapMetis(dom, partitionMap, numTargetProcs, distributionLevel);
        }
        else if ("metisReweigh".equals(partitioningMethod))
        {
            if (wFct != null)
                partitionMapMetisReweigh(dom, partitionMap, numTargetProcs, distributionLevel, wFct);
            else 
            {
                System.out.println("ERROR in CalciumDynamics::distributeDomain: "
                        + "requested partitionMethod \"metisReweigh\", but no weightingFct given.");
                return false;
            }
        }
        else
        {
            System.out.println("ERROR in util.DistributeDomain: Unknown partitioning method.\n"
                + "  Valid partitioning methods are: 'bisection', 'metis' and 'metisReweigh'");
            return false;
        }
	
	boolean success = F_DistributeDomain.invoke(dom, partitionMap, verticalInterfaces);
	
	return success;
    }
    
    
    private void partitionMapBisection(I_Domain dom, I_PartitionMap partitionMapOut, int numProcs)
    {
        if (partitionMapOut.num_target_procs() != numProcs)
        {
            partitionMapOut.clear();
            partitionMapOut.add_target_procs(0, numProcs);
        }
        
        I_ProcessHierarchy procH = new ProcessHierarchy();
	if (dom.grid().const__num_levels() > 0)
            procH.add_hierarchy_level(dom.grid().const__num_levels() - 1, numProcs);
	else
            procH.add_hierarchy_level(0, numProcs);
        
        if (dom.const__domain_info().const__element_type() == dom.const__get_dim() - 2)
        {
            I_HyperManifoldPartitioner_DynamicBisection partitioner = new HyperManifoldPartitioner_DynamicBisection(dom);
            partitioner.enable_clustered_siblings(false);
            partitioner.set_verbose(false);
            partitioner.enable_static_partitioning(true);
            partitioner.set_subset_handler(partitionMapOut.get_partition_handler());
            partitioner.set_next_process_hierarchy(procH);
            partitioner.partition(0, 0);
        }
        else if (dom.const__domain_info().const__element_type() == dom.const__get_dim() - 1)
        {
            I_ManifoldPartitioner_DynamicBisection partitioner = new ManifoldPartitioner_DynamicBisection(dom);
            partitioner.enable_clustered_siblings(false);
            partitioner.set_verbose(false);
            partitioner.enable_static_partitioning(true);
            partitioner.set_subset_handler(partitionMapOut.get_partition_handler());
            partitioner.set_next_process_hierarchy(procH);
            partitioner.partition(0, 0);
        }
        else if (dom.const__domain_info().const__element_type() == dom.const__get_dim())
        {
            Partitioner_DynamicBisection partitioner = new Partitioner_DynamicBisection(dom);
            partitioner.enable_clustered_siblings(false);
            partitioner.set_verbose(false);
            partitioner.enable_static_partitioning(true);
            partitioner.set_subset_handler(partitionMapOut.get_partition_handler());
            partitioner.set_next_process_hierarchy(procH);
            partitioner.partition(0, 0);
        }
    }
    
    private void partitionMapMetis(I_Domain dom, I_PartitionMap partitionMapOut, int numProcs, int baseLevel)
    {
        if (partitionMapOut.num_target_procs() != numProcs)
        {
            partitionMapOut.clear();
            partitionMapOut.add_target_procs(0, numProcs);
        }
        F_PartitionDomain_MetisKWay.invoke(dom, partitionMapOut, numProcs, baseLevel, 1, 1);
    }
    
    private void partitionMapMetisReweigh(I_Domain dom, I_PartitionMap partitionMapOut, int numProcs, int baseLevel, I_PartitionWeighting wFct)
    {
        if (partitionMapOut.num_target_procs() != numProcs)
        {
            partitionMapOut.clear();
            partitionMapOut.add_target_procs(0, numProcs);
        }
	F_PartitionDomain_MetisKWay.invoke(dom, partitionMapOut, numProcs, baseLevel, wFct);
    }
    */
    private boolean checkSubsets(I_Domain dom, String[] neededSubsets)
    {
        I_MGSubsetHandler sh = dom.subset_handler();
	for (String s: neededSubsets)
        {
            if (sh.const__get_subset_index(s) == -1)
            {
                System.out.print("Domain does not contain subset '" + s + "'.");
                return false;
            }
        }
	
	return true;
    }
}
        


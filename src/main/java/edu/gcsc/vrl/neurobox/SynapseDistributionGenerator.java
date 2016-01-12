/**
 * SynapseDistributionGenerator.java
 * 
 * @date 2015-06-30
 * @author mstepnie
**/

package edu.gcsc.vrl.neurobox;

import edu.gcsc.vrl.ug.api.I_SynapseDistributor;
import edu.gcsc.vrl.ug.api.SynapseDistributor;
import edu.gcsc.vrl.ug.api.UGXFileInfo;
import edu.gcsc.vrl.userdata.UserDataTuple;
import edu.gcsc.vrl.ug.api.I_ConstUserNumber;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.system.VMessage;


@ComponentInfo(name="SynapseDistributionGenerator", category="Neuro")
public class SynapseDistributionGenerator implements java.io.Serializable
{
    private static final long serialVersionUID = 1L;
    private transient I_SynapseDistributor sd = null;    
    private int numSubsets;
    private String outpath;

    @MethodInfo(name="create synapse distributor", initializer=true, hide=false, interactive=true)
    public void create_synapse_distributor(@ParamInfo(name="Input grid", style="ugx-load-dialog", options="ugx_tag=\"gridFile\"") java.io.File file,
                                           @ParamInfo(name="Delete existing synapses", options="value=true") boolean bDelSynapses)
                                           
    {
    //  Catch bad file specification
        if (!file.exists())
        {
            VMessage.exception("SynapseDistributionGenerator::constructor failed",
                               "The geometry supposedly created by the synapse distribution generator can not be found.");           
        }        
        
    //  Instantiate UGXFileInfo to parse file for global subset publication
        String gridFile = file.getAbsoluteFile().getAbsolutePath();        
        UGXFileInfo ugxFI = new UGXFileInfo();
        
        if (ugxFI.parse_file(gridFile) == false)
            throw new RuntimeException("Unable to parse ugx-File: " + gridFile);  
        
    //  Grid export file handling        
        outpath = gridFile.substring(0, gridFile.lastIndexOf(file.separator) + 1);                
        String outfile = outpath + "SynDistGenTmpOut.ugx";
        
    ///
    //  MEMBER INITIALIZATION
    ///
        
    //  Get number of subsets in grid
        numSubsets = ugxFI.const__num_subsets(0, 0);
        
    //  construct new synapse distributor object
        sd = new SynapseDistributor(gridFile, outfile, bDelSynapses);                
    }
            
    @MethodInfo(name="place synapses")                                 
    public void place_synapses(@ParamInfo(name="Specify density [1/µm] of synapses to be distributed on the subset(s) of choice", 
                                                style="array", options="ugx_globalTag=\"gridFile\";" 
                                                + "minArraySize=1; type=\"s|n:,density\"") UserDataTuple[] distrData)
    {   
    //  Setup array for user-specified subsets and corresponding synapse densities
        String[] subsets;
        Double[] densities;
        subsets   = new String[numSubsets];
        densities = new Double[numSubsets];
        
    //  Catch density specification for too many subsets
        if(distrData.length > numSubsets)
            VMessage.exception("SynapseDistributionGenerator::place_synapses failed", 
                               "Invalid specification: Density specifiation for more subsets than contained in grid.");
        
    //  Read user specified subsets and densities 
        int cnt = 0;
        
        for(UserDataTuple udt: distrData)
        {                        
            I_ConstUserNumber value = (I_ConstUserNumber)udt.getNumberData(1); 
            
            if (!(value instanceof I_ConstUserNumber))
                VMessage.exception("SynapseDistributionGenerator::place_synapses failed",
                                   "Invalid specification: Synapse densities cannot be given as code.");
                        
            densities[cnt] = value.const__get();            
            subsets[cnt]   = udt.getSubset(0);
            
            cnt++;
        }    
       
    //  Place synapses according to user input    
        for (int i = 0; i < numSubsets; i++ ) 
        {
            if(subsets[i] != null && densities[i] != null)
                sd.place_synapses_uniform(subsets[i], densities[i]);
        }   
             
    //  Export file
        //sd.export_grid();                   
    }
    
    @MethodInfo(name="degenerate synapses")                                 
    public void degenerate_synapses(@ParamInfo(name="Specify fraction of synapses to be degenerated from the subset(s) of choice. "
                                                    + "Fraction means: newNumber = (1-fraction)*oldNumber", 
                                               style="array", options="ugx_globalTag=\"gridFile\";" 
                                               + "minArraySize=1; type=\"s|n:,fraction\"") UserDataTuple[] distrData)
    {   
    //  Setup array for user-specified subsets and corresponding synapse densities
        String[] subsets;
        Double[] fraction;
        subsets   = new String[numSubsets];
        fraction  = new Double[numSubsets];
        
    //  Catch density specification for too many subsets
        if(distrData.length > numSubsets)
            VMessage.exception("SynapseDistributionGenerator::degenerate_synapses failed", 
                               "Invalid specification: Fraction specifiation for more subsets than contained in grid.");
        
    //  Read user specified subsets and densities 
        int cnt = 0;
        
        for(UserDataTuple udt: distrData)
        {                        
            I_ConstUserNumber value = (I_ConstUserNumber)udt.getNumberData(1); 
            
            if (!(value instanceof I_ConstUserNumber))
                VMessage.exception("SynapseDistributionGenerator::degenerate_synapses failed",
                                   "Invalid specification: Degeneration fractions cannot be given as code.");
                        
            fraction[cnt]       = value.const__get();            
            subsets[cnt] = udt.getSubset(0);
            
            cnt++;
        }    
       
    //  Degenerate synapses according to user input    
        for (int i = 0; i < numSubsets; i++ ) 
        {
            if(subsets[i] != null && fraction[i] != null)
                sd.degenerate_uniform(fraction[i], subsets[i]);
        }
        
    //  Export file
        //sd.export_grid();                   
    }
    
    @MethodInfo(name="print total number of synapses")                                 
    public void print_num_synapses()
    {   
        VMessage.info("SynapseDistributionGenerator::print_num_synapses info", sd.num_synapses().toString() + " synapses in total.");
        System.out.println("Done.");
    }
    
    @MethodInfo(name="print number of synapses in specified subset")                                 
    public void print_num_synapses(@ParamInfo(name="Select subset", 
                                              style="default", options="ugx_globalTag=\"gridFile\";" 
                                              + "type=\"s:Subset\"") UserDataTuple subset)
    {   
        String subsetName = subset.getSubset(0);
        VMessage.info("SynapseDistributionGenerator::print_num_synapses info", sd.num_synapses(subsetName).toString() + " synapses in subset " + subsetName + ".");
    }
    
    @MethodInfo(name="print length of specified subset")                                 
    public void print_subset_length(@ParamInfo(name="Select subset", 
                                               style="default", options="ugx_globalTag=\"gridFile\";" 
                                               + "type=\"s:Subset\"") UserDataTuple subset)
    {   
        String subsetName = subset.getSubset(0);
        VMessage.info("SynapseDistributionGenerator::print_subset_length info", "Length of subset " + subsetName + ": " + sd.get_subset_length(subsetName).toString() + " meter.");
    }
    
    @MethodInfo(name="export grid")                                 
    public void export_grid(@ParamInfo(name="Output filename (specify with *.ugx)") String outfile)
    {   
        outfile = outpath + outfile;
        sd.export_grid(outfile);
        
        java.io.File file = new java.io.File(outfile);
        
    //  Catch export error
        if (!file.exists())
        {
            VMessage.exception("SynapseDistributionGenerator::export_grid failed",
                               "The geometry supposedly created by the synapse distribution generator can not be found.");           
        }
        else
            VMessage.info("SynapseDistributionGenerator::export_grid info", "Geometry successfully written.");
    }
    
    @MethodInfo(name="export grid to")                                 
    public void export_grid_to(@ParamInfo(name="File name", style="save-dialog", options="tag=\"TheFile\"") java.io.File file)
    {   
        String filename = file.getAbsoluteFile().getAbsolutePath();
        
        sd.export_grid(filename);
        
        if (!file.exists())
        {
            VMessage.exception("SynapseDistributionGenerator::export_grid failed",
                               "The geometry supposedly created by the synapse distribution generator can not be found.");                        
        }
    }
}

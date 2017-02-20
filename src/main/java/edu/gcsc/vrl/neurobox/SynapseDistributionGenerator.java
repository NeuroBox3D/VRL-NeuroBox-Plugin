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
import static java.lang.Math.floor;


@ComponentInfo(name="SynapseDistributionGenerator", category="Neuro/cable")
public class SynapseDistributionGenerator implements java.io.Serializable
{
    private static final long serialVersionUID = 1L;
    private transient I_SynapseDistributor sd = null;    
    private int numSubsets;
    private String outpath;

    @MethodInfo(name="create synapse_distributor", initializer=true, hide=false, interactive=true)
    public void create_synapse_distributor
	(
		@ParamInfo(name="Input grid", style="ugx-load-dialog", options="ugx_tag=\"gridFile\"") java.io.File file,
        @ParamInfo(name="Delete existing synapses", options="value=true") boolean bDelSynapses
	)                                       
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

    //  Get number of subsets in grid
        numSubsets = ugxFI.const__num_subsets(0, 0);
        
    //  construct new synapse distributor object
        sd = new SynapseDistributor(gridFile);
		
	/// remove all previously ecisting synapses from grid, if required
		if (bDelSynapses) sd.clear();
    }

    /**
     * @brief place alpha synapses on a ball
     * @param distrData 
     */
    @MethodInfo(name="place synapses within ball")
    public void place_synapses_ball
	(
		@ParamInfo
		(
			name = "Synapse type",
			style = "selection",
			options = "value=[\"alpha synapse\",\"biexp synapse\"]"
		) String syn_type,
		
		@ParamInfo
		(
			name = "<html>Set density [1/m] of synapses to be distributed within ball "
                 + "of choice. <br> <br>-> Specify (density) tuple(s)</html>", 
            style = "array",
			options = "ugx_globalTag=\"gridFile\";"
					  + "minArraySize=1; type=\"n|n|n|n|n:density,x,y,z,radius\""
		) UserDataTuple[] distrData
	) 
    {
		for (UserDataTuple udt: distrData){
			I_ConstUserNumber densVal = (I_ConstUserNumber)udt.getNumberData(0); 

			if (!(densVal instanceof I_ConstUserNumber)) {
					VMessage.exception("SynapseDistributionGenerator::place_alpha_synapses_ball failed",
								   "Invalid specification: Synapse densities cannot be given as code.");
			}

			Double density = densVal.const__get();

			I_ConstUserNumber xVal = (I_ConstUserNumber) udt.getNumberData(1);	
			I_ConstUserNumber yVal = (I_ConstUserNumber) udt.getNumberData(2);	
			I_ConstUserNumber zVal = (I_ConstUserNumber) udt.getNumberData(3);	
			I_ConstUserNumber rVal = (I_ConstUserNumber) udt.getNumberData(4);	
			Double x = xVal.const__get();
			Double y = yVal.const__get();
			Double z = zVal.const__get();
			Double r = rVal.const__get();

			if ("alpha synapse".equals(syn_type))
				sd.place_synapses_uniform(density, x, y, z, r, "AlphaPostSynapse");
			else if ("biexp synapse".equals(syn_type))
				sd.place_synapses_uniform(density, x, y, z, r, "Exp2PostSynapse");
			else
			{
				VMessage.exception("SynapseDistributionGenerator::place_synapses_ball failed",
                                   "Invalid specification of synapse type.");
				return;
			}
		}
    }

//	This functionality is now incorporated in the more general function place_synapses_ball().
//    /**
//     * @brief place exp2 synapses on a ball
//     * @param distrData 
//     */
//    @MethodInfo(name="place EXP2 synapses within ball")
//    public void place_exp2_synapses_ball
//	(
//		@ParamInfo
//		(
//			name = "<html>Set density [1/m] of synapses to be distributed within ball "
//				   + "of choice. <br> <br>-> Specify (density) tuple(s)</html>", 
//			style = "array",
//			options = "ugx_globalTag=\"gridFile\";" 
//					  + "minArraySize=1; type=\"n|n|n|n|n:density,x,y,z,radius\""
//		) UserDataTuple[] distrData) 
//    {                
//		for(UserDataTuple udt: distrData) {
//            I_ConstUserNumber densVal = (I_ConstUserNumber)udt.getNumberData(0); 
//            if (!(densVal instanceof I_ConstUserNumber)) {
//                     VMessage.exception("SynapseDistributionGenerator::place_exp2_synapses_ball failed",
//                                "Invalid specification: Synapse densities cannot be given as code.");
//            }
//
//            Double density = densVal.const__get();
//
//            I_ConstUserNumber xVal = (I_ConstUserNumber) udt.getNumberData(1);	
//            I_ConstUserNumber yVal = (I_ConstUserNumber) udt.getNumberData(2);	
//            I_ConstUserNumber zVal = (I_ConstUserNumber) udt.getNumberData(3);	
//            I_ConstUserNumber rVal = (I_ConstUserNumber) udt.getNumberData(4);	
//            Double x = xVal.const__get();
//            Double y = yVal.const__get();
//            Double z = zVal.const__get();
//            Double r = rVal.const__get();
//			    
//            sd.place_synapses_uniform(density, x, y, z, r, "Exp2PostSynapse");
//		}
//	}
 
    @MethodInfo(name="place density of synapses")                                 
    public void place_synapses_density
	(
		@ParamInfo
		(
			name = "Synapse type",
			style = "selection",
			options = "value=[\"alpha synapse\",\"biexp synapse\"]"
		) String syn_type,
		
		@ParamInfo
		(
			name = "<html>Set density [1/m] of synapses to be distributed on the subset(s) "
                   + "of choice. <br> <br>-> Specify (Subset, density) tuple(s)</html>", 
			style = "array",
			options="ugx_globalTag=\"gridFile\";" + "minArraySize=1; type=\"s|n:,density\""
		) UserDataTuple[] distrData)
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
			{
				if ("alpha synapse".equals(syn_type))
					sd.place_synapses_uniform(subsets[i], densities[i], "AlphaPostSynapse");
				else if ("biexp synapse".equals(syn_type))
					sd.place_synapses_uniform(subsets[i], densities[i], "Exp2PostSynapse");
				else
				{
					VMessage.exception("SynapseDistributionGenerator::place_synapses failed",
									   "Invalid specification of synapse type.");
					return;
				}
			}
        }               
    }
 
    @MethodInfo(name="place number of synpases")                                 
    public void place_synapses_number
	(
		@ParamInfo
		(
			name = "Synapse type",
			style = "selection",
			options = "value=[\"alpha synapse\",\"biexp synapse\"]"
		) String syn_type,
		
		@ParamInfo
		(
			name = "<html>Set number of synapses to be distributed on the subset(s) "
                   + "of choice. <br> <br>-> Specify (Subset, number) tuple(s)</html>", 
			style = "array",
			options="ugx_globalTag=\"gridFile\";" + "minArraySize=1; type=\"s|n:,number of synapses\""
		) UserDataTuple[] distrData)
    {                
    //  Setup array for user-specified subsets and corresponding synapse densities
        String[] subsets;
        Integer[] counts;
        subsets   = new String[numSubsets];
        counts = new Integer[numSubsets];
        
    //  Catch density specification for too many subsets
        if(distrData.length > numSubsets)
            VMessage.exception("SynapseDistributionGenerator::place_synapses failed", 
                               "Invalid specification: Density specifiation for more subsets than contained in grid.");
        
    //  Read user specified subsets and densities 
        int cnt = 0;
        
        for (UserDataTuple udt: distrData)
        {                        
            I_ConstUserNumber value = (I_ConstUserNumber)udt.getNumberData(1); 
            
            if (!(value instanceof I_ConstUserNumber))
                VMessage.exception("SynapseDistributionGenerator::place_synapses failed",
                                   "Invalid specification: Synapse densities cannot be given as code.");
                        
            counts[cnt] = (int) floor(value.const__get());            
            subsets[cnt]   = udt.getSubset(0);
            
            cnt++;
        }

    //  Place synapses according to user input    
        for (int i = 0; i < numSubsets; i++ ) 
        {
            if (subsets[i] != null && counts[i] != null)
			{
				if ("alpha synapse".equals(syn_type))
					sd.place_synapses_uniform(subsets[i], counts[i], "AlphaPostSynapse");
				else if ("biexp synapse".equals(syn_type))
					sd.place_synapses_uniform(subsets[i], counts[i], "Exp2PostSynapse");
				else
				{
					VMessage.exception("SynapseDistributionGenerator::place_synapses failed",
									   "Invalid specification of synapse type.");
					return;
				}
			}
        }               
    }
    
//	This method is now incorporated in the more general method place_synapses().
//    @MethodInfo(name="place EXP2 synapses")                                 
//    public void place_exp2_synapses(@ParamInfo(name="<html>Set density [1/m] of synapses to be distributed on the subset(s) "
//                                                + "of choice. <br> <br>-> Specify (Subset, density) tuple(s)</html>", 
//                                                style="array", options="ugx_globalTag=\"gridFile\";" 
//                                                + "minArraySize=1; type=\"s|n:,density\"") UserDataTuple[] distrData)
//    {                
//    //  Setup array for user-specified subsets and corresponding synapse densities
//        String[] subsets;
//        Double[] densities;
//        subsets   = new String[numSubsets];
//        densities = new Double[numSubsets];
//        
//    //  Catch density specification for too many subsets
//        if(distrData.length > numSubsets)
//            VMessage.exception("SynapseDistributionGenerator::place_synapses failed", 
//                               "Invalid specification: Density specifiation for more subsets than contained in grid.");
//        
//    //  Read user specified subsets and densities 
//        int cnt = 0;
//        
//        for(UserDataTuple udt: distrData)
//        {                        
//            I_ConstUserNumber value = (I_ConstUserNumber)udt.getNumberData(1); 
//            
//            if (!(value instanceof I_ConstUserNumber))
//                VMessage.exception("SynapseDistributionGenerator::place_synapses failed",
//                                   "Invalid specification: Synapse densities cannot be given as code.");
//                        
//            densities[cnt] = value.const__get();            
//            subsets[cnt]   = udt.getSubset(0);
//            
//            cnt++;
//        }    
//       
//    //  Place synapses according to user input    
//        for (int i = 0; i < numSubsets; i++ ) 
//        {
//            if(subsets[i] != null && densities[i] != null)
//                sd.place_synapses_uniform(subsets[i], densities[i], "Exp2PostSynapse");
//        }            
//    }
    
    @MethodInfo(name="degenerate synapses")                                 
    public void degenerate_synapses
	(
		@ParamInfo
		(
			name = "<html>Set fraction of synapses to be deleted from the subset(s) of choice in the range [0,1]."
                   + "<br> <br>For the new number of synapses <br>fraction means: <br>newNumber = "
				   + "(1-fraction)*oldNumber <br><br>-> Specify (Subset, fraction) tuple(s)</html>", 
			style = "array",
			options="ugx_globalTag=\"gridFile\";" + "minArraySize=1; type=\"s|n:,fraction\""
		) UserDataTuple[] distrData)
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
           
            if(value.const__get() < 0 || value.const__get() > 1)
                VMessage.exception("SynapseDistributionGenerator::degenerate_synapses failed", 
                                   "Invalid specification: Degeneration fractions out of range [0,1].");
            
            fraction[cnt] = value.const__get();            
            subsets[cnt]  = udt.getSubset(0);
            
            cnt++;
        }    
       
    //  Degenerate synapses according to user input    
        for (int i = 0; i < numSubsets; i++ ) 
        {
            if(subsets[i] != null && fraction[i] != null)
                sd.degenerate_uniform(fraction[i], subsets[i]);
        }                
    }
	
/* The activation timing functions are no longer part of the Distributor. They belong to the Handler.
    @MethodInfo(name="set activation timing")                                 
    public void set_activation_timing
    (
        @ParamInfo(name="<html>ALPHA synapse timings<br>"
                         + "<br>average start time [ms]</html>", 
                   style="default", options="") Double start_time,
        @ParamInfo(name="std deviation of start time [ms]", style="default", options="") Double start_time_dev,
        @ParamInfo(name="duration [ms]", style="default", options="") Double duration,
        @ParamInfo(name="std deviation of duration [ms]", style="default", options="") Double duration_dev,
        @ParamInfo(name="peak conductance [uS]", style="default", options="") Double peak_cond,
        @ParamInfo(name="<html><br>"
                         + "EXP2 synapse timings<br>"
                         + "<br> average start time [ms]</html>", 
                   style="default", options="") Double biexp_onset_time,
        @ParamInfo(name="std deviation of start time [ms]", style="default", options="") Double biexp_onset_time_dev,
        @ParamInfo(name="mean of tau1 [ms]", style="default", options="") Double biexp_tau1_mean,
        @ParamInfo(name="std deviation of tau1 [ms]", style="default", options="") Double biexp_tau1_dev,
        @ParamInfo(name="mean of tau2 [ms]", style="default", options="") Double biexp_tau2_mean,
        @ParamInfo(name="std deviation of tau2 [ms]", style="default", options="") Double biexp_tau2_dev,
        @ParamInfo(name="peak conductance [uS]", style="default", options="") Double biexp_peak_cond
    )
    {   
        Double[] alpha_syn_params = new Double[5];
        alpha_syn_params[0] = start_time;
        alpha_syn_params[1] = start_time_dev;
        alpha_syn_params[2] = duration;
        alpha_syn_params[3] = duration_dev;
        alpha_syn_params[4] = peak_cond;
        
        Double[] biexp_syn_params = new Double[7];
        biexp_syn_params[0] = biexp_onset_time;
        biexp_syn_params[1] = biexp_onset_time_dev;
        biexp_syn_params[2] = biexp_tau1_mean;
        biexp_syn_params[3] = biexp_tau1_dev;
        biexp_syn_params[4] = biexp_tau2_mean;
        biexp_syn_params[5] = biexp_tau2_dev;
        biexp_syn_params[6] = biexp_peak_cond;
        
        sd.set_activation_timing(alpha_syn_params, biexp_syn_params);
    }
    
    @MethodInfo(name="set activation timing ball")                                 
    public void set_activation_timing_ball
    (
        @ParamInfo(name="<html>ALPHA synapse timings<br>"
                         + "<br>average start time [ms]</html>", 
                   style="default", options="") Double start_time,
        @ParamInfo(name="std deviation of start time [ms]", style="default", options="") Double start_time_dev,
        @ParamInfo(name="duration [ms]", style="default", options="") Double duration,
        @ParamInfo(name="std deviation of duration [ms]", style="default", options="") Double duration_dev,
        @ParamInfo(name="peak conductance [uS]", style="default", options="") Double peak_cond,
        @ParamInfo(name="<html><br>"
                         + "EXP2 synapse timings<br>"
                         + "<br> average start time [ms]</html>", 
                   style="default", options="") Double biexp_onset_time,
        @ParamInfo(name="std deviation of start time [ms]", style="default", options="") Double biexp_onset_time_dev,
        @ParamInfo(name="mean of tau1 [ms]", style="default", options="") Double biexp_tau1_mean,
        @ParamInfo(name="std deviation of tau1 [ms]", style="default", options="") Double biexp_tau1_dev,
        @ParamInfo(name="mean of tau2 [ms]", style="default", options="") Double biexp_tau2_mean,
        @ParamInfo(name="std deviation of tau2 [ms]", style="default", options="") Double biexp_tau2_dev,
        @ParamInfo(name="peak conductance [uS]", style="default", options="") Double biexp_peak_cond,
        @ParamInfo(name="<html><br>"
                         + "Spherical localization parameters<br>"
                         + "<br> x</html>", style="default", options="") Double x,
        @ParamInfo(name="y", style="default", options="") Double y,
        @ParamInfo(name="z", style="default", options="") Double z,
        @ParamInfo(name="radius", style="default", options="") Double radius
    )
    {   
        Double[] alpha_syn_params = new Double[5];
        alpha_syn_params[0] = start_time;
        alpha_syn_params[1] = start_time_dev;
        alpha_syn_params[2] = duration;
        alpha_syn_params[3] = duration_dev;
        alpha_syn_params[4] = peak_cond;
        
        Double[] biexp_syn_params = new Double[7];
        biexp_syn_params[0] = biexp_onset_time;
        biexp_syn_params[1] = biexp_onset_time_dev;
        biexp_syn_params[2] = biexp_tau1_mean;
        biexp_syn_params[3] = biexp_tau1_dev;
        biexp_syn_params[4] = biexp_tau2_mean;
        biexp_syn_params[5] = biexp_tau2_dev;
        biexp_syn_params[6] = biexp_peak_cond;
        
        sd.set_activation_timing(alpha_syn_params, biexp_syn_params, x, y, z, radius);
    }
*/
    
    @MethodInfo(name="print total number of synapses")                                 
    public void print_total_num_synapses()
    {   
        VMessage.info("SynapseDistributionGenerator::print_total_num_synapses info",
					  sd.const__num_synapses().toString() + " synapses in total.");
        System.out.println("Done.");
    }
    
    @MethodInfo(name="print number of synapses in specified subset")                                 
    public void print_num_synapses_in_subset
	(
		@ParamInfo
		(
			name = "Select subset",
			style = "default",
			options = "ugx_globalTag=\"gridFile\";" + "type=\"s:Subset\""
		) UserDataTuple subset)
    {   
        String subsetName = subset.getSubset(0);
        VMessage.info("SynapseDistributionGenerator::print_num_synapses_in_subset info",
				      sd.const__num_synapses(subsetName).toString()
					  + " synapses in subset " + subsetName + ".");
    }

/*	The subset_length feature is no longer part of the Distributor.
	However, it can be found as a stand-alone function now.
	
    @MethodInfo(name="print length in [m] of specified subset")                                 
    public void print_subset_length(@ParamInfo(name="Select subset", 
                                               style="default", options="ugx_globalTag=\"gridFile\";" 
                                               + "type=\"s:Subset\"") UserDataTuple subset)
    {   
        String subsetName = subset.getSubset(0);
        VMessage.info("SynapseDistributionGenerator::print_subset_length info", "Length of subset " + subsetName + ": " + sd.get_subset_length(subsetName).toString() + " meter.");
    }
*/
	
    @MethodInfo(name="export grid to input grid path")                                 
    public void export_grid_to_input_grid_path(@ParamInfo(name="Output filename (specify with *.ugx)") String outfile)
    {   
        outfile = outpath + outfile;
		
    //  Catch export error
        if (!sd.export_grid(outfile))
        {
            VMessage.exception("SynapseDistributionGenerator::export_grid failed",
                               "The grid file ending must be '.ugx', did you consider this?");           
        }
        else
            VMessage.info("SynapseDistributionGenerator::export_grid info", "Geometry successfully written.");
    }
    
    @MethodInfo(name="export grid to")                                 
    public void export_grid_to(@ParamInfo(name="File name", style="save-dialog", options="tag=\"TheFile\"") java.io.File file)
    {   
        String filename = file.getAbsoluteFile().getAbsolutePath();
               
    //  Catch export error 
        if (!sd.export_grid(filename))
        {
            VMessage.exception("SynapseDistributionGenerator::export_grid failed",
                               "The grid file ending must be '.ugx', did you consider this?");                        
        }
        else
            VMessage.info("SynapseDistributionGenerator::export_grid_to info", "Geometry successfully written.");
    }
}

package edu.gcsc.vrl.neurobox;

import edu.gcsc.vrl.ug.api.I_NETISynapseHandler;
import edu.gcsc.vrl.ug.api.NETISynapseHandler;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.Serializable;

/**
 *
 * @author mbreit
 * @date 22-06-2015
 */

@ComponentInfo(name="Synapse handler", category="Neuro/cable")
public class SynapseHandler implements Serializable
{
    private static final long serialVersionUID = 1L;

    private transient I_NETISynapseHandler synHandler = null;
    
    /**
     * @return Hodgkin-Huxley channel object
     */
    @MethodInfo(name="create synapse handler", valueName="synapse handler", initializer=true, hide=false, interactive=false)
    public I_NETISynapseHandler create()
    {
        // construct new synapse handler object
        synHandler = new NETISynapseHandler();
        
        return synHandler;
    }
    
    @MethodInfo(name="set ALPHA synapse activation timing", interactive=false)
    public void set_alpha_synapse_activation_timing
    (
	/// start time
        @ParamInfo(name="start time [ms]", 
		   style="default", 
		   options="value=10; description=\"default: 10 ms\"") 
		   Double start_time,
	    
	/// duration
        @ParamInfo(name="duration [ms]", 
 		   style="default", 
		   options="value=2; description=\"default: 2 ms\"")
		   Double duration,
	
	/// std deviation of start time
        @ParamInfo(name="std deviation of start time [ms]", 
		   style="default", 
		   options="value=3; description=\"default: 3 ms\"") 
		   Double start_time_dev,
	
	/// std deviation of duration 
        @ParamInfo(name="std deviation of duration [ms]", 
		   style="default", 
		   options="value=1; description=\"default: 1 ms\"")
		   Double duration_dev,
	
	/// peak conductance
        @ParamInfo(name="peak conductance [uS]", 
		   style="default", 
		   options="value=0.000121; description=\"default: 1.2e-4 uS\"")
		   Double peak_cond
    )
    {
        check_syn_handler();
        synHandler.set_activation_timing(start_time, duration, start_time_dev, duration_dev, peak_cond);
    }
    
    @MethodInfo(name="set EXP2 synapse activation timing", interactive=false)
    public void set_exp2_synapse_activation_timing
    (
	/// average start time
        @ParamInfo(name="average start time [ms]", 
		   style="default", 
		   options="value=10; description=\"default: 10 ms\"") 
		   Double onset_mean,
	    
	/// tau1
        @ParamInfo(name="mean of tau1 [ms]", 
		   style="default",
		   options="value=0.7; description=\"0.7-1 ms for AMPA\"") 
		   Double tau1_mean,

	/// tau2
        @ParamInfo(name="mean of tau2 [ms]", 
		   style="default", 
		   options="value=5; description=\"5-8 ms for AMPA\"") 
		   Double tau2_mean,
	
	/// std deviation of start time 
        @ParamInfo(name="std deviation of start time [ms]", 
		   style="default", 
		   options="value=3; description=\"default: 3 ms\"") 
		   Double onset_dev,

	/// std deviation of tau1
        @ParamInfo(name="std deviation of tau1 [ms]", 
		   style="default", 
		   options="value=0.0; description=\"default 0\"") 
		   Double tau1_dev,
	
	/// std deviation of tau2
        @ParamInfo(name="std deviation of tau2 [ms]", 
		   style="default", 
		   options="value=0.0; description=\"default 0\"") 
		   Double tau2_dev,
	    
	/// peak conductance
        @ParamInfo(name="peak conductance [uS]", 
		   style="default", 
		   options="value=0.000121; description=\"default: 1.2e-4 uS\"")
		   Double peak_cond
    )
    {
        check_syn_handler();
        synHandler.set_activation_timing_biexp(onset_mean, tau1_mean, tau2_mean, onset_dev, tau1_dev, tau2_dev, peak_cond);
    }
    
    @MethodInfo(noGUI=true)
    private void check_syn_handler()
    {
        if (synHandler == null)
        {
            eu.mihosoft.vrl.system.VMessage.exception("Usage before initialization: ",
                "No method can be called on synapse handler object before it has been initialized"
                + "using the 'create()' method.");
        }
    }
}

package edu.gcsc.vrl.neurobox;

import edu.gcsc.vrl.ug.api.I_SynapseHandler;
import edu.gcsc.vrl.ug.api.SynapseHandler;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.Serializable;

/**
 *
 * @author mbreit
 * @date 22-06-2015
 */

@ComponentInfo(name="Synapse Handler", category="Neuro/cable")
public class SynapseHandler_VRL implements Serializable
{
    private static final long serialVersionUID = 1L;

    private transient I_SynapseHandler synHandler = null;
    
    /**
     * @return Hodgkin-Huxley channel object
     */
    @MethodInfo(name="create synapse handler", valueName="synapse handler", initializer=true, hide=false, interactive=false)
    public I_SynapseHandler create()
    {
        // construct new synapse handler object
        synHandler = new SynapseHandler();
        
        return synHandler;
    }
    
	
    @MethodInfo(name="set ALPHA synapse activation timing", interactive=false)
    public void set_alpha_synapse_activation_timing
    (
	/// start time
        @ParamInfo(name="mean onset [s]", 
		   style="default", 
		   options="value=0.01; description=\"default: 10 ms\"") 
		   Double onset,
	    
	/// duration
        @ParamInfo(name="mean tau [s]", 
 		   style="default", 
		   options="value=2e-3; description=\"default: 2 ms\"")
		   Double tau,
	
	/// peak conductance
        @ParamInfo(name="mean peak conductance [S]", 
		   style="default", 
		   options="value=1.2e-9; description=\"default: 1.2 nS\"")
		   Double peak_cond,
	
	/// std deviation of start time
        @ParamInfo(name="std deviation onset [s]", 
		   style="default", 
		   options="value=0.003; description=\"default: 3 ms\"") 
		   Double onset_dev,
	
	/// std deviation of duration 
        @ParamInfo(name="std deviation tau [s]", 
		   style="default", 
		   options="value=2e-4; description=\"default: 200 us\"")
		   Double tau_dev,
	
	/// std deviation of peak conductance
        @ParamInfo(name="std deviation peak conductance [S]", 
		   style="default", 
		   options="value=1e-10; description=\"default: 100 pS\"")
		   Double peak_cond_dev
    )
    {
        check_syn_handler();
        synHandler.set_activation_timing_alpha(onset, tau, peak_cond, onset_dev, tau_dev, peak_cond_dev, true);
    }
    
	
    @MethodInfo(name="set EXP2 synapse activation timing", interactive=false)
    public void set_exp2_synapse_activation_timing
    (
	/// average start time
        @ParamInfo(name="mean onset [s]", 
		   style="default", 
		   options="value=0.01; description=\"default: 10 ms\"") 
		   Double onset_mean,
	    
	/// tau1
        @ParamInfo(name="mean tau1 [s]", 
		   style="default",
		   options="value=7e-4; description=\"0.7-1 ms for AMPA\"") 
		   Double tau1_mean,

	/// tau2
        @ParamInfo(name="mean tau2 [s]", 
		   style="default", 
		   options="value=5e-3; description=\"5-8 ms for AMPA\"") 
		   Double tau2_mean,
	    
	/// peak conductance
        @ParamInfo(name="mean peak conductance [S]", 
		   style="default", 
		   options="value=1.2e-9; description=\"default: 1.2 nS\"")
		   Double peak_cond_mean,
	
	/// std deviation of start time 
        @ParamInfo(name="std deviation onset [s]", 
		   style="default", 
		   options="value=3e-3; description=\"default: 3 ms\"") 
		   Double onset_dev,

	/// std deviation of tau1
        @ParamInfo(name="std deviation tau1 [s]", 
		   style="default", 
		   options="value=0.0; description=\"default 0\"") 
		   Double tau1_dev,
	
	/// std deviation of tau2
        @ParamInfo(name="std deviation tau2 [s]", 
		   style="default", 
		   options="value=0.0; description=\"default 0\"") 
		   Double tau2_dev,
	
	/// std deviation of peak conductance
        @ParamInfo(name="std deviation peak conductance [S]", 
		   style="default", 
		   options="value=1e-10; description=\"default: 100 pS\"")
		   Double peak_cond_dev
    )
    {
        check_syn_handler();
        synHandler.set_activation_timing_biexp(onset_mean, tau1_mean, tau2_mean, peak_cond_mean, onset_dev, tau1_dev, tau2_dev, peak_cond_dev, true);
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

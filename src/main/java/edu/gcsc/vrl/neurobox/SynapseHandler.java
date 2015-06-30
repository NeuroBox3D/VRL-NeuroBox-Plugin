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
    
    @MethodInfo(name="set activation timing", interactive=false)
    public void set_activation_timing
    (
        @ParamInfo(name="start time", style="default", options="") Double start_time,
        @ParamInfo(name="duration", style="default", options="") Double duration,
        @ParamInfo(name="std deviation of start time", style="default", options="") Double start_time_dev,
        @ParamInfo(name="std deviation of duration", style="default", options="") Double duration_dev,
        @ParamInfo(name="peak conductivity", style="default", options="") Double peak_cond
    )
    {
        check_syn_handler();
        
        synHandler.set_activation_timing(start_time, duration, start_time_dev,
            duration_dev, peak_cond);
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

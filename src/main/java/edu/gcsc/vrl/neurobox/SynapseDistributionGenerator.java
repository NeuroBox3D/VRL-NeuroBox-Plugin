/**
 * SynapseDistributor.java
 * 
 * @date 2015-06-30
 * @author mstepnie
**/

package edu.gcsc.vrl.neurobox;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.system.VMessage;


@ComponentInfo(name="SynapseDistributionGenerator", category="Neuro")
public class SynapseDistributionGenerator implements java.io.Serializable{
    private static final long serialVersionUID = 1L;

    @MethodInfo(name="BuildBouton", valueName="file")
    public void DisributeSynapses(@ParamInfo(name="File name", style="load-dialog", options="tag=\"TheFile\"") java.io.File file,
                            @ParamInfo(name="Number of synapses to place", options="value=1000") int numSynapses,
                            @ParamInfo(name="Discrete distribution of synapses on subsets", style="array", options="minArraySize=0") Double[] distr) {
                              
        
        String fileName = file.getAbsoluteFile().getAbsolutePath();
       
        edu.gcsc.vrl.ug.api.I_SynapseDistributor sd = new edu.gcsc.vrl.ug.api.SynapseDistributor(fileName, fileName, true);
        
        sd.place_synapses(distr, numSynapses);
        sd.export_grid();
        
        if (!file.exists())
        {
            VMessage.exception("SynapseDistributionGenerator failed",
                "The geometry supposedly created by the synapse distribution generator can not be found.");           
        }               
    }
    
}

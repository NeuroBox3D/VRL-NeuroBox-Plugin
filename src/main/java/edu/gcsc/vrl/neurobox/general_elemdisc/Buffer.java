package edu.gcsc.vrl.neurobox.general_elemdisc;

import edu.gcsc.vrl.ug.api.BufferFV1;
import edu.gcsc.vrl.ug.api.I_BufferFV1;
import edu.gcsc.vrl.ug.api.I_CplUserNumber;
import edu.gcsc.vrl.userdata.UserDataTuple;
import edu.gcsc.vrl.userdata.UserDependentSubsetModel;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.Serializable;

/**
 *
 * @author mbreit
 * @date 24-10-2014
 */

@ComponentInfo(name="Buffering", category="Neuro")
public class Buffer implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     *
     * @param bufferingData
     * @return Buffering discretization objects
     */
    @MethodInfo(name="create buffer", valueName="Buffering ElemDisc", hide = false)
    public I_BufferFV1 createBuffers
    (
        @ParamInfo(name="", style="default", options="ugx_globalTag=\"gridFile\"; fct_tag=\"fctDef\"; minArraySize=1; type=\"S2|nnn:buffering substance, buffered substance, total buffer, association rate, dissociation rate\"")
        UserDataTuple bufferingData
    )
    {
        // get selected function and selected subsets
        UserDependentSubsetModel.FSDataType fctSsSel = (UserDependentSubsetModel.FSDataType) bufferingData.getData(0);
        String[] fcts = fctSsSel.getSelFct();
        if (fcts.length != 2) throw new RuntimeException("Buffering reaction needs exactly two functions, but has "+fcts.length+".");
        String[] ss = fctSsSel.getSelSs();
        String ssString = "";
        if (ss.length == 0) throw new RuntimeException("No subset definition in buffering reaction!");
        for (String s: ss) ssString = ssString + ", " + s;
        ssString = ssString.substring(2);

        // create elemDisc
        I_BufferFV1 bufferingDisc = new BufferFV1();
        bufferingDisc.constructor(ssString);

        bufferingDisc.add_reaction(fcts[0], fcts[1],
                        (I_CplUserNumber) bufferingData.getNumberData(1),
                        (I_CplUserNumber) bufferingData.getNumberData(2),
                        (I_CplUserNumber) bufferingData.getNumberData(3));
        
        return bufferingDisc;
    }
}

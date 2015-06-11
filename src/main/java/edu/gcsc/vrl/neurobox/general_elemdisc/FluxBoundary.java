package edu.gcsc.vrl.neurobox.general_elemdisc;

import edu.gcsc.vrl.ug.api.I_CplUserNumber;
import edu.gcsc.vrl.ug.api.I_UserFluxBoundaryFV1;
import edu.gcsc.vrl.ug.api.UserFluxBoundaryFV1;
import edu.gcsc.vrl.userdata.UserDataTuple;
import edu.gcsc.vrl.userdata.UserDependentSubsetModel;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.Serializable;

/**
 *
 * @author mbreit
 * @date 23-10-2014
 */

@ComponentInfo(name="FluxBoundary", category="Neuro")
public class FluxBoundary implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     *
     * @param bndData
     * @return Flux boundary discretization object
     */
    @MethodInfo(name="create flux bnd", valueName="FluxBoundary ElemDisc", hide = false)
   public I_UserFluxBoundaryFV1 createFluxBoundaries
    (
        @ParamInfo(name="Neumann Boundarie", style="array", options="ugx_globalTag=\"gridFile\"; fct_tag=\"fctDef\"; minArraySize=0; type=\"S1|n:function & subset, value\"")
        UserDataTuple bndData
    )
    {
        String[] bndFct = ((UserDependentSubsetModel.FSDataType) bndData.getData(0)).getSelFct();
        if (bndFct.length != 1) throw new RuntimeException("Definition of Neumann boundary condition needs exactly one function, but has "+bndFct.length+".");

        String[] bndSelSs = ((UserDependentSubsetModel.FSDataType) bndData.getData(0)).getSelSs();
        String bndSsString = "";
        if (bndSelSs.length == 0) throw new RuntimeException("No subset selected in Neumann boundary definition!");
        for (String s: bndSelSs) bndSsString = bndSsString + ", " + s;
        bndSsString = bndSsString.substring(2);

        I_UserFluxBoundaryFV1 neumannDisc = new UserFluxBoundaryFV1(bndFct[0], bndSsString);
        neumannDisc.set_flux_function((I_CplUserNumber) bndData.getNumberData(1));
        
        return neumannDisc;
    }
}

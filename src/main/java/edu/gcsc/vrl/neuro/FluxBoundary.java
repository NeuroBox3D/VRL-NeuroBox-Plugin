package edu.gcsc.vrl.neuro;

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
     * @return Flux boundary discretization objects
     */
    @MethodInfo(name="create flux bnds", valueName="FluxBoundary ElemDiscs", interactive = false)
   public I_UserFluxBoundaryFV1[] createFluxBoundaries
    (
        @ParamInfo(name="Neumann Boundaries", style="array", options="ugx_globalTag=\"gridFile\"; fct_tag=\"fctDef\"; minArraySize=0; type=\"S1|n:function & subset, value\"")
        UserDataTuple[] bndData
    )
    {
        int i = 0;
        I_UserFluxBoundaryFV1[] neumannDisc = new UserFluxBoundaryFV1[bndData.length];
        for (UserDataTuple bnd : bndData)
        {
            String[] bndFct = ((UserDependentSubsetModel.FSDataType) bnd.getData(0)).getSelFct();
            if (bndFct.length != 1) throw new RuntimeException("Definition of Neumann boundary condition "+i+" needs exactly one function, but has "+bndFct.length+".");
            
            String[] bndSelSs = ((UserDependentSubsetModel.FSDataType) bnd.getData(0)).getSelSs();
            String bndSsString = "";
            if (bndSelSs.length == 0) throw new RuntimeException("No subset selected in Neumann boundary definition "+ i +"!");
            for (String s: bndSelSs) bndSsString = bndSsString + ", " + s;
            bndSsString = bndSsString.substring(2);
            
            neumannDisc[i] = new UserFluxBoundaryFV1(bndFct[0], bndSsString);
            neumannDisc[i].set_flux_function((I_CplUserNumber) bnd.getNumberData(1));
            
            i++;
        }
        
        return neumannDisc;
    }
}

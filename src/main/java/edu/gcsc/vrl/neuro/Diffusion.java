package edu.gcsc.vrl.neuro;

import edu.gcsc.vrl.ug.api.ConvectionDiffusionFV1;
import edu.gcsc.vrl.ug.api.I_ConvectionDiffusionFV1;
import edu.gcsc.vrl.ug.api.I_CplUserMatrix;
import edu.gcsc.vrl.ug.api.I_CplUserNumber;
import edu.gcsc.vrl.ug.api.I_IConvectionShapes;
import edu.gcsc.vrl.ug.api.NoUpwind;
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

@ComponentInfo(name="Diffusion", category="Neuro")
public class Diffusion implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     *
     * @param diffusionData
     * @return diffusion discretization objects
     */
    @MethodInfo(name="create diffusion", valueName="Diffusion ElemDiscs", interactive = false)
    public I_ConvectionDiffusionFV1[] createDiffusion
    (
        @ParamInfo(name="", style="array", options="ugx_globalTag=\"gridFile\"; fct_tag=\"fctDef\"; minArraySize=1; type=\"S1|mnn:function & subset, diffusion, reaction rate, reaction term\"")
        UserDataTuple[] diffusionData
    )
    {
        I_ConvectionDiffusionFV1[] diffusionDisc = new ConvectionDiffusionFV1[diffusionData.length];
        for (int i = 0; i < diffusionData.length; i++)
        { 
            // get selected function and selected subsets
            UserDependentSubsetModel.FSDataType fctSsSel = (UserDependentSubsetModel.FSDataType) diffusionData[i].getData(0);
            if (fctSsSel.getSelFct().length != 1) throw new RuntimeException("Diffusion process "+i+" needs exactly one function, but has "+fctSsSel.getSelFct().length+".");
            String fct = fctSsSel.getSelFct()[0];
            String[] ss = fctSsSel.getSelSs();
            String ssString = "";
            if (ss.length == 0) throw new RuntimeException("No subset definition for function '"+fct+"' in diffusion process "+i+"!");
            for (String s: ss) ssString = ssString + ", " + s;
            ssString = ssString.substring(2);
            
            // create elemDisc
            diffusionDisc[i] = new ConvectionDiffusionFV1();
            diffusionDisc[i].constructor(fct, ssString);

            // upwinding not needed, no convection
            I_IConvectionShapes upwind = new NoUpwind();
            diffusionDisc[i].set_upwind(upwind);
            
            // get parameters for diffusion / reaction
            I_CplUserMatrix diffTensor = (I_CplUserMatrix) diffusionData[i].getMatrixData(1);
            I_CplUserNumber reactionRate = (I_CplUserNumber) diffusionData[i].getNumberData(2);
            I_CplUserNumber reactionTerm = (I_CplUserNumber) diffusionData[i].getNumberData(3);
            
            diffusionDisc[i].set_diffusion(diffTensor);
            diffusionDisc[i].set_reaction_rate(reactionRate);
            diffusionDisc[i].set_reaction(reactionTerm);
        }
        
        return diffusionDisc;
    }
}

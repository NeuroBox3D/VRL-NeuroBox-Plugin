/**
 * Output controller
 * 
 * This class manages output for time-dependent simulations.
 * It allows setting up
 * (a) solution output to vtk files,
 * (b) measurement (averaged over specified subsets) output to csv files.
 *  
 * @date 2016-05-31
 * @author mbreit
**/

package edu.gcsc.vrl.neurobox.control;

import edu.gcsc.vrl.ug.api.F_Take_measurement;
import edu.gcsc.vrl.ug.api.I_GridFunction;
import edu.gcsc.vrl.ug.api.I_VTKOutput;
import edu.gcsc.vrl.ug.api.VTKOutput;
import edu.gcsc.vrl.userdata.UserDataTuple;
import edu.gcsc.vrl.userdata.UserDependentSubsetModel;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ObjectInfo;
import eu.mihosoft.vrl.annotation.ParamGroupInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@ComponentInfo(name="OutputController", category="Neuro")
@ObjectInfo(instances = 1)
public class OutputController implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private String path;            // output path
    
    private transient List<String> measFct;   // functions for measurement cmd
    private transient List<String> measSs;    // subsets for measurement cmd
    
    private transient I_VTKOutput vtkOut;     // vtk output object
    private transient double vtk_plotStep;    // plot interval for vtk output
    
    
    
    @MethodInfo(name="output options", hide = false, interactive = false)
    public void defineOutputOptions
    (
        @ParamInfo(name="Output path", style="save-folder-dialog")
        String outputPath,

        // measurements
        @ParamGroupInfo(group="Measurements|false")
        @ParamInfo(name="Integration Subset", style="array", options="fct_tag=\"fctDef\"; minArraySize=0; type=\"S1:function & subset\"")
        UserDataTuple[] meas,

        // plotting
        @ParamGroupInfo(group="VTK|false")
        @ParamInfo(name="do plot")
        boolean generateVTKoutput,

        @ParamGroupInfo(group="VTK|false")
        @ParamInfo(name="plotting step", style="default", options="value=0.01")
        double plotStep
    )
    {
        // append path separator to output path
        path = outputPath + File.separatorChar;
        
        // measurements
        measFct = new ArrayList<String>();
        measSs = new ArrayList<String>();
        int cntUDT = 0;
        for (UserDataTuple udt: meas)
        {
            // get function to interpolate for
            String[] selFct = ((UserDependentSubsetModel.FSDataType) udt.getData(0)).getSelFct();
            if (selFct.length != 1) throw new RuntimeException("Measurement definition needs exactly one function at a time, but has "+selFct.length+".");
            measFct.add(selFct[0]);

            // get subsets to interpolate for
            String[] selSs = ((UserDependentSubsetModel.FSDataType) udt.getData(0)).getSelSs();
            String ssString = "";
            if (selSs.length == 0) throw new RuntimeException("No subset selection in measurement definition "+cntUDT+".");
            for (String s: selSs) ssString = ssString + ", " + s;
            measSs.add(ssString.substring(2));
            
            cntUDT++;
        }
        
        // VTK output
        vtkOut = null;
        if (generateVTKoutput) vtkOut = new VTKOutput();
        vtk_plotStep = plotStep;
        
    }
    
    @MethodInfo(noGUI = true)
    public void initiate(I_GridFunction u, double time)
    {
        if (vtkOut != null)
        {
            // create vtk subfolder if needed
            new File(path + "vtk" + File.separatorChar).mkdirs();
            
            // output
            vtkOut.print(path + "vtk" + File.separatorChar + "result", u,
                         (int) Math.floor(time/vtk_plotStep+0.5), time);
        }
        
        // create meas subfolder if needed
        if (measFct.size() > 0)
            new File(path + "meas" + File.separatorChar).mkdirs();
            
        // take first measurement
        for (int i=0; i<measFct.size(); i++)
        {
            F_Take_measurement.invoke(u, time, measSs.get(i), measFct.get(i),
                path + "meas" + File.separatorChar + "data");
        }
    }
    
    @MethodInfo(noGUI = true)
    public void step(I_GridFunction u, double time)
    {
        if (vtkOut != null)
        {
            if (Math.abs(time/vtk_plotStep - Math.floor(time/vtk_plotStep+0.5)) < 1e-5)
                vtkOut.print(path + "vtk"+ File.separatorChar + "result", u,
                    (int) Math.floor(time/vtk_plotStep+0.5), time);
        }

        // take measurement every timeStep seconds
        for (int i=0; i<measFct.size(); i++)
        {
            F_Take_measurement.invoke(u, time, measSs.get(i),
                measFct.get(i), path + "meas/data");
        }
    }
    
    @MethodInfo(noGUI = true)
    public void terminate(I_GridFunction u, double time)
    {
        if (vtkOut != null)
            vtkOut.write_time_pvd(path + "vtk" + File.separatorChar + "result", u);
    }
}

package edu.gcsc.vrl.neurobox.control;

import edu.gcsc.vrl.ug.api.AssembledLinearOperator;
import edu.gcsc.vrl.ug.api.CableAssTuner;
import edu.gcsc.vrl.ug.api.CompositeConvCheck;
import edu.gcsc.vrl.ug.api.F_ApplyLinearSolver;
import edu.gcsc.vrl.ug.api.F_AssembleLinearOperatorRhsAndSolution;
import edu.gcsc.vrl.ug.api.F_Interpolate;
import edu.gcsc.vrl.ug.api.F_Print;
import edu.gcsc.vrl.ug.api.F_VecScaleAssign;
import edu.gcsc.vrl.ug.api.GridFunction;
import edu.gcsc.vrl.ug.api.ILU;
import edu.gcsc.vrl.ug.api.I_ApproximationSpace;
import edu.gcsc.vrl.ug.api.I_AssTuner;
import edu.gcsc.vrl.ug.api.I_AssembledLinearOperator;
import edu.gcsc.vrl.ug.api.I_CableAssTuner;
import edu.gcsc.vrl.ug.api.I_CompositeConvCheck;
import edu.gcsc.vrl.ug.api.I_CplUserNumber;
import edu.gcsc.vrl.ug.api.I_DomainDiscretization;
import edu.gcsc.vrl.ug.api.I_GridFunction;
import edu.gcsc.vrl.ug.api.I_ILU;
import edu.gcsc.vrl.ug.api.I_LinearSolver;
import edu.gcsc.vrl.ug.api.I_SolutionTimeSeries;
import edu.gcsc.vrl.ug.api.I_ThetaTimeStep;
import edu.gcsc.vrl.ug.api.I_VTKOutput;
import edu.gcsc.vrl.ug.api.I_Vector;
import edu.gcsc.vrl.ug.api.LinearSolver;
import edu.gcsc.vrl.ug.api.SolutionTimeSeries;
import edu.gcsc.vrl.ug.api.ThetaTimeStep;
import edu.gcsc.vrl.ug.api.VTKOutput;
import edu.gcsc.vrl.userdata.UserDataTuple;
import edu.gcsc.vrl.userdata.UserDependentSubsetModel;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ObjectInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamGroupInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.File;
import java.io.Serializable;

/**
 *
 * @author mbreit
 */
@ComponentInfo(name="CableSolver", category="Neuro/cable")
@ObjectInfo(instances = 1)
public class CableSolver implements Serializable
{
    private static final long serialVersionUID = 1L;
    private boolean stopSolver;
    
    /**
     *
     * @param domainDisc
     * @param approxSpace
     * @param initValues
     * @param timeStart
     * @param timeEnd
     * @param timeStepStart
     * @param stepReductionFactor
     * @param minStepSize
     * @param maxNumIterLinear
     * @param minRedLinear
     * @param absTolLinear
     * @param outputPath
     * @param generateVTKoutput
     * @param plotStep
     * @return
     */
    @MethodInfo(valueStyle="multi-out", interactive = false)
    @OutputInfo
    (
      style="multi-out",
      elemNames = {"PVD File"},
      elemTypes = {File.class}
    )
    public Object[] invoke
    (
        @ParamGroupInfo(group="Problem Setup|false")
        @ParamInfo(name="Domain Disc", style="default")
        I_DomainDiscretization domainDisc,
        
        @ParamGroupInfo(group="Problem Setup|false")
        @ParamInfo(name="Approximation Space", style="default")
        I_ApproximationSpace approxSpace,
        
        @ParamGroupInfo(group="Problem Setup|false")
        @ParamInfo(name="Initial Solution", style="default")
        UserDataTuple[] initValues,        
        
        // Time stepping params
        @ParamGroupInfo(group="Time stepping|false")
        @ParamInfo(name="start time", style="default", options="value=0.00D")
        double timeStart,
        
        @ParamGroupInfo(group="Time stepping|false")
        @ParamInfo(name="end time", style="default", options="value=1.0D")
        double timeEnd,
        
        @ParamGroupInfo(group="Time stepping|false")
        @ParamInfo(name="start with time step", style="default", options="value=0.01")
        double timeStepStart,
        
        @ParamGroupInfo(group="Time stepping|false")
        @ParamInfo(name="time step reduction factor", style="default", options="value=0.5D")
        double stepReductionFactor,
        
        @ParamGroupInfo(group="Time stepping|false")
        @ParamInfo(name="minimal time step", style="default", options="value=0.00001D")
        double minStepSize,
        
        @ParamGroupInfo(group="Solver setup|false")
        @ParamInfo(name="maximal #iterations", style="default", options="value=20")
        int maxNumIterLinear,
        
        @ParamGroupInfo(group="Solver setup|false")
        @ParamInfo(name="minimal defect reduction", style="default", options="value=1E-10")
        double minRedLinear,
        
        @ParamGroupInfo(group="Solver setup|false")
        @ParamInfo(name="minimal residual norm", style="default", options="value=1E-21")
        double absTolLinear,
        
        // OUTPUT
        @ParamGroupInfo(group="Output|false")
        @ParamInfo(name="Output path", style="save-folder-dialog")
        String outputPath,
        
        // plotting
        @ParamGroupInfo(group="Output|false; VTK|false")
        @ParamInfo(name="do plot")
        boolean generateVTKoutput,
        
        @ParamGroupInfo(group="Output|false; VTK|false")
        @ParamInfo(name="plotting step", style="default", options="value=0.01")
        double plotStep
    )
    {
        // set abortion flag to false initially (can be changed using stopSolver-Method)
        stopSolver = false;
        
        
        //// ass tuners ////
        I_AssTuner assTuner = domainDisc.ass_tuner();

        I_CableAssTuner cableAssTuner = new CableAssTuner(domainDisc, approxSpace);
        cableAssTuner.remove_ghosts_from_assembling_iterator();
        
        
        //// linear operator ////
   
        // create time discretization (impl Euler)
        I_ThetaTimeStep timeDisc = new ThetaTimeStep(domainDisc);
        timeDisc.set_theta(1.0);

        // create operator from discretization
        I_AssembledLinearOperator op = new AssembledLinearOperator(timeDisc);
        
        
        
        //// solver ////
        
        // create Convergence Check for linear solver
        I_CompositeConvCheck convCheckLinear =
            new CompositeConvCheck(approxSpace, maxNumIterLinear, absTolLinear, minRedLinear);
        convCheckLinear.set_verbose(true);
        convCheckLinear.set_time_measurement(true);
        
        I_ILU ilu = new ILU();
        I_LinearSolver ls = new LinearSolver();
        ls.set_preconditioner(ilu);
        ls.set_convergence_check(convCheckLinear);
        
        
        
        //// I/O ////
        
        // append path separator to output path
        outputPath = outputPath + File.separator;
        
        // VTK output
        I_VTKOutput vtkOut = null;
        if (generateVTKoutput) vtkOut = new VTKOutput();
        
        
        
        //// simulation ////
        
        // start
        double time = timeStart;
        
        // initialize solution
        // get grid function
        F_Print.invoke("Initializing solution.\n");
        I_GridFunction u = new GridFunction(approxSpace);
        I_GridFunction b = new GridFunction(approxSpace);
        
        // interpolate start values
        int cntUDT = 0;
        for (UserDataTuple udt: initValues)
        {
            // get function to interpolate for
            String[] selFct = ((UserDependentSubsetModel.FSDataType) udt.getData(0)).getSelFct();
            if (selFct.length != 1) throw new RuntimeException("Start value definition needs exactly one function at a time, but has "+selFct.length+".");
            String fct = selFct[0];

            // get subsets to interpolate for
            String[] selSs = ((UserDependentSubsetModel.FSDataType) udt.getData(0)).getSelSs();
            String ssString = "";
            if (selSs.length == 0) throw new RuntimeException("No subset selection in start value definition "+cntUDT+".");
            for (String s: selSs) ssString = ssString + ", " + s;
            ssString = ssString.substring(2);
            
            // get start value
            I_CplUserNumber value = (I_CplUserNumber) udt.getNumberData(1);
            
            // interpolate grid function for time
            F_Interpolate.invoke(value, u, fct, ssString, time);
            
            cntUDT++;
        }
        
        // write initial solution to vtk file
        if (vtkOut != null)
        {
            // create vtk subfolder if needed
            new File(outputPath + "vtk" + File.separator).mkdirs();
            
            // output
            vtkOut.print(outputPath + "vtk" + File.separator + "result", u, (int) Math.floor(time/plotStep+0.5), time);
        }
        
        // create new grid function for old value
        I_GridFunction uOld = u.const__clone();
        
        // store grid function in vector of old solutions
        I_SolutionTimeSeries solTimeSeries = new SolutionTimeSeries();
        solTimeSeries.push(uOld, time);
        
        
        // computations for time stepping
        double dt = timeStepStart;
        
        int LowLv =  (int) Math.ceil(log2(timeStepStart/minStepSize));
        if (LowLv < 0) errorExit("Initial time step is smaller than minimal time step.");
        double minStepSizeNew = timeStepStart / Math.pow(2, LowLv);
        minStepSize = minStepSizeNew;
        
        int checkbackInterval = 10;
        int lv = 0;
        int[] checkbackCounter = new int[LowLv+1];
        for (int i=0; i<checkbackCounter.length; i++) checkbackCounter[i] = 0;
        
        
        // begin simulation loop
        boolean dtChanged = true;
        while (((int)Math.floor(time/dt+0.5))*dt < timeEnd)
        {
            F_Print.invoke("++++++ POINT IN TIME  " + ((int)Math.floor((time+dt)/dt+0.5))*dt + "  BEGIN ++++++\n");

            // setup time disc for old solutions and timestep
            timeDisc.prepare_step(solTimeSeries, dt);

            assTuner.set_matrix_is_const(!dtChanged);
            try {F_AssembleLinearOperatorRhsAndSolution.invoke(op, u, b);} 
            catch (Exception e) {errorExit("Could not assemble operator:" + e.getMessage());}
            
            // apply Newton solver
            ilu.set_disable_preprocessing(!dtChanged);
            if (!F_ApplyLinearSolver.invoke(op, u, b, ls))
            {
                // in case of failure:
                F_Print.invoke("Solver failed at point in time "
                               + ((int)Math.floor((time+dt)/dt+0.5))*dt
                               + " with time step " + dt + ".");

                dt = dt/2;
                dtChanged = true;
                lv = lv++;
                F_VecScaleAssign.invoke(u, 1.0, solTimeSeries.latest());

                // halve time step and try again unless time step below minimum
                if (dt < minStepSize)
                {
                    F_Print.invoke("Time step below minimum. Aborting. Failed at point in time "
                            + ((int)Math.floor((time+dt)/dt+0.5))*dt + ".\n");
                    
                    if (vtkOut != null) vtkOut.write_time_pvd(outputPath + "vtk" + File.separator + "result", u);
                    
                    errorExit("Newton solver failed at point in time "
                        + ((int)Math.floor((time+dt)/dt+0.5))*dt + ".");
                }
                else
                {    
                    F_Print.invoke("Trying with half the time step...\n");
                    checkbackCounter[lv] = 0;
                }
                
                if (stopSolver)
                {
                    F_Print.invoke("\n -------- solver stopped by external stop command --------\n");
                    break;
                }
            }
            else
            {
                dtChanged = false;
                    
                // update new time
                time = solTimeSeries.const__time(0) + dt;

                // update checkback counter and if applicable, reset dt
                checkbackCounter[lv]++;
                while (checkbackCounter[lv] % (2*checkbackInterval) == 0 && lv > 0)
                {
                    F_Print.invoke("Doubling time due to continuing convergence; now: " + 2*dt + "\n");
                    dt = 2*dt;
                    dtChanged = true;
                    lv--;
                    checkbackCounter[lv] = checkbackCounter[lv] + checkbackCounter[lv+1] / 2;
                    checkbackCounter[lv+1] = 0;
                }

                // plot solution every plotStep seconds
                if (vtkOut != null)
                {
                    if (Math.abs(time/plotStep - Math.floor(time/plotStep+0.5)) < 1e-5)
                        vtkOut.print(outputPath + "vtk" + File.separator + "result", u, (int) Math.floor(time/plotStep+0.5), time);
                }

                // get oldest solution
                I_Vector oldestSol = solTimeSeries.oldest();

                // copy values into oldest solution (we reuse the memory here)
                F_VecScaleAssign.invoke(oldestSol, 1.0, u);

                // push oldest solutions with new values to front, oldest sol pointer is popped from end
                solTimeSeries.push_discard_oldest(oldestSol, time);

                F_Print.invoke("++++++ POINT IN TIME  " + ((int)Math.floor(time/dt+0.5))*dt + "  END ++++++++\n");
                
                if (stopSolver)
                {
                    F_Print.invoke("\n -------- solver stopped by external stop command --------\n");
                    break;
                }
            }
        }
        
        // end timeseries, produce gathering file
        if (vtkOut != null) vtkOut.write_time_pvd(outputPath + "vtk/result", u);
        
        if (generateVTKoutput) return new Object[]{new File(outputPath + "vtk/result.pvd")};
        
        return new Object[]{null};
    }

    @MethodInfo(name=" ", buttonText="stop time stepping", hideCloseIcon=true)
    public void stopSolver()
    {
        stopSolver=true;
    }
   
    private void errorExit(String s)
    {
        eu.mihosoft.vrl.system.VMessage.exception("Error in CableSolver: ", s);
    }

    private double log2(double x)
    {
	return Math.log(x)/Math.log(2.0);
    }
    

}

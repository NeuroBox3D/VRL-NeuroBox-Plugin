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
import edu.gcsc.vrl.ug.api.I_CableEquation;
import edu.gcsc.vrl.ug.api.I_CompositeConvCheck;
import edu.gcsc.vrl.ug.api.I_CplUserNumber;
import edu.gcsc.vrl.ug.api.I_DomainDiscretization;
import edu.gcsc.vrl.ug.api.I_GridFunction;
import edu.gcsc.vrl.ug.api.I_ILU;
import edu.gcsc.vrl.ug.api.I_LinearSolver;
import edu.gcsc.vrl.ug.api.I_SolutionTimeSeries;
import edu.gcsc.vrl.ug.api.I_ThetaTimeStep;
import edu.gcsc.vrl.ug.api.I_Vector;
import edu.gcsc.vrl.ug.api.LinearSolver;
import edu.gcsc.vrl.ug.api.SolutionTimeSeries;
import edu.gcsc.vrl.ug.api.ThetaTimeStep;
import edu.gcsc.vrl.userdata.UserDataTuple;
import edu.gcsc.vrl.userdata.UserDependentSubsetModel;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ObjectInfo;
import eu.mihosoft.vrl.annotation.ParamGroupInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
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
     * @param cableDisc
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
     * @param outputCtrl
     */
    @MethodInfo(interactive = false)
    public void invoke
    (
        @ParamGroupInfo(group="Problem Setup|false")
        @ParamInfo(name="Cable Disc", style="default")
        I_CableEquation cableDisc,
            
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
        @ParamInfo(name="end time", style="default", options="value=1e-3")
        double timeEnd,
        
        @ParamGroupInfo(group="Time stepping|false")
        @ParamInfo(name="start with time step", style="default", options="value=2e-5")
        double timeStepStart,
        
        @ParamGroupInfo(group="Time stepping|false")
        @ParamInfo(name="time step reduction factor", style="default", options="value=2")
        int stepReductionFactor,
        
        @ParamGroupInfo(group="Time stepping|false")
        @ParamInfo(name="minimal time step", style="default", options="value=1e-8")
        double minStepSize,
        
        @ParamGroupInfo(group="Solver setup|false")
        @ParamInfo(name="maximal #iterations", style="default", options="value=10")
        int maxNumIterLinear,
        
        @ParamGroupInfo(group="Solver setup|false")
        @ParamInfo(name="minimal defect reduction", style="default", options="value=1E-12")
        double minRedLinear,
        
        @ParamGroupInfo(group="Solver setup|false")
        @ParamInfo(name="minimal residual norm", style="default", options="value=1E-21")
        double absTolLinear,
        
        // OUTPUT
        @ParamGroupInfo(group="Output|false")
        @ParamInfo(name="Output controller", style="default", nullIsValid=true)
        OutputController outputCtrl
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
        if (outputCtrl != null)
            outputCtrl.initiate(u, time);
        
        
        // create new grid function for old value
        I_GridFunction uOld = u.const__clone();
        
        // store grid function in vector of old solutions
        I_SolutionTimeSeries solTimeSeries = new SolutionTimeSeries();
        solTimeSeries.push(uOld, time);
        
        
        // computations for time stepping
        int LowLv =  (int) Math.ceil(Math.log(timeStepStart/minStepSize)
                                     / Math.log(stepReductionFactor));
        if (LowLv < 0) errorExit("Initial time step is smaller than minimal time step.");
        int lv = 0;
        int[] checkbackCounter = new int[LowLv+1];
        for (int i=0; i<checkbackCounter.length; i++) checkbackCounter[i] = 0;
        
        
        // begin simulation loop
        double curr_dt = timeStepStart;
        boolean dtChanged = true;
        while (timeEnd - time > 0.001*curr_dt)
        {
            // setup time Disc for old solutions and timestep
            timeDisc.prepare_step(solTimeSeries, curr_dt);
	
            // reduce time step if cfl < curr_dt
            // (this needs to be done AFTER prepare_step as channels are updated there)
            double cfl = cableDisc.estimate_cfl_cond(solTimeSeries.latest());
            //F_Print.invoke("estimated CFL condition: dt < " + cfl);
            while (curr_dt > cfl)
            {
                if (lv == LowLv)
                    errorExit("Required time step smaller than specified minimum.");
                    
		curr_dt = curr_dt/stepReductionFactor;
		++lv;
		checkbackCounter[lv] = 0;
		//F_Print.invoke("estimated CFL condition: dt < " + cfl
                //               + " - reducing time step to " + curr_dt);
		dtChanged = true;
            }
	
            // increase time step if cfl > curr_dt / dtred
            // (and if time is aligned with new bigger step size)
            while (curr_dt*stepReductionFactor < cfl
                   && lv > 0
                   && checkbackCounter[lv] % stepReductionFactor == 0)
            {
		curr_dt = curr_dt*stepReductionFactor;
		--lv;
		checkbackCounter[lv] += checkbackCounter[lv+1] / stepReductionFactor;
		checkbackCounter[lv+1] = 0;
		//F_Print.invoke("estimated CFL condition: dt < " + cfl
                //                 + " - increasing time step to " + curr_dt);
		dtChanged = true;
            }
	
            F_Print.invoke("++++++ POINT IN TIME " + ((int)Math.floor((time+curr_dt)/curr_dt+0.5))*curr_dt + " BEGIN ++++++");

            // prepare again with new time step size (if needed)
            if (dtChanged) timeDisc.prepare_step(solTimeSeries, curr_dt);
            
            // assemble linear problem
            assTuner.set_matrix_is_const(!dtChanged);
            try {F_AssembleLinearOperatorRhsAndSolution.invoke(op, u, b);} 
            catch (Exception e)
            {
                if (outputCtrl != null)
                    outputCtrl.terminate(u, time);
                
                errorExit("Could not assemble operator:" + e.getMessage());
            }
            
            // apply Newton solver
            ilu.set_disable_preprocessing(!dtChanged);
            if (!F_ApplyLinearSolver.invoke(op, u, b, ls))
            {
                if (outputCtrl != null)
                    outputCtrl.terminate(u, time);
                
                errorExit("Could not apply linear solver.");
            }
            
            // update new time
            time = solTimeSeries.const__time(0) + curr_dt;
            dtChanged = false;
            
            // increment check-back counter
            checkbackCounter[lv]++;
            
            // output
            if (outputCtrl != null)
                outputCtrl.step(u, time);

            // get oldest solution
            I_Vector oldestSol = solTimeSeries.oldest();

            // copy values into oldest solution (we reuse the memory here)
            F_VecScaleAssign.invoke(oldestSol, 1.0, u);

            // push oldest solutions with new values to front, oldest sol pointer is popped from end
            solTimeSeries.push_discard_oldest(oldestSol, time);

            F_Print.invoke("++++++ POINT IN TIME  " + ((int)Math.floor(time/curr_dt+0.5))*curr_dt + "  END ++++++++\n");

            if (stopSolver)
            {
                F_Print.invoke("\n -------- solver stopped by external stop command --------\n");
                break;
            }
        }
        
         if (outputCtrl != null)
            outputCtrl.terminate(u, time);
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

}

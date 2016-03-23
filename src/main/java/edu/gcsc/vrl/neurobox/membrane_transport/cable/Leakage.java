/// package's name
package edu.gcsc.vrl.neurobox.membrane_transport.cable;

/// imports
import edu.gcsc.vrl.ug.api.ChannelLeak;
import edu.gcsc.vrl.ug.api.Const__I_ConstUserNumber;
import edu.gcsc.vrl.ug.api.I_ChannelLeak;
import edu.gcsc.vrl.ug.api.I_ConstUserNumber;
import edu.gcsc.vrl.userdata.UserDataTuple;
import edu.gcsc.vrl.userdata.UserDependentSubsetModel;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Leakage mechanism This is a VRL visualization for any leakage term in a 1D
 * cable equation.
 *
 * @author mbreit
 * @date 30-06-2015
 */
@ComponentInfo(name = "Leakage", category = "Neuro/cable", description = "leakage mechanism in a 1D cable equation")
public class Leakage implements Serializable {

	private static final long serialVersionUID = 1L;

	private transient I_ChannelLeak leak = null;

	private transient ArrayList<String> selectedSubsets; /// remember leakage channels

	/**
	 *
	 * @param leakData
	 * @return leakage mechanism object
	 */
	@MethodInfo(name = "create leak", valueName = "leak", initializer = true, hide = false, interactive = false)
	public I_ChannelLeak create(
		@ParamInfo(name = "", style = "default", options = "ugx_globalTag=\"gridFile\"; fct_tag=\"fctDef\"; type=\"S1:membrane potential\"") UserDataTuple leakData
	) {
		String[] selFct = ((UserDependentSubsetModel.FSDataType) leakData.getData(0)).getSelFct();
		if (selFct.length != 1) {
			throw new RuntimeException("Leakage mechanism needs exactly one function, but has " + selFct.length + ".");
		}

		String[] selSs = ((UserDependentSubsetModel.FSDataType) leakData.getData(0)).getSelSs();
		if (selSs.length == 0) {
			throw new RuntimeException("No subset definition in leakage mechanism definition!");
		}

		// construct leakage object
		leak = new ChannelLeak(selFct, selSs);

		// remember the subsets we installed leakage channels on
                selectedSubsets = new ArrayList<String>();
		selectedSubsets.addAll(Arrays.asList(selSs));

		return leak;
	}

	@MethodInfo(name = "set reversal potential global", interactive = false)
	public void set_reversal_potential_global(
		@ParamInfo(name = "leak rev pot [V]", style = "default", options = "value=-0.065") double revPotLeak
	) {
		check_leak_exists();

		leak.set_rev_pot(revPotLeak);
	}

	/**
	 * @brief supply a glob string to set reversal potential for subsets
	 * This will select by a globbing some subsets matching a given pattern,
	 * then will set the same reversal potential for all subsets in this group
	 * if and only if the channel has actually been installed on the subset
	 * @author sgrein
	 * @param revPotData 
	 */
	@MethodInfo(name = "set reversal potential glob", interactive = false)
	public void set_reversal_potential_glob(
		@ParamInfo(name = " ", 
			style = "array", 
			options = "ugx_globalTag=\"gridFile\"; type=\"s|n:subset glob,rev. pot. [V]\"") 
			UserDataTuple[] revPotData
	) {
		check_leak_exists();

		for (int i = 0; i < revPotData.length; ++i) {

			for (UserDataTuple rPD : revPotData) {
				String subsetGlobString = rPD.getSubset(0);
				for (String subset : selectedSubsets) {
					if (subset.matches(subsetGlobString)) {
						if (!(revPotData[i].getNumberData(1) instanceof I_ConstUserNumber)) {
							eu.mihosoft.vrl.system.VMessage.exception("Invalid specification: ",
								"Hodgkin-Huxley channel conductance cannot be given as code.");
						}
						double revPot = ((Const__I_ConstUserNumber) revPotData[i].getNumberData(1)).const__get();
						leak.set_rev_pot(revPot, subset);
					}
				}
			}

		}

	}

	@MethodInfo(name = "set reversal potential", interactive = false)
	public void set_reversal_potential(
		@ParamInfo(name = " ", style = "array", options = "ugx_globalTag=\"gridFile\"; type=\"s|n:subset,rev. pot. [V]\"") UserDataTuple[] revPotData
	) {
		check_leak_exists();

		for (int i = 0; i < revPotData.length; ++i) {
			String selSs = revPotData[i].getSubset(0);
			if (!(revPotData[i].getNumberData(1) instanceof I_ConstUserNumber)) {
				eu.mihosoft.vrl.system.VMessage.exception("Invalid specification: ",
					"Hodgkin-Huxley channel conductance cannot be given as code.");
			}

			double revPot = ((I_ConstUserNumber) revPotData[i].getNumberData(1)).const__get();

			leak.set_rev_pot(revPot, selSs);
		}
	}

	@MethodInfo(name = "set conductance global", interactive = false)
	public void set_conductance_global(
		@ParamInfo(name = "conductance [S/m^2]", style = "default", options = "value=3.0") double cond
	) {
		check_leak_exists();
		check_value(cond);

		leak.set_cond(cond);
	}

	@MethodInfo(name = "set conductances", interactive = false)
	public void set_conductance(
		@ParamInfo(name = " ", style = "array", options = "ugx_globalTag=\"gridFile\"; type=\"s|n:subset,conductance [S/m^2]\"") UserDataTuple[] condData
	) {
		check_leak_exists();

		for (int i = 0; i < condData.length; ++i) {
			String selSs = condData[i].getSubset(0);
			if (!(condData[i].getNumberData(1) instanceof I_ConstUserNumber)) {
				eu.mihosoft.vrl.system.VMessage.exception("Invalid specification: ",
					"Hodgkin-Huxley channel conductance cannot be given as code.");
			}

			double cond = ((I_ConstUserNumber) condData[i].getNumberData(1)).const__get();

			check_value(cond);

			leak.set_cond(cond, selSs);
		}
	}

	@MethodInfo(noGUI = true)
	private void check_leak_exists() {
		if (leak == null) {
			eu.mihosoft.vrl.system.VMessage.exception("Usage before initialization: ",
				"No method can be called on leakage object before it has been initialized"
				+ "using the 'create()' method.");
		}
	}

	@MethodInfo(noGUI = true)
	private void check_value(double val) {
		if (val < 0.0) {
			eu.mihosoft.vrl.system.VMessage.exception("Invalid value: ",
				"The value " + val + " is not admissable in the leakage object. Values must not be negative.");
		}
	}
}

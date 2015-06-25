package edu.gcsc.vrl.neurobox;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.system.VMessage;
import java.io.File;
import java.io.Serializable;


@ComponentInfo(name="SpineGenerator", category="Neuro")
public class SpineGenerator implements Serializable
{
    private static final long serialVersionUID = 1L;

    //number cyt_radius = 2.0, number er_radius = 0.5, number dend_length = 10.0, int pos_app = 5, number app_neck_radius = 0.4, number app_neck_length = 1.0, number app_head_radius = 0.3, number app_head_length = 0.3, string fileName = "dendrite"
    @MethodInfo(name="build spine", valueName="file")
    public File build_spine
    (
        @ParamInfo(name="Cytosol Radius[µm]", options="value=2.0") double cyt_radius,
        @ParamInfo(name="ER Radius[µm]", options="value=0.5") double er_radius,
        @ParamInfo(name="Dendrite Length[µm]", options="value=10.0") double dend_length,
        @ParamInfo(name="Spine Position[µm]", options="value=5.0") double pos_app,
        @ParamInfo(name="App Neck Radius[µm]", options="value=0.4") double app_neck_radius,
        @ParamInfo(name="App Neck Length[µm]", options="value=1.0") double app_neck_length,
        @ParamInfo(name="App Head Radius[µm]", options="value=0.3") double app_head_radius,
        @ParamInfo(name="App Head Height[µm]", options="value=0.3") double app_head_length,
        @ParamInfo(name="Spine Neck Radius[µm]", options="value=1.0") double spine_neck_radius,
        @ParamInfo(name="Spine Neck Length[µm]", options="value=0.5") double spine_neck_length,
        @ParamInfo(name="Spine Head Radius[µm]", options="value=0.5") double spine_head_radius,
        @ParamInfo(name="Spine Head Height[µm]", options="value=1.5") double spine_head_length,
        @ParamInfo(name="File Name", style="save-dialog", options="tag=\"TheFile\"") File file
    )
    {
        String fileName = file.getAbsoluteFile().getAbsolutePath();
        
        Double[] doubleVector = new Double[12];
        doubleVector[0] = cyt_radius;
        doubleVector[1] = er_radius;
        doubleVector[2] = dend_length;
        doubleVector[3] = pos_app;
        doubleVector[4] = app_neck_radius;
        doubleVector[5] = app_neck_length;
        doubleVector[6] = app_head_radius;
        doubleVector[7] = app_head_length;
        doubleVector[8] = spine_neck_radius;
        doubleVector[9] = spine_neck_length;
        doubleVector[10] = spine_head_radius;
        doubleVector[11] = spine_head_length;
        
        boolean opt_synapse = true;
        boolean opt_ER = true;
        boolean opt_app = true;
        Boolean[] boolVector = new Boolean[3];
        boolVector[0] = opt_synapse;
        boolVector[1] = opt_ER;
        boolVector[2] = opt_app;
        
        edu.gcsc.vrl.ug.api.F_BuildDendrite.invoke(doubleVector, boolVector, fileName);
        
        if (!file.exists())
        {
            VMessage.exception("SpineGenerator failed",
                "The geometry supposedly created by the spine generator can not be found.");
            
            return null;
        }
        
        return file;
    }
}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gcsc.vrl.neurobox;

import edu.gcsc.vrl.neurobox.control.CableEquation;
import edu.gcsc.vrl.neurobox.general_elemdisc.Diffusion;
import edu.gcsc.vrl.neurobox.general_elemdisc.Buffer;
import edu.gcsc.vrl.neurobox.general_elemdisc.FluxBoundary;
import edu.gcsc.vrl.neurobox.control.MembranePotentialMappingSolver;
import edu.gcsc.vrl.neurobox.control.MembranePotentialMapping;
import edu.gcsc.vrl.neurobox.types.IntegerIndexSelectionInputType;
import edu.gcsc.vrl.neurobox.types.IElemDisc_ArrayType;
import edu.gcsc.vrl.neurobox.types.IElemDisc_Type;
import edu.gcsc.vrl.neurobox.control.CalciumDynamics;
import edu.gcsc.vrl.neurobox.control.ModelSetup;
import edu.gcsc.vrl.neurobox.control.DomainAndFunctionDefiniton;
import edu.gcsc.vrl.neurobox.control.InstationarySolver;
import edu.gcsc.vrl.neurobox.control.ResourcePathProvider;
import edu.gcsc.vrl.neurobox.membrane_transport.Hodgkin_Huxley;
import edu.gcsc.vrl.neurobox.membrane_transport.NCX_VRL;
import edu.gcsc.vrl.neurobox.membrane_transport.VDCC_VRL;
import edu.gcsc.vrl.neurobox.membrane_transport.VDCC_with_Vm2uG;
import edu.gcsc.vrl.neurobox.membrane_transport.IP3R_VRL;
import edu.gcsc.vrl.neurobox.membrane_transport.SERCA_VRL;
import edu.gcsc.vrl.neurobox.membrane_transport.PMCA_VRL;
import edu.gcsc.vrl.neurobox.membrane_transport.Leak_VRL;
import edu.gcsc.vrl.neurobox.membrane_transport.RyR_VRL;
import edu.gcsc.vrl.neurobox.types.IChannel_ArrayType;
import edu.gcsc.vrl.neurobox.types.IChannel_Type;
import eu.mihosoft.vrl.io.IOUtil;
import eu.mihosoft.vrl.io.VJarUtil;
import eu.mihosoft.vrl.lang.visual.CompletionUtil;
import eu.mihosoft.vrl.system.InitPluginAPI;
import eu.mihosoft.vrl.system.PluginAPI;
import eu.mihosoft.vrl.system.PluginDependency;
import eu.mihosoft.vrl.system.PluginIdentifier;
import eu.mihosoft.vrl.system.ProjectTemplate;
import eu.mihosoft.vrl.system.VPluginAPI;
import eu.mihosoft.vrl.system.VPluginConfigurator;
import eu.mihosoft.vrl.system.VRLPlugin;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author mbreit
 */
public class NeuroBoxPluginConfigurator extends VPluginConfigurator
{    
    public NeuroBoxPluginConfigurator()
    {
        //specify the plugin name and version
        setIdentifier(new PluginIdentifier("VRL-NeuroBox", "0.1"));

        // optionally allow other plugins to use the api of this plugin
        // you can specify packages that shall be
        // exported by using the exportPackage() method:
        //
        // exportPackage("com.your.package");

        // describe the plugin
        setDescription("Plugin for neuro-related simulations with UG4.");

        // copyright info
        setCopyrightInfo("VRL-NeuroBox-Plugin",
                         "(c) mbreit, sgrein, mstepniewski et al.",
                         "www.g-csc.de", "Proprietary", "Proprietary");

        // specify dependencies
        addDependency(new PluginDependency("VRL", "0.4.2", "0.4.2"));
        addDependency(new PluginDependency("VRL-UG", "0.2", "0.2"));
        addDependency(new PluginDependency("VRL-UG-API", "0.2", "0.2"));
        addDependency(new PluginDependency("VRL-UserData", "0.2", "0.2"));
    }
    
    @Override
    public void register(PluginAPI api)
    {
        // register plugin with canvas
        if (api instanceof VPluginAPI)
        {
            VPluginAPI vapi = (VPluginAPI) api;
            
            ResourcePathProvider.path2plugin = getInitAPI().getResourceFolder();

            // Register visual components:
            //
            // Here you can add additional components,
            // type representations, styles etc.
            //
            // ** NOTE **
            //
            // To ensure compatibility with future versions of VRL,
            // you should only use the vapi or api object for registration.
            // If you directly use the canvas or its properties, please make
            // sure that you specify the VRL versions you are compatible with
            // in the constructor of this plugin configurator because the
            // internal api is likely to change.
            //
            // examples:
            //
            // vapi.addComponent(MyComponent.class);
            // vapi.addTypeRepresentation(MyType.class);

            // not necessarily neuro (move this somewhere else maybe)
            vapi.addTypeRepresentation(IntegerIndexSelectionInputType.class);

            vapi.addTypeRepresentation(IElemDisc_Type.class);
            vapi.addTypeRepresentation(IElemDisc_ArrayType.class);

            vapi.addTypeRepresentation(IChannel_Type.class);
            vapi.addTypeRepresentation(IChannel_ArrayType.class);
            
            vapi.addComponent(ResourcePathProvider.class);


            // general purpose
            vapi.addComponent(DomainAndFunctionDefiniton.class);
            vapi.addComponent(ModelSetup.class);
            vapi.addComponent(Diffusion.class);
            vapi.addComponent(Buffer.class);
            vapi.addComponent(FluxBoundary.class);

            // calcium dynamics
            vapi.addComponent(CalciumDynamics.class);
            vapi.addComponent(InstationarySolver.class);

            vapi.addComponent(IP3R_VRL.class);
            vapi.addComponent(RyR_VRL.class);
            vapi.addComponent(SERCA_VRL.class);
            vapi.addComponent(Leak_VRL.class);
            vapi.addComponent(PMCA_VRL.class);
            vapi.addComponent(NCX_VRL.class);
            vapi.addComponent(VDCC_VRL.class);
            vapi.addComponent(VDCC_with_Vm2uG.class);

            // cable equation
            vapi.addComponent(CableEquation.class);
            vapi.addComponent(Hodgkin_Huxley.class);
            vapi.addComponent(SynapseHandler.class);

            // membrane potential mapping
            vapi.addComponent(MembranePotentialMapping.class);
            vapi.addComponent(MembranePotentialMappingSolver.class);

            // spine generator
            vapi.addComponent(SpineGenerator.class);
        }
    }
    
    
    @Override
    /**
     * @brief install plugins
     * @param iApi
     */
    public void install(InitPluginAPI iApi)
    {
        // ensure template projects are updated (by deleting first)
        new File(iApi.getResourceFolder(), "hh_network_template.vrlp").delete();
        new File(iApi.getResourceFolder(), "testNetwork.ugx").delete();
        
        ////////////////////////////////////
        // ... add your own data here ... //
        ////////////////////////////////////
    }


    @Override
    public void unregister(PluginAPI api)
    {
        // nothing to unregister
    }

    @Override
    public void init(InitPluginAPI iApi)
    {
        //CompletionUtil.registerClassesFromJar(
        //    VJarUtil.getClassLocation(NeuroBoxPluginConfigurator.class));

        initHHNetworkTemplate(iApi);
        
        //////////////////////////////////////////////////////
        // ... add your own template initializers here ... //
        //////////////////////////////////////////////////////
   }
    
    private void initHHNetworkTemplate(InitPluginAPI iApi)
    {
        // add template project files
        final File templateProjectSrc = new File(iApi.getResourceFolder(),
            "hh_network_template.vrlp");
         File templateProjectGeom = new File(iApi.getResourceFolder(),
            "testNetwork.ugx");
        
        // save if not yet existent
        if (!templateProjectSrc.exists())
        {
            InputStream in = NeuroBoxPluginConfigurator.class.getResourceAsStream(
                "/edu/gcsc/vrl/neurobox/project_templates/hh_network_template.vrlp");
            
            saveProjectTemplate(in, templateProjectSrc);
        }
        if (!templateProjectGeom.exists())
        {
            InputStream in = NeuroBoxPluginConfigurator.class.getResourceAsStream(
                "/edu/gcsc/vrl/neurobox/geom/testNetwork.ugx");
            
            saveProjectTemplate(in, templateProjectGeom);
        }
        
        // register as project templates with VRL
        iApi.addProjectTemplate(new ProjectTemplate()
            {
                @Override
                public String getName()
                {
                    return "Cable equation - network template";
                }

                @Override
                public File getSource()
                {
                    return templateProjectSrc;
                }

                @Override
                public String getDescription()
                {
                    return "template for a cable equation network simulation "
                            + "with synapses";
                }

                @Override
                public BufferedImage getIcon()
                {
                    return null;
                }
            }
        );        
    }

    /**
     * @brief saves the project templates
     */
    private void saveProjectTemplate(InputStream in, File outFile)
    {
        try
        {
            IOUtil.saveStreamToFile(in, outFile);
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(VRLPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(VRLPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
 }

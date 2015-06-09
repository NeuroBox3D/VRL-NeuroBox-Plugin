/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gcsc.vrl.neuro;

import eu.mihosoft.vrl.system.InitPluginAPI;
import eu.mihosoft.vrl.system.PluginAPI;
import eu.mihosoft.vrl.system.PluginDependency;
import eu.mihosoft.vrl.system.PluginIdentifier;
import eu.mihosoft.vrl.system.VPluginAPI;
import eu.mihosoft.vrl.system.VPluginConfigurator;



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
           vapi.addComponent(DomainAndFunctionDefiniton.class);
           
           // general purpose
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
           
           // membrane potential mapping
           vapi.addComponent(MembranePotentialMapping.class);
           vapi.addComponent(MembranePotentialMappingSolver.class);
           
           // spine generator
           vapi.addComponent(SpineGenerator.class);
       }
   }

   @Override
   public void unregister(PluginAPI api)
   {
       // nothing to unregister
   }

    @Override
    public void init(InitPluginAPI iApi)
    {
        // nothing to init
    }
 }

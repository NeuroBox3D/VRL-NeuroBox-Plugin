NeuroBox
========
See also http://www.neurobox.eu for further details.

# Development

## VersionEye
* [![Dependency Status](https://www.versioneye.com/user/projects/55828a5f386664001a000727/badge.svg?style=flat)](https://www.versioneye.com/user/projects/55828a5f386664001a000727)

## Code metrics
* [![Codacy Badge](https://api.codacy.com/project/badge/grade/24ca78102c7b48d2912ab069a2b73e2e)](https://www.codacy.com/app/stephan_5/VRL-NeuroBox-Plugin)

## Issues
* [![Stories in Backlog](https://badge.waffle.io/NeuroBox3D/vrl-neurobox-plugin.png?label=backlog&title=Backlog)](http://waffle.io/NeuroBox3D/vrl-neurobox-plugin)
[![Stories in Ready](https://badge.waffle.io/NeuroBox3D/vrl-neurobox-plugin.png?label=ready&title=Ready)](http://waffle.io/NeuroBox3D/vrl-neurobox-plugin)
[![Stories in In Progress](https://badge.waffle.io/NeuroBox3D/vrl-neurobox-plugin.png?label=in progress&title=In Progress)](http://waffle.io/NeuroBox3D/vrl-neurobox-plugin)
[![Stories in Done](https://badge.waffle.io/NeuroBox3D/vrl-neurobox-plugin.png?label=done&title=Done)](http://waffle.io/NeuroBox3D/vrl-neurobox-plugin)

## How To Build The Project

### 1. Dependencies

- JDK >= 1.6
- Internet Connection (other dependencies will be downloaded automatically)
- Optional: IDE with [Gradle](http://www.gradle.org/) support


### 2. Configuration

Specify correct path in `build.properties`, e.g.,
    
    # vrl property folder location (plugin destination)
    vrldir=/Users/myusername/.vrl/0.4.2/default

### 3. Build & Install

#### IDE

To build the project from an IDE do the following:

- open the  [Gradle](http://www.gradle.org/) project
- call the `installVRLPlugin` Gradle task to build and install the plugin
- restart VRL-Studio

#### Command Line

Building the project from the command line is also possible.

Navigate to the project folder and call the `installVRLPlugin` [Gradle](http://www.gradle.org/)
task to build and install the plugin.

##### Bash (Linux/OS X/Cygwin/other Unix-like OS)

    cd Path/To/VRL-NeuronalTopologyImporter-Plugin/VRL-NeuronalTopologyImporter-Plugin
    ./gradlew installVRLPlugin
    
##### Windows (CMD)

    cd Path\To\VRL-NeuronalTopologyImporter-Plugin\VRL-NeuronalTopologyImporter-Plugin
    gradlew installVRLPlugin

Finally, restart VRL-Studio

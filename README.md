<div align="center">

# open-interactive-simulation-deployer

[![Test](https://github.com/attiasas/open-interactive-simulation-deployer/actions/workflows/test.yml/badge.svg)](https://github.com/attiasas/open-interactive-simulation-deployer/actions/workflows/test.yml?branch=master)

</div>

---

## Table of Contents
- [📚 Overview](#-overview)
- [📦 Installation](#-installation)
- [⚙️ Project Configurations](#-project-configurations)
- [🏗️ How to Use](#-how-to-use)
  - [🌱 Developing Your Project](#-developing-your-project)
  - [👀 Running Your Project](#-running-your-project)
  - [🚀 Publishing Your Project](#-publishing-your-project)
- [🐞 Reporting Issues](#-reporting-issues)
- [🤝 Contributions](#-contributions)

---
## 📚 Overview

Experience a user-friendly open-source 🐘 Gradle plugin 🐘 designed to simplify your DevOps journey while developing with [LibGdx](https://libgdx.com/).

This plugin empowers you to effortlessly develop, run, and distribute simulations and games using [LibGdx](https://libgdx.com/) across multiple platforms.
With automation at its core, you can bid farewell to complex configurations and project structure.

Harnessing the power of the [core OIS library](https://github.com/attiasas/open-interactive-simulation-core), this plugin provides a straightforward approach to implementing graphic simulation and games.
Paired with the [OIS runners](https://github.com/attiasas/open-interactive-simulation-runner), distributing your projects across platforms becomes a breeze, all while maintaining a clean project structure.

Embark on your project journey within your preferred IDE, without the hassle of learning new tools to develop simulations or games in Java.
Simplify development and distribution and embrace a more streamlined process.

### 🚥 Requirements
* Java version `15` or a more recent release.
* Gradle version `7` or a newer version.

---
## 📦 Installation

The plugin is not available in Maven central yet, required to be installed locally

<details>
<summary>Manual installation</summary>

Install locally OIS core library
1. Clone the [core library](https://github.com/attiasas/open-interactive-simulation-core)
    ```bash
     git clone https://github.com/attiasas/open-interactive-simulation-core.git
   ```
2. Navigate to the cloned directory and publish the library to maven local
   ```bash
    ./gradlew publishToMavenLocal
   ```

Install locally the plugin
1. Clone this repository
    ```bash
     git clone https://github.com/attiasas/open-interactive-simulation-deployer.git
   ```
2. Navigate to the cloned directory and publish the library to maven local
   ```bash
    ./gradlew publishToMavenLocal
   ```

</details>

Download the installation bash [script](src/main/resources/installOIS.sh) and run it:
```bash
./installOIS.sh
```

---
1. Add at the top of your `settings.gradle` the following snippet
   ```groovy
    pluginManagement {
        repositories {
            mavenLocal()
        }
    }
   ```
2. Add the following repositories to the `buildScript` in your project `build.gradle`
   ```groovy
    buildscript {
        repositories {
            mavenLocal()
            mavenCentral()
            gradlePluginPortal()
            maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
            google()
        }
    }
   ```
3. Apply the plugin in your project `build.gradle`
   ```groovy
    plugins {
        id 'org.attias.open.interactive.simulation.deployer' version '1.0-SNAPSHOT'
    }
   ```
4. [Optional, Recommended] Add the core library dependency in your project `build.gradle`
   ```groovy
    dependencies {
        implementation group: 'org.attias.open.interactive.simulation', name: 'open-interactive-simulation-core', version: '1.0-SNAPSHOT'
    }
   ```

---
## ⚙️ Project Configurations

In order to utilize the plugin and execute OIS projects, it is necessary to set up the configurations or [Generate](#-develop-your-project)
a file known as `simulation.ois`. This file will contain all the essential project configurations required for running your project.

The file follows a JSON format and comprises the subsequent attributes:

| Attribute             | Description                                                                                           |
|-----------------------|-------------------------------------------------------------------------------------------------------|
| title                 | The designated title of your simulation project.                                                      |
| initialState          | The state that will be activated upon initiating the simulation.                                      |
| states                | A mapping of all implemented IState classes within your project, along with their corresponding keys. |
| publish               | The configurations for publishing your OIS project and facilitating its distribution.                 |
| publish.platforms     | The designated platforms on which the simulation will operate, with at least one platform defined.    |
| publish.iconsDir      | [Optional] The directory housing all project icons.                                                   |
| publish.publishedName | [Optional, Default is the project name] The name of the resulting artifacts upon publishing.          |

For instance:
```json
{
  "title" : "OIS simulation", 
  "initialState" : "Green",
  "states" : {
    "Blue" : "org.example.BlueState",
    "Red" : "org.example.RedState",
    "Green" : "org.example.GreenState"
  },
  "publish" : {
    "platforms" : [ "Desktop" ]
  }
}
```

### Custom Icons

You have the opportunity to tailor your own custom icons for the project.
A designated directory should be provided to accommodate these icons.
You are not obliged to provide a complete set of icons; any missing icons will be substituted with default counterparts.

The directory must contain only one item per combination of dimensions and extensions.
If multiple items exist, only one will be considered.

The valid extensions for icons are: `png`, `ico`, `icns`.

Acceptable icon dimensions include: `128x128`, `32x32`, `16x16`.

### Plugin Extension
For seamless development, the option to override specific project `simulation.ois` or plugin configurations is available.
This can be achieved by specifying the `oisDeployer` extension within your `build.gradle` file:

```groovy
oisDeployer {
    // Instead of resolving the runner based on its version in simulation.ois, 
    // run the runner project from this directory.
    runnerPath = 'path-to-dir-of-specific-runner'
    // Instead of retrieving the project configurations from the `simulation.ois` file in the project root directory,
    // obtain project configurations from this file.
    configPath = 'path-to-your-simulation-config-file'
    // Rather than resolving the assets directory from your project resources, resolve it from this path.
    assetsPath = 'path-to-your-resources-dir'
}
```

---
## 🏗️ How to Use

The plugin offers Gradle tasks that facilitate both the execution and distribution of your project across various platforms. 
These tasks are grouped under the `ois` category.

### 🌱 Developing Your Project

To establish the foundation of your OIS project and generate essential files, use the following command:
   ```bash
   gradlew initializeProject
   ```

If you wish to make modifications to your project's structure, files, or attributes and ensure the correctness of your configuration, execute:
   ```bash
   gradlew validateProject
   ```

For implementing your OIS project, consider utilizing LibGdx or consult the [user guide](https://github.com/attiasas/open-interactive-simulation-core/blob/master/USER_GUIDE.md)
for insights into leveraging the core library effectively.

### 👀 Running Your Project

After applying the [configurations](#-project-configurations), you can run your project locally on your `Desktop` by executing:
   ```bash
   gradlew runDesktop
   ```

### 🚀 Distributing Your Project

Once you've implemented your project, you can deploy it to generate the necessary production files. 
These files are essential for running your project on the designated platforms. Use the following command:
   ```bash
   gradlew deployProject
   ```

> **Distributing on the Desktop Platform:**
> 
> Note that the generated zip files are specific to the platform they were built on. 
> For instance, generate the distribution task on a Windows machine for Windows distribution and on a Linux machine for Linux distribution.

The artifacts intended for distribution will be created within your project's `build` directory.
Inside this directory, you'll find a folder named `OIS` containing zip files tailored for each platform configuration specified in your project's configuration file.

---
## 🐞 Reporting Issues

When encountering problems during your build process, 
we recommend using the `-d` option when running Gradle for detailed debug information.

If you face issues with running or deploying your project, start fresh by cleaning the runners:
```bash
    gradlew cleanRunners
```

To contribute to the library's improvement, 
please [report encountered issues](https://github.com/open-interactive-simulation-deployer/issues/new/choose). Your input is valuable to us.

---
## 🤝 Contributions

We welcome pull requests from the community. To help us improve this project, please read
our [Contribution](./CONTRIBUTING.md#-guidelines) guide.
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
        implementation group: 'org.attias.open.interactive.simulation', name: 'open-interactive-simulation-core', version: '0.1'
    }
   ```

---
## ⚙️ Project Configurations

In order to utilize the plugin and execute OIS projects, it is necessary to set up the configurations or [Generate](#-develop-your-project)
a file known as `simulation.ois`. This file will contain all the essential project configurations required for running your project.

The file follows a JSON format and comprises the subsequent attributes:

| Attribute                    | Description                                                                                           |
|------------------------------|-------------------------------------------------------------------------------------------------------|
| title                        | The designated title of your simulation project.                                                      |
| initialState                 | The state that will be activated upon initiating the simulation.                                      |
| states                       | A mapping of all implemented IState classes within your project, along with their corresponding keys. |
| publish                      | The configurations for publishing your OIS project and facilitating its distribution.                 |
| publish.platforms            | The designated platforms on which the simulation will operate, with at least one platform defined.    |
| publish.publishedName        | [Optional, default is the project name] The name of the resulting artifacts upon publishing.          |
| publish.publishNumber        | [Optional default is 1] the publishing version number (typically incremented with each release).      |
| publish.iconsDir             | [Optional] The directory housing all project icons, must follow the [configurations](#-custom-icons). |
| publish.generateMissingIcons | [Optional, default is false] try to generate missing icons base on `iconsDir` attribute content.      |

For instance (minimal, only required attributes):
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
    "platforms" : [ "Desktop", "Android" ]
  }
}
```

### Plugin Extension
For seamless development, the option to override specific project `simulation.ois` or plugin configurations is available.
This can be achieved by specifying the `oisDeployer` extension within your `build.gradle` file:

```groovy
oisDeployer {
    // Instead of resolving the runner based on its version in simulation.ois, 
    // Run the runner project from this directory.
    runnerPath = 'path-to-dir-of-specific-runner'
    // Instead of retrieving the project configurations from the `simulation.ois` file in the project root directory,
    // Obtain project configurations from this file.
    configPath = 'path-to-your-simulation-config-file'
    // Rather than resolving the assets directory from your project resources
    // Resolve it from this path.
    assetsPath = 'path-to-your-resources-dir'
    // Rather than resolving the Android Sdk location from the environment variable 'ANDROID_HOME'
    // Resolve it from this path.
    androidSdkPath = 'path-to-android-sdk-dir'
}
```

### 🖼️ Custom Icons

You have the opportunity to tailor your own custom icons for the project.
A designated directory should be provided to accommodate these icons.
You are not obliged to provide a complete set of icons; any missing icons will be substituted with default counterparts,
or generated from the existing subset.

The directory must contain only one item per combination of dimensions and extensions.
If multiple items exist, only one will be considered.

<details>
<summary>Desktop Platform Icons</summary>

---

The valid extensions for icons are:
* **Windows:** `png`, `ico` (optional).
* **Linux:** `png`.
* **Mac:** `icns`.

Acceptable icon dimensions include: `128x128`, `32x32`.

---
</details>

<details>
<summary>Android Platform Icons</summary>

---

The icon that will be shown as the app has to be in `xml` format and be generated by following this steps:
1. **Generate your Icon**.
   * Must be in `svg` / `psd` format. 
   * Colors must be in black and white (can color shapes later).
   * Must be `108x108`, the logo should be centered, must be at least `48x48` dp; it must not exceed `72x72` dp because the inner `72x72` dp of the icon appears within the masked viewport.
2. **Open, in intellij, any `Android` project** (or use the runner project cloned to the `ois` plugin directory in your `$HOME` directory).
   
    2.1. On the Project tab, right click: `New` -> `Vector Asset`

    2.2. Chose `Asset type` value to `Local file (SVG, PSD)` the path and the target name.

    2.3. Chose `108dpx108dp` as the value for `Sizw`.

    2.4. Continue and generate the `xml` file and copy it to your `iconsDir`.
 
3. **[Optional] Color** the generated icon shapes (`paths` tag) by editing the `xml` file and changing their `fillColor` values.

In addition, you can provide `png` icons for specific dpi,
Acceptable icon dimensions include: `48x48`, `72x72`, `96x96`, `144x144`, `192x192`.

---
</details>

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

To plugin will create running tasks to debug your project for each value you input at `publish.platforms` attribute in the [configurations](#-project-configurations).

<details>
<summary>Desktop Platform</summary>

---
After applying the configurations, you can run your project locally on your `Desktop` by executing:
   ```bash
   gradlew runDesktop
   ```
---
</details>

<details>
<summary>Android Platform</summary>

---
After applying the configurations, you can run your project on a virtual/physical `Android` device by executing:
1. Make sure there is an active device connected (can use Intellij).
2. Run the following gradle command:
   ```bash
   gradlew runAndroid
   ```
> Some changes may require to uninstall the application before rerunning.
---
</details>


### 🚀 Distributing Your Project

Once you've implemented your project, you can deploy it to generate the necessary production files. 
These files are essential for running your project on the designated platforms. Use the following command:
   ```bash
   gradlew deployProject
   ```

The artifacts intended for distribution will be created within your project's `build` directory.
Inside this directory, you'll find a folder named `OIS` containing zip files tailored for each platform configuration specified in your project's configuration file.

> **Publishing to Desktop Platform:**
>
> Note that the generated zip files are specific to the platform they were built on.
> For instance, generate the distribution task on a Windows machine for Windows distribution and on a Linux machine for Linux distribution.

> **Publishing to Android Platform:**
> 
> Note that the generated apk is unsigned and should be signed before distribution.

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
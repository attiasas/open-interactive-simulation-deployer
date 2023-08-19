<div align="center">

# open-interactive-simulation-deployer

[![Test](https://github.com/attiasas/open-interactive-simulation-deployer/actions/workflows/test.yml/badge.svg)](https://github.com/attiasas/open-interactive-simulation-deployer/actions/workflows/test.yml?branch=master)

</div>

---

## Table of Contents
- [📚 Overview](#-overview)
- [📦 Installation](#-installation)
- [⚙️ Project Configurations](#-project-configurations)
- [🏗️ Usage](#-usage)
  - [🌱 Develop your project](#-develop-your-project)
  - [👀 Run your project](#-run-your-project)
  - [🚀 Distribute your project](#-distribute-your-project)
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

---
## 📦 Installation

The plugin is not available in Maven central yet, required to be installed locally

<details>

---
<summary>Install locally OIS core library</summary>

1. Clone the [core library](https://github.com/attiasas/open-interactive-simulation-core)
    ```bash
     git clone https://github.com/attiasas/open-interactive-simulation-core.git
   ```
2. Navigate to the cloned directory and publish the library to maven local
   ```bash
    ./gradlew publishToMavenLocal
   ```
   
---
</details>

<details>

---
<summary>Install locally the plugin</summary>

1. Clone this repository
    ```bash
     git clone https://github.com/attiasas/open-interactive-simulation-deployer.git
   ```
2. Navigate to the cloned directory and publish the library to maven local
   ```bash
    ./gradlew publishToMavenLocal
   ```

---
</details>

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
4. Add the core library dependency in your project `build.gradle`
   ```groovy
    dependencies {
        implementation group: 'org.attias.open.interactive.simulation', name: 'open-interactive-simulation-core', version: '1.0-SNAPSHOT'
    }
   ```

---
## ⚙️ Project Configurations

To use the plugin you need to configure some information
you can do it by adding the plugin Extension in your project `build.gradle`
```groovy
simulationDeployer {
   deployer {
      title = "test title"
      version = '1.0-SNAPSHOT'
      dynamicClass = 'org.example.GreenState'
   }
}
```

The plugin expect a specific project structure inorder to know how to get what it needs from your project.
This information is stored at the `settings.ios`

---
## 🏗️ Usage

The plugin exposed gradle tasks to run and distribute your project to different platforms.
All the task exposed by the plugin are grouped under the group `ois`.

### 🌱 Develop your project


### 👀 Run your project

Apply the configurations and run your project locally on your `Desktop` by running the gradle task
   ```bash
   gradlew runDesktop
   ```

### 🚀 Distribute your project

---
## 🐞 Reporting Issues

We highly recommend running Gradle with the ```-d```
option to get useful and readable debug information if something goes wrong with your build.

Please help us improve the library
by [reporting any issues](https://github.com/jfrog/artifactory-gradle-plugin/issues/new/choose) you encounter.

---
## 🤝 Contributions

We welcome pull requests from the community. To help us improve this project, please read
our [Contribution](./CONTRIBUTING.md#-guidelines) guide.
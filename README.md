# Sonalyze — v1.0-TSA-BUILD

## Goal
To solve a problem.  
  
Data visualization... visualization is in the name. It excludes people who have visual impairments 
leading for data 'sonalyzation' tools to be non-existent. Data analysis is very limited, tedious and
unfair for people with visual impairments. Sonalyze is made to unify tools in aiding data analysis
with audio methods — and a sleek light-weight interface that is easy to navigate.

## Preface
The entry point to Sonalyze is in Sonalyze.kt; this application is designed for and has only been 
tested on macOS — granted its nature allows it to be easily modified to support other operating
systems. Still, some functionality may not work as intended when using other operating systems.

## How to Run
- Method 1 (Program): On macOS run the Sonalyze.jar file provided, this requires either JRE or JDK to
be installed.
- Method 2 (IntelliJ): On macOS open up the sonalyze folder as an IntelliJ project, goto Sonalyze.kt, 
run the main function.

## Features
- Offline Compatibility
- Visually-Impaired Accessibility and Workflow
- Key to button binding — for non-visual controls
- File Scanning and Importing
- Column Selection
- Data Points
- Accompanying UI — so well-sighted people may assist
- Sonalyze
- - Queued Dictations
- - Dictation Controls
- - Dataset Information and Statistics
- - Audio Graphs — a feature that turns lines into audio
- - Audio Linear Regression
- - Audio Shape 

## Technologies Used
- Kotlin
- JavaFX
- Smile
- Maven — used in place of Gradle due to device restrictions
- IntelliJ

## Future Improvements
- Complete Localization Functionality — some systems have already been setup to support localization
- Data Mutation 
- Cross Data Referencing — to directly compare two different data sets in one workspace
- Non Data Point Based Graphs
- Plugin Functionality — to allow Sonalyze to be community-built and support a wider array of more 
specific tools.
- Multiple Active Workspaces
- Audio Guides

## Design Decisions
- Kotlin was chosen as the primary language of choice as a modern, type-safe, cross-platform language.
Kotlin has already been adopted as the recommended language for Android apps; it integrates completely
with JVM — making it cooperable with Java. Which then allows Kotlin applications to take advantage of 
the wide array of Java utilities and libraries with less boilerplate. 

- Although Gradle was the desired build tool it could not be used due to restrictions on the provided 
development devices, so it was substituted for Maven.

### Architecture 
- This app doesn't follow a traditio3nal MVC architecture. The app has a service and page-driven 
architecture with pages acting as Controllers AND Viewers. Models then can be attached to pages as
a variable. Services can be made as singletons to assist Pages in place of direct Controllers. Given 
the vision of the project this is well-suited for the following reasons: 
- - Integrability with JavaFX — JavaFX cannot be modified off thread, keeping everything in the Page
almost guarantees that JavaFX doesn't stall or have unexpected bugs; the Page object is safely
declared which keeps the KeyBoundButton (and keybind) functionality in sync.
- - Reduction of Boilerplate — Functionality for a page, stays in the page.
- - Scalability — and pluggability, a goal for this project in the future is to be easily able to
integrate community plugins.



## Credit
Developed by: TSA Competitors  
Resources:
- [Smile](https://haifengl.github.io)
- [JavaFX](https://openjfx.io/)
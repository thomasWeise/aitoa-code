# Example Codes from the Book "An Introduction to Optimization Algorithms"

[<img alt="Travis CI Build Status" src="https://img.shields.io/travis/thomasWeise/aitoa-code/master.svg" height="20"/>](https://travis-ci.org/thomasWeise/aitoa-code/)
[<img alt="AppVeyor Build Status" src="https://img.shields.io/appveyor/ci/thomasWeise/aitoa-code.svg" height="20"/>](https://ci.appveyor.com/project/thomasWeise/aitoa-code)
[<img alt="drone.io Build Status" src="https://cloud.drone.io/api/badges/thomasWeise/aitoa-code/status.svg" height="20">](https://cloud.drone.io/thomasWeise/aitoa-code)
[![Release](https://jitpack.io/v/thomasWeise/aitoa-code.svg)](https://jitpack.io/#thomasWeise/aitoa-code)

## 1. Introduction

In this repository, we provide example source codes for the book "[An Introduction to Optimization Algorithms](http://github.com/thomasWeise/aitoa)".
With the book, we try to develop a readable and accessible introduction in optimization, optimization algorithms, and, in particular, metaheuristics.
The code is designed as a versatile and general implementation of these algorithms in Java and provides one example application: the Job Shop Scheduling Problem ([JSSP](http://en.wikipedia.org/wiki/Job_shop_scheduling)).

The [book](http://thomasweise.github.io/aitoa/index.html) is available in the following formats:

1. [aitoa.pdf](http://thomasweise.github.io/aitoa/aitoa.pdf), in the [PDF](http://thomasweise.github.io/aitoa/aitoa.pdf) format for reading on the computer and/or printing (but please don't print this, save paper),
2. [aitoa.html](http://thomasweise.github.io/aitoa/aitoa.html), in the [HTML5](http://thomasweise.github.io/aitoa/aitoa.html) format for reading in the browser on any device,
3. [aitoa.epub](http://thomasweise.github.io/aitoa/aitoa.epub), in the [EPUB3](http://thomasweise.github.io/aitoa/aitoa.epub) format for reading on mobile phones or other hand-held devices, and
4. [aitoa.azw3](http://thomasweise.github.io/aitoa/aitoa.azw3), in the [AZW3](http://thomasweise.github.io/aitoa/aitoa.azw3) format for reading on Kindle and similar devices.

## 2. How to Use

### 2.1. Installation

The source code is provided at [GitHub](http://github.com/thomasWeise/aitoa-code) as a [Maven](http://en.wikipedia.org/wiki/Apache_Maven) project for [Java 1.8](http://en.wikipedia.org/wiki/Java_version_history#Java_SE_8) along with settings for the [Eclipse](http://www.eclipse.org/) developer environment.
I therefore recommend using Eclipse for exploring and playing around with it.
Once you have checked-out the code and imported it as existing project in Eclipse, you need to right-click the project, choose "Maven" and then "Update Project".
This will automatically download the required dependencies (currently, only [Junit 4.12](http://junit.org/junit4/)) and set up the folder structure properly.

You can also use this package as a `jar` library directly, which will allow you to use all the implemented algorithms and API directly in your project.
Then, it may make sense to remember that this is rather an educational package, not necessarily designed for efficiency.
Anyway, if your software project uses Maven as well, then you can even directly link these sources as dependency.
Therefore, you need to do the following.

First, you need to add the following repository, which is a repository that can kind of dynamically mirror repositories at GitHub:

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```

Than you can add the dependency on our `aitoa-code` repository into your `dependencies` section.
Here, `0.8.40` is the current version of  `aitoa-code`.
Notice that you may have more dependencies in your `dependencies` section, say on `junit`, but here I just put the one for `aitoa-code` as example.

```xml
<dependencies>
  <dependency>
    <groupId>com.github.thomasWeise</groupId>
    <artifactId>aitoa-code</artifactId>
    <version>0.8.40</version>
  </dependency>
</dependencies>
```

Finally, in order to include all required external `jar`s into your `jar` upon compilation, you may want to add the following plugin into your `<build><plugins>` section:
In the snippet below, you must then replace `${project.mainClass}` with the main class that should run when your `jar` is executed.
This should result in a so-called "fat" `jar`, which includes the dependencies.
In other words, you do not need to have our `jar` in the classpath anymore but can ship your code as one single `jar`.

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-shade-plugin</artifactId>
  <version>3.2.0</version>
  <executions>
    <execution>
      <phase>package</phase>
      <goals>
        <goal>shade</goal>
      </goals>
      <configuration>
        <createSourcesJar>true</createSourcesJar>
        <shadeTestJar>true</shadeTestJar>
        <minimizeJar>false</minimizeJar>
        <shadedArtifactAttached>true</shadedArtifactAttached>
        <createDependencyReducedPom>false</createDependencyReducedPom>

        <shadedClassifierName>full</shadedClassifierName>

        <filters>
          <filter>
            <artifact>*:*</artifact>
            <excludes>
              <exclude>META-INF/*.SF</exclude>
              <exclude>META-INF/*.DSA</exclude>
              <exclude>META-INF/*.RSA</exclude>
            </excludes>
          </filter>
        </filters>

        <transformers>
          <transformer
            implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
            <mainClass>${project.mainClass}</mainClass>
          </transformer>
          <transformer
            implementation="org.apache.maven.plugins.shade.resource.ApacheLicenseResourceTransformer" />
          <transformer
            implementation="org.apache.maven.plugins.shade.resource.ApacheNoticeResourceTransformer" />
          <transformer
            implementation="org.apache.maven.plugins.shade.resource.PluginXmlResourceTransformer" />
          <transformer
            implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer" />
        </transformers>
      </configuration>
    </execution>
  </executions>
</plugin>
```

### 2.2. Running Experiments

How to run experiments with this code is discussed and shown in the book "[An Introduction to Optimization Algorithms](http://github.com/thomasWeise/aitoa)".
Basically, an objective function, a search and a solution space as well as a mapping in between them, and search operators can be composed and provided to a black-box optimization algorithm.
They are encapsulated in a `IBlackBoxProcess` instance which can automatically remember the best solution and create comprehensive log files during an experiment run.
We also provide tools to then read the log files and create result summaries (see package `aitoa.utils.logs`).

## 3. License

The copyright holder of this package is Prof. Dr. Thomas Weise (see Contact).
The package is licensed under the MIT License.

## 4. Contact

If you have any questions or suggestions, please contact
[Prof. Dr. Thomas Weise](http://iao.hfuu.edu.cn/team/director) of the
[Institute of Applied Optimization](http://iao.hfuu.edu.cn/) at
[Hefei University](http://www.hfuu.edu.cn) in
Hefei, Anhui, China via
email to [tweise@hfuu.edu.cn](mailto:tweise@hfuu.edu.cn) with CC to [tweise@ustc.edu.cn](mailto:tweise@ustc.edu.cn).

# Compiling the project

The project can be built for the Java 11 and also for Java 8 target. To
create a new release edit the `version.jim` file and set the

``` {@define VERSION=A.B.C} ``` line to whatever version is to be
released. If the target is Java 8 then the version should be
`A.B.C-JVM8`. To create a release the project has to be built first. This is
simply
 
```
mvn clean install
```

for the latest Java (Java 11 as for now) or

```
mvn -PJDK8 clean install
```

to target Java 8. After the successful build run the shell script
`release.sh` that collects all the files, starts the GPG signature and
creates the ZIP file that can be uploaded to NEXUS.
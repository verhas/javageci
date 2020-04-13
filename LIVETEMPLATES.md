# IntelliJ live templates and Java::Geci

In IntelliJ you can use live templates to insert common constructs into your code.
There are a lot of built-in/preconfigured templates in IntelliJ and there are
some live templates supported by Java::Geci.

The live templates that are help the use of some generators are in the
project directory `javageci-livetemplates`. To use them you have to download the
files from this directory and then install them into IntelliJ. When they are
installed you can get to them using the `Command + Alt + j` key combination on
MacOS or `Control + Alt + j` key combination on Windows.

<img src="images/cmd-alt-j.svg" width="100px"/>

## Importing the live templates

Choose `File | Manage IDE Settings | Import Settings` from the menu.

Specify the path to the archive with the exported live template configuration.

In the Import Settings dialog, select the Live templates checkbox and click OK.

After restarting IntelliJ IDEA, you will see the imported live templates on the 
`Editor | Live Templates` page of IntelliJ IDEA settings.

## Defined Live Templates



### insert Java::Geci editor-fold into the text

Name: `aaa-editor-fold`

```
//<editor-fold id="$id$">
//</editor-fold>
$END$
```

### insert Java::Geci iterate template

Name: `aaa-iterate-template`

```
/*TEMPLATE
ESCAPE
    // this line is not interpreted as a command
SKIP
    // this line does not get into the template
SEP1 $sep1$
SEP2 $sep2$
LOOP field,type=birth$sep1$Date$sep2$name$sep1$String$sep2$age$sep1$int
EDITOR-FOLD-ID $editorfoldId$
    $templateContent$$END$
*/
//<editor-fold id="$editorfoldId$">
/* THIS WILL BE DELETED BY THE CODE GENERATOR, DON'T WORRY
 have at least one editor-fold named 'iterate' or
 annotate the class with @javax0.geci.core.annotations.Iterate
 default value for SEP1 is , if your use this you can delete the SEP1 line
 default value for SEP2 is | if your use this you can delete the SEP2 line
 you can have many loop lines, just have the same names before the =
 a single line after ESCAPE will be in the template even if it is a SEP1, SEP2, LOOP or EDITOR-FOLD-ID line
 a single line after SKIP will be skipped altogether
 */
//</editor-fold>
```

### insert a Java::Geci snipping segment into HTML/XML

Name: `aaa-snip-java-sample-to-md`

```xml
<!-- snip $snippetName$ snippet="$snippetName$" 
          regex="replace='$replace$'
                 kill='$killPattern$"
                 escape='$escape$'"
          trim="to=0"
          number="start=1 step=1 from=0 format='%d. ' to=5"
                 -->
` ` `java
` ` `
```


### insert a Java::Geci snipping segment into Asccidoc

Name: `aaa-snip-to-adoc`

```
// snip $snippetName$ snippet="$snippetName$"
//          regex="replace='$replace$'
//                 kill='$killPattern$"
//                 escape='$escape$'"
//          trim="to=0"
//          number="start=1 step=1 from=0 format='%d. ' to=5"
 this is the content of the snippet, will be overwritten
// end snip
```

### insert a Java::Geci snipping segment into HTML/XML

Name: `aaa-snip-to-html`

```
<!-- snip $snippetName$ snippet="$snippetName$" 
          regex="replace='$replace$'
                 kill='$killPattern$"
                 escape='$escape$'"
          trim="to=0"
          number="start=1 step=1 from=0 format='%d. ' to=5"
                 -->
<!-- end snip -->
```

### create a new Java::Geci documentation snippet

Name: `aaa-snippet-java`

```
// snippet $name$
$SELECTION$
// end snippet
$END$
```

## Using live templates

# Accessor Code Generator

The accessor (setter/getter) generator is part of Java::Geci core. It is implemented in the class
`javax0.geci.accessor.Accessor` and can be used to generate setters and getters. To use it you
should create a test

<!-- USE SNIPPET */TestAccessor -->
```java
package javax0.geci.tests.accessor;

import javax0.geci.accessor.Accessor;
import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class TestAccessor {

    @Test
    public void testAccessor() throws Exception {
        if (new Geci().source(maven().module("examples").javaSource()).register(new Accessor()).generate()) {
            Assertions.fail(Geci.FAILED);
        }
    }
}
```

that will generate the code during test time.

To ignite the code generation for a specific class you have to annotate the class as

```java
@Geci("accessor ... parameters ...")
```

The mnemonic of the generator is `accessor`. There is one parameter that the generator reads on the class level
in addition to the standard `id` parameter:
`include`. The value of this parameter should contains the modifiers comma separated for the fields that need
accessors. The default is to create accessor to `private` fields. If the value, for example, is `private,public`
then the generator will create accessor for each `private` and each `public` fields, but not for the `protected`
or package private fields. The possible values that can be listed in this parameter are:

* `private`      
* `public`       
* `protected`    
* `static`       
* `package`     
* `abstract`     
* `final`        
* `interface`    
* `synchronized` 
* `native`       
* `transient`    
* `volatile`

Note that although all these are Java keywords, the keyword `package` is not a modifier, but here we should use this
string to denote package private fields.

The individual fields can also be annotated with

```java
@Geci("accessor ... parameters ...")
```

On the fields the following parameters can be used:

* `exclude` can signal that the field does not need setter and getter. The value of the parameter can be `1`, `ok`, `yes`
  and `true`. Any other value will not exclude the field from the setter/getter generation process. 
* `access` can define the access modifier of the setter and the getter. The default value is `public`. The value of the
  parameter will be used in front of the setter/getter without syntax check.
* `only` can be `setter` or `getter` or it can be omitted. In case it is defined only the setter or only the getter will
  be generated. 
* `id` can be defined to use a different segment for the specific field.

The name of the setter will always be `set` and the name of the field with the first letter upper cased. The name of
the getter will always be `get` and the name of the field with the first letter upper cased.

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
        Assertions.assertFalse(new Geci().source(maven()
                        .module("javageci-examples").mainSource())
                        .register(new Accessor()).generate(),
                Geci.FAILED);
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

Note that although all these are Java keywords, they are used in the configuration as
strings or rather as a part of a string.

The other parameters are read on the field level, and when not specified on the field level they are inherited from
the class annotation if defined there. The individual fields can also be annotated with

```java
@Geci("accessor ... parameters ...")
```

The following parameters can be used on the field level:

* `exclude` can signal that the field does not need setter and getter. The value of the parameter can be `1`, `ok`, `yes`
  and `true`. Any other value will not exclude the field from the setter/getter generation process. Using this parameter
  on the class level may be reasonable when most of the fields do not need accessors. In that case you can define this
  parameter to be `true` on the class level and `false` on the fields that do need accessors to be generated. 
* `access` can define the access modifier of the setter and the getter. The default value is `public`. The value of the
  parameter will be used in front of the setter/getter without syntax check. So, for example you write `access='blabla'`
  it will put `blabla` as an access modifier in front of the generated accessor method and then the Java compilation
  will obviously fail.
* `only` can have the value `setter` or `getter` or it can be omitted. In case it is defined only the setter or 
  only the getter will
  be generated. You can define this parameter on the class level if all or most of the fields need only setters or
  only getters. If there are some fields that need both then you can specify on the field level `only=''`
  (empty string) which will
  not limit the generation on that specific field either to getter or to setter only.
* `id` can be defined to use a different segment for the specific field.

The name of the setter will always be `set` and the name of the field with the first letter upper cased. The name of
the getter will always be `get` and the name of the field with the first letter upper cased.

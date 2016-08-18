# listing
[![unstable](http://badges.github.io/stability-badges/dist/unstable.svg)](http://github.com/badges/stability-badges)
[![travis](https://travis-ci.org/sormuras/listing.svg?branch=master)](https://travis-ci.org/sormuras/listing)
[![jacoco](https://codecov.io/gh/sormuras/listing/branch/master/graph/badge.svg)](https://codecov.io/gh/sormuras/listing)

Java compilation unit source listing tool.

## features
 - [x] JavaBeans style API
 - [x] Aligned to [JLS](https://docs.oracle.com/javase/specs/jls/se8/html/jls-19.html) syntax grammar
 - [x] Runtime compilation supporting custom annotation processors

## hello world
Here's a simple [HelloWorld](https://github.com/sormuras/listing/blob/master/src/test/readme/HelloWorld.java)
program. It demonstrates basic usage of the main features.
```java
Name out = Name.of(System.class, "out");

CompilationUnit unit = CompilationUnit.of("listing");
unit.getImportDeclarations().addSingleStaticImport(out);

NormalClassDeclaration world = unit.declareClass("World");
world.addModifier(Modifier.PUBLIC);

MethodParameter strings = MethodParameter.of(String[].class, "strings");
MethodDeclaration main = world.declareMethod(void.class, "main");
main.addModifiers(Modifier.PUBLIC, Modifier.STATIC);
main.addParameter(strings);
main.addStatement("{N}.println({S} + {getName}[0])", out, "Hello ", strings);

System.out.println(unit.list(b -> b.setOmitJavaLangPackage(true)));

Class<?> hello = unit.compile();
Object[] arguments = {new String[] {"world!"}};
hello.getMethod("main", String[].class).invoke(null, arguments);
```

The console reads like:
```text
package listing;

import static java.lang.System.out;

public class World {

  public static void main(String[] strings) {
    out.println("Hello " + strings[0]);
  }
}

Hello world!
```

## license

```text
Copyright 2016 Christian Stein

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
```

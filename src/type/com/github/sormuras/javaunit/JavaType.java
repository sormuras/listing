/*
 * Copyright (C) 2016 Christian Stein
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.sormuras.javaunit;

import java.lang.annotation.ElementType;
import java.lang.reflect.Type;

/**
 * The Java programming language is a statically typed language, which means that every variable and
 * every expression has a type that is known at compile time.
 * <p>
 * The types of the Java programming language are divided into two categories: primitive types and
 * reference types. The primitive types (§4.2) are the boolean type and the numeric types. The
 * numeric types are the integral types byte, short, int, long, and char, and the floating-point
 * types float and double. The reference types (§4.3) are class types, interface types, and array
 * types. There is also a special null type. An object (§4.3.1) is a dynamically created instance of
 * a class type or a dynamically created array. The values of a reference type are references to
 * objects. All objects, including arrays, support the methods of class Object (§4.3.2). String
 * literals are represented by String objects (§4.3.3).
 *
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html
 * @author Christian Stein
 */
public abstract class JavaType<T extends JavaType<T>> implements Listable, Annotated<T> {

  // raw (not annotated, not generic) class type factory
  static JavaType<?> of(Class<?> c) {
    if (c.isPrimitive()) {
      if (c == void.class) {
        return new VoidType();
      }
      return new PrimitiveType(c);
    }
    if (c.isArray()) {
      for (int dimensions = 1; c.isArray(); dimensions++) {
        c = c.getComponentType();
        if (!c.isArray()) {
          return new ArrayType(of(c), dimensions);
        }
      }
    }
    // TODO return new ClassType(JavaName.of(c));
    throw new AssertionError("Class " + c.getCanonicalName() + " not supported, yet");
  }

  static JavaType<?> of(Type type) {
    if (type instanceof Class) {
      return of((Class<?>) type);
    }
    throw new AssertionError("Type " + type.getTypeName() + " not supported, yet");
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    return hashCode() == obj.hashCode();
  }

  @Override
  public ElementType getAnnotationTarget() {
    return ElementType.TYPE_USE;
  }

  @Override
  public int hashCode() {
    return list().hashCode();
  }
}

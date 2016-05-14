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
package com.github.sormuras.listing;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.AnnotatedWildcardType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

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

  public static JavaType<?> of(AnnotatedType annotatedType) {
    if (annotatedType instanceof AnnotatedArrayType) {
      return JavaTypes.of((AnnotatedArrayType) annotatedType);
    }
    if (annotatedType instanceof AnnotatedParameterizedType) {
      return JavaTypes.of((AnnotatedParameterizedType) annotatedType);
    }
    if (annotatedType instanceof AnnotatedTypeVariable) {
      return JavaTypes.of((AnnotatedTypeVariable) annotatedType);
    }
    if (annotatedType instanceof AnnotatedWildcardType) {
      return JavaTypes.of((AnnotatedWildcardType) annotatedType);
    }
    // default case: use underlying raw type
    JavaType<?> result = of(annotatedType.getType());
    Annotation[] annotations = annotatedType.getAnnotations();
    result.getAnnotations().addAll(JavaAnnotation.of(annotations));
    return result;
  }

  // raw (not annotated, not generic) class type factory
  public static JavaType<?> of(Class<?> c) {
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
    return new ClassType(JavaName.of(c));
  }

  public static JavaType<?> of(Type type) {
    if (type instanceof GenericArrayType) {
      return JavaTypes.of((GenericArrayType) type);
    }
    if (type instanceof ParameterizedType) {
      return JavaTypes.of((ParameterizedType) type);
    }
    if (type instanceof TypeVariable<?>) {
      return JavaTypes.of((TypeVariable<?>) type);
    }
    if (type instanceof WildcardType) {
      return JavaTypes.of((WildcardType) type);
    }
    return of((Class<?>) type);
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

  public boolean isJavaLangObject() {
    return false;
  }

  /**
   * @see Class#getName()
   * @see Class#forName(String)
   * @return (binary) class name
   */
  public String toClassName() {
    throw new UnsupportedOperationException(getClass() + " does not support toClassName()");
  }
}

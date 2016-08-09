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

package com.github.sormuras.listing.type;

import com.github.sormuras.listing.Annotatable;
import com.github.sormuras.listing.Annotation;
import com.github.sormuras.listing.Name;
import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.AnnotatedWildcardType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

/**
 * The Java programming language is a statically typed language, which means that every variable and
 * every expression has a type that is known at compile time.
 *
 * <p>The types of the Java programming language are divided into two categories: primitive types
 * and reference types. The primitive types (§4.2) are the boolean type and the numeric types. The
 * numeric types are the integral types byte, short, int, long, and char, and the floating-point
 * types float and double. The reference types (§4.3) are class types, interface types, and array
 * types. There is also a special null type. An object (§4.3.1) is a dynamically created instance of
 * a class type or a dynamically created array. The values of a reference type are references to
 * objects. All objects, including arrays, support the methods of class Object (§4.3.2). String
 * literals are represented by String objects (§4.3.3).
 *
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html
 */
public interface JavaType extends Annotatable {

  /** Create {@link JavaType} based on {@link AnnotatedType} instance. */
  public static JavaType of(AnnotatedType annotatedType) {
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
    JavaType result = of(annotatedType.getType());
    result.getAnnotations().addAll(Annotation.of(annotatedType.getAnnotations()));
    return result;
  }

  /**
   * Create {@link JavaType} based on {@link Class} instance.
   *
   * @return raw (not annotated, not generic) JavaType
   */
  public static JavaType of(Class<?> classType) {
    if (classType.isPrimitive()) {
      if (classType == void.class) {
        return new VoidType();
      }
      return PrimitiveType.of(classType);
    }
    if (classType.isArray()) {
      int dimensions = 1;
      while (true) {
        classType = classType.getComponentType();
        if (!classType.isArray()) {
          return ArrayType.of(classType, dimensions);
        }
        dimensions++;
      }
    }
    return new ClassType(Name.of(classType));
  }

  /**
   * Create {@link JavaType} based on {@link java.lang.reflect.Type} instance.
   *
   * @return potentially annotated and generic JavaType
   */
  public static JavaType of(java.lang.reflect.Type type) {
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
  default ElementType getAnnotationTarget() {
    return ElementType.TYPE_USE;
  }

  default boolean isJavaLangObject() {
    return false;
  }

  /**
   * Returns the name of the type (class, interface, array class, primitive type, or void)
   * represented by this object, as a String.
   *
   * @see Class#getName()
   * @see Class#forName(String)
   * @return (binary) class name
   */
  default String toClassName() {
    throw new UnsupportedOperationException(getClass() + " does not support toClassName()");
  }
}

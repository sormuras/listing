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

import static java.util.Arrays.stream;

import com.github.sormuras.listing.Annotation;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.AnnotatedWildcardType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/** Common <code>JavaType</code> factory collection. */
public interface JavaTypes {

  /** Create {@link JavaType} based on {@link AnnotatedArrayType} instance. */
  static JavaType of(AnnotatedArrayType annotatedType) {
    List<ArrayDimension> dimensions = new ArrayList<>();
    AnnotatedType component = annotatedType;
    while (component instanceof AnnotatedArrayType) {
      ArrayDimension dimension = new ArrayDimension();
      stream(component.getAnnotations()).forEach(a -> dimension.addAnnotation(a));
      dimensions.add(dimension);
      component = ((AnnotatedArrayType) component).getAnnotatedGenericComponentType();
    }
    return ArrayType.of(JavaType.of(component), dimensions);
  }

  /** Create {@link JavaType} based on {@link AnnotatedParameterizedType} instance. */
  static JavaType of(AnnotatedParameterizedType annotatedType) {
    List<TypeArgument> arguments = new ArrayList<>();
    for (AnnotatedType actual : annotatedType.getAnnotatedActualTypeArguments()) {
      arguments.add(new TypeArgument(JavaType.of(actual)));
    }
    ParameterizedType pt = (ParameterizedType) annotatedType.getType();
    ClassType result = (ClassType) JavaType.of(pt.getRawType());
    result.getAnnotations().addAll(Annotation.of(annotatedType.getAnnotations()));
    result.getTypeArguments().addAll(arguments);
    return result;
  }

  /** Create {@link JavaType} based on {@link AnnotatedTypeVariable} instance. */
  static JavaType of(AnnotatedTypeVariable annotatedType) {
    // TODO consider/ignore bounds at type use location
    // AnnotatedTypeVariable atv = (AnnotatedTypeVariable) annotatedType;
    // List<TypeArgument> bounds = new ArrayList<>();
    // for (AnnotatedType bound : atv.getAnnotatedBounds()) {
    // bounds.add(new TypeArgument(of(bound)));
    // }
    String name = ((java.lang.reflect.TypeVariable<?>) annotatedType.getType()).getName();
    TypeVariable result = TypeVariable.of(name);
    result.getAnnotations().addAll(Annotation.of(annotatedType.getAnnotations()));
    return result;
  }

  /** Create {@link JavaType} based on {@link AnnotatedWildcardType} instance. */
  static JavaType of(AnnotatedWildcardType annotatedType) {
    WildcardType result = new WildcardType();
    for (AnnotatedType bound : annotatedType.getAnnotatedLowerBounds()) { // ? super lower bound
      result.setBoundSuper((ReferenceType) JavaType.of(bound));
    }
    for (AnnotatedType bound : annotatedType.getAnnotatedUpperBounds()) { // ? extends upper bound
      result.setBoundExtends((ReferenceType) JavaType.of(bound));
    }
    result.getAnnotations().addAll(Annotation.of(annotatedType.getAnnotations()));
    return result;
  }

  /** Create {@link JavaType} based on {@link AnnotatedWildcardType} instance. */
  static JavaType of(GenericArrayType type) {
    List<ArrayDimension> dimensions = new ArrayList<>();
    java.lang.reflect.Type component = type;
    while (component instanceof GenericArrayType) {
      ArrayDimension dimension = new ArrayDimension();
      dimensions.add(dimension);
      component = ((GenericArrayType) component).getGenericComponentType();
    }
    return ArrayType.of(JavaType.of(component), dimensions);
  }

  /** Create {@link JavaType} based on {@link AnnotatedWildcardType} instance. */
  static JavaType of(ParameterizedType type) {
    List<TypeArgument> arguments = new ArrayList<>();
    for (java.lang.reflect.Type actual : type.getActualTypeArguments()) {
      arguments.add(new TypeArgument(JavaType.of(actual)));
    }
    ClassType result = (ClassType) JavaType.of(type.getRawType());
    result.getTypeArguments().addAll(arguments);
    return result;
  }

  /** Create {@link JavaType} based on {@link java.lang.reflect.TypeVariable} instance. */
  static JavaType of(java.lang.reflect.TypeVariable<?> type) {
    return TypeVariable.of(type.getName());
  }

  /** Create {@link JavaType} based on {@link java.lang.reflect.WildcardType} instance. */
  static JavaType of(java.lang.reflect.WildcardType type) {
    WildcardType result = new WildcardType();
    // ? super lower bound
    for (java.lang.reflect.Type bound : type.getLowerBounds()) {
      result.setBoundSuper((ReferenceType) JavaType.of(bound));
      return result;
    }
    // ? extends upper bound
    for (java.lang.reflect.Type bound : type.getUpperBounds()) {
      result.setBoundExtends((ReferenceType) JavaType.of(bound));
    }
    return result;
  }
}

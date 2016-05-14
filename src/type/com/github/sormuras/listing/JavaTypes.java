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

import static java.util.Arrays.stream;

import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.AnnotatedWildcardType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;

/**
 * Common <code>JavaType</code> factory collection.
 *
 * @author Christian Stein
 */
public interface JavaTypes {

  static JavaType<?> of(AnnotatedArrayType annotatedType) {
    List<ArrayDimension> dimensions = new ArrayList<>();
    AnnotatedType component = annotatedType;
    while (component instanceof AnnotatedArrayType) {
      ArrayDimension dimension = new ArrayDimension();
      stream(component.getAnnotations()).forEach(dimension::addAnnotation);
      dimensions.add(dimension);
      component = ((AnnotatedArrayType) component).getAnnotatedGenericComponentType();
    }
    return new ArrayType(JavaType.of(component), dimensions);
  }

  static JavaType<?> of(AnnotatedParameterizedType annotatedType) {
    List<TypeArgument> arguments = new ArrayList<>();
    for (AnnotatedType actual : annotatedType.getAnnotatedActualTypeArguments()) {
      arguments.add(new TypeArgument(JavaType.of(actual)));
    }
    ParameterizedType pt = (ParameterizedType) annotatedType.getType();
    ClassType result = (ClassType) JavaType.of(pt.getRawType());
    result.getAnnotations().addAll(JavaAnnotation.of(annotatedType.getAnnotations()));
    result.getTypeArguments().addAll(arguments);
    return result;
  }

  static JavaType<?> of(AnnotatedTypeVariable annotatedType) {
    // TODO consider/ignore bounds at type use location
    // AnnotatedTypeVariable atv = (AnnotatedTypeVariable) annotatedType;
    // List<TypeArgument> bounds = new ArrayList<>();
    // for (AnnotatedType bound : atv.getAnnotatedBounds()) {
    // bounds.add(new TypeArgument(of(bound)));
    // }
    TypeVariable result = new TypeVariable();
    result.getAnnotations().addAll(JavaAnnotation.of(annotatedType.getAnnotations()));
    result.setName(((java.lang.reflect.TypeVariable<?>) annotatedType.getType()).getName());
    return result;
  }

  static JavaType<?> of(AnnotatedWildcardType annotatedType) {
    Wildcard result = new Wildcard();
    for (AnnotatedType bound : annotatedType.getAnnotatedLowerBounds()) { // ? super lower bound
      result.setBoundSuper((ReferenceType<?>) JavaType.of(bound));
    }
    for (AnnotatedType bound : annotatedType.getAnnotatedUpperBounds()) { // ? extends upper bound
      result.setBoundExtends((ReferenceType<?>) JavaType.of(bound));
    }
    result.getAnnotations().addAll(JavaAnnotation.of(annotatedType.getAnnotations()));
    return result;
  }

  static JavaType<?> of(GenericArrayType type) {
    List<ArrayDimension> dimensions = new ArrayList<>();
    Type component = type;
    while (component instanceof GenericArrayType) {
      ArrayDimension dimension = new ArrayDimension();
      dimensions.add(dimension);
      component = ((GenericArrayType) component).getGenericComponentType();
    }
    return new ArrayType(JavaType.of(component), dimensions);
  }

  static JavaType<?> of(ParameterizedType type) {
    List<TypeArgument> arguments = new ArrayList<>();
    for (Type actual : type.getActualTypeArguments()) {
      arguments.add(new TypeArgument(JavaType.of(actual)));
    }
    ClassType result = (ClassType) JavaType.of(type.getRawType());
    result.getTypeArguments().addAll(arguments);
    return result;
  }

  static JavaType<?> of(java.lang.reflect.TypeVariable<?> type) {
    TypeVariable result = new TypeVariable();
    result.setName(type.getName());
    return result;
  }

  static JavaType<?> of(WildcardType type) {
    Wildcard result = new Wildcard();
    for (Type bound : type.getLowerBounds()) { // ? super lower bound
      result.setBoundSuper((ReferenceType<?>) JavaType.of(bound));
      return result;
    }
    for (Type bound : type.getUpperBounds()) { // ? extends upper bound
      result.setBoundExtends((ReferenceType<?>) JavaType.of(bound));
    }
    return result;
  }
}

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import com.github.sormuras.listing.*;

public class ArrayType extends ReferenceType {

  public static List<ArrayDimension> createArrayDimensions(int size) {
    List<ArrayDimension> dimensions = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      dimensions.add(new ArrayDimension());
    }
    return dimensions;
  }

  private final JavaType componentType;
  private final List<ArrayDimension> dimensions;

  public ArrayType(JavaType componentType, int size) {
    this(componentType, createArrayDimensions(size));
  }

  public ArrayType(JavaType componentType, List<ArrayDimension> dimensions) {
    this.componentType = componentType;
    this.dimensions = Collections.unmodifiableList(dimensions);
  }

  public ArrayType addAnnotations(int index, Annotation... annotations) {
    dimensions.get(index).getAnnotations().addAll(Arrays.asList(annotations));
    return this;
  }

  @Override
  public Listing apply(Listing listing) {
    return listing.add(getComponentType()).add(getDimensions(), Listable.IDENTITY);
  }

  @Override
  public List<Annotation> getAnnotations(boolean readonly) {
    return dimensions.get(0).getAnnotations(readonly);
  }

  public JavaType getComponentType() {
    return componentType;
  }

  public List<ArrayDimension> getDimensions() {
    return dimensions;
  }

  @Override
  public String toClassName() {
    StringBuilder builder = new StringBuilder();
    IntStream.range(0, getDimensions().size()).forEach(i -> builder.append('['));
    if (getComponentType() instanceof PrimitiveType) {
      PrimitiveType primitive = (PrimitiveType) getComponentType();
      if (primitive.getType() == boolean.class) return builder.append('Z').toString();
      if (primitive.getType() == byte.class) return builder.append('B').toString();
      if (primitive.getType() == char.class) return builder.append('C').toString();
      if (primitive.getType() == double.class) return builder.append('D').toString();
      if (primitive.getType() == float.class) return builder.append('F').toString();
      if (primitive.getType() == int.class) return builder.append('I').toString();
      if (primitive.getType() == long.class) return builder.append('J').toString();
      /* if (primitive.getType() == short.class) */ return builder.append('S').toString();
    }
    return builder.append('L').append(getComponentType().toClassName()).append(';').toString();
  }
}

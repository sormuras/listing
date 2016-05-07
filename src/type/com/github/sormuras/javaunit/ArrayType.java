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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ArrayType extends ReferenceType<ArrayType> {

  public static List<ArrayDimension> createArrayDimensions(int size) {
    List<ArrayDimension> dimensions = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      dimensions.add(new ArrayDimension());
    }
    return dimensions;
  }

  private final JavaType<?> componentType;
  private final List<ArrayDimension> dimensions;

  public ArrayType(JavaType<?> componentType, int size) {
    this(componentType, createArrayDimensions(size));
  }

  public ArrayType(JavaType<?> componentType, List<ArrayDimension> dimensions) {
    this.componentType = componentType;
    this.dimensions = Collections.unmodifiableList(dimensions);
  }

  public ArrayType addAnnotations(int index, JavaAnnotation... annotations) {
    dimensions.get(index).getAnnotations().addAll(Arrays.asList(annotations));
    return this;
  }

  @Override
  public Listing apply(Listing listing) {
    return listing.add(getComponentType()).add(getDimensions(), Listable.IDENTITY);
  }

  @Override
  public List<JavaAnnotation> getAnnotations() {
    return dimensions.get(0).getAnnotations();
  }

  public JavaType<?> getComponentType() {
    return componentType;
  }

  public List<ArrayDimension> getDimensions() {
    return dimensions;
  }
}

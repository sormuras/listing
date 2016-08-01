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

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.sormuras.listing.*;

/** Simple and(!) annotatable and(!) typed class or interface name. */
public class ClassName implements Listable, Annotatable {

  private List<Annotation> annotations = Collections.emptyList();
  private final String name;
  private final List<TypeArgument> typeArguments = new ArrayList<>();

  public ClassName(String name, Annotation... annotations) {
    this(name, Arrays.asList(annotations));
  }

  public ClassName(String name, List<Annotation> annotations) {
    this.name = name;
    if (!annotations.isEmpty()) {
      getAnnotations().addAll(annotations);
    }
  }

  @Override
  public Listing apply(Listing listing) {
    listing.add(toAnnotationsListable());
    if (getTypeArguments().isEmpty()) {
      return listing.add(getName());
    }
    listing.add(getName()).add('<').add(getTypeArguments(), ", ").add('>');
    return listing;
  }

  @Override
  public List<Annotation> getAnnotations(boolean readonly) {
    if (annotations == Collections.EMPTY_LIST && !readonly) {
      annotations = new ArrayList<>();
    }
    return annotations;
  }

  @Override
  public ElementType getAnnotationTarget() {
    return ElementType.TYPE_USE;
  }

  public String getName() {
    return name;
  }

  public List<TypeArgument> getTypeArguments() {
    return typeArguments;
  }
}

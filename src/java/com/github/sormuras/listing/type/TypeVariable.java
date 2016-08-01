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

import static java.util.Objects.requireNonNull;

import com.github.sormuras.listing.Annotation;
import com.github.sormuras.listing.Listing;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A type variable is an unqualified identifier used as a type in class, interface, method, and
 * constructor bodies.
 *
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.4
 */
public class TypeVariable extends ReferenceType {

  private List<Annotation> annotations = Collections.emptyList();
  private String name = "*";

  public TypeVariable(String name) {
    setName(name);
  }

  @Override
  public Listing apply(Listing listing) {
    return listing.add(toAnnotationsListable()).add(getName());
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
    return ElementType.TYPE_PARAMETER;
  }

  public String getName() {
    return name;
  }

  public TypeVariable setName(String name) {
    requireNonNull(name, "name");
    if (name.isEmpty()) {
      throw new IllegalArgumentException("TypeVariable name must not be empty!");
    }
    this.name = name;
    return this;
  }
}

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

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** Base {@link Annotation}-collecting implementation. */
public abstract class Annotated implements Listable {

  private List<Annotation> annotations = Collections.emptyList();

  public void addAnnotation(Annotation annotation) {
    getAnnotations().add(annotation);
  }

  public void addAnnotation(
      Class<? extends java.lang.annotation.Annotation> annotation, Object... values) {
    addAnnotation(Annotation.of(annotation, values));
  }

  public void addAnnotation(java.lang.annotation.Annotation annotation) {
    addAnnotation(Annotation.of(annotation));
  }

  public void addAnnotations(Annotation... annotations) {
    addAnnotations(Arrays.asList(annotations));
  }

  public void addAnnotations(Collection<Annotation> annotations) {
    getAnnotations().addAll(annotations);
  }

  public List<Annotation> getAnnotations() {
    if (annotations == Collections.EMPTY_LIST) {
      annotations = new ArrayList<>();
    }
    return annotations;
  }

  /** Return listable separator depending on the annotation target element type. */
  public Listable getAnnotationSeparator() {
    ElementType target = getAnnotationTarget();
    boolean inline =
        target == ElementType.TYPE_PARAMETER
            || target == ElementType.TYPE_USE
            || target == ElementType.PARAMETER;
    return inline ? Listable.SPACE : Listable.NEWLINE;
  }

  public abstract ElementType getAnnotationTarget();

  public boolean isAnnotated() {
    return !annotations.isEmpty();
  }

  public void setAnnotations(Annotation... annotations) {
    getAnnotations().clear();
    addAnnotations(annotations);
  }

  public Listable toAnnotationsListable() {
    if (isAnnotated()) {
      Listable separator = getAnnotationSeparator();
      return listing -> listing.add(getAnnotations(), separator).add(separator);
    }
    return Listable.IDENTITY;
  }
}

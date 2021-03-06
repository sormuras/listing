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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/** Default {@link Annotation} support. */
public interface Annotatable extends Listable {

  default void addAnnotation(Annotation annotation) {
    getAnnotations().add(annotation);
  }

  default void addAnnotation(
      Class<? extends java.lang.annotation.Annotation> annotation, Object... values) {
    addAnnotation(Annotation.of(annotation, values));
  }

  default void addAnnotation(java.lang.annotation.Annotation annotation) {
    addAnnotation(Annotation.of(annotation));
  }

  default void addAnnotations(Annotation... annotations) {
    addAnnotations(Arrays.asList(annotations));
  }

  default void addAnnotations(Collection<Annotation> annotations) {
    getAnnotations().addAll(annotations);
  }

  List<Annotation> getAnnotations();

  /** Return listable separator depending on the annotation target element type. */
  default Listable getAnnotationSeparator() {
    ElementType target = getAnnotationTarget();
    boolean inline =
        target == ElementType.TYPE_PARAMETER
            || target == ElementType.TYPE_USE
            || target == ElementType.PARAMETER;
    return inline ? Listable.SPACE : Listable.NEWLINE;
  }

  ElementType getAnnotationTarget();

  boolean isAnnotated();

  default void setAnnotations(Annotation... annotations) {
    getAnnotations().clear();
    addAnnotations(annotations);
  }

  default Listable toAnnotationsListable() {
    if (isAnnotated()) {
      Listable separator = getAnnotationSeparator();
      return listing -> listing.add(getAnnotations(), separator).add(separator);
    }
    return Listable.IDENTITY;
  }
}

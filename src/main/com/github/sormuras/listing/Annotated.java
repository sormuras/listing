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
import java.util.List;

import com.github.sormuras.listing.Listable;

/**
 * Provides default methods for handling a list of {@linkplain JavaAnnotation}s.
 *
 * @author Christian Stein
 */
public interface Annotated<T> {

  default T addAnnotation(Annotation annotation) {
    return addAnnotation(JavaAnnotation.of(annotation));
  }

  @SuppressWarnings("unchecked")
  default T addAnnotation(JavaAnnotation annotation) {
    getAnnotations().add(annotation);
    return (T) this;
  }

  default T addAnnotation(Class<? extends Annotation> annotation, Object... values) {
    return addAnnotation(JavaAnnotation.of(annotation, values));
  }

  default T addAnnotation(String packageName, String name) {
    return addAnnotation(new JavaAnnotation(JavaName.of(packageName, name)));
  }

  List<JavaAnnotation> getAnnotations();

  default Listable getAnnotationSeparator() {
    ElementType target = getAnnotationTarget();
    boolean inline = target == ElementType.TYPE_PARAMETER || target == ElementType.TYPE_USE;
    return inline ? Listable.SPACE : Listable.NEWLINE;
  }

  ElementType getAnnotationTarget();

  default boolean isAnnotated() {
    return !getAnnotations().isEmpty();
  }

  default Listable toAnnotationsListable() {
    if (isAnnotated()) {
      Listable separator = getAnnotationSeparator();
      return listing -> listing.add(getAnnotations(), separator).add(separator);
    }
    return Listable.IDENTITY;
  }
}

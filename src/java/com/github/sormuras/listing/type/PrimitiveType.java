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

import com.github.sormuras.listing.Annotation;
import com.github.sormuras.listing.Listing;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A primitive type is predefined by the Java language and named by its reserved keyword.
 *
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.2
 */
public class PrimitiveType extends JavaType {

  private List<Annotation> annotations = Collections.emptyList();
  private final Class<?> type;

  public PrimitiveType(Class<?> type) {
    Objects.requireNonNull(type, "type");
    if (type == void.class) {
      throw new AssertionError("expected primitive type, got " + type);
    }
    if (!type.isPrimitive()) {
      throw new AssertionError("expected primitive type, got " + type);
    }
    this.type = type;
  }

  @Override
  public Listing apply(Listing listing) {
    return listing.add(toAnnotationsListable()).add(getType().getTypeName());
  }

  @Override
  public List<Annotation> getAnnotations(boolean readonly) {
    if (annotations == Collections.EMPTY_LIST && !readonly) {
      annotations = new ArrayList<>();
    }
    return annotations;
  }

  public Class<?> getType() {
    return type;
  }

  @Override
  public String toClassName() {
    return getType().getName();
  }
}

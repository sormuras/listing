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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Base {@link Annotation}-collecting implementation. */
public abstract class Annotated implements Annotatable {

  private List<Annotation> annotations = Collections.emptyList();

  @Override
  public List<Annotation> getAnnotations() {
    if (annotations == Collections.EMPTY_LIST) {
      annotations = new ArrayList<>();
    }
    return annotations;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    return hashCode() == obj.hashCode();
  }

  @Override
  public int hashCode() {
    return list().hashCode();
  }

  @Override
  public boolean isAnnotated() {
    return !annotations.isEmpty();
  }
}

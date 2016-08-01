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

import com.github.sormuras.listing.*;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArrayDimension implements Listable, Annotatable {

  private List<Annotation> annotations = Collections.emptyList();

  @Override
  public Listing apply(Listing listing) {
    return listing.add(toAnnotationsListable()).add("[]");
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
}

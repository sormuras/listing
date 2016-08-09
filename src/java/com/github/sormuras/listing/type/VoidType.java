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

import com.github.sormuras.listing.Annotated;
import com.github.sormuras.listing.Annotation;
import com.github.sormuras.listing.Listing;
import java.lang.annotation.ElementType;
import java.util.Collections;
import java.util.List;

public class VoidType extends Annotated implements JavaType {

  @Override
  public Listing apply(Listing listing) {
    return listing.add("void");
  }

  @Override
  public List<Annotation> getAnnotations() {
    return Collections.emptyList();
  }

  @Override
  public ElementType getAnnotationTarget() {
    return null;
  }

  @Override
  public boolean isAnnotated() {
    return false;
  }

  @Override
  public String toClassName() {
    return "void";
  }
}

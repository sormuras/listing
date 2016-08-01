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
package com.github.sormuras.listing.unit;

import com.github.sormuras.listing.Listing;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class declaration specifies a new named reference type.
 *
 * <p>There are two kinds of class declarations: normal class declarations and enum declarations.
 *
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.1
 */
public abstract class ClassDeclaration extends TypeDeclaration {

  private final String keyword;

  private boolean local = false;
  private List<Initializer> initializers = Collections.emptyList();

  public ClassDeclaration(String keyword) {
    this.keyword = keyword;
  }

  @Override
  public Listing apply(Listing listing) {
    if (!isLocal()) {
      listing.newline();
    }
    listing.add(keyword).add(' ').add(getName());
    listing.add(' ').add('{').newline();
    listing.indent(1);
    if (!isDeclarationsEmpty()) {
      getDeclarations().forEach(listing::add);
    }
    if (!isInitializersEmpty()) {
      getInitializers().forEach(listing::add);
    }
    listing.indent(-1).add('}').newline();
    return listing;
  }

  public Initializer declareInitializer(boolean staticInitializer) {
    Initializer initializer = new Initializer();
    initializer.setEnclosing(this);
    initializer.setStatic(staticInitializer);
    getInitializers().add(initializer);
    return initializer;
  }

  public List<Initializer> getInitializers() {
    if (initializers == Collections.EMPTY_LIST) {
      initializers = new ArrayList<>();
    }
    return initializers;
  }

  public boolean isInitializersEmpty() {
    return initializers.isEmpty();
  }

  public boolean isLocal() {
    return local;
  }

  public void setLocal(boolean local) {
    this.local = local;
  }
}
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
package de.codeturm.listing;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

public class ClassDeclaration extends TypeDeclaration {

  private boolean local = false;
  private List<Initializer> initializers;

  @Override
  public Lines apply(Lines lines) {
    Object previouslyBoundDeclaration = lines.getBinding().put("${declaration}", this);
    if (!isLocal()) {
      lines.newline();
    }
    lines.add("class").add(' ').add(getName());
    lines.add(' ').add('{').newline();
    lines.indent(1);
    if (!isDeclarationsEmpty()) {
      getDeclarations().forEach(lines::add);
    }
    if (!isInitializersEmpty()) {
      getInitializers().forEach(lines::add);
    }
    lines.indent(-1).add('}').newline();
    lines.getBinding().put("${declaration}", previouslyBoundDeclaration);
    return lines;
  }

  public Initializer declareInitializer(boolean staticInitializer) {
    Initializer initializer = new Initializer();
    initializer.setEnclosing(this);
    if (staticInitializer) {
      initializer.addModifier(Modifier.STATIC);
    }
    getInitializers().add(initializer);
    return initializer;
  }

  public List<Initializer> getInitializers() {
    if (initializers == null) {
      initializers = new ArrayList<>();
    }
    return initializers;
  }

  public boolean isInitializersEmpty() {
    return initializers == null || initializers.isEmpty();
  }

  public boolean isLocal() {
    return local;
  }

  public void setLocal(boolean local) {
    this.local = local;
  }

  @Override
  public String toString() {
    return getName();
  }
}

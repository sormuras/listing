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

import com.github.sormuras.listing.Annotatable;
import com.github.sormuras.listing.Annotation;
import com.github.sormuras.listing.Listable;
import com.github.sormuras.listing.Modifiable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.lang.model.element.Modifier;

/**
 * Class <b>member</b> declaration.
 *
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.1.6
 */
public abstract class ClassMemberDeclaration implements Annotatable, Listable, Modifiable {

  private List<Annotation> annotations = new ArrayList<>();
  private CompilationUnit compilationUnit = null;
  private TypeDeclaration enclosingType = null;
  private Set<Modifier> modifiers = EnumSet.noneOf(Modifier.class);
  private String name;

  @Override
  public List<Annotation> getAnnotations(boolean readonly) {
    return annotations;
  }

  public Optional<CompilationUnit> getCompilationUnit() {
    return Optional.ofNullable(compilationUnit);
  }

  public Optional<TypeDeclaration> getEnclosingDeclaration() {
    return Optional.ofNullable(enclosingType);
  }

  @Override
  public Set<Modifier> getModifiers(boolean readonly) {
    return modifiers;
  }

  public String getName() {
    return name;
  }

  public void setCompilationUnit(CompilationUnit unit) {
    this.compilationUnit = unit;
  }

  public void setEnclosingDeclaration(TypeDeclaration enclosingType) {
    this.enclosingType = enclosingType;
  }

  public void setName(String name) {
    this.name = name;
  }
}

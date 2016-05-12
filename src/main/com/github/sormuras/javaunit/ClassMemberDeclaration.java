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
package com.github.sormuras.javaunit;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.lang.model.element.Modifier;

/**
 * Class <b>member</b> declaration.
 *
 * @author Christian Stein
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.1.6
 */
public abstract class ClassMemberDeclaration<T> implements Annotated<T>, Modified<T> {

  private List<JavaAnnotation> annotations = new ArrayList<>();
  private JavaUnit unit = null;
  private TypeDeclaration<?> enclosingType = null;
  private Set<Modifier> modifiers = EnumSet.noneOf(Modifier.class);
  private String name;

  @Override
  public List<JavaAnnotation> getAnnotations() {
    return annotations;
  }

  public Optional<JavaUnit> getUnit() {
    return Optional.ofNullable(unit);
  }

  public Optional<TypeDeclaration<?>> getEnclosingType() {
    return Optional.ofNullable(enclosingType);
  }

  @Override
  public Set<Modifier> getModifiers() {
    return modifiers;
  }

  public String getName() {
    return name;
  }

  @SuppressWarnings("unchecked")
  public T setUnit(JavaUnit unit) {
    this.unit = unit;
    return (T) this;
  }

  @SuppressWarnings("unchecked")
  public T setEnclosingType(TypeDeclaration<?> enclosingType) {
    this.enclosingType = enclosingType;
    return (T) this;
  }

  @SuppressWarnings("unchecked")
  public T setName(String name) {
    this.name = name;
    return (T) this;
  }
}

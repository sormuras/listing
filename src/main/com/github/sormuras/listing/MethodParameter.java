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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.sormuras.listing.ArrayType;
import com.github.sormuras.listing.JavaType;
import com.github.sormuras.listing.Listable;
import com.github.sormuras.listing.Listing;

/**
 * The formal parameters of a method or constructor, if any, are specified by a list of
 * comma-separated parameter specifiers. Each parameter specifier consists of a type (optionally
 * preceded by the final modifier and/or one or more annotations) and an identifier (optionally
 * followed by brackets) that specifies the name of the parameter.
 *
 * @author Christian Stein
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.4.1
 */
public class MethodParameter implements Listable, Annotated<MethodParameter> {

  public static MethodParameter of(Class<?> type, String name) {
    return new MethodParameter().setType(JavaType.of(type)).setName(name);
  }

  private final List<JavaAnnotation> annotations = new ArrayList<>();
  private boolean finalModifier;
  private MethodDeclaration methodDeclaration;
  private String name;
  private JavaType<?> type;
  private boolean variable;

  @Override
  public Listing apply(Listing listing) {
    if (isFinal()) {
      listing.add("final ");
    }
    listing.add(toAnnotationsListable());
    if (isVariable()) {
      ArrayType arrayType = (ArrayType) type; // throws ClassCastException
      int toIndex = arrayType.getDimensions().size() - 1;
      listing.add(arrayType.getComponentType());
      listing.add(arrayType.getDimensions().subList(0, toIndex), Listable.IDENTITY);
      listing.add("...");
    } else {
      listing.add(getType());
    }
    listing.add(' ').add(getName());
    return listing;
  }

  @Override
  public List<JavaAnnotation> getAnnotations() {
    return annotations;
  }

  @Override
  public ElementType getAnnotationTarget() {
    return ElementType.PARAMETER;
  }

  public Optional<MethodDeclaration> getMethodDeclaration() {
    return Optional.ofNullable(methodDeclaration);
  }

  public String getName() {
    return name;
  }

  public JavaType<?> getType() {
    return type;
  }

  public boolean isFinal() {
    return finalModifier;
  }

  public boolean isVariable() {
    return variable;
  }

  public MethodParameter setFinal(boolean finalModifier) {
    this.finalModifier = finalModifier;
    return this;
  }

  public MethodParameter setMethodDeclaration(MethodDeclaration methodDeclaration) {
    this.methodDeclaration = methodDeclaration;
    return this;
  }

  public MethodParameter setName(String name) {
    this.name = name;
    return this;
  }

  public MethodParameter setType(JavaType<?> type) {
    this.type = type;
    return this;
  }

  public MethodParameter setVariable(boolean variable) {
    if (variable && !(getType() instanceof ArrayType)) {
      throw new IllegalStateException("array type expected, got: " + getType());
    }
    this.variable = variable;
    return this;
  }
}

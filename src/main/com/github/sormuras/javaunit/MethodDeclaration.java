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

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Method declaration.
 *
 * @author Christian Stein
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.4
 */
public class MethodDeclaration extends ClassMemberDeclaration<MethodDeclaration> {

  private Listable body = null;
  private List<Listable> bodyStatements = new ArrayList<>();
  private List<MethodParameter> parameters = new ArrayList<>();
  private JavaType<?> returnType = JavaType.of(void.class);
  private List<ReferenceType<?>> throwables = new ArrayList<>();
  private List<TypeParameter> typeParameters = new ArrayList<>();

  public MethodDeclaration addParameter(Class<?> type, String name) {
    return addParameter(MethodParameter.of(type, name));
  }

  public MethodDeclaration addParameter(MethodParameter declaration) {
    declaration.setMethodDeclaration(this);
    getParameters().add(declaration);
    return this;
  }

  public MethodDeclaration addStatement(String line) {
    bodyStatements.add(l -> l.add(line).add(';'));
    return this;
  }

  public MethodDeclaration addThrows(Class<?> type) {
    return addThrows((ClassType) JavaType.of(type));
  }

  public MethodDeclaration addThrows(ClassType type) {
    getThrows().add(type);
    return this;
  }

  public MethodDeclaration addThrows(TypeVariable type) {
    getThrows().add(type);
    return this;
  }

  public MethodDeclaration addTypeParameter(TypeParameter typeParameter) {
    getTypeParameters().add(typeParameter);
    return this;
  }

  @Override
  public Listing apply(Listing listing) {
    listing.newline();
    listing.add(toAnnotationsListable());
    listing.add(toModifiersListable());
    if (!getTypeParameters().isEmpty()) {
      listing.add('<');
      listing.add(getTypeParameters(), ", ");
      listing.add("> ");
    }
    if (getName().equals("<init>")) {
      listing.add(getEnclosingType().get().getName());
    } else {
      listing.add(getReturnType());
      listing.add(' ');
      listing.add(getName());
    }
    listing.add('(');
    listing.add(getParameters(), ", ");
    listing.add(')');
    if (!getThrows().isEmpty()) {
      listing.add(" throws ");
      listing.add(getThrows(), ", ");
    }
    if (getBody().isPresent()) {
      listing.add(" {").newline().indent(1);
      listing.add(getBody().get());
      listing.indent(-1).add('}');
    } else if (!bodyStatements.isEmpty()) {
      listing.add(" {").newline().indent(1);
      listing.add(bodyStatements, Listable.NEWLINE);
      listing.newline();
      listing.indent(-1).add('}');
    }
    listing.newline();
    return listing;
  }

  @Override
  public ElementType getAnnotationTarget() {
    return ElementType.METHOD;
  }

  public Optional<Listable> getBody() {
    return Optional.ofNullable(body);
  }

  public List<MethodParameter> getParameters() {
    return parameters;
  }

  public JavaType<?> getReturnType() {
    return returnType;
  }

  public List<ReferenceType<?>> getThrows() {
    return throwables;
  }

  public List<TypeParameter> getTypeParameters() {
    return typeParameters;
  }

  public boolean isVarArgs() {
    if (getParameters().isEmpty()) return false;
    return getParameters().get(getParameters().size() - 1).isVariable();
  }

  public MethodDeclaration setBody(Listable body) {
    this.body = body;
    return this;
  }

  public MethodDeclaration setReturnType(JavaType<?> type) {
    this.returnType = type;
    return this;
  }

  public MethodDeclaration setVarArgs(boolean variable) {
    if (getParameters().isEmpty()) throw new IllegalStateException("no parameters defined");
    getParameters().get(getParameters().size() - 1).setVariable(variable);
    return this;
  }
}

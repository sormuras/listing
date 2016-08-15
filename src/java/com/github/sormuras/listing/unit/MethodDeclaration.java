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

import com.github.sormuras.listing.Listable;
import com.github.sormuras.listing.Listing;
import com.github.sormuras.listing.type.ClassType;
import com.github.sormuras.listing.type.JavaType;
import com.github.sormuras.listing.type.ReferenceType;
import com.github.sormuras.listing.type.TypeVariable;
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
public class MethodDeclaration extends ClassMemberDeclaration {

  private Block body = null;
  private List<Listable> bodyStatements = new ArrayList<>();
  private List<MethodParameter> parameters = new ArrayList<>();
  private JavaType returnType = JavaType.of(void.class);
  private List<ReferenceType> throwables = new ArrayList<>();
  private List<TypeParameter> typeParameters = new ArrayList<>();

  public void addParameter(Class<?> type, String name) {
    addParameter(MethodParameter.of(type, name));
  }

  public void addParameter(MethodParameter declaration) {
    declaration.setMethodDeclaration(this);
    getParameters().add(declaration);
  }

  public void addStatement(String line) {
    bodyStatements.add(l -> l.add(line).add(';'));
  }

  public void addStatement(String source, Object... args) {
    bodyStatements.add(l -> l.add(source, args).add(';'));
  }

  public void addThrows(Class<?> type) {
    addThrows((ClassType) JavaType.of(type));
  }

  public void addThrows(ClassType type) {
    getThrows().add(type);
  }

  public void addThrows(TypeVariable type) {
    getThrows().add(type);
  }

  public void addTypeParameter(TypeParameter typeParameter) {
    getTypeParameters().add(typeParameter);
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
    if (isConstructor()) {
      if (getEnclosingDeclaration() != null) {
        listing.add(getEnclosingDeclaration().getName());
      } else {
        listing.add("<init>");
      }
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
      listing.add(' ');
      listing.add(getBody().get());
    } else if (!bodyStatements.isEmpty()) {
      listing.add(" {").newline().indent(1);
      listing.add(bodyStatements, Listable.NEWLINE);
      listing.newline();
      listing.indent(-1).add('}');
      listing.newline();
    } else {
      listing.add(';');
      listing.newline();
    }
    return listing;
  }

  @Override
  public ElementType getAnnotationTarget() {
    return ElementType.METHOD;
  }

  public Optional<Block> getBody() {
    return Optional.ofNullable(body);
  }

  public List<MethodParameter> getParameters() {
    return parameters;
  }

  public JavaType getReturnType() {
    return returnType;
  }

  public List<ReferenceType> getThrows() {
    return throwables;
  }

  public List<TypeParameter> getTypeParameters() {
    return typeParameters;
  }

  public boolean isConstructor() {
    return "<init>".equals(getName());
  }

  public boolean isVarArgs() {
    if (getParameters().isEmpty()) {
      return false;
    }
    return getParameters().get(getParameters().size() - 1).isVariable();
  }

  public void setBody(Block body) {
    this.body = body;
  }

  public void setReturnType(JavaType type) {
    this.returnType = type;
  }

  public void setVarArgs(boolean variable) {
    if (getParameters().isEmpty()) {
      throw new IllegalStateException("no parameters defined");
    }
    getParameters().get(getParameters().size() - 1).setVariable(variable);
  }
}

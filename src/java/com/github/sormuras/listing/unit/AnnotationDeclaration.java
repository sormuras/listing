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

import com.github.sormuras.listing.Annotation;
import com.github.sormuras.listing.Listable;
import com.github.sormuras.listing.Listing;
import com.github.sormuras.listing.type.JavaType;
import java.util.ArrayList;
import java.util.List;

/**
 * An annotation type declaration specifies a new annotation type, a special kind of interface type.
 * To distinguish an annotation type declaration from a normal interface declaration, the keyword
 * {@code interface} is preceded by an at-sign <code>@</code>.
 *
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.6
 */
public class AnnotationDeclaration extends TypeDeclaration {

  private final List<ConstantDeclaration> constants = new ArrayList<>();
  private final List<AnnotationElement> elements = new ArrayList<>();

  public AnnotationDeclaration() {
    this("AnnotationDeclaration");
  }

  public AnnotationDeclaration(String name) {
    setName(name);
  }

  /** Add new annotation field. */
  public ConstantDeclaration addConstant(JavaType type, String name, Listable initializer) {
    ConstantDeclaration constants = new ConstantDeclaration();
    constants.setName(name);
    constants.setType(type);
    constants.setInitializer(initializer);
    getConstants().add(constants);
    return constants;
  }

  /** Add new annotation field. */
  public ConstantDeclaration addConstant(JavaType type, String name, Object value) {
    return addConstant(type, name, Annotation.value(value));
  }

  /** Add new annotation method w/o default value. */
  public AnnotationElement addElement(JavaType returnType, String name) {
    return addElement(returnType, name, (Listable) null);
  }

  /** Add new annotation method with default value. */
  public AnnotationElement addElement(JavaType returnType, String name, Listable defaultValue) {
    AnnotationElement element = new AnnotationElement();
    element.setName(name);
    element.setReturnType(returnType);
    element.setDefaultValue(defaultValue);
    getElements().add(element);
    return element;
  }

  /** Add new annotation method with default value. */
  public AnnotationElement addElement(JavaType returnType, String name, Object defaultValue) {
    return addElement(returnType, name, Annotation.value(defaultValue));
  }

  @Override
  public Listing apply(Listing listing) {
    listing.newline();
    listing.add(toAnnotationsListable());
    listing.add(toModifiersListable());
    listing.add("@interface").add(' ').add(getName());
    listing.add(' ').add('{').newline();
    listing.indent(1);
    if (!isDeclarationsEmpty()) {
      getDeclarations().forEach(listing::add);
    }
    if (!getConstants().isEmpty()) {
      getConstants().forEach(listing::add);
    }
    if (!getElements().isEmpty()) {
      getElements().forEach(listing::add);
    }
    listing.indent(-1).add('}').newline();
    return listing;
  }

  public List<ConstantDeclaration> getConstants() {
    return constants;
  }

  public List<AnnotationElement> getElements() {
    return elements;
  }
}

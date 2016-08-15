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

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor8;

/** Common {@link JavaType} factory collection parsing {@link javax.lang.model.type.TypeMirror}s. */
public interface JavaMirrors {

  class Visitor extends SimpleTypeVisitor8<JavaType, Object> {

    @Override
    public JavaType visitDeclared(javax.lang.model.type.DeclaredType type, Object tag) {
      return of(type);
    }

    @Override
    public JavaType visitNoType(javax.lang.model.type.NoType type, Object tag) {
      return of(type);
    }

    @Override
    public JavaType visitPrimitive(javax.lang.model.type.PrimitiveType type, Object tag) {
      return of(type);
    }

    @Override
    public JavaType visitWildcard(javax.lang.model.type.WildcardType type, Object tag) {
      return of(type);
    }
  }

  /** Create {@link ClassType} based on {@link javax.lang.model.type.DeclaredType} instance. */
  static ClassType of(javax.lang.model.type.DeclaredType type) {
    ClassType classType = new ClassType();
    // extract package name
    Element packageElement = type.asElement();
    while (packageElement.getKind() != ElementKind.PACKAGE) {
      packageElement = packageElement.getEnclosingElement();
    }
    PackageElement casted = (PackageElement) packageElement;
    classType.setPackageName(casted.getQualifiedName().toString());
    // extract simple names and type arguments
    TypeMirror actualType = type;
    for (Element e = type.asElement();
        e.getKind().isClass() || e.getKind().isInterface();
        e = e.getEnclosingElement()) {
      ClassName name = new ClassName();
      name.setName(e.getSimpleName().toString());
      classType.getNames().add(0, name);
      if ((actualType instanceof javax.lang.model.type.DeclaredType)) {
        javax.lang.model.type.DeclaredType dt = (javax.lang.model.type.DeclaredType) actualType;
        for (TypeMirror ta : dt.getTypeArguments()) {
          name.getTypeArguments().add(TypeArgument.of(JavaMirrors.of(ta)));
        }
        actualType = dt.getEnclosingType();
      }
    }
    return classType;
  }

  /** Create {@link JavaType} based on {@link javax.lang.model.type.NoType} instance. */
  static JavaType of(javax.lang.model.type.NoType type) {
    if (type.getKind() == TypeKind.VOID) {
      return new VoidType();
    }
    throw new AssertionError("Unsupported no type: " + type.getKind());
  }

  /** Create {@link PrimitiveType} based on {@link javax.lang.model.type.PrimitiveType} instance. */
  static PrimitiveType of(javax.lang.model.type.PrimitiveType type) {
    TypeKind kind = type.getKind();
    if (kind == TypeKind.BOOLEAN) {
      return new PrimitiveType.BooleanType();
    }
    if (kind == TypeKind.INT) {
      return new PrimitiveType.IntType();
    }
    if (kind == TypeKind.LONG) {
      return new PrimitiveType.LongType();
    }
    if (kind == TypeKind.CHAR) {
      return new PrimitiveType.CharType();
    }
    if (kind == TypeKind.FLOAT) {
      return new PrimitiveType.FloatType();
    }
    if (kind == TypeKind.DOUBLE) {
      return new PrimitiveType.DoubleType();
    }
    if (kind == TypeKind.BYTE) {
      return new PrimitiveType.ByteType();
    }
    if (kind == TypeKind.SHORT) {
      return new PrimitiveType.ShortType();
    }
    throw new AssertionError("Unsupported primitive type: " + type.getKind());
  }

  static JavaType of(javax.lang.model.type.TypeMirror mirror) {
    Visitor visitor = new Visitor();
    // Map<?, ?> map = new HashMap<>();
    JavaType result = mirror.accept(visitor, null);
    // TODO mirror.getAnnotationMirrors().foreach(result::addAnnotation);
    return result;
  }

  /** Create {@link WildcardType} based on {@link javax.lang.model.type.WildcardType} instance. */
  static WildcardType of(javax.lang.model.type.WildcardType mirror) {
    TypeMirror extendsBound = mirror.getExtendsBound();
    if (extendsBound != null) {
      return WildcardType.subtypeOf(of(extendsBound));
    }
    TypeMirror superBound = mirror.getSuperBound();
    if (superBound != null) {
      return WildcardType.supertypeOf(of(superBound));
    }
    return new WildcardType();
  }
}

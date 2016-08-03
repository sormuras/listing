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

import java.util.HashMap;
import java.util.Map;
import javax.lang.model.type.NoType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor8;

/** Common {@link JavaType} factory collection parsing {@link javax.lang.model.type.TypeMirror}s. */
public interface JavaMirrors {

  class Visitor extends SimpleTypeVisitor8<JavaType, Map<?, ?>> {

    @Override
    public JavaType visitNoType(NoType type, Map<?, ?> map) {
      return of(type);
    }

    @Override
    public JavaType visitPrimitive(PrimitiveType type, Map<?, ?> pap) {
      return of(type);
    }
  }

  /** Create {@link JavaType} based on {@link NoType} instance. */
  static JavaType of(NoType type) {
    if (type.getKind() == TypeKind.VOID) {
      return new VoidType();
    }
    throw new AssertionError("Unsupported no type: " + type.getKind());
  }

  /** Create {@link JavaType} based on {@link PrimitiveType} instance. */
  static JavaType of(PrimitiveType type) {
    TypeKind kind = type.getKind();
    if (kind == TypeKind.BOOLEAN) {
      return JavaType.of(boolean.class);
    }
    if (kind == TypeKind.INT) {
      return JavaType.of(int.class);
    }
    if (kind == TypeKind.LONG) {
      return JavaType.of(long.class);
    }
    if (kind == TypeKind.CHAR) {
      return JavaType.of(char.class);
    }
    if (kind == TypeKind.FLOAT) {
      return JavaType.of(float.class);
    }
    if (kind == TypeKind.DOUBLE) {
      return JavaType.of(double.class);
    }
    if (kind == TypeKind.BYTE) {
      return JavaType.of(byte.class);
    }
    if (kind == TypeKind.SHORT) {
      return JavaType.of(short.class);
    }
    throw new AssertionError("Unsupported primitive type: " + type.getKind());
  }

  static JavaType of(TypeMirror mirror) {
    Visitor visitor = new Visitor();
    Map<?, ?> map = new HashMap<>();
    JavaType result = mirror.accept(visitor, map);
    // TODO mirror.getAnnotationMirrors().foreach(result::addAnnotation);
    return result;
  }
}

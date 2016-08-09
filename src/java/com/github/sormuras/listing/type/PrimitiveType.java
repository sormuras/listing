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

import com.github.sormuras.listing.Annotated;
import com.github.sormuras.listing.Listing;

/**
 * A primitive type is predefined by the Java language and named by its reserved keyword.
 *
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.2
 */
public interface PrimitiveType extends JavaType {

  class BooleanType extends Annotated implements PrimitiveType {
    @Override
    public Class<?> getType() {
      return boolean.class;
    }

    @Override
    public char toArrayClassNameIndicator() {
      return 'Z';
    }
  }

  class ByteType extends Annotated implements PrimitiveType {
    @Override
    public Class<?> getType() {
      return byte.class;
    }
  }

  class CharType extends Annotated implements PrimitiveType {
    @Override
    public Class<?> getType() {
      return char.class;
    }
  }

  class DoubleType extends Annotated implements PrimitiveType {
    @Override
    public Class<?> getType() {
      return double.class;
    }
  }

  class FloatType extends Annotated implements PrimitiveType {
    @Override
    public Class<?> getType() {
      return float.class;
    }
  }

  class IntType extends Annotated implements PrimitiveType {
    @Override
    public Class<?> getType() {
      return int.class;
    }
  }

  class LongType extends Annotated implements PrimitiveType {
    @Override
    public Class<?> getType() {
      return long.class;
    }

    @Override
    public char toArrayClassNameIndicator() {
      return 'J';
    }
  }

  class ShortType extends Annotated implements PrimitiveType {
    @Override
    public Class<?> getType() {
      return short.class;
    }
  }

  /** Creates new instance for passed primitive class <code>type</code>. */
  static JavaType of(Class<?> type) {
    if (type == void.class) {
      throw new AssertionError("expected primitive type, got " + type);
    }
    if (type == boolean.class) {
      return new BooleanType();
    }
    if (type == byte.class) {
      return new ByteType();
    }
    if (type == char.class) {
      return new CharType();
    }
    if (type == double.class) {
      return new DoubleType();
    }
    if (type == float.class) {
      return new FloatType();
    }
    if (type == int.class) {
      return new IntType();
    }
    if (type == long.class) {
      return new LongType();
    }
    if (type == short.class) {
      return new ShortType();
    }
    throw new AssertionError("expected primitive type, got " + type);
  }

  @Override
  default Listing apply(Listing listing) {
    JavaType type = this;
    return listing.add(type.toAnnotationsListable()).add(toClassName());
  }

  Class<?> getType();

  default char toArrayClassNameIndicator() {
    return getClass().getSimpleName().substring(0, 1).charAt(0);
  }

  @Override
  default String toClassName() {
    return getType().getTypeName();
  }
}

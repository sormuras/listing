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

import com.github.sormuras.listing.Listing;

/**
 * A primitive type is predefined by the Java language and named by its reserved keyword.
 *
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.2
 */
public abstract class PrimitiveType extends JavaType {

  public static final class BooleanType extends PrimitiveType {
    public BooleanType() {
      super(boolean.class);
    }
  }

  public static final class ByteType extends PrimitiveType {
    public ByteType() {
      super(byte.class);
    }
  }

  public static final class CharType extends PrimitiveType {
    public CharType() {
      super(char.class);
    }
  }

  public static final class DoubleType extends PrimitiveType {
    public DoubleType() {
      super(double.class);
    }
  }

  public static final class FloatType extends PrimitiveType {
    public FloatType() {
      super(float.class);
    }
  }

  public static final class IntType extends PrimitiveType {
    public IntType() {
      super(int.class);
    }
  }

  public static final class LongType extends PrimitiveType {
    public LongType() {
      super(long.class);
    }
  }

  public static final class ShortType extends PrimitiveType {
    public ShortType() {
      super(short.class);
    }
  }

  /** Creates new instance for passed primitive class <code>type</code>. */
  public static PrimitiveType of(Class<?> type) {
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

  private final Class<?> type;

  private PrimitiveType(Class<?> type) {
    this.type = type;
  }

  @Override
  public Listing apply(Listing listing) {
    return listing.add(toAnnotationsListable()).add(getType().getTypeName());
  }

  public Class<?> getType() {
    return type;
  }

  @Override
  public String toClassName() {
    return getType().getName();
  }
}

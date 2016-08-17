package test;

import java.beans.Transient;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Native;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
public @interface All {

  byte a() default 5;

  short b() default 6;

  int c() default 7;

  long d() default 8;

  float e() default 9.0f;

  double f() default 10.0;

  char[] g() default {0, 0xCAFE, 'z', '€', 'ℕ', '"', '\'', '\t', '\n'};

  boolean h() default true;

  Thread.State i() default Thread.State.BLOCKED;

  Documented j() default @Documented;

  String k() default "kk";

  Class<? extends java.lang.annotation.Annotation> l() default Native.class;

  int[] m() default {1, 2, 3};

  ElementType[] n() default {ElementType.FIELD, ElementType.METHOD};

  Target o();

  int p();

  Transient q() default @Transient(value = false);

  Class<? extends Number>[] r() default {Byte.class, Short.class, Integer.class, Long.class};
}

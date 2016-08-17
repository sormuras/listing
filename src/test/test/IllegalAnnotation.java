package test;

import java.lang.annotation.Annotation;

public class IllegalAnnotation implements Annotation {
  @Override
  public Class<? extends Annotation> annotationType() {
    return IllegalAnnotation.class;
  }

  // Method called via reflection. Fails.
  public void fail() {
    throw new AssertionError("illegal annotation implementation is illegal");
  }
}

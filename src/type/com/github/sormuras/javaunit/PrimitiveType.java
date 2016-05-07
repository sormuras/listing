package com.github.sormuras.javaunit;

import java.util.ArrayList;
import java.util.List;

/**
 * A primitive type is predefined by the Java language and named by its reserved keyword.
 *
 * @author Christian Stein
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.2
 */
public class PrimitiveType extends JavaType<PrimitiveType> {

  private final List<JavaAnnotation> annotations = new ArrayList<>();
  private final Class<?> type;

  public PrimitiveType(Class<?> type) {
    Tool.assume(Tool.check(type, "type").isPrimitive(), "expected primitive type, got %s", type);
    this.type = type;
  }

  @Override
  public Listing apply(Listing listing) {
    return listing.add(toAnnotationsListable()).add(type.getTypeName());
  }

  @Override
  public List<JavaAnnotation> getAnnotations() {
    return annotations;
  }
}

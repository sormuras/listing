package com.github.sormuras.listing.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.github.sormuras.listing.Annotation;
import com.github.sormuras.listing.Listing;

/**
 * A primitive type is predefined by the Java language and named by its reserved keyword.
 *
 * @author Christian Stein
 * @see https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.2
 */
public class PrimitiveType extends JavaType {

  private List<Annotation> annotations = Collections.emptyList();
  private final Class<?> type;

  public PrimitiveType(Class<?> type) {
    Objects.requireNonNull(type, "type");
    assert type.isPrimitive() : "expected primitive type, got " + type;
    this.type = type;
  }

  @Override
  public Listing apply(Listing listing) {
    return listing.add(toAnnotationsListable()).add(getType().getTypeName());
  }

  @Override
  public List<Annotation> getAnnotations(boolean readonly) {
    if (annotations == Collections.EMPTY_LIST && !readonly) {
      annotations = new ArrayList<>();
    }
    return annotations;
  }

  public Class<?> getType() {
    return type;
  }

  @Override
  public String toClassName() {
    return getType().getName();
  }
}

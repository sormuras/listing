package com.github.sormuras.listing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Base {@link Annotatable} implementation. */
public abstract class Annotated implements Annotatable {
  private List<Annotation> annotations = Collections.emptyList();

  @Override
  public List<Annotation> getAnnotations() {
    if (annotations == Collections.EMPTY_LIST) {
      annotations = new ArrayList<>();
    }
    return annotations;
  }

  @Override
  public boolean isAnnotated() {
    return !annotations.isEmpty();
  }
}

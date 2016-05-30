package com.github.sormuras.listing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class Counter extends AbstractProcessor {

  public static @interface Mark {}

  public final List<Element> listOfElements = new ArrayList<>();
  public final Map<String, JavaType<?>> map = new HashMap<>();

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latest();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> set = new HashSet<>();
    set.add(Mark.class.getCanonicalName());
    return set;
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    roundEnv.getElementsAnnotatedWith(Mark.class).forEach(listOfElements::add);
    for (Element e : listOfElements) {
      map.put(e.getSimpleName().toString(), JavaMirrors.of(e.asType()));
    }
    return true;
  }
}

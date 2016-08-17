package com.github.sormuras.listing.type;

import com.github.sormuras.listing.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class Counter extends AbstractProcessor {

  public @interface Mark {}

  public final List<Element> listOfElements = new ArrayList<>();
  public final Map<String, JavaType> map = new HashMap<>();
  public final List<Annotation> annotations = new ArrayList<>();
  public Elements elementUtils;
  public Types typeUtils;

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
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.elementUtils = processingEnv.getElementUtils();
    this.typeUtils = processingEnv.getTypeUtils();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (Element root : roundEnv.getRootElements()) {
      for (AnnotationMirror mirror : root.getAnnotationMirrors()) {
        this.annotations.add(JavaMirrors.of(mirror));
      }
    }
    roundEnv.getElementsAnnotatedWith(Mark.class).forEach(listOfElements::add);
    for (Element element : listOfElements) {
      TypeMirror type = element.asType();
      if (element instanceof ExecutableElement) {
        type = ((ExecutableElement) element).getReturnType();
      }
      map.put(element.getSimpleName().toString(), JavaMirrors.of(type));
    }
    return true;
  }
}

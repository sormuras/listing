package com.github.sormuras.listing.type;

import java.lang.annotation.Annotation;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;

public class DeclaredTypeAsErrorType implements ErrorType {

  private final DeclaredType declaredType;

  public DeclaredTypeAsErrorType(DeclaredType declaredType) {
    this.declaredType = declaredType;
  }

  @Override
  public <R, P> R accept(TypeVisitor<R, P> typeVisitor, P p) {
    return typeVisitor.visitError(this, p);
  }

  @Override
  public Element asElement() {
    return declaredType.asElement();
  }

  @Override
  public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
    return declaredType.getAnnotation(annotationType);
  }

  @Override
  public List<? extends AnnotationMirror> getAnnotationMirrors() {
    return declaredType.getAnnotationMirrors();
  }

  @Override
  public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
    return declaredType.getAnnotationsByType(annotationType);
  }

  @Override
  public TypeMirror getEnclosingType() {
    return declaredType.getEnclosingType();
  }

  @Override
  public TypeKind getKind() {
    return declaredType.getKind();
  }

  @Override
  public List<? extends TypeMirror> getTypeArguments() {
    return declaredType.getTypeArguments();
  }
}

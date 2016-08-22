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

import com.github.sormuras.listing.Annotatable;
import com.github.sormuras.listing.Annotation;
import com.github.sormuras.listing.Name;
import java.util.List;
import java.util.Map;
import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;
import javax.lang.model.util.SimpleTypeVisitor8;

/** Common {@link JavaType} factory collection parsing {@link javax.lang.model.type.TypeMirror}s. */
public interface JavaMirrors {

  /** Annotation value visitor adding members to the {@link Annotation} instance. */
  class AnnotationVisitor extends SimpleAnnotationValueVisitor8<Annotation, String> {
    private final Annotation annotation;

    AnnotationVisitor(Annotation annotation) {
      super(annotation);
      this.annotation = annotation;
    }

    @Override
    protected Annotation defaultAction(Object object, String name) {
      annotation.addObject(name, object);
      return annotation;
    }

    @Override
    public Annotation visitAnnotation(AnnotationMirror mirror, String name) {
      annotation.addMember(name, of(mirror));
      return annotation;
    }

    @Override
    public Annotation visitArray(List<? extends AnnotationValue> values, String name) {
      for (AnnotationValue value : values) {
        value.accept(this, name);
      }
      return annotation;
    }

    @Override
    public Annotation visitEnumConstant(VariableElement element, String name) {
      ClassType classType = (ClassType) JavaMirrors.of(element.asType());
      String constantName = element.getSimpleName().toString();
      annotation.addMember(name, classType.getName().resolve(constantName));
      return annotation;
    }

    @Override
    public Annotation visitType(TypeMirror type, String name) {
      annotation.addMember(name, l -> l.add(JavaMirrors.of(type)).add(".class"));
      return annotation;
    }
  }

  class Visitor extends SimpleTypeVisitor8<JavaType, Object> {

    @Override
    public JavaType visitArray(javax.lang.model.type.ArrayType type, Object tag) {
      return of(type);
    }

    @Override
    public JavaType visitDeclared(javax.lang.model.type.DeclaredType type, Object tag) {
      return of(type);
    }

    @Override
    public JavaType visitError(javax.lang.model.type.ErrorType type, Object tag) {
      return of(type);
    }

    @Override
    public JavaType visitNoType(javax.lang.model.type.NoType type, Object tag) {
      return of(type);
    }

    @Override
    public JavaType visitPrimitive(javax.lang.model.type.PrimitiveType type, Object tag) {
      return of(type);
    }

    @Override
    public JavaType visitTypeVariable(javax.lang.model.type.TypeVariable type, Object tag) {
      return of(type);
    }

    @Override
    public JavaType visitWildcard(javax.lang.model.type.WildcardType type, Object tag) {
      return of(type);
    }
  }

  static <A extends Annotatable> A annotate(A target, AnnotatedConstruct source) {
    source.getAnnotationMirrors().forEach(m -> target.addAnnotation(of(m)));
    return target;
  }

  /** Create {@link Annotation} based on {@link AnnotationMirror} instance. */
  static Annotation of(AnnotationMirror mirror) {
    Element element = mirror.getAnnotationType().asElement();
    Annotation annotation = new Annotation(Name.of(element));
    Map<? extends ExecutableElement, ? extends AnnotationValue> values = mirror.getElementValues();
    if (values.isEmpty()) {
      return annotation;
    }
    AnnotationVisitor visitor = new AnnotationVisitor(annotation);
    for (ExecutableElement executableElement : values.keySet()) {
      String name = executableElement.getSimpleName().toString();
      AnnotationValue value = values.get(executableElement);
      value.accept(visitor, name);
    }
    return annotation;
  }

  /** Create {@link ArrayType} based on {@link javax.lang.model.type.ArrayType} instance. */
  static ArrayType of(javax.lang.model.type.ArrayType type) {
    ArrayType arrayType = new ArrayType();
    TypeMirror mirror = type;
    while (mirror instanceof javax.lang.model.type.ArrayType) {
      ArrayDimension dimension = annotate(new ArrayDimension(), mirror);
      arrayType.getDimensions().add(dimension);
      mirror = ((javax.lang.model.type.ArrayType) mirror).getComponentType();
    }
    arrayType.setComponentType(of(mirror));
    return arrayType;
  }

  /** Create {@link ClassType} based on {@link javax.lang.model.type.DeclaredType} instance. */
  static ClassType of(javax.lang.model.type.DeclaredType type) {
    ClassType classType = new ClassType();
    // extract package name
    Element packageElement = type.asElement();
    while (packageElement.getKind() != ElementKind.PACKAGE) {
      packageElement = packageElement.getEnclosingElement();
    }
    PackageElement casted = (PackageElement) packageElement;
    classType.setPackageName(casted.getQualifiedName().toString());
    // extract simple names and type arguments
    TypeMirror actualType = type;
    for (Element element = type.asElement();
        element.getKind().isClass() || element.getKind().isInterface();
        element = element.getEnclosingElement()) {
      ClassName name = new ClassName(); // annotate(new ClassName(), element); // element.asType());
      name.setName(element.getSimpleName().toString());
      classType.getNames().add(0, name);
      if ((actualType instanceof javax.lang.model.type.DeclaredType)) {
        javax.lang.model.type.DeclaredType dt = (javax.lang.model.type.DeclaredType) actualType;
        annotate(name, dt);
        for (TypeMirror ta : dt.getTypeArguments()) {
          AnnotatedConstruct annotatedConstruct = ta;
          if (ta instanceof javax.lang.model.type.DeclaredType) {
            annotatedConstruct = ((javax.lang.model.type.DeclaredType) ta).asElement();
          }
          JavaType javaType = annotate(JavaMirrors.of(ta), annotatedConstruct);
          name.getTypeArguments().add(TypeArgument.of(javaType));
        }
        actualType = dt.getEnclosingType();
      }
    }
    return classType;
  }

  /** Create {@link JavaType} based on {@link javax.lang.model.type.NoType} instance. */
  static JavaType of(javax.lang.model.type.NoType type) {
    if (type.getKind() == TypeKind.VOID) {
      return new VoidType();
    }
    throw new AssertionError("Unsupported no type: " + type.getKind());
  }

  /** Create {@link PrimitiveType} based on {@link javax.lang.model.type.PrimitiveType} instance. */
  static PrimitiveType of(javax.lang.model.type.PrimitiveType type) {
    TypeKind kind = type.getKind();
    if (kind == TypeKind.BOOLEAN) {
      return annotate(new PrimitiveType.BooleanType(), type);
    }
    if (kind == TypeKind.INT) {
      return annotate(new PrimitiveType.IntType(), type);
    }
    if (kind == TypeKind.LONG) {
      return annotate(new PrimitiveType.LongType(), type);
    }
    if (kind == TypeKind.CHAR) {
      return annotate(new PrimitiveType.CharType(), type);
    }
    if (kind == TypeKind.FLOAT) {
      return annotate(new PrimitiveType.FloatType(), type);
    }
    if (kind == TypeKind.DOUBLE) {
      return annotate(new PrimitiveType.DoubleType(), type);
    }
    if (kind == TypeKind.BYTE) {
      return annotate(new PrimitiveType.ByteType(), type);
    }
    if (kind == TypeKind.SHORT) {
      return annotate(new PrimitiveType.ShortType(), type);
    }
    throw new AssertionError("Unsupported primitive type: " + type.getKind());
  }

  static JavaType of(javax.lang.model.type.TypeMirror mirror) {
    Visitor visitor = new Visitor();
    JavaType result = mirror.accept(visitor, null);
    return result;
  }

  static JavaType of(javax.lang.model.type.TypeVariable type) {
    TypeVariable variable = new TypeVariable();
    variable.setName(type.asElement().getSimpleName().toString());
    // TODO bounds...
    return variable;
  }

  /** Create {@link WildcardType} based on {@link javax.lang.model.type.WildcardType} instance. */
  static WildcardType of(javax.lang.model.type.WildcardType mirror) {
    TypeMirror extendsBound = mirror.getExtendsBound();
    if (extendsBound != null) {
      return annotate(WildcardType.subtypeOf(of(extendsBound)), mirror);
    }
    TypeMirror superBound = mirror.getSuperBound();
    if (superBound != null) {
      return annotate(WildcardType.supertypeOf(of(superBound)), mirror);
    }
    return annotate(new WildcardType(), mirror);
  }
}

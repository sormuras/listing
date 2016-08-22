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

package sandbox;

import com.github.sormuras.listing.type.ClassType;
import com.github.sormuras.listing.type.JavaMirrors;
import com.github.sormuras.listing.type.JavaType;
import com.github.sormuras.listing.unit.InterfaceDeclaration;
import com.github.sormuras.listing.unit.MethodDeclaration;
import com.github.sormuras.listing.unit.MethodParameter;
import com.github.sormuras.listing.unit.TypeDeclaration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Provides utilities based on parsing elements and types from javax.lang.model instances.
 *
 * @author Christian Stein
 */
public class ModelPoetry {

  protected final Map<Class<?>, TypeElement> elemap;
  protected final Elements elements;
  protected final Types types;

  /** C'tor. */
  public ModelPoetry(ProcessingEnvironment processingEnvironment) {
    Objects.requireNonNull(processingEnvironment, "processingEnvironment == null");
    this.elements = processingEnvironment.getElementUtils();
    this.types = processingEnvironment.getTypeUtils();

    this.elemap = new HashMap<>();
    elemap.put(Object.class, element(Object.class));
    // elemap.put(AutoCloseable.class, element(AutoCloseable.class));
    // elemap.put(Override.class, element(Override.class));
  }

  /** Create a type spec builder which copies from {@code element}. */
  public TypeDeclaration buildInterface(TypeElement element) {
    if (!element.getKind().isInterface()) {
      throw new IllegalArgumentException("only interfaces are supported, got " + element.getKind());
    }
    InterfaceDeclaration builder = new InterfaceDeclaration();
    // TODO builder.setCompilationUnit(unit);
    builder.setName(element.getSimpleName().toString());
    JavaMirrors.annotate(builder, element);
    builder.setModifiers(element.getModifiers());
    element.getInterfaces().forEach(t -> builder.addInterface(JavaMirrors.of(t)));
    // element.getTypeParameters()
    // .forEach(e -> builder.addTypeParameter(TypeParameter.of((TypeVariable) e.asType())));
    List<ExecutableElement> methods = ElementFilter.methodsIn(elements.getAllMembers(element));
    methods.removeIf(m -> m.getEnclosingElement().equals(elemap.get(Object.class)));
    for (ExecutableElement method : methods) {
      MethodDeclaration md = buildMethod(method, (DeclaredType) element.asType());
      // TODO
      md.setEnclosingDeclaration(builder);
      md.setCompilationUnit(builder.getCompilationUnit());
      builder.getMethods().add(md);
    }
    return builder;
  }

  /**
   * Create a method spec builder which copies {@code method} that is viewed as being a member of
   * the specified {@code containing} class or interface.
   *
   * <p>This will copy its visibility modifiers, type parameters, return type, name, parameters, and
   * throws declarations.
   */
  public MethodDeclaration buildMethod(ExecutableElement method, DeclaredType containing) {
    Objects.requireNonNull(method, "method == null");
    Objects.requireNonNull(containing, "containing == null");
    String methodName = method.getSimpleName().toString();
    MethodDeclaration builder = new MethodDeclaration();
    builder.setName(methodName);
    JavaMirrors.annotate(builder, method);
    builder.addModifiers(method.getModifiers());
    ExecutableType executableType = (ExecutableType) types.asMemberOf(containing, method);
    builder.setReturnType(JavaMirrors.of(executableType.getReturnType()));
    // for (TypeParameterElement typeParameterElement : method.getTypeParameters()) {
    // // TypeVariable var = (TypeVariable) typeParameterElement.asType();
    // // TODO builder.addTypeParameter(TypeParameter. JavaMirrors.of(var));
    // }
    List<? extends VariableElement> parameters = method.getParameters();
    List<? extends TypeMirror> parameterTypes = executableType.getParameterTypes();
    for (int index = 0; index < parameters.size(); index++) {
      VariableElement parameter = parameters.get(index);
      JavaType type = JavaMirrors.of(parameterTypes.get(index)); // annotations are grabbed later
      String name = parameter.getSimpleName().toString();
      MethodParameter mp = MethodParameter.of(type, name);
      mp.setFinal(parameter.getModifiers().contains(Modifier.FINAL));
      JavaMirrors.annotate(mp, parameter);
      builder.addParameter(mp);
    }
    builder.setVarArgs(method.isVarArgs());
    for (TypeMirror thrownType : method.getThrownTypes()) {
      builder.addThrows((ClassType) JavaMirrors.of(thrownType));
    }
    return builder;
  }

  protected TypeElement element(Class<?> type) {
    return elements.getTypeElement(type.getCanonicalName());
  }
}

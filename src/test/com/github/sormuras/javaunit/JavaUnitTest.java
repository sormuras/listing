package com.github.sormuras.javaunit;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;

import org.junit.Assert;
import org.junit.Test;

public class JavaUnitTest {

  @Test
  public void crazy() throws Exception {
    JavaAnnotation tag = new JavaAnnotation(JavaName.of("", "Tag"));
    ClassType stringType = new ClassType(JavaName.of(String.class));
    ClassType listOfStrings = new ClassType(JavaName.of(List.class), new TypeArgument(stringType));

    JavaUnit unit = new JavaUnit("abc.xyz");
    unit.getPackageDeclaration()
        .addAnnotation(Generated.class, "https://", "github.com/sormuras/listing");
    unit.getImportDeclarations()
        .addSingleTypeImport(Assert.class)
        .addTypeImportOnDemand(JavaName.of("abc"))
        .addSingleStaticImport(JavaName.of(Collections.class.getMethod("shuffle", List.class)))
        .addStaticImportOnDemand(JavaName.of(Objects.class));
    unit.declareAnnotation("TestAnno");
    unit.declareEnum("TestEnum")
        .addAnnotation(Generated.class, "An enum for testing")
        .addModifier(Modifier.PROTECTED)
        .addInterface(JavaType.of(Serializable.class))
        .setBody(l -> l.add("A, B, C").newline());
    unit.declareInterface("TestIntf");
    ClassDeclaration simple =
        unit.declareClass("SimpleClass")
            .addModifier("public", "final")
            .addTypeParameter(
                new TypeParameter("S").addBounds(JavaType.of(Runnable.class).addAnnotation(tag)))
            .addTypeParameter(new TypeParameter("T").setBoundTypeVariable("S"))
            .setSuperClass(JavaType.of(Thread.class).addAnnotation(tag))
            .addInterface(JavaType.of(Cloneable.class))
            .addInterface(JavaType.of(Runnable.class));
    FieldDeclaration i =
        simple
            .addFieldDeclaration(int.class, "i")
            .addModifier("private", "volatile")
            .setInitializer(Listable.of(4711));
    simple
        .addFieldDeclaration(JavaType.of(String.class).addAnnotation(tag), "s")
        .setInitializer(Listable.of("The Story about \"Ping\""));
    simple
        .addFieldDeclaration(listOfStrings, "l")
        .setInitializer(l -> l.add("java.util.Collections.emptyList()"));
    simple
        .addMethodDeclaration(void.class, "run")
        .addAnnotation(Override.class)
        .addModifier(Modifier.PUBLIC, Modifier.FINAL)
        .setBody(l -> l.add("System.out.println(\"Hallo Welt!\");").newline());
    simple
        .addMethodDeclaration(new TypeVariable().setName("N"), "calc")
        .addModifier(Modifier.STATIC)
        .addTypeParameter(new TypeParameter("N").addBounds(JavaType.of(Number.class)))
        .addParameter(int.class, "i")
        .addThrows(Exception.class)
        .setBody(l -> l.add("return null;").newline());
    simple.declareEnum("Innum").setBody(l -> l.add("X, Y, Z").newline());
    simple.declareClass("Cinner").setBody(l -> l.add("// empty").newline());

    Assert.assertSame(simple, i.getEnclosingType().get());

    Text.assertEquals(getClass(), "crazy", unit);
  }
}

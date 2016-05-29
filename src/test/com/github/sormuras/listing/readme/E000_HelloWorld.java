package com.github.sormuras.listing.readme;

import static org.junit.Assert.assertEquals;

import javax.lang.model.element.Modifier;

import org.junit.Test;

import com.github.sormuras.listing.ClassDeclaration;
import com.github.sormuras.listing.JavaName;
import com.github.sormuras.listing.JavaUnit;
import com.github.sormuras.listing.Listable;
import com.github.sormuras.listing.MethodDeclaration;
import com.github.sormuras.listing.Text;

public class E000_HelloWorld {

  @Test
  public void hello() throws Exception {
    JavaUnit unit = new JavaUnit("com.example.helloworld");

    ClassDeclaration helloClass = unit.declareClass("HelloWorld");
    helloClass.addModifier(Modifier.PUBLIC);

    MethodDeclaration psvm = helloClass.declareMethod(void.class, "main");
    psvm.addModifier(Modifier.PUBLIC, Modifier.STATIC);
    psvm.addParameter(String[].class, "args");
    psvm.setBody(
        l ->
            l.add(JavaName.of(System.class))
                .add(".out.println(")
                .add(Listable.of("Hello World"))
                .add(");")
                .newline());

    assertEquals("com.example.helloworld.HelloWorld", unit.compile().getCanonicalName());
    Text.assertEquals(getClass(), "hello", unit);
  }
}

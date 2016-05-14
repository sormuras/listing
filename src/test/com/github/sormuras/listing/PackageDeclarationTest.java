package com.github.sormuras.listing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.annotation.ElementType;

import org.junit.Test;

public class PackageDeclarationTest {

  @Test
  public void list() {
    // unnamed
    assertEquals("", new PackageDeclaration().list());
    // simple
    assertEquals("package abc;\n", new PackageDeclaration("abc").list());
    assertEquals("package abc.xyz;\n", new PackageDeclaration("abc.xyz").list());
    // with annotation(s)
    PackageDeclaration annotated = new PackageDeclaration("abc.xyz");
    annotated.addAnnotation(new JavaAnnotation(JavaName.of("abc", "PackageAnnotation")));
    Text.assertEquals(getClass(), "annotated", annotated);
    // with (hand-crafted) Javadoc
    Listing listing = new Listing();
    listing.add("/**").newline();
    listing.add(" * Testing Javadoc on PackageDeclaration element.").newline();
    listing.add(" * @since 1.0").newline();
    listing.add(" */").newline();
    listing.add(annotated);
    Text.assertEquals(getClass(), "javadoc", listing);
  }

  @Test
  public void separator() {
    assertEquals(Listable.NEWLINE, new PackageDeclaration().getAnnotationSeparator());
  }

  @Test
  public void target() {
    assertEquals(ElementType.PACKAGE, new PackageDeclaration("t").getAnnotationTarget());
  }

  @Test
  public void unnamedAsStringFails() {
    try {
      new PackageDeclaration("");
      fail("unnamed package declaration created?!");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.toString().contains("empty"));
    }
  }
}

package com.github.sormuras.listing;

import static javax.lang.model.element.Modifier.STATIC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.ElementType;
import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.lang.model.element.Modifier;

import org.junit.Test;

import com.github.sormuras.listing.ImportDeclarations;
import com.github.sormuras.listing.JavaName;
import com.github.sormuras.listing.Listing;

public class ImportDeclarationsTest {

  static void check(Consumer<ImportDeclarations> consumer, String... expected) {
    ImportDeclarations declarations = new ImportDeclarations();
    assertTrue(declarations.isEmpty());
    consumer.accept(declarations);
    Listing listing = new Listing();
    listing.add(declarations);
    assertEquals(Arrays.asList(expected), listing.getCollectedLines());
    if (expected.length > 0) {
      assertFalse(declarations.isEmpty());
    }
  }

  static void empty(ImportDeclarations declarations) {}

  @Test
  public void emptyIsEmpty() {
    check(ImportDeclarationsTest::empty);
    assertTrue(new ImportDeclarations().isEmpty());
  }

  @Test
  public void imports() throws Exception {
    ImportDeclarations imports = new ImportDeclarations();
    imports.addSingleStaticImport(JavaName.of(STATIC));
    imports.addSingleStaticImport(JavaName.of(STATIC, "org.junit", "Assert", "assertEquals"));
    imports.addSingleStaticImport(JavaName.of(STATIC, "org.junit", "Assert", "assertFalse"));
    imports.addSingleStaticImport(JavaName.of(STATIC, "org.junit", "Assert", "assertTrue"));
    imports.addStaticImportOnDemand(JavaName.of(Objects.class));
    imports.addSingleTypeImport(JavaName.of(ElementType.class));
    imports.addSingleTypeImport(JavaName.of(Member.class));
    imports.addTypeImportOnDemand(JavaName.of("java.util"));
    Text.assertEquals(getClass(), "imports", imports);
  }

  @Test
  public void singleStaticImport() throws Exception {
    Member micros = TimeUnit.class.getField("MICROSECONDS");
    check(
        imports ->
            imports
                .addSingleStaticImport(Thread.State.NEW)
                .addSingleStaticImport(micros)
                .addSingleStaticImport(
                    JavaName.of("java.lang.annotation", "ElementType", "PARAMETER")
                        .setTarget(ElementType.FIELD)
                        .setModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)),
        "import static java.lang.annotation.ElementType.PARAMETER;",
        "import static java.lang.Thread.State.NEW;",
        "import static java.util.concurrent.TimeUnit.MICROSECONDS;");
  }

  @Test
  public void singleTypeImport() {
    check(i -> i.addSingleTypeImport(Set.class), "import java.util.Set;");
    check(
        imports ->
            imports
                .addSingleTypeImport(Set.class)
                .addSingleTypeImport(Map.Entry.class)
                .addSingleTypeImport(JavaName.of("java.io", "File")),
        "import java.io.File;",
        "import java.util.Map.Entry;",
        "import java.util.Set;");
  }

  @Test
  public void staticImportOnDemand() {
    check(
        imports -> imports.addStaticImportOnDemand(JavaName.of("java.lang", "Thread", "State")),
        "import static java.lang.Thread.State.*;");
  }

  @Test
  public void typeImportOnDemand() {
    check(
        imports -> imports.addTypeImportOnDemand(JavaName.of("java.util")), "import java.util.*;");
  }
}

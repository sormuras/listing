package com.github.sormuras.listing.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.expectThrows;

import java.util.function.Supplier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class DeclarationContainerTest {

  @Disabled
  @Test
  void compilationUnit() {
    test(CompilationUnit::new);
  }

  @Test
  void normalClassDeclaration() {
    test(NormalClassDeclaration::new);
  }

  @Test
  void enumDeclaration() {
    test(EnumDeclaration::new);
  }

  void test(Supplier<DeclarationContainer> supplier) {
    duplicateNestedNameFails(supplier.get());
    duplicateSiblingNameFails(supplier.get());
    illegalJavaNameFails(supplier.get());
  }

  void duplicateNestedNameFails(DeclarationContainer container) {
    DeclarationContainer parent = container.declareClass("A");
    Exception e = expectThrows(Exception.class, () -> parent.declareClass("A"));
    assertTrue(e.getMessage().contains("nested"));
    DeclarationContainer child = parent.declareClass("B");
    e = expectThrows(Exception.class, () -> child.declareClass("A"));
    assertTrue(e.getMessage().contains("nested"));
  }

  void duplicateSiblingNameFails(DeclarationContainer container) {
    container.declareClass("A");
    Exception e = expectThrows(Exception.class, () -> container.declareClass("A"));
    assertTrue(e.getMessage().contains("duplicate"));
  }

  void illegalJavaNameFails(DeclarationContainer container) {
    Exception e = expectThrows(Exception.class, () -> container.declareClass("123"));
    assertTrue(e.getMessage().contains("valid"));
  }
}

package com.github.sormuras.listing.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.expectThrows;

import org.junit.jupiter.api.Test;

class DeclarationContainerTest {

  @Test
  void compilationUnit() {
    new CompilationUnit();
  }

  @Test
  void normalClassDeclaration() {
    new NormalClassDeclaration();
  }

  @Test
  void enumDeclaration() {
    new EnumDeclaration();
  }

  void test(DeclarationContainer container) {
    duplicateNestedNameFails(container);
    duplicateSiblingNameFails(container);
    illegalJavaNameFails(container);
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

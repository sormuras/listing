package de.codeturm.listing;

import java.util.Arrays;
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class DeclarationContainerTest {

  @Parameters(name = "{index}: {0}")
  public static Iterable<Object[]> implementations() {
    Supplier<DeclarationContainer> unitSupplier = () -> new CompilationUnit();
    Supplier<DeclarationContainer> classSupplier = () -> new ClassDeclaration();
    return Arrays.asList(
        new Object[][] { //
          {unitSupplier}, {classSupplier} //
        });
  }

  private final DeclarationContainer container;

  public DeclarationContainerTest(Supplier<DeclarationContainer> supplier) {
    this.container = supplier.get();
  }

  @Test
  public void duplicateNestedNameFails() {
    DeclarationContainer parent = container.declareClass("A");
    try {
      parent.declareClass("A");
    } catch (IllegalArgumentException e) {
      Assert.assertTrue(e.getMessage().contains("nested"));
    }
    DeclarationContainer child = parent.declareClass("B");
    try {
      child.declareClass("A");
    } catch (IllegalArgumentException e) {
      Assert.assertTrue(e.getMessage().contains("nested"));
    }
  }

  @Test
  public void duplicateSiblingNameFails() {
    container.declareClass("A");
    try {
      container.declareClass("A");
    } catch (IllegalArgumentException e) {
      Assert.assertTrue(e.getMessage().contains("duplicate"));
    }
  }

  @Test
  public void illegalJavaNameFails() {
    try {
      container.declareClass("123");
    } catch (IllegalArgumentException e) {
      Assert.assertTrue(e.getMessage().contains("valid"));
    }
  }
}

package de.codeturm.listing;

import org.junit.Assert;
import org.junit.Test;

public class CompilationUnitTest {

  private CompilationUnit unit = new CompilationUnit();

  @Test
  public void topLevelClasses() {
    unit.declareClass("A");
    unit.declareClass("B");
    Assert.assertEquals(2, unit.getDeclarations().size());
  }
}

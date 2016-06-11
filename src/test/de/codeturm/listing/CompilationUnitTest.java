package de.codeturm.listing;

import org.junit.Assert;
import org.junit.Test;

public class CompilationUnitTest {

  @Test
  public void topLevelClasses() {
    Assert.assertEquals(3, Fixtures.simple().getDeclarations().size());
  }
}

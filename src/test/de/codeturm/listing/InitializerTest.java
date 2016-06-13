package de.codeturm.listing;

import org.junit.Assert;
import org.junit.Test;

public class InitializerTest {

  @Test
  public void enclosing() {
    Initializer initializer = new Initializer();
    Assert.assertEquals(null, initializer.getEnclosing());
  }
}

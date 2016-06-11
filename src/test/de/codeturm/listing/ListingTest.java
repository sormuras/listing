package de.codeturm.listing;

import org.junit.Assert;
import org.junit.Test;

public class ListingTest {

  @Test
  public void topLevelClasses() {
    String expected = Fixtures.text(Fixtures.class, "simple");
    String actual = new Listing().toString(Fixtures.simple());
    Assert.assertEquals(expected, actual);
  }
}

import org.junit.Assert;
import org.junit.Test;

import com.github.sormuras.listing.Tool;

public class UnnamedTest {

  @Test
  public void packageOf() {
    Assert.assertEquals("", Tool.packageOf(UnnamedTest.class));
  }
}

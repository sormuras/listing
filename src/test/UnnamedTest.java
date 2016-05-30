import org.junit.Assert;
import org.junit.Test;

import com.github.sormuras.listing.JavaName;

public class UnnamedTest {

  @Test
  public void getPackageName() {
    Assert.assertEquals("", JavaName.of(UnnamedTest.class).getPackageName());
  }
}

package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.jupiter.meta.RestTest;
import org.junit.jupiter.api.extension.RegisterExtension;

@RestTest
public class BaseRestTest {

  @RegisterExtension
  protected static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();
}

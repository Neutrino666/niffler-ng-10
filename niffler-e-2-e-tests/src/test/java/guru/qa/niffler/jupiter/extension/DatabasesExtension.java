package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.data.tpl.Connections;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class DatabasesExtension implements SuiteExtension {

  @Override
  public void afterSuite() {
    Connections.closeAllConnections();
  }
}

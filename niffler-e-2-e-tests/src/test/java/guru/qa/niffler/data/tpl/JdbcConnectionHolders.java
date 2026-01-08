package guru.qa.niffler.data.tpl;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class JdbcConnectionHolders implements AutoCloseable {

  private final List<JdbcConnectionHolder> holders;

  public JdbcConnectionHolders(List<JdbcConnectionHolder> holders) {
    this.holders = holders;
  }

  @Override
  public void close() {
    holders.forEach(JdbcConnectionHolder::closeAllConnections);
  }
}

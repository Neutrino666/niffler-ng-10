package guru.qa.niffler.data.tpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class JdbcTransactionTemplate {

  private static final int DEFAULT_ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
  private final JdbcConnectionHolder holder;
  private final AtomicBoolean closeAfterAction = new AtomicBoolean(true);

  public JdbcTransactionTemplate(String jdbcUrl) {
    this.holder = Connections.holder(jdbcUrl);
  }

  public JdbcTransactionTemplate holdConnectionAfterAction() {
    this.closeAfterAction.set(false);
    return this;
  }

  @Nullable
  public <T> T execute(Supplier<T> action) {
    return execute(DEFAULT_ISOLATION, action);
  }

  @Nullable
  public <T> T execute(int isolation, Supplier<T> action) {
    Connection connection = null;
    try {
      connection = holder.connection();
      connection.setTransactionIsolation(isolation);
      connection.setAutoCommit(false);
      T result = action.get();
      connection.commit();
      connection.setAutoCommit(true);
      return result;
    } catch (SQLException e) {
      if (connection != null) {
        try {
          connection.rollback();
          connection.setAutoCommit(true);
        } catch (SQLException ex) {
          throw new RuntimeException(ex);
        }
      }
      throw new RuntimeException(e);
    } finally {
      if (closeAfterAction.get()) {
        holder.close();
      }
    }
  }
}

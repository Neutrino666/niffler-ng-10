package guru.qa.niffler.data;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;

public class Databases {

  private static final int DEFAULT_ISOLATION = Connection.TRANSACTION_READ_COMMITTED;

  private Databases() {
  }

  private static final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();
  private static final Map<Long, Map<String, Connection>> threadConnections = new ConcurrentHashMap<>();

  public record XaFunction<T>(@Nonnull Function<Connection, T> function, @Nonnull String jdbcUrl) {

  }

  public record XaConsumer(@Nonnull Consumer<Connection> function, @Nonnull String jdbcUrl) {

  }

  public static <T> T transaction(@Nonnull Function<Connection, T> function,
      @Nonnull String jdbcUrl) {
    return transaction(DEFAULT_ISOLATION, function, jdbcUrl);
  }

  @Nonnull
  public static <T> T transaction(int isolation, @Nonnull Function<Connection, T> function,
      @Nonnull String jdbcUrl) {
    Connection connection = null;
    try {
      connection = connection(jdbcUrl);
      connection.setAutoCommit(false);
      connection.setTransactionIsolation(isolation);
      T result = function.apply(connection);
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
    }
  }

  @Nonnull
  public static <T> T xaTransaction(@Nonnull XaFunction<T>... actions) {
    return xaTransaction(DEFAULT_ISOLATION, actions);
  }

  @Nonnull
  public static <T> T xaTransaction(int isolation, @Nonnull XaFunction<T>... actions) {
    UserTransaction ut = new UserTransactionImp();
    try {
      ut.begin();
      T result = null;
      for (XaFunction<T> action : actions) {
        result = action.function.apply(connection(isolation, action.jdbcUrl));
      }
      ut.commit();
      return result;
    } catch (Exception e) {
      try {
        ut.rollback();
      } catch (SystemException ex) {
        throw new RuntimeException(ex);
      }
      throw new RuntimeException(e);
    }
  }

  public static void transaction(@Nonnull Consumer<Connection> consumer, @Nonnull String jdbcUrl) {
    transaction(DEFAULT_ISOLATION, consumer, jdbcUrl);
  }

  public static void transaction(int isolation, @Nonnull Consumer<Connection> consumer,
      @Nonnull String jdbcUrl) {
    Connection connection = null;
    try {
      connection = connection(jdbcUrl);
      connection.setAutoCommit(false);
      connection.setTransactionIsolation(isolation);
      consumer.accept(connection);
      connection.commit();
      connection.setAutoCommit(true);
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
    }
  }

  public static void xaTransaction(@Nonnull XaConsumer... actions) {
    xaTransaction(DEFAULT_ISOLATION, actions);
  }

  public static void xaTransaction(int isolation, @Nonnull XaConsumer... actions) {
    UserTransaction ut = new UserTransactionImp();
    try {
      ut.begin();
      for (XaConsumer action : actions) {
        action.function.accept(connection(isolation, action.jdbcUrl));
      }
      ut.commit();
    } catch (Exception e) {
      try {
        ut.rollback();
      } catch (SystemException ex) {
        throw new RuntimeException(ex);
      }
      throw new RuntimeException(e);
    }
  }

  public static void closeAllConnections() {
    for (Map<String, Connection> connectionMap : threadConnections.values()) {
      for (Connection connection : connectionMap.values()) {
        try {
          if (connection != null && !connection.isClosed()) {
            connection.close();
          }
        } catch (SQLException e) {
          // NOP
        }
      }
    }
  }

  private static @Nonnull Connection connection(@Nonnull String jdbcUrl) throws SQLException {
    return connection(DEFAULT_ISOLATION, jdbcUrl);
  }

  private static @Nonnull Connection connection(int isolation, @Nonnull String jdbcUrl)
      throws SQLException {
    Connection connection = threadConnections.computeIfAbsent(
        Thread.currentThread().threadId(),
        key -> {
          try {
            return new HashMap<>(Map.of(
                jdbcUrl,
                dataSource(jdbcUrl).getConnection()
            ));
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        }
    ).computeIfAbsent(
        jdbcUrl,
        key -> {
          try {
            return dataSource(jdbcUrl).getConnection();
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        }
    );
    connection.setTransactionIsolation(isolation);
    return connection;
  }

  public static @Nonnull DataSource dataSource(@Nonnull String jdbcUrl) {
    return dataSources.computeIfAbsent(
        jdbcUrl,
        key -> {
          AtomikosDataSourceBean dsBean = new AtomikosDataSourceBean();
          final String uniqueId = StringUtils.substringAfter(jdbcUrl, "5432/");
          dsBean.setUniqueResourceName(uniqueId);
          dsBean.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");
          Properties props = new Properties();
          props.put("URL", jdbcUrl);
          props.put("user", "postgres");
          props.put("password", "secret");
          dsBean.setXaProperties(props);
          dsBean.setMaxPoolSize(10);
          return dsBean;
        }
    );
  }
}

package guru.qa.niffler.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.sql.DataSource;
import org.postgresql.ds.PGSimpleDataSource;

public class Databases {

  private static final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

  private Databases() {
  }

  private static @Nonnull DataSource dataSource(@Nonnull String jdbcUrl) {
    return dataSources.computeIfAbsent(
        jdbcUrl,
        key -> {
          PGSimpleDataSource ds = new PGSimpleDataSource();
          ds.setURL(jdbcUrl);
          ds.setUser("postgres");
          ds.setPassword("secret");
          return ds;
        }
    );
  }

  public static @Nonnull Connection connection(@Nonnull String jdbcUrl) throws SQLException {
    return dataSource(jdbcUrl).getConnection();
  }
}

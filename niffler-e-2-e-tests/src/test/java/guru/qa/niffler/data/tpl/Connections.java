package guru.qa.niffler.data.tpl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Connections {

  private static final Map<String, JdbcConnectionHolder> holders = new ConcurrentHashMap<>();

  public static JdbcConnectionHolder holder(String jdbcUrl) {
    return holders.computeIfAbsent(
        jdbcUrl,
        key -> new JdbcConnectionHolder(
            DataSources.dataSource(jdbcUrl)
        )
    );
  }

  public static JdbcConnectionHolders holders(String... jdbcUrl) {
    List<JdbcConnectionHolder> result = Stream.of(jdbcUrl)
        .map(Connections::holder)
        .toList();
    return new JdbcConnectionHolders(result);
  }

  public static void closeAllConnections() {
    holders.values().forEach(JdbcConnectionHolder::closeAllConnections);
  }
}

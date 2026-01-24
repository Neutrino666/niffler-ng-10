package guru.qa.niffler.data.jpa;

import guru.qa.niffler.data.tpl.DataSources;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@ParametersAreNonnullByDefault
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityManagers {

  private static final Map<String, EntityManagerFactory> EMFS = new ConcurrentHashMap<>();

  public static @Nonnull EntityManager em(String jdbcUrl) {
    return new ThreadSafeEntityManager(
        EMFS.computeIfAbsent(
            jdbcUrl,
            key -> {
              DataSources.dataSource(jdbcUrl);
              return Persistence.createEntityManagerFactory(jdbcUrl);
            }
        ).createEntityManager()
    );
  }
}

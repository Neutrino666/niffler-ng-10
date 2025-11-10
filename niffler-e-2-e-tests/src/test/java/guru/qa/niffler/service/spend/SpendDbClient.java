package guru.qa.niffler.service.spend;

import static guru.qa.niffler.data.Databases.dataSource;
import static guru.qa.niffler.data.Databases.transaction;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendingSpringDaoJdbc;
import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.data.entity.SpendEntity;
import guru.qa.niffler.model.SpendJson;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public class SpendDbClient implements SpendClient {

  private final static Config CFG = Config.getInstance();

  @Override
  public SpendJson create(@Nonnull SpendJson spend) {
    return transaction(connection -> {
          SpendEntity spendEntity = SpendEntity.fromJson(spend);
          if (spendEntity.getCategory().getId() == null) {
            CategoryEntity categoryEntity = new CategoryDaoJdbc(connection).create(
                spendEntity.getCategory());
            spendEntity.setCategory(categoryEntity);
          }
          return SpendJson.fromEntity(
              new SpendDaoJdbc(connection).create(spendEntity)
          );
        },
        CFG.spendJdbcUrl()
    );
  }

  public @Nonnull Optional<SpendEntity> findById(@Nonnull UUID id) {
    return transaction(connection -> {
          return new SpendDaoJdbc(connection).findById(id);
        },
        CFG.spendJdbcUrl()
    );
  }

  public List<SpendJson> findAll() {
    return transaction(connection -> {
          return new SpendDaoJdbc(connection).findAll()
              .stream()
              .map(SpendJson::fromEntity)
              .toList();
        },
        CFG.spendJdbcUrl()
    );
  }

  public @Nonnull List<SpendEntity> findAllByUsername(@Nonnull String username) {
    return transaction(connection -> {
          return new SpendDaoJdbc(connection).findAllByUsername(username);
        },
        CFG.spendJdbcUrl()
    );
  }

  public void delete(@Nonnull SpendEntity spend) {
    transaction(connection -> {
          new SpendDaoJdbc(connection).delete(spend);
        },
        CFG.spendJdbcUrl()
    );
  }

  public List<SpendJson> findAllWithSpringJdbc() {
    return getSpendSpringDaoJdbc().findAll()
        .stream()
        .map(SpendJson::fromEntity)
        .toList();
  }

  private SpendingSpringDaoJdbc getSpendSpringDaoJdbc() {
    return new SpendingSpringDaoJdbc(dataSource(CFG.spendJdbcUrl()));
  }
}

package guru.qa.niffler.service.category;

import static guru.qa.niffler.data.Databases.transaction;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.model.CategoryJson;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

public class CategoryDbClient implements CategoryClient {

  private final static Config CFG = Config.getInstance();

  public @Nonnull Optional<CategoryEntity> findByUsernameAndName(
      @Nonnull String username,
      @Nonnull String categoryName
  ) {
    return transaction(connection -> {
          return new CategoryDaoJdbc(connection).findByUsernameAndName(username, categoryName);
        },
        CFG.spendJdbcUrl()
    );
  }

  public @Nonnull List<CategoryEntity> findAllByUsername(@Nonnull String username) {
    return transaction(connection -> {
          return new CategoryDaoJdbc(connection).findAllByUsername(username);
        },
        CFG.spendJdbcUrl()
    );
  }

  public void delete(@Nonnull CategoryEntity category) {
    transaction(connection -> {
          new CategoryDaoJdbc(connection).delete(category);
        },
        CFG.spendJdbcUrl()
    );
  }

  @Override
  public @Nonnull CategoryJson update(@Nonnull CategoryJson category) {
    throw new UnsupportedOperationException("Not implemented :(");
  }

  @Override
  public @Nonnull CategoryJson create(@Nonnull CategoryJson category) {
    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
    return CategoryJson.fromEntity(
        transaction(connection -> {
              return new CategoryDaoJdbc(connection).create(categoryEntity);
            },
            CFG.spendJdbcUrl()
        )
    );
  }
}

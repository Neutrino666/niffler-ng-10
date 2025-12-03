package guru.qa.niffler.service.category;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

public class CategoryDbClient implements CategoryClient {

  private final static Config CFG = Config.getInstance();

  private final CategoryDao categoryDao = new CategoryDaoJdbc();

  private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
      CFG.spendJdbcUrl()
  );

  @Override
  public CategoryJson create(CategoryJson category) {
    return jdbcTxTemplate.execute(() ->
        CategoryJson.fromEntity(categoryDao.create(CategoryEntity.fromJson(category)))
    );
  }

  @Override
  public @Nonnull CategoryJson update(@Nonnull CategoryJson category) {
    throw new UnsupportedOperationException("Not implemented :(");
  }

  public @Nonnull Optional<CategoryEntity> findByUsernameAndName(
      @Nonnull String username,
      @Nonnull String categoryName
  ) {
    return jdbcTxTemplate.execute(() ->
        categoryDao.findByUsernameAndName(username, categoryName)
    );
  }

  public @Nonnull List<CategoryEntity> findAllByUsername(@Nonnull String username) {
    return jdbcTxTemplate.execute(() -> categoryDao.findAllByUsername(username));
  }

  public @Nonnull List<CategoryJson> findAll() {
    return jdbcTxTemplate.execute(() ->
        categoryDao.findAll()
            .stream()
            .map(CategoryJson::fromEntity)
            .toList()
    );
  }

  public void delete(@Nonnull CategoryEntity category) {
    categoryDao.delete(category);
  }
}

package guru.qa.niffler.service.category;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import io.qameta.allure.Step;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CategoryDbClient implements CategoryClient {

  private final static Config CFG = Config.getInstance();

  private final CategoryDao categoryDao = new CategoryDaoJdbc();

  private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
      CFG.spendJdbcUrl()
  );

  @Override
  @Step("SQL Создание категории")
  public CategoryJson create(CategoryJson category) {
    return jdbcTxTemplate.execute(() ->
        CategoryJson.fromEntity(categoryDao.create(CategoryEntity.fromJson(category)))
    );
  }

  @Step("SQL Обновление категории")
  @Override
  public @Nonnull CategoryJson update(CategoryJson category) {
    throw new UnsupportedOperationException("Not implemented :(");
  }

  @Step("SQL Поиск категории по username и categoryName")
  public @Nullable Optional<CategoryEntity> findByUsernameAndName(
      String username,
      String categoryName
  ) {
    return jdbcTxTemplate.execute(() ->
        categoryDao.findByUsernameAndName(username, categoryName)
    );
  }

  @Step("SQL Поиск всех категорий пользователя")
  public @Nullable List<CategoryEntity> findAllByUsername(String username) {
    return jdbcTxTemplate.execute(() -> categoryDao.findAllByUsername(username));
  }

  @Step("SQL Поиск всех категорий")
  public @Nullable List<CategoryJson> findAll() {
    return jdbcTxTemplate.execute(() ->
        categoryDao.findAll()
            .stream()
            .map(CategoryJson::fromEntity)
            .toList()
    );
  }

  @Step("SQL Удаление категории")
  public void delete(@Nonnull CategoryEntity category) {
    categoryDao.remove(category);
  }
}

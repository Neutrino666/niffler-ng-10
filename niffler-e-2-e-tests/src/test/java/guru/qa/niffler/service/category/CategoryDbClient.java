package guru.qa.niffler.service.category;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import io.qameta.allure.Step;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
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
  public @Nonnull CategoryJson create(CategoryJson category) {
    return Objects.requireNonNull(
        jdbcTxTemplate.execute(() ->
            CategoryJson.fromEntity(categoryDao.create(CategoryEntity.fromJson(category)))
        )
    );
  }

  @Step("SQL Обновление категории")
  @Override
  public @Nonnull CategoryJson update(CategoryJson category) {
    return Objects.requireNonNull(
        jdbcTxTemplate.execute(() ->
            CategoryJson.fromEntity(categoryDao.update(CategoryEntity.fromJson(category)))
        )
    );
  }

  @Override
  public @Nonnull List<CategoryJson> findAllByUsername(String username) {
    List<CategoryJson> categories = jdbcTxTemplate.execute(() ->
        categoryDao.findAllByUsername(username).stream()
            .map(CategoryJson::fromEntity)
            .toList()
    );
    return categories == null
        ? List.of()
        : categories;
  }

  @Step("SQL Поиск категории по username и categoryName")
  public @Nonnull Optional<CategoryEntity> findByUsernameAndName(
      String username,
      String categoryName
  ) {
    return Objects.requireNonNull(
        jdbcTxTemplate.execute(() ->
            categoryDao.findByUsernameAndName(username, categoryName)
        )
    );
  }

  @Step("SQL Поиск всех категорий")
  public @Nonnull List<CategoryJson> findAll() {
    List<CategoryJson> categories = jdbcTxTemplate.execute(() ->
        categoryDao.findAll()
            .stream()
            .map(CategoryJson::fromEntity)
            .toList()
    );
    return categories == null
        ? List.of()
        : categories;
  }

  @Step("SQL Удаление категории")
  public void delete(CategoryEntity category) {
    categoryDao.remove(category);
  }
}

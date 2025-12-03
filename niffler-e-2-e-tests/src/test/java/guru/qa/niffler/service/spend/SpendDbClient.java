package guru.qa.niffler.service.spend;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.SpendJson;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public class SpendDbClient {

  private final static Config CFG = Config.getInstance();

  private final CategoryDao categoryDao = new CategoryDaoJdbc();
  private final SpendDao spendDao = new SpendDaoJdbc();

  private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
      CFG.spendJdbcUrl()
  );

  public SpendJson create(@Nonnull SpendJson spend) {
    return jdbcTxTemplate.execute(() -> {
          SpendEntity spendEntity = SpendEntity.fromJson(spend);
          if (spendEntity.getCategory().getId() == null) {
            CategoryEntity categoryEntity = categoryDao.create(
                spendEntity.getCategory());
            spendEntity.setCategory(categoryEntity);
          }
          return SpendJson.fromEntity(spendDao.create(spendEntity));
        }
    );
  }

  public @Nonnull Optional<SpendEntity> findById(@Nonnull UUID id) {
    return jdbcTxTemplate.execute(() -> spendDao.findById(id));
  }

  public List<SpendJson> findAll() {
    return jdbcTxTemplate.execute(() ->
        spendDao.findAll()
            .stream()
            .map(SpendJson::fromEntity)
            .toList()
    );
  }

  public @Nonnull List<SpendEntity> findAllByUsername(@Nonnull String username) {
    return jdbcTxTemplate.execute(() -> spendDao.findAllByUsername(username));
  }

  public void delete(@Nonnull SpendEntity spend) {
    spendDao.delete(spend);
  }

  public List<SpendJson> findAllWithSpringJdbc() {
    return jdbcTxTemplate.execute(() ->
        spendDao.findAll()
            .stream()
            .map(SpendJson::fromEntity)
            .toList()
    );
  }
}

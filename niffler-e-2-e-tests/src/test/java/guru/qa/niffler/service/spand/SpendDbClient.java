package guru.qa.niffler.service.spand;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.data.entity.SpendEntity;
import guru.qa.niffler.model.SpendJson;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public class SpendDbClient implements SpendClient {

  private final SpendDao spendDao = new SpendDaoJdbc();
  private final CategoryDao categoryDao = new CategoryDaoJdbc();

  @Override
  public SpendJson create(@Nonnull SpendJson spend) {
    SpendEntity spendEntity = SpendEntity.fromJson(spend);
    if (spendEntity.getCategory().getId() == null) {
      CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
      spendEntity.setCategory(categoryEntity);
    }
    return SpendJson.fromEntity(
        spendDao.create(spendEntity)
    );
  }

  public @Nonnull Optional<SpendEntity> findById(@Nonnull UUID id) {
    return spendDao.findById(id);
  }

  public @Nonnull List<SpendEntity> findAllByUsername(@Nonnull String username) {
    return spendDao.findAllByUsername(username);
  }

  public void delete(@Nonnull SpendEntity spend) {
    spendDao.delete(spend);
  }
}

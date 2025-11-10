package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.SpendEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public interface SpendDao {

  @Nonnull
  SpendEntity create(@Nonnull SpendEntity spend);

  @Nonnull
  Optional<SpendEntity> findById(@Nonnull UUID id);

  @Nonnull
  List<SpendEntity> findAllByUsername(@Nonnull String username);

  @Nonnull
  List<SpendEntity> findAll();

  void delete(@Nonnull SpendEntity spend);
}

package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;

public interface AuthAuthorityDao {

  void create(@Nonnull AuthAuthorityEntity... authorities);

  @Nonnull
  List<AuthAuthorityEntity> findAllByUserId(@Nonnull UUID userId);

  void delete(@Nonnull AuthAuthorityEntity category);
}

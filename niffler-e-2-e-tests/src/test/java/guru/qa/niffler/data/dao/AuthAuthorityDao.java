package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;

public interface AuthAuthorityDao {

  @Nonnull
  AuthAuthorityEntity create(@Nonnull AuthAuthorityEntity authorities);

  @Nonnull
  List<AuthAuthorityEntity> findAllByUserId(@Nonnull UUID category);

  void delete(@Nonnull AuthAuthorityEntity category);
}

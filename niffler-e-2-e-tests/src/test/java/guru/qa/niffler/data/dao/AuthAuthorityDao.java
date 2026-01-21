package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface AuthAuthorityDao {

  void create(AuthAuthorityEntity... authorities);

  @Nonnull
  List<AuthAuthorityEntity> findAllByUserId(UUID userId);

  void delete(AuthAuthorityEntity authority);
}

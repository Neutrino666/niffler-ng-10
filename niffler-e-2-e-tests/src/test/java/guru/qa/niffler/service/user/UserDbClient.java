package guru.qa.niffler.service.user;

import guru.qa.niffler.data.dao.UserDao;
import guru.qa.niffler.data.dao.impl.UserDaoJdbc;
import guru.qa.niffler.data.entity.UserEntity;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public class UserDbClient implements UserClient {

  private final UserDao userDao = new UserDaoJdbc();

  @Override
  public @Nonnull UserEntity create(@Nonnull UserEntity user) {
    return userDao.create(user);
  }

  @Override
  public @Nonnull Optional<UserEntity> findById(@Nonnull UUID id) {
    return userDao.findById(id);
  }

  @Override
  public @Nonnull Optional<UserEntity> findByUsername(@Nonnull String username) {
    return userDao.findByUsername(username);
  }

  @Override
  public void delete(@Nonnull UserEntity user) {
    userDao.delete(user);
  }
}
